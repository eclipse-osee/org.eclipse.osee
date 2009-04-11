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
package org.eclipse.osee.define.traceability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.define.traceability.operations.FindTraceUnitFromResource;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
public class FindTraceUnitActionDelegate implements IWorkbenchWindowActionDelegate {

   /* (non-Javadoc)
    * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
    */
   @Override
   public void dispose() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
    */
   @Override
   public void init(IWorkbenchWindow window) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
    */
   @Override
   public void run(IAction action) {
      final String jobName = "Resource To Trace Unit Artifact";
      final List<IResource> resources = getSelectedItems();
      FetchBranchJob job = new FetchBranchJob(jobName);
      Jobs.startJob(job, true, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            FetchBranchJob fetcherJob = (FetchBranchJob) event.getJob();
            final Branch branch = fetcherJob.getSelectedBranch();
            if (branch != null) {
               IExceptionableRunnable runnable = new IExceptionableRunnable() {

                  @Override
                  public void run(IProgressMonitor monitor) throws Exception {
                     if (branch != null) {
                        FindTraceUnitFromResource.search(branch, resources.toArray(new IResource[resources.size()]));
                     }
                  }
               };
               Jobs.run(jobName, runnable, DefinePlugin.class, DefinePlugin.PLUGIN_ID);
            }
         }
      });

   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
    */
   @Override
   public void selectionChanged(IAction action, ISelection selection) {
   }

   private List<IResource> getSelectedItems() {
      List<IResource> selectedItems = new ArrayList<IResource>();
      StructuredSelection selection = AWorkspace.getSelection();
      if (selection != null) {
         Iterator<?> iterator = selection.iterator();
         while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof IAdaptable) {
               IResource resource = (IResource) ((IAdaptable) obj).getAdapter(IResource.class);
               if (resource != null) {
                  if (resource instanceof IFile) {
                     selectedItems.add(resource);
                  }
               }
            }
            //            if (obj instanceof IFile) {
            //               IResource resource = (IResource) obj;
            //               if (resource != null) {
            //                  selectedItems.add(resource);
            //               }
            //            }
         }
      }
      return selectedItems;
   }

   private final class FetchBranchJob extends UIJob {
      private Branch branch;

      public FetchBranchJob(String name) {
         super(name);
         branch = null;
      }

      public IStatus runInUIThread(IProgressMonitor monitor) {
         branch = BranchSelectionDialog.getBranchFromUser();
         return Status.OK_STATUS;
      }

      public Branch getSelectedBranch() {
         return branch;
      }
   }
}
