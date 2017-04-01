/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.traceability.report;

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

}
