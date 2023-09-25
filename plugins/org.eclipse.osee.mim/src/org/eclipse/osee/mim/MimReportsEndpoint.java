/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.mim.types.MimReportToken;
import org.eclipse.osee.mim.types.NodeTraceReportItem;

/**
 * @author Ryan Baldwin
 */
@Path("reports")
@Swagger
public interface MimReportsEndpoint {

   @GET()
   @Produces(MediaType.APPLICATION_JSON)
   public List<MimReportToken> getReports();

   @GET()
   @Path("{branchId}/allRequirementsToInterface")
   @Produces(MediaType.APPLICATION_JSON)
   public List<NodeTraceReportItem> getAllRequirementsToInterface(@PathParam("branchId") BranchId branch,
      @QueryParam("pageNum") @DefaultValue("0") long pageNum, @QueryParam("count") @DefaultValue("0") long pageSize);

   @GET()
   @Path("{branchId}/allRequirementsToInterface/count")
   @Produces(MediaType.APPLICATION_JSON)
   public int getCountRequirementsToInterface(@PathParam("branchId") BranchId branch);

   @GET()
   @Path("{branchId}/noRequirementsToInterface")
   @Produces(MediaType.APPLICATION_JSON)
   public List<NodeTraceReportItem> getAllRequirementsToInterfaceWithNoMatch(@PathParam("branchId") BranchId branch,
      @QueryParam("pageNum") @DefaultValue("0") long pageNum, @QueryParam("count") @DefaultValue("0") long pageSize);

   @GET()
   @Path("{branchId}/noRequirementsToInterface/count")
   @Produces(MediaType.APPLICATION_JSON)
   public int getCountRequirementsToInterfaceWithNoMatch(@PathParam("branchId") BranchId branch);

   @GET()
   @Path("{branchId}/allInterfaceToRequirements")
   @Produces(MediaType.APPLICATION_JSON)
   public List<NodeTraceReportItem> getAllInterfaceToRequirements(@PathParam("branchId") BranchId branch,
      @QueryParam("pageNum") @DefaultValue("0") long pageNum, @QueryParam("count") @DefaultValue("0") long pageSize);

   @GET()
   @Path("{branchId}/allInterfaceToRequirements/count")
   @Produces(MediaType.APPLICATION_JSON)
   public int getCountInterfaceToRequirements(@PathParam("branchId") BranchId branch);

   @GET()
   @Path("{branchId}/noInterfaceToRequirements")
   @Produces(MediaType.APPLICATION_JSON)
   public List<NodeTraceReportItem> getAllInterfaceToRequirementsWithNoMatch(@PathParam("branchId") BranchId branch,
      @QueryParam("pageNum") @DefaultValue("0") long pageNum, @QueryParam("count") @DefaultValue("0") long pageSize);

   @GET()
   @Path("{branchId}/noInterfaceToRequirements/count")
   @Produces(MediaType.APPLICATION_JSON)
   public int getCountInterfaceToRequirementsWithNoMatch(@PathParam("branchId") BranchId branch);

   @GET()
   @Path("{branchId}/interfacesFromRequirement/{artId}")
   @Produces(MediaType.APPLICATION_JSON)
   public NodeTraceReportItem getInterfacesFromRequirement(@PathParam("branchId") BranchId branch,
      @PathParam("artId") ArtifactId artId);

   @GET()
   @Path("{branchId}/requirementsFromInterface/{artId}")
   @Produces(MediaType.APPLICATION_JSON)
   public NodeTraceReportItem getRequirementsFromInterface(@PathParam("branchId") BranchId branch,
      @PathParam("artId") ArtifactId artId);

}
