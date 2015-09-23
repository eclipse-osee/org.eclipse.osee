/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.applications;

import java.net.URI;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * @author Angel Avila
 */
@Path("/")
public class OseeRootResource {

   public OseeRootResource() {

   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getRoot() {
      URI uri = UriBuilder.fromUri("/osee/ui/index.html").build();
      return Response.seeOther(uri).build();
   }

}
