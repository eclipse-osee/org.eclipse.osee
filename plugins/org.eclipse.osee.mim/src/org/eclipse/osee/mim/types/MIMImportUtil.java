/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.mim.types;

import java.util.LinkedList;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.rest.model.transaction.AddRelation;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;

public class MIMImportUtil {

   public static TransactionBuilderData getTxBuilderDataFromImportSummary(BranchId branch, ApplicabilityId applicId,
      MimImportSummary summary) {
      TransactionBuilderData data = new TransactionBuilderData();
      data.setBranch(branch.getIdString());
      data.setTxComment(summary.getTxComment());
      data.setCreateArtifacts(new LinkedList<>());
      data.setAddRelations(new LinkedList<>());

      // Create Artifacts

      for (TransportType transportType : summary.getTransportTypes()) {
         data.getCreateArtifacts().add(transportType.createArtifact(transportType.getIdString(), applicId));
      }
      for (InterfaceConnection connection : summary.getConnections()) {
         data.getCreateArtifacts().add(connection.createArtifact(connection.getIdString(), applicId));
      }
      for (InterfaceNode node : summary.getNodes()) {
         data.getCreateArtifacts().add(node.createArtifact(node.getIdString(), applicId));
      }
      for (InterfaceMessageToken message : summary.getMessages()) {
         data.getCreateArtifacts().add(message.createArtifact(message.getIdString(), applicId));
      }
      for (InterfaceSubMessageToken submessage : summary.getSubMessages()) {
         data.getCreateArtifacts().add(submessage.createArtifact(submessage.getIdString(), applicId));
      }
      for (InterfaceStructureToken structure : summary.getStructures()) {
         data.getCreateArtifacts().add(structure.createArtifact(structure.getIdString(), applicId));
      }
      for (InterfaceStructureElementToken element : summary.getElements()) {
         data.getCreateArtifacts().add(element.createArtifact(element.getIdString(), applicId));
      }
      for (PlatformTypeToken pType : summary.getPlatformTypes()) {
         data.getCreateArtifacts().add(pType.createArtifact(pType.getIdString(), applicId));
      }
      for (InterfaceEnumerationSet enumSet : summary.getEnumSets()) {
         data.getCreateArtifacts().add(enumSet.createArtifact(enumSet.getIdString(), applicId));
      }
      for (InterfaceEnumeration enumeration : summary.getEnums()) {
         data.getCreateArtifacts().add(enumeration.createArtifact(enumeration.getIdString(), applicId));
      }
      for (InterfaceUnitToken unit : summary.getUnits()) {
         data.getCreateArtifacts().add(unit.createArtifact(unit.getIdString(), applicId));
      }
      for (ArtifactAccessorResultWithoutGammas messagePeriodicity : summary.getMessagePeriodicities()) {
         data.getCreateArtifacts().add(createNamedIdArtifact(CoreArtifactTypes.InterfaceMessagePeriodicity,
            messagePeriodicity.getName(), applicId));
      }
      for (ArtifactAccessorResultWithoutGammas messageRate : summary.getMessageRates()) {
         data.getCreateArtifacts().add(
            createNamedIdArtifact(CoreArtifactTypes.InterfaceRate, messageRate.getName(), applicId));
      }
      for (ArtifactAccessorResultWithoutGammas messageType : summary.getMessageTypes()) {
         data.getCreateArtifacts().add(
            createNamedIdArtifact(CoreArtifactTypes.InterfaceMessageTypeEnum, messageType.getName(), applicId));
      }
      for (ArtifactAccessorResultWithoutGammas structureCategory : summary.getStructureCategories()) {
         data.getCreateArtifacts().add(
            createNamedIdArtifact(CoreArtifactTypes.InterfaceStructureCategory, structureCategory.getName(), applicId));
      }

      // Create Relations

      for (String connectionId : summary.getConnectionTransportTypeRelations().keySet()) {
         for (String transportTypeId : summary.getConnectionTransportTypeRelations().get(connectionId)) {
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.InterfaceConnectionTransportType, connectionId, transportTypeId));
         }
      }

      for (String connectionId : summary.getConnectionNodeRelations().keySet()) {
         for (String nodeId : summary.getConnectionNodeRelations().get(connectionId)) {
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.InterfaceConnectionNode, connectionId, nodeId));
         }
      }

      for (String connectionId : summary.getConnectionMessageRelations().keySet()) {
         for (String messageId : summary.getConnectionMessageRelations().get(connectionId)) {
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.InterfaceConnectionMessage, connectionId, messageId));
         }
      }

      for (String messageId : summary.getMessagePublisherNodeRelations().keySet()) {
         for (String nodeId : summary.getMessagePublisherNodeRelations().get(messageId)) {
            data.getAddRelations().add(createAddRelation(CoreRelationTypes.InterfaceMessagePubNode, messageId, nodeId));
         }
      }

      for (String messageId : summary.getMessageSubscriberNodeRelations().keySet()) {
         for (String nodeId : summary.getMessageSubscriberNodeRelations().get(messageId)) {
            data.getAddRelations().add(createAddRelation(CoreRelationTypes.InterfaceMessageSubNode, messageId, nodeId));
         }
      }

      for (String messageId : summary.getMessageSubmessageRelations().keySet()) {
         for (String submessageId : summary.getMessageSubmessageRelations().get(messageId)) {
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.InterfaceMessageSubMessageContent, messageId, submessageId));
         }
      }

      for (String submessageId : summary.getSubMessageStructureRelations().keySet()) {
         for (String structureId : summary.getSubMessageStructureRelations().get(submessageId)) {
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.InterfaceSubMessageContent, submessageId, structureId));
         }
      }

      for (String structId : summary.getStructureElementRelations().keySet()) {
         for (String elementId : summary.getStructureElementRelations().get(structId)) {
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.InterfaceStructureContent, structId, elementId));
         }
      }

      for (String elementId : summary.getElementPlatformTypeRelations().keySet()) {
         for (String pTypeId : summary.getElementPlatformTypeRelations().get(elementId)) {
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.InterfaceElementPlatformType, elementId, pTypeId));
         }
      }
      for (String pTypeId : summary.getPlatformTypeEnumSetRelations().keySet()) {
         for (String enumSetId : summary.getPlatformTypeEnumSetRelations().get(pTypeId)) {
            data.getAddRelations().add(
               createAddRelation(CoreRelationTypes.InterfacePlatformTypeEnumeration, pTypeId, enumSetId));
         }
      }
      for (String enumSetId : summary.getEnumSetEnumRelations().keySet()) {
         for (String enumId : summary.getEnumSetEnumRelations().get(enumSetId)) {
            data.getAddRelations().add(createAddRelation(CoreRelationTypes.InterfaceEnumeration, enumSetId, enumId));
         }
      }

      return data;
   }

   private static AddRelation createAddRelation(RelationTypeToken relType, String artAId, String artBId) {
      AddRelation rel = new AddRelation();
      rel.setTypeId(relType.getIdString());
      rel.setaArtId(artAId);
      rel.setbArtId(artBId);
      return rel;
   }

   private static CreateArtifact createNamedIdArtifact(ArtifactTypeToken artType, String name,
      ApplicabilityId applicId) {
      CreateArtifact art = new CreateArtifact();
      art.setName(name);
      art.setTypeId(artType.getIdString());
      art.setApplicabilityId(applicId.getIdString());
      return art;
   }
}
