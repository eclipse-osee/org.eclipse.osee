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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.model.change.ChangeItem;

/**
 * @author Ryan T. Baldwin
 */
public class MimDifferenceReport {
   private final Map<ArtifactId, MimDifferenceReportItem> changeItems;
   private final List<ArtifactId> nodes;
   private final List<ArtifactId> connections;
   private final List<ArtifactId> messages;
   private final List<ArtifactId> subMessages;
   private final List<ArtifactId> structures;
   private final List<ArtifactId> elements;
   private final List<ArtifactId> enumSets;

   public MimDifferenceReport() {
      this.changeItems = new HashMap<>();
      this.nodes = new LinkedList<>();
      this.connections = new LinkedList<>();
      this.messages = new LinkedList<>();
      this.subMessages = new LinkedList<>();
      this.structures = new LinkedList<>();
      this.elements = new LinkedList<>();
      this.enumSets = new LinkedList<>();
   }

   public void addItem(PLGenericDBObject item) {
      addItem(item, new LinkedList<>());
   }

   public void addItem(PLGenericDBObject item, List<ChangeItem> changes) {
      ArtifactId artId = ArtifactId.valueOf(item.getId());
      MimDifferenceReportItem itemChanges =
         changeItems.getOrDefault(artId, new MimDifferenceReportItem(item, new LinkedList<>()));
      itemChanges.getChanges().addAll(changes);
      changeItems.put(artId, itemChanges);
   }

   public boolean containsItem(ArtifactId artId) {
      return changeItems.containsKey(artId);
   }

   public void addParent(ArtifactId item, ArtifactId parent) {
      MimDifferenceReportItem mapItem = changeItems.get(item);
      if (mapItem != null) {
         mapItem.addParent(parent);
      }
   }

   public boolean hasParents(ArtifactId artId) {
      MimDifferenceReportItem mapItem = changeItems.get(artId);
      if (mapItem != null) {
         return !mapItem.getParents().isEmpty();
      }
      return false;
   }

   public Map<ArtifactId, MimDifferenceReportItem> getChangeItems() {
      return changeItems;
   }

   public List<ArtifactId> getNodes() {
      return nodes;
   }

   public List<ArtifactId> getConnections() {
      return connections;
   }

   public List<ArtifactId> getMessages() {
      return messages;
   }

   public List<ArtifactId> getSubMessages() {
      return subMessages;
   }

   public List<ArtifactId> getStructures() {
      return structures;
   }

   public List<ArtifactId> getElements() {
      return elements;
   }

   public List<ArtifactId> getEnumSets() {
      return enumSets;
   }
}