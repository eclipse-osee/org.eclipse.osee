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
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.TagProcessor;
import org.eclipse.osee.framework.search.engine.utility.XmlTextInputStream;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class TestTagProcessor extends TestCase {

   private Map<String, TestData<URL, URL>> getTestTagData() {
      Map<String, TestData<URL, URL>> toReturn = new LinkedHashMap<String, TestData<URL, URL>>();
      Bundle bundle = Activator.getInstance().getBundleContext().getBundle();
      Enumeration<?> urls = bundle.findEntries("data", "*.*", true);
      while (urls.hasMoreElements()) {
         URL url = (URL) urls.nextElement();
         String name = getFileName(url.getPath());
         if (Strings.isValid(name) && (url.getPath().endsWith(".data.xml") || url.getPath().endsWith(".tags.txt"))) {
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

   private void checkValue(int currentCount, Scanner expectedTags, String test, String word, Long codedTag) {
      try {
         if (expectedTags.hasNext()) {
            assertEquals(String.format("Line: [%d] Test: [%s] word: [%s]", currentCount, test, word),
                  expectedTags.next(), word);
            assertEquals(String.format("Line: [%d] Test: [%s] word: [%s]", currentCount, test, word),
                  expectedTags.nextLong(), codedTag.longValue());
         } else {
            assertTrue(String.format("Line: [%d] Test: [%s] word: [%s] tag: [%d] -- Extra Tag Found", currentCount,
                  test, word, codedTag), false);
         }
      } catch (Exception ex) {
         System.out.println(String.format("%s %s", word, codedTag));
      }
   }

   public void testTagFromInputStream() throws IOException {
      Map<String, TestData<URL, URL>> testMap = getTestTagData();
      for (final String key : testMap.keySet()) {
         TestData<URL, URL> testData = testMap.get(key);

         InputStream dataStream = null;
         InputStream expectedStream = null;
         try {
            dataStream = new BufferedInputStream(testData.data.openStream());
            expectedStream = new BufferedInputStream(testData.expected.openStream());
            final Scanner expectedTags = new Scanner(expectedStream, "UTF-8");

            Scanner sourceScanner = new Scanner(new XmlTextInputStream(dataStream));
            final MutableInteger count = new MutableInteger(0);
            TagProcessor.collectFromScanner(sourceScanner, new ITagCollector() {
               @Override
               public void addTag(String word, Long codedTag) {
                  checkValue(count.getValueAndInc(), expectedTags, key, word, codedTag);
               }
            });
         } finally {
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

   private String getFileName(String name) {
      int index = name.lastIndexOf("/");
      if (index > -1) {
         name = name.substring(index + 1, name.length());
      }
      if (name.endsWith(".data.xml")) {
         name = name.substring(0, name.length() - 9);
      }
      if (name.endsWith(".tags.txt")) {
         name = name.substring(0, name.length() - 9);
      }
      return name;
   }

   private boolean isDataFile(String name) {
      return name != null && name.endsWith(".data.xml");
   }

   private boolean isExpectedFile(String name) {
      return name != null && name.endsWith(".tags.txt");
   }

   private class TestData<K, V> {
      private K data;
      private V expected;
   }

   public void testTagWordFile() throws IOException {
      // This is here to be able to look at tags generated from xml file source
      Bundle bundle = Activator.getInstance().getBundleContext().getBundle();
      URL url = bundle.getEntry("data/test5.data.xml");
      InputStream dataStream = null;
      try {
         dataStream = new XmlTextInputStream(new BufferedInputStream(url.openStream()));
         TagProcessor.collectFromInputStream(dataStream, new ITagCollector() {
            @Override
            public void addTag(String word, Long codedTag) {
               System.out.println("Word: [" + word + "] Tag: [" + codedTag + "]");
            }
         });
      } finally {
         dataStream.close();
      }
   }
}
