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
import org.eclipse.osee.framework.branch.management.IBranchCommitService;
import org.eclipse.osee.framework.branch.management.change.ComputeNetChangeOperation;
import org.eclipse.osee.framework.branch.management.change.LoadChangeDataOperation;
import org.eclipse.osee.framework.branch.management.internal.InternalBranchActivator;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public class BranchCommitService implements IBranchCommitService {

   private final BranchCache branchCache;
   private final TransactionCache transactionCache;
   private final IOseeDatabaseServiceProvider oseeDatabaseProvider;

   public BranchCommitService(IOseeDatabaseServiceProvider oseeDatabaseProvider, BranchCache branchCache, TransactionCache transactionCache) {
      this.branchCache = branchCache;
      this.transactionCache = transactionCache;
      this.oseeDatabaseProvider = oseeDatabaseProvider;
   }

   @Override
   public void commitBranch(IProgressMonitor monitor, BranchCommitRequest branchCommitData, BranchCommitResponse response) throws OseeCoreException {
      IBasicArtifact<?> user = branchCommitData.getUser();
      Branch sourceBranch = branchCommitData.getSourceBranch();

      Branch destinationBranch = branchCommitData.getDestinationBranch();

      Branch mergeBranch = branchCache.getMergeBranch(sourceBranch, destinationBranch);
      TransactionVersion txVersion = TransactionVersion.HEAD;
      TransactionRecord sourceTx = transactionCache.getTransaction(sourceBranch, txVersion);
      TransactionRecord destinationTx = transactionCache.getTransaction(destinationBranch, txVersion);
      TransactionRecord mergeTx = transactionCache.getTransaction(mergeBranch, txVersion);

      List<ChangeItem> changes = new ArrayList<ChangeItem>();

      List<IOperation> ops = new ArrayList<IOperation>();
      ops.add(new LoadChangeDataOperation(oseeDatabaseProvider, sourceTx, destinationTx, mergeTx, changes));
      ops.add(new ComputeNetChangeOperation(changes));
      ops.add(new CommitDbOperation(oseeDatabaseProvider, branchCache, transactionCache, user, sourceBranch,
            destinationBranch, mergeBranch, changes, response));

      String opName =
            String.format("Commit: [%s]->[%s]", sourceBranch.getShortName(), destinationBranch.getShortName());
      IOperation op = new CompositeOperation(opName, InternalBranchActivator.PLUGIN_ID, ops);

      Operations.executeWorkAndCheckStatus(op, monitor, -1);

      if (branchCommitData.isArchiveAllowed()) {
         sourceBranch.setArchived(true);
         branchCache.storeItem(sourceBranch);
      }
   }
}
