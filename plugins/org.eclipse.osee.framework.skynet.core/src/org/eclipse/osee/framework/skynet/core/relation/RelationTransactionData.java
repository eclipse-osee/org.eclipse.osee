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

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelation;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public class RelationTransactionData extends BaseTransactionData {
   private static final String INSERT_INTO_RELATION_TABLE =
      "INSERT INTO osee_relation_link (rel_link_id, rel_link_type_id, a_art_id, b_art_id, rationale, gamma_id) VALUES (?,?,?,?,?,?)";

   private final RelationLink relation;
   private final RelationEventType relationEventType;

   public RelationTransactionData(RelationLink relation, ModificationType modificationType, RelationEventType relationEventType) {
      super(Id.valueOf(relation.getId()), modificationType);
      this.relation = relation;
      this.relationEventType = relationEventType;
   }

   @Override
   public OseeSql getSelectTxNotCurrentSql() {
      return OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS;
   }

   @Override
   protected void addInsertToBatch(InsertDataCollector collector) throws OseeCoreException {
      super.addInsertToBatch(collector);
      if (!relation.isUseBackingData()) {
         internalAddInsertToBatch(collector, 4, INSERT_INTO_RELATION_TABLE, relation.getId(),
            relation.getRelationType(), relation.getArtifactA(), relation.getArtifactB(), relation.getRationale(),
            getGammaId());
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
   protected void internalOnRollBack() {
      // do nothing
   }

   @Override
   protected int createGammaId() throws OseeCoreException {
      int newGammaId = 0;
      if (relation.isUseBackingData()) {
         newGammaId = relation.getGammaId();
      } else {
         newGammaId = getNextGammaIdFromSequence();
      }
      return newGammaId;
   }

   @Override
   protected void internalAddToEvents(ArtifactEvent artifactEvent) throws OseeCoreException {
      DefaultBasicUuidRelation defaultBasicGuidRelation = new DefaultBasicUuidRelation(relation.getBranch(),
         relation.getRelationType().getId(), relation.getId(), relation.getGammaId(),
         relation.getArtifactA().getBasicGuidArtifact(), relation.getArtifactB().getBasicGuidArtifact());
      EventBasicGuidRelation event = new EventBasicGuidRelation(relationEventType, relation.getArtifactA(),
         relation.getArtifactB(), defaultBasicGuidRelation);
      if (relationEventType == RelationEventType.ModifiedRationale) {
         event.setRationale(relation.getRationale());
      }
      artifactEvent.getRelations().add(event);
   }

   @Override
   protected ApplicabilityId getApplicabilityId() throws OseeCoreException {
      return relation.getApplicabilityId();
   }

}