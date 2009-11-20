/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Roberto E. Escobar
 */
public class Compare {

   private Compare() {
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
}
