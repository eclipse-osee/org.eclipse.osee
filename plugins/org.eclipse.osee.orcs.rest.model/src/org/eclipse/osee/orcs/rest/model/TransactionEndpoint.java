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
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.jdk.core.type.SystemRoles;

/**
 * @author Roberto E. Escobar
 */
@Path("txs")
@RolesAllowed(SystemRoles.ROLES_AUTHENTICATED)
public interface TransactionEndpoint {

   @GET
   @Produces({MediaType.APPLICATION_JSON})
   List<Transaction> getAllTxs();

   @GET
   @Path("{tx-id}")
   @Produces({MediaType.APPLICATION_JSON})
   Transaction getTx(@PathParam("tx-id") int txId);

   @GET
   @Path("{tx-id1}/diff/{tx-id2}")
   @Produces({MediaType.APPLICATION_JSON})
   CompareResults compareTxs(@PathParam("tx-id1") int txId1, @PathParam("tx-id2") int txId2);

   @PUT
   @Path("{tx-id}/comment/{tx-comment}")
   Response setTxComment(@PathParam("tx-id") int txId, @PathParam("tx-comment") String comment);

   @DELETE
   @Consumes({MediaType.APPLICATION_JSON})
   Response deleteTxs(DeleteTransaction deleteTxs);

   @DELETE
   @Path("{tx-id}")
   Response deleteTxs(@PathParam("tx-id") int txId);

}