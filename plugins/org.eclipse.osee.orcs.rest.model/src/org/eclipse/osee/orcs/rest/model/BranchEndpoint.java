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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.JsonArtifact;
import org.eclipse.osee.framework.core.data.JsonRelations;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.UpdateBranchData;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Roberto E. Escobar
 */
@Path("branches")
public interface BranchEndpoint {

   @POST
   @Path("query")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   List<Branch> getBranches(BranchQueryData query);

   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   BranchId createBranch(NewBranch data);

   @POST
   @Path("validation")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   XResultData createBranchValidation(NewBranch data);

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
      @DefaultValue("false") @QueryParam("deleted") boolean deleted, //
      @DefaultValue("false") @QueryParam("archived") boolean archived, //
      @DefaultValue("") @QueryParam("nameEquals") String nameEquals, //
      @DefaultValue("") @QueryParam("namePattern") String namePattern, //
      @QueryParam("childOf") Long childOf, //
      @QueryParam("ancestorOf") Long ancestorOf, //
      @QueryParam("category") BranchCategoryToken category);

   @GET
   @Path("baseline")
   @Produces(MediaType.APPLICATION_JSON)
   List<Branch> getBaselineBranches();

   @GET
   @Path("working")
   @Produces(MediaType.APPLICATION_JSON)
   List<Branch> getWorkingBranches();

   @GET
   @Path("{type}/category/{category}")
   @Produces(MediaType.APPLICATION_JSON)
   List<Branch> getBranchesByCategoryAndType(@PathParam("type") String type, @PathParam("category") BranchCategoryToken category);

   @GET
   @Path("{branch}")
   @Produces({MediaType.APPLICATION_JSON})
   Branch getBranchById(@PathParam("branch") BranchId branch);

   @GET
   @Path("category/{category}")
   @Produces(MediaType.APPLICATION_JSON)
   List<Branch> getBranchesByCategory(@PathParam("category") BranchCategoryToken id);

   @GET
   @Path("{branch}/category")
   @Produces(MediaType.APPLICATION_JSON)
   List<BranchCategoryToken> getBranchCategories(@PathParam("branch") BranchId branch);

   @POST
   @Path("{branch}/category/{category}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   XResultData setBranchCategory(@PathParam("branch") BranchId branch, @PathParam("category") BranchCategoryToken category);

   @DELETE
   @Path("{branch}/category/{category}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   XResultData deleteBranchCategory(@PathParam("branch") BranchId branch, @PathParam("category") BranchCategoryToken category);

   @POST
   @Path("{branch}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   BranchId createBranchWithId(@PathParam("branch") BranchId branch, NewBranch data);

   @POST
   @Path("{branch}/archive")
   Response archiveBranch(@PathParam("branch") BranchId branch);

   @GET
   @Path("{branch}/artifact/type/{artifactTypes}/attributes")
   @Produces(MediaType.APPLICATION_JSON)
   List<JsonArtifact> getArtifactDetailsByType(@PathParam("branch") BranchId branch, @PathParam("artifactTypes") String artifactTypes);

   @POST
   @Path("{branch}/commit/{destination-branch}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   TransactionResult commitBranch(@PathParam("branch") BranchId branch, @PathParam("destination-branch") BranchId destinationBranch, BranchCommitOptions options);

   @GET
   @Path("{branch1}/diff/{branch2}")
   @Produces({MediaType.APPLICATION_JSON})
   List<ChangeItem> compareBranches(@PathParam("branch1") BranchId branch1, @PathParam("branch2") BranchId branch2);

   @PUT
   @Path("{branch}/name")
   @Consumes({MediaType.TEXT_PLAIN})
   Response setBranchName(@PathParam("branch") BranchId branch, String newName);

   @POST
   @Path("{branch}/program")
   @Consumes({MediaType.TEXT_PLAIN})
   @Produces({MediaType.APPLICATION_JSON})
   BranchToken createProgramBranch(@PathParam("branch") BranchId branchId, String branchName);

   @GET
   @Path("{branch}/relation/type/{relationTypes}")
   @Produces(MediaType.APPLICATION_JSON)
   JsonRelations getRelationsByType(@PathParam("branch") BranchId branch, @PathParam("relationTypes") String relationTypes);

   @PUT
   @Path("{branch}/state/{branch-state}")
   Response setBranchState(@PathParam("branch") BranchId branch, @PathParam("branch-state") BranchState newState);

   @GET
   @Path("{branch}/txs")
   @Produces({MediaType.APPLICATION_JSON})
   List<Transaction> getAllBranchTxs(@PathParam("branch") BranchId branch);

   @POST
   @Path("{branch}/txs")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   Response writeTx(@PathParam("branch") BranchId branch, NewTransaction data);

   @GET
   @Path("{branch}/txs/{tx-id}")
   @Produces({MediaType.APPLICATION_JSON})
   Transaction getBranchTx(@PathParam("branch") BranchId branch, @PathParam("tx-id") TransactionId txId);

   @PUT
   @Path("{branch}/type/{branch-type}")
   Response setBranchType(@PathParam("branch") BranchId branch, @PathParam("branch-type") BranchType newType);

   @GET
   @Path("{branch}/view/{viewId}/artifact/type/{artifactTypes}/attributes")
   @Produces(MediaType.APPLICATION_JSON)
   List<JsonArtifact> getArtifactDetailsByType(@PathParam("branch") BranchId branch, @PathParam("viewId") ArtifactId viewId, @PathParam("artifactTypes") String artifactTypes);

   @POST
   @Path("{branch}/update")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   UpdateBranchData updateBranch(@PathParam("branch") BranchId branch, UpdateBranchData branchData);

   @POST
   @Path("{branch}/permission/{permission}/{subject}")
   void setBranchPermission(@PathParam("subject") ArtifactId subject, @PathParam("branch") BranchId branch, @PathParam("permission") PermissionEnum permission);

   @PUT
   @Path("{branch}/associated-artifact/{art-id}")
   Response associateBranchToArtifact(@PathParam("branch") BranchId branch, @PathParam("art-id") ArtifactId artifact);

   @PUT
   @Path("{branch}/txs/{tx-id}/comment")
   @Consumes({MediaType.TEXT_PLAIN})
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
   @Consumes({MediaType.TEXT_PLAIN})
   Response logBranchActivity(String comment);

   @GET
   @Path("{branchId}/other-mods/{art-id}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   List<BranchId> getOtherBranchesWithModifiedArtifacts(@PathParam("branchId") BranchId branchId, @PathParam("art-id") ArtifactId artifactId);

   /**
    * Undo the latest commit on a branch by purging the latest transaction.
    *
    * @param branch
    * @return
    */
   @DELETE
   @Path("{branchId}/undo")
   @Produces(MediaType.APPLICATION_JSON)
   boolean undoLatest(@PathParam("branchId") BranchId branch);

   /**
    * Undo a commit on a branch by purging a specific transaction.
    *
    * @param branch
    * @param transaction
    * @return
    */
   @DELETE
   @Path("{branchId}/undo/{txId}")
   @Produces(MediaType.APPLICATION_JSON)
   boolean undo(@PathParam("branchId") BranchId branch, @PathParam("txId") TransactionId transaction);

}