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
package org.eclipse.osee.ats.branch.internal;

import java.util.Date;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.branch.AtsBranchUtil;
import org.eclipse.osee.ats.editor.stateItem.AtsStateItemManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.workflow.review.ReviewManager;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit.CommitHandler;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchCommitOperation extends AbstractOperation {
   private final boolean commitPopup;
   private final boolean overrideStateValidation;
   private final BranchId destinationBranch;
   private final boolean archiveWorkingBranch;
   private final TeamWorkFlowArtifact teamArt;

   public AtsBranchCommitOperation(TeamWorkFlowArtifact teamArt, boolean commitPopup, boolean overrideStateValidation, BranchId destinationBranch, boolean archiveWorkingBranch) {
      super("Commit Branch", Activator.PLUGIN_ID);
      this.teamArt = teamArt;
      this.commitPopup = commitPopup;
      this.overrideStateValidation = overrideStateValidation;
      this.destinationBranch = destinationBranch;
      this.archiveWorkingBranch = archiveWorkingBranch;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      BranchId workflowWorkingBranch = teamArt.getWorkingBranch();
      try {
         AtsClientService.get().getBranchService().getBranchesInCommit().add(workflowWorkingBranch);
         if (workflowWorkingBranch.isInvalid()) {
            throw new OseeStateException("Commit Branch Failed: Can not locate branch for workflow [%s]",
               teamArt.getAtsId());
         }

         // Confirm that all blocking reviews are completed
         // Loop through this state's blocking reviews to confirm complete
         if (teamArt.isTeamWorkflow()) {
            for (IAtsAbstractReview review : ReviewManager.getReviewsFromCurrentState(teamArt)) {
               AbstractReviewArtifact reviewArt =
                  (AbstractReviewArtifact) AtsClientService.get().getQueryService().getArtifact(review);
               if (reviewArt.getReviewBlockType() == ReviewBlockType.Commit && !reviewArt.isCompletedOrCancelled()) {
                  throw new OseeStateException("Blocking Review must be completed before commit.");
               }
            }
         }

         if (!overrideStateValidation) {
            final MutableBoolean adminOverride = new MutableBoolean(false);
            // Check extension points for valid commit
            for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
               final Result tempResult = item.committing(teamArt);
               if (tempResult.isFalse()) {
                  // Allow Admin to override state validation
                  if (AtsClientService.get().getUserService().isAtsAdmin()) {
                     Displays.pendInDisplayThread(new Runnable() {
                        @Override
                        public void run() {
                           if (MessageDialog.openConfirm(Displays.getActiveShell(), "Override State Validation",
                              tempResult.getText() + "\n\nYou are set as Admin, OVERRIDE this?")) {
                              adminOverride.setValue(true);
                           } else {
                              adminOverride.setValue(false);
                           }
                        }
                     });
                  }
                  if (!adminOverride.getValue()) {
                     throw new OseeStateException(tempResult.getText());
                  }
               }
            }
         }

         boolean branchCommitted = false;
         ConflictManagerExternal conflictManager =
            new ConflictManagerExternal(destinationBranch, workflowWorkingBranch);

         if (commitPopup) {
            branchCommitted = CommitHandler.commitBranch(conflictManager, archiveWorkingBranch);
         } else {
            BranchManager.commitBranch(null, conflictManager, archiveWorkingBranch, false);
            branchCommitted = true;
         }
         if (branchCommitted) {
            // Create reviews as necessary
            IAtsChangeSet changes = AtsClientService.get().createChangeSet("Create Reviews upon Commit");
            boolean added = AtsBranchUtil.createNecessaryBranchEventReviews(StateEventType.CommitBranch, teamArt,
               new Date(), AtsCoreUsers.SYSTEM_USER, changes);
            if (added) {
               changes.execute();
            }
         }
      } finally {
         if (workflowWorkingBranch != null) {
            AtsClientService.get().getBranchService().getBranchesInCommit().remove(workflowWorkingBranch);
         }
      }
   }
}