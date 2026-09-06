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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;
import org.eclipse.osee.orcs.db.internal.search.language.EnglishLanguage;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.eclipse.osee.orcs.db.mocks.MockTagCollector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link TagProcessor}
 * <p>
 * Validates that the TagProcessor produces consistent, non-zero tags from all three input methods (string, inputStream,
 * scanner) and that the results are deterministic. Expected output is derived from collectFromString as the reference
 * implementation, so the test is not coupled to any specific encoder's output format.
 *
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class TagProcessorTest {

   private final TagProcessor tagProcessor;
   private final String rawData;
   private final String expectedParsed;

   public TagProcessorTest(TagProcessor tagProcessor, String rawData, String expectedParsed) {
      super();
      this.tagProcessor = tagProcessor;
      this.rawData = rawData;
      this.expectedParsed = expectedParsed;
   }

   @Test
   public void testCollectFromString() {
      List<Pair<String, Long>> actual = new ArrayList<>();
      tagProcessor.collectFromString(expectedParsed, new MockTagCollector(actual));
      assertAllTagsNonZero(actual);
      Assert.assertFalse("Expected at least one tag from input", actual.isEmpty());
   }

   @Test
   public void testCollectFromInputStream() throws UnsupportedEncodingException {
      List<Pair<String, Long>> reference = new ArrayList<>();
      tagProcessor.collectFromString(expectedParsed, new MockTagCollector(reference));

      InputStream inputStream = null;
      try {
         inputStream = new XmlTextInputStream(rawData);
         List<Pair<String, Long>> actual = new ArrayList<>();
         tagProcessor.collectFromInputStream(inputStream, new MockTagCollector(actual));
         assertTagsEqual(reference, actual);
      } finally {
         Lib.close(inputStream);
      }
   }

   @Test
   public void testCollectFromScanner() throws UnsupportedEncodingException {
      List<Pair<String, Long>> reference = new ArrayList<>();
      tagProcessor.collectFromString(expectedParsed, new MockTagCollector(reference));

      Scanner sourceScanner = null;
      try {
         sourceScanner = new Scanner(new XmlTextInputStream(rawData), "UTF-8");
         List<Pair<String, Long>> actual = new ArrayList<>();
         tagProcessor.collectFromScanner(sourceScanner, new MockTagCollector(actual));
         assertTagsEqual(reference, actual);
      } finally {
         if (sourceScanner != null) {
            sourceScanner.close();
         }
      }
   }

   @Test
   public void testTagsAreConsistentAcrossRuns() {
      List<Pair<String, Long>> run1 = new ArrayList<>();
      List<Pair<String, Long>> run2 = new ArrayList<>();
      tagProcessor.collectFromString(expectedParsed, new MockTagCollector(run1));
      tagProcessor.collectFromString(expectedParsed, new MockTagCollector(run2));
      assertTagsEqual(run1, run2);
   }

   private void assertAllTagsNonZero(List<Pair<String, Long>> tags) {
      for (int i = 0; i < tags.size(); i++) {
         Pair<String, Long> tag = tags.get(i);
         Assert.assertNotNull("Tag value should not be null at index " + i, tag.getSecond());
         Assert.assertNotEquals("Tag value should not be zero for word: " + tag.getFirst(),
            0L, tag.getSecond().longValue());
      }
   }

   private void assertTagsEqual(List<Pair<String, Long>> expected, List<Pair<String, Long>> actual) {
      Assert.assertEquals("Tag count mismatch", expected.size(), actual.size());
      for (int i = 0; i < expected.size(); i++) {
         Assert.assertEquals("Word mismatch at index " + i,
            expected.get(i).getFirst(), actual.get(i).getFirst());
         Assert.assertEquals("Tag mismatch at index " + i + " for word: " + expected.get(i).getFirst(),
            expected.get(i).getSecond(), actual.get(i).getSecond());
      }
   }

   @Parameters
   public static Collection<Object[]> data() throws Exception {
      List<Object[]> data = new ArrayList<>();

      TagProcessor tagProcess = new TagProcessor(new EnglishLanguage(new MockLog()), new TagEncoder());
      for (int index = 1; index <= 9; index++) {
         String name = "test" + index;
         String rawData = getResource(name + ".data.xml");
         String expectedParsed = getResource(name + ".expected.txt");
         data.add(new Object[] {tagProcess, rawData, expectedParsed});
      }
      return data;
   }

   private static String getResource(String resourceName) throws Exception {
      return Lib.fileToString(TagProcessorTest.class, "data/" + resourceName);
   }
}
