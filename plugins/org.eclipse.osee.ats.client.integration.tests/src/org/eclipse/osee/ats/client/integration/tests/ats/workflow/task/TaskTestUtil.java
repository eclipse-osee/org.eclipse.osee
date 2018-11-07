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
package org.eclipse.osee.ats.client.integration.tests.ats.workflow.task;

import java.util.Arrays;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.workflow.task.TaskStates;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public class TaskTestUtil {

   private TaskTestUtil() {
      // utility class
   }

   public static Result transitionToCompleted(TaskArtifact taskArt, double estimatedHours, double additionalHours, IAtsChangeSet changes) {
      if (taskArt.isInState(TeamState.Completed)) {
         return Result.TrueResult;
      }
      // Assign current user if unassigned
      if (taskArt.getStateMgr().isUnAssigned()) {
         taskArt.getStateMgr().setAssignee(AtsClientService.get().getUserService().getCurrentUser());
      }
      taskArt.getStateMgr().updateMetrics(taskArt.getStateDefinition(), additionalHours, 100, true,
         AtsClientService.get().getUserService().getCurrentUser());
      if (estimatedHours > 0.0) {
         taskArt.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, estimatedHours);
      }
      TransitionHelper helper = new TransitionHelper("Transition to Completed", Arrays.asList(taskArt),
         TaskStates.Completed.getName(), null, null, changes, AtsClientService.get().getServices());
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();

      if (results.isEmpty()) {
         return Result.TrueResult;
      }
      return new Result("Transition Error %s", results.toString());
   }

   public static Result transitionToInWork(TaskArtifact taskArt, IAtsUser toUser, int percentComplete, double additionalHours, IAtsChangeSet changes) {
      if (taskArt.isInState(TaskStates.InWork)) {
         return Result.TrueResult;
      }
      TransitionHelper helper = new TransitionHelper("Transition to InWork", Arrays.asList(taskArt),
         TaskStates.InWork.getName(), Arrays.asList(toUser), null, changes, AtsClientService.get().getServices(),
         TransitionOption.OverrideAssigneeCheck);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      if (!results.isEmpty()) {
         return new Result("Transition Error %s", results.toString());
      }
      if (taskArt.getStateMgr().getPercentComplete(
         taskArt.getCurrentStateName()) != percentComplete || additionalHours > 0) {
         taskArt.getStateMgr().updateMetrics(taskArt.getStateDefinition(), additionalHours, percentComplete, true,
            AtsClientService.get().getUserService().getCurrentUser());
      }
      if (changes != null) {
         taskArt.save(changes);
      }
      return Result.TrueResult;
   }

}
