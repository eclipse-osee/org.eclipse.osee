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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Response.Status;
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
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Donald G. Dunne
 */
public class KanbanOperations {

   private KanbanOperations() {
      // Utility Class
   }

   public static JaxKbSprint getSprintItemsForKb(IAtsServer atsServer, long teamUuid, long sprintUuid) {
      IAgileSprint sprint = atsServer.getAgileService().getAgileSprint(sprintUuid);
      if (sprint == null) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "sprintUuid is not valid");
      }
      IAgileTeam agileTeam = atsServer.getAgileService().getAgileTeam(teamUuid);
      if (agileTeam == null) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "teamUuid is not valid");
      }
      JaxKbSprint items = new JaxKbSprint();
      items.setUuid(sprint.getId());
      items.setName(sprint.getName());
      items.setActive(sprint.isActive());
      items.setTeamUuid(sprint.getTeamUuid());

      Map<String, String> assigneeToName = getNameOverride(sprint, atsServer);
      boolean unAssignedAdded = false;
      for (IAgileItem aItem : atsServer.getAgileService().getItems(sprint)) {

         IAtsWorkItem workItem = atsServer.getTeamWf(aItem.getId());
         ArtifactReadable artifact = atsServer.getArtifact(workItem.getId());

         JaxKbTask task = createJaxKbTask(aItem, workItem, artifact, atsServer);
         items.getTasks().put(String.valueOf(aItem.getId()), task);

         // "userIdToName" : {
         //   "jod6us" : "John Doe",
         //   "sam5us" : "Sam Smith"
         //  },
         for (IAtsUser user : workItem.getStateMgr().getAssignees()) {
            String name = user.getName();
            if (assigneeToName.containsKey(name)) {
               name = assigneeToName.get(name);
            }
            items.getUserIdToName().put(user.getUserId(), name);
            if (user.equals(AtsCoreUsers.UNASSIGNED_USER)) {
               unAssignedAdded = true;
            }
         }

         //  "assigneesToTaskUuids" : {
         //   "jod6us" : [ "1234", "6543", "3434","9898", "5656" ],
         //   "sam5us" : [ "3636","4325","2323" ]
         //  },
         if (workItem.getStateMgr().getStateType().isInWork()) {
            String assigneesUuids = getAssigneeUserIdsString(workItem, atsServer);
            items.addAssigneeIdToTaskUuid(assigneesUuids, String.valueOf(aItem.getId()));
         }

         //  "implementersToTaskUuids" : {
         //   "jod6us" : [ "9898", "5656" ],
         //   "sam5us" : [ "4325" ]
         //  },
         if (workItem.getStateMgr().getStateType().isCompletedOrCancelled()) {
            String implementersUuids = getImplementerUserIdsString(workItem, atsServer);
            items.addImplementerIdToTaskUuid(implementersUuids, String.valueOf(aItem.getId()));
         }

         // "statesToTaskUuids" : {
         //   "New" : [ "1234", "3434","2323" ],
         //   "InProgress" : [ "6543","3636" ],
         //   "Cancelled"  : [ "9898" ],
         //   "Completed"  : [ "5656","4325" ]
         // },
         items.addStateNameToTaskUuid(workItem.getStateMgr().getCurrentStateName(), String.valueOf(aItem.getId()));

         // "availableStates" : [ {
         //    "name" : "New",
         //    "ordinal" : 1,
         //    "stateweights" : 0,
         //    "stateType" : "Working",
         //    "toStates" : [ "Cancelled", "Completed", "InProgress" ]
         // },
         addAvailableStates(items, aItem, workItem, artifact, atsServer);
      }
      if (!unAssignedAdded) {
         items.getUserIdToName().put(AtsCoreUsers.UNASSIGNED_USER.getUserId(), AtsCoreUsers.UNASSIGNED_USER.getName());
         items.addAssigneeIdToTaskUuid(AtsCoreUsers.UNASSIGNED_USER.getUserId(), "");
      }

      return items;
   }

   private static Map<String, String> getNameOverride(IAgileSprint agileSprint, IAtsServer atsServer) {
      Map<String, String> keyValueMap = new HashMap<>();
      String valueStr =
         atsServer.getAttributeResolver().getSoleAttributeValue(agileSprint, AtsAttributeTypes.KanbanStoryName, "");
      if (Strings.isValid(valueStr)) {
         for (String value : valueStr.split("\n")) {
            String[] keyValue = value.split(":");
            keyValueMap.put(keyValue[0], keyValue[1]);
         }
      }
      return keyValueMap;
   }

   private static String getAssigneeUserIdsString(IAtsWorkItem workItem, IAtsServer atsServer) {
      List<IAtsUser> assignees = workItem.getStateMgr().getAssignees();
      if (assignees.isEmpty()) {
         return "";
      } else {
         return assignees.iterator().next().getUserId();
      }
   }

   private static String getImplementerUserIdsString(IAtsWorkItem workItem, IAtsServer atsServer) {
      List<IAtsUser> implementers = atsServer.getImplementerService().getImplementers(workItem);
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

   private static void addAvailableStates(JaxKbSprint items, IAgileItem aItem, IAtsWorkItem workItem, ArtifactReadable artifact, IAtsServer atsServer) {
      try {
         IAtsWorkDefinition workDef = workItem.getWorkDefinition();
         for (IAtsStateDefinition stateDef : atsServer.getWorkDefinitionService().getStatesOrderedByOrdinal(workDef)) {
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

   private static JaxKbTask createJaxKbTask(IAgileItem aItem, IAtsWorkItem wItem, ArtifactReadable artifact, IAtsServer atsServer) {
      JaxKbTask task = new JaxKbTask();
      task.setName(aItem.getName());
      task.setGuid(String.valueOf(aItem.getId()));
      task.setCanEdit(true);
      task.setArtifactType(wItem.getArtifactTypeName());
      task.getAttributeMap().put("Shortname", wItem.getAtsId());
      IAtsVersion version = atsServer.getVersionService().getTargetedVersion(wItem);
      task.getAttributeMap().put("versionName", (version != null ? version.getName() : ""));
      Collection<IAgileFeatureGroup> featureGroups = atsServer.getAgileService().getFeatureGroups(aItem);
      if (!featureGroups.isEmpty()) {
         String grps = Collections.toString(",", featureGroups);
         task.getAttributeMap().put("featureName", grps);
      }

      task.setBranchGuid(String.valueOf(artifact.getBranchId()));
      org.eclipse.osee.framework.jdk.core.type.ResultSet<? extends AttributeReadable<Object>> attributes =
         artifact.getAttributes();
      Collection<? extends AttributeTypeToken> attrTypes =
         atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().getAll();
      Set<String> ais = new HashSet<String>();
      if (!attributes.isEmpty()) {
         for (AttributeTypeToken attrType : attrTypes) {
            if (!attrType.getName().equals("ats.Log") && artifact.isAttributeTypeValid(attrType)) {
               List<Object> attributeValues = artifact.getAttributeValues(attrType);
               if (!attributeValues.isEmpty()) {
                  task.getAttributeMap().put(attrType.getName(),
                     Collections.toString("; ", artifact.getAttributeValues(attrType)));
               }
            }
            if (attrType.getName().equals("ats.Actionable Item")) {
               // skip
            } else if (attrType.equals(AtsAttributeTypes.ActionableItemReference)) {
               for (Object id : artifact.getAttributeValues(attrType)) {
                  ais.add(((IAtsObject) atsServer.getConfigItem((ArtifactId) id)).getName());
               }
            }
         }
      }
      task.getAttributeMap().put("actionableItemName", Collections.toString("; ", ais));
      return task;
   }

}
