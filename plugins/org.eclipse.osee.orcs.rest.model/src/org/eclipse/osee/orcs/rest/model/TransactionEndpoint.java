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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.model.change.ChangeItem;

/**
 * @author Roberto E. Escobar
 */
@Path("txs")
public interface TransactionEndpoint {

   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<Transaction> getAllTxs();

   @GET
   @Path("{tx-id}")
   @Produces({MediaType.APPLICATION_JSON})
   Transaction getTx(@PathParam("tx-id") TransactionId txId);

   @GET
   @Path("{tx-id1}/diff/{tx-id2}")
   @Produces({MediaType.APPLICATION_JSON})
   List<ChangeItem> compareTxs(@PathParam("tx-id1") TransactionId txId1, @PathParam("tx-id2") TransactionId txId2);

   @PUT
   @Path("{tx-id}/comment")
   Response setTxComment(@PathParam("tx-id") TransactionId txId, String comment);

   @DELETE
   @Path("{tx-ids}")
   Response purgeTxs(@PathParam("tx-ids") String txIds);

   @DELETE
   Response purgeUnusedBackingDataAndTransactions();

   @PUT
   @Path("{user}/branch/{branch-id}/transaction/{tx-id}/artifact/{art-id}/comment")
   Response replaceWithBaselineTxVersion(@PathParam("user") UserId userId, @PathParam("branch-id") BranchId branchId, @PathParam("tx-id") TransactionId txId, @PathParam("art-id") ArtifactId artId, String comment);
}