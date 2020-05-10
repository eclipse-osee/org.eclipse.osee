/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.server.ide.api;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;

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
   public OseeSessionGrant createIdeClientSession(OseeCredential clientSession);

   /**
    * @return protocols associated with the specified application server
    */
   @GET
   @Path("session/protocols")
   @Produces({MediaType.APPLICATION_JSON})
   List<String> getIdeClientProtocols();

   /**
    * DeRegister an IDE client with an application server.
    */
   @DELETE
   @Path("session/{sessionId}")
   public void releaseIdeClientSession(@PathParam("sessionId") String sessionId);
}