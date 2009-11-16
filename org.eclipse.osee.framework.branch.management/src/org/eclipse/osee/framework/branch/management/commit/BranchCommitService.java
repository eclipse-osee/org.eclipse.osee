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
package org.eclipse.osee.framework.branch.management.commit;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.branch.management.IBranchCommitService;
import org.eclipse.osee.framework.branch.management.ITransactionService;
import org.eclipse.osee.framework.branch.management.ITransactionService.TransactionVersion;
import org.eclipse.osee.framework.branch.management.change.ComputeNetChangeOperation;
import org.eclipse.osee.framework.branch.management.change.LoadChangeDataOperation;
import org.eclipse.osee.framework.branch.management.internal.InternalBranchActivator;
import org.eclipse.osee.framework.core.data.AbstractOseeCache;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCommitData;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.CommitTransactionRecordResponse;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public class BranchCommitService implements IBranchCommitService {

   @Override
   public IStatus commitBranch(IProgressMonitor monitor, ITransactionService txService, BranchCommitData branchCommitData, CommitTransactionRecordResponse txHolder) throws OseeCoreException {
      return commitBranch(monitor, txService, branchCommitData.getUser(), branchCommitData.getSourceBranch(),
            branchCommitData.getDestinationBranch(), txHolder, branchCommitData.isArchiveAllowed());
   }

   @Override
   public IStatus commitBranch(IProgressMonitor monitor, ITransactionService txService, IBasicArtifact<?> user, Branch sourceBranch, Branch destinationBranch, CommitTransactionRecordResponse txHolder, boolean archiveSourceBranch) throws OseeCoreException {
      AbstractOseeCache<Branch> branchCache = null;
      Branch mergeBranch = null;

      // TODO this can be obtained through the cache
      //      BranchManager.getMergeBranch(sourceBranch, destinationBranch);
      TransactionVersion txVersion = TransactionVersion.HEAD;
      TransactionRecord sourceTx = txService.getTransaction(sourceBranch, txVersion);
      TransactionRecord destinationTx = txService.getTransaction(destinationBranch, txVersion);
      TransactionRecord mergeTx = txService.getTransaction(mergeBranch, txVersion);

      List<ChangeItem> changes = new ArrayList<ChangeItem>();

      List<IOperation> ops = new ArrayList<IOperation>();
      ops.add(new LoadChangeDataOperation(sourceTx, destinationTx, mergeTx, changes));
      ops.add(new ComputeNetChangeOperation(changes));
      ops.add(new CommitDbOperation(branchCache, user, sourceBranch, destinationBranch, mergeBranch, changes, txHolder));

      String opName =
            String.format("Commit: [%s]->[%s]", sourceBranch.getShortName(), destinationBranch.getShortName());
      IOperation op = new CompositeOperation(opName, InternalBranchActivator.PLUGIN_ID, ops);

      Operations.executeWorkAndCheckStatus(op, monitor, -1);

      if (archiveSourceBranch) {
         sourceBranch.setArchived(true);
         // TODO        BranchManager.persist(sourceBranch);
      }
      return op.getStatus();
   }
}
