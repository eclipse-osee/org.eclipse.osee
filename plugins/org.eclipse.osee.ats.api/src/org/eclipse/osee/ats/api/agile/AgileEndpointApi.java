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
import org.eclipse.osee.ats.api.util.ILineChart;
import org.eclipse.osee.ats.api.workflow.JaxAtsObjects;
import org.eclipse.osee.framework.core.util.result.XResultData;

/**
 * @author Donald G. Dunne
 */
@Path("agile")
public interface AgileEndpointApi {

   @GET
   public String get();

   @GET
   @Path("team")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxAgileTeam> team() throws Exception;

   @GET
   @Path("team/{teamUuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileTeam getTeam(@PathParam("teamUuid") long teamUuid);

   @GET
   @Path("team/{teamUuid}/feature")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxAgileFeatureGroup> getFeatureGroups(@PathParam("teamUuid") long teamUuid);

   @GET
   @Path("team/{teamUuid}/feature/{featureUuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileFeatureGroup getFeatureGroup(long teamUuid, long featureUuid);

   @GET
   @Path("team/{teamUuid}/backlog")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileBacklog getBacklog(@PathParam("teamUuid") long teamUuid);

   @GET
   @Path("team/{teamUuid}/backlog/item")
   @Produces(MediaType.APPLICATION_JSON)
   List<AgileItem> getBacklogItems(@PathParam("teamUuid") long teamUuid);

   @GET
   @Path("team/{teamUuid}/sprint")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxAgileSprint> getSprints(@PathParam("teamUuid") long teamUuid);

   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/kb")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxKbSprint getSprintItemsForKb(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   @GET
   @Path("team/{teamUuid}/sprintcurrent")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileSprint getSprintCurrent(@PathParam("teamUuid") long teamUuid);

   /**
    * @param query param </br>
    * "type" = "best" then if inwork, get current, else get stored if exists, else get current</br>
    * "type" = "stored" then get stored or show error page </br>
    * "type" = null then get current
    * @return html sprint summary
    */
   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/summary")
   @Produces(MediaType.TEXT_HTML)
   public String getSprintSummary(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/data")
   @Produces(MediaType.APPLICATION_JSON)
   public AgileSprintData getSprintData(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   /**
    * @param query param </br>
    * "type" = "best" then if inwork, get current, else get stored if exists, else get current</br>
    * "type" = "stored" then get stored or show error page </br>
    * "type" = null then get current
    * @return html representation of weekly sprint metrics. Same metrics used in charts.
    */
   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/data/table")
   @Produces(MediaType.TEXT_HTML)
   public String getSprintDataTable(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   /**
    * @return Find only or first sprint and open best burndown available. If in-work, open live version, else stored (if
    * available), else live.
    */
   @GET
   @Path("team/{teamUuid}/burndown?type=best")
   @Produces(MediaType.TEXT_HTML)
   public String getBurndownBest(@PathParam("teamUuid") long teamUuid);

   /**
    * @param query param </br>
    * "type" = "best" then if inwork, get current, else get stored if exists, else get current</br>
    * "type" = "stored" then get stored or show error page </br>
    * "type" = null then get current
    * @return html sprint burndown
    */
   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/burndown/chart/ui")
   @Produces(MediaType.TEXT_HTML)
   public String getSprintBurndownChartUi(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/burndown/chart/data")
   @Produces(MediaType.APPLICATION_JSON)
   public ILineChart getSprintBurndownChartData(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/storereports")
   @Produces(MediaType.APPLICATION_JSON)
   public XResultData storeSprintReports(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   /**
    * @param query param </br>
    * "type" = "best" then if inwork, get current, else get stored if exists, else get current</br>
    * "type" = "stored" then get stored or show error page </br>
    * "type" = null then get current
    * @return html sprint burnup
    */
   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/burnup/chart/ui")
   @Produces(MediaType.TEXT_HTML)
   public String getSprintBurnupChartUi(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/burnup/chart/data")
   @Produces(MediaType.APPLICATION_JSON)
   public ILineChart getSprintBurnupChartData(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/world")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAtsObjects getSprintItemsAsJax(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/world/ui")
   @Produces(MediaType.TEXT_HTML)
   public Response getSprintItemsUI(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/world/ui/{customizeGuid}")
   @Produces(MediaType.TEXT_HTML)
   public Response getSprintItemsUICustomized(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid, @PathParam("customizeGuid") String customizeGuid);

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
   @Path("team/{teamUuid}/feature")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createFeatureGroup(@PathParam("teamUuid") long teamUuid, JaxNewAgileFeatureGroup newFeatureGroup);

   @POST
   @Path("team/{teamUuid}/sprint")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createSprint(@PathParam("teamUuid") long teamUuid, JaxNewAgileSprint newSprint);

   @POST
   @Path("team/{teamUuid}/backlog")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createBacklog(@PathParam("teamUuid") long teamUuid, JaxNewAgileBacklog newBacklog);

   @PUT
   @Path("team/{teamUuid}/backlog")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   Response updateBacklog(long teamUuid, JaxAgileBacklog newBacklog);

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
   @Path("team/{teamUuid}/feature/{featureUuid}")
   public Response deleteFeatureGroup(@PathParam("teamUuid") long teamUuid, @PathParam("featureUuid") long featureUuid);

   @DELETE
   @Path("team/{teamUuid}/sprint/{sprintUuid}")
   public Response deleteSprint(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   @DELETE
   @Path("team/{teamUuid}")
   public Response deleteTeam(@PathParam("teamUuid") long teamUuid);

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
