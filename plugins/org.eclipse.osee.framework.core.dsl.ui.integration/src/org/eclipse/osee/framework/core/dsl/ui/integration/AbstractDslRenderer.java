/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.ui.integration;

import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.dsl.ui.integration.internal.DslUiIntegrationConstants;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Jonathan E. Jensen
 */
public abstract class AbstractDslRenderer extends FileSystemRenderer {

   public AbstractDslRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
   }

   public AbstractDslRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   private static final String DEFAULT_EDITOR_ID = "org.eclipse.osee.framework.core.dsl.OseeDsl";

   /**
    * Simple String name of the renderer
    */
   @Override
   public abstract String getName();

   /**
    * Provides an instance of the concrete class.
    */
   @Override
   public abstract DefaultArtifactRenderer newInstance();

   /**
    * Provides an instance of the concrete class.
    */
   @Override
   public abstract DefaultArtifactRenderer newInstance(Map<RendererOption, Object> rendererOptions);

   /**
    * Provides a list of the artifact types that the renderer supports.
    *
    * @return a list of the artifact types that the renderer supports
    */
   protected abstract IArtifactType[] getArtifactTypeMatches();

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      ImageDescriptor icon = ArtifactImageManager.getImageDescriptor(artifact);
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "DSL Editor", icon));
   }

   /**
    * Get the file extention associated with the dsl and related artifact.
    */
   @Override
   public abstract String getAssociatedExtension(Artifact artifact) throws OseeCoreException;

   /**
    * Provides a render input stream for the given input.
    */
   @Override
   public abstract InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException;

   /**
    * Provides the update operation for the renderer.
    */
   @Override
   protected abstract IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType);

   /**
    * Provides the minimum match ranking for this renderer.
    */
   @Override
   public abstract int minimumRanking();

   /**
    * This function can be overridden if the subclass does not support compare
    */
   @Override
   public boolean supportsCompare() {
      return true;
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      if (!presentationType.matches(GENERALIZED_EDIT, PRODUCE_ATTRIBUTE) && !artifact.isHistorical()) {
         if (artifact.isOfType(getArtifactTypeMatches())) {
            return ARTIFACT_TYPE_MATCH;
         }
      }
      return NO_MATCH;
   }

   protected String getEditorId() {
      return DEFAULT_EDITOR_ID;
   }

   @Override
   public final void open(final List<Artifact> artifacts, final PresentationType presentationType) {

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (!artifacts.isEmpty()) {
               try {
                  PresentationType onOpenPresentationType = getOnOpenPresentationType(presentationType);
                  IFile file = renderToFile(artifacts, onOpenPresentationType);
                  if (file != null) {
                     IWorkbench workbench = PlatformUI.getWorkbench();
                     IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
                     page.openEditor(new DslEditorInput(file, artifacts), getEditorId());
                  }
               } catch (Exception ex) {
                  OseeLog.log(DslUiIntegrationConstants.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
   }

   public PresentationType getOnOpenPresentationType(PresentationType presentationType) {
      return presentationType == DEFAULT_OPEN ? SPECIALIZED_EDIT : presentationType;
   }

   /**
    * This is required by the base class but not by this renderer. Therefore, we stub it out.
    */
   @Override
   public final Program getAssociatedProgram(Artifact artifact) throws OseeCoreException {
      throw new OseeCoreException("should not be called");
   }

   private static class DslEditorInput extends FileEditorInput {

      private final List<Artifact> artifacts;

      public DslEditorInput(IFile file, List<Artifact> artifacts) {
         super(file);
         this.artifacts = artifacts;
      }

      @Override
      public boolean equals(Object obj) {
         boolean toReturn = false;
         if (obj instanceof DslEditorInput) {
            List<Artifact> toCheck = ((DslEditorInput) obj).artifacts;
            toReturn = toCheck.size() == artifacts.size();
            if (toReturn) {
               for (Artifact art : toCheck) {
                  if (!artifacts.contains(art)) {
                     toReturn = false;
                     break;

                  }
               }
            }
         }
         return toReturn;
      }

   }
}
