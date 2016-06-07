/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.search.QueryModule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.CompareResults;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * @author Roberto E. Escobar
 */
public class TransactionFactoryImpl implements TransactionFactory {

   private final OrcsSession session;
   private final TxDataManager txDataManager;
   private final TxCallableFactory txCallableFactory;
   private final QueryModule query;
   private final QueryFactory queryFactory;
   private final OrcsBranch orcsBranch;
   private final KeyValueOps keyValueOps;

   public TransactionFactoryImpl(OrcsSession session, TxDataManager txDataManager, TxCallableFactory txCallableFactory, QueryModule query, QueryFactory queryFactory, OrcsBranch orcsBranch, KeyValueOps keyValueOps) {
      super();
      this.session = session;
      this.txDataManager = txDataManager;
      this.txCallableFactory = txCallableFactory;
      this.query = query;
      this.queryFactory = queryFactory;
      this.orcsBranch = orcsBranch;
	  this.keyValueOps = keyValueOps;
   }

   @Override
   public CancellableCallable<Integer> purgeTransaction(Collection<? extends ITransaction> transactions) {
      return txCallableFactory.purgeTransactions(session, transactions);
   }

   @Override
   public TransactionBuilder createTransaction(Long branchId, ArtifactReadable userArtifact, String comment) throws OseeCoreException {
      BranchId branch = TokenFactory.createBranch(branchId);
      return createTransaction(branch, userArtifact, comment);
   }

   @Override
   public TransactionBuilder createTransaction(BranchId branch, ArtifactReadable author, String comment) throws OseeCoreException {
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(author, "author");
      Conditions.checkNotNullOrEmpty(comment, "comment");

      TxData txData = txDataManager.createTxData(session, branch.getUuid());
      TransactionBuilderImpl orcsTxn =
         new TransactionBuilderImpl(txCallableFactory, txDataManager, txData, query, keyValueOps);
      orcsTxn.setComment(comment);
      orcsTxn.setAuthor(author);
      return orcsTxn;
   }

   @Override
   public Callable<Void> setTransactionComment(ITransaction transaction, String comment) {
      return txCallableFactory.setTransactionComment(session, transaction, comment);
   }

   @Override
   public CompareResults compareTxs(int txId1, int txId2) {
      TransactionReadable sourceTx = getTxById(txId1);
      TransactionReadable destinationTx = getTxById(txId2);
      Callable<List<ChangeItem>> callable = orcsBranch.compareBranch(sourceTx, destinationTx);
      List<ChangeItem> changes = OrcsTransactionUtil.executeCallable(callable);

      CompareResults data = new CompareResults();
      data.setChanges(changes);
      return data;
   }

   @Override
   public boolean replaceWithBaselineTxVersion(String userId, Long branchId, int txId, int artId, String comment) {
      boolean introduced = false;
      ArtifactReadable userReadable =
         queryFactory.fromBranch(CoreBranches.COMMON).andGuid(userId).getResults().getOneOrNull();
      ArtifactReadable baselineArtifact =
         queryFactory.fromBranch(branchId).fromTransaction(txId).andUuid(artId).getResults().getOneOrNull();

      if (userReadable != null && baselineArtifact != null) {
         TransactionBuilder tx = createTransaction(branchId, userReadable, comment);
         ArtifactReadable destination =
            queryFactory.fromBranch(branchId).includeDeletedArtifacts().andUuid(artId).getResults().getOneOrNull();
         tx.replaceWithVersion(baselineArtifact, destination);
         tx.commit();
         introduced = true;
      } else {
         throw new OseeCoreException("%s Error - The user and baseline artifact were not found.", comment);
      }

      return introduced;
   }

   @Override
   public boolean purgeTxs(String txIds) {
      boolean modified = false;
      List<Integer> txsToDelete = OrcsTransactionUtil.asIntegerList(txIds);
      if (!txsToDelete.isEmpty()) {
         ResultSet<TransactionReadable> results = queryFactory.transactionQuery().andTxIds(txsToDelete).getResults();
         if (!results.isEmpty()) {
            checkAllTxsFound("Purge Transaction", txsToDelete, results);
            List<TransactionReadable> list = Lists.newArrayList(results);
            Callable<?> op = purgeTransaction(list);
            OrcsTransactionUtil.executeCallable(op);
            modified = true;
         }
      }
      return modified;
   }

   @Override
   public boolean setTxComment(int txId, String comment) {
      TransactionReadable tx = getTxById(txId);
      boolean modified = false;
      if (Compare.isDifferent(tx.getComment(), comment)) {
         setTransactionComment(tx, comment);
         modified = true;
      }
      return modified;
   }

   @Override
   public ResultSet<TransactionReadable> getAllTxs() {
      return queryFactory.transactionQuery().getResults();
   }

   @Override
   public TransactionReadable getTx(int txId) {
      return getTxById(txId);
   }

   @Override
   public TransactionReadable getTxById(int txId) {
      ResultSet<TransactionReadable> results = queryFactory.transactionQuery().andTxId(txId).getResults();
      return results.getExactlyOne();
   }

   private void checkAllTxsFound(String opName, List<Integer> txIds, ResultSet<TransactionReadable> result) {
      if (txIds.size() != result.size()) {
         Set<Integer> found = new HashSet<>();
         for (TransactionReadable tx : result) {
            found.add(tx.getGuid());
         }
         SetView<Integer> difference = Sets.difference(Sets.newHashSet(txIds), found);
         if (!difference.isEmpty()) {
            throw new OseeCoreException(
               "%s Error - The following transactions from %s were not found - txs %s - Please remove them from the request and try again.",
               opName, txIds, difference);
         }
      }
   }
}
