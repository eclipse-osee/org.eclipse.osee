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
package org.eclipse.osee.ote.ui.define.reports;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.core.client.server.HttpRequest;
import org.eclipse.osee.framework.core.client.server.HttpResponse;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.client.server.IHttpServerRequest;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;
import org.eclipse.osee.ote.ui.define.Activator;
import org.eclipse.osee.ote.ui.define.reports.output.IReportWriter;
import org.eclipse.osee.ote.ui.define.reports.output.OutputFactory;
import org.eclipse.osee.ote.ui.define.reports.output.OutputFormat;
import org.eclipse.osee.ote.ui.define.utilities.SelectionHelper;
import org.eclipse.osee.ote.ui.define.views.TestRunView;

/**
 * @author Roberto E. Escobar
 */
public class HttpReportRequest implements IHttpServerRequest {
   private static final String REQUEST_TYPE = "GET.REPORT";
   private static final String REPORT_ID = "id";
   private static final String REPORT_FORMAT = "format";
   private static final String REPORT_SOURCE = "source";
   private static final String PREVIEW_SIZE = "preview";

   @Override
   public String getRequestType() {
      return REQUEST_TYPE;
   }

   public static String getUrl(String id, String format, String source) {
      return getUrl(id, format, source, -1);
   }

   public static String getUrl(String id, String format, String source, int preview) {
      Map<String, String> parameters = new HashMap<>();
      parameters.put(REPORT_ID, id);
      parameters.put(REPORT_FORMAT, format);
      parameters.put(REPORT_SOURCE, source);
      if (preview > -1) {
         parameters.put(PREVIEW_SIZE, Integer.toString(preview));
      }
      return HttpUrlBuilderClient.getInstance().getUrlForLocalSkynetHttpServer(HttpReportRequest.REQUEST_TYPE,
         parameters);
   }

   @Override
   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
      if (httpRequest.getOriginatingAddress().isLoopbackAddress()) {
         String reportId = httpRequest.getParameter(REPORT_ID);
         String format = httpRequest.getParameter(REPORT_FORMAT);

         try {
            OutputFormat outputFormat = OutputFormat.fromString(format);
            ITestRunReport report = ExtensionDefinedReports.getInstance().getReportGenerator(reportId);
            report.gatherData(new NullProgressMonitor(), getSourceData(httpRequest));

            IReportWriter writer = OutputFactory.getReportWriter(outputFormat);
            writer.writeTitle(report.getTitle());
            writer.writeHeader(report.getHeader());
            String[][] body = report.getBody();
            for (int index = 0; index < body.length; index++) {
               writer.writeRow(body[index]);
            }
            report.clear();

            if (outputFormat.equals(OutputFormat.HTML) != true) {
               httpResponse.setReponseHeader("Accept-Ranges", "bytes");
               httpResponse.setContentEncoding("ISO-8859-1");
               String fileName = URLEncoder.encode(OutputFactory.getOutputFilename(outputFormat, reportId), "UTF-8");
               httpResponse.setContentDisposition(String.format("attachment; filename=%s", fileName));
            }
            httpResponse.setContentType(OutputFactory.getContentType(outputFormat));
            httpResponse.sendResponseHeaders(200, writer.length());
            writer.writeToOutput(httpResponse.getOutputStream());

         } catch (Exception ex) {
            handleException(httpRequest, httpResponse, ex);
         }
      }
   }

   private ArtifactTestRunOperator[] getSourceData(HttpRequest httpRequest) {
      String source = httpRequest.getParameter(REPORT_SOURCE);

      List<ArtifactTestRunOperator> toReturn = new ArrayList<>();
      if (source.equals("local")) {
         LocalSourceSelection selection = new LocalSourceSelection();
         Displays.ensureInDisplayThread(selection);
         ArtifactTestRunOperator[] data = selection.getArtifacts();
         addData(httpRequest, data, toReturn);
      }
      return toReturn.toArray(new ArtifactTestRunOperator[toReturn.size()]);
   }

   private void addData(HttpRequest httpRequest, ArtifactTestRunOperator[] source, List<ArtifactTestRunOperator> destination) {
      String previewSize = httpRequest.getParameter(PREVIEW_SIZE);
      if (Strings.isValid(previewSize)) {
         int size = 5;
         try {
            size = Integer.parseInt(previewSize);
         } catch (Exception ex) {
            size = 5;
         }
         if (size > source.length) {
            size = source.length;
         }
         for (int index = 0; index < size; index++) {
            destination.add(source[index]);
         }
      } else {
         destination.addAll(Arrays.asList(source));
      }
   }

   private void handleException(HttpRequest httpRequest, HttpResponse httpResponse, Exception ex) {
      OseeLog.log(Activator.class, Level.SEVERE, "Exception occurred.", ex);
   }

   private final class LocalSourceSelection implements Runnable {

      private ArtifactTestRunOperator[] artifacts;
      private boolean done = false;

      public ArtifactTestRunOperator[] getArtifacts() {
         while (this.isDone() != true) {
            ;
         }
         return artifacts;
      }

      @Override
      public void run() {
         done = false;
         List<ArtifactTestRunOperator> toReturn = new ArrayList<>();
         StructuredViewer viewer = TestRunView.getViewer();
         if (viewer != null) {
            toReturn.addAll(SelectionHelper.getInstance().getSelections(viewer));
         }
         artifacts = toReturn.toArray(new ArtifactTestRunOperator[toReturn.size()]);
         done = true;
      }

      public boolean isDone() {
         return done;
      }
   }
}
