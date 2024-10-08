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
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan T. Baldwin
 */
public class ExcelWorkbookWriter {

   private final OutputStream outputStream;
   private final Workbook workbook;
   private final WorkbookFormat format;

   private final Map<String, Sheet> sheets;
   private final Map<String, CellStyle> cellStyles;

   private Sheet activeSheet;
   private int defaultZoom = 100;

   public ExcelWorkbookWriter(OutputStream outputStream, WorkbookFormat format) {
      this.outputStream = outputStream;
      this.workbook = format.equals(WorkbookFormat.XLS) ? new HSSFWorkbook() : new XSSFWorkbook();
      this.format = format;
      this.sheets = new HashMap<>();
      this.cellStyles = new HashMap<>();
   }

   public void writeWorkbook() {
      try {
         workbook.write(outputStream);
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

   public void createSheet(String sheetName) {
      Sheet sheet = workbook.createSheet(sheetName);
      sheet.setZoom(defaultZoom);
      sheets.put(sheetName, sheet);
      activeSheet = sheet;
   }

   public void setActiveSheet(String sheetName) {
      if (sheets.containsKey(sheetName)) {
         activeSheet = sheets.get(sheetName);
      } else {
         throw new OseeArgumentException("No sheet found with name " + sheetName);
      }
   }

   public void setTabColor(CELLSTYLE style) {
      if (!isXSSF() || style == null || style.equals(CELLSTYLE.NONE)) {
         return;
      }
      XSSFSheet sheet = (XSSFSheet) activeSheet;
      IndexedColors color = getIndexedColor(style);
      sheet.setTabColor(new XSSFColor(color, ((XSSFWorkbook) workbook).getStylesSource().getIndexedColors()));
   }

   public List<String> getSheetNames() {
      List<String> sheetNames = new LinkedList<>();
      sheetNames.addAll(sheets.keySet());
      return sheetNames;
   }

   public void setColumnWidth(int index, int width) {
      checkActiveSheet();
      setColumnWidth(activeSheet.getSheetName(), index, width);
   }

   public void setColumnWidth(String sheetName, int index, int width) {
      if (sheets.containsKey(sheetName)) {
         sheets.get(sheetName).setColumnWidth(index, width);
      } else {
         throw new OseeArgumentException("No sheet found with name " + sheetName);
      }
   }

   public void setColumnWidthInCharacters(int index, int numChars, int maxWidth) {
      checkActiveSheet();
      setColumnWidth(index, Math.min(((int) (numChars * 1.2)) * 256, maxWidth));
   }

   public int getColumnWidth(int index) {
      checkActiveSheet();
      return activeSheet.getColumnWidth(index);
   }

   /**
    * @param region - Example: "A1:B1"
    */
   public void addMergedRegion(String region) {
      checkActiveSheet();
      activeSheet.addMergedRegion(CellRangeAddress.valueOf(region));
   }

   public void addMergedRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
      checkActiveSheet();
      activeSheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
   }

   public void setRowHeight(int rowIndex, int height) {
      checkActiveSheet();
      Row row = activeSheet.getRow(rowIndex);
      if (row == null) {
         row = activeSheet.createRow(rowIndex);
      }
      row.setHeight((short) height);
   }

   public void autoSizeAllColumns(int numColumns) {
      checkActiveSheet();
      for (int i = 0; i < numColumns; i++) {
         activeSheet.autoSizeColumn(i);
      }
   }

   public void setDefaultZoom(int scale) {
      this.defaultZoom = scale;
   }

   public void setZoom(int scale) {
      checkActiveSheet();
      activeSheet.setZoom(scale);
   }

   public void writeRow(int rowIndex, Object[] values, CELLSTYLE... styles) {
      checkActiveSheet();
      for (int i = 0; i < values.length; i++) {
         writeCell(rowIndex, i, values[i], styles);
      }
   }

   public void writeCell(int rowIndex, int cellIndex, Object value) {
      writeCell(rowIndex, cellIndex, value, CELLSTYLE.NONE);
   }

   public void writeCell(int rowIndex, int cellIndex, Object value, CELLSTYLE... styles) {
      writeCell(rowIndex, cellIndex, value, Strings.EMPTY_STRING, HyperLinkType.SHEET, styles);
   }

   public void writeCell(int rowIndex, int cellIndex, Object value, String hyperlink, HyperLinkType hyperlinkType,
      CELLSTYLE... styles) {
      Cell cell = getCell(rowIndex, cellIndex);
      cell.setCellStyle(createCellStyle(styles));

      if (!hyperlink.isEmpty()) {
         CreationHelper helper = workbook.getCreationHelper();
         Hyperlink link = helper.createHyperlink(
            hyperlinkType.equals(HyperLinkType.SHEET) ? HyperlinkType.DOCUMENT : HyperlinkType.URL);
         link.setAddress(hyperlink);
         cell.setHyperlink(link);
      }

      if (value instanceof String) {
         cell.setCellValue((String) value);
      } else if (value instanceof Integer) {
         cell.setCellValue((Integer) value);
      } else if (value instanceof Double) {
         cell.setCellValue((Double) value);
      } else if (value instanceof Date) {
         String dateString = DateUtil.get((Date) value, "MM/dd/yyyy");
         cell.setCellValue(dateString);
      }
   }

   public void writeCellStringWithSuperscript(int rowIndex, int cellIndex, String value, int superscriptStart,
      int superscriptEnd, CELLSTYLE superscriptColor, CELLSTYLE... styles) {
      Cell cell = getCell(rowIndex, cellIndex);
      CellStyle cellStyle = createCellStyle(styles);
      cell.setCellStyle(cellStyle);

      Font superscriptFont = workbook.createFont();
      superscriptFont.setTypeOffset(Font.SS_SUPER);
      if (cellStyle.getFillForegroundColor() != IndexedColors.LIGHT_GREEN.getIndex() && superscriptColor.equals(
         CELLSTYLE.GREEN)) {
         superscriptFont.setColor(IndexedColors.GREEN.getIndex());
         superscriptFont.setBold(true);
      } else if (cellStyle.getFillForegroundColor() != IndexedColors.LIGHT_YELLOW.getIndex() && superscriptColor.equals(
         CELLSTYLE.YELLOW)) {
         superscriptFont.setColor(IndexedColors.GOLD.getIndex());
         superscriptFont.setBold(true);
      }

      RichTextString richText = new XSSFRichTextString(value);
      richText.applyFont(superscriptStart, superscriptEnd, superscriptFont);
      cell.setCellValue(richText);
   }

   private Cell getCell(int rowIndex, int cellIndex) {
      checkActiveSheet();
      Row row = activeSheet.getRow(rowIndex);
      if (row == null) {
         row = activeSheet.createRow(rowIndex);
      }
      return row.createCell(cellIndex);
   }

   private CellStyle createCellStyle(CELLSTYLE... styles) {
      String styleString = "";
      for (CELLSTYLE s : styles) {
         styleString += s.toString();
      }

      CellStyle style = cellStyles.get(styleString);
      if (style != null) {
         return style;
      }

      style = workbook.createCellStyle();
      Font font = workbook.createFont();

      for (CELLSTYLE s : styles) {
         switch (s) {
            case BOLD:
               font.setBold(true);
               break;
            case BORDER_ALL:
               style.setBorderTop(BorderStyle.THIN);
               style.setBorderRight(BorderStyle.THIN);
               style.setBorderBottom(BorderStyle.THIN);
               style.setBorderLeft(BorderStyle.THIN);
               break;
            case BORDER_BOTTOM:
               style.setBorderBottom(BorderStyle.THIN);
               break;
            case BORDER_RIGHT:
               style.setBorderRight(BorderStyle.THIN);
               break;
            case BORDER_LEFT:
               style.setBorderLeft(BorderStyle.THIN);
               break;
            case BORDER_TOP:
               style.setBorderTop(BorderStyle.THIN);
               break;
            case CENTERH:
               style.setAlignment(HorizontalAlignment.CENTER);
               break;
            case CENTERV:
               style.setVerticalAlignment(VerticalAlignment.CENTER);
               break;
            case GREEN:
               style.setFillForegroundColor(getIndexedColor(s).getIndex());
               style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
               break;
            case HYPERLINK:
               font.setColor(IndexedColors.BLUE.getIndex());
               font.setUnderline(Font.U_SINGLE);
               break;
            case LIGHT_BLUE:
               style.setFillForegroundColor(getIndexedColor(s).getIndex());
               style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
               break;
            case LIGHT_BLUE_XLSX:
               if (!isXSSF()) {
                  break;
               }
               byte[] rgb = new byte[] {(byte) 217, (byte) 225, (byte) 242}; // Matches Excel's Accent 1 20%
               XSSFColor color = new XSSFColor(rgb, getXSSFWorkbook().getStylesSource().getIndexedColors());
               ((XSSFCellStyle) style).setFillForegroundColor(color);
               style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
               break;
            case LIGHT_GREY:
               style.setFillForegroundColor(getIndexedColor(s).getIndex());
               style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
               break;
            case LIGHT_RED:
               style.setFillForegroundColor(getIndexedColor(s).getIndex());
               style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
               break;
            case WRAP:
               style.setWrapText(true);
               break;
            case YELLOW:
               style.setFillForegroundColor(getIndexedColor(s).getIndex());
               style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
               break;
            default:
               break;
         }
      }

      style.setFont(font);
      cellStyles.put(styleString, style);

      return style;
   }

   private IndexedColors getIndexedColor(CELLSTYLE style) {
      switch (style) {
         case GREEN:
            return IndexedColors.LIGHT_GREEN;
         case LIGHT_BLUE:
            return IndexedColors.LIGHT_CORNFLOWER_BLUE;
         case LIGHT_GREY:
            return IndexedColors.GREY_25_PERCENT;
         case LIGHT_RED:
            return IndexedColors.CORAL;
         case YELLOW:
            return IndexedColors.LIGHT_YELLOW;
         default:
            return IndexedColors.WHITE;
      }
   }

   public Sheet getActiveSheet() {
      return activeSheet;
   }

   public Workbook getWorkbook() {
      return workbook;
   }

   public enum CELLSTYLE {
      BOLD,
      BORDER_ALL,
      BORDER_BOTTOM,
      BORDER_LEFT,
      BORDER_RIGHT,
      BORDER_TOP,
      CENTERH,
      CENTERV,
      GREEN,
      HYPERLINK,
      LIGHT_BLUE,
      LIGHT_GREY,
      LIGHT_RED,
      NONE,
      WRAP,
      LIGHT_BLUE_XLSX,
      YELLOW
   }

   public enum HyperLinkType {
      SHEET,
      URL
   }

   public enum WorkbookFormat {
      XLS,
      XLSX
   }

   private void checkActiveSheet() {
      if (activeSheet == null) {
         throw new OseeArgumentException(
            "No sheet is active. Please create a sheet or set a sheet as active before writing.");
      }
   }

   private boolean isXSSF() {
      return this.format.equals(WorkbookFormat.XLSX);
   }

   private XSSFWorkbook getXSSFWorkbook() {
      return (XSSFWorkbook) workbook;
   }

}
