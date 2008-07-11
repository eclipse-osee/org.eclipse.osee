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

package org.eclipse.osee.framework.skynet.core.revision;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_TYPE_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_TYPE_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TXD_COMMENT;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.INCOMING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.OUTGOING;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.DELETED;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.RsetProcessor;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.core.schema.Table;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactInTransactionSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.RelationInTransactionSearch;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChanged;
import org.eclipse.osee.framework.skynet.core.change.AttributeChanged;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.RelationChanged;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflictBuilder;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflictBuilder;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictBuilder;
import org.eclipse.osee.framework.skynet.core.event.LocalBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalBranchToArtifactCacheUpdateEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalCommitBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalDeletedBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteCommitBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteDeletedBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.BranchMergeException;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionDetailsType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;

/**
 * Manages artifact versions in Skynet
 * 
 * @author Jeff C. Phillips
 */
public class RevisionManager implements IEventReceiver {
   private static final String BRANCH_ATTRIBUTE_IS_CHANGES =
         "SELECT t8.art_type_id, t3.art_id, t3.attr_id, t3.gamma_id, t3.attr_type_id, t3.value as is_value, t1.mod_type FROM osee_define_txs t1, osee_define_tx_details t2, osee_define_attribute t3, osee_define_artifact t8 WHERE t2.branch_id = ? AND t2.transaction_id = t1.transaction_id AND (t1.tx_current = " + TxChange.DELETED.getValue() + " OR t1.tx_current = " + TxChange.CURRENT.getValue() + ") AND t2.tx_type = 0 AND t8.art_id = t3.art_id AND t3.gamma_id = t1.gamma_id";

   private static final String TRANSACTION_ATTRIBUTE_CHANGES =
         "SELECT t8.art_type_id, t3.art_id, t3.attr_id, t3.gamma_id, t3.attr_type_id, t3.value as is_value, t1.mod_type FROM osee_define_txs t1, osee_define_tx_details t2, osee_define_attribute t3, osee_define_artifact t8 WHERE t2.transaction_id = ? AND t2.transaction_id = t1.transaction_id AND t2.tx_type = 0 AND t8.art_id = t3.art_id AND t3.gamma_id = t1.gamma_id";

   private static final String BRANCH_REL_CHANGES =
         "SELECT tx1.mod_type, rl3.gamma_id, rl3.b_art_id, rl3.a_art_id, rl3.a_order, rl3.b_order, rl3.rationale, rl3.rel_link_id, rl3.rel_link_type_id from osee_define_txs tx1, osee_define_tx_details td2, osee_define_rel_link rl3 where (tx1.tx_current = " + TxChange.DELETED.getValue() + " OR tx1.tx_current = " + TxChange.CURRENT.getValue() + ") AND td2.tx_type = 0 AND td2.branch_id = ? AND tx1.transaction_id = td2.transaction_id AND tx1.gamma_id = rl3.gamma_id";

   private static final String TRANSACTION_REL_CHANGES =
         "SELECT tx1.mod_type, rl3.gamma_id, rl3.b_art_id, rl3.a_art_id, rl3.a_order, rl3.b_order, rl3.rationale, rl3.rel_link_id, rl3.rel_link_type_id from osee_define_txs tx1, osee_define_tx_details td2, osee_define_rel_link rl3 where td2.tx_type = 0 AND td2.transaction_id = ? AND tx1.transaction_id = td2.transaction_id AND tx1.gamma_id = rl3.gamma_id";

   private static final String BRANCH_ARTIFACT_CHANGES =
         "select af4.art_id, af4.art_type_id, av3.gamma_id, tx1.mod_type FROM osee_Define_txs tx1, osee_Define_tx_details td2, osee_Define_artifact_version av3, osee_Define_artifact af4 WHERE td2.branch_id = ? AND td2.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND td2.transaction_id = tx1.transaction_id AND tx1.gamma_id = av3.gamma_id AND (tx1.tx_current = " + TxChange.DELETED.getValue() + " OR tx1.mod_type = " + ModificationType.NEW.getValue() + ")  AND av3.art_id = af4.art_id";

   private static final String TRANSACTION_ARTIFACT_CHANGES =
         "select af4.art_id, af4.art_type_id, av3.gamma_id, tx1.mod_type FROM osee_Define_txs tx1, osee_Define_tx_details td2, osee_Define_artifact_version av3, osee_Define_artifact af4 WHERE td2.transaction_id = ? AND td2.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND td2.transaction_id = tx1.transaction_id AND tx1.gamma_id = av3.gamma_id AND (tx1.mod_type = " + ModificationType.DELETED.getValue() + " OR tx1.mod_type = " + ModificationType.NEW.getValue() + ")  AND av3.art_id = af4.art_id";

   private static final String SELECT_TRANSACTIONS =
         "SELECT " + TRANSACTION_DETAIL_TABLE.columns("transaction_id", "commit_art_id", TXD_COMMENT, "time", "author") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ?" + " ORDER BY transaction_id DESC";

   private static final String SELECT_COMMIT_ART_TRANSACTIONS =
         "SELECT transaction_id, commit_art_id from osee_define_tx_details where commit_art_id is not null";

   private static final String GET_CHANGED_ARTIFACTS =
         "SELECT arv2.gamma_id, txs1.mod_type FROM osee_define_artifact ar1, osee_define_artifact_version arv2, osee_define_txs txs1, osee_define_tx_details txd4 WHERE ar1.art_id = ? AND ar1.art_id = arv2.art_id AND arv2.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd4.transaction_id AND txd4.transaction_id > ? AND txd4.transaction_id <= ? AND txd4.branch_id = ?";

   private static final String GET_DELETED_ARTIFACTS =
         Table.generateTableAliasedSql("SELECT txd10.branch_id, att8.value AS name, art5.art_id, ary6.name AS type_name, arv7.modification_id, arv7.gamma_id, (SELECT MAX(txd3.transaction_id) FROM OSEE_DEFINE_ARTIFACT_VERSION arv1,OSEE_DEFINE_TXS txs2,OSEE_DEFINE_TX_DETAILS txd3 WHERE arv1.art_id=arv7.art_id AND arv1.modification_id<> 3 AND arv1.gamma_id=txs2.gamma_id AND txs2.transaction_id=txd3.transaction_id AND txd3.branch_id=txd10.branch_id AND txd3.transaction_id< txd10.transaction_id) PUT_TABLE_ALIAS_HERE last_good_transaction, txs4.transaction_id as deleted_transaction FROM osee_define_txs txs4,OSEE_DEFINE_ARTIFACT art5, OSEE_DEFINE_ARTIFACT_TYPE ary6, OSEE_DEFINE_ARTIFACT_VERSION arv7, OSEE_DEFINE_ATTRIBUTE att8,OSEE_DEFINE_TXS txs9,OSEE_DEFINE_TX_DETAILS txd10, OSEE_DEFINE_TX_DETAILS txd11, (SELECT MAX(att11.gamma_id) PUT_TABLE_ALIAS_HERE gamma_id FROM OSEE_DEFINE_ATTRIBUTE att11, OSEE_DEFINE_ATTRIBUTE_TYPE aty12 WHERE att11.attr_type_id=aty12.attr_type_id AND aty12.name=? GROUP BY att11.art_id) PUT_TABLE_ALIAS_HERE ATTR_GAMMA WHERE txd11.branch_id = txd10.branch_id AND txd11.transaction_id = txs4.transaction_id AND txs4.gamma_id = arv7.gamma_id AND txd10.branch_id=? AND txd10.transaction_id=txs9.transaction_id AND txs9.transaction_id > ?  AND txs9.transaction_id <= ? AND txs9.gamma_id=arv7.gamma_id AND arv7.art_id=art5.art_id AND arv7.modification_id=? AND art5.art_type_id=ary6.art_type_id AND art5.art_id=att8.art_id AND att8.gamma_id= ATTR_GAMMA.gamma_id");

   private static final Table TX_DATA = new Table("tx_data");
   private static final String SELECT_TRANSACTIONS_FOR_ARTIFACT =
         "SELECT DISTINCT " + TX_DATA.columns("transaction_id", TXD_COMMENT, "time", "author", "commit_art_id") + " FROM " + "(" + " SELECT " + TRANSACTION_DETAIL_TABLE.columns(
               "transaction_id", "commit_art_id", TXD_COMMENT, "time", "author") + " FROM " + Collections.toString(",",
               TRANSACTION_DETAIL_TABLE, TRANSACTIONS_TABLE, ARTIFACT_VERSION_TABLE) + " WHERE " + TRANSACTIONS_TABLE.join(
               TRANSACTION_DETAIL_TABLE, "transaction_id") + " AND " + ARTIFACT_VERSION_TABLE.join(TRANSACTIONS_TABLE,
               "gamma_id") + " AND " + ARTIFACT_VERSION_TABLE.column("art_id") + "=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " UNION ALL" + " SELECT " + TRANSACTION_DETAIL_TABLE.columns(
               "transaction_id", "commit_art_id", TXD_COMMENT, "time", "author") + " FROM " + Collections.toString(",",
               TRANSACTION_DETAIL_TABLE, TRANSACTIONS_TABLE, RELATION_LINK_VERSION_TABLE) + " WHERE " + TRANSACTIONS_TABLE.join(
               TRANSACTION_DETAIL_TABLE, "transaction_id") + " AND " + RELATION_LINK_VERSION_TABLE.join(
               TRANSACTIONS_TABLE, "gamma_id") + " AND " + RELATION_LINK_VERSION_TABLE.column("a_art_id") + "=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " UNION ALL" + " SELECT " + TRANSACTION_DETAIL_TABLE.columns(
               "transaction_id", "commit_art_id", TXD_COMMENT, "time", "author") + " FROM " + Collections.toString(",",
               TRANSACTION_DETAIL_TABLE, TRANSACTIONS_TABLE, RELATION_LINK_VERSION_TABLE) + " WHERE " + TRANSACTIONS_TABLE.join(
               TRANSACTION_DETAIL_TABLE, "transaction_id") + " AND " + RELATION_LINK_VERSION_TABLE.join(
               TRANSACTIONS_TABLE, "gamma_id") + " AND " + RELATION_LINK_VERSION_TABLE.column("b_art_id") + "=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + ")" + TX_DATA + " WHERE " + TX_DATA.column("transaction_id") + "<?" + " ORDER BY " + TX_DATA.column("transaction_id") + " DESC";

   private static final String ARTIFACT_CONFLICTS =
         "SELECT art1.art_type_id, arv1.art_id, txs1.mod_type AS source_mod_type, txs1.gamma_id AS source_gamma, txs2.mod_type AS dest_mod_type, txs2.gamma_id AS dest_gamma, arv3.gamma_id AS begin_gamma  FROM osee_define_txs txs1, osee_define_txs txs2, osee_define_txs txs3, osee_define_tx_details txd1, osee_define_tx_details txd2, osee_define_artifact_version arv1, osee_define_artifact_version arv2, osee_define_artifact_version arv3 , osee_define_artifact art1 WHERE txd1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND txs1.gamma_id = arv1.gamma_id and arv1.art_id = art1.art_id AND arv1.art_id = arv2.art_id AND arv2.gamma_id = txs2.gamma_id AND txs2.tx_current in (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND txs2.transaction_id = txd2.transaction_id AND txs2.transaction_id > ? AND txd2.branch_id = ? AND txs3.transaction_id = ? AND txs3.gamma_id = arv3.gamma_id and arv3.art_id = arv1.art_id";

   private static final String ATTRIBUTE_CONFLICTS_NEW =
         "SELECT atr1.art_id, txs1.mod_type, atr1.attr_type_id, atr1.attr_id, atr1.gamma_id AS source_gamma, atr1.value AS source_value, atr2.gamma_id AS dest_gamma, atr2.value as dest_value, txs2.mod_type AS dest_mod_type FROM osee_define_txs txs1, osee_define_txs txs2, osee_define_tx_details txd1, osee_define_tx_details txd2, osee_define_attribute atr1, osee_define_attribute atr2 WHERE txd1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.mod_type =  " + ModificationType.NEW.getValue() + " AND txs1.tx_current in (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND txs1.gamma_id = atr1.gamma_id AND atr1.attr_id = atr2.attr_id AND atr2.gamma_id = txs2.gamma_id AND txs2.tx_current in (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND txs2.transaction_id = txd2.transaction_id AND txs2.transaction_id > ? AND txd2.branch_id = ?";

   private static final String ATTRIBUTE_CONFLICTS =
         "SELECT atr1.art_id, txs1.mod_type, atr1.attr_type_id, atr1.attr_id, atr1.gamma_id AS source_gamma, atr1.value AS source_value, atr2.gamma_id AS dest_gamma, atr2.value as dest_value, txs2.mod_type AS dest_mod_type, atr3.gamma_id AS begin_gamma  FROM osee_define_txs txs1, osee_define_txs txs2, osee_define_txs txs3, osee_define_tx_details txd1, osee_define_tx_details txd2, osee_define_attribute atr1, osee_define_attribute atr2, osee_define_attribute atr3 WHERE txd1.tx_type = " + TransactionDetailsType.NonBaselined.getId() + " AND txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND txs1.gamma_id = atr1.gamma_id AND atr1.attr_id = atr2.attr_id AND atr2.gamma_id = txs2.gamma_id AND txs2.tx_current in (" + TxChange.CURRENT.getValue() + " , " + TxChange.DELETED.getValue() + ") AND txs2.transaction_id = txd2.transaction_id AND txs2.transaction_id > ? AND txd2.branch_id = ? AND txs3.transaction_id = ?  AND txs3.gamma_id = atr3.gamma_id and atr3.attr_id = atr1.attr_id";

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(RevisionManager.class);
   private static final Pair<String, ArtifactType> UNKNOWN_DATA = new Pair<String, ArtifactType>(null, null);

   private Map<Integer, Set<Integer>> commitArtifactIdToTransactionId;

   private static final RevisionManager instance = new RevisionManager();

   private Map<Integer, String> bemsToName;

   private RevisionManager() {
      super();
      this.bemsToName = new HashMap<Integer, String>();
   }

   public static RevisionManager getInstance() {
      return instance;
   }

   public List<TransactionData> getTransactionsPerBranch(Branch branch) throws OseeCoreException, SQLException {
      List<TransactionData> transactionDetails = new LinkedList<TransactionData>();

      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_TRANSACTIONS, SQL3DataType.INTEGER, branch.getBranchId());

         ResultSet rSet = chStmt.getRset();
         while (chStmt.next()) {
            transactionDetails.add(new TransactionData(rSet.getString(TXD_COMMENT), rSet.getTimestamp("time"),
                  rSet.getInt("author"), rSet.getInt("transaction_id"), -1, branch, rSet.getInt("commit_art_id")));
         }
      } catch (SQLException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      } finally {
         DbUtil.close(chStmt);
      }
      return transactionDetails;
   }

   public Set<Integer> getTransactionDataPerCommitArtifact(Artifact commitArtifact) throws SQLException {
      checkCommitArtifactToTransactionCache();

      if (commitArtifact != null && commitArtifactIdToTransactionId.containsKey(commitArtifact.getArtId())) {
         return commitArtifactIdToTransactionId.get(commitArtifact.getArtId());
      }

      return new HashSet<Integer>();
   }

   public void cacheTransactionDataPerCommitArtifact(Artifact commitArtifact, int transactionData) throws SQLException {
      cacheTransactionDataPerCommitArtifact(commitArtifact.getArtId(), transactionData);
   }

   private void checkCommitArtifactToTransactionCache() throws SQLException {
      if (commitArtifactIdToTransactionId == null) {
         commitArtifactIdToTransactionId = new HashMap<Integer, Set<Integer>>();

         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt = ConnectionHandler.runPreparedQuery(SELECT_COMMIT_ART_TRANSACTIONS);
            ResultSet rSet = chStmt.getRset();
            while (chStmt.next()) {
               int commitArtId = rSet.getInt("commit_art_id");
               cacheTransactionDataPerCommitArtifact(commitArtId, rSet.getInt("transaction_id"));
            }
         } finally {
            DbUtil.close(chStmt);
         }
         SkynetEventManager.getInstance().register(LocalBranchEvent.class, this);
         SkynetEventManager.getInstance().register(RemoteBranchEvent.class, this);
      }
   }

   public void cacheTransactionDataPerCommitArtifact(int commitArtifactId, int transactionId) throws SQLException {
      checkCommitArtifactToTransactionCache();

      Set<Integer> transactionIds = commitArtifactIdToTransactionId.get(commitArtifactId);

      if (transactionIds == null) {
         transactionIds = new HashSet<Integer>();
      }

      transactionIds.add(transactionId);
      commitArtifactIdToTransactionId.put(commitArtifactId, transactionIds);
   }

   /**
    * Returns the transactions associated with an artifact for the branch the artifact is on
    * 
    * @param artifact
    * @return - Collection<TransactionData>
    */
   public Collection<TransactionData> getTransactionsPerArtifact(Artifact artifact) throws OseeCoreException, SQLException {
      return getTransactionsPerArtifact(artifact, false);
   }

   /**
    * Returns the transactions associated with an artifact
    * 
    * @param artifact
    * @param includeAncestry - indicate whether or not history from ancestor branches should be included
    * @return - Collection<TransactionData>
    */
   public Collection<TransactionData> getTransactionsPerArtifact(Artifact artifact, boolean includeAncestry) throws OseeCoreException, SQLException {
      List<TransactionData> transactionDetails = new LinkedList<TransactionData>();

      ConnectionHandlerStatement chStmt = null;

      try {
         final Integer artId = artifact.getArtId();
         Branch cursor = artifact.getBranch();
         Integer limit = Integer.MAX_VALUE;

         while (cursor != null) {
            chStmt =
                  ConnectionHandler.runPreparedQuery(SELECT_TRANSACTIONS_FOR_ARTIFACT, SQL3DataType.INTEGER, artId,
                        SQL3DataType.INTEGER, cursor.getBranchId(), SQL3DataType.INTEGER, artId, SQL3DataType.INTEGER,
                        cursor.getBranchId(), SQL3DataType.INTEGER, artId, SQL3DataType.INTEGER, cursor.getBranchId(),
                        SQL3DataType.INTEGER, limit);

            ResultSet rSet = chStmt.getRset();
            while (chStmt.next()) {
               transactionDetails.add(new TransactionData(rSet.getString(TXD_COMMENT), rSet.getTimestamp("time"),
                     rSet.getInt("author"), rSet.getInt("transaction_id"), artId, cursor, rSet.getInt("commit_art_id")));
            }

            if (includeAncestry) {
               cursor = cursor.getParentBranch();
               limit = transactionDetails.get(transactionDetails.size() - 1).getTransactionNumber();
            } else {
               cursor = null;
            }
         }
      } catch (SQLException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      } finally {
         DbUtil.close(chStmt);
      }

      return transactionDetails;
   }

   /**
    * Returns transaction details by creating a union between the attribute table and the rel link table joined by the
    * TransactionData id
    * 
    * @param tData
    * @return - Collection<RevisionChange>
    * @throws SQLException
    * @throws BranchDoesNotExist
    * @throws ArtifactDoesNotExist
    */
   public Collection<RevisionChange> getTransactionChanges(TransactionData tData) throws SQLException, BranchDoesNotExist, ArtifactDoesNotExist {
      IArtifactNameDescriptorResolver resolver = new ArtifactNameDescriptorResolver(tData.getBranch());

      return getTransactionChanges(OUTGOING, tData.getTransactionId(), tData.getTransactionId(),
            tData.getAssociatedArtId(), resolver);
   }

   /**
    * Returns transaction details by creating a union between the attribute table and the rel link table joined by the
    * TransactionData id
    * 
    * @return - Collection<RevisionChange>
    * @throws SQLException
    */
   public Collection<RevisionChange> getTransactionChanges(ArtifactChange artChange, IArtifactNameDescriptorResolver artifactNameDescriptorCache) throws OseeCoreException, SQLException {
      Collection<RevisionChange> changes =
            getTransactionChanges(OUTGOING, artChange.getFromTransactionId(), artChange.getToTransactionId(),
                  artChange.getArtifact().getArtId(), artifactNameDescriptorCache);

      if (artChange.hasConflictingModArtifact()) {
         Collection<RevisionChange> incomingChanges =
               getTransactionChanges(INCOMING, artChange.getBaseParentTransactionId(),
                     artChange.getHeadParentTransactionId(), artChange.getArtifact().getArtId(),
                     artifactNameDescriptorCache);

         changes.addAll(incomingChanges);
      }

      return changes;
   }

   /**
    * @param fromTransactionNumber
    * @param toTransactionNumber
    * @param artId
    * @param artifactNameDescriptorResolver
    * @return All revision changes including artifact, attribute and relationLink.
    * @throws SQLException
    * @throws BranchDoesNotExist
    * @throws ArtifactDoesNotExist
    */
   public Collection<RevisionChange> getAllTransactionChanges(ChangeType changeType, int fromTransactionNumber, int toTransactionNumber, int artId, IArtifactNameDescriptorResolver artifactNameDescriptorResolver) throws SQLException, BranchDoesNotExist, ArtifactDoesNotExist {

      TransactionId fromTransactionId =
            TransactionIdManager.getInstance().getPossiblyEditableTransactionIfFromCache(fromTransactionNumber);
      TransactionId toTransactionId =
            TransactionIdManager.getInstance().getPossiblyEditableTransactionIfFromCache(toTransactionNumber);

      Collection<RevisionChange> changes =
            getTransactionChanges(changeType, fromTransactionId, toTransactionId, artId, artifactNameDescriptorResolver);
      changes.addAll(getArtifactChanges(fromTransactionId, toTransactionId, artId));

      return changes;
   }

   public Collection<RevisionChange> getTransactionChanges(ChangeType changeType, TransactionId fromTransactionId, TransactionId toTransactionId, int artId, IArtifactNameDescriptorResolver artifactNameDescriptorResolver) throws SQLException, ArtifactDoesNotExist {
      Collection<AttributeChange> attributeChanges =
            getAttributeChanges(changeType, fromTransactionId.getTransactionNumber(),
                  toTransactionId.getTransactionNumber(), artId);
      Collection<RelationLinkChange> linkChanges =
            getRelationLinkChanges(changeType, fromTransactionId.getTransactionNumber(),
                  toTransactionId.getTransactionNumber(), artId, artifactNameDescriptorResolver);
      Collection<ArtifactChange> artifactChanges =
            getArtifactChanges(changeType, fromTransactionId, toTransactionId, artId);

      Collection<RevisionChange> changes = new ArrayList<RevisionChange>(attributeChanges.size() + linkChanges.size());
      changes.addAll(attributeChanges);
      changes.addAll(linkChanges);
      changes.addAll(artifactChanges);

      return changes;
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return
    * @throws SQLException
    * @throws OseeCoreException
    */
   public Collection<Change> getChangesPerTransaction(int transactionNumber) throws SQLException, OseeCoreException {
      return getChangesPerBranch(null, transactionNumber);
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return
    * @throws SQLException
    * @throws OseeCoreException
    */
   public Collection<Change> getChangesPerBranch(Branch sourceBranch) throws SQLException, OseeCoreException {
      return getChangesPerBranch(sourceBranch, -1);
   }

   /**
    * Acquires artifact, relation and attribute changes from a source branch since its creation.
    * 
    * @param sourceBranch
    * @param baselineTransactionId
    * @return
    * @throws SQLException
    * @throws OseeCoreException
    */
   private Collection<Change> getChangesPerBranch(Branch sourceBranch, int transactionNumber) throws SQLException, OseeCoreException {
      ArrayList<Change> changes = new ArrayList<Change>();
      Set<Integer> artIds = new HashSet<Integer>();
      Set<Integer> newAndDeletedArtifactIds = new HashSet<Integer>();
      boolean hasBranch = sourceBranch != null;

      loadNewOrDeletedArtifactChanges(sourceBranch, transactionNumber, artIds, changes, newAndDeletedArtifactIds);
      loadAttributeChanges(sourceBranch, transactionNumber, artIds, changes, newAndDeletedArtifactIds);
      loadRelationChanges(sourceBranch, transactionNumber, artIds, changes, newAndDeletedArtifactIds);

      Branch branch =
            hasBranch ? sourceBranch : BranchPersistenceManager.getInstance().getBranchForTransactionNumber(
                  transactionNumber);

      if (!hasBranch) {
         for (Change change : changes) {
            change.setBranch(branch);
         }
      }

      if (!artIds.isEmpty()) {
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

         List<Object[]> datas = new LinkedList<Object[]>();
         for (int artId : artIds) {
            datas.add(new Object[] {SQL3DataType.INTEGER, queryId, SQL3DataType.TIMESTAMP, insertTime,
                  SQL3DataType.INTEGER, artId, SQL3DataType.INTEGER, branch.getBranchId()});
         }
         ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, datas, false);
      }

      return changes;
   }

   /**
    * @param sourceBranch
    * @param changes
    * @throws SQLException
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    */
   private void loadNewOrDeletedArtifactChanges(Branch sourceBranch, int transactionNumber, Set<Integer> artIds, ArrayList<Change> changes, Set<Integer> newAndDeletedArtifactIds) throws SQLException, BranchDoesNotExist, TransactionDoesNotExist {
      ConnectionHandlerStatement connectionHandlerStatement = null;
      Map<Integer, ArtifactChanged> artifactChanges = new HashMap<Integer, ArtifactChanged>();
      boolean hasBranch = sourceBranch != null;
      TransactionId fromTransactionId;
      TransactionId toTransactionId;

      try {
         //Changes per a branch
         if (hasBranch) {
            Pair<TransactionId, TransactionId> branchStartEndTransaction =
                  TransactionIdManager.getInstance().getStartEndPoint(sourceBranch);

            fromTransactionId = branchStartEndTransaction.getKey();
            toTransactionId = branchStartEndTransaction.getValue();

            connectionHandlerStatement =
                  ConnectionHandler.runPreparedQuery(BRANCH_ARTIFACT_CHANGES, SQL3DataType.INTEGER,
                        sourceBranch.getBranchId());
         }
         //Changes per a transaction
         else {
            toTransactionId = TransactionIdManager.getInstance().getPossiblyEditableTransactionId(transactionNumber);
            fromTransactionId = TransactionIdManager.getInstance().getPriorTransaction(toTransactionId);

            connectionHandlerStatement =
                  ConnectionHandler.runPreparedQuery(TRANSACTION_ARTIFACT_CHANGES, SQL3DataType.INTEGER,
                        transactionNumber);
         }
         ResultSet resultSet = connectionHandlerStatement.getRset();

         while (resultSet.next()) {
            int artId = resultSet.getInt("art_id");

            ArtifactChanged artifactChanged =
                  new ArtifactChanged(sourceBranch, resultSet.getInt("art_type_id"), resultSet.getInt("gamma_id"),
                        artId, toTransactionId, fromTransactionId,
                        ModificationType.getMod(resultSet.getInt("mod_type")), ChangeType.OUTGOING);

            //We do not want to display artifacts that were new and then deleted
            //The only was this could happen is if the artifact was in here twice
            //since the sql only returns new or deleted artifacts
            if (!artifactChanges.containsKey(artId)) {
               artIds.add(artId);
               changes.add(artifactChanged);
               artifactChanges.put(artId, artifactChanged);
            } else {
               changes.remove(artifactChanges.get(artId));
               newAndDeletedArtifactIds.add(artId);
            }
         }
      } finally {
         DbUtil.close(connectionHandlerStatement);
      }
   }

   /**
    * @param sourceBranch
    * @param changes
    * @throws SQLException
    * @throws OseeCoreException
    */
   private void loadRelationChanges(Branch sourceBranch, int transactionNumber, Set<Integer> artIds, ArrayList<Change> changes, Set<Integer> newAndDeletedArtifactIds) throws SQLException, OseeCoreException {
      ConnectionHandlerStatement connectionHandlerStatement = null;
      try {
         //Changes per a branch
         if (sourceBranch != null) {
            connectionHandlerStatement =
                  ConnectionHandler.runPreparedQuery(BRANCH_REL_CHANGES, SQL3DataType.INTEGER,
                        sourceBranch.getBranchId());
         }//Changes per a transaction
         else {
            connectionHandlerStatement =
                  ConnectionHandler.runPreparedQuery(TRANSACTION_REL_CHANGES, SQL3DataType.INTEGER, transactionNumber);
         }
         ResultSet resultSet = connectionHandlerStatement.getRset();

         while (resultSet.next()) {
            int aArtId = resultSet.getInt("a_art_id");
            int bArtId = resultSet.getInt("b_art_id");
            int relLinkId = resultSet.getInt("rel_link_id");

            if (!newAndDeletedArtifactIds.contains(aArtId) && !newAndDeletedArtifactIds.contains(bArtId)) {
               artIds.add(aArtId);
               artIds.add(bArtId);

               changes.add(new RelationChanged(sourceBranch, -1, resultSet.getInt("gamma_id"), aArtId, null, null,
                     ModificationType.getMod(resultSet.getInt("mod_type")), ChangeType.OUTGOING, bArtId, relLinkId,
                     resultSet.getString("rationale"), resultSet.getInt("a_order"), resultSet.getInt("b_order"),
                     RelationTypeManager.getType(resultSet.getInt("rel_link_type_id"))));
            }
         }
      } finally {
         DbUtil.close(connectionHandlerStatement);
      }
   }

   /**
    * @param sourceBranch
    * @param changes
    * @throws SQLException
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
 * @throws OseeDataStoreException 
    */
   private void loadAttributeChanges(Branch sourceBranch, int transactionNumber, Set<Integer> artIds, ArrayList<Change> changes, Set<Integer> newAndDeletedArtifactIds) throws SQLException, BranchDoesNotExist, TransactionDoesNotExist, OseeDataStoreException {
      Map<Integer, Change> mightNeedWasValue = new HashMap<Integer, Change>();
      Map<Integer, ModificationType> artModTypes = new HashMap<Integer, ModificationType>();
      Set<Integer> modifiedArtifacts = new HashSet<Integer>();
      ConnectionHandlerStatement connectionHandlerStatement = null;
      ModificationType artModType;
      boolean hasBranch = sourceBranch != null;
      TransactionId fromTransactionId;
      TransactionId toTransactionId;
      int queryId;

      for (Change change : changes) {
         artModTypes.put(change.getArtId(), change.getModificationType());
      }

      try {
         //Changes per a branch
         if (hasBranch) {
            connectionHandlerStatement =
                  ConnectionHandler.runPreparedQuery(BRANCH_ATTRIBUTE_IS_CHANGES, SQL3DataType.INTEGER,
                        sourceBranch.getBranchId());

            Pair<TransactionId, TransactionId> branchStartEndTransaction =
                  TransactionIdManager.getInstance().getStartEndPoint(sourceBranch);

            fromTransactionId = branchStartEndTransaction.getKey();
            toTransactionId = branchStartEndTransaction.getValue();
         }//Changes per transaction number
         else {
            connectionHandlerStatement =
                  ConnectionHandler.runPreparedQuery(TRANSACTION_ATTRIBUTE_CHANGES, SQL3DataType.INTEGER,
                        transactionNumber);

            toTransactionId = TransactionIdManager.getInstance().getPossiblyEditableTransactionId(transactionNumber);
            fromTransactionId = TransactionIdManager.getInstance().getPriorTransaction(toTransactionId);
         }
         ResultSet resultSet = connectionHandlerStatement.getRset();
         AttributeChanged attributeChanged;

         while (resultSet.next()) {
            int attrId = resultSet.getInt(3);
            int artId = resultSet.getInt(2);
            int sourceGamma = resultSet.getInt(4);
            int attrTypeId = resultSet.getInt(5);
            int artTypeId = resultSet.getInt(1);
            int modType = resultSet.getInt(7);
            String isValue = resultSet.getString(6);

            if (artModTypes.containsKey(artId)) {
               artModType = artModTypes.get(artId);
            } else {
               artModType = ModificationType.CHANGE;
            }

            if (!newAndDeletedArtifactIds.contains(artId)) {
               // Want to add an artifact changed item once if any attribute was modified && artifact was not
               // NEW or DELETED
               if (artModType == ModificationType.CHANGE && !modifiedArtifacts.contains(artId)) {
                  ArtifactChanged artifactChanged =
                        new ArtifactChanged(sourceBranch, artTypeId, sourceGamma, artId, toTransactionId,
                              fromTransactionId, ModificationType.CHANGE, ChangeType.OUTGOING);

                  changes.add(artifactChanged);
                  modifiedArtifacts.add(artId);
               }
               attributeChanged =
                     new AttributeChanged(sourceBranch, artTypeId, sourceGamma, artId, toTransactionId,
                           fromTransactionId, hasBranch ? ModificationType.NEW : ModificationType.getMod(modType),
                           ChangeType.OUTGOING, isValue, "", attrId, attrTypeId, artModType);

               changes.add(attributeChanged);
               mightNeedWasValue.put(attrId, attributeChanged);
               artIds.add(artId);
            }
         }

         //Load was values for branch change reports only
         if (hasBranch && !artIds.isEmpty()) {
            queryId = ArtifactLoader.getNewQueryId();
        	 try{
	            Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
	            List<Object[]> datas = new LinkedList<Object[]>();
	
	            // insert into the artifact_join_table
	            for (int artId : artIds) {
	               datas.add(new Object[] {SQL3DataType.INTEGER, queryId, SQL3DataType.TIMESTAMP, insertTime,
	                     SQL3DataType.INTEGER, artId, SQL3DataType.INTEGER, sourceBranch.getBranchId()});
	            }
	            ArtifactLoader.selectArtifacts(datas);
	
	            String BRANCH_ATTRIBUTE_WAS_CHANGE =
	                  "SELECT t3.attr_id, t3.value as was_value, t1.mod_type FROM osee_define_txs t1, osee_define_tx_details t2, osee_define_attribute t3, osee_define_artifact t8, osee_join_artifact t9 WHERE t2.branch_id = ? AND t2.transaction_id = t1.transaction_id AND t2.tx_type = 1 AND t8.art_id = t3.art_id AND t3.gamma_id = t1.gamma_id AND t3.art_id = t9.art_id AND t2.branch_id = t9.branch_id AND t9.query_id = ?";
	
	            connectionHandlerStatement =
	                  ConnectionHandler.runPreparedQuery(BRANCH_ATTRIBUTE_WAS_CHANGE, SQL3DataType.INTEGER,
	                        sourceBranch.getBranchId(), SQL3DataType.INTEGER, queryId);
	            resultSet = connectionHandlerStatement.getRset();
	
	            while (resultSet.next()) {
	               int attrId = resultSet.getInt("attr_id");
	               String wasValue = resultSet.getString("was_value");
	
	               if (mightNeedWasValue.containsKey(attrId) && mightNeedWasValue.get(attrId) instanceof AttributeChanged) {
	                  AttributeChanged changed = (AttributeChanged) mightNeedWasValue.get(attrId);
	                  changed.setModType(ModificationType.CHANGE);
	                  changed.setWasValue(wasValue);
	               }
	            }
        	 }
        	 finally{
        		 ArtifactLoader.clearQuery(queryId);
        	 }
         }
      } finally {
         DbUtil.close(connectionHandlerStatement);
      }
   }

   public List<Conflict> getConflictsPerBranch(Branch sourceBranch, Branch destinationBranch, TransactionId baselineTransaction) throws SQLException, OseeCoreException {
      ArrayList<ConflictBuilder> conflictBuilders = new ArrayList<ConflictBuilder>();
      ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
      Set<Integer> artIdSet = new HashSet<Integer>();
      Set<Integer> artIdSetDontShow = new HashSet<Integer>();
      Set<Integer> artIdSetDontAdd = new HashSet<Integer>();
      if ((sourceBranch == null) || (destinationBranch == null)) {
         throw new IllegalArgumentException(
               "Source Barnch = " + sourceBranch + " Destination Branch = " + destinationBranch);
      }

      loadArtifactVersionConflicts(sourceBranch, destinationBranch, baselineTransaction, conflictBuilders, artIdSet,
            artIdSetDontShow, artIdSetDontAdd);
      loadAttributeConflicts(sourceBranch, destinationBranch, baselineTransaction, conflictBuilders, artIdSet);

      //Remove Art IDs for artifacts that should not be added to the branch because they were deleted etc. 
      for (Integer integer : artIdSetDontAdd) {
         artIdSet.remove(integer);
      }
      if (artIdSet.isEmpty()) return conflicts;

      Branch mergeBranch =
            BranchPersistenceManager.getInstance().getOrCreateMergeBranch(sourceBranch, destinationBranch,
                  new ArrayList<Integer>(artIdSet));

      if (mergeBranch == null) throw new BranchMergeException("Could not create the Merge Branch.");

      preloadConflictArtifacts(sourceBranch, destinationBranch, mergeBranch, artIdSet);

      //Don't create the conflicts for attributes on an artifact that is deleted etc.
      for (ConflictBuilder conflictBuilder : conflictBuilders) {
         Conflict conflict = conflictBuilder.getConflict(mergeBranch, artIdSetDontShow);
         if (conflict != null) {
            conflict.computeStatus();
            conflicts.add(conflict);
         }
      }

      return conflicts;
   }

   /**
    * 
    */
   private void preloadConflictArtifacts(Branch sourceBranch, Branch destinationBranch, Branch mergeBranch, Collection<Integer> artIdSet) throws SQLException {
      if (artIdSet != null && !artIdSet.isEmpty()) {
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

         List<Object[]> datas = new LinkedList<Object[]>();
         for (int artId : artIdSet) {
            datas.add(new Object[] {SQL3DataType.INTEGER, queryId, SQL3DataType.TIMESTAMP, insertTime,
                  SQL3DataType.INTEGER, artId, SQL3DataType.INTEGER, sourceBranch.getBranchId()});
            datas.add(new Object[] {SQL3DataType.INTEGER, queryId, SQL3DataType.TIMESTAMP, insertTime,
                  SQL3DataType.INTEGER, artId, SQL3DataType.INTEGER, destinationBranch.getBranchId()});
            datas.add(new Object[] {SQL3DataType.INTEGER, queryId, SQL3DataType.TIMESTAMP, insertTime,
                  SQL3DataType.INTEGER, artId, SQL3DataType.INTEGER, mergeBranch.getBranchId()});
         }
         ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, datas, true);
      }
   }

   /**
    * @param sourceBranch
    * @param destinationBranch
    * @param baselineTransaction
    * @param conflictBuilders
    * @param artIdSet
    */

   private void loadArtifactVersionConflicts(Branch sourceBranch, Branch destinationBranch, TransactionId baselineTransaction, ArrayList<ConflictBuilder> conflictBuilders, Set<Integer> artIdSet, Set<Integer> artIdSetDontShow, Set<Integer> artIdSetDontAdd) throws SQLException {
      ConnectionHandlerStatement connectionHandlerStatement = null;

      try {
         connectionHandlerStatement =
               ConnectionHandler.runPreparedQuery(ARTIFACT_CONFLICTS, SQL3DataType.INTEGER, sourceBranch.getBranchId(),
                     SQL3DataType.INTEGER, baselineTransaction.getTransactionNumber(), SQL3DataType.INTEGER,
                     destinationBranch.getBranchId(), SQL3DataType.INTEGER, baselineTransaction.getTransactionNumber());

         TransactionId sourceHeadTransactionId =
               TransactionIdManager.getInstance().getEditableTransactionId(sourceBranch);
         ResultSet resultSet = connectionHandlerStatement.getRset();

         if (!resultSet.next()) return;
         ArtifactConflictBuilder artifactConflictBuilder;
         int artId = 0;

         do {
            int nextArtId = resultSet.getInt("art_id");
            int sourceGamma = resultSet.getInt("source_gamma");
            int destGamma = resultSet.getInt("dest_gamma");
            int sourceModType = resultSet.getInt("source_mod_type");
            int destModType = resultSet.getInt("dest_mod_type");
            int artTypeId = resultSet.getInt("art_type_id");
            int beginGamma = resultSet.getInt("begin_gamma");

            if (artId != nextArtId && beginGamma != destGamma) {
               artId = nextArtId;

               if ((destModType == ModificationType.DELETED.getValue() && sourceModType == ModificationType.CHANGE.getValue()) || (destModType == ModificationType.CHANGE.getValue() && sourceModType == ModificationType.DELETED.getValue())) {

                  artifactConflictBuilder =
                        new ArtifactConflictBuilder(sourceGamma, destGamma, artId, baselineTransaction,
                              sourceHeadTransactionId, ModificationType.getMod(sourceModType), sourceBranch,
                              destinationBranch, sourceModType, destModType, artTypeId);

                  conflictBuilders.add(artifactConflictBuilder);
                  artIdSet.add(artId);
               } else if (destModType == ModificationType.DELETED.getValue() && sourceModType == ModificationType.DELETED.getValue()) {
                  artIdSetDontShow.add(artId);
                  artIdSetDontAdd.add(artId);
               }
            }
         } while (resultSet.next());
      } finally {
         DbUtil.close(connectionHandlerStatement);
      }
      for (Integer integer : artIdSet) {
         artIdSetDontShow.add(integer);
      }
   }

   /**
    * @param sourceBranch
    * @param destinationBranch
    * @param baselineTransaction
    * @param conflicts
    * @throws SQLException
    */
   private void loadAttributeConflicts(Branch sourceBranch, Branch destinationBranch, TransactionId baselineTransaction, ArrayList<ConflictBuilder> conflictBuilders, Set<Integer> artIdSet) throws SQLException, OseeCoreException {
      ConnectionHandlerStatement connectionHandlerStatement = null;
      AttributeConflictBuilder attributeConflictBuilder;
      try {
         connectionHandlerStatement =
               ConnectionHandler.runPreparedQuery(ATTRIBUTE_CONFLICTS_NEW, SQL3DataType.INTEGER,
                     sourceBranch.getBranchId(), SQL3DataType.INTEGER, baselineTransaction.getTransactionNumber(),
                     SQL3DataType.INTEGER, destinationBranch.getBranchId());

         TransactionId sourceHeadTransactionId =
               TransactionIdManager.getInstance().getEditableTransactionId(sourceBranch);
         ResultSet resultSet = connectionHandlerStatement.getRset();

         int attrId = 0;

         if (resultSet.next()) {

            do {
               int nextAttrId = resultSet.getInt("attr_id");
               int artId = resultSet.getInt("art_id");
               int sourceGamma = resultSet.getInt("source_gamma");
               int destGamma = resultSet.getInt("dest_gamma");
               int modType = resultSet.getInt("mod_type");
               int attrTypeId = resultSet.getInt("attr_type_id");
               String sourceValue = resultSet.getString("source_value");
               String destValue = resultSet.getString("dest_value");

               if (attrId != nextAttrId && modType == ModificationType.NEW.getValue()) {
                  attrId = nextAttrId;
                  attributeConflictBuilder =
                        new AttributeConflictBuilder(sourceGamma, destGamma, artId, baselineTransaction,
                              sourceHeadTransactionId, ModificationType.getMod(modType), sourceBranch,
                              destinationBranch, sourceValue, destValue, attrId, attrTypeId);

                  conflictBuilders.add(attributeConflictBuilder);
                  artIdSet.add(artId);
               }
            } while (resultSet.next());
         }

      } finally {
         DbUtil.close(connectionHandlerStatement);
      }

      try {
         connectionHandlerStatement =
               ConnectionHandler.runPreparedQuery(ATTRIBUTE_CONFLICTS, SQL3DataType.INTEGER,
                     sourceBranch.getBranchId(), SQL3DataType.INTEGER, baselineTransaction.getTransactionNumber(),
                     SQL3DataType.INTEGER, destinationBranch.getBranchId(), SQL3DataType.INTEGER,
                     baselineTransaction.getTransactionNumber());

         ResultSet resultSet = connectionHandlerStatement.getRset();

         TransactionId sourceHeadTransactionId =
               TransactionIdManager.getInstance().getEditableTransactionId(sourceBranch);
         if (!resultSet.next()) return;
         int attrId = 0;

         do {
            int nextAttrId = resultSet.getInt("attr_id");
            int artId = resultSet.getInt("art_id");
            int sourceGamma = resultSet.getInt("source_gamma");
            int destGamma = resultSet.getInt("dest_gamma");
            int modType = resultSet.getInt("mod_type");
            int attrTypeId = resultSet.getInt("attr_type_id");
            int beginGamma = resultSet.getInt("begin_gamma");
            String sourceValue = resultSet.getString("source_value");
            String destValue = resultSet.getString("dest_value");

            if (attrId != nextAttrId && beginGamma != destGamma) {
               attrId = nextAttrId;
               attributeConflictBuilder =
                     new AttributeConflictBuilder(sourceGamma, destGamma, artId, baselineTransaction,
                           sourceHeadTransactionId, ModificationType.getMod(modType), sourceBranch, destinationBranch,
                           sourceValue, destValue, attrId, attrTypeId);

               conflictBuilders.add(attributeConflictBuilder);
               artIdSet.add(artId);
            }
         } while (resultSet.next());
      } finally {
         DbUtil.close(connectionHandlerStatement);
      }
   }

   private Collection<AttributeChange> getAttributeChanges(ChangeType changeType, int fromTransactionNumber, int toTransactionNumber, int artId) {

      Collection<AttributeChange> revisions = new LinkedList<AttributeChange>();
      String sql =
            "SELECT data_table.*, " + Table.alias(
                  "(SELECT " + SkynetDatabase.ATTRIBUTE_VERSION_TABLE + ".VALUE " + "FROM " + SkynetDatabase.ATTRIBUTE_VERSION_TABLE + " " + "WHERE gamma_id = data_table.was_gamma)",
                  "was_value") + "," + Table.alias(
                  "(SELECT " + SkynetDatabase.ATTRIBUTE_VERSION_TABLE + ".uri " + "FROM " + SkynetDatabase.ATTRIBUTE_VERSION_TABLE + " " + "WHERE gamma_id = data_table.was_gamma)",
                  "was_content") + " FROM " + "(SELECT attr1.gamma_id," + Table.alias("attr1.value", "is_value") + "," + Table.alias(
                  "attr1.uri", "is_content") + "," + "attr1.modification_id," + "attr1.attr_id," + ATTRIBUTE_TYPE_TABLE.column("name") + "," + Table.alias(
                  "(SELECT MAX(attr2.gamma_id) " + "FROM " + SkynetDatabase.ATTRIBUTE_VERSION_TABLE + " attr2, " + SkynetDatabase.TRANSACTIONS_TABLE + " t3, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t4 " + "WHERE attr1.attr_id = attr2.attr_id and attr2.gamma_id = t3.gamma_id and t3.transaction_id = t4.transaction_id and t1.branch_id = t4.branch_id " + "AND attr2.gamma_id < attr1.gamma_id)",
                  "was_gamma") + " FROM " + ATTRIBUTE_VERSION_TABLE + " attr1," + ATTRIBUTE_TYPE_TABLE + "," + TRANSACTIONS_TABLE + ", " + TRANSACTION_DETAIL_TABLE + "," + " (SELECT branch_id FROM " + TRANSACTION_DETAIL_TABLE + " WHERE transaction_id=?) T1" + " WHERE attr1.gamma_id = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND attr1.attr_type_id=" + ATTRIBUTE_TYPE_TABLE.column("attr_type_id") + " AND " + (fromTransactionNumber == toTransactionNumber ? TRANSACTIONS_TABLE.column("transaction_id") + " = ?" : TRANSACTIONS_TABLE.column("transaction_id") + " > ? " + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + " <= ?") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=T1.branch_id" + " AND art_id = ?) data_table" + " ORDER BY gamma_id DESC";

      Collection<Object> dataList = new LinkedList<Object>();
      dataList.add(SQL3DataType.BIGINT);
      dataList.add(fromTransactionNumber);
      if (fromTransactionNumber != toTransactionNumber) {
         dataList.add(SQL3DataType.BIGINT);
         dataList.add(fromTransactionNumber);
      }
      dataList.add(SQL3DataType.BIGINT);
      dataList.add(toTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(artId);

      try {
         Query.acquireCollection(revisions, new AttributeChangeProcessor(changeType), sql, dataList.toArray());

      } catch (SQLException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      }

      return revisions;
   }

   private Collection<RelationLinkChange> getRelationLinkChanges(ChangeType changeType, int fromTransactionNumber, int toTransactionNumber, int artId, IArtifactNameDescriptorResolver artifactNameDescriptorResolver) {

      String transactionCheck =
            fromTransactionNumber == toTransactionNumber ? TRANSACTIONS_TABLE.column("transaction_id") + " = ?" : TRANSACTIONS_TABLE.column("transaction_id") + " > ? " + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + " <= ?";

      Collection<RelationLinkChange> revisions = new LinkedList<RelationLinkChange>();
      String sql =
            "SELECT gamma_id, rationale, modification_id, art_id, rel_link_id, order_val, type_name, side_name FROM " + Table.alias(
                  "(SELECT " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + "," + RELATION_LINK_VERSION_TABLE.column("rationale") + "," + RELATION_LINK_VERSION_TABLE.column("modification_id") + "," + RELATION_LINK_VERSION_TABLE.column("a_art_id") + " AS art_id, " + RELATION_LINK_VERSION_TABLE.column("rel_link_id") + "," + RELATION_LINK_VERSION_TABLE.column("a_order") + " AS order_val, " + RELATION_LINK_TYPE_TABLE.column("type_name") + "," + RELATION_LINK_TYPE_TABLE.column("a_name") + " AS side_name" + " FROM " + RELATION_LINK_VERSION_TABLE + "," + RELATION_LINK_TYPE_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + "," + Table.alias(
                        " (SELECT branch_id FROM " + TRANSACTION_DETAIL_TABLE + " WHERE transaction_id=?)", "T2") + " WHERE " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + transactionCheck + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=T2.branch_id" + " AND " + RELATION_LINK_VERSION_TABLE.column("rel_link_type_id") + "=" + RELATION_LINK_TYPE_TABLE.column("rel_link_type_id") + " AND b_art_id = ?)",
                  "aliasForSyntax") + " UNION ALL " + "(SELECT " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + "," + RELATION_LINK_VERSION_TABLE.column("rationale") + "," + RELATION_LINK_VERSION_TABLE.column("modification_id") + "," + RELATION_LINK_VERSION_TABLE.column("b_art_id") + " AS art_id, " + RELATION_LINK_VERSION_TABLE.column("rel_link_id") + "," + RELATION_LINK_VERSION_TABLE.column("b_order") + " AS order_val, " + RELATION_LINK_TYPE_TABLE.column("type_name") + "," + RELATION_LINK_TYPE_TABLE.column("b_name") + " AS side_name" + " FROM " + RELATION_LINK_VERSION_TABLE + "," + RELATION_LINK_TYPE_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + "," + Table.alias(
                  " (SELECT branch_id FROM " + TRANSACTION_DETAIL_TABLE + " WHERE transaction_id=?)", "T2") + " WHERE " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + transactionCheck + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=T2.branch_id" + " AND " + RELATION_LINK_VERSION_TABLE.column("rel_link_type_id") + "=" + RELATION_LINK_TYPE_TABLE.column("rel_link_type_id") + " AND a_art_id = ?)" + " ORDER BY gamma_id DESC";

      Collection<Object> dataList = new LinkedList<Object>();
      dataList.add(SQL3DataType.BIGINT);
      dataList.add(fromTransactionNumber);
      if (fromTransactionNumber != toTransactionNumber) {
         dataList.add(SQL3DataType.BIGINT);
         dataList.add(fromTransactionNumber);
      }
      dataList.add(SQL3DataType.BIGINT);
      dataList.add(toTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(artId);
      dataList.add(SQL3DataType.BIGINT);
      dataList.add(fromTransactionNumber);
      if (fromTransactionNumber != toTransactionNumber) {
         dataList.add(SQL3DataType.BIGINT);
         dataList.add(fromTransactionNumber);
      }
      dataList.add(SQL3DataType.BIGINT);
      dataList.add(toTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(artId);

      try {
         Query.acquireCollection(revisions,
               new RelationLinkChangeProcessor(changeType, artifactNameDescriptorResolver), sql, dataList.toArray());

      } catch (SQLException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      }

      return revisions;
   }

   /**
    * Returns the name of an artifact
    * 
    * @param artId
    */
   public String getName(int artId) {
      String name = bemsToName.get(artId);

      if (name == null) {
         String sql =
               "SELECT " + ATTRIBUTE_VERSION_TABLE.columns("value") + " FROM " + ATTRIBUTE_VERSION_TABLE + ", " + ATTRIBUTE_TYPE_TABLE + " WHERE art_id = ? AND " + ATTRIBUTE_TYPE_TABLE.column("attr_type_id") + " = " + ATTRIBUTE_VERSION_TABLE.column("attr_type_id") + " AND " + ATTRIBUTE_TYPE_TABLE.column("name") + " = 'Name'";

         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt = ConnectionHandler.runPreparedQuery(sql, SQL3DataType.INTEGER, artId);

            if (chStmt.next()) name = chStmt.getRset().getString("value");
         } catch (SQLException e) {
            logger.log(Level.SEVERE, e.toString(), e);
         } finally {
            DbUtil.close(chStmt);
         }
         bemsToName.put(artId, name);
      }
      return name;
   }

   private Collection<ArtifactChange> getArtifactChanges(TransactionId fromTransactionId, TransactionId toTransactionId, int artId) throws SQLException, ArtifactDoesNotExist {
      return getArtifactChanges(OUTGOING, fromTransactionId, toTransactionId, artId);
   }

   private Collection<ArtifactChange> getArtifactChanges(ChangeType changeType, TransactionId fromTransactionId, TransactionId toTransactionId, int artId) throws SQLException, ArtifactDoesNotExist {
      Collection<ArtifactChange> changes = new LinkedList<ArtifactChange>();
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(GET_CHANGED_ARTIFACTS, SQL3DataType.INTEGER, artId,
                     SQL3DataType.INTEGER, fromTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                     toTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                     fromTransactionId.getBranch().getBranchId());

         Artifact artifact = ArtifactQuery.getArtifactFromId(artId, fromTransactionId.getBranch(), true);

         ResultSet rSet = chStmt.getRset();
         while (chStmt.next()) {
            changes.add(new ArtifactChange(changeType, ModificationType.getMod(rSet.getInt("mod_type")), artifact,
                  null, null, null, toTransactionId, fromTransactionId, rSet.getInt("gamma_id")));
         }
      } finally {
         DbUtil.close(chStmt);
      }
      return changes;
   }

   public Collection<ArtifactChange> getDeletedArtifactChanges(TransactionId transactionId) throws SQLException, BranchDoesNotExist, TransactionDoesNotExist {
      return getDeletedArtifactChanges(null, null,
            TransactionIdManager.getInstance().getPriorTransaction(transactionId), transactionId, null);
   }

   public Collection<ArtifactChange> getDeletedArtifactChanges(TransactionId baseParentTransactionId, TransactionId headParentTransactionId, TransactionId fromTransactionId, TransactionId toTransactionId, ArtifactNameDescriptorCache artifactNameDescriptorCache) throws SQLException {
      Collection<ArtifactChange> deletedArtifacts = new LinkedList<ArtifactChange>();
      //This for the case where the toTransaction is the baseline transaction for a branch
      if (fromTransactionId == null) {
         fromTransactionId = toTransactionId;
      }
      if (!fromTransactionId.getBranch().equals(toTransactionId.getBranch())) {
         throw new IllegalArgumentException("The fromTransactionId and toTransactionId must be on the same branch");
      }
      if (fromTransactionId.getTransactionNumber() > toTransactionId.getTransactionNumber()) {
         throw new IllegalArgumentException("The fromTransactionId can not be greater than the toTransactionId.");
      }

      Query.acquireCollection(deletedArtifacts, new ArtifactChangeProcessor(baseParentTransactionId,
            headParentTransactionId, artifactNameDescriptorCache), GET_DELETED_ARTIFACTS, SQL3DataType.VARCHAR, "Name",
            SQL3DataType.INTEGER, fromTransactionId.getBranch().getBranchId(), SQL3DataType.INTEGER,
            fromTransactionId.getTransactionNumber(), SQL3DataType.INTEGER, toTransactionId.getTransactionNumber(),
            SQL3DataType.INTEGER, DELETED.getValue());

      return deletedArtifacts;
   }

   public Collection<Artifact> getNewAndModifiedArtifacts(Branch branch, boolean includeRelationOnlyChanges) throws OseeCoreException, SQLException {
      List<TransactionData> transactionDataSet = getTransactionsPerBranch(branch);
      if (transactionDataSet.size() == 0) return new ArrayList<Artifact>();
      return getNewAndModifiedArtifacts(transactionDataSet.get(transactionDataSet.size() - 1).getTransactionId(),
            transactionDataSet.get(0).getTransactionId(), includeRelationOnlyChanges);
   }

   public Collection<Artifact> getNewAndModifiedArtifacts(TransactionId baseTransaction, TransactionId toTransaction, boolean includeRelationOnlyChanges) throws SQLException {
      List<ISearchPrimitive> criteria = new ArrayList<ISearchPrimitive>(2);
      criteria.add(new ArtifactInTransactionSearch(baseTransaction, toTransaction));

      if (includeRelationOnlyChanges) {
         criteria.add(new RelationInTransactionSearch(baseTransaction, toTransaction));
      }
      return ArtifactPersistenceManager.getArtifacts(criteria, false, toTransaction.getBranch());
   }

   public Collection<Artifact> getRelationChangedArtifacts(TransactionId baseTransaction, TransactionId toTransaction) throws SQLException {
      List<ISearchPrimitive> criteria = new ArrayList<ISearchPrimitive>(2);
      criteria.add(new RelationInTransactionSearch(baseTransaction, toTransaction));
      return ArtifactPersistenceManager.getArtifacts(criteria, false, toTransaction.getBranch());
   }

   /**
    * @param newAndModArts
    * @throws SQLException
    */
   public Collection<ArtifactChange> getNewAndModArtifactChanges(TransactionId baseParentTransactionId, TransactionId headParentTransactionId, TransactionId fromTransactionId, TransactionId toTransactionId, ArtifactNameDescriptorCache artifactNameDescriptorCache) throws SQLException {

      Collection<Artifact> newAndModArts = getNewAndModifiedArtifacts(fromTransactionId, toTransactionId, true);

      if (newAndModArts.isEmpty()) return new ArrayList<ArtifactChange>(0);

      Collection<ArtifactChange> newAndModArtChanges = new LinkedList<ArtifactChange>();
      Collection<Integer> artIds = new ArrayList<Integer>(newAndModArts.size());
      Map<Integer, TransactionId> artIdToMinOver = new HashMap<Integer, TransactionId>();
      Map<Integer, TransactionId> artIdToMaxUnder = new HashMap<Integer, TransactionId>();

      for (Artifact artifact : newAndModArts)
         artIds.add(artifact.getArtId());

      try {
         Queue<Integer> artIdQueue = new LinkedList<Integer>(artIds);
         Collection<Integer> artIdBlock = new ArrayList<Integer>(1000);

         while (!artIdQueue.isEmpty()) {
            artIdBlock.clear();
            if (artIdQueue.size() > 1000) {
               for (int count = 0; count < 1000; count++) {
                  artIdBlock.add(artIdQueue.poll());
               }
            } else {
               artIdBlock.addAll(artIdQueue);
               artIdQueue.clear();
            }

            ConnectionHandlerStatement chStmt = null;
            try {
               String sql =
                     "SELECT " + TRANSACTION_DETAIL_TABLE.min("transaction_id", "base_tx") + ", " + ARTIFACT_VERSION_TABLE.column("art_id") + " FROM " + ARTIFACT_VERSION_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_VERSION_TABLE.column("art_id") + " IN " + Collections.toString(
                           artIdBlock, "(", ",", ")") + " AND " + ARTIFACT_VERSION_TABLE.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ">= ? " + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<= ? " + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " GROUP BY " + ARTIFACT_VERSION_TABLE.column("art_id");

               chStmt =
                     ConnectionHandler.runPreparedQuery(sql, SQL3DataType.INTEGER,
                           fromTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                           toTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                           fromTransactionId.getBranch().getBranchId());

               ResultSet rset = chStmt.getRset();
               while (rset.next()) {
                  artIdToMinOver.put(rset.getInt("art_id"),
                        TransactionIdManager.getInstance().getPossiblyEditableTransactionIfFromCache(
                              rset.getInt("base_tx")));
               }
            } finally {
               DbUtil.close(chStmt);
            }

            ConnectionHandlerStatement chStmt1 = null;
            try {
               String sql =
                     "SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id", "base_tx") + ", " + ARTIFACT_VERSION_TABLE.column("art_id") + " FROM " + ARTIFACT_VERSION_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_VERSION_TABLE.column("art_id") + " IN " + Collections.toString(
                           artIdBlock, "(", ",", ")") + " AND " + ARTIFACT_VERSION_TABLE.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<= ? " + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " GROUP BY " + ARTIFACT_VERSION_TABLE.column("art_id");
               chStmt1 =
                     ConnectionHandler.runPreparedQuery(sql, SQL3DataType.INTEGER,
                           fromTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                           fromTransactionId.getBranch().getBranchId());

               ResultSet rset = chStmt1.getRset();
               while (rset.next()) {
                  artIdToMaxUnder.put(rset.getInt("art_id"),
                        TransactionIdManager.getInstance().getPossiblyEditableTransactionIfFromCache(
                              rset.getInt("base_tx")));
               }
            } finally {
               DbUtil.close(chStmt1);
            }
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (OseeCoreException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }

      TransactionId baselineTransaction;
      TransactionId minOverTransaction;
      TransactionId maxUnderTransaction;
      for (Artifact artifact : newAndModArts) {
         artifactNameDescriptorCache.cache(artifact.getArtId(), artifact.getDescriptiveName(),
               artifact.getArtifactType());

         minOverTransaction = artIdToMinOver.get(artifact.getArtId());
         maxUnderTransaction = artIdToMaxUnder.get(artifact.getArtId());
         if (maxUnderTransaction != null)
            baselineTransaction = maxUnderTransaction;
         else
            baselineTransaction = minOverTransaction;
         newAndModArtChanges.add(new ArtifactChange(
               OUTGOING,
               (maxUnderTransaction == null && baselineTransaction != fromTransactionId) ? ModificationType.NEW : ModificationType.CHANGE,
               artifact, baseParentTransactionId, headParentTransactionId, baselineTransaction, fromTransactionId,
               toTransactionId, -1));
      }

      return newAndModArtChanges;
   }

   /**
    * Produces <code>ArtifactChange</code>'s from a ResultSet. <br/><br/> For deleted artifacts, the following
    * columns must be available from the set:
    * <li>branch_id</li>
    * <li>modification_id</li>
    * <li>name</li>
    * <li>type_name</li>
    * <br/><br/> For new and modified artifacts, the following columns must be available from the set:
    * <li>art_id</li>
    * <li>branch_id</li>
    * <li>modification_id</li>
    * <li>transaction_id</li>
    * 
    * @author Robert A. Fisher
    */
   private class ArtifactChangeProcessor implements RsetProcessor<ArtifactChange> {
      private final ArtifactNameDescriptorCache artifactNameDescriptorCache;
      private final TransactionId baseParentTransactionId;
      private final TransactionId headParentTransactionId;
      private TransactionId fromTransactionId;
      private TransactionId toTransactionId;

      /**
       * This constructor is to be used for processing deleted artifact change data, and is appropriate for all
       * transaction ranges since from/toTransactionId information is not stored on deleted changes.
       */
      public ArtifactChangeProcessor(TransactionId baseParentTransactionId, TransactionId headParentTransactionId, ArtifactNameDescriptorCache artifactNameDescriptorCache) {
         this.baseParentTransactionId = baseParentTransactionId;
         this.headParentTransactionId = headParentTransactionId;
         this.artifactNameDescriptorCache = artifactNameDescriptorCache;
      }

      public ArtifactChange process(ResultSet set) throws SQLException {
         try {
            ModificationType modType = ModificationType.getMod(set.getInt("modification_id"));

            TransactionId transactionId =
                  TransactionIdManager.getInstance().getPossiblyEditableTransactionIfFromCache(
                        set.getInt("deleted_transaction"));
            int artId = set.getInt("art_id");
            Artifact artifact = ArtifactPersistenceManager.getInstance().getArtifactFromId(artId, transactionId);

            if (artifactNameDescriptorCache != null) artifactNameDescriptorCache.cache(artId,
                  artifact.getDescriptiveName(), artifact.getArtifactType());

            return new ArtifactChange(OUTGOING, modType, artifact, baseParentTransactionId, headParentTransactionId,
                  fromTransactionId, fromTransactionId, transactionId, set.getInt("gamma_id"));

         } catch (OseeCoreException ex) {
            throw new SQLException(ex);
         }
      }

      public boolean validate(ArtifactChange item) {
         return item != null;
      }
   }

   /**
    * Produces <code>AttributeChange</code>'s from a ResultSet. <br/><br/> For deleted attributes, the following
    * columns must be available from the set:
    * <li>gamma_id</li>
    * <li>modification_id</li>
    * <li>name</li>
    * <br/><br/> For new and modified attributes, the following columns must be available from the set:
    * <li>gamma_id</li>
    * <li>modification_id</li>
    * <li>name</li>
    * <li>value</li>
    * 
    * @author Robert A. Fisher
    */
   private static class AttributeChangeProcessor implements RsetProcessor<AttributeChange> {
      private final ChangeType changeType;

      /**
       * @param changeType
       */
      public AttributeChangeProcessor(ChangeType changeType) {
         this.changeType = changeType;
      }

      public AttributeChange process(ResultSet set) throws SQLException {
         ModificationType modType = ModificationType.getMod(set.getInt("modification_id"));
         if (modType == DELETED) {
            String wasValue = set.getString("was_value");
            return new AttributeChange(changeType, set.getInt("attr_id"), set.getLong("gamma_id"),
                  set.getString("name"), wasValue == null ? "" : wasValue);
         } else {
            String isValue = set.getString("is_value");
            String wasValue = set.getString("was_value");
            return new AttributeChange(changeType, modType, set.getInt("attr_id"), set.getLong("gamma_id"),
                  set.getString("name"), isValue == null ? "" : isValue, set.getBinaryStream("is_content"),
                  wasValue == null ? "" : wasValue, set.getBinaryStream("was_content"));
         }
      }

      public boolean validate(AttributeChange item) {
         return item != null;
      }
   }

   /**
    * Produces <code>RelationLinkChange</code>'s from a ResultSet. <br/><br/> For deleted links, the following
    * columns must be available from the set:
    * <li>gamma_id</li>
    * <li>modification_id</li>
    * <li>type_name</li>
    * <li>art_id</li>
    * <br/><br/> For new and modified attributes, the following columns must be available from the set:
    * <li>gamma_id</li>
    * <li>modification_id</li>
    * <li>type_name</li>
    * <li>art_id</li>
    * <li>rationale</li>
    * <li>order_val</li>
    * 
    * @author Robert A. Fisher
    */
   private static class RelationLinkChangeProcessor implements RsetProcessor<RelationLinkChange> {
      private final IArtifactNameDescriptorResolver artifactNameDescriptorResolver;
      private final ChangeType changeType;

      /**
       * @param artifactNameDescriptorResolver
       */
      public RelationLinkChangeProcessor(ChangeType changeType, IArtifactNameDescriptorResolver artifactNameDescriptorResolver) {
         this.changeType = changeType;
         this.artifactNameDescriptorResolver = artifactNameDescriptorResolver;
      }

      public RelationLinkChange process(ResultSet set) throws SQLException {

         ModificationType modType = ModificationType.getMod(set.getInt("modification_id"));

         Pair<String, ArtifactType> artifactData;
         if (artifactNameDescriptorResolver != null)
            artifactData = artifactNameDescriptorResolver.get(set.getInt("art_id"));
         else
            artifactData = UNKNOWN_DATA;

         String relName = set.getString("type_name") + " (" + set.getString("side_name") + ")";
         if (modType == DELETED) {
            return new RelationLinkChange(changeType, set.getInt("rel_link_id"), set.getLong("gamma_id"), relName,
                  artifactData.getKey(), artifactData.getValue());
         } else {
            return new RelationLinkChange(changeType, modType, set.getInt("rel_link_id"), set.getLong("gamma_id"),
                  set.getString("rationale"), set.getInt("order_val"), relName, artifactData.getKey(),
                  artifactData.getValue());
         }
      }

      public boolean validate(RelationLinkChange item) {
         return item != null;
      }
   }

   public boolean branchHasChanges(Branch branch) throws IllegalStateException, SQLException, BranchDoesNotExist, TransactionDoesNotExist {
      Pair<TransactionId, TransactionId> transactions = TransactionIdManager.getInstance().getStartEndPoint(branch);
      return transactions.getKey() != transactions.getValue();
   }

   private static final String OTHER_EDIT_SQL =
         "SELECT distinct(t3.branch_id) " + "FROM " + SkynetDatabase.ARTIFACT_VERSION_TABLE + " t1, " + "  " + SkynetDatabase.TRANSACTIONS_TABLE + " t2, " + "  " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t3, " + "    (SELECT " + TRANSACTION_DETAIL_TABLE.min(
               "transaction_id", "min_tx_id") + ", branch_id " + "   FROM " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " " + "   GROUP BY branch_id) t4, " + "   osee_define_branch t5 " + "WHERE t1.art_id = ? " + " AND t1.gamma_id = t2.gamma_id " + " AND t2.transaction_id <> t4.min_tx_id " + " AND t2.transaction_id = t3.transaction_id " + " and t3.branch_id = t4.branch_id " + " and t4.branch_id <> ?" + " and t5.parent_branch_id = ?" + " and t4.branch_id = t5.branch_id" + " and t5.archived = 0";

   /**
    * Returns all the other branches this artifact has been editted on, besides modifications to program branch.
    * 
    * @param artifact
    */
   public Collection<Branch> getOtherEdittedBranches(Artifact artifact) {
      Collection<Branch> otherBranches = new LinkedList<Branch>();

      // Can only be on other branches it has already been saved
      if (artifact.isInDb()) {
         ConnectionHandlerStatement chStmt = null;

         try {
            chStmt =
                  ConnectionHandler.runPreparedQuery(OTHER_EDIT_SQL, SQL3DataType.INTEGER, artifact.getArtId(),
                        SQL3DataType.INTEGER, artifact.getBranch().getBranchId(), SQL3DataType.INTEGER,
                        artifact.getBranch().getParentBranchId());

            ResultSet rset = chStmt.getRset();

            while (rset.next()) {
               otherBranches.add(BranchPersistenceManager.getInstance().getBranch(rset.getInt("branch_id")));
            }
         } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
         } finally {
            DbUtil.close(chStmt);
         }
      }
      return otherBranches;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#onEvent(org.eclipse.osee.framework.ui.plugin.event.Event)
    */
   public void onEvent(Event event) {
      if ((event instanceof LocalDeletedBranchEvent) || (event instanceof RemoteDeletedBranchEvent) || (event instanceof LocalNewBranchEvent) || (event instanceof RemoteNewBranchEvent) || (event instanceof LocalCommitBranchEvent) || (event instanceof RemoteCommitBranchEvent)) {
         // Clear the cache so it gets reloaded
         commitArtifactIdToTransactionId = null;
         /**
          * Need to kick event for classes that need to be notified only after the cache has been updated; Even though
          * cache has bee set to null, it will be re-created upon next call to get cached information
          */
         SkynetEventManager.getInstance().kick(new LocalBranchToArtifactCacheUpdateEvent(this));
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return false;
   }
}