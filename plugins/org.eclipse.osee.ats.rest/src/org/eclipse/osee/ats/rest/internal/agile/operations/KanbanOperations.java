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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class KanbanOperations {

   private KanbanOperations() {
      // Utility Class
   }

   @SuppressWarnings("unchecked")
   public static JaxKbSprint getSprintItemsForKb(AtsApi atsApi, long teamId, long sprintId) {
      IAgileSprint sprint = atsApi.getAgileService().getAgileSprint(sprintId);
      if (sprint == null) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "sprintId is not valid");
      }
      IAgileTeam agileTeam = atsApi.getAgileService().getAgileTeam(teamId);
      if (agileTeam == null) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamId is not valid");
      }
      JaxKbSprint items = new JaxKbSprint();
      items.setId(sprint.getId());
      items.setName(sprint.getName());
      items.setActive(sprint.isActive());
      items.setTeamId(sprint.getTeamId());

      Map<String, String> assigneeToName = getNameOverride(sprint, atsApi);
      Collection<String> ignoreStates = getIgnoreStates(agileTeam, atsApi);
      boolean unAssignedAdded = false;
      for (IAgileItem aItem : atsApi.getAgileService().getItems(sprint)) {

         IAtsWorkItem workItem = atsApi.getQueryService().getTeamWf(aItem.getId());
         ArtifactToken artifact = atsApi.getQueryService().getArtifact(workItem.getId());

         JaxKbTask task = getAsJaxKbTask(aItem, workItem, artifact, atsApi);
         items.getTasks().put(String.valueOf(aItem.getId()), task);

         // "userIdToName" : {
         //   "jod6us" : "John Doe",
         //   "sam5us" : "Sam Smith"
         //  },
         for (IAtsUser user : Collections.setUnion(workItem.getStateMgr().getAssignees(),
            atsApi.getImplementerService().getImplementers(workItem))) {
            String name = user.getName();
            if (assigneeToName.containsKey(name)) {
               name = assigneeToName.get(name);
            }
            items.getUserIdToName().put(user.getUserId(), name);
            if (user.equals(AtsCoreUsers.UNASSIGNED_USER)) {
               unAssignedAdded = true;
            }
         }

         //  "assigneesToTaskIds" : {
         //   "jod6us" : [ "1234", "6543", "3434","9898", "5656" ],
         //   "sam5us" : [ "3636","4325","2323" ]
         //  },
         if (workItem.getStateMgr().getStateType().isInWork()) {
            String assigneesIds = getAssigneeUserIdsString(workItem, atsApi);
            items.addAssigneeIdToTaskId(assigneesIds, String.valueOf(aItem.getId()));
         }

         //  "implementersToTaskIds" : {
         //   "jod6us" : [ "9898", "5656" ],
         //   "sam5us" : [ "4325" ]
         //  },
         if (workItem.getStateMgr().getStateType().isCompletedOrCancelled()) {
            String implementersIds = getImplementerUserIdsString(workItem, atsApi);
            items.addImplementerIdToTaskId(implementersIds, String.valueOf(aItem.getId()));
         }

         // "statesToTaskIds" : {
         //   "New" : [ "1234", "3434","2323" ],
         //   "InProgress" : [ "6543","3636" ],
         //   "Cancelled"  : [ "9898" ],
         //   "Completed"  : [ "5656","4325" ]
         // },
         items.addStateNameToTaskId(workItem.getStateMgr().getCurrentStateName(), String.valueOf(aItem.getId()));

         // "availableStates" : [ {
         //    "name" : "New",
         //    "ordinal" : 1,
         //    "stateweights" : 0,
         //    "stateType" : "Working",
         //    "toStates" : [ "Cancelled", "Completed", "InProgress" ]
         // },
         addAvailableStates(items, aItem, workItem, artifact, atsApi, ignoreStates);
      }
      if (!unAssignedAdded) {
         items.getUserIdToName().put(AtsCoreUsers.UNASSIGNED_USER.getUserId(), AtsCoreUsers.UNASSIGNED_USER.getName());
         items.addAssigneeIdToTaskId(AtsCoreUsers.UNASSIGNED_USER.getUserId(), "");
      }

      return items;
   }

   private static Collection<String> getIgnoreStates(IAgileTeam agileTeam, AtsApi atsApi) {
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

   private static Map<String, String> getNameOverride(IAgileSprint agileSprint, AtsApi atsApi) {
      Map<String, String> keyValueMap = new HashMap<>();
      String valueStr =
         atsApi.getAttributeResolver().getSoleAttributeValue(agileSprint, AtsAttributeTypes.KanbanStoryName, "");
      if (Strings.isValid(valueStr)) {
         for (String value : valueStr.split("\n")) {
            String[] keyValue = value.split(":");
            keyValueMap.put(keyValue[0], keyValue[1]);
         }
      }
      return keyValueMap;
   }

   private static String getAssigneeUserIdsString(IAtsWorkItem workItem, AtsApi atsApi) {
      List<IAtsUser> assignees = workItem.getStateMgr().getAssignees();
      if (assignees.isEmpty()) {
         return "";
      } else {
         return assignees.iterator().next().getUserId();
      }
   }

   private static String getImplementerUserIdsString(IAtsWorkItem workItem, AtsApi atsApi) {
      List<IAtsUser> implementers = atsApi.getImplementerService().getImplementers(workItem);
      if (implementers.size() > 1) {
         implementers.remove(AtsCoreUsers.SYSTEM_USER);
      }
      if (implementers.isEmpty()) {
         if (workItem.isCancelled()) {
            return workItem.getCancelledBy().getUserId();
         }
         return "";
      } else {
         return implementers.iterator().next().getUserId();
      }
   }

   private static void addAvailableStates(JaxKbSprint items, IAgileItem aItem, IAtsWorkItem workItem, ArtifactToken artifact, AtsApi atsApi, Collection<String> ignoreStates) {
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

   private static JaxKbTask getAsJaxKbTask(IAgileItem aItem, IAtsWorkItem wItem, ArtifactToken artifact, AtsApi atsApi) {
      JaxKbTask task = new JaxKbTask();
      task.setName(aItem.getName());
      task.setGuid(String.valueOf(aItem.getId()));
      task.setCanEdit(true);
      task.setArtifactType(wItem.getArtifactTypeName());
      task.getAttributeMap().put("Shortname", wItem.getAtsId());
      IAtsVersion version = atsApi.getVersionService().getTargetedVersion(wItem);
      task.getAttributeMap().put("versionName", (version != null ? version.getName() : ""));
      Collection<IAgileFeatureGroup> featureGroups = atsApi.getAgileService().getFeatureGroups(aItem);
      if (!featureGroups.isEmpty()) {
         String grps = Collections.toString(",", featureGroups);
         task.getAttributeMap().put("featureName", grps);
      }

      task.setBranchGuid(artifact.getBranchIdString());
      Collection<IAttribute<Object>> attributes = atsApi.getAttributeResolver().getAttributes(artifact);
      Collection<? extends AttributeTypeToken> attrTypes = atsApi.getStoreService().getAttributeTypes();
      Set<String> ais = new HashSet<String>();
      if (!attributes.isEmpty()) {
         for (AttributeTypeToken attrType : attrTypes) {
            if (!attrType.getName().equals("ats.Log") && atsApi.getStoreService().isAttributeTypeValid(artifact,
               attrType)) {
               Collection<IAttribute<Object>> attributeValues = atsApi.getAttributeResolver().getAttributes(artifact);
               if (!attributeValues.isEmpty()) {
                  task.getAttributeMap().put(attrType.getName(),
                     Collections.toString("; ", atsApi.getAttributeResolver().getAttributes(artifact)));
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
      // store transaction to check if when it comes back as a change
      TransactionId transactionId = ((ArtifactReadable) artifact).getTransaction();
      Conditions.assertNotNull(transactionId, "transactId");
      task.setTransactionId(transactionId);
      return task;
   }

}
