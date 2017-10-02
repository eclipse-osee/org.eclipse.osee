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
package org.eclipse.osee.orcs.rest.model;

import java.io.InputStream;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * @author Roberto E. Escobar
 */
@Path("resources")
public interface ResourcesEndpoint {

   @GET
   Response getResource(@QueryParam("path") String path, //
      @DefaultValue("false") @QueryParam("unzip") boolean decompressOnAquire, //
      @DefaultValue("false") @QueryParam("zip") boolean compressOnAcquire);

   @POST
   @Path("{protocol}/{resourceId}")
   Response saveResource(InputStream inputStream, //
      @PathParam("protocol") String protocol, //
      @PathParam("resourceId") String resourceId, //
      @QueryParam("name") String resourceName, //
      @DefaultValue("false") @QueryParam("overwrite") boolean overwriteAllowed, //
      @DefaultValue("false") @QueryParam("compress") boolean compressOnSave);

   @DELETE
   Response deleteResource(@QueryParam("path") String path);

}
