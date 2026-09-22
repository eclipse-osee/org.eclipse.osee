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

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.RequiredIndicator;
import org.eclipse.osee.framework.core.publishing.RequiredIndicator.RequiredIndicatorEnum;
import org.junit.Assert;
import org.junit.Test;

/**
 * Static-initialization guard for the compiled data rights classification seed (Requirements 1.4
 * and 1.5).
 * <p>
 * These are core types with no orcs dependency. This unit-test JVM has no {@code OrcsApi} / JDBC
 * service bound, so there is no database wired at all. That environment is what makes the assertions
 * meaningful rather than trivial: if class-load / static initialization of any of these types
 * required a database, it would fail (throw) here, and if the {@code .any(DataRightsClassification,
 * ...)} default resolution needed a database it could not be read here. Successful static init plus
 * successful seed resolution therefore demonstrate that class-load performs no database access
 * (Requirement 1.4) and that the attribute-type default resolves from the compiled seed
 * (Requirement 1.5).
 * <p>
 * All assertions are strictly read-only on the shared
 * {@link CoreAttributeTypes#DataRightsClassification} singleton; none call
 * {@code addDbLoadedValidEnum} or otherwise mutate it, so this test does not pollute other tests.
 * Installing a fake JDBC layer or bytecode instrumentation is intentionally out of scope (fragile);
 * the no-service test JVM is the guard.
 *
 * @author David W. Miller
 */
public class DataRightsClassificationStaticInitTest {

   /**
    * The footer default classification name. {@code DataRightConfiguration.defaultClassification} is
    * package-private in the {@code org.eclipse.osee.define} bundle and not visible here, so the
    * literal it is defined as ("Unspecified") is asserted directly and cross-checked against
    * {@link RequiredIndicator#UNSPECIFIED}.
    */

   private static final String DEFAULT_CLASSIFICATION = "Unspecified";

   /**
    * Requirement 1.4: loading {@link RequiredIndicator},
    * {@link DataRightsClassificationAttributeType}, and {@link CoreArtifactTypes} performs no
    * database access. Static initialization is forced explicitly via
    * {@code Class.forName(name, true, loader)} (the {@code true} triggers static init). Because this
    * unit-test JVM has no orcs/JDBC service bound, a static initializer that required a database
    * would throw here; completing without throwing demonstrates the absence of a class-load database
    * dependency.
    */

   @Test
   public void testStaticInitCompletesWithoutDatabaseAccess() throws Exception {
      ClassLoader classLoader = DataRightsClassificationStaticInitTest.class.getClassLoader();

      String[] typeNames = {
         "org.eclipse.osee.framework.core.publishing.RequiredIndicator",
         "org.eclipse.osee.framework.core.enums.token.DataRightsClassificationAttributeType",
         "org.eclipse.osee.framework.core.enums.CoreArtifactTypes"};

      for (String typeName : typeNames) {
         try {
            Class.forName(typeName, true, classLoader);
         } catch (Throwable throwable) {
            Assert.fail("Static initialization of '" + typeName
               + "' must complete without a database in the unit-test JVM (Requirement 1.4), but it threw: "
               + throwable);
         }
      }
   }

   /**
    * Requirement 1.5 support: the compiled seed is usable with no database. Every
    * {@link RequiredIndicator} member display name resolves as a valid enum on
    * {@code DataRightsClassification} (count-agnostic), and {@code "Unspecified"} is valid. These
    * come entirely from the compiled seed since no database is available.
    */

   @Test
   public void testCompiledSeedResolvesWithoutDatabase() {
      Assert.assertTrue("'" + DEFAULT_CLASSIFICATION + "' must be a valid enum on DataRightsClassification"
         + " resolved from the compiled seed with no database", CoreAttributeTypes.DataRightsClassification.isValidEnum(
            DEFAULT_CLASSIFICATION));

      RequiredIndicator[] members = RequiredIndicator.values();
      Assert.assertTrue("The compiled RequiredIndicator seed set must be non-empty", members.length > 0);

      for (RequiredIndicator member : members) {
         String displayName = member.getDisplayName();
         Assert.assertTrue("Seed member '" + member.name() + "' (display name '" + displayName
            + "') must resolve as a valid enum on DataRightsClassification from the compiled seed with no database",
            CoreAttributeTypes.DataRightsClassification.isValidEnum(displayName));
      }
   }

   /**
    * Requirement 1.5: the {@code .any(DataRightsClassification,
    * RequiredIndicator.UNSPECIFIED.getEnumToken())} default declared on the
    * {@link CoreArtifactTypes#Controlled} artifact type resolves from the compiled seed with no
    * database access.
    * <p>
    * The recorded default is read through the clean accessor
    * {@link ArtifactTypeToken#getAttributeDefault(org.eclipse.osee.framework.core.data.AttributeTypeGeneric)}:
    * {@code Controlled.getAttributeDefault(DataRightsClassification)} returns the exact
    * {@link RequiredIndicatorEnum} token passed to {@code .any(...)}. Its name is asserted to equal
    * {@code "Unspecified"} ({@code == RequiredIndicator.UNSPECIFIED.getDisplayName()}). Reading this
    * default succeeds with no database, which is only possible because the default is the compiled
    * seed token rather than a database-loaded value.
    */

   @Test
   public void testControlledDefaultResolvesFromCompiledSeed() {
      RequiredIndicatorEnum defaultToken =
         CoreArtifactTypes.Controlled.getAttributeDefault(CoreAttributeTypes.DataRightsClassification);

      Assert.assertNotNull("The Controlled artifact type must carry a compiled default for DataRightsClassification"
         + " (the .any(DataRightsClassification, UNSPECIFIED) declaration)", defaultToken);

      Assert.assertEquals("The DataRightsClassification default token name recorded by .any(...) must equal '"
         + DEFAULT_CLASSIFICATION + "', resolved from the compiled seed with no database (Requirement 1.5)",
         DEFAULT_CLASSIFICATION, defaultToken.getName());

      Assert.assertEquals("The recorded default must equal RequiredIndicator.UNSPECIFIED display name",
         RequiredIndicator.UNSPECIFIED.getDisplayName(), defaultToken.getName());

      Assert.assertTrue("The default token name must itself be a valid compiled-seed enum resolvable with no database",
         CoreAttributeTypes.DataRightsClassification.isValidEnum(defaultToken.getName()));
   }
}
