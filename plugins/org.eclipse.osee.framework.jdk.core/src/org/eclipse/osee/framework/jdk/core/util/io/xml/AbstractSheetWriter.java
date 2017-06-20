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
import java.util.Collection;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractSheetWriter implements ISheetWriter {
   private boolean startRow;
   private int implicitCellIndex;

   public AbstractSheetWriter() {
      startRow = true;
      implicitCellIndex = 0;
   }

   /**
    * must be called by subclasses in their implementations of writeCell(String data, int cellIndex)
    */
   protected void startRowIfNecessary() throws IOException {
      if (startRow) {
         startRow();
         startRow = false;
      }
   }

   @Override
   public void writeRow(Collection<?> row) throws IOException {
      writeRow(row.toArray(new Object[row.size()]));
   }

   @Override
   public void writeRow(Object... row) throws IOException {
      for (int i = 0; i < row.length; i++) {
         writeCell(row[i], implicitCellIndex);
      }

      endRow();
   }

   /*
    * when calling writeCell with an index, the implicit index will be set to one greater than the given index
    */
   @Override
   public void writeCell(Object data, int cellIndex) throws IOException {
      startRowIfNecessary();
      implicitCellIndex = cellIndex + 1;
      writeCellText(data, cellIndex);
   }

   @Override
   public void endRow() throws IOException {
      startRowIfNecessary();
      startRow = true;
      implicitCellIndex = 0;
      writeEndRow();
   }

   /*
    * every time you call writeCell, the implicit index will be incremented
    */
   @Override
   public void writeCell(Object cellData) throws IOException {
      writeCell(cellData, implicitCellIndex);
   }

   protected abstract void startRow() throws IOException;

   protected abstract void writeEndRow() throws IOException;

   protected abstract void writeCellText(Object data, int cellIndex) throws IOException;

}
