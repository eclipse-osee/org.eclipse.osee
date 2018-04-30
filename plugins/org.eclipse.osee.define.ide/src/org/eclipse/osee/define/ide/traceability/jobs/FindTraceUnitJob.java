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
package org.eclipse.osee.define.ide.traceability.jobs;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.define.ide.internal.Activator;
import org.eclipse.osee.define.ide.traceability.operations.FindTraceUnitFromResource;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
public class FindTraceUnitJob extends Job {
   private final IResource[] resources;

   public FindTraceUnitJob(String name, IResource... resources) {
      super(name);
      if (resources != null) {
         this.resources = resources;
      } else {
         this.resources = new IResource[0];
      }
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      if (resources != null && resources.length > 0) {
         FetchBranchJob job = new FetchBranchJob(getName());
         Jobs.startJob(job, true, new JobChangeAdapter() {

            @Override
            public void done(IJobChangeEvent event) {
               FetchBranchJob fetcherJob = (FetchBranchJob) event.getJob();
               final BranchId branch = fetcherJob.getSelectedBranch();
               if (branch != null) {
                  IExceptionableRunnable runnable = new IExceptionableRunnable() {

                     @Override
                     public IStatus run(IProgressMonitor monitor) throws Exception {
                        FindTraceUnitFromResource.search(branch, resources);
                        return Status.OK_STATUS;
                     }
                  };
                  Jobs.runInJob(getName(), runnable, Activator.class, Activator.PLUGIN_ID);
               }
            }
         });
      }
      return Status.OK_STATUS;
   }
   private static final class FetchBranchJob extends UIJob {
      private BranchId branch;

      public FetchBranchJob(String name) {
         super(name);
      }

      @Override
      public IStatus runInUIThread(IProgressMonitor monitor) {
         branch = BranchSelectionDialog.getBranchFromUser();
         return Status.OK_STATUS;
      }

      public BranchId getSelectedBranch() {
         return branch;
      }
   }
}
