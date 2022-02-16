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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceElementArrayApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceStructureFilterEndpoint;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureFilterEndpointImpl implements InterfaceStructureFilterEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final ArtifactId messageId;
   private final ArtifactId subMessageId;
   private final InterfaceStructureApi interfaceStructureApi;
   private final InterfaceElementApi interfaceElementApi;
   private final InterfaceElementArrayApi interfaceElementArrayApi;
   private final InterfacePlatformTypeApi platformApi;

   public InterfaceStructureFilterEndpointImpl(BranchId branch, UserId account, ArtifactId messageId, ArtifactId subMessageId, InterfaceStructureApi interfaceStructureApi, InterfaceElementApi interfaceElementApi, InterfaceElementArrayApi interfaceElementArrayApi, InterfacePlatformTypeApi interfacePlatformTypeApi) {
      this.account = account;
      this.branch = branch;
      this.messageId = messageId;
      this.subMessageId = subMessageId;
      this.interfaceStructureApi = interfaceStructureApi;
      this.interfaceElementApi = interfaceElementApi;
      this.interfaceElementArrayApi = interfaceElementArrayApi;
      this.platformApi = interfacePlatformTypeApi;
   }

   @Override
   public Collection<InterfaceStructureToken> getStructures() {
      List<InterfaceStructureToken> structureList = new LinkedList<InterfaceStructureToken>();
      try {
         structureList = (List<InterfaceStructureToken>) interfaceStructureApi.getAccessor().getAllByRelation(branch,
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, InterfaceStructureToken.class);
         for (InterfaceStructureToken structure : structureList) {
            structure = this.parseStructure(structure);
         }

         return structureList;
      } catch (Exception ex) {
         System.out.println(ex);
         return structureList;
      }
   }

   private List<AttributeTypeId> createStructureAttributeList() {
      List<AttributeTypeId> attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
      attributes.add(CoreAttributeTypes.Description);
      attributes.add(CoreAttributeTypes.InterfaceStructureCategory);
      attributes.add(CoreAttributeTypes.InterfaceMinSimultaneity);
      attributes.add(CoreAttributeTypes.InterfaceMaxSimultaneity);
      attributes.add(CoreAttributeTypes.InterfaceTaskFileType);
      return attributes;
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

   private InterfaceStructureElementToken defaultSetUpElement(InterfaceStructureElementToken element, InterfaceStructureElementToken previousElement) {
      try {
         PlatformTypeToken tempPlatformType = platformApi.getAccessor().getByRelationWithoutId(branch,
            CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(element.getId()),
            PlatformTypeToken.class);
         element.setBeginByte((previousElement.getEndByte() + 1) % 4);
         element.setBeginWord(Math.floor(
            previousElement.getEndByte() == 3 ? previousElement.getEndWord() + 1 : previousElement.getEndWord()));
         element.setInterfacePlatformTypeBitSize(tempPlatformType.getInterfacePlatformTypeBitSize());
         element.setPlatformTypeId(tempPlatformType.getId());
         element.setPlatformTypeName(tempPlatformType.getName());
         element.setLogicalType(
            tempPlatformType.getInterfaceLogicalType() != null ? tempPlatformType.getInterfaceLogicalType() : "");
         element.setInterfacePlatformTypeMinval(
            tempPlatformType.getInterfacePlatformTypeMinval() != null ? tempPlatformType.getInterfacePlatformTypeMinval() : "");
         element.setInterfacePlatformTypeMaxval(
            tempPlatformType.getInterfacePlatformTypeMaxval() != null ? tempPlatformType.getInterfacePlatformTypeMaxval() : "");
         element.setInterfacePlatformTypeDefaultValue(
            tempPlatformType.getInterfacePlatformTypeDefaultValue() != null ? tempPlatformType.getInterfacePlatformTypeDefaultValue() : "");
         element.setUnits(
            tempPlatformType.getInterfacePlatformTypeUnits() != null ? tempPlatformType.getInterfacePlatformTypeUnits() : "");
      } catch (Exception ex) {

      }
      return element;
   }

   private InterfaceStructureToken parseStructure(InterfaceStructureToken structure) {
      try {
         Collection<InterfaceStructureElementToken> elements = new LinkedList<>();
         elements.addAll(interfaceElementApi.getAccessor().getAllByRelation(branch,
            CoreRelationTypes.InterfaceStructureContent_Structure, ArtifactId.valueOf(structure.getId()),
            InterfaceStructureElementToken.class));
         Collection<InterfaceStructureElementToken> tempElements = new LinkedList<>();
         if (elements.size() >= 2) {
            Iterator<InterfaceStructureElementToken> elementIterator = elements.iterator();
            InterfaceStructureElementToken previousElement = elementIterator.next();

            InterfaceStructureElementToken currentElement = elementIterator.next();
            PlatformTypeToken previousPlatformType = platformApi.getAccessor().getByRelationWithoutId(branch,
               CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(previousElement.getId()),
               PlatformTypeToken.class);
            previousElement.setInterfacePlatformTypeBitSize(previousPlatformType.getInterfacePlatformTypeBitSize());
            previousElement.setBeginByte((double) 0);
            previousElement.setBeginWord((double) 0);

            previousElement.setPlatformTypeId(previousPlatformType.getId());
            previousElement.setPlatformTypeName(previousPlatformType.getName());
            previousElement.setLogicalType(
               previousPlatformType.getInterfaceLogicalType() != null ? previousPlatformType.getInterfaceLogicalType() : "");
            previousElement.setInterfacePlatformTypeMinval(
               previousPlatformType.getInterfacePlatformTypeMinval() != null ? previousPlatformType.getInterfacePlatformTypeMinval() : "");
            previousElement.setInterfacePlatformTypeMaxval(
               previousPlatformType.getInterfacePlatformTypeMaxval() != null ? previousPlatformType.getInterfacePlatformTypeMaxval() : "");
            previousElement.setInterfacePlatformTypeDefaultValue(
               previousPlatformType.getInterfacePlatformTypeDefaultValue() != null ? previousPlatformType.getInterfacePlatformTypeDefaultValue() : "");
            previousElement.setUnits(
               previousPlatformType.getInterfacePlatformTypeUnits() != null ? previousPlatformType.getInterfacePlatformTypeUnits() : "");
            tempElements.add(previousElement);
            if (!elementIterator.hasNext()) {
               /**
                * If currentElement = last, set it up so that it may be added/serialized
                */
               currentElement = this.defaultSetUpElement(currentElement, previousElement);
            }
            while (elementIterator.hasNext()) {
               InterfaceStructureElementToken nextElement = elementIterator.next();
               currentElement = this.defaultSetUpElement(currentElement, previousElement);
               if (currentElement.getInterfacePlatformTypeByteSize() >= 4) {
                  if (previousElement.getEndByte() != 3) {
                     /**
                      * Make sure elements of size word or greater start on 0
                      */
                     previousElement =
                        new InterfaceStructureElementToken("spare", "byte align spare for aligning to word start",
                           Math.floor((previousElement.getEndByte() + 1) % 4), previousElement.getEndWord(),
                           (int) Math.floor(3 - (previousElement.getEndByte())), true);
                     tempElements.add(previousElement);
                  }
                  if (currentElement.getInterfacePlatformTypeWordSize() > 1 && (previousElement.getEndWord() + 1) % currentElement.getInterfacePlatformTypeWordSize() != 0) {
                     /**
                      * Make sure elements of size larger than 2 words start on m*n indexed words
                      */
                     previousElement = new InterfaceStructureElementToken("spare",
                        "byte align spare for byte alignment", 0.0, (previousElement.getEndWord() + 1),
                        (int) (Math.floor(
                           (currentElement.getInterfacePlatformTypeWordSize() - ((previousElement.getEndWord() + 1) % currentElement.getInterfacePlatformTypeWordSize()))) * 4) - 1);
                     tempElements.add(previousElement);
                     //make a spare to fill remaining area until beginWord % WordSize=1
                  }
                  //re-set up current Element based on spare
                  currentElement = this.defaultSetUpElement(currentElement, previousElement);
               }
               tempElements.add(currentElement);
               previousElement = currentElement;
               currentElement = nextElement;
            }
            /**
             * Handle last element outside of while loop
             */
            currentElement = this.defaultSetUpElement(currentElement, previousElement);
            if (currentElement.getInterfacePlatformTypeByteSize() >= 4) {
               if (previousElement.getEndByte() != 3) {
                  /**
                   * Make sure elements of size word or greater start on 0
                   */
                  previousElement = new InterfaceStructureElementToken("spare",
                     "byte align spare for aligning to word start", Math.floor((previousElement.getEndByte() + 1) % 4),
                     previousElement.getEndWord(), (int) Math.floor(3 - (previousElement.getEndByte())), true);
                  tempElements.add(previousElement);
               }
               if (currentElement.getInterfacePlatformTypeWordSize() > 1 && (previousElement.getEndWord() + 1) % currentElement.getInterfacePlatformTypeWordSize() != 0) {
                  /**
                   * Make sure elements of size larger than 2 words start on m*n indexed words
                   */
                  previousElement = new InterfaceStructureElementToken("spare", "byte align spare for byte alignment",
                     0.0, (previousElement.getEndWord() + 1), (int) (Math.floor(
                        (currentElement.getInterfacePlatformTypeWordSize() - ((previousElement.getEndWord() + 1) % currentElement.getInterfacePlatformTypeWordSize()))) * 4) - 1);
                  tempElements.add(previousElement);
                  //make a spare to fill remaining area until beginWord % WordSize=1
               }
               //re-set up current Element based on spare
               currentElement = this.defaultSetUpElement(currentElement, previousElement);
            }
            tempElements.add(currentElement);
            if (currentElement.getEndByte() != 3) {
               /**
                * Rule for making sure last element ends on last byte of word(no partials)
                */
               tempElements.add(new InterfaceStructureElementToken("spare",
                  "byte align spare for aligning to word start", currentElement.getEndByte() + 1,
                  currentElement.getEndWord(), (int) Math.floor(3 - (currentElement.getEndByte())), true));
            }
            if (currentElement.getEndWord() % 2 != 1) {
               /**
                * Rule for making sure next element on next structure sent is on boundary of 2n
                */
               currentElement = new InterfaceStructureElementToken("spare", "byte align spare for byte alignment", 0.0,
                  currentElement.getEndWord() + 1, 3);
               tempElements.add(currentElement);
            }
            structure.setElements(tempElements);
         } else {
            /**
             * Condition for when less than 2 elements
             */
            InterfaceStructureElementToken lastElement =
               new InterfaceStructureElementToken("spare", "byte align spare for aligning to word start", 0.0, 0.0, 0);
            for (InterfaceStructureElementToken element : elements) {
               element.setBeginByte(0.0);
               element.setBeginWord(0.0);
               PlatformTypeToken currentPlatformType;
               currentPlatformType = platformApi.getAccessor().getByRelationWithoutId(branch,
                  CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(element.getId()),
                  PlatformTypeToken.class);
               element.setPlatformTypeId(currentPlatformType.getId());
               element.setPlatformTypeName(currentPlatformType.getName());
               element.setInterfacePlatformTypeBitSize(currentPlatformType.getInterfacePlatformTypeBitSize());
               element.setLogicalType(
                  currentPlatformType.getInterfaceLogicalType() != null ? currentPlatformType.getInterfaceLogicalType() : "");
               element.setInterfacePlatformTypeMinval(
                  currentPlatformType.getInterfacePlatformTypeMinval() != null ? currentPlatformType.getInterfacePlatformTypeMinval() : "");
               element.setInterfacePlatformTypeMaxval(
                  currentPlatformType.getInterfacePlatformTypeMaxval() != null ? currentPlatformType.getInterfacePlatformTypeMaxval() : "");
               element.setInterfacePlatformTypeDefaultValue(
                  currentPlatformType.getInterfacePlatformTypeDefaultValue() != null ? currentPlatformType.getInterfacePlatformTypeDefaultValue() : "");
               element.setUnits(
                  currentPlatformType.getInterfacePlatformTypeUnits() != null ? currentPlatformType.getInterfacePlatformTypeUnits() : "");
               lastElement = element;
            }
            tempElements.addAll(elements);
            if (lastElement.getEndByte() != 3) {
               /**
                * Rule for making sure last element ends on last byte of word(no partials)
                */
               tempElements.add(new InterfaceStructureElementToken("spare",
                  "byte align spare for aligning to word start", lastElement.getEndByte() + 1, lastElement.getEndWord(),
                  (int) Math.floor(3 - (lastElement.getEndByte())), true));
            }
            if (lastElement.getEndWord() % 2 != 1) {
               /**
                * Rule for making sure next element on next structure sent is on boundary of 2n
                */
               lastElement = new InterfaceStructureElementToken("spare", "byte align spare for byte alignment", 0.0,
                  lastElement.getEndWord() + 1, 3);
               tempElements.add(lastElement);
            }
            structure.setElements(tempElements);

         }
      } catch (Exception ex) {
         //do nothing
      }
      return structure;
   }

   @Override
   public Collection<InterfaceStructureToken> getStructures(String filter) {
      List<AttributeTypeId> structureAttributes = this.createStructureAttributeList();
      List<AttributeTypeId> elementAttributes = this.createElementAttributeList();
      List<InterfaceStructureToken> totalStructureList = new LinkedList<InterfaceStructureToken>();
      try {
         /**
          * Gets total list of all related structures for lookup later
          */
         totalStructureList =
            (List<InterfaceStructureToken>) interfaceStructureApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, InterfaceStructureToken.class);
         for (InterfaceStructureToken structure : totalStructureList) {
            structure = this.parseStructure(structure);
         }
         /**
          * Gets all structures that match filter conditions
          */
         List<InterfaceStructureToken> structureList =
            (List<InterfaceStructureToken>) interfaceStructureApi.getAccessor().getAllByRelationAndFilter(branch,
               CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, filter, structureAttributes,
               InterfaceStructureToken.class);
         for (InterfaceStructureToken structure : structureList) {
            structure = this.parseStructure(structure);
         }
         /**
          * Gets all elements that match filter conditions, then find their related structures and attach
          */
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) this.interfaceElementApi.getAccessor().getAllByFilter(branch, filter,
               elementAttributes, InterfaceStructureElementToken.class);
         for (InterfaceStructureElementToken element : elements) {
            List<InterfaceStructureToken> subStructureList =
               (List<InterfaceStructureToken>) interfaceStructureApi.getAccessor().getAllByRelation(branch,
                  CoreRelationTypes.InterfaceStructureContent_DataElement, ArtifactId.valueOf(element.getId()),
                  InterfaceStructureToken.class);
            for (InterfaceStructureToken alternateStructure : subStructureList) {
               PlatformTypeToken platformType = platformApi.getAccessor().getByRelationWithoutId(branch,
                  CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(element.getId()),
                  PlatformTypeToken.class);
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
               if (totalStructureList.indexOf(alternateStructure) != -1 && totalStructureList.get(
                  totalStructureList.indexOf(alternateStructure)).getElements().indexOf(element) != -1) {
                  InterfaceStructureElementToken tempElement =
                     totalStructureList.get(totalStructureList.indexOf(alternateStructure)).getElements().get(
                        totalStructureList.get(totalStructureList.indexOf(alternateStructure)).getElements().indexOf(
                           element));
                  element.setBeginByte(tempElement.getBeginByte());
                  //                  element.setEndByte(tempElement.getEndByte());
                  element.setBeginWord(tempElement.getBeginWord());
                  //                  element.setEndWord(tempElement.getEndWord());
               }
               List<InterfaceStructureElementToken> elementList = new LinkedList<InterfaceStructureElementToken>();
               elementList.add(element);
               alternateStructure.setElements(elementList);
               if (totalStructureList.indexOf(alternateStructure) != -1) {
                  InterfaceStructureToken tempStructure =
                     totalStructureList.get(totalStructureList.indexOf(alternateStructure));
                  if (!structureList.contains(alternateStructure)) {
                     structureList.add(alternateStructure);
                  } else {
                     InterfaceStructureToken tempStructure2 =
                        structureList.get(structureList.indexOf(alternateStructure));
                     structureList.remove(alternateStructure);
                     tempStructure2.getElements().add(element);
                     structureList.add(tempStructure2);
                  }
               }
            }
         }
         return structureList;
      } catch (Exception ex) {
         System.out.println(ex);
         return totalStructureList;
      }
   }

}
