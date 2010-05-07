package org.eclipse.osee.ats.util.widgets;

import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.XWorkingBranch.BranchStatus;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

public class XWorkingBranchEnablement {
   boolean populated = false;
   boolean workingBranchInWork = false;
   boolean committedBranchExists = false;
   Branch workingBranch = null;
   private final TeamWorkFlowArtifact teamArt;

   public XWorkingBranchEnablement(TeamWorkFlowArtifact teamArt) {
      this.teamArt = teamArt;
   }

   public boolean isCreateBranchButtonEnabled() {
      try {
         ensurePopulated();
         return !workingBranchInWork && !committedBranchExists;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;

   }

   public boolean isShowArtifactExplorerButtonEnabled() {
      try {
         ensurePopulated();
         return workingBranch != null && !getStatus().equals(BranchStatus.Changes_NotPermitted);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean isShowChangeReportButtonEnabled() {
      try {
         ensurePopulated();
         return workingBranchInWork || committedBranchExists;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean isDeleteBranchButtonEnabled() {
      try {
         ensurePopulated();
         return workingBranchInWork && !committedBranchExists;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public void refresh() {
      populated = false;
   }

   public BranchStatus getStatus() throws OseeCoreException {
      ensurePopulated();
      if (teamArt != null && committedBranchExists) {
         return BranchStatus.Changes_NotPermitted;
      } else if (teamArt != null && workingBranchInWork) {
         return BranchStatus.Changes_InProgress;
      } else {
         return BranchStatus.Not_Started;
      }
   }

   private synchronized void ensurePopulated() throws OseeCoreException {
      if (populated) {
         return;
      }
      workingBranchInWork = teamArt.getBranchMgr().isWorkingBranchInWork();
      committedBranchExists = teamArt.getBranchMgr().isCommittedBranchExists();
      workingBranch = teamArt.getWorkingBranch();
      populated = true;
   }

   public Branch getWorkingBranch() {
      return workingBranch;
   }

}
