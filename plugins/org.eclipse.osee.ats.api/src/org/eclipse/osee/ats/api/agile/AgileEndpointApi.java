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

/**
 * @author Donald G. Dunne
 */
@Path("agile")
public interface AgileEndpointApi {

   @GET
   public String get();

   @GET
   @Path("teams")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxAgileTeam> team() throws Exception;

   @GET
   @Path("teams/{teamUuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileTeam getTeam(@PathParam("teamUuid") long teamUuid);

   @GET
   @Path("teams/{teamUuid}/features")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxAgileFeatureGroup> getFeatureGroups(@PathParam("teamUuid") long teamUuid);

   @GET
   @Path("teams/{teamUuid}/features/{featureUuid}")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileFeatureGroup getFeatureGroup(long teamUuid, long featureUuid);

   @GET
   @Path("teams/{teamUuid}/backlog")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileBacklog getBacklog(@PathParam("teamUuid") long teamUuid);

   @GET
   @Path("teams/{teamUuid}/backlog/item")
   @Produces(MediaType.APPLICATION_JSON)
   List<AgileItem> getBacklogItems(@PathParam("teamUuid") long teamUuid);

   @GET
   @Path("teams/{teamUuid}/sprints")
   @Produces(MediaType.APPLICATION_JSON)
   public List<JaxAgileSprint> getSprints(@PathParam("teamUuid") long teamUuid);

   @POST
   @Path("teams")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createTeam(JaxAgileTeam newTeam);

   @POST
   @Path("teams/{teamUuid}/features")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createFeatureGroup(@PathParam("teamUuid") long teamUuid, JaxAgileFeatureGroup newFeatureGroup);

   @POST
   @Path("teams/{teamUuid}/sprints")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createSprint(@PathParam("teamUuid") long teamUuid, JaxAgileSprint newSprint);

   @POST
   @Path("teams/{teamUuid}/backlog")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createBacklog(@PathParam("teamUuid") long teamUuid, JaxAgileBacklog newBacklog);

   @PUT
   @Path("items/{itemId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateItem(@PathParam("itemId") long itemId, JaxAgileItem newItem);

   @PUT
   @Path("items")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateItems(JaxAgileItem newItem);

   @DELETE
   @Path("teams/{teamUuid}/features/{featureUuid}")
   public Response deleteFeatureGroup(@PathParam("teamUuid") long teamUuid, @PathParam("featureUuid") long featureUuid);

   @DELETE
   @Path("teams/{teamUuid}")
   public Response deleteTeam(@PathParam("teamUuid") long teamUuid);

}
