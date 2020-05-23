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
import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ote.ui.define.reports.html.HtmlActiveTable;
import org.eclipse.osee.ote.ui.define.reports.html.HtmlActiveTableColumnData;
import org.eclipse.osee.ote.ui.define.reports.html.HtmlTableReport;

/**
 * @author Roberto E. Escobar
 */
public class HtmlReportWriter implements IReportWriter {

   private final HtmlTableReport report;
   private String reportHtml;

   public HtmlReportWriter() {
      this.report = new HtmlTableReport();
      report.getActiveTable().setTableTitle("Results");
   }

   @Override
   public void writeTitle(String title) {
      report.getActiveTable().setElementName("tbl" + new Date().getTime());
      report.setReportTitle(title);
   }

   @Override
   public void writeHeader(String[] headers) {
      HtmlActiveTable table = report.getActiveTable();
      String[] metadata = new String[headers.length];
      Arrays.fill(metadata, "");
      for (int index = 0; index < headers.length; index++) {
         table.addColumn(new HtmlActiveTableColumnData(headers[index], metadata[index]));
      }
   }

   @Override
   public void writeRow(String... cellData) {
      report.getActiveTable().addDataRow(cellData);
   }

   @Override
   public int length() throws IOException {
      if (reportHtml == null) {
         generate();
      }
      return reportHtml.length();
   }

   @Override
   public String getReport() throws IOException {
      if (reportHtml == null) {
         generate();
      }
      return reportHtml;
   }

   @Override
   public void writeToOutput(OutputStream outputStream) throws IOException {
      outputStream.write(getReport().getBytes("UTF-8"));
   }

   private void generate() throws IOException {
      reportHtml = report.generate();
   }
}
