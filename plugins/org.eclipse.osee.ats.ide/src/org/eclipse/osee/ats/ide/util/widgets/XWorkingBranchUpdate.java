/*********************************************************************
 * Copyright (c) 2013 Boeing
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UpdateBranchData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.update.ConflictResolverOperation;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.util.RebaselineInProgressHandler;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Angel Avila
 */
public class XWorkingBranchUpdate extends XWorkingBranchButtonAbstract {

   @Override
   protected void initButton(final Button button) {
      button.setToolTipText("Update Working Branch From Targeted Version or Team Configured Branch");
      button.setImage(ImageManager.getImage(FrameworkImage.BRANCH_SYNCH));
      button.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            try {
               BranchToken branchToUpdate = getWorkingBranch();
               if (branchToUpdate != null) {
                  Artifact associatedArtifact = BranchManager.getAssociatedArtifact(branchToUpdate);
                  IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(associatedArtifact);
                  if (workItem == null || !workItem.isTeamWorkflow()) {
                     AWorkbench.popup("Working Branch must have associated Team Workflow");
                     return;
                  }
                  IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
                  BranchId targetedBranch =
                     AtsApiService.get().getBranchService().getConfiguredBranchForWorkflow(teamWf);
                  if (BranchManager.isUpdatable(branchToUpdate)) {
                     if (BranchManager.getState(branchToUpdate).isRebaselineInProgress()) {
                        RebaselineInProgressHandler.handleRebaselineInProgress(branchToUpdate);
                     } else {
                        boolean isUserSure = MessageDialog.openQuestion(
                           PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Update Branch",
                           String.format(
                              "Are you sure you want to update [%s]\n branch from Targeted Version or Team Configured branch [%s]?",
                              branchToUpdate.getName(), BranchManager.getBranchToken(targetedBranch).getName()));
                        if (isUserSure) {
                           UpdateBranchData branchData =
                              BranchManager.updateBranch(branchToUpdate, new UserConflictResolver());
                           if (branchData.getResults().isErrors()) {
                              XResultDataUI.report(branchData.getResults(), "Update Branch Failed");
                              // TODO: Revert operation
                           } else if (branchData.isNeedsMerge()) {
                              // TODO: Future server-based merge
                              XResultDataUI.report(branchData.getResults(), "Branch needs to be merged.");
                           } else {
                              // TODO: Future server-based operation
                           }
                        }
                     }
                  } else {
                     MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Can't Update Branch",
                        String.format("Couldn't update [%s] because it currently has merge branches from commits.  " //
                           + "To perform an update please delete all the merge branches for this branch.",
                           branchToUpdate.getName()));
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });
   }

   @Override
   protected void refreshEnablement(Button button) {
      button.setEnabled(
         !disableAll && isWorkingBranchInWork() && !isCommittedBranchExists() && !isWorkingBranchCommitWithMergeInProgress() && isWidgetAllowedInCurrentState());
   }

   private static final class UserConflictResolver extends ConflictResolverOperation {

      public UserConflictResolver() {
         super("Launch Merge Manager", Activator.PLUGIN_ID);
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         ConflictManagerExternal conflictManager = getConflictManager();
         Job job = createMergeViewJob(conflictManager.getSourceBranch(), conflictManager.getDestinationBranch());
         Jobs.startJob(job);
      }

      private Job createMergeViewJob(final BranchId sourceBranch, final BranchId destinationBranch) {
         Job job = new UIJob("Launch Merge Manager") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               IStatus status = Status.OK_STATUS;
               try {
                  IWorkbenchPage page = AWorkbench.getActivePage();
                  IViewPart viewPart = page.showView(MergeView.VIEW_ID,
                     String.valueOf(sourceBranch.getId() * 100000 + destinationBranch.getId()),
                     IWorkbenchPage.VIEW_ACTIVATE);
                  if (viewPart instanceof MergeView) {
                     MergeView mergeView = (MergeView) viewPart;
                     mergeView.explore(sourceBranch, destinationBranch, null, null, true);
                  }
               } catch (PartInitException ex) {
                  status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error launching merge view", ex);
               }
               return status;
            }
         };
         return job;
      }
   }

   @Override
   protected boolean isWidgetAllowedInCurrentState() {
      return isWidgetInState(XWorkingBranchUpdate.class.getSimpleName());
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      return null;
   }
}
