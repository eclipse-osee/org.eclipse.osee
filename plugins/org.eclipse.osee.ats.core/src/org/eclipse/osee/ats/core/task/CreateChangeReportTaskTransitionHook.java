/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * Contributed through StateDefBuilder
 *
 * @author Donald G. Dunne
 */
public class CreateChangeReportTaskTransitionHook implements IAtsTransitionHook {

   private final AtsTaskDefToken taskDefToken;

   public CreateChangeReportTaskTransitionHook(AtsTaskDefToken taskDefToken) {
      this.taskDefToken = taskDefToken;
   }

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState,
      Collection<AtsUser> toAssignees, AtsUser asUser, IAtsChangeSet changes, AtsApi atsApi) {
      if (!workItem.isTeamWorkflow()) {
         return;
      }
      if (!toState.isCompleted()) {
         return;
      }
      Thread thread = new Thread("Create/Update Tasks") {
         @Override
         public void run() {
            // Multiple TaskSetDefinitions can be registered for a transition; ensure applicable before running
            CreateTasksDefinitionBuilder taskSetDefinition =
               atsApi.getTaskSetDefinitionProviderService().getTaskSetDefinition(taskDefToken);
            if (taskSetDefinition != null && taskSetDefinition.getCreateTasksDef().getHelper().isApplicable(workItem,
               atsApi)) {
               ChangeReportTaskData data = runChangeReportTaskOperation(workItem, taskDefToken, true, changes, asUser);
               if (data.getResults().isErrors()) {
                  throw new OseeArgumentException(data.getResults().toString());
               }
            }
         }
      };
      if (AtsUtil.isInTest()) {
         thread.run();
      } else {
         thread.start();
      }
   }

   public static ChangeReportTaskData runChangeReportTaskOperation(IAtsWorkItem workItem, AtsTaskDefToken taskDefToken,
      boolean finalTaskGen, IAtsChangeSet changes, AtsUser asUser) {
      ChangeReportTaskData data = new ChangeReportTaskData();
      data.setTaskDefToken(taskDefToken);
      data.setHostTeamWf(workItem.getStoreObject());
      data.setAsUser(asUser);
      data.setFinalTaskGen(finalTaskGen);
      data.setCommitComment(CreateChangeReportTaskTransitionHook.class.getSimpleName());

      /**
       * Until all transitions are done on server, need to directly call this operation so it's part of the full
       * IAtsChangeSet. Otherwise transitioning will reload teamWfs and tasks after task creation.
       */
      CreateChangeReportTasksOperation operation =
         new CreateChangeReportTasksOperation(data, AtsApiService.get(), changes);
      operation.run();

      return data;
   }

   @Override
   public String getDescription() {
      return "Checks for and runs Change Report Task Set Definitions during tranisition";
   }

}
