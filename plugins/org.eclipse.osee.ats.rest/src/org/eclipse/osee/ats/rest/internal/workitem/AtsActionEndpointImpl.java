/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.JsonFactory;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.Attribute;
import org.eclipse.osee.ats.api.workflow.AttributeKey;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.ats.rest.internal.util.TargetedVersion;
import org.eclipse.osee.ats.rest.internal.workitem.operations.ActionOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
@Path("action")
public final class AtsActionEndpointImpl implements AtsActionEndpointApi {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;
   private static final String ATS_UI_ACTION_PREFIX = "/ui/action/ID";

   @Context
   private HttpHeaders httpHeaders;

   public AtsActionEndpointImpl(AtsApi atsApi, OrcsApi orcsApi, JsonFactory jsonFactory) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   @Override
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() {
      try {
         return RestUtil.simplePageHtml("Action Resource");
      } catch (Exception ex) {
         return "Error producing action page " + ex.getMessage();
      }
   }

   /**
    * @param ids (artId, atsId) of action to display
    * @return html representation of the action
    */
   @Override
   @Path("{ids}")
   @IdentityView
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public List<IAtsWorkItem> getAction(@PathParam("ids") String ids) {
      List<IAtsWorkItem> workItems = atsApi.getQueryService().getWorkItemsByIds(ids);
      return workItems;
   }

   /**
    * @param ids (artId, atsId) of action to display
    * @return html representation of the action
    */
   @Override
   @Path("{ids}/details")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public List<IAtsWorkItem> getActionDetails(@PathParam("ids") String ids) {
      List<IAtsWorkItem> workItems = atsApi.getQueryService().getWorkItemsByIds(ids);
      return workItems;
   }

   /**
    * @param ids (artId, atsId) of action to display
    * @return html representation of the action
    */
   @Override
   @Path("{ids}/child")
   @IdentityView
   @TargetedVersion
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public List<IAtsWorkItem> getActionChildren(@PathParam("ids") String ids) {
      List<IAtsWorkItem> children = new LinkedList<>();
      for (ArtifactToken action : atsApi.getQueryService().getArtifactsByIds(ids)) {
         for (ArtifactToken childWf : atsApi.getRelationResolver().getRelated(action,
            AtsRelationTypes.ActionToWorkflow_WorkFlow)) {
            IAtsWorkItem child = atsApi.getWorkItemService().getWorkItem(childWf);
            if (child != null) {
               children.add(child);
            }
         }
      }
      return children;
   }

   /**
    * @return valid unreleased versions to select
    */
   @Override
   @GET
   @Path("{id}/UnreleasedVersions")
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getUnreleasedVersionNames(@PathParam("id") String id) {
      List<String> versions = new LinkedList<>();
      IAtsTeamWorkflow teamWf = atsApi.getQueryService().getTeamWf(atsApi.getQueryService().getArtifactById(id));
      IAtsTeamDefinition targedVersionsTeamDef = teamWf.getTeamDefinition().getTeamDefinitionHoldingVersions();
      if (targedVersionsTeamDef != null) {
         for (IAtsVersion version : atsApi.getVersionService().getVersions(targedVersionsTeamDef)) {
            if (!version.isReleased()) {
               versions.add(version.getName());
            }
         }
      }
      return versions;
   }

   /**
    * @return valid transition-to states in order of default state, other states and return states
    */
   @Override
   @GET
   @Path("{id}/TransitionToStates")
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getTransitionToStateNames(@PathParam("id") String id) {
      List<String> states = new LinkedList<>();
      IAtsTeamWorkflow teamWf = atsApi.getQueryService().getTeamWf(atsApi.getQueryService().getArtifactById(id));
      states.add(teamWf.getStateDefinition().getDefaultToState().getName());
      for (IAtsStateDefinition state : teamWf.getStateDefinition().getToStates()) {
         if (!states.contains(state.getName())) {
            states.add(state.getName());
         }
      }
      for (IAtsStateDefinition state : teamWf.getStateDefinition().getOverrideAttributeValidationStates()) {
         if (!states.contains(state.getName())) {
            states.add(state.getName());
         }
      }
      for (IAtsVersion version : atsApi.getVersionService().getVersions(teamWf.getTeamDefinition())) {
         if (!version.isReleased()) {
            states.add(version.getName());
         }
      }
      return states;
   }

   /**
    * @return list of json objects containing artifact ids and names for a related set of requirements
    */
   @Override
   @GET
   @Path("{id}/assocArt/{attrTypeId}")
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getRelatedRequirements(@PathParam("id") ArtifactId workflowId, @PathParam("attrTypeId") AttributeTypeId relatedReqs, @QueryParam("versionType") AttributeTypeId versionType) {
      List<String> requirements = new LinkedList<>();
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(COMMON);
      ArtifactReadable workflow = query.andId(workflowId).getArtifact();
      Integer vertionArtId = workflow.getSoleAttributeValue(versionType);
      ArtifactReadable version = query.andId(ArtifactId.valueOf(vertionArtId)).getArtifact();
      BranchId versionBranch =
         BranchId.valueOf(version.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchId, "-1"));

      String values = workflow.getSoleAttributeValue(relatedReqs);
      if (Strings.isValid(values)) {
         List<String> items = Arrays.asList(values.split("\\s*,\\s*"));
         List<ArtifactId> artIds = Collections.transform(items, ArtifactId::valueOf);

         Collection<ArtifactToken> tokens = atsApi.getQueryService().getArtifacts(artIds, versionBranch);
         for (ArtifactToken token : tokens) {
            requirements.add(
               String.format("{ \"reqUuid\": \"%s\", \"reqName\": \"%s\" }", token.getIdString(), token.getName()));
         }
      }
      return requirements;
   }

   @Override
   @Path("{id}/attributeType/{attrTypeId}")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public Attribute getActionAttributeByType(@PathParam("id") String id, @PathParam("attrTypeId") String attrTypeId) {
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItem(id);
      ActionOperations ops = new ActionOperations(null, workItem, atsApi, orcsApi);
      Attribute attribute = ops.getActionAttributeValues(attrTypeId, workItem);
      return attribute;
   }

   @Override
   @Path("{id}/attributeType/{attrTypeIdOrKey}")
   @PUT
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public Attribute setActionAttributeByType(@PathParam("id") String id, @PathParam("attrTypeIdOrKey") String attrTypeIdOrKey, List<String> values) {
      Conditions.assertNotNull(values, "values can not be null");
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItemsByIds(id).iterator().next();
      IAtsChangeSet changes = atsApi.createChangeSet("Set attr by type/key [" + attrTypeIdOrKey + "]");
      IAtsUser asUser = atsApi.getUserService().getUserByAccountId(httpHeaders);
      if (asUser == null) {
         asUser = AtsCoreUsers.SYSTEM_USER;
      }
      AttributeTypeId attrTypeId = null;
      if (attrTypeIdOrKey.equals(AttributeKey.Title.name())) {
         changes.setSoleAttributeValue(workItem, CoreAttributeTypes.Name, values.iterator().next());
         attrTypeId = CoreAttributeTypes.Name;
      } else if (attrTypeIdOrKey.equals(AttributeKey.Priority.name())) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.PriorityType, values.iterator().next());
         attrTypeId = AtsAttributeTypes.PriorityType;
      } else if (attrTypeIdOrKey.equals(AttributeKey.ColorTeam.name())) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.ColorTeam, values.iterator().next());
         attrTypeId = AtsAttributeTypes.ColorTeam;
      } else if (attrTypeIdOrKey.equals(AttributeKey.IPT.name())) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.IPT, values.iterator().next());
         attrTypeId = AtsAttributeTypes.IPT;
      } else if (attrTypeIdOrKey.equals(AttributeKey.State.name())) {
         String state = values.iterator().next();
         TransitionHelper helper = new TransitionHelper("Transition Workflow", Arrays.asList(workItem), state,
            new ArrayList<IAtsUser>(), "", changes, atsApi, TransitionOption.OverrideAssigneeCheck);
         helper.setTransitionUser(asUser);
         TransitionManager mgr = new TransitionManager(helper);
         TransitionResults results = new TransitionResults();
         mgr.handleTransitionValidation(results);
         if (!results.isEmpty()) {
            throw new OseeArgumentException("Exception transitioning " + results.toString());
         }
         mgr.handleTransition(results);
         if (!results.isEmpty()) {
            throw new OseeArgumentException("Exception transitioning " + results.toString());
         }
         attrTypeId = AtsAttributeTypes.CurrentState;
      } else if (attrTypeIdOrKey.equals(AttributeKey.Version.name())) {
         if (!workItem.isTeamWorkflow()) {
            throw new OseeArgumentException("Not valid to set version for [%s]", workItem.getArtifactTypeName());
         }
         // If values emtpy, clear current version
         IAtsVersion currVersion = atsApi.getVersionService().getTargetedVersion(workItem);
         if (values.isEmpty() && currVersion != null) {
            atsApi.getVersionService().removeTargetedVersion(workItem.getParentTeamWorkflow(), changes);
         }
         // If id, find matching id for this team
         else if (Strings.isNumeric(values.iterator().next())) {
            String version = values.iterator().next();
            if (currVersion == null || !currVersion.getIdString().equals(version)) {
               IAtsVersion newVer = null;
               IAtsTeamDefinition teamDef =
                  workItem.getParentTeamWorkflow().getTeamDefinition().getTeamDefinitionHoldingVersions();
               for (IAtsVersion teamDefVer : atsApi.getVersionService().getVersions(teamDef)) {
                  if (teamDefVer.getIdString().equals(version)) {
                     newVer = teamDefVer;
                     break;
                  }
               }
               if (newVer == null) {
                  throw new OseeArgumentException("Version id [%s] not valid for team ", version,
                     teamDef.toStringWithId());
               }
               atsApi.getVersionService().setTargetedVersion(workItem.getParentTeamWorkflow(), newVer, changes);
            }
         }
         // Else if name, match name with version names for this team
         else if (Strings.isValid(values.iterator().next())) {
            String version = values.iterator().next();
            if (currVersion == null || !currVersion.getName().equals(version)) {
               IAtsVersion newVer = null;
               IAtsTeamDefinition teamDef =
                  workItem.getParentTeamWorkflow().getTeamDefinition().getTeamDefinitionHoldingVersions();
               for (IAtsVersion teamDefVer : atsApi.getVersionService().getVersions(teamDef)) {
                  if (teamDefVer.getName().equals(version)) {
                     newVer = teamDefVer;
                     break;
                  }
               }
               if (newVer == null) {
                  throw new OseeArgumentException("Version name [%s] not valid for team ", version,
                     teamDef.toStringWithId());
               }
               atsApi.getVersionService().setTargetedVersion(workItem.getParentTeamWorkflow(), newVer, changes);
            }
         }
      } else if (attrTypeIdOrKey.equals(AttributeKey.Originator.name())) {
         String accountId = values.iterator().next();
         if (!Strings.isNumeric(accountId)) {
            IAtsUser originator = atsApi.getUserService().getUserByAccountId(Long.valueOf(accountId));
            if (originator == null) {
               throw new OseeArgumentException("No user with account id [%s]", accountId);
            }
            atsApi.getActionFactory().setCreatedBy(workItem, originator, true, workItem.getCreatedDate(), changes);
         }
      } else if (attrTypeIdOrKey.equals(AttributeKey.Assignee.name())) {
         List<IAtsUser> assignees = new LinkedList<>();
         for (String accountIdOrName : values) {
            if (Strings.isNumeric(accountIdOrName)) {
               IAtsUser assignee = atsApi.getUserService().getUserByAccountId(Long.valueOf(accountIdOrName));
               if (assignee == null) {
                  throw new OseeArgumentException("No user with account id [%s]", accountIdOrName);
               } else {
                  assignees.add(assignee);
               }
            } else {
               IAtsUser assignee = atsApi.getUserService().getUserByName(accountIdOrName);
               if (assignee == null) {
                  throw new OseeArgumentException("No user with account name [%s]", accountIdOrName);
               } else {
                  assignees.add(assignee);
               }
            }
         }
         workItem.getStateMgr().setAssignees(assignees);
         changes.add(workItem);
      } else {
         attrTypeId = atsApi.getStoreService().getAttributeType(Long.valueOf(attrTypeIdOrKey));
         if (attrTypeId != null) {
            changes.setAttributeValuesAsStrings(workItem, attrTypeId, values);
         }
      }
      ActionOperations actionOps = new ActionOperations(asUser, workItem, atsApi, orcsApi);
      return actionOps.setActionAttributeByType(id, attrTypeIdOrKey, values);
   }

   @Override
   @Path("{id}/cancel")
   @GET
   @Produces({MediaType.TEXT_HTML})
   public Response cancelAction(@PathParam("id") String id) {
      IAtsUser asUser = atsApi.getUserService().getUserByAccountId(httpHeaders);
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItem(id);
      if (workItem.isInWork()) {
         Conditions.assertNotNull(workItem, "workItem can not be found");
         if (asUser == null) {
            asUser = AtsCoreUsers.SYSTEM_USER;
         }
         ActionOperations ops = new ActionOperations(asUser, workItem, atsApi, orcsApi);
         ops.setActionAttributeByType(id, AttributeKey.State.name(), Arrays.asList("Cancelled"));
      }
      String htmlUrl = atsApi.getWorkItemService().getHtmlUrl(workItem, atsApi);
      try {
         return Response.temporaryRedirect(new URI(htmlUrl)).build();
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   @Path("{workItemId}/changeType/{changeType}")
   @PUT
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Collection<ArtifactToken> setByArtifactToken(@PathParam("workItemId") String workItemId, @PathParam("changeType") String changeType, Collection<ArtifactToken> artifacts) {
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItem(workItemId);
      IAtsUser asUser = atsApi.getUserService().getUserByAccountId(httpHeaders);
      if (asUser == null) {
         asUser = AtsCoreUsers.SYSTEM_USER;
      }
      ActionOperations ops = new ActionOperations(asUser, workItem, atsApi, orcsApi);
      return ops.setByArtifactToken(workItem, changeType, artifacts);

   }

   /**
    * @query_string <attr type name>=<value>, <attr type id>=<value>
    * @return json representation of the matching workItem(s)
    */
   @Override
   @Path("{ids}/legacy/state")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public String getActionStateFromLegacyPcrId(@PathParam("ids") String ids) {
      List<IAtsWorkItem> workItems = new ArrayList<>();
      for (String id : atsApi.getQueryService().getIdsFromStr(ids)) {
         ArtifactToken action = atsApi.getQueryService().getArtifactByLegacyPcrId(id);
         if (action != null) {
            IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(action);
            workItems.add(workItem);
         }
      }
      return atsApi.getActionFactory().getActionStateJson(workItems);
   }

   @Override
   @Path("{ids}/state")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public String getActionState(@PathParam("ids") String ids) {
      List<IAtsWorkItem> workItems = atsApi.getQueryService().getWorkItemsByIds(ids);
      return atsApi.getActionFactory().getActionStateJson(workItems);
   }

   /**
    * @query_string <attr type name>=<value>, <attr type id>=<value>
    * @return json representation of the matching workItem(s)
    */
   @Override
   @Path("query")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public Set<IAtsWorkItem> query(@Context UriInfo uriInfo) {
      Set<IAtsWorkItem> workItems = new HashSet<>();
      MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);
      Set<Entry<String, List<String>>> entrySet = queryParameters.entrySet();
      IAtsQuery query = atsApi.getQueryService().createQuery(WorkItemType.WorkItem);
      Collection<IAtsTeamDefinition> teams = new LinkedList<>();
      for (Entry<String, List<String>> entry : entrySet) {
         if (entry.getKey().equals("Title")) {
            query.andName(entry.getValue().iterator().next(), QueryOption.CONTAINS_MATCH_OPTIONS);
         } else if (entry.getKey().equals("Priority")) {
            query.andAttr(AtsAttributeTypes.PriorityType, entry.getValue());
         } else if (entry.getKey().equals("ColorTeam")) {
            query.andColorTeam(entry.getValue().iterator().next());
         } else if (entry.getKey().equals("Assignee")) {
            Collection<IAtsUser> assignees = new LinkedList<>();
            for (String userId : entry.getValue()) {
               IAtsUser assignee = atsApi.getUserService().getUserById(userId);
               if (assignee != null) {
                  assignees.add(assignee);
               }
            }
            query.andAssignee(assignees.toArray(new IAtsUser[assignees.size()]));
         } else if (entry.getKey().equals("IPT")) {
            query.andAttr(AtsAttributeTypes.IPT, entry.getValue().iterator().next());
         } else if (entry.getKey().equals("Team")) {
            for (String teamId : entry.getValue()) {
               IAtsTeamDefinition team = atsApi.getQueryService().getConfigItem(Long.valueOf(teamId));
               if (team != null) {
                  teams.add(team);
               }
            }
            query.andTeam(teams);
         } else if (entry.getKey().equals("State")) {
            query.andState(entry.getValue().iterator().next());
         } else if (entry.getKey().equals("StateType")) {
            try {
               List<StateType> stateTypes = new LinkedList<>();
               for (String type : entry.getValue()) {
                  StateType stateType2 = StateType.valueOf(type);
                  stateTypes.add(stateType2);
               }
               query.andStateType(stateTypes.toArray(new StateType[stateTypes.size()]));
            } catch (Exception ex) {
               // do nothing
            }
         } else if (entry.getKey().equals("Originator")) {
            IAtsUser assignee = atsApi.getUserService().getUserById(entry.getValue().iterator().next());
            query.andOriginator(assignee);
         } else if (entry.getKey().equals("WorkItemType")) {
            List<WorkItemType> workItemTypes = new LinkedList<>();
            for (String type : entry.getValue()) {
               WorkItemType workItem = WorkItemType.valueOf(type);
               workItemTypes.add(workItem);
            }
            query.andWorkItemType(workItemTypes.toArray(new WorkItemType[workItemTypes.size()]));
         } else if (entry.getKey().equals("Version")) {
            IAtsVersion version =
               atsApi.getQueryService().getConfigItem(Long.valueOf(entry.getValue().iterator().next()));
            query.andVersion(version);
         }
         // else, attempt to resolve as attribute type id or name
         else {
            String key = entry.getKey();
            AttributeTypeId attrType = null;
            if (Strings.isNumeric(key)) {
               attrType = atsApi.getStoreService().getAttributeType(Long.valueOf(key));
            }
            if (attrType == null) {
               attrType = atsApi.getStoreService().getAttributeType(key);
            }
            query.andAttr(attrType, entry.getValue());
         }
      }
      if (teams.isEmpty()) {
         throw new OseeArgumentException("Team(s) are invalid and must be included.");
      }
      workItems.addAll(query.getItems());
      return workItems;
   }

   @Override
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public NewActionResult createAction(NewActionData newActionData) {
      return createNewAction(newActionData);
   }

   private NewActionResult createNewAction(NewActionData newActionData) {
      NewActionResult result = new NewActionResult();
      try {
         IAtsUser asUser = atsApi.getUserService().getUserById(newActionData.getAsUserId());
         if (asUser == null) {
            result.getResults().errorf("asUser [%s] not valid", newActionData.getAsUserId());
            return result;
         }
         IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet("Create Action - Server", asUser);

         ActionFactory factory = new ActionFactory(atsApi);
         ActionResult actionResult = factory.createAction(newActionData, changes);

         TransactionId transaction = changes.executeIfNeeded();
         if (transaction != null && transaction.isInvalid()) {
            result.getResults().errorf("TransactionId came back as inValid.  Action not created.");
            return result;
         }
         result.setAction(ArtifactId.valueOf(actionResult.getActionArt()));
         for (ArtifactId teamWf : actionResult.getTeamWfArts()) {
            result.addTeamWf(teamWf);
            String ret = teamWf.getIdString();
            if (Strings.isInValid(ret)) {
               return null;
            }
         }
      } catch (Exception ex) {
         result.getResults().errorf("Exception creating action [%s]", Lib.exceptionToString(ex));
      }
      return result;
   }

   @Override
   @Path("createEmpty")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public String createEmptyAction(@QueryParam("userId") String userId, @QueryParam("ai") String actionItem, @QueryParam("title") String title) {
      String newActionId = "";
      NewActionData newActionData = getNewActionData(userId, actionItem, title);
      NewActionResult newAction = createNewAction(newActionData);
      if (newAction == null || newAction.getTeamWfs().isEmpty()) {
         throw new OseeCoreException("Unable to create new Action");
      }
      newActionId = newAction.getTeamWfs().get(0).getIdString();
      return String.format("{ \"id\":\"%s\" }", newActionId);
   }

   private NewActionData getNewActionData(String userId, String actionItem, String title) {
      NewActionData newActionData = new NewActionData();
      List<String> actionIds = new ArrayList<>();
      actionIds.add(actionItem);
      newActionData.setAiIds(actionIds);
      newActionData.setTitle(title);
      newActionData.setAsUserId(userId);
      newActionData.setCreatedByUserId(userId);
      return newActionData;
   }

   /**
    * @param form containing information to create a new action
    * @param form.ats_title - (required) title of new action
    * @param form.desc - description of the action
    * @param form.actionableItems - (required) actionable item name
    * @param form.changeType - (required) Improvement, Refinement, Problem, Support
    * @param form.priority - (required) 1-5
    * @param form.userId - (required)
    * @return html representation of action created
    */
   @Override
   @POST
   @Consumes("application/x-www-form-urlencoded")
   public Response createAction(MultivaluedMap<String, String> form) {
      // validate title
      String title = form.getFirst("ats_title");
      if (!Strings.isValid(title)) {
         return RestUtil.returnBadRequest("title is not valid");
      }

      // description is optional
      String description = form.getFirst("desc");

      // validate actionableItemName
      String actionableItems = form.getFirst("actionableItems");
      if (!Strings.isValid(actionableItems)) {
         return RestUtil.returnBadRequest("actionableItems is not valid");
      }
      List<IAtsActionableItem> aias = new ArrayList<>();
      IAtsActionableItem aia =
         atsApi.getQueryService().createQuery(AtsArtifactTypes.ActionableItem).andName(actionableItems).getOneOrDefault(
            IAtsActionableItem.class, IAtsActionableItem.SENTINEL);
      if (aia.isInvalid()) {
         return RestUtil.returnBadRequest(String.format("actionableItems [%s] is not valid", actionableItems));
      }
      aias.add(aia);

      // validate userId
      String userId = form.getFirst("userId");
      if (!Strings.isValid(userId)) {
         return RestUtil.returnBadRequest("userId is not valid");
      }
      IAtsUser atsUser = atsApi.getUserService().getUserById(userId);
      if (atsUser == null) {
         return RestUtil.returnBadRequest(String.format("userId [%s] is not valid", userId));
      }

      // validate changeType
      String changeTypeStr = form.getFirst("changeType");
      if (!Strings.isValid(changeTypeStr)) {
         return RestUtil.returnBadRequest("changeType is not valid");
      }
      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet("Create Action - Server", atsUser);

      ChangeType changeType = null;
      try {
         changeType = ChangeType.valueOf(changeTypeStr);
      } catch (Exception ex) {
         return RestUtil.returnBadRequest(String.format("changeType [%s] is not valid", changeTypeStr));
      }

      // validate priority
      String priority = form.getFirst("priority");
      if (!Strings.isValid(priority)) {
         return RestUtil.returnBadRequest("priority is not valid");
      } else if (!priority.matches("[0-5]{1}")) {
         return RestUtil.returnBadRequest(String.format("priority [%s] is not valid", priority));
      }

      // create action
      ActionResult action = atsApi.getActionFactory().createAction(atsUser, title, description, changeType, priority,
         false, null, aias, new Date(), atsUser, null, changes);
      changes.execute();

      // Redirect to action ui
      return RestUtil.redirect(action.getTeamWfs(), ATS_UI_ACTION_PREFIX, atsApi);
   }

   @Path("query/legacyId")
   @GET
   @Override
   @Produces({MediaType.APPLICATION_JSON})
   public List<IAtsWorkItem> query(@QueryParam("ids") String idsStr) {
      List<IAtsWorkItem> results = new LinkedList<>();
      List<String> ids = new LinkedList<>();
      for (String id : idsStr.split(",")) {
         id = id.replaceAll("^ ", "");
         id = id.replaceAll(" $", "");
         if (Strings.isValid(id)) {
            ids.add(id);
         }
      }
      Collection<IAtsTeamWorkflow> items =
         atsApi.getQueryService().createQuery(WorkItemType.TeamWorkflow).andLegacyIds(ids).getItems(
            IAtsTeamWorkflow.class);
      results.addAll(items);
      return results;
   }

}
