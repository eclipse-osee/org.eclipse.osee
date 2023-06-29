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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.task;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskStates;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public class TaskTestUtil {

   private TaskTestUtil() {
      // utility class
   }

   public static Result transitionToCompleted(TaskArtifact taskArt, double estimatedHours, double additionalHours) {
      if (taskArt.isInState(TeamState.Completed)) {
         return Result.TrueResult;
      }
      // Assign current user if unassigned
      if (taskArt.isUnAssigned()) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Transition");
         changes.setAssignee(taskArt, AtsApiService.get().getUserService().getCurrentUser());
         changes.executeIfNeeded();
      }
      if (estimatedHours > 0.0) {
         taskArt.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, estimatedHours);
      }
      TransitionData transData = new TransitionData("Transition to Completed", Arrays.asList(taskArt),
         TaskStates.Completed.getName(), null, null, null);
      transData.addTransitionHook(new IAtsTransitionHook() {

         @Override
         public String getDescription() {
            return "";
         }

         @Override
         public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState,
            Collection<AtsUser> toAssignees, AtsUser asUser, IAtsChangeSet changes, AtsApi atsApi) {
            AtsApiService.get().getWorkItemMetricsService().updateMetrics(taskArt, taskArt.getStateDefinition(),
               additionalHours, 100, true, AtsApiService.get().getUserService().getCurrentUser(), changes);
         }
      });
      TransitionResults results = AtsApiService.get().getWorkItemServiceIde().transition(transData);

      if (results.isEmpty()) {
         return Result.TrueResult;
      }
      return new Result("Transition Error %s", results.toString());
   }

   public static Result transitionToInWork(TaskArtifact taskArt, AtsUser toUser, int percentComplete,
      double additionalHours) {
      if (taskArt.isInState(TaskStates.InWork)) {
         return Result.TrueResult;
      }
      TransitionData transData = new TransitionData("Transition to InWork", Arrays.asList(taskArt),
         TaskStates.InWork.getName(), Arrays.asList(toUser), null, null, TransitionOption.OverrideAssigneeCheck);
      transData.addTransitionHook(new IAtsTransitionHook() {

         @Override
         public String getDescription() {
            return "";
         }

         @Override
         public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState,
            Collection<AtsUser> toAssignees, AtsUser asUser, IAtsChangeSet changes, AtsApi atsApi) {
            if (AtsApiService.get().getWorkItemMetricsService().getPercentComplete(
               taskArt) != percentComplete || additionalHours > 0) {
               AtsApiService.get().getWorkItemMetricsService().updateMetrics(taskArt, fromState, additionalHours,
                  percentComplete, true, AtsApiService.get().getUserService().getCurrentUser(), changes);
            }
         }
      });
      TransitionResults results = AtsApiService.get().getWorkItemServiceIde().transition(transData);
      if (!results.isEmpty()) {
         return new Result("Transition Error %s", results.toString());
      }
      return Result.TrueResult;
   }

}
