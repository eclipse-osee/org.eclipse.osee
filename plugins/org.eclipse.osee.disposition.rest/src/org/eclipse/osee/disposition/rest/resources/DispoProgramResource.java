/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.DispoFactory;
import org.eclipse.osee.disposition.rest.util.DispoHtmlWriter;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Angel Avila
 */
@Path("program")
public class DispoProgramResource {

   private final DispoApi dispoApi;
   private final DispoHtmlWriter writer;
   private final DispoFactory dispoFactory;

   public DispoProgramResource(DispoApi dispoApi, DispoHtmlWriter writer, DispoFactory dispoFactory) {
      this.dispoApi = dispoApi;
      this.writer = writer;
      this.dispoFactory = dispoFactory;
   }

   /**
    * Get all Disposition Programs
    * 
    * @return The Disposition Programs found
    * @response.representation.200.doc OK, Found Disposition Program
    * @response.representation.404.doc Not Found, Could not find any Disposition Programs
    */
   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response getAllPrograms() {
      ResultSet<IOseeBranch> allPrograms = dispoApi.getDispoPrograms();
      Response.Status status;
      String html;
      if (allPrograms.isEmpty()) {
         status = Status.NOT_FOUND;
         html = DispoMessages.Program_NoneFound;
      } else {
         status = Status.OK;
         html = writer.createSelectPrograms(allPrograms);
      }
      return Response.status(status).entity(html).build();
   }

   @Path("{programId}/set")
   public DispoSetResource getAnnotation(@PathParam("programId") String programId) {
      return new DispoSetResource(dispoApi, writer, dispoFactory.createProgram(programId, Long.parseLong(programId)));
   }
}