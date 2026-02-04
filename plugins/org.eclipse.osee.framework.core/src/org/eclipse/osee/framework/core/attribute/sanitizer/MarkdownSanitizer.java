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
package org.eclipse.osee.framework.core.attribute.sanitizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Jaden W. Puckett
 */

public class MarkdownSanitizer extends TextSanitizer {

   /**
    * Checks if the given text contains Markdown bold symbols, such as '**'.
    */
   public static boolean containsMarkdownBolds(String text) {
      return text.contains("**");
   }

   /**
    * Removes Markdown bold symbols from the given text, such as '**'.
    */
   public static String removeMarkdownBolds(String text) {
      text = text.replace("**", "");
      return text;
   }

   private static final String[] BASE_PATTERNS = {
      "ConfigurationGroup\\[.*?\\]",
      "Configuration\\[.*?\\]",
      "Feature\\[.*?\\]",
      "End ConfigurationGroup",
      "End Configuration(?!Group)",
      "End Feature",
      "ConfigurationGroup Else",
      "Configuration(?!Group) Else",
      "Feature Else"};

   private static final class Rule {
      final Pattern pattern;
      final String replacement;

      Rule(Pattern pattern, String replacement) {
         this.pattern = pattern;
         this.replacement = replacement;
      }
   }

   private static final List<Rule> RULES = buildRules();

   private static List<Rule> buildRules() {
      List<Rule> rules = new ArrayList<>(BASE_PATTERNS.length * 3);
      for (String base : BASE_PATTERNS) {
         rules.add(new Rule(Pattern.compile("(?<!``)(" + base + ")(?!``)"), "``$1``"));
         rules.add(new Rule(Pattern.compile("``(" + base + ")(?!``)"), "``$1``"));
         rules.add(new Rule(Pattern.compile("(?<!``)(" + base + ")``"), "``$1``"));
      }
      return Collections.unmodifiableList(rules);
   }

   /**
    * Enforces that all specified patterns have `` before and after them.
    */
   public static String enforceProperDoubleBacktickSyntaxForFeatureConfigConfigGroupTags(String input) {
      for (Rule rule : RULES) {
         input = rule.pattern.matcher(input).replaceAll(rule.replacement);
      }
      return input;
   }
}