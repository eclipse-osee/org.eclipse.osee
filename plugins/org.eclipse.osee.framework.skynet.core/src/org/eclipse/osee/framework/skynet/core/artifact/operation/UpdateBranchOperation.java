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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.update.ConflictResolverOperation;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class UpdateBranchOperation extends AbstractOperation {
   private final IOseeBranch originalBranch;
   private final ConflictResolverOperation resolver;
   private IOseeBranch newBranch;

   public UpdateBranchOperation(final IOseeBranch branch, final ConflictResolverOperation resolver) {
      this(branch, null, resolver);
   }

   public UpdateBranchOperation(final IOseeBranch branch, final BranchId fromBranch, final ConflictResolverOperation resolver) {
      super(String.format("Update Branch [%s]", branch.getShortName()), Activator.PLUGIN_ID);
      this.originalBranch = branch;
      this.resolver = resolver;
   }

   private static String getUpdatedName(String branchName) {
      String storeName = Strings.truncate(branchName, 100);
      return String.format("%s - for update - %s", storeName, Lib.getDateTimeString());
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      // Only update if there are no other Merge Branches and we haven't committed this branch already
      if (originalBranch != null && !BranchManager.hasMergeBranches(originalBranch) && !BranchManager.getState(
         originalBranch).isCommitted()) {
         performUpdate(monitor, originalBranch);

         OseeClient client = ServiceUtil.getOseeClient();
         BranchEndpoint proxy = client.getBranchEndpoint();

         BranchId parentBranch = BranchManager.getParentBranch(originalBranch);
         proxy.logBranchActivity(
            String.format("Branch Operation Update Branch {branchUUID: %s, branchName: %s parentBranchId: %s",
               originalBranch.getIdString(), originalBranch.getName(), parentBranch.getId()));
      }
   }

   private IOseeBranch createTempBranch(IOseeBranch originalBranch)  {
      BranchId parentBranch = BranchManager.getParentBranch(originalBranch);
      return BranchManager.createWorkingBranch(parentBranch, getUpdatedName(originalBranch.getName()));
   }

   private void performUpdate(IProgressMonitor monitor, IOseeBranch originalBranch) throws Exception {
      boolean wasSuccessful = false;
      try {
         monitor.setTaskName("Creating temporary branch");
         newBranch = createTempBranch(originalBranch);
         BranchManager.setState(originalBranch, BranchState.REBASELINE_IN_PROGRESS);
         monitor.worked(calculateWork(0.40));

         boolean hasChanges = BranchManager.hasChanges(originalBranch);
         if (hasChanges) {
            commitOldWorkingIntoNewWorkingBranch(monitor, originalBranch, newBranch, 0.40);
         } else {
            deleteOldAndSetNewAsWorking(monitor, originalBranch, newBranch, 0.40);
         }
         wasSuccessful = true;
      } finally {
         if (newBranch != null && !wasSuccessful) {
            BranchManager.purgeBranch(newBranch);
         }
         monitor.worked(calculateWork(0.20));
      }
   }

   private void commitOldWorkingIntoNewWorkingBranch(IProgressMonitor monitor, BranchId originalBranch, BranchId newWorkingBranch, double workPercentage) throws Exception {
      monitor.setTaskName("Checking for Conflicts");
      ConflictManagerExternal conflictManager = new ConflictManagerExternal(newWorkingBranch, originalBranch);
      IOperation operation;
      if (!conflictManager.remainingConflictsExist()) {
         operation = new FinishUpdateBranchOperation(conflictManager, true, false);
      } else {
         operation = resolver;
         resolver.setConflictManager(conflictManager);
      }
      doSubWork(operation, monitor, workPercentage);
   }

   private void deleteOldAndSetNewAsWorking(IProgressMonitor monitor, IOseeBranch originalBranch, BranchId newWorkingBranch, double workPercentage) throws Exception {
      String originalBranchName = originalBranch.getName();

      BranchManager.setName(originalBranch, getUpdatedName(originalBranchName));
      monitor.worked(calculateWork(0.20));

      BranchManager.setName(newWorkingBranch, originalBranchName);
      BranchManager.setAssociatedArtifactId(newWorkingBranch, BranchManager.getAssociatedArtifactId(originalBranch));
      BranchManager.setState(originalBranch, BranchState.REBASELINED);

      BranchManager.deleteBranch(originalBranch).join();
      monitor.worked(calculateWork(workPercentage));
   }

   public IOseeBranch getNewBranch() {
      return newBranch;
   }
}
