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
package org.eclipse.osee.framework.ui.skynet.artifact;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceURL;
import org.eclipse.osee.framework.ui.skynet.ArtifactDragDropSupport;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 */
public class OpenArtifactAction implements IObjectActionDelegate {
   private IWorkbenchPart targetPart;
   private Shell shell;

   public void setActivePart(IAction action, IWorkbenchPart targetPart) {
      this.targetPart = targetPart;
      this.shell = targetPart.getSite().getShell();
   }

   public void run(IAction action) {
      IStructuredSelection sel = (IStructuredSelection) targetPart.getSite().getSelectionProvider().getSelection();
      for (Object object : sel.toList()) {
         if (object instanceof IResource) {
            String path = WorkspaceURL.getURL((IResource) object);
            Artifact artifact = null;

            try {
               artifact = ArtifactDragDropSupport.getArtifactFromWorkspaceFile(path, shell);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }

            if (artifact != null) {
               ArtifactEditor.editArtifact(artifact);
            }
         } else {
            MessageDialog.openInformation(targetPart.getSite().getShell(), "Open Associated Artifact",
                  "Type " + object.getClass() + " not handeled.");
            return;
         }
      }
   }

   public void selectionChanged(IAction action, ISelection selection) {
   }
}
