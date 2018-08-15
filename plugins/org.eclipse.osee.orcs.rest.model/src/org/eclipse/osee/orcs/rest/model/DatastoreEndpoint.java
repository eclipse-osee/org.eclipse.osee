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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Roberto E. Escobar
 */
@Path("datastore")
public interface DatastoreEndpoint {

   @GET
   @Path("info")
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   DatastoreInfo getInfo();

   @POST
   @Path("initialize")
   @Consumes(MediaType.TEXT_PLAIN)
   void initialize(String typeModel);

   @POST
   @Path("migrate")
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   Response migrate();

   @POST
   @Path("initialize/createDemoBranches")
   void createDemoBranches();
}