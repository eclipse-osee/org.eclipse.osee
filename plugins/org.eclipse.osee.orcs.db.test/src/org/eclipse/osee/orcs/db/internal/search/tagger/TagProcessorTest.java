/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.tagger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;
import org.eclipse.osee.orcs.db.internal.search.SearchAsserts;
import org.eclipse.osee.orcs.db.internal.search.language.EnglishLanguage;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.eclipse.osee.orcs.db.mocks.MockTagCollector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link TagProcessor}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class TagProcessorTest {

   private final TagProcessor tagProcessor;
   private final String rawData;
   private final String expectedParsed;
   private final List<Pair<String, Long>> expected;

   public TagProcessorTest(TagProcessor tagProcessor, String rawData, String expectedParsed, List<Pair<String, Long>> expected) {
      super();
      this.tagProcessor = tagProcessor;
      this.rawData = rawData;
      this.expectedParsed = expectedParsed;
      this.expected = expected;
   }

   @Test
   public void testCollectFromString() {
      List<Pair<String, Long>> actual = new ArrayList<>();
      TagCollector tagCollector = new MockTagCollector(actual);
      tagProcessor.collectFromString(expectedParsed, tagCollector);
      SearchAsserts.assertTagsEqual(expected, actual);
   }

   @Test
   public void testCollectFromInputStream() throws UnsupportedEncodingException {
      InputStream inputStream = null;
      try {
         inputStream = new XmlTextInputStream(rawData);
         List<Pair<String, Long>> actual = new ArrayList<>();
         TagCollector tagCollector = new MockTagCollector(actual);
         tagProcessor.collectFromInputStream(inputStream, tagCollector);
         SearchAsserts.assertTagsEqual(expected, actual);
      } finally {
         Lib.close(inputStream);
      }
   }

   @Test
   public void testCollectFromScanner() throws UnsupportedEncodingException {
      Scanner sourceScanner = null;
      try {
         sourceScanner = new Scanner(new XmlTextInputStream(rawData), "UTF-8");
         List<Pair<String, Long>> actual = new ArrayList<>();
         TagCollector tagCollector = new MockTagCollector(actual);
         tagProcessor.collectFromScanner(sourceScanner, tagCollector);
         SearchAsserts.assertTagsEqual(expected, actual);
      } finally {
         if (sourceScanner != null) {
            sourceScanner.close();
         }
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
         List<Pair<String, Long>> expected = loadExpected(name + ".tags.txt");
         data.add(new Object[] {tagProcess, rawData, expectedParsed, expected});
      }
      return data;
   }

   private static List<Pair<String, Long>> loadExpected(String resourceName) throws IOException {
      List<Pair<String, Long>> data = new ArrayList<>();
      String rawData = getResource(resourceName);
      String[] entries = rawData.split("\r?\n");
      for (String entry : entries) {
         String[] args = entry.split("\\s");
         data.add(new Pair<>(args[0], Long.valueOf(args[1])));
      }
      return data;
   }

   private static String getResource(String resourceName) throws IOException {
      return Lib.fileToString(TagProcessorTest.class, "data/" + resourceName);
   }
}
