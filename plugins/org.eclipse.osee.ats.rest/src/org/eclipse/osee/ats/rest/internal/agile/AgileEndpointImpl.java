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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.AgileItem;
import org.eclipse.osee.ats.api.agile.AgileUtil;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileBacklog;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileItem;
import org.eclipse.osee.ats.api.agile.JaxAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxNewAgileBacklog;
import org.eclipse.osee.ats.api.agile.JaxNewAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxNewAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxNewAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;

/**
 * Donald G. Dunne
 */
public class AgileEndpointImpl implements AgileEndpointApi {

   @Context
   private UriInfo uriInfo;
   private final IAtsServer atsServer;
   private final IResourceRegistry resourceRegistry;

   public AgileEndpointImpl(IAtsServer atsServer, IResourceRegistry resourceRegistry) {
      this.atsServer = atsServer;
      this.resourceRegistry = resourceRegistry;
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
      List<JaxAgileTeam> teams = new ArrayList<>();
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
   public Response createTeam(JaxNewAgileTeam newTeam) {
      // validate title
      if (!Strings.isValid(newTeam.getName())) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "name is not valid");
      }

      Long uuid = newTeam.getUuid();
      if (uuid == null || uuid <= 0) {
         newTeam.setUuid(Lib.generateArtifactIdAsInt());
      }

      IAgileTeam updatedTeam = atsServer.getAgileService().createAgileTeam(newTeam);
      JaxAgileTeam created = toJaxTeam(updatedTeam);

      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("teams").path(String.valueOf(created.getUuid())).build();
      Response response = Response.created(location).entity(created).build();
      return response;
   }

   @Override
   public Response updateTeam(JaxAgileTeam team) {
      IAgileTeam updatedTeam = atsServer.getAgileService().updateAgileTeam(team);
      JaxAgileTeam created = toJaxTeam(updatedTeam);

      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("teams").path(String.valueOf(created.getUuid())).build();
      Response response = Response.created(location).entity(created).build();
      return response;
   }

   private JaxAgileTeam toJaxTeam(IAgileTeam updatedTeam) {
      JaxAgileTeam created = new JaxAgileTeam();
      created.setName(updatedTeam.getName());
      created.setUuid(updatedTeam.getId());
      created.setActive(updatedTeam.isActive());
      created.getAtsTeamUuids().addAll(updatedTeam.getAtsTeamUuids());
      created.setBacklogUuid(updatedTeam.getBacklogUuid());
      created.setDescription(updatedTeam.getDescription());
      return created;
   }

   @Override
   public Response deleteTeam(long teamUuid) {
      atsServer.getAgileService().deleteAgileTeam(teamUuid);
      return Response.ok().build();
   }

   /********************************
    ** Agile Team Feature
    ***********************************/

   @Override
   public List<JaxAgileFeatureGroup> getFeatureGroups(long teamUuid) {
      List<JaxAgileFeatureGroup> groups = new LinkedList<>();
      ArtifactReadable agileTeamArt = atsServer.getArtifact(teamUuid);
      for (ArtifactReadable child : agileTeamArt.getChildren()) {
         if (child.getName().equals(AgileUtil.FEATURE_GROUP_FOLDER_NAME)) {
            for (ArtifactReadable subChild : child.getChildren()) {
               if (subChild.isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
                  IAgileFeatureGroup group = atsServer.getConfigItemFactory().getAgileFeatureGroup(subChild);
                  JaxAgileFeatureGroup newGroup = new JaxAgileFeatureGroup();
                  newGroup.setName(group.getName());
                  newGroup.setUuid(group.getId());
                  newGroup.setActive(group.isActive());
                  newGroup.setTeamUuid(group.getTeamUuid());
                  groups.add(newGroup);
               }
            }
         }
      }
      return groups;
   }

   @Override
   public Response createFeatureGroup(long teamUuid, JaxNewAgileFeatureGroup newFeatureGroup) {
      // validate title
      if (!Strings.isValid(newFeatureGroup.getName())) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "name is not valid");
      }
      if (newFeatureGroup.getTeamUuid() <= 0) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "teamUuid is not valid");
      }

      String guid = GUID.create();
      Long uuid = newFeatureGroup.getUuid();
      if (uuid == null || uuid <= 0) {
         uuid = Lib.generateArtifactIdAsInt();
      }

      IAgileFeatureGroup team = atsServer.getAgileService().createAgileFeatureGroup(newFeatureGroup.getTeamUuid(),
         newFeatureGroup.getName(), guid, uuid);
      JaxAgileFeatureGroup newGroup = new JaxAgileFeatureGroup();
      newGroup.setName(team.getName());
      newGroup.setUuid(team.getId());
      newGroup.setActive(team.isActive());
      newGroup.setTeamUuid(team.getTeamUuid());

      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("teams").path(String.valueOf(newGroup.getTeamUuid())).path("features").path(
         String.valueOf(newGroup.getUuid())).build();
      return Response.created(location).entity(newGroup).build();
   }

   @Override
   public JaxAgileFeatureGroup getFeatureGroup(long teamUuid, long featureUuid) {
      IAgileFeatureGroup feature =
         atsServer.getAgileService().getAgileFeatureGroups(Arrays.asList(featureUuid)).iterator().next();
      JaxAgileFeatureGroup created = new JaxAgileFeatureGroup();
      created.setName(feature.getName());
      created.setUuid(feature.getId());
      created.setTeamUuid(feature.getTeamUuid());
      created.setActive(feature.isActive());
      return created;
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
   public Response createSprint(long teamUuid, JaxNewAgileSprint newSprint) {
      // validate title
      if (!Strings.isValid(newSprint.getName())) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "name is not valid");
      }
      if (newSprint.getTeamUuid() <= 0) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "teamUuid is not valid");
      }

      String guid = GUID.create();
      Long uuid = newSprint.getUuid();
      if (uuid == null || uuid <= 0) {
         uuid = Lib.generateArtifactIdAsInt();
      }

      IAgileSprint sprint =
         atsServer.getAgileService().createAgileSprint(newSprint.getTeamUuid(), newSprint.getName(), guid, uuid);
      JaxAgileSprint created = toJaxSprint(sprint);

      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("teams").path(String.valueOf(newSprint.getTeamUuid())).path("sprints").path(
         String.valueOf(sprint.getId())).build();
      return Response.created(location).entity(created).build();
   }

   private JaxAgileSprint toJaxSprint(IAgileSprint sprint) {
      JaxAgileSprint created = new JaxAgileSprint();
      created.setName(sprint.getName());
      created.setActive(sprint.isActive());
      created.setUuid(sprint.getId());
      created.setTeamUuid(sprint.getTeamUuid());
      return created;
   }

   @Override
   public List<JaxAgileSprint> getSprints(long teamUuid) {
      if (teamUuid <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamUuid is not valid");
      }
      List<JaxAgileSprint> sprints = new ArrayList<>();
      for (IAgileSprint sprint : atsServer.getAgileService().getSprintsForTeam(teamUuid)) {
         sprints.add(toJaxSprint(sprint));
      }
      return sprints;
   }

   @Override
   public Response getSprintSummary(long teamUuid, long sprintUuid) {

      if (teamUuid <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamUuid is not valid");
      }
      if (sprintUuid <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "sprintUuid is not valid");
      }

      ArtifactReadable sprint = getSprint(sprintUuid);
      SprintPageBuilder page = new SprintPageBuilder(sprint);
      PageCreator appPage = PageFactory.newPageCreator(resourceRegistry);
      String result =
         page.generatePage(appPage, new ClassBasedResourceToken("sprintTemplate.html", SprintPageBuilder.class));

      return Response.ok().entity(result).build();
   }

   private ArtifactReadable getSprint(long sprintUuid) {
      ArtifactReadable sprint = atsServer.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
         new Long(sprintUuid).intValue()).getResults().getAtMostOneOrNull();
      if (sprint == null) {
         throw new OseeCoreException("Sprint for id:%d not found", sprintUuid);
      }
      return sprint;
   }

   @Override
   public Response deleteSprint(long teamUuid, long sprintUuid) {
      atsServer.getAgileService().deleteSprint(sprintUuid);
      return Response.ok().build();
   }

   /********************************
    ** Agile Backlog
    ***********************************/
   @Override
   public Response createBacklog(long teamUuid, JaxNewAgileBacklog newBacklog) {
      // validate title
      if (!Strings.isValid(newBacklog.getName())) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "name is not valid");
      }
      if (newBacklog.getTeamUuid() <= 0) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "teamUuid is not valid");
      }

      // create new backlog
      IAgileBacklog backlog = null;
      if (!Strings.isValid(newBacklog.getName())) {
         new OseeWebApplicationException(Status.BAD_REQUEST, "name is not valid");
      }

      String guid = GUID.create();
      Long uuid = newBacklog.getUuid();
      if (uuid == null || uuid <= 0) {
         uuid = Lib.generateArtifactIdAsInt();
      }
      ArtifactReadable teamArt = atsServer.getArtifact(newBacklog.getTeamUuid());
      if (!teamArt.getRelated(AtsRelationTypes.AgileTeamToBacklog_Backlog).isEmpty()) {
         new OseeWebApplicationException(Status.BAD_REQUEST, "Backlog already set for team %s",
            teamArt.toStringWithId());
      }

      backlog =
         atsServer.getAgileService().createAgileBacklog(newBacklog.getTeamUuid(), newBacklog.getName(), guid, uuid);
      JaxAgileBacklog created = toJaxBacklog(backlog);
      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("teams").path(String.valueOf(backlog.getTeamUuid())).path("backlog").build();
      return Response.created(location).entity(created).build();
   }

   @Override
   public Response updateBacklog(long teamUuid, JaxAgileBacklog newBacklog) {
      IAgileBacklog backlog = atsServer.getAgileService().updateAgileBacklog(newBacklog);

      JaxAgileBacklog created = toJaxBacklog(backlog);
      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("teams").path(String.valueOf(created.getTeamUuid())).build();
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

   @Override
   public List<AgileItem> getBacklogItems(long teamUuid) {
      List<AgileItem> items = new LinkedList<>();
      IAgileTeam team = atsServer.getAgileService().getAgileTeam(teamUuid);
      IAgileBacklog backlog = atsServer.getAgileService().getAgileBacklog(team);
      if (backlog != null) {
         int x = 1;
         for (IAgileItem aItem : atsServer.getAgileService().getItems(backlog)) {
            AgileItem item = new AgileItem();
            item.setName(aItem.getName());
            item.setFeatureGroups(Collections.toString("; ", atsServer.getAgileService().getFeatureGroups(aItem)));
            item.setUuid(aItem.getId());
            item.setAssignees(Collections.toString("; ", aItem.getStateMgr().getAssigneesStr()));
            item.setAtsId(aItem.getAtsId());
            item.setState(aItem.getStateMgr().getCurrentStateName());
            item.setOrder(x++);
            IAgileSprint sprint = atsServer.getAgileService().getSprint(aItem);
            if (sprint != null) {
               item.setSprint(sprint.getName());
            }
            items.add(item);
         }
      }
      return items;
   }

   private JaxAgileBacklog toJaxBacklog(IAgileBacklog backlog) {
      JaxAgileBacklog result = new JaxAgileBacklog();
      result.setActive(backlog.isActive());
      result.setActive(backlog.isActive());
      result.setName(backlog.getName());
      result.setUuid(backlog.getId());
      result.setTeamUuid(backlog.getTeamUuid());
      return result;
   }

   /********************************
    ** Agile Item
    ***********************************/
   @Override
   public Response updateItem(long itemUuid, JaxAgileItem newItem) {
      // validate uuid
      if (newItem.getUuids().isEmpty()) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "itemUuid is not valid");
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

   @Override
   public Response updateItems(JaxAgileItem newItem) {
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
