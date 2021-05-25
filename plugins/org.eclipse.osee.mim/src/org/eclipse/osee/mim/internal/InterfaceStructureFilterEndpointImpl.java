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
            Collection<InterfaceStructureElementToken> elements = new LinkedList<>();
            elements.addAll(interfaceElementApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceStructureContent_Structure, ArtifactId.valueOf(structure.getId()),
               InterfaceStructureElementToken.class));
            for (InterfaceStructureElementToken element : elements) {
               PlatformTypeToken platformType = platformApi.getAccessor().getByRelationWithoutId(branch,
                  CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(element.getId()),
                  PlatformTypeToken.class);
               element.setPlatformTypeId(platformType.getId());
               element.setPlatformTypeName(platformType.getName());
            }
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
         List<InterfaceStructureToken> structureList =
            (List<InterfaceStructureToken>) interfaceStructureApi.getAccessor().getAllByRelationAndFilter(branch,
               CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, filter,
               InterfaceStructureToken.class);
         for (InterfaceStructureToken structure : structureList) {
            structure.setElements(interfaceElementApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceStructureContent_Structure, ArtifactId.valueOf(structure.getId()),
               InterfaceStructureElementToken.class));
         }
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) this.interfaceElementApi.getAccessor().getAllByFilter(branch, filter,
               InterfaceStructureElementToken.class);
         for (InterfaceStructureElementToken element : elements) {
            List<InterfaceStructureToken> subStructureList =
               (List<InterfaceStructureToken>) interfaceStructureApi.getAccessor().getAllByRelation(branch,
                  CoreRelationTypes.InterfaceStructureContent_DataElement, ArtifactId.valueOf(element.getId()),
                  InterfaceStructureToken.class);
            for (InterfaceStructureToken alternateStructure : subStructureList) {
               List<InterfaceStructureElementToken> alternateElements =
                  (List<InterfaceStructureElementToken>) interfaceElementApi.getAccessor().getAllByRelationAndFilter(
                     branch, CoreRelationTypes.InterfaceStructureContent_Structure,
                     ArtifactId.valueOf(alternateStructure.getId()), filter, InterfaceStructureElementToken.class);
               for (InterfaceStructureElementToken element1 : alternateElements) {
                  PlatformTypeToken platformType = platformApi.getAccessor().getByRelationWithoutId(branch,
                     CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(element1.getId()),
                     PlatformTypeToken.class);
                  element1.setPlatformTypeId(platformType.getId());
                  element1.setPlatformTypeName(platformType.getName());
               }
               alternateStructure.setElements(alternateElements);
               if (!structureList.contains(alternateStructure)) {
                  structureList.add(alternateStructure);
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
