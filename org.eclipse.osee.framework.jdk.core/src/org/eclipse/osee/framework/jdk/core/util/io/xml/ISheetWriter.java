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
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public interface ISheetWriter {

   public void startSheet(String worksheetName, int columnCount) throws IOException;

   public void endSheet() throws IOException;

   public void writeRow(String... row) throws IOException;

   public void writeRow(Collection<String> row) throws IOException;

   public void writeCell(String cellData) throws IOException;

   /**
    * @param cellData text value of cell
    * @param cellIndex zero-based index
    * @throws IOException
    */
   public void writeCell(String cellData, int cellIndex) throws IOException;

   public void endRow() throws IOException;

   public void endWorkbook() throws IOException;

}