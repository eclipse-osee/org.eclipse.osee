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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.AgileItem;
import org.eclipse.osee.ats.api.agile.AgileReportType;
import org.eclipse.osee.ats.api.agile.AgileSprintData;
import org.eclipse.osee.ats.api.agile.AgileWriterResult;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileService;
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
import org.eclipse.osee.ats.api.agile.kanban.JaxKbSprint;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.ILineChart;
import org.eclipse.osee.ats.api.workflow.JaxAtsObjects;
import org.eclipse.osee.ats.core.agile.SprintUtil;
import org.eclipse.osee.ats.core.agile.operations.SprintBurndownOperations;
import org.eclipse.osee.ats.core.agile.operations.SprintBurnupOperations;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.chart.LineChart;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.agile.operations.KanbanOperations;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.ats.rest.internal.world.WorldResource;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
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
   private static ObjectMapper mapper;

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
   @Path("team/{teamId}/workpackage")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsWorkPackage> getWorkPackages(@PathParam("teamId") ArtifactId teamId) {
      IAgileTeam aTeam = atsServer.getAgileService().getAgileTeam(teamId);
      Set<IAtsWorkPackage> wps = new HashSet<>();
      for (Long atsTeamUuid : aTeam.getAtsTeamUuids()) {
         IAtsTeamDefinition teamDef = atsServer.getConfigItem(atsTeamUuid);
         if (teamDef != null) {
            for (ArtifactId wpArt : atsServer.getRelationResolver().getRelated(teamDef,
               AtsRelationTypes.WorkPackage_WorkPackage)) {
               IAtsWorkPackage wp = atsServer.getConfigItem(wpArt);
               if (wp != null && wp.isActive()) {
                  wps.add(wp);
               }
            }
            for (IAtsActionableItem ai : atsServer.getActionableItemService().getActiveActionableItemsAndChildren(
               teamDef)) {
               for (ArtifactId wpArt : atsServer.getRelationResolver().getRelated(ai,
                  AtsRelationTypes.WorkPackage_WorkPackage)) {
                  IAtsWorkPackage wp = atsServer.getConfigItem(wpArt);
                  if (wp != null && wp.isActive()) {
                     wps.add(wp);
                  }
               }
            }
         }
      }
      List<IAtsWorkPackage> wpList = new LinkedList<>();
      wpList.addAll(wps);
      Collections.sort(wpList, new NamedComparator(SortOrder.ASCENDING));
      return wpList;
   }

   @Override
   @Path("team/{teamId}/ai")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsActionableItem> getActionableAis(@PathParam("teamId") ArtifactId teamId) {
      IAgileTeam aTeam = atsServer.getAgileService().getAgileTeam(teamId);
      List<IAtsActionableItem> ais = new LinkedList<>();
      for (IAtsTeamDefinition teamDef : atsServer.getAgileService().getAtsTeams(aTeam)) {
         ais.addAll(atsServer.getActionableItemService().getActiveActionableItemsAndChildren(teamDef));
      }
      Collections.sort(ais, new NamedComparator(SortOrder.ASCENDING));
      return ais;
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
      created.setBacklogId(updatedTeam.getBacklogId());
      created.setSprintId(updatedTeam.getSprintId());
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
         if (child.getName().equals(IAgileService.FEATURE_GROUP_FOLDER_NAME)) {
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
   @GET
   @Path("team/{teamUuid}/sprintcurrent")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileSprint getSprintCurrent(@PathParam("teamUuid") long teamUuid) {
      if (teamUuid <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamUuid is not valid");
      }
      for (IAgileSprint sprint : atsServer.getAgileService().getSprintsForTeam(teamUuid)) {
         if (sprint.isActive()) {
            return toJaxSprint(sprint);
         }
      }
      return null;
   }

   @Override
   public String getSprintSummary(long teamId, long sprintId) {
      String report = getBestOrStored(sprintId, AgileReportType.Summary, uriInfo);
      if (Strings.isValid(report)) {
         return report;
      }
      ArtifactReadable team = atsServer.getArtifact(teamId);
      IAgileSprint sprint = atsServer.getAgileService().getAgileSprint(sprintId);
      SprintPageBuilder page = new SprintPageBuilder(team, (ArtifactReadable) sprint.getStoreObject());
      PageCreator appPage = PageFactory.newPageCreator(resourceRegistry);
      String result =
         page.generatePage(appPage, new ClassBasedResourceToken("sprintTemplate.html", SprintPageBuilder.class));
      return result;
   }

   private String getBestOrStored(long sprintId, AgileReportType agileReportType, UriInfo uriInfo) {
      boolean best = false, stored = false;
      if (uriInfo != null) {
         MultivaluedMap<String, String> qp = uriInfo.getQueryParameters(true);
         List<String> values = qp.get("type");
         if (values != null && !values.isEmpty()) {
            best = values.iterator().next().equals("best");
            stored = values.iterator().next().equals("stored");
         }
      }
      IAgileSprint sprint = atsServer.getAgileService().getAgileSprint(sprintId);
      Conditions.assertNotNull(sprint, "Invalid Sprint %s", sprintId);
      if ((best && sprint.isCompletedOrCancelled()) || stored) {
         ArtifactToken rptArt = atsServer.getRelationResolver().getChildNamedOrNull(sprint, agileReportType.name());
         if (rptArt != null) {
            return atsServer.getAttributeResolver().getSoleAttributeValue(rptArt, CoreAttributeTypes.NativeContent,
               null);
         }
         if (stored) {
            return AHTML.simplePage("Stored Summary Not Found");
         }
      }
      return null;
   }

   // Sprint Data and Table
   @Override
   public AgileSprintData getSprintData(long teamUuid, long sprintUuid) {
      AgileSprintData data = SprintUtil.getAgileSprintData(atsServer, teamUuid, sprintUuid);
      data.validate();
      return data;
   }

   @Override
   public String getSprintDataTable(long teamId, long sprintId) {
      String report = getBestOrStored(sprintId, AgileReportType.Data_Table, uriInfo);
      if (Strings.isValid(report)) {
         return report;
      }
      AgileSprintData burndown = SprintUtil.getAgileSprintData(atsServer, teamId, sprintId);
      XResultData results = burndown.validate();
      if (results.isErrors()) {
         throw new OseeArgumentException(results.toString());
      }
      SprintDataTableBuilder pageBuilder = new SprintDataTableBuilder(burndown);
      String html = pageBuilder.getHtml();

      return html;
   }

   // Sprint Burndown Data and UI
   @Override
   public ILineChart getSprintBurndownChartData(long teamUuid, long sprintUuid) {
      SprintBurndownOperations op = new SprintBurndownOperations(atsServer);
      return op.getChartData(teamUuid, sprintUuid);
   }

   @Override
   public String getSprintBurndownChartUi(long teamUuid, long sprintId) {
      String report = getBestOrStored(sprintId, AgileReportType.Burn_Down, uriInfo);
      if (Strings.isValid(report)) {
         return report;
      }
      SprintBurndownOperations op = new SprintBurndownOperations(atsServer);
      return op.getReportHtml(teamUuid, sprintId);
   }

   /**
    * Create/update sprint charts and store as artifact as sprint children
    */
   @Override
   public XResultData storeSprintReports(long teamId, long sprintId) {
      return atsServer.getAgileService().storeSprintReports(teamId, sprintId);
   }

   @Override
   public String getSprintBurnupChartUi(long teamUuid, long sprintId) {
      String report = getBestOrStored(sprintId, AgileReportType.Burn_Up, uriInfo);
      if (Strings.isValid(report)) {
         return report;
      }
      SprintBurnupOperations op = new SprintBurnupOperations(atsServer);
      return op.getReportHtml(teamUuid, sprintId);
   }

   @Override
   public LineChart getSprintBurnupChartData(long teamUuid, long sprintUuid) {
      SprintBurnupOperations op = new SprintBurnupOperations(atsServer);
      return op.getChartData(teamUuid, sprintUuid);
   }

   @Override
   public Response deleteSprint(long teamUuid, long sprintUuid) {
      atsServer.getAgileService().deleteSprint(sprintUuid);
      return Response.ok().build();
   }

   @Override
   public List<AgileItem> getSprintItems(long teamId, long sprintId) {
      List<AgileItem> items = new LinkedList<>();
      IAgileSprint sprint = atsServer.getAgileService().getAgileSprint(sprintId);
      IAgileTeam team = atsServer.getAgileService().getAgileTeam(teamId);
      IAgileBacklog backlog = atsServer.getAgileService().getAgileBacklog(team);
      if (sprint != null) {
         int x = 1;
         for (IAgileItem aItem : atsServer.getAgileService().getItems(sprint)) {
            AgileItem item = SprintUtil.getAgileItem(aItem, atsServer);
            item.setOrder(x++);
            item.setSprint(sprint.getName());
            if (backlog != null) {
               item.setBacklog(backlog.getName());
            }
            items.add(item);
         }
      }
      return items;
   }

   @Override
   public JaxKbSprint getSprintItemsForKb(long teamUuid, long sprintUuid) {
      return KanbanOperations.getSprintItemsForKb(atsServer, teamUuid, sprintUuid);
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
            AgileItem item = SprintUtil.getAgileItem(aItem, atsServer);
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
   public AgileWriterResult updateAgileItem(long itemUuid, JaxAgileItem newItem) {
      // validate uuid
      if (newItem.getUuids().isEmpty()) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "itemUuid is not valid");
      }

      AgileWriterResult result = atsServer.getAgileService().updateAgileItem(newItem);
      JaxAgileItem item = new JaxAgileItem();
      item.getUuids().addAll(result.getJaxAgileItem().getUuids());
      item.getFeatures().addAll(result.getJaxAgileItem().getFeatures());
      item.setSprintUuid(result.getJaxAgileItem().getSprintUuid());

      return result;
   }

   @Override
   public AgileWriterResult updateItems(JaxAgileItem newItem) {
      AgileWriterResult result = atsServer.getAgileService().updateAgileItem(newItem);
      JaxAgileItem item = new JaxAgileItem();
      item.getUuids().addAll(result.getJaxAgileItem().getUuids());
      item.getFeatures().addAll(result.getJaxAgileItem().getFeatures());
      item.setSprintUuid(result.getJaxAgileItem().getSprintUuid());

      return result;
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

   public Collection<IAtsWorkItem> getSprintWorkItems(long teamUuid, long sprintUuid) {
      ArtifactReadable sprintArt = atsServer.getArtifact(sprintUuid);
      return atsServer.getWorkItemFactory().getWorkItems(
         sprintArt.getRelated(AtsRelationTypes.AgileSprintToItem_AtsItem).getList());
   }

   @Override
   public Response getSprintItemsUI(long teamUuid, long sprintUuid) {
      ArtifactReadable sprintArt = atsServer.getArtifact(sprintUuid);
      Conditions.assertNotNull(sprintArt, "Sprint not found with id %s", sprintUuid);
      Collection<IAtsWorkItem> myWorldItems = getSprintWorkItems(teamUuid, sprintUuid);
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
      Collection<IAtsWorkItem> myWorldItems = getSprintWorkItems(teamUuid, sprintUuid);
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
   public String getBurndownBest(long teamUuid) {
      try {
         IAgileSprint sprint = getSingleOrFirstSprint(teamUuid);
         if (sprint != null) {
            return getSprintBurndownChartUi(teamUuid, sprint.getId());
         }
      } catch (Exception ex) {
         return Lib.exceptionToString(ex);
      }
      return AHTML.simplePage("No In-Work Sprint found for team " + teamUuid);
   }

   private IAgileSprint getSingleOrFirstSprint(long teamUuid) {
      ArtifactReadable artifact = atsServer.getArtifact(teamUuid);
      if (artifact != null) {
         for (ArtifactReadable sprintArt : artifact.getRelated(AtsRelationTypes.AgileTeamToSprint_Sprint)) {
            IAgileSprint sprint = atsServer.getWorkItemFactory().getAgileSprint(sprintArt);
            if (sprint.isInWork()) {
               return sprint;
            }
         }
      }
      return null;
   }
}
