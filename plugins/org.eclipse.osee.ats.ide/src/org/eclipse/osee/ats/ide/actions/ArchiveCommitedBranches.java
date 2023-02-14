/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ArchiveCommitedBranches extends AbstractAtsAction {

   private static final String MSG = "Archive Branch(es)";
   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private boolean prompt = true;
   private final boolean executeInCurrentThread;

   public ArchiveCommitedBranches(ISelectedAtsArtifacts selectedAtsArtifacts, boolean executeInCurrentThread) {
      super(MSG, ImageManager.getImageDescriptor(FrameworkImage.ARCHIVE));
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setToolTipText(getText());
      this.executeInCurrentThread = executeInCurrentThread;
   }

   @Override
   public void runWithException() {
      XResultData rd = new XResultData();
      rd.log(getText() + "\n\n");
      List<Branch> toCommit = new ArrayList<>();
      Set<Artifact> artifacts = selectedAtsArtifacts.getSelectedWorkflowArtifacts();
      for (Artifact art : artifacts) {
         if (art instanceof IAtsTeamWorkflow) {
            Branch branch = (Branch) AtsApiService.get().getBranchService().getBranch((IAtsTeamWorkflow) art);
            if (branch == null || branch.isInvalid()) {
               rd.logf("Team Workflow has no branch: %s\n", art.toStringWithId());
            } else {
               if (branch.isArchived()) {
                  rd.logf("Team Workflow branch already archived: %s\n", art.toStringWithId());
               } else {
                  if (!branch.getBranchState().isCommitted()) {
                     rd.logf("Team Workflow branch not Committed: %s\n", art.toStringWithId());
                  } else {
                     toCommit.add(branch);
                  }
               }
            }
         } else {
            rd.logf("Not a Team Workflow: %s\n", art.toStringWithId());
         }
      }
      if (toCommit.isEmpty()) {
         rd.logf("\nNothing to Archive\n");
      } else {
         rd.logf("\n%s Found to Archive", toCommit.size());
         String msg =
            String.format("Archive %s Branches\n\nWARNING: Not recommended to Archive more than a couple at a time\n\n" //
               + "Note: This operation will run in the background and a delay\n" //
               + "will be inserted between Archive so DB and backups are not overwhelmed.\n\nDo NOT shut your OSEE down.",
               toCommit.size());
         if (MessageDialog.openConfirm(Displays.getActiveShell(), getText(), msg + "\n\nAre you sure?")) {
            AWorkbench.popup("Archiving " + toCommit.size());
            for (Branch branch : toCommit) {
               AtsApiService.get().getBranchService().archiveBranch(branch);
               rd.logf("\nArchived: %s", branch.toStringWithId());
               System.err.println("\nArchived: " + branch.toStringWithId());
               try {
                  // Delay to not overwhelm database
                  Thread.sleep(1 * 60 * 1000);
               } catch (InterruptedException ex) {
                  //
               }
            }
         } else {
            rd.log("\n\nOperation Cancelled\n");
         }
      }
      XResultDataUI.report(rd, getText());
   }

   public void setPrompt(boolean prompt) {
      this.prompt = prompt;
   }
}
