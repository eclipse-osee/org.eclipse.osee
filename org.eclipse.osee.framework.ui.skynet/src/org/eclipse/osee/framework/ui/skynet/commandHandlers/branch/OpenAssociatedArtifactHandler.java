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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;

/**
 * @author Jeff C. Phillips
 */
public class OpenAssociatedArtifactHandler extends CommandHandler {

   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      Branch selectedBranch = Handlers.getBranchesFromStructuredSelection(selection).iterator().next();

      try {
         Artifact associatedArtifact = selectedBranch.getAssociatedArtifact().getFullArtifact();
         if (associatedArtifact == null) {
            AWorkbench.popup("Open Associated Artifact", "No artifact associated with branch " + selectedBranch);
            return null;
         }
         if (AccessControlManager.hasPermission(associatedArtifact, PermissionEnum.READ)) {
            if (associatedArtifact instanceof IATSArtifact) {
               OseeAts.openATSArtifact(associatedArtifact);
            } else {
               ArtifactEditor.editArtifact(associatedArtifact);
            }
         } else {
            OseeLog.log(
                  SkynetGuiPlugin.class,
                  OseeLevel.SEVERE_POPUP,
                  "The user " + UserManager.getUser() + " does not have read access to " + selectedBranch.getAssociatedArtifact());
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      if (AWorkbench.getActivePage() == null) {
         return false;
      }
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      List<Branch> branches = Handlers.getBranchesFromStructuredSelection(selection);
      return branches.size() == 1;
   }
}