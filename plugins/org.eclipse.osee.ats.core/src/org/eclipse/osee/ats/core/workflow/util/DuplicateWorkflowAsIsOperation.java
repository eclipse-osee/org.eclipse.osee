/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.core.workflow.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Duplicate Workflow including all fields and, states.
 *
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowAsIsOperation extends AbstractDuplicateWorkflowOperation {

   private final boolean duplicateTasks;
   private List<AttributeTypeId> excludeTypes;
   private static String ATS_CONFIG_EXCLUDE_DUPLICATE_TYPE_IDS_KEY =
      "DuplicateWorkflowAsIsOperation_ExcludeAttrTypeIds";
   private final String comment;
   private final Collection<IDuplicateWorkflowListener> duplicateListeners =
      new ArrayList<IDuplicateWorkflowListener>();
   private final boolean newAction;
   private AtsUser originator;
   private String description;
   private Collection<AtsUser> assigneesOverride;
   private String changeType;
   private String priority;
   private String points;

   public DuplicateWorkflowAsIsOperation(Collection<IAtsTeamWorkflow> teamWfs, boolean duplicateTasks, String title, AtsUser asUser, AtsApi atsApi) {
      this(teamWfs, duplicateTasks, title, asUser, atsApi, "", false, null);
   }

   public DuplicateWorkflowAsIsOperation(Collection<IAtsTeamWorkflow> teamWfs, boolean duplicateTasks, String title, AtsUser asUser, AtsApi atsApi, String comment, boolean newAction, Collection<IDuplicateWorkflowListener> listeners) {
      super(teamWfs, title, asUser, atsApi);
      this.duplicateTasks = duplicateTasks;
      this.comment = comment;
      if (listeners != null) {
         this.duplicateListeners.addAll(listeners);
      }
      this.newAction = newAction;
   }

   public DuplicateWorkflowAsIsOperation(List<IAtsTeamWorkflow> asList, boolean duplicateTasks, String existingName, String newName, AtsUser currentUser, AtsApi atsApi) {
      this(asList, duplicateTasks, newName, currentUser, atsApi, "", false, null);
   }

   @Override
   public XResultData run() {
      XResultData results = validate();
      if (results.isErrors()) {
         return results;
      }
      oldToNewMap = new HashMap<>();

      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet(
         Strings.isValid(comment) ? comment : "Duplicate Workflow - As-Is", asUser);

      for (IAtsTeamWorkflow teamWf : teamWfs) {

         IAtsWorkItem newTeamWf = duplicateWorkItem(changes, teamWf);

         // add notification for originator, assigned and subscribed
         changes.addWorkItemNotificationEvent(AtsNotificationEventFactory.getWorkItemNotificationEvent(asUser,
            newTeamWf, AtsNotifyType.Originator, AtsNotifyType.Assigned, AtsNotifyType.SubscribedTeamOrAi));

         if (duplicateTasks) {
            for (IAtsTask task : atsApi.getTaskService().getTask(teamWf)) {
               IAtsTask dupTaskArt = (IAtsTask) duplicateWorkItem(changes, task);
               dupTaskArt.getLog().addLog(LogType.Note, null, "Task duplicated from " + task.getAtsId(),
                  atsApi.getUserService().getCurrentUser().getUserId());
               changes.relate(newTeamWf.getStoreObject(), AtsRelationTypes.TeamWfToTask_Task, dupTaskArt);
               // for tasks, add notification for subscribed only
               changes.addWorkItemNotificationEvent(AtsNotificationEventFactory.getWorkItemNotificationEvent(asUser,
                  dupTaskArt, AtsNotifyType.SubscribedTeamOrAi));
               changes.add(dupTaskArt);
            }
         }

         oldToNewMap.put(teamWf, (IAtsTeamWorkflow) newTeamWf);

      }

      changes.execute();
      return results;
   }

   private IAtsWorkItem duplicateWorkItem(IAtsChangeSet changes, IAtsWorkItem workItem) {
      ArtifactToken newWorkItemArt = changes.createArtifact(
         atsApi.getStoreService().getArtifactType(workItem.getStoreObject()), getTitle(workItem));

      // If not creating new action art, add new workflow to this action
      if (workItem.isTeamWorkflow() && !newAction) {
         changes.relate(newWorkItemArt, AtsRelationTypes.ActionToWorkflow_Action, workItem.getParentAction());
      }
      IAtsLog atsLog = atsApi.getLogFactory().getLogLoaded(workItem, atsApi.getAttributeResolver());
      atsLog.addLog(LogType.Note, null, "Workflow duplicated from " + workItem.getAtsId(), asUser.getUserId());

      // assignees == add in existing assignees, leads and originator (current user)
      List<AtsUser> assignees = new LinkedList<>();
      if (assigneesOverride != null) {
         if (assigneesOverride.isEmpty()) {
            assignees.addAll(assigneesOverride);
         } else {
            assignees.add(AtsCoreUsers.UNASSIGNED_USER);
         }
      } else {
         assignees.addAll(workItem.getStateMgr().getAssignees());
      }
      if (workItem.isTeamWorkflow()) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
         if (assigneesOverride == null) {
            assignees.addAll(AtsApiService.get().getTeamDefinitionService().getLeads(
               ((IAtsTeamWorkflow) workItem).getTeamDefinition()));
            if (!assignees.contains(asUser)) {
               assignees.add(asUser);
            }
         }
         // Auto-add actions to configured goals
         if (newWorkItemArt instanceof IAtsTeamWorkflow) {
            IAtsGoal goal = null;
            for (IDuplicateWorkflowListener listener : duplicateListeners) {
               goal = listener.addToGoal((IAtsTeamWorkflow) newWorkItemArt, changes);
               listener.handleChanges((IAtsTeamWorkflow) newWorkItemArt, changes);
            }
            atsApi.getActionFactory().addActionToConfiguredGoal(teamWf.getTeamDefinition(),
               (IAtsTeamWorkflow) newWorkItemArt, teamWf.getActionableItems(), goal, changes);
         }
      }
      workItem.getStateMgr().setAssignees(assignees);

      for (IAttribute<Object> attr : atsApi.getAttributeResolver().getAttributes(workItem.getStoreObject())) {
         if (!getExcludeTypes().contains(attr.getAttributeType())) {
            if (attr.getAttributeType().equals(AtsAttributeTypes.Points) || attr.getAttributeType().equals(
               AtsAttributeTypes.PointsNumeric)) {
               if (Strings.isValid(points)) {
                  changes.setSoleAttributeValue(newWorkItemArt, attr.getAttributeType(), points);
               } else {
                  changes.addAttribute(newWorkItemArt, attr.getAttributeType(), attr.getValue());
               }
            } else if (attr.getAttributeType().equals(AtsAttributeTypes.Priority)) {
               if (Strings.isValid(priority)) {
                  changes.setSoleAttributeValue(newWorkItemArt, AtsAttributeTypes.Priority, priority);
               } else {
                  changes.addAttribute(newWorkItemArt, attr.getAttributeType(), attr.getValue());
               }
            } else if (attr.getAttributeType().equals(AtsAttributeTypes.ChangeType)) {
               if (Strings.isValid(changeType)) {
                  changes.setSoleAttributeValue(newWorkItemArt, AtsAttributeTypes.ChangeType, changeType);
               } else {
                  changes.addAttribute(newWorkItemArt, attr.getAttributeType(), attr.getValue());
               }
            } else if (attr.getAttributeType().equals(AtsAttributeTypes.CreatedBy)) {
               if (originator != null) {
                  changes.setSoleAttributeValue(newWorkItemArt, AtsAttributeTypes.CreatedBy, originator.getUserId());
               } else {
                  changes.setSoleAttributeValue(newWorkItemArt, AtsAttributeTypes.CreatedBy,
                     atsApi.getUserService().getCurrentUser().getUserId());
               }
            } else if (attr.getAttributeType().equals(AtsAttributeTypes.CreatedDate)) {
               changes.setSoleAttributeValue(newWorkItemArt, AtsAttributeTypes.CreatedDate, new Date());
            } else if (attr.getAttributeType().equals(AtsAttributeTypes.Description) && Strings.isValid(description)) {
               changes.setSoleAttributeValue(newWorkItemArt, AtsAttributeTypes.Description, description);
            } else {
               changes.addAttribute(newWorkItemArt, attr.getAttributeType(), attr.getValue());
            }
         }
      }
      IAtsWorkItem newWorkItem = atsApi.getWorkItemService().getWorkItem(newWorkItemArt);

      // If action created, set values off original action
      if (newAction) {
         IAtsAction origAction = workItem.getParentAction();
         ChangeType changeType = ChangeTypeUtil.getChangeType(origAction, atsApi);
         String priority =
            atsApi.getAttributeResolver().getSoleAttributeValue(origAction, AtsAttributeTypes.Priority, "");
         boolean validationRequired = atsApi.getAttributeResolver().getSoleAttributeValue(origAction,
            AtsAttributeTypes.ValidationRequired, false);
         Date needByDate =
            atsApi.getAttributeResolver().getSoleAttributeValue(origAction, AtsAttributeTypes.NeedBy, null);
         IAtsAction newAction = atsApi.getActionFactory().createAction(comment, description, changeType, priority,
            validationRequired, needByDate, changes);
         changes.relate(newWorkItemArt, AtsRelationTypes.ActionToWorkflow_Action, newAction);
      }

      atsApi.getActionFactory().setAtsId(newWorkItem, workItem.getParentTeamWorkflow().getTeamDefinition(), null,
         changes);
      return newWorkItem;
   }

   private List<AttributeTypeId> getExcludeTypes() {
      if (excludeTypes == null) {
         excludeTypes = new LinkedList<>();
         excludeTypes.add(AtsAttributeTypes.AtsId);
         excludeTypes.add(CoreAttributeTypes.Name);
         String value = atsApi.getConfigValue(ATS_CONFIG_EXCLUDE_DUPLICATE_TYPE_IDS_KEY);
         if (Strings.isValid(value)) {
            for (String attrTypeId : value.split(";")) {
               if (Strings.isNumeric(attrTypeId)) {
                  AttributeTypeId attributeType = AttributeTypeId.valueOf(attrTypeId);
                  if (attributeType != null) {
                     excludeTypes.add(attributeType);
                  } else {
                     OseeLog.log(DuplicateWorkflowAsIsOperation.class, Level.SEVERE,
                        String.format("Can't resolve Attribute Type for id %s in AtsConfig.%s", attrTypeId,
                           ATS_CONFIG_EXCLUDE_DUPLICATE_TYPE_IDS_KEY));
                  }
               } else {
                  OseeLog.log(DuplicateWorkflowAsIsOperation.class, Level.SEVERE,
                     String.format("Can't resolve non-numeric Attribute Type for id %s in AtsConfig.%s", attrTypeId,
                        ATS_CONFIG_EXCLUDE_DUPLICATE_TYPE_IDS_KEY));
               }
            }
         }
      }
      return excludeTypes;
   }

   public AtsUser getOriginator() {
      return originator;
   }

   public void setOriginator(AtsUser originator) {
      this.originator = originator;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setAssignees(Collection<AtsUser> assignees) {
      this.assigneesOverride = assignees;
   }

   public void setChangeType(String changeType) {
      this.changeType = changeType;
   }

   public void setPriority(String priority) {
      this.priority = priority;
   }

   public void setPoints(String points) {
      this.points = points;
   }

}
