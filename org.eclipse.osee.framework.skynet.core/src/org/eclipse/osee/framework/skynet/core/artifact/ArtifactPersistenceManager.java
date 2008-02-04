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

import static org.eclipse.osee.framework.skynet.core.artifact.search.Operator.EQUAL;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_TYPE_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.FACTORY_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TXD_COMMENT;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
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
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryCache;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactHridSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.artifact.search.RelatedToSearch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
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
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.osee.framework.skynet.core.transaction.data.ArtifactTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.data.AttributeTransactionData;
import org.eclipse.osee.framework.skynet.core.utility.RemoteArtifactEventFactory;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;
import org.eclipse.osee.framework.ui.plugin.util.db.RsetProcessor;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.LocalAliasTable;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.Table;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class ArtifactPersistenceManager implements PersistenceManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactPersistenceManager.class);

   private static final String INSERT_ARTIFACT =
         "INSERT INTO " + ARTIFACT_TABLE + " (art_id, art_type_id, guid, human_readable_id) VALUES (?, ?, ?, ?)";

   private static final String UPDATE_ATTRIBUTE =
         "UPDATE " + ATTRIBUTE_VERSION_TABLE + " SET value = ?, content = ? WHERE art_id = ? and attr_id = ? and gamma_id = ?";

   private static final String UPDATE_TRANSACTION_TABLE =
         " UPDATE " + TRANSACTION_DETAIL_TABLE + " SET " + TRANSACTION_DETAIL_TABLE.column(TXD_COMMENT) + " = ?, TIME = ? WHERE transaction_id = (SELECT transaction_id FROM " + TRANSACTIONS_TABLE + " WHERE gamma_id = ? AND branch_id = ?)";

   private static final LocalAliasTable ARTIFACT_VERSION_ALIAS_1 = new LocalAliasTable(ARTIFACT_VERSION_TABLE, "t1");
   private static final LocalAliasTable ARTIFACT_VERSION_ALIAS_2 = new LocalAliasTable(ARTIFACT_VERSION_TABLE, "t2");
   private static final LocalAliasTable ATTRIBUTE_ALIAS_1 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t1");
   private static final LocalAliasTable ATTRIBUTE_ALIAS_2 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t2");

   private static final String SELECT_ARTIFACT_TYPE_NAME =
         "SELECT name FROM " + ARTIFACT_TYPE_TABLE + " WHERE art_type_id = ?";

   private static final String PURGE_ARTIFACT = "DELETE FROM " + ARTIFACT_TABLE + " WHERE art_id = ?";
   private static final String PURGE_ARTIFACT_GAMMAS =
         "DELETE" + " FROM " + TRANSACTIONS_TABLE + " WHERE gamma_id IN" + "(SELECT gamma_id" + " FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE art_id = ? UNION " + "(SELECT gamma_id" + " FROM " + RELATION_LINK_VERSION_TABLE + " where a_art_id = ? " + " UNION SELECT gamma_id " + " FROM " + RELATION_LINK_VERSION_TABLE + " WHERE b_art_id = ?))";

   private static final String PURGE_ATTRIBUTE = "DELETE FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE attr_id = ?";
   private static final String PURGE_ATTRIBUTE_GAMMAS =
         "DELETE" + " FROM " + TRANSACTIONS_TABLE + " WHERE gamma_id IN" + "(SELECT gamma_id" + " FROM " + ATTRIBUTE_VERSION_TABLE + " WHERE attr_id = ?)";

   private static final String SELECT_ARTIFACT_FOR_INIT =
         "SELECT art1.art_type_id, arv2.gamma_id, arv2.modification_id FROM osee_define_artifact art1, osee_define_artifact_version arv2, osee_define_txs txs3, osee_define_tx_details txd4 WHERE art1.art_id = ? AND art1.art_id = arv2.art_id AND arv2.gamma_id = txs3.gamma_id AND txs3.transaction_id <= ? AND txs3.transaction_id = txd4.transaction_id AND txd4.branch_id = ? order by txs3.transaction_id desc";

   private static final String SELECT_ARTIFACT_BY_GUID =
         "SELECT " + ARTIFACT_TABLE.columns("art_id", "art_type_id", "guid", "human_readable_id") + ", " + ARTIFACT_TYPE_TABLE.columns(
               "factory_id", "factory_key") + "," + FACTORY_TABLE.column("factory_class") + " FROM " + ARTIFACT_TABLE + "," + ARTIFACT_VERSION_ALIAS_1 + "," + FACTORY_TABLE + ", " + ARTIFACT_TYPE_TABLE + "," + TRANSACTIONS_TABLE + " WHERE " + ARTIFACT_TABLE.column("guid") + " = ?" + " AND " + ARTIFACT_TABLE.column("art_type_id") + "=" + ARTIFACT_TYPE_TABLE.column("art_type_id") + " AND " + ARTIFACT_TYPE_TABLE.column("factory_id") + "=" + FACTORY_TABLE.column("factory_id") + " AND " + ARTIFACT_TABLE.column("art_id") + "=" + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + ARTIFACT_VERSION_ALIAS_1.column("modification_id") + "<>?" + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + ARTIFACT_VERSION_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_VERSION_ALIAS_2.column("art_id") + "=" + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   // this query implicitly filters out transactions were an artifact was deleted because those transactions will not also contain matching attribute value
   private static final String SELECT_HISTORICAL_ARTIFACTS =
         String.format(
               "SELECT att1.art_id, %s FROM osee_define_attribute att1, osee_define_txs txs2, osee_define_tx_details txd3, osee_define_branch br4 WHERE att1.VALUE LIKE ? AND att1.gamma_id = txs2.gamma_id AND txs2.tx_type <> ? AND txs2.transaction_id = txd3.transaction_id AND txd3.branch_id = br4.branch_id AND br4.archived = 0 GROUP BY txd3.branch_id, att1.art_id",
               Table.alias("MAX(txs2.transaction_id)", "tx"));
   private static final String SELECT_HISTORICAL_ARTIFACT_DELETED =
         String.format(
               "SELECT tx from osee_define_txs txs3, osee_define_artifact_version arv4, (SELECT %s, arv1.art_id alpha from osee_define_artifact_version arv1, osee_define_txs txs2, osee_define_tx_details txd3 WHERE arv1.art_id = ? AND arv1.gamma_id = txs2.gamma_id and txs2.transaction_id = txd3.transaction_id and txd3.branch_id = ? group by arv1.art_id) t5 where t5.tx = txs3.transaction_id AND txs3.gamma_id = arv4.gamma_id and arv4.art_id = t5.alpha and arv4.modification_id = ?",
               Table.alias("MAX(txd3.transaction_id)", "tx"));

   private static final String SELECT_ARTIFACT_BY_ID =
         "SELECT art1.art_id, art1.art_type_id, art1.guid, art1.human_readable_id, ary2.factory_id, ary2.factory_key, fac3.factory_class FROM osee_define_artifact art1, osee_define_artifact_type ary2, osee_define_factory fac3 WHERE art1.art_id = ? AND art1.art_type_id = ary2.art_type_id AND ary2.factory_id = fac3.factory_id";

   // NOTE: the 'order by transaction_id desc' clause works with the DynamicAttributeManager to
   // allow it to ignore effectively overwritten attributes with {min,max} of {0,1} 
   // with >1 attribute instances.
   private static final String SELECT_ATTRIBUTES_FOR_ARTIFACT =
         "SELECT " + ATTRIBUTE_ALIAS_1.columns("attr_id", "attr_type_id", "gamma_id", "value", "content") + " FROM " + ATTRIBUTE_ALIAS_1 + "," + TRANSACTIONS_TABLE + " WHERE " + ATTRIBUTE_ALIAS_1.column("art_id") + "=?" + " AND " + ATTRIBUTE_ALIAS_1.column("modification_id") + "<> ?" + " AND " + ATTRIBUTE_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT MAX(" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ")" + " FROM " + ATTRIBUTE_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ATTRIBUTE_ALIAS_2.column("attr_id") + "=" + ATTRIBUTE_ALIAS_1.column("attr_id") + " AND " + ATTRIBUTE_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " <= ?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)" + " ORDER BY " + TRANSACTIONS_TABLE.column("transaction_id") + " DESC";

   private static final String UPDATE_ARTIFACT_TYPE =
         "UPDATE " + ARTIFACT_TABLE + " SET art_type_id = ? WHERE art_id =?";

   private static final SkynetActivator plugin = SkynetActivator.getInstance();

   private SkynetTransactionManager transactionManager;
   private TransactionIdManager transactionIdManager;
   private ConfigurationPersistenceManager configurationPersistenceManager;
   private RelationPersistenceManager relationManager;
   private AccessControlManager accessControlManager;
   private ArtifactFactoryCache factoryCache;
   private TagManager tagManager;
   private BranchPersistenceManager branchManager;
   private ExtensionDefinedObjects<IAttributeSaveListener> attributeSaveListeners;

   private final HashMap<Integer, String> artifactTypeCache;
   private final ArtifactDhChildCountCache artifactDhChildCountCache;
   private IProgressMonitor monitor;
   public static final String ROOT_ARTIFACT_TYPE_NAME = "Root Artifact";
   public static final String DEFAULT_HIERARCHY_ROOT_NAME = "Default Heirarchy Root";

   private static final ArtifactPersistenceManager instance = new ArtifactPersistenceManager();

   public static boolean initFinished = false;

   private ArtifactPersistenceManager() {
      this.artifactTypeCache = new HashMap<Integer, String>();
      this.artifactDhChildCountCache = new ArtifactDhChildCountCache();
      this.attributeSaveListeners =
            new ExtensionDefinedObjects<IAttributeSaveListener>(
                  "org.eclipse.osee.framework.skynet.core.AttributeSaveListener", "AttributeSaveListener", "classname");
      if (!plugin.isAutoTaggingEnabled()) {
         System.err.println("tagging on all artifacts being skipped");
      }
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
      configurationPersistenceManager = ConfigurationPersistenceManager.getInstance();
      relationManager = RelationPersistenceManager.getInstance();
      factoryCache = ArtifactFactoryCache.getInstance();
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
         throw new SQLException(ex.getLocalizedMessage());
      }
   }

   public void saveTrace(Artifact artifact, boolean recurse, SkynetTransactionBuilder builder) throws SQLException {
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

   public void doSave(Artifact artifact, SkynetTransaction transaction, boolean persistAttributes) throws SQLException {
      workingOn(artifact.getDescriptiveName());
      boolean newArtifact = artifact.getPersistenceMemo() == null;
      SkynetDatabase.ModificationType modType;

      if (newArtifact) {
         modType = SkynetDatabase.ModificationType.NEW;
      } else {
         modType = SkynetDatabase.ModificationType.CHANGE;
      }

      int artGamma = SkynetDatabase.getNextGammaId();

      if (newArtifact) {
         addArtifactData(artifact, transaction, artGamma);
      }

      processTransactionForArtifact(artifact, modType, transaction, artGamma);

      if (persistAttributes) {
         notifyOnAttributeSave(artifact);

         Collection<DynamicAttributeManager> userAttributes = artifact.getAttributes();
         String content;

         for (DynamicAttributeManager attributeManager : userAttributes) {
            for (Attribute attribute : attributeManager.getAttributes()) {
               if (attribute.isDirty()) {
                  if (attribute.getName().equals(WordAttribute.CONTENT_NAME)) {
                     //remove smart after each save
                     content = WordUtil.removeWordMarkupSmartTags(attribute.getStringData());
                     attribute.setStringData(content);
                  }
                  addAttributeData(attribute, artifact, transaction, artGamma);
               }
            }

            for (AttributeMemo memo : attributeManager.getDeletedAttributes()) {
               deleteAttribute(memo, transaction, artifact);
            }
         }

         if (modType == SkynetDatabase.ModificationType.NEW ? false : true) {
            transaction.addRemoteEvent(RemoteArtifactEventFactory.makeEvent(artifact,
                  transaction.getTransactionNumber()));
         }
         artifact.setInTransaction(true);
      }
      workedOneUnit();
   }

   private void processTransactionForArtifact(Artifact artifact, SkynetDatabase.ModificationType modType, SkynetTransaction transaction, int artGamma) throws SQLException {
      transaction.addTransactionDataItem(new ArtifactTransactionData(artifact, artGamma,
            transaction.getTransactionNumber(), modType));
   }

   private void addArtifactData(Artifact artifact, SkynetTransaction transaction, int gammaId) throws SQLException {
      int artId = SkynetDatabase.getNextArtifactId();

      transaction.addToBatch(INSERT_ARTIFACT, SQL3DataType.INTEGER, artId, SQL3DataType.INTEGER,
            artifact.getArtTypeId(), SQL3DataType.VARCHAR, artifact.getGuid(), SQL3DataType.VARCHAR,
            artifact.getHumanReadableId());

      TransactionId transactionId = transactionIdManager.getEditableTransactionId(artifact.getBranch());
      artifact.setPersistenceMemo(new ArtifactPersistenceMemo(transactionId, artId, gammaId));
      artifact.getFactory().cache(artifact);
   }

   /**
    * Persist an attribute for a particular artifact. Upon completion the attribute will be marked as not dirty.
    * 
    * @param attribute The attribute to persist.
    * @param artifact The artifact that the attribute is attached to.
    * @throws SQLException
    */
   protected void addAttributeData(Attribute attribute, Artifact artifact, SkynetTransaction transaction, int artGamma) throws SQLException {
      int branchId = artifact.getBranch().getBranchId();
      String value = attribute.getVarchar();
      ByteArrayInputStream stream = null;

      if (attribute.getBlobData() != null) stream = new ByteArrayInputStream(attribute.getBlobData());

      addToAttributeTable(attribute, artifact, branchId, value, stream, transaction, artGamma);
   }

   private void addToAttributeTable(Attribute attribute, Artifact artifact, int tagId, String value, InputStream stream, SkynetTransaction transaction, int artGamma) throws SQLException {
      AttributeMemo memo;
      ModType modType;
      SkynetDatabase.ModificationType attrModType;

      if (artifact.isVersionControlled()) {
         if (attribute.getPersistenceMemo() == null) {
            memo =
                  createAttributeMemo(attribute.getManager().getDescriptor().getAttrTypeId(),
                        SkynetDatabase.getNextGammaId());
            attribute.setPersistenceMemo(memo);
            modType = ModType.Added;
            attrModType = SkynetDatabase.ModificationType.NEW;

         } else {
            memo = attribute.getPersistenceMemo();
            memo = new AttributeMemo(memo.getAttrId(), memo.getAttrTypeId(), SkynetDatabase.getNextGammaId());
            attribute.setPersistenceMemo(memo);
            modType = ModType.Changed;
            attrModType = SkynetDatabase.ModificationType.CHANGE;
         }
         transaction.addTransactionDataItem(new AttributeTransactionData(artifact.getArtId(), memo.getAttrId(),
               memo.getAttrTypeId(), value, memo.getGammaId(), transaction.getTransactionNumber(), stream, attrModType));

         transaction.addLocalEvent(new TransactionArtifactModifiedEvent(artifact, modType, this));
      } else {
         if (attribute.getPersistenceMemo() == null) {
            memo =
                  createAttributeMemo(attribute.getManager().getDescriptor().getAttrTypeId(),
                        SkynetDatabase.getNextGammaId());
            attribute.setPersistenceMemo(memo);
            attrModType = SkynetDatabase.ModificationType.NEW;

            transaction.addTransactionDataItem(new AttributeTransactionData(artifact.getArtId(), memo.getAttrId(),
                  memo.getAttrTypeId(), value, memo.getGammaId(), transaction.getTransactionNumber(), stream,
                  attrModType));
         } else {
            memo = attribute.getPersistenceMemo();
            transaction.addToBatch(UPDATE_TRANSACTION_TABLE, SQL3DataType.VARCHAR, transaction.getComment(),
                  SQL3DataType.TIMESTAMP, GlobalTime.GreenwichMeanTimestamp(), SQL3DataType.INTEGER,
                  attribute.getPersistenceMemo().getGammaId(), SQL3DataType.INTEGER, artifact.getBranch().getBranchId());

            transaction.addToBatch(UPDATE_ATTRIBUTE, SQL3DataType.INTEGER, artifact.getArtId(), SQL3DataType.INTEGER,
                  memo.getAttrId(), SQL3DataType.INTEGER, memo.getAttrTypeId(), SQL3DataType.VARCHAR, value,
                  SQL3DataType.INTEGER, memo.getGammaId(), SQL3DataType.BLOB, stream);
         }
      }
   }

   private AttributeMemo createAttributeMemo(int attrTypeId, int gammaId) throws SQLException {
      return new AttributeMemo(Query.getNextSeqVal(null, SkynetDatabase.ATTR_ID_SEQ), attrTypeId, gammaId);
   }

   /**
    * This returns the artifact type name for the given artifact type id
    * 
    * @param artTypeId stored in the db
    * @return artifact type name.
    * @throws SQLException
    */
   public String getArtifactTypeName(int artTypeId) throws SQLException {
      String name = null;
      ConnectionHandlerStatement chStmt = null;

      if (artifactTypeCache.containsKey(artTypeId)) {
         name = artifactTypeCache.get(artTypeId);
      } else {
         try {
            chStmt = ConnectionHandler.runPreparedQuery(SELECT_ARTIFACT_TYPE_NAME, SQL3DataType.INTEGER, artTypeId);
            ResultSet rSet = chStmt.getRset();
            if (rSet.next()) {
               name = rSet.getString("name");
               artifactTypeCache.put(artTypeId, name);
            }
         } finally {
            DbUtil.close(chStmt);
         }
      }

      return name;
   }

   public Collection<Artifact> getArtifactsFromHrid(String hrid, Branch branch) throws SQLException {
      return getArtifacts(new ArtifactHridSearch(hrid), branch);
   }

   public Artifact getArtifact(String guid, Branch branch) throws SQLException {
      TransactionId transactionId = transactionIdManager.getEditableTransactionId(branch);

      if (transactionId == null) return null;

      return getArtifact(guid, transactionId);
   }

   /**
    * This method acquires <code>Artifact</code>'s directly from the database. This should only be called by
    * factories since all caching is performed by the factory.
    * 
    * @param guid The guid of the artifact.
    * @return The <code>Artifact</code> from the database that corresponds to the supplied guid.
    * @throws SQLException
    */
   public Artifact getArtifact(String guid, TransactionId transactionId) throws SQLException {
      Artifact artifact = factoryCache.checkArtifactCache(guid, transactionId);

      // If not available in cache, then do full processing.
      if (artifact == null) {
         ConnectionHandlerStatement chStmt = null;
         try {
            chStmt =
                  ConnectionHandler.runPreparedQuery(SELECT_ARTIFACT_BY_GUID, SQL3DataType.VARCHAR, guid,
                        SQL3DataType.INTEGER, SkynetDatabase.ModificationType.DELETE.getValue(), SQL3DataType.INTEGER,
                        transactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                        transactionId.getBranch().getBranchId());

            if (chStmt.next()) {
               ArtifactProcessor artifactProcessor = new ArtifactProcessor(this, transactionId);
               artifact = artifactProcessor.process(chStmt.getRset());
            }
         } finally {
            DbUtil.close(chStmt);
         }
      }

      return artifact;
   }

   public Artifact getArtifactFromId(int artId, Branch branch) throws SQLException, IllegalArgumentException {
      TransactionId transactionId = transactionIdManager.getEditableTransactionId(branch);

      if (transactionId == null) throw new IllegalArgumentException(
            "Editable transaction id can not be null for branch: " + branch.getBranchId());

      return getArtifactFromId(artId, transactionId);
   }

   /**
    * This method assumes that the artId is for an artifact is not deleted at the specified transaction. However if it
    * is, the artifact will still be returned but the state of the artifact may be inconsistant.
    * 
    * @param artId
    * @param transactionId
    * @return
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public Artifact getArtifactFromId(int artId, TransactionId transactionId) throws SQLException, IllegalArgumentException {
      Artifact artifact = factoryCache.checkArtifactCache(artId, transactionId);

      ConnectionHandlerStatement chStmt = null;
      // If not available in cache, then do full processing.
      if (artifact == null) {
         try {
            chStmt = ConnectionHandler.runPreparedQuery(SELECT_ARTIFACT_BY_ID, SQL3DataType.INTEGER, artId);

            if (chStmt.next()) {
               artifact = new ArtifactProcessor(this, transactionId).process(chStmt.getRset());
            }
         } finally {
            DbUtil.close(chStmt);
         }
      }

      if (artifact == null) {
         throw new IllegalArgumentException(
               "Artifact could not be acquired. Transaction id: " + transactionId.getTransactionNumber() + " Branch:" + transactionId.getBranch().getBranchId());
      }

      return artifact;
   }

   private static final String ARTIFACT_SELECT =
         "SELECT " + ARTIFACT_TABLE.columns("art_id", "art_type_id", "guid", "human_readable_id") + ", " + ARTIFACT_TYPE_TABLE.columns(
               "factory_id", "factory_key") + ", " + FACTORY_TABLE.columns("factory_id", "factory_class") + ", " + TRANSACTIONS_TABLE.column("gamma_id") + " FROM " + ARTIFACT_TABLE + ", " + ARTIFACT_TYPE_TABLE + "," + ARTIFACT_VERSION_ALIAS_1 + ", " + FACTORY_TABLE + "," + TRANSACTIONS_TABLE + " WHERE " + ARTIFACT_TABLE.column("art_type_id") + "=" + ARTIFACT_TYPE_TABLE.column("art_type_id") + " AND " + ARTIFACT_TABLE.column("art_id") + "=" + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " AND " + ARTIFACT_TYPE_TABLE.column("factory_id") + "=" + FACTORY_TABLE.column("factory_id") + " AND " + TRANSACTIONS_TABLE.column("gamma_id") + "=" + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + ARTIFACT_VERSION_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_VERSION_ALIAS_2.column("art_id") + "=" + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<= ?)" + " AND " + ARTIFACT_VERSION_ALIAS_1.column("modification_id") + "<>" + SkynetDatabase.ModificationType.DELETE + " AND ";

   private static final String ARTIFACT_ID_SELECT =
         "SELECT " + ARTIFACT_TABLE.columns("art_id") + " FROM " + ARTIFACT_TABLE + " WHERE ";

   private static final String ARTIFACT_COUNT_SELECT = "SELECT COUNT(*) artifacts FROM " + ARTIFACT_TABLE + " WHERE ";

   private static String getSql(ISearchPrimitive searchCriteria, String header, List<Object> dataList, Branch branch) throws SQLException {
      StringBuilder sql = new StringBuilder(header);

      sql.append(ARTIFACT_TABLE.column("art_id") + " in (");
      sql.append(getSelectArtIdSql(searchCriteria, dataList, branch));
      sql.append(")");

      return sql.toString();
   }

   public static String getSelectArtIdSql(ISearchPrimitive searchCriteria, List<Object> dataList, Branch branch) throws SQLException {
      return getSelectArtIdSql(searchCriteria, dataList, null, branch);
   }

   public static String getSelectArtIdSql(ISearchPrimitive searchCriteria, List<Object> dataList, String alias, Branch branch) throws SQLException {
      StringBuilder sql = new StringBuilder();

      sql.append("SELECT ");
      sql.append(searchCriteria.getArtIdColName());

      if (alias != null) sql.append(" AS " + alias);

      sql.append(" FROM ");
      sql.append(searchCriteria.getTableSql(dataList, branch));

      String criteriaSql = searchCriteria.getCriteriaSql(dataList, branch);
      if (criteriaSql.trim().length() != 0) sql.append(" WHERE (" + criteriaSql + ")");

      return sql.toString();
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

   private String getSql(List<ISearchPrimitive> searchCriteria, boolean all, List<Object> dataList, TransactionId transactionId) throws SQLException {
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(transactionId.getBranch().getBranchId());
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(transactionId.getTransactionNumber());

      return getSql(searchCriteria, all, ARTIFACT_SELECT, dataList, transactionId.getBranch());
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

   public int getArtifactDhChildCount(int artId, Branch branch) {
      return artifactDhChildCountCache.getChildCount(artId, branch);
   }

   public Collection<Artifact> getArtifacts(ISearchPrimitive searchCriteria, Branch branch) throws SQLException {
      LinkedList<Object> dataList = new LinkedList<Object>();
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(branch.getBranchId());
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(transactionIdManager.getEditableTransactionId(branch).getTransactionNumber());

      return getArtifacts(getSql(searchCriteria, ARTIFACT_SELECT, dataList, branch), dataList, branch, null);
   }

   public Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, Branch branch) throws SQLException {
      LinkedList<Object> dataList = new LinkedList<Object>();
      return getArtifacts(getSql(searchCriteria, all, dataList, transactionIdManager.getEditableTransactionId(branch)),
            dataList, branch, null);
   }

   public Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, Branch branch, ISearchConfirmer confirmer) throws SQLException {
      LinkedList<Object> dataList = new LinkedList<Object>();
      return getArtifacts(getSql(searchCriteria, all, dataList, transactionIdManager.getEditableTransactionId(branch)),
            dataList, branch, confirmer);
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

   public Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, TransactionId transactionId) throws SQLException {
      LinkedList<Object> dataList = new LinkedList<Object>();
      return getArtifacts(getSql(searchCriteria, all, dataList, transactionId), dataList, transactionId, null);
   }

   private Collection<Artifact> getArtifacts(String sql, List<Object> dataList, Branch branch, ISearchConfirmer confirmer) throws SQLException {
      return getArtifacts(sql, dataList, transactionIdManager.getEditableTransactionId(branch), confirmer);
   }

   private Collection<Artifact> getArtifacts(String sql, List<Object> dataList, TransactionId transactionId, ISearchConfirmer confirmer) throws SQLException {
      Map<IArtifactFactory, Collection<ArtifactToLoadDescription>> factoryMap =
            new HashMap<IArtifactFactory, Collection<ArtifactToLoadDescription>>();
      Collection<Artifact> artifacts = new LinkedList<Artifact>();
      ConnectionHandlerStatement chStmt = ConnectionHandler.runPreparedQuery(sql, dataList.toArray());

      ResultSet set = chStmt.getRset();

      IArtifactFactory fact;
      Collection<ArtifactToLoadDescription> descriptions;
      int artifactsCount = 0;

      try {
         while (set.next()) {
            fact = configurationPersistenceManager.getFactoryFromName(set.getString("factory_class"));
            descriptions = factoryMap.get(fact);
            if (descriptions == null) {
               descriptions = new LinkedList<ArtifactToLoadDescription>();
               factoryMap.put(fact, descriptions);
            }
            descriptions.add(new ArtifactToLoadDescription(set.getInt("art_id"), set.getInt("art_type_id"),
                  set.getString("guid"), set.getString("human_readable_id"), set.getString("factory_key"),
                  set.getInt("gamma_id")));
            artifactsCount++;
         }
      } finally {
         DbUtil.close(chStmt);
      }
      if (confirmer == null || confirmer.canProceed(artifactsCount)) {
         // Get all of the artifacts from the appropriate factories
         for (IArtifactFactory factory : factoryMap.keySet()) {
            artifacts.addAll(factory.getArtifacts(factoryMap.get(factory), transactionId));
         }
      }

      return artifacts;
   }

   public Collection<Artifact> getArtifactsFromSubtype(ArtifactSubtypeDescriptor descriptor) throws SQLException {
      // TODO: this should be more direct by using the descriptor and not just its name
      return getArtifacts(new ArtifactTypeSearch(descriptor.getName(), Operator.EQUAL),
            descriptor.getTransactionId().getBranch());
   }

   public Collection<Artifact> getArtifactsFromSubtypeName(String subtypeName, Branch branch) throws SQLException {
      return getArtifacts(new ArtifactTypeSearch(subtypeName, Operator.EQUAL), branch);
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

      Collection<DynamicAttributeDescriptor> attributeTypeDescriptors =
            configurationPersistenceManager.getAttributeTypesFromArtifactType(artifact.getDescriptor());
      for (DynamicAttributeDescriptor attributeType : attributeTypeDescriptors) {
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
                        artifact.getArtId(), SQL3DataType.INTEGER, ModificationType.DELETE.getValue(),
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
                     type =
                           configurationPersistenceManager.getDynamicAttributeType(attrTypeId, transactionId).createAttributeManager(
                                 artifact, false);
                     type.setupForInitialization(false);
                     typeHash.put(attrTypeId, type);
                  }

                  try {
                     String varchar = chStmt.getRset().getString("value");
                     attribute = type.injectFromDb(chStmt.getRset().getBinaryStream("content"), varchar);

                  } catch (SQLException e) {
                     throw new RuntimeException(e);
                  }

                  attribute.setPersistenceMemo(new AttributeMemo(chStmt.getRset().getInt("attr_id"), attrTypeId,
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

      artifact.setAttributes(attributes);
   }

   /**
    * Place all of the information from the database for an artifact into the artifact. It is expected that the artifact
    * already has the appropriate GUID and branch for this call to succeed.
    * 
    * @param artifact
    * @throws SQLException
    */

   public void initializeArtifact(int artId, Artifact artifact, TransactionId transactionId) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(1, SELECT_ARTIFACT_FOR_INIT, SQL3DataType.INTEGER, artId,
                     SQL3DataType.INTEGER, transactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                     transactionId.getBranch().getBranchId());

         ResultSet rSet = chStmt.getRset();
         if (!rSet.next()) {
            throw new IllegalStateException(
                  "The artifact with id " + artId + " does not exist for transaction \"" + transactionId + "\"");
         } else if (rSet.getInt("modification_id") == ModificationType.DELETE.getValue()) {
            throw new IllegalStateException(
                  "The artifact with id " + artId + " was deleted in or before the (transactionId) \"" + transactionId + "\"");
         } else {
            artifact.setPersistenceMemo(new ArtifactPersistenceMemo(transactionId, artId, rSet.getInt("gamma_id")));
            artifact.setDescriptor(configurationPersistenceManager.getArtifactSubtypeDescriptor(
                  rSet.getInt("art_type_id"), transactionId));

            setAttributesOnArtifact(artifact);
         }
      } finally {
         DbUtil.close(chStmt);
      }

      // This artifact is fresh, so mark it as not dirty
      artifact.setNotDirty();
   }

   /**
    * Place all of the information from the database for a set of artifacts into the artifacts. It is expected that the
    * artifacts already have the appropriate guids and tags for this call to succeed.
    * 
    * @param artifacts
    * @throws SQLException
    */
   public void initializeArtifacts(Collection<Artifact> artifacts, TransactionId transactionId) throws SQLException {
      if (artifacts.isEmpty()) return;

      initializeArtifactsAttributes(artifacts, transactionId);

      // The artifacts are fresh, so mark them as not dirty
      for (Artifact artifact : artifacts) {

         artifact.setNotDirty();
         artifact.onInitializationComplete();
      }
   }

   private void initializeArtifactsAttributes(Collection<Artifact> artifacts, TransactionId transactionId) throws SQLException {
      DoubleKeyHashMap<Integer, Integer, DynamicAttributeManager> typeHash =
            new DoubleKeyHashMap<Integer, Integer, DynamicAttributeManager>();
      DynamicAttributeManager attributeManager;

      for (Artifact artifact : artifacts) {
         for (DynamicAttributeDescriptor attributeType : configurationPersistenceManager.getAttributeTypesFromArtifactType(artifact.getDescriptor())) {
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
                           "value", "content") + " FROM " + ATTRIBUTE_VERSION_TABLE + "," + TRANSACTIONS_TABLE

                     + ", (SELECT " + ATTRIBUTE_VERSION_TABLE.column("attr_id") + ", MAX(" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ") AS last_transaction_id FROM " + ATTRIBUTE_VERSION_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ATTRIBUTE_VERSION_TABLE.column("art_id") + " IN (" + artIdList + ") " + " AND " + ATTRIBUTE_VERSION_TABLE.column("gamma_id") + EQUAL + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + EQUAL + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " GROUP BY attr_id) t1 " + " WHERE " + TRANSACTIONS_TABLE.column("transaction_id") + "= t1.last_transaction_id" + " AND " + ATTRIBUTE_VERSION_TABLE.column("attr_id") + "= t1.attr_id" + " AND " + ATTRIBUTE_VERSION_TABLE.column("modification_id") + "<> ?" + " AND " + ATTRIBUTE_VERSION_TABLE.column("art_id") + " IN (" + artIdList + ")" + " AND " + ATTRIBUTE_VERSION_TABLE.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " ORDER BY " + TRANSACTIONS_TABLE.column("transaction_id") + " DESC",
                     SQL3DataType.INTEGER, transactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                     transactionId.getBranch().getBranchId(), SQL3DataType.INTEGER, ModificationType.DELETE.getValue());

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
               attributeManager =
                     configurationPersistenceManager.getDynamicAttributeType(attrTypeId, transactionId).createAttributeManager(
                           artifact, false);
               typeHash.put(artId, attrTypeId, attributeManager);
               attributeManager.setupForInitialization(false);
            }

            attribute = attributeManager.injectFromDb(rSet.getBinaryStream("content"), rSet.getString("value"));
            attribute.setPersistenceMemo(new AttributeMemo(rSet.getInt("attr_id"), attrTypeId, rSet.getInt("gamma_id")));

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
         artifact.setAttributes(new LinkedList<DynamicAttributeManager>(attributeMap.values()));
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
    * Define how artifacts are acquired from a ResultSet and validated before being placed in a Collection. This
    * processor expects to receive ResultSet's with access to columns art_id, art_type_id, guid, factory_id.
    * 
    * @author Robert A. Fisher
    */
   private class ArtifactProcessor implements RsetProcessor<Artifact> {
      private final TransactionId transactionId;

      public ArtifactProcessor(ArtifactPersistenceManager manager, TransactionId transactionId) {
         this.transactionId = transactionId;
      }

      public Artifact process(ResultSet set) throws SQLException {
         IArtifactFactory factory = null;
         factory = configurationPersistenceManager.getFactoryFromName(set.getString("factory_class"));
         return factory.getArtifact(set.getInt("art_id"), set.getString("guid"), set.getString("human_readable_id"),
               set.getString("factory_key"), transactionId.getBranch(), transactionId);
      }

      public boolean validate(Artifact item) {
         return item != null;
      }
   }

   /**
    * @param artifacts The artifacts to delete.
    * @throws SQLException
    */
   public void deleteArtifact(final Artifact... artifacts) throws SQLException {
      if (artifacts.length == 0) return;

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
    * @throws SQLException
    */
   public void deleteTrace(Artifact artifact, SkynetTransactionBuilder builder) throws SQLException {

      if (!artifact.isDeleted()) {
         // NOTE: This must be done first since the the actual deletion of an artifact
         // clears out the link manager
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
    * @throws SQLException
    */
   public synchronized void doDelete(Artifact artifact, SkynetTransaction transaction, SkynetTransactionBuilder builder) throws SQLException {
      if (!artifact.isInDb()) return;

      LinkManager linkManager = artifact.getLinkManager();

      processTransactionForArtifact(artifact, SkynetDatabase.ModificationType.DELETE, transaction,
            SkynetDatabase.getNextGammaId());

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
    * Remove an attribute from the database that is represented by a particular persistence memo that the persistence
    * layer marked it with. The persistence memo is used for this since it is the identifying information the
    * persistence layer needs, allowing the attribute to be destroyed and released back to the system.
    * 
    * @throws SQLException
    */
   private void deleteAttribute(AttributeMemo memo, SkynetTransaction transaction, Artifact artifact) throws SQLException {
      // If the memo is null, the attribute was never saved to the database, so we can ignore it
      if (memo == null) return;

      int gammaId = SkynetDatabase.getNextGammaId();
      transaction.addTransactionDataItem(new AttributeTransactionData(artifact.getArtId(), memo.getAttrId(),
            memo.getAttrTypeId(), null, gammaId, transaction.getTransactionNumber(), null,
            SkynetDatabase.ModificationType.DELETE));

      transaction.addLocalEvent(new CacheArtifactModifiedEvent(artifact, ModType.Changed, this));
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
      artifact.checkDeleted();

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
      Collection<Artifact> artifacts = getArtifactsFromSubtypeName(ROOT_ARTIFACT_TYPE_NAME, branch);

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

   public Artifact getArtifactFromTypeName(String artType, String nameValue, Branch branch) throws SQLException {
      return getArtifactFromTypeName(artType, nameValue, branch, true);
   }

   public Artifact getArtifactFromTypeName(String artType, String nameValue, Branch branch, boolean existenceRequired) throws SQLException {
      List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
      criteria.add(new ArtifactTypeSearch(artType, EQUAL));
      criteria.add(new AttributeValueSearch("Name", nameValue, EQUAL));

      Collection<Artifact> artifacts = getArtifacts(criteria, true, branch);

      if (artifacts.size() == 1) return artifacts.iterator().next();
      if (artifacts.size() != 1 && existenceRequired) {
         throw new IllegalStateException(String.format(
               "There must be exactly one \"%s\" artifact named \"%s\" - not %d", artType, nameValue, artifacts.size()));
      }
      return null;
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
         ArtifactFactory<?> factory =
               (ArtifactFactory<?>) configurationPersistenceManager.getFactoryFromName(factoryName);

         if (factory == null) throw new IllegalArgumentException(
               "The factory for this artifact remote event could not be determined.");

         if (factory.containsArtifact(artId, branchId)) {
            Branch branch = branchManager.getBranch(branchId);
            Artifact oldArtifact = getArtifactFromId(artId, branch);
            // this forces the links to load
            if (oldArtifact.isDirty(true)) {
               String message = "Artifact Conflict...this must be handled";
               System.err.println(message);
               logger.log(Level.SEVERE, message);
            } else {
               factory.deCache(oldArtifact);

               if (newTransactionId.getTransactionNumber() != notEditableTransactionId.getTransactionNumber()) {
                  oldArtifact.getPersistenceMemo().setTransactionId(notEditableTransactionId);
                  factory.cache(oldArtifact);
               } else {
                  oldArtifact.getPersistenceMemo().setTransactionId(null);
               }
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

   public Collection<Artifact> getHistoricalArtifactsFromAttribute(String attributeValue) throws SQLException {
      Collection<Artifact> artifacts = new ArrayList<Artifact>();
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(SELECT_HISTORICAL_ARTIFACTS, SQL3DataType.VARCHAR, attributeValue,
                     SQL3DataType.INTEGER, TransactionType.BRANCHED.getId());

         ResultSet rSet = chStmt.getRset();
         while (rSet.next()) {
            TransactionId transactionId = transactionIdManager.getPossiblyEditableTransactionId(rSet.getInt("tx"));
            artifacts.add(getArtifactFromId(rSet.getInt("art_id"), transactionId));
         }
      } finally {
         DbUtil.close(chStmt);
      }
      return artifacts;
   }

   public int getDeletionTransactionId(int artId, int branchId) throws SQLException {
      int transactionId;
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(SELECT_HISTORICAL_ARTIFACT_DELETED, SQL3DataType.INTEGER, artId,
                     SQL3DataType.INTEGER, branchId, SQL3DataType.INTEGER,
                     SkynetDatabase.ModificationType.DELETE.getValue());

         ResultSet rSet = chStmt.getRset();
         if (rSet.next()) {
            transactionId = rSet.getInt("tx");
         } else {
            transactionId = -1;
         }
      } finally {
         DbUtil.close(chStmt);
      }
      return transactionId;
   }

   private Artifact createRoot(Branch branch) throws SQLException {
      ArtifactSubtypeDescriptor descriptor =
            configurationPersistenceManager.getArtifactSubtypeDescriptor(ROOT_ARTIFACT_TYPE_NAME, branch);
      Artifact root = descriptor.makeNewArtifact();
      root.setDescriptiveName(DEFAULT_HIERARCHY_ROOT_NAME);
      root.persistAttributes();
      return root;
   }

   /**
    * Returns the artifact that is referenced by the token.
    * 
    * @param token
    * @see ArtifactPersistenceManager#getArtifactVersionToken(Artifact)
    */
   public Artifact getArtifactFromToken(String token) {
      throw new UnsupportedOperationException();
   }

   /**
    * Returns a token that addresses the version of the artifact passed.
    * 
    * @param artifact
    * @see ArtifactPersistenceManager#getArtifactFromToken(String)
    */
   public String getArtifactVersionToken(Artifact artifact) {
      throw new UnsupportedOperationException();
   }

   /**
    * Changes the artifact type
    * 
    * @param artifact
    * @param descriptor
    * @throws SQLException
    */
   public void changeArtifactSubStype(Artifact artifact, ArtifactSubtypeDescriptor descriptor) throws SQLException {

      if (!artifact.getPersistenceMemo().getTransactionId().equals(descriptor.getTransactionId())) {
         throw new IllegalArgumentException("The artifact and descriptor must be on the same transaction.");
      }

      ConnectionHandler.runPreparedUpdate(UPDATE_ARTIFACT_TYPE, SQL3DataType.INTEGER, descriptor.getArtTypeId(),
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