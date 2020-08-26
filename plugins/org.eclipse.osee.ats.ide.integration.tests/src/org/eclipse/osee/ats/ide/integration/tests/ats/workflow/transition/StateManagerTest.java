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
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.util.HoursSpentUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
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

      teamWf.getStateMgr().updateMetrics(AtsTestUtil.getAnalyzeStateDef(), 1.1, 1, false,
         AtsClientService.get().getUserService().getCurrentUser());
      teamWf.persist(getClass().getSimpleName());

      ITransitionHelper helper = new MockTransitionHelper("dodad", Collections.singletonList(teamWf),
         AtsTestUtil.getImplementStateDef().getName(),
         Collections.singleton(AtsClientService.get().getUserService().getCurrentUser()), null, null);
      TransitionResults results = AtsClientService.get().getWorkItemService().transition(helper);
      Assert.assertTrue(results.isEmpty());

      teamWf.getStateMgr().updateMetrics(AtsTestUtil.getImplementStateDef(), 2.2, 1, false,
         AtsClientService.get().getUserService().getCurrentUser());
      teamWf.persist(getClass().getSimpleName());

      helper = new MockTransitionHelper("dodad", Collections.singletonList(teamWf),
         AtsTestUtil.getCompletedStateDef().getName(),
         Collections.singleton(AtsClientService.get().getUserService().getCurrentUser()), null, null);
      results = AtsClientService.get().getWorkItemService().transition(helper);
      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals(3.3, HoursSpentUtil.getHoursSpentTotal(teamWf, AtsClientService.get()), 0.001);

      teamWf.getStateMgr().updateMetrics(AtsTestUtil.getCompletedStateDef(), -2.2, 1, false,
         AtsClientService.get().getUserService().getCurrentUser());
      AtsClientService.get().getStoreService().executeChangeSet(getClass().getSimpleName(), teamWf);
      Assert.assertEquals(1.1, HoursSpentUtil.getHoursSpentTotal(teamWf, AtsClientService.get()), 0.001);

      teamWf.getStateMgr().updateMetrics(AtsTestUtil.getCompletedStateDef(), -2.2, 1, false,
         AtsClientService.get().getUserService().getCurrentUser());
      AtsClientService.get().getStoreService().executeChangeSet(getClass().getSimpleName(), teamWf);
      Assert.assertEquals(0, HoursSpentUtil.getHoursSpentTotal(teamWf, AtsClientService.get()), 0.001);

      AtsTestUtil.cleanup();
   }
}
