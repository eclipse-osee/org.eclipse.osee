/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
@Path("init")
public class DispoInitResource {

   private final DispoHtmlWriter writer;

   public DispoInitResource(DispoHtmlWriter writer) {
      this.writer = writer;
   }

   /**
    * Get initial Dispo page
    * 
    * @return Html for inital Dispo page
    * @response.representation.200.doc OK, created HTML page
    */
   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response start(@Context UriInfo uriInfo) {
      Response.Status status;
      String html;
      status = Status.OK;
      String host = uriInfo.getAbsolutePath().getHost();
      int port = uriInfo.getAbsolutePath().getPort();
      String url = host + ":" + port;
      html = writer.createMainTable(url);
      return Response.status(status).entity(html).build();
   }

}