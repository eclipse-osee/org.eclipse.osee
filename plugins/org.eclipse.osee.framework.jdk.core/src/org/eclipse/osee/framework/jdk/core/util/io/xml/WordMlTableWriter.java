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

import java.io.IOException;

/**
 * @author Jeff C. Phillips
 */
public class WordMlTableWriter extends AbstractSheetWriter {
   private static final String TABLE_START =
      "<w:tbl><w:tblPr><w:tblBorders><w:top w:val=\"single\" w:sz=\"1\" /><w:left w:val=\"single\" w:sz=\"1\" /><w:bottom w:val=\"single\" w:sz=\"1\" /><w:right w:val=\"single\" w:sz=\"1\" /><w:insideH w:val=\"single\" w:sz=\"1\" /><w:insideV w:val=\"single\" w:sz=\"1\" /></w:tblBorders></w:tblPr>";
   private static final String TABLE_END = "</w:tbl>";
   private static final String START_TABLE_GRID = "<w:tblGrid>";
   private static final String END_TABLE_GRID = "</w:tblGrid>";
   private static final String TABLE_GRID = "<w:gridCol w:w=\"1024\" />";
   private static final String CELL_STRART = "<w:tc><w:tcPr><w:tcW w:w=\"1024\" /></w:tcPr><w:p><w:r><w:t>";
   private static final String CELL_END = "</w:t></w:r></w:p></w:tc>";
   private static final String ROW_START = "<w:tr>";
   private static final String ROW_END = "</w:tr>";
   private final Appendable str;
   private int columnSize;
   private boolean startTable;

   public WordMlTableWriter(Appendable str) {
      this.str = str;
   }

   @Override
   public void startSheet(String worksheetName, int columnCount) throws IOException {
      str.append(TABLE_START);
      columnSize = columnCount;
      startTable = true;
   }

   @Override
   public void endSheet() throws IOException {
      str.append(TABLE_END);
   }

   @Override
   protected void startRow() throws IOException {
      // column size is set when the first row is created.
      if (startTable) {
         writeTableGridData();
      }

      str.append(ROW_START);
   }

   @Override
   public void writeEndRow() throws IOException {
      str.append(ROW_END);
   }

   private void writeTableGridData() throws IOException {
      str.append(START_TABLE_GRID);

      for (int i = 0; i < columnSize; i++) {
         str.append(TABLE_GRID);
      }
      str.append(END_TABLE_GRID);
   }

   @Override
   public void writeCellText(Object cellData, int cellIndex) throws IOException {
      if (cellData instanceof String) {
         String cellDataStr = (String) cellData;
         str.append(CELL_STRART);
         str.append(cellDataStr);
         str.append(CELL_END);
      }
   }

   @Override
   public void endWorkbook() {
      // do nothing
   }

   @Override
   public void setActiveSheet(int sheetNum) {
      //
   }

   @Override
   public void endWorkbook(boolean closeFile) {
      //do nothing
   }

}
