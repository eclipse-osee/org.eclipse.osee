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

import java.util.Arrays;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class ExcelSaxHandler extends AbstractSaxHandler {
   private String[] row;
   private int cellIndex;
   private int rowIndex;
   private final RowProcessor rowProcessor;
   private boolean isRowHeader;
   private final boolean firstRowIsHeader;
   private final boolean multiTable;
   private boolean rowEmpty;

   public ExcelSaxHandler(RowProcessor rowProcessor, boolean firstRowIsHeader, boolean enableMultiTableSupport) {
      super();
      this.rowProcessor = rowProcessor;
      this.firstRowIsHeader = firstRowIsHeader;
      this.multiTable = enableMultiTableSupport;
      rowIndex = 0;
   }

   public ExcelSaxHandler(RowProcessor rowProcessor, boolean hasHeaderRow) {
      this(rowProcessor, hasHeaderRow, false);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#startElementFound(java.lang.String,
    *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
    */
   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (localName.equalsIgnoreCase("Row")) {
         cellIndex = -1; // so that upon finding the first cell start element the index becomes 0
         rowEmpty = true;
         // null out because any empty cells would otherwise contain data from the previous row
         Arrays.fill(row, 0, row.length, null);

         String indexStr = attributes.getValue("ss:Index");
         if (indexStr != null) {
            int oldRowIndex = rowIndex;
            rowIndex = Integer.parseInt(indexStr) - 1; // translate from Excel's 1-based index to
            // our 0-based index
            for (int i = oldRowIndex; i < rowIndex; i++) {
               rowProcessor.processEmptyRow();
            }
         } else {
            rowIndex++;
         }
      } else if (localName.equalsIgnoreCase("Cell")) {
         String indexStr = attributes.getValue("ss:Index");
         if (indexStr != null) {
            cellIndex = Integer.parseInt(indexStr) - 1; // translate from Excel's 1-based index to
            // our 0-based index
         } else {
            cellIndex++;
         }
      } else if (localName.equalsIgnoreCase("Table")) {
         String columnCountStr = attributes.getValue("ss:ExpandedColumnCount");
         if (columnCountStr == null) {
            throw new IllegalArgumentException("missing ss:ExpandedColumnCount attribute of Table element");
         }
         row = new String[Integer.parseInt(columnCountStr)];

         String rowCountStr = attributes.getValue("ss:ExpandedRowCount");
         if (rowCountStr == null) {
            rowCountStr = "0";
         }
         rowProcessor.detectedRowAndColumnCounts(Integer.parseInt(rowCountStr), row.length);
      } else if (localName.equalsIgnoreCase("Worksheet")) {
         isRowHeader = firstRowIsHeader; // next non-empty row will be considered a header (if
         // applicable)
         rowProcessor.foundStartOfWorksheet(attributes.getValue("ss:Name"));
         // System.out.println("Worksheet =" +attributes.getValue("ss:Name"));
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#endElementWithClearContents(java.lang.String,
    *      java.lang.String, java.lang.String)
    */
   @Override
   public void endElementFound(String uri, String localName, String qName) throws SAXException {
      if (localName.equalsIgnoreCase("Data")) {
         String contentStr = getContents();
         if (!contentStr.equals(ExcelXmlWriter.blobMessage)) {
            if (contentStr.equals(ExcelXmlWriter.defaultEmptyString)) {
               row[cellIndex] = "";
            } else {
               row[cellIndex] = contentStr;
            }
         }
         rowEmpty = false;
      } else if (localName.equalsIgnoreCase("Row")) {
         if (rowEmpty) {
            rowProcessor.processEmptyRow();
            if (multiTable) {
               isRowHeader = true; // next non-empty row will be considered a header
            }
         } else if (row[0] != null && row[0].startsWith("#")) {
            rowProcessor.processCommentRow(row);
         } else if (isRowHeader) {
            isRowHeader = false;
            rowProcessor.processHeaderRow(row);
         } else {
            rowProcessor.processRow(row);
         }
      } else if (localName.equalsIgnoreCase("Worksheet")) {
         rowProcessor.reachedEndOfWorksheet();
      } else if (localName.equalsIgnoreCase("EmbeddedClob")) {
         row[cellIndex] = getContents();
      }
   }
}