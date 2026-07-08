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
import javax.ws.rs.POST;
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

   /**
    * Re-indexes the search tags table for all baseline (non-working) branches. This regenerates hash-based tags for
    * external resources and is required after migrating from the legacy bit-packed encoding.
    */
   @POST
   @Path("reindex/baseline")
   Response reindexBaselineBranches(@DefaultValue("false") @QueryParam("includeWorking") boolean includeWorking);

   /**
    * Fast re-index of all current attributes. Queries osee_attribute directly (no branch traversal) for all taggable
    * attributes with tx_current = 1. Much faster than the branch-based reindex for full migration after truncating
    * osee_search_tags. Returns the number of gammas processed. Optionally pass an attrTypeId to index only one type.
    */
   @POST
   @Path("reindex/all")
   Response reindexAllCurrent(@DefaultValue("0") @QueryParam("attrTypeId") long attrTypeId);

}
