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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Roberto E. Escobar
 */
public class RelationOrderData {

   private final CompositeKeyHashMap<String, String, Pair<String, List<String>>> lists;
   private final IRelationOrderAccessor accessor;
   private final IArtifact artifact;

   public RelationOrderData(IRelationOrderAccessor accessor, IArtifact artifact) {
      this.lists = new CompositeKeyHashMap<String, String, Pair<String, List<String>>>();
      this.accessor = accessor;
      this.artifact = artifact;
   }

   public IArtifact getIArtifact() {
      return artifact;
   }

   public void clear() {
      lists.clear();
   }

   public void load() throws OseeCoreException {
      clear();
      accessor.load(getIArtifact(), this);
   }

   public Collection<Entry<Pair<String, String>, Pair<String, List<String>>>> entrySet() {
      return lists.entrySet();
   }

   public Collection<Entry<Pair<String, String>, Pair<String, List<String>>>> getOrderedEntrySet() {
      List<Entry<Pair<String, String>, Pair<String, List<String>>>> entries =
            new ArrayList<Entry<Pair<String, String>, Pair<String, List<String>>>>(entrySet());
      Collections.sort(entries, new EntryComparator());
      return entries;
   }

   public List<String> getOrderList(RelationType type, RelationSide side) throws OseeCoreException {
      Pair<String, List<String>> currentOrder = getTypeSideEntry(type, side);
      return currentOrder != null ? currentOrder.getSecond() : new ArrayList<String>();
   }

   public String getCurrentSorterGuid(RelationType type, RelationSide side) throws OseeCoreException {
      Pair<String, List<String>> currentOrder = getTypeSideEntry(type, side);
      return currentOrder != null ? currentOrder.getFirst() : type.getDefaultOrderTypeGuid();
   }

   private Pair<String, List<String>> getTypeSideEntry(RelationType type, RelationSide side) throws OseeCoreException {
      if (type == null) {
         throw new OseeArgumentException("Relation Type cannot be null");
      }
      if (side == null) {
         throw new OseeArgumentException("Relation Type cannot be null");
      }
      return lists.get(type.getName(), side.name());
   }

   public void addOrderList(RelationType type, RelationSide side, IRelationSorterId sorterId, List<String> guidList) {
      addOrderList(type.getName(), side.name(), sorterId.getGuid(), guidList);
   }

   public void addOrderList(String relationType, String relationSide, String sorterGuid, List<String> guidList) {
      lists.put(relationType, relationSide, new Pair<String, List<String>>(sorterGuid, guidList));
   }

   public void removeOrderList(RelationType type, RelationSide side) throws OseeCoreException {
      if (type == null) {
         throw new OseeArgumentException("Relation Type cannot be null");
      }
      if (side == null) {
         throw new OseeArgumentException("Relation Type cannot be null");
      }
      lists.remove(type.getName(), side.name());
   }

   public boolean hasEntries() {
      return !lists.isEmpty();
   }

   public int size() {
      return lists.size();
   }

   public void store(RelationType type, RelationSide side, IRelationSorterId requestedSorterId, List<? extends IArtifact> relativeSequence) throws OseeCoreException {
      boolean isDifferentSorterId = isDifferentSorterId(type, side, requestedSorterId);
      boolean changingRelatives = isRelativeOrderChange(type, side, requestedSorterId, relativeSequence);
      if (isDifferentSorterId || changingRelatives) {
         if (isRevertingToDefaultTypeOrder(type, side, requestedSorterId)) {
            removeOrderList(type, side);
         } else {
            addOrderList(type, side, requestedSorterId, Artifacts.toGuids(relativeSequence));
         }
         accessor.store(getIArtifact(), this);
      }
   }

   protected boolean isRevertingToDefaultTypeOrder(RelationType type, RelationSide side, IRelationSorterId sorterId) throws OseeCoreException {
      String defaultOrderGuid = type.getDefaultOrderTypeGuid();
      return sorterId.getGuid().equals(defaultOrderGuid) && isDifferentSorterId(type, side, sorterId);
   }

   protected boolean isRelativeOrderChange(RelationType type, RelationSide side, IRelationSorterId sorterId, List<? extends IArtifact> relativeSequence) throws OseeCoreException {
      return sorterId.equals(RelationOrderBaseTypes.USER_DEFINED) && !relativeSequence.isEmpty() && //
      !Artifacts.toGuids(relativeSequence).equals(getOrderList(type, side));
   }

   protected boolean isDifferentSorterId(RelationType type, RelationSide side, IRelationSorterId sorterId) throws OseeCoreException {
      String currentSorterGuid = getCurrentSorterGuid(type, side);
      return !sorterId.getGuid().equals(currentSorterGuid);
   }

   @Override
   public String toString() {
      return String.format("Relation Order Data for artifact:%s", getIArtifact());
   }

   private final static class EntryComparator implements Comparator<Entry<Pair<String, String>, Pair<String, List<String>>>> {

      @Override
      public int compare(Entry<Pair<String, String>, Pair<String, List<String>>> o1, Entry<Pair<String, String>, Pair<String, List<String>>> o2) {
         int result = o1.getKey().getFirst().compareTo(o2.getKey().getFirst());
         if (result == 0) {
            result = o1.getKey().getSecond().compareTo(o2.getKey().getSecond());
         }
         if (result == 0) {
            result = o1.getValue().getFirst().compareTo(o2.getValue().getFirst());
         }
         if (result == 0) {
            List<String> guids1 = new ArrayList<String>(o1.getValue().getSecond());
            List<String> guids2 = new ArrayList<String>(o2.getValue().getSecond());
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
