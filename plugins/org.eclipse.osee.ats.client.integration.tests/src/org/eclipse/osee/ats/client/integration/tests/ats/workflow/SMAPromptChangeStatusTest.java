/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.task.TaskStates;
import org.eclipse.osee.ats.core.client.team.TeamState;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AtsWorkStateFactory;
import org.eclipse.osee.ats.core.client.workflow.HoursSpentUtil;
import org.eclipse.osee.ats.core.client.workflow.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Donald G. Dunne
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SMAPromptChangeStatusTest {

   public static TeamWorkFlowArtifact teamArt;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsUtil.isProductionDb());
   }

   @BeforeClass
   public static void testCleanupPre() throws Exception {
      AtsTestUtil.cleanupSimpleTest(SMAPromptChangeStatusTest.class.getSimpleName());
   }

   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(SMAPromptChangeStatusTest.class.getSimpleName());
   }

   @Test
   public void test01Initialize() throws Exception {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      teamArt = DemoTestUtil.createSimpleAction(getClass().getSimpleName(), transaction);
      transaction.execute();
      assertNotNull(teamArt);
   }

   @Test
   public void test02ChangeTaskStatusNoResolution() throws Exception {

      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      Collection<TaskArtifact> tasks =
         DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_NoRes", 4,
            teamArt.getCurrentStateName(), transaction);
      transaction.execute();

      assertTrue(tasks.size() == 4);

      assertTrue(SMAPromptChangeStatus.isValidToChangeStatus(tasks).isTrue());

      // Change two to 100, 1 hr split
      SMAPromptChangeStatus.performChangeStatus(tasks, null, 1, 100, true, true);
      validateSMAs(tasks, TaskStates.InWork.getName(), 100, 0.25);

      // Change two to 100, 1 hr split
      // hours should be added to inwork state; make sure completed state isn't statused
      SMAPromptChangeStatus.performChangeStatus(tasks, null, 1, 100, true, true);
      validateSMAs(tasks, TaskStates.InWork.getName(), 100, 0.50);

      // Change two to 99, 1 hr split
      // transitions to InWork and adds hours
      // make sure hours not added to completed state
      SMAPromptChangeStatus.performChangeStatus(tasks, null, 1, 99, true, true);
      validateSMAs(tasks, TaskStates.InWork.getName(), 99, 0.75);

      // Change two to 55, 0
      // no transition, no hours spent
      SMAPromptChangeStatus.performChangeStatus(tasks, null, 0, 55, true, true);
      validateSMAs(tasks, TaskStates.InWork.getName(), 55, 0.75);

   }

   @Test
   public void test03ChangeStatusFailsIfTaskCancelled() throws Exception {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      Collection<TaskArtifact> tasks =
         DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_Cancel", 2, null, transaction);
      transaction.execute();

      assertTrue(tasks.size() == 2);
      TaskArtifact cancelTask = tasks.iterator().next();

      // test that if one task is cancelled, can't change status
      transaction = TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      TransitionHelper helper =
         new TransitionHelper("Transition to Cancelled", Arrays.asList(cancelTask), TaskStates.Cancelled.getName(),
            null, null, TransitionOption.None);
      TransitionManager transitionMgr = new TransitionManager(helper, transaction);
      TransitionResults results = transitionMgr.handleAll();
      transitionMgr.getTransaction().execute();
      assertEquals("Transition should have no errors", true, results.isEmpty());

      Result result = SMAPromptChangeStatus.isValidToChangeStatus(tasks);
      assertTrue(result.isFalse());
      assertTrue(result.getText().contains("Can not status a cancelled"));

   }

   @Test
   public void test04ChangeStatusFailsIfTaskWrongRelatedToState() throws Exception {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      Collection<TaskArtifact> tasks =
         DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_RelState", 2,
            teamArt.getCurrentStateName(), transaction);
      transaction.execute();

      assertTrue(tasks.size() == 2);
      TaskArtifact taskArt = tasks.iterator().next();

      // test that if task not in related-to state of workflows's current status, can't change status
      transaction = TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      taskArt.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, TeamState.Analyze.getName());
      transaction.execute();
      Result result = SMAPromptChangeStatus.isValidToChangeStatus(tasks);
      assertTrue(result.isFalse());
      assertTrue(result.getText().contains("Task work must be done in"));
   }

   private static void validateSMAs(Collection<? extends AbstractWorkflowArtifact> awas, String stateName, int totalPercent, double hoursSpent) throws Exception {
      for (AbstractWorkflowArtifact awa : awas) {
         assertEquals("Current State wrong for " + awa.getHumanReadableId(), awa.getStateMgr().getCurrentStateName(),
            stateName);
         if (awa.isCompletedOrCancelled()) {
            assertEquals("ats.CurrentState wrong " + awa.getHumanReadableId(),
               awa.getStateMgr().getCurrentStateName() + ";;;",
               awa.getSoleAttributeValue(AtsAttributeTypes.CurrentState));
         }
         assertEquals("Percent wrong for " + awa.getHumanReadableId(),
            PercentCompleteTotalUtil.getPercentCompleteTotal(awa), totalPercent);
         assertEquals("Hours Spent wrong for " + awa.getHumanReadableId(), HoursSpentUtil.getHoursSpentTotal(awa),
            hoursSpent, 0.0);

         for (String xml : awa.getAttributesToStringList(AtsAttributeTypes.State)) {
            WorkStateImpl state = AtsWorkStateFactory.getFromXml(xml);
            boolean isCompletedCancelledState = isCompletedCancelledState(awa, state.getName());
            if (isCompletedCancelledState) {
               assertTrue("completed/cancelled ats.State [" + xml + "] wrong " + awa.getHumanReadableId(),
                  xml.endsWith(";;;"));
            }
         }
      }
   }

   private static boolean isCompletedCancelledState(AbstractWorkflowArtifact aba, String stateName) {
      return aba.getWorkDefinition().getStateByName(stateName).getStateType().isCompletedOrCancelledState();
   }

}
