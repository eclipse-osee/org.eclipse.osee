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

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.SPECIALIZED_EDIT;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.dsl.ui.integration.internal.DslUiIntegrationConstants;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Jonathan E. Jensen
 */
public abstract class AbstractDslRenderer extends FileSystemRenderer {

   protected static final String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.render.dsl.editor.command";

   protected AbstractDslRenderer() {
      super();
   }

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
    * Provides a list of the artifact types that the renderer supports.
    * 
    * @return a list of the artifact types that the renderer supports
    */
   protected abstract IArtifactType[] getArtifactTypeMatches();

   /**
    * Provides the command ID associated with this renderer and adds it to the commandGroup.
    */
   @Override
   public List<String> getCommandIds(CommandGroup commandGroup) {
      ArrayList<String> commandIds = new ArrayList<String>(1);
      if (commandGroup.isEdit()) {
         commandIds.add(COMMAND_ID);
      }

      return commandIds;
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
   protected abstract IOperation getUpdateOperation(File file, List<Artifact> artifacts, IOseeBranch branch, PresentationType presentationType);

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
   public int getApplicabilityRating(PresentationType presentationType, IArtifact artifact) throws OseeCoreException {
      Artifact aArtifact = artifact.getFullArtifact();
      if (!presentationType.matches(GENERALIZED_EDIT, PRODUCE_ATTRIBUTE) && !aArtifact.isHistorical()) {
         if (aArtifact.isOfType(getArtifactTypeMatches())) {
            return ARTIFACT_TYPE_MATCH;
         }
      }
      return NO_MATCH;
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
                     IDE.openEditor(page, file);
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
}
