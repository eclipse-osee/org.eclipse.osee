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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.IExceptionableRunnable;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.BrowserComposite;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

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
    * Maps all renderers in the system to their applicable artifact types
    */
   private void registerRendersFromExtensionPoints() {
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements(SkynetGuiPlugin.getInstance(), "ArtifactRenderer", "Renderer");

      for (IConfigurationElement element : elements) {
         String classname = element.getAttribute("classname");
         String bundleName = element.getContributor().getName();
         try {
            Class<IRenderer> clazz = Platform.getBundle(bundleName).loadClass(classname);
            Constructor<IRenderer> constructor = clazz.getConstructor(new Class[] {String.class});
            IRenderer renderer =
                  constructor.newInstance(new Object[] {element.getDeclaringExtension().getUniqueIdentifier()});
            renderers.put(renderer.getId(), renderer);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         } catch (NoClassDefFoundError er) {
            OseeLog.log(SkynetGuiPlugin.class, Level.WARNING,
                  "Failed to find a class definition for " + classname + ", registered from bundle " + bundleName, er);
         }
      }
   }

   public static FileRenderer getBestFileRenderer(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      return getBestFileRenderer(presentationType, artifact, null);
   }

   public static FileRenderer getBestFileRenderer(PresentationType presentationType, Artifact artifact, VariableMap options) throws OseeCoreException {
      IRenderer bestRenderer = getBestRenderer(presentationType, artifact, options);
      if (bestRenderer instanceof FileRenderer) {
         return (FileRenderer) bestRenderer;
      }
      throw new OseeArgumentException("No FileRenderer found for " + artifact);
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

   public static void preview(Artifact artifact, IProgressMonitor monitor, VariableMap options) throws OseeCoreException {
      getBestRenderer(PresentationType.PREVIEW, artifact, options).preview(artifact, monitor);
   }

   public static void previewInJob(final Artifact artifact) throws OseeCoreException {
      previewInJob(artifact, null);
   }

   public static void previewInJob(final Artifact artifact, VariableMap options) throws OseeCoreException {
      previewInJob(getBestRenderer(PresentationType.PREVIEW, artifact, options), artifact);
   }

   private static void previewInJob(final IRenderer renderer, final Artifact artifact) {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public void run(IProgressMonitor monitor) throws Exception {
            renderer.preview(artifact, monitor);
         }
      };

      Jobs.run("Preview " + artifact.getDescriptiveName(), runnable, SkynetGuiPlugin.class, SkynetGuiPlugin.PLUGIN_ID,
            false);
   }

   public static void previewInJob(final List<Artifact> artifacts) throws OseeCoreException {
      previewInJob(artifacts, null);
   }

   public static void previewInJob(final List<Artifact> artifacts, final VariableMap options) throws OseeCoreException {
      if (artifacts.size() == 1) {
         previewInJob(artifacts.get(0), options);
      } else {
         IExceptionableRunnable runnable = new IExceptionableRunnable() {
            public void run(IProgressMonitor monitor) throws Exception {
               HashCollection<IRenderer, Artifact> rendererArtifactMap =
                     createRenderMap(PresentationType.PREVIEW, artifacts, options);

               for (IRenderer renderer : rendererArtifactMap.keySet()) {
                  renderer.preview((LinkedList<Artifact>) rendererArtifactMap.getValues(renderer), monitor);
               }
            }
         };

         Jobs.run("Preview " + artifacts.size() + " artifacts", runnable, SkynetGuiPlugin.class,
               SkynetGuiPlugin.PLUGIN_ID, false);
      }
   }

   public static void preview(final List<Artifact> artifacts, IProgressMonitor monitor, final VariableMap options) throws OseeCoreException {
      if (artifacts.size() == 1) {
         preview(artifacts.get(0), monitor, options);
      } else {
         HashCollection<IRenderer, Artifact> rendererArtifactMap =
               createRenderMap(PresentationType.PREVIEW, artifacts, options);

         for (IRenderer renderer : rendererArtifactMap.keySet()) {
            renderer.preview((LinkedList<Artifact>) rendererArtifactMap.getValues(renderer), monitor);
         }
      }
   }

   public static void edit(final List<Artifact> artifacts, IProgressMonitor monitor) throws OseeCoreException {
      edit(artifacts, monitor, null);
   }

   public static void edit(final List<Artifact> artifacts, IProgressMonitor monitor, final VariableMap options) throws OseeCoreException {
      HashCollection<IRenderer, Artifact> rendererArtifactMap =
            createRenderMap(PresentationType.EDIT, artifacts, options);

      for (IRenderer renderer : rendererArtifactMap.keySet()) {
         renderer.edit((LinkedList<Artifact>) rendererArtifactMap.getValues(renderer), monitor);
      }
   }

   public static void editInJob(final List<Artifact> artifacts) throws OseeCoreException {
      editInJob(artifacts, null);
   }

   public static void editInJob(final List<Artifact> artifacts, final VariableMap options) throws OseeCoreException {
      if (ArtifactGuis.checkOtherEdit(artifacts)) {
         if (artifacts.size() == 1) {
            editInJob(artifacts.get(0), options);
         } else {
            IExceptionableRunnable runnable = new IExceptionableRunnable() {
               public void run(IProgressMonitor monitor) throws OseeCoreException {
                  edit(artifacts, monitor, options);
               }
            };

            Jobs.run("Edit " + artifacts.size() + " artifacts", runnable, SkynetGuiPlugin.class,
                  SkynetGuiPlugin.PLUGIN_ID);
         }
      }
   }

   public static void editInJob(final Artifact artifact) throws OseeCoreException {
      editInJob(artifact, null);
   }

   public static void editInJob(final Artifact artifact, final VariableMap options) throws OseeCoreException {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public void run(IProgressMonitor monitor) throws Exception {
            getBestRenderer(PresentationType.EDIT, artifact, options).edit(artifact, monitor);
         }
      };

      Jobs.run("Edit " + artifact.getDescriptiveName(), runnable, SkynetGuiPlugin.class, SkynetGuiPlugin.PLUGIN_ID);
   }

   public static String merge(Artifact baseVersion, Artifact newerVersion, String fileName, boolean show) throws OseeStateException, OseeCoreException {
      return merge(baseVersion, newerVersion, null, fileName, show);
   }

   public static String merge(Artifact baseVersion, Artifact newerVersion, IProgressMonitor monitor, String fileName, boolean show) throws OseeStateException, OseeCoreException {
      return getBestRenderer(PresentationType.MERGE, baseVersion, new VariableMap("fileName", fileName)).compare(
            baseVersion, newerVersion, monitor, PresentationType.MERGE, show);
   }

   public static String merge(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, String fileName, boolean show) throws OseeCoreException {
      return getBestRenderer(PresentationType.MERGE_EDIT, baseVersion, new VariableMap("fileName", fileName)).compare(
            baseVersion, newerVersion, baseFile, newerFile, PresentationType.MERGE_EDIT, show);
   }

   public static void diffInJob(final Artifact baseVersion, final Artifact newerVersion) {
      diffInJob(baseVersion, newerVersion, null);
   }

   public static void diffInJob(final Artifact baseVersion, final Artifact newerVersion, final VariableMap options) {

      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public void run(IProgressMonitor monitor) throws OseeCoreException {
            diff(baseVersion, newerVersion, true, options);
         }
      };

      String jobName =
            "Compare " + (baseVersion == null ? " new " : baseVersion.getDescriptiveName()) + " to " + (newerVersion == null ? " delete " : newerVersion.getDescriptiveName());
      Jobs.run(jobName, runnable, SkynetGuiPlugin.class, SkynetGuiPlugin.PLUGIN_ID);

   }

   public static String diff(final Artifact baseVersion, final Artifact newerVersion, IProgressMonitor monitor, boolean show) throws OseeCoreException {
      return diff(baseVersion, newerVersion, monitor, show, null);
   }

   public static String diff(final Artifact baseVersion, final Artifact newerVersion, IProgressMonitor monitor, boolean show, final VariableMap options) throws OseeCoreException {
      // To handle comparisons with new or deleted artifacts
      Artifact artifactToSelectRender = baseVersion == null ? newerVersion : baseVersion;
      IRenderer renderer = getBestRenderer(PresentationType.DIFF, artifactToSelectRender, options);
      return renderer.compare(baseVersion, newerVersion, new NullProgressMonitor(), PresentationType.DIFF, show);
   }

   public static String diff(final Artifact baseVersion, final Artifact newerVersion, boolean show) throws OseeCoreException {
      return diff(baseVersion, newerVersion, show, null);
   }

   public static String diff(final Artifact baseVersion, final Artifact newerVersion, boolean show, final VariableMap options) throws OseeCoreException {
      return diff(baseVersion, newerVersion, new NullProgressMonitor(), show, options);
   }

   public static void diffInJob(final List<Artifact> baseArtifacts, final List<Artifact> newerArtifacts) {
      diffInJob(baseArtifacts, newerArtifacts, null);
   }

   public static void diffInJob(final List<Artifact> baseArtifacts, final List<Artifact> newerArtifacts, final VariableMap options) {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public void run(IProgressMonitor monitor) throws OseeCoreException {
            Artifact sampleArtifact = baseArtifacts.get(0) == null ? newerArtifacts.get(0) : baseArtifacts.get(0);
            IRenderer renderer = getBestRenderer(PresentationType.DIFF, sampleArtifact, options);
            renderer.compareArtifacts(baseArtifacts, newerArtifacts, monitor, sampleArtifact.getBranch(),
                  PresentationType.DIFF);
         }
      };
      Jobs.run("Combined Diff", runnable, SkynetGuiPlugin.class, SkynetGuiPlugin.PLUGIN_ID);
   }

   public static void previewInComposite(final BrowserComposite previewComposite, final Artifact artifact) {
      previewInComposite(previewComposite, artifact, null);
   }

   public static void previewInComposite(final BrowserComposite previewComposite, final Artifact artifact, final VariableMap options) {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public void run(IProgressMonitor monitor) throws Exception {
            IRenderer renderer = getBestRenderer(PresentationType.PREVIEW_IN_COMPOSITE, artifact, options);
            final String url = renderer.getArtifactUrl(artifact);
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  previewComposite.setUrl(url);
               }
            });
         }
      };

      Jobs.run("Preview " + artifact.getDescriptiveName(), runnable, SkynetGuiPlugin.class, SkynetGuiPlugin.PLUGIN_ID,
            false);
   }

   public static String renderToHtml(Artifact artifact) throws OseeCoreException {
      return renderToHtml(artifact, null);
   }

   public static String renderToHtml(Artifact artifact, VariableMap options) throws OseeCoreException {
      return getBestRenderer(PresentationType.PREVIEW_IN_COMPOSITE, artifact, options).generateHtml(artifact);
   }
}