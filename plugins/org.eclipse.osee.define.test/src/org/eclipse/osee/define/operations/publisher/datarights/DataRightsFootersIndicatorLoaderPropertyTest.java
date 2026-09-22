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

package org.eclipse.osee.define.operations.publisher.datarights;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Property test for {@link DataRightsFootersIndicatorLoaderImpl} (Property 4, loader half).
 * <p>
 * Property (footer coverage, loader half): for every classification name derivable from a generated
 * footer value set, the loader returns that name, and it returns nothing else.
 * <p>
 * No property-based library is on the module classpath (see task 1 of the spec), so this property is
 * exercised as a randomized JUnit 4 table test driven by a seeded {@link Random}. The fixed seed
 * makes any failure reproducible; the seed and the offending generated inputs are included in the
 * assertion failure message so a counterexample can be reconstructed. Mockito stands in for the
 * {@link QueryBuilder} and {@link ArtifactReadable} so the property can be checked without a
 * database.
 *
 * @author David W. Miller
 */

public class DataRightsFootersIndicatorLoaderPropertyTest {

   private static final long SEED = 0x0DA7A21617445L;

   private static final int ITERATIONS = 200;

   private final DataRightsFootersIndicatorLoader loader = new DataRightsFootersIndicatorLoaderImpl();

   private static QueryBuilder queryReturning(List<String> footerValues) {
      QueryBuilder queryBuilder = mock(QueryBuilder.class);
      ArtifactReadable artifact = mock(ArtifactReadable.class);
      when(queryBuilder.andId(any(ArtifactId.class))).thenReturn(queryBuilder);
      when(queryBuilder.asArtifact()).thenReturn(artifact);
      when(artifact.<String> getAttributeValues(any(AttributeTypeToken.class))).thenReturn(footerValues);
      return queryBuilder;
   }

   private static String generateValidName(Random random) {
      StringBuilder builder = new StringBuilder();
      int length = 1 + random.nextInt(8);
      for (int index = 0; index < length; index++) {
         char character = (char) ('a' + random.nextInt(26));
         if (random.nextInt(5) == 0) {
            character = ' ';
         }
         builder.append(character);
      }
      builder.append((char) ('A' + random.nextInt(26)));
      return builder.toString();
   }

   private static String pad(Random random, String core) {
      return whitespace(random) + core + whitespace(random);
   }

   private static String whitespace(Random random) {
      int length = random.nextInt(3);
      StringBuilder builder = new StringBuilder();
      char[] whitespace = new char[] {' ', '\t'};
      for (int index = 0; index < length; index++) {
         builder.append(whitespace[random.nextInt(whitespace.length)]);
      }
      return builder.toString();
   }

   private static String randomFooterContent(Random random) {
      StringBuilder builder = new StringBuilder();
      int lines = 1 + random.nextInt(3);
      for (int line = 0; line < lines; line++) {
         if (line > 0) {
            builder.append('\n');
         }
         int length = random.nextInt(6);
         for (int index = 0; index < length; index++) {
            builder.append((char) ('a' + random.nextInt(26)));
         }
      }
      return builder.toString();
   }

   /**
    * <b>Validates: Requirements 3.2</b>
    * <p>
    * For a generated set of valid classification names embedded as two-part footer values, and
    * interleaved with noise (blank-first-line values, single-line values, duplicate names, and
    * whitespace-padded names), the loader must return exactly the distinct set of valid trimmed
    * names. The expected set is computed with the same rule the loader applies: a value contributes a
    * name only when it has two parts and a non-blank trimmed first line; names are de-duplicated.
    */

   @Test
   public void testLoaderFooterCoverage() {
      Random random = new Random(SEED);

      for (int iteration = 0; iteration < ITERATIONS; iteration++) {

         int nameCount = 1 + random.nextInt(6);
         List<String> baseNames = new ArrayList<>();
         for (int index = 0; index < nameCount; index++) {
            baseNames.add(generateValidName(random));
         }

         List<String> footerValues = new ArrayList<>();
         Set<String> expected = new LinkedHashSet<>();

         for (String name : baseNames) {
            String paddedName = pad(random, name);
            footerValues.add(paddedName + "\n" + randomFooterContent(random));
            expected.add(name.trim());

            switch (random.nextInt(4)) {
               case 0:
                  footerValues.add("\n" + randomFooterContent(random));
                  break;
               case 1:
                  footerValues.add(generateValidName(random));
                  break;
               case 2:
                  footerValues.add(whitespace(random) + "\n" + randomFooterContent(random));
                  break;
               default:
                  footerValues.add(paddedName + "\n" + randomFooterContent(random));
                  break;
            }
         }

         Collections.shuffle(footerValues, random);

         Set<String> actual = this.loader.loadClassificationNames(queryReturning(footerValues));

         String message = String.format(
            "loader footer-coverage mismatch [seed=0x%XL iteration=%d]%n  footerValues=%s%n  expected=%s%n  actual=%s",
            SEED, iteration, describe(footerValues), expected, actual);

         Assert.assertEquals(message, expected, actual);
      }
   }

   private static String describe(List<String> values) {
      StringBuilder builder = new StringBuilder("[");
      for (int index = 0; index < values.size(); index++) {
         if (index > 0) {
            builder.append(", ");
         }
         String value = values.get(index);
         builder.append(value == null ? "<null>" : "\"" + value.replace("\n", "\\n").replace("\t", "\\t") + "\"");
      }
      builder.append("]");
      return builder.toString();
   }

}
