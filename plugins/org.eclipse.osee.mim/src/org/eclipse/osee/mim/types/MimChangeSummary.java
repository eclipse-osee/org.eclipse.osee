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
package org.eclipse.osee.mim.types;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Ryan T. Baldwin
 */
public class MimChangeSummary {
   private final Map<ArtifactId, MimChangeSummaryItem> allChanges;
   private final Map<ArtifactId, MimChangeSummaryItem> nodes;
   private final Map<ArtifactId, MimChangeSummaryItem> connections;
   private final Map<ArtifactId, MimChangeSummaryItem> messages;
   private final Map<ArtifactId, MimChangeSummaryItem> subMessages;
   private final Map<ArtifactId, MimChangeSummaryItem> structures;

   public MimChangeSummary(Map<ArtifactId, MimChangeSummaryItem> allChanges) {
      this.allChanges = allChanges;
      this.nodes = new HashMap<>();
      this.connections = new HashMap<>();
      this.messages = new HashMap<>();
      this.subMessages = new HashMap<>();
      this.structures = new HashMap<>();
   }

   public Map<ArtifactId, MimChangeSummaryItem> getAll() {
      return allChanges;
   }

   public Map<ArtifactId, MimChangeSummaryItem> getNodes() {
      return nodes;
   }

   public Map<ArtifactId, MimChangeSummaryItem> getConnections() {
      return connections;
   }

   public Map<ArtifactId, MimChangeSummaryItem> getMessages() {
      return messages;
   }

   public Map<ArtifactId, MimChangeSummaryItem> getSubMessages() {
      return subMessages;
   }

   public Map<ArtifactId, MimChangeSummaryItem> getStructures() {
      return structures;
   }

   public MimChangeSummaryItem get(ArtifactReadable art) {
      return get(ArtifactId.valueOf(art.getId()), art.getArtifactType());
   }

   public MimChangeSummaryItem get(ArtifactId artId, ArtifactTypeToken artType) {
      if (CoreArtifactTypes.InterfaceNode.equals(artType)) {
         return nodes.get(artId);
      } else if (CoreArtifactTypes.InterfaceConnection.equals(artType)) {
         return connections.get(artId);
      } else if (CoreArtifactTypes.InterfaceMessage.equals(artType)) {
         return messages.get(artId);
      } else if (CoreArtifactTypes.InterfaceSubMessage.equals(artType)) {
         return subMessages.get(artId);
      } else if (CoreArtifactTypes.InterfaceStructure.equals(artType)) {
         return structures.get(artId);
      }
      return null;
   }

}