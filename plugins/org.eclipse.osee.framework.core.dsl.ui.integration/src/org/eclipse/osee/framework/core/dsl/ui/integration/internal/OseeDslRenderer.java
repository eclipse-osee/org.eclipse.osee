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
package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.GENERALIZED_EDIT;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.core.dsl.integration.util.OseeUtil;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IArtifactUpdateOperationFactory;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Roberto E. Escobar
 */
public final class OseeDslRenderer extends FileSystemRenderer {

   private static final String COMMAND_ID = "org.eclipse.osee.framework.core.dsl.OseeDsl.editor.command";

   private static final class OseeDslArtifactUpdateOperationFactory implements IArtifactUpdateOperationFactory {

      @SuppressWarnings("unused")
      @Override
      public IOperation createUpdateOp(File file) throws OseeCoreException {
         return new OseeDslArtifactUpdateOperation(file);
      }
   };

   public OseeDslRenderer() {
      super(new OseeDslArtifactUpdateOperationFactory());
   }

   @Override
   public String getName() {
      return "OseeDsl Editor";
   }

   @Override
   public DefaultArtifactRenderer newInstance() {
      return new OseeDslRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if (presentationType != GENERALIZED_EDIT && !artifact.isHistorical()) {
         if (artifact.isOfType(CoreArtifactTypes.AccessControlModel)) {
            return SUBTYPE_TYPE_MATCH;
         }
      }
      return NO_MATCH;
   }

   @Override
   public Image getImage(Artifact artifact) {
      return super.getImage(artifact);
   }

   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(1);
      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add(COMMAND_ID);
      }
      return commandIds;
   }

   @Override
   public boolean supportsCompare() {
      return true;
   }

   @SuppressWarnings("unused")
   @Override
   public void open(final List<Artifact> artifacts, final PresentationType presentationType) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (!artifacts.isEmpty()) {
               try {
                  IFile file = getRenderedFile(artifacts, presentationType);
                  if (file != null) {
                     IWorkbench workbench = PlatformUI.getWorkbench();
                     IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
                     IDE.openEditor(page, file);
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               } catch (PartInitException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
   }

   @SuppressWarnings("unused")
   @Override
   public String getAssociatedExtension(Artifact artifact) throws OseeCoreException {
      return "osee";
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException {
      Artifact artifact = artifacts.iterator().next();
      StringBuilder builder = new StringBuilder();
      builder.append(OseeUtil.getOseeDslArtifactSource(artifact));
      builder.append("\n");
      builder.append(artifact.getSoleAttributeValueAsString(CoreAttributeTypes.GeneralStringData, ""));
      InputStream inputStream = null;
      try {
         inputStream = new ByteArrayInputStream(builder.toString().getBytes("UTF-8"));
      } catch (UnsupportedEncodingException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return inputStream;
   }

   @Override
   public Program getAssociatedProgram(Artifact artifact) throws OseeCoreException {
      throw new OseeCoreException("should not be called");
   }
}
