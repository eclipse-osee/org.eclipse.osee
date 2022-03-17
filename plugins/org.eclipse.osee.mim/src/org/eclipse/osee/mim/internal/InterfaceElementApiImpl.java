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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceElementApiImpl implements InterfaceElementApi {

   private ArtifactAccessor<InterfaceStructureElementToken> accessor;
   private final InterfacePlatformTypeApi platformApi;
   private final List<AttributeTypeId> elementAttributeList;

   InterfaceElementApiImpl(OrcsApi orcsApi, InterfacePlatformTypeApi platformTypeApi) {
      this.setAccessor(new InterfaceElementAccessor(orcsApi));
      this.platformApi = platformTypeApi;
      this.elementAttributeList = this.createElementAttributeList();
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
   public List<InterfaceStructureElementToken> getAll(BranchId branch) {
      try {
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) this.getAccessor().getAll(branch,
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
               CoreRelationTypes.InterfaceStructureContent_Structure, structureId,
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
         InterfaceStructureElementToken element = this.getAccessor().getByRelation(branch, elementId,
            CoreRelationTypes.InterfaceStructureContent_Structure, structureId, InterfaceStructureElementToken.class);
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
            platformType = this.platformApi.getAccessor().getByRelationWithoutId(branch,
               CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(element.getId()),
               PlatformTypeToken.class);
         } else {
            platformType = defaultPlatformType;
         }
         if (previousElement.isInvalid()) {
            element.setBeginByte((double) 0);
            element.setBeginWord((double) 0);
         } else {
            element.setBeginByte((previousElement.getEndByte() + 1) % 4);
            element.setBeginWord(Math.floor(
               previousElement.getEndByte() == 3 ? previousElement.getEndWord() + 1 : previousElement.getEndWord()));
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
      } catch (Exception ex) {

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
            defaultPlatformType);
         tempElements.add(previousElement);
         if (!elementIterator.hasNext()) {
            /**
             * If currentElement = last, set it up so that it may be added/serialized
             */
            currentElement = this.defaultSetUpElement(branch, currentElement, previousElement, defaultPlatformType);
         }
         while (elementIterator.hasNext()) {
            InterfaceStructureElementToken nextElement = elementIterator.next();
            currentElement = this.defaultSetUpElement(branch, currentElement, previousElement, defaultPlatformType);
            tempElements.add(currentElement);
            previousElement = currentElement;
            currentElement = nextElement;
         }
         currentElement = this.defaultSetUpElement(branch, currentElement, previousElement, defaultPlatformType);
         tempElements.add(currentElement);
         elements = tempElements;
      } else {
         /**
          * less than 2 elements
          */
         for (InterfaceStructureElementToken element : elements) {
            element =
               this.defaultSetUpElement(branch, element, InterfaceStructureElementToken.SENTINEL, defaultPlatformType);
         }
      }
      return elements;
   }

   @Override
   public List<InterfaceStructureElementToken> getFiltered(BranchId branch, String filter) {
      try {
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) this.getAccessor().getAllByFilter(branch, filter,
               this.elementAttributeList, InterfaceStructureElementToken.class);
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
         PlatformTypeToken platformType =
            this.platformApi.getAccessor().get(branch, platformTypeId, PlatformTypeToken.class);
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) this.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceElementPlatformType_PlatformType, platformTypeId,
               InterfaceStructureElementToken.class);
         elements = parseElements(branch, elements, platformType);
         return elements;
      } catch (Exception ex) {
         return new LinkedList<InterfaceStructureElementToken>();
      }
   }
}
