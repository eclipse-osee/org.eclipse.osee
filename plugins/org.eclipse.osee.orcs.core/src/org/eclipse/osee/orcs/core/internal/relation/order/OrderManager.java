/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.relation.order;

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.internal.relation.sorter.Sorter;
import org.eclipse.osee.orcs.core.internal.relation.sorter.SorterProvider;

/**
 * @author Roberto E. Escobar
 */
public class OrderManager implements HasOrderData {

   private static final OrderEntryComparator ENTRY_COMPARATOR = new OrderEntryComparator();

   private final Map<RelationTypeSide, OrderData> orderDataMap = new ConcurrentHashMap<>();
   private final OrderAccessor accessor;
   private final SorterProvider sorterProvider;

   public OrderManager(SorterProvider sorterProvider, OrderAccessor accessor) {
      super();
      this.sorterProvider = sorterProvider;
      this.accessor = accessor;
   }

   @Override
   public void add(RelationTypeSide typeAndSide, OrderData data) {
      Conditions.checkNotNull(typeAndSide, "type and side key");
      Conditions.checkNotNull(data, "orderData");

      orderDataMap.put(typeAndSide, data);
   }

   @Override
   public void remove(RelationTypeSide typeAndSide) {
      Conditions.checkNotNull(typeAndSide, "type and side key");

      orderDataMap.remove(typeAndSide);
   }

   @Override
   public Iterator<Entry<RelationTypeSide, OrderData>> iterator() {
      List<Entry<RelationTypeSide, OrderData>> entries =
         new ArrayList<>(orderDataMap.entrySet());
      Collections.sort(entries, ENTRY_COMPARATOR);
      return entries.iterator();
   }

   @Override
   public void clear() {
      orderDataMap.clear();
   }

   @Override
   public boolean isEmpty() {
      return orderDataMap.isEmpty();
   }

   @Override
   public int size() {
      return orderDataMap.size();
   }

   public void load() {
      accessor.load(this);
   }

   public void store() {
      accessor.store(this, OrderChange.Forced);
   }

   public Collection<RelationTypeSide> getExistingTypes() {
      return orderDataMap.keySet();
   }

   private OrderData getOrderData(RelationTypeSide typeAndSide) {
      Conditions.checkNotNull(typeAndSide, "type and side key");
      return orderDataMap.get(typeAndSide);
   }

   public List<String> getOrderIds(RelationTypeSide typeAndSide) {
      Conditions.checkNotNull(typeAndSide, "type and side key");
      OrderData data = orderDataMap.get(typeAndSide);
      return data != null ? data.getOrderIds() : Collections.<String> emptyList();
   }

   public RelationSorter getSorterId(RelationTypeSide typeAndSide) {
      Conditions.checkNotNull(typeAndSide, "type and side key");
      OrderData data = orderDataMap.get(typeAndSide);
      RelationSorter sorterId = null;
      if (data != null) {
         sorterId = data.getSorterId();
      } else {
         sorterId = getDefaultSorterId(typeAndSide);
      }
      return sorterId;
   }

   private RelationSorter getDefaultSorterId(IRelationType type) {
      return sorterProvider.getDefaultSorterId(type);
   }

   public void sort(RelationTypeSide typeAndSide, List<? extends ArtifactToken> listToOrder) {
      if (listToOrder.size() > 1) {
         RelationSorter sorterId = getSorterId(typeAndSide);
         List<String> relativeOrder = getOrderIds(typeAndSide);

         Sorter order = sorterProvider.getSorter(sorterId);
         order.sort(listToOrder, relativeOrder);
      }
   }

   public void setOrder(RelationTypeSide typeAndSide, List<? extends ArtifactToken> relativeSequence) {
      RelationSorter sorterId = getSorterId(typeAndSide);
      setOrder(typeAndSide, sorterId, relativeSequence);
   }

   public void setOrder(RelationTypeSide typeAndSide, RelationSorter sorterId, List<? extends ArtifactToken> relativeSequence) {
      List<String> sequence;
      if (!relativeSequence.isEmpty()) {
         sequence = new ArrayList<>();
         for (ArtifactToken item : relativeSequence) {
            sequence.add(item.getGuid());
         }
      } else {
         sequence = Collections.emptyList();
      }
      setAndStoreOrder(typeAndSide, sorterId, sequence);
   }

   private void setAndStoreOrder(RelationTypeSide typeAndSide, RelationSorter requestedSorterId, List<String> relativeSequence) {
      boolean isDifferentSorterId = isDifferentSorterId(typeAndSide, requestedSorterId);
      boolean changingRelatives = isRelativeOrderChange(typeAndSide, requestedSorterId, relativeSequence);

      OrderChange changeType = OrderChange.NoChange;
      if (isDifferentSorterId || changingRelatives) {
         if (isDifferentSorterId && isSetToDefaultSorter(typeAndSide, requestedSorterId)) {
            remove(typeAndSide);
            changeType = OrderChange.SetToDefault;
         } else {
            OrderData orderData = getOrderData(typeAndSide);
            if (orderData == null) {
               orderData = new OrderData(requestedSorterId, relativeSequence);
               add(typeAndSide, orderData);
            } else {
               orderData.setSorterId(requestedSorterId);
               orderData.setOrderIds(relativeSequence);
            }
            changeType = OrderChange.OrderRequest;
         }
      }
      accessor.store(this, changeType);
   }

   private boolean isDifferentSorterId(RelationTypeSide typeAndSide, RelationSorter newSorterId) {
      RelationSorter currentSorter = getSorterId(typeAndSide);
      return !currentSorter.equals(newSorterId);
   }

   private boolean isSetToDefaultSorter(RelationTypeSide typeAndSide, RelationSorter sorterId) {
      RelationSorter defaultSorterId = getDefaultSorterId(typeAndSide);
      return defaultSorterId.equals(sorterId);
   }

   private boolean isRelativeOrderChange(RelationTypeSide typeAndSide, RelationSorter sorterId, List<String> relativeSequence) {
      boolean result = false;
      if (sorterId.equals(USER_DEFINED)) {
         List<String> currentOrder = getOrderIds(typeAndSide);
         result = !areSame(currentOrder, relativeSequence);
      }
      return result;
   }

   private boolean areSame(List<String> list1, List<String> list2) {
      boolean result = list1.size() == list2.size();
      if (result) {
         for (int index = 0; index < list1.size(); index++) {
            String obj1 = list1.get(index);
            String obj2 = list2.get(index);
            if (!obj1.equals(obj2)) {
               result = false;
               break;
            }
         }
      }
      return result;
   }
}
