/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.ClientProvider;

/**
 * @author Roberto E. Escobar
 */
@Path("/clients")
public class ClientRegistrationService {

   private ClientProvider provider;

   public void setDataProvider(ClientProvider provider) {
      this.provider = provider;
   }

   @GET
   public Response getClient() {
      return Response.ok().build();
   }

   @Path("{client-id}")
   @GET
   public Response getClient(@PathParam("{client-id}") String clientId) {
      Client client = provider.getClient(clientId);
      return Response.ok(client).build();
   }

}