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
package org.eclipse.osee.ote.ui.define.reports.html;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Roberto E. Escobar
 */
public class HtmlTableReport {
   private static String HTML_HEADER =
      "<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"><head>";
   private static String HTML_FOOTER = "</body></html>";

   private String reportTitle;
   private final HtmlActiveTable activeTable;

   public HtmlTableReport() {
      this.activeTable = new HtmlActiveTable();
      this.reportTitle = "Report";
   }

   public void setReportTitle(String title) {
      this.reportTitle = title;
   }

   public HtmlActiveTable getActiveTable() {
      return activeTable;
   }

   public String generate() throws IOException {
      StringBuilder builder = new StringBuilder();
      builder.append(HTML_HEADER);
      builder.append("<title>");
      builder.append(reportTitle);
      builder.append("</title>");
      builder.append(activeTable.generate());
      builder.append("</head>");
      builder.append(getReportSummary());
      builder.append("<br/>");
      builder.append("<div id=\"");
      builder.append(activeTable.getElementName());
      builder.append("\" style=\"width:100%;\">");
      builder.append("</div>");
      builder.append(HTML_FOOTER);
      return builder.toString();
   }

   private String getReportSummary() {
      StringBuilder builder = new StringBuilder();
      builder.append(
         "<div id=\"title\" class=\"x-grid3-hd-text\" style=\"color:#15428b;background:#ebf3fd;border:1px solid #aaccf6;\">");
      builder.append("<table class=\" x-grid3-summary-row x-grid3-hd-text\" style=\"width:95%;color:#15428b;\">");
      builder.append("<tr>");
      builder.append("<td style=\"text-align:left; font-size:14px;\"><b>");
      builder.append(reportTitle);
      builder.append("</b></td>");
      builder.append("<td style=\"text-align:right; font-size:14px;\"><b>");
      builder.append(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()));
      builder.append("</b></td>");
      builder.append("</tr>");
      builder.append("</table>");
      builder.append("</div>");
      return builder.toString();
   }
}
