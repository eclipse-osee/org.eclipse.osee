/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.transition;

import java.util.Collections;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.util.HoursSpentUtil;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
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
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());

      ITransitionHelper helper = new MockTransitionHelper("dodad", Collections.singletonList(teamWf),
         AtsTestUtil.getImplementStateDef().getName(),
         Collections.singleton(AtsClientService.get().getUserService().getCurrentUser()), null, changes);
      IAtsTransitionManager manager = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = manager.handleAllAndPersist();
      Assert.assertTrue(results.isEmpty());

      changes.clear();
      teamWf.getStateMgr().updateMetrics(AtsTestUtil.getImplementStateDef(), 2.2, 1, false,
         AtsClientService.get().getUserService().getCurrentUser());
      helper = new MockTransitionHelper("dodad", Collections.singletonList(teamWf),
         AtsTestUtil.getCompletedStateDef().getName(),
         Collections.singleton(AtsClientService.get().getUserService().getCurrentUser()), null, changes);
      manager = TransitionFactory.getTransitionManager(helper);
      results = manager.handleAllAndPersist();

      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals(3.3, HoursSpentUtil.getHoursSpentTotal(teamWf, AtsClientService.get().getServices()), 0.001);

      teamWf.getStateMgr().updateMetrics(AtsTestUtil.getCompletedStateDef(), -2.2, 1, false,
         AtsClientService.get().getUserService().getCurrentUser());
      AtsClientService.get().getStoreService().executeChangeSet(getClass().getSimpleName(), teamWf);
      Assert.assertEquals(1.1, HoursSpentUtil.getHoursSpentTotal(teamWf, AtsClientService.get().getServices()), 0.001);

      teamWf.getStateMgr().updateMetrics(AtsTestUtil.getCompletedStateDef(), -2.2, 1, false,
         AtsClientService.get().getUserService().getCurrentUser());
      AtsClientService.get().getStoreService().executeChangeSet(getClass().getSimpleName(), teamWf);
      Assert.assertEquals(0, HoursSpentUtil.getHoursSpentTotal(teamWf, AtsClientService.get().getServices()), 0.001);

      AtsTestUtil.cleanup();
   }
}
