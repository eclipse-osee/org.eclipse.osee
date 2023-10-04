/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.api.util;

import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Audrey E Denk
 */
@Path("ple")
@Swagger
public interface AtsProductLineEndpointApi {

   @GET
   @Path("branches")
   @Produces(MediaType.APPLICATION_JSON)
   public List<BranchToken> getBranches(@QueryParam("type") @DefaultValue("-1") BranchType type,
      @QueryParam("workType") @DefaultValue("None") String workType,
      @QueryParam("category") @DefaultValue("-1") BranchCategoryToken category,
      @QueryParam("filter") @DefaultValue("") String filter, @QueryParam("pageNum") @DefaultValue("-1") long pageNum,
      @QueryParam("count") @DefaultValue("-1") long pageSize);

   @GET
   @Path("branches/count")
   @Produces(MediaType.APPLICATION_JSON)
   public int getBranchCount(@QueryParam("type") @DefaultValue("-1") BranchType type,
      @QueryParam("workType") @DefaultValue("None") String workType,
      @QueryParam("category") @DefaultValue("-1") BranchCategoryToken category,
      @QueryParam("filter") @DefaultValue("") String filter);

   @GET
   @Path("action/{id}/approval")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData checkPlarbApproval(@PathParam("id") String id);

   @POST
   @Path("action/{id}/approval")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData setPlarbApproval(@PathParam("id") String id);
}