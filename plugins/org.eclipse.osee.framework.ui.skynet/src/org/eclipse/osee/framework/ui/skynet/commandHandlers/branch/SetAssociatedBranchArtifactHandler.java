/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class SetAssociatedBranchArtifactHandler extends CommandHandler {

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      BranchToken selectedBranch = Handlers.getBranchesFromStructuredSelection(selection).iterator().next();
      ArtifactId artId = BranchManager.getAssociatedArtifactId(selectedBranch);
      EntryDialog ed = new EntryDialog("Set Associated Artifact",
         "Set Associated Artifact for Branch\n\n\"" + selectedBranch.getName() + "\"\n\nCurrently: " + artId + "\n\nEnter new Artifact Id to associate:");
      ed.setEntry(artId.getIdString());
      if (ed.open() == 0) {
         ArtifactId newArtId = ArtifactId.valueOf(ed.getEntry());
         Artifact associatedArtifact = ArtifactQuery.getArtifactFromId(newArtId, COMMON);
         if (MessageDialog.openConfirm(Displays.getActiveShell(), "Set Associated Artifact",
            "Set Associated Artifact for Branch\n\n\"" + selectedBranch.getName() + "\"\nto\nArtifact: " + associatedArtifact)) {
            BranchManager.setAssociatedArtifactId(selectedBranch, newArtId);
         }
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<? extends BranchId> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      return branches.size() == 1 && ServiceUtil.accessControlService().isOseeAdmin();
   }
}