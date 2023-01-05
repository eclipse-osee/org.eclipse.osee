/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.ide.branch;

import java.util.Date;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;

public interface AtsBranchServiceIde {

   public final String PARENT_BRANCH_CAN_NOT_BE_DETERMINED =
      "Parent Branch cannot be determined.\n\nPlease specify parent branch through Targeted Version or Team Definition.\n\nContact your team lead to configure this.";

   void showMergeManager(TeamWorkFlowArtifact teamArt);

   void showMergeManager(TeamWorkFlowArtifact teamArt, BranchId destinationBranch);

   /**
    * If working branch has no changes, allow for deletion.
    */
   boolean deleteWorkingBranch(TeamWorkFlowArtifact teamWf, boolean promptUser, boolean pend);

   /**
    * Either return a single commit transaction or user must choose from a list of valid commit transactions
    */
   TransactionToken getTransactionIdOrPopupChoose(IAtsTeamWorkflow teamWf, String title, boolean showMergeManager);

   /**
    * Display change report associated with the branch, if exists, or transaction, if branch has been committed.
    */
   void showChangeReport(IAtsTeamWorkflow teamArt);

   /**
    * Grab the change report for the indicated branch
    */
   void showChangeReportForBranch(TeamWorkFlowArtifact teamArt, BranchId destinationBranch);

   /**
    * Grabs changes from that Team Workflow and sends them into a Word Diff Change Report
    */
   void generateWordChangeReport(IAtsTeamWorkflow teamArt);

   /**
    * Grabs changes from supplied TeamWorkflow, uses WordChangeUtil to generate a list of artifacts that give context to
    * the ones that have Word Template Content Changes, and then sorts them in order. These artifacts are then used to
    * create ArtifactDeltas that are then used in a Word Diff. No attributes other than Word Template Content are used.
    */
   void generateContextChangeReport(IAtsTeamWorkflow teamArt);

   /**
    * @param commitPopup if true, pop-up errors associated with results
    * @param overrideStateValidation if true, don't do checks to see if commit can be performed. This should only be
    * used for developmental testing or automation
    */
   XResultData commitWorkingBranch(TeamWorkFlowArtifact teamArt, boolean commitPopup, boolean overrideStateValidation, BranchId destinationBranch, boolean archiveWorkingBranch, XResultData rd);

   ChangeData getChangeDataFromEarliestTransactionId(IAtsTeamWorkflow teamWf);

   /**
    * Return ChangeItemData represented by commit to commitConfigArt or earliest commit if commitConfigArt == null
    *
    * @param commitConfigItem that configures commit or null
    */
   ChangeData getChangeData(IAtsTeamWorkflow teamWf, CommitConfigItem commitConfigItem);

   /**
    * @return true if one or more reviews were created
    */
   boolean createNecessaryBranchEventReviews(StateEventType stateEventType, IAtsTeamWorkflow teamWf, Date createdDate, AtsUser createdBy, IAtsChangeSet changes);

   /**
    * Perform error checks and popup confirmation dialogs associated with creating a working branch.
    *
    * @param popup if true, errors are popped up to user; otherwise sent silently in Results
    * @return Result return of status
    */
   Result createWorkingBranch_Validate(IAtsTeamWorkflow teamWf);

   /**
    * Create a working branch associated with this Team Workflow. Call createWorkingBranch_Validate first to validate
    * that branch can be created.
    */
   Job createWorkingBranch_Create(TeamWorkFlowArtifact teamArt);

   /**
    * Create a working branch associated with this state machine artifact. This should NOT be called by applications
    * except in test cases or automated tools. Use createWorkingBranchWithPopups
    */
   Job createWorkingBranch_Create(IAtsTeamWorkflow teamWf, boolean pend);

   Job createWorkingBranch_Create(TeamWorkFlowArtifact teamArt, BranchId parentBranch);

   Job createWorkingBranch_Create(IAtsTeamWorkflow teamWf, BranchId parentBranch, boolean pend);

   Job createWorkingBranch(IAtsTeamWorkflow teamWf, TransactionToken parentTransactionId, boolean pend);

   Result deleteWorkingBranch(TeamWorkFlowArtifact teamArt, boolean pend);

}