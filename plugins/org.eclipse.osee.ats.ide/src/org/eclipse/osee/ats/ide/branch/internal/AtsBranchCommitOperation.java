/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.branch.internal;

import java.util.Date;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit.CommitHandler;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchCommitOperation {
   private final boolean commitPopup;
   private final boolean overrideStateValidation;
   private final BranchId destinationBranch;
   private final boolean archiveWorkingBranch;
   private final TeamWorkFlowArtifact teamArt;
   private XResultData rd;

   public AtsBranchCommitOperation(TeamWorkFlowArtifact teamArt, boolean commitPopup, boolean overrideStateValidation, BranchId destinationBranch, boolean archiveWorkingBranch, XResultData rd) {
      this.teamArt = teamArt;
      this.commitPopup = commitPopup;
      this.overrideStateValidation = overrideStateValidation;
      this.destinationBranch = destinationBranch;
      this.archiveWorkingBranch = archiveWorkingBranch;
      this.rd = rd;
   }

   protected XResultData run() {
      if (rd == null) {
         rd = new XResultData();
      }
      Branch branch = BranchManager.getBranch(destinationBranch);
      rd.logf("Committing %s to desination %s\n\n", teamArt.toStringWithId(), branch.toStringWithId());
      BranchId workflowWorkingBranch = teamArt.getWorkingBranch();
      try {
         AtsApiService.get().getBranchService().getBranchesInCommit().add(workflowWorkingBranch);
         if (workflowWorkingBranch.isInvalid()) {
            rd.errorf("Commit Branch Failed: Can not locate branch for workflow [%s]", teamArt.getAtsId());
            return rd;
         }

         /**
          * Confirm that all blocking reviews are completed. Loop through this state's blocking reviews to confirm
          * complete
          */
         for (IAtsAbstractReview review : ReviewManager.getReviewsFromCurrentState(teamArt)) {
            AbstractReviewArtifact reviewArt =
               (AbstractReviewArtifact) AtsApiService.get().getQueryService().getArtifact(review);
            if (reviewArt.getReviewBlockType() == ReviewBlockType.Commit && !reviewArt.isCompletedOrCancelled()) {
               rd.error(
                  "All blocking reviews must be completed before committing the working branch.  Please complete all blocking reviews in order to continue.");
               return rd;
            }
         }

         if (!overrideStateValidation) {
            final MutableBoolean adminOverride = new MutableBoolean(false);
            // Check extension points for valid commit
            for (IAtsWorkItemHook item : AtsApiService.get().getWorkItemService().getWorkItemHooks()) {
               rd = item.committing(teamArt, rd);
               if (rd.isErrors()) {
                  // Allow Admin to override state validation
                  if (AtsApiService.get().getUserService().isAtsAdmin()) {
                     Displays.pendInDisplayThread(new Runnable() {
                        @Override
                        public void run() {
                           String msg = rd.toString();
                           if (msg.length() > 512) {
                              msg = Strings.truncate(msg, 512, true);
                           }
                           if (MessageDialog.openConfirm(Displays.getActiveShell(), "Override State Validation",
                              msg + "\n\nYou are set as Admin, OVERRIDE this?")) {
                              adminOverride.setValue(true);
                           } else {
                              adminOverride.setValue(false);
                           }
                        }
                     });
                  }
                  if (!adminOverride.getValue()) {
                     return rd;
                  }
               }
            }
         }

         boolean branchCommitted = false;
         ConflictManagerExternal conflictManager =
            new ConflictManagerExternal(destinationBranch, workflowWorkingBranch);

         if (commitPopup) {
            boolean atsAdmin = AtsApiService.get().getUserService().isAtsAdmin();
            branchCommitted = CommitHandler.commitBranch(conflictManager, atsAdmin, archiveWorkingBranch);
         } else {
            BranchManager.commitBranch(null, conflictManager, archiveWorkingBranch, false);
            branchCommitted = true;
         }
         if (branchCommitted) {
            // Create reviews as necessary
            IAtsChangeSet changes = AtsApiService.get().createChangeSet("Create Reviews upon Commit");
            boolean added = AtsApiService.get().getBranchServiceIde().createNecessaryBranchEventReviews(
               StateEventType.CommitBranch, teamArt, new Date(), AtsCoreUsers.SYSTEM_USER, changes);
            if (added) {
               changes.execute();
            }
         }
      } finally {
         if (workflowWorkingBranch != null) {
            AtsApiService.get().getBranchService().getBranchesInCommit().remove(workflowWorkingBranch);
         }
      }
      return rd;
   }
}