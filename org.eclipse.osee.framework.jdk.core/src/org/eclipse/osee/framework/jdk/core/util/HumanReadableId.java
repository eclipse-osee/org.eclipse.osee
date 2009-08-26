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

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author Ryan D. Brooks
 */
public final class HumanReadableId {
   /**
    * 5 character human readable identifier where the first and last characters are in the range [A-Z0-9] except 'I' and
    * 'O' and the middle three characters have the same range as above with the additional restrictions of 'A', 'E', 'U'
    * thus the total number of unique values is: 34 * 31 * 31 *31 * 34 = 34,438,396
    */
   private static final char[][] chars =
         new char[][] {
               {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
                     'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'},
               {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
                     'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'}};
   private static final int[] charsIndexLookup = new int[] {0, 1, 1, 1, 0};
   private static final int SEARCH_SPACE_SIZE = 34 * 31 * 31 * 31 * 34;
   private static final Pattern HRID_PATTERN = Pattern.compile(constructHridPattern());
   private static int rawHrid;

   public static String generate() {
      StringBuffer textHrid = new StringBuffer();

      rawHrid = new Random().nextInt(SEARCH_SPACE_SIZE);
      for (int i = 0; i < getHridLength(); i++) {
         textHrid.append(generateCharForPos(i));
      }
      return textHrid.toString();
   }

   private static int getHridLength() {
      return charsIndexLookup.length;
   }

   private static char generateCharForPos(int pos) {
      char[] possibleChars = getCharsValidForPos(pos);
      int radix = possibleChars.length;

      char returnChar = possibleChars[rawHrid % radix];
      rawHrid = rawHrid / radix;

      return returnChar;
   }

   private static char[] getCharsValidForPos(int pos) {
      return chars[charsIndexLookup[pos]];
   }

   public static boolean isValid(String hrid) {
      return HRID_PATTERN.matcher(hrid).matches();
   }

   private static String constructHridPattern() {
      StringBuilder pattern = new StringBuilder();
      for (int i = 0; i < getHridLength(); i++) {
         pattern.append(getRegexForPosition(i));
      }
      return pattern.toString();
   }

   private static String getRegexForPosition(int pos) {
      return "[" + Arrays.toString(getCharsValidForPos(pos)) + "]";
   }
}
