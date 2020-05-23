/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public final class Compare {

   private Compare() {
      // Utility Class
   }

   @SuppressWarnings("unchecked")
   public static boolean isDifferent(Object original, Object other) {
      boolean result = true;
      if (original == null && other == null) {
         result = false;
      } else if (original != null && other != null) {
         if (original instanceof Map<?, ?> && other instanceof Map<?, ?>) {
            result = isDifferent((Map<Object, Object>) original, (Map<Object, Object>) other);
         } else if (original instanceof Collection<?> && other instanceof Collection<?>) {
            result = isDifferent((Collection<Object>) original, (Collection<Object>) other);
         } else if (original instanceof Object[] && other instanceof Object[]) {
            result = isDifferent(Arrays.asList((Object[]) original), Arrays.asList((Object[]) other));
         } else if (original instanceof Dictionary<?, ?> && other instanceof Dictionary<?, ?>) {
            result =
               isDifferent(toMap((Dictionary<Object, Object>) original), toMap((Dictionary<Object, Object>) other));
         } else {
            result = !original.equals(other);
         }
      }
      return result;
   }

   private static boolean isDifferent(Collection<Object> original, Collection<Object> other) {
      return original.size() != other.size() || //
         !Collections.setComplement(original, other).isEmpty() || //
         !Collections.setComplement(other, original).isEmpty();
   }

   private static boolean isDifferent(Map<Object, Object> original, Map<Object, Object> other) {
      boolean result = true;
      if (original.size() == other.size()) {
         Set<Object> set1 = original.keySet();
         Set<Object> set2 = other.keySet();
         if (!isDifferent(set1, set2)) {
            result = false;
            for (Object key : set1) {
               Object value1 = original.get(key);
               Object value2 = other.get(key);
               if (isDifferent(value1, value2)) {
                  result = true;
                  break;
               }
            }
         }
      }
      return result;
   }

   private static Map<Object, Object> toMap(Dictionary<Object, Object> source) {
      Map<Object, Object> sink = new HashMap<>(source.size());
      for (Enumeration<Object> keys = source.keys(); keys.hasMoreElements();) {
         Object key = keys.nextElement();
         sink.put(key, source.get(key));
      }
      return sink;
   }
}
