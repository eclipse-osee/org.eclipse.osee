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
      BOLD,
      ITALICS,
      ERROR,
      CENTERED,
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
      "<Style ss:ID=\"OseeBoldStyle\"><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style>\n" + //
      "<Style ss:ID=\"OseeItalicStyle\"><Font x:Family=\"Swiss\" ss:Italic=\"1\"/></Style>\n" + //
      "<Style ss:ID=\"OseeErrorStyle\"><Font x:Family=\"Swiss\" ss:Color=\"#FF0000\" ss:Bold=\"1\"/></Style>\n" + //
      "<Style ss:ID=\"OseeCentered\"><Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/></Style>\n" + //
      "<Style ss:ID=\"OseeWraped\"><Alignment ss:Vertical=\"Top\" ss:WrapText=\"1\"/></Style>";

   private final BufferedWriter out;
   private boolean inSheet;
   private final String emptyStringRepresentation;
   private int previousCellIndex;

   private boolean applyStyle = false;
   private final Map<Integer, String> mStyleMap;
   private final Map<Integer, Integer> mColSpanMap = new HashMap<Integer, Integer>();

   private double rowHeight;

   public ExcelXmlWriter(File file) throws IOException {
      this(new FileWriter(file));
   }

   public ExcelXmlWriter(Writer writer) throws IOException {
      this(writer, null);
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
      mStyleMap = new HashMap<Integer, String>();
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
      if (worksheetName.length() > 31) {
         worksheetName = worksheetName.substring(0, 31);
      }
      out.write(" <Worksheet ss:Name=\"");
      out.write(worksheetName);
      out.write("\">\n");

      out.write("  <Table x:FullColumns=\"1\" x:FullRows=\"1\" ss:ExpandedColumnCount=\"");
      out.write(String.valueOf(columns.length));
      out.write("\">\n");

      for (ExcelColumn column : columns) {
         column.writeColumnDefinition(out);
      }

      if (columns[0].getName() != null) {
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
   }

   @Override
   public void endWorkbook() throws IOException {
      try {
         if (inSheet) {
            endSheet();
         }
         out.write("</Workbook>\n");
      } finally {
         Lib.close(out);
      }
   }

   @Override
   protected void startRow() throws IOException {
      out.write("   <Row");
      if (rowHeight != 0.0) {
         out.write(String.format(" ss:AutoFitHeight=\"0\" ss:Height=\"%f\"", rowHeight));
      }
      out.write(">\n");
      previousCellIndex = -1;
   }

   @Override
   public void writeEndRow() throws IOException {
      out.write("   </Row>\n");
   }

   @Override
   public void writeCellText(Object cellData, int cellIndex) throws IOException {
      if (cellData == null) {
         previousCellIndex = -1; // the next cell will need to use an explicit index
      } else {

         out.write("    <Cell");

         //Cell styles
         if (cellData instanceof Date) {
            out.write(" ss:StyleID=\"OseeDate\"");
         } else if (applyStyle) {
            applyStyleToCell(cellIndex);
         }

         if (previousCellIndex + 1 != cellIndex) { // use explicit index if at least one cell was skipped
            out.write(" ss:Index=\"" + (cellIndex + 1) + "\"");
         }
         previousCellIndex = cellIndex;

         if (cellData instanceof String) {
            String cellDataStr = (String) cellData;
            if (!cellDataStr.equals("") && cellDataStr.charAt(0) == '=') {
               String value = cellDataStr.replaceAll("\"", "&quot;");
               out.write(" ss:Formula=\"" + value + "\">");
            } else {
               out.write("><Data ss:Type=\"String\">");
               if (cellDataStr.equals("")) {
                  out.write(emptyStringRepresentation);
               } else {
                  if (cellDataStr.length() > 32767) {
                     out.write(blobMessage);
                  } else {
                     Xml.writeWhileHandlingCdata(out, cellDataStr);
                  }
               }
               out.write("</Data>");
               if (cellDataStr.length() > 32767) {
                  out.write("<EmbeddedClob>");
                  Xml.writeWhileHandlingCdata(out, cellDataStr);
                  out.write("</EmbeddedClob>");
               }
            }
         } else if (cellData instanceof Number) {
            Number cellDataNum = (Number) cellData;
            out.write("><Data ss:Type=\"Number\">");
            Xml.writeWhileHandlingCdata(out, cellDataNum.toString());
            out.write("</Data>");
         } else if (cellData instanceof Date) {
            Date cellDataDate = (Date) cellData;
            out.write("><Data ss:Type=\"DateTime\">");
            String dateString = DateUtil.get(cellDataDate, "yyyy-MM-dd") + "T00:00:00.000";
            Xml.writeWhileHandlingCdata(out, dateString);
            out.write("</Data>");
         } else {
            out.write("><Data ss:Type=\"String\">");
            Xml.writeWhileHandlingCdata(out, cellData.toString());
            out.write("</Data>");
         }
         out.write("</Cell>\n");
      }
   }

   /**
    * Needs to be called before write* operations are called.
    */
   public void setCellStyle(ExcelXmlWriter.STYLE style, int cellIndex) {
      applyStyle = true;
      switch (style) {
         case BOLD:
            mStyleMap.put(cellIndex, "OseeBoldStyle");
            break;
         case ITALICS:
            mStyleMap.put(cellIndex, "OseeItalicStyle");
            break;
         case ERROR:
            mStyleMap.put(cellIndex, "OseeErrorStyle");
            break;
         case CENTERED:
            mStyleMap.put(cellIndex, "OseeCentered");
            break;
      }
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

   private void applyStyleToCell(int cellIndex) throws IOException {
      String applyThisStyle = mStyleMap.remove(cellIndex);
      if (applyThisStyle != null) {
         out.write(" ss:StyleID=\"" + applyThisStyle + "\"");
      }

      Integer colSpanWidth = mColSpanMap.remove(cellIndex);
      if (colSpanWidth != null) {
         out.write(" ss:MergeAcross=\"" + colSpanWidth + "\"");
      }
      applyStyle = mStyleMap.size() > 0 || mColSpanMap.size() > 0;
   }

   public BufferedWriter getOut() {
      return out;
   }
}