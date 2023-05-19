/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.disposition.rest.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.disposition.model.CopySetParams;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.MassTeamAssignParams;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.DispoRoles;
import org.eclipse.osee.disposition.rest.internal.report.ExportSet;
import org.eclipse.osee.disposition.rest.internal.report.STRSReport;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Angel Avila
 */
@Swagger
public class DispoAdminResource {
   private final DispoApi dispoApi;
   private final BranchId branch;

   public DispoAdminResource(DispoApi dispoApi, BranchId branch) {
      this.dispoApi = dispoApi;
      this.branch = branch;
   }

   @Path("/report")
   @GET
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Operation(summary = "Get Dispo Set report given a primary Set")
   @Tags(value = {@Tag(name = "report"), @Tag(name = "sets")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request")})
   public Response getDispoSetReport(
      @Parameter(description = "Primary Set", required = true) @Encoded @QueryParam("primarySet") String primarySet,
      @Parameter(description = "Secondary Set", required = true) @Encoded @QueryParam("secondarySet") String secondarySet) {
      final DispoSet dispoSet = dispoApi.getDispoSetById(branch, primarySet);
      final DispoSet dispoSet2 = dispoApi.getDispoSetById(branch, secondarySet);
      final STRSReport writer = new STRSReport(dispoApi);

      final String fileName = String.format("STRS_Report_%s", System.currentTimeMillis());

      StreamingOutput streamingOutput = new StreamingOutput() {

         @Override
         public void write(OutputStream outputStream) throws WebApplicationException, IOException {
            writer.runReport(branch, dispoSet, dispoSet2, outputStream);
            outputStream.flush();
         }
      };
      String contentDisposition =
         String.format("attachment; filename=\"%s.xml\"; creation-date=\"%s\"", fileName, new Date());
      return Response.ok(streamingOutput).header("Content-Disposition", contentDisposition).type(
         "application/xml").build();
   }

   @Path("/export")
   @GET
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Operation(summary = "Post Dispo Set export given a primary Set")
   @Tags(value = {@Tag(name = "export"), @Tag(name = "sets")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request")})
   public Response postDispoSetExport(
      @Parameter(description = "Primary Set", required = true) @Encoded @QueryParam("primarySet") String primarySet,
      @Parameter(description = "Option", required = true) @QueryParam("option") String option) {
      final DispoSet dispoSet = dispoApi.getDispoSetById(branch, primarySet);
      final ExportSet writer = new ExportSet(dispoApi);
      final String options = option;
      Date date = new Date();
      String newstring = new SimpleDateFormat("yyyy-MM-dd").format(date);
      final String fileName = String.format("Coverage_%s", newstring);

      StreamingOutput streamingOutput = new StreamingOutput() {

         @Override
         public void write(OutputStream outputStream) throws WebApplicationException, IOException {
            String dispoType = dispoSet.getDispoType();
            if (dispoType.equals("testScript")) {
               writer.runDispoReport(branch, dispoSet, options, outputStream, "");
            } else {
               writer.runCoverageReport(branch, dispoSet, options, outputStream, "");
            }
            outputStream.flush();
         }
      };
      String contentDisposition =
         String.format("attachment; filename=\"%s.xml\"; creation-date=\"%s\"", fileName, new Date());
      return Response.ok(streamingOutput).header("Content-Disposition", contentDisposition).type(
         "application/xml").build();
   }

   @Path("/exportSummaryCoverage")
   @GET
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Operation(summary = "Post Dispo Set export summary given a primary Set")
   @Tags(value = {@Tag(name = "export"), @Tag(name = "sets")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request")})
   public Response postDispoSetExportSummary(
      @Parameter(description = "Primary Set", required = true) @Encoded @QueryParam("primarySet") String primarySet) {
      final DispoSet dispoSet = dispoApi.getDispoSetById(branch, primarySet);
      final ExportSet exportSet = new ExportSet(dispoApi);
      HashMap<String, Object> summaryCoverage = new HashMap<String, Object>();
      HashMap<String, Object> jsonResponse = new HashMap<String, Object>();
      exportSet.runCoverageSummaryReport(branch, dispoSet, summaryCoverage);
      Set<String> keys = summaryCoverage.keySet();
      String keyInString = String.join(",", keys);
      jsonResponse.put("Keys", keyInString);
      jsonResponse.put("Primary_Set", primarySet);
      jsonResponse.put("Summary_Of_Coverage", summaryCoverage);
      ObjectMapper mapper = new ObjectMapper();
      String response = "";
      try {
         response = mapper.writeValueAsString(jsonResponse);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
         return Response.serverError().entity(ex.getMessage()).build();
      }

      return Response.ok(response).build();
   }

   @Path("/{primarySet}/exportToFile")
   @GET
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Operation(summary = "Post Dispo Set export directory given a primary Set")
   @Tags(value = {@Tag(name = "export"), @Tag(name = "sets")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request")})
   public Response postDispoSetExportDirectory(
      @Parameter(description = "Primary Set", required = true) @Encoded @PathParam("primarySet") String primarySet,
      @Parameter(description = "Option", required = true) @QueryParam("option") String option,
      @Parameter(description = "Expo file name", required = true) @HeaderParam("expoFileName") String expoFileName) {
      final DispoSet dispoSet = dispoApi.getDispoSetById(branch, primarySet);
      final ExportSet writer = new ExportSet(dispoApi);
      final String options = option;
      Date date = new Date();
      String newstring = new SimpleDateFormat("yyyy-MM-dd").format(date);
      final String fileName = String.format("Coverage_%s", newstring);

      StreamingOutput streamingOutput = new StreamingOutput() {

         @Override
         public void write(OutputStream outputStream) throws WebApplicationException, IOException {
            String dispoType = dispoSet.getDispoType();
            if (dispoType.equals("testScript")) {
               writer.runDispoReport(branch, dispoSet, options, outputStream, expoFileName);
            } else {
               writer.runCoverageReport(branch, dispoSet, options, outputStream, expoFileName);
            }
            outputStream.flush();
         }
      };
      String contentDisposition =
         String.format("attachment; filename=\"%s.xml\"; creation-date=\"%s\"", fileName, new Date());
      return Response.ok(streamingOutput).header("Content-Disposition", contentDisposition).type(
         "application/xml").build();
   }

   @Path("/copyCoverage")
   @POST
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get Dispo Set copy coverage given a destination Set")
   @Tags(value = {@Tag(name = "copy"), @Tag(name = "sets")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request")})
   public Response getDispoSetCopyCoverage(
      @Parameter(description = "Destination Set", required = true) @QueryParam("destinationSet") String destinationSet,
      @Parameter(description = "Source Branch", required = true) @QueryParam("sourceBranch") BranchId sourceBranch,
      @Parameter(description = "Source Package", required = true) @QueryParam("sourcePackage") Long sourcePackage,
      CopySetParams params, @QueryParam("userName") String userName) {
      Response.Status status;
      dispoApi.copyDispoSetCoverage(sourceBranch, sourcePackage, branch, destinationSet, params, userName);
      status = Status.OK;
      return Response.status(status).build();
   }

   @Path("/copy")
   @POST
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Get Dispo Set copy given a destination Set")
   @Tags(value = {@Tag(name = "copy"), @Tag(name = "sets")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request")})
   public Response getDispoSetCopy(
      @Parameter(description = "Destination Set", required = true) @QueryParam("destinationSet") String destinationSet,
      @Parameter(description = "Source Program", required = true) @QueryParam("sourceProgram") BranchId sourceBranch,
      @Parameter(description = "Source Set", required = true) @QueryParam("sourceSet") String sourceSet,
      CopySetParams params,
      @Parameter(description = "The Username", required = true) @QueryParam("userName") String userName) {
      dispoApi.copyDispoSet(branch, destinationSet, sourceBranch, sourceSet, params, userName);
      Response.Status status = Status.OK;
      return Response.status(status).build();
   }

   @Path("/multiItemEdit")
   @POST
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Consumes(MediaType.APPLICATION_JSON)
   @Operation(summary = "Multi Item edit")
   @Tag(name = "edit")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request")})
   public Response multiItemEdit(MassTeamAssignParams params,
      @Parameter(description = "The Username", required = true) @QueryParam("userName") String userName) {
      Response.Status status;
      dispoApi.massEditTeam(branch, params.getSetId(), params.getNamesList(), params.getTeam(),
         String.format("Mult Item Edit by: %s", params.getUserName()), userName);
      status = Status.OK;
      return Response.status(status).build();
   }

   @Path("/rerun")
   @GET
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Produces(MediaType.APPLICATION_XML)
   @Operation(summary = "Get rerun report")
   @Tags(value = {@Tag(name = "report"), @Tag(name = "rerun")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400", description = "Bad Request")})
   public Response getRerunReport(
      @Parameter(description = "Primary Set", required = true) @QueryParam("primarySet") String primarySet) {
      DispoSet set = dispoApi.getDispoSetById(branch, primarySet);
      String rerunList = set.getRerunList();

      StreamingOutput streamingOutput = new StreamingOutput() {

         @Override
         public void write(OutputStream outputStream) throws IOException, WebApplicationException {
            Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter out = new BufferedWriter(writer);
            try {
               out.write(rerunList);
            } finally {
               Lib.close(out);
            }
            outputStream.flush();
         }
      };

      String contentDisposition =
         String.format("attachment; filename=\"%s.xml\"; creation-date=\"%s\"", "batch-list", new Date());
      return Response.ok(streamingOutput).header("Content-Disposition", contentDisposition).type(
         "application/xml").build();
   }

}
