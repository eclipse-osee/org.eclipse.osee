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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.WordMLProducer;
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
   public static List<IRenderer> getCommonRenderers(Collection<Artifact> artifacts, PresentationType presentationType)  {
      List<IRenderer> commonRenders = getApplicableRenderers(presentationType, artifacts.iterator().next());

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

   public static IRenderer getBestRenderer(PresentationType presentationType, Artifact artifact) {
      return getBestRenderer(presentationType, artifact, new HashMap<RendererOption, Object>());
   }

   public static IRenderer getBestRenderer(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      IRenderer bestRenderer =
         getBestRendererPrototype(presentationType, artifact, rendererOptions).newInstance(rendererOptions);
      return bestRenderer;
   }

   private static IRenderer getBestRendererPrototype(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions)  {
      if (presentationType == DEFAULT_OPEN && UserManager.getBooleanSetting(
         UserManager.DOUBLE_CLICK_SETTING_KEY_ART_EDIT)) {
         presentationType = GENERAL_REQUESTED;
      }
      IRenderer bestRendererPrototype = null;
      int bestRating = IRenderer.NO_MATCH;
      ensurePopulated();
      for (IRenderer renderer : renderers) {
         int rating = renderer.getApplicabilityRating(presentationType, artifact, rendererOptions);

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

   public static void renderAttribute(AttributeTypeToken attributeType, PresentationType presentationType, Artifact artifact, WordMLProducer producer, String format, String label, String footer, Map<RendererOption, Object> rendererOptions)  {
      getBestRenderer(PRODUCE_ATTRIBUTE, artifact, rendererOptions).renderAttribute(attributeType, artifact,
         presentationType, producer, format, label, footer);
   }

   public static Collection<AttributeTypeToken> getAttributeTypeOrderList(Artifact artifact)  {
      return getBestRenderer(PresentationType.PRODUCE_ATTRIBUTE, artifact,
         new HashMap<RendererOption, Object>()).getOrderedAttributeTypes(artifact, artifact.getAttributeTypes());
   }

   private static List<IRenderer> getApplicableRenderers(PresentationType presentationType, Artifact artifact, Object... data)  {
      ArrayList<IRenderer> applicableRenderers = new ArrayList<>();

      IRenderer bestRenderer = getBestRenderer(presentationType, artifact, new HashMap<RendererOption, Object>());

      int rendererMinimumRanking = bestRenderer.minimumRanking();
      int minimumRank = Math.max(rendererMinimumRanking, IRenderer.BASE_MATCH);

      for (IRenderer prototypeRenderer : renderers) {
         // Add Catch Exception Code --

         int rating =
            prototypeRenderer.getApplicabilityRating(presentationType, artifact, new HashMap<RendererOption, Object>());
         if (rating >= minimumRank) {
            IRenderer renderer = prototypeRenderer.newInstance();
            applicableRenderers.add(renderer);
         }
      }
      return applicableRenderers;
   }

   public static HashCollection<IRenderer, Artifact> createRenderMap(PresentationType presentationType, Collection<Artifact> artifacts, Map<RendererOption, Object> rendererOptions) {
      HashCollection<IRenderer, Artifact> prototypeRendererArtifactMap =
         new HashCollection<IRenderer, Artifact>(false, LinkedList.class);
      for (Artifact artifact : artifacts) {
         IRenderer renderer = getBestRendererPrototype(presentationType, artifact, rendererOptions);
         prototypeRendererArtifactMap.put(renderer, artifact);
      }

      // now that the artifacts are grouped based on best renderer type, create instances of those renderer with the supplied options
      HashCollection<IRenderer, Artifact> rendererArtifactMap =
         new HashCollection<IRenderer, Artifact>(false, LinkedList.class);
      for (IRenderer prototypeRenderer : prototypeRendererArtifactMap.keySet()) {
         IRenderer renderer = prototypeRenderer.newInstance(rendererOptions);
         rendererArtifactMap.put(renderer, prototypeRendererArtifactMap.getValues(prototypeRenderer));
      }
      return rendererArtifactMap;
   }

   public static void openInJob(Artifact artifact, PresentationType presentationType) {
      openInJob(Collections.singletonList(artifact), presentationType, new HashMap<RendererOption, Object>());
   }

   public static void openInJob(Collection<Artifact> artifacts, PresentationType presentationType) {
      openInJob(artifacts, presentationType, new HashMap<RendererOption, Object>());
   }

   public static void openInJob(Collection<Artifact> artifacts, PresentationType presentationType, Map<RendererOption, Object> rendererOptions) {
      Operations.executeAsJob(new OpenUsingRenderer(artifacts, presentationType, rendererOptions), true);
   }

   public static String open(Collection<Artifact> artifacts, PresentationType presentationType, IProgressMonitor monitor, Map<RendererOption, Object> rendererOptions)  {
      OpenUsingRenderer operation = new OpenUsingRenderer(artifacts, presentationType, rendererOptions);
      Operations.executeWorkAndCheckStatus(operation, monitor);
      return operation.getResultPath();
   }

   public static String open(Collection<Artifact> artifacts, PresentationType presentationType)  {
      return open(artifacts, presentationType, new NullProgressMonitor(), new HashMap<RendererOption, Object>());
   }

   public static String open(Artifact artifact, PresentationType presentationType, Map<RendererOption, Object> rendererOptions)  {
      return open(Collections.singletonList(artifact), presentationType, new NullProgressMonitor(), rendererOptions);
   }

   public static String open(Artifact artifact, PresentationType presentationType, IProgressMonitor monitor)  {
      return open(Collections.singletonList(artifact), presentationType, monitor,
         new HashMap<RendererOption, Object>());
   }

   public static String open(Artifact artifact, PresentationType presentationType)  {
      return open(Collections.singletonList(artifact), presentationType);
   }

   public static void merge(CompareDataCollector collector, Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, String pathPrefix, Map<RendererOption, Object> rendererOptions)  {
      IRenderer renderer = getBestRenderer(PresentationType.MERGE, baseVersion, rendererOptions);
      IComparator comparator = renderer.getComparator();
      comparator.compare(collector, baseVersion, newerVersion, baseFile, newerFile, PresentationType.MERGE, pathPrefix);
   }

   public static void diffInJob(ArtifactDelta artifactDelta, String pathPrefix) {
      diffInJob(artifactDelta, pathPrefix, new HashMap<RendererOption, Object>());
   }

   public static void diffInJob(ArtifactDelta artifactDelta, String pathPrefix, Map<RendererOption, Object> rendererOptions) {
      CompareDataCollector collector = new NoOpCompareDataCollector();
      Operations.executeAsJob(new DiffUsingRenderer(collector, artifactDelta, pathPrefix, rendererOptions), true);
   }

   public static void diff(CompareDataCollector collector, Collection<ArtifactDelta> artifactDelta, String pathPrefix, Map<RendererOption, Object> rendererOptions) {
      IRenderer renderer = new WordTemplateRenderer(rendererOptions);
      DiffUsingRenderer operation =
         new DiffUsingRenderer(collector, artifactDelta, pathPrefix, renderer, rendererOptions);
      Operations.executeWork(operation);
   }

   public static void diff(CompareDataCollector collector, ArtifactDelta artifactDelta, String pathPrefix, Map<RendererOption, Object> rendererOptions) {
      DiffUsingRenderer operation = new DiffUsingRenderer(collector, artifactDelta, pathPrefix, rendererOptions);
      Operations.executeWork(operation);
   }

   public static void diffInJobWithPreferedRenderer(Collection<ArtifactDelta> artifactDeltas, String pathPrefix, IRenderer preferedRenderer) {
      diffInJobWithPreferedRenderer(artifactDeltas, pathPrefix, preferedRenderer,
         new HashMap<RendererOption, Object>());
   }

   public static void diffInJobWithPreferedRenderer(Collection<ArtifactDelta> artifactDeltas, String pathPrefix, IRenderer preferedRenderer, Map<RendererOption, Object> rendererOptions) {
      CompareDataCollector collector = new NoOpCompareDataCollector();
      IOperation operation =
         new DiffUsingRenderer(collector, artifactDeltas, pathPrefix, preferedRenderer, rendererOptions);
      Operations.executeAsJob(operation, true);
   }

   public static void diffInJob(Collection<ArtifactDelta> artifactDeltas, String pathPrefix) {
      diffInJob(artifactDeltas, pathPrefix, new HashMap<RendererOption, Object>());
   }

   public static void diffInJob(Collection<ArtifactDelta> artifactDeltas, String pathPrefix, Map<RendererOption, Object> rendererOptions) {
      diffInJobWithPreferedRenderer(artifactDeltas, pathPrefix, null, rendererOptions);
   }

   public static void diff(Collection<ArtifactDelta> artifactDeltas, String pathPrefix, Map<RendererOption, Object> rendererOptions) {
      CompareDataCollector collector = new NoOpCompareDataCollector();
      IOperation operation = new DiffUsingRenderer(collector, artifactDeltas, pathPrefix, rendererOptions);
      IProgressMonitor monitor = null;
      if (rendererOptions.containsKey(RendererOption.PROGRESS_MONITOR)) {
         monitor = (IProgressMonitor) rendererOptions.get(RendererOption.PROGRESS_MONITOR);
      }

      Operations.executeWork(operation, monitor);
   }

   public static void diffWithRenderer(Collection<ArtifactDelta> artifactDeltas, String pathPrefix, IRenderer preferredRenderer) {
      diffWithRenderer(artifactDeltas, pathPrefix, preferredRenderer, new HashMap<RendererOption, Object>());
   }

   public static void diffWithRenderer(Collection<ArtifactDelta> artifactDeltas, String pathPrefix, IRenderer preferredRenderer, Map<RendererOption, Object> rendererOptions) {
      CompareDataCollector collector = new NoOpCompareDataCollector();
      IOperation operation =
         new DiffUsingRenderer(collector, artifactDeltas, pathPrefix, preferredRenderer, rendererOptions);
      IProgressMonitor monitor = null;
      if (rendererOptions.containsKey(RendererOption.PROGRESS_MONITOR)) {
         monitor = (IProgressMonitor) rendererOptions.get(RendererOption.PROGRESS_MONITOR);
      }

      Operations.executeWork(operation, monitor);
   }

}