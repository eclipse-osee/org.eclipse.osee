/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.define.rest.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.rest.GenericReportBuilder;
import org.eclipse.osee.define.rest.internal.reflection.TemplateParser;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author David W. Miller
 */
public final class PublishTemplateReport implements StreamingOutput {
   private final OrcsApi orcsApi;
   private final QueryFactory queryApi;
   private final IOseeBranch branch;
   private final ArtifactId view;
   private final ArtifactId reportTemplateArt;
   private final ActivityLog activityLog;
   private ExcelXmlWriter writer;
   private final GenericReportBuilder report;

   public PublishTemplateReport(ActivityLog activityLog, OrcsApi orcsApi, BranchId branch, ArtifactId view, ArtifactId templateArt) {
      this.activityLog = activityLog;
      this.orcsApi = orcsApi;
      this.queryApi = orcsApi.getQueryFactory();
      this.branch = queryApi.branchQuery().andId(branch).getResultsAsId().getExactlyOne();
      this.view = view;
      this.reportTemplateArt = templateArt;
      report = new GenericReportBuilder(branch, view, orcsApi);
   }

   @Override
   public void write(OutputStream output) {
      try {
         writer = new ExcelXmlWriter(new OutputStreamWriter(output, "UTF-8"));
         if (reportTemplateArt.isValid()) {
            writeReport();
         } else {
            writeReportFromGenericReportCode(); // default to basic subsystem code trace
         }
         writer.endWorkbook();
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }

   private void writeReport() throws IOException {
      TemplateParser parser = new TemplateParser(activityLog, orcsApi, branch, view, reportTemplateArt);
      parser.parseTemplateData(report);
      int numColumns = report.getColumnCount();
      writer.startSheet(parser.getTemplateArtifact().getName(), numColumns);
      finishFillingData();
   }

   private void writeReportFromGenericReportCode() throws IOException {
      GenericReportCode generic = new GenericReportCode();
      generic.traceCode(report);
      int numColumns = report.getColumnCount();
      writer.startSheet("Subsystem to Code trace", numColumns);
      finishFillingData();
   }

   private void finishFillingData() throws IOException {
      List<Object[]> data = new ArrayList<>();
      report.getDataRowsFromQuery(data);
      for (Object[] row : data) {
         writer.writeRow(row);
      }
      writer.endSheet();
   }
}