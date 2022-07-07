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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceDifferenceReportApi;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceEnumerationApi;
import org.eclipse.osee.mim.InterfaceEnumerationSetApi;
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
import org.eclipse.osee.mim.types.MimDifferenceItem;
import org.eclipse.osee.mim.types.MimDifferenceReport;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.TransactionReadable;

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
   private final InterfaceEnumerationSetApi interfaceEnumerationSetApi;
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

   InterfaceDifferenceReportApiImpl(OrcsApi orcsApi, InterfaceNodeViewApi interfaceNodeApi, InterfaceConnectionViewApi interfaceConnectionViewApi, InterfaceMessageApi interfaceMessageApi, InterfaceSubMessageApi interfaceSubMessageApi, InterfaceStructureApi interfaceStructureApi, InterfaceElementApi interfaceElementApi, InterfacePlatformTypeApi interfacePlatformApi, InterfaceEnumerationSetApi interfaceEnumerationSetApi, InterfaceEnumerationApi interfaceEnumerationApi) {
      this.orcsApi = orcsApi;
      this.interfaceNodeApi = interfaceNodeApi;
      this.interfaceConnectionApi = interfaceConnectionViewApi;
      this.interfaceMessageApi = interfaceMessageApi;
      this.interfaceSubMessageApi = interfaceSubMessageApi;
      this.interfaceStructureApi = interfaceStructureApi;
      this.interfaceElementApi = interfaceElementApi;
      this.interfacePlatformApi = interfacePlatformApi;
      this.interfaceEnumerationSetApi = interfaceEnumerationSetApi;
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

      // NODES
      for (ArtifactId artId : nodeList) {
         processNode(artId);
      }

      // CONNECTIONS
      for (ArtifactId artId : connectionList) {
         processConnection(artId);
      }

      // MESSAGES
      for (ArtifactId artId : messageList) {
         processMessage(artId);
      }

      // SUBMESSAGES
      for (ArtifactId artId : submessageList) {
         processSubMessage(artId);
      }

      // STRUCTURES
      for (ArtifactId artId : structureList) {
         processStructure(artId);
      }

      // ELEMENTS
      for (ArtifactId artId : elementList) {
         processElement(artId, ArtifactId.SENTINEL);
      }

      // PLATFORM TYPES
      for (ArtifactId artId : pTypeList) {
         processPlatformType(artId);
      }

      // ENUMS
      for (ArtifactId artId : enumList) {
         processEnumeration(artId);
      }

      return diffReport;
   }

   private void processNode(ArtifactId nodeId) {
      List<ChangeItem> changeItems = changeMap.get(nodeId);
      InterfaceNode node = interfaceNodeApi.get(getBranchId(nodeId), nodeId);
      if (node.isValid()) {
         diffReport.addItem(node, changeItems);
         diffReport.getNodes().add(nodeId);
      }
   }

   private void processConnection(ArtifactId connectionId) {
      InterfaceConnection connection = interfaceConnectionApi.get(getBranchId(connectionId), connectionId);
      if (connection.isValid()) {
         diffReport.addItem(connection, getChangeList(connectionId));
         diffReport.getConnections().add(connectionId);
      }
   }

   private void processMessage(ArtifactId messageId) {
      InterfaceMessageToken message = interfaceMessageApi.getWithAllParentRelations(getBranchId(messageId), messageId);

      if (message.isValid()) {
         diffReport.addItem(message, getChangeList(messageId));

         addMessageParent(message);

         diffReport.getMessages().add(messageId);
      }
   }

   private void processSubMessage(ArtifactId submessageId) {
      InterfaceSubMessageToken subMessage =
         interfaceSubMessageApi.getWithAllParentRelations(getBranchId(submessageId), submessageId);

      if (subMessage.isValid()) {
         diffReport.addItem(subMessage, getChangeList(submessageId));

         addSubMessageParents(subMessage);

         diffReport.getSubMessages().add(submessageId);
      }
   }

   private void processStructure(ArtifactId structureId) {
      InterfaceStructureToken structure =
         interfaceStructureApi.getWithAllParentRelations(getBranchId(structureId), structureId);

      if (structure.isValid()) {
         diffReport.addItem(structure, getChangeList(structureId));

         addStructureParents(structure);

         diffReport.getStructures().add(structureId);
      }
   }

   private void processElement(ArtifactId elementId, ArtifactId typeId) {
      InterfaceStructureElementToken element = interfaceElementApi.get(getBranchId(elementId), elementId);
      pTypeList.add(ArtifactId.valueOf(element.getPlatformTypeId())); // Add platform type id to list to process later
      processElement(elementId, typeId, element);
   }

   private void processElement(ArtifactId elementId, ArtifactId typeId, InterfaceStructureElementToken element) {
      if (element.isValid()) {
         diffReport.addItem(element, getChangeList(elementId));

         if (typeId.isValid()) {
            diffReport.addItem(element, getChangeList(typeId));
         }

         if (!diffReport.hasParents(elementId)) {
            InterfaceStructureElementToken elementWithParentRelations =
               interfaceElementApi.getWithAllParentRelations(branch1, elementId);
            addElementParents(elementWithParentRelations);
         }

         if (!diffReport.getElements().contains(elementId)) {
            diffReport.getElements().add(elementId);
         }
      }
   }

   private void processPlatformType(ArtifactId pTypeId) {
      PlatformTypeToken pType = interfacePlatformApi.get(getBranchId(pTypeId), pTypeId);
      if (pType.isValid()) {
         diffReport.addItem(pType, getChangeList(pTypeId));

         // Process Elements
         List<InterfaceStructureElementToken> elements =
            interfaceElementApi.getElementsByType(getBranchId(pTypeId), pTypeId);
         for (InterfaceStructureElementToken element : elements) {
            processElement(ArtifactId.valueOf(element.getId()), pTypeId, element);
         }
      }
   }

   private void processEnumeration(ArtifactId enumId) {
      InterfaceEnumeration enumeration = interfaceEnumerationApi.get(getBranchId(enumId), enumId,
         Arrays.asList(CoreRelationTypes.InterfaceEnumeration_EnumerationSet,
            CoreRelationTypes.InterfacePlatformTypeEnumeration_Element,
            CoreRelationTypes.InterfaceElementPlatformType_Element));

      List<InterfaceEnumerationSet> enumSets = enumeration.getArtifactReadable().getRelatedList(
         CoreRelationTypes.InterfaceEnumeration_EnumerationSet).stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InterfaceEnumerationSet(a)).collect(
               Collectors.toList());

      for (InterfaceEnumerationSet enumSet : enumSets) {
         // Get enum set with populated enum list
         ArtifactId enumSetId = ArtifactId.valueOf(enumSet.getId());
         InterfaceEnumerationSet populatedSet = interfaceEnumerationSetApi.get(getBranchId(enumId), enumSetId);
         diffReport.addItem(populatedSet, getChangeList(enumId));
         diffReport.getEnumSets().add(enumSetId);
         addEnumSetParents(enumSet); // Use the non-populated enum set that has the parent relations here
      }
   }

   private void addMessageParent(InterfaceMessageToken message) {
      ArtifactId messageId = ArtifactId.valueOf(message.getId());
      if (!diffReport.hasParents(messageId)) {
         InterfaceConnection connection = interfaceConnectionApi.getRelatedFromMessage(message);
         ArtifactId connectionId = ArtifactId.valueOf(connection.getId());
         diffReport.addParent(messageId, connectionId);
         diffReport.addItem(connection);
      }
   }

   private void addSubMessageParents(InterfaceSubMessageToken subMessage) {
      ArtifactId subMessageId = ArtifactId.valueOf(subMessage.getId());
      if (!diffReport.hasParents(subMessageId)) {
         List<InterfaceMessageToken> messages = interfaceMessageApi.getAllRelatedFromSubMessage(subMessage);
         for (InterfaceMessageToken message : messages) {
            ArtifactId messageId = ArtifactId.valueOf(message.getId());
            diffReport.addParent(subMessageId, messageId);
            diffReport.addItem(message);
            addMessageParent(message);
         }
      }
   }

   private void addStructureParents(InterfaceStructureToken structure) {
      ArtifactId structureId = ArtifactId.valueOf(structure.getId());
      if (!diffReport.hasParents(structureId)) {
         List<InterfaceSubMessageToken> subMessages = interfaceSubMessageApi.getAllRelatedFromStructure(structure);
         for (InterfaceSubMessageToken subMessage : subMessages) {
            ArtifactId subMessageId = ArtifactId.valueOf(subMessage.getId());
            diffReport.addParent(structureId, subMessageId);
            diffReport.addItem(subMessage);
            addSubMessageParents(subMessage);
         }
      }
   }

   private void addElementParents(InterfaceStructureElementToken element) {
      ArtifactId elementId = ArtifactId.valueOf(element.getId());
      if (!diffReport.hasParents(elementId)) {
         List<InterfaceStructureToken> structures = interfaceStructureApi.getAllRelatedFromElement(element);
         for (InterfaceStructureToken structure : structures) {
            ArtifactId structureId = ArtifactId.valueOf(structure.getId());
            diffReport.addParent(elementId, structureId);
            diffReport.addItem(structure);
            addStructureParents(structure);
         }
      }
   }

   private void addEnumSetParents(InterfaceEnumerationSet enumSet) {
      ArtifactId enumSetId = ArtifactId.valueOf(enumSet.getId());
      if (!diffReport.hasParents(enumSetId)) {
         for (PlatformTypeToken pType : interfacePlatformApi.getAllFromEnumerationSet(enumSet)) {
            ArtifactId pTypeId = ArtifactId.valueOf(pType.getId());
            diffReport.addParent(enumSetId, pTypeId);
            for (InterfaceStructureElementToken element : interfaceElementApi.getAllFromPlatformType(pType)) {
               processElement(ArtifactId.valueOf(element.getId()), ArtifactId.SENTINEL);
            }
         }
      }
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

   private void addChangeToMap(ChangeItem changeItem) {
      ArtifactId artId = changeItem.getArtId();
      long itemTypeId = changeItem.getItemTypeId().getId();
      if (itemTypeId == CoreArtifactTypes.InterfaceNode.getId() || itemTypeId == CoreRelationTypes.InterfaceConnectionPrimary_Node.getId() || itemTypeId == CoreRelationTypes.InterfaceConnectionSecondary_Node.getId()) {
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
   public Map<ArtifactId, MimDifferenceItem> getDifferences(BranchId branch, BranchId compareBranch) {
      Map<ArtifactId, List<ChangeItem>> changeItems = new HashMap<>();
      Map<ArtifactId, MimDifferenceItem> diffs = new HashMap<>();
      List<ChangeItem> changes;

      if (branch.equals(compareBranch)) {
         List<TransactionReadable> txs =
            orcsApi.getQueryFactory().transactionQuery().andBranch(branch).getResults().getList();
         TransactionToken currentTx = txs.get(txs.size() - 1);
         TransactionToken compareToTx = txs.get(txs.size() - 2);
         changes = orcsApi.getTransactionFactory().compareTxs(compareToTx, currentTx);
      } else {
         TransactionToken currentTx =
            orcsApi.getQueryFactory().transactionQuery().andIsHead(branch).getResults().getExactlyOne();
         TransactionToken compareTx =
            orcsApi.getQueryFactory().transactionQuery().andIsHead(compareBranch).getResults().getExactlyOne();
         changes = orcsApi.getBranchOps().compareBranch(currentTx, compareTx); // This will get the diffs but currently will not give correct was/is values.
      }

      for (ChangeItem change : changes) {
         if (change.getArtId().isValid()) {
            List<ChangeItem> artChanges = changeItems.getOrDefault(change.getArtId(), new LinkedList<>());
            artChanges.add(change);
            changeItems.put(change.getArtId(), artChanges);
         }
      }

      for (ArtifactId id : changeItems.keySet()) {
         List<ChangeItem> artChanges = changeItems.get(id);
         MimDifferenceItem item = new MimDifferenceItem(id);
         boolean added = true;
         boolean deleted = false;
         for (ChangeItem change : artChanges) {
            if (!(change.getNetChange().getModType().equals(
               ModificationType.NEW) || change.getNetChange().getModType().equals(ModificationType.INTRODUCED))) {
               added = false;
            }
            if (change.getChangeType().equals(ChangeType.Attribute)) {
               String oldValue = change.getBaselineVersion().getValue();
               String newValue = change.getCurrentVersion().getValue();
               item.addAttributeChange(change.getItemTypeId().getId(), oldValue, newValue);
            } else if (change.getChangeType().equals(
               ChangeType.Artifact) && change.getCurrentVersion().getModType().equals(ModificationType.DELETED)) {
               deleted = true;
            } else if (change.getChangeType().equals(ChangeType.Relation)) {
               boolean relationAdded = change.getCurrentVersion().getModType().equals(
                  ModificationType.NEW) || change.getCurrentVersion().getModType().equals(ModificationType.INTRODUCED);
               item.addRelationChange(change.getItemTypeId().getId(), change.getArtIdB(), relationAdded);
            }
         }
         if (item.getDiffs().isEmpty()) {
            added = false;
         }
         item.setAdded(added);
         item.setDeleted(deleted);
         diffs.put(id, item);
      }

      return diffs;
   }

}
