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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

import org.eclipse.osee.ote.rest.model.OTEConfiguration;
import org.eclipse.osee.ote.rest.model.OTEConfigurationItem;
import org.eclipse.osee.ote.rest.model.OTEJobStatus;

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
   public OTEConfiguration getConfiguration() throws MalformedURLException, IllegalArgumentException, UriBuilderException, InterruptedException, ExecutionException {
      return getDataStore().getConfiguration(uriInfo);
   }
   
   @POST
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public OTEJobStatus createConfig(OTEConfiguration config) throws IOException, InterruptedException, ExecutionException {
      return getDataStore().setup(config, uriInfo);
   }
   
   @GET
   @Produces({MediaType.TEXT_HTML})
   public String getHtmlConfiguration() throws MalformedURLException, IllegalArgumentException, UriBuilderException, InterruptedException, ExecutionException {
      OTEConfiguration config = getDataStore().getConfiguration(uriInfo);
      HTMLBuilder b = new HTMLBuilder();
      b.open("OTE Server Configuration");
      b.h2("Server Configuration");      
      b.ulStart();
      List<OTEConfigurationItem> items = config.getItems();
      Collections.sort(items, new OTEConfigItemSort());
      for(OTEConfigurationItem item:config.getItems()){
         b.li(String.format("%s %s", item.getBundleName(), item.getBundleVersion()));
      }
      b.ulStop();
      return b.get();
   }
   
   private OteConfigurationStore getDataStore(){
      return OteRestApplication.get();
   }
}
