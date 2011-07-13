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
package org.eclipse.osee.ats.util.widgets;

import org.eclipse.osee.ats.core.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.XWorkingBranch.BranchStatus;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

public class XWorkingBranchEnablement {
   boolean populated = false;
   boolean workingBranchCreationInProgress = false;
   boolean workingBranchCommitInProgress = false;
   boolean workingBranchInWork = false;
   boolean committedBranchExists = false;
   boolean disableAll = false;
   Branch workingBranch = null;
   private final TeamWorkFlowArtifact teamArt;

   public XWorkingBranchEnablement(TeamWorkFlowArtifact teamArt) {
      this.teamArt = teamArt;
   }

   public boolean isCreateBranchButtonEnabled() {
      if (disableAll) {
         return false;
      }
      try {
         ensurePopulated();
         return !workingBranchCommitInProgress && !workingBranchCreationInProgress && !workingBranchInWork && !committedBranchExists;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;

   }

   public boolean isShowArtifactExplorerButtonEnabled() {
      if (disableAll) {
         return false;
      }
      try {
         ensurePopulated();
         return workingBranch != null && getStatus().isChangesPermitted();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean isShowChangeReportButtonEnabled() {
      if (disableAll) {
         return false;
      }
      try {
         ensurePopulated();
         return workingBranchInWork || committedBranchExists;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean isDeleteBranchButtonEnabled() {
      if (disableAll) {
         return false;
      }
      try {
         ensurePopulated();
         return workingBranchInWork && !committedBranchExists;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean isFavoriteBranchButtonEnabled() {
      if (disableAll) {
         return false;
      }
      try {
         ensurePopulated();
         return workingBranchInWork;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public synchronized void refresh() {
      populated = false;
      disableAll = false;
   }

   public BranchStatus getStatus() throws OseeCoreException {
      ensurePopulated();
      if (teamArt != null) {
         if (workingBranchCreationInProgress) {
            return BranchStatus.Changes_NotPermitted__CreationInProgress;
         } else if (workingBranchCommitInProgress) {
            return BranchStatus.Changes_NotPermitted__CommitInProgress;
         } else if (committedBranchExists) {
            return BranchStatus.Changes_NotPermitted__BranchCommitted;
         } else if (workingBranchInWork) {
            return BranchStatus.Changes_InProgress;
         }
      }
      return BranchStatus.Not_Started;
   }

   private synchronized void ensurePopulated() throws OseeCoreException {
      if (populated) {
         return;
      }
      workingBranch = AtsBranchManagerCore.getWorkingBranch(teamArt, true);
      workingBranchCreationInProgress =
         teamArt.isWorkingBranchCreationInProgress() || (workingBranch != null && workingBranch.getBranchState() == BranchState.CREATION_IN_PROGRESS);
      workingBranchCommitInProgress =
         teamArt.isWorkingBranchCommitInProgress() || workingBranch != null && workingBranch.getBranchState() == BranchState.COMMIT_IN_PROGRESS;
      workingBranchInWork = AtsBranchManagerCore.isWorkingBranchInWork(teamArt);
      committedBranchExists = AtsBranchManagerCore.isCommittedBranchExists(teamArt);
      disableAll = workingBranchCommitInProgress;
      populated = true;
   }

   public Branch getWorkingBranch() {
      return workingBranch;
   }

   public void disableAll() {
      disableAll = true;
   }

   @Override
   public String toString() {
      return String.format(
         "disableAll [%s] CreateInProgress [%s] CommitInProgress [%s] InWorkBranch [%s] CommittedBranch [%s] Branch [%s]",
         disableAll, workingBranchCreationInProgress, workingBranchCommitInProgress, workingBranchInWork,
         committedBranchExists, workingBranch);
   }
}
