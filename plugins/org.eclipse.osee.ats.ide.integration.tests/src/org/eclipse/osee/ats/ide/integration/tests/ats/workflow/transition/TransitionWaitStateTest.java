/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.transition;

import java.util.Arrays;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for the wait state mechanism. Verifies that a normal transition out of the Open state enforces
 * required fields, while transitioning into the Monitor wait state (declared via andToWaitStates) and back out of it
 * skips required-field validation.
 *
 * @author Donald G. Dunne
 */
public class TransitionWaitStateTest {

   @Before
   @After
   public void cleanup() {
      Assert.assertTrue("This can not be run on production database.",
         !AtsApiService.get().getStoreService().isProductionDb());
      AtsTestUtil.cleanupSimpleTest(getClass().getSimpleName());
   }

   @Test
   public void testTransitionIntoAndOutOfMonitorWaitState() {
      AtsApi atsApi = AtsApiService.get();
      IAtsTeamWorkflow teamWf = DemoTestUtil.createDemoProblemReport(getClass().getSimpleName());
      AtsUser currentUser = atsApi.getUserService().getCurrentUser();

      Assert.assertEquals("Expected workflow to start in Open", TeamState.Open.getName(),
         teamWf.getCurrentStateName());

      // 1. Open -> Analyzed is a normal (validated) transition. Required fields are unset, so it must fail.
      TransitionResults toAnalyzed = transition(atsApi, teamWf, TeamState.Analyzed, currentUser);
      Assert.assertTrue("Open -> Analyzed should fail on required fields: " + toAnalyzed.toString(),
         toAnalyzed.isErrors());
      Assert.assertEquals("Workflow should remain in Open after failed transition", TeamState.Open.getName(),
         teamWf.getCurrentStateName());

      // 2. Open -> Monitor is a wait state transition, so it must succeed despite unset required fields.
      TransitionResults toMonitor = transition(atsApi, teamWf, TeamState.Monitor, currentUser);
      Assert.assertTrue("Open -> Monitor should skip validation and succeed: " + toMonitor.toString(),
         toMonitor.isEmpty());
      teamWf = reload(atsApi, teamWf);
      Assert.assertEquals("Workflow should be in Monitor", TeamState.Monitor.getName(), teamWf.getCurrentStateName());

      // LastStateName should record Open as the state we came from
      Assert.assertEquals("Last state should be Open", TeamState.Open.getName(), teamWf.getLastStateName());

      // 3. A wait state is a temporary hold: the only valid exits are back to the last state or a cancelled state.
      // Monitor -> Analyzed is neither, so it must be blocked outright (cannot be used to advance past Open's
      // required fields).
      TransitionResults toAnalyzedFromMonitor = transition(atsApi, teamWf, TeamState.Analyzed, currentUser);
      Assert.assertTrue("Monitor -> Analyzed should be blocked: " + toAnalyzedFromMonitor.toString(),
         toAnalyzedFromMonitor.isErrors());
      teamWf = reload(atsApi, teamWf);
      Assert.assertEquals("Workflow should remain in Monitor after blocked transition", TeamState.Monitor.getName(),
         teamWf.getCurrentStateName());

      // 4. Monitor -> Open (the last state) is a sanctioned exit; it skips validation and succeeds.
      TransitionResults backToOpen = transition(atsApi, teamWf, TeamState.Open, currentUser);
      Assert.assertTrue("Monitor -> Open (last state) should skip validation and succeed: " + backToOpen.toString(),
         backToOpen.isEmpty());
      teamWf = reload(atsApi, teamWf);
      Assert.assertEquals("Workflow should be back in Open", TeamState.Open.getName(), teamWf.getCurrentStateName());
   }

   @Test
   public void testCancelFromWaitStateSkipsValidation() {
      AtsApi atsApi = AtsApiService.get();
      IAtsTeamWorkflow teamWf = DemoTestUtil.createDemoProblemReport(getClass().getSimpleName() + "-cancel");
      AtsUser currentUser = atsApi.getUserService().getCurrentUser();

      // Park in Monitor (wait state), then cancel from it. Cancelling from a wait state skips validation.
      TransitionResults toMonitor = transition(atsApi, teamWf, TeamState.Monitor, currentUser);
      Assert.assertTrue(toMonitor.toString(), toMonitor.isEmpty());
      teamWf = reload(atsApi, teamWf);
      Assert.assertEquals(TeamState.Monitor.getName(), teamWf.getCurrentStateName());

      TransitionResults toCancelled = transition(atsApi, teamWf, TeamState.Cancelled, currentUser);
      Assert.assertTrue("Monitor -> Cancelled should succeed: " + toCancelled.toString(), toCancelled.isEmpty());
      teamWf = reload(atsApi, teamWf);
      Assert.assertEquals(TeamState.Cancelled.getName(), teamWf.getCurrentStateName());
   }

   private static IAtsTeamWorkflow reload(AtsApi atsApi, IAtsTeamWorkflow teamWf) {
      return atsApi.getQueryService().getTeamWf(teamWf.getId());
   }

   private static TransitionResults transition(AtsApi atsApi, IAtsTeamWorkflow teamWf, TeamState toState,
      AtsUser user) {
      String toStateName = toState.getName();
      TransitionData transData =
         new TransitionData("Transition to " + toStateName, Arrays.asList(teamWf), toStateName, Arrays.asList(user),
            null, atsApi.createChangeSet("Transition to " + toStateName), TransitionOption.OverrideAssigneeCheck);
      return atsApi.getWorkItemService().transition(transData);
   }
}
