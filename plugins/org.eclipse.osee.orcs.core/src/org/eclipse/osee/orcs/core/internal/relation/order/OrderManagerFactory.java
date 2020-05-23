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

package org.eclipse.osee.orcs.core.internal.relation.order;

import org.eclipse.osee.orcs.core.internal.relation.sorter.SorterProvider;

/**
 * @author Roberto E. Escobar
 */
public class OrderManagerFactory {

   private final OrderParser parser;
   private final SorterProvider sorterProvider;

   public OrderManagerFactory(OrderParser parser, SorterProvider sorterProvider) {
      super();
      this.parser = parser;
      this.sorterProvider = sorterProvider;
   }

   public OrderManager createOrderManager(OrderStore store) {
      OrderAccessor accessor = new OrderAccessorImpl(parser, store);
      OrderManager manager = new OrderManager(sorterProvider, accessor);
      manager.load();
      return manager;
   }

}
