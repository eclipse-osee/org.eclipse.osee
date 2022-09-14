/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.types.ArtifactMatch;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureElementTokenWithPath;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceElementApiImpl implements InterfaceElementApi {

   private ArtifactAccessor<InterfaceStructureElementToken> accessor;
   private final InterfacePlatformTypeApi platformApi;
   private final List<AttributeTypeId> elementAttributeList;
   private final List<RelationTypeSide> relations;
   private final List<RelationTypeSide> affectedRelations;

   InterfaceElementApiImpl(OrcsApi orcsApi, InterfacePlatformTypeApi platformTypeApi) {
      this.setAccessor(new InterfaceElementAccessor(orcsApi));
      this.platformApi = platformTypeApi;
      this.elementAttributeList = this.createElementAttributeList();
      this.relations = this.createRelationTypeSideList();
      this.affectedRelations = this.createAffectedRelationTypeSideList();
   }

   private List<AttributeTypeId> createElementAttributeList() {
      List<AttributeTypeId> attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
      attributes.add(CoreAttributeTypes.Description);
      attributes.add(CoreAttributeTypes.Notes);
      attributes.add(CoreAttributeTypes.InterfaceElementAlterable);
      attributes.add(CoreAttributeTypes.InterfaceElementIndexEnd);
      attributes.add(CoreAttributeTypes.InterfaceElementIndexStart);
      return attributes;
   }

   private List<RelationTypeSide> createRelationTypeSideList() {
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfaceElementPlatformType_PlatformType);
      return relations;
   }

   private List<RelationTypeSide> createAffectedRelationTypeSideList() {
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfaceStructureContent_DataElement);
      return relations;
   }

   private ArtifactAccessor<InterfaceStructureElementToken> getAccessor() {
      return this.accessor;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceStructureElementToken> accessor) {
      this.accessor = accessor;
   }

   @Override
   /**
    * Leave this one using the legacy parsing methods for Platform Type, data size is too big and causes an out of
    * memory error.
    */
   public List<InterfaceStructureElementToken> getAll(BranchId branch) {
      try {
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) this.getAccessor().getAll(branch, this.getFollowRelationDetails(),
               InterfaceStructureElementToken.class);
         elements = this.parseElements(branch, elements);
         return elements;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return new LinkedList<InterfaceStructureElementToken>();
      }
   }

   @Override
   public List<InterfaceStructureElementToken> getAllRelated(BranchId branch, ArtifactId structureId) {
      try {
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) this.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceStructureContent_Structure, structureId, this.getFollowRelationDetails(),
               InterfaceStructureElementToken.class);
         elements = this.parseElements(branch, elements);
         return elements;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return new LinkedList<InterfaceStructureElementToken>();
      }
   }

   @Override
   public InterfaceStructureElementToken getRelated(BranchId branch, ArtifactId structureId, ArtifactId elementId) {
      try {
         InterfaceStructureElementToken element =
            this.getAccessor().getByRelation(branch, elementId, CoreRelationTypes.InterfaceStructureContent_Structure,
               structureId, this.getFollowRelationDetails(), InterfaceStructureElementToken.class);
         element = this.defaultSetUpElement(branch, element, InterfaceStructureElementToken.SENTINEL);
         return element;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceStructureElementToken.SENTINEL;
   }

   @Override
   public InterfaceStructureElementToken get(BranchId branch, ArtifactId elementId) {
      try {
         InterfaceStructureElementToken element = this.getAccessor().get(branch, elementId,
            this.getFollowRelationDetails(), InterfaceStructureElementToken.class);
         element = this.defaultSetUpElement(branch, element, InterfaceStructureElementToken.SENTINEL);
         return element;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

   private InterfaceStructureElementToken defaultSetUpElement(BranchId branch, InterfaceStructureElementToken element, InterfaceStructureElementToken previousElement) {
      return this.defaultSetUpElement(branch, element, previousElement, PlatformTypeToken.SENTINEL);
   }

   private InterfaceStructureElementToken defaultSetUpElement(BranchId branch, InterfaceStructureElementToken element, InterfaceStructureElementToken previousElement, PlatformTypeToken defaultPlatformType) {
      try {
         PlatformTypeToken platformType;
         if (defaultPlatformType.isInvalid()) {
            ArtifactReadable platformTypeReadable = element.getArtifactReadable().getRelated(
               CoreRelationTypes.InterfaceElementPlatformType_PlatformType).getOneOrDefault(ArtifactReadable.SENTINEL);
            platformType = new PlatformTypeToken(platformTypeReadable);
         } else {
            platformType = defaultPlatformType;
         }
         if (previousElement.isInvalid()) {
            element.setBeginByte((double) 0);
            element.setBeginWord((double) 0);
         } else {
            element.setBeginByte((previousElement.getEndByte() + 1) % 4);
            element.setBeginWord(
               Math.floor(((previousElement.getEndWord() * 4) + previousElement.getEndByte() + 1) / 4));
         }
         element.setInterfacePlatformTypeBitSize(platformType.getInterfacePlatformTypeBitSize());
         element.setPlatformTypeId(platformType.getId());
         element.setPlatformTypeName(platformType.getName());
         element.setLogicalType(
            platformType.getInterfaceLogicalType() != null ? platformType.getInterfaceLogicalType() : "");
         element.setInterfacePlatformTypeMinval(
            platformType.getInterfacePlatformTypeMinval() != null ? platformType.getInterfacePlatformTypeMinval() : "");
         element.setInterfacePlatformTypeMaxval(
            platformType.getInterfacePlatformTypeMaxval() != null ? platformType.getInterfacePlatformTypeMaxval() : "");
         element.setInterfacePlatformTypeDefaultValue(
            platformType.getInterfacePlatformTypeDefaultValue() != null ? platformType.getInterfacePlatformTypeDefaultValue() : "");
         element.setUnits(
            platformType.getInterfacePlatformTypeUnits() != null ? platformType.getInterfacePlatformTypeUnits() : "");
         element.setInterfacePlatformTypeDescription(
            platformType.getDescription() != null ? platformType.getDescription() : "");
      } catch (Exception ex) {
         System.out.println(ex);
      }
      return element;
   }

   private List<InterfaceStructureElementToken> parseElements(BranchId branch, List<InterfaceStructureElementToken> elements) {
      return this.parseElements(branch, elements, PlatformTypeToken.SENTINEL);
   }

   private List<InterfaceStructureElementToken> parseElements(BranchId branch, List<InterfaceStructureElementToken> elements, PlatformTypeToken defaultPlatformType) {
      Iterator<InterfaceStructureElementToken> elementIterator = elements.iterator();
      List<InterfaceStructureElementToken> tempElements = new LinkedList<>();
      if (elements.size() >= 2) {
         InterfaceStructureElementToken previousElement = elementIterator.next();

         InterfaceStructureElementToken currentElement = elementIterator.next();
         this.defaultSetUpElement(branch, previousElement, InterfaceStructureElementToken.SENTINEL,
            previousElement.getPlatformType().isValid() ? previousElement.getPlatformType() : defaultPlatformType);
         tempElements.add(previousElement);
         if (!elementIterator.hasNext()) {
            /**
             * If currentElement = last, set it up so that it may be added/serialized
             */
            currentElement = this.defaultSetUpElement(branch, currentElement, previousElement,
               currentElement.getPlatformType().isValid() ? currentElement.getPlatformType() : defaultPlatformType);
         }
         while (elementIterator.hasNext()) {
            InterfaceStructureElementToken nextElement = elementIterator.next();
            currentElement = this.defaultSetUpElement(branch, currentElement, previousElement,
               currentElement.getPlatformType().isValid() ? currentElement.getPlatformType() : defaultPlatformType);
            tempElements.add(currentElement);
            previousElement = currentElement;
            currentElement = nextElement;
         }
         currentElement = this.defaultSetUpElement(branch, currentElement, previousElement,
            currentElement.getPlatformType().isValid() ? currentElement.getPlatformType() : defaultPlatformType);
         tempElements.add(currentElement);
         elements = tempElements;
      } else {
         /**
          * less than 2 elements
          */
         for (InterfaceStructureElementToken element : elements) {
            element = this.defaultSetUpElement(branch, element, InterfaceStructureElementToken.SENTINEL,
               element.getPlatformType().isValid() ? element.getPlatformType() : defaultPlatformType);
         }
      }
      return elements;
   }

   @Override
   public List<InterfaceStructureElementToken> getFiltered(BranchId branch, String filter) {
      try {
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) this.getAccessor().getAllByFilter(branch, filter,
               this.elementAttributeList, relations, InterfaceStructureElementToken.class);
         elements = this.parseElements(branch, elements);
         return elements;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         return new LinkedList<InterfaceStructureElementToken>();
      }
   }

   @Override
   public List<InterfaceStructureElementToken> getElementsByType(BranchId branch, ArtifactId platformTypeId) {
      try {
         //pre-cache the platform type so that all elements can re-use
         PlatformTypeToken platformType = this.platformApi.getWithRelations(branch, platformTypeId,
            Arrays.asList(CoreRelationTypes.InterfaceElementPlatformType_Element));
         List<InterfaceStructureElementToken> elements = platformType.getArtifactReadable().getRelatedList(
            CoreRelationTypes.InterfaceElementPlatformType_Element).stream().map(
               a -> new InterfaceStructureElementToken(a)).collect(Collectors.toList());
         elements = parseElements(branch, elements, platformType);
         return elements;
      } catch (Exception ex) {
         System.out.println(ex);
         return new LinkedList<InterfaceStructureElementToken>();
      }
   }

   @Override
   public List<InterfaceStructureElementTokenWithPath> getElementsByType(BranchId branch) {
      return this.platformApi.getAllWithElementRelations(branch).stream().map(
         type -> type.getArtifactReadable().getRelatedList(
            CoreRelationTypes.InterfaceElementPlatformType_Element).stream().map(
               element -> new InterfaceStructureElementTokenWithPath(element)).collect(Collectors.toList())).collect(
                  Collectors.toList()).stream().flatMap(List::stream).distinct().collect(Collectors.toList());
   }

   @Override
   public List<InterfaceStructureElementTokenWithPath> getElementsByTypeFilter(BranchId branch, String filter) {
      return this.platformApi.getFilteredWithElementRelations(branch, filter).stream().map(
         type -> type.getArtifactReadable().getRelatedList(
            CoreRelationTypes.InterfaceElementPlatformType_Element).stream().map(
               element -> new InterfaceStructureElementTokenWithPath(element)).collect(Collectors.toList())).collect(
                  Collectors.toList()).stream().flatMap(List::stream).distinct().collect(Collectors.toList());
   }

   @Override
   public List<InterfaceStructureElementToken> getAllRelatedAndFilter(BranchId branch, ArtifactId structureId, String filter) {
      try {
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) this.getAccessor().getAllByRelationAndFilter(branch,
               CoreRelationTypes.InterfaceStructureContent_Structure, structureId, filter, elementAttributeList,
               this.getFollowRelationDetails(), InterfaceStructureElementToken.class);
         elements = this.parseElements(branch, elements);
         return elements;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return new LinkedList<InterfaceStructureElementToken>();
      }
   }

   @Override
   public Collection<InterfaceStructureElementToken> query(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, false);
   }

   @Override
   public List<RelationTypeSide> getFollowRelationDetails() {
      return this.relations;
   }

   @Override
   public InterfaceStructureElementToken getWithAllParentRelations(BranchId branch, ArtifactId elementId) {
      try {
         List<RelationTypeSide> parentRelations = Arrays.asList(CoreRelationTypes.InterfaceStructureContent_Structure,
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage,
            CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
            CoreRelationTypes.InterfaceConnectionContent_Connection);
         return this.getAccessor().get(branch, elementId, parentRelations, InterfaceStructureElementToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceStructureElementToken.SENTINEL;
   }

   @Override
   public List<InterfaceStructureElementToken> getAllFromPlatformType(PlatformTypeToken pType) {
      return pType.getArtifactReadable().getRelated(
         CoreRelationTypes.InterfaceElementPlatformType_Element).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InterfaceStructureElementToken(a)).collect(
               Collectors.toList());
   }

   @Override
   public Collection<InterfaceStructureElementToken> queryExact(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, true);
   }

   @Override
   public Collection<InterfaceStructureElementToken> query(BranchId branch, MimAttributeQuery query, boolean isExact) {
      try {
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) this.getAccessor().getAllByQuery(branch, query,
               this.getFollowRelationDetails(), isExact, InterfaceStructureElementToken.class);
         elements = this.parseElements(branch, elements);
         return elements;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceStructureElementToken>();
   }

   @Override
   public Collection<ArtifactMatch> getAffectedArtifacts(BranchId branch, ArtifactId relatedId) {
      try {
         return this.getAccessor().getAffectedArtifacts(branch, relatedId, affectedRelations);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
      }
      return new LinkedList<ArtifactMatch>();
   }

}
