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
import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ote.ui.define.reports.html.HtmlActiveTable;
import org.eclipse.osee.ote.ui.define.reports.html.HtmlActiveTableColumnData;
import org.eclipse.osee.ote.ui.define.reports.html.HtmlTableReport;

/**
 * @author Roberto E. Escobar
 */
public class HtmlReportWriter implements IReportWriter {

   private HtmlTableReport report;
   private String reportHtml;

   public HtmlReportWriter() {
      this.report = new HtmlTableReport();
      report.getActiveTable().setTableTitle("Results");
   }

   public void writeTitle(String title) {
      report.getActiveTable().setElementName("tbl" + new Date().getTime());
      report.setReportTitle(title);
   }

   public void writeHeader(String[] headers) {
      HtmlActiveTable table = report.getActiveTable();
      String[] metadata = new String[headers.length];
      Arrays.fill(metadata, "");
      for (int index = 0; index < headers.length; index++) {
         table.addColumn(new HtmlActiveTableColumnData(headers[index], metadata[index]));
      }
   }

   public void writeRow(String... cellData) {
      report.getActiveTable().addDataRow(cellData);
   }

   public int length() throws IOException {
      if (reportHtml == null) {
         generate();
      }
      return reportHtml.length();
   }

   public String getReport() throws IOException {
      if (reportHtml == null) {
         generate();
      }
      return reportHtml;
   }

   public void writeToOutput(OutputStream outputStream) throws IOException {
      outputStream.write(getReport().getBytes("UTF-8"));
   }

   private void generate() throws IOException {
      reportHtml = report.generate();
   }
}
