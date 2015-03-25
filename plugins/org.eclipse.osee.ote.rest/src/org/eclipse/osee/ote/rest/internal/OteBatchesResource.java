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

import java.io.File;
import java.util.Arrays;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.core.ServiceUtility;

/**
 * @author Andrew M. Finkbeiner
 */
@Path("batches")
public class OteBatchesResource {

   // Allows to insert contextual objects into the class, 
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getBatches() throws OseeCoreException {
      OTEApi ote = ServiceUtility.getService(OTEApi.class);
      File[] batches = ote.getServerFolder().getBatchesFolder().listFiles();
      String url = uriInfo.getAbsolutePath().toASCIIString();
      HTMLBuilder builder = new HTMLBuilder();
      builder.open("OTE Server Batches");
      builder.commonHeader(ote.getServerFolder().getBatchesFolder());
      
      builder.h2("Archived Batches:");
      Arrays.sort(batches, new ReverseAlphabeticalSort());
      for(File file:batches){
         if(file.isDirectory()){
            builder.addLink(url, file.getName(), file.getName());
            builder.br();
         }
      }
      builder.close();
	   return builder.get(); 
   }
   
   @Path("{path}")
   public OteBatchResource getConfiguration(@PathParam("path") String path) {
      return new OteBatchResource(uriInfo, request, path);
   }

   
}
