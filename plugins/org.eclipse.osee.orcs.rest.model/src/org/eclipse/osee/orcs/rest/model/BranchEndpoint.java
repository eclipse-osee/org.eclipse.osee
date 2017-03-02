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

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.change.CompareResults;

/**
 * @author Roberto E. Escobar
 */
@Path("branches")
public interface BranchEndpoint {

   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<Branch> getBranches();

   /**
    * Perform a branch query based on query parameter input
    *
    * @param branchUuids comma separated list of branch uuids
    * @param branchTypes comma separated list of {@link org.eclipse.osee.framework.core.enums.BranchType BranchType}
    * @param branchStates comma separated list of {@link org.eclipse.osee.framework.core.enums.BranchState BranchState}
    * @param deleted to include deleted branches in the search
    * @param archived to include archived branches in the search
    * @param childOf branch uuid of the parent to search children of
    * @param ancestorOf branch uuid of ancestor to search decendents of
    * @param pretty if the returned JSON should be pretty printed
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   List<Branch> getBranches(@DefaultValue("") @QueryParam("branchUuids") String branchUuids, //
   @DefaultValue("") @QueryParam("branchTypes") String branchTypes, //
   @DefaultValue("") @QueryParam("branchStates") String branchStates, //
   @QueryParam("deleted") boolean deleted, //
   @QueryParam("archived") boolean archived, //
   @DefaultValue("") @QueryParam("nameEquals") String nameEquals, //
   @DefaultValue("") @QueryParam("namePattern") String namePattern, //
   @QueryParam("childOf") Long childOf, //
   @QueryParam("ancestorOf") Long ancestorOf);

   @GET
   @Path("baseline")
   @Produces(MediaType.APPLICATION_JSON)
   List<Branch> getBaselineBranches();

   @GET
   @Path("working")
   @Produces(MediaType.APPLICATION_JSON)
   List<Branch> getWorkingBranches();

   @GET
   @Path("{branch-uuid}")
   @Produces({MediaType.APPLICATION_JSON})
   Branch getBranch(@PathParam("branch-uuid") long branchUuid);

   @GET
   @Path("{branch-uuid}/diff/{branch-uuid2}")
   @Produces({MediaType.APPLICATION_JSON})
   CompareResults compareBranches(@PathParam("branch-uuid") long branchUuid, @PathParam("branch-uuid2") long branchUuid2);

   @GET
   @Path("{branch-uuid}/txs")
   @Produces({MediaType.APPLICATION_JSON})
   List<Transaction> getAllBranchTxs(@PathParam("branch-uuid") long branchUuid);

   @GET
   @Path("{branch-uuid}/txs/{tx-id}")
   @Produces({MediaType.APPLICATION_JSON})
   Transaction getBranchTx(@PathParam("branch-uuid") long branchUuid, @PathParam("tx-id") int txId);

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<Branch> getBranches(BranchQueryData query);

   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   Response createBranch(NewBranch data);

   @POST
   @Path("{branch-uuid}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   Response createBranchWithId(@PathParam("branch-uuid") long branchUuid, NewBranch data);

   @POST
   @Path("{branch-uuid}/commit/{destination-branch-uuid}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   Response commitBranch(@PathParam("branch-uuid") long branchUuid, @PathParam("destination-branch-uuid") long destinationBranchUuid, BranchCommitOptions options);

   @POST
   @Path("{branch-uuid}/archive")
   Response archiveBranch(@PathParam("branch-uuid") long branchUuid);

   @POST
   @Path("{branch-uuid}/txs")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   Response writeTx(@PathParam("branch-uuid") long branchUuid, NewTransaction data);

   @POST
   @Path("exchange/validation")
   Response validateExchange(@QueryParam("path") String path);

   @POST
   @Path("exchange/export")
   @Consumes({MediaType.APPLICATION_JSON})
   Response exportBranches(BranchExportOptions options);

   @POST
   @Path("exchange/import")
   @Consumes({MediaType.APPLICATION_JSON})
   Response importBranches(BranchImportOptions options);

   @DELETE
   @Path("exchange")
   Response deleteBranchExchange(@QueryParam("path") String path);

   @PUT
   @Path("{branch-uuid}/name")
   Response setBranchName(@PathParam("branch-uuid") long branchUuid, String newName);

   @PUT
   @Path("{branch-uuid}/type/{branch-type}")
   Response setBranchType(@PathParam("branch-uuid") long branchUuid, @PathParam("branch-type") BranchType newType);

   @PUT
   @Path("{branch-uuid}/state/{branch-state}")
   Response setBranchState(@PathParam("branch-uuid") long branchUuid, @PathParam("branch-state") BranchState newState);

   @PUT
   @Path("{branch-uuid}/associated-artifact/{art-id}")
   Response associateBranchToArtifact(@PathParam("branch-uuid") long branchUuid, @PathParam("art-id") int artifactId);

   @PUT
   @Path("{branch-uuid}/txs/{tx-id}/comment/{tx-comment}")
   Response setTxComment(@PathParam("branch-uuid") long branchUuid, @PathParam("tx-id") int txId, @PathParam("tx-comment") String comment);

   @DELETE
   @Path("{branch-uuid}")
   Response purgeBranch(@PathParam("branch-uuid") long branchUuid, @DefaultValue("false") @QueryParam("recurse") boolean recurse);

   @DELETE
   @Path("{branch-uuid}/associated-artifact")
   Response unassociateBranch(@PathParam("branch-uuid") long branchUuid);

   @DELETE
   @Path("{branch-uuid}/commit/{destination-branch-uuid}")
   @Produces({MediaType.APPLICATION_JSON})
   Response unCommitBranch(@PathParam("branch-uuid") long branchUuid, @PathParam("destination-branch-uuid") long destinationBranchUuid);

   @DELETE
   @Path("{branch-uuid}/archive")
   @Produces({MediaType.APPLICATION_JSON})
   Response unarchiveBranch(@PathParam("branch-uuid") long branchUuid);

   @DELETE
   @Path("{branch-uuid}/txs/{tx-ids}")
   Response purgeTxs(@PathParam("branch-uuid") long branchUuid, @PathParam("tx-ids") String txIds);

   @PUT
   @Path("log/{comment}")
   Response logBranchActivity(@PathParam("comment") String comment);
}
