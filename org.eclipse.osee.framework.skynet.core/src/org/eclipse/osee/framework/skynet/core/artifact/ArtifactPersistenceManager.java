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
import java.sql.Connection;
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.db.connection.core.schema.LocalAliasTable;
import org.eclipse.osee.framework.db.connection.core.transaction.AbstractDbTxTemplate;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
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
   private static final String INSERT_ARTIFACT =
         "INSERT INTO " + ARTIFACT_TABLE + " (art_id, art_type_id, guid, human_readable_id) VALUES (?, ?, ?, ?)";

   private static final String REMOVE_EMPTY_TRANSACTION_DETAILS =
         "DELETE FROM osee_tx_details det WHERE det.branch_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_txs txs where txs.transaction_id = det.transaction_id)";

   private static final LocalAliasTable ATTRIBUTE_ALIAS_1 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t1");
   private static final LocalAliasTable ATTRIBUTE_ALIAS_2 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t2");

   private static final String PURGE_BASELINE_ATTRIBUTE_TRANS =
         "DELETE from " + TRANSACTIONS_TABLE + " T2 WHERE EXISTS (SELECT 'x' from " + TRANSACTION_DETAIL_TABLE + " T1, " + ATTRIBUTE_VERSION_TABLE + " T3 WHERE T1.transaction_id = T2.transaction_id and T3.gamma_id = T2.gamma_id and T1.tx_type = " + TransactionDetailsType.Baselined.getId() + " and T1.branch_id = ? and T3.art_id = ?)";
   private static final String PURGE_BASELINE_RELATION_TRANS =
         "DELETE from " + TRANSACTIONS_TABLE + " T2 WHERE EXISTS (SELECT 'x' from " + TRANSACTION_DETAIL_TABLE + " T1, " + RELATION_LINK_VERSION_TABLE + " T3 WHERE T1.transaction_id = T2.transaction_id and T3.gamma_id = T2.gamma_id and T1.tx_type = " + TransactionDetailsType.Baselined.getId() + " and T1.branch_id = ? and (T3.a_art_id = ? or T3.b_art_id = ?))";
   private static final String PURGE_BASELINE_ARTIFACT_TRANS =
         "DELETE from " + TRANSACTIONS_TABLE + " T2 WHERE EXISTS (SELECT 'x' from " + TRANSACTION_DETAIL_TABLE + " T1, " + ARTIFACT_VERSION_TABLE + " T3 WHERE T1.transaction_id = T2.transaction_id and T3.gamma_id = T2.gamma_id and T1.tx_type = " + TransactionDetailsType.Baselined.getId() + " and T1.branch_id = ? and T3.art_id = ?)";

   private static final String GET_GAMMAS_REVERT =
         "SELECT txs1.gamma_id, txd1.tx_type, txs1.transaction_id  FROM osee_tx_details txd1, osee_txs  txs1, osee_attribute atr1 where  txd1.transaction_id = txs1.transaction_id and txs1.gamma_id = atr1.gamma_id and txd1.branch_id = ? and atr1.art_id = ? UNION ALL SELECT txs2.gamma_id, txd2.tx_type, txs2.transaction_id FROM osee_tx_details txd2, osee_txs  txs2, osee_relation_link rel2 where txd2.transaction_id = txs2.transaction_id and txs2.gamma_id = rel2.gamma_id and txd2.branch_id = ? and (rel2.a_art_id = ?  or  rel2.b_art_id = ?) UNION ALL SELECT txs3.gamma_id, txd3.tx_type, txs3.transaction_id FROM osee_tx_details txd3, osee_txs txs3, osee_artifact_version art3 where  txd3.transaction_id = txs3.transaction_id and txs3.gamma_id = art3.gamma_id and txd3.branch_id = ? and art3.art_id = ?";

   private static final String REVERT_SELECT =
         "(SELECT txh1.gamma_id FROM osee_join_transaction txh1 where txh1.query_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_txs txs where txs.gamma_id = txh1.gamma_id))";

   private static final String DELETE_ATTRIBUTE_GAMMAS_REVERT =
         "DELETE FROM osee_attribute  atr1 WHERE atr1.gamma_id  in " + REVERT_SELECT;
   private static final String DELETE_RELATION_GAMMAS_REVERT =
         "DELETE FROM osee_relation_link  rel1 WHERE rel1.gamma_id in " + REVERT_SELECT;
   private static final String DELETE_ARTIFACT_GAMMAS_REVERT =
         "DELETE FROM osee_artifact_version art1 WHERE art1.gamma_id in " + REVERT_SELECT;

   private static final String DELETE_TXS_GAMMAS_REVERT =
         "DELETE from osee_txs txs1 WHERE (txs1.transaction_id , txs1.gamma_id ) in (SELECT txh1.transaction_id , txh1.gamma_id FROM osee_join_transaction  txh1 WHERE query_id = ? )";

   private static final String SET_TX_CURRENT_REVERT =
         "UPDATE osee_txs txs1 SET tx_current = 1 WHERE (txs1.transaction_id , txs1.gamma_id ) in (SELECT txh1.transaction_id , txh1.gamma_id FROM osee_join_transaction  txh1 WHERE query_id = ? )";

   private static final String PURGE_ATTRIBUTE = "DELETE FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE attr_id = ?";
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
   //This SQL only exists to support the old change reports
   private static final String ARTIFACT_SELECT_NOT_CURRENT =
         "SELECT osee_artifact.art_id, txd1.branch_id FROM osee_artifact, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE " + ARTIFACT_TABLE.column("art_id") + "=arv1.art_id AND arv1.gamma_id=txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id=? AND ";

   private static final String ARTIFACT_ID_SELECT =
         "SELECT " + ARTIFACT_TABLE.columns("art_id") + " FROM " + ARTIFACT_TABLE + " WHERE ";

   private static final String UPDATE_TRANSACTION =
         "Update osee_txs set tx_current = CASE WHEN mod_type = 3 THEN 2 WHEN mod_type = 5 THEN 3 ELSE 1 END where (gamma_id, transaction_id) in ";
   private static final String DELETE_TRANSACTION = "Delete from osee_txs where (gamma_id, transaction_id) in ";

   private static final String[] REVERT_CURRENT_SELECT =
         {"(SELECT txs.gamma_id, txs.transaction_id FROM osee_tx_details det, osee_txs txs, ",
               " t1 WHERE det.branch_id = ? and det.tx_type = ",
               " and det.transaction_id = txs.transaction_id and txs.gamma_id = t1.gamma_id "};
   private static final String REVERT_ATTRIBUTE_SELECT =
         REVERT_CURRENT_SELECT[0] + "osee_attribute" + REVERT_CURRENT_SELECT[1] + "0" + REVERT_CURRENT_SELECT[2] + " and t1.art_id = ? and t1.attr_id = ?)";
   private static final String REVERT_ATTRIBUTE_CURRENT_SELECT =
         REVERT_CURRENT_SELECT[0] + "osee_attribute" + REVERT_CURRENT_SELECT[1] + "1" + REVERT_CURRENT_SELECT[2] + " and t1.art_id = ? and t1.attr_id = ?)";
   private static final String REVERT_REL_LINK_SELECT =
         REVERT_CURRENT_SELECT[0] + "osee_relation_link" + REVERT_CURRENT_SELECT[1] + "0" + REVERT_CURRENT_SELECT[2] + "and t1.rel_link_id = ?)";
   private static final String REVERT_REL_LINK_CURRENT_SELECT =
         REVERT_CURRENT_SELECT[0] + "osee_relation_link" + REVERT_CURRENT_SELECT[1] + "1" + REVERT_CURRENT_SELECT[2] + " and t1.rel_link_id = ?)";

   private static final String REVERT_ATTRIBUTE_SET_CURRENT = UPDATE_TRANSACTION + REVERT_ATTRIBUTE_CURRENT_SELECT;
   private static final String REVERT_ATTRIBUTE_ADDRESSING = DELETE_TRANSACTION + REVERT_ATTRIBUTE_SELECT;

   private static final String REVERT_REL_LINK_SET_CURRENT = UPDATE_TRANSACTION + REVERT_REL_LINK_CURRENT_SELECT;
   private static final String REVERT_REL_LINK_ADDRESSING = DELETE_TRANSACTION + REVERT_REL_LINK_SELECT;

   private static final String REVERT_ARTIFACT_VERSION_SELECT =
         "(SELECT txs.gamma_id, txs.transaction_id FROM osee_tx_details det, osee_txs txs, osee_artifact_version art WHERE txs.transaction_id = ?  AND det.tx_type = 0 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id AND NOT EXISTS (SELECT 'x' FROM osee_txs txs2 WHERE txs2.transaction_id = txs.transaction_id AND txs2.gamma_id != txs.gamma_id))";
   private static final String REVERT_ARTIFACT_VERSION_ADDRESSING = DELETE_TRANSACTION + REVERT_ARTIFACT_VERSION_SELECT;
   private static final String REVERT_ARTIFACT_VERSION_CURRENT_SELECT =
         "(SELECT txs1.gamma_id, txs1.transaction_id FROM osee_txs txs1, osee_artifact_version art1 WHERE art1.art_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.transaction_id = (SELECT max(txs.transaction_id) FROM osee_tx_details det, osee_txs txs, osee_artifact_version art WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id AND art.art_id = ?))";
   private static final String REVERT_ARTIFACT_VERSION_SET_CURRENT =
         UPDATE_TRANSACTION + REVERT_ARTIFACT_VERSION_CURRENT_SELECT;

   private static final String REVERT_ATTRIBUTE_DATA =
         "DELETE FROM osee_attribute where gamma_id in (SELECT gamma_id FROM osee_attribute attr WHERE attr.attr_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_txs txs WHERE txs.gamma_id = attr.gamma_id))";
   private static final String REVERT_REL_LINK_DATA =
         "DELETE FROM osee_relation_link where gamma_id in (SELECT gamma_id FROM osee_relation_link rel WHERE rel.rel_link_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_txs txs WHERE txs.gamma_id = rel.gamma_id))";

   private static final String REVERT_ARTIFACT_VERSION_DATA =
         "DELETE FROM osee_artifact_version where gamma_id in (SELECT gamma_id FROM osee_artifact_version art WHERE art.art_id = ? AND NOT EXISTS (SELECT 'x' FROM osee_txs txs WHERE txs.gamma_id = art.gamma_id))";

   private final ExtensionDefinedObjects<IAttributeSaveListener> attributeSaveListeners;

   private IProgressMonitor monitor;
   public static final String ROOT_ARTIFACT_TYPE_NAME = "Root Artifact";
   public static final String DEFAULT_HIERARCHY_ROOT_NAME = "Default Hierarchy Root";

   private static final ArtifactPersistenceManager instance = new ArtifactPersistenceManager();

   public static boolean initFinished = false;

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Persistence"));

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
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
   }

   public void persistArtifact(Artifact artifact, SkynetTransaction transaction) throws OseeCoreException {
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

   private void processTransactionForArtifact(Artifact artifact, ModificationType modType, SkynetTransaction transaction, int artGamma) {
      transaction.addTransactionDataItem(new ArtifactTransactionData(artifact, artGamma,
            transaction.getTransactionId(), modType, transaction.getBranch()));
   }

   /**
    * This method acquires <code>Artifact</code>'s directly from the database. This should only be called by factories
    * since all caching is performed by the factory.
    * 
    * @param guid The guid of the artifact.
    * @return The <code>Artifact</code> from the database that corresponds to the supplied guid.
    */
   public Artifact getArtifact(String guid, TransactionId transactionId) throws OseeCoreException {
      return getArtifactInternal(transactionId, SELECT_ARTIFACT_BY_GUID, guid, -1, true);
   }

   public Artifact getArtifactFromId(int artId, TransactionId transactionId) throws OseeCoreException {
      return getArtifactInternal(transactionId, SELECT_ARTIFACT_BY_ID, null, artId, false);
   }

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
         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt =
                  ConnectionHandler.runPreparedQuery(1, query, data, transactionLimit.getTransactionNumber(),
                        transactionLimit.getBranch().getBranchId());

            if (!chStmt.next()) {
               throw new IllegalStateException(
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
            ConnectionHandler.close(chStmt);
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
    * This is method only exists to support the old change reports
    */
   @Deprecated
   public static Collection<Artifact> getArtifactsNotCurrent(List<ISearchPrimitive> searchCriteria, boolean all, TransactionId transactionId, ISearchConfirmer confirmer) throws OseeCoreException {
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
    * @throws OseeDataStoreException
    * @throws OseeCoreException
    */
   public static void setAttributesOnHistoricalArtifact(Artifact artifact) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = null;
      try {
         // Acquire previously stored attributes
         chStmt =
               ConnectionHandler.runPreparedQuery(SELECT_ATTRIBUTES_FOR_ARTIFACT, artifact.getArtId(),
                     ModificationType.DELETED.getValue(), artifact.getTransactionNumber(),
                     artifact.getBranch().getBranchId());

         while (chStmt.next()) {
            AttributeToTransactionOperation.initializeAttribute(artifact, chStmt.getInt("attr_type_id"),
                  chStmt.getInt("attr_id"), chStmt.getInt("gamma_id"), chStmt.getString("value"),
                  chStmt.getString("uri"));
         }
      } finally {
         ConnectionHandler.close(chStmt);
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

   public static void deleteArtifact(final Artifact... artifacts) throws OseeCoreException {
      deleteArtifact(false, artifacts);
   }

   /**
    * @param artifacts The artifacts to delete.
    */
   public static void deleteArtifact(boolean overrideDeleteCheck, final Artifact... artifacts) throws OseeCoreException {
      if (artifacts.length == 0) return;

      if (overrideDeleteCheck != true) {
         // Confirm artifacts are fit to delete
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            Result result = check.isDeleteable(Arrays.asList(artifacts));
            if (result.isFalse()) throw new IllegalStateException(result.getText());
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
      ArtifactQuery.getArtifactsFromIds(artIds, artifacts[0].getBranch(), true);
      final Branch branch = artifacts[0].getBranch();
      if (SkynetTransactionManager.getInstance().isInBatch(branch)) {
         for (Artifact artifact : artifacts) {
            deleteTrace(artifact, SkynetTransactionManager.getInstance().getTransactionBuilder(branch), true);
         }
      } else {
         AbstractSkynetTxTemplate deleteTx = new AbstractSkynetTxTemplate(branch) {
            @Override
            protected void handleTxWork() throws OseeCoreException {
               for (Artifact artifact : artifacts) {
                  if (!artifact.isDeleted()) {
                     deleteTrace(artifact, getTxBuilder(), true);
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
    * @param reorderReloations TODO
    * @throws Exception
    */
   public static void deleteTrace(Artifact artifact, SkynetTransactionBuilder builder, boolean reorderRelations) throws OseeCoreException {
      if (!artifact.isDeleted()) {
         // This must be done first since the the actual deletion of an artifact clears out the link manager
         for (Artifact childArtifact : artifact.getChildren()) {
            deleteTrace(childArtifact, builder, false);
         }
         try {
            artifact.setDeleted();
            builder.deleteArtifact(artifact, reorderRelations);
         } catch (Exception ex) {
            artifact.setNotDeleted();
            throw new OseeCoreException(ex);
         }
      }
   }

   /**
    * @param artifact
    * @param transaction
    * @param reorderRelations TODO
    * @throws Exception
    */
   public synchronized void doDelete(Artifact artifact, SkynetTransaction transaction, SkynetTransactionBuilder builder, boolean reorderRelations) throws OseeCoreException {
      if (!artifact.isInDb()) return;

      processTransactionForArtifact(artifact, ModificationType.DELETED, transaction, SequenceManager.getNextGammaId());

      // Kick Local Event
      transaction.addArtifactModifiedEvent(this, ArtifactModType.Deleted, artifact);

      RelationManager.deleteRelationsAll(artifact, reorderRelations);
      artifact.deleteAttributes();

      artifact.persistAttributesAndRelations();
   }

   /**
    * Removes an artifact, it's attributes and any relations that have become invalid from the removal of this artifact
    * from the database. It also removes all history associated with this artifact (i.e. all transactions and gamma ids
    * will also be removed from the database only for the branch it is on).
    * 
    * @param artifact
    */
   public void purgeArtifactFromBranch(int branchId, int artId) throws OseeCoreException {
      revertArtifact(branchId, artId);

      //Remove from Baseline
      ConnectionHandler.runPreparedUpdate(PURGE_BASELINE_ATTRIBUTE_TRANS, branchId, artId);
      ConnectionHandler.runPreparedUpdate(PURGE_BASELINE_RELATION_TRANS, branchId, artId, artId);
      ConnectionHandler.runPreparedUpdate(PURGE_BASELINE_ARTIFACT_TRANS, branchId, artId);
   }

   public void revertAttribute(Attribute<?> attribute) throws OseeDataStoreException {
      revertAttribute(attribute.getArtifact().getBranch().getBranchId(), attribute.getArtifact().getArtId(),
            attribute.getAttrId());
   }

   public void revertAttribute(int branchId, int artId, int attributeId) throws OseeDataStoreException {
      try {
         new RevertAttrDbTx(branchId, artId, attributeId).execute();
      } catch (Exception ex) {
         throw new OseeDataStoreException(ex);
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
      protected void handleTxWork() throws OseeDataStoreException {
         //Clean up the Attribute Addressing
         long time = System.currentTimeMillis();
         long totalTime = time;
         ConnectionHandlerStatement chStmt = null;
         OseeConnection connection = OseeDbConnection.getConnection();
         List<Object[]> insertParameters = new LinkedList<Object[]>();
         try {
            chStmt =
                  ConnectionHandler.runPreparedQuery(connection, REVERT_ATTRIBUTE_SELECT, branchId, artId, attributeId);
            while (chStmt.next()) {
               insertParameters.add(new Object[] {chStmt.getInt("transaction_id")});
               if (DEBUG) {
                  System.out.println(String.format("  Revert Attribute: Delete Gamma ID = %d , Transaction ID = %d",
                        chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               }
            }
         } finally {
            ConnectionHandler.close(chStmt);
         }
         if (DEBUG) {
            System.out.println(String.format("  Revert Attribute: Ran the Attribute Select Query in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }

         ConnectionHandler.runPreparedUpdate(connection, REVERT_ATTRIBUTE_ADDRESSING, branchId, artId, attributeId);

         if (DEBUG) {
            try {
               chStmt =
                     ConnectionHandler.runPreparedQuery(connection, REVERT_ATTRIBUTE_CURRENT_SELECT, branchId, artId,
                           attributeId);
               while (chStmt.next()) {
                  System.out.println(String.format(
                        "  Revert Attribute: Set Current Gamma ID = %d , Transaction ID = %d",
                        chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               }
            } finally {
               ConnectionHandler.close(chStmt);
            }
         }
         if (DEBUG) {
            System.out.println(String.format("  Revert Attribute: Ran the Revert Attribute Current Select Query in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }
         //Set Attribute baseline transactions to current
         ConnectionHandler.runPreparedUpdate(connection, REVERT_ATTRIBUTE_SET_CURRENT, branchId, artId, attributeId);
         if (DEBUG) {
            System.out.println(String.format("  Revert Attribute: Ran the Revert Attribute Current Set Query in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }
         //Clean up artifact version Addressing.
         if (insertParameters.size() > 0) {
            ConnectionHandler.runPreparedUpdate(connection, REVERT_ARTIFACT_VERSION_ADDRESSING, insertParameters);
         }
         if (DEBUG) {
            System.out.println(String.format("  Revert Attribute: Ran the Artifact Version Current Set Query in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }
         //Set Artifact Version transactions to current
         updateArtifactVersion(connection, branchId, artId);
         if (DEBUG) {
            System.out.println(String.format(
                  "  Revert Attribute: Ran the Revert Artifact Version Current Set Query in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }

         //Remove old attribute data
         ConnectionHandler.runPreparedUpdate(connection, REVERT_ATTRIBUTE_DATA, attributeId);
         if (DEBUG) {
            System.out.println(String.format("  Revert Attribute: Removed old attribute data in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }
         //Remove old artifact version data
         ConnectionHandler.runPreparedUpdate(connection, REVERT_ARTIFACT_VERSION_DATA, artId);
         if (DEBUG) {
            System.out.println(String.format("  Revert Attribute: Removed old artifact version data in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }
         ConnectionHandler.runPreparedUpdate(connection, REMOVE_EMPTY_TRANSACTION_DETAILS, branchId);
         if (DEBUG) {
            System.out.println(String.format("  Revert Attribute: Removed empty tx details data in %s",
                  Lib.getElapseString(time)));
            System.out.println(String.format("  Revert Attribute: Reverted the Attribute %d in %s", attributeId,
                  Lib.getElapseString(totalTime)));
         }
      }

      @Override
      protected void handleTxFinally() throws Exception {
         super.handleTxFinally();
      }
   }

   /**
    * @deprecated Not really deprecated but not usable yet because we don't handle fixing the reordering issue that may
    *             have occured
    */
   private void revertRelationLink(RelationLink link) throws OseeCoreException {
      //Only reverts relation links that don't span multiple branches.  Need to revisit if additional functionality is needed.
      if (!link.getArtifactA().getBranch().equals(link.getArtifactB().getBranch())) {
         throw new OseeArgumentException(String.format("Can not revert Relation %d. Relation spans multiple branches",
               link.getRelationId()));
      }
      revertRelationLink(link.getArtifactA().getBranch().getBranchId(), link.getRelationId(),
            link.getArtifactA().getArtId(), link.getArtifactB().getArtId());
   }

   public void revertRelationLink(int branchId, int relLinkId, int aArtId, int bArtId) throws OseeDataStoreException {
      try {
         new RevertRelDbTx(branchId, relLinkId, aArtId, bArtId).execute();
      } catch (Exception ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   private final class RevertRelDbTx extends AbstractDbTxTemplate {
      int branchId;
      int relLinkId;
      int aArtId;
      int bArtId;

      public RevertRelDbTx(int branchId, int relLinkId, int aArtId, int bArtId) {
         this.branchId = branchId;
         this.relLinkId = relLinkId;
         this.aArtId = aArtId;
         this.bArtId = bArtId;
      }

      @Override
      protected void handleTxWork() throws OseeDataStoreException {
         //Clean up the Attribute Addressing
         long time = System.currentTimeMillis();
         long totalTime = time;
         ConnectionHandlerStatement chStmt = null;
         OseeConnection connection = OseeDbConnection.getConnection();
         List<Object[]> insertParameters = new LinkedList<Object[]>();
         try {
            chStmt = ConnectionHandler.runPreparedQuery(connection, REVERT_REL_LINK_SELECT, branchId, relLinkId);
            while (chStmt.next()) {
               insertParameters.add(new Object[] {chStmt.getInt("transaction_id")});
               if (DEBUG) {
                  System.out.println(String.format("  RevertRelationLink: Delete Gamma ID = %d , Transaction ID = %d",
                        chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               }
            }
         } finally {
            ConnectionHandler.close(chStmt);
         }
         if (DEBUG) {
            System.out.println(String.format("  RevertRelationLink: Ran the Relation Select Query in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }

         ConnectionHandler.runPreparedUpdate(connection, REVERT_REL_LINK_ADDRESSING, branchId, relLinkId);

         if (DEBUG) {
            try {
               chStmt =
                     ConnectionHandler.runPreparedQuery(connection, REVERT_REL_LINK_CURRENT_SELECT, branchId, relLinkId);
               while (chStmt.next()) {
                  System.out.println(String.format(
                        "  RevertRelationLink: Set Current Gamma ID = %d , Transaction ID = %d",
                        chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
               }
            } finally {
               ConnectionHandler.close(chStmt);
            }
         }
         if (DEBUG) {
            System.out.println(String.format(
                  "  RevertRelationLink: Ran the Revert Relation Link Current Select Query in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }
         //Set Attribute baseline transactions to current
         ConnectionHandler.runPreparedUpdate(connection, REVERT_REL_LINK_SET_CURRENT, branchId, relLinkId);
         if (DEBUG) {
            System.out.println(String.format(
                  "  RevertRelationLink: Ran the Revert Relation Link Current Set Query in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }
         //Clean up artifact version Addressing.
         if (insertParameters.size() > 0) {
            ConnectionHandler.runPreparedUpdate(connection, REVERT_ARTIFACT_VERSION_ADDRESSING, insertParameters);
         }
         if (DEBUG) {
            System.out.println(String.format("  RevertRelationLink: Ran the Artifact Version Current Set Query in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }
         //Set Artifact Version transactions to current
         updateArtifactVersion(connection, branchId, aArtId);
         updateArtifactVersion(connection, branchId, bArtId);
         if (DEBUG) {
            System.out.println(String.format(
                  "  RevertRelationLink: Ran the Revert Artifact Version Current Set Query in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }

         //Remove old attribute data
         ConnectionHandler.runPreparedUpdate(connection, REVERT_REL_LINK_DATA, relLinkId);
         if (DEBUG) {
            System.out.println(String.format("  RevertRelationLink: Removed old rel link data in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }
         //Remove old artifact version data
         ConnectionHandler.runPreparedUpdate(connection, REVERT_ARTIFACT_VERSION_DATA, aArtId);
         ConnectionHandler.runPreparedUpdate(connection, REVERT_ARTIFACT_VERSION_DATA, bArtId);
         if (DEBUG) {
            System.out.println(String.format("  RevertRelationLink: Removed old artifact version data in %s",
                  Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }
         ConnectionHandler.runPreparedUpdate(connection, REMOVE_EMPTY_TRANSACTION_DETAILS, branchId);
         if (DEBUG) {
            System.out.println(String.format("  RevertRelationLink: Removed empty tx details data in %s",
                  Lib.getElapseString(time)));
            System.out.println(String.format("  RevertRelationLink: Reverted the Relation Link %d in %s", relLinkId,
                  Lib.getElapseString(totalTime)));
         }
      }

      @Override
      protected void handleTxFinally() throws Exception {
         super.handleTxFinally();
      }
   }

   private void updateArtifactVersion(Connection connection, int branchId, int artId) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = null;
      //Set Artifact Version transactions to current
      if (DEBUG) {
         try {
            chStmt =
                  ConnectionHandler.runPreparedQuery(connection, REVERT_ARTIFACT_VERSION_CURRENT_SELECT, artId,
                        branchId, artId);
            while (chStmt.next()) {
               System.out.println(String.format(
                     "  Revert Artifact Current Version: Set Current Gamma ID = %d , Transaction ID = %d for art ID = %d branch ID = %d",
                     chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id"), artId, branchId));
            }
         } finally {
            ConnectionHandler.close(chStmt);
         }
      }
      ConnectionHandler.runPreparedUpdate(connection, REVERT_ARTIFACT_VERSION_SET_CURRENT, artId, branchId, artId);
   }

   public void revertArtifact(Artifact artifact) throws OseeCoreException {
      if (artifact == null) return;
      revertArtifact(artifact.getBranch().getBranchId(), artifact.getArtId());
   }

   public void revertArtifact(int branchId, int artId) throws OseeCoreException {
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
      protected void handleTxWork() throws OseeCoreException {
         TransactionJoinQuery gammaIdsModifications = JoinUtility.createTransactionJoinQuery();
         TransactionJoinQuery gammaIdsBaseline = JoinUtility.createTransactionJoinQuery();
         OseeConnection connection = OseeDbConnection.getConnection();
         long time = System.currentTimeMillis();
         long totalTime = time;
         //Get attribute Gammas
         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt =
                  ConnectionHandler.runPreparedQuery(connection, GET_GAMMAS_REVERT, branchId, artId, branchId, artId,
                        artId, branchId, artId);
            while (chStmt.next()) {
               if (chStmt.getInt("tx_type") == TransactionDetailsType.NonBaselined.getId()) {
                  gammaIdsModifications.add(chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id"));
               } else {
                  gammaIdsBaseline.add(chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id"));
               }
            }
         } finally {
            ConnectionHandler.close(chStmt);
         }
         if (DEBUG) {
            System.out.println(String.format(" Took %s to run the gamma selection Query", Lib.getElapseString(time)));
            time = System.currentTimeMillis();
         }
         if (!gammaIdsModifications.isEmpty()) {
            try {
               gammaIdsModifications.store(connection);
               int gammaIdModsQID = gammaIdsModifications.getQueryId();

               int count = ConnectionHandler.runPreparedUpdate(connection, DELETE_TXS_GAMMAS_REVERT, gammaIdModsQID);

               if (DEBUG) {
                  System.out.println(String.format("Deleted %d txs for gamma revert in %s", count,
                        Lib.getElapseString(time)));
                  time = System.currentTimeMillis();
                  try {
                     chStmt =
                           ConnectionHandler.runPreparedQuery(connection,
                                 "Select * from osee_join_transaction where query_id = ?", gammaIdModsQID);
                     while (chStmt.next()) {
                        System.out.println(String.format(
                              " Revert Artifact: Addressing Gamma_id = %d Transaction Id = %d ",
                              chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
                     }
                  } finally {
                     ConnectionHandler.close(chStmt);
                  }
                  try {
                     chStmt = ConnectionHandler.runPreparedQuery(connection, REVERT_SELECT, gammaIdModsQID);
                     while (chStmt.next()) {
                        System.out.println(String.format("     Revert Artifact: Gammas To Remove %d",
                              chStmt.getInt("gamma_id")));
                     }
                  } finally {
                     ConnectionHandler.close(chStmt);
                  }
                  System.out.println(String.format("     Displayed all the data in %s", Lib.getElapseString(time)));
               }

               time = System.currentTimeMillis();
               count = ConnectionHandler.runPreparedUpdate(connection, DELETE_ATTRIBUTE_GAMMAS_REVERT, gammaIdModsQID);
               if (DEBUG) {
                  System.out.println(String.format("   Deleted %d attribute gammas for revert in %s", count,
                        Lib.getElapseString(time)));
               }
               time = System.currentTimeMillis();
               ConnectionHandler.runPreparedUpdate(connection, DELETE_RELATION_GAMMAS_REVERT, gammaIdModsQID);
               if (DEBUG) {
                  System.out.println(String.format("   Deleted %d relation gammas for revert in %s", count,
                        Lib.getElapseString(time)));
               }
               time = System.currentTimeMillis();
               ConnectionHandler.runPreparedUpdate(connection, DELETE_ARTIFACT_GAMMAS_REVERT, gammaIdModsQID);
               if (DEBUG) {
                  System.out.println(String.format("   Deleted %d artifact gammas for revert in %s", count,
                        Lib.getElapseString(time)));
               }

               time = System.currentTimeMillis();
               if (!gammaIdsBaseline.isEmpty()) {
                  gammaIdsBaseline.store(connection);
                  count =
                        ConnectionHandler.runPreparedUpdate(connection, SET_TX_CURRENT_REVERT,
                              gammaIdsBaseline.getQueryId());
                  if (DEBUG) {
                     System.out.println(String.format("   Set %d tx currents for revert in %s", count,
                           Lib.getElapseString(time)));

                     chStmt =
                           ConnectionHandler.runPreparedQuery(connection,
                                 "Select * from osee_join_transaction where query_id = ?",
                                 gammaIdsBaseline.getQueryId());
                     while (chStmt.next()) {
                        System.out.println(String.format(
                              " Revert Artifact: Baseline Gamma_id = %d Transaction Id = %d",
                              chStmt.getInt("gamma_id"), chStmt.getInt("transaction_id")));
                     }
                  }
               }

               time = System.currentTimeMillis();
               ConnectionHandler.runPreparedUpdate(connection, REMOVE_EMPTY_TRANSACTION_DETAILS, branchId);
               if (DEBUG) {
                  System.out.println(String.format("   Cleaned up the Transaction Detail Table in %s",
                        Lib.getElapseString(time)));
                  System.out.println(String.format(" Reverted the Artifact %d in %s", artId,
                        Lib.getElapseString(totalTime)));

               }

            } finally {
               gammaIdsModifications.delete(connection);
               gammaIdsBaseline.delete(connection);
               ConnectionHandler.close(chStmt);
            }
         }

      }

      @Override
      protected void handleTxFinally() throws Exception {
         super.handleTxFinally();
      }
   }

   public static Artifact getDefaultHierarchyRootArtifact(Branch branch, boolean createIfNecessary) throws OseeCoreException {
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
            OseeLog.log(SkynetActivator.class, Level.INFO,
                  "Created " + DEFAULT_HIERARCHY_ROOT_NAME + " because no root was found.");
            Artifact root =
                  ArtifactTypeManager.addArtifact(ROOT_ARTIFACT_TYPE_NAME, branch, DEFAULT_HIERARCHY_ROOT_NAME);
            root.persistAttributes();
            ArtifactCache.putByTextId(DEFAULT_HIERARCHY_ROOT_NAME, root);
            return root;
         }
         throw ex;
      }
   }

   public static Artifact getDefaultHierarchyRootArtifact(Branch branch) throws OseeCoreException {
      return getDefaultHierarchyRootArtifact(branch, false);
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

   private static final String INSERT_SELECT_RELATIONS =
         "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) SELECT ?, ?, txs1.gamma_id, txs1.transaction_id FROM osee_join_artifact al1, osee_relation_link rel1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND (al1.art_id = rel1.a_art_id OR al1.art_id = rel1.b_art_id) AND rel1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id";

   private static final String INSERT_SELECT_ATTRIBUTES =
         "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) SELECT ?, ?, txs1.gamma_id, txs1.transaction_id FROM osee_join_artifact al1, osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by al1.branch_id, al1.art_id";

   private static final String INSERT_SELECT_ARTIFACTS =
         "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) SELECT ?, ?, txs1.gamma_id, txs1.transaction_id FROM osee_join_artifact al1, osee_artifact art1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txd1.branch_id = al1.branch_id AND txd1.transaction_id = txs1.transaction_id";

   private static final String COUNT_ARTIFACT_VIOLATIONS =
         "SELECT art1.art_id, txd1.branch_id FROM osee_join_artifact al1, osee_artifact art1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txd1.branch_id = al1.branch_id AND txd1.transaction_id = txs1.transaction_id";
   private static final String DELETE_FROM_TXS_USING_JOIN_TRANSACTION =
         "DELETE FROM osee_txs txs1 WHERE EXISTS ( select 1 from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.transaction_id = txs1.transaction_id AND jt1.gamma_id = txs1.gamma_id)";
   private static final String DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION =
         "DELETE FROM osee_tx_details txd1 WHERE EXISTS ( select 1 from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.transaction_id = txd1.transaction_id AND not exists ( select * from osee_txs txs1 where txs1.transaction_id = jt1.transaction_id))";
   private static final String DELETE_FROM_RELATION_VERSIONS =
         "DELETE FROM osee_relation_link rel1 WHERE EXISTS ( select * from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.gamma_id = rel1.gamma_id AND not exists ( select * from osee_txs txs1 where txs1.gamma_id = jt1.gamma_id))";
   private static final String DELETE_FROM_ATTRIBUTE_VERSIONS =
         "DELETE FROM osee_attribute attr1 WHERE EXISTS ( select * from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.gamma_id = attr1.gamma_id AND not exists ( select * from osee_txs txs1 where txs1.gamma_id = jt1.gamma_id))";
   private static final String DELETE_FROM_ARTIFACT_VERSIONS =
         "DELETE FROM osee_artifact_version artv1 WHERE EXISTS ( select * from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.gamma_id = artv1.gamma_id AND not exists ( select * from osee_txs txs1 where txs1.gamma_id = jt1.gamma_id))";
   private static final String DELETE_FROM_ARTIFACT =
         "DELETE FROM osee_artifact art1 WHERE EXISTS ( select * from osee_join_artifact ja1 WHERE ja1.query_id = ? AND ja1.art_id = art1.art_id AND not exists ( select * from osee_artifact_version artv1 where artv1.art_id = ja1.art_id))";

   /**
    * @param artifactsToPurge
    * @param collection
    * @throws OseeCoreException
    */
   public static void purgeArtifacts(Collection<? extends Artifact> artifactsToPurge) throws OseeCoreException {
      //first determine if the purge is legal.
      List<Object[]> batchParameters = new ArrayList<Object[]>();
      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      OseeConnection connection = OseeDbConnection.getConnection();
      try {
         for (Artifact art : artifactsToPurge) {
            for (Branch branch : art.getBranch().getChildBranches(true)) {
               batchParameters.add(new Object[] {queryId, insertTime, art.getArtId(), branch.getBranchId(),
                     SQL3DataType.INTEGER});
            }
         }
         if (batchParameters.size() > 0) {
            ArtifactLoader.selectArtifacts(connection, batchParameters);
            ConnectionHandlerStatement chStmt = null;
            try {
               chStmt = ConnectionHandler.runPreparedQuery(connection, COUNT_ARTIFACT_VIOLATIONS, queryId);
               boolean failed = false;
               StringBuilder sb = new StringBuilder();
               while (chStmt.next()) {
                  failed = true;
                  sb.append("ArtifactId[");
                  sb.append(chStmt.getInt("art_id"));
                  sb.append("] BranchId[");
                  sb.append(chStmt.getInt("branch_id"));
                  sb.append("]\n");
               }
               if (failed) {
                  throw new OseeCoreException(String.format(
                        "Unable to purge because the following artifacts exist on child branches.\n%s", sb.toString()));
               }
            } finally {
               ArtifactLoader.clearQuery(connection, queryId);
               ConnectionHandler.close(chStmt);
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
         ArtifactLoader.selectArtifacts(connection, batchParameters);

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
         ArtifactLoader.clearQuery(connection, queryId);
         connection.close();
      }
   }
}