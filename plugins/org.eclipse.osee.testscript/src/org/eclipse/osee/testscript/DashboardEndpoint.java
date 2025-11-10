/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript;

import java.util.Collection;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.testscript.internal.CIStatsToken;

/**
 * @author Ryan T. Baldwin
 */
@Path("dashboard")
@Swagger
public interface DashboardEndpoint {

   @GET
   @Path("{branch}/{ciSet}/teamstats")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<CIStatsToken> getTeamStats(@PathParam("branch") BranchId branch,
      @PathParam("ciSet") ArtifactId ciSet, @QueryParam("viewId") ArtifactId viewId);

   @GET
   @Path("{branch}/{ciSet}/subsystemstats")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<CIStatsToken> getSubsystemStats(@PathParam("branch") BranchId branch,
      @PathParam("ciSet") ArtifactId ciSet, @QueryParam("viewId") ArtifactId viewId);

   @GET
   @Path("{branch}/{ciSet}/timeline/teams")
   @Produces(MediaType.APPLICATION_JSON)
   public List<TimelineStatsToken> getTeamTimelines(@PathParam("branch") BranchId branch,
      @PathParam("ciSet") ArtifactId ciSet);

   @GET
   @Path("{branch}/timeline/compare")
   @Produces(MediaType.APPLICATION_JSON)
   public List<TimelineStatsToken> getTimelineCompare(@PathParam("branch") BranchId branch);

   @GET
   @Path("{branch}/{ciSet}/timeline")
   @Produces(MediaType.APPLICATION_JSON)
   public TimelineStatsToken getTimeline(@PathParam("branch") BranchId branch, @PathParam("ciSet") ArtifactId ciSet);

   @POST
   @Path("{branch}/{ciSet}/timeline/update")
   @Produces(MediaType.APPLICATION_JSON)
   public TransactionResult updateTimelines(@PathParam("branch") BranchId branch, @PathParam("ciSet") ArtifactId ciSet);

   @POST
   @Path("{branch}/timeline/update")
   @Produces(MediaType.APPLICATION_JSON)
   public boolean updateAllActiveTimelines(@PathParam("branch") BranchId branch);

   @GET
   @Path("{branch}/subsystems")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<ArtifactAccessorResultWithoutGammas> getSubsystems(@PathParam("branch") BranchId branch,
      @QueryParam("filter") String filter, @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize,
      @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);

   @GET
   @Path("{branch}/subsystems/count")
   @Produces(MediaType.APPLICATION_JSON)
   public Integer getSubsystemsCount(@PathParam("branch") BranchId branch, @QueryParam("filter") String filter);

   @GET
   @Path("{branch}/teams")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<ScriptTeamToken> getTeams(@PathParam("branch") BranchId branch,
      @QueryParam("filter") String filter, @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize,
      @QueryParam("orderByAttributeType") AttributeTypeToken orderByAttributeType);

   @GET
   @Path("{branch}/teams/count")
   @Produces(MediaType.APPLICATION_JSON)
   public Integer getTeamsCount(@PathParam("branch") BranchId branch, @QueryParam("filter") String filter);

   @GET
   @Path("{branch}/export")
   @Produces("text/csv")
   public Response exportDashboardBranchData(@PathParam("branch") BranchId branch,
      @QueryParam("viewId") ArtifactId viewId);

   @GET
   @Path("{branch}/{ciSet}/export")
   @Produces("text/csv")
   public Response exportDashboardSetData(@PathParam("branch") BranchId branch, @PathParam("ciSet") ArtifactId ciSet,
      @QueryParam("viewId") ArtifactId viewId);
}