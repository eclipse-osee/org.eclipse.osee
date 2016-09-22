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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Roberto E. Escobar
 */
public class OrderEntryComparator implements Comparator<Entry<RelationTypeSide, OrderData>> {

   @Override
   public int compare(Entry<RelationTypeSide, OrderData> o1, Entry<RelationTypeSide, OrderData> o2) {
      RelationTypeSide typeSide1 = o1.getKey();
      RelationTypeSide typeSide2 = o2.getKey();
      OrderData orderData1 = o1.getValue();
      OrderData orderData2 = o2.getValue();

      int result = typeSide1.getGuid().compareTo(typeSide2.getGuid());
      if (result == 0) {
         result = typeSide1.getSide().compareTo(typeSide2.getSide());
      }
      if (result == 0) {
         result = orderData1.getSorterId().compareTo(orderData2.getSorterId());
      }
      if (result == 0) {
         List<String> guids1 = new ArrayList<>(orderData1.getOrderIds());
         List<String> guids2 = new ArrayList<>(orderData2.getOrderIds());
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