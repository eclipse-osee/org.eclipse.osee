/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.jdk.core.util.io.excel;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelWorkbookUtils {

   public static void copyActiveSheet(ExcelWorkbookReader copyFromReader, ExcelWorkbookWriter copyToWriter) {
      Sheet srcSheet = copyFromReader.getActiveSheet();
      Sheet newSheet = copyToWriter.getActiveSheet();
      int maxColumnNum = 0;
      Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();

      for (int i = srcSheet.getFirstRowNum(); i <= srcSheet.getLastRowNum(); i++) {
         Row srcRow = srcSheet.getRow(i);
         Row newRow = newSheet.createRow(i);

         if (srcRow != null) {
            copyRow(copyToWriter.getWorkbook(), srcRow, newRow, styleMap);
            maxColumnNum = Math.max(maxColumnNum, srcRow.getLastCellNum());
         }
      }

      for (int i = 0; i <= maxColumnNum; i++) {
         newSheet.setColumnWidth(i, srcSheet.getColumnWidth(i));
      }
   }

   private static void copyRow(Workbook newWorkbook, Row srcRow, Row newRow, Map<Integer, CellStyle> styleMap) {
      newRow.setHeight(srcRow.getHeight());
      for (int i = srcRow.getFirstCellNum(); i <= srcRow.getLastCellNum(); i++) {
         Cell srcCell = srcRow.getCell(i);
         Cell newCell = newRow.createCell(i);
         if (srcCell != null) {
            copyCell(newWorkbook, srcCell, newCell, styleMap);
         }
      }
   }

   private static void copyCell(Workbook newWorkbook, Cell srcCell, Cell newCell, Map<Integer, CellStyle> styleMap) {
      CellStyle newCellStyle = styleMap.get(srcCell.getCellStyle().hashCode());
      if (newCellStyle == null) {
         newCellStyle = newWorkbook.createCellStyle();
         newCellStyle.cloneStyleFrom(srcCell.getCellStyle());
         styleMap.put(srcCell.getCellStyle().hashCode(), newCellStyle);
      }
      newCell.setCellStyle(newCellStyle);

      switch (srcCell.getCellType()) {
         case BOOLEAN:
            newCell.setCellValue(srcCell.getBooleanCellValue());
            break;
         case ERROR:
            newCell.setCellErrorValue(srcCell.getErrorCellValue());
            break;
         case FORMULA:
            newCell.setCellValue(srcCell.getCellFormula());
            break;
         case NUMERIC:
            newCell.setCellValue(srcCell.getNumericCellValue());
            break;
         case STRING:
            newCell.setCellValue(srcCell.getStringCellValue());
            break;
         default:
            break;
      }
   }

}
