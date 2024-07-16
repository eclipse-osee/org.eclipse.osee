/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.branch.BranchStatus;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.swt.widgets.Control;

/**
 * @author Shawn F. Cook
 */
public abstract class XWorkingBranchWidgetAbstract extends GenericXWidget implements ArtifactWidget, IArtifactEventListener, IArtifactTopicEventListener, IBranchEventListener {
   private TeamWorkFlowArtifact teamArt;
   private boolean workingBranchCreationInProgress = false;
   private boolean workingBranchCommitInProgress = false;
   private boolean workingBranchCommitWithMergeInProgress = false;
   private boolean workingBranchInWork = false;
   private boolean committedBranchExists = false;
   protected boolean disableAll = false;
   private BranchToken workingBranch = null;

   public XWorkingBranchWidgetAbstract() {
      OseeEventManager.addListener(this);
   }

   @Override
   public TeamWorkFlowArtifact getArtifact() {
      return teamArt;
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   @Override
   public Control getControl() {
      return labelWidget;
   }

   @Override
   public IStatus isValid() {
      // Need this cause it removes all error items of this NAMESPACE
      return new Status(IStatus.OK, getClass().getSimpleName(), "");
   }

   protected abstract void refreshWorkingBranchWidget();

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         this.teamArt = (TeamWorkFlowArtifact) artifact;
         updateBranchState();
      }
   }

   @Override
   public String toString() {
      return String.format("%s", getLabel());
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      refreshWorkingBranchWidget();
   }

   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
      refreshWorkingBranchWidget();
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            try {
               updateBranchState();
               refreshWorkingBranchWidget();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      Thread thread = new Thread(runnable);
      thread.start();
   }

   private void updateBranchState() {
      if (teamArt != null) {
         workingBranch = AtsApiService.get().getBranchService().getWorkingBranch(teamArt, true);
         workingBranchCreationInProgress =
            AtsApiService.get().getBranchService().isWorkingBranchCreationInProgress(teamArt);
         workingBranchInWork = AtsApiService.get().getBranchService().isWorkingBranchInWork(teamArt);

         if (workingBranch.isInvalid()) {
            workingBranchCommitInProgress = false;
         } else {
            BranchState state = BranchManager.getState(workingBranch);
            workingBranchCreationInProgress |= state.isCreationInProgress();
            workingBranchCommitInProgress = AtsApiService.get().getBranchService().isWorkingBranchCommitInProgress(
               teamArt) || state.isCommitInProgress();
            workingBranchCommitWithMergeInProgress =
               BranchManager.hasMergeBranches(workingBranch) && !state.isRebaselineInProgress();
         }
         committedBranchExists = AtsApiService.get().getBranchService().isCommittedBranchExists(teamArt);
         disableAll = workingBranchCommitInProgress;
      }
   }

   protected BranchStatus getStatus() {
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

   public TeamWorkFlowArtifact getTeamArt() {
      return teamArt;
   }

   public BranchToken getWorkingBranch() {
      return workingBranch;
   }

   public boolean isWorkingBranchCreationInProgress() {
      return workingBranchCreationInProgress;
   }

   public boolean isWorkingBranchCommitWithMergeInProgress() {
      return workingBranchCommitWithMergeInProgress;
   }

   public boolean isWorkingBranchCommitInProgress() {
      return workingBranchCommitInProgress;
   }

   public boolean isWorkingBranchInWork() {
      return workingBranchInWork;
   }

   public boolean isCommittedBranchExists() {
      return committedBranchExists;
   }

}
