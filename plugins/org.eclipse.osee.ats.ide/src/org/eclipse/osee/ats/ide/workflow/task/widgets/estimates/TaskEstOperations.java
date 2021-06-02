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
package org.eclipse.osee.ats.ide.workflow.task.widgets.estimates;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TaskEstOperations {

   private final AtsApi atsApi;
   private final AtsWorkDefinitionToken taskWorkDef;
   private final TaskEstNameProvider nameProvider;

   public TaskEstOperations(AtsApi atsApi, AtsWorkDefinitionToken taskWorkDef, TaskEstNameProvider nameProvider) {
      this.atsApi = atsApi;
      this.taskWorkDef = taskWorkDef;
      this.nameProvider = nameProvider;
   }

   public NewTaskSet createCannedTasks(IAtsTeamWorkflow teamWf, Collection<TaskEstDefinition> items) {
      NewTaskSet newTaskSet = NewTaskSet.create("Create Task(s)", atsApi.getUserService().getCurrentUserId());
      NewTaskData newTaskData = NewTaskData.create(newTaskSet, teamWf);
      for (TaskEstDefinition ted : items) {
         if (ted.isChecked() && ted.getTask() == null) {
            newTaskData.setTaskWorkDef(taskWorkDef);
            JaxAtsTask task = new JaxAtsTask();
            String name = nameProvider.getTaskName(ted);
            task.setName(name);
            task.addAttribute(CoreAttributeTypes.StaticId, ted.getId());
            if (Strings.isValid(ted.getDescription())) {
               task.addAttribute(AtsAttributeTypes.Description, ted.getDescription());
            }
            task.setCreatedByUserId(atsApi.getUserService().getCurrentUserId());
            task.setCreatedDate(new Date());
            task.setAssigneeAccountIds(ted.getAssigneeAccountIds());
            newTaskData.add(task);
         }
      }
      newTaskSet = atsApi.getTaskService().createTasks(newTaskSet);
      return newTaskSet;
   }

}
