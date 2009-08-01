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
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;

/**
 * @author Ryan D. Brooks
 */
public class ExcelXmlWriter extends AbstractSheetWriter {
   private BufferedWriter out;
   private boolean inSheet;
   private boolean startTable;
   private int columnCount;
   private String emptyStringRepresentation;
   private int previuosCellIndex;

   public static final String defaultEmptyStringXmlRep = "&#248;";
   public static final String defaultEmptyString = "\u00F8";
   public static final String blobMessage = "data stored in EmbeddedClob since longer than 32767 chars";

   public ExcelXmlWriter(Writer writer) throws IOException {
      super();
      out = new BufferedWriter(writer);
      emptyStringRepresentation = defaultEmptyStringXmlRep;

      out.write("<?xml version=\"1.0\"?>\n");
      out.write("<?mso-application progid=\"Excel.Sheet\"?>\n");
      out.write("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
      out.write(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n");
      out.write(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n");
      out.write(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
      out.write(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n");
   }

   public ExcelXmlWriter(File file) throws IOException {
      this(new FileWriter(file));
   }

   public void startSheet(String worksheetName, int columnCount) throws IOException {
      this.columnCount = columnCount;
      if (worksheetName.length() > 31) {
         worksheetName = worksheetName.substring(0, 31);
      }
      out.write(String.format(" <Worksheet ss:Name=\"%s\">\n", worksheetName));

      inSheet = true;
      startTable = true;
   }

   public void endSheet() throws IOException {
      startTableIfNecessary();

      out.write("  </Table>\n");
      out.write(" </Worksheet>\n");
      inSheet = false;
      startTable = false;
   }

   public void endWorkbook() throws IOException {
      if (inSheet) {
         endSheet();
      }
      out.write("</Workbook>\n");
      out.close();
   }

   protected void startRow() throws IOException {
      startTableIfNecessary();

      out.write("   <Row>\n");
      previuosCellIndex = -1;
   }

   @Override
   public void writeEndRow() throws IOException {
      out.write("   </Row>\n");
   }

   private void startTableIfNecessary() throws IOException {
      if (startTable) {
         out.write("  <Table x:FullColumns=\"1\" x:FullRows=\"1\" ss:ExpandedColumnCount=\"" + columnCount + "\">\n");
         startTable = false;
      }
   }

   @Override
   public void writeCellText(String cellData, int cellIndex) throws IOException {
      if (cellData == null) {
         previuosCellIndex = -1; // the next cell will need to use an explicit index
      } else {
         out.write("    <Cell");
         if (previuosCellIndex + 1 != cellIndex) { // use explicit index if at least one cell was skipped 
            out.write(" ss:Index=\"" + (cellIndex + 1) + "\"");
         }
         previuosCellIndex = cellIndex;
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
}