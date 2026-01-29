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

import org.eclipse.osee.framework.core.attribute.cleaner.AttributeCleaner;

/**
 * Utility to clean Markdown
 *
 * @author Jaden W. Puckett
 */
public class MarkdownCleaner extends AttributeCleaner {

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

   /**
    * Enforces that all specified patterns have `` before and after them.
    *
    * @param input The input string to be cleaned.
    * @return The cleaned string with proper double backtick syntax for feature tags.
    */
   public static String enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(String input) {
      String[] patterns = {
         "ConfigurationGroup\\[.*?\\]",
         "Configuration\\[.*?\\]",
         "Feature\\[.*?\\]",
         "End ConfigurationGroup",
         "End Configuration(?!Group)",
         "End Feature",
         "ConfigurationGroup Else",
         "Configuration(?!Group) Else",
         "Feature Else"};

      // Loop through each pattern and ensure `` before and after
      for (String pattern : patterns) {
         // Match the pattern without `` before and after
         String regex = "(?<!``)(" + pattern + ")(?!``)";
         String replacement = "``$1``";

         // Replace all occurrences
         input = input.replaceAll(regex, replacement);

         // Match the pattern with `` before but not after
         regex = "``(" + pattern + ")(?!``)";
         replacement = "``$1``";
         input = input.replaceAll(regex, replacement);

         // Match the pattern with `` after but not before
         regex = "(?<!``)(" + pattern + ")``";
         replacement = "``$1``";
         input = input.replaceAll(regex, replacement);
      }

      return input;
   }

}