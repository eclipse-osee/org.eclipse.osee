/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.sorters.LexicographicalRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.LexicographicalRelationSorter.SortMode;
import org.eclipse.osee.framework.skynet.core.relation.sorters.UnorderedRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.UserDefinedRelationSorter;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public class RelationSorterProvider {

   private final Map<RelationSorter, IRelationSorter> orderMap;

   public RelationSorterProvider() {
      orderMap = new ConcurrentHashMap<>();

      registerOrderType(new LexicographicalRelationSorter(SortMode.ASCENDING));
      registerOrderType(new LexicographicalRelationSorter(SortMode.DESCENDING));
      registerOrderType(new UnorderedRelationSorter());
      registerOrderType(new UserDefinedRelationSorter());
   }

   private void registerOrderType(IRelationSorter order) {
      orderMap.put(order.getSorterId(), order);
   }

   public IRelationSorter getRelationOrder(RelationSorter sorterId) {
      if (sorterId.equals(RelationSorter.PREEXISTING)) {
         throw new OseeArgumentException("No sorted is defined for preexisting (nor should there be).");
      }
      IRelationSorter order = orderMap.get(sorterId);
      if (order == null) {
         throw new OseeCoreException("Unable to locate RelationSorter[%s].", sorterId);
      }
      return order;
   }

   public Collection<IRelationSorter> getSorters() {
      return orderMap.values();
   }

   public List<RelationSorter> getAllRelationOrderIds() {
      Collection<IRelationSorter> relationOrder = orderMap.values();
      List<RelationSorter> ids = new ArrayList<>();
      for (IRelationSorter order : relationOrder) {
         ids.add(order.getSorterId());
      }
      Collections.sort(ids);
      return ids;
   }
}
