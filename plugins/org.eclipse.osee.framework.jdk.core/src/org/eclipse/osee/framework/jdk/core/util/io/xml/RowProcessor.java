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

/**
 * @author Ryan D. Brooks
 */
public interface RowProcessor {
   void processRow(String[] row) throws Exception;

   void processHeaderRow(String[] row);

   void processEmptyRow();

   void processCommentRow(String[] row);

   void reachedEndOfWorksheet();

   void foundStartOfWorksheet(String sheetName) throws Exception;

   void detectedRowAndColumnCounts(int rowCount, int columnCount);
}
