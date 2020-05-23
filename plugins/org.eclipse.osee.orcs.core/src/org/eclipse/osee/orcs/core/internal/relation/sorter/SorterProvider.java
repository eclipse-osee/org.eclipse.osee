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

package org.eclipse.osee.orcs.core.internal.relation.sorter;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public class SorterProvider {

   private final Map<RelationSorter, Sorter> orderMap = new HashMap<>();

   public SorterProvider() {

      registerOrderType(new LexicographicalSorter(SortOrder.ASCENDING));
      registerOrderType(new LexicographicalSorter(SortOrder.DESCENDING));
      registerOrderType(new UnorderedSorter());
      registerOrderType(new UserDefinedSorter());
   }

   private void registerOrderType(Sorter order) {
      orderMap.put(order.getId(), order);
   }

   public RelationSorter getDefaultSorterId(RelationTypeToken relationType) {
      Conditions.checkNotNull(relationType, "type");
      return relationType.getOrder();
   }

   public boolean exists(RelationSorter sorterId) {
      Conditions.checkNotNull(sorterId, "sorterId");
      return orderMap.containsKey(sorterId);
   }

   public Sorter getSorter(RelationSorter sorterId) {
      if (sorterId.equals(RelationSorter.PREEXISTING)) {
         throw new OseeArgumentException("No sorted is defined for preexisting (nor should there be).");
      }
      Sorter sorter = orderMap.get(sorterId);
      Conditions.checkNotNull(sorter, "sorter", "Unable to locate sorter with sorterId %s", sorterId);
      return sorter;
   }
}