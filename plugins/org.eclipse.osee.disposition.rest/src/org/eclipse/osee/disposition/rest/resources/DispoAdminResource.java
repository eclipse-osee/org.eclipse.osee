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
package org.eclipse.osee.disposition.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.disposition.rest.util.DispoHtmlWriter;

/**
 * @author Angel Avila
 */
@Path("admin")
public class DispoAdminResource {

   private final DispoHtmlWriter writer;

   public DispoAdminResource(DispoHtmlWriter writer) {
      this.writer = writer;
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response start(@Context UriInfo uriInfo) {
      Response.Status status;
      String html;
      status = Status.OK;
      String host = uriInfo.getAbsolutePath().getHost();
      int port = uriInfo.getAbsolutePath().getPort();
      String url = host + ":" + port;
      html = writer.createAdminTable(url);
      return Response.status(status).entity(html).build();
   }

}