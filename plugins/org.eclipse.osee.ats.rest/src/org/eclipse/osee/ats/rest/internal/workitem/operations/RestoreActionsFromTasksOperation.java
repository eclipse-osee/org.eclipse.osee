/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.rest.internal.workitem.operations;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.create.TasksFromAction;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.AtsConstants;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;

public class RestoreActionsFromTasksOperation {

   private final TasksFromAction tfa;
   private final AtsApi atsApi;

   public RestoreActionsFromTasksOperation(TasksFromAction tfa, AtsApi atsApi) {
      this.tfa = tfa;
      this.atsApi = atsApi;
   }

   public TasksFromAction run() {
      restore();
      return tfa;
   }

   private void restore() {
      IAtsChangeSet changes = atsApi.createChangeSet(AtsConstants.RestoreTasksFromActions.getName());
      for (ArtifactToken taskTok : tfa.getSourceTasks()) {
         IAtsTask task = atsApi.getTaskService().getTask(taskTok);
         Collection<ArtifactToken> related =
            atsApi.getRelationResolver().getRelated(task, AtsRelationTypes.Derive_From);
         if (related.size() == 1 && related.iterator().next().isOfType(AtsArtifactTypes.TeamWorkflow)) {
            IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(related.iterator().next());
            if (teamWf != null) {
               if (teamWf.isCancelled()) {
                  String cancelledFromState = atsApi.getAttributeResolver().getSoleAttributeValue(teamWf,
                     AtsAttributeTypes.CancelledFromState, "");
                  TransitionData tData = new TransitionData(AtsConstants.RestoreTasksFromActions.name(),
                     Arrays.asList(teamWf), cancelledFromState, Arrays.asList(AtsCoreUsers.UNASSIGNED_USER), "",
                     changes, TransitionOption.None);
                  tData.setExecute(false);
                  TransitionResults results = atsApi.getWorkItemService().transition(tData);
                  if (!results.isSuccess() || results.isErrors()) {
                     tfa.getRd().errorf(results.toString());
                     return;
                  }
                  atsApi.getActionService().addActionToConfiguredGoal(teamWf.getTeamDefinition(), teamWf,
                     teamWf.getActionableItems(), null, changes);
               }
            }
         }
      }
      if (tfa.getRd().isErrors()) {
         return;
      }
      TransactionToken tx = changes.executeIfNeeded();
      if (tx.isValid()) {
         tfa.getRd().log("Success.  Restored Action(s) from Task(s)");
         tfa.getRd().setTxId(tx.getIdString());
      } else {
         tfa.getRd().log("Nothing to Do");
      }
   }

}
