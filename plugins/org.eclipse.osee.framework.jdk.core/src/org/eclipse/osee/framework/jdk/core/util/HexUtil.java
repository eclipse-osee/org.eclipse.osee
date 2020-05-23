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

package org.eclipse.osee.framework.jdk.core.util;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public final class HexUtil {

   private HexUtil() {
      // Utility class
   }

   public static boolean isHexString(String hexString) {
      return hexString.startsWith("0x");
   }

   public static long toLong(String hexString) {
      Long toReturn = -1L;
      try {
         String hex = hexString;
         if (isHexString(hex)) {
            hex = hexString.substring(2);
         }
         toReturn = Long.parseLong(hex, 16);
      } catch (Exception ex) {
         throw new OseeCoreException(String.format("Error converting [%s] to java.util.Long", hexString), ex);
      }
      return toReturn;

   }

   public static String toString(long value) {
      try {
         return String.format("0x%016X", value);
      } catch (Exception ex) {
         throw new OseeCoreException(String.format("Error converting [%s] to java.util.String", value), ex);
      }
   }

}
