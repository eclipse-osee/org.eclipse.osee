/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.AbstractAtsTaskService;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.task.CreateChangeReportTasksOperation;
import org.eclipse.osee.ats.core.task.CreateTasksOperation;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskService extends AbstractAtsTaskService {
   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public AtsTaskService(AtsApi atsApi, OrcsApi orcsApi) {
      super(atsApi);
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   @Override
   public Collection<IAtsTask> createTasks(NewTaskData newTaskData, XResultData results) {
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet(newTaskData.getCommitComment(), AtsCoreUsers.SYSTEM_USER);
      return createTasks(newTaskData, changes, results);
   }

   @Override
   public Collection<IAtsTask> createTasks(NewTaskData newTaskData, IAtsChangeSet changes, XResultData results) {
      return createTasks(newTaskData, changes, results, new HashMap<Long, IAtsTeamWorkflow>());
   }

   /**
    * @param idToTeamWf - Map of team workflows created during operation that may not be in database yet
    */
   @Override
   public Collection<IAtsTask> createTasks(NewTaskData newTaskData, IAtsChangeSet changes, XResultData results, Map<Long, IAtsTeamWorkflow> idToTeamWf) {
      List<IAtsTask> tasks = new LinkedList<>();
      CreateTasksOperation operation = new CreateTasksOperation(newTaskData, atsApi, results);
      operation.setIdToTeamWf(idToTeamWf);
      operation.validate();
      if (results.isSuccess()) {
         operation.run(changes);
         if (results.isSuccess()) {
            for (JaxAtsTask task : operation.getTasks()) {
               tasks.add(atsApi.getWorkItemService().getTask(atsApi.getQueryService().getArtifact(task.getId())));
            }
         }
      }
      return tasks;
   }

   @Override
   public Collection<IAtsTask> createTasks(NewTaskDatas newTaskDatas) {
      CreateTasksOperation operation = new CreateTasksOperation(newTaskDatas, atsApi, new XResultData());
      XResultData results = operation.validate();

      if (results.isErrors()) {
         throw new OseeStateException("Error validating task creation - " + results.toString());
      }
      operation.run();
      if (results.isErrors()) {
         throw new OseeStateException("Error creating tasks - " + results.toString());
      }
      List<IAtsTask> tasks = new LinkedList<>();
      for (JaxAtsTask task : operation.getTasks()) {
         tasks.add(atsApi.getWorkItemService().getTask(atsApi.getQueryService().getArtifact(task.getId())));
      }
      return tasks;
   }

   @Override
   public Collection<IAtsTask> createTasks(IAtsTeamWorkflow teamWf, List<String> titles, List<IAtsUser> assignees, Date createdDate, IAtsUser createdBy, String relatedToState, String taskWorkDef, Map<String, List<Object>> attributes, IAtsChangeSet changes) {
      NewTaskData tasks = atsApi.getTaskService().getNewTaskData(teamWf, titles, assignees, createdDate, createdBy,
         relatedToState, taskWorkDef, attributes, changes.getComment());
      return createTasks(tasks, changes, new XResultData());
   }

   @Override
   public ChangeReportTaskData createTasks(ChangeReportTaskData changeReportTaskData) {
      CreateChangeReportTasksOperation operation =
         new CreateChangeReportTasksOperation(changeReportTaskData, atsApi, null);
      return operation.run();
   }

   @Override
   public ChangeReportTaskData createTasks(ArtifactToken hostTeamWf, AtsTaskDefToken taskDefToken, ArtifactToken asUser) {
      ChangeReportTaskData data = new ChangeReportTaskData();
      data.setTaskDefToken(taskDefToken);
      data.setHostTeamWf(hostTeamWf);
      AtsUser atsUser = (AtsUser) atsApi.getUserService().getUserByAccountId(asUser.getId());
      data.setAsUser(atsUser);
      return createTasks(data);
   }

}
