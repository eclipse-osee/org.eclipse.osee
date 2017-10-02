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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public class SorterProvider {

   private final Map<RelationSorter, Sorter> orderMap = new HashMap<>();

   private final RelationTypes typeCache;

   public SorterProvider(RelationTypes typeCache) {
      this.typeCache = typeCache;

      registerOrderType(new LexicographicalSorter(SortOrder.ASCENDING));
      registerOrderType(new LexicographicalSorter(SortOrder.DESCENDING));
      registerOrderType(new UnorderedSorter());
      registerOrderType(new UserDefinedSorter());
   }

   private void registerOrderType(Sorter order) {
      orderMap.put(order.getId(), order);
   }

   public RelationSorter getDefaultSorterId(IRelationType relationType)  {
      Conditions.checkNotNull(relationType, "type");
      return typeCache.getDefaultOrderTypeGuid(relationType);
   }

   public boolean exists(RelationSorter sorterId)  {
      Conditions.checkNotNull(sorterId, "sorterId");
      return orderMap.containsKey(sorterId);
   }

   public Sorter getSorter(RelationSorter sorterId)  {
      if (sorterId.equals(RelationSorter.PREEXISTING)) {
         throw new OseeArgumentException("No sorted is defined for preexisting (nor should there be).");
      }
      Sorter sorter = orderMap.get(sorterId);
      Conditions.checkNotNull(sorter, "sorter", "Unable to locate sorter with sorterId %s", sorterId);
      return sorter;
   }
}