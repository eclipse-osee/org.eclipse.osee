/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.core.internal.transaction;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.model.dto.ChangeReportRowDto;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Roberto E. Escobar
 */
public class TransactionFactoryImpl implements TransactionFactory {

   private final OrcsSession session;
   private final TxDataManager txDataManager;
   private final TxCallableFactory txCallableFactory;
   private final OrcsApi orcsApi;
   private final QueryFactory queryFactory;
   private final OrcsBranch orcsBranch;
   private final KeyValueOps keyValueOps;
   private final TxDataStore txDataStore;
   private final TransactionQuery transactionQuery;

   public TransactionFactoryImpl(OrcsSession session, TxDataManager txDataManager, TxCallableFactory txCallableFactory, OrcsApi orcsApi, OrcsBranch orcsBranch, KeyValueOps keyValueOps, TxDataStore txDataStore) {
      this.session = session;
      this.txDataManager = txDataManager;
      this.txCallableFactory = txCallableFactory;
      this.orcsApi = orcsApi;
      this.queryFactory = orcsApi.getQueryFactory();
      this.orcsBranch = orcsBranch;
      this.keyValueOps = keyValueOps;
      this.txDataStore = txDataStore;
      this.transactionQuery = queryFactory.transactionQuery();
   }

   @Override
   public CancellableCallable<Integer> purgeTransaction(Collection<? extends TransactionId> transactions) {
      return txCallableFactory.purgeTransactions(session, transactions);
   }

   @Override
   public TransactionBuilder createTransaction(BranchId branch, String comment) {
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNullOrEmpty(comment, "comment");
      if (!queryFactory.branchQuery().andId(branch).exists()) {
         throw new ItemDoesNotExist("BranchId %s does not exist", branch);
      }

      TxData txData = txDataManager.createTxData(session, branch);
      TransactionBuilderImpl orcsTxn =
         new TransactionBuilderImpl(txCallableFactory, txDataManager, txData, orcsApi, keyValueOps);
      orcsTxn.setComment(comment);
      orcsTxn.setAuthor(orcsApi.userService().getUser());
      return orcsTxn;

   }

   @Override
   public TransactionBuilder createTransaction(BranchId branch, UserId author, String comment) {
      TxData txData = txDataManager.createTxData(session, branch);
      TransactionBuilderImpl orcsTxn =
         new TransactionBuilderImpl(txCallableFactory, txDataManager, txData, orcsApi, keyValueOps);
      orcsTxn.setComment(comment);
      UserToken user = orcsApi.userService().getUser(author.getId());
      orcsTxn.setAuthor(user);
      return orcsTxn;
   }

   @Override
   public Callable<Void> setTransactionComment(TransactionId transaction, String comment) {
      return txCallableFactory.setTransactionComment(session, transaction, comment);
   }

   @Override
   public List<ChangeItem> compareTxs(TransactionId txId1, TransactionId txId2) {
      TransactionToken sourceTx = getTx(txId1);
      TransactionToken destinationTx = getTx(txId2);

      try {
         return orcsBranch.compareBranch(sourceTx, destinationTx);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public List<ChangeItem> comparedToParent(BranchId branch) {
      BranchId parentBranch = queryFactory.branchQuery().andId(branch).getResults().getExactlyOne().getParentBranch();
      TransactionId sourceTx = transactionQuery.andIsHead(branch).getResultsAsIds().getExactlyOne();
      TransactionId destionationTx = transactionQuery.andIsHead(parentBranch).getResultsAsIds().getExactlyOne();
      return compareTxs(sourceTx, destionationTx);
   }

   @Override
   public List<ChangeItem> comparedToPreviousTx(TransactionToken txId) {
      TransactionId startTx = transactionQuery.andIsPriorTx(txId).getResultsAsIds().getExactlyOne();
      return compareTxs(startTx, txId);
   }

   @Override
   public boolean replaceWithBaselineTxVersion(UserId userId, BranchId branchId, TransactionId txId, ArtifactId artId,
      String comment) {
      boolean introduced = false;
      ArtifactReadable baselineArtifact =
         queryFactory.fromBranch(branchId).fromTransaction(txId).andId(artId).getResults().getOneOrDefault(
            ArtifactReadable.SENTINEL);

      if (userId.isValid() && baselineArtifact.isValid()) {
         TransactionBuilder tx = createTransaction(branchId, userId, comment);
         ArtifactReadable destination =
            queryFactory.fromBranch(branchId).includeDeletedArtifacts().andId(artId).getResults().getOneOrDefault(
               ArtifactReadable.SENTINEL);
         tx.replaceWithVersion(baselineArtifact, destination);
         tx.commit();
         introduced = true;
      } else {
         throw new OseeCoreException("%s Error - The user and/or baseline artifact were not found.", comment);
      }

      return introduced;
   }

   @Override
   public boolean purgeTxs(String txIds) {
      boolean modified = false;
      List<TransactionId> txsToDelete = Collections.fromString(txIds, TransactionId::valueOf);

      if (!txsToDelete.isEmpty()) {
         ResultSet<? extends TransactionId> results = transactionQuery.andTxIds(txsToDelete).getResults();
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
      return transactionQuery.getResults();
   }

   @Override
   public TransactionReadable getTx(TransactionId tx) {
      if (tx instanceof TransactionReadable) {
         return (TransactionReadable) tx;
      }
      return transactionQuery.andTxId(tx).getResults().getExactlyOne();
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
   public boolean setTransactionCommitArtifact(TransactionId trans, ArtifactId commitArt) {
      TransactionReadable tx = getTx(trans);
      boolean modified = false;
      if (Compare.isDifferent(tx.getCommitArt(), commitArt)) {
         txCallableFactory.setTransactionCommitArtifact(session, trans, commitArt);
         modified = true;
      }
      return modified;
   }

   @Override
   public List<ChangeItem> getArtifactHistory(ArtifactId artifact, BranchId branch) {
      try {
         return txDataStore.getArtifactHistory(session, queryFactory, artifact, branch).call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public List<ChangeReportRowDto> getTxChangeReport(BranchId branch1, BranchId branch2, TransactionId txId1,
      TransactionId txId2) {
      List<ChangeReportRowDto> changeReportRows = new LinkedList<>();
      List<ChangeItem> changeItems = compareTxs(txId1, txId2);

      List<ArtifactId> artIds = new LinkedList<>();

      for (ChangeItem change : changeItems) {
         if (change.getArtId().isValid() && !artIds.contains(change.getArtId())) {
            artIds.add(change.getArtId());
         }
         if (change.getArtIdB().isValid() && !artIds.contains(change.getArtIdB())) {
            artIds.add(change.getArtIdB());
         }
      }

      List<ArtifactReadable> artifacts = orcsApi.getQueryFactory().fromBranch(branch1).andIds(artIds).asArtifacts();

      for (ChangeItem changeItem : changeItems) {
         ArtifactReadable artA = artifacts.stream().filter(
            a -> ArtifactId.valueOf(a.getId()).equals(changeItem.getArtId())).findFirst().orElse(
               ArtifactReadable.SENTINEL);
         ArtifactReadable artB = changeItem.getArtIdB().isValid() ? artifacts.stream().filter(
            a -> ArtifactId.valueOf(a.getId()).equals(changeItem.getArtIdB())).findFirst().orElse(
               ArtifactReadable.SENTINEL) : ArtifactReadable.SENTINEL;

         if (!artA.isValid() && branch2.isValid()) {
            artA = orcsApi.getQueryFactory().fromBranch(branch2).andIds(changeItem.getArtId()).asArtifact();
            if (!artA.isValid()) {
               continue;
            }
         }

         if (changeItem.getArtIdB().isValid() && !artB.isValid() && branch2.isValid()) {
            artB = orcsApi.getQueryFactory().fromBranch(branch2).andIds(changeItem.getArtIdB()).asArtifact();
         }

         ApplicabilityToken currentApplic = ApplicabilityToken.SENTINEL;
         ApplicabilityToken baselineApplic = ApplicabilityToken.SENTINEL;
         ChangeType itemKindType = changeItem.getChangeType();
         ModificationType modType = changeItem.getNetChange().getModType();
         String isValue = itemKindType.equals(ChangeType.Relation) ? "" : changeItem.getCurrentVersion().getValue();
         String wasValue = itemKindType.equals(ChangeType.Relation) ? "" : changeItem.getBaselineVersion().getValue();
         String changeType = modType.getName();

         // Artifact Names
         String names = artA.getName();
         if (itemKindType.equals(ChangeType.Relation) && artB.isValid()) {
            names += " <---> " + artB.getName();
         }

         // Item Type
         String itemType = "";
         if (itemKindType.equals(ChangeType.Artifact)) {
            itemType = artA.getArtifactType().getName();
         } else if (itemKindType.equals(ChangeType.Attribute)) {
            AttributeTypeToken attrToken = artA.getExistingAttributeTypes().stream().filter(
               a -> a.getIdString().equals(changeItem.getItemTypeId().getIdString())).findFirst().orElse(
                  AttributeTypeToken.SENTINEL);
            itemType = attrToken.isValid() ? attrToken.getName() : "";
         } else if (itemKindType.equals(ChangeType.Relation)) {
            RelationTypeToken relToken = artA.getExistingRelationTypes().stream().filter(
               a -> a.getIdString().equals(changeItem.getItemTypeId().getIdString())).findFirst().orElse(
                  RelationTypeToken.SENTINEL);
            itemType = relToken.isValid() ? relToken.getName() : "";
         }

         // Handle applicability changes
         if (itemKindType.equals(ChangeType.Artifact)) {
            currentApplic = changeItem.getCurrentVersion().getApplicabilityToken();
            baselineApplic = changeItem.getBaselineVersion().getApplicabilityToken();
            if (currentApplic != null && baselineApplic != null && !currentApplic.equals(baselineApplic)) {
               changeType = "Applicability";
               isValue = currentApplic.getName();
               wasValue = baselineApplic.getName();
            }
            if (currentApplic == null) {
               currentApplic = ApplicabilityToken.SENTINEL;
            }
            if (baselineApplic == null) {
               baselineApplic = ApplicabilityToken.SENTINEL;
            }
         }

         // Special case for modified artifact is/was values
         if (itemKindType.equals(ChangeType.Artifact) && modType.equals(ModificationType.MODIFIED)) {
            isValue = "";
            wasValue = "";
         }

         changeReportRows.add(new ChangeReportRowDto(artA, artB, names, itemType, changeType, isValue, wasValue,
            itemKindType, modType, currentApplic, baselineApplic));
      }

      changeReportRows.sort(new Comparator<ChangeReportRowDto>() {
         @Override
         public int compare(ChangeReportRowDto o1, ChangeReportRowDto o2) {
            return o1.getArtA().getId().compareTo(o2.getArtA().getId());
         }
      });

      return changeReportRows;
   }

}
