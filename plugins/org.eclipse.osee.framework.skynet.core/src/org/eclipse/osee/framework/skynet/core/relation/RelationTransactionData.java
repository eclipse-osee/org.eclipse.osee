/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.relation;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.DefaultBasicIdRelation;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicRelationTransfer;
import org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public class RelationTransactionData extends BaseTransactionData {
   private static final String INSERT_INTO_RELATION_TABLE =
      "INSERT INTO osee_relation_link (rel_link_id, rel_link_type_id, a_art_id, b_art_id, rationale, gamma_id) VALUES (?,?,?,?,?,?)";

   private static final String INSERT_INTO_RELATION_TABLE2 =
      "INSERT INTO osee_relation (rel_type, a_art_id, b_art_id, rel_order, rel_art_id, gamma_id) VALUES (?,?,?,?,?,?)";
   private final RelationLink relation;
   private final RelationEventType relationEventType;

   public RelationTransactionData(RelationLink relation, ModificationType modificationType, RelationEventType relationEventType) {
      super(Id.valueOf(relation.getId()), modificationType);
      this.relation = relation;
      this.relationEventType = relationEventType;
   }

   @Override
   public OseeSql getSelectTxNotCurrentSql() {
      if (this.relation.getRelationType().isNewRelationTable()) {
         return OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS2;
      } else {
         return OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS;
      }
   }

   @Override
   protected void addInsertToBatch(InsertDataCollector collector) {
      super.addInsertToBatch(collector);
      if (!relation.isUseBackingData()) {
         if (!relation.getRelationType().isNewRelationTable()) {
            internalAddInsertToBatch(collector, 4, INSERT_INTO_RELATION_TABLE, relation.getId(),
               relation.getRelationType(), relation.getArtifactIdA(), relation.getArtifactIdB(),
               relation.getRationale(), getGammaId());
         } else {
            internalAddInsertToBatch(collector, 4, INSERT_INTO_RELATION_TABLE2, relation.getRelationType(),
               relation.getArtifactIdA(), relation.getArtifactIdB(), relation.getRelOrder(), relation.getRelArtId(),
               getGammaId());
         }
      }
   }

   @Override
   protected void internalUpdate(TransactionRecord transactionId) {
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
   protected GammaId createGammaId() {
      GammaId newGammaId;
      if (relation.isUseBackingData()) {
         newGammaId = relation.getGammaId();
      } else {
         newGammaId = getNextGammaIdFromSequence();
      }
      return newGammaId;
   }

   @Override
   protected void internalAddToEvents(ArtifactEvent artifactEvent) {
      ArtifactToken artifactA = relation.getArtifactA();
      ArtifactToken artifactB = relation.getArtifactB();
      DefaultBasicGuidArtifact guidArtA = new DefaultBasicGuidArtifact(artifactA.getBranch(), artifactA);
      DefaultBasicGuidArtifact guidArtB = new DefaultBasicGuidArtifact(artifactB.getBranch(), artifactB);
      DefaultBasicIdRelation defaultBasicGuidRelation;

      if (!relation.getRelationType().isNewRelationTable()) {
         defaultBasicGuidRelation = new DefaultBasicIdRelation(relation.getBranch(), relation.getRelationType().getId(),
            relation.getId(), relation.getGammaId(), guidArtA, guidArtB);
      } else {
         defaultBasicGuidRelation = new DefaultBasicIdRelation(relation.getBranch(), relation.getRelationType().getId(),
            relation.getRelOrder(), relation.getGammaId(), guidArtA, guidArtB, relation.getRelArtId().getId());
      }
      EventBasicGuidRelation event =
         new EventBasicGuidRelation(relationEventType, artifactA, artifactB, defaultBasicGuidRelation);
      if (relationEventType == RelationEventType.ModifiedRationale) {
         event.setRationale(relation.getRationale());
      }
      artifactEvent.getRelations().add(event);
   }

   @Override
   protected void internalAddToEvents(ArtifactTopicEvent artifactTopicEvent) {
      String rationale =
         relationEventType == RelationEventType.ModifiedRationale ? relation.getRationale() : "RelationTransactionData transfer";

      EventTopicRelationTransfer event =
         FrameworkEventUtil.relationTransferFactory(relationEventType, relation.getArtifactA(), relation.getArtifactB(),
            RelationId.valueOf(relation.getId()), relation.getRelationType().getId(), relation.getGammaId(), rationale);
      artifactTopicEvent.getRelations().add(event);
   }

   @Override
   protected ApplicabilityId getApplicabilityId() {
      return relation.getApplicabilityId();
   }

   public RelationLink getRelation() {
      return relation;
   }

}