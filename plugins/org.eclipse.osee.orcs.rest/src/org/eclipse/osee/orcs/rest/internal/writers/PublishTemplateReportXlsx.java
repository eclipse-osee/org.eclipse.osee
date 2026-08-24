/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.orcs.rest.internal.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.writers.MarkdownExcelCellRenderer.CellRenderResult;

/**
 * POI-based (XLSX) report writer for the Generic Report system. Produces native .xlsx files with support for:
 * <ul>
 * <li>Embedded images resolved from &lt;image-link&gt; tags</li>
 * <li>Markdown tables rendered as embedded PNG images</li>
 * <li>Rich text formatting for inline markdown (bold, italic, code)</li>
 * </ul>
 * This is the XLSX counterpart to {@link PublishTemplateReport} which produces SpreadsheetML XML.
 *
 * @author David W. Miller
 */
public final class PublishTemplateReportXlsx implements StreamingOutput {
   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final ArtifactId view;
   private final ArtifactId reportTemplateArt;
   private final GenericReportBuilder report;
   private final XResultData results;
   private final MarkdownExcelCellRenderer cellRenderer;

   public PublishTemplateReportXlsx(OrcsApi orcsApi, BranchId branch, ArtifactId view, ArtifactId templateArt) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.view = view;
      this.reportTemplateArt = templateArt;
      this.results = new XResultData();
      this.report = new GenericReportBuilder(branch, view, orcsApi);
      this.cellRenderer = new MarkdownExcelCellRenderer(orcsApi, branch);
   }

   @Override
   public void write(OutputStream output) {
      try (XSSFWorkbook workbook = new XSSFWorkbook()) {
         if (reportTemplateArt.isValid()) {
            writeReport(workbook);
         } else {
            results.errorf("Invalid Template Report artifact provided: %s", reportTemplateArt);
         }
         if (!results.getResults().isEmpty()) {
            writeDebugSheet(workbook);
         }
         workbook.write(output);
      } catch (IOException ex) {
         throw new WebApplicationException(ex);
      }
   }

   private void writeReport(XSSFWorkbook workbook) {
      TemplateParser parser = new TemplateParser(orcsApi, branch, view, reportTemplateArt, results);
      parser.parseTemplateData(report);

      if (!results.isErrors()) {
         String sheetName = sanitizeSheetName(parser.getTemplateArtifact().getName());
         Sheet sheet = workbook.createSheet(sheetName);
         writeReportData(workbook, sheet);
      }
   }

   private void writeReportData(Workbook workbook, Sheet sheet) {
      List<Object[]> data = new ArrayList<>();
      report.getDataRowsFromQuery(data);

      // Create styles once for the entire workbook
      CellStyle headerStyle = createHeaderStyle(workbook);
      CellStyle topRowStyle = createTopRowStyle(workbook);
      CellStyle dataStyle = createDataStyle(workbook);
      CellStyle wrappedDataStyle = createWrappedDataStyle(workbook);
      MarkdownExcelCellRenderer.WorkbookFonts fonts = MarkdownExcelCellRenderer.WorkbookFonts.create(workbook);

      int rowIndex = 0;
      for (Object[] rowData : data) {
         Row row = sheet.createRow(rowIndex);
         CellStyle styleToUse;

         if (rowIndex == 0) {
            styleToUse = topRowStyle;
         } else if (rowIndex == 1) {
            styleToUse = headerStyle;
         } else {
            styleToUse = dataStyle;
         }

         for (int colIndex = 0; colIndex < rowData.length; colIndex++) {
            Cell cell = row.createCell(colIndex);
            Object value = rowData[colIndex];

            if (value == null) {
               cell.setCellStyle(styleToUse);
               continue;
            }

            String cellValue = value.toString();

            // For data rows, check if content has markdown that needs rich rendering
            if (rowIndex >= 2 && cellRenderer.containsMarkdownContent(cellValue)) {
               CellRenderResult renderResult = cellRenderer.renderCell(cellValue, workbook, fonts);
               cellRenderer.applyToCell(renderResult, cell, sheet, rowIndex, colIndex);
               cell.setCellStyle(wrappedDataStyle);
            } else {
               cell.setCellValue(cellValue);
               cell.setCellStyle(styleToUse);
            }
         }
         rowIndex++;
      }

      // Auto-size columns with a reasonable max
      int numColumns = report.getColumnCount();
      for (int col = 0; col < numColumns; col++) {
         sheet.autoSizeColumn(col);
         int currentWidth = sheet.getColumnWidth(col);
         int maxWidth = 50 * 256; // 50 characters max
         if (currentWidth > maxWidth) {
            sheet.setColumnWidth(col, maxWidth);
         }
      }
   }

   /**
    * Sanitizes a string for use as an Excel sheet name. Removes characters that are invalid in sheet names
    * ({@code [ ] * / \ ? :}) and truncates to the 31-character maximum.
    */
   private static String sanitizeSheetName(String name) {
      String sanitized = name.replaceAll("[\\[\\]\\*\\/\\\\\\?:]", "");
      if (sanitized.isEmpty()) {
         sanitized = "Report";
      }
      if (sanitized.length() > 31) {
         sanitized = sanitized.substring(0, 31);
      }
      return sanitized;
   }

   private void writeDebugSheet(Workbook workbook) {
      Sheet debugSheet = workbook.createSheet("DebugInfo");
      int rowIndex = 0;

      Row headerRow = debugSheet.createRow(rowIndex++);
      headerRow.createCell(0).setCellValue("Result Text");

      for (String result : results.getResults()) {
         Row row = debugSheet.createRow(rowIndex++);
         row.createCell(0).setCellValue(result);
      }

      debugSheet.autoSizeColumn(0);
   }

   private CellStyle createTopRowStyle(Workbook workbook) {
      CellStyle style = workbook.createCellStyle();
      Font font = workbook.createFont();
      font.setFontName("Calibri");
      font.setFontHeightInPoints((short) 11);
      font.setBold(true);
      style.setFont(font);
      style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
      style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      style.setAlignment(HorizontalAlignment.CENTER);
      style.setVerticalAlignment(VerticalAlignment.CENTER);
      return style;
   }

   private CellStyle createHeaderStyle(Workbook workbook) {
      CellStyle style = workbook.createCellStyle();
      Font font = workbook.createFont();
      font.setFontName("Calibri");
      font.setFontHeightInPoints((short) 11);
      font.setBold(true);
      style.setFont(font);
      style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
      style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      style.setVerticalAlignment(VerticalAlignment.CENTER);
      return style;
   }

   private CellStyle createDataStyle(Workbook workbook) {
      CellStyle style = workbook.createCellStyle();
      Font font = workbook.createFont();
      font.setFontName("Calibri");
      font.setFontHeightInPoints((short) 11);
      style.setFont(font);
      style.setVerticalAlignment(VerticalAlignment.TOP);
      return style;
   }

   private CellStyle createWrappedDataStyle(Workbook workbook) {
      CellStyle style = workbook.createCellStyle();
      Font font = workbook.createFont();
      font.setFontName("Calibri");
      font.setFontHeightInPoints((short) 11);
      style.setFont(font);
      style.setVerticalAlignment(VerticalAlignment.TOP);
      style.setWrapText(true);
      return style;
   }
}
