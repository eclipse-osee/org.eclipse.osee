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
package org.eclipse.osee.ats.rest.internal.agile;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.AgileUtil;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileBacklog;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileItem;
import org.eclipse.osee.ats.api.agile.JaxAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * Donald G. Dunne
 */
public class AgileEndpointImpl implements AgileEndpointApi {

   @Context
   private UriInfo uriInfo;
   private final IAtsServer atsServer;

   public AgileEndpointImpl(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   public void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }

   /********************************
    ** Agile Team
    ***********************************/
   @Override
   public String get() {
      return "Agile Resource";
   }

   @Override
   public List<JaxAgileTeam> team() throws Exception {
      List<JaxAgileTeam> teams = new ArrayList<JaxAgileTeam>();
      for (IAgileTeam team : atsServer.getAgileService().getTeams()) {
         teams.add(toJaxTeam(team));
      }
      return teams;
   }

   @Override
   public JaxAgileTeam getTeam(long teamUuid) {
      IAgileTeam team = atsServer.getAgileService().getAgileTeamById(teamUuid);
      return toJaxTeam(team);
   }

   @Override
   public Response createTeam(JaxAgileTeam team) {
      // validate title
      if (!Strings.isValid(team.getName())) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "name is not valid");
      }

      String guid = team.getGuid();
      if (guid == null) {
         guid = GUID.create();
         team.setGuid(guid);
      }

      IAgileTeam updatedTeam = atsServer.getAgileService().createUpdateAgileTeam(team);
      JaxAgileTeam created = toJaxTeam(updatedTeam);

      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("team").path(String.valueOf(created.getUuid())).build();
      Response response = Response.created(location).entity(created).build();
      return response;
   }

   private JaxAgileTeam toJaxTeam(IAgileTeam updatedTeam) {
      JaxAgileTeam created = new JaxAgileTeam();
      created.setGuid(updatedTeam.getGuid());
      created.setName(updatedTeam.getName());
      created.setUuid(updatedTeam.getId());
      created.getAtsTeamUuids().addAll(updatedTeam.getAtsTeamUuids());
      created.setBacklogUuid(updatedTeam.getBacklogUuid());
      return created;
   }

   @Override
   public Response deleteTeam(@PathParam("teamUuid") long teamUuid) {
      atsServer.getAgileService().deleteAgileTeam(teamUuid);
      return Response.ok().build();
   }

   /********************************
    ** Agile Team Feature
    ***********************************/

   @Override
   public List<JaxAgileFeatureGroup> getFeatureGroups(long teamUuid) {
      List<JaxAgileFeatureGroup> groups = new LinkedList<JaxAgileFeatureGroup>();
      ArtifactReadable agileTeamArt = atsServer.getArtifactByUuid(teamUuid);
      for (ArtifactReadable child : agileTeamArt.getChildren()) {
         if (child.getName().equals(AgileUtil.FEATURE_GROUP_FOLDER_NAME)) {
            for (ArtifactReadable subChild : child.getChildren()) {
               if (subChild.isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
                  IAgileFeatureGroup group = atsServer.getConfigItemFactory().getAgileFeatureGroup(subChild);
                  JaxAgileFeatureGroup newGroup = new JaxAgileFeatureGroup();
                  newGroup.setName(group.getName());
                  newGroup.setUuid(group.getId());
                  newGroup.setGuid(group.getGuid());
                  newGroup.setTeamUuid(group.getTeamUuid());
                  groups.add(newGroup);
               }
            }
         }
      }
      return groups;
   }

   @Override
   public Response createFeatureGroup(long teamUuid, JaxAgileFeatureGroup newFeatureGroup) {
      // validate title
      if (!Strings.isValid(newFeatureGroup.getName())) {
         throw new OseeArgumentException("name is not valid");
      }
      if (newFeatureGroup.getTeamUuid() <= 0) {
         throw new OseeArgumentException("teamUuid is not valid");
      }

      String guid = newFeatureGroup.getGuid();
      if (guid == null) {
         guid = GUID.create();
      }

      IAgileFeatureGroup team =
         atsServer.getAgileService().createAgileFeatureGroup(newFeatureGroup.getTeamUuid(), newFeatureGroup.getName(),
            guid);
      JaxAgileFeatureGroup created = new JaxAgileFeatureGroup();
      created.setGuid(team.getGuid());
      created.setName(team.getName());
      created.setUuid(team.getId());
      created.setTeamUuid(team.getTeamUuid());

      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("feature").path(String.valueOf(created.getUuid())).build();
      return Response.created(location).entity(created).build();
   }

   @Override
   public Response deleteFeatureGroup(long teamUuid, long featureUuid) {
      atsServer.getAgileService().deleteAgileFeatureGroup(featureUuid);
      return Response.ok().build();
   }

   /********************************
    ** Agile Sprint
    ***********************************/
   @Override
   public Response createSprint(long teamUuid, JaxAgileSprint newSprint) {
      // validate title
      if (!Strings.isValid(newSprint.getName())) {
         throw new OseeArgumentException("name is not valid");
      }
      if (newSprint.getTeamUuid() <= 0) {
         throw new OseeArgumentException("teamUuid is not valid");
      }

      String guid = newSprint.getGuid();
      if (guid == null) {
         guid = GUID.create();
      }

      IAgileSprint sprint =
         atsServer.getAgileService().createAgileSprint(newSprint.getTeamUuid(), newSprint.getName(), guid);
      JaxAgileSprint created = toJaxSprint(sprint);

      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("team").path(String.valueOf(newSprint.getTeamUuid())).build();
      return Response.created(location).entity(created).build();
   }

   private JaxAgileSprint toJaxSprint(IAgileSprint sprint) {
      JaxAgileSprint created = new JaxAgileSprint();
      created.setGuid(sprint.getGuid());
      created.setName(sprint.getName());
      created.setUuid(sprint.getId());
      created.setTeamUuid(sprint.getTeamUuid());
      return created;
   }

   @Override
   public List<JaxAgileSprint> getSprints(long teamUuid) {
      if (teamUuid <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamUuid is not valid");
      }
      List<JaxAgileSprint> sprints = new ArrayList<JaxAgileSprint>();
      for (IAgileSprint sprint : atsServer.getAgileService().getSprintsForTeam(teamUuid)) {
         sprints.add(toJaxSprint(sprint));
      }
      return sprints;
   }

   /********************************
    ** Agile Backlog
    ***********************************/
   @Override
   public Response createBacklog(long teamUuid, JaxAgileBacklog newBacklog) {
      // validate title
      if (!Strings.isValid(newBacklog.getName())) {
         throw new OseeArgumentException("name is not valid");
      }
      if (newBacklog.getTeamUuid() <= 0) {
         throw new OseeArgumentException("teamUuid is not valid");
      }

      String guid = newBacklog.getGuid();
      if (guid == null) {
         guid = GUID.create();
      }

      IAgileBacklog backlog =
         atsServer.getAgileService().createAgileBacklog(newBacklog.getTeamUuid(), newBacklog.getName(), guid);
      JaxAgileBacklog created = toJaxBacklog(backlog);
      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("team").path(String.valueOf(created.getTeamUuid())).build();
      return Response.created(location).entity(created).build();
   }

   @Override
   public JaxAgileBacklog getBacklog(long teamUuid) {
      if (teamUuid <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamUuid is not valid");
      }
      IAgileBacklog backlog = atsServer.getAgileService().getBacklogForTeam(teamUuid);
      if (backlog != null) {
         return toJaxBacklog(backlog);
      }
      return null;
   }

   private JaxAgileBacklog toJaxBacklog(IAgileBacklog backlog) {
      JaxAgileBacklog result = new JaxAgileBacklog();
      result.setActive(backlog.isActive());
      result.setGuid(backlog.getGuid());
      result.setName(backlog.getName());
      result.setUuid(backlog.getId());
      return result;
   }

   /********************************
    ** Agile Item
    ***********************************/
   @Override
   public Response updateItem(long teamUuid, JaxAgileItem newItem) {
      // validate uuid
      if (newItem.getUuids().isEmpty()) {
         throw new OseeArgumentException("uuids is not valid");
      }

      JaxAgileItem sprint = atsServer.getAgileService().updateItem(newItem);
      JaxAgileItem item = new JaxAgileItem();
      item.getUuids().addAll(sprint.getUuids());
      item.getFeatures().addAll(sprint.getFeatures());
      item.setSprintUuid(sprint.getSprintUuid());

      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("team").path(String.valueOf(item.getSprintUuid())).build();
      return Response.created(location).entity(item).build();
   }

}
