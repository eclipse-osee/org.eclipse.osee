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
package org.eclipse.osee.framework.server.ide.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.OseeCredential;

/**
 * @author Donald G. Dunne
 */
public interface SessionEndpoint {

   /**
    * Register an IDE client with an application server.
    */
   @PUT
   @Path("session")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public Response createIdeClientSession(OseeCredential clientSession);

   /**
    * @return protocols associated with the specified application server
    */
   @GET
   @Path("session/protocols")
   @Produces({MediaType.APPLICATION_JSON})
   Response getIdeClientProtocols();

   /**
    * DeRegister an IDE client with an application server.
    */
   @DELETE
   @Path("session/{sessionId}")
   @Produces({MediaType.APPLICATION_JSON})
   public Response releaseIdeClientSession(@PathParam("sessionId") String sessionId);

}
