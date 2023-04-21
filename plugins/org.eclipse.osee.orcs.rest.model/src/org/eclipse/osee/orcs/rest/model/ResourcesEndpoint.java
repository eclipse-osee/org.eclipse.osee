/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.rest.model;

import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Roberto E. Escobar
 */
@Path("resources")
@Swagger
public interface ResourcesEndpoint {

   @GET
   Response getResource(@QueryParam("path") String path, //
      @DefaultValue("false") @QueryParam("unzip") boolean decompressOnAquire, //
      @DefaultValue("false") @QueryParam("zip") boolean compressOnAcquire);

   @POST
   @Path("{protocol}/{resourceId}")
   @Consumes({MediaType.APPLICATION_JSON})
   Response saveResource(InputStream inputStream, //
      @PathParam("protocol") String protocol, //
      @PathParam("resourceId") String resourceId, //
      @QueryParam("name") String resourceName, //
      @DefaultValue("false") @QueryParam("overwrite") boolean overwriteAllowed, //
      @DefaultValue("false") @QueryParam("compress") boolean compressOnSave);

   @DELETE
   Response deleteResource(@QueryParam("path") String path);

   /**
    * @return file located in main publish dir
    */
   @GET
   @Path("publish")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   Response getPublishResource(@QueryParam("path") String path);

}
