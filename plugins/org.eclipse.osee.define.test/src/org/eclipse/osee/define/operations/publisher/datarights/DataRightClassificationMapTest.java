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
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.publishing.DataRight;
import org.eclipse.osee.framework.core.publishing.PublishingOutputFormatter;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests asserting that {@link DataRightClassificationMap#create} behavior is unchanged after it
 * was refactored to derive classification names through the shared
 * {@code DataRightsClassificationNameParser}. These tests live in the same package as
 * {@link DataRightClassificationMap} because the class and its {@code create}/{@code get} methods are
 * package-private; the fragment host {@code org.eclipse.osee.define} makes them visible here without a
 * visibility change.
 * <p>
 * The database read is stubbed with Mockito so the tests run without a live data store: the
 * {@link QueryBuilder} returns a mocked {@link ArtifactReadable} whose
 * {@code getAttributeValues(GeneralStringData)} yields the raw multi-line footer values the parser
 * consumes. Content for a value is the remainder after the first line, matching
 * {@code value.split("\n", 2)[1].trim()}.
 *
 * @author David W. Miller
 */

public class DataRightClassificationMapTest {

   /**
    * Builds a {@link PublishingOutputFormatter} whose {@code getDataRightsMappingArtifact} returns a
    * throwaway token; the query is stubbed to ignore the id, so the specific token is irrelevant.
    */

   private static PublishingOutputFormatter mockFormatter() {
      PublishingOutputFormatter formatter = mock(PublishingOutputFormatter.class);
      ArtifactToken mappingArtifact = mock(ArtifactToken.class);
      when(formatter.getDataRightsMappingArtifact()).thenReturn(mappingArtifact);
      return formatter;
   }

   /**
    * Builds a {@link QueryBuilder} that resolves to an artifact whose {@code GeneralStringData} values
    * are the supplied footer strings.
    */

   private static QueryBuilder mockQueryReturningFooters(List<String> footerValues) {
      QueryBuilder queryBuilder = mock(QueryBuilder.class);
      ArtifactReadable artifact = mock(ArtifactReadable.class);

      when(queryBuilder.andId(any(ArtifactId.class))).thenReturn(queryBuilder);
      when(queryBuilder.asArtifact()).thenReturn(artifact);
      when(artifact.<String> getAttributeValues(any(AttributeTypeToken.class))).thenReturn(footerValues);

      return queryBuilder;
   }

   /**
    * Builds a {@link QueryBuilder} whose {@code asArtifact} throws, simulating a missing or unreadable
    * mapping artifact.
    */

   private static QueryBuilder mockQueryThrowingOnResolve() {
      QueryBuilder queryBuilder = mock(QueryBuilder.class);
      when(queryBuilder.andId(any(ArtifactId.class))).thenReturn(queryBuilder);
      when(queryBuilder.asArtifact()).thenThrow(new IllegalStateException("no mapping artifact"));
      return queryBuilder;
   }

   /**
    * A normal, well-formed footers artifact with several distinct classification names must produce a
    * map with one {@link DataRight} entry per name whose content is the footer text following the
    * first line.
    */

   @Test
   public void testNormalMultiValueFooterProducesEntriesWithCorrectContent() {
      List<String> footers = List.of(
         "Unspecified\n<ftr>unspecified footer content</ftr>",
         "Proprietary\n<ftr>proprietary footer content</ftr>",
         "Export Controlled\n<ftr>export controlled footer content</ftr>");

      DataRightClassificationMap map =
         DataRightClassificationMap.create(mockQueryReturningFooters(footers), mockFormatter());

      DataRight unspecified = map.get("Unspecified");
      Assert.assertNotNull("Expected a DataRight for the \"Unspecified\" classification.", unspecified);
      Assert.assertEquals("Unspecified classification name mismatch.", "Unspecified", unspecified.getClassification());
      Assert.assertEquals("Unspecified footer content mismatch.", "<ftr>unspecified footer content</ftr>",
         unspecified.getContent());

      DataRight proprietary = map.get("Proprietary");
      Assert.assertEquals("Proprietary classification name mismatch.", "Proprietary",
         proprietary.getClassification());
      Assert.assertEquals("Proprietary footer content mismatch.", "<ftr>proprietary footer content</ftr>",
         proprietary.getContent());

      DataRight exportControlled = map.get("Export Controlled");
      Assert.assertEquals("Export Controlled classification name mismatch.", "Export Controlled",
         exportControlled.getClassification());
      Assert.assertEquals("Export Controlled footer content mismatch.",
         "<ftr>export controlled footer content</ftr>", exportControlled.getContent());
   }

   /**
    * When the mapping artifact cannot be read, {@code create} must fall back to the unspecified map so
    * an unknown classification still resolves to the default unspecified footer.
    */

   @Test
   public void testMissingArtifactFallsBackToUnspecifiedMap() {
      DataRightClassificationMap map =
         DataRightClassificationMap.create(mockQueryThrowingOnResolve(), mockFormatter());

      DataRight unspecified = map.get(DataRightConfiguration.defaultClassification);
      Assert.assertNotNull("Missing artifact fallback must still resolve the unspecified classification.",
         unspecified);
      Assert.assertEquals("Fallback classification name mismatch.", DataRightConfiguration.defaultClassification,
         unspecified.getClassification());
      Assert.assertEquals("Fallback footer content must be the default unspecified value.",
         DataRightConfiguration.unspecifiedValue, unspecified.getContent());

      DataRight unknown = map.get("Some Classification Not In The Map");
      Assert.assertEquals("An unknown classification must resolve to the unspecified footer content.",
         DataRightConfiguration.unspecifiedValue, unknown.getContent());
   }

   /**
    * When the artifact yields no usable classification names (every value is malformed under the
    * parser rule), {@code create} must fall back to the unspecified map rather than an empty map.
    */

   @Test
   public void testEmptyResultFallsBackToUnspecifiedMap() {
      List<String> malformedOnly = List.of(
         "SingleLineNoFooterContent",
         "\n<ftr>blank first line contributes no name</ftr>");

      DataRightClassificationMap map =
         DataRightClassificationMap.create(mockQueryReturningFooters(malformedOnly), mockFormatter());

      DataRight unspecified = map.get(DataRightConfiguration.defaultClassification);
      Assert.assertNotNull("Empty-result fallback must still resolve the unspecified classification.", unspecified);
      Assert.assertEquals("Empty-result fallback classification name mismatch.",
         DataRightConfiguration.defaultClassification, unspecified.getClassification());
      Assert.assertEquals("Empty-result fallback footer content must be the default unspecified value.",
         DataRightConfiguration.unspecifiedValue, unspecified.getContent());
   }

   /**
    * When valid classification names are present but none is the unspecified default, {@code create}
    * must inject an unspecified entry so the default classification always resolves.
    */

   @Test
   public void testInjectsUnspecifiedDefaultWhenAbsent() {
      List<String> footersWithoutUnspecified = List.of(
         "Proprietary\n<ftr>proprietary footer content</ftr>",
         "Export Controlled\n<ftr>export controlled footer content</ftr>");

      DataRightClassificationMap map =
         DataRightClassificationMap.create(mockQueryReturningFooters(footersWithoutUnspecified), mockFormatter());

      DataRight proprietary = map.get("Proprietary");
      Assert.assertEquals("Parsed classification content must be preserved when the default is injected.",
         "<ftr>proprietary footer content</ftr>", proprietary.getContent());

      DataRight injectedUnspecified = map.get(DataRightConfiguration.defaultClassification);
      Assert.assertNotNull("An unspecified entry must be injected when the footers omit it.", injectedUnspecified);
      Assert.assertEquals("Injected classification name mismatch.", DataRightConfiguration.defaultClassification,
         injectedUnspecified.getClassification());
      Assert.assertEquals("Injected unspecified footer content must be the default unspecified value.",
         DataRightConfiguration.unspecifiedValue, injectedUnspecified.getContent());
   }

}
