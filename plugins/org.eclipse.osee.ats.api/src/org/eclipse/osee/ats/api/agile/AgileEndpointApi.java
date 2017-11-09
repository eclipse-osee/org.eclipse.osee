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
package org.eclipse.osee.ats.api.agile;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.agile.kanban.JaxKbSprint;
import org.eclipse.osee.ats.api.agile.sprint.SprintConfigurations;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.util.ILineChart;
import org.eclipse.osee.ats.api.util.JaxAtsObjectToken;
import org.eclipse.osee.ats.api.workflow.JaxAtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.util.result.XResultData;

/**
 * @author Donald G. Dunne
 */
@Path("agile")
public interface AgileEndpointApi {

   @GET
   public String get();

   @GET
   @Path("team/token")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxAtsObjectToken> getTeamTokens() throws Exception;

   @GET
   @Path("team")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxAgileTeam> team() throws Exception;

   @GET
   @Path("team/{teamId}")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileTeam getTeam(@PathParam("teamId") long teamId);

   @GET
   @Path("team/{teamId}/token")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAtsObjectToken getTeamToken(@PathParam("teamId") long teamId);

   @GET
   @Path("team/{teamId}/feature")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxAgileFeatureGroup> getFeatureGroups(@PathParam("teamId") long teamId);

   @GET
   @Path("team/{teamId}/ai")
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsActionableItem> getActionableAis(@PathParam("teamId") ArtifactId teamId);

   @GET
   @Path("team/{teamId}/member")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxAtsObjectToken> getTeamMembers(@PathParam("teamId") ArtifactId teamId);

   @Path("team/{teamId}/workpackage")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsWorkPackage> getWorkPackages(@PathParam("teamId") ArtifactId teamId);

   @GET
   @Path("team/{teamId}/feature/{featureId}")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileFeatureGroup getFeatureGroup(long teamId, long featureId);

   @GET
   @Path("team/{teamId}/backlog")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileBacklog getBacklog(@PathParam("teamId") long teamId);

   @GET
   @Path("team/{teamId}/backlog/token")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAtsObjectToken getBacklogToken(@PathParam("teamId") long teamId);

   @GET
   @Path("team/{teamId}/backlog/item")
   @Produces(MediaType.APPLICATION_JSON)
   List<AgileItem> getBacklogItems(@PathParam("teamId") long teamId);

   @GET
   @Path("team/{teamId}/sprint/{sprintId}/item")
   @Produces(MediaType.APPLICATION_JSON)
   List<AgileItem> getSprintItems(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   @GET
   @Path("team/{teamId}/sprint")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxAgileSprint> getSprints(@PathParam("teamId") long teamId);

   /**
    * @return list of in-work sprint tokens
    */
   @GET
   @Path("team/{teamId}/sprint/token")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxAtsObjectToken> getSprintsTokens(@PathParam("teamId") long teamId);

   @GET
   @Path("team/{teamId}/sprint/{sprintId}")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileSprint getSprint(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   @POST
   @Path("team/{teamId}/sprint/{sprintId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public AgileSprintData updateSprint(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId, AgileSprintData sprintData);

   @POST
   @Path("team/{teamId}/sprint/{sprintId}/config")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public SprintConfigurations updateSprintConfig(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId, SprintConfigurations sprintConfig);

   @GET
   @Path("team/{teamId}/sprint/{sprintId}/kb")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxKbSprint getSprintItemsForKb(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   @GET
   @Path("team/{teamId}/sprint/{sprintId}/config")
   @Produces(MediaType.APPLICATION_JSON)
   public SprintConfigurations getSprintConfig(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   @GET
   @Path("team/{teamId}/sprintcurrent")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileSprint getSprintCurrent(@PathParam("teamId") long teamId);

   /**
    * @param query param </br>
    * "type" = "best" then if inwork, get current, else get stored if exists, else get current</br>
    * "type" = "stored" then get stored or show error page </br>
    * "type" = null then get current
    * @return html sprint summary
    */
   @GET
   @Path("team/{teamId}/sprint/{sprintId}/summary")
   @Produces(MediaType.TEXT_HTML)
   public String getSprintSummary(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   @GET
   @Path("team/{teamId}/sprint/{sprintId}/data")
   @Produces(MediaType.APPLICATION_JSON)
   public AgileSprintData getSprintData(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   /**
    * @param query param </br>
    * "type" = "best" then if inwork, get current, else get stored if exists, else get current</br>
    * "type" = "stored" then get stored or show error page </br>
    * "type" = null then get current
    * @return html representation of weekly sprint metrics. Same metrics used in charts.
    */
   @GET
   @Path("team/{teamId}/sprint/{sprintId}/data/table")
   @Produces(MediaType.TEXT_HTML)
   public String getSprintDataTable(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   /**
    * @return Find only or first sprint and open best burndown available. If in-work, open live version, else stored (if
    * available), else live.
    */
   @GET
   @Path("team/{teamId}/burndown?type=best")
   @Produces(MediaType.TEXT_HTML)
   public String getBurndownBest(@PathParam("teamId") long teamId);

   /**
    * @param query param </br>
    * "type" = "best" then if inwork, get current, else get stored if exists, else get current</br>
    * "type" = "stored" then get stored or show error page </br>
    * "type" = null then get current
    * @return html sprint burndown
    */
   @GET
   @Path("team/{teamId}/sprint/{sprintId}/burndown/chart/ui")
   @Produces(MediaType.TEXT_HTML)
   public String getSprintBurndownChartUi(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   @GET
   @Path("team/{teamId}/sprint/{sprintId}/burndown/chart/data")
   @Produces(MediaType.APPLICATION_JSON)
   public ILineChart getSprintBurndownChartData(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   @GET
   @Path("team/{teamId}/sprint/{sprintId}/storereports")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData storeSprintReports(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   /**
    * @param query param </br>
    * "type" = "best" then if inwork, get current, else get stored if exists, else get current</br>
    * "type" = "stored" then get stored or show error page </br>
    * "type" = null then get current
    * @return html sprint burnup
    */
   @GET
   @Path("team/{teamId}/sprint/{sprintId}/burnup/chart/ui")
   @Produces(MediaType.TEXT_HTML)
   public String getSprintBurnupChartUi(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   @GET
   @Path("team/{teamId}/sprint/{sprintId}/burnup/chart/data")
   @Produces(MediaType.APPLICATION_JSON)
   public ILineChart getSprintBurnupChartData(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   @GET
   @Path("team/{teamId}/sprint/{sprintId}/world")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAtsObjects getSprintItemsAsJax(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   @GET
   @Path("team/{teamId}/sprint/{sprintId}/world/ui")
   @Produces(MediaType.TEXT_HTML)
   public Response getSprintItemsUI(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   @GET
   @Path("team/{teamId}/sprint/{sprintId}/world/ui/{customizeGuid}")
   @Produces(MediaType.TEXT_HTML)
   public Response getSprintItemsUICustomized(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId, @PathParam("customizeGuid") String customizeGuid);

   @POST
   @Path("team")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createTeam(JaxNewAgileTeam newTeam);

   @PUT
   @Path("team")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateTeam(JaxAgileTeam team);

   @POST
   @Path("team/{teamId}/feature")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createFeatureGroup(@PathParam("teamId") long teamId, JaxNewAgileFeatureGroup newFeatureGroup);

   @POST
   @Path("team/{teamId}/sprint")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createSprint(@PathParam("teamId") long teamId, JaxNewAgileSprint newSprint);

   @POST
   @Path("team/{teamId}/backlog")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createBacklog(@PathParam("teamId") long teamId, JaxNewAgileBacklog newBacklog);

   @PUT
   @Path("team/{teamId}/backlog")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   Response updateBacklog(long teamId, JaxAgileBacklog newBacklog);

   @PUT
   @Path("item/{itemId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public AgileWriterResult updateAgileItem(@PathParam("itemId") long itemId, JaxAgileItem newItem);

   @PUT
   @Path("items")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public AgileWriterResult updateItems(JaxAgileItem newItem);

   @DELETE
   @Path("team/{teamId}/feature/{featureId}")
   public Response deleteFeatureGroup(@PathParam("teamId") long teamId, @PathParam("featureId") long featureId);

   @DELETE
   @Path("team/{teamId}/sprint/{sprintId}")
   public Response deleteSprint(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId);

   @DELETE
   @Path("team/{teamId}")
   public Response deleteTeam(@PathParam("teamId") long teamId);

   @PUT
   @Path("item/{itemId}/feature")
   public Response addFeatureGroup(@PathParam("itemId") long itemId, String featureGroupName);

   @PUT
   @Path("item/{itemId}/unplanned")
   public Response setUnPlanned(@PathParam("itemId") long itemId, boolean unPlanned);

   /**
    * Set points based on points attribute type configured in Agile Team. ats.Points is default.
    */
   @PUT
   @Path("item/{itemId}/points")
   public Response setPoints(@PathParam("itemId") long itemId, String points);

}
