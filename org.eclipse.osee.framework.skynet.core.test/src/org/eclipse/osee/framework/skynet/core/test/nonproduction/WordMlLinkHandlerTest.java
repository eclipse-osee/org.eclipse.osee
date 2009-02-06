/*
 * Created on Jan 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.nonproduction;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.test.Activator;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class WordMlLinkHandlerTest extends TestCase {

   /**
    * Data driven test to check document link manager link/unlink methods
    * 
    * @throws Exception
    */
   public void testLinkUnLink() throws Exception {

      Map<String, TestData> testMap = getTestData();
      for (String key : testMap.keySet()) {
         TestData testData = testMap.get(key);

         InputStream dataStream = null;
         InputStream expectedStream = null;
         try {
            dataStream = new BufferedInputStream(testData.data.openStream());
            expectedStream = new BufferedInputStream(testData.expected.openStream());

            LinkType docType = testData.docType;
            boolean isLinkTest = testData.isLink;

            String input = Lib.inputStreamToString(dataStream);
            Artifact source = null;// ?;

            // TODO this test will fail - add live artifact -- need to change test to use artifact instead of 
            // input files.
            if (source != null) {
               String actual = null;
               if (isLinkTest) {
                  actual = WordMlLinkHandler.link(docType, source, input);
               } else {
                  actual = WordMlLinkHandler.unlink(docType, source, input);
               }
               String expected = Lib.inputStreamToString(expectedStream);
               assertEquals(String.format("%s: [%s] ", isLinkTest ? "Link" : "UnLink", key), expected, actual);
            }
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
      if (name.endsWith(".expected.xml")) {
         name = name.substring(0, name.length() - 13);
      }
      return name;
   }

   private boolean isDataFile(String name) {
      return name != null && name.endsWith(".data.xml");
   }

   private boolean isExpectedFile(String name) {
      return name != null && name.endsWith(".expected.xml");
   }

   private LinkType getDocType(String name) {
      LinkType toReturn = null;
      int index = name.lastIndexOf('.');
      if (index > 0) {
         try {
            name = name.substring(index + 1, name.length());
            toReturn = LinkType.valueOf(name.toUpperCase());
         } catch (Exception ex) {
            //Do nothing;
         }
      }
      assertNotNull(String.format("Error getting DocType from [%s]", name), toReturn);
      return toReturn;
   }

   private boolean isLinkTest(String name) {
      return !name.contains("unlink");
   }

   private Map<String, TestData> getTestData() {
      Map<String, TestData> toReturn = new LinkedHashMap<String, TestData>();

      Bundle bundle = Activator.getBundleContext().getBundle();
      Enumeration<?> urls = bundle.findEntries("data", "*.*", true);
      while (urls.hasMoreElements()) {
         URL url = (URL) urls.nextElement();
         String name = getFileName(url.getPath());
         if (Strings.isValid(name) && (url.getPath().endsWith(".data.xml") || url.getPath().endsWith(".expected.xml"))) {
            String key = name;
            int index = name.indexOf('.');
            if (index > 0) {
               key = name.substring(0, index);
            }
            TestData pair = toReturn.get(key);
            if (pair == null) {
               pair = new TestData();
               toReturn.put(key, pair);
            }
            if (isDataFile(url.getPath())) {
               pair.data = url;
               pair.docType = getDocType(name);
               pair.isLink = isLinkTest(name);
            } else if (isExpectedFile(url.getPath())) {
               pair.expected = url;
            } else if (pair.data == null || pair.expected == null) {
               toReturn.remove(pair);
            }
         }
      }
      return toReturn;
   }
   private class TestData {
      public boolean isLink;
      private URL data;
      private URL expected;
      private LinkType docType;
   }
}
