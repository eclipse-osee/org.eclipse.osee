/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.define.ide.traceability.report;

import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;

/**
 * @author Roberto E. Escobar
 */
public interface ISimpleTable {

   public String getWorksheetName();

   public int getColumnCount();

   public String getHeader();

   public String getHeaderStyles();

   public void generateBody(ExcelXmlWriter sheetWriter) throws Exception;

   default void initializeSheet(ExcelXmlWriter sheetWriter) throws Exception {
      // do nothing
   }

}
