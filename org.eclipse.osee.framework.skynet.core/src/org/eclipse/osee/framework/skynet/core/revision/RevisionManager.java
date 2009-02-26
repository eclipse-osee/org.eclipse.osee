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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.RsetProcessor;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.core.schema.Table;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.event.BranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
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
         "SELECT transaction_id, commit_art_id from osee_tx_details where commit_art_id is not null";

   private static final String GET_CHANGED_ARTIFACTS =
         "SELECT arv2.gamma_id, txs1.mod_type FROM osee_artifact ar1, osee_artifact_version arv2, osee_txs txs1, osee_tx_details txd4 WHERE ar1.art_id = ? AND ar1.art_id = arv2.art_id AND arv2.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd4.transaction_id AND txd4.branch_id = ?";

   private static final String GET_DELETED_ARTIFACTS =
         "SELECT txd10.branch_id, att8.value AS name, art5.art_id, ary6.name AS type_name, arv7.modification_id, arv7.gamma_id, (SELECT MAX(txd3.transaction_id) FROM osee_ARTIFACT_VERSION arv1,osee_TXS txs2,osee_TX_DETAILS txd3 WHERE arv1.art_id=arv7.art_id AND arv1.modification_id<> 3 AND arv1.gamma_id=txs2.gamma_id AND txs2.transaction_id=txd3.transaction_id AND txd3.branch_id=txd10.branch_id AND txd3.transaction_id< txd10.transaction_id) as last_good_transaction, txs4.transaction_id as deleted_transaction FROM osee_txs txs4,osee_ARTIFACT art5, osee_ARTIFACT_TYPE ary6, osee_ARTIFACT_VERSION arv7, osee_ATTRIBUTE att8,osee_TXS txs9,osee_TX_DETAILS txd10, osee_TX_DETAILS txd11, (SELECT MAX(att11.gamma_id) as gamma_id FROM osee_ATTRIBUTE att11, osee_ATTRIBUTE_TYPE aty12 WHERE att11.attr_type_id=aty12.attr_type_id AND aty12.name=? GROUP BY att11.art_id) ATTR_GAMMA WHERE txd11.branch_id = txd10.branch_id AND txd11.transaction_id = txs4.transaction_id AND txs4.gamma_id = arv7.gamma_id AND txd10.branch_id=? AND txd10.transaction_id=txs9.transaction_id AND txs9.transaction_id > ?  AND txs9.transaction_id <= ? AND txs9.gamma_id=arv7.gamma_id AND arv7.art_id=art5.art_id AND arv7.modification_id=? AND art5.art_type_id=ary6.art_type_id AND art5.art_id=att8.art_id AND att8.gamma_id= ATTR_GAMMA.gamma_id";

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

   public List<TransactionData> getTransactionsPerBranch(Branch branch) throws OseeCoreException {
      List<TransactionData> transactionDetails = new LinkedList<TransactionData>();

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_TRANSACTIONS, branch.getBranchId());

         while (chStmt.next()) {
            transactionDetails.add(new TransactionData(chStmt.getString(TXD_COMMENT), chStmt.getTimestamp("time"),
                  chStmt.getInt("author"), chStmt.getInt("transaction_id"), -1, branch, chStmt.getInt("commit_art_id")));
         }
      } finally {
         chStmt.close();
      }
      return transactionDetails;
   }

   /**
    * Return the transaction(s) that a commitArtifact authored
    */
   // TODO need to specify what method replaces this
   @Deprecated
   public Set<Integer> getTransactionDataPerCommitArtifact(Artifact commitArtifact) throws OseeDataStoreException {
      checkCommitArtifactToTransactionCache();

      if (commitArtifact != null && commitArtifactIdToTransactionId.containsKey(commitArtifact.getArtId())) {
         return commitArtifactIdToTransactionId.get(commitArtifact.getArtId());
      }

      return new HashSet<Integer>();
   }

   public void cacheTransactionDataPerCommitArtifact(Artifact commitArtifact, int transactionData) throws OseeDataStoreException {
      cacheTransactionDataPerCommitArtifact(commitArtifact.getArtId(), transactionData);
   }

   private void checkCommitArtifactToTransactionCache() throws OseeDataStoreException {
      if (commitArtifactIdToTransactionId == null) {
         commitArtifactIdToTransactionId = new HashMap<Integer, Set<Integer>>();

         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(SELECT_COMMIT_ART_TRANSACTIONS);
            while (chStmt.next()) {
               int commitArtId = chStmt.getInt("commit_art_id");
               cacheTransactionDataPerCommitArtifact(commitArtId, chStmt.getInt("transaction_id"));
            }
         } finally {
            chStmt.close();
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
                     OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
                  }
               }
            }
         });
      }
   }

   public void cacheTransactionDataPerCommitArtifact(int commitArtifactId, int transactionId) throws OseeDataStoreException {
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
   public Collection<TransactionData> getTransactionsPerArtifact(Artifact artifact) throws OseeCoreException {
      return getTransactionsPerArtifact(artifact, false);
   }

   /**
    * Returns the transactions associated with an artifact
    * 
    * @param artifact
    * @param includeAncestry - indicate whether or not history from ancestor branches should be included
    * @return - Collection<TransactionData>
    */
   public Collection<TransactionData> getTransactionsPerArtifact(Artifact artifact, boolean includeAncestry) throws OseeCoreException {
      List<TransactionData> transactionDetails = new LinkedList<TransactionData>();

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      final Integer artId = artifact.getArtId();
      Branch branch = artifact.getBranch();
      Integer limit = Integer.MAX_VALUE;

      while (branch != null) {
         try {
            int branchId = branch.getBranchId();
            chStmt.runPreparedQuery(SELECT_TRANSACTIONS_FOR_ARTIFACT, artId, branchId, artId, branchId, artId,
                  branchId, limit);

            while (chStmt.next()) {
               transactionDetails.add(new TransactionData(chStmt.getString(TXD_COMMENT), chStmt.getTimestamp("time"),
                     chStmt.getInt("author"), chStmt.getInt("transaction_id"), artId, branch,
                     chStmt.getInt("commit_art_id")));
            }
         } finally {
            chStmt.close();
         }

         if (includeAncestry && branch.hasParentBranch()) {
            branch = branch.getParentBranch();
            limit = transactionDetails.get(transactionDetails.size() - 1).getTransactionNumber();
         } else {
            branch = null;
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
    * @throws BranchDoesNotExist
    * @throws ArtifactDoesNotExist
    * @throws TransactionDoesNotExist
    */
   public Collection<RevisionChange> getTransactionChanges(TransactionData tData) throws OseeCoreException {
      IArtifactNameDescriptorResolver resolver = new ArtifactNameDescriptorResolver(tData.getBranch());

      return getTransactionChanges(OUTGOING, tData.getTransactionId(), tData.getTransactionId(),
            tData.getAssociatedArtId(), resolver);
   }

   /**
    * Returns transaction details by creating a union between the attribute table and the rel link table joined by the
    * TransactionData id
    * 
    * @return - Collection<RevisionChange>
    */
   public Collection<RevisionChange> getTransactionChanges(ArtifactChange artChange, IArtifactNameDescriptorResolver artifactNameDescriptorCache) throws OseeCoreException {
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

   public Collection<RevisionChange> getTransactionChanges(ChangeType changeType, TransactionId fromTransactionId, TransactionId toTransactionId, int artId, IArtifactNameDescriptorResolver artifactNameDescriptorResolver) throws OseeCoreException {
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

   @Deprecated
   private Collection<AttributeChange> getAttributeChanges(ChangeType changeType, int fromTransactionNumber, int toTransactionNumber, int artId) {

      Collection<AttributeChange> revisions = new LinkedList<AttributeChange>();
      String sql =
            "SELECT data_table.*, (SELECT " + SkynetDatabase.ATTRIBUTE_VERSION_TABLE + ".VALUE " + "FROM " + SkynetDatabase.ATTRIBUTE_VERSION_TABLE + " " + "WHERE gamma_id = data_table.was_gamma) as was_value, " + "(SELECT " + SkynetDatabase.ATTRIBUTE_VERSION_TABLE + ".uri " + "FROM " + SkynetDatabase.ATTRIBUTE_VERSION_TABLE + " " + "WHERE gamma_id = data_table.was_gamma) as was_content FROM (SELECT attr1.gamma_id, attr1.value as is_value, attr1.uri as is_content, attr1.modification_id, attr1.attr_id," + ATTRIBUTE_TYPE_TABLE.column("name") + "," + "(SELECT MAX(attr2.gamma_id) " + "FROM " + SkynetDatabase.ATTRIBUTE_VERSION_TABLE + " attr2, " + SkynetDatabase.TRANSACTIONS_TABLE + " t3, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t4 " + "WHERE attr1.attr_id = attr2.attr_id and attr2.gamma_id = t3.gamma_id and t3.transaction_id = t4.transaction_id and t1.branch_id = t4.branch_id AND attr2.gamma_id < attr1.gamma_id) as was_gamma FROM " + ATTRIBUTE_VERSION_TABLE + " attr1," + ATTRIBUTE_TYPE_TABLE + "," + TRANSACTIONS_TABLE + ", " + TRANSACTION_DETAIL_TABLE + "," + " (SELECT branch_id FROM " + TRANSACTION_DETAIL_TABLE + " WHERE transaction_id=?) T1" + " WHERE attr1.gamma_id = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND attr1.attr_type_id=" + ATTRIBUTE_TYPE_TABLE.column("attr_type_id") + " AND " + (fromTransactionNumber == toTransactionNumber ? TRANSACTIONS_TABLE.column("transaction_id") + " = ?" : TRANSACTIONS_TABLE.column("transaction_id") + " > ? " + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + " <= ?") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=T1.branch_id" + " AND art_id = ?) data_table" + " ORDER BY gamma_id DESC";

      Collection<Object> dataList = new LinkedList<Object>();
      dataList.add(fromTransactionNumber);
      if (fromTransactionNumber != toTransactionNumber) {
         dataList.add(fromTransactionNumber);
      }
      dataList.add(toTransactionNumber);
      dataList.add(artId);

      try {
         Query.acquireCollection(revisions, new AttributeChangeProcessor(changeType), sql, dataList.toArray());
      } catch (OseeDataStoreException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }

      return revisions;
   }

   @Deprecated
   private Collection<RelationLinkChange> getRelationLinkChanges(ChangeType changeType, int fromTransactionNumber, int toTransactionNumber, int artId, IArtifactNameDescriptorResolver artifactNameDescriptorResolver) {

      String transactionCheck =
            fromTransactionNumber == toTransactionNumber ? TRANSACTIONS_TABLE.column("transaction_id") + " = ?" : TRANSACTIONS_TABLE.column("transaction_id") + " > ? " + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + " <= ?";

      Collection<RelationLinkChange> revisions = new LinkedList<RelationLinkChange>();
      String sql =
            "SELECT gamma_id, rationale, modification_id, art_id, rel_link_id, order_val, type_name, side_name FROM (SELECT " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + "," + RELATION_LINK_VERSION_TABLE.column("rationale") + "," + RELATION_LINK_VERSION_TABLE.column("modification_id") + "," + RELATION_LINK_VERSION_TABLE.column("a_art_id") + " AS art_id, " + RELATION_LINK_VERSION_TABLE.column("rel_link_id") + "," + RELATION_LINK_VERSION_TABLE.column("a_order") + " AS order_val, " + RELATION_LINK_TYPE_TABLE.column("type_name") + "," + RELATION_LINK_TYPE_TABLE.column("a_name") + " AS side_name" + " FROM " + RELATION_LINK_VERSION_TABLE + "," + RELATION_LINK_TYPE_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + ",(SELECT branch_id FROM " + TRANSACTION_DETAIL_TABLE + " WHERE transaction_id=?) T2 WHERE " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + transactionCheck + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=T2.branch_id" + " AND " + RELATION_LINK_VERSION_TABLE.column("rel_link_type_id") + "=" + RELATION_LINK_TYPE_TABLE.column("rel_link_type_id") + " AND b_art_id = ?) aliasForSyntax UNION ALL " + "(SELECT " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + "," + RELATION_LINK_VERSION_TABLE.column("rationale") + "," + RELATION_LINK_VERSION_TABLE.column("modification_id") + "," + RELATION_LINK_VERSION_TABLE.column("b_art_id") + " AS art_id, " + RELATION_LINK_VERSION_TABLE.column("rel_link_id") + "," + RELATION_LINK_VERSION_TABLE.column("b_order") + " AS order_val, " + RELATION_LINK_TYPE_TABLE.column("type_name") + "," + RELATION_LINK_TYPE_TABLE.column("b_name") + " AS side_name" + " FROM " + RELATION_LINK_VERSION_TABLE + "," + RELATION_LINK_TYPE_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + ",(SELECT branch_id FROM " + TRANSACTION_DETAIL_TABLE + " WHERE transaction_id=?) T2 WHERE " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + transactionCheck + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=T2.branch_id" + " AND " + RELATION_LINK_VERSION_TABLE.column("rel_link_type_id") + "=" + RELATION_LINK_TYPE_TABLE.column("rel_link_type_id") + " AND a_art_id = ?)" + " ORDER BY gamma_id DESC";

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

      } catch (OseeDataStoreException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }

      return revisions;
   }

   /**
    * Returns the name of an artifact
    * 
    * @param artId
    * @throws OseeDataStoreException
    */
   public String getName(int artId) throws OseeDataStoreException {
      String name = bemsToName.get(artId);

      if (name == null) {
         String sql =
               "SELECT " + ATTRIBUTE_VERSION_TABLE.columns("value") + " FROM " + ATTRIBUTE_VERSION_TABLE + ", " + ATTRIBUTE_TYPE_TABLE + " WHERE art_id = ? AND " + ATTRIBUTE_TYPE_TABLE.column("attr_type_id") + " = " + ATTRIBUTE_VERSION_TABLE.column("attr_type_id") + " AND " + ATTRIBUTE_TYPE_TABLE.column("name") + " = 'Name'";

         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(sql, artId);

            if (chStmt.next()) name = chStmt.getString("value");
         } finally {
            chStmt.close();
         }
         bemsToName.put(artId, name);
      }
      return name;
   }

   private Collection<ArtifactChange> getArtifactChanges(ChangeType changeType, TransactionId fromTransactionId, TransactionId toTransactionId, int artId) throws OseeCoreException {
      Collection<ArtifactChange> changes = new LinkedList<ArtifactChange>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         String sql = fromTransactionId != toTransactionId ? GET_CHANGED_ARTIFACTS + " AND txd4.transaction_id > ? AND txd4.transaction_id <= ?" : GET_CHANGED_ARTIFACTS + " AND txd4.transaction_id = ? AND txd4.transaction_id = ?";
         
         chStmt.runPreparedQuery(sql, artId, fromTransactionId.getBranchId(), fromTransactionId.getTransactionNumber(),
               toTransactionId.getTransactionNumber());

         Artifact artifact = ArtifactQuery.getArtifactFromId(artId, fromTransactionId.getBranch(), true);

         while (chStmt.next()) {
            changes.add(new ArtifactChange(changeType, ModificationType.getMod(chStmt.getInt("mod_type")), artifact,
                  null, null, null, toTransactionId, fromTransactionId, chStmt.getInt("gamma_id")));
         }
      } finally {
         chStmt.close();
      }
      return changes;
   }

   public Collection<ArtifactChange> getDeletedArtifactChanges(TransactionId transactionId) throws OseeCoreException {
      return getDeletedArtifactChanges(null, null, TransactionIdManager.getPriorTransaction(transactionId),
            transactionId, null);
   }

   @Deprecated
   public Collection<ArtifactChange> getDeletedArtifactChanges(TransactionId baseParentTransactionId, TransactionId headParentTransactionId, TransactionId fromTransactionId, TransactionId toTransactionId, ArtifactNameDescriptorCache artifactNameDescriptorCache) {
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

      try {
         Query.acquireCollection(deletedArtifacts, new ArtifactChangeProcessor(baseParentTransactionId,
               headParentTransactionId, artifactNameDescriptorCache), GET_DELETED_ARTIFACTS, "Name",
               fromTransactionId.getBranchId(), fromTransactionId.getTransactionNumber(),
               toTransactionId.getTransactionNumber(), ModificationType.DELETED.getValue());
      } catch (OseeDataStoreException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }

      return deletedArtifacts;
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

      /**
       * This constructor is to be used for processing deleted artifact change data, and is appropriate for all
       * transaction ranges since from/toTransactionId information is not stored on deleted changes.
       */
      public ArtifactChangeProcessor(TransactionId baseParentTransactionId, TransactionId headParentTransactionId, ArtifactNameDescriptorCache artifactNameDescriptorCache) {
         this.baseParentTransactionId = baseParentTransactionId;
         this.headParentTransactionId = headParentTransactionId;
         this.artifactNameDescriptorCache = artifactNameDescriptorCache;
      }

      public ArtifactChange process(ConnectionHandlerStatement chStmt) throws OseeDataStoreException {
         try {
            ModificationType modType = ModificationType.getMod(chStmt.getInt("modification_id"));

            TransactionId transactionId = TransactionIdManager.getTransactionId(chStmt.getInt("deleted_transaction"));

            int artId = chStmt.getInt("art_id");
            Artifact artifact = ArtifactPersistenceManager.getInstance().getArtifactFromId(artId, transactionId);

            if (artifactNameDescriptorCache != null) artifactNameDescriptorCache.cache(artId,
                  artifact.getDescriptiveName(), artifact.getArtifactType());

            return new ArtifactChange(OUTGOING, modType, artifact, baseParentTransactionId, headParentTransactionId,
                  fromTransactionId, fromTransactionId, transactionId, chStmt.getInt("gamma_id"));
         } catch (OseeCoreException ex) {
            throw new OseeDataStoreException(ex);
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

      public AttributeChange process(ConnectionHandlerStatement chStmt) throws OseeDataStoreException {
         ModificationType modType = ModificationType.getMod(chStmt.getInt("modification_id"));
         if (modType == ModificationType.DELETED) {
            String wasValue = chStmt.getString("was_value");
            return new AttributeChange(changeType, chStmt.getInt("attr_id"), chStmt.getLong("gamma_id"),
                  chStmt.getString("name"), wasValue == null ? "" : wasValue);
         } else {
            String isValue = chStmt.getString("is_value");
            String wasValue = chStmt.getString("was_value");
            return new AttributeChange(changeType, modType, chStmt.getInt("attr_id"), chStmt.getLong("gamma_id"),
                  chStmt.getString("name"), isValue == null ? "" : isValue, chStmt.getBinaryStream("is_content"),
                  wasValue == null ? "" : wasValue, chStmt.getBinaryStream("was_content"));
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

      public RelationLinkChange process(ConnectionHandlerStatement chStmt) throws OseeDataStoreException {

         ModificationType modType = ModificationType.getMod(chStmt.getInt("modification_id"));

         Pair<String, ArtifactType> artifactData;
         if (artifactNameDescriptorResolver != null)
            artifactData = artifactNameDescriptorResolver.get(chStmt.getInt("art_id"));
         else
            artifactData = UNKNOWN_DATA;

         String relName = chStmt.getString("type_name") + " (" + chStmt.getString("side_name") + ")";
         if (modType == ModificationType.DELETED) {
            return new RelationLinkChange(changeType, chStmt.getInt("rel_link_id"), chStmt.getLong("gamma_id"),
                  relName, artifactData.getKey(), artifactData.getValue());
         } else {
            return new RelationLinkChange(changeType, modType, chStmt.getInt("rel_link_id"),
                  chStmt.getLong("gamma_id"), chStmt.getString("rationale"), chStmt.getInt("order_val"), relName,
                  artifactData.getKey(), artifactData.getValue());
         }
      }

      public boolean validate(RelationLinkChange item) {
         return item != null;
      }
   }

   public boolean branchHasChanges(Branch branch) throws OseeCoreException {
      Pair<TransactionId, TransactionId> transactions = TransactionIdManager.getStartEndPoint(branch);
      return transactions.getKey() != transactions.getValue();
   }

   private static final String OTHER_EDIT_SQL =
         "SELECT distinct(t3.branch_id) " + "FROM " + SkynetDatabase.ARTIFACT_VERSION_TABLE + " t1, " + "  " + SkynetDatabase.TRANSACTIONS_TABLE + " t2, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t3, (SELECT min(transaction_id) as min_tx_id, branch_id " + "   FROM " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " " + "   GROUP BY branch_id) t4, " + "   osee_branch t5 " + "WHERE t1.art_id = ? " + " AND t1.gamma_id = t2.gamma_id " + " AND t2.transaction_id <> t4.min_tx_id " + " AND t2.transaction_id = t3.transaction_id " + " and t3.branch_id = t4.branch_id " + " and t4.branch_id <> ?" + " and t5.parent_branch_id = ?" + " and t4.branch_id = t5.branch_id" + " and t5.archived = 0";

   /**
    * Returns all the other branches this artifact has been editted on, besides modifications to program branch.
    * 
    * @param artifact
    */
   public Collection<Branch> getOtherEdittedBranches(Artifact artifact) {
      Collection<Branch> otherBranches = new LinkedList<Branch>();

      // Can only be on other branches it has already been saved
      if (artifact.isInDb()) {
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

         try {
            chStmt.runPreparedQuery(OTHER_EDIT_SQL, artifact.getArtId(), artifact.getBranch().getBranchId(),
                  artifact.getBranch().getParentBranchId());

            while (chStmt.next()) {
               otherBranches.add(BranchManager.getBranch(chStmt.getInt("branch_id")));
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
         } finally {
            chStmt.close();
         }
      }
      return otherBranches;
   }

}