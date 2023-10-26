/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript;

import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.testscript.internal.ScriptBatchToken;

/**
 * @author Ryan T. Baldwin
 */
@Path("batch")
@Swagger
public interface ScriptBatchEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   Collection<ScriptBatchToken> getAll(@QueryParam("filter") String filter, @QueryParam("viewId") ArtifactId viewId,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET()
   @Path("set/{setId}")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<ScriptBatchToken> getAllForSet(@PathParam("setId") ArtifactId setId, @QueryParam("filter") String filter,
      @QueryParam("viewId") ArtifactId viewId, @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET()
   @Path("set/{setId}/count")
   @Produces(MediaType.APPLICATION_JSON)
   int getAllForSetCount(@PathParam("setId") ArtifactId setId, @QueryParam("filter") String filter,
      @QueryParam("viewId") ArtifactId viewId);

   @GET()
   @Path("count")
   @Produces(MediaType.APPLICATION_JSON)
   int getCount(@QueryParam("filter") String filter, @QueryParam("viewId") ArtifactId viewId);

   @GET()
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   ScriptBatchToken getBatch(@PathParam("id") ArtifactId batchId);

}