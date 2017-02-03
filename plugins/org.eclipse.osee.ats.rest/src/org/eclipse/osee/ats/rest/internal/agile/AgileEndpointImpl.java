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

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.AgileBurndown;
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
import org.eclipse.osee.ats.api.agile.JaxBurndownExcel;
import org.eclipse.osee.ats.api.agile.JaxNewAgileBacklog;
import org.eclipse.osee.ats.api.agile.JaxNewAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxNewAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxNewAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.JaxAtsObjects;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.ats.rest.internal.world.WorldResource;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.jdbc.JdbcService;
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
   private static ObjectMapper mapper;
   private final JdbcService jdbcService;

   public AgileEndpointImpl(IAtsServer atsServer, IResourceRegistry resourceRegistry, JdbcService jdbcService) {
      this.atsServer = atsServer;
      this.resourceRegistry = resourceRegistry;
      this.jdbcService = jdbcService;
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

   @Override
   public Response getSprintBurndown(long teamUuid, long sprintUuid) {
      if (teamUuid <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamUuid is not valid");
      }
      if (sprintUuid <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "sprintUuid is not valid");
      }
      ArtifactReadable sprintArt = getSprint(sprintUuid);
      IAgileTeam agileTeam = atsServer.getAgileService().getAgileTeam(teamUuid);
      IAgileSprint sprint = atsServer.getAgileService().getAgileSprint(sprintArt);

      SprintBurndownDataBuilder builder = new SprintBurndownDataBuilder(agileTeam, sprint, atsServer, jdbcService);
      AgileBurndown burndown = builder.get();

      return Response.ok().entity(burndown).build();
   }

   @Override
   public Response getSprintBurndownUi(long teamUuid, long sprintUuid) {
      if (teamUuid <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamUuid is not valid");
      }
      if (sprintUuid <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "sprintUuid is not valid");
      }
      ArtifactReadable sprintArt = getSprint(sprintUuid);
      IAgileTeam agileTeam = atsServer.getAgileService().getAgileTeam(teamUuid);
      IAgileSprint sprint = atsServer.getAgileService().getAgileSprint(sprintArt);

      SprintBurndownDataBuilder builder = new SprintBurndownDataBuilder(agileTeam, sprint, atsServer, jdbcService);
      AgileBurndown burndown = builder.get();

      SprintBurndownPageBuilder pageBuilder = new SprintBurndownPageBuilder(burndown);
      String html = pageBuilder.getHtml();

      return Response.ok().entity(html).build();
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

   /********************************
    ** Sprint Reporting
    ***********************************/
   @Override
   public JaxAtsObjects getSprintItemsAsJax(long teamUuid, long sprintUuid) {
      ArtifactReadable sprintArt = atsServer.getArtifact(sprintUuid);
      JaxAtsObjects objs = new JaxAtsObjects();
      for (IAtsWorkItem workItem : atsServer.getWorkItemFactory().getWorkItems(
         sprintArt.getRelated(AtsRelationTypes.AgileSprintToItem_AtsItem).getList())) {
         objs.getAtsObjects().add(JaxAtsObjects.create(workItem));
      }
      return objs;
   }

   public Collection<IAtsWorkItem> getSprintItems(long teamUuid, long sprintUuid) {
      ArtifactReadable sprintArt = atsServer.getArtifact(sprintUuid);
      return atsServer.getWorkItemFactory().getWorkItems(
         sprintArt.getRelated(AtsRelationTypes.AgileSprintToItem_AtsItem).getList());
   }

   @Override
   public Response getSprintItemsUI(long teamUuid, long sprintUuid) {
      ArtifactReadable sprintArt = atsServer.getArtifact(sprintUuid);
      Conditions.assertNotNull(sprintArt, "Sprint not found with id %s", sprintUuid);
      Collection<IAtsWorkItem> myWorldItems = getSprintItems(teamUuid, sprintUuid);
      CustomizeData custData = getDefaultAgileCustData();
      Conditions.assertNotNull(custData, "Can't retrieve default customization");
      String table =
         WorldResource.getCustomizedTable(atsServer, "Sprint - " + sprintArt.getName(), custData, myWorldItems);
      return Response.ok().entity(table).build();
   }

   private CustomizeData getDefaultAgileCustData() {
      CustomizeData result = null;
      try {
         String custDataStr = RestUtil.getResource("support/DefaultAgileCustomization.json");
         if (Strings.isValid(custDataStr)) {
            result = getMapper().readValue(custDataStr, CustomizeData.class);
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      return result;
   }

   @Override
   public Response getSprintItemsUICustomized(long teamUuid, long sprintUuid, String customizeGuid) {
      ArtifactReadable sprintArt = atsServer.getArtifact(sprintUuid);
      Conditions.assertNotNull(sprintArt, "Sprint not found with id %s", sprintUuid);
      Collection<IAtsWorkItem> myWorldItems = getSprintItems(teamUuid, sprintUuid);
      CustomizeData custData = atsServer.getCustomizationByGuid(customizeGuid);
      Conditions.assertNotNull(custData, "Can't retrieve customization with id %s", customizeGuid);
      String table =
         WorldResource.getCustomizedTable(atsServer, "Sprint - " + sprintArt.getName(), custData, myWorldItems);
      return Response.ok().entity(table).build();
   }

   public static ObjectMapper getMapper() {
      if (mapper == null) {
         mapper = new ObjectMapper();
         mapper.setDateFormat(new SimpleDateFormat("MMM d, yyyy h:mm:ss aa"));
      }
      return mapper;
   }

   @Override
   @PUT
   @Path("item/{itemId}/feature")
   public Response addFeatureGroup(@PathParam("itemId") long itemId, String featureGroupName) {
      ArtifactReadable itemArt = atsServer.getArtifact(itemId);
      Conditions.assertNotNull(itemArt, "Work Item not found with id %s", itemId);
      IAgileItem item = atsServer.getWorkItemFactory().getAgileItem(itemArt);
      boolean found = false;
      // check to make sure item is not already related
      for (IAgileFeatureGroup feature : atsServer.getAgileService().getFeatureGroups(item)) {
         if (feature.getName().equals(featureGroupName)) {
            found = true;
            break;
         }
      }
      if (!found) {
         IAgileTeam team = atsServer.getAgileService().getAgileTeam(item);
         for (ArtifactReadable featureArt : ((ArtifactReadable) team.getStoreObject()).getRelated(
            AtsRelationTypes.AgileTeamToFeatureGroup_FeatureGroup)) {
            if (featureArt.getName().equals(featureGroupName)) {
               IAtsChangeSet changes =
                  atsServer.createChangeSet("Add Feature Group to WorkItem", AtsCoreUsers.SYSTEM_USER);
               changes.relate(featureArt, AtsRelationTypes.AgileFeatureToItem_AtsItem, item);
               changes.execute();
               return Response.ok().build();
            }
         }
      }
      return Response.notModified().build();
   }

   @Override
   @PUT
   @Path("item/{itemId}/unplanned")
   public Response setUnPlanned(@PathParam("itemId") long itemId, boolean unPlanned) {
      ArtifactReadable itemArt = atsServer.getArtifact(itemId);
      Conditions.assertNotNull(itemArt, "Work Item not found with id %s", itemId);
      IAgileItem item = atsServer.getWorkItemFactory().getAgileItem(itemArt);
      IAtsChangeSet changes = atsServer.createChangeSet("Set Agile UnPlanned", AtsCoreUsers.SYSTEM_USER);
      changes.setSoleAttributeValue(item, AtsAttributeTypes.UnPlannedWork, unPlanned);
      changes.execute();
      return Response.ok().build();
   }

   @Override
   @PUT
   @Path("item/{itemId}/points")
   public Response setPoints(@PathParam("itemId") long itemId, String points) {
      ArtifactReadable itemArt = atsServer.getArtifact(itemId);
      Conditions.assertNotNull(itemArt, "Work Item not found with id %s", itemId);
      IAgileItem item = atsServer.getWorkItemFactory().getAgileItem(itemArt);
      IAgileTeam team = atsServer.getAgileService().getAgileTeam(item);
      AttributeTypeId agileTeamPointsAttributeType = atsServer.getAgileService().getAgileTeamPointsAttributeType(team);
      IAtsChangeSet changes = atsServer.createChangeSet("Set Points", AtsCoreUsers.SYSTEM_USER);
      changes.setSoleAttributeValue(item, agileTeamPointsAttributeType, points);
      changes.execute();
      return Response.ok().build();
   }

   @Override
   @GET
   @Path("team/{teamUuid}/sprint/{sprintUuid}/burndownExcel")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSprintBurndownExcel(@PathParam("teamUuid") long teamUuid, @PathParam("sprintUuid") long sprintUuid) {
      JaxBurndownExcel report = new JaxBurndownExcel();
      ArtifactReadable sprintArt = atsServer.getArtifact(sprintUuid);
      IAgileSprint sprint = atsServer.getWorkItemFactory().getAgileSprint(sprintArt);
      Conditions.assertNotNull(sprintArt, "Sprint not found with id %s", sprintUuid);
      ArtifactReadable burndownExcel = null, burndownQuery = null;
      for (ArtifactReadable art : sprintArt.getChildren()) {
         if (art.getName().equals(
            "OSEE_Sprint_Burndown") && art.getSoleAttributeValue(CoreAttributeTypes.Extension, "").equals("xls")) {
            burndownExcel = art;
         } else if (art.getName().equals(
            "OSEE_Sprint_Burndown") && art.getSoleAttributeValue(CoreAttributeTypes.Extension, "").equals("iqy")) {
            burndownQuery = art;
         }
      }
      if ((burndownExcel != null && burndownQuery == null) || (burndownExcel == null && burndownQuery != null)) {
         report.setError(
            "Either OSEE_Sprint_Burndown.xls or OSEE_Sprint_Burndown.iqy was found.  Both need to be found or none.  Names must match exactly.");
         return Response.ok(report).build();
      }
      try {
         // If not found, create and save artifacts related to this burndown
         if (burndownExcel == null) {
            IAtsChangeSet changes = atsServer.createChangeSet("Create Burndown Artifacts", AtsCoreUsers.SYSTEM_USER);

            // Store xls file to OSEE databasePlugin
            File burndownXls = RestUtil.getResourceAsFile("support/OSEE_Sprint_Burndown.xls");
            FileInputStream burndownFileInputStream = new FileInputStream(burndownXls);

            ArtifactId burndownExcelArt =
               changes.createArtifact(CoreArtifactTypes.GeneralDocument, "OSEE_Sprint_Burndown");
            changes.setSoleAttributeValue(burndownExcelArt, CoreAttributeTypes.Extension, "xls");
            changes.setSoleAttributeFromStream(burndownExcelArt, CoreAttributeTypes.NativeContent,
               burndownFileInputStream);
            changes.relate(sprint, CoreRelationTypes.Default_Hierarchical__Child, burndownExcelArt);
            report.setExcelSheetUuid(burndownExcelArt.getUuid());

            // Store query file to OSEE database
            String burndownqry = RestUtil.getResource("support/OSEE_Sprint_Burndown.iqy");
            burndownqry = burndownqry.replace("BASE_URI", uriInfo.getBaseUri().toString());
            IAgileTeam team =
               atsServer.getConfigItemFactory().getAgileTeam(atsServer.getArtifact(sprint.getTeamUuid()));
            burndownqry = burndownqry.replace("TEAM_ID", team.getId().toString());
            burndownqry = burndownqry.replace("SPRINT_ID", sprintArt.getId().toString());
            ArtifactId burndownQryArt =
               changes.createArtifact(CoreArtifactTypes.GeneralDocument, "OSEE_Sprint_Burndown");
            changes.setSoleAttributeValue(burndownQryArt, CoreAttributeTypes.Extension, "iqy");
            changes.setSoleAttributeFromStream(burndownQryArt, CoreAttributeTypes.NativeContent,
               Lib.stringToInputStream(burndownqry));
            changes.relate(sprint, CoreRelationTypes.Default_Hierarchical__Child, burndownQryArt);
            report.setExcelQueryUuid(burndownQryArt.getUuid());

            changes.execute();
         }
         // Else if found
         else {
            ArtifactReadable excelArt = null, queryArt = null;
            for (ArtifactReadable child : sprintArt.getChildren()) {
               if (child.getSoleAttributeValue(CoreAttributeTypes.Extension, "").equals("xls")) {
                  excelArt = child;
                  report.setExcelSheetUuid(excelArt.getUuid());
               } else if (child.getSoleAttributeValue(CoreAttributeTypes.Extension, "").equals("iqy")) {
                  queryArt = child;
                  report.setExcelQueryUuid(queryArt.getUuid());
               }
            }
            if (excelArt == null) {
               report.setError("Could not access Excel burndown artifact.");
            }
            if (queryArt == null) {
               report.setError(report.getError() + "\nCould not access Excel Query burndown artifact.");
            }
         }
      } catch (Exception ex) {
         throw new OseeWebApplicationException(ex, Status.INTERNAL_SERVER_ERROR);
      }
      return Response.ok(report).build();
   }

}