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
import org.eclipse.osee.ats.api.workflow.JaxAtsObjects;

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
   @Path("team/{teamUuid}/sprint/{sprintUuid}/summary")
   @Produces(MediaType.TEXT_HTML)
   public Response getSprintSummary(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/burndown")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSprintBurndown(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/burndown/ui")
   @Produces(MediaType.TEXT_HTML)
   public Response getSprintBurndownUi(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

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
   public Response updateItem(@PathParam("itemId") long itemId, JaxAgileItem newItem);

   @PUT
   @Path("item")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateItems(JaxAgileItem newItem);

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

   /**
    * If missing, creates artifacts OSEE_Sprint_Burndown.iqy and OSEE_Sprint_Burndown.xls as children of sprint or
    * returns existing if already exist
    */
   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/burndownExcel")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSprintBurndownExcel(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid);

}
