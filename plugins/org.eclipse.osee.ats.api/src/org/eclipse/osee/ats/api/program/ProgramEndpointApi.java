/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.program;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.ats.api.util.SkipAtsConfigJsonWriter;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Donald G. Dunne
 */
@Path("programep")
@Swagger
public interface ProgramEndpointApi {

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @SkipAtsConfigJsonWriter
   public List<JaxProgram> get();

   @GET
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @SkipAtsConfigJsonWriter
   public JaxProgram get(@PathParam("id") long id);

   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @SkipAtsConfigJsonWriter
   public JaxProgram create(JaxProgram program);

   @DELETE
   @Path("{id}")
   public void delete(@PathParam("id") long id);

   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @SkipAtsConfigJsonWriter
   public JaxProgram update(JaxProgram program);

   @Path("{id}/insertion")
   @Produces(MediaType.APPLICATION_JSON)
   public InsertionEndpointApi getInsertion(@PathParam("id") long id);

   @GET
   @Path("version")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ProgramVersions> getVersions(@Context UriInfo uriInfo);

}
