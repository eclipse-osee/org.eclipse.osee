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

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.DefaultBasicGuidRelation;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.RelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.PersistEvent;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public class RelationTransactionData extends BaseTransactionData {
   private static final String INSERT_INTO_RELATION_TABLE =
         "INSERT INTO osee_relation_link (rel_link_id, rel_link_type_id, a_art_id, b_art_id, rationale, gamma_id) VALUES (?,?,?,?,?,?)";

   private final RelationLink relation;

   public RelationTransactionData(RelationLink relation, ModificationType modificationType) throws OseeDataStoreException {
      super(relation.getId(), modificationType);
      this.relation = relation;
   }

   @Override
   public OseeSql getSelectTxNotCurrentSql() {
      return OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS;
   }

   @Override
   protected void addInsertToBatch(SkynetTransaction transaction) throws OseeCoreException {
      super.addInsertToBatch(transaction);
      if (!useExistingBackingData()) {
         internalAddInsertToBatch(transaction, 4, INSERT_INTO_RELATION_TABLE, relation.getId(),
               relation.getRelationType().getId(), relation.getAArtifactId(), relation.getBArtifactId(),
               relation.getRationale(), getGammaId());
      }
   }

   @Override
   protected void internalUpdate(TransactionRecord transactionId) throws OseeCoreException {
      relation.internalSetGammaId(getGammaId());
   }

   @Override
   protected void internalClearDirtyState() {
      relation.setNotDirty();
   }

   @Override
   protected void internalOnRollBack() throws OseeCoreException {
   }

   @Override
   protected int createGammaId() throws OseeCoreException {
      int newGammaId = 0;
      if (useExistingBackingData()) {
         newGammaId = relation.getGammaId();
      } else {
         newGammaId = ConnectionHandler.getSequence().getNextGammaId();
      }
      return newGammaId;
   }

   @Override
   protected void internalAddToEvents(Collection<ArtifactTransactionModifiedEvent> events, PersistEvent persistEvent) throws OseeCoreException {
      RelationEventType relationEventType =
            getModificationType().isDeleted() ? RelationEventType.Deleted : RelationEventType.Added;
      events.add(new RelationModifiedEvent(new Sender(this.getClass().getName()), relationEventType, relation,
            relation.getBranch(), relation.getRelationType().getName()));
      DefaultBasicGuidRelation defaultBasicGuidRelation =
            new DefaultBasicGuidRelation(relation.getBranch().getGuid(), relation.getRelationType().getGuid(),
                  relation.getId(), relation.getGammaId(), relation.getArtifactA().getBasicGuidArtifact(),
                  relation.getArtifactB().getBasicGuidArtifact());
      if (getModificationType() == ModificationType.ARTIFACT_DELETED || getModificationType() == ModificationType.DELETED) {
         persistEvent.getRelations().add(
               new EventBasicGuidRelation(RelationEventType.Deleted, relation.getAArtifactId(),
                     relation.getBArtifactId(), defaultBasicGuidRelation));
      } else if (getModificationType() == ModificationType.MODIFIED) {
         EventBasicGuidRelation event =
               new EventBasicGuidRelation(RelationEventType.ModifiedRationale, relation.getAArtifactId(),
                     relation.getBArtifactId(), defaultBasicGuidRelation);
         event.setRationale(relation.getRationale());
         persistEvent.getRelations().add(event);
      } else if (getModificationType() == ModificationType.INTRODUCED || getModificationType() == ModificationType.NEW || getModificationType() == ModificationType.UNDELETED) {
         persistEvent.getRelations().add(
               new EventBasicGuidRelation(RelationEventType.Added, relation.getAArtifactId(),
                     relation.getBArtifactId(), defaultBasicGuidRelation));
      } else {
         OseeLog.log(Activator.class, Level.SEVERE, "Unhandled relation modified type " + relationEventType);
      }
   }
}