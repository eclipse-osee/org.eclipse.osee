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
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.internal.change.ComputeNetChangeCallable;
import org.eclipse.osee.orcs.db.internal.change.LoadDeltasBetweenBranches;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;

/**
 * @author Roberto E. Escobar
 */
public class CommitBranchDatabaseCallable extends AbstractDatastoreCallable<TransactionRecord> {

   private final TransactionRecordFactory txFactory;
   private final BranchCache branchCache;
   private final ArtifactReadable committer;
   private final Branch source;
   private final Branch destination;
   private final MissingChangeItemFactory missingChangeItemFactory;

   public CommitBranchDatabaseCallable(Log logger, OrcsSession session, IOseeDatabaseService service, BranchCache branchCache, TransactionRecordFactory txFactory, ArtifactReadable committer, Branch source, Branch destination, MissingChangeItemFactory missingChangeItemFactory) {
      super(logger, session, service);
      this.branchCache = branchCache;
      this.txFactory = txFactory;
      this.committer = committer;
      this.source = source;
      this.destination = destination;
      this.missingChangeItemFactory = missingChangeItemFactory;
   }

   private BranchCache getBranchCache() {
      return branchCache;
   }

   private TransactionRecordFactory getTxFactory() {
      return txFactory;
   }

   private TransactionRecord getHeadTx(Branch branch) throws OseeCoreException {
      return getBranchCache().getTransaction(branch, TransactionVersion.HEAD);
   }

   private Branch getMergeBranch(Branch sourceBranch, Branch destinationBranch) throws OseeCoreException {
      return getBranchCache().findMergeBranch(sourceBranch, destinationBranch);
   }

   private TransactionRecord getMergeTx(Branch mergeBranch) throws OseeCoreException {
      return mergeBranch != null ? getBranchCache().getTransaction(mergeBranch, TransactionVersion.HEAD) : null;
   }

   private int getUserArtId() {
      return committer != null ? committer.getLocalId() : -1;
   }

   private List<ChangeItem> callComputeChanges(Branch mergeBranch) throws Exception {
      TransactionRecord sourceTx = getHeadTx(source);
      TransactionRecord destinationTx = getHeadTx(destination);

      TransactionRecord mergeTx = getMergeTx(mergeBranch);

      Long mergeBranchId = null;
      Integer mergeTxId = null;
      if (mergeTx != null) {
         mergeBranchId = mergeTx.getBranchId();
         mergeTxId = mergeTx.getId();
      }

      Callable<List<ChangeItem>> loadChanges =
         new LoadDeltasBetweenBranches(getLogger(), getSession(), getDatabaseService(), source.getUuid(),
            destinationTx.getBranchId(), destinationTx.getId(), mergeBranchId, mergeTxId);
      List<ChangeItem> changes = callAndCheckForCancel(loadChanges);

      changes.addAll(missingChangeItemFactory.createMissingChanges(this, getSession(), changes, sourceTx, destinationTx));

      Callable<List<ChangeItem>> computeChanges = new ComputeNetChangeCallable(changes);
      return callAndCheckForCancel(computeChanges);
   }

   @Override
   public TransactionRecord call() throws Exception {
      Branch mergeBranch = getMergeBranch(source, destination);
      List<ChangeItem> changes = callComputeChanges(mergeBranch);

      CancellableCallable<TransactionRecord> commitCallable =
         new CommitBranchDatabaseTxCallable(getLogger(), getSession(), getDatabaseService(), getBranchCache(),
            getUserArtId(), source, destination, mergeBranch, changes, getTxFactory());
      TransactionRecord commitTransaction = callAndCheckForCancel(commitCallable);

      getBranchCache().cache(commitTransaction);

      return commitTransaction;
   }

}
