/*********************************************************************
 * Copyright (c) 2012 Boeing
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author John Misinco
 * @author Donald G. Dunne
 */
public class StateManagerTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @Test
   public void testAssignees() {

      AtsUser joe = AtsApiService.get().getUserService().getUserById(DemoUsers.Joe_Smith);
      AtsUser jason = AtsApiService.get().getUserService().getUserById(DemoUsers.Jason_Michael);
      AtsUser janice = AtsApiService.get().getUserService().getUserById(DemoUsers.Janice_Michael);

      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();
      Assert.assertNotNull(teamWf);
      Assert.assertTrue(teamWf.getAssigneesStr().equals(DemoUsers.Joe_Smith.getName()));
      assertMatches(teamWf.getAssignees(), joe);
      assertMatches(
         AtsApiService.get().getWorkItemService().getStateMgr(teamWf).getAssignees(teamWf.getCurrentStateName()), joe);

      setAssignees(teamWf, Arrays.asList(janice, jason));
      Assert.assertTrue(teamWf.getAssigneesStr().equals(
         DemoUsers.Janice_Michael.getName() + "; " + DemoUsers.Jason_Michael.getName()));
      assertMatches(teamWf.getAssignees(), janice, jason);
      assertMatches(
         AtsApiService.get().getWorkItemService().getStateMgr(teamWf).getAssignees(teamWf.getCurrentStateName()),
         janice, jason);

      setAssignees(teamWf, Arrays.asList(janice, jason));
      Assert.assertTrue(teamWf.getAssigneesStr().equals(
         DemoUsers.Janice_Michael.getName() + "; " + DemoUsers.Jason_Michael.getName()));
      assertMatches(teamWf.getAssignees(), janice, jason);
      assertMatches(
         AtsApiService.get().getWorkItemService().getStateMgr(teamWf).getAssignees(teamWf.getCurrentStateName()),
         janice, jason);

      setAssignees(teamWf, Arrays.asList(joe, joe, janice, janice));
      Assert.assertTrue(
         teamWf.getAssigneesStr().equals(DemoUsers.Janice_Michael.getName() + "; " + DemoUsers.Joe_Smith.getName()));
      assertMatches(teamWf.getAssignees(), joe, janice);
      assertMatches(
         AtsApiService.get().getWorkItemService().getStateMgr(teamWf).getAssignees(teamWf.getCurrentStateName()), joe,
         janice);

      setAssignee(teamWf, jason);
      Assert.assertTrue(teamWf.getAssigneesStr().equals(DemoUsers.Jason_Michael.getName()));
      assertMatches(teamWf.getAssignees(), jason);
      assertMatches(
         AtsApiService.get().getWorkItemService().getStateMgr(teamWf).getAssignees(teamWf.getCurrentStateName()),
         jason);

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      changes.addAssignee(teamWf, joe);
      changes.execute();

      Assert.assertTrue(
         teamWf.getAssigneesStr().equals(DemoUsers.Jason_Michael.getName() + "; " + DemoUsers.Joe_Smith.getName()));
      assertMatches(teamWf.getAssignees(), jason, joe);
      assertMatches(
         AtsApiService.get().getWorkItemService().getStateMgr(teamWf).getAssignees(teamWf.getCurrentStateName()), jason,
         joe);

      changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      changes.removeAssignee(teamWf, jason);
      changes.execute();

      Assert.assertTrue(teamWf.getAssigneesStr().equals(DemoUsers.Joe_Smith.getName()));
      assertMatches(teamWf.getAssignees(), joe);
      assertMatches(
         AtsApiService.get().getWorkItemService().getStateMgr(teamWf).getAssignees(teamWf.getCurrentStateName()), joe);

      changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      changes.clearAssignees(teamWf);
      changes.execute();

      Assert.assertTrue(teamWf.getAssigneesStr().equals(""));
      Assert.assertTrue(teamWf.getAssignees().isEmpty());
      Assert.assertTrue(AtsApiService.get().getWorkItemService().getStateMgr(teamWf).getAssignees(
         teamWf.getCurrentStateName()).isEmpty());

   }

   private void setAssignee(TeamWorkFlowArtifact teamWf, AtsUser user) {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      changes.setAssignee(teamWf, user);
      changes.execute();
   }

   private void setAssignees(TeamWorkFlowArtifact teamWf, List<AtsUser> users) {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      changes.setAssignees(teamWf, users);
      changes.execute();
   }

   private void assertMatches(Collection<AtsUser> actual, AtsUser... expected) {
      XResultData rd = org.eclipse.osee.framework.jdk.core.util.Collections.matches(
         org.eclipse.osee.framework.jdk.core.util.Collections.asList(expected), actual);
      if (rd.isErrors()) {
         Assert.fail(rd.toString());
      }
   }

   @Test
   public void testUpdateMetrics() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());

      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      AtsApiService.get().getWorkItemMetricsService().updateMetrics(teamWf, AtsTestUtil.getAnalyzeStateDef(), 1.1, 1,
         false, AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      TransitionData transData =
         new TransitionData("dodad", Collections.singletonList(teamWf), AtsTestUtil.getImplementStateDef().getName(),
            Collections.singleton(AtsApiService.get().getUserService().getCurrentUser()), null, null);
      TransitionResults results = AtsApiService.get().getWorkItemService().transition(transData);
      Assert.assertTrue(results.isEmpty());

      changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      AtsApiService.get().getWorkItemMetricsService().updateMetrics(teamWf, AtsTestUtil.getImplementStateDef(), 2.2, 1,
         false, AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      transData = new TestTransitionData("dodad", Collections.singletonList(teamWf),
         AtsTestUtil.getCompletedStateDef().getName(),
         Collections.singleton(AtsApiService.get().getUserService().getCurrentUser()), null, null);
      results = AtsApiService.get().getWorkItemService().transition(transData);
      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals(3.3, AtsApiService.get().getWorkItemMetricsService().getHoursSpentTotal(teamWf), 0.001);

      changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      AtsApiService.get().getWorkItemMetricsService().updateMetrics(teamWf, AtsTestUtil.getCompletedStateDef(), -2.2, 1,
         false, AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();
      AtsApiService.get().getStoreService().executeChangeSet(getClass().getSimpleName(), teamWf);
      Assert.assertEquals(1.1, AtsApiService.get().getWorkItemMetricsService().getHoursSpentTotal(teamWf), 0.001);

      changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      AtsApiService.get().getWorkItemMetricsService().updateMetrics(teamWf, AtsTestUtil.getCompletedStateDef(), -2.2, 1,
         false, AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();
      Assert.assertEquals(0, AtsApiService.get().getWorkItemMetricsService().getHoursSpentTotal(teamWf), 0.001);

      AtsTestUtil.cleanup();
   }
}
