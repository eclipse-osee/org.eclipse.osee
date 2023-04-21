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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Roberto E. Escobar
 */
@Path("index")
@Swagger
public interface IndexerEndpoint {

   @PUT
   @Path("branches/{branch-uuids}")
   Response indexBranches(@PathParam("branch-uuids") String branchUuids, //
      @DefaultValue("true") @QueryParam("missingItemsOnly") boolean missingItemsOnly);

   @PUT
   @Path("resources")
   @Consumes(MediaType.APPLICATION_JSON)
   Response indexResources(IndexResources options);

   @DELETE
   @Path("queue")
   Response deleteIndexQueue();

   @DELETE
   @Path("queue/{query-id}")
   Response deleteIndexQueueItem(@PathParam("query-id") int queryId);

}
