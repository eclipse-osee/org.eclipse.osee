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
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceElementArrayApi;
import org.eclipse.osee.mim.InterfaceElementEndpoint;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.types.InterfaceStructureElementArrayToken;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceElementEndpointImpl implements InterfaceElementEndpoint {
   private final BranchId branch;
   private final UserId account;
   private final ArtifactId messageId;
   private final ArtifactId subMessageId;
   private final ArtifactId structureId;
   private final InterfaceElementApi elementApi;
   private final InterfaceElementArrayApi elementArrayApi;
   private final InterfacePlatformTypeApi platformApi;

   public InterfaceElementEndpointImpl(BranchId branch, UserId account, ArtifactId messageId, ArtifactId subMessageId, ArtifactId structureId, InterfaceElementApi interfaceElementApi, InterfaceElementArrayApi interfaceElementArrayApi, InterfacePlatformTypeApi interfacePlatformTypeApi) {
      this.account = account;
      this.branch = branch;
      this.messageId = messageId;
      this.subMessageId = subMessageId;
      this.structureId = structureId;
      this.elementApi = interfaceElementApi;
      this.elementArrayApi = interfaceElementArrayApi;
      this.platformApi = interfacePlatformTypeApi;
   }

   @Override
   public Collection<InterfaceStructureElementToken> getAllElements() {
      try {
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) elementApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceStructureContent_Structure, structureId,
               InterfaceStructureElementToken.class);
         for (InterfaceStructureElementToken element : elements) {
            PlatformTypeToken platformType = platformApi.getAccessor().getByRelationWithoutId(branch,
               CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(element.getId()),
               PlatformTypeToken.class);
            element.setPlatformTypeId(platformType.getId());
            element.setPlatformTypeName(platformType.getName());
         }
         return elements;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

   @Override
   public XResultData createNewElement(InterfaceStructureElementToken token) {
      if (token.getInterfaceElementIndexEnd() != null && token.getInterfaceElementIndexEnd() > 0) {
         XResultData createResults =
            elementArrayApi.getInserter().addArtifact(new InterfaceStructureElementArrayToken(token), account, branch);
         if (!createResults.isErrors()) {
            createResults.merge(
               elementArrayApi.getInserter().relateArtifact(ArtifactId.valueOf(createResults.getIds().get(0)),
                  structureId, CoreRelationTypes.InterfaceStructureContent_DataElement, branch, account));
         }
         return createResults;
      } else {
         XResultData createResults = elementApi.getInserter().addArtifact(token, account, branch);
         if (!createResults.isErrors()) {
            createResults.merge(
               elementApi.getInserter().relateArtifact(ArtifactId.valueOf(createResults.getIds().get(0)), structureId,
                  CoreRelationTypes.InterfaceStructureContent_DataElement, branch, account));
         }
         return createResults;
      }
   }

   @Override
   public XResultData updateElement(InterfaceStructureElementToken token) {
      if (token.getInterfaceElementIndexEnd() != null && token.getInterfaceElementIndexEnd() > 0) {
         return elementArrayApi.getInserter().replaceArtifact(new InterfaceStructureElementArrayToken(token), account,
            branch);
      } else {
         return elementApi.getInserter().replaceArtifact(token, account, branch);
      }
   }

   @Override
   public XResultData patchElement(InterfaceStructureElementToken token) {
      if (token.getInterfaceElementIndexEnd() != null && token.getInterfaceElementIndexEnd() > 0) {
         return elementArrayApi.getInserter().patchArtifact(new InterfaceStructureElementArrayToken(token), account,
            branch);
      } else {
         return elementApi.getInserter().patchArtifact(token, account, branch);
      }
   }

   @Override
   public InterfaceStructureElementToken getElement(ArtifactId elementId) {
      try {
         InterfaceStructureElementToken element = this.elementApi.getAccessor().getByRelation(branch, elementId,
            CoreRelationTypes.InterfaceStructureContent_Structure, structureId, InterfaceStructureElementToken.class);
         PlatformTypeToken platformType = platformApi.getAccessor().getByRelationWithoutId(branch,
            CoreRelationTypes.InterfaceElementPlatformType_Element, elementId, PlatformTypeToken.class);
         element.setPlatformTypeId(platformType.getId());
         element.setPlatformTypeName(platformType.getName());
         return element;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

   @Override
   public XResultData relateElement(ArtifactId elementId) {
      return elementApi.getInserter().relateArtifact(elementId, structureId,
         CoreRelationTypes.InterfaceStructureContent_DataElement, branch, account);
   }

   @Override
   public XResultData removeElement(ArtifactId elementId) {
      return elementApi.getInserter().unrelateArtifact(elementId, structureId,
         CoreRelationTypes.InterfaceStructureContent_DataElement, branch, account);
   }

   @Override
   public XResultData relatePlatformType(ArtifactId elementId, ArtifactId platformTypeId) {
      return elementApi.getInserter().relateArtifact(platformTypeId, elementId,
         CoreRelationTypes.InterfaceElementPlatformType_PlatformType, branch, account);
   }

}
