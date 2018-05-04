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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.OrcsTypesData;
import org.eclipse.osee.framework.core.data.OrcsTypesVersion;

/**
 * @author Roberto E. Escobar
 */
@Path("types")
public interface TypesEndpoint {

   @GET
   @Produces({OrcsMediaType.APPLICATION_ORCS_TYPES, MediaType.TEXT_PLAIN})
   Response getTypes();

   @GET
   @Path("config")
   @Produces({MediaType.APPLICATION_JSON})
   Response getConfig();

   @GET
   @Path("config/sheet")
   @Produces({MediaType.APPLICATION_JSON})
   Response getConfigSheets();

   @POST
   @Path("config/sheet")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   Response setConfigSheets(OrcsTypesVersion version);

   @GET
   @Path("attribute/enum")
   @Produces({MediaType.APPLICATION_JSON})
   Response getEnums();

   @GET
   @Path("attribute/enum/{uuid}")
   @Produces({MediaType.APPLICATION_JSON})
   Response getEnums(@PathParam("uuid") Long uuid);

   @GET
   @Path("attribute/enum/{uuid}/entry")
   @Produces({MediaType.APPLICATION_JSON})
   Response getEnumEntries(@PathParam("uuid") Long uuid);

   @POST
   @Consumes({OrcsMediaType.APPLICATION_ORCS_TYPES, MediaType.TEXT_PLAIN})
   @Produces(MediaType.APPLICATION_JSON)
   void setTypes(InputStream inputStream);

   @POST
   @Path("invalidate-caches")
   Response invalidateCaches();

   @POST
   @Path("import-types")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_PLAIN)
   Response importOrcsTypes(OrcsTypesData typesData);

   @POST
   @Path("dbinit")
   @Produces(MediaType.TEXT_PLAIN)
   Response dbInit();

}