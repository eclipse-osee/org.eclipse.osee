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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Andrew M. Finkbeiner
 */
@Path("job")
public class OteJobsResource {

   // Allows to insert contextual objects into the class, 
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   @Path("{uuid}")
   public OteJobResource getConfiguration(@PathParam("uuid") String id) {
      return new OteJobResource(uriInfo, request, getDataStore(), id);
   }

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String getConfiguration() throws OseeCoreException {
      StringBuilder sb = new StringBuilder();
      for(String id:getDataStore().getAllJobIds()){
         sb.append(id);
         sb.append("\n");
      } 
	   return sb.toString(); 
   }
   
   private OteConfigurationStore getDataStore(){
      return OteRestApplication.get();
   }
}
