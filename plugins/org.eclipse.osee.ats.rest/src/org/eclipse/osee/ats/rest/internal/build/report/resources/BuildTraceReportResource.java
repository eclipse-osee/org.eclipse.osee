/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.build.report.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.rest.internal.build.report.BuildTraceReport;
import org.eclipse.osee.ats.rest.internal.build.report.ProgramsAndBuildsTables;
import org.eclipse.osee.ats.rest.internal.build.report.SourceFileRetriever;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import com.sun.jersey.core.header.ContentDisposition;

/**
 * @author John Misinco
 */
@Path(BuildTraceReportResource.RESOURCE_BASE)
public class BuildTraceReportResource {

   private final Log logger;
   private final OrcsApi orcsApi;
   private final ProgramsAndBuildsTables programsAndBuilds = new ProgramsAndBuildsTables();
   private final BuildTraceReport report = new BuildTraceReport();
   private final SourceFileRetriever soureFiles = new SourceFileRetriever();

   public static final String RESOURCE_BASE = "/report/buildTrace";

   public BuildTraceReportResource(Log logger, OrcsApi orcsApi) {
      this.logger = logger;
      this.orcsApi = orcsApi;
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response getPrograms(@Context final UriInfo uriInfo) {
      return Response.ok(new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException {
            programsAndBuilds.getProgramsTable(logger, output, uriInfo);
         }
      }).build();
   }

   @GET
   @Path("/program/{programId}")
   @Produces(MediaType.TEXT_HTML)
   public Response getBuilds(@PathParam("programId") final String programId, @DefaultValue("UNKNOWN") @QueryParam("program") final String programName, @Context final UriInfo uriInfo) {
      return Response.ok(new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException {
            programsAndBuilds.getBuildsTable(logger, output, programName, programId, uriInfo);
         }
      }).build();
   }

   @GET
   @Path("/{programId}/{buildId}")
   @Produces(MediaType.TEXT_HTML)
   public Response getBuildReport(@PathParam("programId") final String programId, @PathParam("buildId") final String buildId, @DefaultValue("UNKNOWN") @QueryParam("program") final String programName, @DefaultValue("UNKNOWN") @QueryParam("build") final String buildName, @Context final UriInfo uriInfo) {
      return Response.ok(new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException {
            report.getBuildReport(output, orcsApi, logger, programId, buildId, programName, buildName, uriInfo);
         }
      }).build();
   }

   @GET
   @Path("/archive/{programId}/{buildId}")
   @Produces("application/zip")
   public Response getBuildArchive(@PathParam("programId") final String programId, @PathParam("buildId") final String buildId, @DefaultValue("UNKNOWN") @QueryParam("program") final String programName, @DefaultValue("UNKNOWN") @QueryParam("build") final String buildName, @Context final UriInfo uriInfo) {
      final String fileName = programName + "_" + buildName;
      ContentDisposition contentDisposition =
         ContentDisposition.type("attachment").fileName(fileName + ".zip").creationDate(new Date()).build();

      return Response.ok(new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException {
            report.getBuildArchive(output, orcsApi, logger, fileName, programId, buildId, programName, buildName,
               uriInfo);
         }
      }).header("Content-Disposition", contentDisposition).build();
   }

   @GET
   @Path("/sourceFile")
   @Produces(MediaType.TEXT_HTML)
   public Response getSourceFile(@DefaultValue("UNKNOWN") @QueryParam("url") final String urlToSource, @DefaultValue("false") @QueryParam("offline") final boolean offline) {
      return Response.ok(new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException {
            soureFiles.getSourceFile(output, orcsApi.getResourceRegistry(), urlToSource, offline);
         }
      }).build();
   }

   @GET
   @Path("/supportFiles")
   @Produces("application/zip")
   public Response getSupportFiles() {
      return Response.ok(new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException, IOException {
            soureFiles.getSupportFiles(output);
         }
      }).build();
   }

}
