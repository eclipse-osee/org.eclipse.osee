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
package org.eclipse.osee.mim.types;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

public class InterfaceStructureElementTokenWithPath extends InterfaceStructureElementToken {

   public InterfaceStructureElementTokenWithPath(ArtifactToken art) {
      super(art);
   }

   public InterfaceStructureElementTokenWithPath(ArtifactReadable art) {
      super(art);
   }

   public InterfaceStructureElementTokenWithPath(String name, String description, Double beginByte, Double beginWord, Integer size) {
      super(name, description, beginByte, beginWord, size);
   }

   public InterfaceStructureElementTokenWithPath(String name, String description, Double beginByte, Double beginWord, Integer size, boolean offset) {
      super(name, description, beginByte, beginWord, size, offset);
   }

   public InterfaceStructureElementTokenWithPath(Long id, String name, ApplicabilityToken applicability, PlatformTypeToken pType) {
      super(id, name, applicability, pType);
   }

   public InterfaceStructureElementTokenWithPath(Long id, String name) {
      super(id, name);
   }

   public InterfaceStructureElementTokenWithPath() {
   }

   public List<String> getPaths() {
      List<ArtifactReadable> structures = new LinkedList<>();
      this.getArtifactReadable().getRelatedList(
         CoreRelationTypes.InterfaceElementArrayElement_Element).stream().forEach(
            element -> structures.addAll(
               element.getRelatedList(CoreRelationTypes.InterfaceStructureContent_Structure)));
      structures.addAll(
         this.getArtifactReadable().getRelatedList(CoreRelationTypes.InterfaceStructureContent_Structure));
      return structures.stream().map(
         structure -> structure.getRelatedList(CoreRelationTypes.InterfaceSubMessageContent_SubMessage).stream().map(
            submessage -> submessage.getRelatedList(
               CoreRelationTypes.InterfaceMessageSubMessageContent_Message).stream().map(
                  message -> message.getRelatedList(
                     CoreRelationTypes.InterfaceConnectionMessage_Connection).stream().map(
                        connection -> "/" + connection.getIdString() + "/messages/" + message.getIdString() + "/" + submessage.getIdString() + "/elements/" + structure.getIdString())).flatMap(
                           result -> result)).flatMap(result -> result)).flatMap(result -> result).distinct().collect(
                              Collectors.toList());

   }

   public List<String> getButtonNames() {
      List<ArtifactReadable> structures = new LinkedList<>();
      this.getArtifactReadable().getRelatedList(
         CoreRelationTypes.InterfaceElementArrayElement_Element).stream().forEach(
            element -> structures.addAll(
               element.getRelatedList(CoreRelationTypes.InterfaceStructureContent_Structure)));
      structures.addAll(
         this.getArtifactReadable().getRelatedList(CoreRelationTypes.InterfaceStructureContent_Structure));
      return structures.stream().map(
         structure -> structure.getRelatedList(CoreRelationTypes.InterfaceSubMessageContent_SubMessage).stream().map(
            submessage -> submessage.getRelatedList(
               CoreRelationTypes.InterfaceMessageSubMessageContent_Message).stream().map(
                  message -> message.getRelatedList(
                     CoreRelationTypes.InterfaceConnectionMessage_Connection).stream().map(
                        connection -> "Connection: " + connection.getName() + " " + message.getName() + " > " + submessage.getName() + " > " + structure.getName())).flatMap(
                           result -> result)).flatMap(result -> result)).flatMap(result -> result).distinct().collect(
                              Collectors.toList());
   }

}
