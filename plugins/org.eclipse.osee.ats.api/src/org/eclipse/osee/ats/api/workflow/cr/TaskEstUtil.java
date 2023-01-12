/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.api.workflow.cr;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public class TaskEstUtil {

   // Used to identify which user groups or tasks are part of CR TaskEst config
   public static String TASK_EST_STATIC_ID = "taskest";
   public static String TASK_EST_MANUAL = "manual";
   public static String TASK_EST_CANNED = "canned";

   public static IAtsTeamWorkflow getWorkflow(IAtsTeamWorkflow crTeamWf, TaskEstDefinition ted, AtsApi atsApi) {
      IAtsTeamWorkflow teamWf = null;
      IAtsTask task = getTask(crTeamWf, ted, atsApi);
      if (task.isValid()) {
         teamWf = getWorkflow(teamWf, task, atsApi);
      }
      return teamWf;
   }

   public static IAtsTeamWorkflow getWorkflow(IAtsTeamWorkflow crTeamWf, IAtsTask task, AtsApi atsApi) {
      IAtsTeamWorkflow foundWf = null;
      for (ArtifactToken derived : atsApi.getRelationResolver().getRelated(task, AtsRelationTypes.Derive_To)) {
         if (atsApi.getAttributeResolver().hasTag(derived, TaskEstUtil.TASK_EST_STATIC_ID)) {
            foundWf = atsApi.getWorkItemService().getTeamWf(derived);
            break;
         }
      }
      return foundWf;
   }

   public static boolean hasWorkflow(IAtsTeamWorkflow crTeamWf, TaskEstDefinition ted, AtsApi atsApi) {
      return getWorkflow(crTeamWf, ted, atsApi) != null;
   }

   public static IAtsTask getTask(IAtsTeamWorkflow crTeamWf, TaskEstDefinition ted, AtsApi atsApi) {
      for (IAtsTask task : atsApi.getTaskService().getTasks(crTeamWf)) {
         if (task.hasTag(ted.getIdString())) {
            return task;
         }
      }
      return IAtsTask.SENTINEL;
   }

   public static boolean hasTask(IAtsTeamWorkflow crTeamWf, TaskEstDefinition ted, AtsApi atsApi) {
      return getTask(crTeamWf, ted, atsApi).isValid();
   }

   public static IAtsTask getTask(IAtsTeamWorkflow teamWf, AtsApi atsApi) {
      IAtsTask foundTask = null;
      for (ArtifactToken derivedFrom : atsApi.getRelationResolver().getRelated(teamWf, AtsRelationTypes.Derive_From)) {
         if (atsApi.getAttributeResolver().hasTag(derivedFrom, TaskEstUtil.TASK_EST_STATIC_ID)) {
            if (derivedFrom.isOfType(AtsArtifactTypes.Task)) {
               foundTask = atsApi.getWorkItemService().getTask(derivedFrom);
            }
            break;
         }
      }
      return foundTask;
   }

   public static List<AtsUser> getAssignees(TaskEstDefinition ted, AtsApi atsApi) {
      List<AtsUser> assignees = new ArrayList<>();
      if (ted != null) {
         for (ArtifactId id : ted.getAssigneeAccountIds()) {
            AtsUser user = atsApi.getUserService().getUserById(id);
            if (user.isActive()) {
               assignees.add(user);
            }
         }
      }
      return assignees;
   }

   /**
    * Create dynamic TEDs from children UserGroups off given teamDef where UserGroup has TaskEst static id
    */
   public static void getTaskDefsFromUserGroupsOff(IAtsTeamDefinitionArtifactToken teamDef, List<TaskEstDefinition> taskDefs, AtsApi atsApi) {
      for (ArtifactToken childArt : atsApi.getRelationResolver().getChildren(teamDef)) {
         if (atsApi.getAttributeResolver().getAttributesToStringList(childArt, CoreAttributeTypes.StaticId).contains(
            TaskEstUtil.TASK_EST_STATIC_ID)) {
            String desc = atsApi.getAttributeResolver().getSoleAttributeValueAsString(childArt,
               CoreAttributeTypes.Description, "");
            List<ArtifactId> assigneeAccountIds = new LinkedList<>();
            for (UserToken user : atsApi.userService().getUserGroup(childArt).getMembers()) {
               assigneeAccountIds.add(ArtifactId.create(user));
            }
            ArtifactToken aiArt = atsApi.getRelationResolver().getRelatedOrSentinel(childArt,
               AtsRelationTypes.UserGroupToActionableItem_AI);
            taskDefs.add(new TaskEstDefinition(childArt.getId(), childArt.getName(), desc, assigneeAccountIds, aiArt));
         }
      }
   }

}
