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

package org.eclipse.osee.framework.core.enums.token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.RequiredIndicator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Randomized property test for Property 1 (seed always valid): for every current seed value
 * <code>s</code>, <code>isValidEnum(s)</code> is true independent of database state; in particular
 * <code>isValidEnum("Unspecified")</code> is always true.
 * <p>
 * No property-based library is on the module classpath, so this property is implemented as a
 * randomized JUnit 4 test. The generator source is the full current seed set itself
 * (<code>RequiredIndicator.values()</code>), which is count-agnostic. Ordering independence is
 * additionally exercised by repeatedly shuffling the member list with a fixed-seed
 * {@link java.util.Random} and re-checking every member; the fixed seed keeps failures reproducible
 * and is reported in the assertion message alongside the offending member.
 * <p>
 * "Independent of database state" holds by construction here: the seed is compiled and no database
 * is available in a unit test. The test is strictly read-only on the shared
 * {@link CoreAttributeTypes#DataRightsClassification} singleton; it never calls
 * {@code addDbLoadedValidEnum} or otherwise mutates the singleton, so it does not pollute other
 * tests and does not fabricate database state.
 *
 * @author David W. Miller
 */
public class DataRightsClassificationSeedPropertyTest {

   /**
    * Fixed seed so shuffles are reproducible; reported in assertion messages so a failing ordering
    * can be reconstructed.
    */

   private static final long RANDOM_SEED = 20250101L;

   /**
    * Number of shuffled iterations over the full seed set. Each iteration re-checks every member in
    * a different order to exercise ordering independence.
    */

   private static final int ITERATIONS = 200;

   /**
    * The footer default classification name. {@code DataRightConfiguration.defaultClassification} is
    * package-private in the {@code org.eclipse.osee.define} bundle and not visible here, so the
    * literal it is defined as ("Unspecified") is asserted directly.
    */

   private static final String DEFAULT_CLASSIFICATION = "Unspecified";

   /**
    * Property 1 (seed always valid): iterate the full current seed set (count-agnostic) as the
    * generator source and, for many seeded-random orderings, assert every seed member's display name
    * is a valid enum on {@code DataRightsClassification}. This demonstrates the property holds
    * independent of iteration order and repeated checks. <br>
    * <b>Validates: Requirements 1.2, 1.3</b>
    */

   @Test
   public void testSeedAlwaysValidAcrossRandomOrderings() {
      RequiredIndicator[] members = RequiredIndicator.values();
      Assert.assertTrue("The compiled RequiredIndicator seed set must be non-empty", members.length > 0);

      List<RequiredIndicator> order = new ArrayList<>(members.length);
      for (RequiredIndicator member : members) {
         order.add(member);
      }

      Random random = new Random(RANDOM_SEED);

      for (int iteration = 0; iteration < ITERATIONS; iteration++) {
         Collections.shuffle(order, random);

         for (RequiredIndicator member : order) {
            String displayName = member.getDisplayName();
            Assert.assertTrue("Property 1 (seed always valid) violated: seed member '" + member.name()
               + "' (display name '" + displayName + "') was not a valid enum on DataRightsClassification"
               + " [random seed " + RANDOM_SEED + ", iteration " + iteration + "]",
               CoreAttributeTypes.DataRightsClassification.isValidEnum(displayName));
         }
      }
   }

   /**
    * Property 1 special case: {@code isValidEnum("Unspecified")} is always true. Re-checked across the
    * same iteration count to confirm repeated evaluation never changes the result. <br>
    * <b>Validates: Requirements 1.3</b>
    */

   @Test
   public void testUnspecifiedAlwaysValid() {
      for (int iteration = 0; iteration < ITERATIONS; iteration++) {
         Assert.assertTrue("Property 1 special case violated: '" + DEFAULT_CLASSIFICATION
            + "' was not a valid enum on DataRightsClassification [iteration " + iteration + "]",
            CoreAttributeTypes.DataRightsClassification.isValidEnum(DEFAULT_CLASSIFICATION));
      }
   }
}
