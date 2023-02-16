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

import java.util.Collections;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author John Misinco
 */
public class StateManagerTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @Test
   public void testUpdateMetrics() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());

      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      AtsApiService.get().getWorkItemMetricsService().updateMetrics(teamWf, AtsTestUtil.getAnalyzeStateDef(), 1.1, 1,
         false, AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      ITransitionHelper helper = new MockTransitionHelper("dodad", Collections.singletonList(teamWf),
         AtsTestUtil.getImplementStateDef().getName(),
         Collections.singleton(AtsApiService.get().getUserService().getCurrentUser()), null, null);
      TransitionResults results = AtsApiService.get().getWorkItemService().transition(helper);
      Assert.assertTrue(results.isEmpty());

      changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      AtsApiService.get().getWorkItemMetricsService().updateMetrics(teamWf, AtsTestUtil.getImplementStateDef(), 2.2, 1,
         false, AtsApiService.get().getUserService().getCurrentUser(), changes);
      changes.execute();

      helper = new MockTransitionHelper("dodad", Collections.singletonList(teamWf),
         AtsTestUtil.getCompletedStateDef().getName(),
         Collections.singleton(AtsApiService.get().getUserService().getCurrentUser()), null, null);
      results = AtsApiService.get().getWorkItemService().transition(helper);
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
