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
package org.eclipse.osee.ats.api.config;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Donald G. Dunne
 */
public interface BaseConfigEndpointApi<T extends JaxAtsObject> {

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<T> get() throws Exception;

   @GET
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public T get(@PathParam("id") long id) throws Exception;

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public Response create(T atsConfigObject) throws Exception;

   @DELETE
   @Path("{id}")
   public Response delete(@PathParam("id") long id) throws Exception;

}
