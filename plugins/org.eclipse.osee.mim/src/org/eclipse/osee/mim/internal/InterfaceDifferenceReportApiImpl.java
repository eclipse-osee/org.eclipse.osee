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
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.model.dto.ChangeReportRowDto;
import org.eclipse.osee.mim.InterfaceDifferenceReportApi;
import org.eclipse.osee.mim.types.MimChangeSummary;
import org.eclipse.osee.mim.types.MimChangeSummaryItem;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.QueryType;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Ryan T. Baldwin
 */
public class InterfaceDifferenceReportApiImpl implements InterfaceDifferenceReportApi {

   private final OrcsApi orcsApi;

   InterfaceDifferenceReportApiImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
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
      return this.getChangeSummary(branch1, branch2, view, ArtifactId.SENTINEL);
   }

   @Override
   public MimChangeSummary getChangeSummary(BranchId branch1, BranchId branch2, ArtifactId view,
      ArtifactId connectionId) {
      Map<ArtifactId, MimChangeSummaryItem> changes = getChangeSummaryItems(branch1, branch2, view);
      return getChangeSummary(branch1, branch2, view, connectionId, changes, new LinkedList<>());
   }

   @Override
   public MimChangeSummary getChangeSummary(BranchId branch1, BranchId branch2, ArtifactId view,
      ArtifactId connectionId, Map<ArtifactId, MimChangeSummaryItem> changes) {
      return getChangeSummary(branch1, branch2, view, connectionId, changes, new LinkedList<>());
   }

   @Override
   public MimChangeSummary getChangeSummary(BranchId branch1, BranchId branch2, ArtifactId view,
      ArtifactId connectionId, Map<ArtifactId, MimChangeSummaryItem> changes,
      List<ApplicabilityToken> applicTokenList) {
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

      List<ArtifactTypeToken> elementTypes =
         Arrays.asList(CoreArtifactTypes.InterfaceDataElement, CoreArtifactTypes.InterfaceDataElementArray,
            CoreArtifactTypes.InterfacePlatformType, CoreArtifactTypes.InterfaceEnumSet,
            CoreArtifactTypes.InterfaceEnum, CoreArtifactTypes.InterfaceArrayIndexDescription);

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
         if (!item.isAdded() && !item.isDeleted() && !item.isAddedDueToApplicChange() && !item.isApplicabilityChanged() && !item.isDeletedDueToApplicChange() && item.getAttributeChanges().isEmpty() && item.getRelationChanges().isEmpty() && item.getChildren().isEmpty()) {
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

               // If the element is part of an array, add the element as a child of the array header, creating the array
               // header if it doesn't exist.
               for (ArtifactReadable arrayHeader : element.getRelated(
                  CoreRelationTypes.InterfaceElementArrayElement_Element).getList()) {
                  if (!applicTokens.contains(arrayHeader.getApplicabilityToken())) {
                     continue;
                  }
                  MimChangeSummaryItem arrayHeaderItem = changeMap.get(arrayHeader.getArtifactId());
                  if (arrayHeaderItem == null) {
                     arrayHeaderItem = new MimChangeSummaryItem(arrayHeader);
                     changeMap.put(arrayHeader.getArtifactId(), arrayHeaderItem);
                  }
                  if (!arrayHeaderItem.getChildren().contains(elementItem)) {
                     arrayHeaderItem.getChildren().add(elementItem);
                  }

                  // Add array headers as children to the affected structures, creating the structure items if they don't exist.
                  for (ArtifactReadable structure : arrayHeader.getRelated(
                     CoreRelationTypes.InterfaceStructureContent_Structure).getList()) {
                     if (!applicTokens.contains(structure.getApplicabilityToken())) {
                        continue;
                     }
                     MimChangeSummaryItem structureItem = changeMap.get(structure.getArtifactId());
                     if (structureItem == null) {
                        structureItem = new MimChangeSummaryItem(structure);
                        changeMap.put(structure.getArtifactId(), structureItem);
                     }
                     if (!structureItem.getChildren().contains(arrayHeaderItem)) {
                        structureItem.getChildren().add(arrayHeaderItem);
                     }
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
                              relationChange.getIsApplic(), relationChange.getWasApplic(), relationChange.getTxId());
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
                  ChangeReportRowDto newChange =
                     new ChangeReportRowDto(elementItem.getArtifactReadable(), ArtifactReadable.SENTINEL,
                        elementItem.getName(), itemType, AttributeId.SENTINEL.getId(), "Modified", isValue, wasValue,
                        ChangeType.Attribute, ModificationType.MODIFIED, ApplicabilityToken.SENTINEL,
                        ApplicabilityToken.SENTINEL, item.getArtifactReadable().getTransaction());
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
                  ChangeReportRowDto newChange =
                     new ChangeReportRowDto(elementItem.getArtifactReadable(), ArtifactReadable.SENTINEL,
                        elementItem.getName(), "Enum Literal", AttributeId.SENTINEL.getId(), "Modified", isValue,
                        wasValue, ChangeType.Attribute, ModificationType.MODIFIED, ApplicabilityToken.SENTINEL,
                        ApplicabilityToken.SENTINEL, elementItem.getArtifactReadable().getTransaction());
                  elementItem.getAttributeChanges().add(newChange);
               }
            }
         }
      }

      for (ArtifactId artId : changeMap.keySet()) {
         MimChangeSummaryItem item = changeMap.get(artId);
         ArtifactTypeToken artType = item.getArtType();

         // If there are no actual changes to this item, do not add it to the summary.
         if (item.getRelationChanges().isEmpty() && item.getAttributeChanges().isEmpty() && !item.isApplicabilityChanged() && !item.isAdded() && !item.isDeleted() && item.getChildren().isEmpty()) {
            continue;
         }

         if (CoreArtifactTypes.InterfaceNode.equals(artType)) {
            summary.getNodes().put(artId, item);
         } else if (CoreArtifactTypes.InterfaceConnection.equals(artType)) {
            summary.getConnections().put(artId, item);
            // Children that are added/removed due to relation changes only need to be added to to their respective lists.
            item.getChildren().forEach(child -> {
               if (child.getArtType().equals(
                  CoreArtifactTypes.InterfaceMessage) && !summary.getMessages().containsKey(child.getArtId())) {
                  summary.getMessages().put(child.getArtId(), child);
               }
            });
         } else if (CoreArtifactTypes.InterfaceMessage.equals(artType)) {
            summary.getMessages().put(artId, item);
            item.getChildren().forEach(child -> {
               if (child.getArtType().equals(
                  CoreArtifactTypes.InterfaceSubMessage) && !summary.getMessages().containsKey(child.getArtId())) {
                  summary.getSubMessages().put(child.getArtId(), child);
               }
            });
         } else if (CoreArtifactTypes.InterfaceSubMessage.equals(artType)) {
            summary.getSubMessages().put(artId, item);
            item.getChildren().forEach(child -> {
               if (child.getArtType().equals(
                  CoreArtifactTypes.InterfaceStructure) && !summary.getMessages().containsKey(child.getArtId())) {
                  summary.getStructures().put(child.getArtId(), child);
               }
            });
         } else if (CoreArtifactTypes.InterfaceStructure.equals(artType)) {
            summary.getStructures().put(artId, item);
         }
      }

      // Remove items that are not a part of the specified connection
      narrowDiffsToConnection(summary, branch1, branch2, connectionId, view);

      return summary;
   }

   private List<ArtifactReadable> getElements(ArtifactId artId, ArtifactTypeToken artType, BranchId branch,
      Map<ArtifactId, List<ArtifactReadable>> elementMap) {
      List<ArtifactReadable> elements = new LinkedList<>();
      QueryBuilder subQuery = new QueryData(QueryType.SELECT, orcsApi.tokenService()).follow(
         CoreRelationTypes.InterfaceStructureContent_Structure);
      if (elementMap.containsKey(artId)) {
         elements = elementMap.get(artId);
      } else {
         if (CoreArtifactTypes.InterfaceEnum.equals(artType)) {
            ArtifactReadable enumeration = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).follow(
               CoreRelationTypes.InterfaceEnumeration_EnumerationSet).follow(
                  CoreRelationTypes.InterfacePlatformTypeEnumeration_Element).follow(
                     CoreRelationTypes.InterfaceElementPlatformType_Element).followFork(
                        CoreRelationTypes.InterfaceElementArrayElement_Element, subQuery).follow(
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
                  CoreRelationTypes.InterfaceElementPlatformType_Element).followFork(
                     CoreRelationTypes.InterfaceElementArrayElement_Element, subQuery).follow(
                        CoreRelationTypes.InterfaceStructureContent_Structure).asArtifact();
            for (ArtifactReadable pType : enumSet.getRelated(
               CoreRelationTypes.InterfacePlatformTypeEnumeration_Element)) {
               for (ArtifactReadable element : pType.getRelated(
                  CoreRelationTypes.InterfaceElementPlatformType_Element)) {
                  elements.add(element);
               }
            }
         } else if (CoreArtifactTypes.InterfaceArrayIndexDescription.equals(artType)) {
            ArtifactReadable desc = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).follow(
               CoreRelationTypes.InterfaceArrayIndexDescription_Set).follow(
                  CoreRelationTypes.InterfaceElementArrayIndexDescriptionSet_Element).followFork(
                     CoreRelationTypes.InterfaceElementArrayElement_Element, subQuery).follow(
                        CoreRelationTypes.InterfaceStructureContent_Structure).asArtifact();
            for (ArtifactReadable descSet : desc.getRelated(CoreRelationTypes.InterfaceArrayIndexDescription_Set)) {
               for (ArtifactReadable element : descSet.getRelated(
                  CoreRelationTypes.InterfaceElementArrayIndexDescriptionSet_Element)) {
                  elements.add(element);
               }
            }
         } else if (CoreArtifactTypes.InterfacePlatformType.equals(artType)) {
            ArtifactReadable pType = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).follow(
               CoreRelationTypes.InterfaceElementPlatformType_Element).followFork(
                  CoreRelationTypes.InterfaceElementArrayElement_Element, subQuery).follow(
                     CoreRelationTypes.InterfaceStructureContent_Structure).asArtifact();
            for (ArtifactReadable element : pType.getRelated(CoreRelationTypes.InterfaceElementPlatformType_Element)) {
               elements.add(element);
            }
         } else if (CoreArtifactTypes.InterfaceDataElement.equals(
            artType) || CoreArtifactTypes.InterfaceDataElementArray.equals(artType)) {
            ArtifactReadable element = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).followFork(
               CoreRelationTypes.InterfaceElementArrayElement_Element, subQuery).follow(
                  CoreRelationTypes.InterfaceStructureContent_Structure).asArtifact();
            elements.add(element);
         }
         elementMap.put(artId, elements);
      }
      return elements;
   }

   private void narrowDiffsToConnection(MimChangeSummary summary, BranchId branch1, BranchId branch2,
      ArtifactId connectionId, ArtifactId view) {
      if (connectionId.isValid()) {
         List<ArtifactId> branch1Ids = new LinkedList<>();
         List<ArtifactId> branch2Ids = new LinkedList<>();

         // Nodes
         for (MimChangeSummaryItem node : summary.getNodes().values()) {
            if (node.isDeleted()) {
               branch2Ids.add(node.getArtId());
            } else {
               branch1Ids.add(node.getArtId());
            }
         }
         List<ArtifactReadable> nodes = new LinkedList<>();
         nodes.addAll(orcsApi.getQueryFactory().fromBranch(branch1, view).andIds(branch1Ids).follow(
            CoreRelationTypes.InterfaceConnectionNode_Connection).asArtifacts());
         nodes.addAll(orcsApi.getQueryFactory().fromBranch(branch1, view).andIds(branch2Ids).follow(
            CoreRelationTypes.InterfaceConnectionNode_Connection).asArtifacts());
         for (ArtifactReadable node : nodes) {
            if (node.getRelated(CoreRelationTypes.InterfaceConnectionNode_Connection).getList().stream().filter(
               c -> c.getArtifactId().equals(connectionId)).findAny().isEmpty()) {
               summary.getNodes().remove(node.getArtifactId());
            }
         }

         // Connections
         List<MimChangeSummaryItem> connectionItems = new LinkedList<>(summary.getConnections().values());
         for (MimChangeSummaryItem connection : connectionItems) {
            if (!connection.getArtId().equals(connectionId)) {
               summary.getConnections().remove(connection.getArtId());
            }
         }

         // Messages
         branch1Ids = new LinkedList<>();
         branch2Ids = new LinkedList<>();
         for (MimChangeSummaryItem message : summary.getMessages().values()) {
            if (message.isDeleted()) {
               branch2Ids.add(message.getArtId());
            } else {
               branch1Ids.add(message.getArtId());
            }
         }
         List<ArtifactReadable> messages = new LinkedList<>();
         messages.addAll(orcsApi.getQueryFactory().fromBranch(branch1, view).andIds(branch1Ids).follow(
            CoreRelationTypes.InterfaceConnectionMessage_Connection).asArtifacts());
         messages.addAll(orcsApi.getQueryFactory().fromBranch(branch2, view).andIds(branch2Ids).follow(
            CoreRelationTypes.InterfaceConnectionMessage_Connection).asArtifacts());

         for (ArtifactReadable message : messages) {
            if (message.getRelated(CoreRelationTypes.InterfaceConnectionMessage_Connection).getList().stream().filter(
               c -> c.getArtifactId().equals(connectionId)).findAny().isEmpty()) {
               summary.getMessages().remove(message.getArtifactId());
            }
         }

         // Submessages
         branch1Ids = new LinkedList<>();
         branch2Ids = new LinkedList<>();
         for (MimChangeSummaryItem submessage : summary.getSubMessages().values()) {
            if (submessage.isDeleted()) {
               branch2Ids.add(submessage.getArtId());
            } else {
               branch1Ids.add(submessage.getArtId());
            }
         }
         List<ArtifactReadable> submessages = new LinkedList<>();
         submessages.addAll(orcsApi.getQueryFactory().fromBranch(branch1, view).andIds(branch1Ids).follow(
            CoreRelationTypes.InterfaceMessageSubMessageContent_Message).follow(
               CoreRelationTypes.InterfaceConnectionMessage_Connection).asArtifacts());
         submessages.addAll(orcsApi.getQueryFactory().fromBranch(branch2, view).andIds(branch2Ids).follow(
            CoreRelationTypes.InterfaceMessageSubMessageContent_Message).follow(
               CoreRelationTypes.InterfaceConnectionMessage_Connection).asArtifacts());

         for (ArtifactReadable submessage : submessages) {
            List<ArtifactReadable> connections = new LinkedList<>();
            for (ArtifactReadable msg : submessage.getRelated(
               CoreRelationTypes.InterfaceMessageSubMessageContent_Message)) {
               connections.addAll(msg.getRelated(CoreRelationTypes.InterfaceConnectionMessage_Connection).getList());
            }
            if (connections.stream().filter(c -> c.getArtifactId().equals(connectionId)).findAny().isEmpty()) {
               summary.getSubMessages().remove(submessage.getArtifactId());
            }
         }

         // Structures
         branch1Ids = new LinkedList<>();
         branch2Ids = new LinkedList<>();
         for (MimChangeSummaryItem structure : summary.getStructures().values()) {
            if (structure.isDeleted()) {
               branch2Ids.add(structure.getArtId());
            } else {
               branch1Ids.add(structure.getArtId());
            }
         }
         List<ArtifactReadable> structures = new LinkedList<>();
         structures.addAll(orcsApi.getQueryFactory().fromBranch(branch1, view).andIds(branch1Ids).follow(
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage).follow(
               CoreRelationTypes.InterfaceMessageSubMessageContent_Message).follow(
                  CoreRelationTypes.InterfaceConnectionMessage_Connection).asArtifacts());
         structures.addAll(orcsApi.getQueryFactory().fromBranch(branch2, view).andIds(branch2Ids).follow(
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage).follow(
               CoreRelationTypes.InterfaceMessageSubMessageContent_Message).follow(
                  CoreRelationTypes.InterfaceConnectionMessage_Connection).asArtifacts());

         for (ArtifactReadable structure : structures) {
            List<ArtifactReadable> connections = new LinkedList<>();
            for (ArtifactReadable sub : structure.getRelated(CoreRelationTypes.InterfaceSubMessageContent_SubMessage)) {
               for (ArtifactReadable msg : sub.getRelated(
                  CoreRelationTypes.InterfaceMessageSubMessageContent_Message)) {
                  connections.addAll(msg.getRelated(CoreRelationTypes.InterfaceConnectionMessage_Connection).getList());
               }
            }
            if (connections.stream().filter(c -> c.getArtifactId().equals(connectionId)).findAny().isEmpty()) {
               summary.getStructures().remove(structure.getArtifactId());
            }
         }
      }
   }

}
