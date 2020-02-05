/*******************************************************************************
 * Copyright (c) 2016 Boeing.
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;

/**
 * @author Ryan D. Brooks
 */
public final class TextSheetWriter extends AbstractSheetWriter {

   private final Map<String, CharBackedInputStream> sheetMap;
   private CharBackedInputStream currentStream;
   private boolean wasDataAdded;
   private final String lineSeparator;
   private int columnCount;

   public TextSheetWriter() {
      sheetMap = new LinkedHashMap<>();
      lineSeparator = System.getProperty("line.separator", "\r\n");
      currentStream = null;
      wasDataAdded = false;
   }

   public boolean hasData() {
      return wasDataAdded;
   }

   public CharBackedInputStream getInputStream() {
      return currentStream;
   }

   public Set<String> getSheetNames() {
      return sheetMap.keySet();
   }

   public CharBackedInputStream getSheetBackerByName(String tabName) {
      return sheetMap.get(tabName);
   }

   @Override
   protected void startRow() {
      // do nothing
   }

   @Override
   protected void writeCellText(Object data, int cellIndex) throws IOException {
      if (data instanceof String) {
         String dataStr = (String) data;
         if (Strings.isValid(dataStr)) {
            currentStream.append(dataStr);
         }
         if (cellIndex < columnCount - 1) {
            currentStream.append("\t");
         }
         wasDataAdded = true;
      }
   }

   @Override
   protected void writeEndRow() throws IOException {
      currentStream.append(lineSeparator);
      wasDataAdded = true;
   }

   @Override
   public void endSheet() {
      currentStream = null;
   }

   @Override
   public void endWorkbook() {
      // do nothing
   }

   @Override
   public void startSheet(String worksheetName, int columnCount) throws IOException {
      this.columnCount = columnCount;
      currentStream = new CharBackedInputStream();
      sheetMap.put(worksheetName, currentStream);
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
