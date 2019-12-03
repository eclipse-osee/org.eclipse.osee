/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.create.CreateTaskDefinition;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CreateTasksRuleRunner {

   private final IAtsTeamWorkflow teamWf;
   private final AtsApi atsApi;
   private XResultData results;
   private final List<CreateTasksDefinition> createTasksDefs;

   public CreateTasksRuleRunner(IAtsTeamWorkflow teamWf, CreateTasksDefinition createTasksDef, AtsApi atsApi) {
      this(teamWf, Arrays.asList(createTasksDef), atsApi);
   }

   public CreateTasksRuleRunner(IAtsTeamWorkflow teamWf, List<CreateTasksDefinition> createTasksDefs, AtsApi atsApi) {
      this.teamWf = teamWf;
      this.createTasksDefs = createTasksDefs;
      this.atsApi = atsApi;
   }

   public XResultData run() {
      results = new XResultData();
      for (CreateTasksDefinition tasksDef : createTasksDefs) {
         if (tasksDef.getStaticTaskDefs().isEmpty()) {
            results.error("StaticTaskDefs can not be empty");
            return results;
         }
         if (Strings.isInValid(tasksDef.getComment())) {
            results.error("Comment can not be null or empty");
            return results;
         }
         List<String> existingTaskNames = getExistingTaskNames();
         List<CreateTaskDefinition> missingTasks = getMissingTasks(tasksDef, existingTaskNames);
         createMissingTasks(tasksDef, missingTasks, results);
      }
      return results;
   }

   private void createMissingTasks(CreateTasksDefinition tasksDef, List<CreateTaskDefinition> missingTasks, XResultData results) {
      NewTaskData newTaskData = new NewTaskData();
      newTaskData.setTeamWfId(teamWf.getId());
      newTaskData.setAsUserId(atsApi.getUserService().getCurrentUserId());
      newTaskData.setCommitComment(tasksDef.getComment());
      Date createdDate = new Date();
      for (CreateTaskDefinition createTaskDef : missingTasks) {
         JaxAtsTask jTask = new JaxAtsTask();
         jTask.setName(createTaskDef.getTitle());
         jTask.setCreatedByUserId(atsApi.getUserService().getCurrentUserId());
         jTask.setDescription(createTaskDef.getDescription());
         jTask.setRelatedToState(createTaskDef.getRelatedToState());
         jTask.setCreatedDate(createdDate);
         for (Long accountId : createTaskDef.getAssigneeAccountIds()) {
            jTask.getAssigneeAccountIds().add(ArtifactId.valueOf(accountId));
         }
         if (createTaskDef.getWorkDefTok().isValid()) {
            IAtsWorkDefinition workDef =
               atsApi.getWorkDefinitionService().getWorkDefinition(createTaskDef.getWorkDefTok());
            if (workDef != null) {
               jTask.setTaskWorkDef(workDef.getIdString());
            }
         }
         newTaskData.getNewTasks().add(jTask);
      }
      for (IAtsTask task : atsApi.getTaskService().createTasks(newTaskData, results)) {
         results.getIds().add(task.getIdString());
      }
   }

   private List<CreateTaskDefinition> getMissingTasks(CreateTasksDefinition tasksDef, List<String> existingTaskNames) {
      List<CreateTaskDefinition> missingTasks = new ArrayList<>();
      for (CreateTaskDefinition createTaskDef : tasksDef.getStaticTaskDefs()) {
         if (!existingTaskNames.contains(createTaskDef.getTitle())) {
            missingTasks.add(createTaskDef);
         }
      }
      return missingTasks;
   }

   private List<String> getExistingTaskNames() {
      List<String> taskNames = new ArrayList<String>();
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWf)) {
         taskNames.add(task.getName());
      }
      return taskNames;
   }
}
