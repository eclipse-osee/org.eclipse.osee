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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.relation.sorters.LexicographicalRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.UnorderedRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.UserDefinedRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.sorters.LexicographicalRelationSorter.SortMode;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public class RelationSorterProvider {

   private final Map<String, IRelationSorter> orderMap;

   public RelationSorterProvider() {
      orderMap = new ConcurrentHashMap<String, IRelationSorter>();

      registerOrderType(new LexicographicalRelationSorter(SortMode.ASCENDING));
      registerOrderType(new LexicographicalRelationSorter(SortMode.DESCENDING));
      registerOrderType(new UnorderedRelationSorter());
      registerOrderType(new UserDefinedRelationSorter());
   }

   private void registerOrderType(IRelationSorter order) {
      orderMap.put(order.getSorterId().getGuid(), order);
   }

   public boolean exists(String orderGuid) throws OseeCoreException {
      if (!GUID.isValid(orderGuid)) {
         throw new OseeArgumentException("Error invalid guid argument");
      }
      return orderMap.get(orderGuid) != null;
   }

   public IRelationSorter getRelationOrder(String orderGuid) throws OseeCoreException {
      if (!GUID.isValid(orderGuid)) {
         throw new OseeArgumentException("Error invalid guid argument");
      }
      IRelationSorter order = orderMap.get(orderGuid);
      if (order == null) {
         throw new OseeCoreException(String.format("Unable to locate RelationOrder for guid[%s].", orderGuid));
      }
      return order;
   }

   public List<IRelationSorterId> getAllRelationOrderIds() {
      Collection<IRelationSorter> relationOrder = orderMap.values();
      List<IRelationSorterId> ids = new ArrayList<IRelationSorterId>();
      for (IRelationSorter order : relationOrder) {
         ids.add(order.getSorterId());
      }
      Collections.sort(ids, new RelationSorterIdComparator());
      return ids;
   }
}
