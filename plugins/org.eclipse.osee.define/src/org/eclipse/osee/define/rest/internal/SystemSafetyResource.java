/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.app.OseeAppletPage;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
@Path("/")
public final class SystemSafetyResource {
   private final OrcsApi orcsApi;
   private final IResourceRegistry resourceRegistry;
   private final ActivityLog activityLog;

   public SystemSafetyResource(ActivityLog activityLog, IResourceRegistry resourceRegistry, OrcsApi orcsApi) {
      this.activityLog = activityLog;
      this.resourceRegistry = resourceRegistry;
      this.orcsApi = orcsApi;
   }

   /**
    * Produce the System Safety Report
    *
    * @param branchId The Branch to run the System Safety Report on.
    * @param codeRoot The root directory accessible on the server for the code traces.
    * @return Produces a streaming xml file containing the System Safety Report
    */
   @Path("safety")
   @GET
   @Produces(MediaType.APPLICATION_XML)
   public Response getSystemSafetyReport(@QueryParam("branch") BranchId branchId,
      @QueryParam("code_root") String codeRoot, @DefaultValue("on") @QueryParam("style") String validate) {
      StreamingOutput streamingOutput =
         new SafetyStreamingOutput(activityLog, orcsApi, branchId, ArtifactId.SENTINEL, codeRoot, validate);
      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + "Safety_Report.xml");
      return builder.build();
   }

   /**
    * Produce the Requirements Only System Safety Report
    *
    * @param branchId The Branch to run the System Safety Report on.
    * @view view The applicability view for the requirements.
    * @return Produces a streaming xml file containing the System Safety Report
    */
   @Path("safety/swreqts")
   @GET
   @Produces(MediaType.APPLICATION_XML)
   public Response getReqtsOnlySafetyReport(@QueryParam("branch") BranchId branchId,
      @QueryParam("view") ArtifactId view) {
      StreamingOutput reqtsOutput = new SafetyReqtsOnlyStreamingOutput(activityLog, orcsApi, branchId, view);
      ResponseBuilder builder = Response.ok(reqtsOutput);
      builder.header("Content-Disposition", "attachment; filename=" + "Safety_Reqts_Only_Report.xml");
      return builder.build();
   }

   /**
    * Produce the System Safety Report with a view.
    *
    * @param branchId The Branch to run the System Safety Report on.
    * @param codeRoot The root directory accessible on the server for the code traces.
    * @param view The artifact view to use when producing the report.
    * @param validate Indicates whether style/validation should be enabled. Defaults to {@code "on"}.
    * @return Produces a streaming xml file containing the System Safety Report
    */
   @Path("view/safety")
   @GET
   @Produces(MediaType.APPLICATION_XML)
   public Response getSystemSafetyReportWithView(@QueryParam("branch") BranchId branchId,
      @QueryParam("code_root") String codeRoot, @QueryParam("view") ArtifactId view,
      @DefaultValue("on") @QueryParam("style") String validate) {
      StreamingOutput streamingOutput =
         new SafetyStreamingOutput(activityLog, orcsApi, branchId, view, codeRoot, validate);
      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + "Safety_Report.xml");
      return builder.build();
   }

   /**
    * Produces the System Safety Report from a TAR archive of source content.
    * <p>
    * The TAR payload is spooled to a temporary file, then read as a local tar archive. Each tar entry matching the
    * expected source file patterns is parsed for trace marks in memory. The temporary file is deleted after processing.
    * This avoids extracting thousands of individual files to disk while working around network stream issues that
    * cause "Truncated TAR archive" errors when reading directly from the HTTP request body.
    * </p>
    *
    * @param branchId The branch to run the System Safety Report on.
    * @param view The artifact view to use when producing the report.
    * @param validate Indicates whether style/validation should be enabled. Defaults to {@code "on"}.
    * @param tarInputStream The TAR archive input stream containing the source content.
    * @return A response containing a streaming XML System Safety Report.
    */
   @Path("view/safety/tar")
   @POST
   @Consumes("application/x-tar")
   @Produces(MediaType.APPLICATION_XML)
   public Response getSystemSafetyReportWithTar(@QueryParam("branch") BranchId branchId,
      @QueryParam("view") ArtifactId view, @DefaultValue("on") @QueryParam("style") String validate,
      InputStream tarInputStream) {

      final TraceAccumulator traces = SafetyReportConstants.newTraceAccumulator();

      java.nio.file.Path tempTar = null;
      try {
         tempTar = Files.createTempFile("safety-tar-", ".tar");
         Files.copy(tarInputStream, tempTar, StandardCopyOption.REPLACE_EXISTING);

         TarArchiveInputStream tarIn = new TarArchiveInputStream(
            new BufferedInputStream(Files.newInputStream(tempTar), 1024 * 1024));
         try {
            traces.extractTracesFromTar(tarIn);
         } finally {
            try {
               tarIn.close();
            } catch (IOException ignored) {
               // TarArchiveInputStream.close() may throw "Truncated TAR archive"
            }
         }
      } catch (IOException e) {
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
            "<error>Error reading tar stream: " + e.getMessage() + "</error>").build();
      } finally {
         if (tempTar != null) {
            try {
               Files.deleteIfExists(tempTar);
            } catch (IOException ignored) {
            }
         }
      }

      SafetyStreamingOutput delegate =
         new SafetyStreamingOutput(activityLog, orcsApi, branchId, view, traces, validate);

      return Response.ok((StreamingOutput) delegate).header("Content-Disposition",
         "attachment; filename=Safety_Report.xml").build();
   }

   /**
    * Provides the user interface for the System Safety Report
    */
   @Path("ui/safety")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getApplet() {
      OseeAppletPage pageUtil = new OseeAppletPage(orcsApi.getQueryFactory().branchQuery());
      return pageUtil.realizeApplet(resourceRegistry, "systemSafetyReport.html", getClass());
   }
}
