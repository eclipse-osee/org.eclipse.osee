/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.orcs.writer.model.reader.OwCollector;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Donald G. Dunne
 */
public class OrcsWriterExcelReader {

   private OwCollector collector;
   private final XResultData result;

   public OrcsWriterExcelReader(XResultData result) throws Exception {
      this.result = result;
   }

   public void run(URI source) throws SAXException, IOException, UnsupportedEncodingException, MalformedURLException {
      collector = new OwCollector();
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new ExcelSaxHandler(new ExcelRowProcessor(collector, result), true));
      InputStreamReader inputStreamReader = new InputStreamReader(source.toURL().openStream(), "UTF-8");
      xmlReader.parse(new InputSource(inputStreamReader));
      inputStreamReader.close();
   }

   public void run(InputStream source) throws SAXException, IOException, UnsupportedEncodingException, MalformedURLException {
      collector = new OwCollector();
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new ExcelSaxHandler(new ExcelRowProcessor(collector, result), true));
      xmlReader.parse(new InputSource(source));
   }

   private static final class ExcelRowProcessor implements RowProcessor {

      private final OwCollector collector;
      private OrcsWriterSheetProcessorForCreateUpdate createSheet;
      private OrcsWriterSheetProcessorForSettings settingsSheet;
      private String sheetName = "";
      private final XResultData result;
      private OrcsWriterSheetProcessorForCreateUpdate updateSheet;

      public ExcelRowProcessor(OwCollector collector, XResultData result) {
         this.collector = collector;
         this.result = result;
      }

      @Override
      public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
         // do nothing
      }

      @Override
      public void foundStartOfWorksheet(String sheetName) throws OseeCoreException {
         System.out.println("Processing Sheet " + sheetName);
         this.sheetName = sheetName;
         if (sheetName.equals(OrcsWriterUtil.CREATE_SHEET_NAME)) {
            createSheet = new OrcsWriterSheetProcessorForCreateUpdate(collector, result, true);
            return;
         } else if (sheetName.equals(OrcsWriterUtil.UPDATE_SHEET_NAME)) {
            updateSheet = new OrcsWriterSheetProcessorForCreateUpdate(collector, result, false);
            return;
         } else if (sheetName.equals(OrcsWriterUtil.INSTRUCTIONS_AND_SETTINGS_SHEET_NAME)) {
            settingsSheet = new OrcsWriterSheetProcessorForSettings(collector, result);
            return;
         }
      }

      @Override
      public void processCommentRow(String[] row) {
         // do nothing
      }

      @Override
      public void processEmptyRow() {
         // do nothing
      }

      @Override
      public void processHeaderRow(String[] headerRow) {
         if (isCreateSheet()) {
            createSheet.processHeaderRow(headerRow);
         } else if (isUpdateSheet()) {
            updateSheet.processHeaderRow(headerRow);
         }
      }

      private boolean isCreateSheet() {
         return sheetName.equals(OrcsWriterUtil.CREATE_SHEET_NAME);
      }

      private boolean isUpdateSheet() {
         return sheetName.equals(OrcsWriterUtil.UPDATE_SHEET_NAME);
      }

      private boolean isSettingsSheet() {
         return sheetName.equals(OrcsWriterUtil.INSTRUCTIONS_AND_SETTINGS_SHEET_NAME);
      }

      @Override
      public void processRow(String[] row) throws OseeCoreException {
         if (isCreateSheet()) {
            processCreateSheetRow(row);
         } else if (isUpdateSheet()) {
            processUpdateSheetRow(row);
         } else if (isSettingsSheet()) {
            settingsSheet.processRow(row);
         }
      }

      private void processUpdateSheetRow(String[] row) {
         updateSheet.processRow(row);
      }

      private void processCreateSheetRow(String[] row) {
         createSheet.processRow(row);
      }

      @Override
      public void reachedEndOfWorksheet() {
         // do nothing
      }

   }

   public OwCollector getCollector() {
      return collector;
   }
}
