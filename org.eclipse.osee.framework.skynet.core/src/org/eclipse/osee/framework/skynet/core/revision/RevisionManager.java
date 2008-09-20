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
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactInTransactionSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.RelationInTransactionSearch;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.event.BranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * Manages artifact versions in Skynet
 * 
 * @author Jeff C. Phillips
 */
public class RevisionManager {
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

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(RevisionManager.class);
   private static final Pair<String, ArtifactType> UNKNOWN_DATA = new Pair<String, ArtifactType>(null, null);

   private Map<Integer, Set<Integer>> commitArtifactIdToTransactionId;

   private static final RevisionManager instance = new RevisionManager();

   private final Map<Integer, String> bemsToName;

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
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_TRANSACTIONS, branch.getBranchId());

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

   @Deprecated
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
         OseeEventManager.addListener(new BranchEventListener() {
            /* (non-Javadoc)
             * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, org.eclipse.osee.framework.skynet.core.artifact.Branch, int)
             */
            @Override
            public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
               if (branchModType == BranchEventType.Added || branchModType == BranchEventType.Deleted || branchModType == BranchEventType.Committed) {
                  // Clear the cache so it gets reloaded
                  commitArtifactIdToTransactionId = null;
                  /**
                   * Need to kick event for classes that need to be notified only after the cache has been updated; Even
                   * though cache has bee set to null, it will be re-created upon next call to get cached information
                   */
                  try {
                     OseeEventManager.kickLocalBranchToArtifactCacheUpdateEvent(this);
                  } catch (Exception ex) {
                     SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                  }
               }
            }
         });
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

      final Integer artId = artifact.getArtId();
      Branch cursor = artifact.getBranch();
      Integer limit = Integer.MAX_VALUE;

      while (cursor != null) {
         try {
            chStmt =
                  ConnectionHandler.runPreparedQuery(SELECT_TRANSACTIONS_FOR_ARTIFACT, artId, cursor.getBranchId(),
                        artId, cursor.getBranchId(), artId, cursor.getBranchId(), limit);

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
         } finally {
            DbUtil.close(chStmt);
         }
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
    * @throws TransactionDoesNotExist
    */
   public Collection<RevisionChange> getTransactionChanges(TransactionData tData) throws SQLException, BranchDoesNotExist, ArtifactDoesNotExist, TransactionDoesNotExist {
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
   public Collection<RevisionChange> getAllTransactionChanges(ChangeType changeType, TransactionId fromTransactionId, TransactionId toTransactionId, int artId, IArtifactNameDescriptorResolver artifactNameDescriptorResolver) throws SQLException, BranchDoesNotExist, ArtifactDoesNotExist {
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
      dataList.add(fromTransactionNumber);
      if (fromTransactionNumber != toTransactionNumber) {
         dataList.add(fromTransactionNumber);
      }
      dataList.add(toTransactionNumber);
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
      dataList.add(fromTransactionNumber);
      if (fromTransactionNumber != toTransactionNumber) {
         dataList.add(fromTransactionNumber);
      }
      dataList.add(toTransactionNumber);
      dataList.add(artId);
      dataList.add(fromTransactionNumber);
      if (fromTransactionNumber != toTransactionNumber) {
         dataList.add(fromTransactionNumber);
      }
      dataList.add(toTransactionNumber);
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
            chStmt = ConnectionHandler.runPreparedQuery(sql, artId);

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
               ConnectionHandler.runPreparedQuery(GET_CHANGED_ARTIFACTS, artId,
                     fromTransactionId.getTransactionNumber(), toTransactionId.getTransactionNumber(),
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
      return getDeletedArtifactChanges(null, null, TransactionIdManager.getPriorTransaction(transactionId),
            transactionId, null);
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
            headParentTransactionId, artifactNameDescriptorCache), GET_DELETED_ARTIFACTS, "Name",
            fromTransactionId.getBranch().getBranchId(), fromTransactionId.getTransactionNumber(),
            toTransactionId.getTransactionNumber(), DELETED.getValue());

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
      Set<Artifact> modOnlyArtifacts = new HashSet<Artifact>();
      for (Artifact artifact : ArtifactPersistenceManager.getArtifactsNotCurrent(criteria, false, toTransaction, null)) {
         if (!artifact.isDeleted()) {
            modOnlyArtifacts.add(artifact);
         }
      }
      return modOnlyArtifacts;
   }

   public Collection<Artifact> getRelationChangedArtifacts(TransactionId baseTransaction, TransactionId toTransaction) throws SQLException {
      List<ISearchPrimitive> criteria = new ArrayList<ISearchPrimitive>(2);
      criteria.add(new RelationInTransactionSearch(baseTransaction, toTransaction));
      return ArtifactPersistenceManager.getArtifactsNotCurrent(criteria, false, toTransaction, null);
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
                     ConnectionHandler.runPreparedQuery(sql, fromTransactionId.getTransactionNumber(),
                           toTransactionId.getTransactionNumber(), fromTransactionId.getBranch().getBranchId());

               ResultSet rset = chStmt.getRset();
               while (rset.next()) {
                  artIdToMinOver.put(rset.getInt("art_id"),
                        TransactionIdManager.getTransactionId(rset.getInt("base_tx")));
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
                     ConnectionHandler.runPreparedQuery(sql, fromTransactionId.getTransactionNumber(),
                           fromTransactionId.getBranch().getBranchId());

               ResultSet rset = chStmt1.getRset();
               while (rset.next()) {
                  artIdToMaxUnder.put(rset.getInt("art_id"),
                        TransactionIdManager.getTransactionId(rset.getInt("base_tx")));
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

         ModificationType modificationType;
         if (artifact.isDeleted()) {
            modificationType = ModificationType.DELETED;
         } else {
            modificationType =
                  (maxUnderTransaction == null && baselineTransaction != fromTransactionId) ? ModificationType.NEW : ModificationType.CHANGE;
         }

         newAndModArtChanges.add(new ArtifactChange(OUTGOING, modificationType, artifact, baseParentTransactionId,
               headParentTransactionId, baselineTransaction, fromTransactionId, toTransactionId, -1));
      }

      return newAndModArtChanges;
   }

   /**
    * Produces <code>ArtifactChange</code>'s from a ResultSet. <br/><br/> For deleted artifacts, the following columns
    * must be available from the set: <li>branch_id</li> <li>modification_id</li> <li>name</li> <li>type_name</li>
    * <br/><br/> For new and modified artifacts, the following columns must be available from the set: <li>art_id</li>
    * <li>branch_id</li> <li>modification_id</li> <li>transaction_id</li>
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

            TransactionId transactionId = TransactionIdManager.getTransactionId(set.getInt("deleted_transaction"));
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
    * Produces <code>AttributeChange</code>'s from a ResultSet. <br/><br/> For deleted attributes, the following columns
    * must be available from the set: <li>gamma_id</li> <li>modification_id</li> <li>name</li> <br/><br/> For new and
    * modified attributes, the following columns must be available from the set: <li>gamma_id</li> <li>modification_id</li>
    * <li>name</li> <li>value</li>
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
    * Produces <code>RelationLinkChange</code>'s from a ResultSet. <br/><br/> For deleted links, the following columns
    * must be available from the set: <li>gamma_id</li> <li>modification_id</li> <li>type_name</li> <li>art_id</li>
    * <br/><br/> For new and modified attributes, the following columns must be available from the set: <li>gamma_id</li>
    * <li>modification_id</li> <li>type_name</li> <li>art_id</li> <li>rationale</li> <li>order_val</li>
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
      Pair<TransactionId, TransactionId> transactions = TransactionIdManager.getStartEndPoint(branch);
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
                  ConnectionHandler.runPreparedQuery(OTHER_EDIT_SQL, artifact.getArtId(),
                        artifact.getBranch().getBranchId(), artifact.getBranch().getParentBranchId());

            ResultSet rset = chStmt.getRset();

            while (rset.next()) {
               otherBranches.add(BranchPersistenceManager.getBranch(rset.getInt("branch_id")));
            }
         } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
         } finally {
            DbUtil.close(chStmt);
         }
      }
      return otherBranches;
   }

}