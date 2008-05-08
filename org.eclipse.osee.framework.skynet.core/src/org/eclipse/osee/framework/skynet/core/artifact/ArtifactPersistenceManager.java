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
import static org.eclipse.osee.framework.skynet.core.artifact.search.Operator.EQUAL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.schema.LocalAliasTable;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetArtifactEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.ArtifactVersionIncrementedEvent;
import org.eclipse.osee.framework.skynet.core.PersistenceManager;
import org.eclipse.osee.framework.skynet.core.PersistenceManagerInit;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.artifact.search.RelatedToSearch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeToTransactionOperation;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLink;
import org.eclipse.osee.framework.skynet.core.relation.LinkManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.tagging.SystemTagDescriptor;
import org.eclipse.osee.framework.skynet.core.tagging.TagManager;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionBuilder;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.transaction.data.ArtifactTransactionData;
import org.eclipse.osee.framework.skynet.core.utility.RemoteArtifactEventFactory;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class ArtifactPersistenceManager implements PersistenceManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactPersistenceManager.class);

   private static final String INSERT_ARTIFACT =
         "INSERT INTO " + ARTIFACT_TABLE + " (art_id, art_type_id, guid, human_readable_id) VALUES (?, ?, ?, ?)";

   private static final LocalAliasTable ARTIFACT_VERSION_ALIAS_1 = new LocalAliasTable(ARTIFACT_VERSION_TABLE, "t1");
   private static final LocalAliasTable ARTIFACT_VERSION_ALIAS_2 = new LocalAliasTable(ARTIFACT_VERSION_TABLE, "t2");
   private static final LocalAliasTable ATTRIBUTE_ALIAS_1 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t1");
   private static final LocalAliasTable ATTRIBUTE_ALIAS_2 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t2");

   private static final String PURGE_ARTIFACT = "DELETE FROM " + ARTIFACT_TABLE + " WHERE art_id = ?";
   private static final String PURGE_ARTIFACT_GAMMAS =
         "DELETE" + " FROM " + TRANSACTIONS_TABLE + " WHERE gamma_id IN" + "(SELECT gamma_id" + " FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE art_id = ? UNION " + "(SELECT gamma_id" + " FROM " + RELATION_LINK_VERSION_TABLE + " where a_art_id = ? " + " UNION SELECT gamma_id " + " FROM " + RELATION_LINK_VERSION_TABLE + " WHERE b_art_id = ?))";

   private static final String PURGE_ATTRIBUTE = "DELETE FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE attr_id = ?";
   private static final String PURGE_ATTRIBUTE_GAMMAS =
         "DELETE" + " FROM " + TRANSACTIONS_TABLE + " WHERE gamma_id IN" + "(SELECT gamma_id" + " FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE attr_id = ?)";

   // 'order by transaction_id desc' clause works with the DynamicAttributeManager to ignore effectively overwritten attributes with {min,max} of {0,1} with >1 attribute instances.
   private static final String SELECT_ATTRIBUTES_FOR_ARTIFACT =
         "SELECT " + ATTRIBUTE_ALIAS_1.columns("attr_id", "attr_type_id", "gamma_id", "value", "uri") + " FROM " + ATTRIBUTE_ALIAS_1 + "," + TRANSACTIONS_TABLE + " WHERE " + ATTRIBUTE_ALIAS_1.column("art_id") + "=?" + " AND " + ATTRIBUTE_ALIAS_1.column("modification_id") + "<> ?" + " AND " + ATTRIBUTE_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT MAX(" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ")" + " FROM " + ATTRIBUTE_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ATTRIBUTE_ALIAS_2.column("attr_id") + "=" + ATTRIBUTE_ALIAS_1.column("attr_id") + " AND " + ATTRIBUTE_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " <= ?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)" + " ORDER BY " + TRANSACTIONS_TABLE.column("transaction_id") + " DESC";

   private static final String UPDATE_ARTIFACT_TYPE = "UPDATE osee_define_artifact SET art_type_id = ? WHERE art_id =?";

   private SkynetTransactionManager transactionManager;
   private TransactionIdManager transactionIdManager;
   private ConfigurationPersistenceManager configurationManager;
   private RelationPersistenceManager relationManager;
   private AccessControlManager accessControlManager;
   private TagManager tagManager;
   private BranchPersistenceManager branchManager;
   private ExtensionDefinedObjects<IAttributeSaveListener> attributeSaveListeners;

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
      PersistenceManagerInit.initManagerWeb(instance);
      return instance;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.PersistenceManager#setRelatedManagers()
    */
   public void onManagerWebInit() throws Exception {
      configurationManager = ConfigurationPersistenceManager.getInstance();
      relationManager = RelationPersistenceManager.getInstance();
      transactionIdManager = TransactionIdManager.getInstance();
      transactionManager = SkynetTransactionManager.getInstance();
      accessControlManager = AccessControlManager.getInstance();
      tagManager = TagManager.getInstance();
      branchManager = BranchPersistenceManager.getInstance();
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

   public void makePersistent(Artifact artifact) throws SQLException {
      makePersistent(artifact, false, true);
   }

   public void makePersistent(Artifact artifact, boolean recurse) throws SQLException {
      makePersistent(artifact, recurse, true);
   }

   public void makePersistent(final Artifact artifact, final boolean recurse, boolean persistAttributes) throws SQLException {
      AbstractSkynetTxTemplate artifactPersistTx = new AbstractSkynetTxTemplate(artifact.getBranch()) {
         @Override
         protected void handleTxWork() throws Exception {
            saveTrace(artifact, recurse, getTxBuilder());
         }
      };
      try {
         artifactPersistTx.execute();
      } catch (Exception ex) {
         throw new SQLException(ex);
      }
   }

   public void saveTrace(Artifact artifact, boolean recurse, SkynetTransactionBuilder builder) throws Exception {
      if (artifact.isInAttributeInitialization()) throw new IllegalArgumentException(
            "The artifact is in attribute initialization still");
      if (!accessControlManager.checkObjectPermission(artifact.getBranch(), PermissionEnum.WRITE)) throw new IllegalArgumentException(
            "No write permissions for the branch that this artifact belongs to:" + artifact.getBranch());
      if (artifact.getPersistenceMemo() != null && !artifact.getPersistenceMemo().getTransactionId().isEditable()) throw new IllegalArgumentException(
            "The artifact " + artifact.getGuid() + " must be at the head of the branch to be edited.");

      if (artifact.isDirty() && !artifact.isInTransaction()) {
         builder.addArtifact(artifact);
      }

      if (recurse) artifact.getLinkManager().traceLinks(recurse, builder);
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

   public void doSave(Artifact artifact, SkynetTransaction transaction, boolean persistAttributes) throws Exception {
      workingOn(artifact.getDescriptiveName());
      boolean newArtifact = artifact.getPersistenceMemo() == null;
      ModificationType modType;

      if (newArtifact) {
         modType = ModificationType.NEW;
      } else {
         modType = ModificationType.CHANGE;
      }

      int artGamma = SkynetDatabase.getNextGammaId();

      if (newArtifact) {
         addArtifactData(artifact, transaction, artGamma);
      }

      processTransactionForArtifact(artifact, modType, transaction, artGamma);

      if (persistAttributes) {
         notifyOnAttributeSave(artifact);

         // Add Attributes to Transaction
         AttributeToTransactionOperation operation = new AttributeToTransactionOperation(artifact, transaction);
         operation.execute();

         if (modType == ModificationType.NEW ? false : true) {
            transaction.addRemoteEvent(RemoteArtifactEventFactory.makeEvent(artifact,
                  transaction.getTransactionNumber()));
         }
         artifact.setInTransaction(true);
      }
      workedOneUnit();
   }

   private void processTransactionForArtifact(Artifact artifact, ModificationType modType, SkynetTransaction transaction, int artGamma) throws SQLException {
      transaction.addTransactionDataItem(new ArtifactTransactionData(artifact, artGamma,
            transaction.getTransactionNumber(), modType, transaction.getBranch()));
   }

   private void addArtifactData(Artifact artifact, SkynetTransaction transaction, int gammaId) throws SQLException {
      int artId = SkynetDatabase.getNextArtifactId();

      transaction.addToBatch(INSERT_ARTIFACT, SQL3DataType.INTEGER, artId, SQL3DataType.INTEGER,
            artifact.getArtTypeId(), SQL3DataType.VARCHAR, artifact.getGuid(), SQL3DataType.VARCHAR,
            artifact.getHumanReadableId());

      TransactionId transactionId = transactionIdManager.getEditableTransactionId(artifact.getBranch());
      artifact.setPersistenceMemo(new ArtifactPersistenceMemo(transactionId, artId, gammaId));
   }

   private static final String SELECT_ARTIFACT_START =
         "SELECT art1.*, arv1.gamma_id, txs1.* FROM osee_define_artifact art1, osee_define_artifact_version arv1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE ";
   private static final String SELECT_ARTIFACT_END =
         " AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= ? AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? order by txs1.transaction_id desc";
   private static final String SELECT_ARTIFACT_BY_GUID = SELECT_ARTIFACT_START + "art1.guid =?" + SELECT_ARTIFACT_END;
   private static final String SELECT_ARTIFACT_BY_ID = SELECT_ARTIFACT_START + "art1.art_id =?" + SELECT_ARTIFACT_END;

   /**
    * This method acquires <code>Artifact</code>'s directly from the database. This should only be called by
    * factories since all caching is performed by the factory.
    * 
    * @param guid The guid of the artifact.
    * @return The <code>Artifact</code> from the database that corresponds to the supplied guid.
    * @throws SQLException
    */
   public Artifact getArtifact(String guid, TransactionId transactionId) throws SQLException {
      return getArtifactInternal(transactionId, SELECT_ARTIFACT_BY_GUID, SQL3DataType.VARCHAR, guid, -1, true);
   }

   public Artifact getArtifactFromId(int artId, TransactionId transactionId) throws SQLException, IllegalArgumentException {
      return getArtifactInternal(transactionId, SELECT_ARTIFACT_BY_ID, SQL3DataType.INTEGER, null, artId, false);
   }

   private Artifact getArtifactInternal(TransactionId transactionId, String query, SQL3DataType sqlDataType, String guid, int artId, boolean useGuid) throws SQLException {
      // First try to acquire the artifact from cache
      Artifact artifact;
      Object data;
      String idString;
      if (useGuid) {
         artifact = ArtifactCache.getInstance().getArtifactFromCache(guid, transactionId);
         data = guid;
         idString = "guid \"" + guid + "\"";
      } else {
         artifact = ArtifactCache.getInstance().getArtifactFromCache(artId, transactionId);
         data = artId;
         idString = "id \"" + artId + "\"";
      }

      // If it wasn't found, then it must be acquired from the database
      if (artifact == null) {
         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt =
                  ConnectionHandler.runPreparedQuery(1, query, sqlDataType, data, SQL3DataType.INTEGER,
                        transactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                        transactionId.getBranch().getBranchId());

            ResultSet rSet = chStmt.getRset();
            if (!rSet.next()) {
               throw new IllegalStateException(
                     "The artifact with " + idString + " or does not exist for transaction \"" + transactionId + "\"");
            }

            artifact = prepArtifact(rSet, transactionId);

            setAttributesOnArtifact(artifact);
            // This artifact is fresh, so mark it as not dirty
            artifact.setNotDirty();

            artifact.onInitializationComplete();
         } finally {
            DbUtil.close(chStmt);
         }
      }

      return artifact;
   }

   private static final String ARTIFACT_SELECT =
         "SELECT " + ARTIFACT_TABLE.column("*") + ", " + TRANSACTIONS_TABLE.column("*") + " FROM " + ARTIFACT_TABLE + "," + ARTIFACT_VERSION_ALIAS_1 + ", " + TRANSACTIONS_TABLE + " WHERE " + ARTIFACT_TABLE.column("art_id") + "=" + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " AND " + TRANSACTIONS_TABLE.column("gamma_id") + "=" + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + ARTIFACT_VERSION_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_VERSION_ALIAS_2.column("art_id") + "=" + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<= ?)" + " AND " + ARTIFACT_VERSION_ALIAS_1.column("modification_id") + "<>" + ModificationType.DELETED + " AND ";

   private static final String ARTIFACT_ID_SELECT =
         "SELECT " + ARTIFACT_TABLE.columns("art_id") + " FROM " + ARTIFACT_TABLE + " WHERE ";

   private static final String ARTIFACT_COUNT_SELECT = "SELECT COUNT(*) artifacts FROM " + ARTIFACT_TABLE + " WHERE ";

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
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(sql, dataList.toArray());

         chStmt.next();
         return chStmt.getRset().getInt("artifacts");
      } finally {
         DbUtil.close(chStmt);
      }
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

   private String getSql(List<ISearchPrimitive> searchCriteria, boolean all, List<Object> dataList, TransactionId transactionId) throws SQLException {
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(transactionId.getBranch().getBranchId());
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(transactionId.getTransactionNumber());

      return getSql(searchCriteria, all, ARTIFACT_SELECT, dataList, transactionId.getBranch());
   }

   public Collection<Artifact> getArtifacts(ISearchPrimitive searchCriteria, TransactionId transactionId) throws SQLException {
      LinkedList<Object> dataList = new LinkedList<Object>();
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(transactionId.getBranch().getBranchId());
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(transactionId.getTransactionNumber());

      return getArtifacts(getSql(searchCriteria, ARTIFACT_SELECT, dataList, transactionId.getBranch()), dataList,
            transactionId, null);
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

   public int getArtifactsCount(List<ISearchPrimitive> searchCriteria, boolean all, Branch branch) throws SQLException {
      StringBuilder sql = new StringBuilder();
      LinkedList<Object> dataList = new LinkedList<Object>();
      int count = 0;

      sql.append("SELECT COUNT(criteriaTable.art_id) as art_count FROM (");
      sql.append(getSql(searchCriteria, all, dataList, transactionIdManager.getEditableTransactionId(branch)));
      sql.append(") criteriaTable");

      ConnectionHandlerStatement chStmt = ConnectionHandler.runPreparedQuery(sql.toString(), dataList.toArray());

      if (chStmt.next()) {
         count = chStmt.getRset().getInt("art_count");
      }
      DbUtil.close(chStmt);

      return count;
   }

   public Collection<Artifact> getArtifacts(ISearchPrimitive searchCriteria, Branch branch) throws SQLException {
      return getArtifacts(searchCriteria, transactionIdManager.getEditableTransactionId(branch));
   }

   public Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, Branch branch) throws SQLException {
      return getArtifacts(searchCriteria, all, branch, null);
   }

   public Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, Branch branch, ISearchConfirmer confirmer) throws SQLException {
      LinkedList<Object> dataList = new LinkedList<Object>();
      TransactionId transactionId = transactionIdManager.getEditableTransactionId(branch);
      return getArtifacts(getSql(searchCriteria, all, dataList, transactionId), dataList, transactionId, confirmer);
   }

   public Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, TransactionId transactionId) throws SQLException {
      LinkedList<Object> dataList = new LinkedList<Object>();
      return getArtifacts(getSql(searchCriteria, all, dataList, transactionId), dataList, transactionId, null);
   }

   public Collection<Artifact> getArtifacts(String sql, List<Object> dataList, TransactionId transactionId, ISearchConfirmer confirmer) throws SQLException {
      Collection<Artifact> artifacts = new ArrayList<Artifact>(50);
      Collection<Artifact> artifactsToInit = new LinkedList<Artifact>();

      ConnectionHandlerStatement chStmt = null;
      int artifactsCount = 0;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(sql, dataList.toArray());
         ResultSet rSet = chStmt.getRset();
         while (rSet.next()) {
            artifactsCount++;

            Artifact artifact = ArtifactCache.getInstance().getArtifactFromCache(rSet.getInt("art_id"), transactionId);
            if (artifact == null) { // if not in cache
               artifact = prepArtifact(rSet, transactionId);
               artifactsToInit.add(artifact);
            }
            artifacts.add(artifact);
         }
      } finally {
         DbUtil.close(chStmt);
      }

      if (confirmer == null || confirmer.canProceed(artifactsCount)) {
         initializeArtifacts(artifactsToInit, transactionId);
      } else {
         artifacts.clear();
      }

      return artifacts;
   }

   private Artifact prepArtifact(ResultSet rSet, TransactionId transactionId) throws SQLException {
      ArtifactSubtypeDescriptor artifactType =
            configurationManager.getArtifactSubtypeDescriptor(rSet.getInt("art_type_id"));
      IArtifactFactory factory = artifactType.getFactory();

      Artifact artifact =
            factory.getNewArtifact(rSet.getString("guid"), rSet.getString("human_readable_id"),
                  artifactType.getFactoryKey(), transactionId.getBranch(), artifactType);
      artifact.setPersistenceMemo(new ArtifactPersistenceMemo(transactionId, rSet.getInt("art_id"),
            rSet.getInt("gamma_id")));

      if (rSet.getInt("mod_type") == ModificationType.DELETED.getValue()) {
         artifact.setDeleted(rSet.getInt("transaction_id"));
      }

      return artifact;
   }

   /**
    * Acquires the user defined attributes for an artifact. If none are in the table, then it returns the 'default set'
    * of attributes for the artifact.
    * 
    * @param artifact The artifact to acquire the attributes for.
    * @param branch The tag to get the data for.
    * @throws SQLException
    */
   protected void setAttributesOnArtifact(Artifact artifact) throws SQLException {
      HashMap<Integer, DynamicAttributeManager> typeHash = new HashMap<Integer, DynamicAttributeManager>();
      TransactionId transactionId;

      if (artifact.getPersistenceMemo() != null)
         transactionId = artifact.getPersistenceMemo().getTransactionId();
      else
         transactionId = transactionIdManager.getEditableTransactionId(artifact.getBranch());

      DynamicAttributeManager attributeManager;

      Collection<AttributeType> attributeTypeDescriptors =
            configurationManager.getAttributeTypesFromArtifactType(artifact.getArtifactType(), artifact.getBranch());
      for (AttributeType attributeType : attributeTypeDescriptors) {
         attributeManager = attributeType.createAttributeManager(artifact, false);
         attributeManager.setupForInitialization(false);
         typeHash.put(attributeType.getAttrTypeId(), attributeManager);
      }

      ConnectionHandlerStatement chStmt = null;
      try {
         if (artifact.getPersistenceMemo() != null) {
            // Acquire previously stored attributes
            chStmt =
                  ConnectionHandler.runPreparedQuery(SELECT_ATTRIBUTES_FOR_ARTIFACT, SQL3DataType.INTEGER,
                        artifact.getArtId(), SQL3DataType.INTEGER, ModificationType.DELETED.getValue(),
                        SQL3DataType.INTEGER, transactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                        transactionId.getBranch().getBranchId());

            if (chStmt.next()) {
               Attribute attribute;
               int attrTypeId;
               DynamicAttributeManager type;

               do {
                  attrTypeId = chStmt.getRset().getInt("attr_type_id");
                  type = typeHash.get(attrTypeId);

                  // This handles attributes that have been stored on the Artifact but are not
                  // currently
                  // defined in the schema as being appropriate for this Artifact.
                  if (type == null) {
                     type = AttributeTypeManager.getType(attrTypeId).createAttributeManager(artifact, false);
                     type.setupForInitialization(false);
                     typeHash.put(attrTypeId, type);
                  }

                  try {
                     String varchar = chStmt.getRset().getString("value");
                     attribute = type.injectFromDb(varchar, chStmt.getRset().getString("uri"));

                  } catch (SQLException e) {
                     throw new RuntimeException(e);
                  }

                  attribute.setPersistenceMemo(new AttributeMemo(chStmt.getRset().getInt("attr_id"),
                        chStmt.getRset().getInt("gamma_id")));
               } while (chStmt.next());
            }
         }
      } finally {
         DbUtil.close(chStmt);
      }

      Collection<DynamicAttributeManager> attributes = typeHash.values();
      // Finalize the initialization of all the attribute sets
      for (DynamicAttributeManager attr : attributes)
         attr.enforceMinMaxConstraints();

      artifact.setAttributeManagers(attributes);
   }

   /**
    * Place all of the information from the database for a set of artifacts into the artifacts. It is expected that the
    * artifacts already have the appropriate guids and tags for this call to succeed.
    * 
    * @param artifacts
    * @throws SQLException
    */
   public void initializeArtifacts(Collection<Artifact> artifacts, TransactionId transactionId) throws SQLException {
      if (artifacts.isEmpty()) {
         return;
      }

      initializeArtifactsAttributes(artifacts, transactionId);

      for (Artifact artifact : artifacts) {
         artifact.setNotDirty(); // The artifacts are fresh, so mark them as not dirty
         artifact.onInitializationComplete();
      }
   }

   private void initializeArtifactsAttributes(Collection<Artifact> artifacts, TransactionId transactionId) throws SQLException {
      DoubleKeyHashMap<Integer, Integer, DynamicAttributeManager> typeHash =
            new DoubleKeyHashMap<Integer, Integer, DynamicAttributeManager>();
      DynamicAttributeManager attributeManager;

      for (Artifact artifact : artifacts) {
         for (AttributeType attributeType : configurationManager.getAttributeTypesFromArtifactType(
               artifact.getArtifactType(), artifact.getBranch())) {
            attributeManager = attributeType.createAttributeManager(artifact, false);
            attributeManager.setupForInitialization(false);
            typeHash.put(artifact.getArtId(), attributeType.getAttrTypeId(), attributeManager);
         }
      }

      // Can only ask for 1000 distinct values at a time
      ArrayList<Artifact> tempArts = new ArrayList<Artifact>(artifacts);
      while (!tempArts.isEmpty()) {

         String artIdList = getArtIdList(tempArts);
         // Acquire previously stored attributes
         // NOTE: the 'order by transaction_id desc' clause works with the DynamicAttributeManager
         // to allow
         // it to ignore effectively overwritten attributes with {min,max} of {0,1} with >1
         // attribute instances.
         ConnectionHandlerStatement chStmt =
               ConnectionHandler.runPreparedQuery(
                     "SELECT " + ATTRIBUTE_VERSION_TABLE.columns("art_id", "attr_id", "attr_type_id", "gamma_id",
                           "value", "uri") + " FROM " + ATTRIBUTE_VERSION_TABLE + "," + TRANSACTIONS_TABLE

                     + ", (SELECT " + ATTRIBUTE_VERSION_TABLE.column("attr_id") + ", MAX(" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ") AS last_transaction_id FROM " + ATTRIBUTE_VERSION_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ATTRIBUTE_VERSION_TABLE.column("art_id") + " IN (" + artIdList + ") " + " AND " + ATTRIBUTE_VERSION_TABLE.column("gamma_id") + EQUAL + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + EQUAL + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " GROUP BY attr_id) t1 " + " WHERE " + TRANSACTIONS_TABLE.column("transaction_id") + "= t1.last_transaction_id" + " AND " + ATTRIBUTE_VERSION_TABLE.column("attr_id") + "= t1.attr_id" + " AND " + ATTRIBUTE_VERSION_TABLE.column("modification_id") + "<> ?" + " AND " + ATTRIBUTE_VERSION_TABLE.column("art_id") + " IN (" + artIdList + ")" + " AND " + ATTRIBUTE_VERSION_TABLE.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " ORDER BY " + TRANSACTIONS_TABLE.column("transaction_id") + " DESC",
                     SQL3DataType.INTEGER, transactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                     transactionId.getBranch().getBranchId(), SQL3DataType.INTEGER, ModificationType.DELETED.getValue());

         Attribute attribute;
         int artId;
         int lastArtId = -1; // Set to an invalid value to force a trigger on the first run of the
         // loop

         int attrTypeId;
         Artifact artifact = null;
         ResultSet rSet = chStmt.getRset();
         while (rSet.next()) {
            artId = rSet.getInt("art_id");

            // Get a new artifact reference if the ID has changed
            if (artId != lastArtId) {
               lastArtId = artId;
               artifact = getArtifactFromId(artId, transactionId);
            }

            attrTypeId = rSet.getInt("attr_type_id");
            attributeManager = typeHash.get(artId, attrTypeId);
            if (attributeManager == null) {
               attributeManager = AttributeTypeManager.getType(attrTypeId).createAttributeManager(artifact, false);
               typeHash.put(artId, attrTypeId, attributeManager);
               attributeManager.setupForInitialization(false);
            }

            attribute = attributeManager.injectFromDb(rSet.getString("value"), rSet.getString("uri"));
            attribute.setPersistenceMemo(new AttributeMemo(rSet.getInt("attr_id"), rSet.getInt("gamma_id")));

         }
         DbUtil.close(chStmt);
      }

      // Finalize the initialization of all the attribute sets
      for (DynamicAttributeManager attr : typeHash.allValues())
         attr.enforceMinMaxConstraints();

      Map<Integer, DynamicAttributeManager> attributeMap;
      for (Artifact artifact : artifacts) {
         attributeMap = typeHash.getSubHash(artifact.getArtId());
         // The attributeMap.values() is wrapped with a new LinkedList because
         // the returned
         // collection is backed by the hashmap. This causes a lot of
         // restrictions of the collection
         // such as not being able to add, and that will not work.
         artifact.setAttributeManagers(new LinkedList<DynamicAttributeManager>(attributeMap.values()));
      }
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
    * @param artifacts The artifacts to delete.
    * @throws SQLException
    */
   public void deleteArtifact(final Artifact... artifacts) throws Exception {
      if (artifacts.length == 0) return;

      // Confirm artifacts are fit to delete
      for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
         Result result = check.isDeleteable(Arrays.asList(artifacts));
         if (result.isFalse()) throw new IllegalStateException(result.getText());
      }

      final Branch branch = artifacts[0].getBranch();
      if (transactionManager.isInBatch(branch)) {
         for (Artifact artifact : artifacts) {
            deleteTrace(artifact, transactionManager.getTransactionBuilder(branch));
         }
      } else {
         String listDescription = (artifacts.length == 1) ? artifacts[0].getDescriptiveName() : "Artifacts";
         Job job = new Job("Delete " + listDescription + " and Children") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
               IStatus toReturn = Status.CANCEL_STATUS;
               monitor.beginTask("Delete artifacts", artifacts.length);

               AbstractSkynetTxTemplate deleteTx = new AbstractSkynetTxTemplate(branch) {
                  @Override
                  protected void handleTxWork() throws Exception {
                     for (Artifact artifact : artifacts) {
                        if (!artifact.isDeleted()) {
                           monitor.setTaskName("delete " + artifact.getDescriptiveName());
                           deleteTrace(artifact, getTxBuilder());
                           monitor.worked(1);
                        }
                     }
                  }
               };

               try {
                  deleteTx.execute();
                  toReturn = Status.OK_STATUS;
               } catch (Exception ex) {
                  logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                  toReturn = new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, -1, ex.getMessage(), ex);
               } finally {
                  monitor.done();
               }

               return toReturn;
            }
         };
         Jobs.startJob(job);
      }
   }

   /**
    * @param artifact
    * @param builder
    * @throws Exception
    */
   public void deleteTrace(Artifact artifact, SkynetTransactionBuilder builder) throws Exception {
      if (!artifact.isDeleted()) {
         // This must be done first since the the actual deletion of an artifact clears out the link manager
         for (Artifact childArtifact : artifact.getChildren()) {
            deleteTrace(childArtifact, builder);
         }

         builder.deleteArtifact(artifact);

         artifact.setDeleted();
      }
   }

   /**
    * @param artifact
    * @param transaction
    * @throws Exception
    */
   public synchronized void doDelete(Artifact artifact, SkynetTransaction transaction, SkynetTransactionBuilder builder) throws Exception {
      if (!artifact.isInDb()) return;

      LinkManager linkManager = artifact.getLinkManager();

      processTransactionForArtifact(artifact, ModificationType.DELETED, transaction, SkynetDatabase.getNextGammaId());

      transaction.addRemoteEvent(new NetworkArtifactDeletedEvent(artifact.getBranch().getBranchId(),
            transaction.getTransactionNumber(), artifact.getArtId(), artifact.getArtTypeId(),
            artifact.getFactory().getClass().getCanonicalName(),
            SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId()));
      transaction.addLocalEvent(new TransactionArtifactModifiedEvent(artifact.getGuid(), artifact.getBranch(),
            ModType.Deleted, this));

      linkManager.deleteAllLinks();
      linkManager.traceLinks(false, builder);
      tagManager.clearTags(artifact, SystemTagDescriptor.AUTO_INDEXED.getDescriptor());
   }

   /**
    * this method does not update the in memory model or send events. It also does not purge any child artifacts. The
    * more full featured version of this method takes an artifact as an argument rather than and artifact id.
    * 
    * @param artifactId
    * @throws SQLException
    */
   public void purgeArtifact(int artifactId) throws SQLException {
      ConnectionHandler.runPreparedUpdate(PURGE_ARTIFACT_GAMMAS, SQL3DataType.INTEGER, artifactId,
            SQL3DataType.INTEGER, artifactId, SQL3DataType.INTEGER, artifactId);
      ConnectionHandler.runPreparedUpdate(PURGE_ARTIFACT, SQL3DataType.INTEGER, artifactId);
      // System.out.println("Purge empty transactions");
      // ConnectionHandler.runPreparedUpdate(PURGE_EMPTY_TRANSACTIONS);
   }

   /**
    * Removes an artifact, it's attributes and any relations that have become invalid from the removal of this artifact
    * from the database. It also removes all history associated with this artifact (i.e. all transactions and gamma ids
    * will also be removed from the database).
    * 
    * @param artifact
    * @throws SQLException
    */
   public void purgeArtifact(final Artifact artifact) throws SQLException {
      purgeArtifact(artifact.getArtId());

      System.out.println("number of children:" + artifact.getChildren().size());
      for (Artifact child : artifact.getChildren()) {
         purgeArtifact(child);
      }

      artifact.getLinkManager().purge();
      artifact.setDeleted();
      SkynetEventManager.getInstance().kick(new TransactionArtifactModifiedEvent(artifact, ModType.Purged, this));
   }

   public Artifact getDefaultHierarchyRootArtifact(Branch branch, boolean createIfNecessary) throws SQLException {
      Collection<Artifact> artifacts = ArtifactQuery.getArtifactsFromType(ROOT_ARTIFACT_TYPE_NAME, branch);

      if (artifacts.size() == 0) {
         if (createIfNecessary) {
            logger.log(Level.INFO, "Created " + DEFAULT_HIERARCHY_ROOT_NAME + " becuase no root was found.");
            return createRoot(branch);
         }
         throw new IllegalStateException("No root artifact was found");
      }
      if (artifacts.size() > 1) {
         throw new IllegalStateException(String.format("There must be exactly one Root Artifact - not %d",
               artifacts.size()));
      }
      return artifacts.iterator().next();
   }

   public Artifact getDefaultHierarchyRootArtifact(Branch branch) throws SQLException {
      return getDefaultHierarchyRootArtifact(branch, false);
   }

   /**
    * Updates local cache
    * 
    * @param event
    */
   public void updateArtifactCache(ISkynetArtifactEvent event, Collection<Event> localEvents, TransactionId newTransactionId, TransactionId notEditableTransactionId) {
      try {
         int artId = event.getArtId();
         int branchId = event.getBranchId();
         String factoryName = event.getFactoryName();
         Collection<IRelationLink> links;
         ArtifactFactory<?> factory = (ArtifactFactory<?>) configurationManager.getFactoryFromName(factoryName);

         if (factory == null) throw new IllegalArgumentException(
               "The factory for this artifact remote event could not be determined.");

         Artifact oldArtifact = ArtifactCache.getArtifact(artId, branchManager.getBranch(branchId));
         if (oldArtifact != null) {

            // this forces the links to load
            oldArtifact.isDirty(true);

            if (newTransactionId.getTransactionNumber() != notEditableTransactionId.getTransactionNumber()) {
               oldArtifact.getPersistenceMemo().setTransactionId(notEditableTransactionId);
            }

            if (event instanceof NetworkArtifactModifiedEvent) {
               // only if links are loaded
               if (oldArtifact.isLinkManagerLoaded()) {
                  links = oldArtifact.getLinkManager().getLinks();
               } else {
                  links = new ArrayList<IRelationLink>(0);
               }

               Artifact newArtifact = (Artifact) oldArtifact.clone();
               setChangedAttributesOnNewArtifact(newArtifact,
                     ((NetworkArtifactModifiedEvent) event).getAttributeChanges());
               relationManager.resetLinksToNewArtifact(newArtifact, oldArtifact, links);
               newArtifact.setNotDirty();

               localEvents.add(new ArtifactVersionIncrementedEvent(oldArtifact, newArtifact, this));
            } else if (event instanceof NetworkArtifactDeletedEvent) {
               oldArtifact.setDeleted();
               localEvents.add(new TransactionArtifactModifiedEvent(oldArtifact, ModType.Deleted, this));
            }
         }
      } catch (Exception e) {
         logger.log(Level.SEVERE, e.toString(), e);
      }
   }

   private void setChangedAttributesOnNewArtifact(Artifact artifact, Collection<SkynetAttributeChange> changes) throws SQLException {
      for (SkynetAttributeChange change : changes) {
         artifact.setAttribute(change);
      }
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

   public Collection<Artifact> getArtifactsFromAttribute(String attributeName, String attributeValue, Branch branch) throws SQLException {
      return getArtifacts(new AttributeValueSearch(attributeName, attributeValue, Operator.EQUAL), branch);
   }

   private Artifact createRoot(Branch branch) throws SQLException {
      ArtifactSubtypeDescriptor descriptor = configurationManager.getArtifactSubtypeDescriptor(ROOT_ARTIFACT_TYPE_NAME);
      Artifact root = descriptor.makeNewArtifact(branch);
      root.setDescriptiveName(DEFAULT_HIERARCHY_ROOT_NAME);
      root.persistAttributes();
      return root;
   }

   /**
    * Changes the artifact type
    * 
    * @param artifact
    * @param artifactType
    * @throws SQLException
    */
   public void changeArtifactSubStype(Artifact artifact, ArtifactSubtypeDescriptor artifactType) throws SQLException {
      ConnectionHandler.runPreparedUpdate(UPDATE_ARTIFACT_TYPE, SQL3DataType.INTEGER, artifactType.getArtTypeId(),
            SQL3DataType.INTEGER, artifact.getArtId());
   }

   /**
    * Purge attribute from the database.
    * 
    * @param attribute
    */
   public void purgeAttribute(Attribute attribute) throws SQLException {
      ConnectionHandler.runPreparedUpdate(PURGE_ATTRIBUTE_GAMMAS, SQL3DataType.INTEGER,
            attribute.getPersistenceMemo().getAttrId());
      ConnectionHandler.runPreparedUpdate(PURGE_ATTRIBUTE, SQL3DataType.INTEGER,
            attribute.getPersistenceMemo().getAttrId());
   }
}