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

import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link StringsTest}
 *
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 * @author Karol M. Wilk
 */
public class Strings {

   private static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?"); //match a number with optional '-' and decimal.
   private static final String AMP = "&";
   private static final String DBL_AMP = AMP + AMP;

   private static final String SPACE_COMMON = "\n|\t|\r|" + System.getProperty("line.separator");
   private static final String AND = "and";
   private static final String STR = "%s%s%s";
   private static final String QUOTE_STR = "\"";
   public static final String EMPTY_STRING = "";
   public static final Charset UTF_8 = Charset.forName("UTF-8");

   private Strings() {
      // Utility class
   }

   /**
    * OTE pre-compile dependency. Left for binary compatibility for 0.9.8
    */
   public static boolean isValid(String value) {
      return value != null && value.length() > 0;
   }

   public static String intern(String str) {
      return str == null ? null : str.intern();
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
      return saferReplace(stringWithAmp, AMP, DBL_AMP);
   }

   /**
    * <p>
    * Remove all <code>\n</code> and <code>\t</code>.
    * </p>
    */
   public static String minimize(String value) {
      return saferReplace(value, SPACE_COMMON, EMPTY_STRING);
   }

   /**
    * <b>NOTE</b> isValid() check is only applied to <code>inputStr</code>.
    *
    * @param inputStr string to be evaluated
    * @return modified, new version of <code>inputStr</code> or <code>inputStr</code> if it is not valid.
    */
   public static String saferReplace(String inputStr, String target, String replacement) {
      return isValid(inputStr) ? inputStr.replaceAll(target, replacement) : inputStr;
   }

   /**
    * Truncates at length, with no ellipsis.
    */
   public static String truncate(String value, int length) {
      return truncate(value, length, false);
   }

   /**
    * Trims ASCII <code>value</code> from end of <code>str</code> iff <b>found</b>.
    *
    * @param str "Requirement."
    * @param value of the character from the ASCII charset, i.e. <code>0x2E</code> for '.'
    * @return "Requirement"
    */
   public static String truncateEndChar(String str, int value) {
      if (isValid(str) && value == str.charAt(str.length() - 1)) {
         str = truncate(str, str.length() - 1);
      }
      return str;
   }

   /**
    * Will truncate string if necessary and add "..." to end if addDots and truncated
    */
   public static String truncate(String value, int length, boolean ellipsis) {
      if (!isValid(value)) {
         return emptyString();
      }

      String toReturn = value;
      if (value.length() > length) {
         int len = ellipsis && length - 3 > 0 ? length - 3 : length;
         toReturn = value.substring(0, Math.min(length, len)) + (ellipsis ? "..." : emptyString());
      }
      return toReturn;
   }

   public static String unquote(String nameReference) {
      return wrapWith(nameReference, QUOTE_STR, true);
   }

   public static String quote(String nameReference) {
      return wrapWith(nameReference, QUOTE_STR, false);
   }

   /**
    * Wrap <code>value</code> with <code>surroundStr</code>. <br/>
    * <b>NOTE</b> <code>value</code> will be trimmed of whitespace.
    *
    * @param value <code>A</code>
    * @param surroundStr <code>"</code>
    * @param unWrap reverse behavior. Takes out 2 * surroundStr.lenth() from value.length()
    * @return <code>"A"</code>
    */
   public static String wrapWith(String value, String surroundStr, boolean unWrap) {
      if (isValid(value)) {
         value = value.trim();
         if (unWrap) {
            if (value.startsWith(surroundStr) && value.endsWith(
               surroundStr) && 2 * surroundStr.length() < value.length()) {
               value = value.substring(surroundStr.length(), value.length() - surroundStr.length());
            }
         } else {
            value = String.format(STR, surroundStr, value, surroundStr);
         }
      }
      return value;
   }

   /**
    * @param items items to be joined in a sentence, i.e. <code>{A, B, C, D}</code>
    * @param joiningWord default joining word <code>" and "</code>
    * @return <code>A, B, C joiningWord D</code>
    */
   public static String buildStatment(List<?> items) {
      return buildStatement(items, AND);
   }

   /**
    * Provides a nicer list of items with an 'and' at the end. <br/>
    * TODO:This could be done using iterator().
    *
    * @param items Lists of form { apple, banana, orange } or { apple, banana }
    * @return string of form "apple, banana and orange" or "apple and banana" depending on size of list
    */
   public static String buildStatement(List<?> items, String joiningWord) {
      String statement = null;
      if (items != null) {
         StringBuilder niceList = new StringBuilder();
         if (items.size() >= 2) {
            int andIndex = items.size() - 2;
            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
               niceList.append(items.get(itemIndex));
               if (itemIndex == andIndex) {
                  niceList.append(' ');
                  niceList.append(joiningWord);
                  niceList.append(' ');
               } else if (itemIndex < andIndex) {
                  niceList.append(", ");
               }
            }
         } else {
            if (!items.isEmpty()) {
               niceList.append(items.get(0));
            }
         }
         statement = niceList.toString();
      }
      return statement;
   }

   /**
    * <p>
    * Capitalizes a String changing the first letter to title case as per {@link Character#toTitleCase(char)}. No other
    * letters are changed.
    * </p>
    * <p>
    * For a word based algorithm, see {@link WordUtils#capitalize(String)}. A <code>null</code> input String returns
    * <code>null</code>.
    * </p>
    *
    * <pre>
    * StringUtils.capitalize(null) = null
    * StringUtils.capitalize("") = ""
    * StringUtils.capitalize("cat") = "Cat"
    * StringUtils.capitalize("cAt") = "CAt"
    * </pre>
    *
    * @param str the String to capitalize, may be null
    * @return the capitalized String, <code>null</code> if null String input
    */
   public static String capitalize(String str) {
      int strLen;
      if (str == null || (strLen = str.length()) == 0) {
         return str;
      }
      return new StringBuffer(strLen).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
   }

   public static boolean isNumeric(String value) {
      boolean result = false;
      if (Strings.isValid(value)) {
         Matcher matcher = NUMERIC_PATTERN.matcher(value);
         result = matcher.matches();
      }
      return result;
   }

   public static boolean isInValid(String value) {
      return !isValid(value);
   }

}
