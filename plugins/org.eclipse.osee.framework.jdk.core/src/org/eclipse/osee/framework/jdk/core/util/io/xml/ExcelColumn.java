/*********************************************************************
 * Copyright (c) 2013 Boeing
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
import java.util.Arrays;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public final class ExcelColumn {
   private static final int DEFAULT_COLUMN_WIDTH = 85;
   private final double columnWidth;
   private final String styleId;
   private final String name;

   private ExcelColumn(String name, double columnWidth, String styleId) {
      this.name = name;
      this.columnWidth = columnWidth;
      this.styleId = styleId;
   }

   private ExcelColumn(double columnWidth) {
      this(null, columnWidth, null);
   }

   private ExcelColumn(String name, double columnWidth) {
      this(name, columnWidth, null);
   }

   public static ExcelColumn newCol(double columnWidth) {
      return new ExcelColumn(columnWidth);
   }

   public static ExcelColumn newCol(String name, double columnWidth) {
      return new ExcelColumn(name, columnWidth);
   }

   public static ExcelColumn newCol(String name, double columnWidth, String styleId) {
      return new ExcelColumn(name, columnWidth, styleId);
   }

   public void writeColumnDefinition(Appendable out) throws IOException {
      out.append("   <Column ss:Width=\"");
      out.append(String.valueOf(columnWidth));
      if (Strings.isValid(styleId)) {
         out.append("\" ss:StyleID=\"");
         out.append(styleId);
      }
      out.append("\"/>\n");
   }

   public static ExcelColumn[] newEmptyColumns(int columnCount) {
      return newEqualWidthColumns(columnCount, DEFAULT_COLUMN_WIDTH);
   }

   public static ExcelColumn[] newEqualWidthColumns(int columnCount, double columnWidth) {
      double[] columnWidths = new double[columnCount];
      Arrays.fill(columnWidths, columnWidth);
      return newColumns(columnWidths);
   }

   public static ExcelColumn[] newColumns(double... columnWidths) {
      ExcelColumn[] columns = new ExcelColumn[columnWidths.length];

      for (int i = 0; i < columnWidths.length; i++) {
         columns[i] = new ExcelColumn(columnWidths[i]);
      }
      return columns;
   }

   public String getName() {
      return name;
   }
}