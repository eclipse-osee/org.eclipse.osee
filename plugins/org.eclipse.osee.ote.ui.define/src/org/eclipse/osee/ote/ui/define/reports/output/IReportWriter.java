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
