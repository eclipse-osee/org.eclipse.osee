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
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.token.DataRightsClassificationAttributeType;
import org.eclipse.osee.framework.core.publishing.RequiredIndicator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link RequiredIndicatorRefresherImpl}.
 * <p>
 * The refresher reads the classification names from the {@code DataRightsFooters} artifact with a
 * {@link DataRightsFootersIndicatorLoader} and promotes the names that are not already valid into the
 * valid enum set of the target attribute type. To keep the tests isolated they inject a mock loader
 * (so no database is touched) and a fresh {@link DataRightsClassificationAttributeType} seeded from
 * every {@link RequiredIndicator} member. The fresh instance is grown per test so the shared
 * {@code CoreAttributeTypes.DataRightsClassification} singleton is never mutated.
 *
 * @author David W. Miller
 */

public class RequiredIndicatorRefresherTest {

   /**
    * Builds a fresh {@link DataRightsClassificationAttributeType} whose valid enum set is seeded from
    * every current {@link RequiredIndicator} member. The identifier is a throwaway value; only the
    * seeded valid set matters for these tests. Each test grows its own instance so the shared
    * singleton is left untouched.
    */

   private static DataRightsClassificationAttributeType freshAttributeType() {
      return new DataRightsClassificationAttributeType(123456789L, "Data Rights Classification", "",
         TaggerTypeToken.PlainTextTagger, MediaType.TEXT_PLAIN, NamespaceToken.OSEE);
   }

   /**
    * Returns the compiled seed display names, that is every {@link RequiredIndicator} member's display
    * name. The fresh attribute type's initial valid set equals this set.
    */

   private static Set<String> seedNames() {
      return Stream.of(RequiredIndicator.values()).map(RequiredIndicator::getDisplayName).collect(
         Collectors.toCollection(LinkedHashSet::new));
   }

   private static RequiredIndicatorRefresher refresherReturning(AttributeTypeEnum<?> attributeType, Set<String> names) {
      DataRightsFootersIndicatorLoader loader = mock(DataRightsFootersIndicatorLoader.class);
      when(loader.loadClassificationNames(any())).thenReturn(names);
      return new RequiredIndicatorRefresherImpl(loader, attributeType);
   }

   /**
    * Requirements 5.1, 5.2, 5.5: footer-only classification names (names not already in the seed) must
    * be added to the valid set, the returned count must equal the number added, and each added name
    * must be reported as a valid enum value after the refresh.
    */

   @Test
   public void testAddsFooterOnlyNamesAndReturnsCount() {
      DataRightsClassificationAttributeType attributeType = freshAttributeType();
      Set<String> footerNames = new LinkedHashSet<>(Arrays.asList("Proprietary-New", "Custom Class"));

      RequiredIndicatorRefresher refresher = refresherReturning(attributeType, footerNames);

      int added = refresher.refresh(mock(QueryBuilder.class));

      Assert.assertEquals("refresh must return the count of footer-only names added", 2, added);
      Assert.assertTrue("footer name \"Proprietary-New\" must be valid after refresh",
         attributeType.isValidEnum("Proprietary-New"));
      Assert.assertTrue("footer name \"Custom Class\" must be valid after refresh",
         attributeType.isValidEnum("Custom Class"));
   }

   /**
    * Requirement 5.3: the refresh is additive only, so every compiled seed display name must still be
    * reported as a valid enum value after footer-only names are added.
    */

   @Test
   public void testPreservesSeed() {
      DataRightsClassificationAttributeType attributeType = freshAttributeType();
      Set<String> footerNames = new LinkedHashSet<>(Arrays.asList("Proprietary-New", "Custom Class"));

      RequiredIndicatorRefresher refresher = refresherReturning(attributeType, footerNames);
      refresher.refresh(mock(QueryBuilder.class));

      for (String seedName : seedNames()) {
         Assert.assertTrue("seed name \"" + seedName + "\" must remain valid after refresh",
            attributeType.isValidEnum(seedName));
      }
   }

   /**
    * Requirement 5.4: a second refresh with unchanged footer content must add nothing and return zero,
    * while the first refresh returns the number of newly added names.
    */

   @Test
   public void testSecondCallWithSameContentAddsZero() {
      DataRightsClassificationAttributeType attributeType = freshAttributeType();
      Set<String> footerNames = new LinkedHashSet<>(Arrays.asList("Proprietary-New", "Custom Class"));

      RequiredIndicatorRefresher refresher = refresherReturning(attributeType, footerNames);

      int firstAdded = refresher.refresh(mock(QueryBuilder.class));
      int secondAdded = refresher.refresh(mock(QueryBuilder.class));

      Assert.assertEquals("first refresh must add the two footer-only names", 2, firstAdded);
      Assert.assertEquals("second refresh with unchanged content must add zero", 0, secondAdded);
   }

   /**
    * Requirement 5.5: every classification name present in the footers must be reported as a valid enum
    * value after refresh, including any name that overlapped the compiled seed.
    */

   @Test
   public void testFooterDefinedNamesBecomeValid() {
      DataRightsClassificationAttributeType attributeType = freshAttributeType();
      Set<String> footerNames =
         new LinkedHashSet<>(Arrays.asList("Unspecified", "Proprietary-New", "Custom Class"));

      RequiredIndicatorRefresher refresher = refresherReturning(attributeType, footerNames);
      refresher.refresh(mock(QueryBuilder.class));

      for (String footerName : footerNames) {
         Assert.assertTrue("footer-defined name \"" + footerName + "\" must be valid after refresh",
            attributeType.isValidEnum(footerName));
      }
   }

   /**
    * Requirements 5.1, 5.3: a loaded name that is already a seed name must not be counted as added and
    * must not create a duplicate. "Unspecified" is a guaranteed seed name, so a footer set that
    * includes it plus one new name must report exactly one addition.
    */

   @Test
   public void testExistingSeedNameIsNotCountedAsAdded() {
      DataRightsClassificationAttributeType attributeType = freshAttributeType();

      Assert.assertTrue("\"Unspecified\" must be a seed name before refresh",
         attributeType.isValidEnum("Unspecified"));

      Set<String> footerNames = new LinkedHashSet<>(Arrays.asList("Unspecified", "Custom Class"));

      RequiredIndicatorRefresher refresher = refresherReturning(attributeType, footerNames);
      int added = refresher.refresh(mock(QueryBuilder.class));

      Assert.assertEquals("a footer name equal to a seed name must not be counted as added", 1, added);
      Assert.assertTrue("the genuinely new footer name must be valid after refresh",
         attributeType.isValidEnum("Custom Class"));
   }

}
