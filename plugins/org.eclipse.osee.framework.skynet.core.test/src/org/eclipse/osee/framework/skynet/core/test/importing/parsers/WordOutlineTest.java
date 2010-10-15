/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.importing.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.Assert;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.ReservedCharacters;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests different forms of outline numbers and titles
 * 
 * @author Karol M. Wilk
 */
@RunWith(Parameterized.class)
public final class WordOutlineTest {

   private static final Pattern PARAGRAPH_REGEX = Pattern.compile("<w:p[ >].*?</w:p>");
   private static final Pattern OUTLINE_NUMBER_PATTERN = Pattern.compile("((?>\\d+\\.)+\\d*)\\s*");

   private static final String OUTLINE_WITH_NUMBER = "outlineNameWithNumber.xml";
   private static final String OUTLINE_WITH_NUMBER_AND_CONTENT = "outlineNameNumberAndContent.xml";
   private static final String NUMBER_EMBED_IN_CONTENT = "numberEmbeddedInTheContent.xml";

   private final WordOutlineExtractorDelegate delegate;
   private final String dataFileName;
   private final DelegateData[] expectedData;

   public WordOutlineTest(String dataFileName, DelegateData... expectedData) {
      this.delegate = new WordOutlineExtractorDelegate();
      this.dataFileName = dataFileName;
      this.expectedData = expectedData;
   }

   //@formatter:off
   /**
	 * Note: some of the data objects need to repeat data
	 * from previous test because they are considered to
	 * be lastHeaderNumber or lastHeaderName or lastContent
	 * 
	 * @return collection of data sets used as input for parameterized unit test
	 */
   @Parameters
   public static Collection<Object[]> getData() {
      List<Object[]> data = new ArrayList<Object[]>();
      addTest(data, OUTLINE_WITH_NUMBER, data("1.", "Outline TITLE", ""));
      addTest(data, OUTLINE_WITH_NUMBER_AND_CONTENT, data("5.", "SCOPE", ""), data("5.", "SCOPE", "content content content more content"));
      
      StringBuilder builder = new StringBuilder();
      builder.append("This 1.6 is some interesting content 2.3SAMPL");
      builder.append(ReservedCharacters.toCharacter("&acirc;"));
      builder.append(ReservedCharacters.toCharacter("&euro;"));
      builder.append(ReservedCharacters.toCharacter("&ldquo;"));
      builder.append("10.");
      
      addTest(data, NUMBER_EMBED_IN_CONTENT, data("1.", "SCOPE", ""), data("1.", "SCOPE", builder.toString()));
      return data;
   }
   //@formatter:on

   private static void addTest(List<Object[]> data, String dataFileName, DelegateData... expectedData) {
      data.add(new Object[] {dataFileName, expectedData});
   }

   private static DelegateData data(String headerNumber, String headerName, String content) {
      return new DelegateData(headerNumber, headerName, content);
   }

   private static String getResourceData(String name) throws IOException {
      InputStream inputStream = null;
      try {
         inputStream = WordOutlineTest.class.getResourceAsStream(name);
         String data = Lib.inputStreamToString(inputStream);
         Assert.assertTrue(Strings.isValid(data));
         return data;
      } finally {
         Lib.close(inputStream);
      }
   }

   @Test
   public void testDelegate() throws Exception {
      delegate.initialize();

      String rawData = getResourceData(dataFileName);
      Matcher matcher = PARAGRAPH_REGEX.matcher(rawData);
      boolean foundSomething = false;

      List<DelegateData> actualData = new ArrayList<DelegateData>();

      while (matcher.find()) {
         foundSomething = true;
         String data = matcher.group();
         delegate.processContent(null, false, false, null, null, null, data, false);

         String headerNumber = delegate.getLastHeaderNumber().trim();
         String headerName = delegate.getLastHeaderName().trim();
         String content = delegate.getLastContent().trim();

         actualData.add(data(headerNumber, headerName, content));
      }

      Assert.assertTrue("WordOutlineTester no paragraphs found...", foundSomething);
      for (int index = 0; index < expectedData.length; index++) {
         DelegateData expected = expectedData[index];
         DelegateData actual = actualData.get(index);
         Assert.assertEquals(
            String.format(
               "\nChecking %s of %s,\nEXPECTED: \n\t Number:\"%s\" \n\t Title:\"%s\" \n\t Content:\"%s\"\nACTUAL: \n\t Number:\"%s\" \n\t Title:\"%s\" \n\t Content:\"%s\"\n",
               index, dataFileName, expected.getHeaderNumber(), expected.getHeaderName(), expected.getContent(),
               actual.getHeaderNumber(), actual.getHeaderName(), actual.getContent()), expected, actual);
         if (Strings.isValid(expected.getHeaderNumber())) {
            Assert.assertTrue("WordOutlineTester doesn't look like a outline number...",
               OUTLINE_NUMBER_PATTERN.matcher(actual.getHeaderNumber()).matches());
         }
      }
   }

   @After
   public void testCleanup() {
      delegate.dispose();
      Assert.assertNull(delegate.getLastHeaderNumber());
      Assert.assertNull(delegate.getLastHeaderName());
      Assert.assertNull(delegate.getLastContent());
   }

   private static final class DelegateData {
      private final String headerNumber;
      private final String headerName;
      private final String content;

      public DelegateData(String headerNumber, String headerName, String content) {
         super();
         this.headerNumber = headerNumber;
         this.headerName = headerName;
         this.content = content;
      }

      public String getHeaderNumber() {
         return this.headerNumber;
      }

      public String getHeaderName() {
         return this.headerName;
      }

      public String getContent() {
         return this.content;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (content == null ? 0 : content.hashCode());
         result = prime * result + (headerName == null ? 0 : headerName.hashCode());
         result = prime * result + (headerNumber == null ? 0 : headerNumber.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         DelegateData other = (DelegateData) obj;
         if (content == null) {
            if (other.content != null) {
               return false;
            }
         } else if (!content.equals(other.content)) {
            return false;
         }
         if (headerName == null) {
            if (other.headerName != null) {
               return false;
            }
         } else if (!headerName.equals(other.headerName)) {
            return false;
         }
         if (headerNumber == null) {
            if (other.headerNumber != null) {
               return false;
            }
         } else if (!headerNumber.equals(other.headerNumber)) {
            return false;
         }
         return true;
      }

      @Override
      public String toString() {
         return "DelegateData [headerNumber=" + headerNumber + ", headerName=" + headerName + ", content=" + content + "]";
      }

   }
}
