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

package org.eclipse.osee.framework.core.publishing;

import java.util.Optional;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

/**
 * Property test for {@link DataRightsClassificationNameParser}.
 * <p>
 * Property (shared-parser no-drift): for all footer values, the shared parser produces the same
 * classification-name result the original inline {@code split("\n", 2)} / {@code parts.length == 2}
 * logic produced in {@code DataRightClassificationMap.create}.
 * <p>
 * No property-based library is on the module classpath (see task 1 of the spec), so this property is
 * exercised as a randomized JUnit 4 table test driven by a seeded {@link Random}. The fixed seed
 * makes any failure reproducible; the seed and the offending generated input are included in the
 * assertion failure message so a counterexample can be reconstructed.
 * <p>
 * <b>Documented divergence.</b> The original inline logic contributed an EMPTY-STRING classification
 * name for a value whose first line is blank (it ran {@code name = parts[0].trim()} unconditionally
 * once {@code parts.length == 2}). The shared parser deliberately skips a blank first line and
 * contributes no name. This is the spec-sanctioned change covered by Requirement 3.4. To assert
 * parity for the intended semantics, the reference logic below treats an empty-string reference name
 * as "no name" ({@code Optional.empty()}) before comparing.
 *
 * @author David W. Miller
 */

public class DataRightsClassificationNameParserTest {

   /**
    * Fixed seed so a failing run is reproducible. If this test fails, rerun with this seed and the
    * reported iteration index to reconstruct the offending input.
    */

   private static final long SEED = 0x0FEEDDA7A5EED1L;

   private static final int ITERATIONS = 200;

   /**
    * Reference reproduction of the ORIGINAL inline logic from {@code DataRightClassificationMap.create}:
    *
    * <pre>
    * String[] parts = value.split("\n", 2);
    * if (parts.length == 2) {
    *    name = parts[0].trim();
    * }
    * </pre>
    *
    * The original contributed {@code parts[0].trim()} whenever the split yielded two parts, including
    * the empty string for a blank first line. Per the documented divergence, an empty-string result is
    * normalized to {@link Optional#empty()} here so parity holds for the intended semantics (blank
    * first line contributes no name, Requirement 3.4). A {@code null} value contributes no name in both
    * implementations.
    */

   private static Optional<String> referenceParse(String value) {
      if (value == null) {
         return Optional.empty();
      }
      String[] parts = value.split("\n", 2);
      if (parts.length != 2) {
         return Optional.empty();
      }
      String name = parts[0].trim();
      if (name.isEmpty()) {
         return Optional.empty();
      }
      return Optional.of(name);
   }

   /**
    * Generates a footer value across the categories the parser must handle: single-line, two-part,
    * blank first line, leading/trailing whitespace, embedded newlines, and noisy multi-line content.
    * An occasional {@code null} is produced to cover the null-tolerance path.
    */

   private static String generateFooterValue(Random random) {
      int category = random.nextInt(9);
      switch (category) {
         case 0:
            // null value
            return null;
         case 1:
            // single line, no newline (fails the two-part guard)
            return randomToken(random);
         case 2:
            // two-part: name on first line, footer content after
            return randomToken(random) + "\n" + randomToken(random);
         case 3:
            // blank first line (empty), footer content after
            return "\n" + randomToken(random);
         case 4:
            // whitespace-only first line, footer content after
            return randomWhitespace(random) + "\n" + randomToken(random);
         case 5:
            // leading/trailing whitespace around a real name, footer content after
            return randomWhitespace(random) + randomToken(random) + randomWhitespace(random) + "\n" + randomToken(
               random);
         case 6:
            // embedded newlines: multiple lines of footer content after the name
            return randomToken(random) + "\n" + randomToken(random) + "\n" + randomToken(random) + "\n" + randomToken(
               random);
         case 7:
            // trailing newline only (name then empty footer content)
            return randomToken(random) + "\n";
         default:
            // fully arbitrary short string that may or may not contain newlines
            return randomArbitrary(random);
      }
   }

   private static String randomToken(Random random) {
      int length = random.nextInt(6);
      StringBuilder builder = new StringBuilder();
      for (int index = 0; index < length; index++) {
         // printable ASCII excluding newline, biased toward letters and spaces
         char character = (char) ('a' + random.nextInt(26));
         if (random.nextInt(4) == 0) {
            character = ' ';
         }
         builder.append(character);
      }
      return builder.toString();
   }

   private static String randomWhitespace(Random random) {
      int length = random.nextInt(4);
      StringBuilder builder = new StringBuilder();
      char[] whitespace = new char[] {' ', '\t'};
      for (int index = 0; index < length; index++) {
         builder.append(whitespace[random.nextInt(whitespace.length)]);
      }
      return builder.toString();
   }

   private static String randomArbitrary(Random random) {
      int length = random.nextInt(12);
      StringBuilder builder = new StringBuilder();
      for (int index = 0; index < length; index++) {
         int choice = random.nextInt(10);
         if (choice == 0) {
            builder.append('\n');
         } else if (choice == 1) {
            builder.append(' ');
         } else if (choice == 2) {
            builder.append('\t');
         } else {
            builder.append((char) ('A' + random.nextInt(26)));
         }
      }
      return builder.toString();
   }

   /**
    * <b>Validates: Requirements 4.2, 4.3, 4.4</b>
    * <p>
    * For every generated footer value the shared parser must produce the same classification-name
    * result as the reference reproduction of the original inline logic (with the documented
    * blank-first-line divergence normalized).
    */

   @Test
   public void testSharedParserNoDrift() {
      Random random = new Random(SEED);
      for (int iteration = 0; iteration < ITERATIONS; iteration++) {
         String value = generateFooterValue(random);

         Optional<String> expected = referenceParse(value);
         Optional<String> actual = DataRightsClassificationNameParser.parseClassificationName(value);

         String message = String.format(
            "shared-parser no-drift mismatch [seed=0x%XL iteration=%d] for value=%s : expected=%s actual=%s", SEED,
            iteration, describe(value), expected, actual);
         Assert.assertEquals(message, expected, actual);
      }
   }

   /**
    * <b>Validates: Requirements 4.2, 4.3</b>
    * <p>
    * {@link DataRightsClassificationNameParser#parseClassificationName(String)} must return exactly
    * the classification of the footer produced by
    * {@link DataRightsClassificationNameParser#parseFooter(String)} for the same value, so the name
    * and content can never be derived from different rules.
    */

   @Test
   public void testParseFooterNameMatchesParseClassificationName() {
      Random random = new Random(SEED);
      for (int iteration = 0; iteration < ITERATIONS; iteration++) {
         String value = generateFooterValue(random);

         Optional<String> name = DataRightsClassificationNameParser.parseClassificationName(value);
         Optional<DataRightsClassificationNameParser.DataRightsFooter> footer =
            DataRightsClassificationNameParser.parseFooter(value);

         String message = String.format("parseFooter/parseClassificationName mismatch [seed=0x%XL iteration=%d] for value=%s",
            SEED, iteration, describe(value));
         Assert.assertEquals(message, name.isPresent(), footer.isPresent());
         if (footer.isPresent()) {
            Assert.assertEquals(message, name.get(), footer.get().getClassification());
         }
      }
   }

   /**
    * <b>Validates: Requirements 4.2, 4.3</b>
    * <p>
    * When a footer yields a result, its content must be the trimmed remainder after the first line,
    * matching the single split the parser performs.
    */

   @Test
   public void testParseFooterContentIsTrimmedRemainder() {
      Optional<DataRightsClassificationNameParser.DataRightsFooter> footer =
         DataRightsClassificationNameParser.parseFooter("Proprietary\n  <ftr>content</ftr>  \nmore  ");

      Assert.assertTrue("expected a parsed footer", footer.isPresent());
      Assert.assertEquals("Proprietary", footer.get().getClassification());
      Assert.assertEquals("<ftr>content</ftr>  \nmore", footer.get().getContent());
   }

   /**
    * Renders a value for the failure message with visible escapes so embedded newlines and whitespace
    * in a counterexample are legible.
    */

   private static String describe(String value) {
      if (value == null) {
         return "<null>";
      }
      return "\"" + value.replace("\\", "\\\\").replace("\n", "\\n").replace("\t", "\\t") + "\"";
   }

}
