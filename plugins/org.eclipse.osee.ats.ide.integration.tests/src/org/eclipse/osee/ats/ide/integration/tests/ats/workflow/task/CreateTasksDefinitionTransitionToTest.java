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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.ide.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CreateTasksDefinitionTransitionToTest {

   private static AtsTaskDefToken ImplementTaskSetToken =
      AtsTaskDefToken.valueOf(23492840234L, "Create Implement Tasks");
   private static IAtsTeamWorkflow teamWf;

   @AfterClass
   public static void cleanup() {
      IAtsChangeSet changes =
         AtsApiService.get().createChangeSet(CreateTasksDefinitionTransitionToTest.class.getSimpleName());
      if (teamWf != null) {
         for (IAtsTask task : AtsApiService.get().getTaskService().getTasks(teamWf)) {
            changes.deleteArtifact(task);
         }
         for (IAtsAbstractReview review : AtsApiService.get().getReviewService().getReviews(teamWf)) {
            changes.deleteArtifact(review);
         }
         changes.deleteArtifact(teamWf);
      }
      changes.executeIfNeeded();
   }

   @Test
   public void testTransitionTo() {

      CreateTasksDefinitionBuilder implementTaskSet =
         AtsApiService.get().getTaskService().createTasksSetDefinitionBuilder(ImplementTaskSetToken) //
            .andTransitionTo(StateToken.Analyze) //
            .andStaticTask("First Task", "desc", null) //
            .andStaticTask("Second Task", "desc2", StateToken.Implement) //
            .andStaticTask("Third Task", "desc2", StateToken.Implement, DemoWorkDefinitions.WorkDef_Task_Demo_SwDesign); //

      IAtsWorkDefinition swDesignWorkDef = AtsApiService.get().getWorkDefinitionService().getWorkDefinition(
         DemoWorkDefinitions.WorkDef_Team_Demo_SwDesign);
      swDesignWorkDef.addCreateTasksDefinition(implementTaskSet);

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      String title = getClass().getSimpleName();
      Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(DemoArtifactToken.SAW_SW_Design_AI);
      Date createdDate = new Date();
      AtsUser createdBy = AtsApiService.get().getUserService().getCurrentUser();
      String priority = "3";

      ActionResult actionResult =
         AtsApiService.get().getActionFactory().createAction(null, title, "Problem with the Diagram View",
            ChangeType.Problem, priority, false, null, aias, createdDate, createdBy, null, changes);
      teamWf = actionResult.getFirstTeam();
      boolean isSwDesign = teamWf.getTeamDefinition().getName().contains("SW Design");
      Assert.assertTrue(isSwDesign);
      Assert.assertEquals(StateToken.Endorse.getName(), teamWf.getStateMgr().getCurrentStateName());

      changes.execute();
      changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " - 2");

      TeamWorkFlowManager dtwm =
         new TeamWorkFlowManager(teamWf, AtsApiService.get(), TransitionOption.OverrideAssigneeCheck);

      TeamState toState = TeamState.Analyze;
      Result result = dtwm.transitionTo(toState, teamWf.getAssignees().iterator().next(), false, changes);
      if (result.isFalse()) {
         throw new OseeCoreException("Error transitioning [%s] to Analyze state [%s] error [%s]",
            teamWf.toStringWithId(), toState.getName(), result.getText());
      }
      changes.execute();

      AtsApiService.get().getStoreService().reload(Arrays.asList(teamWf));

      Collection<IAtsTask> tasks = AtsApiService.get().getTaskService().getTasks(teamWf);
      Assert.assertEquals(3, tasks.size());

   }

}
