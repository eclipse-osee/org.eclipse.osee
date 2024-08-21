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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.TasksFromAction;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.AtsConstants;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.task.CreateTasksOperation;
import org.eclipse.osee.ats.rest.internal.workitem.operations.CreateTasksFromActionsOperation;
import org.eclipse.osee.ats.rest.internal.workitem.operations.RestoreActionsFromTasksOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskEndpointImpl implements AtsTaskEndpointApi {
   private final AtsApi atsApi;

   public AtsTaskEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public ChangeReportTaskData create(ChangeReportTaskData changeReportTaskData) {
      return atsApi.getTaskService().createTasks(changeReportTaskData);
   }

   @Override
   public NewTaskSet create(NewTaskSet newTaskSet) {
      CreateTasksOperation operation = new CreateTasksOperation(newTaskSet, atsApi);
      newTaskSet = operation.validate();
      if (newTaskSet.isErrors()) {
         return newTaskSet;
      }
      operation.run();
      return newTaskSet;
   }

   @Override
   public JaxAtsTask get(long taskId) {
      IAtsWorkItem task =
         atsApi.getQueryService().createQuery(WorkItemType.WorkItem).isOfType(WorkItemType.Task).andIds(
            taskId).getResults().getOneOrDefault(IAtsWorkItem.SENTINEL);
      if (task.getId().equals(IAtsWorkItem.SENTINEL.getId())) {
         throw new OseeArgumentException("No Task found with id %d", taskId);
      }
      JaxAtsTask jaxAtsTask = CreateTasksOperation.createNewJaxTask(task.getId(), atsApi);
      return jaxAtsTask;
   }

   @Override
   public void delete(long taskId) {
      IAtsWorkItem task =
         atsApi.getQueryService().createQuery(WorkItemType.WorkItem).isOfType(WorkItemType.Task).andIds(
            taskId).getResults().getOneOrDefault(IAtsWorkItem.SENTINEL);
      if (task.isValid()) {
         IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet("Delete Task", AtsCoreUsers.SYSTEM_USER);
         changes.deleteArtifact(task);
         changes.execute();
      }
   }

   @Override
   public TasksFromAction create(TasksFromAction tfa) {
      tfa.getRd().log(AtsConstants.CreateTasksFromActions.name() + "\n");
      CreateTasksFromActionsOperation op = new CreateTasksFromActionsOperation(tfa, atsApi);
      return op.run();
   }

   @Override
   public TasksFromAction restore(TasksFromAction tfa) {
      tfa.getRd().log(AtsConstants.CreateTasksFromActions.name() + "\n");
      RestoreActionsFromTasksOperation op = new RestoreActionsFromTasksOperation(tfa, atsApi);
      return op.run();
   }
}
