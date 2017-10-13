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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.AtsApi;
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
import org.eclipse.osee.ats.api.agile.sprint.SprintConfigurations;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.ILineChart;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.JaxAtsObjects;
import org.eclipse.osee.ats.core.agile.SprintUtil;
import org.eclipse.osee.ats.core.agile.operations.SprintBurndownOperations;
import org.eclipse.osee.ats.core.agile.operations.SprintBurnupOperations;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.chart.LineChart;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.agile.operations.KanbanOperations;
import org.eclipse.osee.ats.rest.internal.agile.operations.SprintConfigOperations;
import org.eclipse.osee.ats.rest.internal.query.TokenSearchOperations;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.ats.rest.internal.world.WorldResource;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.JsonUtil;
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
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;

/**
 * Donald G. Dunne
 */
public class AgileEndpointImpl implements AgileEndpointApi {

   @Context
   private UriInfo uriInfo;
   private final AtsApi atsApi;
   private final IResourceRegistry resourceRegistry;
   private final JdbcService jdbcService;
   private final OrcsApi orcsApi;

   public AgileEndpointImpl(AtsApi atsApi, IResourceRegistry resourceRegistry, JdbcService jdbcService, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.resourceRegistry = resourceRegistry;
      this.jdbcService = jdbcService;
      this.orcsApi = orcsApi;
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
   @Path("team/token")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<ArtifactToken> getTeamTokens() throws Exception {
      return atsApi.getArtifacts(AtsArtifactTypes.AgileTeam);
   }

   @Override
   public List<JaxAgileTeam> team() throws Exception {
      List<JaxAgileTeam> teams = new ArrayList<>();
      for (IAgileTeam team : atsApi.getAgileService().getTeams()) {
         teams.add(toJaxTeam(team));
      }
      return teams;
   }

   @Override
   public JaxAgileTeam getTeam(long teamId) {
      IAgileTeam team = atsApi.getAgileService().getAgileTeamById(teamId);
      return toJaxTeam(team);
   }

   @Override
   @GET
   @Path("team/{teamId}/token")
   @Produces(MediaType.APPLICATION_JSON)
   public ArtifactToken getTeamToken(@PathParam("teamId") long teamId) {
      return atsApi.getQueryService().getArtifactToken(teamId);
   }

   @Override
   @Path("team/{teamId}/workpackage")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsWorkPackage> getWorkPackages(@PathParam("teamId") ArtifactId teamId) {
      IAgileTeam aTeam = atsApi.getAgileService().getAgileTeam(teamId);
      Set<IAtsWorkPackage> wps = new HashSet<>();
      for (Long atsTeamId : aTeam.getAtsTeamIds()) {
         IAtsTeamDefinition teamDef = atsApi.getConfigItem(atsTeamId);
         if (teamDef != null) {
            for (ArtifactId wpArt : atsApi.getRelationResolver().getRelated(teamDef,
               AtsRelationTypes.WorkPackage_WorkPackage)) {
               IAtsWorkPackage wp = atsApi.getConfigItem(wpArt);
               if (wp != null && wp.isActive()) {
                  wps.add(wp);
               }
            }
            for (IAtsActionableItem ai : atsApi.getActionableItemService().getActiveActionableItemsAndChildren(
               teamDef)) {
               for (ArtifactId wpArt : atsApi.getRelationResolver().getRelated(ai,
                  AtsRelationTypes.WorkPackage_WorkPackage)) {
                  IAtsWorkPackage wp = atsApi.getConfigItem(wpArt);
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
   @GET
   @Path("team/{teamId}/member")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ArtifactToken> getTeamMembers(@PathParam("teamId") ArtifactId teamId) {
      IAgileTeam aTeam = atsApi.getConfigItem(teamId);
      Set<IAtsUser> activeMembers = atsApi.getAgileService().getTeamMebers(aTeam);

      // Construct list of users with team members sorted first and other users last
      List<ArtifactToken> results = new LinkedList<>();
      for (IAtsUser user : activeMembers) {
         results.add(ArtifactToken.valueOf(user.getStoreObject(), user.getName() + " (Team)"));
      }
      Collections.sort(results, new NamedComparator(SortOrder.ASCENDING));

      List<ArtifactToken> othersForSort = new LinkedList<>();
      for (IAtsUser user : atsApi.getUserService().getUsers()) {
         if (user.isActive() && !activeMembers.contains(user)) {
            othersForSort.add(user.getStoreObject());
         }
      }
      Collections.sort(othersForSort, new NamedComparator(SortOrder.ASCENDING));
      results.addAll(othersForSort);

      return results;
   }

   @Override
   @Path("team/{teamId}/ai")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<IAtsActionableItem> getActionableAis(@PathParam("teamId") ArtifactId teamId) {
      IAgileTeam aTeam = atsApi.getAgileService().getAgileTeam(teamId);
      List<IAtsActionableItem> ais = new LinkedList<>();
      // If ATS Teams are related, use their actionable items
      for (IAtsTeamDefinition teamDef : atsApi.getAgileService().getAtsTeams(aTeam)) {
         ais.addAll(atsApi.getActionableItemService().getActiveActionableItemsAndChildren(teamDef));
      }
      // Add any AgileTeam to AI relations
      for (ArtifactId aiArt : atsApi.getRelationResolver().getRelated(aTeam,
         AtsRelationTypes.AgileTeamToAtsAtsAis_AtsAis)) {
         ais.add(atsApi.getConfigItemFactory().getActionableItem(aiArt));
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

      Long id = newTeam.getId();
      if (id == null || id <= 0) {
         newTeam.setId(Lib.generateArtifactIdAsInt());
      }

      IAgileTeam updatedTeam = atsApi.getAgileService().createAgileTeam(newTeam);
      JaxAgileTeam created = toJaxTeam(updatedTeam);

      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("teams").path(String.valueOf(created.getId())).build();
      Response response = Response.created(location).entity(created).build();
      return response;
   }

   @Override
   public Response updateTeam(JaxAgileTeam team) {
      IAgileTeam updatedTeam = atsApi.getAgileService().updateAgileTeam(team);
      JaxAgileTeam created = toJaxTeam(updatedTeam);

      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("teams").path(String.valueOf(created.getId())).build();
      Response response = Response.created(location).entity(created).build();
      return response;
   }

   private JaxAgileTeam toJaxTeam(IAgileTeam updatedTeam) {
      JaxAgileTeam created = new JaxAgileTeam();
      created.setName(updatedTeam.getName());
      created.setId(updatedTeam.getId());
      created.setActive(updatedTeam.isActive());
      created.getAtsTeamIds().addAll(updatedTeam.getAtsTeamIds());
      created.setBacklogId(updatedTeam.getBacklogId());
      created.setSprintId(updatedTeam.getSprintId());
      created.setDescription(updatedTeam.getDescription());
      return created;
   }

   @Override
   public Response deleteTeam(long teamId) {
      atsApi.getAgileService().deleteAgileTeam(teamId);
      return Response.ok().build();
   }

   /********************************
    ** Agile Team Feature
    ***********************************/

   @Override
   public List<JaxAgileFeatureGroup> getFeatureGroups(long teamId) {
      List<JaxAgileFeatureGroup> groups = new LinkedList<>();
      ArtifactToken agileTeamArt = atsApi.getArtifact(teamId);
      for (ArtifactToken child : atsApi.getRelationResolver().getChildren(agileTeamArt)) {
         if (child.getName().equals(IAgileService.FEATURE_GROUP_FOLDER_NAME)) {
            for (ArtifactToken subChild : atsApi.getRelationResolver().getChildren(child)) {
               if (atsApi.getStoreService().isOfType(subChild, AtsArtifactTypes.AgileFeatureGroup)) {
                  IAgileFeatureGroup group = atsApi.getConfigItemFactory().getAgileFeatureGroup(subChild);
                  JaxAgileFeatureGroup newGroup = new JaxAgileFeatureGroup();
                  newGroup.setName(group.getName());
                  newGroup.setId(group.getId());
                  newGroup.setActive(group.isActive());
                  newGroup.setTeamId(group.getTeamId());
                  groups.add(newGroup);
               }
            }
         }
      }
      return groups;
   }

   @Override
   public Response createFeatureGroup(long teamId, JaxNewAgileFeatureGroup newFeatureGroup) {
      // validate title
      if (!Strings.isValid(newFeatureGroup.getName())) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "name is not valid");
      }
      if (newFeatureGroup.getTeamId() <= 0) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "teamId is not valid");
      }

      String guid = GUID.create();
      Long id = newFeatureGroup.getId();
      if (id == null || id <= 0) {
         id = Lib.generateArtifactIdAsInt();
      }

      IAgileFeatureGroup team = atsApi.getAgileService().createAgileFeatureGroup(newFeatureGroup.getTeamId(),
         newFeatureGroup.getName(), guid, id);
      JaxAgileFeatureGroup newGroup = new JaxAgileFeatureGroup();
      newGroup.setName(team.getName());
      newGroup.setId(team.getId());
      newGroup.setActive(team.isActive());
      newGroup.setTeamId(team.getTeamId());

      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("teams").path(String.valueOf(newGroup.getTeamId())).path("features").path(
         String.valueOf(newGroup.getId())).build();
      return Response.created(location).entity(newGroup).build();
   }

   @Override
   public JaxAgileFeatureGroup getFeatureGroup(long teamId, long featureId) {
      IAgileFeatureGroup feature =
         atsApi.getAgileService().getAgileFeatureGroups(Arrays.asList(featureId)).iterator().next();
      JaxAgileFeatureGroup created = new JaxAgileFeatureGroup();
      created.setName(feature.getName());
      created.setId(feature.getId());
      created.setTeamId(feature.getTeamId());
      created.setActive(feature.isActive());
      return created;
   }

   @Override
   public Response deleteFeatureGroup(long teamId, long featureId) {
      atsApi.getAgileService().deleteAgileFeatureGroup(featureId);
      return Response.ok().build();
   }

   /********************************
    ** Agile Sprint
    ***********************************/
   @Override
   public Response createSprint(long teamId, JaxNewAgileSprint newSprint) {
      // validate title
      if (!Strings.isValid(newSprint.getName())) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "name is not valid");
      }
      if (newSprint.getTeamId() <= 0) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "teamId is not valid");
      }

      String guid = GUID.create();
      Long id = newSprint.getId();
      if (id == null || id <= 0) {
         id = Lib.generateArtifactIdAsInt();
      }

      IAgileSprint sprint =
         atsApi.getAgileService().createAgileSprint(newSprint.getTeamId(), newSprint.getName(), guid, id);
      JaxAgileSprint created = toJaxSprint(sprint);

      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("teams").path(String.valueOf(newSprint.getTeamId())).path("sprints").path(
         String.valueOf(sprint.getId())).build();
      return Response.created(location).entity(created).build();
   }

   private JaxAgileSprint toJaxSprint(IAgileSprint sprint) {
      JaxAgileSprint created = new JaxAgileSprint();
      created.setName(sprint.getName());
      created.setActive(sprint.isActive());
      created.setId(sprint.getId());
      created.setTeamId(sprint.getTeamId());
      return created;
   }

   @Override
   public List<JaxAgileSprint> getSprints(long teamId) {
      if (teamId <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamId is not valid");
      }
      List<JaxAgileSprint> sprints = new ArrayList<>();
      for (IAgileSprint sprint : atsApi.getAgileService().getSprintsForTeam(teamId)) {
         sprints.add(toJaxSprint(sprint));
      }
      return sprints;
   }

   @Override
   public List<ArtifactToken> getSprintsTokens(long teamId) {
      if (teamId <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamId is not valid");
      }
      Collection<ArtifactToken> relatedSprints = atsApi.getQueryService().getRelatedToTokens(atsApi.getAtsBranch(),
         ArtifactId.valueOf(teamId), AtsRelationTypes.AgileTeamToSprint_Sprint, AtsArtifactTypes.AgileSprint);

      Collection<ArtifactToken> inWorkSprints =
         TokenSearchOperations.getArtifactTokensMatchingAttrValue(atsApi.getAtsBranch(), relatedSprints,
            AtsAttributeTypes.CurrentStateType, StateType.Working.name(), orcsApi, jdbcService);

      List<ArtifactToken> sprints = new ArrayList<>();
      for (ArtifactToken sprintArt : inWorkSprints) {
         sprints.add(sprintArt);
      }
      return sprints;
   }

   @Override
   public JaxAgileSprint getSprint(long teamId, long sprintId) {
      if (teamId <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamId is not valid");
      }
      if (sprintId <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "sprintId is not valid");
      }
      for (IAgileSprint sprint : atsApi.getAgileService().getSprintsForTeam(teamId)) {
         if (sprint.getId().equals(sprintId)) {
            return toJaxSprint(sprint);
         }
      }
      return null;
   }

   @Override
   @GET
   @Path("team/{teamId}/sprintcurrent")
   @Produces(MediaType.APPLICATION_JSON)
   public JaxAgileSprint getSprintCurrent(@PathParam("teamId") long teamId) {
      if (teamId <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamId is not valid");
      }
      for (IAgileSprint sprint : atsApi.getAgileService().getSprintsForTeam(teamId)) {
         if (sprint.isActive()) {
            return toJaxSprint(sprint);
         }
      }
      return null;
   }

   @Override
   public String getSprintSummary(long teamId, long sprintId) {
      try {
         String report = getBestOrStored(sprintId, AgileReportType.Summary, uriInfo);
         if (Strings.isValid(report)) {
            return report;
         }
         ArtifactToken team = atsApi.getArtifact(teamId);
         IAgileSprint sprint = atsApi.getAgileService().getAgileSprint(sprintId);
         SprintPageBuilder page =
            new SprintPageBuilder((ArtifactReadable) team, (ArtifactReadable) sprint.getStoreObject(), atsApi);
         PageCreator appPage = PageFactory.newPageCreator(resourceRegistry);
         String result =
            page.generatePage(appPage, new ClassBasedResourceToken("sprintTemplate.html", SprintPageBuilder.class));
         return result;
      } catch (Exception ex) {
         return AHTML.simplePage(Lib.exceptionToString(ex).replaceAll("\n", "<br/>"));
      }
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
      IAgileSprint sprint = atsApi.getAgileService().getAgileSprint(sprintId);
      Conditions.assertNotNull(sprint, "Invalid Sprint %s", sprintId);
      if ((best && sprint.isCompletedOrCancelled()) || stored) {
         ArtifactToken rptArt = atsApi.getRelationResolver().getChildNamedOrNull(sprint, agileReportType.name());
         if (rptArt != null) {
            return atsApi.getAttributeResolver().getSoleAttributeValue(rptArt, CoreAttributeTypes.NativeContent, null);
         }
         if (stored) {
            return AHTML.simplePage("Stored Summary Not Found");
         }
      }
      return null;
   }

   // Sprint Data and Table
   @Override
   public AgileSprintData getSprintData(long teamId, long sprintId) {
      XResultData results = new XResultData();
      AgileSprintData data = SprintUtil.getAgileSprintData(atsApi, teamId, sprintId, results);
      data.validate();
      return data;
   }

   @Override
   @POST
   @Path("team/{teamId}/sprint/{sprintId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public AgileSprintData updateSprint(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId, AgileSprintData sprintData) {
      XResultData results = new XResultData();
      AgileSprintData data = SprintUtil.updateAgileSprintData(atsApi, teamId, sprintId, sprintData, results);
      return data;
   }

   @Override
   public SprintConfigurations getSprintConfig(long teamId, long sprintId) {
      SprintConfigOperations ops = new SprintConfigOperations(atsApi);
      return ops.get(ArtifactId.valueOf(sprintId));
   }

   @Override
   @POST
   @Path("team/{teamId}/sprint/{sprintId}/config")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public SprintConfigurations updateSprintConfig(@PathParam("teamId") long teamId, @PathParam("sprintId") long sprintId, SprintConfigurations sprintConfig) {
      SprintConfigOperations ops = new SprintConfigOperations(atsApi);
      ops.update(sprintConfig);
      return sprintConfig;
   }

   @Override
   public String getSprintDataTable(long teamId, long sprintId) {
      try {
         String report = getBestOrStored(sprintId, AgileReportType.Data_Table, uriInfo);
         if (Strings.isValid(report)) {
            return report;
         }
         AgileSprintData sprintData = SprintUtil.getAgileSprintData(atsApi, teamId, sprintId, new XResultData());
         XResultData results = sprintData.validate();
         if (results.isErrors()) {
            throw new OseeArgumentException(results.toString());
         }
         SprintDataTableBuilder pageBuilder = new SprintDataTableBuilder(sprintData);
         String html = pageBuilder.getHtml();
         return html;
      } catch (Exception ex) {
         return AHTML.simplePage(Lib.exceptionToString(ex).replaceAll("\n", "<br/>"));
      }
   }

   // Sprint Burndown Data and UI
   @Override
   public ILineChart getSprintBurndownChartData(long teamId, long sprintId) {
      SprintBurndownOperations op = new SprintBurndownOperations(atsApi);
      return op.getChartData(teamId, sprintId);
   }

   @Override
   public String getSprintBurndownChartUi(long teamId, long sprintId) {
      String report = getBestOrStored(sprintId, AgileReportType.Burn_Down, uriInfo);
      if (Strings.isValid(report)) {
         return report;
      }
      SprintBurndownOperations op = new SprintBurndownOperations(atsApi);
      return op.getReportHtml(teamId, sprintId);
   }

   /**
    * Create/update sprint charts and store as artifact as sprint children
    */
   @Override
   public XResultData storeSprintReports(long teamId, long sprintId) {
      return atsApi.getAgileService().storeSprintReports(teamId, sprintId);
   }

   @Override
   public String getSprintBurnupChartUi(long teamId, long sprintId) {
      String report = getBestOrStored(sprintId, AgileReportType.Burn_Up, uriInfo);
      if (Strings.isValid(report)) {
         return report;
      }
      SprintBurnupOperations op = new SprintBurnupOperations(atsApi);
      return op.getReportHtml(teamId, sprintId);
   }

   @Override
   public LineChart getSprintBurnupChartData(long teamId, long sprintId) {
      SprintBurnupOperations op = new SprintBurnupOperations(atsApi);
      return op.getChartData(teamId, sprintId);
   }

   @Override
   public Response deleteSprint(long teamId, long sprintId) {
      atsApi.getAgileService().deleteSprint(sprintId);
      return Response.ok().build();
   }

   @Override
   public List<AgileItem> getSprintItems(long teamId, long sprintId) {
      List<AgileItem> items = new LinkedList<>();
      IAgileSprint sprint = atsApi.getAgileService().getAgileSprint(sprintId);
      IAgileTeam team = atsApi.getAgileService().getAgileTeam(teamId);
      IAgileBacklog backlog = atsApi.getAgileService().getAgileBacklog(team);
      if (sprint != null) {
         int x = 1;
         for (IAgileItem aItem : atsApi.getAgileService().getItems(sprint)) {
            AgileItem item = SprintUtil.getAgileItem(aItem, atsApi);
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
   public JaxKbSprint getSprintItemsForKb(long teamId, long sprintId) {
      return KanbanOperations.getSprintItemsForKb((IAtsServer) atsApi, teamId, sprintId);
   }

   /********************************
    ** Agile Backlog
    ***********************************/
   @Override
   public Response createBacklog(long teamId, JaxNewAgileBacklog newBacklog) {
      // validate title
      if (!Strings.isValid(newBacklog.getName())) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "name is not valid");
      }
      if (newBacklog.getTeamId() <= 0) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "teamId is not valid");
      }

      // create new backlog
      IAgileBacklog backlog = null;
      if (!Strings.isValid(newBacklog.getName())) {
         new OseeWebApplicationException(Status.BAD_REQUEST, "name is not valid");
      }

      String guid = GUID.create();
      Long id = newBacklog.getId();
      if (id == null || id <= 0) {
         id = Lib.generateArtifactIdAsInt();
      }
      ArtifactToken teamArt = atsApi.getArtifact(newBacklog.getTeamId());
      if (!atsApi.getRelationResolver().getRelated(teamArt, AtsRelationTypes.AgileTeamToBacklog_Backlog).isEmpty()) {
         new OseeWebApplicationException(Status.BAD_REQUEST, "Backlog already set for team %s",
            teamArt.toStringWithId());
      }

      backlog = atsApi.getAgileService().createAgileBacklog(newBacklog.getTeamId(), newBacklog.getName(), guid, id);
      JaxAgileBacklog created = toJaxBacklog(backlog);
      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("teams").path(String.valueOf(backlog.getTeamId())).path("backlog").build();
      return Response.created(location).entity(created).build();
   }

   @Override
   public Response updateBacklog(long teamId, JaxAgileBacklog newBacklog) {
      IAgileBacklog backlog = atsApi.getAgileService().updateAgileBacklog(newBacklog);

      JaxAgileBacklog created = toJaxBacklog(backlog);
      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("teams").path(String.valueOf(created.getTeamId())).build();
      return Response.created(location).entity(created).build();
   }

   @Override
   public JaxAgileBacklog getBacklog(long teamId) {
      if (teamId <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamId is not valid");
      }
      IAgileBacklog backlog = atsApi.getAgileService().getBacklogForTeam(teamId);
      if (backlog != null) {
         return toJaxBacklog(backlog);
      }
      return null;
   }

   @Override
   public ArtifactToken getBacklogToken(long teamId) {
      if (teamId <= 0) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamId is not valid");
      }
      return atsApi.getQueryService().getArtifactToken(teamId);
   }

   @Override
   public List<AgileItem> getBacklogItems(long teamId) {
      List<AgileItem> items = new LinkedList<>();
      IAgileTeam team = atsApi.getAgileService().getAgileTeam(teamId);
      IAgileBacklog backlog = atsApi.getAgileService().getAgileBacklog(team);
      if (backlog != null) {
         int x = 1;
         for (IAgileItem aItem : atsApi.getAgileService().getItems(backlog)) {
            AgileItem item = SprintUtil.getAgileItem(aItem, atsApi);
            item.setOrder(x++);
            IAgileSprint sprint = atsApi.getAgileService().getSprint(aItem);
            if (sprint != null) {
               item.setSprint(sprint.getName());
            }
            item.setBacklog(backlog.getName());
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
      result.setId(backlog.getId());
      result.setTeamId(backlog.getTeamId());
      return result;
   }

   /********************************
    ** Agile Item
    ***********************************/
   @Override
   public AgileWriterResult updateAgileItem(long itemId, JaxAgileItem newItem) {
      // validate id
      if (newItem.getIds().isEmpty()) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "itemId is not valid");
      }

      AgileWriterResult result = atsApi.getAgileService().updateAgileItem(newItem);
      JaxAgileItem item = new JaxAgileItem();
      item.getIds().addAll(result.getJaxAgileItem().getIds());
      item.getFeatures().addAll(result.getJaxAgileItem().getFeatures());
      item.setSprintId(result.getJaxAgileItem().getSprintId());

      return result;
   }

   @Override
   public AgileWriterResult updateItems(JaxAgileItem newItem) {
      AgileWriterResult result = atsApi.getAgileService().updateAgileItem(newItem);
      JaxAgileItem item = new JaxAgileItem();
      item.getIds().addAll(result.getJaxAgileItem().getIds());
      item.getFeatures().addAll(result.getJaxAgileItem().getFeatures());
      item.setSprintId(result.getJaxAgileItem().getSprintId());

      return result;
   }

   /********************************
    ** Sprint Reporting
    ***********************************/
   @Override
   public JaxAtsObjects getSprintItemsAsJax(long teamId, long sprintId) {
      ArtifactToken sprintArt = atsApi.getArtifact(sprintId);
      JaxAtsObjects objs = new JaxAtsObjects();
      for (IAtsWorkItem workItem : atsApi.getWorkItemFactory().getWorkItems(
         atsApi.getRelationResolver().getRelated(sprintArt, AtsRelationTypes.AgileSprintToItem_AtsItem))) {
         objs.getAtsObjects().add(JaxAtsObjects.create(workItem));
      }
      return objs;
   }

   public Collection<IAtsWorkItem> getSprintWorkItems(long teamId, long sprintId) {
      ArtifactToken sprintArt = atsApi.getArtifact(sprintId);
      return atsApi.getWorkItemFactory().getWorkItems(
         atsApi.getRelationResolver().getRelated(sprintArt, AtsRelationTypes.AgileSprintToItem_AtsItem));
   }

   @Override
   public Response getSprintItemsUI(long teamId, long sprintId) {
      ArtifactToken sprintArt = atsApi.getArtifact(sprintId);
      Conditions.assertNotNull(sprintArt, "Sprint not found with id %s", sprintId);
      Collection<IAtsWorkItem> myWorldItems = getSprintWorkItems(teamId, sprintId);
      CustomizeData custData = getDefaultAgileCustData();
      Conditions.assertNotNull(custData, "Can't retrieve default customization");
      String table = WorldResource.getCustomizedTable((IAtsServer) atsApi, "Sprint - " + sprintArt.getName(), custData,
         myWorldItems);
      return Response.ok().entity(table).build();
   }

   private CustomizeData getDefaultAgileCustData() {
      CustomizeData result = null;
      try {
         String custDataStr = RestUtil.getResource("support/DefaultAgileCustomization.json");
         if (Strings.isValid(custDataStr)) {
            result = JsonUtil.getMapper().readValue(custDataStr, CustomizeData.class);
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      return result;
   }

   @Override
   public Response getSprintItemsUICustomized(long teamId, long sprintId, String customizeGuid) {
      ArtifactToken sprintArt = atsApi.getArtifact(sprintId);
      Conditions.assertNotNull(sprintArt, "Sprint not found with id %s", sprintId);
      Collection<IAtsWorkItem> myWorldItems = getSprintWorkItems(teamId, sprintId);
      CustomizeData custData = ((IAtsServer) atsApi).getCustomizationByGuid(customizeGuid);
      Conditions.assertNotNull(custData, "Can't retrieve customization with id %s", customizeGuid);
      String table = WorldResource.getCustomizedTable(((IAtsServer) atsApi), "Sprint - " + sprintArt.getName(),
         custData, myWorldItems);
      return Response.ok().entity(table).build();
   }

   @Override
   @PUT
   @Path("item/{itemId}/feature")
   public Response addFeatureGroup(@PathParam("itemId") long itemId, String featureGroupName) {
      ArtifactToken itemArt = atsApi.getArtifact(itemId);
      Conditions.assertNotNull(itemArt, "Work Item not found with id %s", itemId);
      IAgileItem item = atsApi.getWorkItemFactory().getAgileItem(itemArt);
      boolean found = false;
      // check to make sure item is not already related
      for (IAgileFeatureGroup feature : atsApi.getAgileService().getFeatureGroups(item)) {
         if (feature.getName().equals(featureGroupName)) {
            found = true;
            break;
         }
      }
      if (!found) {
         IAgileTeam team = atsApi.getAgileService().getAgileTeam(item);
         for (ArtifactReadable featureArt : ((ArtifactReadable) team.getStoreObject()).getRelated(
            AtsRelationTypes.AgileTeamToFeatureGroup_FeatureGroup)) {
            if (featureArt.getName().equals(featureGroupName)) {
               IAtsChangeSet changes =
                  atsApi.createChangeSet("Add Feature Group to WorkItem", AtsCoreUsers.SYSTEM_USER);
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
      ArtifactToken itemArt = atsApi.getArtifact(itemId);
      Conditions.assertNotNull(itemArt, "Work Item not found with id %s", itemId);
      IAgileItem item = atsApi.getWorkItemFactory().getAgileItem(itemArt);
      IAtsChangeSet changes = atsApi.createChangeSet("Set Agile UnPlanned", AtsCoreUsers.SYSTEM_USER);
      changes.setSoleAttributeValue(item, AtsAttributeTypes.UnPlannedWork, unPlanned);
      changes.execute();
      return Response.ok().build();
   }

   @Override
   @PUT
   @Path("item/{itemId}/points")
   public Response setPoints(@PathParam("itemId") long itemId, String points) {
      ArtifactToken itemArt = atsApi.getArtifact(itemId);
      Conditions.assertNotNull(itemArt, "Work Item not found with id %s", itemId);
      IAgileItem item = atsApi.getWorkItemFactory().getAgileItem(itemArt);
      IAgileTeam team = atsApi.getAgileService().getAgileTeam(item);
      AttributeTypeId agileTeamPointsAttributeType = atsApi.getAgileService().getAgileTeamPointsAttributeType(team);
      IAtsChangeSet changes = atsApi.createChangeSet("Set Points", AtsCoreUsers.SYSTEM_USER);
      changes.setSoleAttributeValue(item, agileTeamPointsAttributeType, points);
      changes.execute();
      return Response.ok().build();
   }

   @Override
   public String getBurndownBest(long teamId) {
      try {
         IAgileSprint sprint = getSingleOrFirstSprint(teamId);
         if (sprint != null) {
            return getSprintBurndownChartUi(teamId, sprint.getId());
         }
      } catch (Exception ex) {
         return Lib.exceptionToString(ex);
      }
      return AHTML.simplePage("No In-Work Sprint found for team " + teamId);
   }

   private IAgileSprint getSingleOrFirstSprint(long teamId) {
      ArtifactToken artifact = atsApi.getArtifact(teamId);
      if (artifact != null) {
         for (ArtifactToken sprintArt : atsApi.getRelationResolver().getRelated(artifact,
            AtsRelationTypes.AgileTeamToSprint_Sprint)) {
            IAgileSprint sprint = atsApi.getWorkItemFactory().getAgileSprint(sprintArt);
            if (sprint.isInWork()) {
               return sprint;
            }
         }
      }
      return null;
   }
}
