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
package org.eclipse.osee.framework.jdk.core.util.io.xml.excel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;

/**
 * @author Ryan D. Brooks
 */
public class ExcelXmlWriter implements ISheetWriter {
   private BufferedWriter out;
   private boolean inSheet;
   private boolean startTable;
   private int columnCount;
   private String emptyStringRepresentation;
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.excel.ISheetWriter#startSheet(java.lang.String)
    */
   public void startSheet(String worksheetName) throws IOException {
      startSheet(worksheetName, -1);
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.excel.ISheetWriter#endSheet()
    */
   public void endSheet() throws IOException {
      if (startTable) {
         out.write("  <Table x:FullColumns=\"1\" x:FullRows=\"1\">\n");
         startTable = false;
      }
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.excel.ISheetWriter#writeRow(java.lang.String)
    */
   public void writeRow(String... row) throws IOException {
      if (startTable) {
         if (columnCount < 0) {
            columnCount = row.length;
         }
         out.write("  <Table x:FullColumns=\"1\" x:FullRows=\"1\" ss:ExpandedColumnCount=\"" + columnCount + "\">\n");
         startTable = false;
      }

      out.write("   <Row>\n");
      boolean cellSkipped = false;
      for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
         String cellData = row[cellIndex];
         if (cellData == null) {
            cellSkipped = true;
         } else {
            out.write("    <Cell");
            if (cellSkipped) {
               out.write(" ss:Index=\"" + (cellIndex + 1) + "\"");
               cellSkipped = false;
            }
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
      out.write("   </Row>\n");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.excel.ISheetWriter#writeRow(java.util.Collection)
    */
   public void writeRow(Collection<String> row) throws IOException {
      writeRow(row.toArray(new String[row.size()]));
   }
}