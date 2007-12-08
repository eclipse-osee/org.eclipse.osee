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

public class StringFormat {

   public static String separateWith(String[] items, String separateWith) {
      StringBuilder string = new StringBuilder();
      boolean first = true;
      for (String item : items) {
         if (first)
            first = false;
         else
            string.append(separateWith);
         string.append(item);
      }

      return string.toString();
   }

   public static String commaSeparate(String[] items) {
      return separateWith(items, ",");
   }

   public static String listToCommaSeparatedString(List<String> list) {
      return commaSeparate(list.toArray(new String[list.size()]));
   }

   public static String listToValueSeparatedString(List<String> list, String value) {
      return separateWith(list.toArray(new String[list.size()]), value);
   }

   public static String padWithLeadingZeroes(String toPad, int length) {
      char[] charArray = new char[20];
      int charArrayIndex = charArray.length - 1;
      for (int index = toPad.length() - 1; index >= 0; index--, charArrayIndex--) {
         charArray[charArrayIndex] = toPad.charAt(index);
      }
      for (; charArrayIndex >= 0; charArrayIndex--) {
         charArray[charArrayIndex] = '0';
      }
      return new String(charArray);
   }

   public static String truncate(String str, int length) {
      return str.substring(0, Math.min(length, str.length()));
   }
}
