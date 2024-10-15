/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceValidationApi;
import org.eclipse.osee.mim.types.ConnectionValidationResult;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Ryan T. Baldwin
 */
public class InterfaceValidationApiImpl implements InterfaceValidationApi {

   private final InterfaceConnectionViewApi connectionApi;
   private final InterfaceStructureApi structureApi;
   InterfaceValidationApiImpl(InterfaceConnectionViewApi connectionApi, InterfaceStructureApi structureApi) {
      this.connectionApi = connectionApi;
      this.structureApi = structureApi;
   }

   @Override
   public ConnectionValidationResult validateConnection(BranchId branch, ArtifactId viewId, ArtifactId connectionId) {
      InterfaceConnection connection = this.connectionApi.get(branch, viewId, connectionId,
         Arrays.asList(FollowRelation.fork(CoreRelationTypes.InterfaceConnectionNode_Node),
            FollowRelation.fork(CoreRelationTypes.InterfaceConnectionTransportType_TransportType),
            FollowRelation.follow(CoreRelationTypes.InterfaceConnectionMessage_Message),
            FollowRelation.fork(CoreRelationTypes.InterfaceMessagePubNode_Node),
            FollowRelation.follow(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage),
            FollowRelation.follow(CoreRelationTypes.InterfaceSubMessageContent_Structure),
            FollowRelation.follow(CoreRelationTypes.InterfaceStructureContent_DataElement),
            FollowRelation.fork(CoreRelationTypes.InterfaceElementArrayElement_ArrayElement,
               FollowRelation.follow(CoreRelationTypes.InterfaceElementPlatformType_PlatformType)),
            FollowRelation.follow(CoreRelationTypes.InterfaceElementPlatformType_PlatformType)));

      List<InterfaceMessageToken> messages = connection.getArtifactReadable().getRelated(
         CoreRelationTypes.InterfaceConnectionMessage_Message).getList().stream().map(
            art -> new InterfaceMessageToken(art)).collect(Collectors.toList());

      List<InterfaceStructureToken> structures = new LinkedList<>();

      ConnectionValidationResult result =
         new ConnectionValidationResult(branch, viewId, connection.getName().getValue());

      // All messages must have a message type
      for (InterfaceMessageToken message : messages) {
         if (Strings.isInvalid(message.getInterfaceMessageType().getValue())) {
            result.getMessageTypeErrors().put(message.getArtifactId(), message.getName().getValue());
         }

         // Get all the structs for this message while we're here
         structures.addAll(message.getSubMessages().stream().flatMap(subMsg -> subMsg.getArtifactReadable().getRelated(
            CoreRelationTypes.InterfaceSubMessageContent_Structure).getList().stream()).map(
               art -> new InterfaceStructureToken(art)).map(
                  struct -> structureApi.parseStructure(branch, connectionId, struct, viewId,
                     struct.getElements())).collect(Collectors.toList()));
      }

      // All structures must be byte aligned based on transport type settings
      // Structures should not have duplicate names or abbreviations
      Set<String> structureSheetNames = new HashSet<>();
      for (InterfaceStructureToken structure : structures) {
         structure.getElements().stream().filter(a -> a.getId() == -1L).collect(Collectors.toList());
         if (structure.getIncorrectlySized()) {
            result.getStructureByteAlignmentErrors().put(structure.getArtifactId(), structure.getName().getValue());
         }
         if (structure.getElements().stream().anyMatch(a -> a.getId() == -1L)) {
            result.getStructureWordAlignmentErrors().put(structure.getArtifactId(), structure.getName().getValue());
         }
         ;
         String structureSheetName =
            structure.getNameAbbrev().getValue().isEmpty() ? structure.getName().getValue() : structure.getNameAbbrev().getValue();
         if (structureSheetNames.contains(structureSheetName)) {
            result.getDuplicateStructureNameErrors().put(structure.getArtifactId(), structureSheetName);
         }
         structureSheetNames.add(structureSheetName);
      }

      return result;
   }

}
