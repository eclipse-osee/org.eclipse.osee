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

package org.eclipse.osee.framework.skynet.core.artifact.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UpdateBranchData;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
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
   private final BranchToken originalBranch;
   private final BranchId fromBranch;
   private final ConflictResolverOperation resolver;
   private BranchToken newBranch;
   private UpdateBranchData branchData;

   public UpdateBranchOperation(final BranchToken branch, final ConflictResolverOperation resolver) {
      this(branch, BranchManager.getParentBranch(branch), resolver);
   }

   public UpdateBranchOperation(final BranchToken branch, final BranchId fromBranch, final ConflictResolverOperation resolver) {
      super(String.format("Update Branch [%s]", branch.getShortName()), Activator.PLUGIN_ID);
      this.originalBranch = branch;
      this.fromBranch = fromBranch;
      this.resolver = resolver;
      this.branchData = new UpdateBranchData();
   }

   private static String getUpdatedName(String branchName) {
      String storeName = Strings.truncate(branchName, 100);
      return String.format("%s - for update - %s", storeName, Lib.getDateTimeString());
   }

   public UpdateBranchData run() {
      Operations.executeWorkAndCheckStatus(this);
      return this.branchData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      // Only update if there are no other Merge Branches and we haven't committed this branch already
      if (originalBranch != null && !BranchManager.hasMergeBranches(originalBranch) && !BranchManager.getState(
         originalBranch).isCommitted() && !BranchManager.getState(originalBranch).equals(
            BranchState.REBASELINE_IN_PROGRESS)) {
         performUpdate(monitor, originalBranch);
      }
   }

   private void performUpdate(IProgressMonitor monitor, BranchToken originalBranch) throws Exception {
      boolean wasSuccessful = false;
      OseeClient client = ServiceUtil.getOseeClient();
      BranchEndpoint branchEp = client.getBranchEndpoint();
      BranchState originalState = BranchManager.getState(originalBranch);
      try {
         BranchManager.setState(originalBranch, BranchState.REBASELINE_IN_PROGRESS);
         monitor.setTaskName("Creating temporary branch");
         String originalBranchName = originalBranch.getName();
         branchData.setToName(getUpdatedName(originalBranchName));
         branchData.setToBranch(originalBranch);
         branchData.setFromBranch(fromBranch);
         branchData = branchEp.updateBranch(fromBranch, branchData); // Creates a branch server-side
         branchData.setNeedsMerge(BranchManager.hasChanges(originalBranch));
         newBranch = BranchManager.getBranch(branchData.getNewBranchId());
         monitor.worked(calculateWork(0.40));

         if (branchData.isNeedsMerge()) {
            commitOldWorkingIntoNewWorkingBranch(monitor, originalBranch, newBranch, 0.40);
         } else {
            deleteOldAndSetNewAsWorking(monitor, originalBranch, newBranch, 0.40);
         }
         wasSuccessful = true;
      } finally {
         if (newBranch != null && !wasSuccessful) {
            BranchManager.purgeBranch(newBranch);
            BranchManager.setState(originalBranch, originalState);
         }
         monitor.worked(calculateWork(0.20));
         branchEp.logBranchActivity(
            String.format("Branch Operation Update Branch {branchUUID: %s, branchName: %s fromBranch: %s",
               originalBranch.getIdString(), originalBranch.getName(), fromBranch));
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

   private void deleteOldAndSetNewAsWorking(IProgressMonitor monitor, BranchToken originalBranch, BranchId newWorkingBranch, double workPercentage) throws Exception {
      String originalBranchName = originalBranch.getName();

      BranchManager.setName(originalBranch, getUpdatedName(originalBranchName));
      monitor.worked(calculateWork(0.20));

      BranchManager.setName(newWorkingBranch, originalBranchName);
      BranchManager.setAssociatedArtifactId(newWorkingBranch, BranchManager.getAssociatedArtifactId(originalBranch));
      BranchManager.setState(originalBranch, BranchState.REBASELINED);

      BranchManager.deleteBranch(originalBranch).join();
      monitor.worked(calculateWork(workPercentage));
   }

   public BranchToken getNewBranch() {
      return newBranch;
   }
}
