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

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.CompareResults;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.core.internal.search.QueryModule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

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
   private final TxDataStore txDataStore;

   public TransactionFactoryImpl(OrcsSession session, TxDataManager txDataManager, TxCallableFactory txCallableFactory, QueryModule query, QueryFactory queryFactory, OrcsBranch orcsBranch, KeyValueOps keyValueOps, TxDataStore txDataStore) {
      super();
      this.session = session;
      this.txDataManager = txDataManager;
      this.txCallableFactory = txCallableFactory;
      this.query = query;
      this.queryFactory = queryFactory;
      this.orcsBranch = orcsBranch;
      this.keyValueOps = keyValueOps;
      this.txDataStore = txDataStore;
   }

   @Override
   public CancellableCallable<Integer> purgeTransaction(Collection<? extends TransactionId> transactions) {
      return txCallableFactory.purgeTransactions(session, transactions);
   }

   @Override
   public TransactionBuilder createTransaction(BranchId branch, ArtifactId author, String comment)  {
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(author, "author");
      Conditions.checkNotNullOrEmpty(comment, "comment");

      TxData txData = txDataManager.createTxData(session, branch);
      TransactionBuilderImpl orcsTxn =
         new TransactionBuilderImpl(txCallableFactory, txDataManager, txData, query, keyValueOps);
      orcsTxn.setComment(comment);
      orcsTxn.setAuthor(UserId.valueOf(author.getId()));
      return orcsTxn;
   }

   @Override
   public Callable<Void> setTransactionComment(TransactionId transaction, String comment) {
      return txCallableFactory.setTransactionComment(session, transaction, comment);
   }

   @Override
   public CompareResults compareTxs(TransactionId txId1, TransactionId txId2) {
      TransactionToken sourceTx = getTx(txId1);
      TransactionToken destinationTx = getTx(txId2);

      try {
         List<ChangeItem> changes = orcsBranch.compareBranch(sourceTx, destinationTx).call();
         CompareResults data = new CompareResults();
         data.setChanges(changes);
         return data;
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public boolean replaceWithBaselineTxVersion(String userId, BranchId branchId, TransactionId txId, int artId, String comment) {
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
      List<TransactionId> txsToDelete = Collections.fromString(txIds, TransactionId::valueOf);

      if (!txsToDelete.isEmpty()) {
         ResultSet<? extends TransactionId> results =
            queryFactory.transactionQuery().andTxIds(txsToDelete).getResults();
         if (!results.isEmpty()) {
            checkAllTxsFound("Purge Transaction", txsToDelete, results);
            List<TransactionId> list = Lists.newArrayList(results);
            try {
               purgeTransaction(list).call();
            } catch (Exception ex) {
               throw OseeCoreException.wrap(ex);
            }
            modified = true;
         }
      }
      return modified;
   }

   @Override
   public boolean setTxComment(TransactionId txId, String comment) {
      TransactionReadable tx = getTx(txId);
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
   public TransactionReadable getTx(TransactionId tx) {
      if (tx instanceof TransactionReadable) {
         return (TransactionReadable) tx;
      }
      return queryFactory.transactionQuery().andTxId(tx).getResults().getExactlyOne();
   }

   private void checkAllTxsFound(String opName, List<TransactionId> txIds, ResultSet<? extends TransactionId> result) {
      if (txIds.size() != result.size()) {
         List<TransactionId> difference = Collections.setComplement(txIds, result.getList());
         if (!difference.isEmpty()) {
            throw new OseeNotFoundException(
               "%s Error - The following transactions from %s were not found - txs %s - Please remove them from the request and try again.",
               opName, txIds, difference);
         }
      }
   }

   @Override
   public int[] purgeUnusedBackingDataAndTransactions() {
      return txDataStore.purgeUnusedBackingDataAndTransactions();
   }

   @Override
   public boolean setTransactionCommitArtifact(TransactionId trans, ArtifactToken commitArt) {
      TransactionReadable tx = getTx(trans);
      boolean modified = false;
      if (Compare.isDifferent(tx.getCommitArt(), commitArt)) {
         txCallableFactory.setTransactionCommitArtifact(session, trans, commitArt);
         modified = true;
      }
      return modified;
   }

}
