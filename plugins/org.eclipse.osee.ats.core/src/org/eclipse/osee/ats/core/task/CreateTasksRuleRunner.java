/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinition;
import org.eclipse.osee.ats.api.task.create.StaticTaskDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Named;
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
         List<StaticTaskDefinition> missingTasks = getMissingTasks(tasksDef, existingTaskNames);
         createMissingTasks(teamWf, tasksDef, missingTasks, results);
      }
      return results;
   }

   private void createMissingTasks(IAtsTeamWorkflow teamWf, CreateTasksDefinition tasksDef, List<StaticTaskDefinition> missingTasks, XResultData results) {
      NewTaskSet newTaskSet = NewTaskSet.create("CreateTasksRuleRunner", atsApi.getUserService().getCurrentUserId());
      NewTaskData newTaskData = NewTaskData.create(newTaskSet, teamWf);
      Date createdDate = new Date();
      for (StaticTaskDefinition createTaskDef : missingTasks) {
         JaxAtsTask jTask = new JaxAtsTask();
         newTaskData.getTasks().add(jTask);
         jTask.setName(createTaskDef.getName());
         jTask.setCreatedByUserId(atsApi.getUserService().getCurrentUserId());
         jTask.setDescription(createTaskDef.getDescription());
         jTask.setRelatedToState(createTaskDef.getRelatedToState());
         jTask.setCreatedDate(createdDate);
         for (Long accountId : createTaskDef.getAssigneeAccountIds()) {
            jTask.getAssigneeAccountIds().add(ArtifactId.valueOf(accountId));
         }
         if (createTaskDef.getWorkDefTok().isValid()) {
            WorkDefinition workDef =
               atsApi.getWorkDefinitionService().getWorkDefinition(createTaskDef.getWorkDefTok());
            if (workDef != null) {
               jTask.setWorkDef(workDef.getIdString());
            }
         }
      }
      for (NewTaskData ntd : atsApi.getTaskService().createTasks(newTaskSet).getNewTaskDatas()) {
         for (JaxAtsTask task : ntd.getTasks()) {
            results.getIds().add(task.getIdString());
         }
      }
   }

   private List<StaticTaskDefinition> getMissingTasks(CreateTasksDefinition tasksDef, List<String> existingTaskNames) {
      List<StaticTaskDefinition> missingTasks = new ArrayList<>();
      for (StaticTaskDefinition createTaskDef : tasksDef.getStaticTaskDefs()) {
         if (!existingTaskNames.contains(createTaskDef.getName())) {
            missingTasks.add(createTaskDef);
         }
      }
      return missingTasks;
   }

   private List<String> getExistingTaskNames() {
      return Named.getNames(atsApi.getTaskService().getTasks(teamWf));
   }
}