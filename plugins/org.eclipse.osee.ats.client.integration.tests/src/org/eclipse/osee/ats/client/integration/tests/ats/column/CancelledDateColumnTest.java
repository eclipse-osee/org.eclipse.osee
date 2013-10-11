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
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.column.CancelledDateColumn;
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

      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtil.getAtsBranch(), CancelledDateColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt =
         DemoTestUtil.createSimpleAction(CancelledDateColumnTest.class.getSimpleName(), transaction);
      transaction.execute();

      Assert.assertEquals("",
         CancelledDateColumn.getInstance().getColumnText(teamArt, CancelledDateColumn.getInstance(), 0));
      Date date = CancelledDateColumn.getDate(teamArt);
      Assert.assertNull(date);
      Assert.assertEquals("", CancelledDateColumn.getDateStr(teamArt));

      TransitionHelper helper =
         new TransitionHelper("Transition to Cancelled", Arrays.asList(teamArt), TeamState.Cancelled.getName(), null,
            "reason", TransitionOption.OverrideTransitionValidityCheck, TransitionOption.OverrideAssigneeCheck);
      TransitionManager transitionMgr = new TransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      Assert.assertTrue(results.toString(), results.isEmpty());
      transitionMgr.getTransaction().execute();

      date = CancelledDateColumn.getDate(teamArt);
      Assert.assertNotNull(date);
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date), CancelledDateColumn.getDateStr(teamArt));
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date),
         CancelledDateColumn.getInstance().getColumnText(teamArt, CancelledDateColumn.getInstance(), 0));

      helper =
         new TransitionHelper("Transition to Endorse", Arrays.asList(teamArt), TeamState.Endorse.getName(),
            Collections.singleton(AtsClientService.get().getUserAdmin().getCurrentUser()), null, TransitionOption.OverrideTransitionValidityCheck,
            TransitionOption.OverrideAssigneeCheck);
      transitionMgr = new TransitionManager(helper);
      results = transitionMgr.handleAll();
      Assert.assertTrue(results.toString(), results.isEmpty());
      transitionMgr.getTransaction().execute();

      Assert.assertEquals("Cancelled date should be blank again", "",
         CancelledDateColumn.getInstance().getColumnText(teamArt, CancelledDateColumn.getInstance(), 0));
      date = CancelledDateColumn.getDate(teamArt);
      Assert.assertNull(date);

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
