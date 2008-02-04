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

package org.eclipse.osee.framework.skynet.core.relation;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_TYPE_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetRelationLinkEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkNewRelationLinkEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkModifiedEvent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.PersistenceManager;
import org.eclipse.osee.framework.skynet.core.PersistenceManagerInit;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionBuilder;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.transaction.data.RelationTransactionData;
import org.eclipse.osee.framework.skynet.core.utility.RemoteLinkEventFactory;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;
import org.eclipse.osee.framework.ui.plugin.util.db.RsetProcessor;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.LocalAliasTable;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

/**
 * Controls all aspects of saving and recovering relations. The data-store happens to be a database, but that should be
 * abstracted by this class in such a way that the application code only has to worry about the fact that SQLExceptions
 * may be thrown. <br/><br/> Each relation object in the system represents a relation within the Define system. For
 * this reason, successive calls to the database for the same relation will return a reference to the same exact object.
 * 
 * @see org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager
 * @see org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager
 * @author Robert A. Fisher
 */
public class RelationPersistenceManager implements PersistenceManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(RelationPersistenceManager.class);
   private static final LocalAliasTable LINK_ALIAS_1 = new LocalAliasTable(RELATION_LINK_VERSION_TABLE, "t1");
   private static final LocalAliasTable LINK_ALIAS_2 = new LocalAliasTable(RELATION_LINK_VERSION_TABLE, "t2");

   private static final String SELECT_LINKS =
         "SELECT DISTINCT " + RELATION_LINK_TYPE_TABLE.columns("type_name", "a_name", "b_name", "ab_phrasing",
               "ba_phrasing", "short_name") + ", " + LINK_ALIAS_1.columns("rel_link_id", "a_art_id", "b_art_id",
               "rationale", "a_order_value", "b_order_value", "gamma_id")

         + " FROM " + LINK_ALIAS_1 + "," + RELATION_LINK_TYPE_TABLE + "," + TRANSACTIONS_TABLE

         + " WHERE " + LINK_ALIAS_1.column("rel_link_type_id") + "=" + RELATION_LINK_TYPE_TABLE.column("rel_link_type_id") + " AND " + LINK_ALIAS_1.column("a_art_id") + "= ?" + " AND " + LINK_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + LINK_ALIAS_1.column("modification_id") + "<> ?" + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + LINK_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + LINK_ALIAS_2.column("rel_link_id") + "=" + LINK_ALIAS_1.column("rel_link_id") + " AND " + LINK_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?)"

         + " UNION ALL "

         + "SELECT DISTINCT " + RELATION_LINK_TYPE_TABLE.columns("type_name", "a_name", "b_name", "ab_phrasing",
               "ba_phrasing", "short_name") + ", " + LINK_ALIAS_1.columns("rel_link_id", "a_art_id", "b_art_id",
               "rationale", "a_order_value", "b_order_value", "gamma_id")

         + " FROM " + LINK_ALIAS_1 + "," + RELATION_LINK_TYPE_TABLE + "," + TRANSACTIONS_TABLE

         + " WHERE " + LINK_ALIAS_1.column("rel_link_type_id") + "=" + RELATION_LINK_TYPE_TABLE.column("rel_link_type_id") + " AND " + LINK_ALIAS_1.column("b_art_id") + "= ?" + " AND " + LINK_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + LINK_ALIAS_1.column("modification_id") + "<> ?" + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + LINK_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + LINK_ALIAS_2.column("rel_link_id") + "=" + LINK_ALIAS_1.column("rel_link_id") + " AND " + LINK_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?)";

   private static final String UPDATE_RELATION_ORDERS =
         "UPDATE " + RELATION_LINK_VERSION_TABLE + " t1 SET a_order_value=?, b_order_value=? WHERE gamma_id=?";
   private TransactionIdManager transactionIdManager;
   private ArtifactPersistenceManager artifactManager;
   private ConfigurationPersistenceManager configurationManager;

   // This must be declared here cause it can't be declared in enum RelationSide
   public static DoubleKeyHashMap<String, Boolean, IRelationEnumeration> sideHash =
         new DoubleKeyHashMap<String, Boolean, IRelationEnumeration>();
   public enum Direction {
      Back, Forward
   };
   public enum InsertLocation {
      BeforeTarget, AfterTarget
   }

   private IRelationLinkDescriptorCache relationLinkDescriptorCache;

   // This hash is keyed on the rel_link_id of the relation link, then transaction_id
   private DoubleKeyHashMap<Integer, TransactionId, IRelationLink> relationsCache;

   private static final RelationPersistenceManager instance = new RelationPersistenceManager();

   private RelationPersistenceManager() {
      this.relationsCache = new DoubleKeyHashMap<Integer, TransactionId, IRelationLink>();
      this.relationLinkDescriptorCache = new IRelationLinkDescriptorCache();
   }

   /**
    * Acquire an instance of the <code>RelationPeristenceManager</code>.
    */
   public static RelationPersistenceManager getInstance() {
      PersistenceManagerInit.initManagerWeb(instance);
      return instance;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.PersistenceManager#setRelatedManagers()
    */
   public void onManagerWebInit() throws Exception {
      artifactManager = ArtifactPersistenceManager.getInstance();
      configurationManager = ConfigurationPersistenceManager.getInstance();
      transactionIdManager = TransactionIdManager.getInstance();
   }

   /**
    * Persist a relation link so that its current state can be acquired at a later time.
    * 
    * @param relationLink The relationLink to persist.
    */
   public void makePersistent(IRelationLink relationLink) throws SQLException {
      makePersistent(relationLink, false);
   }

   public void makePersistent(final IRelationLink relationLink, final boolean recurse) throws SQLException {

      if (relationLink.isDirty()) {

         AbstractSkynetTxTemplate relationPersistTx = new AbstractSkynetTxTemplate(relationLink.getBranch()) {

            @Override
            protected void handleTxWork() throws Exception {
               trace(relationLink, recurse, getTxBuilder());
            }

         };
         try {
            relationPersistTx.execute();
         } catch (Exception ex) {
            throw new SQLException(ex.getLocalizedMessage());
         }
      }
   }

   public void trace(IRelationLink relationLink, boolean recurse, SkynetTransactionBuilder builder) throws SQLException {
      if (relationLink.isDirty()) {
         builder.addLink(relationLink);

         if (recurse) {
            artifactManager.saveTrace(relationLink.getArtifactA(), recurse, builder);
            artifactManager.saveTrace(relationLink.getArtifactB(), recurse, builder);
         }
      }
   }

   private void insertRelationLinkTable(IRelationLink relationLink, SkynetTransaction transaction) throws SQLException {

      int linkId;
      int gammaId = SkynetDatabase.getNextGammaId();
      Artifact aArtifact = relationLink.getArtifactA();
      Artifact bArtifact = relationLink.getArtifactB();
      int aArtId = aArtifact.getArtId();
      int aArtTypeId = aArtifact.getArtTypeId();
      int bArtId = bArtifact.getArtId();
      int bArtTypeId = bArtifact.getArtTypeId();
      ModType modType;
      SkynetDatabase.ModificationType modId;

      if (relationLink.getPersistenceMemo() == null) {
         linkId = Query.getNextSeqVal(null, SkynetDatabase.REL_LINK_ID_SEQ);
         relationLink.setPersistenceMemo(new LinkPersistenceMemo(linkId, gammaId));
         cache(relationLink);
         transaction.addRemoteEvent(RemoteLinkEventFactory.makeEvent(relationLink, transaction.getTransactionNumber()));
         modType = ModType.Added;
         modId = SkynetDatabase.ModificationType.NEW;
      } else {
         LinkPersistenceMemo memo = relationLink.getPersistenceMemo();
         relationLink.getPersistenceMemo().setGammaId(gammaId);
         linkId = memo.getLinkId();

         transaction.addRemoteEvent(new NetworkRelationLinkModifiedEvent(
               relationLink.getPersistenceMemo().getGammaId(), relationLink.getBranch().getBranchId(),
               transaction.getTransactionNumber(), linkId, aArtId, aArtTypeId, bArtId, bArtTypeId,
               relationLink.getRationale(), relationLink.getAOrder(), relationLink.getBOrder(),
               aArtifact.getFactory().getClass().getCanonicalName(),
               bArtifact.getFactory().getClass().getCanonicalName(),
               SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId()));

         modType = ModType.Changed;
         modId = SkynetDatabase.ModificationType.CHANGE;
      }

      transaction.addTransactionDataItem(new RelationTransactionData(relationLink, gammaId,
            transaction.getTransactionNumber(), modId));

      transaction.addLocalEvent(new TransactionRelationModifiedEvent(relationLink, aArtifact.getBranch(),
            relationLink.getLinkDescriptor().getName(), relationLink.getASideName(), modType, this));
   }

   /**
    * Remove all relations stored in the list awaiting to be deleted.
    */
   protected void deleteRelationLinks(final Collection<IRelationLink> links, Branch branch) throws SQLException {

      if (!links.isEmpty()) {
         AbstractSkynetTxTemplate deleteRelationsTx = new AbstractSkynetTxTemplate(branch) {

            @Override
            protected void handleTxWork() throws Exception {
               for (IRelationLink link : links) {
                  getTxBuilder().deleteLink(link);
               }
            }
         };

         try {
            deleteRelationsTx.execute();
         } catch (Exception ex) {
            throw new SQLException(ex.getLocalizedMessage());
         }
      }
   }

   public void doDelete(IRelationLink relationLink, SkynetTransaction transaction) throws SQLException {
      // if the persistence memo is null the link has never been saved, therefore it does not
      // need to be version controlled.
      if (relationLink.getPersistenceMemo() == null) return;

      int gammaId = SkynetDatabase.getNextGammaId();
      Artifact aArtifact = relationLink.getArtifactA();
      Artifact bArtifact = relationLink.getArtifactB();
      int aArtId = aArtifact.getArtId();
      int aArtTypeId = aArtifact.getArtTypeId();
      int bArtId = bArtifact.getArtId();
      int bArtTypeId = bArtifact.getArtTypeId();

      transaction.addTransactionDataItem(new RelationTransactionData(relationLink, gammaId,
            transaction.getTransactionNumber(), SkynetDatabase.ModificationType.DELETE));

      transaction.addRemoteEvent(new NetworkRelationLinkDeletedEvent(relationLink.getPersistenceMemo().getGammaId(),
            relationLink.getBranch().getBranchId(), transaction.getTransactionNumber(),
            relationLink.getPersistenceMemo().getLinkId(), aArtId, aArtTypeId, bArtId, bArtTypeId,
            aArtifact.getFactory().getClass().getCanonicalName(), bArtifact.getFactory().getClass().getCanonicalName(),
            SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId()));

      transaction.addLocalEvent(new TransactionRelationModifiedEvent(relationLink, aArtifact.getBranch(),
            relationLink.getLinkDescriptor().getName(), relationLink.getASideName(), ModType.Deleted, this));
   }

   /**
    * Remove all relations stored in the list awaiting to be deleted.
    * 
    * @throws SQLException
    */
   public void purgeRelationLinks(Collection<RelationLinkBase> links) throws SQLException {
      boolean firstTime = true;

      if (links.isEmpty()) return;

      // TODO this will fail if over 1000 links are removed at once ... consider handling this by
      // breaking into chunks
      StringBuffer deleteSql =
            new StringBuffer(" Delete from " + RELATION_LINK_VERSION_TABLE + " WHERE REL_LINK_ID in ( ");

      for (RelationLinkBase link : links) {
         if (!firstTime) deleteSql.append(" , ");

         deleteSql.append(link.getPersistenceMemo().getLinkId());
         firstTime = false;
         link.delete();
      }
      deleteSql.append(" ) ");
      try {
         ConnectionHandler.runPreparedUpdate(deleteSql.toString());
         links.clear();
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
   }

   public void doSave(IRelationLink link, SkynetTransaction transaction) throws SQLException {

      if (link.isDeleted()) {
         link.getArtifactA().getLinkManager().removeDeleted(link);
         link.getArtifactB().getLinkManager().removeDeleted(link);

         doDelete(link, transaction);
      } else {

         try {
            if (link.isDirty()) {
               // The relation link will be clean by the end of this, so mark it early so that if this relation get's
               // persisted by a one if it's artifact we don't get an infinite loop
               link.setNotDirty();

               link.getArtifactA().persist(false, false);
               link.getArtifactB().persist(false, false);

               // If the relation does not have a persistence memo then it is 'new'
               if (link.getPersistenceMemo() == null || link.isVersionControlled()) {
                  insertRelationLinkTable(link, transaction);
               } else {
                  Artifact aArtifact = link.getArtifactA();
                  Artifact bArtifact = link.getArtifactB();
                  int aArtId = aArtifact.getArtId();
                  int aArtTypeId = aArtifact.getArtTypeId();
                  int bArtId = bArtifact.getArtId();
                  int bArtTypeId = bArtifact.getArtTypeId();
                  int aOrder = link.getAOrder();
                  int bOrder = link.getBOrder();
                  int linkId = link.getPersistenceMemo().getLinkId();
                  String rationale = link.getRationale();

                  ConnectionHandler.runPreparedUpdate(
                        "UPDATE " + RELATION_LINK_VERSION_TABLE + " SET a_art_id=?, b_art_id=?, a_order_value=?, b_order_value=?, rationale=? WHERE rel_link_id=?",
                        SQL3DataType.INTEGER, aArtId, SQL3DataType.INTEGER, bArtId, SQL3DataType.INTEGER, aOrder,
                        SQL3DataType.INTEGER, bOrder, SQL3DataType.VARCHAR, rationale, SQL3DataType.INTEGER, linkId);

                  transaction.addRemoteEvent(new NetworkRelationLinkModifiedEvent(
                        link.getPersistenceMemo().getGammaId(), link.getBranch().getBranchId(),
                        transaction.getTransactionNumber(), linkId, aArtId, aArtTypeId, bArtId, bArtTypeId, rationale,
                        aOrder, bOrder, aArtifact.getFactory().getClass().getCanonicalName(),
                        bArtifact.getFactory().getClass().getCanonicalName(),
                        SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId()));

                  transaction.addLocalEvent(new TransactionRelationModifiedEvent(link, aArtifact.getBranch(),
                        link.getLinkDescriptor().getName(), link.getASideName(), ModType.Changed, this));

               }

            }
         } catch (SQLException ex) {
            throw new RuntimeException(ex);
         }
      }
   }

   /**
    * Updates the aOrder and bOrder of a IRelationLink without producing a transaction.
    * 
    * @param link
    * @throws SQLException
    */
   public void updateRelationOrdersWithoutTransaction(IRelationLink... links) throws SQLException {
      List<Object[]> data = new LinkedList<Object[]>();

      for (IRelationLink link : links) {
         data.add(new Object[] {SQL3DataType.INTEGER, link.getAOrder(), SQL3DataType.INTEGER, link.getBOrder(),
               SQL3DataType.INTEGER, link.getPersistenceMemo().getGammaId()});
      }
      ConnectionHandler.runBatchablePreparedUpdate(UPDATE_RELATION_ORDERS, true, data);
   }

   public void populateArtifactRelations(Artifact artifact) throws SQLException {

      if (artifact.getPersistenceMemo() == null) {
         return;
      }

      Collection<IRelationLink> relationCollection = new ArrayList<IRelationLink>();
      Branch branch = artifact.getBranch();
      TransactionId transactionId = artifact.getPersistenceMemo().getTransactionId();
      Query.acquireCollection(relationCollection, new RelationLinkProcessor(transactionId), SELECT_LINKS,
            SQL3DataType.INTEGER, artifact.getArtId(), SQL3DataType.INTEGER,
            SkynetDatabase.ModificationType.DELETE.getValue(), SQL3DataType.INTEGER, branch.getBranchId(),
            SQL3DataType.INTEGER, transactionId.getTransactionNumber(), SQL3DataType.INTEGER, artifact.getArtId(),
            SQL3DataType.INTEGER, SkynetDatabase.ModificationType.DELETE.getValue(), SQL3DataType.INTEGER,
            branch.getBranchId(), SQL3DataType.INTEGER, transactionId.getTransactionNumber()

      );
   }

   public void cacheDescriptor(IRelationLinkDescriptor descriptor) {
      relationLinkDescriptorCache.cache(descriptor);
   }

   /**
    * Get a listing of all the available descriptors.
    * 
    * @return A collection of all the available descriptors.
    */
   public Collection<IRelationLinkDescriptor> getIRelationLinkDescriptors(Branch branch) {
      // The collection from the cache is backed by the map, so guard access to the internal data
      TreeSet<IRelationLinkDescriptor> sortedSet = new TreeSet<IRelationLinkDescriptor>();
      for (IRelationLinkDescriptor linkDescriptor : relationLinkDescriptorCache.getAllDescriptors(branch)) {
         sortedSet.add(linkDescriptor);
      }

      return sortedSet;
   }

   /**
    * Get a listing of all the available descriptors that are applicable to an Artifact Type.
    * 
    * @return A collection of all the available descriptors.
    */
   public Collection<IRelationLinkDescriptor> getIRelationLinkDescriptors(ArtifactSubtypeDescriptor artifactDescriptor) {
      Collection<IRelationLinkDescriptor> linkDescriptors = new LinkedList<IRelationLinkDescriptor>();
      for (IRelationLinkDescriptor linkDescriptor : getIRelationLinkDescriptors(artifactDescriptor.getTransactionId())) {
         if (linkDescriptor.canLinkType(artifactDescriptor.getArtTypeId())) {
            linkDescriptors.add(linkDescriptor);
         }
      }

      return linkDescriptors;
   }

   /**
    * Get a particular link descriptor by its type name. If no such descriptor exists then a null reference will be
    * returned.
    * 
    * @param name The type name of the relation link.
    * @return The corresponding descriptor, null if one does not exist.
    */
   public IRelationLinkDescriptor getIRelationLinkDescriptor(String name, Branch branch) {
      return relationLinkDescriptorCache.getDescriptor(name, branch);
   }

   /**
    * Get a listing of all the available descriptors.
    * 
    * @return A collection of all the available descriptors.
    */
   public Collection<IRelationLinkDescriptor> getIRelationLinkDescriptors(TransactionId transactionId) {
      // The collection from the cache is backed by the map, so guard access to the internal data
      TreeSet<IRelationLinkDescriptor> sortedSet = new TreeSet<IRelationLinkDescriptor>();
      for (IRelationLinkDescriptor linkDescriptor : relationLinkDescriptorCache.getAllDescriptors(transactionId)) {
         sortedSet.add(linkDescriptor);
      }

      return sortedSet;
   }

   /**
    * Get a particular link descriptor by its type name. If no such descriptor exists then a null reference will be
    * returned.
    * 
    * @param name The type name of the relation link.
    * @return The corresponding descriptor, null if one does not exist.
    */
   public IRelationLinkDescriptor getIRelationLinkDescriptor(String name, TransactionId transactionId) {
      return relationLinkDescriptorCache.getDescriptor(name, transactionId);
   }

   /**
    * Define how relation links are acquired from a ResultSet and validated before being placed in a Collection. This
    * processor expects to receive ResultSet's with access to columns art_id, link_code, rationale, and tag_id.
    * 
    * @author Robert A. Fisher
    * @author Jeff C. Phillips
    */
   private class RelationLinkProcessor implements RsetProcessor<IRelationLink> {

      private final TransactionId transactionId;

      /**
       * @param transactionId
       */
      public RelationLinkProcessor(TransactionId transactionId) {
         this.transactionId = transactionId;
      }

      public IRelationLink process(ResultSet set) throws SQLException {
         IRelationLink link = null;
         IRelationLinkDescriptor descriptor = null;
         String rationale = set.getString("rationale");
         int relId = set.getInt("rel_link_id");
         int aArtId = set.getInt("a_art_id");
         int bArtId = set.getInt("b_art_id");
         int aOrderValue = set.getInt("a_order_value");
         int bOrderValue = set.getInt("b_order_value");
         int gammaId = set.getInt("gamma_id");

         descriptor = relationLinkDescriptorCache.getDescriptor(set.getString("type_name"), transactionId);
         link = relationsCache.get(relId, transactionId);

         if (link != null) {

            if (!link.getArtifactA().getLinkManager().deletedLinks.contains(link) && !link.isDeleted()) {

               link.getArtifactA().getLinkManager().addLink(link);
               link.getArtifactB().getLinkManager().addLink(link);
               return link;
            }
         } else {
            Artifact artA = null;
            Artifact artB = null;

            try {
               artA = artifactManager.getArtifactFromId(aArtId, transactionId);
               artB = artifactManager.getArtifactFromId(bArtId, transactionId);
            } catch (RuntimeException ex) {
               logger.log(
                     Level.WARNING,
                     "Loading link failed:  " + ex.getLocalizedMessage() + ": rel_id = " + relId + " art_id = " + (artA == null ? aArtId : bArtId),
                     ex);
               return null;
            }

            link =
                  new DynamicRelationLink(artA, artB, descriptor, new LinkPersistenceMemo(relId, gammaId),
                        (rationale != null && !rationale.equals("null")) ? rationale : "", aOrderValue, bOrderValue,
                        false);

            relationsCache.put(relId, transactionId, link);

            if (link.getArtifactA().isLinkManagerLoaded()) link.getArtifactA().getLinkManager().addLink(link);

            if (link.getArtifactB().isLinkManagerLoaded()) link.getArtifactB().getLinkManager().addLink(link);

            link.setNotDirty();
         }
         return link;
      }

      public boolean validate(IRelationLink item) {
         return item != null;
      }
   }

   public void cache(IRelationLink link) {
      relationsCache.put(link.getPersistenceMemo().getLinkId(), link.getLinkDescriptor().getTransactionId(), link);
   }

   /**
    * Update local cache
    * 
    * @param event
    */
   public void updateRelationCache(ISkynetRelationLinkEvent event, Collection<Event> localEvents, TransactionId newTransactionId, TransactionId notEditableTransactionId) {
      try {
         Integer relId = event.getRelId();
         Integer gammaId = event.getGammaId();
         Branch branch = BranchPersistenceManager.getInstance().getBranch(event.getBranchId());
         int artAId = event.getArtAId();
         int artBId = event.getArtBId();

         if (relationsCache.containsKey(relId, newTransactionId)) {
            IRelationLink link = relationsCache.get(relId, newTransactionId);

            if (event instanceof NetworkRelationLinkModifiedEvent) {

               NetworkRelationLinkModifiedEvent remoteRelationLinkModifiedEvent =
                     (NetworkRelationLinkModifiedEvent) event;

               if (link.isDirty()) {
                  String msg = "There has been a conflict with a relationLink";

                  System.err.println(msg);
                  logger.log(Level.SEVERE, msg);
               } else {
                  link.setRationale(remoteRelationLinkModifiedEvent.getRationale(), false);
                  link.setAOrder(remoteRelationLinkModifiedEvent.getAOrder());
                  link.setBOrder(remoteRelationLinkModifiedEvent.getBOrder());
                  link.setNotDirty();

                  localEvents.add(new TransactionRelationModifiedEvent(link, branch,
                        link.getLinkDescriptor().getName(), link.getASideName(), ModType.Changed, this));
               }
            } else if (event instanceof NetworkRelationLinkDeletedEvent) {
               Artifact aArt = link.getArtifactA();
               Artifact bArt = link.getArtifactB();

               if (aArt.isLinkManagerLoaded()) aArt.getLinkManager().removeLink(link);

               if (bArt.isLinkManagerLoaded()) bArt.getLinkManager().removeLink(link);

               localEvents.add(new TransactionRelationModifiedEvent(link, branch, link.getLinkDescriptor().getName(),
                     link.getASideName(), ModType.Deleted, this));
            }
         } else if (event instanceof NetworkNewRelationLinkEvent) {
            NetworkNewRelationLinkEvent newRelationEvent = (NetworkNewRelationLinkEvent) event;

            try {
               ArtifactFactory<?> aFactory =
                     (ArtifactFactory<?>) configurationManager.getFactoryFromName(newRelationEvent.getAFactoryName());
               ArtifactFactory<?> bFactory =
                     (ArtifactFactory<?>) configurationManager.getFactoryFromName(newRelationEvent.getBFactoryName());

               if (aFactory.containsArtifact(artAId, branch.getBranchId()) || bFactory.containsArtifact(artBId,
                     branch.getBranchId())) {
                  Artifact artA =
                        artifactManager.getArtifactFromId(artAId, transactionIdManager.getEditableTransactionId(branch));
                  Artifact artB =
                        artifactManager.getArtifactFromId(artBId, transactionIdManager.getEditableTransactionId(branch));

                  IRelationLinkDescriptor descriptor =
                        relationLinkDescriptorCache.getDescriptor(newRelationEvent.getRelTypeId(), branch);

                  LinkPersistenceMemo memo = new LinkPersistenceMemo(relId, gammaId);
                  String rationale = newRelationEvent.getRationale();
                  int aOrder = newRelationEvent.getAOrder();
                  int bOrder = newRelationEvent.getBOrder();

                  DynamicRelationLink link =
                        new DynamicRelationLink(artA, artB, descriptor, memo, rationale, aOrder, bOrder, false);

                  if (artA.isLinkManagerLoaded()) artA.getLinkManager().addLink(link);
                  if (artB.isLinkManagerLoaded()) artB.getLinkManager().addLink(link);

                  relationsCache.put(relId, newTransactionId, link);
                  link.setNotDirty();

                  localEvents.add(new TransactionRelationModifiedEvent(link, branch,
                        link.getLinkDescriptor().getName(), link.getASideName(), ModType.Added, this));
               }
            } catch (Exception e) {
               logger.log(Level.SEVERE, e.toString(), e);
            }
         }
      } catch (SQLException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      }
   }

   /**
    * @param newArtifact
    * @param oldArtifact
    * @param links
    */
   public void resetLinksToNewArtifact(Artifact newArtifact, Artifact oldArtifact, Collection<IRelationLink> links) {
      if (links.size() > 0) {
         // newArtifact.clearLinkManager();

         for (IRelationLink link : links) {
            if (link.getArtifactA() == oldArtifact) {
               link.setArtifactA(newArtifact, true);
            } else if (link.getArtifactB() == oldArtifact) {
               link.setArtifactB(newArtifact, true);
            } else {
               throw new IllegalArgumentException("oldArtifact does not belong on one of the links supplied.");
            }
            newArtifact.createOrGetEmptyLinkManager().addLink(link);
         }
         // oldArtifact.clearLinkManager();
      }
   }

   public void deCacheLinks(Collection<IRelationLink> links) {
   }

   public void setRelatedManagers() {
   }

   public void insertObjectsOnSideB(Artifact sideAArt, Artifact targetArt, Collection<Artifact> insertArtifacts, RelationSide relSide, InsertLocation insertLocation) throws SQLException {
      // Ensure all insertArts exist first; if not, add them
      Set<Artifact> bSideArts = sideAArt.getArtifacts(relSide);
      for (Artifact insertArtifact : insertArtifacts) {
         if (!bSideArts.contains(insertArtifact)) sideAArt.relate(relSide, insertArtifact, true);
      }

      // For each insertArt
      for (Artifact insertArt : insertArtifacts) {

         // Find targetLink; re-do this every time in case it moves/changes
         IRelationLink targetLink = null;
         if (sideAArt.getRelations(relSide, targetArt).size() == 1)
            targetLink = sideAArt.getRelations(relSide, targetArt).iterator().next();
         else
            logger.log(Level.SEVERE, "More than one link exists for sideBArt");

         // Find insertArtLink
         IRelationLink insertLink = null;
         if (sideAArt.getRelations(relSide, insertArt).size() == 1)
            insertLink = sideAArt.getRelations(relSide, insertArt).iterator().next();
         else
            logger.log(Level.SEVERE, "More than one link exists for sideBArt");

         // Move insertArtLink to insertLocation
         RelationLinkGroup group = sideAArt.getLinkManager().getGroup(RelationSide.UNIVERSAL_GROUPING__MEMBERS);
         group.moveLink(targetLink, insertLink, insertLocation != InsertLocation.AfterTarget);
         sideAArt.persist(true);
      }

   }

   public void moveObjectB(Artifact sideAArt, Artifact sideBArt, RelationSide relSide, Direction dir) throws SQLException {
      Set<Artifact> arts = sideAArt.getArtifacts(relSide);
      Artifact prevArt = null;
      Artifact nextArt = null;
      Object objs[] = arts.toArray();
      for (int x = 0; x < arts.size(); x++) {
         if (objs[x].equals(sideBArt)) {
            if (x >= 1) prevArt = (Artifact) objs[x - 1];
            if (x < objs.length - 1) nextArt = (Artifact) objs[x + 1];
         }
      }
      IRelationLink thisLink = null;
      if (sideAArt.getRelations(relSide, sideBArt).size() == 1)
         thisLink = sideAArt.getRelations(relSide, sideBArt).iterator().next();
      else
         logger.log(Level.SEVERE, "More than one link exists for sideBArt");
      IRelationLink prevLink = null;
      if (prevArt != null) {
         if (sideAArt.getRelations(relSide, prevArt).size() == 1)
            prevLink = sideAArt.getRelations(relSide, prevArt).iterator().next();
         else
            logger.log(Level.SEVERE, "More than one link exists for prevArt");
      }
      IRelationLink nextLink = null;
      if (nextArt != null) {
         if (sideAArt.getRelations(relSide, nextArt).size() == 1)
            nextLink = sideAArt.getRelations(relSide, nextArt).iterator().next();
         else
            logger.log(Level.SEVERE, "More than one link exists for nextArt");
      }

      if (dir == Direction.Back && thisLink != null && prevLink != null) {
         prevLink.getArtifactA().getLinkManager();
         prevLink.getArtifactB().getLinkManager();
         thisLink.getArtifactA().getLinkManager();
         thisLink.getArtifactB().getLinkManager();
         prevLink.swapAOrder(thisLink);
         prevLink.persist();
         thisLink.persist();
      } else if (dir == Direction.Forward && thisLink != null && nextLink != null) {
         nextLink.getArtifactA().getLinkManager();
         nextLink.getArtifactB().getLinkManager();
         thisLink.getArtifactA().getLinkManager();
         thisLink.getArtifactB().getLinkManager();
         nextLink.swapAOrder(thisLink);
         nextLink.persist();
         thisLink.persist();
      }

   }

   public void moveObjectA(Artifact sideAArt, Artifact sideBArt, RelationSide relSide, Direction dir) throws SQLException {
      Set<Artifact> arts = sideBArt.getArtifacts(relSide);
      Artifact prevArt = null;
      Artifact nextArt = null;
      Object objs[] = arts.toArray();
      for (int x = 0; x < arts.size(); x++) {
         if (objs[x].equals(sideAArt)) {
            if (x >= 1) prevArt = (Artifact) objs[x - 1];
            if (x < objs.length - 1) nextArt = (Artifact) objs[x + 1];
         }
      }
      IRelationLink thisLink = null;
      if (sideBArt.getRelations(relSide, sideAArt).size() == 1)
         thisLink = sideBArt.getRelations(relSide, sideAArt).iterator().next();
      else
         logger.log(Level.SEVERE, "More than one link exists for sideBArt");
      IRelationLink prevLink = null;
      if (prevArt != null) {
         if (sideBArt.getRelations(relSide, prevArt).size() == 1)
            prevLink = sideBArt.getRelations(relSide, prevArt).iterator().next();
         else
            logger.log(Level.SEVERE, "More than one link exists for prevArt");
      }
      IRelationLink nextLink = null;
      if (nextArt != null) {
         if (sideBArt.getRelations(relSide, nextArt).size() == 1)
            nextLink = sideBArt.getRelations(relSide, nextArt).iterator().next();
         else
            logger.log(Level.SEVERE, "More than one link exists for nextArt");
      }

      if (dir == Direction.Back && thisLink != null && prevLink != null) {
         prevLink.getArtifactA().getLinkManager();
         prevLink.getArtifactB().getLinkManager();
         thisLink.getArtifactA().getLinkManager();
         thisLink.getArtifactB().getLinkManager();
         prevLink.swapBOrder(thisLink);
         prevLink.persist();
         thisLink.persist();
      } else if (dir == Direction.Forward && thisLink != null && nextLink != null) {
         nextLink.getArtifactA().getLinkManager();
         nextLink.getArtifactB().getLinkManager();
         thisLink.getArtifactA().getLinkManager();
         thisLink.getArtifactB().getLinkManager();
         nextLink.swapBOrder(thisLink);
         nextLink.persist();
         thisLink.persist();
      }
   }
}
