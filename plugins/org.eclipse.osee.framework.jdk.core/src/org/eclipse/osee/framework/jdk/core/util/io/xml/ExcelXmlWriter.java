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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;

/**
 * Test: @link ExcelXmlWriterTest
 * 
 * @author Ryan D. Brooks
 * @author Karol M. Wilk
 */
public final class ExcelXmlWriter extends AbstractSheetWriter {
   public static enum STYLE {
      BOLD,
      ITALICS,
      ERROR
   };

   public static final Pattern stylePattern = Pattern.compile("<Styles>.*</Styles>\\s*", Pattern.DOTALL);

   public static final String defaultEmptyStringXmlRep = "&#248;";
   public static final String defaultEmptyString = "\u00F8";
   public static final String blobMessage = "data stored in EmbeddedClob since longer than 32767 chars";

   public static final String XML_HEADER = //
      "<?xml version=\"1.0\"?>\n" + //
      "<?mso-application progid=\"Excel.Sheet\"?>\n" + //
      "<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n" + //
      " xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n" + //
      " xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n" + //
      " xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n" + //
      " xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n";

   public static final String DEFAULT_STYLE = //
      "<Styles>\n" + //
      "<Style ss:ID=\"Default\" ss:Name=\"Normal\">\n" + //
      "<Alignment ss:Vertical=\"Top\" ss:WrapText=\"1\"/>\n" + //
      "</Style>\n" + //
      "<Style ss:ID=\"OseeBoldStyle\">" + //
      "<Font x:Family=\"Swiss\" ss:Bold=\"1\"/>" + //
      "</Style>\n" + //
      "<Style ss:ID=\"OseeItalicStyle\">" + //
      "<Font x:Family=\"Swiss\" ss:Italic=\"1\"/>" + //
      "</Style>\n" + //
      "<Style ss:ID=\"OseeErrorStyle\">" + //
      "<Font x:Family=\"Swiss\" ss:Color=\"#FF0000\" ss:Bold=\"1\"/>" + //
      "</Style>\n" + //
      "</Styles>\n";

   private final BufferedWriter out;
   private boolean inSheet;
   private boolean startTable;
   private int columnCount;
   private final String emptyStringRepresentation;
   private int previousCellIndex;

   private boolean applyStyle = false;
   private final Map<Integer, STYLE> mStyleMap;

   private double rowHeight;
   private double columnWidth;

   public ExcelXmlWriter(Writer writer) throws IOException {
      this(writer, null);
   }

   public ExcelXmlWriter(File file) throws IOException {
      this(new FileWriter(file));
   }

   /**
    * Calls original constructor with provided style.
    * 
    * @param writer output
    * @param style Excel Style XML of form <Styles><Style/><Style/></Styles>
    */
   public ExcelXmlWriter(Writer writer, String style) throws IOException {
      super();

      out = new BufferedWriter(writer);
      mStyleMap = new HashMap<Integer, ExcelXmlWriter.STYLE>();
      emptyStringRepresentation = defaultEmptyStringXmlRep;
      out.write(XML_HEADER);

      if (Strings.isValid(style)) {
         if (stylePattern.matcher(style).matches()) {
            out.write(style);
         } else {
            throw new IllegalArgumentException("incomingStyle must match the pattern " + stylePattern);
         }
      }
   }

   @Override
   public void startSheet(String worksheetName, int columnCount) throws IOException {
      this.columnCount = columnCount;
      if (worksheetName.length() > 31) {
         worksheetName = worksheetName.substring(0, 31);
      }
      out.write(String.format(" <Worksheet ss:Name=\"%s\">\n", worksheetName));

      inSheet = true;
      startTable = true;
   }

   @Override
   public void endSheet() throws IOException {
      startTableIfNecessary();

      out.write("  </Table>\n");
      out.write(" </Worksheet>\n");
      inSheet = false;
      startTable = false;
   }

   @Override
   public void endWorkbook() throws IOException {
      if (inSheet) {
         endSheet();
      }
      out.write("</Workbook>\n");
      out.close();
   }

   @Override
   protected void startRow() throws IOException {
      startTableIfNecessary();

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

   private void startTableIfNecessary() throws IOException {
      if (startTable) {
         out.write("  <Table x:FullColumns=\"1\" x:FullRows=\"1\" ss:ExpandedColumnCount=\"" + columnCount + "\">\n");

         if (columnWidth != 0.0) {
            for (int i = 0; i < columnCount; i++) {
               out.write(String.format("   <Column ss:Width=\"%f\"/>\n", columnWidth));
            }
         }

         startTable = false;
      }
   }

   @Override
   public void writeCellText(String cellData, int cellIndex) throws IOException {
      if (cellData == null) {
         previousCellIndex = -1; // the next cell will need to use an explicit index
      } else {

         out.write("    <Cell");

         if (applyStyle) {
            applyStyleToCell(cellIndex);
         }

         if (previousCellIndex + 1 != cellIndex) { // use explicit index if at least one cell was skipped
            out.write(" ss:Index=\"" + (cellIndex + 1) + "\"");
         }
         previousCellIndex = cellIndex;
         if (!cellData.equals("") && cellData.charAt(0) == '=') {
            out.write(" ss:Formula=\"" + cellData + "\">");
         } else {
            out.write("><Data ss:Type=\"String\">");
            if (cellData.equals("")) {
               out.write(emptyStringRepresentation);
            } else {
               if (cellData.length() > 32767) {
                  out.write(blobMessage);
               } else {
                  Xml.writeAsCdata(out, cellData);
               }
            }
            out.write("</Data>");
            if (cellData.length() > 32767) {
               out.write("<EmbeddedClob>");
               Xml.writeAsCdata(out, cellData);
               out.write("</EmbeddedClob>");
            }
         }
         out.write("</Cell>\n");
      }
   }

   /**
    * Needs to be called before write* operations are called.
    */
   public void setCellStyle(ExcelXmlWriter.STYLE style, int cellIndex) {
      applyStyle = true;
      mStyleMap.put(cellIndex, style);
   }

   public void setRowHeight(double rowHeight) {
      this.rowHeight = rowHeight;
   }

   public void setColumnWidth(double columnWidth) {
      this.columnWidth = columnWidth;
   }

   private void applyStyleToCell(int cellIndex) throws IOException {
      ExcelXmlWriter.STYLE applyThisStyle = mStyleMap.remove(cellIndex);
      if (applyThisStyle != null) {
         switch (applyThisStyle) {
            case BOLD:
               out.write(" ss:StyleID=\"OseeBoldStyle\"");
               break;
            case ITALICS:
               out.write(" ss:StyleID=\"OseeItalicStyle\"");
               break;
            case ERROR:
               out.write(" ss:StyleID=\"OseeErrorStyle\"");
               break;
         }
      }
      applyStyle = mStyleMap.size() > 0;
   }

   public BufferedWriter getOut() {
      return out;
   }
}