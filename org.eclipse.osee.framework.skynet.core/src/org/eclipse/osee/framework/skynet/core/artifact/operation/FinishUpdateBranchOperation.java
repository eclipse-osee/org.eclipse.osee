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
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Roberto E. Escobar
 */
public class FinishUpdateBranchOperation extends AbstractOperation {
   private final ConflictManagerExternal conflictManager;
   private final boolean archiveSourceBranch;
   private final boolean overwriteUnresolvedConflicts;

   /**
    * @param operationName
    * @param pluginId
    */
   public FinishUpdateBranchOperation(String pluginId, ConflictManagerExternal conflictManager, boolean archiveSourceBranch, boolean overwriteUnresolvedConflicts) {
      super("Complete Branch Update", pluginId);
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

      Branch sourceBranch = conflictManager.getSourceBranch();
      Branch destinationBranch = conflictManager.getDestinationBranch();

      String originalBranchName = sourceBranch.getName();
      IArtifact originalAssociatedArtifact = sourceBranch.getAssociatedArtifact();

      sourceBranch.setName(getUpdatedName(originalBranchName));
      monitor.worked(calculateWork(0.20));

      destinationBranch.setName(originalBranchName);
      destinationBranch.setAssociatedArtifact(originalAssociatedArtifact);
      sourceBranch.setBranchState(BranchState.REBASELINED);

      BranchManager.persist(sourceBranch, destinationBranch);

      monitor.worked(calculateWork(0.20));
   }

   @Override
   protected IStatus createErrorStatus(Throwable error) {
      setStatusMessage(String.format("Error merging updates between [%s] and [%s]",
            conflictManager.getSourceBranch().getShortName(), conflictManager.getDestinationBranch().getShortName()));
      return super.createErrorStatus(error);
   }

}
