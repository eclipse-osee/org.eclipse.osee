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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.core.ServiceUtility;

/**
 * @author Andrew M. Finkbeiner
 */
@Path("/")
public class OteRootResource {

   // Allows to insert contextual objects into the class, 
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getConfiguration() throws OseeCoreException {
      String url = uriInfo.getAbsolutePath().toASCIIString();
      OTEApi ote = ServiceUtility.getService(OTEApi.class);
      File serverFolder = ote.getServerFolder().getServerFolder();
      HTMLBuilder builder = new HTMLBuilder();
      builder.open("OTE Server");
      builder.commonHeader(serverFolder);
      builder.h2("Batch Data:");
      builder.addLink(url, "latestbatch", "Latest Batch");
      builder.br();
      builder.addLink(url, "batches", "Archived Batches");
      builder.br();
      builder.br();
      builder.h2("Server Info:");
      builder.addLink(url, "config", "Current Server Configuration");
      builder.br();
      builder.close();
	   return builder.get(); 
   }
   
   @Path("latestbatch")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getLatestBatch() throws Exception {
      OTEApi ote = ServiceUtility.getService(OTEApi.class);
      File[] batches = ote.getServerFolder().getBatchesFolder().listFiles();
      
      Arrays.sort(batches, new ReverseAlphabeticalSort());
      if(batches.length > 0){
         return new OteBatchResource(uriInfo, request, batches[0].getName()).getBatches();
      } else {
         return new OteBatchResource().getBatches();
      }
   }
   
   @Path("latestbatch/content.zip")
   @GET
   @Produces({"application/zip"})
   public StreamingOutput getLatestBatchContent() throws Exception {
      OTEApi ote = ServiceUtility.getService(OTEApi.class);
      File[] batches = ote.getServerFolder().getBatchesFolder().listFiles();
      
      Arrays.sort(batches, new ReverseAlphabeticalSort());
      if(batches.length > 0){
         return new OteBatchResource(uriInfo, request, batches[0].getName()).getZip();
      } else {
         return new OteBatchResource().getZip();
      }
   }

   
}
