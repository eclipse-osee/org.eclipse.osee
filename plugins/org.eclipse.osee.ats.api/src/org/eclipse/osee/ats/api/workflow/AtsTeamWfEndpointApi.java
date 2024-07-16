/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.dto.DiffReportEndpointDto;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.jaxrs.mvc.IdentityView;

/**
 * @author Donald G. Dunne
 */
@Path("teamwf")
@Swagger
public interface AtsTeamWfEndpointApi {

   @GET
   @Path("{id}/changedata")
   @Produces({MediaType.APPLICATION_JSON})
   public List<ChangeItem> getChangeData(@PathParam("id") String id);

   @GET
   @IdentityView
   @Path("{aiId}/version")
   @Produces({MediaType.APPLICATION_JSON})
   Collection<IAtsVersion> getVersionsbyTeamDefinition(@PathParam("aiId") String aiId, @QueryParam("sort") String sort);

   /**
    * @param ids (atsId, artId) of action to display
    */
   @GET
   @Path("{id}")
   @Produces({MediaType.APPLICATION_JSON})
   IAtsTeamWorkflow getTeamWorkflow(@PathParam("id") String id);

   /**
    * @param ids (atsId, artId) of action to display
    */
   @GET
   @Path("ids/{id}")
   @Produces({MediaType.APPLICATION_JSON})
   Collection<IAtsTeamWorkflow> getTeamWorkflows(@PathParam("id") String id);

   /**
    * @param id can be ai,teamdef,workflow id
    */
   @GET
   @IdentityView
   @Path("{id}/changeTypes")
   @Produces({MediaType.APPLICATION_JSON})
   Collection<ChangeTypes> getChangeTypes(@PathParam("id") String id, @QueryParam("sort") String sort);

   @PUT
   @Path("{id}/addchangeids/{teamId}")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   XResultData addChangeIds(@PathParam("id") String workItemId, @PathParam("teamId") String teamId,
      List<String> changeIds);

   @GET
   @Path("{id}/goal")
   @Produces({MediaType.APPLICATION_JSON})
   List<IAtsGoal> getGoals(@PathParam("id") String id);

   @GET
   @Path("search")
   @Produces(MediaType.APPLICATION_JSON)
   List<TeamWorkflowToken> search(@QueryParam("search") String search,
      @QueryParam("originator") List<ArtifactId> originators, @QueryParam("assignee") List<ArtifactId> assignees,
      @QueryParam("inProgressOnly") boolean inProgressOnly, @QueryParam("searchByArtId") boolean searchByArtId,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET
   @Path("search/token")
   @Produces(MediaType.APPLICATION_JSON)
   List<ArtifactToken> searchTokens(@QueryParam("search") String search,
      @QueryParam("originator") List<ArtifactId> originators, @QueryParam("assignee") List<ArtifactId> assignees,
      @QueryParam("inProgressOnly") boolean inProgressOnly, @QueryParam("searchByArtId") boolean searchByArtId,
      @QueryParam("pageNum") long pageNum, @QueryParam("count") long pageSize);

   @GET
   @Path("search/count")
   @Produces(MediaType.APPLICATION_JSON)
   int getSearchResultCount(@QueryParam("search") String search, @QueryParam("originator") List<ArtifactId> originators,
      @QueryParam("assignee") List<ArtifactId> assignees, @QueryParam("inProgressOnly") boolean inProgressOnly,
      @QueryParam("searchByArtId") boolean searchByArtId);

   @GET
   @TeamWorkflowDetails
   @Path("details/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   IAtsTeamWorkflow getTeamWorkflowDetails(@PathParam("id") ArtifactId id);

   @GET
   @Path("release/{release}")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<ArtifactToken> getWfByRelease(@PathParam("release") String releaseName);

   @PUT
   @Path("build/{build}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   XResultData relateReleaseToWorkflow(@PathParam("build") String build, List<String> changeIds);

   @GET
   @Path("diff")
   @Produces(MediaType.APPLICATION_JSON)
   public DiffReportEndpointDto getDiffReportEndpoint();

   @GET
   @Path("{id}/review")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<IAtsAbstractReview> getReviews(@PathParam("id") String id);

   @GET
   @Path("{id}/commitstatus")
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<TeamWorkflowBranchCommitStatus> getBranchCommitStatus(@PathParam("id") ArtifactId teamWfId);

}