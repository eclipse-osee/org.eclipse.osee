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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.internal.report.STRSReport;
import org.eclipse.osee.disposition.rest.util.DispoHtmlWriter;

/**
 * @author Angel Avila
 */

public class DispoReportResource {
   private final DispoApi dispoApi;
   private final DispoProgram program;

   public DispoReportResource(DispoApi dispoApi, DispoHtmlWriter writer, DispoProgram program) {
      this.dispoApi = dispoApi;
      this.program = program;
   }

   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response postDispoSetReport(@QueryParam("primarySet") String primarySet, @QueryParam("secondarySet") String secondarySet) {
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
}
