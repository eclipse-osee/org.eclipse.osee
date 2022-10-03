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
      return this.getArtifactReadable().getRelatedList(
         CoreRelationTypes.InterfaceStructureContent_Structure).stream().map(
            structureArtifact -> new InterfaceStructureToken(structureArtifact)).map(
               structure -> structure.getArtifactReadable().getRelatedList(
                  CoreRelationTypes.InterfaceSubMessageContent_SubMessage).stream().map(
                     submessageArtifact -> new InterfaceSubMessageToken(submessageArtifact)).map(
                        submessage -> submessage.getArtifactReadable().getRelatedList(
                           CoreRelationTypes.InterfaceMessageSubMessageContent_Message).stream().map(
                              messageArtifact -> new InterfaceMessageToken(messageArtifact)).map(
                                 message -> message.getArtifactReadable().getRelatedList(
                                    CoreRelationTypes.InterfaceConnectionContent_Connection).stream().map(
                                       connection -> new ConnectionView(connection)).map(
                                          connection -> "/" + connection.getIdString() + "/messages/" + message.getIdString() + "/" + submessage.getIdString() + "/elements/" + structure.getIdString())).flatMap(
                                             result -> result)).flatMap(result -> result)).flatMap(
                                                result -> result).distinct().collect(Collectors.toList());

   }

   public List<String> getButtonNames() {
      return this.getArtifactReadable().getRelatedList(
         CoreRelationTypes.InterfaceStructureContent_Structure).stream().map(
            structureArtifact -> new InterfaceStructureToken(structureArtifact)).map(
               structure -> structure.getArtifactReadable().getRelatedList(
                  CoreRelationTypes.InterfaceSubMessageContent_SubMessage).stream().map(
                     submessageArtifact -> new InterfaceSubMessageToken(submessageArtifact)).map(
                        submessage -> submessage.getArtifactReadable().getRelatedList(
                           CoreRelationTypes.InterfaceMessageSubMessageContent_Message).stream().map(
                              messageArtifact -> new InterfaceMessageToken(messageArtifact)).map(
                                 message -> message.getArtifactReadable().getRelatedList(
                                    CoreRelationTypes.InterfaceConnectionContent_Connection).stream().map(
                                       connection -> new ConnectionView(connection)).map(
                                          connection -> "Connection: " + connection.getName() + " " + message.getName() + " > " + submessage.getName() + " > " + structure.getName())).flatMap(
                                             result -> result)).flatMap(result -> result)).flatMap(
                                                result -> result).distinct().collect(Collectors.toList());
   }

}
