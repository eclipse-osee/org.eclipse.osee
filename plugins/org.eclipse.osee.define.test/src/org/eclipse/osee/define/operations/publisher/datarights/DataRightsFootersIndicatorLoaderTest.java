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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link DataRightsFootersIndicatorLoaderImpl}.
 * <p>
 * The loader reads the {@code DataRightsFooters} artifact's {@code GeneralStringData} values from the
 * common branch and maps each through the shared classification-name parser. These tests use Mockito
 * to stand in for the {@link QueryBuilder} and {@link ArtifactReadable} so the parsing and collection
 * behavior can be exercised without a database.
 *
 * @author David W. Miller
 */

public class DataRightsFootersIndicatorLoaderTest {

   private final DataRightsFootersIndicatorLoader loader = new DataRightsFootersIndicatorLoaderImpl();

   private static QueryBuilder queryReturning(List<String> footerValues) {
      QueryBuilder queryBuilder = mock(QueryBuilder.class);
      ArtifactReadable artifact = mock(ArtifactReadable.class);
      when(queryBuilder.andId(any(ArtifactId.class))).thenReturn(queryBuilder);
      when(queryBuilder.asArtifact()).thenReturn(artifact);
      when(artifact.<String> getAttributeValues(any(AttributeTypeToken.class))).thenReturn(footerValues);
      return queryBuilder;
   }

   /**
    * Requirement 3.3: a missing or unreadable {@code DataRightsFooters} artifact must yield an empty
    * set instead of throwing, so callers fall back to the compiled seed set.
    */

   @Test
   public void testMissingArtifactReturnsEmptySet() {
      QueryBuilder queryBuilder = mock(QueryBuilder.class);
      when(queryBuilder.andId(any(ArtifactId.class))).thenReturn(queryBuilder);
      when(queryBuilder.asArtifact()).thenThrow(new IllegalStateException("missing"));

      Set<String> names = this.loader.loadClassificationNames(queryBuilder);

      Assert.assertNotNull("loader must never return null", names);
      Assert.assertTrue("missing artifact must yield an empty set, but was " + names, names.isEmpty());
   }

   /**
    * Requirement 3.2: for footer values that yield two parts, the loader returns exactly the distinct
    * trimmed first lines. Duplicate names are de-duplicated and leading/trailing whitespace on the
    * first line is trimmed.
    */

   @Test
   public void testMultiValueArtifactReturnsDistinctTrimmedFirstLines() {
      List<String> footerValues = Arrays.asList(
         "Proprietary\nfooter content for proprietary",
         "  Restricted Rights  \nfooter content for restricted",
         "Proprietary\ndifferent footer content, same name",
         "Export Controlled ITAR\nfooter content for itar");

      Set<String> names = this.loader.loadClassificationNames(queryReturning(footerValues));

      Set<String> expected =
         new LinkedHashSet<>(Arrays.asList("Proprietary", "Restricted Rights", "Export Controlled ITAR"));

      Assert.assertEquals("loader must return the distinct trimmed first lines", expected, names);
   }

   /**
    * Requirement 3.4: single-line values (no footer content), blank-first-line values, and otherwise
    * malformed values must be skipped without throwing. A mix of valid and malformed values must yield
    * only the valid trimmed names.
    */

   @Test
   public void testSingleLineBlankAndMalformedValuesAreSkipped() {
      List<String> footerValues = Arrays.asList(
         "Proprietary\nfooter content",
         "SingleLineNoNewline",
         "\nfooter content with blank name",
         "   \nfooter content, whitespace name",
         "Restricted Rights\nfooter content");

      Set<String> names = this.loader.loadClassificationNames(queryReturning(footerValues));

      Set<String> expected = new LinkedHashSet<>(Arrays.asList("Proprietary", "Restricted Rights"));

      Assert.assertEquals("only valid two-part values must contribute names", expected, names);
   }

   /**
    * Requirement 3.4: an artifact with no {@code GeneralStringData} values contributes no names and
    * must not throw.
    */

   @Test
   public void testEmptyValueListReturnsEmptySet() {
      Set<String> names = this.loader.loadClassificationNames(queryReturning(Arrays.asList()));

      Assert.assertNotNull("loader must never return null", names);
      Assert.assertTrue("an artifact with no values must yield an empty set, but was " + names, names.isEmpty());
   }

}
