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
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.jira.JiraByEpicData;
import org.eclipse.osee.ats.api.agile.jira.JiraDiffData;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.Attribute;
import org.eclipse.osee.ats.api.workflow.AttributeKey;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.ats.api.workflow.WorkItemLastMod;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.api.workflow.journal.JournalData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.ats.rest.internal.util.TargetedVersion;
import org.eclipse.osee.ats.rest.internal.workitem.bids.BidsOperations;
import org.eclipse.osee.ats.rest.internal.workitem.journal.JournalOperations;
import org.eclipse.osee.ats.rest.internal.workitem.operations.ActionOperations;
import org.eclipse.osee.ats.rest.internal.workitem.sync.jira.JiraReportDiffOperation;
import org.eclipse.osee.ats.rest.internal.workitem.sync.jira.JiraReportEpicDiffsOperation;
import org.eclipse.osee.ats.rest.internal.workitem.sync.jira.SyncJiraOperation;
import org.eclipse.osee.ats.rest.internal.workitem.sync.jira.SyncTeam;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
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
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public final class AtsActionEndpointImpl implements AtsActionEndpointApi {

   private final OrcsApi orcsApi;
   private static final String ATS_UI_ACTION_PREFIX = "/ui/action/ID";
   private final AtsApi atsApi;

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
      if (atsApi.getWorkItemService().getDefaultToState(teamWf) != null) {
         states.add(atsApi.getWorkItemService().getDefaultToState(teamWf).getName());
      }
      for (StateDefinition state : teamWf.getStateDefinition().getToStates()) {
         if (!states.contains(state.getName())) {
            states.add(state.getName());
         }
      }
      for (StateDefinition state : atsApi.getWorkItemService().getAllToStates(teamWf)) {
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
   public List<String> getRelatedRequirements(ArtifactId workflowId, AttributeTypeToken relatedReqs,
      AttributeTypeToken versionType) {
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
      ActionOperations ops = new ActionOperations(workItem, atsApi, orcsApi);
      return ops.getActionAttributeValues(attributeType, workItem);
   }

   @Override
   public Attribute setActionAttributeByType(String id, String attrTypeIdOrKey, List<String> values) {
      Conditions.assertNotNull(values, "values can not be null");
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItemsByIds(id).iterator().next();
      IAtsChangeSet changes = atsApi.createChangeSet("Set attr by type/key [" + attrTypeIdOrKey + "]");
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
         helper.setTransitionUser(atsApi.getUserService().getCurrentUser());
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
      ActionOperations actionOps = new ActionOperations(workItem, atsApi, orcsApi);
      return actionOps.setActionAttributeByType(id, attrTypeIdOrKey, values);
   }

   @Override
   public Response cancelAction(String id) {
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItem(id);
      if (workItem.isInWork()) {
         Conditions.assertNotNull(workItem, "workItem can not be found");
         ActionOperations ops = new ActionOperations(workItem, atsApi, orcsApi);
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
   public Collection<ArtifactToken> setByArtifactToken(String workItemId, String changeType,
      Collection<ArtifactToken> artifacts) {
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItem(workItemId);
      ActionOperations ops = new ActionOperations(workItem, atsApi, orcsApi);
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
    * @query_string <art type id>=<value>
    * @return json representation of the matching workItem(s)
    */
   @Override
   public Collection<WorkItemLastMod> queryOpenLastMod(UriInfo uriInfo) {
      List<WorkItemLastMod> items = new ArrayList<>();
      MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);
      Set<Entry<String, List<String>>> entrySet = queryParameters.entrySet();
      for (Entry<String, List<String>> entry : entrySet) {
         if (entry.getKey().equals("ArtTypeId")) {
            String artTypeIdStr = entry.getValue().iterator().next();
            Long artTypeId = Long.valueOf(artTypeIdStr);
            ArtifactTypeToken artType = atsApi.tokenService().getArtifactType(artTypeId);
            for (ArtifactToken art : atsApi.getQueryService().createQuery(artType).andAttr(
               AtsAttributeTypes.CurrentStateType, StateType.Working.name(),
               QueryOption.EXACT_MATCH_OPTIONS).getArtifacts()) {
               TransactionId lastModTransId = ((ArtifactReadable) art).getLastModifiedTransaction();
               TransactionReadable tx = orcsApi.getTransactionFactory().getTx(lastModTransId);
               String atsId =
                  atsApi.getAttributeResolver().getSoleAttributeValueAsString(art, AtsAttributeTypes.AtsId, "");
               items.add(new WorkItemLastMod(atsId, art.getIdString(), tx.getDate().getTime()));
            }
         }
      }
      return items;
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
         AtsUser asUser = atsApi.getUserService().getCurrentUser();
         if (asUser == null) {
            result.getResults().errorf("asUser [%s] not valid", newActionData.getAsUserId());
            return result;
         }
         Collection<IAtsActionableItem> ais = new ArrayList<>();
         ActionableItem ai =
            atsApi.getQueryService().getConfigItem(Long.valueOf(newActionData.getAiIds().iterator().next()));
         ais.add(ai);

         IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
         ChangeTypes changeType =
            newActionData.getChangeType().isValid() ? newActionData.getChangeType() : ChangeTypes.Improvement;
         IAtsVersion version =
            atsApi.getVersionService().getVersionById(ArtifactId.valueOf(newActionData.getVersionId()));
         ActionResult actionResult = atsApi.getActionService().createAction(asUser, newActionData.getTitle(),
            newActionData.getDescription(), changeType, newActionData.getPriority(), false, null, ais, new Date(),
            atsApi.getUserService().getCurrentUser(), null, changes);

         IAtsTeamWorkflow teamWf = actionResult.getTeamWfs().iterator().next();
         atsApi.getVersionService().setTargetedVersion(teamWf, version, changes);

         changes.execute();

         BranchId parentBranch = atsApi.getBranchService().getConfiguredBranchForWorkflow(teamWf);
         BranchToken parentBranchToken =
            orcsApi.getQueryFactory().branchQuery().andId(parentBranch).getResultsAsId().getExactlyOne();
         Branch workingBranch = orcsApi.getBranchOps().createWorkingBranch(
            BranchToken.create(teamWf.getAtsId() + " " + newActionData.getTitle()), parentBranchToken,
            teamWf.getArtifactId());
         result.setWorkingBranchId(workingBranch);
      } catch (Exception ex) {
         result.getResults().errorf("Exception creating action [%s]", Lib.exceptionToString(ex));
      }
      return result;

   }

   @Override
   public XResultData commitWorkingBranch(String teamWfId, BranchId destinationBranch) {
      XResultData rd = new XResultData();
      try {
         IAtsTeamWorkflow teamWf =
            atsApi.getQueryService().getTeamWf(atsApi.getQueryService().getArtifactById(teamWfId));
         if (teamWf.isInvalid()) {
            rd.errorf("[%s] is not a valid workflow.", teamWfId);
         }
         AtsUser asUser = atsApi.getUserService().getCurrentUser();
         if (asUser.isInvalid()) {
            rd.errorf("asUser [%s] not valid", asUser.toString());
            return rd;
         }
         atsApi.getBranchService().commitBranch(teamWf, destinationBranch, asUser, rd);
      } catch (Exception ex) {
         rd.errorf("Exception committing working branch [%s]", Lib.exceptionToString(ex));
      }
      return rd;

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

      ChangeTypes changeType = null;
      try {
         changeType = ChangeTypes.valueOf(changeTypeStr);
         if (changeType == ChangeTypes.None) {
            return RestUtil.returnBadRequest(String.format("changeType [%s] is not valid", changeTypeStr));
         }
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
   public JiraByEpicData reportEpicDiffsByEpic(JiraByEpicData data) {
      JiraReportEpicDiffsOperation op = new JiraReportEpicDiffsOperation(data, atsApi);
      op.run();
      return data;
   }

   @Override
   public JiraDiffData reportEpicDiffs(JiraDiffData data) {
      JiraReportDiffOperation op = new JiraReportDiffOperation(data, atsApi, orcsApi);
      op.run();
      return data;
   }

   @Path("journal")
   @POST
   @Override
   @Consumes("application/x-www-form-urlencoded")
   public Response journal(MultivaluedMap<String, String> form) {
      String atsId = form.getFirst("atsid");
      if (Strings.isInValid(atsId)) {
         return Response.notModified(String.format("Inavlid ATS Id [%s]", atsId)).build();
      }
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItemByAtsId(atsId);
      if (workItem == null) {
         return Response.notModified(String.format("Inavlid ATS Id [%s]", atsId)).build();
      }
      String comment = form.getFirst("desc");
      if (Strings.isInValid(comment)) {
         return Response.notModified(String.format("Inavlid Comment [%s]", comment)).build();
      }
      String useraid = form.getFirst("useraid");
      if (Strings.isInValid(useraid)) {
         return Response.notModified(String.format("Inavlid ATS Art Id [%s]", useraid)).build();
      }
      AtsUser user = atsApi.getUserService().getUserById(ArtifactId.valueOf(useraid));
      if (user == null) {
         return Response.notModified(String.format("Inavlid ATS User Art Id [%s]", useraid)).build();
      }
      JournalData jData = new JournalData();
      jData.setAddMsg(comment);
      jData.setResults(new XResultData());
      jData.setUser(user);
      JournalOperations journalOp = new JournalOperations(jData, atsId, atsApi);
      journalOp.addJournal();

      workItem = atsApi.getQueryService().getWorkItem(workItem.getIdString());

      String actionUrl = String.format("ui/action/%s/journal/%s", workItem.getAtsId(), user.getIdString());
      URI uri = UriBuilder.fromUri(actionUrl).build();
      return Response.seeOther(uri).build();
   }

   /**
    * @return html5 journal comments
    */
   @Override
   @Path("{atsId}/journal/text")
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String getJournalText(@PathParam("atsId") String atsId) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAtsId(atsId);
      if (workItem == null) {
         return String.format("Invalid ATS Id [%s]", atsId);
      }
      return atsApi.getAttributeResolver().getSoleAttributeValueAsString(workItem, AtsAttributeTypes.Journal, "");
   }

   @Override
   @Path("{atsId}/journal")
   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public JournalData addJournal(@PathParam("atsId") String atsId, JournalData journalData) {
      JournalOperations journalOp = new JournalOperations(journalData, atsId, atsApi);
      journalOp.addJournal();
      return journalData;
   }

   @Override
   @Path("{atsId}/journal")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public JournalData getJournalData(@PathParam("atsId") String atsId) {
      JournalData journalData = atsApi.getWorkItemService().getJournalData(atsId);
      return journalData;
   }

   @Override
   @Path("{atsId}/bids")
   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public BuildImpactDatas updateBids(@PathParam("atsId") String atsId, BuildImpactDatas bids) {
      BidsOperations ops = new BidsOperations(atsApi, orcsApi);
      return ops.createBids(bids);
   }

   @Override
   @Path("{atsId}/bids")
   @DELETE
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public BuildImpactDatas deleteBids(@PathParam("atsId") String atsId, BuildImpactDatas bids) {
      orcsApi.userService().requireRole(CoreUserGroups.OseeAccessAdmin);
      BidsOperations ops = new BidsOperations(atsApi, orcsApi);
      return ops.deleteBids(bids);
   }

   @Override
   @Path("{atsId}/bids")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public BuildImpactDatas getBids(@PathParam("atsId") String atsId) {
      BidsOperations ops = new BidsOperations(atsApi, orcsApi);
      return ops.getBids(atsId);
   }

}
