/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.jira.JiraByEpicData;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
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
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.ats.rest.internal.util.TargetedVersion;
import org.eclipse.osee.ats.rest.internal.workitem.operations.ActionOperations;
import org.eclipse.osee.ats.rest.internal.workitem.sync.jira.JiraReportEpicDiffsOperation;
import org.eclipse.osee.ats.rest.internal.workitem.sync.jira.SyncJiraOperation;
import org.eclipse.osee.ats.rest.internal.workitem.sync.jira.SyncTeam;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public final class AtsActionEndpointImpl implements AtsActionEndpointApi {

   private final OrcsApi orcsApi;
   private static final String ATS_UI_ACTION_PREFIX = "/ui/action/ID";
   private final AtsApi atsApi;

   @HeaderParam(OseeClient.OSEE_ACCOUNT_ID)
   private UserId accountId;

   public AtsActionEndpointImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   @Override
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
   public List<IAtsWorkItem> getAction(String ids) {
      List<IAtsWorkItem> workItems = atsApi.getQueryService().getWorkItemsByIds(ids);
      return workItems;
   }

   /**
    * @param ids (artId, atsId) of action to display
    * @return html representation of the action
    */
   @Override
   public List<IAtsWorkItem> getActionDetails(String ids) {
      List<IAtsWorkItem> workItems = atsApi.getQueryService().getWorkItemsByIds(ids);
      return workItems;
   }

   /**
    * @param ids (artId, atsId) of action to display
    * @return html representation of the action
    */
   @Override
   @TargetedVersion
   public List<IAtsWorkItem> getActionChildren(String ids) {
      List<IAtsWorkItem> children = new LinkedList<>();
      for (ArtifactToken action : atsApi.getQueryService().getArtifactsByIds(ids)) {
         for (ArtifactToken childWf : atsApi.getRelationResolver().getRelated(action,
            AtsRelationTypes.ActionToWorkflow_TeamWorkflow)) {
            IAtsWorkItem child = atsApi.getWorkItemService().getWorkItem(childWf);
            if (child != null) {
               children.add(child);
            }
         }
      }
      return children;
   }

   @Override
   @TargetedVersion
   public List<IAtsWorkItem> getSiblings(String ids) {
      List<IAtsWorkItem> siblings = new LinkedList<>();
      for (ArtifactToken teamWfArt : atsApi.getQueryService().getArtifactsByIds(ids)) {
         IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(teamWfArt);
         if (teamWf != null) {
            for (IAtsTeamWorkflow sibTeamWf : atsApi.getWorkItemService().getSiblings(teamWf)) {
               siblings.add(sibTeamWf);
            }
         }
      }
      return siblings;
   }

   /**
    * @return valid unreleased versions to select
    */
   @Override
   public List<String> getUnreleasedVersionNames(String id) {
      List<String> versions = new LinkedList<>();
      IAtsTeamWorkflow teamWf = atsApi.getQueryService().getTeamWf(atsApi.getQueryService().getArtifactById(id));
      IAtsTeamDefinition targedVersionsTeamDef =
         atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamWf.getTeamDefinition());
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
   public List<String> getTransitionToStateNames(String id) {
      List<String> states = new LinkedList<>();
      IAtsTeamWorkflow teamWf = atsApi.getQueryService().getTeamWf(atsApi.getQueryService().getArtifactById(id));
      states.add(atsApi.getWorkItemService().getDefaultToState(teamWf).getName());
      for (IAtsStateDefinition state : teamWf.getStateDefinition().getToStates()) {
         if (!states.contains(state.getName())) {
            states.add(state.getName());
         }
      }
      for (IAtsStateDefinition state : atsApi.getWorkItemService().getAllToStates(teamWf)) {
         if (!states.contains(state.getName())) {
            states.add(state.getName());
         }
      }
      return states;
   }

   /**
    * @return list of json objects containing artifact ids and names for a related set of requirements
    */
   @Override
   public List<String> getRelatedRequirements(ArtifactId workflowId, AttributeTypeToken relatedReqs, AttributeTypeToken versionType) {
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
   public Attribute getActionAttributeByType(String id, AttributeTypeToken attributeType) {
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItem(id);
      ActionOperations ops = new ActionOperations(null, workItem, atsApi, orcsApi);
      return ops.getActionAttributeValues(attributeType, workItem);
   }

   @Override
   public Attribute setActionAttributeByType(String id, String attrTypeIdOrKey, List<String> values) {
      Conditions.assertNotNull(values, "values can not be null");
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItemsByIds(id).iterator().next();
      IAtsChangeSet changes = atsApi.createChangeSet("Set attr by type/key [" + attrTypeIdOrKey + "]");
      AtsUser asUser = atsApi.getUserService().getUserByAccountId(accountId);
      AttributeTypeToken attrTypeId = null;
      if (attrTypeIdOrKey.equals(AttributeKey.Title.name())) {
         changes.setSoleAttributeValue(workItem, CoreAttributeTypes.Name, values.iterator().next());
         attrTypeId = CoreAttributeTypes.Name;
      } else if (attrTypeIdOrKey.equals(AttributeKey.Priority.name())) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.Priority, values.iterator().next());
         attrTypeId = AtsAttributeTypes.Priority;
      } else if (attrTypeIdOrKey.equals(AttributeKey.State.name())) {
         String state = values.iterator().next();
         TransitionHelper helper = new TransitionHelper("Transition Workflow", Arrays.asList(workItem), state,
            new ArrayList<AtsUser>(), "", changes, atsApi, TransitionOption.OverrideAssigneeCheck);
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
               IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(
                  workItem.getParentTeamWorkflow().getTeamDefinition());
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
               IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(
                  workItem.getParentTeamWorkflow().getTeamDefinition());
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
            AtsUser originator = atsApi.getUserService().getUserById(ArtifactId.valueOf(accountId));
            if (originator == null) {
               throw new OseeArgumentException("No user with account id [%s]", accountId);
            }
            atsApi.getActionService().setCreatedBy(workItem, originator, true, workItem.getCreatedDate(), changes);
         }
      } else if (attrTypeIdOrKey.equals(AttributeKey.Assignee.name())) {
         List<AtsUser> assignees = new LinkedList<>();
         for (String accountIdOrName : values) {
            if (Strings.isNumeric(accountIdOrName)) {
               AtsUser assignee = atsApi.getUserService().getUserById(ArtifactId.valueOf(accountIdOrName));
               if (assignee == null) {
                  throw new OseeArgumentException("No user with account id [%s]", accountIdOrName);
               } else {
                  assignees.add(assignee);
               }
            } else {
               AtsUser assignee = atsApi.getUserService().getUserByName(accountIdOrName);
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
         attrTypeId = atsApi.tokenService().getAttributeType(Long.valueOf(attrTypeIdOrKey));
         if (attrTypeId != null) {
            changes.setAttributeValuesAsStrings(workItem, attrTypeId, values);
         }
      }
      ActionOperations actionOps = new ActionOperations(asUser, workItem, atsApi, orcsApi);
      return actionOps.setActionAttributeByType(id, attrTypeIdOrKey, values);
   }

   @Override
   public Response cancelAction(String id) {
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItem(id);
      if (workItem.isInWork()) {
         Conditions.assertNotNull(workItem, "workItem can not be found");
         AtsUser asUser = atsApi.getUserService().getUserByAccountId(accountId);
         ActionOperations ops = new ActionOperations(asUser, workItem, atsApi, orcsApi);
         ops.setActionAttributeByType(id, AttributeKey.State.name(), Arrays.asList("Cancelled"));
      }
      String htmlUrl = atsApi.getWorkItemService().getHtmlUrl(workItem, atsApi);
      try {
         return Response.temporaryRedirect(new URI(htmlUrl)).build();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public Collection<ArtifactToken> setByArtifactToken(String workItemId, String changeType, Collection<ArtifactToken> artifacts) {
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItem(workItemId);
      AtsUser asUser = atsApi.getUserService().getUserByAccountId(accountId);
      ActionOperations ops = new ActionOperations(asUser, workItem, atsApi, orcsApi);
      return ops.setByArtifactToken(workItem, changeType, artifacts);

   }

   /**
    * @query_string <attr type name>=<value>, <attr type id>=<value>
    * @return json representation of the matching workItem(s)
    */
   @Override
   public String getActionStateFromLegacyPcrId(String ids) {
      List<IAtsWorkItem> workItems = new ArrayList<>();
      for (String id : atsApi.getQueryService().getIdsFromStr(ids)) {
         ArtifactToken action = atsApi.getQueryService().getArtifactByLegacyPcrId(id);
         if (action != null) {
            IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(action);
            workItems.add(workItem);
         }
      }
      return atsApi.getActionService().getActionStateJson(workItems);
   }

   @Override
   public String getActionState(String ids) {
      List<IAtsWorkItem> workItems = atsApi.getQueryService().getWorkItemsByIds(ids);
      return atsApi.getActionService().getActionStateJson(workItems);
   }

   /**
    * @query_string <attr type name>=<value>, <attr type id>=<value>
    * @return json representation of the matching workItem(s)
    */
   @Override
   public Set<IAtsWorkItem> query(UriInfo uriInfo) {
      Set<IAtsWorkItem> workItems = new HashSet<>();
      MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);
      Set<Entry<String, List<String>>> entrySet = queryParameters.entrySet();
      IAtsQuery query = atsApi.getQueryService().createQuery(WorkItemType.WorkItem);
      Collection<IAtsTeamDefinition> teams = new LinkedList<>();
      for (Entry<String, List<String>> entry : entrySet) {
         if (entry.getKey().equals("Title")) {
            query.andName(entry.getValue().iterator().next(), QueryOption.CONTAINS_MATCH_OPTIONS);
         } else if (entry.getKey().equals("Priority")) {
            query.andAttr(AtsAttributeTypes.Priority, entry.getValue());
         } else if (entry.getKey().equals("Assignee")) {
            Collection<AtsUser> assignees = new LinkedList<>();
            for (String userId : entry.getValue()) {
               AtsUser assignee = atsApi.getUserService().getUserByUserId(userId);
               if (assignee != null) {
                  assignees.add(assignee);
               }
            }
            query.andAssignee(assignees.toArray(new AtsUser[assignees.size()]));
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
                  stateTypes.add(StateType.valueOf(type));
               }
               query.andStateType(stateTypes.toArray(new StateType[stateTypes.size()]));
            } catch (Exception ex) {
               // do nothing
            }
         } else if (entry.getKey().equals("Originator")) {
            AtsUser assignee = atsApi.getUserService().getUserByUserId(entry.getValue().iterator().next());
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
            AttributeTypeToken attrType = null;
            if (Strings.isNumeric(key)) {
               attrType = atsApi.tokenService().getAttributeType(Long.valueOf(key));
            }
            if (attrType == null) {
               attrType = atsApi.tokenService().getAttributeType(key);
            }
            if (attrType != null) {
               query.andAttr(attrType, entry.getValue());
            }
         }
      }
      if (teams.isEmpty()) {
         throw new OseeArgumentException("Team(s) are invalid and must be included.");
      }
      workItems.addAll(query.getItems());
      return workItems;
   }

   @Override
   public NewActionResult createAction(NewActionData newActionData) {
      return createNewAction(newActionData);
   }

   @Override
   public NewActionResult createActionAndWorkingBranch(NewActionData newActionData) {

      NewActionResult result = new NewActionResult();
      try {

         AtsUser asUser = atsApi.getUserService().getUserByUserId(newActionData.getAsUserId());
         if (asUser == null) {
            result.getResults().errorf("asUser [%s] not valid", newActionData.getAsUserId());
            return result;
         }
         Collection<IAtsActionableItem> ais = new ArrayList<>();
         ActionableItem ai =
            atsApi.getQueryService().getConfigItem(Long.valueOf(newActionData.getAiIds().iterator().next()));
         ais.add(ai);

         IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
         IAtsVersion version =
            atsApi.getVersionService().getVersionById(ArtifactId.valueOf(newActionData.getVersionId()));
         ActionResult actionResult = atsApi.getActionService().createAction(asUser, newActionData.getTitle(),
            newActionData.getDescription(), ChangeType.Improvement, newActionData.getPriority(), false, null, ais,
            new Date(), atsApi.getUserService().getCurrentUser(), null, changes);

         IAtsTeamWorkflow teamWf = actionResult.getTeamWfs().iterator().next();
         atsApi.getVersionService().setTargetedVersion(teamWf, version, changes);

         changes.execute();

         BranchId parentBranch = atsApi.getBranchService().getConfiguredBranchForWorkflow(teamWf);
         BranchToken parentBranchToken =
            orcsApi.getQueryFactory().branchQuery().andId(parentBranch).getResultsAsId().getExactlyOne();
         Branch workingBranch = orcsApi.getBranchOps().createWorkingBranch(
            BranchToken.create(teamWf.getAtsId() + " " + newActionData.getTitle()), asUser.getArtifactId(),
            parentBranchToken, teamWf.getArtifactId());
         result.setWorkingBranchId(workingBranch);
      } catch (Exception ex) {
         result.getResults().errorf("Exception creating action [%s]", Lib.exceptionToString(ex));
      }
      return result;

   }

   private NewActionResult createNewAction(NewActionData newActionData) {
      NewActionResult result = new NewActionResult();
      try {
         AtsUser asUser = atsApi.getUserService().getUserByUserId(newActionData.getAsUserId());
         if (asUser == null) {
            result.getResults().errorf("asUser [%s] not valid", newActionData.getAsUserId());
            return result;
         }
         IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet("Create Action - Server", asUser);

         ActionResult actionResult = atsApi.getActionService().createAction(newActionData, changes);

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
   public String createEmptyAction(String userId, String actionItem, String title) {
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
      AtsUser atsUser = atsApi.getUserService().getUserByUserId(userId);
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
      ActionResult action = atsApi.getActionService().createAction(atsUser, title, description, changeType, priority,
         false, null, aias, new Date(), atsUser, null, changes);
      changes.execute();

      // Redirect to action ui
      return RestUtil.redirect(action.getTeamWfs(), ATS_UI_ACTION_PREFIX, atsApi);
   }

   @Override
   public List<IAtsWorkItem> query(String idsStr) {
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

   @Override
   public List<ChangeItem> getBranchChangeData(BranchId branch) {
      return atsApi.getBranchService().getChangeData(branch);
   }

   @Override
   public List<ChangeItem> getTransactionChangeData(TransactionId transactionId) {
      return atsApi.getBranchService().getChangeData(transactionId);
   }

   @Override
   public TransitionResults transition(TransitionData transData) {
      if (transData.getTransitionUser() == null && transData.getTransitionUserArtId().isValid()) {
         transData.setTransitionUser(atsApi.getUserService().getUserById(transData.getTransitionUserArtId()));
      }
      TransitionResults results = atsApi.getWorkItemService().transition(transData);
      return results;
   }

   @Override
   public TransitionResults transitionValidate(TransitionData transData) {
      if (transData.getTransitionUser() == null && transData.getTransitionUserArtId().isValid()) {
         transData.setTransitionUser(atsApi.getUserService().getUserById(transData.getTransitionUserArtId()));
      }
      TransitionResults results = atsApi.getWorkItemService().transitionValidate(transData);
      return results;
   }

   @Override
   public XResultData syncJira() {
      SyncJiraOperation op = new SyncJiraOperation(atsApi, new SyncTeam(), true);
      XResultData results = op.run();
      return results;
   }

   @Override
   public XResultData syncJiraAndPersist() {
      SyncJiraOperation op = new SyncJiraOperation(atsApi, new SyncTeam(), false);
      XResultData results = op.run();
      return results;
   }

   @Override
   public JiraByEpicData reportEpicDiffs(JiraByEpicData data) {
      JiraReportEpicDiffsOperation op = new JiraReportEpicDiffsOperation(data, atsApi);
      op.run();
      return data;
   }

}
