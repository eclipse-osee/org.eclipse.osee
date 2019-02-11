/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow.util;

import java.util.Collection;
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
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.util.AtsCoreFactory;
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

   public DuplicateWorkflowAsIsOperation(Collection<IAtsTeamWorkflow> teamWfs, boolean duplicateTasks, String title, IAtsUser asUser, AtsApi atsApi) {
      super(teamWfs, title, asUser, atsApi);
      this.duplicateTasks = duplicateTasks;
   }

   @Override
   public XResultData run() {
      XResultData results = validate();
      if (results.isErrors()) {
         return results;
      }
      oldToNewMap = new HashMap<>();

      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet("Duplicate Workflow - As-Is", asUser);

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

      if (workItem.isTeamWorkflow()) {
         changes.relate(newWorkItemArt, AtsRelationTypes.ActionToWorkflow_Action, workItem.getParentAction());
      }
      IAtsLog atsLog = AtsCoreFactory.getLogFactory().getLogLoaded(workItem, atsApi.getAttributeResolver());
      atsLog.addLog(LogType.Note, null, "Workflow duplicated from " + workItem.getAtsId(), asUser.getUserId());

      // assignees == add in existing assignees, leads and originator (current user)
      List<IAtsUser> assignees = new LinkedList<>();
      assignees.addAll(workItem.getStateMgr().getAssignees());
      if (workItem.isTeamWorkflow()) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
         assignees.addAll(((IAtsTeamWorkflow) workItem).getTeamDefinition().getLeads());
         if (!assignees.contains(asUser)) {
            assignees.add(asUser);
         }
         // Auto-add actions to configured goals
         atsApi.getActionFactory().addActionToConfiguredGoal(teamWf.getTeamDefinition(), teamWf,
            teamWf.getActionableItems(), changes);
      }

      for (IAttribute<Object> attr : atsApi.getAttributeResolver().getAttributes(workItem.getStoreObject())) {
         if (!getExcludeTypes().contains(attr.getAttributeType())) {
            changes.addAttribute(newWorkItemArt, attr.getAttributeType(), attr.getValue());
         }
      }
      IAtsWorkItem newWorkItem = atsApi.getWorkItemService().getWorkItem(newWorkItemArt);
      atsApi.getActionFactory().setAtsId(newWorkItem, workItem.getParentTeamWorkflow().getTeamDefinition(), changes);
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

}
