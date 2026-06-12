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

package org.eclipse.osee.orcs.rest.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.util.IOseeEmail;
import org.eclipse.osee.framework.core.util.OseeEmail.BodyType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.writers.PublishTemplateReport;
import org.eclipse.osee.orcs.rest.model.ReportEndpoint;

/**
 * @author David W. Miller
 */
public final class ReportEndpointImpl implements ReportEndpoint {

   private final OrcsApi orcsApi;

   public ReportEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public Response getReportFromTemplate(BranchId branch, ArtifactId view, ArtifactId templateArt) {
      StreamingOutput streamingOutput = new PublishTemplateReport(orcsApi, branch, view, templateArt);
      String fileName = String.format("Generic_Trace_Report_%s.xml", Lib.getDateTimeString());

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   @Override
   public Response getReportFromTemplateAsync(BranchId branch, ArtifactId view, ArtifactId templateArt,
      String emailRecipient) {
      String jsonResponse = "";
      try {
         if (EmailUtil.isEmailInValid(emailRecipient)) {
            throw new OseeArgumentException("Invalid Email Address");
         }
         String fileName = String.format("Generic_Trace_Report_%s.xml", Lib.getDateTimeString());
         String dataPath = orcsApi.getSystemProperties().getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);
         File publishDir = new File(dataPath, "publish");
         if (!publishDir.exists()) {
            publishDir.mkdirs();
         }
         File reportFile = new File(publishDir, fileName);
         String serverAddress = OseeClient.getOseeApplicationServer();
         String downloadLink = String.format("%s/orcs/resources/publish?path=%s", serverAddress, fileName);
         Thread reportThread = new Thread("Async Report Generator") {
            @Override
            public void run() {
               try {
                  PublishTemplateReport report = new PublishTemplateReport(orcsApi, branch, view, templateArt);
                  try (FileOutputStream fos = new FileOutputStream(reportFile)) {
                     report.write(fos);
                  }

                  String subject = "Report Generation Complete";
                  String body = String.format(
                     "Your report has been generated successfully.\n\nFile: %s\nBranch: %s\nView: %s\nTemplate: %s\n\nDownload your report here:\n%s",
                     fileName, branch, view, templateArt, downloadLink);

                  IOseeEmail emailMessage = orcsApi.getEmailService().create(Collections.singletonList(emailRecipient),
                     emailRecipient, emailRecipient, subject, body, BodyType.Text, Collections.emptySet(),
                     "Report generation complete.");
                  emailMessage.send();
               } catch (Exception ex) {
                  throw new OseeCoreException("Error generating async generic report: " + Lib.exceptionToString(ex));
               }
            }
         };
         reportThread.start();
         jsonResponse = String.format(
            "{\"status\": \"Report generation started\", \"fileName\": \"%s\", \"branch\": \"%s\", \"view\": \"%s\", \"template\": \"%s\", \"emailRecipient\": \"%s\", \"downloadLink\": \"%s\"}",
            fileName, branch, view, templateArt, emailRecipient, downloadLink);
      } catch (Exception ex) {
         String errorJson = String.format("{\"error\": \"%s\"}", ex.getMessage().replace("\"", "\\\""));
         return Response.serverError().entity(errorJson).build();
      }

      return Response.ok(jsonResponse).build();
   }

}
