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

/**
 * @author Roberto E. Escobar
 */
public class OrderAccessorImpl implements OrderAccessor {

   private final OrderParser parser;
   private final OrderStore storage;

   public OrderAccessorImpl(OrderParser parser, OrderStore storage) {
      super();
      this.parser = parser;
      this.storage = storage;
   }

   @Override
   public void load(HasOrderData data) {
      data.clear();
      String value = storage.getOrderData();
      parser.loadFromXml(data, value);
   }

   @Override
   public void store(HasOrderData data, OrderChange changeType) {
      if (changeType != OrderChange.NoChange) {
         if (storage.isAccessible()) {
            String value = "";
            if (!data.isEmpty()) {
               value = parser.toXml(data);
            }
            storage.storeOrderData(changeType, value);
         }
      }
   }
}
