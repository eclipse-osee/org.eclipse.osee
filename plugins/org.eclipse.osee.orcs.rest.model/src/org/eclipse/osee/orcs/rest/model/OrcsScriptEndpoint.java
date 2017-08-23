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
package org.eclipse.osee.orcs.rest.model;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("script")
public interface OrcsScriptEndpoint {

   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN})
   public Response getScriptResult(@Context HttpHeaders httpHeaders, @DefaultValue("") @QueryParam("script") String script, //
      @DefaultValue("") @QueryParam("parameters") String parameters, //
      @DefaultValue("") @QueryParam("filename") String filename, //
      @DefaultValue("false") @QueryParam("debug") boolean debug);

   @POST
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN})
   public Response postScript(@Context HttpHeaders httpHeaders, @DefaultValue("") @FormParam("script") String script, //
      @DefaultValue("") @FormParam("parameters") String parameters, //
      @DefaultValue("") @FormParam("filename") String filename, //
      @DefaultValue("false") @FormParam("debug") boolean debug);

}
