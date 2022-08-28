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
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * Contributed through StateDefBuilder
 *
 * @author Donald G. Dunne
 */
public class CreateChangeReportTaskCommitHook implements IAtsWorkItemHook {

   private final AtsTaskDefToken taskDefToken;

   public CreateChangeReportTaskCommitHook(AtsTaskDefToken taskDefToken) {
      this.taskDefToken = taskDefToken;
   }

   @Override
   public void committed(IAtsTeamWorkflow teamWf, XResultData rd) {
      AtsApi atsApi = AtsApiService.get();

      if (teamWf.getTags().contains(ChangeReportTasksUtil.FINAL_TASK_GEN_TAG)) {
         rd.log(ChangeReportTasksUtil.FINAL_TASK_GEN_MSG);
         return;
      }

      Collection<BranchToken> branchesCommittedTo = atsApi.getBranchService().getBranchesCommittedTo(teamWf);
      if (branchesCommittedTo.size() != 1) {
         return;
      }
      Thread thread = new Thread("Create/Update Tasks on Commit") {
         @Override
         public void run() {
            // Multiple TaskSetDefinitions can be registered for a transition; ensure applicable before running
            CreateTasksDefinitionBuilder taskSetDefinition =
               AtsApiService.get().getTaskSetDefinitionProviderService().getTaskSetDefinition(taskDefToken);
            if (taskSetDefinition != null && taskSetDefinition.getCreateTasksDef().getHelper().isApplicable(teamWf,
               AtsApiService.get())) {
               IAtsChangeSet changes = atsApi.createChangeSet(getName());
               ChangeReportTaskData data = runChangeReportTaskOperation(teamWf, taskDefToken, true, changes,
                  atsApi.getUserService().getCurrentUser());
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

   public static ChangeReportTaskData runChangeReportTaskOperation(IAtsWorkItem workItem, AtsTaskDefToken taskDefToken, boolean finalTaskGen, IAtsChangeSet changes, AtsUser asUser) {
      ChangeReportTaskData data = new ChangeReportTaskData();
      data.setTaskDefToken(taskDefToken);
      data.setHostTeamWf(workItem.getStoreObject());
      data.setAsUser(asUser);
      data.setFinalTaskGen(finalTaskGen);

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
      return "Checks for and runs Change Report Task Set Definitions after first commit";
   }

}
