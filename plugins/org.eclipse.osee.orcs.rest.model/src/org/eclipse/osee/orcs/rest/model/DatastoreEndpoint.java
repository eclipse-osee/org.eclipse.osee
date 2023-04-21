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

package org.eclipse.osee.orcs.rest.model;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Roberto E. Escobar
 */
@Path("datastore")
@Swagger
public interface DatastoreEndpoint {

   @GET
   @Path("info")
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   DatastoreInfo getInfo();

   @POST
   @Path("initialize")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionId initialize(UserToken superUser);

   @POST
   @Path("synonyms")
   @Consumes(MediaType.TEXT_PLAIN)
   void synonyms();

   @POST
   @Path("user")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   TransactionId createUsers(Iterable<UserToken> users);

   /**
    * Used for initial authentication from the web to the server.
    *
    * @param userId This header is used when you can control the authentication provider to provide data closer to what
    * OSEE needs.
    * @param authHeader Typical use case is authentication is provided by an external service (i.e. Auth0, Firebase,
    * KeyCloak etc). This should be the default authentication method for web clients.
    * @return The current logged in user.
    */
   @GET
   @Path("user")
   @Produces(MediaType.APPLICATION_JSON)
   UserToken getUserInfo(@Context HttpHeaders headers, @HeaderParam("osee.user.id") String userId,
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader);

   @GET
   @Path("gamma/unused/{ids}")
   @Produces({MediaType.APPLICATION_JSON})
   List<String> getUnusedGammaById(@PathParam("ids") String ids);

   @GET
   @Path("gamma/unused")
   @Produces({MediaType.APPLICATION_JSON})
   List<String> getUnusedGammas(@PathParam("ids") String ids);

   /**
    * Clears the OSEE server's {@link UserToken} cache.
    */

   @DELETE
   @Path("user/cache")
   void clearUserCache();
}