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
package org.eclipse.osee.disposition.rest.resources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.internal.report.ExportSet;
import org.eclipse.osee.disposition.rest.internal.report.STRSReport;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Angel Avila
 */

public class DispoAdminResource {
   private final DispoApi dispoApi;
   private final DispoProgram program;

   public DispoAdminResource(DispoApi dispoApi, DispoProgram program) {
      this.dispoApi = dispoApi;
      this.program = program;
   }

   @Path("/report")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response getDispoSetReport(@Encoded @QueryParam("primarySet") String primarySet, @Encoded @QueryParam("secondarySet") String secondarySet) {
      final DispoSet dispoSet = dispoApi.getDispoSetById(program, primarySet);
      final DispoSet dispoSet2 = dispoApi.getDispoSetById(program, secondarySet);
      final STRSReport writer = new STRSReport(dispoApi);

      final String fileName = String.format("STRS_Report_%s", System.currentTimeMillis());

      StreamingOutput streamingOutput = new StreamingOutput() {

         @Override
         public void write(OutputStream outputStream) throws WebApplicationException, IOException {
            writer.runReport(program, dispoSet, dispoSet2, outputStream);
            outputStream.flush();
         }
      };
      String contentDisposition =
         String.format("attachment; filename=\"%s.xml\"; creation-date=\"%s\"", fileName, new Date());
      return Response.ok(streamingOutput).header("Content-Disposition", contentDisposition).type("application/xml").build();
   }

   @Path("/export")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response postDispoSetExport(@Encoded @QueryParam("primarySet") String primarySet, @QueryParam("option") String option) throws FileNotFoundException {
      final DispoSet dispoSet = dispoApi.getDispoSetById(program, primarySet);
      final ExportSet writer = new ExportSet(dispoApi);
      final String options = option;
      final String fileName = String.format("STRS_Report_%s", System.currentTimeMillis());

      StreamingOutput streamingOutput = new StreamingOutput() {

         @Override
         public void write(OutputStream outputStream) throws WebApplicationException, IOException {
            writer.runReport(program, dispoSet, options, outputStream);
            outputStream.flush();
         }
      };
      String contentDisposition =
         String.format("attachment; filename=\"%s.xml\"; creation-date=\"%s\"", fileName, new Date());
      return Response.ok(streamingOutput).header("Content-Disposition", contentDisposition).type("application/xml").build();
   }

   @Path("/copy")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getDispoSetCopy(@Encoded @QueryParam("destinationSet") String destinationSet, @Encoded @QueryParam("sourceSet") String sourceSet) {
      Response.Status status;
      final DispoSet destination = dispoApi.getDispoSetById(program, destinationSet);
      final DispoSet source = dispoApi.getDispoSetById(program, sourceSet);

      String reportUrl = dispoApi.copyDispoSet(program, destination, source);
      DispoSetData responseSet = new DispoSetData();
      responseSet.setOperationStatus(reportUrl);

      if (Strings.isValid(reportUrl)) {
         status = Status.OK;
      } else {
         status = Status.NOT_FOUND;
      }
      return Response.status(status).entity(responseSet).build();
   }
}
