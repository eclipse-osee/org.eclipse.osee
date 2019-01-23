package org.eclipse.osee.ats.api.branch;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.IOseeBranch;

public class BranchEnablementData {
   private final IAtsTeamWorkflow teamWf;
   private IOseeBranch workingBranch;

   private boolean workingBranchInWork;
   private boolean committedBranchExists;
   private boolean disableAll;
   private final AtsApi atsApi;

   public BranchEnablementData(IAtsTeamWorkflow teamArt, AtsApi atsApi) {
      this.teamWf = teamArt;
      this.atsApi = atsApi;
   }

   public void setWorkingBranchInWork(boolean workingBranchInWork) {
      this.workingBranchInWork = workingBranchInWork;
   }

   public void setCommittedBranchExists(boolean committedBranchExists) {
      this.committedBranchExists = committedBranchExists;
   }

   public void setWorkingBranch(IOseeBranch workingBranch) {
      this.workingBranch = workingBranch;
   }

   public void setDisableAll(boolean disableAll) {
      this.disableAll = disableAll;
   }

   public boolean isWorkingBranchCreationInProgress() {
      return atsApi.getBranchService().isWorkingBranchCreationInProgress(
         teamWf) || workingBranch.isValid() && atsApi.getBranchService().getBranchState(
            workingBranch).isCreationInProgress();
   }

   public boolean isWorkingBranchCommitInProgress() {
      return atsApi.getBranchService().isWorkingBranchCommitInProgress(
         teamWf) || workingBranch.isValid() && atsApi.getBranchService().getBranchState(
            workingBranch).isCommitInProgress();
   }

   public boolean isWorkingBranchInWork() {
      return workingBranchInWork;
   }

   public boolean isCommittedBranchExists() {
      return committedBranchExists;
   }

   public boolean isDisableAll() {
      return disableAll;
   }

   public IOseeBranch getWorkingBranch() {
      return workingBranch;
   }

   public BranchStatus getBranchStatus() {
      if (teamWf != null) {
         if (isWorkingBranchCreationInProgress()) {
            return BranchStatus.Changes_NotPermitted__CreationInProgress;
         } else if (isWorkingBranchCommitInProgress()) {
            return BranchStatus.Changes_NotPermitted__CommitInProgress;
         } else if (isCommittedBranchExists()) {
            return BranchStatus.Changes_NotPermitted__BranchCommitted;
         } else if (isWorkingBranchInWork()) {
            return BranchStatus.Changes_InProgress;
         }
      }
      return BranchStatus.Not_Started;
   }

   @Override
   public String toString() {
      return "BranchEnablementData [workingBranch=" + workingBranch + ", workingBranchCreationInProgress=" + isWorkingBranchCreationInProgress() + ", workingBranchCommitInProgress=" + isWorkingBranchCommitInProgress() + ", workingBranchInWork=" + workingBranchInWork + ", committedBranchExists=" + committedBranchExists + ", disableAll=" + disableAll + "]";
   }

}
