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
package org.eclipse.osee.ats.ide.integration.tests.ats.column;

import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.column.CompletedDateColumn;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.ide.column.CompletedDateColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @tests CompletedDateColumn
 * @author Donald G. Dunne
 */
public class CompletedDateColumnTest {

   @AfterClass
   @BeforeClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(CompletedDateColumnTest.class.getSimpleName());
   }

   @org.junit.Test
   public void testGetDateAndStrAndColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(CancelledDateColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.createSimpleAction(CompletedDateColumnTest.class.getSimpleName(),
            changes).getStoreObject();
      changes.execute();

      changes.clear();
      Assert.assertEquals("",
         CompletedDateColumnUI.getInstance().getColumnText(teamArt, CompletedDateColumnUI.getInstance(), 0));
      Date date = CompletedDateColumn.getCompletedDate(teamArt);
      Assert.assertNull(date);
      Assert.assertEquals("", CompletedDateColumn.getCompletedDateStr(teamArt));

      TransitionHelper helper = new TransitionHelper("Transition to Completed", Arrays.asList(teamArt),
         TeamState.Completed.getName(), null, null, changes, AtsClientService.get().getServices(),
         TransitionOption.OverrideTransitionValidityCheck, TransitionOption.OverrideAssigneeCheck);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAllAndPersist();
      Assert.assertTrue(results.toString(), results.isEmpty());

      changes.clear();
      date = CompletedDateColumn.getCompletedDate(teamArt);
      Assert.assertNotNull(date);
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date), CompletedDateColumn.getCompletedDateStr(teamArt));
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date),
         CompletedDateColumnUI.getInstance().getColumnText(teamArt, CompletedDateColumnUI.getInstance(), 0));

      helper = new TransitionHelper("Transition to Endorse", Arrays.asList(teamArt), TeamState.Endorse.getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null, changes,
         AtsClientService.get().getServices(), TransitionOption.OverrideTransitionValidityCheck,
         TransitionOption.OverrideAssigneeCheck);
      transitionMgr = TransitionFactory.getTransitionManager(helper);
      results = transitionMgr.handleAll();
      Assert.assertTrue(results.toString(), results.isEmpty());
      changes.execute();

      Assert.assertEquals("Cancelled date should be blank again", "",
         CompletedDateColumnUI.getInstance().getColumnText(teamArt, CompletedDateColumnUI.getInstance(), 0));
      date = CompletedDateColumn.getCompletedDate(teamArt);
      Assert.assertNull(date);

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
