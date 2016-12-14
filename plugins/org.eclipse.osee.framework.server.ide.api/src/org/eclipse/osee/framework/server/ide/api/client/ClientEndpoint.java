/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.server.ide.api.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.server.ide.api.model.IdeVersion;

/**
 * @author Donald G. Dunne
 */
public interface ClientEndpoint {

   @GET
   @Path("client")
   @Produces({MediaType.APPLICATION_JSON})
   public Response getAll();

   @GET
   @Path("client/details")
   @Produces({MediaType.APPLICATION_JSON})
   public Response getAllDetails();

   /**
    * @param idOrName as userId or name; underscores can be used instead of spaces if calling from browser;
    * @return all client sessions matching idOrName; multiple users sessions can be returned depending
    */
   @GET
   @Path("client/{idOrName}")
   @Produces({MediaType.APPLICATION_JSON})
   public Response getClientsForUser(@PathParam("idOrName") String idOrName);

   @GET
   @Path("client/{userId}/session/{sessionId}")
   @Produces({MediaType.TEXT_PLAIN})
   public Response getClientInfo(@PathParam("userId") String userId, @PathParam("sessionId") String sessionId);

   @GET
   @Path("versions")
   @Produces({MediaType.APPLICATION_JSON})
   IdeVersion getSupportedVersions();

}
