/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.transaction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeRow;
import org.eclipse.osee.framework.skynet.core.attribute.RelationRow;
import org.eclipse.osee.framework.skynet.core.internal.accessors.DatabaseBranchAccessor;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * Manages a cache of <code>TransactionId</code>.
 *
 * @author Jeff C. Phillips
 */
public final class TransactionManager {

   private static final String INSERT_INTO_TRANSACTION_DETAIL =
      "INSERT INTO osee_tx_details (transaction_id, osee_comment, time, author, branch_id, tx_type, build_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

   private static final String SELECT_TRANSACTIONS =
      "SELECT * FROM osee_tx_details WHERE branch_id = ? ORDER BY transaction_id DESC";

   private static final String SELECT_TRANSACTIONS_BY_IDS =
      "SELECT * FROM osee_tx_details WHERE transaction_id in (%s)";

   private static final String SELECT_COMMIT_TRANSACTIONS = "SELECT * FROM osee_tx_details WHERE commit_art_id = ?";

   private static final String UPDATE_TRANSACTION_COMMENTS =
      "UPDATE osee_tx_details SET osee_comment = ? WHERE transaction_id = ?";

   private static final String SELECT_TRANSACTION_COMMENTS = "SELECT * FROM osee_tx_details WHERE osee_comment LIKE ?";

   private static final String SELECT_BRANCH_TRANSACTION_BY_DATE =
      "SELECT * FROM osee_tx_details WHERE branch_id = ? AND time < ? ORDER BY time DESC";

   private static final String SELECT_HEAD_TRANSACTION =
      "select * from osee_tx_details where transaction_id = (select max(transaction_id) from osee_tx_details where branch_id = ?) and branch_id = ?";

   private static final String SELECT_PRIOR_TRANSACTION =
      "select * from osee_tx_details where transaction_id = (select max(transaction_id) from osee_tx_details where branch_id = ? and transaction_id < ?) and branch_id = ?";

   private static final String TX_GET_TRANSACTION_BY_ID = "SELECT * FROM osee_tx_details WHERE transaction_id = ?";

   private static final String TX_GET_TRANSACTION_FROM_ATTR_ID =
      "select transaction_id from osee_attribute attr, osee_txs txs where txs.branch_id = ? and attr.gamma_id = ? and attr.gamma_id = txs.gamma_id";

   private static final String SELECT_ATTRIBUTES_FROM_ART_IN_TRANS_ID =
      "select * from osee_attribute attr, osee_txs txs where txs.branch_id = ? and attr.ART_ID = ? and txs.TRANSACTION_ID = ? and attr.gamma_id = txs.GAMMA_ID";

   private static final String SELECT_RELATIONS_FROM_ART_IN_TRANS_ID =
      "select * from osee_relation_link rel, osee_txs txs where txs.branch_id = ? and (rel.a_art_id = ? or rel.b_art_id = ?) and txs.TRANSACTION_ID = ? and rel.gamma_id = txs.GAMMA_ID";

   private static final String SELECT_RELATIONS2_FROM_ART_IN_TRANS_ID =
      "select * from osee_relation rel, osee_txs txs where txs.branch_id = ? and (rel.a_art_id = ? or rel.b_art_id = ?) and txs.TRANSACTION_ID = ? and rel.gamma_id = txs.GAMMA_ID";

   private static final String SELECT_ART_TRANSACTION_IDS =
      "select max(transaction_id) as prevTx from osee_attribute atr, osee_txs txs where branch_id = ? and art_id = ? and atr.gamma_id = txs.gamma_id and transaction_id < ?";

   private static final TxMonitorImpl<BranchId> txMonitor = new TxMonitorImpl<>(new TxMonitorCache<>());
   /**
    * The commitArtifactIdMap and processedCommitArtifactId are protected from concurrent access via synchronizing all
    * methods of this class that use it
    */
   private static final HashCollectionSet<ArtifactId, TransactionRecord> commitArtifactIdMap =
      new HashCollectionSet<>(HashSet::new);
   private static final Set<ArtifactId> processedCommitArtifactId = new HashSet<>();

   public static SkynetTransaction createTransaction(BranchId branch, String comment) {
      SkynetTransaction tx = new SkynetTransaction(txMonitor, branch, comment);
      txMonitor.createTx(branch, tx);
      return tx;
   }

   public static List<TransactionRecord> getTransaction(String comment) {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      ArrayList<TransactionRecord> transactions = new ArrayList<>();
      jdbcClient.runQuery(stmt -> transactions.add(loadTransaction(stmt)), SELECT_TRANSACTION_COMMENTS, comment);
      return transactions;
   }

   public static void setTransactionComment(TransactionId transaction, String comment) {
      ConnectionHandler.runPreparedUpdate(UPDATE_TRANSACTION_COMMENTS, comment, transaction);
   }

   public static List<TransactionRecord> getTransactionsForBranch(BranchId branch) {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      ArrayList<TransactionRecord> transactions = new ArrayList<>();
      jdbcClient.runQueryWithMaxFetchSize(stmt -> transactions.add(loadTransaction(branch, stmt)), SELECT_TRANSACTIONS,
         branch);
      return transactions;
   }

   public static ArtifactId getCommitArtifact(TransactionId tx) {
      return getTransaction(tx).getCommitArtifact();
   }

   public synchronized static Collection<TransactionRecord> getCommittedArtifactTransactionIds(ArtifactId artifact) {
      if (!processedCommitArtifactId.contains(artifact)) {
         if (!commitArtifactIdMap.containsKey(artifact)) {
            ConnectionHandler.getJdbcClient().runQuery(stmt -> commitArtifactIdMap.put(artifact, loadTransaction(stmt)),
               SELECT_COMMIT_TRANSACTIONS, artifact);
            processedCommitArtifactId.add(artifact);
         }
      }
      return commitArtifactIdMap.safeGetValues(artifact);
   }

   /**
    * Allow commitArtifactIdMap cache to be cleared for a given associatedArtifact. This will force a refresh of the
    * cache the next time it's accessed. This is provided for remote event commits. All other updates to cache should be
    * performed through cacheCommittedArtifactTransaction.
    */
   public synchronized static void clearCommitArtifactCacheForAssociatedArtifact(ArtifactId associatedArtifact) {
      commitArtifactIdMap.removeValues(associatedArtifact);
      processedCommitArtifactId.remove(associatedArtifact);
   }

   public synchronized static void cacheCommittedArtifactTransaction(ArtifactId artifact,
      TransactionToken transactionId) {
      commitArtifactIdMap.put(artifact, getTransactionRecord(transactionId.getId()));
      processedCommitArtifactId.add(artifact);
   }

   /**
    * @return the largest (most recent) transaction on the given branch
    */
   public static TransactionToken getHeadTransaction(BranchId branch) {
      return getTransaction(branch, SELECT_HEAD_TRANSACTION, branch, branch);
   }

   public static TransactionToken getPriorTransaction(TransactionToken tx) {
      BranchId branch = tx.getBranch();
      return getTransaction(branch, SELECT_PRIOR_TRANSACTION, branch, tx.getId(), branch);
   }

   private static TransactionRecord getTransaction(BranchId branch, String sql, Object... data) {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      return jdbcClient.fetchOrException(
         () -> new TransactionDoesNotExist("No transactions where found in the database for branch: %d",
            branch.getId()),
         stmt -> loadTransaction(branch, stmt), sql, data);
   }

   private static TransactionRecord loadTransaction(JdbcStatement stmt) {
      return loadTransaction(BranchId.valueOf(stmt.getLong("branch_id")), stmt);
   }

   public static TransactionRecord loadTransaction(BranchId branch, JdbcStatement stmt) {
      Long transactionNumber = stmt.getLong("transaction_id");
      String comment = stmt.getString("osee_comment");
      Date timestamp = stmt.getTimestamp("time");
      ArtifactId commitArtId = ArtifactId.valueOf(stmt.getLong("commit_art_id"));
      Long buildId = stmt.getLong("build_id");
      TransactionDetailsType txType = TransactionDetailsType.valueOf(stmt.getInt("tx_type"));

      UserService userService = OsgiUtil.getService(DatabaseBranchAccessor.class, OseeClient.class).userService();
      UserToken author = userService.getUserIfLoaded(stmt.getLong("author"));

      return new TransactionRecord(transactionNumber, branch, comment, timestamp, author, commitArtId, txType, buildId);
   }

   public static synchronized void internalPersist(JdbcConnection connection, TransactionRecord transactionRecord) {
      ConnectionHandler.runPreparedUpdate(connection, INSERT_INTO_TRANSACTION_DETAIL, transactionRecord.getId(),
         transactionRecord.getComment(), transactionRecord.getTimeStamp(), transactionRecord.getAuthor(),
         transactionRecord.getBranch(), transactionRecord.getTxType().getId(), OseeCodeVersion.getVersionId());
   }

   public static TransactionToken getTransactionAtDate(BranchId branch, Date maxDateExclusive) {
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(maxDateExclusive, "max date exclusive");

      TransactionRecord txRecord = null;

      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_BRANCH_TRANSACTION_BY_DATE, branch, new Timestamp(maxDateExclusive.getTime()));
         if (chStmt.next()) {
            txRecord = loadTransaction(chStmt);
         }
      } finally {
         chStmt.close();
      }
      return txRecord;
   }

   public static TransactionRecord getTransaction(TransactionId tx) {
      if (tx instanceof TransactionRecord) {
         return (TransactionRecord) tx;
      }
      return getTransactionRecord(tx.getId());
   }

   public static TransactionToken getTransaction(long txId) {
      return getTransactionRecord(txId);
   }

   public static String getComment(long txId) {
      return getTransactionRecord(txId).getComment();
   }

   public static String getComment(TransactionId tx) {
      return getComment(tx.getId());
   }

   private static TransactionRecord getTransactionRecord(long txId) {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      return jdbcClient.fetchOrException(
         () -> new TransactionDoesNotExist("A transaction with id %d was not found.", txId),
         stmt -> loadTransaction(stmt), TX_GET_TRANSACTION_BY_ID, txId);
   }

   public static Collection<TransactionRecord> getTransactions(Set<Long> ids) {
      List<TransactionRecord> transactions = new LinkedList<>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         String query = String.format(SELECT_TRANSACTIONS_BY_IDS,
            org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", ids));
         chStmt.runPreparedQuery(query);
         while (chStmt.next()) {
            transactions.add(loadTransaction(chStmt));
         }
      } finally {
         chStmt.close();
      }
      return transactions;
   }

   public static TransactionId getTransaction(BranchId branch, Attribute<Object> attr) {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      return jdbcClient.fetchOrException(
         () -> new TransactionDoesNotExist("A transaction from attr gamma id %d was not found.", attr.getGammaId()),
         stmt -> TransactionId.valueOf(stmt.getLong("transaction_id")), TX_GET_TRANSACTION_FROM_ATTR_ID, branch.getId(),
         attr.getGammaId());
   }

   /**
    * This method will attempt to revert the changes made to the given artifact in the revertTransaction. It does not
    * handle ARTIFACT_DELETED or relations and only handles NEW and MODIFIED ModTypes. Those cases should be added as
    * needed.
    *
    * @param results - contains the changes that need to be made (if persist == false) or changes that were made (if
    * persist == true)
    * @param persist - if true, changes will be made to attributes and artifact and added to persistTransaction
    * @return true if changes were found
    */
   public static boolean revertArtifactFromTransaction(Artifact art, TransactionId revertTransaction,
      XResultData results, boolean persist, SkynetTransaction persistTransaction) {
      List<AttributeRow> attributesFromArtifactAndTransaction =
         getAttributesFromArtifactAndTransaction(art, revertTransaction);
      for (AttributeRow attr : attributesFromArtifactAndTransaction) {
         if (attr.getModType() == ModificationType.ARTIFACT_DELETED) {
            throw new UnsupportedOperationException(
               "Revert of Artifact Deleted is not supported (but could be added as needed)");
         }
      }

      List<RelationRow> relations = getRelationsFromArtifactAndTransaction(art, revertTransaction);
      if (!relations.isEmpty()) {
         throw new UnsupportedOperationException(
            "Revert of Relations Modified is not supported (but could be added as needed)");
      }

      TransactionId prevTransId = getPreviousTransactionId(art, revertTransaction);
      Artifact prevArt = ArtifactQuery.getHistoricalArtifactFromId(art,
         TransactionToken.valueOf(prevTransId, art.getBranch()), DeletionFlag.EXCLUDE_DELETED);

      boolean changed = false;
      for (AttributeRow attr : attributesFromArtifactAndTransaction) {
         AttributeTypeToken type = attr.getAttributeType();
         if (attr.getModType() == ModificationType.NEW) {
            changed = true;
            if (persist) {
               art.deleteAttribute(attr.getAttrId());
            }
            results.logf("Deleting created attribute type [%s]\n", type);
         } else if (attr.getModType() == ModificationType.MODIFIED) {
            if (prevArt.getArtifactType().getMax(type) == 1) {
               Object curValue = art.getSoleAttributeValue(type, null);
               Object prevValue = getPreviousValue(prevArt, attr.getAttrId());
               changed = true;
               if (persist && prevValue != null) {
                  art.setSoleAttributeValue(type, prevValue);
               }
               String currValueAsText = "";

               if (curValue != null) {
                  currValueAsText = curValue.toString();
                  currValueAsText = AHTML.textToHtml(currValueAsText);
               }
               if (prevValue != null) {
                  String prevValueAsText = prevValue.toString();
                  prevValueAsText = AHTML.textToHtml(prevValueAsText);
                  results.logf("Setting modified type [%s] from [%s] to [%s]\n", type, currValueAsText,
                     prevValueAsText);
               }
            } else {
               results.errorf("Max Occurrences > 1 not supported for attribute %s (but could be added as needed)\n",
                  attr);
            }
         } else {
            results.errorf("Mod Type %s not supported for attribute %s (but could be added as needed)\n",
               attr.getModType(), attr);
         }
      }
      if (persist && changed) {
         art.persist(persistTransaction);
      }
      return changed;
   }

   private static Object getPreviousValue(Artifact prevArt, AttributeId attrId) {
      for (Attribute<?> attr : prevArt.getAttributes()) {
         if (attrId.equals(attr)) {
            return attr.getValue();
         }
      }
      return null;
   }

   public static TransactionId getPreviousTransactionId(Artifact art, TransactionId trans) {
      return ConnectionHandler.getJdbcClient().fetch(TransactionId.SENTINEL, SELECT_ART_TRANSACTION_IDS,
         art.getBranch(), art, trans);
   }

   private static List<RelationRow> getRelationsFromArtifactAndTransaction(Artifact art, TransactionId trans) {
      List<RelationRow> relationChanges = new LinkedList<>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_RELATIONS_FROM_ART_IN_TRANS_ID, art.getBranch(), art, art, trans);
         while (chStmt.next()) {
            relationChanges.add(loadRelationChange(chStmt));
         }
      } finally {
         chStmt.close();
      }
      return relationChanges;

   }

   private static List<RelationRow> getRelations2FromArtifactAndTransaction(Artifact art, TransactionId trans) {
      List<RelationRow> relationChanges = new LinkedList<>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_RELATIONS2_FROM_ART_IN_TRANS_ID, art.getBranch(), art, art, trans);
         while (chStmt.next()) {
            relationChanges.add(loadRelation2Change(chStmt));
         }
      } finally {
         chStmt.close();
      }
      return relationChanges;

   }

   public static List<AttributeRow> getAttributesFromArtifactAndTransaction(Artifact art, TransactionId trans) {
      List<AttributeRow> attributeChanges = new LinkedList<>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_ATTRIBUTES_FROM_ART_IN_TRANS_ID, art.getBranch(), art, trans);
         while (chStmt.next()) {
            attributeChanges.add(loadAttributeChange(chStmt));
         }
      } finally {
         chStmt.close();
      }
      return attributeChanges;

   }

   private static RelationRow loadRelationChange(JdbcStatement chStmt) {
      OrcsTokenService tokenService = OsgiUtil.getService(TransactionManager.class, OrcsTokenService.class);
      RelationTypeToken relationType = tokenService.getRelationType(chStmt.getLong("rel_link_type_id"));
      BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
      GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
      ArtifactId aArtId = ArtifactId.valueOf(chStmt.getLong("a_art_id"));
      ArtifactId bArtId = ArtifactId.valueOf(chStmt.getLong("b_art_id"));
      RelationId relId = RelationId.valueOf(chStmt.getLong("rel_link_id"));
      String rationale = chStmt.getString("rationale");
      return new RelationRow(branch, relId, relationType, aArtId, bArtId, rationale, gammaId);
   }

   private static RelationRow loadRelation2Change(JdbcStatement chStmt) {
      OrcsTokenService tokenService = OsgiUtil.getService(TransactionManager.class, OrcsTokenService.class);
      RelationTypeToken relationType = tokenService.getRelationType(chStmt.getLong("rel_type"));
      BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
      GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
      ArtifactId aArtId = ArtifactId.valueOf(chStmt.getLong("a_art_id"));
      ArtifactId bArtId = ArtifactId.valueOf(chStmt.getLong("b_art_id"));
      ArtifactId relArtId = ArtifactId.valueOf(chStmt.getLong("rel_art_id"));
      int relOrder = chStmt.getInt("rel_order");

      return new RelationRow(branch, relationType, aArtId, bArtId, relArtId, relOrder, gammaId);
   }

   private static AttributeRow loadAttributeChange(JdbcStatement chStmt) {
      OrcsTokenService tokenService = OsgiUtil.getService(TransactionManager.class, OrcsTokenService.class);
      AttributeTypeToken attributeType = tokenService.getAttributeTypeOrCreate(chStmt.getLong("attr_type_id"));
      BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
      GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
      ArtifactId artId = ArtifactId.valueOf(chStmt.getLong("art_id"));
      ModificationType modType = ModificationType.valueOf(chStmt.getInt("mod_type"));
      AttributeId attrId = AttributeId.valueOf(chStmt.getLong("attr_id"));
      String value = chStmt.getString("value");
      return new AttributeRow(branch, gammaId, artId, modType, value, attrId, attributeType);
   }

   public static void persistInTransaction(String comment, final Collection<? extends Artifact> artifacts) {
      persistInTransaction(comment, artifacts.toArray(new Artifact[artifacts.size()]));
   }

   public static void persistInTransaction(String comment, Artifact... artifacts) {
      SkynetTransaction transaction = createTransaction(artifacts[0].getBranch(), comment);
      for (Artifact art : artifacts) {
         art.persist(transaction);
      }
      transaction.execute();
   }
}