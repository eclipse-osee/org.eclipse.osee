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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.List;

/**
 * @author Jeff C. Phillips
 * @author Don Dunne
 * @author Karol M. Wilk
 */
public class Strings {
   private final static String EMPTY_STRING = "";

   private Strings() {
      // Utility class
   }

   /**
    * OTE pre-compile dependency. Left for binary compatibility for 0.9.8
    */
   public static boolean isValid(String value) {
      return value != null && value.length() > 0;
   }

   public static boolean isValid(CharSequence... values) {
      for (CharSequence value : values) {
         if (value == null || value.length() == 0) {
            return false;
         }
      }
      return true;
   }

   public static String emptyString() {
      return EMPTY_STRING;
   }

   /**
    * Adjusts '&'-containing strings to break the keyboard shortcut ("Accelerator") feature some widgets offer, where
    * &Test will make Alt+T a shortcut. This method breaks the accelerator by escaping ampersands.
    * 
    * @return a string with doubled ampersands.
    */
   public static String escapeAmpersands(String stringWithAmp) {
      return isValid(stringWithAmp) ? stringWithAmp.replace("&", "&&") : null;
   }

   public static String intern(String str) {
      return (str == null) ? null : str.intern();
   }

   /**
    * Will truncate string if necessary and add "..." to end if addDots and truncated
    */
   public static String truncate(String value, int length, boolean addDots) {
      if (value == null) {
         return emptyString();
      }
      String toReturn = value;
      if (Strings.isValid(value) && value.length() > length) {
         int len = addDots && length - 3 > 0 ? length - 3 : length;
         toReturn = value.substring(0, Math.min(length, len)) + (addDots ? "..." : emptyString());
      }
      return toReturn;
   }

   public static String truncate(String value, int length) {
      return truncate(value, length, false);
   }

   public static String unquote(String nameReference) {
      String toReturn = nameReference;
      if (toReturn != null) {
         toReturn = toReturn.trim();
         if (Strings.isValid(toReturn) && toReturn.startsWith("\"") && toReturn.endsWith("\"")) {
            toReturn = toReturn.substring(1, toReturn.length() - 1);
         }
      }
      return toReturn;
   }

   public static String quote(String nameReference) {
      String toReturn = nameReference;
      if (Strings.isValid(nameReference)) {
         toReturn = String.format("\"%s\"", nameReference);
      }
      return toReturn;
   }

   /**
    * Provides a nicer list of items with an 'and' at the end. This could be done using iterator().
    * 
    * @param items Lists of form { apple, banana, orange } or { apple, banana }
    * @return string of form "apple, banana and orange" or "apple and banana" depending on size of list
    */
   public static String buildStatment(List<?> items) {
      StringBuilder niceList = new StringBuilder();
      if (items.size() >= 2) {
         int andIndex = items.size() - 2;
         for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
            niceList.append(items.get(itemIndex));
            if (itemIndex == andIndex) {
               niceList.append(" and ");
            } else if (itemIndex < andIndex) {
               niceList.append(", ");
            }
         }
      } else {
         if (!items.isEmpty()) {
            niceList.append(items.get(0));
         }
      }
      return niceList.toString();
   }
}
