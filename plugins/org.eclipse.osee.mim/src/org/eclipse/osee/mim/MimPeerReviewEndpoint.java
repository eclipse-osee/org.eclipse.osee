/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.mim;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.mim.types.ApplyResult;
import org.eclipse.osee.mim.types.PeerReviewApplyData;

/**
 * @author Audrey Denk
 */
@Path("pr")
@Swagger
public interface MimPeerReviewEndpoint {

   @POST
   @Path("{branchId}/apply")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   ApplyResult applyWorkingBranches(@PathParam("branchId") BranchId prBranch, PeerReviewApplyData data);

   @GET
   @Path("{branchId}/appliedBranches")
   @Produces(MediaType.APPLICATION_JSON)
   List<BranchId> getAppliedBranches(@PathParam("branchId") BranchId prBranch);
}
