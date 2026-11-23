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

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.sysml.SysmlPackage;
import org.eclipse.osee.ats.api.sysml.SysmlTextWriter;
import org.eclipse.osee.ats.api.sysml.WorkflowExportData;
import org.eclipse.osee.ats.api.task.track.TaskTrackingData;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.Points;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.RecentlyVisitedItems;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.SignDateWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workdef.model.web.WorkflowData;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.Attribute;
import org.eclipse.osee.ats.api.workflow.AttributeKey;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.AtsTeamWfEndpointApi;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionDataMulti;
import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.ats.api.workflow.WorkItemLastMod;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.api.workflow.journal.JournalData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.sysml.ConfigExporter;
import org.eclipse.osee.ats.core.sysml.WorkflowExporter;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.ats.rest.internal.config.ConfigJsonWriter;
import org.eclipse.osee.ats.rest.internal.util.TargetedVersion;
import org.eclipse.osee.ats.rest.internal.workitem.bids.BidsOperations;
import org.eclipse.osee.ats.rest.internal.workitem.journal.JournalOperations;
import org.eclipse.osee.ats.rest.internal.workitem.operations.ActionOperations;
import org.eclipse.osee.ats.rest.internal.workitem.operations.ActionQueryOperations;
import org.eclipse.osee.ats.rest.internal.workitem.task.track.TaskTrackingOperation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
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
   public WorkflowData getWorkflowData(ArtifactId workItemId) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemNew(workItemId);
      WorkDefinition workDef = workItem.getWorkDefinition();
      return (new WorkflowDataCreator(workDef, workItem, atsApi, orcsApi)).get();
   }

   @Override
   public XResultData queryIds(AtsSearchData data) {
      ActionQueryOperations ops = new ActionQueryOperations(data, atsApi);
      return ops.getIds();
   }

   @Override
   public List<IAtsWorkItem> query(AtsSearchData data) {
      ActionQueryOperations ops = new ActionQueryOperations(data, atsApi);
      return ops.getWorkItems();
   }

   /**
    * @param ids (artId, atsId) of action to display
    * @return html representation of the action
    */
   @Override
   public List<IAtsWorkItem> getAction(String ids) {
      List<IAtsWorkItem> workItems = atsApi.getWorkItemService().getWorkItemsByIds(ids);
      return workItems;
   }

   /**
    * @param ids (artId, atsId) of action to display
    * @return html representation of the action
    */
   @Override
   public List<IAtsWorkItem> getActionDetails(String ids) {
      List<IAtsWorkItem> workItems = atsApi.getWorkItemService().getWorkItemsByIds(ids);
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
      List<ArtifactToken> artifactsByIds = atsApi.getQueryService().getArtifactsByIds(ids);
      for (ArtifactToken teamWfArt : artifactsByIds) {
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

   @Override
   public Attribute getActionAttributeByType(String id, AttributeTypeToken attributeType) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(id);
      ActionOperations ops = new ActionOperations(workItem, atsApi, orcsApi);
      return ops.getActionAttributeValues(attributeType, workItem);
   }

   @Override
   public Attribute setActionAttributeByType(String id, String attrTypeIdOrKey, List<String> values) {
      Conditions.assertNotNull(values, "values can not be null");
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemsByIds(id).iterator().next();
      ActionOperations actionOps = new ActionOperations(workItem, atsApi, orcsApi);
      return actionOps.setActionAttributeByType(id, attrTypeIdOrKey, values);
   }

   @Override
   public Response cancelAction(String id) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(id);
      if (workItem.isInWork()) {
         Conditions.assertNotNull(workItem, "workItem can not be found");
         ActionOperations ops = new ActionOperations(workItem, atsApi, orcsApi);
         ops.setActionAttributeByType(id, AttributeKey.State.name(), Arrays.asList("Cancelled"));
      }
      String htmlUrl = atsApi.getWorkItemService().getHtmlUrl(workItem, atsApi);
      if (atsApi.isInTest()) {
         return Response.ok().build();
      }
      try {
         return Response.temporaryRedirect(new URI(htmlUrl)).build();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public Collection<ArtifactToken> setByArtifactToken(String workItemId, String changeType,
      Collection<ArtifactToken> artifacts) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(workItemId);
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
      List<IAtsWorkItem> workItems = atsApi.getWorkItemService().getWorkItemsByIds(ids);
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

   @Override
   public int queryOpenWorkItemsCount(ArtifactTypeToken artType, AttributeTypeToken orderBy, String maxTime,
      String nameFilter) {

      Date maxTimeDate;
      try {
         maxTimeDate = DateUtil.getDate("yyyyMMddHHmmss", maxTime);
      } catch (ParseException ex) {
         maxTimeDate = DateUtil.getSentinalDate();
      }
      QueryBuilder builder = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(artType);
      if (!nameFilter.isEmpty()) {
         builder = builder.and(CoreAttributeTypes.Name, nameFilter);
      }
      builder = builder.setMaxTime(maxTimeDate);
      return builder.getCount();
   }

   @Override
   public Collection<WorkItemLastMod> queryOpenWorkItems(ArtifactTypeToken artType, int pageSize, int pageNum,
      AttributeTypeToken orderBy, String orderDirection, String maxTime, String nameFilter) {
      List<WorkItemLastMod> items = new ArrayList<>();
      QueryBuilder builder = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(artType);
      if (!nameFilter.isEmpty()) {
         builder = builder.and(CoreAttributeTypes.Name, nameFilter);
      }
      Date maxTimeDate;
      try {
         maxTimeDate = DateUtil.getDate("yyyyMMddHHmmss", maxTime);
      } catch (ParseException ex) {
         maxTimeDate = DateUtil.getSentinalDate();
      }
      if (pageSize != 0 && pageNum != 0) {
         builder = builder.isOnPage(pageNum, pageSize);
      }
      if (orderBy.isValid()) {
         builder = builder.setOrderByAttribute(orderBy);
         if (orderDirection.startsWith("DESC")) {
            builder = builder.setOrderByAttributeDirection(SortOrder.DESCENDING);
         } else {
            builder = builder.setOrderByAttributeDirection(SortOrder.ASCENDING);
         }
         if (orderBy.isDate() && maxTimeDate.after(DateUtil.getSentinalDate())) {
            builder = builder.setMaxTime(maxTimeDate);
         }
      }

      builder = builder.includeTransactionDetails().follow(AtsRelationTypes.ActionToWorkflow_Action).follow(
         AtsRelationTypes.ActionToWorkflow_TeamWorkflow, AtsArtifactTypes.TeamWorkflow);

      List<ArtifactReadable> asArtifacts = builder.asArtifacts();
      for (ArtifactReadable art : asArtifacts) {
         String atsId = art.getSoleAttributeAsString(AtsAttributeTypes.AtsId);
         String name = art.getSoleAttributeValue(CoreAttributeTypes.Name, "");
         String id = art.getIdString();
         Long lastmod = art.getLatestTxDetails().getTime().getTime();
         List<String> siblings = new ArrayList<>();
         int relatedCount = art.getRelatedCount(AtsRelationTypes.ActionToWorkflow_Action);
         ArtifactReadable parent = ArtifactReadable.SENTINEL;
         if (relatedCount > 0) {
            parent = art.getRelated(AtsRelationTypes.ActionToWorkflow_Action, DeletionFlag.EXCLUDE_DELETED).get(0);
         }
         if (parent.isValid()) {

            siblings.addAll(parent.getRelated(AtsRelationTypes.ActionToWorkflow_TeamWorkflow,
               DeletionFlag.EXCLUDE_DELETED).stream().filter(a -> !a.getArtifactId().equals(art.getArtifactId())).map(
                  a -> a.getSoleAttributeAsString(AtsAttributeTypes.AtsId)).collect(Collectors.toList()));
         }
         Long opened = art.getSoleAttributeValue(AtsAttributeTypes.CreatedDate, DateUtil.getSentinalDate()).getTime();
         Long closed = art.getSoleAttributeValue(AtsAttributeTypes.CompletedDate, DateUtil.getSentinalDate()).getTime();
         if (closed == DateUtil.getSentinalDate().getTime()) {
            closed = 0L;
         }
         items.add(new WorkItemLastMod(atsId, id, lastmod, siblings, opened, closed, name));
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
            query.andStates(entry.getValue());
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
   public NewActionData createAction(NewActionData newActionData) {
      ElapsedTime time =
         new ElapsedTime(getClass().getSimpleName() + " - ActionEndpoint.createAction.TOTAL SERVER", false);
      NewActionData data = atsApi.getActionService().createAction(newActionData);
      newActionData.getDebugRd().log(time.getTimeSpentString(Units.MSEC));
      return data;
   }

   @Override
   public NewActionDataMulti createActions(NewActionDataMulti newActionDatas) {
      NewActionDataMulti datas = atsApi.getActionService().createActions(newActionDatas);
      return datas;
   }

   @Override
   public NewActionResult createActionAndWorkingBranch(NewActionData newActionData) {
      NewActionResult result = atsApi.getActionService().createActionAndWorkingBranch(newActionData);
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

   @Override
   public String createEmptyAction(String userId, String actionableItemId, String title) {
      AtsUser user = atsApi.getUserService().getUserByUserId(userId);
      if (user == null) {
         throw new OseeArgumentException("UserId [%s] is invalid", userId);
      }
      IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItem(actionableItemId);
      if (ai == null) {
         throw new OseeArgumentException("Actionable Item Id [%s] is invalid", actionableItemId);
      }
      if (Strings.isInvalid(title)) {
         throw new OseeArgumentException("Title must be specified");
      }
      NewActionData data =
         atsApi.getActionService().createActionData("Create Empty Action", title, ai.getArtifactToken()).andAsUser(
            user).andCreatedBy(user);
      data = atsApi.getActionService().createAction(data);
      if (data.getRd().isErrors()) {
         throw new OseeCoreException("Error Creating Action: " + data.getRd().toString());
      }
      return String.format("{ \"id\":\"%s\" }", data.getActResult().getTeamWfs().iterator().next().getIdString());
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

      NewActionData data = atsApi.getActionService() //
         .createActionData("Create Action Server", title, description) //
         .andAsUser(atsUser) //
         .andCreatedBy(atsUser) //
         .andAis(aias) //
         .andChangeType(changeType) //
         .andPriority(priority);
      NewActionData newActionData = atsApi.getActionService().createAction(data);
      if (newActionData.getRd().isErrors()) {
         return RestUtil.returnBadRequest(newActionData.toString());
      }
      IAtsTeamWorkflow teamWf = newActionData.getActResult().getAtsTeamWfs().iterator().next();

      // Redirect to action ui
      return RestUtil.redirect(teamWf, ATS_UI_ACTION_PREFIX, atsApi);
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
   public List<ChangeItem> getBranchChangeData(BranchId branchId) {
      BranchToken branch = atsApi.getBranchService().getBranch(branchId);
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

   @Path("journal")
   @POST
   @Override
   @Consumes("application/x-www-form-urlencoded")
   public Response journal(MultivaluedMap<String, String> form) {
      String atsId = form.getFirst("atsid");
      if (Strings.isInValid(atsId)) {
         return Response.notModified(String.format("Inavlid ATS Id [%s]", atsId)).build();
      }
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAtsId(atsId);
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

      workItem = atsApi.getWorkItemService().getWorkItem(workItem.getIdString());

      if (atsApi.isInTest()) {
         return Response.ok().build();
      }
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
   public BuildImpactDatas updateBids(@PathParam("atsId") ArtifactId pwTwId, BuildImpactDatas bids) {
      BidsOperations ops = new BidsOperations(atsApi, orcsApi);
      return ops.createBids(bids);
   }

   @Override
   @Path("{atsId}/bids")
   @DELETE
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public BuildImpactDatas deleteBids(@PathParam("atsId") ArtifactId prTwId, BuildImpactDatas bids) {
      BidsOperations ops = new BidsOperations(atsApi, orcsApi);
      return ops.deleteBids(bids);
   }

   @Override
   @Path("{prTwId}/bids")
   @GET
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public BuildImpactDatas getBidsById(@PathParam("prTwId") ArtifactId prTwId) {
      BidsOperations ops = new BidsOperations(atsApi, orcsApi);
      return ops.getBids(prTwId);
   }

   @Override
   @Path("{id}/bidParents")
   @GET
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public BuildImpactDatas getBidParents(@PathParam("id") ArtifactId twId) {
      BidsOperations ops = new BidsOperations(atsApi, orcsApi);
      return ops.getBidParents(twId);
   }

   @Override
   public Collection<String> getPointValues() {
      return Points.getValuesStr();
   }

   @Override
   public boolean checkApproval(String atsId) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(atsId);
      for (LayoutItem layoutItem : workItem.getStateDefinition().getLayoutItems()) {
         if (layoutItem instanceof SignDateWidgetDef) {
            SignDateWidgetDef widgetDef = (SignDateWidgetDef) layoutItem;
            AttributeTypeToken userAttrType = widgetDef.getAttributeType();
            AttributeTypeToken dateAttrType = widgetDef.getAttributeType2();
            Object userVal = atsApi.getAttributeResolver().getSoleAttributeValue(workItem, userAttrType, null);
            Object dateVal = atsApi.getAttributeResolver().getSoleAttributeValue(workItem, dateAttrType, null);
            if (userVal == null || dateVal == null) {
               return false;
            }
            break;
         }
      }
      return true;
   }

   @Override
   public boolean setApproval(String atsId) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(atsId);
      Date resultDate = new Date(System.currentTimeMillis());
      UserId account = orcsApi.userService().getUser();
      List<LayoutItem> layoutItems = workItem.getStateDefinition().getLayoutItems();
      IAtsChangeSet changes = null;
      for (LayoutItem layoutItem : layoutItems) {
         if (layoutItem instanceof SignDateWidgetDef) {
            SignDateWidgetDef widgetDef = (SignDateWidgetDef) layoutItem;
            AttributeTypeToken userAttrType = widgetDef.getAttributeType();
            AttributeTypeToken dateAttrType = widgetDef.getAttributeType2();
            if (changes == null) {
               changes = atsApi.createChangeSet("Set Approval");
            }
            changes.setSoleAttributeValue(workItem, dateAttrType, resultDate);
            changes.setSoleAttributeValue(workItem, userAttrType, account.getIdString());
         }
      }
      if (changes != null) {
         TransactionToken tx = changes.execute();
         return tx.isValid();
      }
      return false;
   }

   @Override
   public TaskTrackingData createUpdateTaskTrack(TaskTrackingData taskTrackingData) {
      TaskTrackingOperation op = new TaskTrackingOperation(taskTrackingData, atsApi);
      return op.run();
   }

   @Path("visited/{userArtId}")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Override
   public void storeVisited(@PathParam("userArtId") ArtifactId userArtId, RecentlyVisitedItems visitedItems) {
      Conditions.assertNotSentinel(userArtId);
      String toStoreJson = atsApi.jaxRsApi().toJson(visitedItems);
      Long storeId =
         atsApi.getAttributeResolver().getSoleAttributeValue(userArtId, CoreAttributeTypes.RecentlyVisitedItemsKey, 0L);
      if (storeId == 0) {
         storeId = Lib.generateId();
         IAtsChangeSet changes = atsApi.createChangeSet("Set Recently Visited Key");
         changes.setSoleAttributeValue(userArtId, CoreAttributeTypes.RecentlyVisitedItemsKey, storeId);
         changes.execute();
         orcsApi.keyValueSvc().putByKey(storeId, toStoreJson);
      } else {
         orcsApi.keyValueSvc().updateByKey(storeId, toStoreJson);
      }
   }

   @Path("visited/{userArtId}")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Override
   public RecentlyVisitedItems getVisited(@PathParam("userArtId") ArtifactId userArtId) {
      RecentlyVisitedItems visitedItems = RecentlyVisitedItems.EMPTY_ITEMS;
      if (userArtId.isValid()) {
         Long storeId = atsApi.getAttributeResolver().getSoleAttributeValue(userArtId,
            CoreAttributeTypes.RecentlyVisitedItemsKey, 0L);
         if (storeId > 0) {
            try {
               String visitedItemsJson = orcsApi.keyValueSvc().getByKey(storeId);
               if (Strings.isValid(visitedItemsJson)) {
                  visitedItems = atsApi.jaxRsApi().readValue(visitedItemsJson, RecentlyVisitedItems.class);
               }
            } catch (Exception ex) {
               // do nothing
            }
         }
      }
      return visitedItems;
   }

   @Override
   public WorkflowExportData exportSysml(WorkflowExportData exportData) {
      XResultData rd = exportData.getResults();
      if (exportData.getProgramId() == null || exportData.getProgramId().isInvalid()) {
         rd.errorf("Program id is required for SysML export");
         return exportData;
      }
      SysmlTextWriter writer = new SysmlTextWriter();

      // Config is always the full configuration for the given program
      try {
         rd.logf("Exporting config for program [%s]\n", exportData.getProgramId().getIdString());
         SysmlPackage configPkg = new ConfigExporter(atsApi).export(exportData);
         exportData.setConfigSysml(writer.write(configPkg));
      } catch (Exception ex) {
         rd.errorf("Error exporting SysML config for program [%s]: %s", exportData.getProgramId().getIdString(),
            Lib.exceptionToString(ex));
      }

      // Workflows are filtered by the provided team def / artifact type / version options
      try {
         SysmlPackage workflowPkg = new WorkflowExporter(atsApi).export(exportData);
         exportData.setWorkflowSysml(writer.write(workflowPkg));
      } catch (Exception ex) {
         rd.errorf("Error exporting SysML workflows for program [%s]: %s", exportData.getProgramId().getIdString(),
            Lib.exceptionToString(ex));
      }

      return exportData;
   }

   @Override
   public WorkflowExportData getConfigJson(WorkflowExportData exportData) {
      XResultData rd = exportData.getResults();
      if (!hasProgram(exportData, "JSON configuration export")) {
         return exportData;
      }
      try {
         IAtsProgram program = atsApi.getProgramService().getProgramById(exportData.getProgramId());
         if (program == null) {
            throw new OseeArgumentException("Program not found for id [%s]", exportData.getProgramId());
         }
         StringWriter output = new StringWriter();
         com.fasterxml.jackson.core.JsonGenerator writer = JsonUtil.getFactory().createGenerator(output);
         writer.writeStartArray();
         addConfigJson(writer, program);
         for (IAtsTeamDefinition teamDef : atsApi.getProgramService().getTeamDefs(program)) {
            addConfigJson(writer, teamDef);
            Set<Long> emittedAiIds = new HashSet<>();
            for (IAtsActionableItem ai : teamDef.getActionableItems()) {
               addActionableItemJson(writer, ai, emittedAiIds);
            }
            for (IAtsVersion version : atsApi.getTeamDefinitionService().getVersions(teamDef)) {
               addConfigJson(writer, version);
            }
            for (AtsUser lead : atsApi.getTeamDefinitionService().getLeads(teamDef)) {
               addConfigJson(writer, lead);
            }
            for (AtsUser member : atsApi.getTeamDefinitionService().getMembers(teamDef)) {
               addConfigJson(writer, member);
            }
         }
         writer.writeEndArray();
         writer.close();
         exportData.setConfigJson(output.toString());
         rd.logf("Exported JSON configuration for program [%s]\n", exportData.getProgramId().getIdString());
      } catch (Exception ex) {
         rd.errorf("Error exporting JSON config for program [%s]: %s", exportData.getProgramId().getIdString(),
            Lib.exceptionToString(ex));
      }
      return exportData;
   }

   @Override
   public WorkflowExportData getWorkflowJson(WorkflowExportData exportData) {
      XResultData rd = exportData.getResults();
      if (!hasProgram(exportData, "JSON workflow export")) {
         return exportData;
      }
      try {
         List<IAtsTeamWorkflow> teamWfs = queryTeamWorkflows(exportData, rd);
         List<String> workflowJson = new ArrayList<>();
         for (IAtsTeamWorkflow teamWf : teamWfs) {
            workflowJson.add(toWorkflowJson(teamWf));
         }
         exportData.setWorkflowJson(workflowJson);
         rd.logf("Exported %d workflow JSON file(s)\n", workflowJson.size());
      } catch (Exception ex) {
         rd.errorf("Error exporting JSON workflows for program [%s]: %s", exportData.getProgramId().getIdString(),
            Lib.exceptionToString(ex));
      }
      return exportData;
   }

   private boolean hasProgram(WorkflowExportData exportData, String exportName) {
      if (exportData.getProgramId() == null || exportData.getProgramId().isInvalid()) {
         exportData.getResults().errorf("Program id is required for %s", exportName);
         return false;
      }
      return true;
   }

   private void addConfigJson(com.fasterxml.jackson.core.JsonGenerator writer, IAtsObject config) throws IOException {
      ConfigJsonWriter.addConfigObject(atsApi, orcsApi, config, new java.lang.annotation.Annotation[0], writer, false);
   }

   private void addActionableItemJson(com.fasterxml.jackson.core.JsonGenerator writer, IAtsActionableItem ai,
      Set<Long> emittedAiIds) throws IOException {
      if (!emittedAiIds.add(ai.getId())) {
         return;
      }
      addConfigJson(writer, ai);
      for (IAtsActionableItem child : ai.getChildrenActionableItems()) {
         addActionableItemJson(writer, child, emittedAiIds);
      }
   }

   private String toWorkflowJson(IAtsTeamWorkflow teamWf) throws IOException {
      StringWriter output = new StringWriter();
      com.fasterxml.jackson.core.JsonGenerator writer = JsonUtil.getFactory().createGenerator(output);
      try {
         java.lang.annotation.Annotation[] annotations = AtsTeamWfEndpointApi.class.getMethod(
            "getTeamWorkflowDetails", ArtifactId.class).getAnnotations();
         WorkItemJsonWriter.addWorkItem(atsApi, orcsApi, teamWf, annotations, writer, false,
            java.util.Collections.emptyList());
      } catch (NoSuchMethodException ex) {
         throw new IOException("Unable to load TeamWorkflowDetails annotation", ex);
      }
      writer.close();
      return output.toString();
   }

   private List<IAtsTeamWorkflow> queryTeamWorkflows(WorkflowExportData exportData, XResultData rd) {
      IAtsQuery query = atsApi.getQueryService().createQuery(WorkItemType.TeamWorkflow);
      IAtsProgram program = atsApi.getProgramService().getProgramById(exportData.getProgramId());
      if (program == null) {
         throw new OseeArgumentException("Program not found for id [%s]", exportData.getProgramId());
      }
      if (exportData.isTeamDefIdValid()) {
         IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(exportData.getTeamDefId());
         if (teamDef == null) {
            rd.errorf("Team Definition not found for id [%s]", exportData.getTeamDefId());
            return java.util.Collections.emptyList();
         }
         query = query.andTeam(teamDef);
      } else {
         query = query.andTeam(atsApi.getProgramService().getTeamDefs(program));
      }
      if (exportData.isArtifactTypeValid()) {
         query = query.andArtType(exportData.getArtifactType());
      }
      if (exportData.isVersionIdValid()) {
         query = query.andVersion(exportData.getVersionId().getId());
      }
      List<IAtsTeamWorkflow> teamWfs = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
         query.getResultsNew().getList());
      rd.logf("JSON workflow query returned %d Team Workflow(s)\n", teamWfs.size());
      return teamWfs;
   }

}
