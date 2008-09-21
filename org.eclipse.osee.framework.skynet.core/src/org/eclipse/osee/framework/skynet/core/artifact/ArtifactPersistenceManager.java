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
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.db.connection.core.schema.LocalAliasTable;
import org.eclipse.osee.framework.db.connection.core.transaction.AbstractDbTxTemplate;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.RelatedToSearch;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeToTransactionOperation;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.transaction.ArtifactTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionBuilder;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionDetailsType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class ArtifactPersistenceManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactPersistenceManager.class);

   private static final String INSERT_ARTIFACT =
         "INSERT INTO " + ARTIFACT_TABLE + " (art_id, art_type_id, guid, human_readable_id) VALUES (?, ?, ?, ?)";

   private static final String REMOVE_EMPTY_TRANSACTION_DETAILS =
         "DELETE FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ?" + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " NOT IN " + "(SELECT " + TRANSACTIONS_TABLE.column("transaction_id") + " FROM " + TRANSACTIONS_TABLE + ")";

   private static final LocalAliasTable ATTRIBUTE_ALIAS_1 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t1");
   private static final LocalAliasTable ATTRIBUTE_ALIAS_2 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t2");

   private static final String PURGE_BASELINE_ATTRIBUTE_TRANS =
         "DELETE from " + TRANSACTIONS_TABLE + " T2 WHERE EXISTS (SELECT 'x' from " + TRANSACTION_DETAIL_TABLE + " T1, " + ATTRIBUTE_VERSION_TABLE + " T3 WHERE T1.transaction_id = T2.transaction_id and T3.gamma_id = T2.gamma_id and T1.tx_type = " + TransactionDetailsType.Baselined.getId() + " and T1.branch_id = ? and T3.art_id = ?)";
   private static final String PURGE_BASELINE_RELATION_TRANS =
         "DELETE from " + TRANSACTIONS_TABLE + " T2 WHERE EXISTS (SELECT 'x' from " + TRANSACTION_DETAIL_TABLE + " T1, " + RELATION_LINK_VERSION_TABLE + " T3 WHERE T1.transaction_id = T2.transaction_id and T3.gamma_id = T2.gamma_id and T1.tx_type = " + TransactionDetailsType.Baselined.getId() + " and T1.branch_id = ? and (T3.a_art_id = ? or T3.b_art_id = ?))";
   private static final String PURGE_BASELINE_ARTIFACT_TRANS =
         "DELETE from " + TRANSACTIONS_TABLE + " T2 WHERE EXISTS (SELECT 'x' from " + TRANSACTION_DETAIL_TABLE + " T1, " + ARTIFACT_VERSION_TABLE + " T3 WHERE T1.transaction_id = T2.transaction_id and T3.gamma_id = T2.gamma_id and T1.tx_type = " + TransactionDetailsType.Baselined.getId() + " and T1.branch_id = ? and T3.art_id = ?)";

   private static final String GET_GAMMAS_REVERT =
         "SELECT txs1.gamma_id, txd1.tx_type, txs1.transaction_id  FROM osee_define_tx_details txd1, osee_define_txs  txs1, osee_define_attribute atr1 where  txd1.transaction_id = txs1.transaction_id and txs1.gamma_id = atr1.gamma_id and txd1.branch_id = ? and atr1.art_id = ? UNION ALL SELECT txs2.gamma_id, txd2.tx_type, txs2.transaction_id FROM osee_define_tx_details txd2, osee_define_txs  txs2, osee_define_rel_link rel2 where txd2.transaction_id = txs2.transaction_id and txs2.gamma_id = rel2.gamma_id and txd2.branch_id = ? and (rel2.a_art_id = ?  or  rel2.b_art_id = ?) UNION ALL SELECT txs3.gamma_id, txd3.tx_type, txs3.transaction_id   FROM osee_define_tx_details txd3, osee_define_txs txs3, osee_define_artifact_version art3 where  txd3.transaction_id = txs3.transaction_id and txs3.gamma_id = art3.gamma_id and txd3.branch_id = ? and art3.art_id = ?";

   private static final String DELETE_ATTRIBUTE_GAMMAS_REVERT =
         "DELETE FROM osee_define_attribute  atr1 WHERE atr1.gamma_id  in (SELECT txh1.gamma_id FROM osee_join_transaction txh1 where txh1.query_id = ?)";
   private static final String DELETE_RELATION_GAMMAS_REVERT =
         "DELETE FROM osee_define_rel_link  rel1 WHERE rel1.gamma_id in (SELECT txh1.gamma_id FROM osee_join_transaction txh1 where txh1.query_id = ?)";
   private static final String DELETE_ARTIFACT_GAMMAS_REVERT =
         "DELETE FROM osee_define_artifact_version art1 WHERE art1.gamma_id in (SELECT txh1.gamma_id FROM osee_join_transaction txh1 where txh1.query_id = ?)";

   private static final String DELETE_TXS_GAMMAS_REVERT =
         "DELETE from osee_define_txs txs1 WHERE (txs1.transaction_id , txs1.gamma_id ) in (SELECT txh1.transaction_id , txh1.gamma_id FROM osee_join_transaction  txh1 WHERE query_id = ? )";

   private static final String SET_TX_CURRENT_REVERT =
         "UPDATE osee_define_txs txs1 SET tx_current = 1 WHERE (txs1.transaction_id , txs1.gamma_id ) in (SELECT txh1.transaction_id , txh1.gamma_id FROM osee_join_transaction  txh1 WHERE query_id = ? )";

   private static final String PURGE_ATTRIBUTE = "DELETE FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE attr_id = ?";
   private static final String PURGE_ATTRIBUTE_GAMMAS =
         "DELETE" + " FROM " + TRANSACTIONS_TABLE + " WHERE gamma_id IN" + "(SELECT gamma_id" + " FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE attr_id = ?)";

   private static final String SELECT_ATTRIBUTES_FOR_ARTIFACT =
         "SELECT " + ATTRIBUTE_ALIAS_1.columns("attr_id", "attr_type_id", "gamma_id", "value", "uri") + " FROM " + ATTRIBUTE_ALIAS_1 + "," + TRANSACTIONS_TABLE + " WHERE " + ATTRIBUTE_ALIAS_1.column("art_id") + "=?" + " AND " + ATTRIBUTE_ALIAS_1.column("modification_id") + "<> ?" + " AND " + ATTRIBUTE_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT MAX(" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ")" + " FROM " + ATTRIBUTE_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ATTRIBUTE_ALIAS_2.column("attr_id") + "=" + ATTRIBUTE_ALIAS_1.column("attr_id") + " AND " + ATTRIBUTE_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " <= ?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   private static final String UPDATE_ARTIFACT_TYPE = "UPDATE osee_define_artifact SET art_type_id = ? WHERE art_id =?";

   private static final String SELECT_ARTIFACT_START =
         "SELECT art1.*, txs1.*, txd1.* FROM osee_define_artifact art1, osee_define_artifact_version arv1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE ";
   private static final String SELECT_ARTIFACT_END =
         " AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= ? AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? order by txs1.transaction_id desc";
   private static final String SELECT_ARTIFACT_BY_GUID = SELECT_ARTIFACT_START + "art1.guid =?" + SELECT_ARTIFACT_END;
   private static final String SELECT_ARTIFACT_BY_ID = SELECT_ARTIFACT_START + "art1.art_id =?" + SELECT_ARTIFACT_END;

   private static final String ARTIFACT_SELECT =
         "SELECT osee_define_artifact.art_id, txd1.branch_id FROM osee_define_artifact, osee_define_artifact_version arv1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE " + ARTIFACT_TABLE.column("art_id") + "=arv1.art_id AND arv1.gamma_id=txs1.gamma_id AND txs1.tx_current=" + TxChange.CURRENT.getValue() + " AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id=? AND ";
   //This SQL only exists to support the old change reports
   private static final String ARTIFACT_SELECT_NOT_CURRENT =
         "SELECT osee_define_artifact.art_id, txd1.branch_id FROM osee_define_artifact, osee_define_artifact_version arv1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE " + ARTIFACT_TABLE.column("art_id") + "=arv1.art_id AND arv1.gamma_id=txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id=? AND ";

   private static final String ARTIFACT_ID_SELECT =
         "SELECT " + ARTIFACT_TABLE.columns("art_id") + " FROM " + ARTIFACT_TABLE + " WHERE ";

   private static final String ARTIFACT_COUNT_SELECT = "SELECT COUNT(art_id) FROM " + ARTIFACT_TABLE + " WHERE ";

   private static final String REVERT_ATTRIBUTE_ADDRESSING =
         "Delete from osee_define_txs where (gamma_id, transaction_id) in (SELECT txs.gamma_id, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_attribute att WHERE det.branch_id = ? and det.tx_type = 0 and det.transaction_id = txs.transaction_id and txs.gamma_id = att.gamma_id and att.art_id = ? and att.attr_id = ?)";

   private static final String REVERT_ATTRIBUTE_CURRENT =
         "Update osee_define_txs set tx_current = CASE WHEN mod_type = 3 THEN 2 ELSE 1  END  where (gamma_id, transaction_id) = (SELECT txs.gamma_id, txs.transaction_id FROM osee_define_tx_details det, osee_define_txs txs, osee_define_attribute att WHERE det.branch_id = ? and det.tx_type = 1 and det.transaction_id = txs.transaction_id and txs.gamma_id = att.gamma_id and att.art_id = ? and att.attr_id = ?)";

   private static final String REVERT_ATTRIBUTE_DATA =
         "DELETE FROM osee_define_attribute where gamma_id in (Select att3.gamma_id FROM osee_define_attribute att3 WHERE att3.attr_id = ? and att3.art_id = ? and NOT EXISTS (select 'x' from osee_define_attribute att, osee_define_txs txs WHERE att.attr_id = att3.attr_id and att.art_id = att3.art_id and att.gamma_id = txs.gamma_id))";

   private static final String GET_GAMMAS_ATTRIBUTE_REVERT =
         "SELECT txs.gamma_id, txs.transaction_id FROM osee_define_artifact_version art, osee_define_txs txs, osee_define_tx_details det Where art.art_id = ? AND art.gamma_id = txs.gamma_id AND txs.mod_type Not in (1,3) AND txs.transaction_id = det.transaction_id and det.branch_id = ? AND NOT EXISTS (Select 'x' From  osee_define_txs txs2, osee_define_attribute att where txs2.transaction_id = txs.transaction_id AND txs2.gamma_id = att.gamma_id)";

   private static final String REMOVE_TXS_ATTRIBUTE_REVERT =
         "DELETE FROM osee_define_txs where transaction_id = ? AND gamma_id = ?";
   private static final String REMOVE_AV_ATTRIBUTE_REVERT =
         "DELETE FROM osee_define_artifact_version where gamma_id = ?";
   private static final String REMOVE_DET_ATTRIBUTE_REVERT =
         "DELETE FROM osee_define_tx_details det WHERE det.branch_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_define_txs txs where det.transaction_id = txs.transaction_id)";
   private static final String UPDATE_TXS_ATTRIBUTE_REVERT =
         "SELECT txs.transaction_id, txs.gamma_id FROM osee_define_txs txs, osee_define_tx_details det, osee_define_artifact_version ver WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = ver.gamma_id AND ver.art_id = ?  order by transaction_id desc";
   private static final String SET_TXS_ATTRIBUTE_REVERT =
         "Update osee_define_txs set tx_current = CASE WHEN mod_type = 3 THEN 2 ELSE 1  END  Where transaction_id = ? and gamma_id = ?";
   private final ExtensionDefinedObjects<IAttributeSaveListener> attributeSaveListeners;

   private IProgressMonitor monitor;
   public static final String ROOT_ARTIFACT_TYPE_NAME = "Root Artifact";
   public static final String DEFAULT_HIERARCHY_ROOT_NAME = "Default Hierarchy Root";

   private static final ArtifactPersistenceManager instance = new ArtifactPersistenceManager();

   public static boolean initFinished = false;

   private ArtifactPersistenceManager() {
      this.attributeSaveListeners =
            new ExtensionDefinedObjects<IAttributeSaveListener>(
                  "org.eclipse.osee.framework.skynet.core.AttributeSaveListener", "AttributeSaveListener", "classname");
   }

   public static ArtifactPersistenceManager getInstance() {
      return instance;
   }

   /**
    * Sets the monitor to report work to. Passing a <code>null</null> will cause the manager
    * to stop reporting work.
    * 
    * @param monitor The monitor to report work to, or <code>null</code> to stop reporting.
    */
   public void setProgressMonitor(IProgressMonitor monitor) {
      this.monitor = monitor;
   }

   protected void workedOneUnit() {
      if (monitor != null) monitor.worked(1);
   }

   protected void workingOn(String name) {
      if (monitor != null) {
         monitor.subTask(name);
      }
   }

   private void notifyOnAttributeSave(Artifact artifact) {
      try {
         List<IAttributeSaveListener> listeners = attributeSaveListeners.getObjects();
         for (IAttributeSaveListener listener : listeners) {
            listener.notifyOnAttributeSave(artifact);
         }
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   public void persistArtifact(Artifact artifact, SkynetTransaction transaction) throws OseeCoreException, SQLException {
      workingOn(artifact.getInternalDescriptiveName());
      ModificationType modType;
      ArtifactModType artifactModType;

      if (artifact.isInDb()) {
         if (artifact.isDeleted()) {
            modType = ModificationType.DELETED;
            artifactModType = ArtifactModType.Deleted;
         } else {
            modType = ModificationType.CHANGE;
            artifactModType = ArtifactModType.Changed;
         }
      } else {
         modType = ModificationType.NEW;
         artifactModType = ArtifactModType.Added;
         transaction.addToBatch(INSERT_ARTIFACT, artifact.getArtId(), artifact.getArtTypeId(), artifact.getGuid(),
               artifact.getHumanReadableId());
      }

      int artGamma = SequenceManager.getNextGammaId();
      artifact.setGammaId(artGamma);
      processTransactionForArtifact(artifact, modType, transaction, artGamma);

      // Add Attributes to Transaction
      AttributeToTransactionOperation operation = new AttributeToTransactionOperation(artifact, transaction);
      operation.execute();

      notifyOnAttributeSave(artifact);

      // Kick Local Event
      transaction.addArtifactModifiedEvent(this, artifactModType, artifact);

      workedOneUnit();
   }

   private void processTransactionForArtifact(Artifact artifact, ModificationType modType, SkynetTransaction transaction, int artGamma) throws SQLException {
      transaction.addTransactionDataItem(new ArtifactTransactionData(artifact, artGamma,
            transaction.getTransactionId(), modType, transaction.getBranch()));
   }

   /**
    * This method acquires <code>Artifact</code>'s directly from the database. This should only be called by factories
    * since all caching is performed by the factory.
    * 
    * @param guid The guid of the artifact.
    * @return The <code>Artifact</code> from the database that corresponds to the supplied guid.
    * @throws SQLException
    */
   public Artifact getArtifact(String guid, TransactionId transactionId) throws SQLException, OseeCoreException {
      return getArtifactInternal(transactionId, SELECT_ARTIFACT_BY_GUID, guid, -1, true);
   }

   public Artifact getArtifactFromId(int artId, TransactionId transactionId) throws SQLException, IllegalArgumentException, OseeCoreException {
      return getArtifactInternal(transactionId, SELECT_ARTIFACT_BY_ID, null, artId, false);
   }

   private Artifact getArtifactInternal(TransactionId transactionLimit, String query, String guid, int artId, boolean useGuid) throws SQLException, OseeCoreException {
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
         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt =
                  ConnectionHandler.runPreparedQuery(1, query, data, transactionLimit.getTransactionNumber(),
                        transactionLimit.getBranch().getBranchId());

            ResultSet rSet = chStmt.getRset();
            if (!rSet.next()) {
               throw new IllegalStateException(
                     "The artifact with " + idString + " does not exist for transaction \"" + transactionLimit + "\"");
            }

            ArtifactType artifactType = ArtifactTypeManager.getType(rSet.getInt("art_type_id"));
            ArtifactFactory factory = artifactType.getFactory();
            TransactionId transactionId = TransactionIdManager.getTransactionId(rSet);

            artifact =
                  factory.loadExisitingArtifact(rSet.getInt("art_id"), rSet.getString("guid"),
                        rSet.getString("human_readable_id"), artifactType, rSet.getInt("gamma_id"), transactionId,
                        ModificationType.getMod(rSet.getInt("mod_type")), true);

            setAttributesOnHistoricalArtifact(artifact);

            artifact.onInitializationComplete();
         } finally {
            DbUtil.close(chStmt);
         }
      }

      return artifact;
   }

   public static CharSequence getSelectArtIdSql(ISearchPrimitive searchCriteria, List<Object> dataList, Branch branch) throws SQLException {
      return getSelectArtIdSql(searchCriteria, dataList, null, branch);
   }

   public static CharSequence getSelectArtIdSql(ISearchPrimitive searchCriteria, List<Object> dataList, String alias, Branch branch) throws SQLException {
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

   public int getArtifactCount(ISearchPrimitive searchCriteria, Branch branch) throws SQLException {
      List<Object> dataList = new LinkedList<Object>();
      return getArtifactCount(getSql(searchCriteria, ARTIFACT_COUNT_SELECT, dataList, branch), dataList);
   }

   public int getArtifactCount(List<ISearchPrimitive> searchCriteria, boolean all, Branch branch) throws SQLException {
      List<Object> dataList = new LinkedList<Object>();
      return getArtifactCount(getSql(searchCriteria, all, ARTIFACT_COUNT_SELECT, dataList, branch), dataList);
   }

   private int getArtifactCount(String sql, List<Object> dataList) throws SQLException {
      int toReturn = -1;
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(sql, dataList.toArray());
         if (chStmt.next()) {
            toReturn = chStmt.getRset().getInt(1);
         }
      } finally {
         DbUtil.close(chStmt);
      }
      return toReturn;
   }

   public static String getIdSql(List<ISearchPrimitive> searchCriteria, boolean all, List<Object> dataList, Branch branch) throws SQLException {
      return getSql(searchCriteria, all, ARTIFACT_ID_SELECT, dataList, branch);
   }

   private static String getSql(ISearchPrimitive searchCriteria, String header, List<Object> dataList, Branch branch) throws SQLException {
      StringBuilder sql = new StringBuilder(header);

      sql.append(ARTIFACT_TABLE.column("art_id") + " in (");
      sql.append(getSelectArtIdSql(searchCriteria, dataList, branch));
      sql.append(")");

      return sql.toString();
   }

   private static String getSql(List<ISearchPrimitive> searchCriteria, boolean all, String header, List<Object> dataList, Branch branch) throws SQLException {
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
   public Collection<Artifact> getArtifacts(ISearchPrimitive searchCriteria, Branch branch) throws SQLException {
      LinkedList<Object> queryParameters = new LinkedList<Object>();
      queryParameters.add(branch.getBranchId());
      return ArtifactLoader.getArtifacts(getSql(searchCriteria, ARTIFACT_SELECT, queryParameters, branch),
            queryParameters.toArray(), 100, ArtifactLoad.FULL, false, null, null, false);
   }

   @Deprecated
   public Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, Branch branch, ISearchConfirmer confirmer) throws SQLException {
      LinkedList<Object> queryParameters = new LinkedList<Object>();
      queryParameters.add(branch.getBranchId());
      return ArtifactLoader.getArtifacts(getSql(searchCriteria, all, ARTIFACT_SELECT, queryParameters, branch),
            queryParameters.toArray(), 100, ArtifactLoad.FULL, false, confirmer, null, false);
   }

   @Deprecated
   public static Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, Branch branch) throws SQLException {
      return instance.getArtifacts(searchCriteria, all, branch, null);
   }

   /**
    * This is method only exists to support the old change reports
    */
   @Deprecated
   public static Collection<Artifact> getArtifactsNotCurrent(List<ISearchPrimitive> searchCriteria, boolean all, TransactionId transactionId, ISearchConfirmer confirmer) throws SQLException {
      LinkedList<Object> queryParameters = new LinkedList<Object>();
      queryParameters.add(transactionId.getBranch().getBranchId());
      return ArtifactLoader.getArtifacts(getSql(searchCriteria, all, ARTIFACT_SELECT_NOT_CURRENT, queryParameters,
            transactionId.getBranch()), queryParameters.toArray(), 100, ArtifactLoad.FULL, false, confirmer,
            transactionId, true);
   }

   /**
    * Acquires the user defined attributes for an artifact. If none are in the table, then it returns the 'default set'
    * of attributes for the artifact.
    * 
    * @param artifact The artifact to acquire the attributes for.
    * @param branch The tag to get the data for.
    * @throws SQLException
    * @throws OseeDataStoreException
    * @throws OseeCoreException
    */
   public static void setAttributesOnHistoricalArtifact(Artifact artifact) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = null;
      try {
         // Acquire previously stored attributes
         chStmt =
               ConnectionHandler.runPreparedQuery(SELECT_ATTRIBUTES_FOR_ARTIFACT, artifact.getArtId(),
                     ModificationType.DELETED.getValue(), artifact.getTransactionNumber(),
                     artifact.getBranch().getBranchId());

         ResultSet rSet = chStmt.getRset();
         while (rSet.next()) {
            AttributeToTransactionOperation.initializeAttribute(artifact, rSet.getInt("attr_type_id"),
                  rSet.getInt("attr_id"), rSet.getInt("gamma_id"), rSet.getString("value"), rSet.getString("uri"));
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      } finally {
         DbUtil.close(chStmt);
      }

      AttributeToTransactionOperation.meetMinimumAttributeCounts(artifact, false);
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

   public static void deleteArtifact(final Artifact... artifacts) throws OseeCoreException, SQLException {
      deleteArtifact(false, artifacts);
   }

   /**
    * @param artifacts The artifacts to delete.
    * @throws SQLException
    */
   public static void deleteArtifact(boolean overrideDeleteCheck, final Artifact... artifacts) throws OseeCoreException, SQLException {
      if (artifacts.length == 0) return;

      if (overrideDeleteCheck != true) {
         // Confirm artifacts are fit to delete
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            Result result = check.isDeleteable(Arrays.asList(artifacts));
            if (result.isFalse()) throw new IllegalStateException(result.getText());
         }
      }

      final Branch branch = artifacts[0].getBranch();
      if (SkynetTransactionManager.getInstance().isInBatch(branch)) {
         for (Artifact artifact : artifacts) {
            deleteTrace(artifact, SkynetTransactionManager.getInstance().getTransactionBuilder(branch));
         }
      } else {
         AbstractSkynetTxTemplate deleteTx = new AbstractSkynetTxTemplate(branch) {
            @Override
            protected void handleTxWork() throws OseeCoreException, SQLException {
               for (Artifact artifact : artifacts) {
                  if (!artifact.isDeleted()) {
                     deleteTrace(artifact, getTxBuilder());
                  }
               }
            }
         };
         deleteTx.execute();
      }
   }

   /**
    * @param artifact
    * @param builder
    * @throws Exception
    */
   public static void deleteTrace(Artifact artifact, SkynetTransactionBuilder builder) throws OseeCoreException, SQLException {
      if (!artifact.isDeleted()) {
         // This must be done first since the the actual deletion of an artifact clears out the link manager
         for (Artifact childArtifact : artifact.getChildren()) {
            deleteTrace(childArtifact, builder);
         }
         try {
            artifact.setDeleted();
            builder.deleteArtifact(artifact);
         } catch (Exception ex) {
            artifact.setNotDeleted();
            throw new OseeCoreException(ex);
         }
      }
   }

   /**
    * @param artifact
    * @param transaction
    * @throws Exception
    */
   public synchronized void doDelete(Artifact artifact, SkynetTransaction transaction, SkynetTransactionBuilder builder) throws OseeCoreException, SQLException {
      if (!artifact.isInDb()) return;

      processTransactionForArtifact(artifact, ModificationType.DELETED, transaction, SequenceManager.getNextGammaId());

      // Kick Local Event
      transaction.addArtifactModifiedEvent(this, ArtifactModType.Deleted, artifact);

      RelationManager.deleteRelationsAll(artifact);
      artifact.deleteAttributes();

      artifact.persistAttributesAndRelations();
   }

   public void purgeArtifactFromBranch(Artifact artifact) throws OseeCoreException, SQLException {
      if (artifact == null) throw new IllegalArgumentException("Artifact = null in purgeArtifactFromBranch");
      purgeArtifacts(Collections.getAggregate(artifact));
   }

   /**
    * Removes an artifact, it's attributes and any relations that have become invalid from the removal of this artifact
    * from the database. It also removes all history associated with this artifact (i.e. all transactions and gamma ids
    * will also be removed from the database only for the branch it is on).
    * 
    * @param artifact
    * @throws SQLException
    */
   public void purgeArtifactFromBranch(int branchId, int artId) throws OseeCoreException, SQLException {
      revertArtifact(branchId, artId);

      //Remove from Baseline
      ConnectionHandler.runPreparedUpdate(PURGE_BASELINE_ATTRIBUTE_TRANS, branchId, artId);
      ConnectionHandler.runPreparedUpdate(PURGE_BASELINE_RELATION_TRANS, branchId, artId, artId);
      ConnectionHandler.runPreparedUpdate(PURGE_BASELINE_ARTIFACT_TRANS, branchId, artId);
   }

   public void revertAttribute(Artifact artifact, Attribute<?> attribute) throws OseeCoreException, SQLException {
      revertAttribute(artifact.getBranch().getBranchId(), artifact.getArtId(), attribute.getAttrId());
   }

   public void revertAttribute(int branchId, int artId, int attributeId) throws OseeCoreException, SQLException {
      try {
         new RevertAttrDbTx(branchId, artId, attributeId).execute();
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   private final class RevertAttrDbTx extends AbstractDbTxTemplate {
      int branchId;
      int artId;
      int attributeId;

      public RevertAttrDbTx(int branchId, int artId, int attributeId) {
         this.branchId = branchId;
         this.artId = artId;
         this.attributeId = attributeId;
      }

      @Override
      protected void handleTxWork() throws OseeCoreException, SQLException {
         ConnectionHandler.runPreparedUpdate(REVERT_ATTRIBUTE_ADDRESSING, branchId, artId, attributeId);
         ConnectionHandler.runPreparedUpdate(REVERT_ATTRIBUTE_CURRENT, branchId, artId, attributeId);
         ConnectionHandler.runPreparedUpdate(REVERT_ATTRIBUTE_DATA, attributeId, artId);
         //Clean up artifact version table and transaction details table.
         ConnectionHandlerStatement connectionHandlerStatement = null;
         ResultSet resultSet = null;
         try {
            connectionHandlerStatement =
                  ConnectionHandler.runPreparedQuery(GET_GAMMAS_ATTRIBUTE_REVERT, artId, branchId);
            resultSet = connectionHandlerStatement.getRset();
            while (resultSet.next()) {
               ConnectionHandler.runPreparedUpdate(REMOVE_TXS_ATTRIBUTE_REVERT, resultSet.getInt("transaction_id"),
                     resultSet.getInt("gamma_id"));
               ConnectionHandler.runPreparedUpdate(REMOVE_AV_ATTRIBUTE_REVERT, resultSet.getInt("gamma_id"));
            }
         } finally {
            DbUtil.close(connectionHandlerStatement);
         }
         try {
            connectionHandlerStatement =
                  ConnectionHandler.runPreparedQuery(UPDATE_TXS_ATTRIBUTE_REVERT, branchId, artId);
            resultSet = connectionHandlerStatement.getRset();
            if (resultSet.next()) {
               ConnectionHandler.runPreparedUpdate(SET_TXS_ATTRIBUTE_REVERT, resultSet.getInt("transaction_id"),
                     resultSet.getInt("gamma_id"));
            }
         } finally {
            DbUtil.close(connectionHandlerStatement);
         }
         ConnectionHandler.runPreparedUpdate(REMOVE_DET_ATTRIBUTE_REVERT, branchId);
      }

      @Override
      protected void handleTxFinally() throws Exception {
         super.handleTxFinally();
      }
   }

   public void revertArtifact(Artifact artifact) throws OseeCoreException, SQLException {
      if (artifact == null) return;
      revertArtifact(artifact.getBranch().getBranchId(), artifact.getArtId());
   }

   public void revertArtifact(int branchId, int artId) throws OseeCoreException, SQLException {
      try {
         new RevertDbTx(branchId, artId).execute();
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }
   private final class RevertDbTx extends AbstractDbTxTemplate {
      int branchId;
      int artId;

      public RevertDbTx(int branchId, int artId) {
         this.branchId = branchId;
         this.artId = artId;
      }

      @Override
      protected void handleTxWork() throws OseeCoreException, SQLException {
         TransactionJoinQuery gammaIdsModifications = JoinUtility.createTransactionJoinQuery();
         TransactionJoinQuery gammaIdsBaseline = JoinUtility.createTransactionJoinQuery();

         //Get attribute Gammas
         ConnectionHandlerStatement connectionHandlerStatement = null;
         ResultSet resultSet = null;
         try {
            connectionHandlerStatement =
                  ConnectionHandler.runPreparedQuery(GET_GAMMAS_REVERT, branchId, artId, branchId, artId, artId,
                        branchId, artId);
            resultSet = connectionHandlerStatement.getRset();
            while (resultSet.next()) {
               if (resultSet.getInt("tx_type") == TransactionDetailsType.NonBaselined.getId()) {
                  gammaIdsModifications.add(resultSet.getInt("gamma_id"), resultSet.getInt("transaction_id"));
               } else {
                  gammaIdsBaseline.add(resultSet.getInt("gamma_id"), resultSet.getInt("transaction_id"));
               }
            }
         } finally {
            DbUtil.close(connectionHandlerStatement);
            connectionHandlerStatement = null;
            resultSet = null;
         }

         if (!gammaIdsModifications.isEmpty()) {
            try {
               gammaIdsModifications.store();
               int gammaIdModsQID = gammaIdsModifications.getQueryId();
               ConnectionHandler.runPreparedUpdate(DELETE_ATTRIBUTE_GAMMAS_REVERT, gammaIdModsQID);
               ConnectionHandler.runPreparedUpdate(DELETE_RELATION_GAMMAS_REVERT, gammaIdModsQID);
               ConnectionHandler.runPreparedUpdate(DELETE_ARTIFACT_GAMMAS_REVERT, gammaIdModsQID);

               ConnectionHandler.runPreparedUpdate(DELETE_TXS_GAMMAS_REVERT, gammaIdModsQID);

               if (!gammaIdsBaseline.isEmpty()) {
                  gammaIdsBaseline.store();
                  ConnectionHandler.runPreparedUpdate(SET_TX_CURRENT_REVERT, gammaIdsBaseline.getQueryId());
               }
            } finally {
               gammaIdsModifications.delete();
               gammaIdsBaseline.delete();
            }
         }

         ConnectionHandler.runPreparedUpdate(REMOVE_EMPTY_TRANSACTION_DETAILS, branchId);

      }

      @Override
      protected void handleTxFinally() throws Exception {
         super.handleTxFinally();
      }
   }

   public static Artifact getDefaultHierarchyRootArtifact(Branch branch, boolean createIfNecessary) throws SQLException, MultipleArtifactsExist, ArtifactDoesNotExist {
      try {
         Artifact root = ArtifactCache.getByTextId(DEFAULT_HIERARCHY_ROOT_NAME, branch);
         if (root == null) {
            root =
                  ArtifactQuery.getArtifactFromTypeAndName(ROOT_ARTIFACT_TYPE_NAME, DEFAULT_HIERARCHY_ROOT_NAME, branch);
            ArtifactCache.putByTextId(DEFAULT_HIERARCHY_ROOT_NAME, root);
         }
         return root;
      } catch (ArtifactDoesNotExist ex) {
         if (createIfNecessary) {
            logger.log(Level.INFO, "Created " + DEFAULT_HIERARCHY_ROOT_NAME + " because no root was found.");
            Artifact root =
                  ArtifactTypeManager.addArtifact(ROOT_ARTIFACT_TYPE_NAME, branch, DEFAULT_HIERARCHY_ROOT_NAME);
            root.persistAttributes();
            ArtifactCache.putByTextId(DEFAULT_HIERARCHY_ROOT_NAME, root);
            return root;
         }
         throw ex;
      }
   }

   public static Artifact getDefaultHierarchyRootArtifact(Branch branch) throws SQLException, MultipleArtifactsExist, ArtifactDoesNotExist {
      return getDefaultHierarchyRootArtifact(branch, false);
   }

   public void bulkLoadArtifacts(Collection<? extends Artifact> arts, Branch branch) throws SQLException, IllegalArgumentException {
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
    * @throws SQLException
    */
   public static void changeArtifactSubStype(Artifact artifact, ArtifactType artifactType) throws SQLException {
      ConnectionHandler.runPreparedUpdate(UPDATE_ARTIFACT_TYPE, artifactType.getArtTypeId(), artifact.getArtId());
   }

   /**
    * Purge attribute from the database.
    * 
    * @param attribute
    */
   public static void purgeAttribute(Attribute<?> attribute, int attributeId) throws SQLException {
      ConnectionHandler.runPreparedUpdate(PURGE_ATTRIBUTE_GAMMAS, attributeId);
      ConnectionHandler.runPreparedUpdate(PURGE_ATTRIBUTE, attributeId);
   }

   private static final String INSERT_SELECT_RELATIONS =
         "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) SELECT ?, ?, txs1.gamma_id, txs1.transaction_id FROM osee_join_artifact al1, osee_define_rel_link rel1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE al1.query_id = ? AND (al1.art_id = rel1.a_art_id OR al1.art_id = rel1.b_art_id) AND rel1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id";

   private static final String INSERT_SELECT_ATTRIBUTES =
         "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) SELECT ?, ?, txs1.gamma_id, txs1.transaction_id FROM osee_join_artifact al1, osee_define_attribute att1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by al1.branch_id, al1.art_id";

   private static final String INSERT_SELECT_ARTIFACTS =
         "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) SELECT ?, ?, txs1.gamma_id, txs1.transaction_id FROM osee_join_artifact al1, osee_define_artifact art1, osee_define_artifact_version arv1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txd1.branch_id = al1.branch_id AND txd1.transaction_id = txs1.transaction_id";

   private static final String COUNT_ARTIFACT_VIOLATIONS =
         "SELECT art1.art_id, txd1.branch_id FROM osee_join_artifact al1, osee_define_artifact art1, osee_define_artifact_version arv1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txd1.branch_id = al1.branch_id AND txd1.transaction_id = txs1.transaction_id";
   private static final String DELETE_FROM_TXS_USING_JOIN_TRANSACTION =
         "DELETE FROM osee_define_txs txs1 WHERE EXISTS ( select 1 from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.transaction_id = txs1.transaction_id AND jt1.gamma_id = txs1.gamma_id)";
   private static final String DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION =
         "DELETE FROM osee_define_tx_details txd1 WHERE EXISTS ( select 1 from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.transaction_id = txd1.transaction_id AND not exists ( select * from osee_define_txs txs1 where txs1.transaction_id = jt1.transaction_id))";
   private static final String DELETE_FROM_RELATION_VERSIONS =
         "DELETE FROM osee_define_rel_link rel1 WHERE EXISTS ( select * from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.gamma_id = rel1.gamma_id AND not exists ( select * from osee_define_txs txs1 where txs1.gamma_id = jt1.gamma_id))";
   private static final String DELETE_FROM_ATTRIBUTE_VERSIONS =
         "DELETE FROM osee_define_attribute attr1 WHERE EXISTS ( select * from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.gamma_id = attr1.gamma_id AND not exists ( select * from osee_define_txs txs1 where txs1.gamma_id = jt1.gamma_id))";
   private static final String DELETE_FROM_ARTIFACT_VERSIONS =
         "DELETE FROM osee_define_artifact_version artv1 WHERE EXISTS ( select * from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.gamma_id = artv1.gamma_id AND not exists ( select * from osee_define_txs txs1 where txs1.gamma_id = jt1.gamma_id))";
   private static final String DELETE_FROM_ARTIFACT =
         "DELETE FROM osee_define_artifact art1 WHERE EXISTS ( select * from osee_join_artifact ja1 WHERE ja1.query_id = ? AND ja1.art_id = art1.art_id AND not exists ( select * from osee_define_artifact_version artv1 where artv1.art_id = ja1.art_id))";

   /**
    * @param artifactsToPurge
    * @param collection
    * @throws SQLException
    * @throws OseeCoreException
    */
   public static void purgeArtifacts(Collection<? extends Artifact> artifactsToPurge) throws SQLException, OseeCoreException {
      //first determine if the purge is legal.
      List<Object[]> batchParameters = new ArrayList<Object[]>();
      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      try {
         for (Artifact art : artifactsToPurge) {
            for (Branch branch : art.getBranch().getChildBranches(true)) {
               batchParameters.add(new Object[] {queryId, insertTime, art.getArtId(), branch.getBranchId(),
                     SQL3DataType.INTEGER});
            }
         }
         if (batchParameters.size() > 0) {
            ArtifactLoader.selectArtifacts(batchParameters);
            ConnectionHandlerStatement stmt = null;
            try {
               stmt = ConnectionHandler.runPreparedQuery(COUNT_ARTIFACT_VIOLATIONS, queryId);
               boolean failed = false;
               StringBuilder sb = new StringBuilder();
               while (stmt.getRset().next()) {
                  failed = true;
                  sb.append("ArtifactId[");
                  sb.append(stmt.getRset().getInt(1));
                  sb.append("] BranchId[");
                  sb.append(stmt.getRset().getInt(2));
                  sb.append("]\n");
               }
               if (failed) {
                  throw new OseeCoreException(String.format(
                        "Unable to purge because the following artifacts exist on child branches.\n%s", sb.toString()));
               }
            } finally {
               ArtifactLoader.clearQuery(queryId);
               DbUtil.close(stmt);
            }
         }

         // now load the artifacts to be purged
         batchParameters.clear();
         queryId = ArtifactLoader.getNewQueryId();
         insertTime = GlobalTime.GreenwichMeanTimestamp();

         // insert into the artifact_join_table
         for (Artifact art : artifactsToPurge) {
            batchParameters.add(new Object[] {queryId, insertTime, art.getArtId(), art.getBranch().getBranchId(),
                  SQL3DataType.INTEGER});
         }
         ArtifactLoader.selectArtifacts(batchParameters);

         //run the insert select queries to populate the osee_join_transaction table  (this will take care of the txs table)    
         int transactionJoinId = ArtifactLoader.getNewQueryId();
         ConnectionHandler.runPreparedUpdate(INSERT_SELECT_RELATIONS, transactionJoinId, insertTime, queryId);
         ConnectionHandler.runPreparedUpdate(INSERT_SELECT_ATTRIBUTES, transactionJoinId, insertTime, queryId);
         ConnectionHandler.runPreparedUpdate(INSERT_SELECT_ARTIFACTS, transactionJoinId, insertTime, queryId);

         //delete from the txs table
         int txsDeletes =
               ConnectionHandler.runPreparedUpdate(DELETE_FROM_TXS_USING_JOIN_TRANSACTION, transactionJoinId);

         int txdDeletes =
               ConnectionHandler.runPreparedUpdate(DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION, transactionJoinId);

         int relationVersions = ConnectionHandler.runPreparedUpdate(DELETE_FROM_RELATION_VERSIONS, transactionJoinId);
         int attributeVersions = ConnectionHandler.runPreparedUpdate(DELETE_FROM_ATTRIBUTE_VERSIONS, transactionJoinId);
         int artifactVersions = ConnectionHandler.runPreparedUpdate(DELETE_FROM_ARTIFACT_VERSIONS, transactionJoinId);
         int artifact = ConnectionHandler.runPreparedUpdate(DELETE_FROM_ARTIFACT, queryId);

         // Delete tags for purged artifacts
         try {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("queryId", Integer.toString(transactionJoinId));
            String url = HttpUrlBuilder.getInstance().getOsgiServletServiceUrl("search", parameters);
            String response = HttpProcessor.delete(new URL(url));

         } catch (Exception ex) {
            OseeLog.log(SkynetActivator.class, Level.WARNING, "Error Deleting Tags during purge.", ex);
         }

         OseeLog.log(
               SkynetActivator.class,
               Level.INFO,
               String.format(
                     "Purge Row Deletes: txs rows [%d], rel ver rows [%d], attr ver rows [%d] art ver rows [%d] art rows [%d].  txs vs. total versions [%d vs %d]",
                     txsDeletes, relationVersions, attributeVersions, artifactVersions, artifact, txsDeletes,
                     (relationVersions + attributeVersions + artifactVersions)));

         ConnectionHandler.runPreparedUpdate("DELETE FROM osee_join_transaction where query_id = ?", transactionJoinId);

         for (Artifact art : artifactsToPurge) {
            art.setDeleted();
            for (RelationLink rel : art.getRelationsAll(false)) {
               rel.markAsPurged();
            }
            for (Attribute<?> attr : art.internalGetAttributes()) {
               attr.markAsPurged();
            }
         }

         // Kick Local and Remote Events
         OseeEventManager.kickArtifactsPurgedEvent(instance, new LoadedArtifacts(artifactsToPurge));

      } finally {
         ArtifactLoader.clearQuery(queryId);
      }
   }
}