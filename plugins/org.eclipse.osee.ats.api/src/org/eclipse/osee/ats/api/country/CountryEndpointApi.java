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

package org.eclipse.osee.ats.api.country;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.program.ProgramEndpointApi;
import org.eclipse.osee.ats.api.util.SkipAtsConfigJsonWriter;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Donald G. Dunne
 */
@Path("countryep")
@Swagger
public interface CountryEndpointApi {

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxCountry> get();

   @GET
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @SkipAtsConfigJsonWriter
   public JaxCountry get(@PathParam("id") long id);

   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @SkipAtsConfigJsonWriter
   public JaxCountry create(JaxCountry country);

   @DELETE
   @Path("{id}")
   public void delete(@PathParam("id") long id);

   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @SkipAtsConfigJsonWriter
   public JaxCountry update(JaxCountry country);

   @Path("{id}/program")
   @Produces(MediaType.APPLICATION_JSON)
   public ProgramEndpointApi getProgram(@PathParam("id") long id);

}
