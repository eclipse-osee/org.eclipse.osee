/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.framework.skynet.core.relation.sorters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractUserDefinedOrderComparator {

   protected final Map<String, Integer> value;

   public AbstractUserDefinedOrderComparator(List<String> guidOrder) {
      if (guidOrder == null) {
         value = new HashMap<>();
      } else {
         value = new HashMap<>(guidOrder.size());
         for (int i = 0; i < guidOrder.size(); i++) {
            value.put(guidOrder.get(i), i);
         }
      }
   }

   public int compareIntegers(Integer val1, Integer val2) {
      if (val1 == null) {
         val1 = Integer.MAX_VALUE - 1;
      }
      if (val2 == null) {
         val2 = Integer.MAX_VALUE;
      }
      return val1 - val2;
   }

}
