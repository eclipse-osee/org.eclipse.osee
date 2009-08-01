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
package org.eclipse.osee.ote.ui.define.viewers.actions;

import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;
import org.eclipse.osee.ote.ui.define.utilities.SelectionHelper;

/**
 * @author Theron Virgin
 */
public class EditDispositionAction extends AbstractActionHandler {

   public EditDispositionAction(StructuredViewer viewer, String text) throws Exception {
      super(viewer, text);
   }

   public EditDispositionAction(StructuredViewer viewer, String text, ImageDescriptor image) throws Exception {
      super(viewer, text, image);
   }

   @Override
   public void run() {
      try {
         TestRunOperator operator = SelectionHelper.getInstance().getSelection(getViewer());
         if (isValidSelection(operator)) {
            Artifact artifact = operator.getTestRunArtifact();
            checkPermissions(artifact);
            ArtifactEditor.editArtifact(artifact);
         }
      } catch (Exception ex) {
         OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, "Unable to open artifact.", ex);
      }
   }

   @Override
   public void updateState() throws OseeCoreException {
      TestRunOperator operator = SelectionHelper.getInstance().getSelection(getViewer());
      setEnabled(isValidSelection(operator));
   }

   private boolean isValidSelection(TestRunOperator operator) throws OseeCoreException {
      return operator != null && operator.hasValidArtifact() && operator.isFromLocalWorkspace() != true;
   }

   private void checkPermissions(Artifact artifact) throws OseeCoreException {
      if (true != AccessControlManager.hasPermission(artifact, PermissionEnum.READ)) {
         throw new OseeArgumentException(String.format("The user %s does not have read access to %s",
               UserManager.getUser(), artifact));
      }
   }
}
