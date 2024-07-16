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

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * {@link StringsTest}
 *
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 * @author Karol M. Wilk
 * @author Loren K. Ashley
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
    * Predicate to determine if a {@link String} is non-<code>null</code> and not empty.
    *
    * @param value the {@link String} to test.
    * @return <code>true</code>, when the {@link String} is non-<code>null</code> and not empty; otherwise,
    * <code>false</code>.
    */

   public static boolean isValid(String value) {
      return (value != null) && !value.isEmpty();
   }

   /**
    * Predicate to determine if a {@link String} is non-<code>null</code> and not blank.
    *
    * @param value the {@link String} to test.
    * @return <code>true</code>, when the {@link String} is non-<code>null</code> and not blank; otherwise,
    * <code>false</code>.
    */

   public static boolean isValidAndNonBlank(String value) {
      return (value != null) && !value.isBlank();
   }

   /**
    * Predicate to determine if an array of {@link CharSequece} objects are all non-<code>null</code> and not empty.
    *
    * @param values the array of {@link CharSequence} implementations to test.
    * @return <code>true</code>, when the array is non-<code>null</code>, has a length greater than or equal to 1, all
    * array elements are non-<code>null</code>, and all the {@link CharSequence} implementations have a length greater
    * than or equal to one; otherwise <code>false</code>.
    */

   public static boolean isValid(CharSequence... values) {

      if ((values == null) || (values.length == 0)) {
         return false;
      }

      for (CharSequence value : values) {
         if (value == null || value.length() == 0) {
            return false;
         }
      }

      return true;
   }

   /**
    * Predicate to determine if a {@link String} is <code>null</code> or empty.
    *
    * @param value the {@link String} to test.
    * @return <code>true</code>, when the {@link String} is <code>null</code> or empty; otherwise, <code>false</code>.
    */

   public static boolean isInvalid(String value) {
      return (value == null) || value.isEmpty();
   }

   /**
    * Predicate to determine if a {@link String} is <code>null</code> or blank.
    *
    * @param value the {@link String} to test.
    * @return <code>true</code>, when the {@link String} is <code>null</code> or blank; otherwise, <code>false</code>.
    */

   public static boolean isInvalidOrBlank(String value) {
      return (value == null) || value.isBlank();
   }

   /**
    * Predicate to determine if any array elements are <code>null</code> or contain an {@link CharSequence}
    * implementation that is empty.
    *
    * @param values the array of {@link CharSequence} implementations to test.
    * @return <code>false</code>, when the array is non-<code>null</code>, has a length greater than or equal to 1, all
    * array elements are non-<code>null</code>, and all the {@link CharSequence} implementations have a length greater
    * than or equal to one; otherwise <code>true</code>.
    */

   public static boolean isInvalid(CharSequence... values) {

      if ((values == null) || (values.length == 0)) {
         return true;
      }

      for (CharSequence value : values) {
         if ((value == null) || (value.length() == 0)) {
            return true;
         }
      }

      return false;
   }

   public static String intern(String str) {
      return (str == null) ? null : str.intern();
   }

   public static String emptyString() {
      return EMPTY_STRING;
   }

   public static boolean notEquals(String tested, String... tests) {

      if ((tested == null) && (((tests == null) || (tests.length == 0)))) {
         //both are empty
         return false;
      }

      if (tested == null) {
         //only tested is empty
         return true;
      }

      if ((tests == null) || (tests.length == 0)) {
         //only tests are empty
         return true;
      }

      for (int i = 0; i < tests.length; i++) {
         var toTest = tests[i];
         if (toTest == null) {
            //only toTest string is null
            return true;
         }

         if (tested.compareTo(toTest) != 0) {
            return true;
         }
      }
      return false;
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
    * Gets the length of the {@link CharSequence} when non-<code>null</code>; otherwise, zero.
    *
    * @param input the {@link CharSequnce} to get the length of.
    * @return when <code>input</code> is non-<code>null</code>, the {@link CharSequence} length; otherwise, zero.
    */

   public static int saferLength(CharSequence input) {
      return Strings.isValid(input) ? input.length() : 0;
   }

   /**
    * Replaces every subsequence of the <code>inputStr</code> that matches the <code>target</code> regular expression
    * with the <code>replacement</code>. If the <code>inputStr</code> is <code>null</code> or empty; the
    * <code>inputStr</code> is returned with out processing.
    *
    * @param inputStr the {@link String} to be processed.
    * @param target the regular express for the sub-sequence to search for.
    * @param replacement the replacement {@link String} for matches of <code>target</code>.
    * @return modified, new version of <code>inputStr</code> or <code>inputStr</code> if it is not valid.
    * @throws PatternSyntaxException when <code>target</code> is not a valid regular expression.
    * @throws NullPointerException when <code>target</code> or <code>replacement</code> are <code>null</code>.
    */

   public static String saferReplace(String inputStr, String target, String replacement) {
      return isValid(inputStr) ? inputStr.replaceAll(target, replacement) : inputStr;
   }

   /**
    * Replaces every subsequence of the <code>input</code> sequence that matches the <code>pattern</code> with the
    * <code>replacement</code>. If the <code>input</code> is <code>null</code> or empty; and the <code>pattern</code> is
    * <code>null</code>, the <code>input</code> as a string is returned when <code>input</code> is
    * non-<code>null</code>; otherwise <code>null</code> is returned. If the <code>pattern</code> is <code>null</code>,
    * the replacement is made with the empty string.
    *
    * @param input the {@link CharSequence} to be processed.
    * @param pattern the search {@link Pattern}.
    * @param replacement the replacement {@link String}.
    * @return when all parameters are valid, a {@link String} with the subsequence matched by the <code>pattern</code>
    * in the <code>input</code> {@link CharSequence} replaced with the <code>replacement</code> {@link String};
    * otherwise, the <code>input</code> parameter is returned unmodified.
    */

   public static String totallySaferReplace(CharSequence input, Pattern pattern, String replacement) {
      //@formatter:off
      return
         ( Strings.isInvalid( input ) || Objects.isNull( pattern ) )
            ? Objects.isNull( input ) ? null : input.toString()
            : pattern.matcher(input).replaceAll( Objects.nonNull( replacement ) ? replacement : "" );
       //@formatter:on
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

   public static boolean isPrintable(String str) {
      return str.equals(removeNonPrintableCharacters(str));
   }

   public static String removeNonPrintableCharacters(String str) {
      // strips off all non-ASCII characters
      str = str.replaceAll("[^\\x00-\\x7F]", "");

      // erases all the ASCII control characters
      str = str.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

      // removes non-printable characters from Unicode
      str = str.replaceAll("\\p{C}", "");

      return str.trim();
   }

   public static boolean isPrintable(char c) {
      Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
      return (!Character.isISOControl(c)) && block != null && block != Character.UnicodeBlock.SPECIALS;
   }

   /**
    * @return all but alpha numeric and replace spaces underscores
    */
   public static String removeAllButAlphaNumeric(String str) {
      str = str.replaceAll(" ", "_");
      // Remove all characters except alpha-numeric and underscores
      str = str.replaceAll("[^a-zA-Z0-9_]", "");
      return str;
   }

   public static boolean isNotNumeric(String idStr) {
      return !isNumeric(idStr);
   }

   public static boolean containsIgnoreCase(Collection<String> validValues, String val) {
      for (String validValue : validValues) {
         if (validValue.equalsIgnoreCase(val)) {
            return true;
         }
      }
      return false;
   }
}