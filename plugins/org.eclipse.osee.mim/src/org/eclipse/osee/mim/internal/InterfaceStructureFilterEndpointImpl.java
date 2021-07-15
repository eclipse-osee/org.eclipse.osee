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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
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
      try {
         List<InterfaceStructureToken> structureList =
            (List<InterfaceStructureToken>) interfaceStructureApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, InterfaceStructureToken.class);
         for (InterfaceStructureToken structure : structureList) {
            double beginWord = 0.0;
            double endWord = 0.0;
            double beginWordDisplay = 0.0;
            double endWordDisplay = 0.0;
            double beginByte = 0.0;
            double endByte = 0.0;
            double beginByteDisplay = 0.0;
            double endByteDisplay = 0.0;
            double sizeInBytes = 0.0;
            Collection<InterfaceStructureElementToken> elements = new LinkedList<>();
            elements.addAll(interfaceElementApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceStructureContent_Structure, ArtifactId.valueOf(structure.getId()),
               InterfaceStructureElementToken.class));
            for (InterfaceStructureElementToken element : elements) {
               PlatformTypeToken platformType = platformApi.getAccessor().getByRelationWithoutId(branch,
                  CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(element.getId()),
                  PlatformTypeToken.class);
               sizeInBytes += Double.parseDouble(platformType.getInterfacePlatformTypeBitSize()) / 8 * (Math.max(1,
                  element.getInterfaceElementIndexEnd() - element.getInterfaceElementIndexStart()));
               endByte = beginByte + (((Double.parseDouble(
                  platformType.getInterfacePlatformTypeBitSize()) / 8) - 1.0) * (Math.max(1,
                     element.getInterfaceElementIndexEnd() - element.getInterfaceElementIndexStart()))) % 4;
               endWord = beginWord + (((Double.parseDouble(
                  platformType.getInterfacePlatformTypeBitSize()) / 8) - 1.0) * (Math.max(1,
                     element.getInterfaceElementIndexEnd() - element.getInterfaceElementIndexStart()))) / 4;
               beginByteDisplay = Math.floor(beginByte);
               endByteDisplay = Math.floor(endByte);
               element.setBeginByte(beginByteDisplay);
               element.setEndByte(endByteDisplay);
               beginByte = (endByte + 1) % 4;
               beginWordDisplay = Math.floor(beginWord);
               endWordDisplay = Math.floor(endWord);
               element.setBeginWord(beginWordDisplay);
               element.setEndWord(endWordDisplay);
               beginWord =
                  beginWord + (((Double.parseDouble(platformType.getInterfacePlatformTypeBitSize())) / 8) * (Math.max(1,
                     element.getInterfaceElementIndexEnd() - element.getInterfaceElementIndexStart()))) / 4;

               element.setPlatformTypeId(platformType.getId());
               element.setPlatformTypeName(platformType.getName());
            }
            structure.setSizeInBytes(sizeInBytes);
            structure.setElements(elements);
         }

         return structureList;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

   @Override
   public Collection<InterfaceStructureToken> getStructures(String filter) {
      try {
         /**
          * Gets total list of all related structures for lookup later
          */
         List<InterfaceStructureToken> totalStructureList =
            (List<InterfaceStructureToken>) interfaceStructureApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, InterfaceStructureToken.class);
         for (InterfaceStructureToken structure : totalStructureList) {
            double beginWord = 0.0;
            double endWord = 0.0;
            double beginWordDisplay = 0.0;
            double endWordDisplay = 0.0;
            double beginByte = 0.0;
            double endByte = 0.0;
            double beginByteDisplay = 0.0;
            double endByteDisplay = 0.0;
            double sizeInBytes = 0.0;
            Collection<InterfaceStructureElementToken> elements = new LinkedList<>();
            elements.addAll(interfaceElementApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceStructureContent_Structure, ArtifactId.valueOf(structure.getId()),
               InterfaceStructureElementToken.class));
            for (InterfaceStructureElementToken element : elements) {
               PlatformTypeToken platformType = platformApi.getAccessor().getByRelationWithoutId(branch,
                  CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(element.getId()),
                  PlatformTypeToken.class);
               sizeInBytes += Double.parseDouble(platformType.getInterfacePlatformTypeBitSize()) / 8 * (Math.max(1,
                  element.getInterfaceElementIndexEnd() - element.getInterfaceElementIndexStart()));
               endByte = beginByte + (((Double.parseDouble(
                  platformType.getInterfacePlatformTypeBitSize()) / 8) - 1.0) * (Math.max(1,
                     element.getInterfaceElementIndexEnd() - element.getInterfaceElementIndexStart()))) % 4;
               endWord = beginWord + (((Double.parseDouble(
                  platformType.getInterfacePlatformTypeBitSize()) / 8) - 1.0) * (Math.max(1,
                     element.getInterfaceElementIndexEnd() - element.getInterfaceElementIndexStart()))) / 4;
               beginByteDisplay = Math.floor(beginByte);
               endByteDisplay = Math.floor(endByte);
               element.setBeginByte(beginByteDisplay);
               element.setEndByte(endByteDisplay);
               beginByte = (endByte + 1) % 4;
               beginWordDisplay = Math.floor(beginWord);
               endWordDisplay = Math.floor(endWord);
               element.setBeginWord(beginWordDisplay);
               element.setEndWord(endWordDisplay);
               beginWord =
                  beginWord + (((Double.parseDouble(platformType.getInterfacePlatformTypeBitSize())) / 8) * (Math.max(1,
                     element.getInterfaceElementIndexEnd() - element.getInterfaceElementIndexStart()))) / 4;

               element.setPlatformTypeId(platformType.getId());
               element.setPlatformTypeName(platformType.getName());
            }
            structure.setSizeInBytes(sizeInBytes);
            structure.setElements(elements);
         }
         /**
          * Gets all structures that match filter conditions
          */
         List<InterfaceStructureToken> structureList =
            (List<InterfaceStructureToken>) interfaceStructureApi.getAccessor().getAllByRelationAndFilter(branch,
               CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, filter,
               InterfaceStructureToken.class);
         for (InterfaceStructureToken structure : structureList) {
            double beginWord = 0.0;
            double endWord = 0.0;
            double beginWordDisplay = 0.0;
            double endWordDisplay = 0.0;
            double beginByte = 0.0;
            double endByte = 0.0;
            double beginByteDisplay = 0.0;
            double endByteDisplay = 0.0;
            double sizeInBytes = 0.0;
            Collection<InterfaceStructureElementToken> elements = new LinkedList<>();
            elements.addAll(interfaceElementApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceStructureContent_Structure, ArtifactId.valueOf(structure.getId()),
               InterfaceStructureElementToken.class));
            for (InterfaceStructureElementToken element : elements) {
               PlatformTypeToken platformType = platformApi.getAccessor().getByRelationWithoutId(branch,
                  CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(element.getId()),
                  PlatformTypeToken.class);
               sizeInBytes += Double.parseDouble(platformType.getInterfacePlatformTypeBitSize()) / 8 * (Math.max(1,
                  element.getInterfaceElementIndexEnd() - element.getInterfaceElementIndexStart()));
               endByte = beginByte + (((Double.parseDouble(
                  platformType.getInterfacePlatformTypeBitSize()) / 8) - 1.0) * (Math.max(1,
                     element.getInterfaceElementIndexEnd() - element.getInterfaceElementIndexStart()))) % 4;
               endWord = beginWord + (((Double.parseDouble(
                  platformType.getInterfacePlatformTypeBitSize()) / 8) - 1.0) * (Math.max(1,
                     element.getInterfaceElementIndexEnd() - element.getInterfaceElementIndexStart()))) / 4;
               beginByteDisplay = Math.floor(beginByte);
               endByteDisplay = Math.floor(endByte);
               element.setBeginByte(beginByteDisplay);
               element.setEndByte(endByteDisplay);
               beginByte = (endByte + 1) % 4;
               beginWordDisplay = Math.floor(beginWord);
               endWordDisplay = Math.floor(endWord);
               element.setBeginWord(beginWordDisplay);
               element.setEndWord(endWordDisplay);
               beginWord =
                  beginWord + (((Double.parseDouble(platformType.getInterfacePlatformTypeBitSize())) / 8) * (Math.max(1,
                     element.getInterfaceElementIndexEnd() - element.getInterfaceElementIndexStart()))) / 4;

               element.setPlatformTypeId(platformType.getId());
               element.setPlatformTypeName(platformType.getName());
            }
            structure.setSizeInBytes(sizeInBytes);
            structure.setElements(elements);
         }
         /**
          * Gets all elements that match filter conditions, then find their related structures and attach
          */
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) this.interfaceElementApi.getAccessor().getAllByFilter(branch, filter,
               InterfaceStructureElementToken.class);
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
               if (totalStructureList.indexOf(alternateStructure) != -1 && totalStructureList.get(
                  totalStructureList.indexOf(alternateStructure)).getElements().indexOf(element) != -1) {
                  InterfaceStructureElementToken tempElement =
                     totalStructureList.get(totalStructureList.indexOf(alternateStructure)).getElements().get(
                        totalStructureList.get(totalStructureList.indexOf(alternateStructure)).getElements().indexOf(
                           element));
                  element.setBeginByte(tempElement.getBeginByte());
                  element.setEndByte(tempElement.getEndByte());
                  element.setBeginWord(tempElement.getBeginWord());
                  element.setEndWord(tempElement.getEndWord());
               }
               List<InterfaceStructureElementToken> elementList = new LinkedList<InterfaceStructureElementToken>();
               elementList.add(element);
               alternateStructure.setElements(elementList);
               if (totalStructureList.indexOf(alternateStructure) != -1) {
                  InterfaceStructureToken tempStructure =
                     totalStructureList.get(totalStructureList.indexOf(alternateStructure));
                  alternateStructure.setSizeInBytes(tempStructure.getSizeInBytes());
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
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

}
