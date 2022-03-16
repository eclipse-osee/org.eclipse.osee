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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.GetAllMIMRelatedObjectsEndpoint;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.ResolvedStructurePath;
import org.eclipse.osee.mim.types.StructurePath;

/**
 * @author Luciano T. Vaglienti
 */
public class GetAllMIMRelatedObjectsEndpointImpl implements GetAllMIMRelatedObjectsEndpoint {
   private final BranchId branch;
   private final InterfaceNodeViewApi interfaceNodeApi;
   private final InterfaceConnectionViewApi interfaceConnectionApi;
   private final InterfaceMessageApi messageApi;
   private final InterfaceSubMessageApi subMessageApi;
   private final InterfaceStructureApi interfaceStructureApi;
   public GetAllMIMRelatedObjectsEndpointImpl(BranchId branch, InterfaceStructureApi interfaceStructureApi, InterfaceMessageApi interfaceMessageApi, InterfaceSubMessageApi interfaceSubMessageApi, InterfaceNodeViewApi interfaceNodeApi, InterfaceConnectionViewApi interfaceConnectionViewApi) {
      this.branch = branch;
      this.interfaceNodeApi = interfaceNodeApi;
      this.interfaceConnectionApi = interfaceConnectionViewApi;
      this.messageApi = interfaceMessageApi;
      this.subMessageApi = interfaceSubMessageApi;
      this.interfaceStructureApi = interfaceStructureApi;
   }

   @Override
   public Collection<StructurePath> getAllStructureNames() {
      List<StructurePath> structures = this.interfaceStructureApi.getAllWithoutRelations(branch).stream().map(
         a -> new StructurePath(a.getId(), a.getName())).collect(Collectors.toList());
      structures = getStructureNames(structures);
      return structures;
   }

   private List<StructurePath> getStructureNames(List<StructurePath> structures) {
      //work up the structure to generate a path i.e. structure -> submessage -> message -> connection
      //structure.addPath(path)
      try {
         for (StructurePath structure : structures) {
            //get all submessages
            for (InterfaceSubMessageToken submessage : this.subMessageApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceSubMessageContent_Structure, ArtifactId.valueOf(structure.getId()),
               InterfaceSubMessageToken.class)) {
               //get all messages
               for (InterfaceMessageToken message : this.messageApi.getAccessor().getAllByRelation(branch,
                  CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage,
                  ArtifactId.valueOf(submessage.getId()), InterfaceMessageToken.class)) {
                  //get all connections
                  for (InterfaceConnection connection : this.interfaceConnectionApi.getAccessor().getAllByRelation(
                     branch, CoreRelationTypes.InterfaceConnectionContent_Message, ArtifactId.valueOf(message.getId()),
                     InterfaceConnection.class)) {
                     structure.addPath(new ResolvedStructurePath(message.getName() + " > " + submessage.getName(),
                        "/" + connection.getIdString() + "/messages/" + message.getIdString() + "/" + submessage.getIdString() + "/" + message.getName() + " > " + submessage.getName() + "/elements/" + structure.getIdString()));
                  }
               }
            }
         }
         Collections.sort(structures, new Comparator<StructurePath>() {
            @Override
            public int compare(StructurePath o1, StructurePath o2) {
               return o1.getName().compareTo(o2.getName());
            }
         });
         return structures;
      } catch (Exception ex) {
         return new LinkedList<StructurePath>();
      }
   }

   @Override
   public Collection<StructurePath> getFilteredStructureNames(String filter) {
      List<StructurePath> structures =
         this.interfaceStructureApi.getFilteredWithoutRelations(branch, filter).stream().map(
            a -> new StructurePath(a.getId(), a.getName())).collect(Collectors.toList());
      structures = getStructureNames(structures);
      return structures;
   }


}
