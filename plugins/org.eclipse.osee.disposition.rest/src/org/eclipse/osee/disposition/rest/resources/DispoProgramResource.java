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
import org.eclipse.osee.disposition.rest.util.HtmlWriter;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Angel Avila
 */
@Path("program")
public class DispoProgramResource {

   private final DispoApi dispoApi;
   private final HtmlWriter writer;
   private final DispoFactory dispoFactory;

   public DispoProgramResource(DispoApi dispoApi, HtmlWriter writer, DispoFactory factory) {
      this.dispoApi = dispoApi;
      this.writer = writer;
      this.dispoFactory = factory;
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
         html = writer.createDispositionPage("Programs", "", allPrograms);
      }
      return Response.status(status).entity(html).build();
   }

   /**
    * Get a specific Disposition Program given a programId
    * 
    * @param programId The Id of the Disposition Program to search for
    * @return The found Disposition Program if successful. Error Code otherwise
    * @response.representation.200.doc OK, Found Disposition Program
    * @response.representation.404.doc Not Found, Could not find any Disposition Program
    */
   @Path("{programId}")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response getProgramById(@PathParam("programId") String programId) {
      IOseeBranch dispoBranch = dispoApi.getDispoProgramById(dispoFactory.createProgram(programId));
      Response.Status status;
      String html;
      if (dispoBranch == null) {
         status = Status.NOT_FOUND;
         html = DispoMessages.Program_NotFound;
      } else {
         status = Status.OK;
         String subTitle = "Disposition Sets";
         String prefixPath = programId + "/set";
         html = writer.createDispoPage(dispoBranch.getName(), prefixPath, subTitle, "[]");
      }

      return Response.status(status).entity(html).build();
   }

   @Path("{programId}/set")
   public DispoSetResource getAnnotation(@PathParam("programId") String programId) {
      return new DispoSetResource(dispoApi, writer, dispoFactory.createProgram(programId));
   }
}