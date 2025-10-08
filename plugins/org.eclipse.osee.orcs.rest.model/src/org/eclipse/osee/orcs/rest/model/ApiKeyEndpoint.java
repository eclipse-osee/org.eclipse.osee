/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ApiKey;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

@Path("apikeys")
@Swagger
public interface ApiKeyEndpoint {

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   Response createApiKey(ApiKey apiKey);

   @GET
   @Produces({MediaType.APPLICATION_JSON})
   Response getApiKeys();

   @GET
   @Path("scopes")
   @Produces({MediaType.APPLICATION_JSON})
   Response getKeyScopes();

   @DELETE
   @Path("{keyUID}")
   Response revokeApiKey(@PathParam("keyUID") long keyUID);

}