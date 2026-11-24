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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Donald G. Dunne
 */
public class EmailUtil {

   private static Pattern addressPattern = Pattern.compile(".+?@.+?\\.[a-z]+");
   private static final Pattern ADDRESS_SEPARATOR = Pattern.compile("[,;\\s]+");

   public static boolean isEmailValid(String email) {
      return email != null && addressPattern.matcher(email).matches();
   }

   public static boolean isEmailInValid(String email) {
      return !isEmailValid(email);
   }

   /**
    * Splits on commas, semicolons and whitespace. Order is preserved and duplicates are dropped. Addresses are not
    * validated; use {@link #isEmailValid(String)} on each result.
    */
   public static List<String> splitAddresses(String addresses) {
      if (addresses == null) {
         return new ArrayList<>();
      }
      Set<String> unique = new LinkedHashSet<>();
      for (String address : ADDRESS_SEPARATOR.split(addresses.trim())) {
         if (!address.isEmpty()) {
            unique.add(address);
         }
      }
      return new ArrayList<>(unique);
   }

}
