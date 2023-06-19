/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.column;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.column.CancelledDateColumn;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.column.CancelledDateColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
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

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(CancelledDateColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.createSimpleAction(CancelledDateColumnTest.class.getSimpleName(), changes);
      changes.execute();

      Assert.assertEquals("",
         CancelledDateColumnUI.getInstance().getColumnText(teamArt, CancelledDateColumnUI.getInstance(), 0));
      Date date = CancelledDateColumn.getCancelledDate(teamArt);
      Assert.assertNull(date);
      Assert.assertEquals("", CancelledDateColumn.getCancelledDateStr(teamArt));

      TransitionData transData =
         new TransitionData("Transition to Cancelled", Arrays.asList(teamArt), TeamState.Cancelled.getName(), null,
            "reason", null, TransitionOption.OverrideTransitionValidityCheck, TransitionOption.OverrideAssigneeCheck);
      TransitionResults results = AtsApiService.get().getWorkItemServiceIde().transition(transData);
      Assert.assertTrue(results.toString(), results.isEmpty());

      date = CancelledDateColumn.getCancelledDate(teamArt);
      Assert.assertNotNull(date);
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date), CancelledDateColumn.getCancelledDateStr(teamArt));
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date),
         CancelledDateColumnUI.getInstance().getColumnText(teamArt, CancelledDateColumnUI.getInstance(), 0));

      transData = new TransitionData("Transition to Endorse", Arrays.asList(teamArt), TeamState.Endorse.getName(),
         Collections.singleton(AtsApiService.get().getUserService().getCurrentUser()), null, null,
         TransitionOption.OverrideTransitionValidityCheck, TransitionOption.OverrideAssigneeCheck);
      results = AtsApiService.get().getWorkItemServiceIde().transition(transData);
      Assert.assertTrue(results.toString(), results.isEmpty());

      teamArt = (TeamWorkFlowArtifact) teamArt.reloadAttributesAndRelations();
      Assert.assertEquals("Cancelled date should be blank again", "",
         CancelledDateColumnUI.getInstance().getColumnText(teamArt, CancelledDateColumnUI.getInstance(), 0));
      date = CancelledDateColumn.getCancelledDate(teamArt);
      Assert.assertNull(date);

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
