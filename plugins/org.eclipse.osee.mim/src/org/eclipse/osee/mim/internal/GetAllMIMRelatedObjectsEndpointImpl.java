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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.GetAllMIMRelatedObjectsEndpoint;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.ResolvedStructurePath;
import org.eclipse.osee.mim.types.StructurePath;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Luciano T. Vaglienti
 */
public class GetAllMIMRelatedObjectsEndpointImpl implements GetAllMIMRelatedObjectsEndpoint {
   private final BranchId branch;
   private final InterfaceStructureApi interfaceStructureApi;
   private final InterfaceElementApi elementApi;
   public GetAllMIMRelatedObjectsEndpointImpl(BranchId branch, InterfaceStructureApi interfaceStructureApi, InterfaceElementApi interfaceElementApi) {
      this.branch = branch;
      this.interfaceStructureApi = interfaceStructureApi;
      this.elementApi = interfaceElementApi;
   }

   @Override
   public Collection<StructurePath> getAllStructureNames(String filter, ArtifactId connectionId) {
      List<StructurePath> structures = this.interfaceStructureApi.getAllWithRelations(branch,
         FollowRelation.followList(CoreRelationTypes.InterfaceSubMessageContent_SubMessage,
            CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
            CoreRelationTypes.InterfaceConnectionMessage_Connection),
         filter, Arrays.asList(CoreAttributeTypes.Name), CoreAttributeTypes.Name).stream().map(
            s -> new StructurePath(s)).collect(Collectors.toList());

      List<StructurePath> connectionStructures = new LinkedList<>();

      //work up the structure to generate a path i.e. structure -> submessage -> message -> connection
      for (StructurePath structure : structures) {
         for (ArtifactReadable submessage : structure.getStructure().getArtifactReadable().getRelatedList(
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage)) {
            for (ArtifactReadable message : submessage.getRelatedList(
               CoreRelationTypes.InterfaceMessageSubMessageContent_Message)) {
               for (ArtifactReadable connection : message.getRelatedList(
                  CoreRelationTypes.InterfaceConnectionMessage_Connection)) {
                  if (connectionId.isInvalid() || connectionId.equals(connection.getArtifactId())) {
                     structure.addPath(new ResolvedStructurePath(message.getName() + " > " + submessage.getName(),
                        "/" + connection.getIdString() + "/messages/" + message.getIdString() + "/" + submessage.getIdString() + "/elements/" + structure.getIdString()));
                     connectionStructures.add(structure);
                  }
               }
            }
         }
      }

      return connectionStructures;
   }

   @Override
   public Collection<InterfaceStructureElementToken> getElements() {
      return this.elementApi.getAll(branch);
   }

}
