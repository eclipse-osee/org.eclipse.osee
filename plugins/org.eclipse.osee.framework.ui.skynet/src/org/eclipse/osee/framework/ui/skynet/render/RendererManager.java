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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
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
   public static List<IRenderer> getCommonRenderers(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      List<IRenderer> commonRenders = getApplicableRenderers(presentationType, artifacts.get(0), null);

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

   private static IRenderer getBestRenderer(PresentationType presentationType, Artifact artifact, VariableMap options) throws OseeCoreException {
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
         throw new OseeStateException("At least the DefaultArtifactRenderer should have been found.");
      }
      return bestRendererPrototype;
   }

   public static void renderAttribute(String attrType, PresentationType presentationType, Artifact artifact, VariableMap options, Producer producer, AttributeElement attributeElement) throws OseeCoreException {
      getBestRenderer(presentationType, artifact, options).renderAttribute(attrType, artifact, presentationType,
            producer, options, attributeElement);
   }

   public static List<IRenderer> getApplicableRenderers(PresentationType presentationType, Artifact artifact, VariableMap options) throws OseeCoreException {
      ArrayList<IRenderer> renderers = new ArrayList<IRenderer>();

      for (IRenderer prototypeRenderer : instance.renderers.values()) {
         // Add Catch Exception Code --

         int rating = prototypeRenderer.getApplicabilityRating(presentationType, artifact);
         if (rating > getBestRenderer(presentationType, artifact, options).minimumRanking()) {
            IRenderer renderer = prototypeRenderer.newInstance();
            renderer.setOptions(options);
            renderers.add(renderer);
         }
      }
      return renderers;
   }

   private static HashCollection<IRenderer, Artifact> createRenderMap(PresentationType presentationType, List<Artifact> artifacts, VariableMap options) throws OseeCoreException {
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

   public static void openInJob(final List<Artifact> artifacts, PresentationType presentationType) {
      openInJob(artifacts, null, presentationType);
   }

   public static void openInJob(final List<Artifact> artifacts, final VariableMap options, final PresentationType presentationType) {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public IStatus run(IProgressMonitor monitor) throws Exception {

            if (presentationType != PresentationType.SPECIALIZED_EDIT || ArtifactGuis.checkOtherEdit(artifacts)) {
               HashCollection<IRenderer, Artifact> rendererArtifactMap =
                     createRenderMap(presentationType, artifacts, options);

               for (IRenderer renderer : rendererArtifactMap.keySet()) {
                  renderer.open((LinkedList<Artifact>) rendererArtifactMap.getValues(renderer));
               }
            }
            return Status.OK_STATUS;
         }
      };

      Jobs.runInJob("Open ", runnable, SkynetGuiPlugin.class, SkynetGuiPlugin.PLUGIN_ID);
   }

   public static void openMergeEditJob(final Artifact artifact) {
      openMergeEditJob(artifact, null);
   }

   public static void openMergeEditJob(final Artifact artifact, final VariableMap options) {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public IStatus run(IProgressMonitor monitor) throws Exception {
            ArrayList<Artifact> artifactList = new ArrayList<Artifact>(1);
            artifactList.add(artifact);

            HashCollection<IRenderer, Artifact> rendererArtifactMap =
                  createRenderMap(PresentationType.MERGE_EDIT, artifactList, options);

            for (IRenderer renderer : rendererArtifactMap.keySet()) {
               renderer.openMergeEdit((LinkedList<Artifact>) rendererArtifactMap.getValues(renderer));
            }
            return Status.OK_STATUS;
         }
      };

      Jobs.runInJob("Merge Edit ", runnable, SkynetGuiPlugin.class, SkynetGuiPlugin.PLUGIN_ID);
   }

   public static void previewInJob(final Artifact artifact) throws OseeCoreException {
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>(1);
      artifacts.add(artifact);

      previewInJob(artifacts, null);
   }

   public static void previewInJob(final List<Artifact> artifacts) throws OseeCoreException {
      previewInJob(artifacts, null);
   }

   public static void previewInJob(final List<Artifact> artifacts, final VariableMap options) throws OseeCoreException {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public IStatus run(IProgressMonitor monitor) throws Exception {
            preview(artifacts, new NullProgressMonitor(), options);
            return Status.OK_STATUS;
         }
      };

      Jobs.runInJob("Preview " + artifacts.size() + " artifacts", runnable, SkynetGuiPlugin.class,
            SkynetGuiPlugin.PLUGIN_ID, false);
   }

   public static void preview(final List<Artifact> artifacts, IProgressMonitor monitor, final VariableMap options) throws OseeCoreException {
      HashCollection<IRenderer, Artifact> rendererArtifactMap =
            createRenderMap(PresentationType.PREVIEW, artifacts, options);

      for (IRenderer renderer : rendererArtifactMap.keySet()) {
         renderer.preview((LinkedList<Artifact>) rendererArtifactMap.getValues(renderer));
      }
   }

   public static String merge(Artifact baseVersion, Artifact newerVersion, String fileName, boolean show) throws OseeStateException, OseeCoreException {
      VariableMap variableMap = new VariableMap("fileName", fileName);
      IRenderer renderer = getBestRenderer(PresentationType.MERGE, baseVersion, variableMap);
      IComparator comparator = renderer.getComparator();
      ArtifactDelta delta = new ArtifactDelta(null, baseVersion, newerVersion);
      return comparator.compare(new NullProgressMonitor(), PresentationType.MERGE, delta, show);
   }

   public static String merge(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, String fileName, boolean show) throws OseeCoreException {
      VariableMap variableMap = new VariableMap("fileName", fileName);
      IRenderer renderer = getBestRenderer(PresentationType.MERGE_EDIT, baseVersion, variableMap);
      IComparator comparator = renderer.getComparator();
      return comparator.compare(baseVersion, newerVersion, baseFile, newerFile, PresentationType.MERGE_EDIT, show);
   }

   public static void diffInJob(ArtifactDelta artifactDelta) {
      diffInJob(artifactDelta, null);
   }

   public static void diffInJob(final ArtifactDelta artifactDelta, final VariableMap options) {

      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public IStatus run(IProgressMonitor monitor) throws OseeCoreException {
            diff(artifactDelta, true, options);
            return Status.OK_STATUS;
         }
      };

      Artifact startVersion = artifactDelta.getStartArtifact();
      Artifact endVersion = artifactDelta.getEndArtifact();

      String jobName =
            String.format("Compare %s to %s", startVersion == null ? " new " : startVersion.getName(),
                  endVersion == null ? " delete " : endVersion.getName());
      Jobs.runInJob(jobName, runnable, SkynetGuiPlugin.class, SkynetGuiPlugin.PLUGIN_ID);

   }

   public static String diff(final ArtifactDelta delta, IProgressMonitor monitor, boolean show) throws OseeCoreException {
      return diff(delta, monitor, show, null);
   }

   public static String diff(final ArtifactDelta delta, IProgressMonitor monitor, boolean show, final VariableMap options) throws OseeCoreException {
      // To handle comparisons with new or deleted artifacts
      Artifact sampleArtifact = delta.getStartArtifact() != null ? delta.getStartArtifact() : delta.getEndArtifact();
      IRenderer renderer = getBestRenderer(PresentationType.DIFF, sampleArtifact, options);
      IComparator comparator = renderer.getComparator();
      return comparator.compare(monitor, PresentationType.DIFF, delta, show);
   }

   public static String diff(ArtifactDelta artifactDelta, boolean show) throws OseeCoreException {
      return diff(artifactDelta, show, null);
   }

   public static String diff(ArtifactDelta artifactDelta, boolean show, final VariableMap options) throws OseeCoreException {
      return diff(artifactDelta, new NullProgressMonitor(), show, options);
   }

   public static void diffInJob(final Collection<ArtifactDelta> itemsToCompare, final VariableMap options) {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public IStatus run(IProgressMonitor monitor) throws OseeCoreException {
            ArtifactDelta entry = itemsToCompare.iterator().next();
            Artifact sampleArtifact =
                  entry.getStartArtifact() != null ? entry.getStartArtifact() : entry.getEndArtifact();

            IRenderer renderer = getBestRenderer(PresentationType.DIFF, sampleArtifact, options);
            IComparator comparator = renderer.getComparator();
            comparator.compareArtifacts(monitor, PresentationType.DIFF, itemsToCompare);
            return Status.OK_STATUS;
         }
      };
      Jobs.runInJob("Combined Diff", runnable, SkynetGuiPlugin.class, SkynetGuiPlugin.PLUGIN_ID);
   }
}