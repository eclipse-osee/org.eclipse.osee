/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UpdateBranchData;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.update.ConflictResolverOperation;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.util.RebaselineInProgressHandler;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
public class UpdateBranchHandler extends CommandHandler {

   protected boolean isValid(BranchToken branch) {
      return !BranchManager.isParentSystemRoot(branch) && BranchManager.isEditable(
         branch) && BranchManager.getType(branch).matches(BranchType.WORKING,
            BranchType.BASELINE) && !BranchManager.hasChildren(branch);
   }

   private BranchToken getSelectedBranch(IStructuredSelection selection) {
      BranchToken branch = null;

      List<BranchToken> branches = Handlers.getBranchesFromStructuredSelection(selection);
      if (branches.size() == 1) {
         branch = branches.iterator().next();
      }
      return branch;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      boolean enabled = false;
      BranchToken branch = getSelectedBranch(structuredSelection);
      if (branch != null) {
         enabled = isValid(branch);
      }
      return enabled;
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      BranchToken branchToUpdate = getSelectedBranch(selection);

      if (branchToUpdate != null) {
         if (BranchManager.isUpdatable(branchToUpdate)) {
            if (BranchManager.getState(branchToUpdate).isRebaselineInProgress()) {
               RebaselineInProgressHandler.handleRebaselineInProgress(branchToUpdate);
            } else {
               boolean isUserSure = MessageDialog.openQuestion(
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Update Branch",
                  String.format("Are you sure you want to update Branch: \n\n[%s]", branchToUpdate.getName()));
               if (isUserSure) {

                  Job updateBranch = new Job("Update Branch") {

                     @Override
                     protected IStatus run(IProgressMonitor monitor) {
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
                        return Status.OK_STATUS;
                     }
                  };
                  updateBranch.setUser(true);
                  updateBranch.setPriority(Job.LONG);
                  updateBranch.schedule();
               }
            }
         } else {
            MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
               "Can't Update Branch",
               String.format(
                  "Couldn't update [%s] because it currently has merge branches from commits.  To perform an update please delete all the merge branches for this branch",
                  branchToUpdate.getName()));
         }
      }
      return null;
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
}
