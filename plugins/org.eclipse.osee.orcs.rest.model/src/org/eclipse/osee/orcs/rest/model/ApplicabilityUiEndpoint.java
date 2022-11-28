/*********************************************************************
 * Copyright (c) 2018 Boeing
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Donald G. Dunne
 */
@Path("applicui")
public interface ApplicabilityUiEndpoint {

   @GET
   @Produces({MediaType.TEXT_HTML})
   Response get();

   @GET
   @Path("ro")
   @Produces({MediaType.TEXT_HTML})
   Response getRO();

   /**
    * @return Un-archived baseline and working branches available for Product Line configuration
    */
   @GET
   @Path("branches")
   @Produces({MediaType.APPLICATION_JSON})
   List<BranchId> getApplicabilityBranches();

   @GET
   @Path("branches/{branchQueryType}")
   @Produces({MediaType.APPLICATION_JSON})
   List<BranchId> getApplicabilityBranchesByType(@PathParam("branchQueryType") String branchQueryType);

   @GET
   @Path("branch/{branch}")
   @Produces({MediaType.APPLICATION_JSON})
   public ApplicabilityBranchConfig getConfig(@PathParam("branch") BranchId branch);

   @GET
   @Path("branch/{branch}/all")
   @Produces({MediaType.APPLICATION_JSON})
   public ApplicabilityBranchConfig getConfigWithCompoundApplics(@PathParam("branch") BranchId branch);

}