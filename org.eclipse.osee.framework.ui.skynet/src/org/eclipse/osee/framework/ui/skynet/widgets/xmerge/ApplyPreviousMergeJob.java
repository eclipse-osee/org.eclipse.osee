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
package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Theron Virgin
 */
public class ApplyPreviousMergeJob extends Job {
   private int destBranchId;
   private Conflict[] conflicts;

   public ApplyPreviousMergeJob(Conflict[] conflicts, int destBranchId) {
      super("Apply Previous Merge");
      this.conflicts = conflicts;
      this.destBranchId = destBranchId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      for (Conflict conflict : conflicts) {
         try {
            conflict.applyPreviousMerge(ConflictManagerInternal.getMergeBranchId(
                  conflict.getSourceBranch().getBranchId(), destBranchId), destBranchId);
         } catch (OseeCoreException ex) {
            OSEELog.logException(ApplyPreviousMergeJob.class, ex, false);
         }
      }
      return Status.OK_STATUS;
   }

}
