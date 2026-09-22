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
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.token.DataRightsClassificationAttributeType;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Fallback-path tests for the loader plus refresher composition.
 * <p>
 * Unlike {@link RequiredIndicatorRefresherTest}, which injects a mock
 * {@link DataRightsFootersIndicatorLoader}, these tests exercise the behavior through the REAL
 * {@link DataRightsFootersIndicatorLoaderImpl} composed with a {@link RequiredIndicatorRefresherImpl}
 * and a fresh {@link DataRightsClassificationAttributeType}. The point of task 9 is to verify that
 * the real loader and refresher composition guarantees the absent/malformed footer fallback: a
 * missing artifact leaves the seed set governing, and a malformed footer value is skipped while its
 * valid siblings are still promoted. Only the {@link QueryBuilder} and {@link ArtifactReadable} are
 * mocked, standing in for the common branch read so no database is touched. Each test grows its own
 * fresh attribute type so the shared {@code CoreAttributeTypes.DataRightsClassification} singleton is
 * never mutated.
 *
 * @author David W. Miller
 */

public class RequiredIndicatorRefresherFallbackTest {

   /**
    * Builds a fresh {@link DataRightsClassificationAttributeType} whose valid enum set is seeded from
    * every current {@code RequiredIndicator} member. The identifier is a throwaway value; only the
    * seeded valid set matters for these tests.
    */

   private static DataRightsClassificationAttributeType freshAttributeType() {
      return new DataRightsClassificationAttributeType(123456789L, "Data Rights Classification", "",
         TaggerTypeToken.PlainTextTagger, MediaType.TEXT_PLAIN, NamespaceToken.OSEE);
   }

   /**
    * Builds a {@link QueryBuilder} mock whose {@code asArtifact()} throws, standing in for a missing or
    * unreadable {@code DataRightsFooters} artifact.
    */

   private static QueryBuilder queryWithMissingArtifact() {
      QueryBuilder queryBuilder = mock(QueryBuilder.class);
      when(queryBuilder.andId(any(ArtifactId.class))).thenReturn(queryBuilder);
      when(queryBuilder.asArtifact()).thenThrow(new IllegalStateException("missing"));
      return queryBuilder;
   }

   /**
    * Builds a {@link QueryBuilder} mock returning an artifact whose {@code GeneralStringData} values are
    * the supplied footer strings.
    */

   private static QueryBuilder queryReturning(List<String> footerValues) {
      QueryBuilder queryBuilder = mock(QueryBuilder.class);
      ArtifactReadable artifact = mock(ArtifactReadable.class);
      when(queryBuilder.andId(any(ArtifactId.class))).thenReturn(queryBuilder);
      when(queryBuilder.asArtifact()).thenReturn(artifact);
      when(artifact.<String> getAttributeValues(any(AttributeTypeToken.class))).thenReturn(footerValues);
      return queryBuilder;
   }

   /**
    * Requirements 9.1, 9.2: when the {@code DataRightsFooters} artifact is missing, the real loader
    * returns an empty set, so the refresher adds nothing and returns zero. The valid set must be
    * unchanged from its seed and {@code isValidEnum("Unspecified")} must remain true so publishing
    * still works with the compiled seed.
    */

   @Test
   public void testMissingArtifactLeavesSeedOnlyValidSet() {
      DataRightsClassificationAttributeType attributeType = freshAttributeType();
      Set<String> seedValidSet = new LinkedHashSet<>(attributeType.getEnumStrValues());

      RequiredIndicatorRefresher refresher =
         new RequiredIndicatorRefresherImpl(new DataRightsFootersIndicatorLoaderImpl(), attributeType);

      int added = refresher.refresh(queryWithMissingArtifact());

      Assert.assertEquals("a missing footers artifact must add no values", 0, added);
      Assert.assertEquals("the valid set must be unchanged from the seed when the artifact is missing", seedValidSet,
         new LinkedHashSet<>(attributeType.getEnumStrValues()));
      Assert.assertTrue("\"Unspecified\" must remain valid while the footers artifact is absent",
         attributeType.isValidEnum("Unspecified"));
   }

   /**
    * Requirement 9.3: when a footer value is malformed under the shared parser rule, the refresher must
    * skip it while still promoting its valid siblings. A single-line value contributes no name (the
    * split yields one part) and a blank-first-line value contributes no name (the trimmed first line is
    * empty); only the well-formed two-part value with a new name is added. No exception is thrown.
    */

   @Test
   public void testMalformedValueSkippedSiblingsAdded() {
      DataRightsClassificationAttributeType attributeType = freshAttributeType();

      List<String> footerValues = Arrays.asList(
         "MalformedNoFooter",
         "\nfooter content with blank name",
         "Custom Class\nfooter content");

      RequiredIndicatorRefresher refresher =
         new RequiredIndicatorRefresherImpl(new DataRightsFootersIndicatorLoaderImpl(), attributeType);

      int added = refresher.refresh(queryReturning(footerValues));

      Assert.assertEquals("only the well-formed sibling value must be added", 1, added);
      Assert.assertTrue("the well-formed footer name \"Custom Class\" must be valid after refresh",
         attributeType.isValidEnum("Custom Class"));
      Assert.assertFalse("a single-line malformed value must not become valid",
         attributeType.isValidEnum("MalformedNoFooter"));
   }

}
