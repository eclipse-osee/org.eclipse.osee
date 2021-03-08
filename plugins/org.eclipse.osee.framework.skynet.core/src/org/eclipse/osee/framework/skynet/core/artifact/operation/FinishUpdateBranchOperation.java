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
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class FinishUpdateBranchOperation extends AbstractOperation {
   private final ConflictManagerExternal conflictManager;
   private final boolean archiveSourceBranch;
   private final boolean overwriteUnresolvedConflicts;

   public FinishUpdateBranchOperation(ConflictManagerExternal conflictManager, boolean archiveSourceBranch, boolean overwriteUnresolvedConflicts) {
      super(String.format("Complete Branch Update [%s]", conflictManager.getSourceBranch().getShortName()),
         Activator.PLUGIN_ID);
      this.conflictManager = conflictManager;
      this.archiveSourceBranch = archiveSourceBranch;
      this.overwriteUnresolvedConflicts = overwriteUnresolvedConflicts;
   }

   private String getUpdatedName(String branchName) {
      String storeName = Strings.truncate(branchName, 100);
      return String.format("%s - moved by update on - %s", storeName, Lib.getDateTimeString());
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      monitor.setTaskName("Merging updates");
      BranchManager.commitBranch(null, conflictManager, archiveSourceBranch, overwriteUnresolvedConflicts);
      monitor.worked(calculateWork(0.60));

      BranchToken sourceBranch = conflictManager.getSourceBranch();
      BranchId destinationBranch = conflictManager.getDestinationBranch();

      String originalBranchName = sourceBranch.getName();

      BranchManager.setName(sourceBranch, getUpdatedName(originalBranchName));
      monitor.worked(calculateWork(0.20));

      BranchManager.setName(destinationBranch, originalBranchName);
      BranchManager.setAssociatedArtifactId(destinationBranch, BranchManager.getAssociatedArtifactId(sourceBranch));
      BranchManager.setState(sourceBranch, BranchState.REBASELINED);

      monitor.worked(calculateWork(0.20));
   }
}