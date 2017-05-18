/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.render;

import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERAL_REQUESTED;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareDataCollector;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.NoOpCompareDataCollector;
import org.eclipse.osee.framework.ui.skynet.render.word.AttributeElement;
import org.eclipse.osee.framework.ui.skynet.render.word.Producer;

/**
 * @author Ryan D. Brooks
 */
public final class RendererManager {
   private static final List<IRenderer> renderers = new ArrayList<>(20);
   private static boolean firstTimeThrough = true;

   private RendererManager() {
      // Utility Class
   }

   /**
    * @return Returns the intersection of renderers applicable for all of the artifacts
    */
   public static List<IRenderer> getCommonRenderers(Collection<Artifact> artifacts, PresentationType presentationType, Object... data) throws OseeCoreException {
      List<IRenderer> commonRenders = getApplicableRenderers(presentationType, artifacts.iterator().next(), data);

      for (Artifact artifact : artifacts) {
         List<IRenderer> applicableRenders = getApplicableRenderers(presentationType, artifact);

         Iterator<IRenderer> commIterator = commonRenders.iterator();

         while (commIterator.hasNext()) {
            IRenderer commRenderer = commIterator.next();
            boolean found = false;
            for (IRenderer appRenderer : applicableRenders) {
               if (appRenderer.getName().equals(commRenderer.getName())) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               commIterator.remove();
            }
         }
      }
      return commonRenders;
   }

   /**
    * Maps all renderers in the system to their applicable artifact types
    */
   private static synchronized void ensurePopulated() {
      if (firstTimeThrough) {
         firstTimeThrough = false;
         registerRendersFromExtensionPoints();
      }
   }

   private static void registerRendersFromExtensionPoints() {
      ExtensionDefinedObjects<IRenderer> contributions =
         new ExtensionDefinedObjects<IRenderer>(Activator.PLUGIN_ID + ".ArtifactRenderer", "Renderer", "classname");
      for (IRenderer renderer : contributions.getObjects()) {
         renderers.add(renderer);
      }
   }

   public static IRenderer getBestRenderer(PresentationType presentationType, Artifact artifact, Object... options) throws OseeCoreException {
      IRenderer bestRenderer = getBestRendererPrototype(presentationType, artifact, options).newInstance();
      bestRenderer.setOptions(options);
      return bestRenderer;
   }

   private static IRenderer getBestRendererPrototype(PresentationType presentationType, Artifact artifact, Object... options) throws OseeCoreException {
      if (presentationType == DEFAULT_OPEN && UserManager.getBooleanSetting(
         UserManager.DOUBLE_CLICK_SETTING_KEY_ART_EDIT)) {
         presentationType = GENERAL_REQUESTED;
      }
      IRenderer bestRendererPrototype = null;
      int bestRating = IRenderer.NO_MATCH;
      ensurePopulated();
      for (IRenderer renderer : renderers) {
         renderer.setOptions(options);
         int rating = renderer.getApplicabilityRating(presentationType, artifact, options);
         if (rating > bestRating) {
            bestRendererPrototype = renderer;
            bestRating = rating;
         }
      }
      if (bestRendererPrototype == null) {
         throw new OseeStateException("No renderer configured for %s of %s", presentationType, artifact);
      }

      return bestRendererPrototype;
   }

   public static void renderAttribute(AttributeTypeToken attributeType, PresentationType presentationType, Artifact artifact, Producer producer, AttributeElement attributeElement, String footer, Object... options) throws OseeCoreException {
      getBestRenderer(PRODUCE_ATTRIBUTE, artifact, options).renderAttribute(attributeType, artifact, presentationType,
         producer, attributeElement, footer);
   }

   public static Collection<AttributeTypeToken> getAttributeTypeOrderList(Artifact artifact) throws OseeCoreException {
      return getBestRenderer(PresentationType.PRODUCE_ATTRIBUTE, artifact).getOrderedAttributeTypes(artifact,
         artifact.getAttributeTypes());
   }

   private static List<IRenderer> getApplicableRenderers(PresentationType presentationType, Artifact artifact, Object... data) throws OseeCoreException {
      ArrayList<IRenderer> applicableRenderers = new ArrayList<>();
      IRenderer bestRenderer = getBestRenderer(presentationType, artifact, data);
      int rendererMinimumRanking = bestRenderer.minimumRanking();
      int minimumRank = Math.max(rendererMinimumRanking, IRenderer.BASE_MATCH);

      for (IRenderer prototypeRenderer : renderers) {
         // Add Catch Exception Code --

         int rating = prototypeRenderer.getApplicabilityRating(presentationType, artifact);
         if (rating >= minimumRank) {
            IRenderer renderer = prototypeRenderer.newInstance();
            applicableRenderers.add(renderer);
         }
      }
      return applicableRenderers;
   }

   public static HashCollection<IRenderer, Artifact> createRenderMap(PresentationType presentationType, Collection<Artifact> artifacts, Object... options) throws OseeCoreException {
      HashCollection<IRenderer, Artifact> prototypeRendererArtifactMap =
         new HashCollection<IRenderer, Artifact>(false, LinkedList.class);
      for (Artifact artifact : artifacts) {
         IRenderer renderer = getBestRendererPrototype(presentationType, artifact, options);
         prototypeRendererArtifactMap.put(renderer, artifact);
      }

      // now that the artifacts are grouped based on best renderer type, create instances of those renderer with the supplied options
      HashCollection<IRenderer, Artifact> rendererArtifactMap =
         new HashCollection<IRenderer, Artifact>(false, LinkedList.class);
      for (IRenderer prototypeRenderer : prototypeRendererArtifactMap.keySet()) {
         IRenderer renderer = prototypeRenderer.newInstance();
         renderer.setOptions(options);
         rendererArtifactMap.put(renderer, prototypeRendererArtifactMap.getValues(prototypeRenderer));
      }
      return rendererArtifactMap;
   }

   public static void openInJob(Artifact artifact, PresentationType presentationType) {
      openInJob(Collections.singletonList(artifact), presentationType);
   }

   public static void openInJob(Collection<Artifact> artifacts, PresentationType presentationType, Object... options) {
      Operations.executeAsJob(new OpenUsingRenderer(artifacts, presentationType, options), true);
   }

   public static String open(Collection<Artifact> artifacts, PresentationType presentationType, IProgressMonitor monitor, Object... options) throws OseeCoreException {
      OpenUsingRenderer operation = new OpenUsingRenderer(artifacts, presentationType, options);
      Operations.executeWorkAndCheckStatus(operation, monitor);
      return operation.getResultPath();
   }

   public static String open(Collection<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      return open(artifacts, presentationType, new NullProgressMonitor());
   }

   public static String open(Artifact artifact, PresentationType presentationType, Object... options) throws OseeCoreException {
      return open(Collections.singletonList(artifact), presentationType, new NullProgressMonitor(), options);
   }

   public static String open(Artifact artifact, PresentationType presentationType, IProgressMonitor monitor) throws OseeCoreException {
      return open(Collections.singletonList(artifact), presentationType, monitor);
   }

   public static String open(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      return open(Collections.singletonList(artifact), presentationType);
   }

   public static void merge(CompareDataCollector collector, Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, String pathPrefix, Object... options) throws OseeCoreException {
      IRenderer renderer = getBestRenderer(PresentationType.MERGE, baseVersion, options);
      IComparator comparator = renderer.getComparator();
      comparator.compare(collector, baseVersion, newerVersion, baseFile, newerFile, PresentationType.MERGE, pathPrefix);
   }

   public static void diffInJob(ArtifactDelta artifactDelta, String pathPrefix, Object... options) {
      CompareDataCollector collector = new NoOpCompareDataCollector();
      Operations.executeAsJob(new DiffUsingRenderer(collector, artifactDelta, pathPrefix, options), true);
   }

   public static void diff(CompareDataCollector collector, Collection<ArtifactDelta> artifactDelta, String pathPrefix, Object... options) {
      IRenderer renderer = new WordTemplateRenderer();
      renderer.setOptions(options);
      DiffUsingRenderer operation = new DiffUsingRenderer(collector, artifactDelta, pathPrefix, renderer, options);
      Operations.executeWork(operation);
   }

   public static void diff(CompareDataCollector collector, ArtifactDelta artifactDelta, String pathPrefix, Object... options) {
      DiffUsingRenderer operation = new DiffUsingRenderer(collector, artifactDelta, pathPrefix, options);
      Operations.executeWork(operation);
   }

   public static void diffInJobWithPreferedRenderer(Collection<ArtifactDelta> artifactDeltas, String pathPrefix, IRenderer preferedRenderer, Object... options) {
      CompareDataCollector collector = new NoOpCompareDataCollector();
      IOperation operation = new DiffUsingRenderer(collector, artifactDeltas, pathPrefix, preferedRenderer, options);
      Operations.executeAsJob(operation, true);
   }

   public static void diffInJob(Collection<ArtifactDelta> artifactDeltas, String pathPrefix, Object... options) {
      diffInJobWithPreferedRenderer(artifactDeltas, pathPrefix, null, options);
   }

   public static void diff(Collection<ArtifactDelta> artifactDeltas, String pathPrefix, Object... options) {
      CompareDataCollector collector = new NoOpCompareDataCollector();
      IOperation operation = new DiffUsingRenderer(collector, artifactDeltas, pathPrefix, options);
      IProgressMonitor monitor = null;
      for (int i = 0; i < options.length; i += 2) {
         if (((String) options[i]).equals("Progress Monitor")) {
            monitor = (IProgressMonitor) options[i + 1];
            break;
         }
      }
      Operations.executeWork(operation, monitor);
   }

   public static void diffWithRenderer(Collection<ArtifactDelta> artifactDeltas, String pathPrefix, IRenderer preferredRenderer, Object... options) {
      CompareDataCollector collector = new NoOpCompareDataCollector();
      IOperation operation = new DiffUsingRenderer(collector, artifactDeltas, pathPrefix, preferredRenderer, options);
      IProgressMonitor monitor = null;
      for (int i = 0; i < options.length; i += 2) {
         if (((String) options[i]).equals("Progress Monitor")) {
            monitor = (IProgressMonitor) options[i + 1];
            break;
         }
      }
      Operations.executeWork(operation, monitor);
   }

}