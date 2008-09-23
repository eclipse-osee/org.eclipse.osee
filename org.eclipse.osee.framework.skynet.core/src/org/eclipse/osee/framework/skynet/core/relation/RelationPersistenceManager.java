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

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.transaction.RelationTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * Controls all aspects of saving and recovering relations. The data-store happens to be a database, but that should be
 * abstracted by this class in such a way that the application code only has to worry about the fact that SQLExceptions
 * may be thrown. <br/><br/> Each relation object in the system represents a relation within the Define system. For this
 * reason, successive calls to the database for the same relation will return a reference to the same exact object.
 * 
 * @see org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager
 * @see org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager
 * @author Robert A. Fisher
 */
public class RelationPersistenceManager {
   private static final String UPDATE_RELATION_ORDERS =
         "UPDATE osee_define_rel_link SET a_order=?, b_order=? WHERE gamma_id=?";

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
         throw new SQLException(ex);
      }
   }

   public static void persist(RelationLink link, SkynetTransaction transaction) throws OseeCoreException, SQLException, ArtifactDoesNotExist {
      // The relation will be clean by the end of this, so mark it early so that this relation won't be
      // persisted by its other artifact
      link.setNotDirty();

      int gammaId = SequenceManager.getNextGammaId();
      ModificationType modId;

      if (link.isInDb()) {
         if (link.isDeleted()) {
            Artifact aArtifact = ArtifactCache.getActive(link.getAArtifactId(), link.getABranch());
            Artifact bArtifact = ArtifactCache.getActive(link.getBArtifactId(), link.getBBranch());

            if ((aArtifact != null && aArtifact.isDeleted()) || (bArtifact != null && bArtifact.isDeleted())) {
               modId = ModificationType.ARTIFACT_DELETED;
            } else {
               modId = ModificationType.DELETED;
               link.setGammaId(gammaId);
            }

            transaction.addRelationModifiedEvent(RelationPersistenceManager.instance, RelationModType.Deleted, link,
                  link.getBranch(), link.getRelationType().getTypeName());

         } else {
            link.setGammaId(gammaId);
            modId = ModificationType.CHANGE;

            transaction.addRelationModifiedEvent(RelationPersistenceManager.instance, RelationModType.Added, link,
                  link.getBranch(), link.getRelationType().getTypeName());

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

         int relationId = SequenceManager.getNextRelationId();
         link.setPersistenceIds(relationId, gammaId);
         modId = ModificationType.NEW;

         transaction.addRelationModifiedEvent(RelationPersistenceManager.instance, RelationModType.Added, link,
               link.getBranch(), link.getRelationType().getTypeName());

      }

      transaction.addTransactionDataItem(new RelationTransactionData(link, link.getGammaId(),
            transaction.getTransactionId(), modId, transaction.getBranch()));
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
         data.add(new Object[] {link.getAOrder(), link.getBOrder(), link.getGammaId()});
      }
      ConnectionHandler.runPreparedUpdateBatch(UPDATE_RELATION_ORDERS, data);
   }
}
