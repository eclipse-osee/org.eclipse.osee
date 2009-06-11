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

package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
class DeleteBranchJob extends Job {

   private final Branch branch;

   /**
    * @param name
    * @param branch
    */
   public DeleteBranchJob(Branch branch) {
      super("Delete Branch: " + branch);
      this.branch = branch;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus status = Status.OK_STATUS;
      monitor.beginTask(getName(), 1);
      try {
         BranchManager.setBranchState(branch, BranchState.DELETED);
         BranchManager.archive(branch);
         BranchManager.handleBranchDeletion(branch.getBranchId());
      } catch (Exception ex) {
         status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error - " + getName(), ex);
      } finally {
         monitor.done();
      }
      return status;
   }
}