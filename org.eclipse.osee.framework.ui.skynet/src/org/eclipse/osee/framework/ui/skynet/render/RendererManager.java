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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.IExceptionableRunnable;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.BrowserComposite;
import org.osgi.framework.Bundle;

/**
 * @author Ryan D. Brooks
 */
public class RendererManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(RendererManager.class);
   private static final RendererManager instance = new RendererManager();
   private static final ConfigurationPersistenceManager configurationManager =
         ConfigurationPersistenceManager.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private final HashMap<String, IRenderer> renderers;
   private HashCollection<ArtifactSubtypeDescriptor, IRenderer> applicableArtifactSubTypes;

   private RendererManager() {
      renderers = new HashMap<String, IRenderer>(40);
      registerRenders();
   }

   public static RendererManager getInstance() {
      return instance;
   }

   private void registerRendersFromExtensionPoints() {
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.ui.skynet.ArtifactRenderer");
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement element : elements) {
            if (element.getName().equals("Renderer")) {
               classname = element.getAttribute("classname");
               bundleName = element.getContributor().getName();

               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> renderClass = bundle.loadClass(classname);
                     Object obj = renderClass.newInstance();
                     Renderer renderer = (Renderer) obj;
                     renderer.setId(extension.getUniqueIdentifier());

                     renderers.put(renderer.getId(), renderer);

                     String applicableArtifactSubType = element.getAttribute("ApplicableArtifactSubtype");
                     if (applicableArtifactSubType != null) {
                        ArtifactSubtypeDescriptor artifactSubtype =
                              configurationManager.getArtifactSubtypeDescriptor(applicableArtifactSubType,
                                    branchManager.getCommonBranch());
                        applicableArtifactSubTypes.put(artifactSubtype, renderer);
                     }
                  } catch (Exception ex) {
                     logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                  } catch (NoClassDefFoundError er) {
                     logger.log(
                           Level.WARNING,
                           "Failed to find a class definition for " + classname + ", registered from bundle " + bundleName,
                           er);
                  }
               }
            }
         }
      }
   }

   public List<IRenderer> getValidRenderers(PresentationType presentationType, Artifact artifact) {
      List<IRenderer> rendererList = new LinkedList<IRenderer>();

      for (IRenderer renderer : renderers.values()) {
         if (renderer.getApplicabilityRating(presentationType, artifact) > IRenderer.NO_MATCH) {
            rendererList.add(renderer);
         }
      }

      if (rendererList.isEmpty()) throw new IllegalStateException(
            "At least the DefaultArtifactRenderer should have been found.");

      return rendererList;
   }

   public IRenderer getBestRenderer(PresentationType presentationType, Artifact artifact) {
      IRenderer bestRenderer = null;
      int bestRating = IRenderer.NO_MATCH;
      for (IRenderer renderer : renderers.values()) {
         int rating = renderer.getApplicabilityRating(presentationType, artifact);
         if (rating > bestRating) {
            bestRenderer = renderer;
            bestRating = rating;
         }
      }
      if (bestRenderer == null) {
         throw new IllegalStateException("At least the DefaultArtifactRenderer should have been found.");
      }
      return bestRenderer;
   }

   public IRenderer getRendererById(String rendererId) {
      return renderers.get(rendererId);
   }

   /**
    * Maps all renderes in the system to their applicable artifact types
    */
   private void registerRenders() {
      registerRendersFromExtensionPoints();
   }

   private HashCollection<IRenderer, Artifact> createRenderMap(PresentationType presentationType, List<Artifact> artifacts) {
      HashCollection<IRenderer, Artifact> rendererArtifactMap =
            new HashCollection<IRenderer, Artifact>(false, LinkedList.class);
      for (Artifact artifact : artifacts) {
         rendererArtifactMap.put(getBestRenderer(presentationType, artifact), artifact);
      }
      return rendererArtifactMap;
   }

   public void previewInJob(final List<Artifact> artifacts) {
      previewInJob(artifacts, null);
   }

   public void previewInJob(final List<Artifact> artifacts, final String option) {
      if (artifacts.size() == 1) {
         previewInJob(artifacts.get(0), option);
      } else {
         IExceptionableRunnable runnable = new IExceptionableRunnable() {
            public void run(IProgressMonitor monitor) throws Exception {
               HashCollection<IRenderer, Artifact> rendererArtifactMap =
                     createRenderMap(PresentationType.PREVIEW, artifacts);

               for (IRenderer renderer : rendererArtifactMap.keySet()) {
                  renderer.preview((LinkedList<Artifact>) rendererArtifactMap.getValues(renderer), option, monitor);
               }
            }
         };

         Jobs.run("Preview " + artifacts.size() + " artifacts", runnable, logger, SkynetGuiPlugin.PLUGIN_ID, false);
      }
   }

   public void editInJob(final List<Artifact> artifacts) {
      editInJob(artifacts, null);
   }

   public void editInJob(final List<Artifact> artifacts, final String option) {
      if (ArtifactGuis.checkOtherEdit(artifacts)) {
         if (artifacts.size() == 1) {
            editInJob(artifacts.get(0), option);
         } else {
            IExceptionableRunnable runnable = new IExceptionableRunnable() {
               public void run(IProgressMonitor monitor) throws Exception {
                  HashCollection<IRenderer, Artifact> rendererArtifactMap =
                        createRenderMap(PresentationType.EDIT, artifacts);

                  for (IRenderer renderer : rendererArtifactMap.keySet()) {
                     renderer.edit((LinkedList<Artifact>) rendererArtifactMap.getValues(renderer), option, monitor);
                  }
               }
            };

            Jobs.run("Edit " + artifacts.size() + " artifacts", runnable, logger, SkynetGuiPlugin.PLUGIN_ID);
         }
      }
   }

   public void editInJob(Artifact artifact) {
      editInJob(artifact, null);
   }

   public void editInJob(final Artifact artifact, final String option) {
      editInJob(getBestRenderer(PresentationType.EDIT, artifact), artifact, option);
   }

   public void editInJobWith(String rendererId, final Artifact artifact, final String option) {
      editInJob(getRendererById(rendererId), artifact, option);
   }

   private void editInJob(final IRenderer renderer, final Artifact artifact, final String option) {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public void run(IProgressMonitor monitor) throws Exception {
            renderer.edit(artifact, option, monitor);
         }
      };

      Jobs.run("Edit " + artifact.getDescriptiveName(), runnable, logger, SkynetGuiPlugin.PLUGIN_ID);
   }

   public void previewInJob(final Artifact artifact) {
      previewInJob(artifact, null);
   }

   public void previewInJobWith(String rendererId, final Artifact artifact, final String option) {
      previewInJob(getRendererById(rendererId), artifact, option);
   }

   public void previewInJob(final Artifact artifact, final String option) {
      previewInJob(getBestRenderer(PresentationType.PREVIEW, artifact), artifact, option);
   }

   public void previewInJob(final IRenderer renderer, final Artifact artifact, final String option) {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public void run(IProgressMonitor monitor) throws Exception {
            renderer.preview(artifact, option, monitor);
         }
      };

      Jobs.run("Preview " + artifact.getDescriptiveName(), runnable, logger, SkynetGuiPlugin.PLUGIN_ID, false);
   }

   public void compareInJob(Artifact baseVersion, Artifact newerVersion) throws Exception {
      compareInJob(baseVersion, newerVersion, null);
   }

   public void compareInJob(final Artifact baseVersion, final Artifact newerVersion, final String option) throws Exception {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public void run(IProgressMonitor monitor) throws Exception {

            // To handle comparisons with new or deleted artifacts
            Artifact artifactToSelectRender = baseVersion == null ? newerVersion : baseVersion;

            getBestRenderer(PresentationType.DIFF, artifactToSelectRender).compare(baseVersion, newerVersion, option,
                  monitor);
         }
      };

      Jobs.run(
            "Compare " + (baseVersion == null ? " new " : baseVersion.getDescriptiveName()) + " to " + (newerVersion == null ? " delete " : newerVersion.getDescriptiveName()),
            runnable, logger, SkynetGuiPlugin.PLUGIN_ID);
   }

   public void previewInComposite(final BrowserComposite previewComposite, final Artifact artifact) {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public void run(IProgressMonitor monitor) throws Exception {

            IRenderer renderer = getBestRenderer(PresentationType.PREVIEW_IN_COMPOSITE, artifact);
            final String url = renderer.getArtifactUrl(artifact);
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  previewComposite.setUrl(url);
               }
            });

         }
      };

      Jobs.run("Preview " + artifact.getDescriptiveName(), runnable, logger, SkynetGuiPlugin.PLUGIN_ID, false);
   }
}