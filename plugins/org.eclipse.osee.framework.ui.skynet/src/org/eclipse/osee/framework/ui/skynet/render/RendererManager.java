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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.word.AttributeElement;
import org.eclipse.osee.framework.ui.skynet.render.word.Producer;

/**
 * @author Ryan D. Brooks
 */
public class RendererManager {
   private static final RendererManager instance = new RendererManager();
   private final HashMap<String, IRenderer> renderers = new HashMap<String, IRenderer>(40);

   private RendererManager() {
      registerRendersFromExtensionPoints();
   }

   /**
    * @param artifacts
    * @param presentationType
    * @return Returns the intersection of renderers applicable for all of the artifacts
    * @throws OseeCoreException
    */
   public static List<IRenderer> getCommonRenderers(Collection<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      List<IRenderer> commonRenders = getApplicableRenderers(presentationType, artifacts.iterator().next(), null);

      for (Artifact artifact : artifacts) {
         List<IRenderer> applicableRenders = getApplicableRenderers(presentationType, artifact, null);

         Iterator<?> commIterator = commonRenders.iterator();

         while (commIterator.hasNext()) {
            IRenderer commRenderer = (IRenderer) commIterator.next();
            boolean found = false;
            for (IRenderer appRenderer : applicableRenders) {
               if (appRenderer.getName().equals(commRenderer.getName())) {
                  found = true;
                  continue;
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
   @SuppressWarnings("unchecked")
   private void registerRendersFromExtensionPoints() {
      List<IConfigurationElement> elements =
         ExtensionPoints.getExtensionElements(SkynetGuiPlugin.getInstance(), "ArtifactRenderer", "Renderer");

      for (IConfigurationElement element : elements) {
         String classname = element.getAttribute("classname");
         String bundleName = element.getContributor().getName();
         try {
            Class<IRenderer> clazz = Platform.getBundle(bundleName).loadClass(classname);
            Constructor<IRenderer> constructor = clazz.getConstructor();
            IRenderer renderer = constructor.newInstance();
            renderers.put(renderer.getClass().getCanonicalName(), renderer);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         } catch (NoClassDefFoundError er) {
            OseeLog.log(SkynetGuiPlugin.class, Level.WARNING,
               "Failed to find a class definition for " + classname + ", registered from bundle " + bundleName, er);
         }
      }
   }

   public static FileSystemRenderer getBestFileRenderer(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      return getBestFileRenderer(presentationType, artifact, null);
   }

   public static FileSystemRenderer getBestFileRenderer(PresentationType presentationType, Artifact artifact, VariableMap options) throws OseeCoreException {
      IRenderer bestRenderer = getBestRenderer(presentationType, artifact, options);
      if (bestRenderer instanceof FileSystemRenderer) {
         return (FileSystemRenderer) bestRenderer;
      }
      throw new OseeArgumentException(
         "No FileRenderer found for " + artifact + " of type " + artifact.getArtifactTypeName());
   }

   public static IRenderer getBestRenderer(PresentationType presentationType, Artifact artifact, VariableMap options) throws OseeCoreException {
      IRenderer bestRenderer = getBestRendererPrototype(presentationType, artifact).newInstance();
      bestRenderer.setOptions(options);
      return bestRenderer;
   }

   private static IRenderer getBestRendererPrototype(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      IRenderer bestRendererPrototype = null;
      int bestRating = IRenderer.NO_MATCH;
      for (IRenderer renderer : instance.renderers.values()) {
         int rating = renderer.getApplicabilityRating(presentationType, artifact);
         if (rating > bestRating) {
            bestRendererPrototype = renderer;
            bestRating = rating;
         }
      }
      if (bestRendererPrototype == null) {
         throw new OseeStateException(String.format("No renderer configured for %s of %s", presentationType, artifact));
      }
      return bestRendererPrototype;
   }

   public static void renderAttribute(IAttributeType attributeType, PresentationType presentationType, Artifact artifact, VariableMap options, Producer producer, AttributeElement attributeElement) throws OseeCoreException {
      getBestRenderer(presentationType, artifact, options).renderAttribute(attributeType, artifact, presentationType,
         producer, options, attributeElement);
   }

   public static Collection<AttributeType> getAttributeTypeOrderList(Artifact artifact) throws OseeCoreException {
      return getBestRenderer(PresentationType.PREVIEW, artifact, null).orderAttributeNames(artifact,
         artifact.getAttributeTypes());
   }

   public static List<IRenderer> getApplicableRenderers(PresentationType presentationType, Artifact artifact, VariableMap options) throws OseeCoreException {
      ArrayList<IRenderer> renderers = new ArrayList<IRenderer>();
      int minimumRanking = getBestRenderer(presentationType, artifact, options).minimumRanking();

      for (IRenderer prototypeRenderer : instance.renderers.values()) {
         // Add Catch Exception Code --

         int rating = prototypeRenderer.getApplicabilityRating(presentationType, artifact);
         if (rating >= minimumRanking) {
            IRenderer renderer = prototypeRenderer.newInstance();
            renderer.setOptions(options);
            renderers.add(renderer);
         }
      }
      return renderers;
   }

   public static HashCollection<IRenderer, Artifact> createRenderMap(PresentationType presentationType, Collection<Artifact> artifacts, VariableMap options) throws OseeCoreException {
      HashCollection<IRenderer, Artifact> prototypeRendererArtifactMap =
         new HashCollection<IRenderer, Artifact>(false, LinkedList.class);
      for (Artifact artifact : artifacts) {
         prototypeRendererArtifactMap.put(getBestRendererPrototype(presentationType, artifact), artifact);
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
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>(1);
      artifacts.add(artifact);

      openInJob(artifacts, null, presentationType);
   }

   public static void openInJob(Collection<Artifact> artifacts, PresentationType presentationType) {
      openInJob(artifacts, null, presentationType);
   }

   public static void openInJob(Collection<Artifact> artifacts, VariableMap options, PresentationType presentationType) {
      Operations.executeAsJob(new OpenUsingRenderer(artifacts, options, presentationType), true);
   }

   public static void open(Collection<Artifact> artifacts, PresentationType presentationType, VariableMap options, IProgressMonitor monitor) throws OseeCoreException {
      Operations.executeWorkAndCheckStatus(new OpenUsingRenderer(artifacts, options, presentationType), monitor);
   }

   public static void open(Collection<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      open(artifacts, presentationType, null, new NullProgressMonitor());
   }

   public static void open(Artifact artifact, final PresentationType presentationType, IProgressMonitor monitor) throws OseeCoreException {
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>(1);
      artifacts.add(artifact);
      open(artifacts, presentationType, null, monitor);
   }

   public static void open(Artifact artifact, final PresentationType presentationType) throws OseeCoreException {
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>(1);
      artifacts.add(artifact);
      open(artifacts, presentationType);
   }

   public static String merge(Artifact baseVersion, Artifact newerVersion, VariableMap options) throws OseeStateException, OseeCoreException {
      IRenderer renderer = getBestRenderer(PresentationType.MERGE, baseVersion, options);
      IComparator comparator = renderer.getComparator();
      ArtifactDelta artifactDelta = new ArtifactDelta(baseVersion, newerVersion);
      return comparator.compare(new NullProgressMonitor(), PresentationType.MERGE, artifactDelta);
   }

   public static String merge(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, VariableMap options) throws OseeCoreException {
      IRenderer renderer = getBestRenderer(PresentationType.MERGE_EDIT, baseVersion, options);
      IComparator comparator = renderer.getComparator();
      return comparator.compare(baseVersion, newerVersion, baseFile, newerFile, PresentationType.MERGE_EDIT);
   }

   public static Job diffInJob(ArtifactDelta artifactDelta) {
      return diff(artifactDelta, null, true);
   }

   public static Job diffInJob(ArtifactDelta artifactDelta, VariableMap options) {
      return diff(artifactDelta, options, true);
   }

   public static Job diff(ArtifactDelta artifactDelta, VariableMap options) {
      return diff(artifactDelta, options, false);
   }

   public static Job diff(ArtifactDelta artifactDelta) {
      return diff(artifactDelta, null, false);
   }

   public static Job diffInJob(Collection<ArtifactDelta> artifactDeltas) {
      return diff(artifactDeltas, null);
   }

   public static Job diffInJob(Collection<ArtifactDelta> artifactDeltas, VariableMap options) {
      return diff(artifactDeltas, options);
   }

   private static Job diff(Collection<ArtifactDelta> artifactDeltas, VariableMap options) {
      IOperation operation = new DiffUsingRenderer(artifactDeltas, options);
      return Operations.executeAsJob(operation, true);
   }

   private static Job diff(ArtifactDelta artifactDelta, VariableMap options, boolean asynchronous) {
      IOperation operation = new DiffUsingRenderer(artifactDelta, options);

      if (asynchronous) {
         return Operations.executeAsJob(operation, true);
      } else {
         return Operations.executeAndPend(operation, true);
      }
   }
}