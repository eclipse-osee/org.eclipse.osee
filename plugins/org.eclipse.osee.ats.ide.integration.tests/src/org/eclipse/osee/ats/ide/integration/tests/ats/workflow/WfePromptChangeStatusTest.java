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
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.util.HoursSpentUtil;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.ide.editor.tab.workflow.util.WfePromptChangeStatus;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskStates;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.Result;
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
public class WfePromptChangeStatusTest {

   public static TeamWorkFlowArtifact teamArt;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsClientService.get().getStoreService().isProductionDb());
   }

   @BeforeClass
   public static void testCleanupPre() throws Exception {
      AtsTestUtil.cleanupSimpleTest(WfePromptChangeStatusTest.class.getSimpleName());
   }

   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(WfePromptChangeStatusTest.class.getSimpleName());
   }

   @Test
   public void test01Initialize() throws Exception {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Prompt Change Status Test");
      teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.createSimpleAction(getClass().getSimpleName(), changes).getStoreObject();
      changes.execute();
      assertNotNull(teamArt);
   }

   @Test
   public void test02ChangeTaskStatusNoResolution() throws Exception {
      Collection<TaskArtifact> tasks = DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_NoRes", 4,
         teamArt.getCurrentStateName());

      assertTrue(tasks.size() == 4);

      assertTrue(WfePromptChangeStatus.isValidToChangeStatus(tasks).isTrue());

      // Change two to 100, 1 hr split
      WfePromptChangeStatus.performChangeStatusAndPersist(tasks, null, 1, 100, true);
      validateWorkflows(tasks, TaskStates.InWork.getName(), 100, 0.25);

      // Change two to 100, 1 hr split
      // hours should be added to inwork state; make sure completed state isn't statused
      WfePromptChangeStatus.performChangeStatusAndPersist(tasks, null, 1, 100, true);
      validateWorkflows(tasks, TaskStates.InWork.getName(), 100, 0.50);

      // Change two to 99, 1 hr split
      // transitions to InWork and adds hours
      // make sure hours not added to completed state
      WfePromptChangeStatus.performChangeStatusAndPersist(tasks, null, 1, 99, true);
      validateWorkflows(tasks, TaskStates.InWork.getName(), 99, 0.75);

      // Change two to 55, 0
      // no transition, no hours spent
      WfePromptChangeStatus.performChangeStatusAndPersist(tasks, null, 0, 55, true);
      validateWorkflows(tasks, TaskStates.InWork.getName(), 55, 0.75);

   }

   @Test
   public void test03ChangeStatusFailsIfTaskCancelled() throws Exception {
      Collection<TaskArtifact> tasks =
         DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_Cancel", 2, null);

      assertTrue(tasks.size() == 2);
      TaskArtifact cancelTask = tasks.iterator().next();

      // test that if one task is cancelled, can't change status
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      TransitionHelper helper =
         new TransitionHelper("Transition to Cancelled", Arrays.asList(cancelTask), TaskStates.Cancelled.getName(),
            null, null, changes, AtsClientService.get().getServices(), TransitionOption.None);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAllAndPersist();
      assertEquals("Transition should have no errors", true, results.isEmpty());

      Result result = WfePromptChangeStatus.isValidToChangeStatus(tasks);
      assertTrue(result.isFalse());
      assertTrue(result.getText().contains("Can not status a cancelled"));

   }

   @Test
   public void test04ChangeStatusFailsIfTaskWrongRelatedToState() throws Exception {
      Collection<TaskArtifact> tasks = DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_RelState",
         2, teamArt.getCurrentStateName());

      assertTrue(tasks.size() == 2);
      TaskArtifact taskArt = tasks.iterator().next();

      // test that if task not in related-to state of workflows's current status, can't change status
      taskArt.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, TeamState.Analyze.getName());
      taskArt.persist(getClass().getSimpleName());
      Result result = WfePromptChangeStatus.isValidToChangeStatus(tasks);
      assertTrue(result.isFalse());
      assertTrue(result.getText().contains("Task work must be done in"));
   }

   @Test
   public void test05ChangeStatusPassesIfTaskNotUsingRelatedToState() throws Exception {
      Collection<TaskArtifact> tasks =
         DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_RelState", 2, "");

      assertTrue(tasks.size() == 2);

      Result result = WfePromptChangeStatus.isValidToChangeStatus(tasks);
      assertTrue(result.isTrue());
   }

   private static void validateWorkflows(Collection<? extends AbstractWorkflowArtifact> awas, String stateName, int totalPercent, double hoursSpent) throws Exception {
      for (AbstractWorkflowArtifact awa : awas) {
         assertEquals("Current State wrong for " + awa.getAtsId(), awa.getStateMgr().getCurrentStateName(), stateName);
         if (awa.isCompletedOrCancelled()) {
            assertEquals("ats.CurrentState wrong " + awa.getAtsId(), awa.getStateMgr().getCurrentStateName() + ";;;",
               awa.getSoleAttributeValue(AtsAttributeTypes.CurrentState));
         }
         assertEquals("Percent wrong for " + awa.getAtsId(),
            PercentCompleteTotalUtil.getPercentCompleteTotal(awa, AtsClientService.get().getServices()), totalPercent);
         assertEquals("Hours Spent wrong for " + awa.getAtsId(),
            HoursSpentUtil.getHoursSpentTotal(awa, AtsClientService.get().getServices()), hoursSpent, 0.0);

         for (String xml : awa.getAttributesToStringList(AtsAttributeTypes.State)) {
            WorkState state = AtsClientService.get().getWorkStateFactory().fromStoreStr(xml);
            boolean isCompletedCancelledState = isCompletedCancelledState(awa, state.getName());
            if (isCompletedCancelledState) {
               assertTrue("completed/cancelled ats.State [" + xml + "] wrong " + awa.getAtsId(), xml.endsWith(";;;"));
            }
         }
      }
   }

   private static boolean isCompletedCancelledState(AbstractWorkflowArtifact aba, String stateName) {
      return aba.getWorkDefinition().getStateByName(stateName).getStateType().isCompletedOrCancelledState();
   }

}
