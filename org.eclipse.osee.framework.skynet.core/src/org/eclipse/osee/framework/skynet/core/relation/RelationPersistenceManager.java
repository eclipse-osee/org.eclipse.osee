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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionBuilder;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.transaction.data.RelationTransactionData;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.utility.RemoteLinkEventFactory;
import org.eclipse.osee.framework.ui.plugin.event.Event;

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
   private static final String SELECT_LINKS =
         "SELECT rl_1.*, txs1.* FROM osee_define_rel_link rl_1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE (rl_1.a_art_id = ?  OR rl_1.b_art_id = ?) AND rl_1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= ? AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? order by rl_1.rel_link_id, txs1.transaction_id desc";

   private static final String UPDATE_RELATION_ORDERS =
         "UPDATE " + RELATION_LINK_VERSION_TABLE + " t1 SET a_order_value=?, b_order_value=? WHERE gamma_id=?";
   private ArtifactPersistenceManager artifactManager;

   // This must be declared here cause it can't be declared in enum RelationSide
   public static DoubleKeyHashMap<String, Boolean, IRelationEnumeration> sideHash =
         new DoubleKeyHashMap<String, Boolean, IRelationEnumeration>();
   public enum Direction {
      Back, Forward
   };
   public enum InsertLocation {
      BeforeTarget, AfterTarget
   }
   private static final RelationPersistenceManager instance = new RelationPersistenceManager();

   private RelationPersistenceManager() {
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
   }

   /**
    * Persist a relation link so that its current state can be acquired at a later time.
    * 
    * @param relationLink The relationLink to persist.
    */
   public void makePersistent(final RelationLink relationLink) throws SQLException {
      if (relationLink.isDirty()) {

         AbstractSkynetTxTemplate relationPersistTx = new AbstractSkynetTxTemplate(relationLink.getBranch()) {

            @Override
            protected void handleTxWork() throws Exception {
               trace(relationLink, false, getTxBuilder());
            }

         };
         try {
            relationPersistTx.execute();
         } catch (Exception ex) {
            throw new SQLException(ex.getLocalizedMessage());
         }
      }
   }

   public void trace(RelationLink relationLink, boolean recurse, SkynetTransactionBuilder builder) throws Exception {
      if (relationLink.isDirty()) {
         builder.addLink(relationLink);

         if (recurse) {
            artifactManager.saveTrace(relationLink.getArtifactA(), recurse, builder);
            artifactManager.saveTrace(relationLink.getArtifactB(), recurse, builder);
         }
      }
   }

   private void insertRelationLinkTable(RelationLink relationLink, SkynetTransaction transaction) throws SQLException {

      int linkId;
      int gammaId = SkynetDatabase.getNextGammaId();
      Artifact aArtifact = relationLink.getArtifactA();
      Artifact bArtifact = relationLink.getArtifactB();
      int aArtId = aArtifact.getArtId();
      int aArtTypeId = aArtifact.getArtTypeId();
      int bArtId = bArtifact.getArtId();
      int bArtTypeId = bArtifact.getArtTypeId();
      ModType modType;
      ModificationType modId;

      if (relationLink.getPersistenceMemo() == null) {
         linkId = Query.getNextSeqVal(null, SkynetDatabase.REL_LINK_ID_SEQ);
         relationLink.setPersistenceMemo(new LinkPersistenceMemo(linkId, gammaId));
         RelationCache.cache(relationLink, transaction.getTransactionId().getTransactionNumber());
         transaction.addRemoteEvent(RemoteLinkEventFactory.makeEvent(relationLink, transaction.getTransactionNumber()));
         modType = ModType.Added;
         modId = ModificationType.NEW;
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
         modId = ModificationType.CHANGE;
      }

      transaction.addTransactionDataItem(new RelationTransactionData(relationLink, gammaId,
            transaction.getTransactionNumber(), modId, transaction.getBranch()));

      transaction.addLocalEvent(new TransactionRelationModifiedEvent(relationLink, aArtifact.getBranch(),
            relationLink.getRelationType().getTypeName(), relationLink.getASideName(), modType, this));
   }

   /**
    * Remove all relations stored in the list awaiting to be deleted.
    */
   protected void deleteRelationLinks(final Collection<RelationLink> links, Branch branch) throws SQLException {

      if (!links.isEmpty()) {
         AbstractSkynetTxTemplate deleteRelationsTx = new AbstractSkynetTxTemplate(branch) {

            @Override
            protected void handleTxWork() throws Exception {
               for (RelationLink link : links) {
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

   public void doDelete(RelationLink relationLink, SkynetTransaction transaction) throws SQLException {
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
            transaction.getTransactionNumber(), ModificationType.DELETED, transaction.getBranch()));

      transaction.addRemoteEvent(new NetworkRelationLinkDeletedEvent(relationLink.getPersistenceMemo().getGammaId(),
            relationLink.getBranch().getBranchId(), transaction.getTransactionNumber(),
            relationLink.getPersistenceMemo().getLinkId(), aArtId, aArtTypeId, bArtId, bArtTypeId,
            aArtifact.getFactory().getClass().getCanonicalName(), bArtifact.getFactory().getClass().getCanonicalName(),
            SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId()));

      transaction.addLocalEvent(new TransactionRelationModifiedEvent(relationLink, aArtifact.getBranch(),
            relationLink.getRelationType().getTypeName(), relationLink.getASideName(), ModType.Deleted, this));
   }

   /**
    * Remove all relations stored in the list awaiting to be deleted.
    * 
    * @throws SQLException
    */
   public void purgeRelationLinks(Collection<RelationLink> links) throws SQLException {
      boolean firstTime = true;

      if (links.isEmpty()) return;

      // TODO this will fail if over 1000 links are removed at once ... consider handling this by
      // breaking into chunks
      StringBuffer deleteSql =
            new StringBuffer(" Delete from " + RELATION_LINK_VERSION_TABLE + " WHERE REL_LINK_ID in ( ");

      for (RelationLink link : links) {
         if (!firstTime) deleteSql.append(" , ");

         deleteSql.append(link.getPersistenceMemo().getLinkId());
         firstTime = false;
         link.delete();
      }
      deleteSql.append(" ) ");
      ConnectionHandler.runPreparedUpdate(deleteSql.toString());
      links.clear();
   }

   public void doSave(RelationLink link, SkynetTransaction transaction) throws SQLException {

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

               link.getArtifactA().persist(false);
               link.getArtifactB().persist(false);

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
                        link.getRelationType().getTypeName(), link.getASideName(), ModType.Changed, this));

               }

            }
         } catch (SQLException ex) {
            throw new RuntimeException(ex);
         }
      }
   }

   /**
    * Updates the aOrder and bOrder of a RelationLink without producing a transaction.
    * 
    * @param link
    * @throws SQLException
    */
   public void updateRelationOrdersWithoutTransaction(RelationLink... links) throws SQLException {
      List<Object[]> data = new LinkedList<Object[]>();

      for (RelationLink link : links) {
         data.add(new Object[] {SQL3DataType.INTEGER, link.getAOrder(), SQL3DataType.INTEGER, link.getBOrder(),
               SQL3DataType.INTEGER, link.getPersistenceMemo().getGammaId()});
      }
      ConnectionHandler.runPreparedUpdateBatch(UPDATE_RELATION_ORDERS, data);
   }

   public void populateArtifactRelations(Artifact artifact) throws SQLException {
      int previousRelationId = -1;
      int branchId = artifact.getBranch().getBranchId();
      TransactionId transactionId;
      if (artifact.getTransactionNumber() == 0) {
         transactionId = TransactionIdManager.getInstance().getEditableTransactionId(artifact.getBranch());
      } else {
         transactionId =
               TransactionIdManager.getInstance().getPossiblyEditableTransactionId(artifact.getTransactionNumber());
      }
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(100, SELECT_LINKS, SQL3DataType.INTEGER, artifact.getArtId(),
                     SQL3DataType.INTEGER, artifact.getArtId(), SQL3DataType.INTEGER, transactionId,
                     SQL3DataType.INTEGER, branchId);
         ResultSet rSet = chStmt.getRset();

         while (rSet.next()) {
            int relId = rSet.getInt("rel_link_id");
            if (relId == previousRelationId) {
               continue;
            }
            previousRelationId = relId;
            if (rSet.getInt("modification_id") == ModificationType.DELETED.getValue()) {
               continue;
            }
            loadLinkData(rSet, relId, transactionId);

         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   private void loadLinkData(ResultSet rSet, int relId, TransactionId transactionId) throws SQLException {
      RelationLink link = null;
      String rationale = rSet.getString("rationale");
      int aArtId = rSet.getInt("a_art_id");
      int bArtId = rSet.getInt("b_art_id");
      int aOrderValue = rSet.getInt("a_order_value");
      int bOrderValue = rSet.getInt("b_order_value");
      int gammaId = rSet.getInt("gamma_id");

      link = RelationCache.getRelation(relId, transactionId.getTransactionNumber());

      if (link == null) {
         try {
            Artifact artA = artifactManager.getArtifactFromId(aArtId, transactionId);
            Artifact artB = artifactManager.getArtifactFromId(bArtId, transactionId);
            RelationType relationType = RelationTypeManager.getType(rSet.getInt("rel_link_type_id"));

            link =
                  new RelationLink(artA, artB, relationType, new LinkPersistenceMemo(relId, gammaId),
                        (rationale != null && !rationale.equals("null")) ? rationale : "", aOrderValue, bOrderValue,
                        false);

            RelationCache.cache(link, transactionId.getTransactionNumber());

            if (link.getArtifactA().isLinksLoaded()) {
               link.getArtifactA().getLinkManager().addLink(link);
            }
            if (link.getArtifactB().isLinksLoaded()) {
               link.getArtifactB().getLinkManager().addLink(link);
            }
         } catch (RuntimeException ex) {
            logger.log(Level.WARNING,
                  ex.getLocalizedMessage() + ": relId = " + relId + " a_art_id = " + aArtId + " b_art_id = " + bArtId,
                  ex);
         }
      } else {
         if (!link.getArtifactA().getLinkManager().deletedLinks.contains(link) && !link.isDeleted()) {

            link.getArtifactA().getLinkManager().addLink(link);
            link.getArtifactB().getLinkManager().addLink(link);
         }
      }
   }

   /**
    * Update local cache
    * 
    * @param event
    */
   public void updateRelationCache(ISkynetRelationLinkEvent event, Collection<Event> localEvents, int newTransactionId) {
      try {
         Integer relId = event.getRelId();
         Integer gammaId = event.getGammaId();
         Branch branch = BranchPersistenceManager.getInstance().getBranch(event.getBranchId());
         int artAId = event.getArtAId();
         int artBId = event.getArtBId();

         RelationLink link = RelationCache.getRelation(relId, newTransactionId);
         if (link != null) {
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
                        link.getRelationType().getTypeName(), link.getASideName(), ModType.Changed, this));
               }
            } else if (event instanceof NetworkRelationLinkDeletedEvent) {
               Artifact aArt = link.getArtifactA();
               Artifact bArt = link.getArtifactB();

               if (aArt.isLinksLoaded()) aArt.getLinkManager().removeLink(link);

               if (bArt.isLinksLoaded()) bArt.getLinkManager().removeLink(link);

               localEvents.add(new TransactionRelationModifiedEvent(link, branch, link.getRelationType().getTypeName(),
                     link.getASideName(), ModType.Deleted, this));
            }
         } else if (event instanceof NetworkNewRelationLinkEvent) {
            NetworkNewRelationLinkEvent newRelationEvent = (NetworkNewRelationLinkEvent) event;

            try {
               Artifact artA = ArtifactCache.get(artAId, branch);
               Artifact artB = ArtifactCache.get(artBId, branch);

               if (artA != null && artB != null) {
                  RelationType relationType = RelationTypeManager.getType(newRelationEvent.getRelTypeId());

                  LinkPersistenceMemo memo = new LinkPersistenceMemo(relId, gammaId);
                  String rationale = newRelationEvent.getRationale();
                  int aOrder = newRelationEvent.getAOrder();
                  int bOrder = newRelationEvent.getBOrder();

                  RelationLink newLink =
                        new RelationLink(artA, artB, relationType, memo, rationale, aOrder, bOrder, false);

                  if (artA.isLinksLoaded()) artA.getLinkManager().addLink(newLink);
                  if (artB.isLinksLoaded()) artB.getLinkManager().addLink(newLink);

                  RelationCache.cache(newLink, newTransactionId);
                  localEvents.add(new TransactionRelationModifiedEvent(newLink, branch,
                        newLink.getRelationType().getTypeName(), newLink.getASideName(), ModType.Added, this));
               }
            } catch (Exception e) {
               logger.log(Level.SEVERE, e.toString(), e);
            }
         }
      } catch (SQLException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      }
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
         RelationLink targetLink = null;
         if (sideAArt.getRelations(relSide, targetArt).size() == 1)
            targetLink = sideAArt.getRelations(relSide, targetArt).iterator().next();
         else
            logger.log(Level.SEVERE, "More than one link exists for sideBArt");

         // Find insertArtLink
         RelationLink insertLink = null;
         if (sideAArt.getRelations(relSide, insertArt).size() == 1)
            insertLink = sideAArt.getRelations(relSide, insertArt).iterator().next();
         else
            logger.log(Level.SEVERE, "More than one link exists for sideBArt");

         // Move insertArtLink to insertLocation
         RelationLinkGroup group = sideAArt.getLinkManager().getGroup(RelationSide.UNIVERSAL_GROUPING__MEMBERS);
         group.moveLink(targetLink, insertLink, insertLocation != InsertLocation.AfterTarget);
         sideAArt.persist();
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
      RelationLink thisLink = null;
      if (sideAArt.getRelations(relSide, sideBArt).size() == 1)
         thisLink = sideAArt.getRelations(relSide, sideBArt).iterator().next();
      else
         logger.log(Level.SEVERE, "More than one link exists for sideBArt");
      RelationLink prevLink = null;
      if (prevArt != null) {
         if (sideAArt.getRelations(relSide, prevArt).size() == 1)
            prevLink = sideAArt.getRelations(relSide, prevArt).iterator().next();
         else
            logger.log(Level.SEVERE, "More than one link exists for prevArt");
      }
      RelationLink nextLink = null;
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

   public void moveObjectA(Artifact sideAArt, Artifact sideBArt, RelationSide relSide, Direction dir) throws SQLException, ArtifactDoesNotExist {
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
      RelationLink thisLink = null;
      if (sideBArt.getRelations(relSide, sideAArt).size() == 1)
         thisLink = sideBArt.getRelations(relSide, sideAArt).iterator().next();
      else
         logger.log(Level.SEVERE, "More than one link exists for sideBArt");
      RelationLink prevLink = null;
      if (prevArt != null) {
         if (sideBArt.getRelations(relSide, prevArt).size() == 1)
            prevLink = sideBArt.getRelations(relSide, prevArt).iterator().next();
         else
            logger.log(Level.SEVERE, "More than one link exists for prevArt");
      }
      RelationLink nextLink = null;
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
