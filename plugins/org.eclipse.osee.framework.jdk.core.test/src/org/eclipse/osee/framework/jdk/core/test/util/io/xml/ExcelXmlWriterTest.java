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
package org.eclipse.osee.framework.jdk.core.test.util.io.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.Assert;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.junit.Test;

/**
 * @link: ExcelXmlWriter
 * @author Karol M. Wilk
 */
public final class ExcelXmlWriterTest {

   private static final String SAMPLE_STYLE = //
      "<Styles>\n" + //
      "<Style ss:ID=\"Default\" ss:Name=\"Normal\">\n" + //
      "<Alignment ss:Vertical=\"Top\" ss:WrapText=\"1\"/>\n" + //
      "</Style>\n" + //
      "</Styles>\n";
   private static final String BROKEN_TAGS_STYLE = //
      "<Styl" + "</Styles>\n";
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

   @Test
   public void testNullStyle() throws Exception {
      buildSampleExcelXmlFile(null);
      Assert.assertFalse("Styles unexpectedly found.", resultBuffer.toString().contains("Styles"));
   }

   private void buildSampleExcelXmlFile(String style) throws IOException {
      //start
      excelWriter = new ExcelXmlWriter(resultBuffer, style);
      excelWriter.startSheet(getClass().getName(), 10);
      excelWriter.writeRow(new String[] {"Column1", "Column2"});

      excelWriter.writeRow(new String[] {"TestData1", "TestData2"});

      //end
      excelWriter.endSheet();
      excelWriter.endWorkbook();
   }
}