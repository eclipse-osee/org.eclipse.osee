/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.handlers;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.db.internal.search.language.EnglishLanguage;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagEncoder;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.eclipse.osee.orcs.db.mocks.MockTagCollector;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the hybrid search query logic by verifying that the TagProcessor (used at query time in
 * AttributeTokenSqlHandler) produces correct FNV-1a hashes that would match what was stored during indexing.
 * <p>
 * This validates the contract between the indexing path and the query path: both use the same TagProcessor/TagEncoder
 * combination, so the hashes generated at query time must match those stored at index time.
 *
 * @author Roberto E. Escobar
 */
public class AttributeTokenSqlHandlerHybridQueryTest {

   private final TagProcessor tagProcessor = new TagProcessor(new EnglishLanguage(new MockLog()), new TagEncoder());

   /**
    * Verifies that tokenizing a search term produces non-empty tags that are consistent across invocations. This is the
    * core contract that makes the hybrid query work: the query handler tokenizes the search term at query time and the
    * resulting tags must match what was stored during indexing.
    */
   @Test
   public void testQueryTokenizationProducesConsistentTags() {
      List<Pair<String, Long>> tags1 = collectTags("hello world");
      List<Pair<String, Long>> tags2 = collectTags("hello world");

      Assert.assertFalse("Tokenization should produce tags", tags1.isEmpty());
      Assert.assertEquals("Tokenization should be deterministic", tags1.size(), tags2.size());
      for (int i = 0; i < tags1.size(); i++) {
         Assert.assertEquals(tags1.get(i).getFirst(), tags2.get(i).getFirst());
         Assert.assertEquals(tags1.get(i).getSecond(), tags2.get(i).getSecond());
      }
   }

   /**
    * Verifies that previously-colliding terms now produce different tags with FNV-1a encoding.
    */
   @Test
   public void testPreviouslyCollidingTermsProduceDifferentTags() {
      List<Pair<String, Long>> tw8Tags = collectTags("TW8");
      List<Pair<String, Long>> tw9Tags = collectTags("TW9");

      Assert.assertEquals(1, tw8Tags.size());
      Assert.assertEquals(1, tw9Tags.size());
      Assert.assertNotEquals("TW8 and TW9 should produce different query tags",
         tw8Tags.get(0).getSecond(), tw9Tags.get(0).getSecond());
   }

   /**
    * Verifies that CR0091 and CR1091 (which collided under the old encoder) now produce different tags.
    */
   @Test
   public void testCRNumberCollisionsResolved() {
      List<Pair<String, Long>> cr0091Tags = collectTags("CR0091");
      List<Pair<String, Long>> cr1091Tags = collectTags("CR1091");

      Assert.assertEquals(1, cr0091Tags.size());
      Assert.assertEquals(1, cr1091Tags.size());
      Assert.assertNotEquals("CR0091 and CR1091 should produce different query tags",
         cr0091Tags.get(0).getSecond(), cr1091Tags.get(0).getSecond());
   }

   /**
    * Verifies that multi-word search terms produce multiple tags (one per word after tokenization), which would be
    * combined with INTERSECT in the SQL query for external document search.
    */
   @Test
   public void testMultiWordSearchProducesMultipleTags() {
      List<Pair<String, Long>> tags = collectTags("system power test");

      // Each word should produce one tag after tokenization/singularization
      Assert.assertEquals("Expected one tag per word", 3, tags.size());

      // All tags should be unique
      Assert.assertNotEquals(tags.get(0).getSecond(), tags.get(1).getSecond());
      Assert.assertNotEquals(tags.get(1).getSecond(), tags.get(2).getSecond());
      Assert.assertNotEquals(tags.get(0).getSecond(), tags.get(2).getSecond());
   }

   /**
    * Verifies case insensitivity: the same word in different cases should produce the same tag at query time.
    */
   @Test
   public void testCaseInsensitiveQueryMatching() {
      List<Pair<String, Long>> lowerTags = collectTags("hello");
      List<Pair<String, Long>> upperTags = collectTags("HELLO");
      List<Pair<String, Long>> mixedTags = collectTags("Hello");

      Assert.assertEquals(1, lowerTags.size());
      Assert.assertEquals(1, upperTags.size());
      Assert.assertEquals(1, mixedTags.size());
      Assert.assertEquals("Case should not affect tag value",
         lowerTags.get(0).getSecond(), upperTags.get(0).getSecond());
      Assert.assertEquals("Case should not affect tag value",
         lowerTags.get(0).getSecond(), mixedTags.get(0).getSecond());
   }

   /**
    * Verifies that empty or whitespace-only search terms produce no tags (which would result in the handler using a
    * different SQL path).
    */
   @Test
   public void testEmptySearchProducesNoTags() {
      List<Pair<String, Long>> emptyTags = collectTags("");
      List<Pair<String, Long>> whitespaceTags = collectTags("   ");

      Assert.assertTrue("Empty search should produce no tags", emptyTags.isEmpty());
      Assert.assertTrue("Whitespace-only search should produce no tags", whitespaceTags.isEmpty());
   }

   /**
    * Verifies that the indexing path and query path produce identical tags for the same input, ensuring that indexed
    * documents will be found by queries.
    */
   @Test
   public void testIndexAndQueryPathsProduceSameTags() {
      // Simulate indexing: a document containing "power system test"
      List<Pair<String, Long>> indexTags = collectTags("power system test");

      // Simulate querying: user searches for "power system test"
      List<Pair<String, Long>> queryTags = collectTags("power system test");

      Assert.assertEquals(indexTags.size(), queryTags.size());
      for (int i = 0; i < indexTags.size(); i++) {
         Assert.assertEquals("Word mismatch at index " + i,
            indexTags.get(i).getFirst(), queryTags.get(i).getFirst());
         Assert.assertEquals("Tag mismatch at index " + i + " for word: " + indexTags.get(i).getFirst(),
            indexTags.get(i).getSecond(), queryTags.get(i).getSecond());
      }
   }

   private List<Pair<String, Long>> collectTags(String value) {
      List<Pair<String, Long>> tags = new ArrayList<>();
      TagCollector collector = new MockTagCollector(tags);
      tagProcessor.collectFromString(value, collector);
      return tags;
   }
}
