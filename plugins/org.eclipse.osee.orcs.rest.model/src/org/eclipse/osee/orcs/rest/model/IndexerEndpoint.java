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
    * Fast re-index of all current attributes using the queue-based async pipeline. Queries osee_attribute directly
    * (no branch traversal) for all taggable attributes with tx_current = 1 that do not already have entries in
    * osee_search_tags_hash. Returns 202 immediately; indexing runs in the background.
    * <p>
    * Best for incremental top-ups: picks up newly-created attributes that haven't been indexed yet.
    * For bulk migration after a full table truncate, prefer {@code /reindex/direct} which is significantly faster.
    */
   @POST
   @Path("reindex/all")
   Response reindexAllCurrent(@DefaultValue("0") @QueryParam("attrTypeId") long attrTypeId);

   /**
    * Direct batch re-index that bypasses the async pipeline. Streams attribute data, tokenizes in Java, and batch
    * inserts tags directly into osee_search_tags_hash. Skips gammas that already have tags (safe to call on a
    * partially-indexed database). Returns 202 immediately; indexing runs in the background.
    * <p>
    * Preferred for bulk migration after truncating osee_search_tags_hash, as it avoids the overhead of the
    * queue/join-table pipeline. For incremental re-indexing of a few missing gammas, {@code /reindex/all} is simpler.
    */
   @POST
   @Path("reindex/direct")
   Response reindexDirect(@DefaultValue("0") @QueryParam("attrTypeId") long attrTypeId);

}
