/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.jdk.core.util.io.excel.pojo;

import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.ECell;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EFile;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EHeaderCell;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EHeaderRow;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.ERow;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EWorksheet;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Read Excel xml workbook into generic EFile java pojo. This abstracts the reading of the excel file from the data
 * that's gained. Also provides for easier testing as tests do not have to start with excel files, but instead can
 * assume they were read in to pojo correctly and test from there.
 *
 * @author Donald G. Dunne
 */
@SuppressWarnings("deprecation")
public class ExcelToPojoOperation {

   private final File file;

   public ExcelToPojoOperation(String excelFile) {
      this(new File(excelFile));
   }

   public ExcelToPojoOperation(File file) {
      this.file = file;
   }

   public EFile run() {

      EFile eFile = new EFile(file.getAbsolutePath());
      eFile.getResults().log("Reading input: " + file.getAbsolutePath());
      try {
         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
         InputStreamReader inputStream = null;
         try {
            xmlReader.setContentHandler(new ExcelSaxHandler(new InternalRowProcessor(eFile), true));
            inputStream = new InputStreamReader(file.toURL().openStream(), "UTF-8");
            xmlReader.parse(new InputSource(inputStream));
         } catch (Exception ex) {
            eFile.getResults().errorf("Exception in parsing import (see log for details) %s\n",
               (Strings.isValid(ex.getLocalizedMessage()) ? ex.getLocalizedMessage() : ""));
         } finally {
            if (inputStream != null) {
               inputStream.close();
            }
         }
      } catch (Exception ex) {
         eFile.getResults().errorf("Exception reading input: %s", Lib.exceptionToString(ex));
      }
      return eFile;
   }

   private final static class InternalRowProcessor implements RowProcessor {

      private int rowNum = 1;
      private final XResultData rd;
      private EWorksheet eSheet;
      private final EFile eFile;
      private final Map<Integer, EHeaderCell> idToHeaderCell = new HashMap<>();

      protected InternalRowProcessor(EFile eFile) {
         this.eFile = eFile;
         this.rd = eFile.getResults();
      }

      @Override
      public void processEmptyRow() {
         // do nothing
      }

      @Override
      public void processCommentRow(String[] row) {
         // do nothing
      }

      @Override
      public void reachedEndOfWorksheet() {
         // do nothing
      }

      @Override
      public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
         // do nothing
      }

      @Override
      public void foundStartOfWorksheet(String sheetName) {
         eSheet = new EWorksheet(eFile.getWorkbook(), sheetName);
      }

      @Override
      public void processHeaderRow(String[] headerRow) {
         EHeaderRow hRow = new EHeaderRow(eSheet);
         int x = 1;
         for (String headerStr : headerRow) {
            EHeaderCell hCell = new EHeaderCell(hRow, headerStr, x);
            idToHeaderCell.put(x - 1, hCell);
            x++;
         }
      }

      @Override
      public void processRow(String[] cols) {
         rowNum++;

         boolean fullRow = false;
         for (int i = 0; i < cols.length; i++) {
            if (Strings.isValid(cols[i])) {
               fullRow = true;
               break;
            }
         }
         if (!fullRow) {
            rd.warning("Empty Row Found => " + rowNum + " skipping...");
            return;
         }

         ERow row = new ERow(eSheet, rowNum);
         for (int colNum = 0; colNum < cols.length; colNum++) {
            EHeaderCell hCell = idToHeaderCell.get(colNum);
            String value = cols[colNum];
            if (value == null) {
               value = "";
            }
            new ECell(row, hCell, value);
         }
      }

   }

}
