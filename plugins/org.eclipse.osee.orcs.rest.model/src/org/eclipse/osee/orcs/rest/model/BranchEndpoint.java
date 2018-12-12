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
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.change.CompareResults;

/**
 * @author Roberto E. Escobar
 */
@Path("branches")
public interface BranchEndpoint {

   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<Branch> getBranches();

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<Branch> getBranches(BranchQueryData query);

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
   @Path("{branch}")
   @Produces({MediaType.APPLICATION_JSON})
   Branch getBranchById(@PathParam("branch") BranchId branch);

   @GET
   @Path("{branch1}/diff/{branch2}")
   @Produces({MediaType.APPLICATION_JSON})
   CompareResults compareBranches(@PathParam("branch1") BranchId branch1, @PathParam("branch2") BranchId branch2);

   @GET
   @Path("{branch}/txs")
   @Produces({MediaType.APPLICATION_JSON})
   List<Transaction> getAllBranchTxs(@PathParam("branch") BranchId branch);

   @GET
   @Path("{branch}/txs/{tx-id}")
   @Produces({MediaType.APPLICATION_JSON})
   Transaction getBranchTx(@PathParam("branch") BranchId branch, @PathParam("tx-id") TransactionId txId);

   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   BranchId createBranch(NewBranch data);

   @POST
   @Path("program")
   @Consumes({MediaType.TEXT_PLAIN})
   @Produces({MediaType.APPLICATION_JSON})
   IOseeBranch createProgramBranch(@HeaderParam("osee.account.id") UserId account, String branchName);

   @POST
   @Path("{branch}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   BranchId createBranchWithId(@PathParam("branch") BranchId branch, NewBranch data);

   @POST
   @Path("{branch}/commit/{destination-branch}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   TransactionToken commitBranch(@PathParam("branch") BranchId branch, @PathParam("destination-branch") BranchId destinationBranch, BranchCommitOptions options);

   @POST
   @Path("{branch}/archive")
   Response archiveBranch(@PathParam("branch") BranchId branch);

   @POST
   @Path("{branch}/txs")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   Response writeTx(@PathParam("branch") BranchId branch, NewTransaction data);

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
   @Path("{branch}/name")
   Response setBranchName(@PathParam("branch") BranchId branch, String newName);

   @PUT
   @Path("{branch}/type/{branch-type}")
   Response setBranchType(@PathParam("branch") BranchId branch, @PathParam("branch-type") BranchType newType);

   @PUT
   @Path("{branch}/state/{branch-state}")
   Response setBranchState(@PathParam("branch") BranchId branch, @PathParam("branch-state") BranchState newState);

   @POST
   @Path("{branch}/permission/{permission}/{subject}")
   void setBranchPermission(@PathParam("subject") ArtifactId subject, @PathParam("branch") BranchId branch, @PathParam("permission") PermissionEnum permission);

   @PUT
   @Path("{branch}/associated-artifact/{art-id}")
   Response associateBranchToArtifact(@PathParam("branch") BranchId branch, @PathParam("art-id") ArtifactId artifact);

   @PUT
   @Path("{branch}/txs/{tx-id}/comment")
   Response setTxComment(@PathParam("branchd") BranchId branch, @PathParam("tx-id") TransactionId txId, String comment);

   @DELETE
   @Path("{branch}")
   Response purgeBranch(@PathParam("branch") BranchId branch, @DefaultValue("false") @QueryParam("recurse") boolean recurse);

   @DELETE
   @Path("{branch}/associated-artifact")
   Response unassociateBranch(@PathParam("branch") BranchId branch);

   @DELETE
   @Path("{branch}/commit/{destination-branch}")
   @Produces({MediaType.APPLICATION_JSON})
   Response unCommitBranch(@PathParam("branch") BranchId branch, @PathParam("destination-branch") BranchId destinationBranch);

   @DELETE
   @Path("{branch}/archive")
   @Produces({MediaType.APPLICATION_JSON})
   Response unarchiveBranch(@PathParam("branch") BranchId branch);

   @DELETE
   @Path("{branch}/txs/{tx-ids}")
   Response purgeTxs(@PathParam("branch") BranchId branch, @PathParam("tx-ids") String txIds);

   @PUT
   @Path("log")
   Response logBranchActivity(String comment);
}