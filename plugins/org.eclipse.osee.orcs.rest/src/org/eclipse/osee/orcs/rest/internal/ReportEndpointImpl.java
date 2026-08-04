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
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.core.util.IOseeEmail;
import org.eclipse.osee.framework.core.util.OseeEmail.BodyType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.writers.PublishTemplateReport;
import org.eclipse.osee.orcs.rest.internal.writers.PublishTemplateReportHtml;
import org.eclipse.osee.orcs.rest.internal.writers.PublishTemplateReportXlsx;
import org.eclipse.osee.orcs.rest.model.ReportEndpoint;
import org.eclipse.osee.orcs.rest.model.ReportFormat;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author David W. Miller
 */
public final class ReportEndpointImpl implements ReportEndpoint {

   private final OrcsApi orcsApi;
   private final ExecutorAdmin executorAdmin;

   public ReportEndpointImpl(OrcsApi orcsApi, ExecutorAdmin executorAdmin) {
      this.orcsApi = orcsApi;
      this.executorAdmin = executorAdmin;
   }

   @Override
   public Response getReportFromTemplate(BranchId branch, ArtifactId view, ArtifactId templateArt, String format,
      String emailRecipient) {
      ReportFormat reportFormat = ReportFormat.fromString(format);

      if (emailRecipient != null && !emailRecipient.isBlank()) {
         return generateReportAsync(branch, view, templateArt, emailRecipient, reportFormat);
      }
      return generateReportSync(branch, view, templateArt, reportFormat);
   }

   private Response generateReportSync(BranchId branch, ArtifactId view, ArtifactId templateArt,
      ReportFormat format) {
      StreamingOutput streamingOutput = createWriter(branch, view, templateArt, format);
      String fileName = String.format("Generic_Trace_Report_%s.%s", Lib.getDateTimeString(), format.extension());

      ResponseBuilder builder = Response.ok(streamingOutput, format.mediaType());
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   /**
    * Submits a background task that writes the report to disk, then emails the recipient with a download link.
    */
   private Response generateReportAsync(BranchId branch, ArtifactId view, ArtifactId templateArt,
      String emailRecipient, ReportFormat format) {
      String jsonResponse;
      try {
         if (EmailUtil.isEmailInValid(emailRecipient)) {
            throw new OseeArgumentException("Invalid Email Address");
         }
         String fileName =
            String.format("Generic_Trace_Report_%s.%s", Lib.getDateTimeString(), format.extension());
         String dataPath = orcsApi.getSystemProperties().getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);
         File publishDir = new File(dataPath, "publish");
         if (!publishDir.exists()) {
            publishDir.mkdirs();
         }
         File reportFile = new File(publishDir, fileName);
         String serverAddress = OseeClient.getOseeApplicationServer();
         String downloadLink = String.format("%s/orcs/resources/publish?path=%s", serverAddress, fileName);

         executorAdmin.submit("Async " + format.extension().toUpperCase() + " Report Generator", () -> {
            try {
               StreamingOutput report = createWriter(branch, view, templateArt, format);
               try (FileOutputStream fos = new FileOutputStream(reportFile)) {
                  report.write(fos);
               }

               String subject = "Report Generation Complete";
               String body = String.format(
                  "Your %s report has been generated successfully.\n\nFile: %s\nBranch: %s\nView: %s\nTemplate: %s\n\nDownload your report here:\n%s",
                  format.extension().toUpperCase(), fileName, branch, view, templateArt, downloadLink);

               IOseeEmail emailMessage = orcsApi.getEmailService().create(
                  Collections.singletonList(emailRecipient), emailRecipient, emailRecipient, subject, body,
                  BodyType.Text, Collections.emptySet(), "Report generation complete.");
               emailMessage.send();
            } catch (Exception ex) {
               // Send a failure notification email so the user knows the report did not complete
               try {
                  String failSubject = "Report Generation Failed";
                  String failBody = String.format(
                     "Your %s report could not be generated.\n\nBranch: %s\nView: %s\nTemplate: %s\n\nError: %s",
                     format.extension().toUpperCase(), branch, view, templateArt, ex.getMessage());
                  IOseeEmail failEmail = orcsApi.getEmailService().create(
                     Collections.singletonList(emailRecipient), emailRecipient, emailRecipient, failSubject,
                     failBody, BodyType.Text, Collections.emptySet(), "Report generation failed.");
                  failEmail.send();
               } catch (Exception emailEx) {
                  OseeLog.log(ReportEndpointImpl.class, OseeLevel.SEVERE_POPUP,
                     "Failed to send failure notification email for async report", emailEx);
               }
               OseeLog.log(ReportEndpointImpl.class, OseeLevel.SEVERE_POPUP,
                  "Error generating async " + format.extension() + " report", ex);
            }
         });

         jsonResponse = String.format(
            "{\"status\": \"Report generation started\", \"fileName\": \"%s\", \"branch\": \"%s\", \"view\": \"%s\", \"template\": \"%s\", \"emailRecipient\": \"%s\", \"downloadLink\": \"%s\"}",
            escapeJsonValue(fileName), escapeJsonValue(branch.toString()),
            escapeJsonValue(view.toString()), escapeJsonValue(templateArt.toString()),
            escapeJsonValue(emailRecipient), escapeJsonValue(downloadLink));
      } catch (Exception ex) {
         String errorJson =
            String.format("{\"error\": \"%s\"}", escapeJsonValue(ex.getMessage()));
         return Response.serverError().entity(errorJson).type(MediaType.APPLICATION_JSON).build();
      }

      return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
   }

   private StreamingOutput createWriter(BranchId branch, ArtifactId view, ArtifactId templateArt,
      ReportFormat format) {
      switch (format) {
         case XLSX:
            return new PublishTemplateReportXlsx(orcsApi, branch, view, templateArt);
         case HTML:
            return new PublishTemplateReportHtml(orcsApi, branch, view, templateArt);
         case XML:
            return new PublishTemplateReport(orcsApi, branch, view, templateArt);
         default:
            throw new OseeArgumentException("Unsupported report format: %s", format);
      }
   }

   private static final int MAX_HIERARCHY_DEPTH = 100;
   private static final int MAX_PADDING = 5;
   private static final Set<Long> ALLOWED_NUMBERING_ATTRIBUTE_TYPES = Set.of( //
      CoreAttributeTypes.Annotation.getId(), //
      CoreAttributeTypes.Description.getId(), //
      CoreAttributeTypes.DoorsHierarchy.getId(), //
      CoreAttributeTypes.ParagraphNumber.getId());

   @Override
   public XResultData applyHierarchyNumbers(BranchId branch, ArtifactId startArtifact, long attributeTypeId,
      int padding) {

      XResultData results = new XResultData();
      orcsApi.userService().requireRole(CoreUserGroups.OseeAccessAdmin);

      if (padding < 1 || padding > MAX_PADDING) {
         results.errorf("Padding must be between 1 and %d, got %d", MAX_PADDING, padding);
         return results;
      }

      if (attributeTypeId == 0L) {
         results.errorf("attributeType query parameter is required");
         return results;
      }

      if (!ALLOWED_NUMBERING_ATTRIBUTE_TYPES.contains(attributeTypeId)) {
         results.errorf(
            "Attribute type %d is not allowed for hierarchy numbering. Allowed types: Annotation, Description, DoorsHierarchy, ParagraphNumber",
            attributeTypeId);
         return results;
      }

      AttributeTypeToken attributeType = orcsApi.tokenService().getAttributeType(attributeTypeId);
      ArtifactReadable rootArt = orcsApi.getQueryFactory().fromBranch(branch).andId(startArtifact).getArtifact();

      if (rootArt == null) {
         results.errorf("Artifact %s not found on branch %s", startArtifact, branch);
         return results;
      }

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, "Apply hierarchy numbers");

      int maxNumber = intPow(10, padding) - 1;
      results.logf("Applying hierarchy numbers starting at artifact %s with padding %d (max per level: %d)",
         rootArt.getIdString(), padding, maxNumber);

      try {
         int count = applyHierarchyNumbersRecursive(rootArt, "", attributeType, padding, maxNumber, tx, results, 0);
         tx.commit();
         results.logf("Hierarchy numbering committed successfully. %d artifacts numbered.", count);
      } catch (Exception ex) {
         results.errorf("Error during hierarchy numbering, transaction discarded: %s", ex.getMessage());
      }
      return results;
   }

   private static int intPow(int base, int exponent) {
      int result = 1;
      for (int i = 0; i < exponent; i++) {
         result *= base;
      }
      return result;
   }

   /**
    * Escapes a string for safe inclusion as a JSON string value. Handles double quotes, backslashes, and control
    * characters.
    */
   private static String escapeJsonValue(String value) {
      if (value == null) {
         return "";
      }
      return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t",
         "\\t");
   }

   private int applyHierarchyNumbersRecursive(ArtifactReadable artifact, String prefix,
      AttributeTypeToken attributeType, int padding, int maxNumber, TransactionBuilder tx, XResultData results,
      int depth) {
      if (depth >= MAX_HIERARCHY_DEPTH) {
         results.warningf("Maximum hierarchy depth (%d) reached at artifact %s — skipping deeper levels.",
            MAX_HIERARCHY_DEPTH, artifact.getIdString());
         return 0;
      }

      List<ArtifactReadable> children =
         artifact.getRelated(CoreRelationTypes.DefaultHierarchical_Child, DeletionFlag.EXCLUDE_DELETED);

      int count = 0;
      int index = 0;
      for (ArtifactReadable child : children) {
         index++;
         int number = Math.min(index, maxNumber);
         String segment = String.format("%0" + padding + "d", number);
         String hierarchyNumber = prefix.isEmpty() ? segment : prefix + "." + segment;

         try {
            tx.setSoleAttributeFromString(child, attributeType, hierarchyNumber);
            results.logf("Set %s = %s on artifact %s", attributeType.getName(), hierarchyNumber,
               child.getIdString());
            count++;
         } catch (OseeArgumentException ex) {
            results.warningf("Skipping artifact %s: attribute type %s not valid for artifact type - %s",
               child.getIdString(), attributeType.getName(), ex.getMessage());
         }

         count += applyHierarchyNumbersRecursive(child, hierarchyNumber, attributeType, padding, maxNumber, tx,
            results, depth + 1);
      }
      return count;
   }

}
