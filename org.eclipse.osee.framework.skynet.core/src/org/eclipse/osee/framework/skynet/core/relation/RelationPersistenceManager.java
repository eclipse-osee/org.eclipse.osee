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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkNewRelationLinkEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkModifiedEvent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.data.RelationTransactionData;

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
public class RelationPersistenceManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(RelationPersistenceManager.class);
   private static final String UPDATE_RELATION_ORDERS =
         "UPDATE " + RELATION_LINK_VERSION_TABLE + " t1 SET a_order=?, b_order=? WHERE gamma_id=?";

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
    * Persist a relation, and if necessary, its two artifacts if they are not already in the datastore
    * 
    * @param relationLink The relationLink to persist.
    */
   public static void makePersistent(final RelationLink relationLink) throws SQLException {
      AbstractSkynetTxTemplate relationPersistTx = new AbstractSkynetTxTemplate(relationLink.getBranch()) {

         @Override
         protected void handleTxWork() throws OseeCoreException, SQLException {
            getTxBuilder().addLinkToPersist(relationLink);
         }
      };
      try {
         relationPersistTx.execute();
      } catch (Exception ex) {
         throw new SQLException(ex.getLocalizedMessage());
      }
   }

   public static void persist(RelationLink link, SkynetTransaction transaction) throws SQLException, ArtifactDoesNotExist {
      // The relation will be clean by the end of this, so mark it early so that this relation won't be
      // persisted by its other artifact
      link.setNotDirty();

      int gammaId = SkynetDatabase.getNextGammaId();
      ModType modType;
      ModificationType modId;

      if (link.isInDb()) {
         if (link.isDeleted()) {
            modType = ModType.Deleted;
            modId = ModificationType.DELETED;

            transaction.addRemoteEvent(new NetworkRelationLinkDeletedEvent(link.getRelationType().getRelationTypeId(),
                  link.getGammaId(), link.getBranch().getBranchId(), transaction.getTransactionNumber(),
                  link.getRelationId(), link.getArtifactId(RelationSide.SIDE_A),
                  link.getArtifactId(RelationSide.SIDE_B), SkynetAuthentication.getUser().getArtId()));
         } else {
            link.setGammaId(gammaId);
            modType = ModType.Changed;
            modId = ModificationType.CHANGE;

            transaction.addRemoteEvent(new NetworkRelationLinkModifiedEvent(link.getGammaId(),
                  link.getBranch().getBranchId(), transaction.getTransactionNumber(), link.getRelationId(),
                  link.getAArtifactId(), link.getBArtifactId(), link.getRationale(), link.getAOrder(),
                  link.getBOrder(), SkynetAuthentication.getUser().getArtId(),
                  link.getRelationType().getRelationTypeId()));
         }
      } else {
         if (link.isDeleted()) return;

         Artifact aArtifact = link.getArtifact(RelationSide.SIDE_A);
         if (!aArtifact.isInDb()) {
            aArtifact.persistAttributes();
         }
         Artifact bArtifact = link.getArtifact(RelationSide.SIDE_B);
         if (!bArtifact.isInDb()) {
            bArtifact.persistAttributes();
         }

         int relationId = Query.getNextSeqVal(SkynetDatabase.REL_LINK_ID_SEQ);
         link.setPersistenceIds(relationId, gammaId);
         modType = ModType.Added;
         modId = ModificationType.NEW;

         transaction.addRemoteEvent(new NetworkNewRelationLinkEvent(link.getGammaId(), link.getBranch().getBranchId(),
               transaction.getTransactionNumber(), link.getRelationId(), link.getAArtifactId(), link.getBArtifactId(),
               link.getRationale(), link.getAOrder(), link.getBOrder(), link.getRelationType().getRelationTypeId(),
               link.getRelationType().getTypeName(), SkynetAuthentication.getUser().getArtId()));
      }

      transaction.addTransactionDataItem(new RelationTransactionData(link, gammaId, transaction.getTransactionNumber(),
            modId, transaction.getBranch()));

      transaction.addLocalEvent(new TransactionRelationModifiedEvent(link, link.getBranch(),
            link.getRelationType().getTypeName(), link.getASideName(), modType, instance));
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

   public void moveObjectB(Artifact sideAArt, Artifact sideBArt, IRelationEnumeration relSide, Direction dir) throws SQLException {
      List<Artifact> arts = sideAArt.getRelatedArtifacts(relSide);
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
         //         prevLink.getArtifactA().getLinkManager();
         //         prevLink.getArtifactB().getLinkManager();
         //         thisLink.getArtifactA().getLinkManager();
         //         thisLink.getArtifactB().getLinkManager();
         prevLink.swapAOrder(thisLink);
         prevLink.persist();
         thisLink.persist();
      } else if (dir == Direction.Forward && thisLink != null && nextLink != null) {
         //         nextLink.getArtifactA().getLinkManager();
         //         nextLink.getArtifactB().getLinkManager();
         //         thisLink.getArtifactA().getLinkManager();
         //         thisLink.getArtifactB().getLinkManager();
         nextLink.swapAOrder(thisLink);
         nextLink.persist();
         thisLink.persist();
      }

   }

}
