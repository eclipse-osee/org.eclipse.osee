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

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.RequiredIndicator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Verifies that the {@code DataRightsClassification} attribute type reports every compiled
 * {@link RequiredIndicator} seed member as a valid enum independent of database state, and that the
 * attribute default classification name ({@code "Unspecified"}) is a valid seed value. These
 * assertions read only from the shared {@link CoreAttributeTypes#DataRightsClassification} singleton;
 * they never mutate it, so they are safe to run against the process-wide instance.
 *
 * @author David W. Miller
 */
public class DataRightsClassificationSeedTest {

   /**
    * The footer default classification name. {@code DataRightConfiguration.defaultClassification} is
    * package-private in the {@code org.eclipse.osee.define} bundle and not visible here, so the
    * literal it is defined as ("Unspecified") is asserted directly and cross-checked against
    * {@link RequiredIndicator#UNSPECIFIED}.
    */

   private static final String DEFAULT_CLASSIFICATION = "Unspecified";

   /**
    * Requirement 1.2: every compiled seed member must be a valid enum value independent of database
    * state. Iterates the full current seed set count-agnostically.
    */

   @Test
   public void testEverySeedMemberIsValidEnum() {
      for (RequiredIndicator member : RequiredIndicator.values()) {
         String displayName = member.getDisplayName();
         Assert.assertTrue(
            "Seed member '" + member.name() + "' (display name '" + displayName
               + "') must be a valid enum on DataRightsClassification independent of database state",
            CoreAttributeTypes.DataRightsClassification.isValidEnum(displayName));
      }
   }

   /**
    * Requirement 1.3: "Unspecified" must be a valid enum value independent of database state.
    */

   @Test
   public void testUnspecifiedIsValidEnum() {
      Assert.assertTrue("'" + DEFAULT_CLASSIFICATION + "' must be a valid enum on DataRightsClassification",
         CoreAttributeTypes.DataRightsClassification.isValidEnum(DEFAULT_CLASSIFICATION));
   }

   /**
    * Requirements 1.3 and 2.1: the attribute default token name must equal the footer default
    * classification. Asserts the {@link RequiredIndicator#UNSPECIFIED} display name equals the
    * default classification literal and that the token is present and valid in the seed set.
    */

   @Test
   public void testDefaultClassificationConsistency() {
      Assert.assertEquals("RequiredIndicator.UNSPECIFIED display name must equal the default classification",
         DEFAULT_CLASSIFICATION, RequiredIndicator.UNSPECIFIED.getDisplayName());

      Assert.assertNotNull("The UNSPECIFIED enum token must be present as the attribute default seed",
         RequiredIndicator.UNSPECIFIED.getEnumToken());

      Assert.assertTrue("The default classification token must be a valid enum on DataRightsClassification",
         CoreAttributeTypes.DataRightsClassification.isValidEnum(RequiredIndicator.UNSPECIFIED.getDisplayName()));
   }
}
