/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.change.operations;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

public class LoadAssociatedArtifactOperation extends AbstractOperation {
   private final ChangeUiData changeData;

   public LoadAssociatedArtifactOperation(ChangeUiData changeData) {
      super("Load Associated Artifact", Activator.PLUGIN_ID);
      this.changeData = changeData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Artifact associatedArtifact = null;
      TransactionDelta txDelta = changeData.getTxDelta();
      if (changeData.getCompareType().areSpecificTxs()) {

         Long commitId = TransactionManager.getCommitArtId(txDelta.getEndTx());
         if (!commitId.equals(0)) {
            associatedArtifact = ArtifactQuery.getArtifactFromId(commitId, COMMON);
         }
      } else {
         BranchId sourceBranch = txDelta.getStartTx().getBranch();
         if (!BranchManager.getState(sourceBranch).isDeleted()) {
            associatedArtifact = BranchManager.getAssociatedArtifact(sourceBranch);
         }
      }
      monitor.worked(calculateWork(0.80));
      changeData.setAssociatedArtifact(associatedArtifact);
      monitor.worked(calculateWork(0.20));
   }
}