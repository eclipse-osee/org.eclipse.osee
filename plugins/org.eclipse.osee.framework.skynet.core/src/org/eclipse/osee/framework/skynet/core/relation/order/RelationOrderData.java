/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.relation.order;

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.core.model.event.RelationOrderModType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Roberto E. Escobar
 */
public class RelationOrderData {

   private final CompositeKeyHashMap<RelationTypeToken, RelationSide, Pair<RelationSorter, List<String>>> lists;
   private final IRelationOrderAccessor accessor;
   private final Artifact artifact;

   public RelationOrderData(IRelationOrderAccessor accessor, Artifact artifact) {
      this.lists = new CompositeKeyHashMap<>();
      this.accessor = accessor;
      this.artifact = artifact;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public void clear() {
      lists.clear();
   }

   public void load() {
      clear();
      accessor.load(artifact, this);
   }

   public Collection<Entry<Pair<RelationTypeToken, RelationSide>, Pair<RelationSorter, List<String>>>> entrySet() {
      return lists.entrySet();
   }

   public Collection<Entry<Pair<RelationTypeToken, RelationSide>, Pair<RelationSorter, List<String>>>> getOrderedEntrySet() {
      List<Entry<Pair<RelationTypeToken, RelationSide>, Pair<RelationSorter, List<String>>>> entries =
         new ArrayList<>(entrySet());
      Collections.sort(entries, new EntryComparator());
      return entries;
   }

   public List<String> getOrderList(RelationTypeToken type, RelationSide side) {
      Pair<RelationSorter, List<String>> currentOrder = getTypeSideEntry(type, side);
      return currentOrder != null ? currentOrder.getSecond() : new ArrayList<>();
   }

   public RelationSorter getCurrentSorterGuid(RelationType type, RelationSide side) {
      Pair<RelationSorter, List<String>> currentOrder = getTypeSideEntry(type, side);
      return currentOrder != null ? currentOrder.getFirst() : type.getDefaultOrderTypeGuid();
   }

   private Pair<RelationSorter, List<String>> getTypeSideEntry(RelationTypeToken type, RelationSide side) {
      return lists.get(type, side);
   }

   public void addOrderList(RelationTypeToken relationType, RelationSide relationSide, RelationSorter sorterId, List<String> guidList) {
      lists.put(relationType, relationSide, new Pair<>(sorterId, guidList));
   }

   public void removeOrderList(RelationTypeToken type, RelationSide side) {
      Conditions.checkNotNull(type, "relationType");
      Conditions.checkNotNull(side, "relationSide");
      lists.removeAndGet(type, side);
   }

   public boolean hasEntries() {
      return !lists.isEmpty();
   }

   public int size() {
      return lists.size();
   }

   public void store(RelationType type, RelationSide side, RelationSorter requestedSorterId, List<Artifact> relativeSequence) {
      storeFromGuids(type, side, requestedSorterId, Artifacts.toGuids(relativeSequence));
   }

   public void storeFromGuids(RelationType type, RelationSide side, RelationSorter requestedSorterId, List<String> relativeSequence) {
      boolean isDifferentSorterId = isDifferentSorterId(type, side, requestedSorterId);
      boolean changingRelatives = isRelativeOrderChange(type, side, requestedSorterId, relativeSequence);
      if (isDifferentSorterId || changingRelatives) {
         RelationOrderModType relationOrderModType = null;
         if (isRevertingToDefaultTypeOrder(type, side, requestedSorterId)) {
            removeOrderList(type, side);
            relationOrderModType = RelationOrderModType.Default;
         } else {
            addOrderList(type, side, requestedSorterId, relativeSequence);
            relationOrderModType = RelationOrderModType.Absolute;
         }
         DefaultBasicGuidArtifact guidArtifact = artifact.getBasicGuidArtifact();
         DefaultBasicUuidRelationReorder reorder = new DefaultBasicUuidRelationReorder(relationOrderModType,
            artifact.getBranch(), type.getGuid(), guidArtifact);

         accessor.store(artifact, this, reorder);
      }
   }

   protected boolean isRevertingToDefaultTypeOrder(RelationType type, RelationSide side, RelationSorter sorterId) {
      return sorterId.equals(type.getDefaultOrderTypeGuid()) && isDifferentSorterId(type, side, sorterId);
   }

   protected boolean isRelativeOrderChange(RelationTypeToken type, RelationSide side, RelationSorter sorterId, List<String> relativeSequence) {
      return sorterId.equals(USER_DEFINED) && !relativeSequence.equals(getOrderList(type, side));
   }

   protected boolean isDifferentSorterId(RelationType type, RelationSide side, RelationSorter sorterId) {
      RelationSorter currentSorterGuid = getCurrentSorterGuid(type, side);
      return !sorterId.equals(currentSorterGuid);
   }

   @Override
   public String toString() {
      return String.format("Relation Order Data for artifact:%s", artifact);
   }

   public List<Pair<RelationTypeToken, RelationSide>> getAvailableTypeSides() {
      return lists.getEnumeratedKeys();
   }

   private final static class EntryComparator implements Serializable, Comparator<Entry<Pair<RelationTypeToken, RelationSide>, Pair<RelationSorter, List<String>>>> {
      private static final long serialVersionUID = 5242452476694174988L;

      @Override
      public int compare(Entry<Pair<RelationTypeToken, RelationSide>, Pair<RelationSorter, List<String>>> o1, Entry<Pair<RelationTypeToken, RelationSide>, Pair<RelationSorter, List<String>>> o2) {
         int result = o1.getKey().getFirst().compareTo(o2.getKey().getFirst());
         if (result == 0) {
            result = o1.getKey().getSecond().compareTo(o2.getKey().getSecond());
         }
         if (result == 0) {
            result = o1.getValue().getFirst().compareTo(o2.getValue().getFirst());
         }
         if (result == 0) {
            List<String> guids1 = new ArrayList<>(o1.getValue().getSecond());
            List<String> guids2 = new ArrayList<>(o2.getValue().getSecond());
            result = guids1.size() - guids2.size();
            if (result == 0) {
               Collections.sort(guids1);
               Collections.sort(guids2);
               for (int index = 0; index < guids1.size(); index++) {
                  result = guids1.get(index).compareTo(guids2.get(index));
                  if (result != 0) {
                     break;
                  }
               }
            }
         }
         return result;
      }
   }
}