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
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkModifiedEvent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.PersistenceManager;
import org.eclipse.osee.framework.skynet.core.PersistenceManagerInit;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionBuilder;
import org.eclipse.osee.framework.skynet.core.transaction.data.RelationTransactionData;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.utility.RemoteLinkEventFactory;

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
    * Persist a relation, and if necessary, its two artifacts if they are not already in the datastore
    * 
    * @param relationLink The relationLink to persist.
    */
   public static void makePersistent(final RelationLink relationLink) throws SQLException {
      AbstractSkynetTxTemplate relationPersistTx = new AbstractSkynetTxTemplate(relationLink.getBranch()) {

         @Override
         protected void handleTxWork() throws Exception {
            if (relationLink.isDeleted()) {
               getTxBuilder().deleteLink(relationLink);
            } else {
               getTxBuilder().addLink(relationLink);
            }
         }
      };
      try {
         relationPersistTx.execute();
      } catch (Exception ex) {
         throw new SQLException(ex.getLocalizedMessage());
      }
   }

   public static void trace(RelationLink relationLink, boolean recurse, SkynetTransactionBuilder builder) throws Exception {
      if (relationLink.isDeleted()) {
         builder.deleteLink(relationLink);
      } else {
         builder.addLink(relationLink);

         if (recurse) {
            ArtifactPersistenceManager.getInstance().saveTrace(relationLink.getArtifactA(), recurse, builder);
            ArtifactPersistenceManager.getInstance().saveTrace(relationLink.getArtifactB(), recurse, builder);
         }
      }
   }

   private void insertRelationLinkTable(RelationLink relation, SkynetTransaction transaction) throws SQLException, ArtifactDoesNotExist {
      int gammaId = SkynetDatabase.getNextGammaId();
      ModType modType;
      ModificationType modId;

      if (!relation.isInDb()) {
         Artifact aArtifact = relation.getArtifact(RelationSide.SIDE_A);
         if (!aArtifact.isInDb()) {
            aArtifact.persistAttributes();
         }
         Artifact bArtifact = relation.getArtifact(RelationSide.SIDE_B);
         if (!bArtifact.isInDb()) {
            bArtifact.persistAttributes();
         }

         int relationId = Query.getNextSeqVal(null, SkynetDatabase.REL_LINK_ID_SEQ);
         relation.setPersistenceIds(relationId, gammaId);
         transaction.addRemoteEvent(RemoteLinkEventFactory.makeEvent(relation, transaction.getTransactionNumber()));
         modType = ModType.Added;
         modId = ModificationType.NEW;
      } else {
         relation.setGammaId(gammaId);

         transaction.addRemoteEvent(new NetworkRelationLinkModifiedEvent(relation.getGammaId(),
               relation.getBranch().getBranchId(), transaction.getTransactionNumber(), relation.getRelationId(),
               relation.getAArtifactId(), relation.getBArtifactId(), relation.getRationale(), relation.getAOrder(),
               relation.getBOrder(), SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId(),
               relation.getRelationType().getRelationTypeId()));

         modType = ModType.Changed;
         modId = ModificationType.CHANGE;
      }

      transaction.addTransactionDataItem(new RelationTransactionData(relation, gammaId,
            transaction.getTransactionNumber(), modId, transaction.getBranch()));

      transaction.addLocalEvent(new TransactionRelationModifiedEvent(relation, relation.getBranch(),
            relation.getRelationType().getTypeName(), relation.getASideName(), modType, this));
   }

   /**
    * Remove all relations stored in the list awaiting to be deleted.
    */
   static void deleteRelationLinks(final Collection<RelationLink> links, Branch branch) throws SQLException {

      if (!links.isEmpty()) {
         AbstractSkynetTxTemplate deleteRelationsTx = new AbstractSkynetTxTemplate(branch) {

            @Override
            protected void handleTxWork() throws SQLException {
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
      if (!relationLink.isInDb()) return;

      int gammaId = SkynetDatabase.getNextGammaId();

      transaction.addTransactionDataItem(new RelationTransactionData(relationLink, gammaId,
            transaction.getTransactionNumber(), ModificationType.DELETED, transaction.getBranch()));

      transaction.addRemoteEvent(new NetworkRelationLinkDeletedEvent(
            relationLink.getRelationType().getRelationTypeId(), relationLink.getGammaId(),
            relationLink.getBranch().getBranchId(), transaction.getTransactionNumber(), relationLink.getRelationId(),
            relationLink.getArtifactId(RelationSide.SIDE_A), relationLink.getArtifactId(RelationSide.SIDE_B),
            SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId()));

      transaction.addLocalEvent(new TransactionRelationModifiedEvent(relationLink, relationLink.getBranch(),
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

         deleteSql.append(link.getRelationId());
         firstTime = false;
         link.delete();
      }
      deleteSql.append(" ) ");
      ConnectionHandler.runPreparedUpdate(deleteSql.toString());
      links.clear();
   }

   public void doSave(RelationLink link, SkynetTransaction transaction) throws SQLException, ArtifactDoesNotExist {
      // The relation will be clean by the end of this, so mark it early so that this relation won't be
      // persisted by its other artifact
      link.setNotDirty();

      if (!link.isInDb() || link.isVersionControlled()) {
         insertRelationLinkTable(link, transaction);
      } else {
         String rationale = link.getRationale();

         ConnectionHandler.runPreparedUpdate(
               "UPDATE " + RELATION_LINK_VERSION_TABLE + " SET a_art_id=?, b_art_id=?, a_order_value=?, b_order_value=?, rationale=? WHERE rel_link_id=?",
               SQL3DataType.INTEGER, link.getArtifactId(RelationSide.SIDE_A), SQL3DataType.INTEGER,
               link.getArtifactId(RelationSide.SIDE_B), SQL3DataType.INTEGER, link.getOrder(RelationSide.SIDE_A),
               SQL3DataType.INTEGER, link.getOrder(RelationSide.SIDE_B), SQL3DataType.VARCHAR, rationale,
               SQL3DataType.INTEGER, link.getRelationId());

         transaction.addRemoteEvent(new NetworkRelationLinkModifiedEvent(link.getGammaId(),
               link.getBranch().getBranchId(), transaction.getTransactionNumber(), link.getRelationId(),
               link.getArtifactId(RelationSide.SIDE_A), link.getArtifactId(RelationSide.SIDE_B), rationale,
               link.getOrder(RelationSide.SIDE_A), link.getOrder(RelationSide.SIDE_B),
               SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId(),
               link.getRelationType().getRelationTypeId()));

         transaction.addLocalEvent(new TransactionRelationModifiedEvent(link, link.getBranch(),
               link.getRelationType().getTypeName(), link.getASideName(), ModType.Changed, this));
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
               SQL3DataType.INTEGER, link.getGammaId()});
      }
      ConnectionHandler.runPreparedUpdateBatch(UPDATE_RELATION_ORDERS, data);
   }

   public void insertObjectsOnSideB(Artifact sideAArt, Artifact targetArt, Collection<Artifact> insertArtifacts, CoreRelationEnumeration relSide, InsertLocation insertLocation) throws SQLException {
      // Ensure all insertArts exist first; if not, add them
      Set<Artifact> bSideArts = sideAArt.getRelatedArtifacts(relSide);
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
         RelationLinkGroup group =
               sideAArt.getLinkManager().getGroup(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS);
         group.moveLink(targetLink, insertLink, insertLocation != InsertLocation.AfterTarget);
         sideAArt.persistAttributesAndRelations();
      }

   }

   public void moveObjectB(Artifact sideAArt, Artifact sideBArt, CoreRelationEnumeration relSide, Direction dir) throws SQLException {
      Set<Artifact> arts = sideAArt.getRelatedArtifacts(relSide);
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

   public void moveObjectA(Artifact sideAArt, Artifact sideBArt, CoreRelationEnumeration relSide, Direction dir) throws SQLException, ArtifactDoesNotExist {
      Set<Artifact> arts = sideBArt.getRelatedArtifacts(relSide);
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
