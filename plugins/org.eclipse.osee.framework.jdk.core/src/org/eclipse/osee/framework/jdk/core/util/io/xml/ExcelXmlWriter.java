/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.CellData;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;

/**
 * @see ExcelXmlWriterTest
 * @author Ryan D. Brooks
 * @author Karol M. Wilk
 */
public final class ExcelXmlWriter extends AbstractSheetWriter {
   public static enum STYLE {
      NONE(""),
      BOLD("OseeBoldStyle"),
      BOLD_GOOD_BG("OseeBoldGoodBackground"),
      BOLD_NEUTRAL_BG("OseeBoldNeutralBackground"),
      CENTERED("OseeCentered"),
      DATE("OseeDate"),
      DATE_GOOD_BG("OseeDateGoodBackground"),
      DATE_NEUTRAL_BG("OseeDateNeutralBackground"),
      ERROR("OseeErrorStyle"),
      GOOD_BG("OseeGoodBackground"),
      HYPERLINK_GOOD_BG("OseeHyperlinkGoodBackground"),
      HYPERLINK_NEUTRAL_BG("OseeHyperlinkNeutralBackground"),
      ITALICS("OseeItalicStyle"),
      NEUTRAL_BG("OseeNeutralBackground"),
      WRAPPED("OseeWraped");

      private String text;

      STYLE(String text) {
         this.text = text;
      }

      public String getText() {
         return text;
      }
   };

   public static final String WrappedStyle = "OseeWraped";

   public static final Pattern stylePattern = Pattern.compile("<Style.*</Style>\\s*", Pattern.DOTALL);

   public static final String defaultEmptyStringXmlRep = "&#248;";
   public static final String defaultEmptyString = "\u00F8";
   public static final String blobMessage = "data stored in EmbeddedClob since longer than 32767 chars";
   public static final int DEFAULT_FONT_SIZE = 11;

   public static final String XML_HEADER = //
      "<?xml version=\"1.0\"?>\n" + //
         "<?mso-application progid=\"Excel.Sheet\"?>\n" + //
         "<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n" + //
         " xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n" + //
         " xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n" + //
         " xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n" + //
         " xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n";

   public static final String DEFAULT_OSEE_STYLES = //
      "<Style ss:ID=\"Default\" ss:Name=\"Normal\">\n" + //
         " <Alignment ss:Vertical=\"Bottom\"/>\n" + //
         " <Borders/>\n" + //
         " <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"%d\" ss:Color=\"#000000\"/>\n" + //
         " <Interior/>\n" + //
         " <NumberFormat/>\n" + //
         " <Protection/>\n" + //
         "</Style>\n" + //
         "<Style ss:ID=\"OseeDate\"><NumberFormat ss:Format=\"Short Date\"/></Style>\n" + //
         "<Style ss:ID=\"OseeDateGoodBackground\"><NumberFormat ss:Format=\"Short Date\"/><Interior ss:Color=\"#C6EFCE\" ss:Pattern=\"Solid\"/></Style>\n" + //
         "<Style ss:ID=\"OseeDateNeutralBackground\"><NumberFormat ss:Format=\"Short Date\"/><Interior ss:Color=\"#FFEB9C\" ss:Pattern=\"Solid\"/></Style>\n" + //
         "<Style ss:ID=\"OseeBoldStyle\"><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style>\n" + //
         "<Style ss:ID=\"OseeBoldGoodBackground\"><Font x:Family=\"Swiss\" ss:Bold=\"1\"/><Interior ss:Color=\"#C6EFCE\" ss:Pattern=\"Solid\"/></Style>\n" + //
         "<Style ss:ID=\"OseeBoldNeutralBackground\"><Font x:Family=\"Swiss\" ss:Bold=\"1\"/><Interior ss:Color=\"#FFEB9C\" ss:Pattern=\"Solid\"/></Style>\n" + //
         "<Style ss:ID=\"OseeItalicStyle\"><Font x:Family=\"Swiss\" ss:Italic=\"1\"/></Style>\n" + //
         "<Style ss:ID=\"OseeErrorStyle\"><Font x:Family=\"Swiss\" ss:Color=\"#FF0000\" ss:Bold=\"1\"/></Style>\n" + //
         "<Style ss:ID=\"OseeCentered\"><Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/></Style>\n" + //
         "<Style ss:ID=\"OseeWraped\"><Alignment ss:Vertical=\"Top\" ss:WrapText=\"1\"/></Style>\n" + //
         "<Style ss:ID=\"OseeGoodBackground\"><Interior ss:Color=\"#C6EFCE\" ss:Pattern=\"Solid\"/></Style>\n" + //
         "<Style ss:ID=\"OseeNeutralBackground\"><Interior ss:Color=\"#FFEB9C\" ss:Pattern=\"Solid\"/></Style>\n" + //
         "<Style ss:ID=\"OseeHyperlink\" ss:Name=\"Hyperlink\"><Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Color=\"#0563C1\" ss:Underline=\"Single\"/></Style>\n" + //
         "<Style ss:ID=\"OseeHyperlinkGoodBackground\" ss:Name=\"HyperlinkGoodBg\"><Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Color=\"#0563C1\" ss:Underline=\"Single\"/><Interior ss:Color=\"#C6EFCE\" ss:Pattern=\"Solid\"/></Style>\n" + //
         "<Style ss:ID=\"OseeHyperlinkNeutralBackground\" ss:Name=\"HyperlinkNeutralBg\"><Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Color=\"#0563C1\" ss:Underline=\"Single\"/><Interior ss:Color=\"#FFEB9C\" ss:Pattern=\"Solid\"/></Style>";

   private final BufferedWriter out;
   private boolean inSheet;
   private final String emptyStringRepresentation;
   private int previouslyWrittenCellIndex;

   private boolean applyStyle = false;
   private final Map<Integer, String> mStyleMap;
   private final Map<Integer, Integer> mColSpanMap = new HashMap<>();

   private String[] rowBuffer;
   private int numColumns = -1;

   private double rowHeight;

   private int richTextCell = -1;

   private int numSheetsWritten = 0;
   private int activeSheetNum = -1;

   public ExcelXmlWriter(File file) throws IOException {
      this(new FileWriter(file));
   }

   public ExcelXmlWriter(Writer writer) throws IOException {
      this(writer, null);
   }

   @SuppressWarnings("ChainingConstructorIgnoresParameter")
   public ExcelXmlWriter(String fileName, String style) throws IOException {
      this(fileName, null, defaultEmptyStringXmlRep, DEFAULT_FONT_SIZE);
   }

   /**
    * Calls original constructor with provided style.
    *
    * @param writer output
    * @param style Excel Style XML of form <Styles><Style/><Style/></Styles>
    */
   public ExcelXmlWriter(Writer writer, String style) throws IOException {
      this(writer, style, defaultEmptyStringXmlRep);
   }

   public ExcelXmlWriter(Writer writer, String style, String emptyStringRepresentation) throws IOException {
      this(writer, style, emptyStringRepresentation, DEFAULT_FONT_SIZE);
   }

   public ExcelXmlWriter(Writer writer, String style, String emptyStringRepresentation, int defaultFontSize) throws IOException {
      out = new BufferedWriter(writer);
      mStyleMap = new HashMap<>();
      this.emptyStringRepresentation = emptyStringRepresentation;
      out.write(XML_HEADER);

      out.write("<Styles>\n");

      out.write(String.format(DEFAULT_OSEE_STYLES, defaultFontSize));
      if (Strings.isValid(style)) {
         if (stylePattern.matcher(style).matches()) {
            out.write(style);
         } else {
            throw new IllegalArgumentException("incomingStyle must match the pattern " + stylePattern);
         }
      }
      out.write("</Styles>\n");
   }

   public ExcelXmlWriter(String filePath, String style, String emptyStringRepresentation, int defaultFontSize) throws IOException {
      FileWriter file = new FileWriter(filePath);
      out = new BufferedWriter(file);
      mStyleMap = new HashMap<>();
      this.emptyStringRepresentation = emptyStringRepresentation;
      out.write(XML_HEADER);

      out.write("<Styles>\n");

      out.write(String.format(DEFAULT_OSEE_STYLES, defaultFontSize));
      if (Strings.isValid(style)) {
         if (stylePattern.matcher(style).matches()) {
            out.write(style);
         } else {
            throw new IllegalArgumentException("incomingStyle must match the pattern " + stylePattern);
         }
      }
      out.write("</Styles>\n");
   }

   @Override
   public void startSheet(String worksheetName, int columnCount) throws IOException {
      startSheet(worksheetName, ExcelColumn.newEmptyColumns(columnCount));
   }

   public void startSheet(String worksheetName, ExcelColumn... columns) throws IOException {
      if (inSheet) {
         throw new OseeCoreException("Cannot start a new sheet until the current sheet is closed");
      }
      if (worksheetName.length() > 31) {
         worksheetName = worksheetName.substring(0, 31);
      }
      numColumns = columns.length;

      out.write(" <Worksheet ss:Name=\"");
      out.write(worksheetName);
      out.write("\">\n");

      out.write("  <Table x:FullColumns=\"1\" x:FullRows=\"1\" ss:ExpandedColumnCount=\"");
      out.write(String.valueOf(numColumns));
      out.write("\">\n");

      for (ExcelColumn column : columns) {
         column.writeColumnDefinition(out);
      }

      if (columns[0].getName() != null) {

         rowBuffer = new String[numColumns];
         for (ExcelColumn column : columns) {
            writeCell(column.getName());
         }
         endRow();
      }

      inSheet = true;
   }

   @Override
   public void endSheet() throws IOException {
      out.write("  </Table>\n");
      out.write(" </Worksheet>\n");
      inSheet = false;
      numColumns = -1;
      ++numSheetsWritten;
   }

   @Override
   public void endWorkbook() throws IOException {
      try {
         if (inSheet) {
            endSheet();
         }
         if (activeSheetNum >= 0) {
            out.write(" <ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
            out.write("  <ActiveSheet>" + activeSheetNum + "</ActiveSheet>\n");
            out.write(" </ExcelWorkbook>\n");
         }
         out.write("</Workbook>\n");
      } finally {
         Lib.close(out);
      }
   }

   @Override
   public void endWorkbook(boolean close) throws IOException {
      try {
         if (inSheet) {
            endSheet();
         }
         if (activeSheetNum >= 0) {
            out.write(" <ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">\n");
            out.write("  <ActiveSheet>" + activeSheetNum + "</ActiveSheet>\n");
            out.write(" </ExcelWorkbook>\n");
         }
         out.write("</Workbook>\n");
      } finally {
         if (close) {
            Lib.close(out);
         } else {
            out.flush();
         }
      }
   }

   @Override
   protected void startRow() throws IOException {
      out.write("   <Row");
      if (rowHeight != 0.0) {
         out.write(String.format(" ss:AutoFitHeight=\"0\" ss:Height=\"%f\"", rowHeight));
      }
      out.write(">\n");

      rowBuffer = new String[numColumns];

      previouslyWrittenCellIndex = -1;
   }

   @Override
   public void writeEndRow() throws IOException {
      for (int i = 0; i < numColumns; i++) {
         if (rowBuffer[i] != null && rowBuffer[i].length() > 0) {
            out.write(rowBuffer[i]);
         }
      }
      out.write("   </Row>\n");
      rowBuffer = null;
   }

   @Override
   public void writeCellText(Object cellData, int cellIndex) throws IOException {
      if (cellIndex >= numColumns) {
         throw new OseeCoreException("ExcelWriter out of bounds: %d, index %d", numColumns, cellIndex);
      } else if (cellData == null) {
         rowBuffer[cellIndex] = null;
      } else {
         StringBuilder sb = new StringBuilder();

         sb.append("    <Cell");

         //Cell styles
         if (cellData instanceof Date) {
            sb.append(" ss:StyleID=\"OseeDate\"");
         } else if (applyStyle) {
            applyStyleToCell(sb, cellIndex);
         }

         if (previouslyWrittenCellIndex + 1 != cellIndex) { // use explicit index if at least one cell was skipped
            sb.append(" ss:Index=\"" + (cellIndex + 1) + "\"");
         }
         previouslyWrittenCellIndex = cellIndex;

         if (cellData instanceof String) {
            writeString(cellData, cellIndex, sb);
         } else if (cellData instanceof Number) {
            Number cellDataNum = (Number) cellData;
            sb.append("><Data ss:Type=\"Number\">");
            Xml.writeWhileHandlingCdata(sb, cellDataNum.toString());
            sb.append("</Data>");
         } else if (cellData instanceof Date) {
            Date cellDataDate = (Date) cellData;
            sb.append("><Data ss:Type=\"DateTime\">");
            String dateString = DateUtil.get(cellDataDate, "yyyy-MM-dd") + "T00:00:00.000";
            Xml.writeWhileHandlingCdata(sb, dateString);
            sb.append("</Data>");
         } else if (cellData instanceof CellData) {
            if (!((CellData) cellData).getHyperlink().isEmpty()) {
               if (!((CellData) cellData).getStyle().isEmpty()) {
                  sb.append(
                     " ss:StyleID=\"" + ((CellData) cellData).getStyle() + "\" ss:HRef=\"#'" + ((CellData) cellData).getHyperlink() + "'!A1\"");
               } else {
                  sb.append(
                     " ss:StyleID=\"OseeHyperlink\" ss:HRef=\"#'" + ((CellData) cellData).getHyperlink() + "'!A1\"");
               }
            } else if (!((CellData) cellData).getStyle().isEmpty()) {
               sb.append(" ss:StyleID=\"" + ((CellData) cellData).getStyle() + "\"");
            }
            if (!((CellData) cellData).getMergeAcross().isEmpty()) {
               sb.append(" ss:MergeAcross=\"" + ((CellData) cellData).getMergeAcross() + "\"");
            }
            if (!((CellData) cellData).getMergeDown().isEmpty()) {
               sb.append(" ss:MergeDown=\"" + ((CellData) cellData).getMergeDown() + "\"");
            }
            writeString(((CellData) cellData).getText(), cellIndex, sb);
         } else {
            sb.append("><Data ss:Type=\"String\">");
            Xml.writeWhileHandlingCdata(sb, cellData.toString());
            sb.append("</Data>");
         }
         sb.append("</Cell>\n");
         rowBuffer[cellIndex] = sb.toString();
      }
   }

   private void writeString(Object cellData, int cellIndex, StringBuilder sb) throws IOException {
      String cellDataStr = (String) cellData;
      if (!cellDataStr.equals("") && cellDataStr.charAt(0) == '=') {
         String value = cellDataStr.replaceAll("\"", "&quot;");
         sb.append(" ss:Formula=\"" + value + "\">");
      } else {
         boolean isRichText = richTextCell == cellIndex;
         if (isRichText) {
            sb.append("><ss:Data ss:Type=\"String\" xmlns=\"http://www.w3.org/TR/REC-html40\">");
         } else {
            sb.append("><Data ss:Type=\"String\">");
         }
         if (cellDataStr.equals("")) {
            sb.append(emptyStringRepresentation);
         } else {
            if (cellDataStr.length() > 32767) {
               sb.append(blobMessage);
            } else if (isRichText) {
               Xml.writeData(sb, cellDataStr);
            } else {
               Xml.writeWhileHandlingCdata(sb, cellDataStr);
            }
         }
         if (isRichText) {
            richTextCell = -1;
            sb.append("</ss:Data>");
         } else {
            sb.append("</Data>");
         }
         if (cellDataStr.length() > 32767) {
            sb.append("<EmbeddedClob>");
            Xml.writeWhileHandlingCdata(sb, cellDataStr);
            sb.append("</EmbeddedClob>");
         }
      }
   }

   /**
    * Needs to be called before write* operations are called.
    */
   public void setCellStyle(ExcelXmlWriter.STYLE style, int cellIndex) {
      applyStyle = true;
      if (style != STYLE.NONE) {
         mStyleMap.put(cellIndex, style.getText());
      }
   }

   public void setRichTextCell(int cellIndex) {
      richTextCell = cellIndex;
   }

   public void setCellStyle(String style, int cellIndex) {
      applyStyle = true;
      mStyleMap.put(cellIndex, style);
   }

   public void setCellColSpanWidth(int cellIndex, int colWidth) {
      applyStyle = true;
      mColSpanMap.put(cellIndex, colWidth);
   }

   public void setRowHeight(double rowHeight) {
      this.rowHeight = rowHeight;
   }

   private void applyStyleToCell(StringBuilder sb, int cellIndex) {
      String applyThisStyle = mStyleMap.remove(cellIndex);
      if (applyThisStyle != null) {
         sb.append(" ss:StyleID=\"" + applyThisStyle + "\"");
      }

      Integer colSpanWidth = mColSpanMap.remove(cellIndex);
      if (colSpanWidth != null) {
         sb.append(" ss:MergeAcross=\"" + colSpanWidth + "\"");
      }
      applyStyle = mStyleMap.size() > 0 || mColSpanMap.size() > 0;
   }

   public BufferedWriter getOut() {
      return out;
   }

   /*
    * @param sheetNum - the sheet number uses 0 based counting, i.e. 0 is the first sheet.
    */
   @Override
   public void setActiveSheet(int sheetNum) {
      if (sheetNum >= 0 && sheetNum < numSheetsWritten) {
         activeSheetNum = sheetNum;
      } else if (sheetNum < 0) {
         throw new OseeArgumentException("Cannot set active sheet less than zero");
      } else {
         throw new OseeArgumentException("Cannot set active sheet higher than the number of sheets written");
      }
   }

   public void writeHeaderRow(Object... row) throws IOException {
      for (int i = 0; i < row.length; i++) {
         writeCell(new CellData((String) row[i], Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING,
            "OseeBoldStyle"));
      }

      endRow();
   }
}