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
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.OUTGOING;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.RsetProcessor;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.core.schema.Table;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * Manages artifact versions in Skynet
 * 
 * @author Jeff C. Phillips
 */
public class RevisionManager {
   private static final String GET_CHANGED_ARTIFACTS =
         "SELECT arv2.gamma_id, txs1.mod_type FROM osee_artifact ar1, osee_artifact_version arv2, osee_txs txs1, osee_tx_details txd4 WHERE ar1.art_id = ? AND ar1.art_id = arv2.art_id AND arv2.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd4.transaction_id AND txd4.branch_id = ?";

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

   private RevisionManager() {
   }

   /**
    * Returns the transactions associated with an artifact
    * 
    * @param artifact
    * @param includeAncestry - indicate whether or not history from ancestor branches should be included
    * @return - Collection<TransactionData>
    */
   @Deprecated
   public static Collection<TransactionData> getTransactionsPerArtifact(Artifact artifact, boolean includeAncestry) throws OseeCoreException {
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

         if (includeAncestry && branch.hasParentBranch() && transactionDetails.size() > 0) {
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
   @Deprecated
   public static Collection<RevisionChange> getTransactionChanges(TransactionData tData) throws OseeCoreException {
      IArtifactNameDescriptorResolver resolver = new ArtifactNameDescriptorResolver(tData.getBranch());

      return getTransactionChanges(OUTGOING, tData.getTransactionId(), tData.getTransactionId(),
            tData.getAssociatedArtId(), resolver);
   }

   @Deprecated
   private static Collection<RevisionChange> getTransactionChanges(ChangeType changeType, TransactionId fromTransactionId, TransactionId toTransactionId, int artId, IArtifactNameDescriptorResolver artifactNameDescriptorResolver) throws OseeCoreException {
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
   private static Collection<AttributeChange> getAttributeChanges(ChangeType changeType, int fromTransactionNumber, int toTransactionNumber, int artId) {

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
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      return revisions;
   }

   @Deprecated
   private static Collection<RelationLinkChange> getRelationLinkChanges(ChangeType changeType, int fromTransactionNumber, int toTransactionNumber, int artId, IArtifactNameDescriptorResolver artifactNameDescriptorResolver) {

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

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      return revisions;
   }

   @Deprecated
   private static Collection<ArtifactChange> getArtifactChanges(ChangeType changeType, TransactionId fromTransactionId, TransactionId toTransactionId, int artId) throws OseeCoreException {
      Collection<ArtifactChange> changes = new LinkedList<ArtifactChange>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         String sql =
               fromTransactionId != toTransactionId ? GET_CHANGED_ARTIFACTS + " AND txd4.transaction_id > ? AND txd4.transaction_id <= ?" : GET_CHANGED_ARTIFACTS + " AND txd4.transaction_id = ? AND txd4.transaction_id = ?";

         chStmt.runPreparedQuery(sql, artId, fromTransactionId.getBranchId(), fromTransactionId.getTransactionNumber(),
               toTransactionId.getTransactionNumber());

         Artifact artifact = ArtifactQuery.getArtifactFromId(artId, fromTransactionId.getBranch(), true);

         while (chStmt.next()) {
            changes.add(new ArtifactChange(changeType, ModificationType.getMod(chStmt.getInt("mod_type")), artifact,
                  null, null, null, toTransactionId, fromTransactionId, chStmt.getInt("gamma_id"), false));
         }
      } finally {
         chStmt.close();
      }
      return changes;
   }

   /**
    * Produces <code>AttributeChange</code>'s from a ResultSet. <br/>
    * <br/>
    * For deleted attributes, the following columns must be available from the set: <li>gamma_id</li> <li>
    * modification_id</li> <li>name</li> <br/>
    * <br/>
    * For new and modified attributes, the following columns must be available from the set: <li>gamma_id</li> <li>
    * modification_id</li> <li>name</li> <li>value</li>
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

      public AttributeChange process(ConnectionHandlerStatement chStmt) throws OseeDataStoreException, OseeArgumentException {
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
    * Produces <code>RelationLinkChange</code>'s from a ResultSet. <br/>
    * <br/>
    * For deleted links, the following columns must be available from the set: <li>gamma_id</li> <li>modification_id</li>
    * <li>type_name</li> <li>art_id</li> <br/>
    * <br/>
    * For new and modified attributes, the following columns must be available from the set: <li>gamma_id</li> <li>
    * modification_id</li> <li>type_name</li> <li>art_id</li> <li>rationale</li> <li>order_val</li>
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

      public RelationLinkChange process(ConnectionHandlerStatement chStmt) throws OseeDataStoreException, OseeArgumentException {

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

   public static boolean branchHasChanges(Branch branch) throws OseeCoreException {
      Pair<TransactionId, TransactionId> transactions = TransactionIdManager.getStartEndPoint(branch);
      return transactions.getKey() != transactions.getValue();
   }

   private static final String OTHER_EDIT_SQL =
         "select distinct t3.branch_id from osee_artifact_version t1, osee_txs t2, osee_tx_details t3, (select min(transaction_id) as min_tx_id, branch_id from osee_tx_details group by branch_id) t4, osee_branch t5 where t1.art_id = ? and t1.gamma_id = t2.gamma_id and t2.transaction_id <> t4.min_tx_id and t2.transaction_id = t3.transaction_id and t3.branch_id = t4.branch_id and t4.branch_id <> ? and t5.parent_branch_id = ? and t4.branch_id = t5.branch_id and t5.archived = 0";

   /**
    * Returns all the other branches this artifact has been editted on, besides modifications to program branch.
    * 
    * @param artifact
    * @throws OseeDataStoreException
    * @throws BranchDoesNotExist
    */
   public static Collection<Branch> getOtherEdittedBranches(Artifact artifact) throws OseeDataStoreException, BranchDoesNotExist {
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
         } finally {
            chStmt.close();
         }
      }
      return otherBranches;
   }
}