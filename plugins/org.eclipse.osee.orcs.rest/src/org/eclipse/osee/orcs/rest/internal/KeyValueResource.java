/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Angel Avila
 */
public class KeyValueResource {

   private final OrcsApi orcsApi;

   @Context
   private UriInfo uriInfo;

   public KeyValueResource(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Path("/key_value")
   @POST
   public Response putKeyValue(@QueryParam("key") Long key, @QueryParam("value") String value) {
      boolean result = orcsApi.getKeyValueOps().putByKey(key, value);
      return Response.status(result ? Status.OK : Status.CONFLICT).build();
   }
}