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
package org.eclipse.osee.framework.jdk.core.util.io.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @link: ExcelXmlWriter
 * @author Karol M. Wilk
 */
public final class ExcelXmlWriterTest {

   private static final String SAMPLE_STYLE = //
      "<Style ss:ID=\"Default\" ss:Name=\"Normal\">\n" + //
         "<Alignment ss:Vertical=\"Top\" ss:WrapText=\"1\"/>\n" + //
         "</Style>\n";
   private static final String BROKEN_TAGS_STYLE = //
      "<Styl" + "</Style>\n";
   private ISheetWriter excelWriter = null;

   private static final Pattern INDIVIDUAL_STYLE_REGEX = Pattern.compile("<Style .*</Style>", Pattern.DOTALL);

   private final StringWriter resultBuffer = new StringWriter();

   @Test
   public void testExcelStylesExistance() throws Exception {
      buildSampleExcelXmlFile(SAMPLE_STYLE);

      Matcher stylesRegexMatcher = ExcelXmlWriter.stylePattern.matcher(resultBuffer.toString());
      if (stylesRegexMatcher.find()) {
         Assert.assertTrue("No individual excel style found.",
            INDIVIDUAL_STYLE_REGEX.matcher(stylesRegexMatcher.group()).find());
      } else {
         Assert.fail("No excel style found.");
      }
   }

   @Test(expected = IllegalArgumentException.class)
   public void testIncorrectStyleSheet() throws Exception {
      buildSampleExcelXmlFile(BROKEN_TAGS_STYLE);
   }

   @Test(expected = OseeCoreException.class)
   public void testWritingTooManyColumns() throws Exception {
      excelWriter = new ExcelXmlWriter(resultBuffer);
      excelWriter.startSheet("test", 2);
      excelWriter.writeCell("one");
      excelWriter.writeCell("two");
      excelWriter.writeCell("three");
      excelWriter.endRow();
   }

   @Test
   public void testOverwritingCells() throws Exception {
      excelWriter = new ExcelXmlWriter(resultBuffer);
      excelWriter.startSheet("test", 2);
      excelWriter.writeCell("11111111", 0);
      excelWriter.writeCell("22222222", 1);
      excelWriter.writeCell("33333333", 0);
      excelWriter.writeCell("44444444");
      excelWriter.endRow();
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      String result = resultBuffer.toString();
      assertTrue(result.contains("33333333"));
      assertTrue(result.contains("44444444"));
      assertFalse(result.contains("11111111"));
      assertFalse(result.contains("22222222"));
   }

   @Test
   public void testIncorrectCellWritingOrder() throws Exception {
      excelWriter = new ExcelXmlWriter(resultBuffer);
      excelWriter.startSheet("test", 8);
      excelWriter.writeCell("11111111", 7);
      excelWriter.writeCell("22222222", 4);
      excelWriter.writeCell("33333333", 1);
      excelWriter.writeCell("44444444");
      excelWriter.endRow();
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      String result = resultBuffer.toString();
      assertTrue(result.indexOf("33333333") < result.indexOf("44444444"));
      assertTrue(result.indexOf("22222222") < result.indexOf("11111111"));
   }

   @Test(expected = OseeCoreException.class)
   public void testDoubleSheetStart() throws Exception {
      excelWriter = new ExcelXmlWriter(resultBuffer);
      excelWriter.startSheet("test", 8);
      excelWriter.startSheet("test2", 8);
   }

   @Test(expected = OseeCoreException.class)
   public void testWritingHighCellIndex() throws Exception {
      excelWriter = new ExcelXmlWriter(resultBuffer);
      excelWriter.startSheet("test", 2);
      excelWriter.writeCell("one", 2);
      excelWriter.endRow();
   }

   @Test
   public void testWritingNullCell() throws Exception {
      excelWriter = new ExcelXmlWriter(resultBuffer);
      excelWriter.startSheet("test", 5);
      excelWriter.writeCell("one", 2);
      excelWriter.writeCell(null);
      excelWriter.writeCell("two");
      excelWriter.endRow();
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      assertTrue(countCells(resultBuffer.toString()) == 2);
   }

   @Test
   public void testOverwritingWithNull() throws Exception {
      excelWriter = new ExcelXmlWriter(resultBuffer);
      excelWriter.startSheet("test", 5);
      excelWriter.writeCell("111111");
      excelWriter.writeCell("222222");
      excelWriter.writeCell("333333");
      excelWriter.writeCell(null, 1);
      excelWriter.endRow();
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      String result = resultBuffer.toString();
      assertTrue(!result.contains("222222"));
      assertTrue(countCells(result) == 2);
   }

   @Test
   public void testSetActiveSheet() throws Exception {
      excelWriter = new ExcelXmlWriter(resultBuffer);
      excelWriter.startSheet("test", 2);
      excelWriter.writeCell("111");
      excelWriter.writeCell("222");
      excelWriter.endRow();
      excelWriter.endSheet();
      excelWriter.startSheet("second", 2);
      excelWriter.writeCell("one");
      excelWriter.writeCell("two");
      excelWriter.endRow();
      excelWriter.endSheet();
      excelWriter.setActiveSheet(1);
      excelWriter.startSheet("third", 2);
      excelWriter.writeCell("uno");
      excelWriter.writeCell("dos");
      excelWriter.endRow();
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      String result = resultBuffer.toString();
      assertTrue(result.contains("<ActiveSheet>1</ActiveSheet>"));
   }

   @Test(expected = OseeArgumentException.class)
   public void testSetInvalidSheetNoSheet() throws Exception {
      excelWriter = new ExcelXmlWriter(resultBuffer);
      excelWriter.setActiveSheet(0);
      excelWriter.startSheet("first", 2);
      excelWriter.writeCell("one");
      excelWriter.writeCell("two");
      excelWriter.endRow();
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      String result = resultBuffer.toString();
      assertTrue(!result.contains("<ActiveSheet>1</ActiveSheet>"));
   }

   @Test(expected = OseeArgumentException.class)
   public void testSetInvalidSheetGTNumSheets() throws Exception {
      excelWriter = new ExcelXmlWriter(resultBuffer);
      excelWriter.startSheet("first", 2);
      excelWriter.writeCell("one");
      excelWriter.writeCell("two");
      excelWriter.endRow();
      excelWriter.endSheet();
      excelWriter.setActiveSheet(1);
      excelWriter.endWorkbook();
      String result = resultBuffer.toString();
      assertTrue(!result.contains("<ActiveSheet>1</ActiveSheet>"));
   }

   @Test(expected = OseeArgumentException.class)
   public void testSetInvalidSheetLTZero() throws Exception {
      excelWriter = new ExcelXmlWriter(resultBuffer);
      excelWriter.startSheet("first", 2);
      excelWriter.writeCell("one");
      excelWriter.writeCell("two");
      excelWriter.endRow();
      excelWriter.endSheet();
      excelWriter.setActiveSheet(-1);
      excelWriter.endWorkbook();
      String result = resultBuffer.toString();
      assertTrue(!result.contains("<ActiveSheet>1</ActiveSheet>"));
   }

   private int countCells(String xml) {
      // expects an excel xml file with one excel row in it
      int ct = 0;
      String subset = xml.substring(xml.indexOf("<Row>"), xml.indexOf("/Row"));
      int index = subset.indexOf("/Cell", 0);
      while (index > 0) {
         ct++;
         index = subset.indexOf("/Cell", index + 4);
      }
      return ct;
   }

   private void buildSampleExcelXmlFile(String style) throws IOException {
      //start
      excelWriter = new ExcelXmlWriter(resultBuffer, style);
      excelWriter.startSheet(getClass().getName(), 2);
      excelWriter.writeRow("Column1", "Column2");
      excelWriter.writeRow("TestData1", "TestData2");

      //end
      excelWriter.endSheet();
      excelWriter.endWorkbook();
   }
}