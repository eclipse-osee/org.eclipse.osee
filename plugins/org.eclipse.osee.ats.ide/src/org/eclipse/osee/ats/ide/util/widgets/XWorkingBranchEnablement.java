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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.ats.api.branch.BranchEnablementData;
import org.eclipse.osee.ats.api.branch.BranchStatus;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

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

   private static final class BranchEnablementLazyObject extends LazyObject<BranchEnablementData> {
      private final TeamWorkFlowArtifact teamArt;

      public BranchEnablementLazyObject(TeamWorkFlowArtifact teamArt) {
         this.teamArt = teamArt;
      }

      @Override
      protected FutureTask<BranchEnablementData> createLoaderTask() {
         Callable<BranchEnablementData> callable = new Callable<BranchEnablementData>() {

            @Override
            public BranchEnablementData call() throws Exception {
               BranchEnablementData enablementData = new BranchEnablementData(teamArt, AtsClientService.get());
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

}
