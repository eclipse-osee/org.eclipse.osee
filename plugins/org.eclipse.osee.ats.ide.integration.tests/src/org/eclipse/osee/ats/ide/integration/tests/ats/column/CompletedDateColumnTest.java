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
import java.util.Date;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.column.CompletedDateColumn;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
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

      Assert.assertEquals("",
         CompletedDateColumnUI.getInstance().getColumnText(teamArt, CompletedDateColumnUI.getInstance(), 0));
      Date date = CompletedDateColumn.getCompletedDate(teamArt);
      Assert.assertNull(date);
      Assert.assertEquals("", CompletedDateColumn.getCompletedDateStr(teamArt));

      TransitionHelper helper = new TransitionHelper("Transition to Completed", Arrays.asList(teamArt),
         TeamState.Completed.getName(), null, null, null, AtsClientService.get(),
         TransitionOption.OverrideTransitionValidityCheck, TransitionOption.OverrideAssigneeCheck);
      TransitionResults results = AtsClientService.get().getWorkItemServiceClient().transition(helper);
      Assert.assertTrue(results.toString(), results.isEmpty());

      date = CompletedDateColumn.getCompletedDate(teamArt);
      Assert.assertNotNull(date);
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date), CompletedDateColumn.getCompletedDateStr(teamArt));
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date),
         CompletedDateColumnUI.getInstance().getColumnText(teamArt, CompletedDateColumnUI.getInstance(), 0));

      helper = new TransitionHelper("Transition to Endorse", Arrays.asList(teamArt), TeamState.Endorse.getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null, null,
         AtsClientService.get(), TransitionOption.OverrideTransitionValidityCheck,
         TransitionOption.OverrideAssigneeCheck);
      results = AtsClientService.get().getWorkItemServiceClient().transition(helper);
      Assert.assertTrue(results.toString(), results.isEmpty());

      teamArt = (TeamWorkFlowArtifact) teamArt.reloadAttributesAndRelations();
      Assert.assertEquals("Cancelled date should be blank again", "",
         CompletedDateColumnUI.getInstance().getColumnText(teamArt, CompletedDateColumnUI.getInstance(), 0));
      date = CompletedDateColumn.getCompletedDate(teamArt);
      Assert.assertNull(date);

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
