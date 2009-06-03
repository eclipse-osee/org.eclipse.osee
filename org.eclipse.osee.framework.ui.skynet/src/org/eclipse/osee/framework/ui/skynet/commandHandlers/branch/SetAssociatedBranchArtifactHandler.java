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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 */
public class SetAssociatedBranchArtifactHandler extends CommandHandler {

   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      Branch selectedBranch = Handlers.getBranchesFromStructuredSelection(selection).iterator().next();

      try {
         EntryDialog ed =
               new EntryDialog(
                     "Set Associated Artifact",
                     "Set Associated Artifact for Branch\n\n\"" + selectedBranch.getBranchName() + "\"" + (selectedBranch.getAssociatedArtifact() != null ? "\n\nCurrently: " + selectedBranch.getAssociatedArtifact() : ""));
         if (selectedBranch.getAssociatedArtifact() != null) ed.setEntry(String.valueOf(selectedBranch.getAssociatedArtifactId()));
         if (ed.open() == 0) {
            String artId = ed.getEntry();
            Artifact associatedArtifact =
                  ArtifactQuery.getArtifactFromId(Integer.parseInt(artId), BranchManager.getCommonBranch());
            if (MessageDialog.openConfirm(
                  Display.getCurrent().getActiveShell(),
                  "Set Associated Artifact",
                  "Set Associated Artifact for Branch\n\n\"" + selectedBranch.getBranchName() + "\"\nto\nArtifact: " + associatedArtifact)) {
               selectedBranch.setAssociatedArtifact(associatedArtifact);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      if (AWorkbench.getActivePage() == null) return false;
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      List<Branch> branches = Handlers.getBranchesFromStructuredSelection(selection);
      return branches.size() == 1 && AccessControlManager.isOseeAdmin();
   }
}