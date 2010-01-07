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
package org.eclipse.osee.framework.branch.management.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.branch.management.change.ComputeNetChangeOperation;
import org.eclipse.osee.framework.branch.management.change.LoadChangeDataOperation;
import org.eclipse.osee.framework.branch.management.commit.CommitDbOperation;
import org.eclipse.osee.framework.branch.management.creation.CreateBranchOperation;
import org.eclipse.osee.framework.branch.management.purge.PurgeBranchOperation;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.data.BranchCreationRequest;
import org.eclipse.osee.framework.core.data.BranchCreationResponse;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportRequest;
import org.eclipse.osee.framework.core.data.ChangeReportResponse;
import org.eclipse.osee.framework.core.data.PurgeBranchRequest;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.LogProgressMonitor;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IOseeBranchService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;

/**
 * @author Jeff C. Phillips
 * @author Megumi Telles
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 */
public class OseeBranchService implements IOseeBranchService {
   private final IOseeDatabaseServiceProvider oseeDatabaseProvider;
   private final IOseeCachingServiceProvider cachingService;
   private final IOseeModelFactoryServiceProvider modelFactory;

   public OseeBranchService(IOseeDatabaseServiceProvider oseeDatabaseProvider, IOseeCachingServiceProvider cachingService, IOseeModelFactoryServiceProvider modelFactory) {
      super();
      this.oseeDatabaseProvider = oseeDatabaseProvider;
      this.cachingService = cachingService;
      this.modelFactory = modelFactory;
   }

   @Override
   public void commitBranch(IProgressMonitor monitor, BranchCommitRequest branchCommitData, BranchCommitResponse response) throws OseeCoreException {
      int userId = branchCommitData.getUserArtId();
      BranchCache branchCache = cachingService.getOseeCachingService().getBranchCache();
      TransactionCache transactionCache = cachingService.getOseeCachingService().getTransactionCache();

      Branch sourceBranch = branchCache.getById(branchCommitData.getSourceBranchId());
      Branch destinationBranch = branchCache.getById(branchCommitData.getDestinationBranchId());
      Branch mergeBranch = branchCache.findMergeBranch(sourceBranch, destinationBranch);

      TransactionVersion txVersion = TransactionVersion.HEAD;
      TransactionRecord sourceTx =
            sourceBranch != null ? transactionCache.getTransaction(sourceBranch, txVersion) : null;
      TransactionRecord destinationTx =
            destinationBranch != null ? transactionCache.getTransaction(destinationBranch, txVersion) : null;
      TransactionRecord mergeTx = mergeBranch != null ? transactionCache.getTransaction(mergeBranch, txVersion) : null;

      List<ChangeItem> changes = new ArrayList<ChangeItem>();

      List<IOperation> ops = new ArrayList<IOperation>();
      ops.add(new LoadChangeDataOperation(oseeDatabaseProvider, sourceTx, destinationTx, mergeTx, changes));
      ops.add(new ComputeNetChangeOperation(changes));
      ops.add(new CommitDbOperation(oseeDatabaseProvider, branchCache, userId, sourceBranch, destinationBranch,
            mergeBranch, changes, response, modelFactory));

      String opName =
            String.format("Commit: [%s]->[%s]", sourceBranch.getShortName(), destinationBranch.getShortName());
      IOperation op = new CompositeOperation(opName, Activator.PLUGIN_ID, ops);
      Operations.executeWorkAndCheckStatus(op, monitor, -1);

      TransactionRecord newTransaction = response.getTransaction();
      newTransaction.setBranchCache(branchCache);
      transactionCache.cache(newTransaction);
      if (branchCommitData.isArchiveAllowed()) {
         sourceBranch.setArchived(true);
         branchCache.storeItems(sourceBranch);
      }
   }

   @Override
   public void createBranch(IProgressMonitor monitor, BranchCreationRequest request, BranchCreationResponse response) throws OseeCoreException {
      IOperation operation =
            new CreateBranchOperation(oseeDatabaseProvider, modelFactory, cachingService, request, response);
      Operations.executeWorkAndCheckStatus(operation, new LogProgressMonitor(), -1);
   }

   @Override
   public void getChanges(IProgressMonitor monitor, ChangeReportRequest request, ChangeReportResponse response) throws OseeCoreException {
      TransactionCache txCache = cachingService.getOseeCachingService().getTransactionCache();
      TransactionRecord srcTx = txCache.getOrLoad(request.getSourceTx());
      TransactionRecord destTx = txCache.getOrLoad(request.getDestinationTx());

      List<IOperation> ops = new ArrayList<IOperation>();
      if (request.isHistorical()) {
         ops.add(new LoadChangeDataOperation(oseeDatabaseProvider, srcTx, destTx, response.getChangeItems()));
      } else {
         ops.add(new LoadChangeDataOperation(oseeDatabaseProvider, srcTx, destTx, null, response.getChangeItems()));
      }
      ops.add(new ComputeNetChangeOperation(response.getChangeItems()));

      String opName = String.format("Gathering changes");
      IOperation op = new CompositeOperation(opName, Activator.PLUGIN_ID, ops);
      Operations.executeWorkAndCheckStatus(op, monitor, -1);
   }

   @Override
   public void purge(IProgressMonitor monitor, PurgeBranchRequest request) throws OseeCoreException {
      BranchCache branchCache = cachingService.getOseeCachingService().getBranchCache();
      IOperation operation =
            new PurgeBranchOperation(branchCache.getById(request.getBranchId()), cachingService, oseeDatabaseProvider);
      Operations.executeWork(operation, new NullProgressMonitor(), -1);
      try {
         Operations.checkForStatusSeverityMask(operation.getStatus(), IStatus.ERROR | IStatus.WARNING);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

}
