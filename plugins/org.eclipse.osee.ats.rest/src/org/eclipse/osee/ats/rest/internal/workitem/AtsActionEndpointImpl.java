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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
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

   private final IAtsServices services;
   private final OrcsApi orcsApi;
   private static final String ATS_UI_ACTION_PREFIX = "/ui/action/UUID";

   public AtsActionEndpointImpl(IAtsServices services, OrcsApi orcsApi) {
      this.services = services;
      this.orcsApi = orcsApi;
   }

   @Override
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() throws Exception {

      QueryBuilder query = ((IAtsServer) services).getOrcsApi().getQueryFactory().fromBranch(services.getAtsBranch());
      query.and(AtsAttributeTypes.TeamDefinition, "At2WHxC2lxLOGB0YiuQA");
      query.and(AtsAttributeTypes.PriorityType, Arrays.asList("3", "2"));
      ResultSet<ArtifactReadable> results = query.getResults();

      for (ArtifactReadable art : results) {
         System.err.println(art.getSoleAttributeValue(AtsAttributeTypes.PriorityType, "def"));
      }

      return RestUtil.simplePageHtml("Action Resource");
   }

   /**
    * @param ids (guid, atsId) of action to display
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
    * @query_string <attr type name>=<value>, <attr type id>=<value>
    * @return json representation of the matching workItem(s)
    */
   @Override
   @Path("query")
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   public Set<IAtsWorkItem> query(@Context UriInfo uriInfo) throws Exception {
      MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);
      Set<IAtsWorkItem> workItems = new HashSet<>();
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
      orcsApi.getTransactionFactory().createTransaction(services.getAtsBranch(), atsUser.getStoreObject(),
         "Create Action - Server");

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
