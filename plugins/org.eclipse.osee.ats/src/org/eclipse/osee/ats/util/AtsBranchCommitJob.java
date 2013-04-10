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
package org.eclipse.osee.ats.util;

import java.util.Date;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.core.client.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.editor.stateItem.AtsStateItemManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit.CommitHandler;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchCommitJob extends Job {
   private final boolean commitPopup;
   private final boolean overrideStateValidation;
   private final Branch destinationBranch;
   private final boolean archiveWorkingBranch;
   private final TeamWorkFlowArtifact teamArt;

   public AtsBranchCommitJob(TeamWorkFlowArtifact teamArt, boolean commitPopup, boolean overrideStateValidation, Branch destinationBranch, boolean archiveWorkingBranch) {
      super("Commit Branch");
      this.teamArt = teamArt;
      this.commitPopup = commitPopup;
      this.overrideStateValidation = overrideStateValidation;
      this.destinationBranch = destinationBranch;
      this.archiveWorkingBranch = archiveWorkingBranch;
   }

   private boolean adminOverride;

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      Branch workflowWorkingBranch = null;
      try {
         workflowWorkingBranch = teamArt.getWorkingBranch();
         AtsBranchManagerCore.branchesInCommit.add(workflowWorkingBranch);
         if (workflowWorkingBranch == null) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
               "Commit Branch Failed: Can not locate branch for workflow " + teamArt.getHumanReadableId());
         }

         // Confirm that all blocking reviews are completed
         // Loop through this state's blocking reviews to confirm complete
         if (teamArt.isTeamWorkflow()) {
            for (AbstractReviewArtifact reviewArt : ReviewManager.getReviewsFromCurrentState(teamArt)) {
               if (reviewArt.getReviewBlockType() == ReviewBlockType.Commit && !reviewArt.isCompletedOrCancelled()) {
                  return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                     "Blocking Review must be completed before commit.");
               }
            }
         }

         if (!overrideStateValidation) {
            adminOverride = false;
            // Check extension points for valid commit
            for (IAtsStateItem item : AtsStateItemManager.getStateItems()) {
               final Result tempResult = item.committing(teamArt);
               if (tempResult.isFalse()) {
                  // Allow Admin to override state validation
                  if (AtsUtilCore.isAtsAdmin()) {
                     Displays.pendInDisplayThread(new Runnable() {
                        @Override
                        public void run() {
                           if (MessageDialog.openConfirm(Displays.getActiveShell(), "Override State Validation",
                              tempResult.getText() + "\n\nYou are set as Admin, OVERRIDE this?")) {
                              adminOverride = true;
                           } else {
                              adminOverride = false;
                           }
                        }
                     });
                  }
                  if (!adminOverride) {
                     return new Status(IStatus.ERROR, Activator.PLUGIN_ID, tempResult.getText());
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
            BranchManager.commitBranch(null, conflictManager, archiveWorkingBranch, true);
            branchCommitted = true;
         }
         if (branchCommitted) {
            // Create reviews as necessary
            SkynetTransaction transaction =
               TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Create Reviews upon Commit");
            AtsBranchManagerCore.createNecessaryBranchEventReviews(StateEventType.CommitBranch, teamArt, new Date(),
               AtsCoreUsers.SYSTEM_USER, transaction);
            transaction.execute();
         }
      } catch (OseeCoreException ex) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Strings.truncate(ex.getLocalizedMessage(), 250, true),
            ex);
      } finally {
         if (workflowWorkingBranch != null) {
            AtsBranchManagerCore.branchesInCommit.remove(workflowWorkingBranch);
         }
      }
      return Status.OK_STATUS;
   }

}