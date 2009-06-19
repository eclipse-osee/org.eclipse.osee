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
package org.eclipse.osee.framework.skynet.core.artifact;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.core.schema.LocalAliasTable;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.RelatedToSearch;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class ArtifactPersistenceManager {
   private static final LocalAliasTable ATTRIBUTE_ALIAS_1 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t1");
   private static final LocalAliasTable ATTRIBUTE_ALIAS_2 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t2");

   private static final String PURGE_BASELINE_ATTRIBUTE_TRANS =
         "DELETE from " + TRANSACTIONS_TABLE + " T2 WHERE EXISTS (SELECT 'x' from " + TRANSACTION_DETAIL_TABLE + " T1, " + ATTRIBUTE_VERSION_TABLE + " T3 WHERE T1.transaction_id = T2.transaction_id and T3.gamma_id = T2.gamma_id and T1.tx_type = " + TransactionDetailsType.Baselined.getId() + " and T1.branch_id = ? and T3.art_id = ?)";
   private static final String PURGE_BASELINE_RELATION_TRANS =
         "DELETE from " + TRANSACTIONS_TABLE + " T2 WHERE EXISTS (SELECT 'x' from " + TRANSACTION_DETAIL_TABLE + " T1, " + RELATION_LINK_VERSION_TABLE + " T3 WHERE T1.transaction_id = T2.transaction_id and T3.gamma_id = T2.gamma_id and T1.tx_type = " + TransactionDetailsType.Baselined.getId() + " and T1.branch_id = ? and (T3.a_art_id = ? or T3.b_art_id = ?))";
   private static final String PURGE_BASELINE_ARTIFACT_TRANS =
         "DELETE from " + TRANSACTIONS_TABLE + " T2 WHERE EXISTS (SELECT 'x' from " + TRANSACTION_DETAIL_TABLE + " T1, " + ARTIFACT_VERSION_TABLE + " T3 WHERE T1.transaction_id = T2.transaction_id and T3.gamma_id = T2.gamma_id and T1.tx_type = " + TransactionDetailsType.Baselined.getId() + " and T1.branch_id = ? and T3.art_id = ?)";

   private static final String GET_GAMMAS_ARTIFACT_REVERT =
         "SELECT txs1.gamma_id, txd1.tx_type, txs1.transaction_id  FROM osee_tx_details txd1, osee_txs  txs1, osee_attribute atr1 where txd1.transaction_id = txs1.transaction_id and txs1.gamma_id = atr1.gamma_id and txd1.branch_id = ? and atr1.art_id = ? UNION ALL SELECT txs2.gamma_id, txd2.tx_type, txs2.transaction_id FROM osee_tx_details txd2, osee_txs txs2, osee_relation_link rel2 where txd2.transaction_id = txs2.transaction_id and txs2.gamma_id = rel2.gamma_id and txd2.branch_id = ? and (rel2.a_art_id = ? or rel2.b_art_id = ?) UNION ALL SELECT txs3.gamma_id, txd3.tx_type, txs3.transaction_id FROM osee_tx_details txd3, osee_txs txs3, osee_artifact_version art3 where txd3.transaction_id = txs3.transaction_id and txs3.gamma_id = art3.gamma_id and txd3.branch_id = ? and art3.art_id = ?";

   private static final String GET_GAMMAS_RELATION_REVERT =
         "SELECT txs2.gamma_id, txd2.tx_type, txs2.transaction_id FROM osee_tx_details txd2, osee_txs txs2, osee_relation_link rel2 where txd2.transaction_id = txs2.transaction_id and txs2.gamma_id = rel2.gamma_id and txd2.branch_id = ? and rel2.rel_link_id = ?";

   private static final String GET_GAMMAS_ATTRIBUTE_REVERT =
         "SELECT txs2.gamma_id, txd2.tx_type, txs2.transaction_id FROM osee_tx_details txd2, osee_txs txs2, osee_attribute atr2 where txd2.transaction_id = txs2.transaction_id and txs2.gamma_id = atr2.gamma_id and txd2.branch_id = ? and atr2.attr_id = ?";

   private static final String PURGE_ATTRIBUTE = "DELETE FROM osee_attribute WHERE attr_id = ?";
   private static final String PURGE_ATTRIBUTE_GAMMAS =
         "DELETE" + " FROM " + TRANSACTIONS_TABLE + " WHERE gamma_id IN" + "(SELECT gamma_id" + " FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE attr_id = ?)";

   private static final String SELECT_ATTRIBUTES_FOR_ARTIFACT =
         "SELECT " + ATTRIBUTE_ALIAS_1.columns("attr_id", "attr_type_id", "gamma_id", "value", "uri") + " FROM " + ATTRIBUTE_ALIAS_1 + "," + TRANSACTIONS_TABLE + " WHERE " + ATTRIBUTE_ALIAS_1.column("art_id") + "=?" + " AND " + ATTRIBUTE_ALIAS_1.column("modification_id") + "<> ?" + " AND " + ATTRIBUTE_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT MAX(" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ")" + " FROM " + ATTRIBUTE_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ATTRIBUTE_ALIAS_2.column("attr_id") + "=" + ATTRIBUTE_ALIAS_1.column("attr_id") + " AND " + ATTRIBUTE_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " <= ?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   private static final String UPDATE_ARTIFACT_TYPE = "UPDATE osee_artifact SET art_type_id = ? WHERE art_id =?";

   private static final String SELECT_ARTIFACT_START =
         "SELECT art1.*, txs1.*, txd1.* FROM osee_artifact art1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE ";
   private static final String SELECT_ARTIFACT_END =
         " AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= ? AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? order by txs1.transaction_id desc";
   private static final String SELECT_ARTIFACT_BY_GUID = SELECT_ARTIFACT_START + "art1.guid =?" + SELECT_ARTIFACT_END;
   private static final String SELECT_ARTIFACT_BY_ID = SELECT_ARTIFACT_START + "art1.art_id =?" + SELECT_ARTIFACT_END;

   private static final String ARTIFACT_SELECT =
         "SELECT osee_artifact.art_id, txd1.branch_id FROM osee_artifact, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE " + ARTIFACT_TABLE.column("art_id") + "=arv1.art_id AND arv1.gamma_id=txs1.gamma_id AND txs1.tx_current=" + TxChange.CURRENT.getValue() + " AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id=? AND ";

   private static final String ARTIFACT_ID_SELECT =
         "SELECT " + ARTIFACT_TABLE.columns("art_id") + " FROM " + ARTIFACT_TABLE + " WHERE ";

   private static final String ARTIFACT_NEW_ON_BRANCH =
         "Select det.tx_type from osee_tx_details det, osee_txs txs, osee_artifact_version art WHERE det.branch_id = ? AND det.tx_type = 1 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id AND art.art_id = ?";
   private static final String ATTRIBUTE_NEW_ON_BRANCH =
         "Select det.tx_type from osee_tx_details det, osee_txs txs, osee_attribute atr WHERE det.branch_id = ? AND det.tx_type = 1 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = atr.gamma_id AND atr.attr_id = ?";
   private static final String RELATION_NEW_ON_BRANCH =
         "Select det.tx_type from osee_tx_details det, osee_txs txs, osee_relation_link rel WHERE det.branch_id = ? AND det.tx_type = 1 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id AND rel.rel_link_id = ?";

   private static final ArtifactPersistenceManager instance = new ArtifactPersistenceManager();

   public static boolean initFinished = false;

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Persistence"));

   private ArtifactPersistenceManager() {
   }

   public static ArtifactPersistenceManager getInstance() {
      return instance;
   }

   /**
    * This method acquires <code>Artifact</code>'s directly from the database. This should only be called by factories
    * since all caching is performed by the factory.
    * 
    * @param guid The guid of the artifact.
    * @return The <code>Artifact</code> from the database that corresponds to the supplied guid. call the method below
    *         directly instead
    * @see ArtifactQuery#getHistoricalArtifactFromId(String, TransactionId, boolean)
    */
   @Deprecated
   public Artifact getArtifact(String guid, TransactionId transactionId) throws OseeCoreException {
      return ArtifactQuery.getHistoricalArtifactFromId(guid, transactionId, true);
   }

   public Artifact getArtifactFromId(int artId, TransactionId transactionId) throws OseeCoreException {
      return getArtifactInternal(transactionId, SELECT_ARTIFACT_BY_ID, null, artId, false);
   }

   @Deprecated
   private Artifact getArtifactInternal(TransactionId transactionLimit, String query, String guid, int artId, boolean useGuid) throws OseeCoreException {
      // First try to acquire the artifact from cache
      Artifact artifact;
      Object data;
      String idString;
      if (useGuid) {
         artifact = ArtifactCache.getHistorical(guid, transactionLimit.getTransactionNumber());
         data = guid;
         idString = "guid \"" + guid + "\"";
      } else {
         artifact = ArtifactCache.getHistorical(artId, transactionLimit.getTransactionNumber());
         data = artId;
         idString = "id \"" + artId + "\"";
      }

      // If it wasn't found, then it must be acquired from the database
      if (artifact == null) {
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(1, query, data, transactionLimit.getTransactionNumber(),
                  transactionLimit.getBranchId());

            if (!chStmt.next()) {
               throw new OseeStateException(
                     "The artifact with " + idString + " does not exist for transaction \"" + transactionLimit + "\"");
            }

            ArtifactType artifactType = ArtifactTypeManager.getType(chStmt.getInt("art_type_id"));
            ArtifactFactory factory = artifactType.getFactory();
            TransactionId transactionId = TransactionIdManager.getTransactionId(chStmt);

            artifact =
                  factory.loadExisitingArtifact(chStmt.getInt("art_id"), chStmt.getString("guid"),
                        chStmt.getString("human_readable_id"), artifactType, chStmt.getInt("gamma_id"), transactionId,
                        ModificationType.getMod(chStmt.getInt("mod_type")), true);

            setAttributesOnHistoricalArtifact(artifact);

            artifact.onInitializationComplete();
         } finally {
            chStmt.close();
         }
      }

      return artifact;
   }

   public static CharSequence getSelectArtIdSql(ISearchPrimitive searchCriteria, List<Object> dataList, Branch branch) {
      return getSelectArtIdSql(searchCriteria, dataList, null, branch);
   }

   public static CharSequence getSelectArtIdSql(ISearchPrimitive searchCriteria, List<Object> dataList, String alias, Branch branch) {
      StringBuilder sql = new StringBuilder();

      sql.append("SELECT ");
      sql.append(searchCriteria.getArtIdColName());

      if (alias != null) {
         sql.append(" AS " + alias);
      }

      sql.append(" FROM ");
      sql.append(searchCriteria.getTableSql(dataList, branch));

      String criteriaSql = searchCriteria.getCriteriaSql(dataList, branch);
      if (criteriaSql.trim().length() != 0) {
         sql.append(" WHERE (");
         sql.append(criteriaSql);
         sql.append(")");
      }

      return sql;
   }

   public static String getIdSql(List<ISearchPrimitive> searchCriteria, boolean all, List<Object> dataList, Branch branch) {
      return getSql(searchCriteria, all, ARTIFACT_ID_SELECT, dataList, branch);
   }

   private static String getSql(ISearchPrimitive searchCriteria, String header, List<Object> dataList, Branch branch) {
      StringBuilder sql = new StringBuilder(header);

      sql.append(ARTIFACT_TABLE.column("art_id") + " in (");
      sql.append(getSelectArtIdSql(searchCriteria, dataList, branch));
      sql.append(")");

      return sql.toString();
   }

   private static String getSql(List<ISearchPrimitive> searchCriteria, boolean all, String header, List<Object> dataList, Branch branch) {
      StringBuilder sql = new StringBuilder(header);

      if (all) {
         ISearchPrimitive primitive = null;
         Iterator<ISearchPrimitive> iter = searchCriteria.iterator();

         while (iter.hasNext()) {
            primitive = iter.next();
            sql.append(ARTIFACT_TABLE.column("art_id") + " in (");
            sql.append(getSelectArtIdSql(primitive, dataList, branch));

            if (iter.hasNext()) {
               sql.append(") AND ");
            }
         }
         sql.append(")");
      } else {
         ISearchPrimitive primitive = null;
         Iterator<ISearchPrimitive> iter = searchCriteria.iterator();

         sql.append(ARTIFACT_TABLE.column("art_id") + " IN(SELECT art_id FROM " + ARTIFACT_TABLE + ", (");

         while (iter.hasNext()) {
            primitive = iter.next();
            sql.append(getSelectArtIdSql(primitive, dataList, "desired_art_id", branch));
            if (iter.hasNext()) sql.append(" UNION ALL ");
         }
         sql.append(") ORD_ARTS");
         sql.append(" WHERE art_id = ORD_ARTS.desired_art_id");

         sql.append(")");
      }

      return sql.toString();
   }

   @Deprecated
   public Collection<Artifact> getArtifacts(ISearchPrimitive searchCriteria, Branch branch) throws OseeCoreException {
      LinkedList<Object> queryParameters = new LinkedList<Object>();
      queryParameters.add(branch.getBranchId());
      return ArtifactLoader.getArtifacts(getSql(searchCriteria, ARTIFACT_SELECT, queryParameters, branch),
            queryParameters.toArray(), 100, ArtifactLoad.FULL, false, null, null, false);
   }

   @Deprecated
   public Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, Branch branch, ISearchConfirmer confirmer) throws OseeCoreException {
      LinkedList<Object> queryParameters = new LinkedList<Object>();
      queryParameters.add(branch.getBranchId());
      return ArtifactLoader.getArtifacts(getSql(searchCriteria, all, ARTIFACT_SELECT, queryParameters, branch),
            queryParameters.toArray(), 100, ArtifactLoad.FULL, false, confirmer, null, false);
   }

   @Deprecated
   public static Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, Branch branch) throws OseeCoreException {
      return instance.getArtifacts(searchCriteria, all, branch, null);
   }

   /**
    * Acquires the user defined attributes for an artifact. If none are in the table, then it returns the 'default set'
    * of attributes for the artifact.
    * 
    * @param artifact The artifact to acquire the attributes for.
    * @param branch The tag to get the data for.
    * @throws OseeDataStoreException
    * @throws OseeCoreException
    */
   private static void setAttributesOnHistoricalArtifact(Artifact artifact) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         // Acquire previously stored attributes
         chStmt.runPreparedQuery(SELECT_ATTRIBUTES_FOR_ARTIFACT, artifact.getArtId(),
               ModificationType.DELETED.getValue(), artifact.getTransactionNumber(), artifact.getBranch().getBranchId());

         while (chStmt.next()) {
            Attribute.initializeAttribute(artifact, chStmt.getInt("attr_type_id"), chStmt.getInt("attr_id"),
                  chStmt.getInt("gamma_id"), chStmt.getString("value"), chStmt.getString("uri"));
         }
      } finally {
         chStmt.close();
      }

      artifact.meetMinimumAttributeCounts(false);
   }

   /**
    * This method returns 1000 of the ids, removing them from the collection
    * 
    * @param artifacts
    */
   public String getArtIdList(final ArrayList<Artifact> artifacts) {
      StringBuilder artIdList = new StringBuilder();
      int count = -1;
      while (!artifacts.isEmpty() && count++ < 999) {
         if (count > 0) artIdList.append(",");
         artIdList.append(artifacts.remove(0).getArtId());
      }

      return artIdList.toString();
   }

   /**
    * @param transaction if the transaction is null then persist is not called otherwise
    * @param overrideDeleteCheck if <b>true</b> deletes without checking preconditions
    * @param artifacts The artifacts to delete.
    */
   public static void deleteArtifact(SkynetTransaction transaction, boolean overrideDeleteCheck, final Artifact... artifacts) throws OseeCoreException {
      if (artifacts.length == 0) return;

      if (!overrideDeleteCheck) {
         // Confirm artifacts are fit to delete
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            IStatus result = check.isDeleteable(Arrays.asList(artifacts));
            if (!result.isOK()) {
               throw new OseeStateException(result.getMessage());
            }
         }
      }
      //Bulk Load Artifacts
      Collection<Integer> artIds = new LinkedList<Integer>();
      for (Artifact artifact : artifacts) {
         for (RelationLink link : artifact.getRelationsAll(false)) {
            if (link.getRelationType().isOrdered()) {
               artIds.add(artifact.getArtId() == link.getAArtifactId() ? link.getBArtifactId() : link.getAArtifactId());
            }
         }
      }
      Branch branch = artifacts[0].getBranch();
      ArtifactQuery.getArtifactsFromIds(artIds, branch, true);

      for (Artifact artifact : artifacts) {
         deleteTrace(artifact, transaction, true);
      }
   }

   /**
    * @param artifact
    * @param builder
    * @param reorderReloations
    * @throws Exception
    */
   private static void deleteTrace(Artifact artifact, SkynetTransaction transaction, boolean reorderRelations) throws OseeCoreException {
      if (!artifact.isDeleted()) {
         // This must be done first since the the actual deletion of an artifact clears out the link manager
         for (Artifact childArtifact : artifact.getChildren()) {
            deleteTrace(childArtifact, transaction, false);
         }
         try {
            artifact.setDeleted();
            RelationManager.deleteRelationsAll(artifact, reorderRelations);
            if (transaction != null) {
               artifact.persistAttributesAndRelations(transaction);
            }
         } catch (OseeCoreException ex) {
            artifact.resetToPreviousModType();
            throw ex;
         }
      }
   }

   /**
    * Removes an artifact, it's attributes and any relations that have become invalid from the removal of this artifact
    * from the database. It also removes all history associated with this artifact (i.e. all transactions and gamma ids
    * will also be removed from the database only for the branch it is on).
    * 
    * @param artifact
    */
   public static void purgeArtifactFromBranch(OseeConnection connection, int branchId, int artId) throws OseeCoreException {
      revertArtifact(connection, branchId, artId);

      //Remove from Baseline
      ConnectionHandler.runPreparedUpdate(connection, PURGE_BASELINE_ATTRIBUTE_TRANS, branchId, artId);
      ConnectionHandler.runPreparedUpdate(connection, PURGE_BASELINE_RELATION_TRANS, branchId, artId, artId);
      ConnectionHandler.runPreparedUpdate(connection, PURGE_BASELINE_ARTIFACT_TRANS, branchId, artId);
   }

   public static void revertAttribute(OseeConnection connection, Attribute<?> attribute) throws OseeCoreException {
      if (attribute == null) return;
      revertAttribute(connection, attribute.getArtifact().getBranch().getBranchId(),
            attribute.getArtifact().getArtId(), attribute.getAttrId());
   }

   public static void revertAttribute(OseeConnection connection, int branchId, int artId, int attributeId) throws OseeCoreException {
      TransactionId transId =
            TransactionIdManager.createNextTransactionId(BranchManager.getBranch(branchId), UserManager.getUser(), "");
      long totalTime = System.currentTimeMillis();
      //Get attribute Gammas
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      RevertAction revertAction = null;
      try {
         chStmt.runPreparedQuery(GET_GAMMAS_ATTRIBUTE_REVERT, branchId, attributeId);
         revertAction = new RevertAction(connection, chStmt, transId);
         revertAction.revertObject(totalTime, artId, "Attribute");
      } finally {
         chStmt.close();
      }
      revertAction.fixArtifactVersionForAttributeRevert(branchId, artId);
   }

   /**
    * Should NOT be used for relation types that maintain order. Not handled yet
    */
   public static void revertRelationLink(OseeConnection connection, RelationLink link) throws OseeCoreException {
      //Only reverts relation links that don't span multiple branches.  Need to revisit if additional functionality is needed.
      if (!link.getArtifactA().getBranch().equals(link.getArtifactB().getBranch())) {
         throw new OseeArgumentException(String.format("Can not revert Relation %d. Relation spans multiple branches",
               link.getRelationId()));
      }
      revertRelationLink(connection, link.getArtifactA().getBranch().getBranchId(), link.getRelationId(),
            link.getArtifactA().getArtId(), link.getArtifactB().getArtId());
   }

   private static void revertRelationLink(OseeConnection connection, int branchId, int relLinkId, int aArtId, int bArtId) throws BranchDoesNotExist, OseeCoreException {
      long time = System.currentTimeMillis();
      long totalTime = time;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);

      TransactionId transId =
            TransactionIdManager.createNextTransactionId(BranchManager.getBranch(branchId), UserManager.getUser(), "");

      try {
         chStmt.runPreparedQuery(GET_GAMMAS_RELATION_REVERT, branchId, relLinkId);
         new RevertAction(connection, chStmt, transId).revertObject(totalTime, relLinkId, "Relation Link");
      } finally {
         chStmt.close();
      }
   }

   public static void revertArtifact(OseeConnection connection, Artifact artifact) throws OseeCoreException {
      if (artifact == null) return;
      revertArtifact(connection, artifact.getBranch().getBranchId(), artifact.getArtId());
   }

   public static void revertArtifact(OseeConnection connection, int branchId, int artId) throws OseeCoreException {
      TransactionId transId =
            TransactionIdManager.createNextTransactionId(BranchManager.getBranch(branchId), UserManager.getUser(), "");
      long totalTime = System.currentTimeMillis();
      //Get attribute Gammas
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      try {
         chStmt.runPreparedQuery(GET_GAMMAS_ARTIFACT_REVERT, branchId, artId, branchId, artId, artId, branchId, artId);
         new RevertAction(connection, chStmt, transId).revertObject(totalTime, artId, "Artifact");
      } finally {
         chStmt.close();
      }
   }

   public void bulkLoadArtifacts(Collection<? extends Artifact> arts, Branch branch) throws OseeCoreException {
      if (arts.size() == 0) return;

      List<ISearchPrimitive> bulkLoad = new LinkedList<ISearchPrimitive>();
      for (Artifact art : arts) {
         bulkLoad.add(new RelatedToSearch(art.getArtId(), true));
         bulkLoad.add(new RelatedToSearch(art.getArtId(), false));
      }
      getArtifacts(bulkLoad, false, branch);
   }

   /**
    * Changes the artifact type
    * 
    * @param artifact
    * @param artifactType
    * @throws OseeDataStoreException
    */
   public static void changeArtifactSubStype(Artifact artifact, ArtifactType artifactType) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(UPDATE_ARTIFACT_TYPE, artifactType.getArtTypeId(), artifact.getArtId());
   }

   /**
    * Purge attribute from the database.
    * 
    * @param attribute
    * @throws OseeDataStoreException
    */
   public static void purgeAttribute(Attribute<?> attribute, int attributeId) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(PURGE_ATTRIBUTE_GAMMAS, attributeId);
      ConnectionHandler.runPreparedUpdate(PURGE_ATTRIBUTE, attributeId);
   }

   public static void purgeArtifacts(Collection<? extends Artifact> artifactsToPurge) throws OseeCoreException {
      new PurgeDbTransaction(artifactsToPurge).execute();
   }

   public static boolean isArtifactNewOnBranch(Artifact artifact) throws OseeDataStoreException {
      return (ConnectionHandler.runPreparedQueryFetchInt(-1, ARTIFACT_NEW_ON_BRANCH,
            artifact.getBranch().getBranchId(), artifact.getArtId()) == -1);
   }

   public static boolean isAttributeNewOnBranch(Attribute<?> attribute) throws OseeDataStoreException {
      return (ConnectionHandler.runPreparedQueryFetchInt(-1, ATTRIBUTE_NEW_ON_BRANCH,
            attribute.getArtifact().getBranch().getBranchId(), attribute.getAttrId()) == -1);
   }

   public static boolean isRelationNewOnBranch(RelationLink relation, Branch branch) throws OseeDataStoreException {
      return (ConnectionHandler.runPreparedQueryFetchInt(-1, RELATION_NEW_ON_BRANCH, branch.getBranchId(),
            relation.getRelationId()) == -1);
   }
}