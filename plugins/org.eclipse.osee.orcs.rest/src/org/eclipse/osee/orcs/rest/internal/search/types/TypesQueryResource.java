/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.search.types;

import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author John Misinco
 */
@Path("typesQuery")
public class TypesQueryResource {

   private final Gson plainG = new Gson();
   private final Gson prettyG = new GsonBuilder().setPrettyPrinting().create();
   private final OrcsApi orcsApi;

   public TypesQueryResource(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Path("/artifact")
   @GET
   public Response getAllArtifactTypes(@QueryParam("pretty") boolean pretty) {
      OrcsTypes orcsTypes = orcsApi.getOrcsTypes(null);
      Collection<? extends IArtifactType> all = orcsTypes.getArtifactTypes().getAll();
      Gson gson = pretty ? prettyG : plainG;
      return Response.ok(gson.toJson(all)).build();
   }

}
