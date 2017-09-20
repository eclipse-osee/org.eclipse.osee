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

import java.io.IOException;
import java.io.StringWriter;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
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
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.mvc.IdentityView;

/**
 * @author Donald G. Dunne
 */
@Path("action")
public final class AtsActionEndpointImpl implements AtsActionEndpointApi {

   private final IAtsServices services;
   private static final String ATS_UI_ACTION_PREFIX = "/ui/action/UUID";

   @Context
   private HttpHeaders httpHeaders;
   private final JsonFactory jsonFactory;

   public AtsActionEndpointImpl(IAtsServices services, JsonFactory jsonFactory) {
      this.services = services;
      this.jsonFactory = jsonFactory;
   }

   @Override
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() throws Exception {
      return RestUtil.simplePageHtml("Action Resource");
   }

   /**
    * @param ids (guid, atsId, legacy pcr id) of action to display
    * @return html representation of the action
    */
   @Override
   @Path("{ids}")
   @IdentityView
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public List<IAtsWorkItem> getAction(@PathParam("ids") String ids) throws Exception {
      List<IAtsWorkItem> workItems = services.getQueryService().getWorkItemListByIds(ids);
      return workItems;
   }

   /**
    * @param ids (guid, atsId) of action to display
    * @return html representation of the action
    */
   @Override
   @Path("{ids}/details")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public List<IAtsWorkItem> getActionDetails(@PathParam("ids") String ids) throws Exception {
      List<IAtsWorkItem> workItems = services.getQueryService().getWorkItemListByIds(ids);
      return workItems;
   }

   /**
    * @return valid unreleased versions to select
    */
   @Override
   @GET
   @Path("{id}/UnrelasedVersions")
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getUnreleasedVersionNames(@PathParam("id") String id) {
      List<String> versions = new LinkedList<>();
      IAtsTeamWorkflow teamWf = services.getTeamWf(services.getArtifactById(id));
      IAtsTeamDefinition targedVersionsTeamDef = teamWf.getTeamDefinition().getTeamDefinitionHoldingVersions();
      if (targedVersionsTeamDef != null) {
         for (IAtsVersion version : services.getVersionService().getVersions(targedVersionsTeamDef)) {
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
      IAtsTeamWorkflow teamWf = services.getTeamWf(services.getArtifactById(id));
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
      for (IAtsVersion version : services.getVersionService().getVersions(teamWf.getTeamDefinition())) {
         if (!version.isReleased()) {
            states.add(version.getName());
         }
      }
      return states;
   }

   /**
    * @param ids (guid, atsId, long) of workItem
    * @return html representation of the action
    */
   @Override
   @Path("{actionId}/attributeType/{attrTypeId}")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public Attribute getActionAttributeByType(@PathParam("actionId") String actionId, @PathParam("attrTypeId") String attrTypeId) {
      IAtsWorkItem workItem = services.getWorkItemService().getWorkItemByAnyId(actionId);
      Attribute attribute = getActionAttributeValues(attrTypeId, workItem);
      return attribute;
   }

   private Attribute getActionAttributeValues(String attrTypeId, IAtsWorkItem workItem) {
      AttributeTypeId attrType = services.getStoreService().getAttributeType(Long.valueOf(attrTypeId));
      return getActionAttributeValues(attrType, workItem);
   }

   private Attribute getActionAttributeValues(AttributeTypeId attrType, IAtsWorkItem workItem) {
      Attribute attribute = new Attribute();
      attribute.setArtId(workItem.getStoreObject());
      attribute.setAttrTypeId(attrType);
      for (IAttribute<?> attr : services.getAttributeResolver().getAttributes(workItem, attrType)) {
         attribute.addAttribute(attr);
      }
      return attribute;
   }

   /**
    * @param actionId (guid, atsId, long) of workItem
    * @param attrTypeId can be the id of the attrType or one of (Title, Priority, ColorTeam, Assignee, IPT, Originator,
    * Version, State). If State is sent in, it will result in the "transition" of the workflow.
    * @return html representation of the action
    */
   @Override
   @Path("{actionId}/attributeType/{attrTypeIdOrKey}")
   @PUT
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public Attribute setActionAttributeByType(@PathParam("actionId") String actionId, @PathParam("attrTypeIdOrKey") String attrTypeIdOrKey, List<String> values) {
      Conditions.assertNotNullOrEmpty(values, "values can not be null or empty");
      IAtsWorkItem workItem = services.getWorkItemService().getWorkItemByAnyId(actionId);
      IAtsChangeSet changes = services.createChangeSet("set attr by type or key " + attrTypeIdOrKey);
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
            new ArrayList<IAtsUser>(), "", changes, services, TransitionOption.OverrideAssigneeCheck);
         IAtsUser asUser = services.getUserService().getUserByAccountId(httpHeaders);
         if (asUser == null) {
            asUser = AtsCoreUsers.SYSTEM_USER;
         }
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
         String version = values.iterator().next();
         if (Strings.isValid(version)) {
            IAtsVersion currVersion = services.getVersionService().getTargetedVersion(workItem);
            if (!currVersion.getName().equals(version)) {
               IAtsVersion newVer = null;
               IAtsTeamDefinition teamDef = workItem.getParentTeamWorkflow().getTeamDefinition();
               for (IAtsVersion teamDefVer : services.getVersionService().getVersions(teamDef)) {
                  if (teamDefVer.getName().equals(version)) {
                     newVer = teamDefVer;
                     break;
                  }
               }
               if (newVer == null) {
                  throw new OseeArgumentException("Version [%s] not valid for team ", version,
                     teamDef.toStringWithId());
               }
               services.getVersionService().setTargetedVersion(workItem.getParentTeamWorkflow(), newVer, changes);
            }
         }
      } else if (attrTypeIdOrKey.equals(AttributeKey.Originator.name())) {
         String accountId = values.iterator().next();
         if (!Strings.isNumeric(accountId)) {
            IAtsUser originator = services.getUserService().getUserByAccountId(Long.valueOf(accountId));
            if (originator == null) {
               throw new OseeArgumentException("No user with account id [%s]", accountId);
            }
            services.getActionFactory().setCreatedBy(workItem, originator, true, workItem.getCreatedDate(), changes);
         }
      } else {
         attrTypeId = services.getStoreService().getAttributeType(Long.valueOf(attrTypeIdOrKey));
         if (attrTypeId != null) {
            changes.setAttributeValuesAsStrings(workItem, attrTypeId, values);
         }
      }
      changes.executeIfNeeded();

      // reload to get latest
      workItem = services.getWorkItemService().getWorkItemByAnyId(actionId);
      if (attrTypeId != null) {
         return getActionAttributeValues(attrTypeId, workItem);
      }
      return null;
   }

   /**
    * @query_string <attr type name>=<value>, <attr type id>=<value>
    * @return json representation of the matching workItem(s)
    */
   @Override
   @Path("{ids}/legacy/state")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public String getActionStateFromLegacyPcrId(@PathParam("ids") String ids) throws Exception {
      List<IAtsWorkItem> workItems = new ArrayList<>();
      for (String id : services.getQueryService().getIdsFromStr(ids)) {
         ArtifactToken action = services.getArtifactByLegacyPcrId(id);
         if (action != null) {
            IAtsWorkItem workItem = services.getWorkItemFactory().getWorkItem(action);
            workItems.add(workItem);
         }
      }
      return getActionStateResultString(workItems);
   }

   @Override
   @Path("{ids}/state")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public String getActionState(@PathParam("ids") String ids) throws Exception {
      List<IAtsWorkItem> workItems = services.getWorkItemListByIds(ids);
      return getActionStateResultString(workItems);
   }

   private String getActionStateResultString(List<IAtsWorkItem> workItems) throws IOException, JsonGenerationException {
      JsonGenerator writer = null;
      StringWriter stringWriter = new StringWriter();
      writer = jsonFactory.createJsonGenerator(stringWriter);
      if (workItems.size() > 1) {
         writer.writeStartArray();
      }
      for (IAtsWorkItem workItem : workItems) {
         writer.writeStartObject();
         writer.writeStringField("id", workItem.getIdString());
         writer.writeStringField("atsId", workItem.getAtsId());
         writer.writeStringField("legacyId",
            services.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.LegacyPcrId, ""));
         writer.writeStringField("stateType", workItem.getStateMgr().getStateType().name());
         writer.writeStringField("state", workItem.getStateMgr().getCurrentStateName());
         writer.writeEndObject();
      }
      if (workItems.size() > 1) {
         writer.writeEndArray();
      }
      writer.close();
      return stringWriter.toString();
   }

   /**
    * @query_string <attr type name>=<value>, <attr type id>=<value>
    * @return json representation of the matching workItem(s)
    */
   @Override
   @Path("query")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public Set<IAtsWorkItem> query(@Context UriInfo uriInfo) throws Exception {
      Set<IAtsWorkItem> workItems = new HashSet<>();
      MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);
      Set<Entry<String, List<String>>> entrySet = queryParameters.entrySet();
      IAtsQuery query = services.getQueryService().createQuery(WorkItemType.WorkItem);
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
               IAtsUser assignee = services.getUserService().getUserById(userId);
               if (assignee != null) {
                  assignees.add(assignee);
               }
            }
            query.andAssignee(assignees.toArray(new IAtsUser[assignees.size()]));
         } else if (entry.getKey().equals("IPT")) {
            query.andAttr(AtsAttributeTypes.IPT, entry.getValue().iterator().next());
         } else if (entry.getKey().equals("Team")) {
            Collection<IAtsTeamDefinition> teams = new LinkedList<>();
            for (String teamId : entry.getValue()) {
               IAtsTeamDefinition team = services.getConfigItem(Long.valueOf(teamId));
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
                  if (stateType2 != null) {
                     stateTypes.add(stateType2);
                  }
               }
               query.andStateType(stateTypes.toArray(new StateType[stateTypes.size()]));
            } catch (Exception ex) {
               // do nothing
            }
         } else if (entry.getKey().equals("Originator")) {
            IAtsUser assignee = services.getUserService().getUserById(entry.getValue().iterator().next());
            query.andOriginator(assignee);
         } else if (entry.getKey().equals("WorkItemType")) {
            List<WorkItemType> workItemTypes = new LinkedList<>();
            for (String type : entry.getValue()) {
               WorkItemType workItem = WorkItemType.valueOf(type);
               if (workItem != null) {
                  workItemTypes.add(workItem);
               }
            }
            query.andWorkItemType(workItemTypes.toArray(new WorkItemType[workItemTypes.size()]));
         } else if (entry.getKey().equals("Version")) {
            IAtsVersion version = services.getConfigItem(Long.valueOf(entry.getValue().iterator().next()));
            query.andVersion(version);
         }
         // else, attempt to resolve as attribute type id or name
         else {
            String key = entry.getKey();
            AttributeTypeId attrType = null;
            if (Strings.isNumeric(key)) {
               attrType = services.getStoreService().getAttributeType(Long.valueOf(key));
            }
            if (attrType == null) {
               attrType = services.getStoreService().getAttributeType(key);
            }
            if (attrType != null) {
               query.andAttr(attrType, entry.getValue());
            }
         }
      }
      workItems.addAll(query.getItems());
      return workItems;
   }

   @Override
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public NewActionResult createAction(NewActionData newActionData) {
      NewActionResult result = new NewActionResult();
      try {
         IAtsUser asUser = services.getUserService().getUserById(newActionData.getAsUserId());
         if (asUser == null) {
            result.getResults().errorf("asUser [%s] not valid", newActionData.getAsUserId());
            return result;
         }
         IAtsChangeSet changes = services.getStoreService().createAtsChangeSet("Create Action - Server", asUser);

         ActionFactory factory = new ActionFactory(services);
         ActionResult actionResult = factory.createAction(newActionData, changes);

         TransactionId transaction = changes.executeIfNeeded();
         if (transaction != null && transaction.isInvalid()) {
            result.getResults().errorf("TransactionId came back as inValid.  Action not created.");
            return result;
         }
         result.setAction(ArtifactId.valueOf(actionResult.getActionArt()));
         for (ArtifactId teamWf : actionResult.getTeamWfArts()) {
            result.addTeamWf(teamWf);
         }
      } catch (Exception ex) {
         result.getResults().errorf("Exception creating action [%s]", Lib.exceptionToString(ex));
      }
      return result;
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
   public Response createAction(MultivaluedMap<String, String> form) throws Exception {
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
         services.getQueryService().createQuery(AtsArtifactTypes.ActionableItem).andName(actionableItems).getOneOrNull(
            IAtsActionableItem.class);
      if (aia == null) {
         return RestUtil.returnBadRequest(String.format("actionableItems [%s] is not valid", actionableItems));
      }
      aias.add(aia);

      // validate userId
      String userId = form.getFirst("userId");
      if (!Strings.isValid(userId)) {
         return RestUtil.returnBadRequest("userId is not valid");
      }
      IAtsUser atsUser = services.getUserService().getUserById(userId);
      if (atsUser == null) {
         return RestUtil.returnBadRequest(String.format("userId [%s] is not valid", userId));
      }

      // validate changeType
      String changeTypeStr = form.getFirst("changeType");
      if (!Strings.isValid(changeTypeStr)) {
         return RestUtil.returnBadRequest("changeType is not valid");
      }
      IAtsChangeSet changes = services.getStoreService().createAtsChangeSet("Create Action - Server", atsUser);

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
      ActionResult action = services.getActionFactory().createAction(atsUser, title, description, changeType, priority,
         false, null, aias, new Date(), atsUser, null, changes);
      changes.execute();

      // Redirect to action ui
      return RestUtil.redirect(action.getTeamWfs(), ATS_UI_ACTION_PREFIX, (IAtsServer) services);
   }

}
