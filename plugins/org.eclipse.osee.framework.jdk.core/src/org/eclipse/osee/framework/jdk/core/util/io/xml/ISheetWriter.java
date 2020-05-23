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

package org.eclipse.osee.framework.jdk.core.util.io.xml;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public interface ISheetWriter {

   public void startSheet(String worksheetName, int columnCount) throws IOException;

   public void endSheet() throws IOException;

   public void setActiveSheet(int sheetNum);

   public void writeRow(Object... row) throws IOException;

   public void writeRow(Collection<?> row) throws IOException;

   public void writeCell(Object cellData) throws IOException;

   /**
    * @param cellData text value of cell
    * @param cellIndex zero-based index
    */
   public void writeCell(Object cellData, int cellIndex) throws IOException;

   public void writeEmptyCell() throws IOException;

   public void endRow() throws IOException;

   public void endWorkbook() throws IOException;

   public void endWorkbook(boolean closeFile) throws IOException;

}