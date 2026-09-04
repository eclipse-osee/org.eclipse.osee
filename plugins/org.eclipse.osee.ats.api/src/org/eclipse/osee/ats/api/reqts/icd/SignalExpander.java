/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.api.reqts.icd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stateless utility that expands compact signal expressions into concrete signal names.
 * <p>
 * Supported expansion patterns:
 * <ul>
 * <li>Numeric range: {@code (01..10)} expands to 01, 02, ..., 10 (zero-padded to match input width)</li>
 * <li>Alternation: {@code (PLT|CPG)} expands to PLT, CPG</li>
 * <li>Multiple alternation: {@code (F|B|L|R)} expands to F, B, L, R</li>
 * <li>Mixed: Multiple groups in one expression are expanded combinatorially</li>
 * </ul>
 * After expansion, each name is validated against the allowed-character rule {@code [A-Za-z0-9_.]}.
 *
 * @author Donald G. Dunne
 */
public final class SignalExpander {

   private static final Pattern ALLOWED_CHARS = Pattern.compile("[A-Za-z0-9_.]+");
   private static final Pattern GROUP_PATTERN = Pattern.compile("\\(([^)]+)\\)");
   private static final Pattern RANGE_PATTERN = Pattern.compile("^(\\d+)\\.\\.(\\d+)$");

   private SignalExpander() {
      // utility class
   }

   /**
    * Determines whether the raw signal contains expandable groups.
    */
   public static boolean isExpandable(String rawSignal) {
      return GROUP_PATTERN.matcher(rawSignal).find();
   }

   /**
    * Expands a raw signal expression into all concrete signal names. Returns a single-element list containing the
    * original signal if no expansion groups are found. Returns an empty list if expansion fails (malformed group).
    *
    * @param rawSignal the compact signal expression
    * @param errors list to which error messages are appended on failure
    * @return list of expanded concrete signal names (may contain invalid characters — caller should validate)
    */
   public static List<String> expand(String rawSignal, List<String> errors) {
      if (!isExpandable(rawSignal)) {
         return Collections.singletonList(rawSignal);
      }

      try {
         return expandGroups(rawSignal, errors);
      } catch (Exception ex) {
         errors.add("Failed to expand '" + rawSignal + "': " + ex.getMessage());
         return Collections.emptyList();
      }
   }

   /**
    * Validates that a signal name contains only allowed characters.
    */
   public static boolean isValidName(String signalName) {
      return ALLOWED_CHARS.matcher(signalName).matches();
   }

   private static List<String> expandGroups(String rawSignal, List<String> errors) {
      // Split the signal into literal segments and group segments
      Matcher matcher = GROUP_PATTERN.matcher(rawSignal);
      List<String> segments = new ArrayList<>();
      List<List<String>> expansions = new ArrayList<>();
      int lastEnd = 0;

      while (matcher.find()) {
         // Add literal text before this group
         if (matcher.start() > lastEnd) {
            String literal = rawSignal.substring(lastEnd, matcher.start());
            segments.add(literal);
            expansions.add(Collections.singletonList(literal));
         }

         String groupContent = matcher.group(1);
         List<String> groupExpansion = expandSingleGroup(groupContent, errors);
         if (groupExpansion.isEmpty()) {
            errors.add("Empty expansion for group '(" + groupContent + ")' in signal '" + rawSignal + "'");
            return Collections.emptyList();
         }
         segments.add(matcher.group());
         expansions.add(groupExpansion);
         lastEnd = matcher.end();
      }

      // Add trailing literal
      if (lastEnd < rawSignal.length()) {
         String literal = rawSignal.substring(lastEnd);
         segments.add(literal);
         expansions.add(Collections.singletonList(literal));
      }

      // Compute cartesian product of all expansion groups
      return cartesianProduct(expansions);
   }

   private static List<String> expandSingleGroup(String groupContent, List<String> errors) {
      // Check if it's a numeric range like 01..10
      Matcher rangeMatcher = RANGE_PATTERN.matcher(groupContent);
      if (rangeMatcher.matches()) {
         return expandRange(rangeMatcher.group(1), rangeMatcher.group(2), errors);
      }

      // Otherwise treat as alternation (pipe-separated)
      if (groupContent.contains("|")) {
         String[] alternatives = groupContent.split("\\|", -1);
         List<String> result = new ArrayList<>();
         for (String alt : alternatives) {
            if (alt.isEmpty()) {
               errors.add("Empty alternative in group '(" + groupContent + ")'");
               return Collections.emptyList();
            }
            result.add(alt);
         }
         return result;
      }

      // Single value in parens — not really expandable but valid
      return Collections.singletonList(groupContent);
   }

   private static List<String> expandRange(String startStr, String endStr, List<String> errors) {
      int padWidth = startStr.length();
      int start;
      int end;
      try {
         start = Integer.parseInt(startStr);
         end = Integer.parseInt(endStr);
      } catch (NumberFormatException ex) {
         errors.add("Invalid numeric range: " + startStr + ".." + endStr);
         return Collections.emptyList();
      }

      if (end < start) {
         errors.add("Range end (" + end + ") is less than start (" + start + ") in " + startStr + ".." + endStr);
         return Collections.emptyList();
      }

      if (end - start > 9999) {
         errors.add("Range too large: " + startStr + ".." + endStr + " (max 10000 values)");
         return Collections.emptyList();
      }

      List<String> result = new ArrayList<>();
      String format = "%0" + padWidth + "d";
      for (int i = start; i <= end; i++) {
         result.add(String.format(format, i));
      }
      return result;
   }

   private static List<String> cartesianProduct(List<List<String>> expansions) {
      List<String> result = new ArrayList<>();
      result.add("");

      for (List<String> group : expansions) {
         List<String> newResult = new ArrayList<>();
         for (String prefix : result) {
            for (String suffix : group) {
               newResult.add(prefix + suffix);
            }
         }
         result = newResult;
      }

      return result;
   }

}
