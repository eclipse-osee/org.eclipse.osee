/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
@Path("keyvalue")
public interface KeyValueEndpoint {

   @GET
   String getByKey(@QueryParam("id") Long id);

   @POST
   @Path("{id}")
   @Produces({MediaType.APPLICATION_JSON})
   XResultData putWithKeyIfAbsent(@PathParam("id") Long key, String value);

   @PUT
   @Path("{id}")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces({MediaType.APPLICATION_JSON})
   XResultData updateByKey(@PathParam("id") Long key, String value);

}
