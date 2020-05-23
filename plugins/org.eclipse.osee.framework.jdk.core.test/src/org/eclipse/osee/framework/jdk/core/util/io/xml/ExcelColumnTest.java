/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.framework.jdk.core.util.io.xml;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/*
 * @author John Misinco
 */
public class ExcelColumnTest {

   @Test
   public void testWriteColumnDefNoStyle() throws IOException {
      ExcelColumn col = ExcelColumn.newCol(11.0);
      StringBuilder builder = new StringBuilder();
      col.writeColumnDefinition(builder);
      Assert.assertEquals("   <Column ss:Width=\"11.0\"/>\n", builder.toString());
   }

   @Test
   public void testWriteColumnDefWithStyle() throws IOException {
      ExcelColumn col = ExcelColumn.newCol("TestCol", 11.0, "TestStyle");
      StringBuilder builder = new StringBuilder();
      col.writeColumnDefinition(builder);
      Assert.assertEquals("   <Column ss:Width=\"11.0\" ss:StyleID=\"TestStyle\"/>\n", builder.toString());
   }

   @Test
   public void testEqualWidthColumns() throws IOException {
      int count = 3;
      ExcelColumn[] columns = ExcelColumn.newEqualWidthColumns(count, 11.1);
      Assert.assertEquals(count, columns.length);
      for (int i = 0; i < count; i++) {
         StringBuilder builder = new StringBuilder();
         columns[i].writeColumnDefinition(builder);
         Assert.assertEquals("   <Column ss:Width=\"11.1\"/>\n", builder.toString());
      }
   }

   @Test
   public void testNonEqualWidthColumns() throws IOException {
      double[] widths = {11.1, 12.2, 13.3};
      ExcelColumn[] columns = ExcelColumn.newColumns(widths);
      Assert.assertEquals(widths.length, columns.length);
      for (int i = 0; i < widths.length; i++) {
         StringBuilder builder = new StringBuilder();
         columns[i].writeColumnDefinition(builder);
         String width = String.valueOf(widths[i]);
         Assert.assertEquals("   <Column ss:Width=\"" + width + "\"/>\n", builder.toString());
      }
   }
}
