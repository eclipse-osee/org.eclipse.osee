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

package org.eclipse.osee.orcs.db.internal.search.tagger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.db.mocks.MockTagCollector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link TagEncoder}
 *
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class TagEncoderTest {

   private final String toEncode;
   private final TagEncoder encoder;

   public TagEncoderTest(String toEncode) {
      this.toEncode = toEncode;
      this.encoder = new TagEncoder();
   }

   @Test
   public void testTagEncoderProducesOneTagPerWord() {
      List<Pair<String, Long>> actualTags = new ArrayList<>();
      encoder.encode(toEncode, new MockTagCollector(actualTags));
      // Hash-based encoder produces exactly one tag per word
      Assert.assertEquals("Expected exactly one tag for input: " + toEncode, 1, actualTags.size());
      Assert.assertEquals(toEncode, actualTags.get(0).getFirst());
      Assert.assertNotNull(actualTags.get(0).getSecond());
   }

   @Test
   public void testTagEncoderIsCaseInsensitive() {
      List<Pair<String, Long>> lowerTags = new ArrayList<>();
      List<Pair<String, Long>> upperTags = new ArrayList<>();
      encoder.encode(toEncode.toLowerCase(), new MockTagCollector(lowerTags));
      encoder.encode(toEncode.toUpperCase(), new MockTagCollector(upperTags));
      Assert.assertEquals("Case-insensitive encoding should produce same tag",
         lowerTags.get(0).getSecond(), upperTags.get(0).getSecond());
   }

   @Test
   public void testTagEncoderProducesUniqueHashes() {
      // Verify that similar strings produce different hashes (no collisions)
      TagEncoder enc = new TagEncoder();
      List<Pair<String, Long>> tags1 = new ArrayList<>();
      List<Pair<String, Long>> tags2 = new ArrayList<>();
      enc.encode("TW8", new MockTagCollector(tags1));
      enc.encode("TW9", new MockTagCollector(tags2));
      Assert.assertNotEquals("TW8 and TW9 should produce different tags",
         tags1.get(0).getSecond(), tags2.get(0).getSecond());

      List<Pair<String, Long>> tags3 = new ArrayList<>();
      List<Pair<String, Long>> tags4 = new ArrayList<>();
      enc.encode("CR0091", new MockTagCollector(tags3));
      enc.encode("CR1091", new MockTagCollector(tags4));
      Assert.assertNotEquals("CR0091 and CR1091 should produce different tags",
         tags3.get(0).getSecond(), tags4.get(0).getSecond());
   }

   @Test
   public void testTagEncoderEmptyInput() {
      List<Pair<String, Long>> actualTags = new ArrayList<>();
      encoder.encode("", new MockTagCollector(actualTags));
      Assert.assertEquals("Empty string should produce no tags", 0, actualTags.size());
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<>();
      data.add(new Object[] {"hello"});
      data.add(new Object[] {"world"});
      data.add(new Object[] {"TW8"});
      data.add(new Object[] {"TW9"});
      data.add(new Object[] {"CR0091"});
      data.add(new Object[] {"CR1091"});
      return data;
   }

}
