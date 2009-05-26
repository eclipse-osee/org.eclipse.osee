/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.update.IConflictResolver;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
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

   protected boolean useParentBranchValid(Branch branch) {
      boolean hasValidParent = branch.hasParentBranch();
      if (hasValidParent) {
         try {
            hasValidParent = !branch.getParentBranch().equals(BranchManager.getSystemRootBranch());
            hasValidParent &= (!branch.isArchived() && branch.isWorkingBranch()) || AccessControlManager.isOseeAdmin();
            hasValidParent &= branch.getChildBranches().isEmpty();
         } catch (Exception ex) {
            hasValidParent = false;
         }
      }
      return hasValidParent;
   }

   private Branch getSelectedBranch() {
      Branch branch = null;
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      List<Branch> branches = Handlers.getBranchesFromStructuredSelection(selection);
      if (branches.size() == 1) {
         branch = branches.iterator().next();
      }
      return branch;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.util.CommandHandler#isEnabledWithException()
    */
   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      boolean enabled = false;
      Branch branch = getSelectedBranch();
      if (branch != null) {
         enabled = useParentBranchValid(branch);
      }
      return enabled;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      Branch branchToUpdate = getSelectedBranch();
      if (branchToUpdate != null) {
         boolean isUpdateAllowed =
               MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                     "Update Branch", String.format("Are you sure you want to update [%s] branch",
                           branchToUpdate.getBranchName()));
         if (isUpdateAllowed) {
            BranchManager.updateBranch(branchToUpdate, new UserConflictResolver());
         }
      }
      return null;
   }

   private final class UserConflictResolver implements IConflictResolver {

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.artifact.update.IConflictResolver#resolveConflicts(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal)
       */
      @Override
      public IStatus resolveConflicts(IProgressMonitor monitor, ConflictManagerExternal conflictManager) throws OseeCoreException {
         monitor.beginTask("Launch Merge Manager", 100);
         Job job = createMergeViewJob(conflictManager.getFromBranch(), conflictManager.getToBranch());
         Jobs.startJob(job);
         monitor.worked(100);
         monitor.done();
         return Status.OK_STATUS;
      }

      private Job createMergeViewJob(final Branch sourceBranch, final Branch destinationBranch) {
         Job job = new UIJob("Launch Merge Manager") {
            /* (non-Javadoc)
             * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
             */
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               IStatus status = Status.OK_STATUS;
               try {
                  IWorkbenchPage page = AWorkbench.getActivePage();
                  IViewPart viewPart =
                        page.showView(MergeView.VIEW_ID,
                              String.valueOf(sourceBranch.getBranchId() * 100000 + destinationBranch.getBranchId()),
                              IWorkbenchPage.VIEW_ACTIVATE);
                  if (viewPart instanceof MergeView) {
                     MergeView mergeView = (MergeView) viewPart;
                     mergeView.explore(sourceBranch, destinationBranch, null, null, true);
                  }
               } catch (PartInitException ex) {
                  status = new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, "Error launching merge view", ex);
               }
               return status;
            }
         };
         return job;
      }
   }
}
