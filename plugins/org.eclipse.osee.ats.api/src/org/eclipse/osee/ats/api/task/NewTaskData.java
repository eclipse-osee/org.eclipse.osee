/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.task;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class NewTaskData {

   @JsonSerialize(using = ToStringSerializer.class)
   Long teamWfId;
   List<JaxAtsTask> newTasks = new ArrayList<>();
   Boolean fixTitles = false;
   TransactionToken transaction = TransactionToken.SENTINEL;
   AtsWorkDefinitionToken taskWorkDef = AtsWorkDefinitionToken.SENTINEL;

   public NewTaskData() {
      // for jax-rs
   }

   public Long getTeamWfId() {
      return teamWfId;
   }

   public void setTeamWfId(Long teamWfId) {
      this.teamWfId = teamWfId;
   }

   public List<JaxAtsTask> getTasks() {
      return newTasks;
   }

   public void add(JaxAtsTask jTask) {
      this.newTasks.add(jTask);
   }

   public void setNewTasks(List<JaxAtsTask> newTasks) {
      this.newTasks = newTasks;
   }

   @Override
   public String toString() {
      return "NewTaskData [teamId=" + teamWfId + ", tasks=" + newTasks + "]";
   }

   public boolean isEmpty() {
      return newTasks == null || newTasks.isEmpty();
   }

   public Boolean getFixTitles() {
      return fixTitles;
   }

   public Boolean isFixTitles() {
      return fixTitles;
   }

   public void setFixTitles(Boolean fixTitles) {
      this.fixTitles = fixTitles;
   }

   public static NewTaskData create(NewTaskSet newTaskSet, IAtsTeamWorkflow teamWf) {
      NewTaskData ntd = new NewTaskData();
      newTaskSet.add(ntd);
      ntd.setTeamWfId(teamWf.getId());
      return ntd;
   }

   public static NewTaskData create(IAtsTeamWorkflow teamWf, List<String> titles, List<AtsUser> assignees, Date createdDate, AtsUser createdBy, String relatedToState, String taskWorkDef, Map<AttributeTypeToken, List<Object>> attributes) {
      NewTaskData newTaskData = new NewTaskData();
      newTaskData.setTeamWfId(teamWf.getId());
      if (createdDate == null) {
         createdDate = new Date();
      }

      for (String title : titles) {
         JaxAtsTask task = new JaxAtsTask();
         task.setName(title);
         if (assignees != null) {
            for (AtsUser assignee : assignees) {
               task.addAssigneeUserIds(assignee.getUserId());
            }
         } else {
            task.addAssigneeUserIds(AtsCoreUsers.UNASSIGNED_USER.getUserId());
         }
         if (Strings.isValid(relatedToState)) {
            task.setRelatedToState(relatedToState);
         }
         task.setCreatedByUserId(createdBy.getUserId());
         task.setCreatedDate(createdDate);
         if (Strings.isValid(taskWorkDef)) {
            task.setWorkDef(taskWorkDef);
         }
         newTaskData.getTasks().add(task);
         if (attributes != null) {
            for (Entry<AttributeTypeToken, List<Object>> entry : attributes.entrySet()) {
               task.addAttributes(entry.getKey(), entry.getValue());
            }
         }
      }
      return newTaskData;
   }

   public TransactionToken getTransaction() {
      return transaction;
   }

   public void setTransaction(TransactionToken transaction) {
      this.transaction = transaction;
   }

   public AtsWorkDefinitionToken getTaskWorkDef() {
      return taskWorkDef;
   }

   public void setTaskWorkDef(AtsWorkDefinitionToken taskWorkDef) {
      this.taskWorkDef = taskWorkDef;
   }

}
