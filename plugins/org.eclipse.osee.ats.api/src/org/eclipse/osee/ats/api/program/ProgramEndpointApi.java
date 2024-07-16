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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.config.BaseConfigEndpointApi;
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Donald G. Dunne
 */
@Path("programep")
@Swagger
public interface ProgramEndpointApi extends BaseConfigEndpointApi<JaxProgram> {

   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   public Response update(JaxProgram program) throws Exception;

   @Path("{programId}/program")
   @Produces(MediaType.APPLICATION_JSON)
   public InsertionEndpointApi getInsertion(@PathParam("programId") long programId);

   @GET
   @Path("version")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ProgramVersions> getVersions(@Context UriInfo uriInfo);

}
