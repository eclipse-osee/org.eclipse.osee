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

/**
 * @author Jeff C. Phillips
 * @author Don Dunne
 * @author Karol M. Wilk
 */
public class Strings {
   private final static String EMPTY_STRING = "";

   public static boolean isValid(String value) {
      return value != null && value.length() > 0;
   }

   public static String emptyString() {
      return EMPTY_STRING;
   }

   /**
    * For adjusting '&' containing strings to break the accelerator(a.k.a) mnemonic feature some widgets offer. i.e.
    * &Test should make Alt+T a shortcut. This method breaks the accelerator by doubling ampersands.
    * 
    * @param stringWithAmp
    * @return a string with doubled ampersands.
    */
   public static String ampersandInsteadOfAccelerator(String stringWithAmp) {
      if (isValid(stringWithAmp)) {
         return stringWithAmp.replace("&", "&&");
      } else {
         return null;
      }
   }

   public static String intern(String str) {
      if (str == null) {
         return null;
      }
      return str.intern();
   }

   /**
    * Will truncate string if necessary and add "..." to end if addDots and truncated
    */
   public static String truncate(String value, int length, boolean addDots) {
      if (value == null) {
         return "";
      }
      String toReturn = value;
      if (Strings.isValid(value) && value.length() > length) {
         int len = (addDots && length - 3 > 0 ? length - 3 : length);
         toReturn = value.substring(0, Math.min(length, len)) + (addDots ? "..." : "");
      }
      return toReturn;
   }

   public static String truncate(String value, int length) {
      return truncate(value, length, false);
   }

}
