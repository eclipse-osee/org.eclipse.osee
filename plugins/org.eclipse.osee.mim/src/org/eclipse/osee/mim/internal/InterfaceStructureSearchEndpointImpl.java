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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceElementArrayApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceStructureSearchEndpoint;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureSearchEndpointImpl implements InterfaceStructureSearchEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final InterfaceStructureApi interfaceStructureApi;
   private final InterfaceElementApi interfaceElementApi;
   private final InterfaceElementArrayApi interfaceElementArrayApi;
   private final InterfacePlatformTypeApi platformApi;

   public InterfaceStructureSearchEndpointImpl(BranchId branch, UserId account, InterfaceStructureApi interfaceStructureApi, InterfaceElementApi interfaceElementApi, InterfaceElementArrayApi interfaceElementArrayApi, InterfacePlatformTypeApi interfacePlatformTypeApi) {
      this.account = account;
      this.branch = branch;
      this.interfaceStructureApi = interfaceStructureApi;
      this.interfaceElementApi = interfaceElementApi;
      this.interfaceElementArrayApi = interfaceElementArrayApi;
      this.platformApi = interfacePlatformTypeApi;
   }

   @Override
   public Collection<InterfaceStructureToken> getAllStructures() {
      try {
         List<InterfaceStructureToken> structureList =
            (List<InterfaceStructureToken>) interfaceStructureApi.getAccessor().getAll(branch,
               InterfaceStructureToken.class);
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
         return new LinkedList<InterfaceStructureToken>();
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

   @Override
   public Collection<InterfaceStructureToken> getFilteredStructures(String filter) {
      List<AttributeTypeId> structureAttributes = this.createStructureAttributeList();
      try {
         List<InterfaceStructureToken> structureList =
            (List<InterfaceStructureToken>) interfaceStructureApi.getAccessor().getAllByFilter(branch, filter,
               structureAttributes, InterfaceStructureToken.class);
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
         return new LinkedList<InterfaceStructureToken>();
      }
   }

}
