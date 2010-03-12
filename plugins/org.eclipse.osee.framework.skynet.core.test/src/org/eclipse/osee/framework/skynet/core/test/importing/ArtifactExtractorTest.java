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
package org.eclipse.osee.framework.skynet.core.test.importing;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.parsers.ExcelArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.NativeDocumentExtractor;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class ArtifactExtractorTest {
   private static final String TEST_DATA_PATH = "support/ArtifactExtractorTests/";
   private final IArtifactExtractor extractor;
   private final String testDataFileName;
   private final PropertyStore propertyStore;
   private static String BASE_DATA_PATH;

   public ArtifactExtractorTest(IArtifactExtractor extractor, String testDataFileName) {
      this.extractor = extractor;
      this.testDataFileName = testDataFileName;
      this.propertyStore = new PropertyStore();

   }

   @BeforeClass
   public static void testInitialize() throws Exception {
      BASE_DATA_PATH = Lib.getBasePath(ArtifactExtractorTest.class) + "../";
      BASE_DATA_PATH += TEST_DATA_PATH;
   }

   @Before
   public void setup() throws Exception {
      Assert.assertTrue(Strings.isValid(testDataFileName));
      InputStream inputStream = null;
      try {
         URL url = Lib.getUrlFromString(BASE_DATA_PATH + testDataFileName);
         inputStream = url.openStream();
         propertyStore.load(inputStream);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               Assert.assertTrue("Error closing resource: " + testDataFileName, false);
            }
         }
      }
   }

   @After
   public void tearDown() throws Exception {
   }

   @org.junit.Test
   public void testGetters() {
      Assert.assertFalse(extractor.usesTypeList());
      Assert.assertNotNull(extractor.getFileFilter());

      Assert.assertEquals(propertyStore.get("expected.name"), extractor.getName());
      Assert.assertEquals(propertyStore.get("expected.toString"), extractor.toString());
      Assert.assertEquals(propertyStore.get("expected.description"), extractor.getDescription());
      Assert.assertEquals(propertyStore.getBoolean("expected.isDelegateRequired"), extractor.isDelegateRequired());
      Assert.assertFalse(extractor.hasDelegate());
   }

   private List<TestData> getFileTestData() {
      List<TestData> testDatas = new ArrayList<TestData>();
      String separator = propertyStore.get("list.entry.separator");
      String[] tests = propertyStore.getArray("fileTestData");
      Assert.assertNotNull(tests);
      Assert.assertTrue(tests.length > 0);
      int cnt = 0;
      for (String entry : tests) {
         String[] values = entry.split(separator);
         Assert.assertTrue(values.length == 2);
         String message = String.format("File Test %d: ", ++cnt);
         String data = convert(values[0]);
         boolean expected = Boolean.valueOf(values[1]);
         testDatas.add(new TestData(message, data, expected));
      }
      return testDatas;
   }

   private String convert(String rawData) {
      StringBuilder builder = new StringBuilder();
      builder.append(System.getProperty("user.home"));
      builder.append(File.separator);
      for (int index = 0; index < rawData.length(); index++) {
         char value = rawData.charAt(index);
         if (index == 0 && value == '/') {
            continue;
         }

         if (value == '/') {
            builder.append(File.separator);
         } else {
            builder.append(value);
         }
      }
      return builder.toString();
   }

   @org.junit.Test
   public void testFileFilter() throws IOException {
      FileFilter filter = extractor.getFileFilter();
      boolean success = true;
      for (TestData testData : getFileTestData()) {
         File file = new File(testData.getFile());
         try {
            if (testData.getFile().endsWith(File.separator)) {
               success &= file.mkdirs();
            } else {
               Lib.writeStringToFile("test", file);
            }
            boolean actual = filter.accept(file);
            Assert.assertEquals(String.format("%s [%s]", testData.getMessage(), testData.getFile()),
                  testData.getExpected(), actual);
         } finally {
            success &= file.delete();
         }
      }

      if (!success) {
         throw new IOException();
      }
   }

   private List<ParseTestData> getParseTestData() {
      List<ParseTestData> testDatas = new ArrayList<ParseTestData>();
      String separator = propertyStore.get("list.entry.separator");
      String[] tests = propertyStore.getArray("parseTestData");
      Assert.assertNotNull(tests);
      Assert.assertTrue(tests.length > 0);
      int cnt = 0;
      for (String entry : tests) {
         String[] values = entry.split(separator);
         String message = String.format("Parse Test %d: ", ++cnt);
         String source = values[0];
         String expected = ""; // TODO figure out the parsed data format
         testDatas.add(new ParseTestData(message, source, expected));
      }
      return testDatas;
   }

   @org.junit.Test
   public void testProcessing() throws Exception {
      RoughArtifact parent = new RoughArtifact(RoughArtifactKind.PRIMARY);
      RoughArtifactCollector actualCollector = new RoughArtifactCollector(parent);
      RoughArtifactCollector expectedCollector = new RoughArtifactCollector(parent);
      for (ParseTestData testData : getParseTestData()) {

         RoughArtifactUtil.resetCollector(actualCollector);
         RoughArtifactUtil.resetCollector(expectedCollector);

         URL url = Lib.getUrlFromString(BASE_DATA_PATH + testData.getSourceData());
         extractor.process(url.toURI(), actualCollector);

         URL expectedData = Lib.getUrlFromString(BASE_DATA_PATH + testData.getExpectedData());
         RoughArtifactUtil.loadDataFrom(expectedData, expectedCollector);
         RoughArtifactUtil.checkCollectors(expectedCollector, actualCollector);
      }
   }

   @Parameters
   public static Collection<Object[]> data() {
      Collection<Object[]> data = new ArrayList<Object[]>();

      data.add(new Object[] {new ExcelArtifactExtractor(), "ExcelArtifactExtractorTest.xml"});
      data.add(new Object[] {new NativeDocumentExtractor(), "NativeDocumentExtractorTest.xml"});
      //      data.add(new Object[] {new WholeWordDocumentExtractor()});
      //      data.add(new Object[] {new WordOutlineExtractor()});
      //      data.add(new Object[] {new XmlDataExtractor()});
      return data;
   }

   private static final class ParseTestData {
      private final String message;
      private final String sourceData;
      private final String expectedData;

      public ParseTestData(String message, String sourceData, String expectedData) {
         super();
         this.message = message;
         this.sourceData = sourceData;
         this.expectedData = expectedData;
      }

      public String getMessage() {
         return message;
      }

      public String getSourceData() {
         return sourceData;
      }

      public String getExpectedData() {
         return expectedData;
      }

      @Override
      public String toString() {
         return String.format("msg:[%s] source:[%s] expected:[%s]", getMessage(), getSourceData(), getExpectedData());
      }
   }

   private static final class TestData {
      private final String message;
      private final String file;
      private final boolean expected;

      public TestData(String message, String file, boolean expected) {
         super();
         this.message = message;
         this.file = file;
         this.expected = expected;
      }

      public String getMessage() {
         return message;
      }

      public String getFile() {
         return file;
      }

      public boolean getExpected() {
         return expected;
      }

      @Override
      public String toString() {
         return String.format("msg:[%s] file:[%s] isAccepted:[%s]", getMessage(), getFile(), getExpected());
      }
   }
}
