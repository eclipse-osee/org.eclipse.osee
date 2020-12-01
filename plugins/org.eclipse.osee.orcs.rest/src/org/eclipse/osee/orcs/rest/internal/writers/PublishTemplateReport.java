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

package org.eclipse.osee.orcs.rest.internal.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
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
   private ExcelXmlWriter writer;
   private final GenericReportBuilder report;
   private final XResultData results;

   public PublishTemplateReport(OrcsApi orcsApi, BranchId branch, ArtifactId view, ArtifactId templateArt) {
      this.orcsApi = orcsApi;
      this.queryApi = orcsApi.getQueryFactory();
      this.branch = queryApi.branchQuery().andId(branch).getResultsAsId().getExactlyOne();
      this.view = view;
      this.reportTemplateArt = templateArt;
      this.results = new XResultData();
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
      TemplateParser parser = new TemplateParser(orcsApi, branch, view, reportTemplateArt, results);
      parser.parseTemplateData(report);
      if (!results.isErrors()) {
         int numColumns = report.getColumnCount();
         writer.startSheet(parser.getTemplateArtifact().getName(), numColumns);
         finishFillingData();
         writer.endSheet();
      }
      writeResults();
   }

   private void writeResults() throws IOException {
      writer.startSheet("DebugInfo", 1);
      Object[] row = new String[1];
      row[0] = "Result Text";
      writer.writeRow(row);
      for (String result : results.getResults()) {
         row[0] = result;
         writer.writeRow(row);
      }
      writer.endSheet();
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
   }
}