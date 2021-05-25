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
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceElementArrayApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceStructureEndpoint;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureEndpointImpl implements InterfaceStructureEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final ArtifactId messageId;
   private final ArtifactId subMessageId;
   private final InterfaceStructureApi interfaceStructureApi;
   private final InterfaceElementApi interfaceElementApi;
   private final InterfaceElementArrayApi interfaceElementArrayApi;
   private final InterfacePlatformTypeApi platformApi;

   public InterfaceStructureEndpointImpl(BranchId branch, UserId accountId, ArtifactId messageId, ArtifactId subMessageId, InterfaceStructureApi interfaceStructureApi, InterfaceElementApi interfaceElementApi, InterfaceElementArrayApi interfaceElementArrayApi, InterfacePlatformTypeApi interfacePlatformTypeApi) {
      this.account = accountId;
      this.branch = branch;
      this.messageId = messageId;
      this.subMessageId = subMessageId;
      this.interfaceStructureApi = interfaceStructureApi;
      this.interfaceElementApi = interfaceElementApi;
      this.interfaceElementArrayApi = interfaceElementArrayApi;
      this.platformApi = interfacePlatformTypeApi;

   }

   @Override
   public Collection<InterfaceStructureToken> getAllStructures() {
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
   public XResultData createNewStructure(InterfaceStructureToken token) {
      XResultData createResults = interfaceStructureApi.getInserter().addArtifact(token, account, branch);
      if (!createResults.isErrors()) {
         createResults.merge(
            interfaceStructureApi.getInserter().relateArtifact(ArtifactId.valueOf(createResults.getIds().get(0)),
               subMessageId, CoreRelationTypes.InterfaceSubMessageContent_Structure, branch, account));
      }
      return createResults;
   }

   @Override
   public XResultData updateStructure(InterfaceStructureToken token) {
      return interfaceStructureApi.getInserter().replaceArtifact(token, account, branch);
   }

   @Override
   public XResultData patchStructure(InterfaceStructureToken token) {
      return interfaceStructureApi.getInserter().patchArtifact(token, account, branch);
   }

   @Override
   public InterfaceStructureToken getStructure(ArtifactId structureId) {
      try {
         return this.interfaceStructureApi.getAccessor().getByRelation(branch, structureId,
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessageId, InterfaceStructureToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

   @Override
   public XResultData relateStructure(ArtifactId structureId) {
      return interfaceStructureApi.getInserter().relateArtifact(structureId, subMessageId,
         CoreRelationTypes.InterfaceSubMessageContent_Structure, branch, account);
   }

   @Override
   public XResultData removeStructure(ArtifactId structureId) {
      return interfaceStructureApi.getInserter().unrelateArtifact(structureId, subMessageId,
         CoreRelationTypes.InterfaceSubMessageContent_Structure, branch, account);
   }

}
