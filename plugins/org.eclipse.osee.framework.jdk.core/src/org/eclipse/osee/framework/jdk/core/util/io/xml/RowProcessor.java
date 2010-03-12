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
   public abstract void processRow(String[] row);

   public abstract void processHeaderRow(String[] row);

   public abstract void processEmptyRow();

   public abstract void processCommentRow(String[] row);

   public abstract void reachedEndOfWorksheet();

   public abstract void foundStartOfWorksheet(String sheetName);

   public abstract void detectedRowAndColumnCounts(int rowCount, int columnCount);
}
