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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.model.dto.ChangeReportRowDto;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OseeDb;
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

      if (author == null) {
         throw new OseeCoreException(
            "In TransactionFactoryImpl.createTransaction, the parameter \"author\" is null which is dereferenced");
      }

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
   public boolean replaceWithBaselineTxVersion(BranchId branchId, TransactionId txId, ArtifactId artId,
      String comment) {
      boolean introduced = false;
      ArtifactReadable baselineArtifact =
         queryFactory.fromBranch(branchId).fromTransaction(txId).andId(artId).getResults().getOneOrDefault(
            ArtifactReadable.SENTINEL);

      if (baselineArtifact.isValid()) {
         TransactionBuilder tx = createTransaction(branchId, comment);
         ArtifactReadable destination =
            queryFactory.fromBranch(branchId).includeDeletedArtifacts().andId(artId).getResults().getOneOrDefault(
               ArtifactReadable.SENTINEL);
         tx.replaceWithVersion(baselineArtifact, destination);
         tx.commit();
         introduced = true;
      } else {
         throw new OseeCoreException("%s Error - The baseline artifact was not found.", comment);
      }

      return introduced;
   }

   @Override
   public boolean purgeTxs(String txIds) {

      boolean modified = false;
      List<String> insertTxStatements = new ArrayList<>();
      List<Long> impactedGammaIds = new ArrayList<>();
      List<Long> unusedGammas = new ArrayList<>();
      List<TransactionId> txsToDelete = Collections.fromString(txIds, TransactionId::valueOf);
      String recoveryFileNamePrefix = "purgeTxs_";
      //set up to create recovery files
      for (TransactionId txIdToDelete : txsToDelete) {
         BranchId branchId = orcsApi.getTransactionFactory().getTx(txIdToDelete).getBranch();
         orcsApi.getJdbcService().getClient().runQuery(stmt -> insertTxStatements.add(stmt.getString("insertString")),
            OseeDb.TX_DETAILS_TABLE.getSelectInsertString(" where branch_id = ? and transaction_id = ?"),
            branchId, txIdToDelete);
         orcsApi.getJdbcService().getClient().runQuery(stmt -> insertTxStatements.add(stmt.getString("insertString")),
            OseeDb.TXS_TABLE.getSelectInsertString(" where branch_id = ? and transaction_id = ?"), branchId,
            txIdToDelete);
         orcsApi.getJdbcService().getClient().runQuery(stmt -> impactedGammaIds.add(stmt.getLong("gamma_id")),
            OseeSql.SELECT_IMPACTED_GAMMAS_BY_BRANCH_TX.getSql(), branchId, txIdToDelete);
      }
      if (!txsToDelete.isEmpty()) {
         recoveryFileNamePrefix =
            recoveryFileNamePrefix + txsToDelete.get(0) + "_" + txsToDelete.get(txsToDelete.size() - 1);
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
            //Purge unused gammas if job was successful
            //recovery files are created and stored inside PurgeUnusedBackingDataAndTransactions
            for (Long gamma : impactedGammaIds) {
               orcsApi.getJdbcService().getClient().runQuery(stmt -> unusedGammas.add(stmt.getLong("gamma_id")),
                  OseeSql.UNUSED_IMPACTED_GAMMAS_AFTER_PURGE.getSql(), gamma, gamma, gamma, 0);

            }
            txDataStore.purgeUnusedBackingDataAndTransactions(unusedGammas, insertTxStatements,
               recoveryFileNamePrefix);
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
   public int[] purgeUnusedBackingDataAndTransactions(List<Long> gammasToPurge, List<String> additionalStatements,
      String prefixRecoveryFile) {
      return txDataStore.purgeUnusedBackingDataAndTransactions(gammasToPurge, additionalStatements,
         prefixRecoveryFile);
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

      for (ChangeItem changeItem : changeItems.stream().filter(a -> a.getIgnoreType().isNone()).collect(
         Collectors.toList())) {
         ArtifactReadable artA = artifacts.stream().filter(
            a -> ArtifactId.valueOf(a.getId()).equals(changeItem.getArtId())).findFirst().orElse(
               ArtifactReadable.SENTINEL);
         ArtifactReadable artB = changeItem.getArtIdB().isValid() ? artifacts.stream().filter(
            a -> ArtifactId.valueOf(a.getId()).equals(changeItem.getArtIdB())).findFirst().orElse(
               ArtifactReadable.SENTINEL) : ArtifactReadable.SENTINEL;

         if (!artA.isValid() && branch2.isValid()) {
            artA = orcsApi.getQueryFactory().fromBranch(branch2).andIds(changeItem.getArtId()).asArtifactOrSentinel();
            if (!artA.isValid()) {
               continue;
            }
         }

         if (changeItem.getArtIdB().isValid() && !artB.isValid() && branch2.isValid()) {
            artB = orcsApi.getQueryFactory().fromBranch(branch2).andIds(changeItem.getArtIdB()).asArtifactOrSentinel();
         }

         ApplicabilityToken currentApplic = ApplicabilityToken.SENTINEL;
         ApplicabilityToken baselineApplic = ApplicabilityToken.SENTINEL;
         ChangeType itemKindType = changeItem.getChangeType();
         ModificationType modType = changeItem.getNetChange().getModType();
         String isValue = itemKindType.equals(ChangeType.Relation) ? "" : changeItem.getCurrentVersion().getValue();
         String wasValue = itemKindType.equals(ChangeType.Relation) ? "" : changeItem.getBaselineVersion().getValue();
         String changeType = modType.getName();
         TransactionToken tx = changeItem.getCurrentVersion().getTransactionToken();

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

         changeReportRows.add(new ChangeReportRowDto(artA, artB, names, itemType, changeItem.getItemTypeId().getId(),
            changeType, isValue, wasValue, itemKindType, modType, currentApplic, baselineApplic, tx));
      }

      changeReportRows.sort(new Comparator<ChangeReportRowDto>() {
         @Override
         public int compare(ChangeReportRowDto o1, ChangeReportRowDto o2) {
            return o1.getArtA().getId().compareTo(o2.getArtA().getId());
         }
      });

      return changeReportRows;
   }

   @Override
   public boolean undoTx(BranchId branch, TransactionId transaction) {
      //@formatter:off
String query =
"WITH\r\n" +
   "currentTx as (SELECT * from osee_txs txs where txs.transaction_id =? and txs.branch_id=?),\r\n" +
   "newArts1 as (SELECT art.*, 2 as event_type, '' as name from osee_artifact art, currentTx txs  WHERE art.gamma_id = txs.gamma_id and txs.mod_type =1),\r\n" +
   "newArts2 as (SELECT art.*, 2 as event_type, '' as name from osee_artifact art, currentTx txs  WHERE art.gamma_id = txs.gamma_id and txs.mod_type =6),\r\n" +
   "newArts as (SELECT * FROM newArts1 UNION SELECT * FROM newArts2 ),\r\n" +
   "modifiedArts as (SELECT art.*, 8 as event_type, '' as name from osee_artifact art, currentTx txs WHERE art.gamma_id = txs.gamma_id and txs.mod_type =2),\r\n" +
   "deletedArts as (SELECT art.*, 3 as event_type, attr.value as name from osee_artifact art, currentTx txs, osee_attribute attr, osee_txs attrTxs WHERE art.gamma_id = txs.gamma_id and txs.mod_type =3 and attrTxs.transaction_id = txs.transaction_id and attr.gamma_id = attrTxs.gamma_id and attr.attr_type_id = ?),\r\n" +
   "artsCombined as (SELECT * from newArts UNION SELECT * from modifiedArts UNION SELECT * from deletedArts),\r\n" +
   "newAttr1 as (SELECT attr.*, 1 as event_type,'' as previous_value from osee_attribute attr, currentTx txs WHERE attr.gamma_id=txs.gamma_id and txs.mod_type=1),\r\n"+
   "newAttr2 as (SELECT attr.*, 1 as event_type, '' as previous_value from osee_attribute attr, currentTx txs WHERE attr.gamma_id=txs.gamma_id and txs.mod_type=6),\r\n"+
   "newAttr as (SELECT * FROM newAttr1 UNION SELECT * FROM newAttr2 ),\r\n"+
   "modifiedAttr as (SELECT attr.*, 7 as event_type from osee_attribute attr, currentTx txs WHERE attr.gamma_id=txs.gamma_id and txs.mod_type=2),\r\n"+
   "previousModifiedAttr as (SELECT attr.* from osee_attribute attr, modifiedAttr modAttr, osee_txs txs WHERE txs.branch_id = ? and txs.tx_current <>? and txs.gamma_id = attr.gamma_id and attr.attr_type_id = modAttr.attr_type_id and attr.art_id = modAttr.art_id),\r\n"+
   "fullModifiedAttr as (SELECT attr.*, prevMod.value as previous_value from modifiedAttr attr, previousModifiedAttr prevMod),\r\n"+
   "deletedAttr1 as (SELECT attr.*, 6 as event_type ,'' as previous_value from osee_attribute attr, currentTx txs WHERE attr.gamma_id=txs.gamma_id and txs.mod_type=3),\r\n"+
   "deletedAttr2 as (SELECT attr.*, 6 as event_type, '' as previous_value from osee_attribute attr, currentTx txs WHERE attr.gamma_id=txs.gamma_id and txs.mod_type=5),\r\n"+
   "deletedAttr as (SELECT * FROM deletedAttr1 UNION SELECT * FROM deletedAttr2 ),\r\n"+
   "attrCombined as (SELECT * FROM newAttr UNION SELECT * from fullModifiedAttr UNION SELECT * from deletedAttr),\r\n"+
   "newRel1 as (SELECT rel.*, 0 as event_type from osee_relation rel, currentTx txs WHERE rel.gamma_id=txs.gamma_id and txs.mod_type=1),\r\n" +
   "newRel2 as (SELECT rel.*, 0 as event_type from osee_relation rel, currentTx txs WHERE rel.gamma_id=txs.gamma_id and txs.mod_type=6),\r\n" +
   "newRel as (SELECT * from newRel1 UNION SELECT * from newRel2),\r\n" +
   "modifiedRel as (SELECT rel.*, 5 as event_type from osee_relation rel, currentTx txs WHERE rel.gamma_id=txs.gamma_id and txs.mod_type=2),\r\n" +
   "deletedRel1 as (SELECT rel.*, 5 as event_type from osee_relation rel, currentTx txs WHERE rel.gamma_id=txs.gamma_id and txs.mod_type=3),\r\n" +
   "deletedRel2 as (SELECT rel.*, 5 as event_type from osee_relation rel, currentTx txs WHERE rel.gamma_id=txs.gamma_id and txs.mod_type=5),\r\n" +
   "deletedRel as (SELECT * FROM deletedRel1 UNION SELECT * FROM deletedRel2 ),\r\n" +
   "relsCombined as (SELECT * from newRel UNION SELECT * from modifiedRel UNION SELECT * from deletedRel ),\r\n" +
   "afterArtifactLocation as (SELECT MAX(rel2.rel_order) OVER (PARTITION BY rel2.rel_type, rel2.a_art_id, rel1.event_type) as rel_order, rel2.rel_type,rel2.a_art_id, rel1.event_type as event_type \r\n" +
   "                    FROM relsCombined rel1, osee_txs txs, osee_relation rel2 WHERE \r\n" +
   "            txs.branch_id = ? and txs.tx_current =? and txs.gamma_id = rel2.gamma_id and\r\n" +
   "           rel2.a_art_id = rel1.a_art_id and rel2.rel_type = rel1.rel_type and rel2.rel_order < rel1.rel_order  ),\r\n" +
   "missingRels AS (SELECT rels.rel_order, rels.rel_type, rels.a_art_id, rels.event_type from relsCombined rels WHERE NOT EXISTS (\r\n" +
   "SELECT NULL FROM afterArtifactLocation art WHERE art.a_art_id = rels.a_art_id and art.rel_type = rels.rel_type and art.event_type = rels.event_type\r\n" +
   ")),"+
   "afterArtifactWithMissing as (SELECT DISTINCT rel_order, rel_type,a_art_id, event_type from afterArtifactLocation UNION SELECT rels.rel_order, rels.rel_type, rels.a_art_id, rels.event_type from missingRels rels),\r\n"+
   "afterArtifact as (SELECT rel.a_art_id, rel.rel_type, art.event_type, CASE WHEN art.rel_order <>0 THEN rel.b_art_id ELSE 0 END as b_art_id FROM osee_relation rel, afterArtifactWithMissing art WHERE rel.rel_order = art.rel_order and rel.rel_type = art.rel_type and rel.a_art_id =art.a_art_id),\r\n" +
   "completeNewRels as (SELECT relsCombined.rel_type, relsCombined.a_art_id, relsCombined.b_art_id, CASE WHEN afterArtifact.b_art_id <>0 THEN afterArtifact.b_art_id ELSE 0 END as rel_order, relsCombined.event_type from relsCombined, afterArtifact WHERE afterArtifact.rel_type = relsCombined.rel_type and afterArtifact.a_art_id = relsCombined.a_art_id and afterArtifact.event_type = relsCombined.event_type ),\r\n" +
   "newTuple21 as (SELECT t2.*, 9 as event_type from osee_tuple2 t2, currentTx txs  WHERE t2.gamma_id = txs.gamma_id and txs.mod_type =1),\r\n" +
   "newTuple22 as (SELECT t2.*, 9 as event_type from osee_tuple2 t2, currentTx txs  WHERE t2.gamma_id = txs.gamma_id and txs.mod_type =6),\r\n" +
   "newTuple2 as (SELECT * FROM newTuple21 UNION SELECT * FROM newTuple22 ),\r\n" +
   "modifiedTuple2 as (SELECT t2.*, 10 as event_type from osee_tuple2 t2, currentTx txs WHERE t2.gamma_id = txs.gamma_id and txs.mod_type =2),\r\n" +
   "deletedTuple2 as (SELECT t2.*, 11 as event_type from osee_tuple2 t2, currentTx txs WHERE t2.gamma_id = txs.gamma_id and txs.mod_type =3),\r\n" +
   "tuple2Combined as (SELECT * from newTuple2 UNION SELECT * from modifiedTuple2 UNION SELECT * FROM deletedTuple2),\r\n" +
   "newTuple31 as (SELECT t3.*, 12 as event_type from osee_tuple3 t3, currentTx txs  WHERE t3.gamma_id = txs.gamma_id and txs.mod_type =1),\r\n" +
   "newTuple32 as (SELECT t3.*, 12 as event_type from osee_tuple3 t3, currentTx txs  WHERE t3.gamma_id = txs.gamma_id and txs.mod_type =6),\r\n" +
   "newTuple3 as (SELECT * FROM newTuple31 UNION SELECT * FROM newTuple32 ),\r\n" +
   "modifiedTuple3 as (SELECT t3.*, 13 as event_type from osee_tuple3 t3, currentTx txs WHERE t3.gamma_id = txs.gamma_id and txs.mod_type =2),\r\n" +
   "deletedTuple3 as (SELECT t3.*, 14 as event_type from osee_tuple3 t3, currentTx txs WHERE t3.gamma_id = txs.gamma_id and txs.mod_type =3),\r\n" +
   "tuple3Combined as (SELECT * from newTuple3 UNION SELECT * from modifiedTuple3 UNION SELECT * FROM deletedTuple3),\r\n" +
   "newTuple41 as (SELECT t4.*, 15 as event_type from osee_tuple4 t4, currentTx txs  WHERE t4.gamma_id = txs.gamma_id and txs.mod_type =1),\r\n" +
   "newTuple42 as (SELECT t4.*, 15 as event_type from osee_tuple4 t4, currentTx txs  WHERE t4.gamma_id = txs.gamma_id and txs.mod_type =6),\r\n" +
   "newTuple4 as (SELECT * FROM newTuple41 UNION SELECT * FROM newTuple42 ),\r\n" +
   "modifiedTuple4 as (SELECT t4.*, 16 as event_type from osee_tuple4 t4, currentTx txs WHERE t4.gamma_id = txs.gamma_id and txs.mod_type =2),\r\n" +
   "deletedTuple4 as (SELECT t4.*, 17 as event_type from osee_tuple4 t4, currentTx txs WHERE t4.gamma_id = txs.gamma_id and txs.mod_type =3),\r\n" +
   "tuple4Combined as (SELECT * from newTuple4 UNION SELECT * from modifiedTuple4 UNION SELECT * FROM deletedTuple4),\r\n" +
   "newOldRel1 as (SELECT rel.*, 0 as event_type from osee_relation_link rel, currentTx txs WHERE rel.gamma_id=txs.gamma_id and txs.mod_type=1),\r\n"+
   "newOldRel2 as (SELECT rel.*, 0 as event_type from osee_relation_link rel, currentTx txs WHERE rel.gamma_id=txs.gamma_id and txs.mod_type=6),\r\n"+
   "newOldRel as (SELECT * from newOldRel1 UNION SELECT * from newOldRel2),\r\n"+
   "modifiedOldRel as (SELECT rel.*, 4 as event_type from osee_relation_link rel, currentTx txs WHERE rel.gamma_id=txs.gamma_id and txs.mod_type=2),\r\n"+
   "deletedOldRel1 as (SELECT rel.*, 5 as event_type from osee_relation_link rel, currentTx txs WHERE rel.gamma_id=txs.gamma_id and txs.mod_type=3),\r\n"+
   "deletedOldRel2 as (SELECT rel.*, 5 as event_type from osee_relation_link rel, currentTx txs WHERE rel.gamma_id=txs.gamma_id and txs.mod_type=5),\r\n"+
   "deletedOldRel as (SELECT * FROM deletedOldRel1 UNION SELECT * FROM deletedOldRel2 ),\r\n"+
   "oldRelsCombined as (SELECT * from newOldRel UNION SELECT * from modifiedOldRel UNION SELECT * from deletedOldRel ),\r\n"+
   "arts as (SELECT art_id as id , art_type_id as type_id , guid as extra1,0 as extra2, 0 as extra3, 0 as extra4,0 as extra5, name as extra6,event_type from artsCombined),\r\n"+
   "attr as (SELECT attr_id as id , attr_type_id as type_id ,value as extra1,art_id as extra2, 0 as extra3, 0 as extra4, 0 as extra5, previous_value as extra6, event_type from attrCombined),\r\n"+
   "rels as (SELECT 0 as id, rel_type as type_id, '' as extra1, a_art_id as extra2,b_art_id as extra3, rel_order as extra4, 0 as extra5, '' as extra6, event_type from completeNewRels ),\r\n"+
   "oldRels as (SELECT rel_link_id as id, rel_link_type_id as type_id, '' as extra1, a_art_id as extra2, b_art_id as extra3,0 as extra4, 0 as extra5, '' as extra6, event_type from oldRelsCombined),\r\n"+
   "tuple2s as (SELECT 0 as id, tuple_type as type_id, '' as extra1, e1 as extra2,e2 as extra3, 0 as extra4, 0 as extra5, '' as extra6, event_type from tuple2Combined ),\r\n"+
   "tuple3s as (SELECT 0 as id, tuple_type as type_id, '' as extra1, e1 as extra2, e2 as extra3, e3 as extra4, 0 as extra5, '' as extra6, event_type from tuple3Combined), \r\n"+
   "tuple4s as (SELECT 0 as id, tuple_type as type_id, '' as extra1, e1 as extra2, e2 as extra3, e3 as extra4, e4 as extra5, '' as extra6, event_type from tuple4Combined)\r\n"+
   "select * from (SELECT * from attr UNION SELECT * from arts UNION SELECT * FROM rels UNION SELECT * from tuple2s UNION SELECT * from tuple3s UNION SELECT * from tuple4s UNION SELECT * from oldRels) t1 ORDER BY event_type\r\n";
//@formatter:on
      TransactionBuilder tx = createTransaction(branch, "Undoing " + transaction.getIdString());
      List<ArtifactToken> createdArts = new LinkedList<>();
      Consumer<JdbcStatement> consumer = stmt -> {
         int event_type = stmt.getInt("event_type");
         switch (event_type) {
            case 0:
               // new rel2
               tx.unrelate(ArtifactId.valueOf(stmt.getLong("extra2")),
                  orcsApi.tokenService().getRelationType(stmt.getLong("type_id")),
                  ArtifactId.valueOf(stmt.getLong("extra3")));
               break;
            case 1:
               // new attr
               tx.deleteByAttributeId(ArtifactId.valueOf(stmt.getLong("extra2")),
                  AttributeId.valueOf(stmt.getLong("id")));
               break;
            case 2:
               //new art
               tx.deleteArtifact(ArtifactId.valueOf(stmt.getLong("id")));
               break;
            case 3:
               // deleted art
               createdArts.add(tx.createArtifact(ArtifactToken.valueOf(stmt.getLong("id"), stmt.getString("extra1"),
                  stmt.getString("extra6"), branch, orcsApi.tokenService().getArtifactType(stmt.getLong("type_id")))));
               break;
            case 4:
               // modified old rel
               // special case - can't handle due to tx builder deficiences
               break;
            case 5:
               // deleted rel2
               Long afterArtifact = stmt.getLong("extra4");
               if (afterArtifact != 0L) {
                  tx.relate(ArtifactId.valueOf(stmt.getLong("extra2")),
                     orcsApi.tokenService().getRelationType(stmt.getLong("type_id")),
                     ArtifactId.valueOf(stmt.getLong("extra3")), ArtifactId.SENTINEL,
                     String.valueOf(stmt.getLong("extra4")));
               } else {
                  tx.relate(ArtifactId.valueOf(stmt.getLong("extra2")),
                     orcsApi.tokenService().getRelationType(stmt.getLong("type_id")),
                     ArtifactId.valueOf(stmt.getLong("extra3")), ArtifactId.SENTINEL, "end");
               }
               break;
            case 6:
               // deleted attr
               AttributeTypeGeneric<?> attrType = orcsApi.tokenService().getAttributeType(stmt.getLong("type_id"));
               ArtifactId artId = ArtifactId.valueOf(stmt.getLong("extra2"));
               if (!attrType.equals(CoreAttributeTypes.Name)) {

                  tx.createAttribute(artId, attrType, stmt.getString("extra1"));
               }
               break;
            case 7:
               // modified attr
               tx.setAttributeById(ArtifactId.valueOf(stmt.getLong("extra2")), AttributeId.valueOf(stmt.getLong("id")),
                  stmt.getString("extra6"));
               break;
            case 8:
               // modified art
               // no-op?
               break;
            case 9:
               // added tuple2
               tx.deleteTuple2(orcsApi.tokenService().getTuple2Type(stmt.getLong("type_id")), stmt.getLong("extra2"),
                  stmt.getLong("extra3"));
               break;
            case 10:
               // modified tuple2
               // no-op
               break;
            case 11:
               // deleted tuple2
               tx.addTuple2(orcsApi.tokenService().getTuple2Type(stmt.getLong("type_id")), stmt.getLong("extra2"),
                  stmt.getLong("extra3"));
               break;
            case 12:
               // created tuple3
               tx.deleteTuple3(orcsApi.tokenService().getTuple3Type(stmt.getLong("type_id")), stmt.getLong("extra2"),
                  stmt.getLong("extra3"), stmt.getLong("extra4"));
               break;
            case 13:
               // modified tuple3
               // no-op
               break;
            case 14:
               // deleted tuple3
               tx.addTuple3(orcsApi.tokenService().getTuple3Type(stmt.getLong("type_id")), stmt.getLong("extra2"),
                  stmt.getLong("extra3"), stmt.getLong("extra4"));
               break;
            case 15:
               // created tuple4
               tx.deleteTuple4(orcsApi.tokenService().getTuple4Type(stmt.getLong("type_id")), stmt.getLong("extra2"),
                  stmt.getLong("extra3"), stmt.getLong("extra4"), stmt.getLong("extra5"));
               break;
            case 16:
               // modified tuple4
               // no-op
               break;
            case 17:
               // deleted tuple4
               tx.addTuple4(orcsApi.tokenService().getTuple4Type(stmt.getLong("type_id")), stmt.getLong("extra2"),
                  stmt.getLong("extra3"), stmt.getLong("extra4"), stmt.getLong("extra5"));
               break;
            default:
               break;

         }
      };
      orcsApi.getJdbcService().getClient().runQuery(consumer, query, transaction, branch, CoreAttributeTypes.Name,
         branch, 1, branch, 1);
      return tx.commit().isValid();
   }

}
