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
package org.eclipse.osee.orcs.core.internal.relation.sorter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.RelationSorter;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.eclipse.osee.orcs.utility.SortOrder;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public class SorterProvider {

   private final Map<RelationSorter, Sorter> orderMap = new HashMap<>();
   private final List<RelationSorter> ids = new ArrayList<>();

   private final RelationTypes typeCache;

   public SorterProvider(RelationTypes typeCache) {
      this.typeCache = typeCache;

      registerOrderType(new LexicographicalSorter(SortOrder.ASCENDING));
      registerOrderType(new LexicographicalSorter(SortOrder.DESCENDING));
      registerOrderType(new UnorderedSorter());
      registerOrderType(new UserDefinedSorter());

      Collection<Sorter> sorters = orderMap.values();
      for (Sorter sorter : sorters) {
         ids.add(sorter.getId());
      }
      Collections.sort(ids, new CaseInsensitiveNameComparator());
   }

   private void registerOrderType(Sorter order) {
      orderMap.put(order.getId(), order);
   }

   public RelationSorter getDefaultSorterId(IRelationType relationType) throws OseeCoreException {
      Conditions.checkNotNull(relationType, "type");
      String orderTypeGuid = typeCache.getDefaultOrderTypeGuid(relationType);
      Conditions.checkNotNullOrEmpty(orderTypeGuid, "defaultOrderTypeGuid", "Invalid default order type uuid for [%s]",
         relationType);
      return RelationOrderBaseTypes.getFromGuid(orderTypeGuid);
   }

   public List<RelationSorter> getSorterIds() {
      return ids;
   }

   public boolean exists(RelationSorter sorterId) throws OseeCoreException {
      Conditions.checkNotNull(sorterId, "sorterId");
      return orderMap.containsKey(sorterId);
   }

   public Sorter getSorter(RelationSorter sorterId) throws OseeCoreException {
      Conditions.checkNotNull(sorterId, "sorterId");
      Sorter sorter = orderMap.get(sorterId);
      Conditions.checkNotNull(sorter, "sorter", "Unable to locate sorter with sorterId %s", sorterId);
      return sorter;
   }

   private static final class CaseInsensitiveNameComparator implements Comparator<Named> {
      @Override
      public int compare(Named o1, Named o2) {
         return o1.getName().compareToIgnoreCase(o2.getName());
      }
   }
}
