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

package org.eclipse.osee.ats.rest.internal.workitem;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskNameProviderToken;
import org.eclipse.osee.ats.api.task.create.IAtsChangeReportTaskNameProvider;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.task.AbstractAtsTaskServiceCore;
import org.eclipse.osee.ats.core.task.ChangeReportTaskNameProviderService;
import org.eclipse.osee.ats.core.task.CreateChangeReportTasksOperation;
import org.eclipse.osee.ats.core.task.CreateTasksOperation;
import org.eclipse.osee.ats.core.workflow.Task;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskService extends AbstractAtsTaskServiceCore {

   public AtsTaskService(AtsApi atsApi) {
      super(atsApi);
   }

   /**
    * @param idToTeamWf - Map of team workflows created during operation that may not be in database yet
    */
   @Override
   public NewTaskSet createTasks(NewTaskSet newTaskSet, Map<Long, IAtsTeamWorkflow> idToTeamWf) {
      List<IAtsTask> tasks = new LinkedList<>();
      CreateTasksOperation operation = new CreateTasksOperation(newTaskSet, atsApi);
      operation.setIdToTeamWf(idToTeamWf);
      NewTaskSet taskSet = operation.validate();
      if (taskSet.getResults().isSuccess()) {

         IAtsChangeSet changes = atsApi.createChangeSet(newTaskSet.getCommitComment());
         operation.run(changes);
         if (newTaskSet.getResults().isSuccess()) {
            for (JaxAtsTask task : operation.getTasks()) {
               tasks.add(atsApi.getWorkItemService().getTask(atsApi.getQueryService().getArtifact(task.getId())));
            }
         }
      }
      return taskSet;
   }

   @Override
   public NewTaskSet createTasks(NewTaskSet newTaskSet) {
      CreateTasksOperation operation = new CreateTasksOperation(newTaskSet, atsApi);
      operation.validate();

      if (newTaskSet.getResults().isErrors()) {
         throw new OseeStateException("Error validating task creation - " + newTaskSet.getResults().toString());
      }
      operation.run();
      if (newTaskSet.getResults().isErrors()) {
         throw new OseeStateException("Error creating tasks - " + newTaskSet.toString());
      }
      List<IAtsTask> tasks = new LinkedList<>();
      for (JaxAtsTask task : operation.getTasks()) {
         tasks.add(atsApi.getWorkItemService().getTask(atsApi.getQueryService().getArtifact(task.getId())));
      }
      return newTaskSet;
   }

   @Override
   public ChangeReportTaskData createTasks(ChangeReportTaskData changeReportTaskData) {
      CreateChangeReportTasksOperation operation =
         new CreateChangeReportTasksOperation(changeReportTaskData, atsApi, null);
      return operation.run();
   }

   @Override
   public IAtsChangeReportTaskNameProvider getChangeReportOptionNameProvider(ChangeReportTaskNameProviderToken token) {
      return ChangeReportTaskNameProviderService.getChangeReportOptionNameProvider(token);
   }

   @Override
   public IAtsTask getTask(ArtifactToken artifact) {
      if (!artifact.isOfType(AtsArtifactTypes.Task)) {
         throw new OseeArgumentException("Artifact %s must be of type Task", artifact.toStringWithId());
      }
      return new Task(atsApi.getLogger(), atsApi, artifact);
   }

}
