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

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Ryan D. Brooks
 */
public class ExtractReqPriority implements RowProcessor {
   private final HashMap<String, String> reqPriorities;

   public ExtractReqPriority(String excelMlPath) throws UnsupportedEncodingException, FileNotFoundException, IOException, SAXException {
      this.reqPriorities = new HashMap<>();

      File file = new File(excelMlPath);

      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new ExcelSaxHandler(this, true));
      xmlReader.parse(new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8")));
   }

   public HashMap<String, String> getReqPriorities() {
      return reqPriorities;
   }

   @Override
   public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
      // do nothing
   }

   @Override
   public void foundStartOfWorksheet(String sheetName) {
      // do nothing
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
   public void processHeaderRow(String[] row) {
      // do nothing
   }

   @Override
   public void processRow(String[] row) {
      // pick the highest priority specified in the workbook (in case there are multiple priorities for the same item)
      if (row[1] != null) {
         String priority = reqPriorities.get(row[1]);
         if (priority != null) {
            if (priority.compareTo(row[0]) > 0) {
               return;
            }
         }
         reqPriorities.put(row[1], row[0]);
      }
   }

   @Override
   public void reachedEndOfWorksheet() {
      // do nothing
   }
}