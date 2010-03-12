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
package org.eclipse.osee.ote.ui.define.reports.output;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Roberto E. Escobar
 */
public interface IReportWriter {

   public void writeTitle(String title);

   public void writeHeader(String[] headers);

   public void writeRow(String... cellData);

   public int length() throws Exception;

   public String getReport() throws IOException;

   public void writeToOutput(OutputStream outputStream) throws IOException;

}
