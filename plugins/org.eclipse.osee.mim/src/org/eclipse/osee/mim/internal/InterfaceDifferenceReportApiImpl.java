/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.mim.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.model.dto.ChangeReportRowDto;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceDifferenceReportApi;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceEnumerationApi;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.MimChangeSummary;
import org.eclipse.osee.mim.types.MimChangeSummaryItem;
import org.eclipse.osee.mim.types.MimDifferenceReport;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Ryan T. Baldwin
 */
public class InterfaceDifferenceReportApiImpl implements InterfaceDifferenceReportApi {

   private BranchId branch1;
   private BranchId branch2;

   private final OrcsApi orcsApi;
   private final InterfaceNodeViewApi interfaceNodeApi;
   private final InterfaceConnectionViewApi interfaceConnectionApi;
   private final InterfaceMessageApi interfaceMessageApi;
   private final InterfaceSubMessageApi interfaceSubMessageApi;
   private final InterfaceStructureApi interfaceStructureApi;
   private final InterfaceElementApi interfaceElementApi;
   private final InterfacePlatformTypeApi interfacePlatformApi;
   private final InterfaceEnumerationApi interfaceEnumerationApi;

   private Map<ArtifactId, List<ChangeItem>> changeMap;

   private List<ArtifactId> nodeList;
   private List<ArtifactId> connectionList;
   private List<ArtifactId> messageList;
   private List<ArtifactId> submessageList;
   private List<ArtifactId> structureList;
   private List<ArtifactId> elementList;
   private List<ArtifactId> pTypeList;
   private List<ArtifactId> enumList;

   private MimDifferenceReport diffReport;

   InterfaceDifferenceReportApiImpl(OrcsApi orcsApi, InterfaceNodeViewApi interfaceNodeApi, InterfaceConnectionViewApi interfaceConnectionViewApi, InterfaceMessageApi interfaceMessageApi, InterfaceSubMessageApi interfaceSubMessageApi, InterfaceStructureApi interfaceStructureApi, InterfaceElementApi interfaceElementApi, InterfacePlatformTypeApi interfacePlatformApi, InterfaceEnumerationApi interfaceEnumerationApi) {
      this.orcsApi = orcsApi;
      this.interfaceNodeApi = interfaceNodeApi;
      this.interfaceConnectionApi = interfaceConnectionViewApi;
      this.interfaceMessageApi = interfaceMessageApi;
      this.interfaceSubMessageApi = interfaceSubMessageApi;
      this.interfaceStructureApi = interfaceStructureApi;
      this.interfaceElementApi = interfaceElementApi;
      this.interfacePlatformApi = interfacePlatformApi;
      this.interfaceEnumerationApi = interfaceEnumerationApi;
   }

   @Override
   public MimDifferenceReport getDifferenceReport(BranchId branch1, BranchId branch2) {
      this.branch1 = branch1;
      this.branch2 = branch2;

      // Initialize
      this.changeMap = new HashMap<>();
      this.diffReport = new MimDifferenceReport();

      this.nodeList = new LinkedList<>();
      this.connectionList = new LinkedList<>();
      this.messageList = new LinkedList<>();
      this.submessageList = new LinkedList<>();
      this.structureList = new LinkedList<>();
      this.elementList = new LinkedList<>();
      this.pTypeList = new LinkedList<>();
      this.enumList = new LinkedList<>();

      TransactionToken sourceTx =
         orcsApi.getQueryFactory().transactionQuery().andIsHead(branch1).getResults().getExactlyOne();
      TransactionToken destinationTx =
         orcsApi.getQueryFactory().transactionQuery().andIsHead(branch2).getResults().getExactlyOne();
      List<ChangeItem> changes = orcsApi.getBranchOps().compareBranch(sourceTx, destinationTx);

      // First, add all of the artifact changes to the correct maps. Every diff should have an artifact change.
      changes.stream().filter(
         c -> c.getChangeType().getId() == ChangeType.Artifact.getId() || c.getChangeType().getId() == ChangeType.Relation.getId()).forEach(
            c -> addChangeToMap(c));

      // Add all change items to the changeMap. These are the lists that will be put into the diff report.
      for (ChangeItem change : changes) {
         List<ChangeItem> list = getChangeList(change.getArtId());
         list.add(change);
         changeMap.put(change.getArtId(), list);
      }

      processNodes(nodeList);
      processConnections(connectionList);
      processMessages(messageList);
      processSubMessages(submessageList);
      //elementList.addAll(processStructures(structureList)); //TODO: add moved elements so they show up on diff report
      processStructures(structureList);

      processElements(elementList, ArtifactId.SENTINEL);
      processPlatformTypes(pTypeList);
      processEnumerations(enumList);

      return diffReport;
   }

   private void processNodes(List<ArtifactId> nodeIds) {
      List<InterfaceNode> nodes =
         (List<InterfaceNode>) interfaceNodeApi.get(branch1, getArtIdsForBranch(nodeIds, branch1));
      nodes.addAll(interfaceNodeApi.get(branch2, getArtIdsForBranch(nodeIds, branch2)));
      for (InterfaceNode node : nodes) {
         List<ChangeItem> changeItems = changeMap.get(node.getArtifactId());
         if (node.isValid()) {
            diffReport.addItem(node, changeItems);
            diffReport.getNodes().add(node.getArtifactId());
         }
      }
   }

   private void processConnections(List<ArtifactId> connectionIds) {
      List<InterfaceConnection> connections =
         (List<InterfaceConnection>) interfaceConnectionApi.get(branch1, getArtIdsForBranch(connectionIds, branch1));
      connections.addAll(interfaceConnectionApi.get(branch2, getArtIdsForBranch(connectionIds, branch2)));
      for (InterfaceConnection connection : connections) {
         if (connection.isValid()) {
            diffReport.addItem(connection, getChangeList(connection.getArtifactId()));
            diffReport.getConnections().add(connection.getArtifactId());
         }
      }
   }

   private void processMessages(List<ArtifactId> messageIds) {
      List<FollowRelation> parentRelations =
         FollowRelation.followList(CoreRelationTypes.InterfaceConnectionMessage_Connection,
            CoreRelationTypes.InterfaceConnectionTransportType_TransportType);
      List<InterfaceMessageToken> messages = (List<InterfaceMessageToken>) interfaceMessageApi.get(branch1,
         getArtIdsForBranch(messageIds, branch1), parentRelations);
      messages.addAll(interfaceMessageApi.get(branch2, getArtIdsForBranch(messageIds, branch2), parentRelations));
      for (InterfaceMessageToken message : messages) {
         if (message.isValid()) {
            diffReport.addItem(message, getChangeList(message.getArtifactId()));
            addMessageParent(message);
            diffReport.getMessages().add(message.getArtifactId());
         }
      }
   }

   private void processSubMessages(List<ArtifactId> submessageIds) {
      List<FollowRelation> parentRelations =
         FollowRelation.followList(CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
            CoreRelationTypes.InterfaceConnectionMessage_Connection,
            CoreRelationTypes.InterfaceConnectionTransportType_TransportType);
      List<InterfaceSubMessageToken> subMessages = (List<InterfaceSubMessageToken>) interfaceSubMessageApi.get(branch1,
         getArtIdsForBranch(submessageIds, branch1), parentRelations);
      subMessages.addAll(
         interfaceSubMessageApi.get(branch2, getArtIdsForBranch(submessageIds, branch2), parentRelations));
      for (InterfaceSubMessageToken subMessage : subMessages) {
         if (subMessage.isValid()) {
            diffReport.addItem(subMessage, getChangeList(subMessage.getArtifactId()));
            addSubMessageParents(subMessage);
            diffReport.getSubMessages().add(subMessage.getArtifactId());
         }
      }
   }

   private LinkedList<ArtifactId> processStructures(List<ArtifactId> structureIds) {
      LinkedList<ArtifactId> elementsMoved = new LinkedList<>();
      List<FollowRelation> parentRelations =
         FollowRelation.followList(CoreRelationTypes.InterfaceSubMessageContent_SubMessage,
            CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
            CoreRelationTypes.InterfaceConnectionMessage_Connection,
            CoreRelationTypes.InterfaceConnectionTransportType_TransportType);
      List<InterfaceStructureToken> structures = (List<InterfaceStructureToken>) interfaceStructureApi.get(branch1,
         getArtIdsForBranch(structureIds, branch1), parentRelations);
      structures.addAll(interfaceStructureApi.get(branch2, getArtIdsForBranch(structureIds, branch2), parentRelations));
      for (InterfaceStructureToken structure : structures) {
         if (structure.isValid()) {
            diffReport.addItem(structure, getChangeList(structure.getArtifactId()));
            addStructureParents(structure);
            diffReport.getStructures().add(structure.getArtifactId());
            for (ChangeItem changeItem : getChangeList(structure.getArtifactId())) {
               if (changeItem.getChangeType().isRelationChange()) {
                  elementsMoved.add(changeItem.getArtIdB());
               }
            }
         }
      }
      return elementsMoved;
   }

   private void processElements(List<ArtifactId> elementIds, ArtifactId typeId) {
      List<FollowRelation> parentRelations = new LinkedList<>();
      parentRelations.add(FollowRelation.fork(CoreRelationTypes.InterfaceElementPlatformType_PlatformType));
      parentRelations.addAll(FollowRelation.followList(CoreRelationTypes.InterfaceStructureContent_Structure,
         CoreRelationTypes.InterfaceSubMessageContent_SubMessage,
         CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
         CoreRelationTypes.InterfaceConnectionMessage_Connection,
         CoreRelationTypes.InterfaceConnectionTransportType_TransportType));
      List<InterfaceStructureElementToken> elements =
         (List<InterfaceStructureElementToken>) interfaceElementApi.get(branch1,
            getArtIdsForBranch(elementIds, branch1), parentRelations);
      elements.addAll(interfaceElementApi.get(branch2, getArtIdsForBranch(elementIds, branch2), parentRelations));
      for (InterfaceStructureElementToken element : elements) {
         //         pTypeList.add(element.getPlatformType().getArtifactId()); // Add platform type id to list to process later
         addElementParents(element);
         processElement(element.getArtifactId(), typeId, element);
      }
   }

   private void processElement(ArtifactId elementId, ArtifactId typeId, InterfaceStructureElementToken element) {
      if (element.isValid()) {
         diffReport.addItem(element, getChangeList(elementId));

         if (typeId.isValid()) {
            diffReport.addItem(element, getChangeList(typeId));
         }

         if (!diffReport.hasParents(elementId)) {
            addElementParents(element);
         }

         if (!diffReport.getElements().contains(elementId)) {
            diffReport.getElements().add(elementId);
         }
      }
   }

   private void processPlatformTypes(List<ArtifactId> pTypeIds) {
      List<FollowRelation> relations = new LinkedList<>();
      relations.add(FollowRelation.follow(CoreRelationTypes.InterfaceElementPlatformType_Element));
      relations.add(FollowRelation.fork(CoreRelationTypes.InterfaceElementPlatformType_PlatformType));
      relations.addAll(FollowRelation.followList(CoreRelationTypes.InterfaceStructureContent_Structure,
         CoreRelationTypes.InterfaceSubMessageContent_SubMessage,
         CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
         CoreRelationTypes.InterfaceConnectionMessage_Connection,
         CoreRelationTypes.InterfaceConnectionTransportType_TransportType));
      List<PlatformTypeToken> pTypes =
         interfacePlatformApi.get(branch1, getArtIdsForBranch(pTypeIds, branch1), relations);
      pTypes.addAll(interfacePlatformApi.get(branch2, getArtIdsForBranch(pTypeIds, branch2), relations));
      for (PlatformTypeToken pType : pTypes) {
         if (pType.isValid()) {
            diffReport.addItem(pType, getChangeList(pType.getArtifactId()));

            // Process Elements
            @SuppressWarnings("unchecked")
            List<InterfaceStructureElementToken> elements =
               (List<InterfaceStructureElementToken>) getFromArtifactReadable(pType.getArtifactReadable(),
                  CoreRelationTypes.InterfaceElementPlatformType_Element, InterfaceStructureElementToken.class);
            for (InterfaceStructureElementToken element : elements) {
               processElement(ArtifactId.valueOf(element.getId()), pType.getArtifactId(), element);
            }
         }
      }
   }

   private void processEnumerations(List<ArtifactId> enumIds) {
      List<FollowRelation> relations = new LinkedList<>();
      relations.add(FollowRelation.follow(CoreRelationTypes.InterfaceEnumeration_EnumerationSet));
      relations.add(FollowRelation.fork(CoreRelationTypes.InterfaceEnumeration_EnumerationState));
      relations.add(FollowRelation.follow(CoreRelationTypes.InterfacePlatformTypeEnumeration_Element));
      relations.add(FollowRelation.follow(CoreRelationTypes.InterfaceElementPlatformType_Element));
      relations.add(FollowRelation.fork(CoreRelationTypes.InterfaceElementPlatformType_PlatformType));
      relations.addAll(FollowRelation.followList(CoreRelationTypes.InterfaceStructureContent_Structure,
         CoreRelationTypes.InterfaceSubMessageContent_SubMessage,
         CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
         CoreRelationTypes.InterfaceConnectionMessage_Connection,
         CoreRelationTypes.InterfaceConnectionTransportType_TransportType));
      List<InterfaceEnumeration> enumerations =
         interfaceEnumerationApi.get(branch1, getArtIdsForBranch(enumIds, branch1), relations);
      enumerations.addAll(interfaceEnumerationApi.get(branch2, getArtIdsForBranch(enumIds, branch2), relations));

      for (InterfaceEnumeration enumeration : enumerations) {
         @SuppressWarnings("unchecked")
         List<InterfaceEnumerationSet> enumSets =
            (List<InterfaceEnumerationSet>) getFromArtifactReadable(enumeration.getArtifactReadable(),
               CoreRelationTypes.InterfaceEnumeration_EnumerationSet, InterfaceEnumerationSet.class);

         for (InterfaceEnumerationSet enumSet : enumSets) {
            diffReport.addItem(enumSet, getChangeList(enumeration.getArtifactId()));
            diffReport.getEnumSets().add(enumSet.getArtifactId());
            addEnumSetParents(enumSet); // Use the non-populated enum set that has the parent relations here
         }
      }
   }

   private void addMessageParent(InterfaceMessageToken message) {
      ArtifactId messageId = message.getArtifactId();
      if (!diffReport.hasParents(messageId)) {
         List<?> connections = getFromArtifactReadable(message.getArtifactReadable(),
            CoreRelationTypes.InterfaceConnectionMessage_Connection, InterfaceConnection.class);
         if (connections.size() > 0) {
            InterfaceConnection connection = (InterfaceConnection) connections.get(0);
            diffReport.addParent(messageId, connection.getArtifactId());
            diffReport.addItem(connection);
         }
      }
   }

   private void addSubMessageParents(InterfaceSubMessageToken subMessage) {
      if (!diffReport.hasParents(subMessage.getArtifactId())) {
         @SuppressWarnings("unchecked")
         List<InterfaceMessageToken> messages =
            (List<InterfaceMessageToken>) getFromArtifactReadable(subMessage.getArtifactReadable(),
               CoreRelationTypes.InterfaceMessageSubMessageContent_Message, InterfaceMessageToken.class);
         for (InterfaceMessageToken message : messages) {
            diffReport.addParent(subMessage.getArtifactId(), message.getArtifactId());
            diffReport.addItem(message);
            addMessageParent(message);
         }
      }
   }

   private void addStructureParents(InterfaceStructureToken structure) {
      if (!diffReport.hasParents(structure.getArtifactId())) {
         @SuppressWarnings("unchecked")
         List<InterfaceSubMessageToken> subMessages =
            (List<InterfaceSubMessageToken>) getFromArtifactReadable(structure.getArtifactReadable(),
               CoreRelationTypes.InterfaceSubMessageContent_SubMessage, InterfaceSubMessageToken.class);
         for (InterfaceSubMessageToken subMessage : subMessages) {
            diffReport.addParent(structure.getArtifactId(), subMessage.getArtifactId());
            diffReport.addItem(subMessage);
            addSubMessageParents(subMessage);
         }
      }
   }

   private void addElementParents(InterfaceStructureElementToken element) {
      if (!diffReport.hasParents(element.getArtifactId())) {
         @SuppressWarnings("unchecked")
         List<InterfaceStructureToken> structures =
            (List<InterfaceStructureToken>) getFromArtifactReadable(element.getArtifactReadable(),
               CoreRelationTypes.InterfaceStructureContent_Structure, InterfaceStructureToken.class);
         for (InterfaceStructureToken structure : structures) {
            diffReport.addParent(element.getArtifactId(), structure.getArtifactId());
            diffReport.addItem(structure);
            addStructureParents(structure);
         }
      }
   }

   private void addEnumSetParents(InterfaceEnumerationSet enumSet) {
      if (!diffReport.hasParents(enumSet.getArtifactId())) {
         @SuppressWarnings("unchecked")
         List<PlatformTypeToken> pTypes =
            (List<PlatformTypeToken>) getFromArtifactReadable(enumSet.getArtifactReadable(),
               CoreRelationTypes.InterfacePlatformTypeEnumeration_Element, PlatformTypeToken.class);
         for (PlatformTypeToken pType : pTypes) {
            diffReport.addParent(enumSet.getArtifactId(), pType.getArtifactId());
            @SuppressWarnings("unchecked")
            List<InterfaceStructureElementToken> elements =
               (List<InterfaceStructureElementToken>) getFromArtifactReadable(pType.getArtifactReadable(),
                  CoreRelationTypes.InterfaceElementPlatformType_Element, InterfaceStructureElementToken.class);
            for (InterfaceStructureElementToken element : elements) {
               processElement(element.getArtifactId(), ArtifactId.SENTINEL, element);
            }
         }
      }
   }

   private List<?> getFromArtifactReadable(ArtifactReadable art, RelationTypeSide relation, Class<?> clazz) {
      if (art == null || art.isInvalid()) {
         return new LinkedList<>();
      }
      return art.getRelatedList(relation).stream().map(a -> {
         try {
            return clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(a);
         } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
            | NoSuchMethodException | SecurityException ex) {
            return new LinkedList<>();
         }
      }).collect(Collectors.toList());
   }

   private BranchId getBranchId(ArtifactId artId) {
      List<ChangeItem> changes = changeMap.get(artId);
      if (changes == null || changes.isEmpty()) {
         return branch1;
      }
      ChangeItem change =
         changes.stream().filter(c -> c.getChangeType().getId() == ChangeType.Artifact.getId()).findFirst().orElse(
            null);
      // If no artifact change is found, look for a relation change
      change = change == null ? changes.stream().filter(
         c -> c.getChangeType().getId() == ChangeType.Relation.getId()).findFirst().orElse(null) : change;
      return change != null && change.isDeleted() ? branch2 : branch1;
   }

   private List<ChangeItem> getChangeList(ArtifactId artId) {
      return changeMap.getOrDefault(artId, new LinkedList<>());
   }

   private List<ArtifactId> getArtIdsForBranch(List<ArtifactId> artIds, BranchId branch) {
      return artIds.stream().filter(id -> getBranchId(id).equals(branch)).collect(Collectors.toList());
   }

   private void addChangeToMap(ChangeItem changeItem) {
      ArtifactId artId = changeItem.getArtId();
      long itemTypeId = changeItem.getItemTypeId().getId();
      if (itemTypeId == CoreArtifactTypes.InterfaceNode.getId() || itemTypeId == CoreRelationTypes.InterfaceConnectionNode.getId()) {
         if (!nodeList.contains(artId)) {
            nodeList.add(artId);
         }
      } else if (itemTypeId == CoreArtifactTypes.InterfaceConnection.getId()) {
         if (!connectionList.contains(artId)) {
            connectionList.add(artId);
         }
      } else if (itemTypeId == CoreArtifactTypes.InterfaceMessage.getId()) {
         if (!messageList.contains(artId)) {
            messageList.add(artId);
         }
      } else if (itemTypeId == CoreArtifactTypes.InterfaceSubMessage.getId()) {
         if (!submessageList.contains(artId)) {
            submessageList.add(artId);
         }
      } else if (itemTypeId == CoreArtifactTypes.InterfaceStructure.getId() || itemTypeId == CoreRelationTypes.InterfaceStructureContent.getId()) {
         if (!structureList.contains(artId)) {
            structureList.add(artId);
         }
      } else if (itemTypeId == CoreArtifactTypes.InterfaceDataElementArray.getId() || itemTypeId == CoreArtifactTypes.InterfaceDataElement.getId() || itemTypeId == CoreRelationTypes.InterfaceElementPlatformType.getId()) {
         if (!elementList.contains(artId)) {
            elementList.add(artId);
         }
      } else if (itemTypeId == CoreArtifactTypes.InterfacePlatformType.getId()) {
         if (!pTypeList.contains(artId)) {
            pTypeList.add(artId);
         }
      } else if (itemTypeId == CoreArtifactTypes.InterfaceEnum.getId()) {
         if (!enumList.contains(artId)) {
            enumList.add(artId);
         }
      }
   }

   @Override
   public Map<ArtifactId, MimChangeSummaryItem> getChangeSummaryItems(BranchId branch1, BranchId branch2,
      ArtifactId view) {
      TransactionToken sourceTx =
         orcsApi.getQueryFactory().transactionQuery().andIsHead(branch1).getResults().getExactlyOne();
      TransactionToken destinationTx =
         orcsApi.getQueryFactory().transactionQuery().andIsHead(branch2).getResults().getExactlyOne();
      List<ChangeReportRowDto> changes =
         orcsApi.getTransactionFactory().getTxChangeReport(branch1, branch2, sourceTx, destinationTx);

      List<ApplicabilityToken> applicTokens;
      if (view.isValid()) {
         applicTokens = orcsApi.getQueryFactory().applicabilityQuery().getViewApplicabilityTokens(view, branch1);
      } else {
         applicTokens =
            orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch1).values().stream().collect(
               Collectors.toList());
      }

      Map<ArtifactId, MimChangeSummaryItem> changeMap = new HashMap<>(); // Collection of all change items
      for (ChangeReportRowDto change : changes) {
    	  if (!change.getArtType().getSuperTypes().contains(CoreArtifactTypes.InterfaceArtifact)) {
    		  continue;
    	  }
         ArtifactId artId = change.getArtA().getArtifactId();

         MimChangeSummaryItem item = changeMap.get(artId);
         if (item == null) {
            item = new MimChangeSummaryItem(change.getArtA());
            changeMap.put(artId, item);
         }

         if (change.getItemKindType().equals(ChangeType.Attribute)) {
            item.getAttributeChanges().add(change);
         } else if (change.getItemKindType().equals(ChangeType.Relation)) {
        	item.getRelationChanges().add(change);
         } else if (change.getItemKindType().equals(ChangeType.Artifact)) {
            if (change.getChangeType().equals("Applicability")) {
               item.setIsApplic(change.getIsApplic());
               item.setWasApplic(change.getWasApplic());
               boolean isApplic = applicTokens.contains(change.getIsApplic());
               boolean wasApplic = applicTokens.contains(change.getWasApplic());
               if (isApplic && !wasApplic) {
                  item.setAdded(true);
                  item.setAddedDueToApplicChange(true);
               } else if (!isApplic && wasApplic) {
                  item.setDeleted(true);
                  item.setDeletedDueToApplicChange(true);
               }
            } else if (change.getModType().equals(ModificationType.NEW) || change.getModType().equals(
               ModificationType.INTRODUCED)) {
               item.setAdded(true);
            } else if (change.getModType().equals(ModificationType.DELETED)) {
               item.setDeleted(true);
            }
         }
      }

      return changeMap;
   }

   @Override
   public MimChangeSummary getChangeSummary(BranchId branch1, BranchId branch2, ArtifactId view) {
      Map<ArtifactId, MimChangeSummaryItem> changes = getChangeSummaryItems(branch1, branch2, view);
      return getChangeSummary(branch1, branch2, view, changes);
   }

   @Override
   public MimChangeSummary getChangeSummary(BranchId branch1, BranchId branch2, ArtifactId view,
      Map<ArtifactId, MimChangeSummaryItem> changes) {
      return getChangeSummary(branch1, branch2, view, changes, new LinkedList<>());
   }

   @Override
   public MimChangeSummary getChangeSummary(BranchId branch1, BranchId branch2, ArtifactId view,
      Map<ArtifactId, MimChangeSummaryItem> changes, List<ApplicabilityToken> applicTokenList) {
      List<ApplicabilityToken> applicTokens = applicTokenList;
      if (applicTokenList.isEmpty()) {
         if (view.isValid()) {
            applicTokens = orcsApi.getQueryFactory().applicabilityQuery().getViewApplicabilityTokens(view, branch1);
         } else {
            applicTokens =
               orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch1).values().stream().collect(
                  Collectors.toList());
         }
      }

      List<ArtifactTypeToken> elementTypes = Arrays.asList(CoreArtifactTypes.InterfaceDataElement,
         CoreArtifactTypes.InterfaceDataElementArray, CoreArtifactTypes.InterfacePlatformType,
         CoreArtifactTypes.InterfaceEnumSet, CoreArtifactTypes.InterfaceEnum);

      Map<ArtifactId, MimChangeSummaryItem> changeMap =
         changes.isEmpty() ? getChangeSummaryItems(branch1, branch2, view) : changes;

      MimChangeSummary summary = new MimChangeSummary(changeMap);
      Map<ArtifactId, List<ArtifactReadable>> elementMap = new HashMap<>(); // Maps platformTypes, enum sets, enums to a list of affected elements
      Map<ArtifactId, MimChangeSummaryItem> elementsAndTypes = new HashMap<>(); // Collection of platform type, enum set, and enum changes.

      // Once all changes are read, process the summary items to add extra context or remove them if they are not applicable
      List<MimChangeSummaryItem> items = new LinkedList<>();
      items.addAll(changeMap.values());
      for (MimChangeSummaryItem item : items) {
         // For Messages, SubMessages, Structures, create summary items and add them as children
         if (!elementTypes.contains(item.getArtType())) {
            for (ChangeReportRowDto change : item.getRelationChanges()) {
               if (item.getChildren().stream().filter(
                  c -> c.getArtifactReadable().equals(change.getArtB())).findFirst().orElse(null) == null) {
                  MimChangeSummaryItem child = changeMap.get(ArtifactId.valueOf(change.getArtB().getId()));
                  if (child == null) {
                     child = new MimChangeSummaryItem(change.getArtB());
                  }
                  if (change.getModType().equals(ModificationType.NEW)) {
                     child.setAdded(true);
                  } else if (change.getModType().equals(ModificationType.DELETED)) {
                     child.setDeleted(true);
                  }
                  item.getChildren().add(child);
               }
            }
         }

         // If the item is not applicable and was not added or deleted due to an applic change, do not show it in the report
         if (!applicTokens.contains(
            item.getArtifactReadable().getApplicabilityToken()) && !item.isAddedDueToApplicChange() && !item.isDeletedDueToApplicChange()) {
            changeMap.remove(item.getArtId());
            continue;
         }

         // If there are no real changes, do not show in report
         if (!item.isAdded() && !item.isDeleted() && !item.isAddedDueToApplicChange() && !item.isDeletedDueToApplicChange() && item.getAttributeChanges().isEmpty() && item.getRelationChanges().isEmpty() && item.getChildren().isEmpty()) {
            changeMap.remove(item.getArtId());
            continue;
         }

         ArtifactId artId = item.getArtId();
         ArtifactTypeToken artType = item.getArtType();

         // For changes to artifact types below elements in the hierarchy, add those changes to their parent elements.
         if (elementTypes.contains(artType)) {
            List<ArtifactReadable> currentElements = new LinkedList<>();
            if (elementMap.containsKey(artId)) {
               currentElements = elementMap.get(artId);
            } else {
               BranchId branchToUse = item.isDeleted() && !item.isDeletedDueToApplicChange() ? branch2 : branch1;
               currentElements = getElements(artId, artType, branchToUse, elementMap);
               elementMap.put(artId, currentElements);
            }
            for (ArtifactReadable element : currentElements) {
               if (!(CoreArtifactTypes.InterfaceDataElement.equals(
                  artType) || CoreArtifactTypes.InterfaceDataElement.equals(artType)) && !applicTokens.contains(
                     element.getApplicabilityToken())) {
                  continue;
               }
               MimChangeSummaryItem elementItem = changeMap.get(element.getArtifactId());
               if (elementItem == null) {
                  if (CoreArtifactTypes.InterfaceDataElement.equals(
                     artType) || CoreArtifactTypes.InterfaceDataElement.equals(artType)) {
                     elementItem = item;
                  } else {
                     elementItem = new MimChangeSummaryItem(element);
                     changeMap.put(element.getArtifactId(), elementItem);
                  }
               }

               // Add elements as children to the affected structures, creating the structure items if they don't exist.
               for (ArtifactReadable structure : element.getRelated(
                  CoreRelationTypes.InterfaceStructureContent_Structure).getList()) {
                  if (!applicTokens.contains(structure.getApplicabilityToken())) {
                     continue;
                  }
                  MimChangeSummaryItem structureItem = changeMap.get(structure.getArtifactId());
                  if (structureItem == null) {
                     structureItem = new MimChangeSummaryItem(structure);
                     changeMap.put(structure.getArtifactId(), structureItem);
                  }
                  if (!structureItem.getChildren().contains(elementItem)) {
                     structureItem.getChildren().add(elementItem);
                  }
               }
            }

            elementsAndTypes.put(artId, item);
         }
      }

      // Go back through platform types, enum sets, and enums and process the changes now they they have all been read
      for (MimChangeSummaryItem item : elementsAndTypes.values()) {
         ArtifactTypeToken artType = item.getArtType();

         List<MimChangeSummaryItem> elementItems =
            elementMap.get(item.getArtId()).stream().map(a -> changeMap.get(ArtifactId.valueOf(a.getId()))).filter(
               i -> i != null).collect(Collectors.toList());

         // For elements, check relations for platform type changes. If a platform type was replaced, convert those relations to attribute changes
         if (CoreArtifactTypes.InterfaceDataElement.equals(
            artType) || CoreArtifactTypes.InterfaceDataElementArray.equals(artType)) {
            ChangeReportRowDto deletedPType = null;
            ChangeReportRowDto addedPType = null;
            for (ChangeReportRowDto relationChange : item.getRelationChanges()) {
               if (relationChange.getModType().equals(
                  ModificationType.DELETED) && CoreArtifactTypes.InterfacePlatformType.equals(
                     relationChange.getArtB().getArtifactType())) {
                  deletedPType = relationChange;
               } else if (relationChange.getModType().equals(
                  ModificationType.NEW) && CoreArtifactTypes.InterfacePlatformType.equals(
                     relationChange.getArtB().getArtifactType())) {
                  addedPType = relationChange;
               }
               if (deletedPType != null && addedPType != null) {
                  ArtifactReadable oldPType = deletedPType.getArtB();
                  ArtifactReadable newPType = addedPType.getArtB();
                  Arrays.asList(CoreAttributeTypes.InterfaceLogicalType,
                     CoreAttributeTypes.InterfacePlatformTypeBitSize,
                     CoreAttributeTypes.InterfacePlatformType2sComplement,
                     CoreAttributeTypes.InterfacePlatformTypeMinval, CoreAttributeTypes.InterfacePlatformTypeMaxval,
                     CoreAttributeTypes.InterfacePlatformTypeUnits, CoreAttributeTypes.InterfaceDefaultValue,
                     CoreAttributeTypes.InterfacePlatformTypeMsbValue,
                     CoreAttributeTypes.InterfacePlatformTypeBitsResolution,
                     CoreAttributeTypes.InterfacePlatformTypeCompRate,
                     CoreAttributeTypes.InterfacePlatformTypeAnalogAccuracy,
                     CoreAttributeTypes.InterfacePlatformTypeValidRangeDescription).stream().forEach(attr -> {
                        String wasValue = oldPType.getSoleAttributeAsString(attr, "");
                        String isValue = newPType.getSoleAttributeAsString(attr, "");
                        if (!wasValue.equals(isValue)) {
                           ChangeReportRowDto attrChange = new ChangeReportRowDto(relationChange.getArtA(),
                              ArtifactReadable.SENTINEL, item.getName(), attr.getName(), attr.getId(), "Modified",
                              isValue, wasValue, ChangeType.Attribute, ModificationType.MODIFIED,
                              relationChange.getIsApplic(), relationChange.getWasApplic());
                           item.getAttributeChanges().add(attrChange);
                        }
                     });
               }
            }
         }

         // For platform types, attribute changes should be added to the element's attribute change list
         // Do not add platform type name changes since the name is not important to the ICD
         if (CoreArtifactTypes.InterfacePlatformType.equals(artType)) {
            List<ChangeReportRowDto> attrChanges = item.getAttributeChanges().stream().filter(
               a -> !CoreAttributeTypes.Name.getName().equals(a.getItemType())).collect(Collectors.toList());
            elementItems.stream().forEach(e -> e.getAttributeChanges().addAll(attrChanges));
         }

         // For enum sets, only add name changes because other changes are not important for the ICD
         else if (CoreArtifactTypes.InterfaceEnumSet.equals(artType)) {
            List<ChangeReportRowDto> attrChanges = item.getAttributeChanges().stream().filter(
               a -> CoreAttributeTypes.Name.getName().equals(a.getItemType())).collect(Collectors.toList());
            attrChanges.forEach(a -> a.setItemType("Enum Set Name"));
            elementItems.stream().forEach(e -> e.getAttributeChanges().addAll(attrChanges));
         }

         else if (CoreArtifactTypes.InterfaceEnum.equals(artType)) {
            // If enum is added or deleted, add attribute change to element with that info
            String isOrdinal =
               item.getArtifactReadable().getSoleAttributeAsString(CoreAttributeTypes.InterfaceEnumOrdinal);
            String isName = item.getName();
            if (item.isAdded() || item.isAddedDueToApplicChange() || item.isDeleted() || item.isDeletedDueToApplicChange()) {
               String isValue = "";
               String wasValue = "";
               String itemType = "";
               if (item.isDeleted() || item.isDeletedDueToApplicChange()) {
                  wasValue = isOrdinal + " = " + isName;
                  itemType = "Deleted Enum Literal";
               } else {
                  isValue = isOrdinal + " = " + isName;
                  itemType = "Added Enum Literal";
               }
               for (MimChangeSummaryItem elementItem : elementItems) {
                  ChangeReportRowDto newChange = new ChangeReportRowDto(elementItem.getArtifactReadable(),
                     ArtifactReadable.SENTINEL, elementItem.getName(), itemType, AttributeId.SENTINEL.getId(),
                     "Modified", isValue, wasValue, ChangeType.Attribute, ModificationType.MODIFIED,
                     ApplicabilityToken.SENTINEL, ApplicabilityToken.SENTINEL);
                  elementItem.getAttributeChanges().add(newChange);
               }
            } else {
               String wasOrdinal = isOrdinal;
               String wasName = isName;
               for (ChangeReportRowDto attrChange : item.getAttributeChanges()) {
                  if (CoreAttributeTypes.Name.getName().equals(attrChange.getItemType())) {
                     wasName = attrChange.getWasValue();
                  } else if (CoreAttributeTypes.InterfaceEnumOrdinal.getName().equals(attrChange.getItemType())) {
                     wasOrdinal = attrChange.getWasValue();
                  }
               }
               String isValue = isOrdinal + " = " + isName;
               String wasValue = wasOrdinal + " = " + wasName;
               for (MimChangeSummaryItem elementItem : elementItems) {
                  ChangeReportRowDto newChange = new ChangeReportRowDto(elementItem.getArtifactReadable(),
                     ArtifactReadable.SENTINEL, elementItem.getName(), "Enum Literal", AttributeId.SENTINEL.getId(),
                     "Modified", isValue, wasValue, ChangeType.Attribute, ModificationType.MODIFIED,
                     ApplicabilityToken.SENTINEL, ApplicabilityToken.SENTINEL);
                  elementItem.getAttributeChanges().add(newChange);
               }
            }
         }
      }

      for (ArtifactId artId : changeMap.keySet()) {
         MimChangeSummaryItem item = changeMap.get(artId);
         ArtifactTypeToken artType = item.getArtType();

         // If there are no actual changes to this item, do not add it to the summary.
         if (item.getRelationChanges().isEmpty() && item.getAttributeChanges().isEmpty() && !item.isAdded() && !item.isDeleted() && item.getChildren().isEmpty()) {
            continue;
         }

         if (CoreArtifactTypes.InterfaceNode.equals(artType)) {
            summary.getNodes().put(artId, item);
         } else if (CoreArtifactTypes.InterfaceConnection.equals(artType)) {
            summary.getConnections().put(artId, item);
         } else if (CoreArtifactTypes.InterfaceMessage.equals(artType)) {
            summary.getMessages().put(artId, item);
         } else if (CoreArtifactTypes.InterfaceSubMessage.equals(artType)) {
            summary.getSubMessages().put(artId, item);
         } else if (CoreArtifactTypes.InterfaceStructure.equals(artType)) {
            summary.getStructures().put(artId, item);
         }
      }

      return summary;
   }

   private List<ArtifactReadable> getElements(ArtifactId artId, ArtifactTypeToken artType, BranchId branch,
      Map<ArtifactId, List<ArtifactReadable>> elementMap) {
      List<ArtifactReadable> elements = new LinkedList<>();
      if (elementMap.containsKey(artId)) {
         elements = elementMap.get(artId);
      } else {
         if (CoreArtifactTypes.InterfaceEnum.equals(artType)) {
            ArtifactReadable enumeration = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).follow(
               CoreRelationTypes.InterfaceEnumeration_EnumerationSet).follow(
                  CoreRelationTypes.InterfacePlatformTypeEnumeration_Element).follow(
                     CoreRelationTypes.InterfaceElementPlatformType_Element).follow(
                        CoreRelationTypes.InterfaceStructureContent_Structure).asArtifact();
            for (ArtifactReadable enumSet : enumeration.getRelated(
               CoreRelationTypes.InterfaceEnumeration_EnumerationSet).getList()) {
               for (ArtifactReadable pType : enumSet.getRelated(
                  CoreRelationTypes.InterfacePlatformTypeEnumeration_Element)) {
                  for (ArtifactReadable element : pType.getRelated(
                     CoreRelationTypes.InterfaceElementPlatformType_Element)) {
                     elements.add(element);
                  }
               }
            }
         } else if (CoreArtifactTypes.InterfaceEnumSet.equals(artType)) {
            ArtifactReadable enumSet = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).follow(
               CoreRelationTypes.InterfacePlatformTypeEnumeration_Element).follow(
                  CoreRelationTypes.InterfaceElementPlatformType_Element).follow(
                     CoreRelationTypes.InterfaceStructureContent_Structure).asArtifact();
            for (ArtifactReadable pType : enumSet.getRelated(
               CoreRelationTypes.InterfacePlatformTypeEnumeration_Element)) {
               for (ArtifactReadable element : pType.getRelated(
                  CoreRelationTypes.InterfaceElementPlatformType_Element)) {
                  elements.add(element);
               }
            }
         } else if (CoreArtifactTypes.InterfacePlatformType.equals(artType)) {
            ArtifactReadable pType = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).follow(
               CoreRelationTypes.InterfaceElementPlatformType_Element).follow(
                  CoreRelationTypes.InterfaceStructureContent_Structure).asArtifact();
            for (ArtifactReadable element : pType.getRelated(CoreRelationTypes.InterfaceElementPlatformType_Element)) {
               elements.add(element);
            }
         } else if (CoreArtifactTypes.InterfaceDataElement.equals(
            artType) || CoreArtifactTypes.InterfaceDataElementArray.equals(artType)) {
            ArtifactReadable element = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).follow(
               CoreRelationTypes.InterfaceStructureContent_Structure).asArtifact();
            elements.add(element);
         }
         elementMap.put(artId, elements);
      }
      return elements;
   }

}
