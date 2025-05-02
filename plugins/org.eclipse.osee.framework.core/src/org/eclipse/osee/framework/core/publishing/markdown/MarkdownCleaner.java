/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.publishing.markdown;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility to clean Markdown
 * 
 * @author Jaden W. Puckett
 */

public class MarkdownCleaner {

   /**
    * Checks if the given text contains any special characters. The method searches for a range of special characters
    * that are commonly used for hidden or special formatting, such as non-breaking spaces, zero-width spaces, en
    * dashes, and curly quotes. The method uses a precompiled regular expression pattern to match any of the special
    * characters within the provided text. Special characters checked include:
    * <ul>
    * <li>Non-breaking spaces (\u00A0)</li>
    * <li>Zero-width spaces (\u200B, \u200C, \u200D)</li>
    * <li>Various spaces and invisible characters (\u2003, \u2002, \u2009, \u200A, \u205F, \u3000)</li>
    * <li>Left-to-right and right-to-left marks (\u200E, \u200F)</li>
    * <li>Bidirectional text formatting characters (\u202A, \u202B, \u202C, \u202D, \u202E, \u2066, \u2067, \u2068,
    * \u2069)</li>
    * <li>Object replacement character (\uFFFC)</li>
    * <li>Zero-width no-break space (\uFEFF)</li>
    * <li>Curly apostrophe (\u2019)</li>
    * <li>En dash (\u2013)</li>
    * <li>Left and right double quotation marks (\u201C, \u201D)</li>
    * </ul>
    *
    * @param text the string to check for special characters
    * @return true if the text contains any special characters; false otherwise
    */
   public static boolean containsSpecialCharacters(String text) {
      // Define a pattern that matches any of the special characters
      Pattern specialCharsPattern = Pattern.compile(
         "[\u00A0\u200B\u200C\u200D\u2003\u2002\u2009\u200A\u205F\u3000\u200E\u200F\u202A\u202B\u202C\u202D\u202E\u2066\u2067\u2068\u2069\uFFFC\uFEFF\u2019\u2013\u201C\u201D]");

      // Use the pattern to check if the text contains any special character
      return specialCharsPattern.matcher(text).find();
   }

   /**
    * Removes special characters from the given text, replacing them with designated characters. This method processes a
    * predefined set of special characters and replaces them as follows:
    * <ul>
    * <li>Right single quotation mark (U+2019) with an apostrophe (')</li>
    * <li>En dash (U+2013) with a hyphen (-)</li>
    * <li>Left double quotation mark (U+201C) and right double quotation mark (U+201D) with a double quote (")</li>
    * </ul>
    * All other special characters are replaced with a regular space. The method uses a map to define specific
    * replacements and iterates through a list of special characters to apply these replacements to the provided text.
    * Example:
    * 
    * <pre>
    * String input = "This is a sample text with special characters: \u2019 \u2013 \u201C \u201D.";
    * String result = removeSpecialCharacters(input);
    * // result: This is a sample text with special characters: ' - " "
    * </pre>
    *
    * @param text the string from which special characters will be removed
    * @return the modified string with special characters replaced according to the defined rules
    */
   public static String removeSpecialCharacters(String text) {
      // Define a map of special characters to their replacements
      Map<Character, Character> replacementMap = new HashMap<>();
      replacementMap.put('\u2019', '\''); // Right single quotation mark
      replacementMap.put('\u2013', '-'); // En dash
      replacementMap.put('\u201C', '"'); // Left double quotation mark
      replacementMap.put('\u201D', '"'); // Right double quotation mark

      // Other special characters are replaced with a regular space
      char[] specialChars = {
         '\u00A0',
         '\u200B',
         '\u200C',
         '\u200D',
         '\u2003',
         '\u2002',
         '\u2009',
         '\u200A',
         '\u205F',
         '\u3000',
         '\u200E',
         '\u200F',
         '\u202A',
         '\u202B',
         '\u202C',
         '\u202D',
         '\u202E',
         '\u2066',
         '\u2067',
         '\u2068',
         '\u2069',
         '\uFFFC',
         '\uFEFF',
         '\u2019',
         '\u2013',
         '\u201C',
         '\u201D'};

      // Replace special characters in the text
      for (char specialChar : specialChars) {
         char replacement = replacementMap.getOrDefault(specialChar, ' ');
         text = text.replace(specialChar, replacement);
      }

      return text;
   }

   /**
    * Checks if the given text contains Markdown bold symbols, such as '**'.
    *
    * @param text the text to check for Markdown bold symbols
    * @return true if the text contains Markdown bold symbols, false otherwise
    */
   public static boolean containsMarkdownBolds(String text) {
      return text.contains("**");
   }

   /**
    * Removes Markdown bold symbols from the given text, such as '**'.
    *
    * @param text the text to clean of Markdown bold symbols
    * @return the cleaned text with Markdown bold symbols removed
    */
   public static String removeMarkdownBolds(String text) {
      text = text.replace("**", "");
      return text;
   }
}