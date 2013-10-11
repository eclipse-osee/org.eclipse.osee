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
import java.util.Date;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.column.CompletedDateColumn;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
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

      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtil.getAtsBranch(), CompletedDateColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt =
         DemoTestUtil.createSimpleAction(CompletedDateColumnTest.class.getSimpleName(), transaction);
      transaction.execute();

      Assert.assertEquals("",
         CompletedDateColumn.getInstance().getColumnText(teamArt, CompletedDateColumn.getInstance(), 0));
      Date date = CompletedDateColumn.getDate(teamArt);
      Assert.assertNull(date);
      Assert.assertEquals("", CompletedDateColumn.getDateStr(teamArt));

      TransitionHelper helper =
         new TransitionHelper("Transition to Completed", Arrays.asList(teamArt), TeamState.Completed.getName(), null,
            null, TransitionOption.OverrideTransitionValidityCheck, TransitionOption.OverrideAssigneeCheck);
      TransitionManager transitionMgr = new TransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      Assert.assertTrue(results.toString(), results.isEmpty());
      transitionMgr.getTransaction().execute();

      date = CompletedDateColumn.getDate(teamArt);
      Assert.assertNotNull(date);
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date), CompletedDateColumn.getDateStr(teamArt));
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date),
         CompletedDateColumn.getInstance().getColumnText(teamArt, CompletedDateColumn.getInstance(), 0));

      helper =
         new TransitionHelper("Transition to Endorse", Arrays.asList(teamArt), TeamState.Endorse.getName(),
            Arrays.asList(AtsClientService.get().getUserAdmin().getCurrentUser()), null, TransitionOption.OverrideTransitionValidityCheck,
            TransitionOption.OverrideAssigneeCheck);
      transitionMgr = new TransitionManager(helper);
      results = transitionMgr.handleAll();
      Assert.assertTrue(results.toString(), results.isEmpty());
      transitionMgr.getTransaction().execute();

      Assert.assertEquals("Cancelled date should be blank again", "",
         CompletedDateColumn.getInstance().getColumnText(teamArt, CompletedDateColumn.getInstance(), 0));
      date = CompletedDateColumn.getDate(teamArt);
      Assert.assertNull(date);

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
