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

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.XWorkingBranch.BranchStatus;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Donald G. Dunne
 */
public class XWorkingBranchEnablement {

   private final LazyObject<BranchEnablementData> dataProvider;

   public XWorkingBranchEnablement(TeamWorkFlowArtifact teamArt) {
      dataProvider = new BranchEnablementLazyObject(teamArt);
   }

   private BranchEnablementData getEnablementData() {
      return dataProvider.get();
   }

   public boolean isCreateBranchButtonEnabled() {
      boolean result = false;
      BranchEnablementData data = getEnablementDataLogException();
      if (data != null && !data.isDisableAll()) {
         result =
            !data.isWorkingBranchCommitInProgress() && !data.isWorkingBranchCreationInProgress() && !data.isWorkingBranchInWork() && !data.isCommittedBranchExists();
      }
      return result;
   }

   public boolean isShowArtifactExplorerButtonEnabled() {
      boolean result = false;
      BranchEnablementData data = getEnablementDataLogException();
      if (data != null && !data.isDisableAll()) {
         result = data.getWorkingBranch() != null && data.getBranchStatus().isChangesPermitted();
      }
      return result;
   }

   public boolean isShowChangeReportButtonEnabled() {
      boolean result = false;
      BranchEnablementData data = getEnablementDataLogException();
      if (data != null && !data.isDisableAll()) {
         result = data.isWorkingBranchInWork() || data.isCommittedBranchExists();
      }
      return result;
   }

   public boolean isDeleteBranchButtonEnabled() {
      boolean result = false;
      BranchEnablementData data = getEnablementDataLogException();
      if (data != null && !data.isDisableAll()) {
         result = data.isWorkingBranchInWork() && !data.isCommittedBranchExists();
      }
      return result;
   }

   public boolean isFavoriteBranchButtonEnabled() {
      boolean result = false;
      BranchEnablementData data = getEnablementDataLogException();
      if (data != null && !data.isDisableAll()) {
         result = data.isWorkingBranchInWork();
      }
      return result;
   }

   private BranchEnablementData getEnablementDataLogException() {
      BranchEnablementData data = null;
      try {
         data = getEnablementData();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return data;
   }

   public void refresh() {
      synchronized (dataProvider) {
         dataProvider.invalidate();
      }
   }

   public BranchStatus getStatus() {
      return getEnablementData().getBranchStatus();
   }

   public IOseeBranch getWorkingBranch() {
      return getEnablementData().getWorkingBranch();
   }

   public void disableAll() {
      getEnablementData().setDisableAll(true);
   }

   @Override
   public String toString() {
      String toReturn;
      try {
         BranchEnablementData data = getEnablementData();
         toReturn = data.toString();
      } catch (OseeCoreException ex) {
         toReturn = String.format("Error computing enablement data - [%s]", Lib.exceptionToString(ex));
      }
      return toReturn;
   }

   private static final class BranchEnablementLazyObject extends LazyObject<XWorkingBranchEnablement.BranchEnablementData> {
      private final TeamWorkFlowArtifact teamArt;

      public BranchEnablementLazyObject(TeamWorkFlowArtifact teamArt) {
         this.teamArt = teamArt;
      }

      @Override
      protected FutureTask<BranchEnablementData> createLoaderTask() {
         Callable<BranchEnablementData> callable = new Callable<BranchEnablementData>() {

            @Override
            public BranchEnablementData call() throws Exception {
               BranchEnablementData enablementData = new BranchEnablementData(teamArt);
               if (teamArt != null) {
                  IOseeBranch workingBranch = AtsClientService.get().getBranchService().getWorkingBranch(teamArt, true);
                  enablementData.setWorkingBranch(workingBranch);

                  enablementData.setWorkingBranchInWork(
                     AtsClientService.get().getBranchService().isWorkingBranchInWork(teamArt));
                  enablementData.setCommittedBranchExists(
                     AtsClientService.get().getBranchService().isCommittedBranchExists(teamArt));

                  enablementData.setDisableAll(enablementData.isWorkingBranchCommitInProgress());
               }
               return enablementData;
            }
         };
         return new FutureTask<>(callable);
      }
   };

   private static final class BranchEnablementData {
      private final TeamWorkFlowArtifact teamArt;
      private IOseeBranch workingBranch;

      private boolean workingBranchInWork;
      private boolean committedBranchExists;
      private boolean disableAll;

      public BranchEnablementData(TeamWorkFlowArtifact teamArt) {
         this.teamArt = teamArt;
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
         return AtsClientService.get().getBranchService().isWorkingBranchCreationInProgress(
            teamArt) || workingBranch.isValid() && BranchManager.getState(workingBranch).isCreationInProgress();
      }

      public boolean isWorkingBranchCommitInProgress() {
         return AtsClientService.get().getBranchService().isWorkingBranchCommitInProgress(
            teamArt) || workingBranch.isValid() && BranchManager.getState(workingBranch).isCommitInProgress();
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
         if (teamArt != null) {
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
}
