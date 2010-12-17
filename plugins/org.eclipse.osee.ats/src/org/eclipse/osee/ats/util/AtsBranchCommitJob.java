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
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.AbstractReviewArtifact;
import org.eclipse.osee.ats.artifact.AbstractReviewArtifact.ReviewBlockType;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.stateItem.AtsStateItemManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.workflow.item.StateEventType;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit.CommitHandler;
import org.eclipse.osee.framework.ui.swt.Displays;

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
         AtsBranchManager.branchesInCommit.add(workflowWorkingBranch);
         if (workflowWorkingBranch == null) {
            return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID,
               "Commit Branch Failed: Can not locate branch for workflow " + teamArt.getHumanReadableId());
         }

         // Confirm that all blocking reviews are completed
         // Loop through this state's blocking reviews to confirm complete
         if (teamArt.isTeamWorkflow()) {
            for (AbstractReviewArtifact reviewArt : ReviewManager.getReviewsFromCurrentState(teamArt)) {
               if (reviewArt.getReviewBlockType() == ReviewBlockType.Commit && !reviewArt.isCompletedOrCancelled()) {
                  return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID,
                     "Blocking Review must be completed before commit.");
               }
            }
         }

         if (!overrideStateValidation) {
            adminOverride = false;
            // Check extension points for valid commit
            for (IAtsStateItem item : AtsStateItemManager.getStateItems(teamArt.getStateDefinition())) {
               final Result tempResult = item.committing(teamArt);
               if (tempResult.isFalse()) {
                  // Allow Admin to override state validation
                  if (AtsUtil.isAtsAdmin()) {
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
                     return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, tempResult.getText());
                  }
               }
            }
         }

         commit(commitPopup, workflowWorkingBranch, destinationBranch, archiveWorkingBranch);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, ex.getLocalizedMessage(), ex);
      } finally {
         if (workflowWorkingBranch != null) {
            AtsBranchManager.branchesInCommit.remove(workflowWorkingBranch);
         }
      }
      return Status.OK_STATUS;
   }

   private void commit(boolean commitPopup, Branch sourceBranch, Branch destinationBranch, boolean archiveWorkingBranch) throws OseeCoreException {
      boolean branchCommitted = false;
      ConflictManagerExternal conflictManager = new ConflictManagerExternal(destinationBranch, sourceBranch);

      if (commitPopup) {
         branchCommitted = CommitHandler.commitBranch(conflictManager, archiveWorkingBranch);
      } else {
         BranchManager.commitBranch(null, conflictManager, archiveWorkingBranch, true);
         branchCommitted = true;
      }
      if (branchCommitted) {
         // Create reviews as necessary
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Create Reviews upon Commit");
         AtsBranchManager.createNecessaryBranchEventReviews(StateEventType.CommitBranch, teamArt, new Date(),
            UserManager.getUser(SystemUser.OseeSystem), transaction);
         transaction.execute();
      }
   }

}