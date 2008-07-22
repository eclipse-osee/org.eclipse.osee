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
package org.eclipse.osee.framework.search.engine.test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.search.engine.utility.WordsUtil;
import org.eclipse.osee.framework.search.engine.utility.XmlTextInputStream;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class TestWordsUtil extends TestCase {

   private Map<String, String> getSingularToPluralData() {
      Map<String, String> testMap = new LinkedHashMap<String, String>();
      testMap.put("tries", "try");
      testMap.put("volcanoes", "volcano");
      testMap.put("geese", "goose");
      testMap.put("windows", "window");
      testMap.put("glasses", "glass");
      testMap.put("fishes", "fish");
      testMap.put("houses", "house");
      testMap.put("judges", "judge");
      testMap.put("dishes", "dish");
      testMap.put("phases", "phase");
      testMap.put("witches", "witch");
      testMap.put("baths", "bath");
      testMap.put("calves", "calf");
      testMap.put("lives", "life");
      testMap.put("proofs", "proof");
      testMap.put("boys", "boy");
      testMap.put("dwarfs", "dwarf");
      testMap.put("dwarves", "dwarf");
      testMap.put("hooves", "hoof");
      testMap.put("chairs", "chair");
      testMap.put("heroes", "hero");
      testMap.put("cantos", "canto");
      testMap.put("porticos", "portico");
      testMap.put("indeces", "index");
      testMap.put("leaves", "leaf");
      testMap.put("hello", "hello");
      testMap.put("axes", "axis");
      testMap.put("boxes", "box");
      testMap.put("foxes", "fox");
      testMap.put("species", "species");
      testMap.put("series", "series");
      testMap.put("status", "status");
      testMap.put("as", "as");
      testMap.put("appendeces", "appendix");
      return testMap;
   }

   public void testSingularToPlural() {
      Map<String, String> testMap = getSingularToPluralData();
      for (String key : testMap.keySet()) {
         String expected = testMap.get(key);
         String actual = WordsUtil.toSingular(key);
         assertEquals(String.format("Original: [%s] ", key), expected, actual);
      }
   }

   private Map<String, String> getStripPossessiveData() {
      Map<String, String> toReturn = new LinkedHashMap<String, String>();
      toReturn.put("don't", "don't");
      toReturn.put("yours", "yours");
      toReturn.put("Larry's", "Larry");
      toReturn.put("Joe's bag of tricks", "Joe bag of tricks");
      toReturn.put("What's that?", "What that?");
      toReturn.put("Company's books", "Company books");
      toReturn.put("Charles's cars.", "Charles cars.");
      toReturn.put("witches' brooms", "witches brooms");
      toReturn.put("babies' beds.", "babies beds.");
      toReturn.put("children's books", "children books");
      return toReturn;
   }

   public void testStripPossessive() throws UnsupportedEncodingException {
      Map<String, String> testMap = getStripPossessiveData();
      for (String key : testMap.keySet()) {
         StringBuilder builder = new StringBuilder();
         Scanner scanner = new Scanner(key);
         while (scanner.hasNext()) {
            if (builder.length() > 0) {
               builder.append(" ");
            }
            builder.append(WordsUtil.stripPossesive(scanner.next()));
         }
         assertEquals(String.format("Original: [%s] ", key), testMap.get(key), builder.toString());
      }
   }

   private Map<String, String[]> getSplitOnPuntucationData() {
      Map<String, String[]> toReturn = new LinkedHashMap<String, String[]>();
      toReturn.put("test.db.preset", new String[] {"test", "db", "preset"});
      toReturn.put("{what is this}", new String[] {"what", "is", "this"});
      toReturn.put("What is your name?", new String[] {"What", "is", "your", "name"});
      toReturn.put("Run!!", new String[] {"Run"});
      toReturn.put("don't", new String[] {"don't"});
      toReturn.put("hello", new String[] {"hello"});
      return toReturn;
   }

   public void testSplitOnPunctuation() {
      Map<String, String[]> testMap = getSplitOnPuntucationData();
      for (String key : testMap.keySet()) {
         String[] results = WordsUtil.splitOnPunctuation(key);
         assertEquals(String.format("Original: [%s] ", key), Arrays.deepToString(testMap.get(key)),
               Arrays.deepToString(results));
      }
   }

   private String getFileName(String name) {
      int index = name.lastIndexOf("/");
      if (index > -1) {
         name = name.substring(index + 1, name.length());
      }
      if (name.endsWith(".data.xml")) {
         name = name.substring(0, name.length() - 9);
      }
      if (name.endsWith(".expected.txt")) {
         name = name.substring(0, name.length() - 13);
      }
      return name;
   }

   private boolean isDataFile(String name) {
      return name != null && name.endsWith(".data.xml");
   }

   private boolean isExpectedFile(String name) {
      return name != null && name.endsWith(".expected.txt");
   }

   private Map<String, TestData<URL, URL>> getXmlMarkupRemovalData() {
      Map<String, TestData<URL, URL>> toReturn = new LinkedHashMap<String, TestData<URL, URL>>();

      Bundle bundle = Activator.getInstance().getBundleContext().getBundle();
      Enumeration<?> urls = bundle.findEntries("data", "*.*", true);
      while (urls.hasMoreElements()) {
         URL url = (URL) urls.nextElement();
         String name = getFileName(url.getPath());
         if (Strings.isValid(name) && (url.getPath().endsWith(".data.xml") || url.getPath().endsWith(".expected.txt"))) {
            TestData<URL, URL> pair = toReturn.get(name);
            if (pair == null) {
               pair = new TestData<URL, URL>();
               toReturn.put(name, pair);
            }
            if (isDataFile(url.getPath())) {
               pair.data = url;
            } else if (isExpectedFile(url.getPath())) {
               pair.expected = url;
            } else if (pair.data == null || pair.expected == null) {
               toReturn.remove(pair);
            }
         }
      }

      return toReturn;
   }

   //   public void testXmlMarkupRemovalScanner() throws IOException {
   //      Map<String, TestData<URL, URL>> testMap = getXmlMarkupRemovalData();
   //      for (String key : testMap.keySet()) {
   //         TestData<URL, URL> testData = testMap.get(key);
   //
   //         InputStream dataStream = null;
   //         InputStream expectedStream = null;
   //         Scanner scanner = null;
   //         try {
   //            dataStream = new BufferedInputStream(testData.data.openStream());
   //            expectedStream = new BufferedInputStream(testData.expected.openStream());
   //
   //            StringBuilder builder = new StringBuilder();
   //            scanner = WordsUtil.inputStreamToXmlTextScanner(dataStream);
   //            while (scanner.hasNext()) {
   //               String value = scanner.next();
   //               if (value.length() > 0) {
   //                  builder.append(value);
   //                  if (scanner.hasNext()) {
   //                     builder.append(" ");
   //                  }
   //               }
   //            }
   //
   //            String actual = builder.toString();
   //            String expected = Lib.inputStreamToString(expectedStream);
   //            assertEquals(String.format("Original: [%s] ", key), expected, actual);
   //         } finally {
   //            if (scanner != null) {
   //               scanner.close();
   //            }
   //            if (dataStream != null) {
   //               try {
   //                  dataStream.close();
   //               } catch (IOException ex) {
   //               }
   //            }
   //            if (expectedStream != null) {
   //               try {
   //                  expectedStream.close();
   //               } catch (IOException ex) {
   //               }
   //            }
   //         }
   //      }
   //   }

   public void testXmlMarkupRemovalStream() throws IOException {
      Map<String, TestData<URL, URL>> testMap = getXmlMarkupRemovalData();
      for (String key : testMap.keySet()) {
         TestData<URL, URL> testData = testMap.get(key);

         InputStream dataStream = null;
         InputStream expectedStream = null;
         Scanner scanner = null;
         try {
            dataStream = new XmlTextInputStream(new BufferedInputStream(testData.data.openStream()));
            expectedStream = new BufferedInputStream(testData.expected.openStream());

            StringBuilder builder = new StringBuilder();
            scanner = new Scanner(dataStream, "UTF-8");
            while (scanner.hasNext()) {
               String value = scanner.next();
               if (value.length() > 0) {
                  builder.append(value);
                  if (scanner.hasNext()) {
                     builder.append(" ");
                  }
               }
            }

            String actual = builder.toString();
            String expected = Lib.inputStreamToString(expectedStream);
            assertEquals(String.format("Original: [%s] ", key), expected, actual);
         } finally {
            if (scanner != null) {
               scanner.close();
            }
            if (dataStream != null) {
               try {
                  dataStream.close();
               } catch (IOException ex) {
               }
            }
            if (expectedStream != null) {
               try {
                  expectedStream.close();
               } catch (IOException ex) {
               }
            }
         }
      }
   }

   private class TestData<K, V> {
      private K data;
      private V expected;
   }
}
