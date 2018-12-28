/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.agile.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.kanban.JaxKbAvailableState;
import org.eclipse.osee.ats.api.agile.kanban.JaxKbSprint;
import org.eclipse.osee.ats.api.agile.kanban.JaxKbTask;
import org.eclipse.osee.ats.api.agile.kanban.KanbanRowType;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;

/**
 * @author Donald G. Dunne
 */
public class KanbanOperations {

   private final long sprintId;
   private final KanbanRowType rowType;
   private final long teamId;
   private final Map<Long, Set<String>> aItemToRowMap = new HashMap<>();
   private final Map<Long, Set<String>> aItemToRowIdMap = new HashMap<>();
   private final AtsApi atsApi;

   public KanbanOperations(AtsApi atsApi, long teamId, long sprintId, KanbanRowType rowType) {
      this.atsApi = atsApi;
      this.teamId = teamId;
      this.sprintId = sprintId;
      this.rowType = rowType;
   }

   public JaxKbSprint getSprintItemsForKb() {
      IAgileSprint sprint = atsApi.getAgileService().getAgileSprint(sprintId);
      if (sprint == null) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "sprintId is not valid");
      }
      IAgileTeam agileTeam = atsApi.getAgileService().getAgileTeam(teamId);
      if (agileTeam == null) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamId is not valid");
      }
      JaxKbSprint jSprint = new JaxKbSprint();
      jSprint.setId(sprint.getId());
      jSprint.setName(sprint.getName());
      jSprint.setActive(sprint.isActive());
      jSprint.setTeamId(sprint.getTeamId());
      jSprint.setRowType(rowType);

      Collection<String> ignoreStates = getIgnoreStates(agileTeam, atsApi);

      Set<ArtifactToken> teamMembers = new HashSet<>();
      // add set of team members; going to add assignees and implementers if not in team
      teamMembers.addAll(atsApi.getAgileService().getTeamMembersOrdered(agileTeam));

      for (IAgileItem aItem : atsApi.getAgileService().getItems(sprint)) {

         IAtsWorkItem workItem = atsApi.getQueryService().getTeamWf(aItem.getId());
         ArtifactToken artifact = atsApi.getQueryService().getArtifact(workItem.getId());

         JaxKbTask task = createJaxKbTask(aItem, workItem, artifact, agileTeam, atsApi);
         jSprint.getTasks().put(String.valueOf(aItem.getId()), task);

         if (rowType == KanbanRowType.BY_ASSIGNEE) {
            addRowsByAssignees(jSprint, task, aItem, workItem, teamMembers);
            addAssigneesToTask(aItem, task, workItem, KanbanRowType.BY_ASSIGNEE, teamMembers);
         } else {
            addRowsByStory(jSprint, task, aItem, workItem);
            addAssigneesToTask(aItem, task, workItem, KanbanRowType.BY_STORY, teamMembers);
         }

         // "statesToTaskIds" : {
         //   "New" : [ "1234", "3434","2323" ],
         //   "InProgress" : [ "6543","3636" ],
         //   "Cancelled"  : [ "9898" ],
         //   "Completed"  : [ "5656","4325" ]
         // },
         jSprint.addStateNameToTaskId(workItem.getStateMgr().getCurrentStateName(), String.valueOf(aItem.getId()));

         // "availableStates" : [ {
         //    "name" : "New",
         //    "ordinal" : 1,
         //    "stateweights" : 0,
         //    "stateType" : "Working",
         //    "toStates" : [ "Cancelled", "Completed", "InProgress" ]
         // },
         addAvailableStates(jSprint, aItem, workItem, artifact, atsApi, ignoreStates);
      }

      List<ArtifactToken> teamMembersOrdered = new LinkedList<>();
      teamMembersOrdered.addAll(teamMembers);
      java.util.Collections.sort(teamMembersOrdered, new NamedComparator(SortOrder.ASCENDING));

      for (ArtifactToken user : teamMembersOrdered) {
         jSprint.getUserNameToId().put(user.getName(), user.getIdString());
         jSprint.getTeamMembersOrdered().add(user.getName());
         jSprint.getUserIdToName().put(user.getIdString(), user.getName());
         if (rowType == KanbanRowType.BY_ASSIGNEE) {
            // make sure each team member has a row
            jSprint.getRowIdToName().put(user.getIdString(), user.getName());
         }
      }

      for (ArtifactToken user : atsApi.getAgileService().getOtherMembersOrdered(agileTeam)) {
         if (!teamMembers.contains(user)) {
            jSprint.getUserNameToId().put(user.getName(), user.getIdString());
            jSprint.getOtherMembersOrdered().add(user.getName());
            jSprint.getUserIdToName().put(user.getIdString(), user.getName());
         }
      }

      return jSprint;
   }

   private void addAssigneesToTask(IAgileItem aItem, JaxKbTask task, IAtsWorkItem workItem, KanbanRowType rowType, Set<ArtifactToken> teamMembers) {
      Set<String> assigneeIds = new HashSet<>();
      Set<String> assigneeNames = new HashSet<>();
      if (rowType == KanbanRowType.BY_ASSIGNEE) {
         // use cached assignees since same as row
         assigneeIds = aItemToRowIdMap.get(aItem.getId());
         assigneeNames = aItemToRowMap.get(aItem.getId());
      } else {
         @SuppressWarnings("unchecked")
         Set<IAtsUser> assigneeImplementers = Collections.setUnion(workItem.getStateMgr().getAssignees(),
            atsApi.getImplementerService().getImplementers(workItem));
         for (IAtsUser user : assigneeImplementers) {
            if (!AtsCoreUsers.isSystemUser(user)) {
               teamMembers.add(user.getStoreObject());
               String name = user.getName();
               assigneeNames.add(name);
               assigneeIds.add(user.getIdString());
            }
         }
      }
      String assigneesIdStr = assigneeIds == null ? "" : Collections.toString("; ", assigneeIds);
      task.getAttributeMap().put("AssigneesIds", assigneesIdStr);
      String assigneesStr = assigneeNames == null ? "" : Collections.toString("; ", assigneeNames);
      task.getAttributeMap().put("AssigneesStr", assigneesStr);
      task.getAttributeMap().put("AssigneesStrShort", Strings.truncate(assigneesStr, 30, true));
   }

   private void addRowsByStory(JaxKbSprint jSprint, JaxKbTask task, IAgileItem aItem, IAtsWorkItem workItem) {
      ArtifactToken sprintArt = atsApi.getQueryService().getArtifact(jSprint.getId());
      Collection<ArtifactToken> sprintStories =
         atsApi.getRelationResolver().getRelated(sprintArt, AtsRelationTypes.AgileStoryToSprint_Story);

      // "rowIdToName" : {
      //   "4345" : "As a user I will move the user right and left",
      //   "6433" : "As a user I will move the user forward and backward"
      //  },
      Set<String> rowNames = new HashSet<>(sprintStories.size());
      Set<String> rowIds = new HashSet<>(sprintStories.size());
      for (ArtifactToken sprintStory : sprintStories) {
         String name = sprintStory.getName();
         rowNames.add(name);
         rowIds.add(sprintStory.getIdString());
         jSprint.getRowIdToName().put(sprintStory.getIdString(), sprintStory.getName());
      }
      aItemToRowMap.put(aItem.getId(), rowNames);
      aItemToRowIdMap.put(aItem.getId(), rowIds);

      //  "rowToTaskIds" : {
      //   "4345" : [ "1234", "6543", "3434","9898", "5656" ],
      //   "6433" : [ "3636","4325","2323" ]
      //  },
      rowIds.clear();
      for (ArtifactToken story : atsApi.getRelationResolver().getRelated(workItem,
         AtsRelationTypes.AgileStoryToItems_Story)) {
         jSprint.addRowIdToTaskId(story.getIdString(), String.valueOf(aItem.getId()));
      }
   }

   @SuppressWarnings("unchecked")
   private void addRowsByAssignees(JaxKbSprint jSprint, JaxKbTask task, IAgileItem aItem, IAtsWorkItem workItem, Set<ArtifactToken> teamMembers) {
      // "rowIdToName" : {
      //   "jod6us" : "John Doe",
      //   "sam5us" : "Sam Smith"
      //  },
      Set<IAtsUser> assigneeImplementers = Collections.setUnion(workItem.getStateMgr().getAssignees(),
         atsApi.getImplementerService().getImplementers(workItem));
      Set<String> assigneeNames = new HashSet<>(assigneeImplementers.size());
      Set<String> assigneeIds = new HashSet<>(assigneeImplementers.size());
      for (IAtsUser user : assigneeImplementers) {
         if (!AtsCoreUsers.isSystemUser(user)) {
            teamMembers.add(user.getStoreObject());
            String name = user.getName();
            assigneeNames.add(name);
            assigneeIds.add(user.getIdString());
            // add user if assigned to make sure in list
            jSprint.getUserIdToName().put(user.getIdString(), user.getName());
            jSprint.getRowIdToName().put(user.getIdString(), user.getName());
         }
      }

      // Cache assignees for use if rowType == BY_ASSIGNEE
      aItemToRowMap.put(aItem.getId(), assigneeNames);
      aItemToRowIdMap.put(aItem.getId(), assigneeIds);

      //  "rowToTaskIds" : {
      //   "jod6us" : [ "1234", "6543", "3434","9898", "5656" ],
      //   "sam5us" : [ "3636","4325","2323" ]
      //  },
      List<String> assigneesIds = new LinkedList<>();
      if (workItem.getStateMgr().getStateType().isInWork()) {
         assigneesIds.addAll(getAssigneeUserIdsString(workItem, atsApi, teamMembers));
      } else {
         assigneesIds.addAll(getImplementerUserIdsString(workItem, atsApi, teamMembers));
      }
      for (String assigneeId : assigneeIds) {
         jSprint.addRowIdToTaskId(assigneeId, String.valueOf(aItem.getId()));
      }
   }

   private Collection<String> getIgnoreStates(IAgileTeam agileTeam, AtsApi atsApi) {
      List<String> values = new ArrayList<>();
      String strValue =
         atsApi.getAttributeResolver().getSoleAttributeValue(agileTeam, AtsAttributeTypes.KanbanIgnoreStates, "");
      if (Strings.isValid(strValue)) {
         for (String value : strValue.split(";")) {
            values.add(value);
         }
      }
      return values;
   }

   private Collection<String> getAssigneeUserIdsString(IAtsWorkItem workItem, AtsApi atsApi, Set<ArtifactToken> teamMembers) {
      List<IAtsUser> assignees = workItem.getStateMgr().getAssignees();
      Set<String> ids = new HashSet<>();
      // Make sure team includes any assigned even if not configured as part of team
      for (IAtsUser assignee : assignees) {
         if (!AtsCoreUsers.isSystemUser(assignee)) {
            teamMembers.add(assignee.getStoreObject());
         } else {
            ids.add(assignee.getIdString());
         }
      }
      return ids;
   }

   private Collection<String> getImplementerUserIdsString(IAtsWorkItem workItem, AtsApi atsApi, Set<ArtifactToken> teamMembers) {
      if (workItem.isCancelled()) {
         return java.util.Collections.singleton(workItem.getCancelledBy().getStoreObject().getIdString());
      }

      List<IAtsUser> implementers = atsApi.getImplementerService().getImplementers(workItem);
      Set<String> ids = new HashSet<>();
      // Make sure team includes any assigned even if not configured as part of team
      for (IAtsUser implementer : implementers) {
         if (!AtsCoreUsers.isSystemUser(implementer)) {
            teamMembers.add(implementer.getStoreObject());
         }
      }
      if (implementers.size() > 1) {
         implementers.remove(AtsCoreUsers.SYSTEM_USER);
      }

      for (IAtsUser implementer : implementers) {
         ids.add(implementer.getIdString());
      }
      return ids;
   }

   private void addAvailableStates(JaxKbSprint items, IAgileItem aItem, IAtsWorkItem workItem, ArtifactToken artifact, AtsApi atsApi, Collection<String> ignoreStates) {
      try {
         IAtsWorkDefinition workDef = workItem.getWorkDefinition();
         for (IAtsStateDefinition stateDef : atsApi.getWorkDefinitionService().getStatesOrderedByOrdinal(workDef)) {
            if (ignoreStates.contains(stateDef.getName())) {
               continue;
            }
            JaxKbAvailableState state = null;
            for (JaxKbAvailableState availState : items.getAvailableStates()) {
               if (availState.getName().equals(stateDef.getName())) {
                  state = availState;
                  break;
               }
            }
            if (state == null) {
               state = new JaxKbAvailableState();
               state.setName(stateDef.getName());
               state.setOrdinal(stateDef.getOrdinal());
               state.setStateType(stateDef.getStateType().name());
               for (IAtsStateDefinition toState : stateDef.getToStates()) {
                  state.getToStates().add(toState.getName());
               }
               items.getAvailableStates().add(state);
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

   private JaxKbTask createJaxKbTask(IAgileItem aItem, IAtsWorkItem wItem, ArtifactToken artifact, IAgileTeam agileTeam, AtsApi atsApi) {
      JaxKbTask task = new JaxKbTask();
      task.setName(aItem.getName());
      task.setGuid(String.valueOf(aItem.getId()));
      task.setCanEdit(true);
      task.setArtifactType(wItem.getArtifactTypeName());
      task.getAttributeMap().put("Shortname", wItem.getAtsId());
      IAtsVersion version = atsApi.getVersionService().getTargetedVersion(wItem);
      task.getAttributeMap().put("versionName", version != null ? version.getName() : "");
      Collection<IAgileFeatureGroup> featureGroups = atsApi.getAgileService().getFeatureGroups(aItem);
      if (!featureGroups.isEmpty()) {
         String grps = Collections.toString(",", featureGroups);
         task.getAttributeMap().put("featureName", grps);
      }

      task.setBranchGuid(artifact.getBranchIdString());
      Collection<IAttribute<Object>> attributes = atsApi.getAttributeResolver().getAttributes(artifact);
      Collection<? extends AttributeTypeToken> attrTypes = atsApi.getStoreService().getAttributeTypes();
      Set<String> ais = new HashSet<>();
      if (!attributes.isEmpty()) {
         for (AttributeTypeToken attrType : attrTypes) {
            if (!attrType.getName().equals("ats.Log") && atsApi.getStoreService().isAttributeTypeValid(artifact,
               attrType)) {
               Collection<IAttribute<Object>> attributeValues =
                  atsApi.getAttributeResolver().getAttributes(artifact, attrType);
               if (!attributeValues.isEmpty()) {
                  task.getAttributeMap().put(attrType.getName(), Collections.toString("; ", attributeValues));
               }
            }
            if (attrType.getName().equals("ats.Actionable Item")) {
               // skip
            } else if (attrType.equals(AtsAttributeTypes.ActionableItemReference)) {
               for (Object id : atsApi.getAttributeResolver().getAttributeValues(artifact,
                  AtsAttributeTypes.ActionableItemReference)) {
                  ais.add(((IAtsObject) atsApi.getQueryService().getConfigItem((ArtifactId) id)).getName());
               }
            }
         }
      }
      task.getAttributeMap().put("actionableItemName", Collections.toString("; ", ais));
      return task;
   }

}
