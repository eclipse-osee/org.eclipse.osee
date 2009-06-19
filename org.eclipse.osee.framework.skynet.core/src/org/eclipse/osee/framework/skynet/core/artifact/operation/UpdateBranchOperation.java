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
package org.eclipse.osee.framework.skynet.core.artifact.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.update.ConflictResolverOperation;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;

/**
 * @author Roberto E. Escobar
 */
public class UpdateBranchOperation extends AbstractOperation {
   private final Branch originalBranch;
   private final ConflictResolverOperation resolver;
   private final String pluginId;

   public UpdateBranchOperation(final String pluginId, final Branch branch, final ConflictResolverOperation resolver) {
      super("Update Branch", pluginId);
      this.pluginId = pluginId;
      this.originalBranch = branch;
      this.resolver = resolver;
   }

   private static String getUpdatedName(String branchName) {
      String storeName = StringFormat.truncate(branchName, 100);
      return String.format("%s - for update - %s", storeName, Lib.getDateTimeString());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (originalBranch != null && originalBranch.hasParentBranch()) {
         performUpdate(monitor, originalBranch);
      } else {
         monitor.worked(getTotalWorkUnits());
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.AbstractOperation#createErrorStatus(java.lang.Throwable)
    */
   @Override
   protected IStatus createErrorStatus(Throwable error) {
      setStatusMessage(String.format("Error updating branch [%s]", originalBranch.getBranchShortName()));
      return super.createErrorStatus(error);
   }

   private Branch createTempBranch(Branch originalBranch) throws OseeCoreException {
      Branch parentBranch = originalBranch.getParentBranch();
      String branchUpdateName = getUpdatedName(originalBranch.getBranchName());
      return BranchManager.createWorkingBranch(parentBranch, branchUpdateName,
            UserManager.getUser(SystemUser.OseeSystem));
   }

   private void performUpdate(IProgressMonitor monitor, Branch originalBranch) throws Exception {
      Branch newWorkingBranch = null;
      boolean wasSuccessful = false;
      try {
         monitor.setTaskName("Creating temporary branch");
         newWorkingBranch = createTempBranch(originalBranch);
         monitor.worked(calculateWork(0.40));

         BranchManager.setBranchState(originalBranch, BranchState.REBASELINE_IN_PROGRESS);

         monitor.setTaskName("Checking for Conflicts");
         ConflictManagerExternal conflictManager = new ConflictManagerExternal(newWorkingBranch, originalBranch);
         IOperation operation;
         if (!conflictManager.remainingConflictsExist()) {
            operation = new FinishUpdateBranchOperation(pluginId, conflictManager, true, false);
         } else {
            operation = resolver;
            resolver.setConflictManager(conflictManager);
         }
         doSubWork(operation, monitor, 0.40);
         wasSuccessful = true;
      } finally {
         if (newWorkingBranch != null && !wasSuccessful) {
            BranchManager.purgeBranch(newWorkingBranch);
         }
         monitor.worked(calculateWork(0.20));
      }
   }
}
