/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.rest.model.Client;

/**
 * @author Donald G. Dunne
 */
@Path("client")
public class ClientResource {

   // Allows to insert contextual objects into the class, 
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   UriInfo uriInfo;
   @Context
   Request request;
   private final IApplicationServerManager serverManager;

   public ClientResource(IApplicationServerManager serverManager) {
      this.serverManager = serverManager;
   }

   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Client get() throws OseeCoreException {
      Client client = new Client();
      String[] supportedVersions = serverManager.getSupportedVersions();
      for (String ver : supportedVersions) {
         client.addSupportedVersion(ver);
      }
      return client;
   }

}
