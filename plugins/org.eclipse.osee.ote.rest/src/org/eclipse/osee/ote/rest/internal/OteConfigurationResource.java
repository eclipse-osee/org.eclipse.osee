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
package org.eclipse.osee.ote.rest.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

import org.eclipse.osee.ote.rest.model.OteConfiguration;
import org.eclipse.osee.ote.rest.model.OteJobStatus;

/**
 * @author Andrew M. Finkbeiner
 */
@Path("config")
public class OteConfigurationResource {

   // Allows to insert contextual objects into the class, 
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public OteConfiguration getConfiguration() throws MalformedURLException, IllegalArgumentException, UriBuilderException, InterruptedException, ExecutionException {
      return getDataStore().getConfiguration(uriInfo);
   }
   
   @POST
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public OteJobStatus createConfig(OteConfiguration config) throws IOException, InterruptedException, ExecutionException {
      return getDataStore().setup(config, uriInfo);
   }
   
   private OteConfigurationStore getDataStore(){
      return OteRestApplication.get();
   }
}
