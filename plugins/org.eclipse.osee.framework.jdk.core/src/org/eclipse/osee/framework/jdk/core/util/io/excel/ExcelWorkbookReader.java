/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

public class ExcelWorkbookReader {

   private Workbook workbook;
   private Sheet activeSheet;

   public ExcelWorkbookReader(InputStream inputStream) {
      try {
         this.workbook = new HSSFWorkbook(inputStream);
      } catch (IOException ex) {
         System.out.println(ex);
      }
   }

   public void closeWorkbook() {
      try {
         workbook.close();
      } catch (IOException ex) {
         System.out.println(ex);
      }
   }

   public void setActiveSheet(String sheetName) {
      Sheet sheet = workbook.getSheet(sheetName);
      if (sheet == null) {
         throw new OseeArgumentException("No sheet found with name " + sheetName);
      }
      activeSheet = sheet;
   }

   /**
    * @param index - 0-based sheet index
    */
   public void setActiveSheet(int index) {
      Sheet sheet = workbook.getSheetAt(index);
      if (sheet == null) {
         throw new OseeArgumentException("No sheet found at index " + index);
      }
      activeSheet = sheet;
   }

   public Object getCellValue(int rowIndex, int cellIndex) {
      checkActiveSheet();
      Row row = activeSheet.getRow(rowIndex);
      if (row == null) {
         throw new OseeArgumentException(
            "Row index " + rowIndex + " is invalid for sheet " + activeSheet.getSheetName());
      }
      Cell cell = row.getCell(cellIndex);
      if (cell == null) {
         return ""; // If the cell is not populated, return an empty string to represent the empty cell.
      }
      switch (cell.getCellType()) {
         case STRING:
         case FORMULA:
            return cell.getStringCellValue();
         case NUMERIC:
            return cell.getNumericCellValue();
         default:
            return null;
      }
   }

   /**
    * Gets the value of the cell as a string. Returns an empty string if the cell does not exist.
    *
    * @param rowIndex
    * @param cellIndex
    * @return
    */
   public String getCellStringValue(int rowIndex, int cellIndex) {
      Object val = getCellValue(rowIndex, cellIndex);
      return val == null ? "" : val.toString();
   }

   public double getCellNumericValue(int rowIndex, int cellIndex) {
      return (double) getCellValue(rowIndex, cellIndex);
   }

   public String getCellFormulaValue(int rowIndex, int cellIndex) {
      checkActiveSheet();
      Cell cell = getCell(rowIndex, cellIndex);
      return cell.getCellFormula();
   }

   public String getCellHyperlinkString(int rowIndex, int cellIndex) {
      checkActiveSheet();
      Cell cell = getCell(rowIndex, cellIndex);
      return cell.getHyperlink().getAddress();
   }

   private Cell getCell(int rowIndex, int cellIndex) {
      checkActiveSheet();
      Row row = activeSheet.getRow(rowIndex);
      if (row == null) {
         throw new OseeArgumentException(
            "Row index " + rowIndex + " is invalid for sheet " + activeSheet.getSheetName());
      }
      Cell cell = row.getCell(cellIndex);
      if (cell == null) {
         throw new OseeArgumentException(
            "Cell index " + cellIndex + " is invalid for row " + rowIndex + " on sheet " + activeSheet.getSheetName());
      }
      return cell;
   }

   public boolean rowExists(int rowIndex) {
      checkActiveSheet();
      return activeSheet.getRow(rowIndex) != null;
   }

   public boolean isCellValid(int rowIndex, int cellIndex) {
      checkActiveSheet();
      Row row = activeSheet.getRow(rowIndex);
      Cell cell = row.getCell(cellIndex);
      return cell != null;
   }

   public List<String> getMergedRegions() {
      checkActiveSheet();
      return activeSheet.getMergedRegions().stream().map(r -> r.formatAsString()).collect(Collectors.toList());
   }

   private void checkActiveSheet() {
      if (activeSheet == null) {
         throw new OseeArgumentException(
            "No sheet is active. Please create a sheet or set a sheet as active before writing.");
      }
   }

}
