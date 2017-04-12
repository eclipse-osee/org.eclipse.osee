/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.column;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.column.CancelledDateColumnUI;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.column.CancelledDateColumn;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @tests CancelledDateColumn
 * @author Donald G. Dunne
 */
public class CancelledDateColumnTest {

   @AfterClass
   @BeforeClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(CancelledDateColumnTest.class.getSimpleName());
   }

   @org.junit.Test
   public void testGetDateAndStrAndColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(CancelledDateColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.createSimpleAction(CancelledDateColumnTest.class.getSimpleName(), changes);
      changes.execute();

      changes.clear();
      Assert.assertEquals("",
         CancelledDateColumnUI.getInstance().getColumnText(teamArt, CancelledDateColumnUI.getInstance(), 0));
      Date date = CancelledDateColumn.getCancelledDate(teamArt);
      Assert.assertNull(date);
      Assert.assertEquals("", CancelledDateColumn.getCancelledDateStr(teamArt));

      TransitionHelper helper = new TransitionHelper("Transition to Cancelled", Arrays.asList(teamArt),
         TeamState.Cancelled.getName(), null, "reason", changes, AtsClientService.get().getServices(),
         TransitionOption.OverrideTransitionValidityCheck, TransitionOption.OverrideAssigneeCheck);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAllAndPersist();
      Assert.assertTrue(results.toString(), results.isEmpty());

      changes.clear();
      date = CancelledDateColumn.getCancelledDate(teamArt);
      Assert.assertNotNull(date);
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date), CancelledDateColumn.getCancelledDateStr(teamArt));
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date),
         CancelledDateColumnUI.getInstance().getColumnText(teamArt, CancelledDateColumnUI.getInstance(), 0));

      helper = new TransitionHelper("Transition to Endorse", Arrays.asList(teamArt), TeamState.Endorse.getName(),
         Collections.singleton(AtsClientService.get().getUserService().getCurrentUser()), null, changes,
         AtsClientService.get().getServices(), TransitionOption.OverrideTransitionValidityCheck,
         TransitionOption.OverrideAssigneeCheck);
      transitionMgr = TransitionFactory.getTransitionManager(helper);
      results = transitionMgr.handleAllAndPersist();
      Assert.assertTrue(results.toString(), results.isEmpty());

      Assert.assertEquals("Cancelled date should be blank again", "",
         CancelledDateColumnUI.getInstance().getColumnText(teamArt, CancelledDateColumnUI.getInstance(), 0));
      date = CancelledDateColumn.getCancelledDate(teamArt);
      Assert.assertNull(date);

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
