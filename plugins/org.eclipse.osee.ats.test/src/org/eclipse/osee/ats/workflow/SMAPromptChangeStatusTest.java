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
package org.eclipse.osee.ats.workflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.task.TaskStates;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.ats.util.SMATestUtil;
import org.eclipse.osee.ats.util.widgets.dialog.SimpleTaskResolutionOptionsRule;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
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

   @org.junit.Test
   public void testInitialize() throws Exception {
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      teamArt = DemoTestUtil.createSimpleAction(getClass().getSimpleName(), transaction);
      transaction.execute();
      assertNotNull(teamArt);
   }

   @org.junit.Test
   public void testChangeTaskStatusNoResolution() throws Exception {

      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      Collection<TaskArtifact> tasks =
         DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_NoRes", 4, transaction);
      transaction.execute();

      assertTrue(tasks.size() == 4);

      assertTrue(SMAPromptChangeStatus.isValidToChangeStatus(tasks).isTrue());

      // Change two to 100, 1 hr split
      SMAPromptChangeStatus.performChangeStatus(tasks, null, null, 1, 100, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.Completed.getPageName(), 100, 0.25);

      // Change two to 100, 1 hr split
      // hours should be added to inwork state; make sure completed state isn't statused
      SMAPromptChangeStatus.performChangeStatus(tasks, null, null, 1, 100, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.Completed.getPageName(), 100, 0.50);

      // Change two to 99, 1 hr split
      // transitions to InWork and adds hours
      // make sure hours not added to completed state
      SMAPromptChangeStatus.performChangeStatus(tasks, null, null, 1, 99, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.InWork.getPageName(), 99, 0.75);

      // Change two to 55, 0
      // no transition, no hours spent
      SMAPromptChangeStatus.performChangeStatus(tasks, null, null, 0, 55, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.InWork.getPageName(), 55, 0.75);

   }

   @org.junit.Test
   public void testChangeTaskStatusWithResolutionOptions() throws Exception {

      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      Collection<TaskArtifact> tasks =
         DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_Res", 4, transaction);
      transaction.execute();

      assertTrue(tasks.size() == 4);

      assertTrue(SMAPromptChangeStatus.isValidToChangeStatus(tasks).isTrue());
      SimpleTaskResolutionOptionsRule optionsRule = new SimpleTaskResolutionOptionsRule();

      // Change two to 100, 1 hr split
      SMAPromptChangeStatus.performChangeStatus(tasks, optionsRule.getOptions(),
         SimpleTaskResolutionOptionsRule.States.Complete.name(), 1, 100, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.Completed.getPageName(), 100, 0.25);

      // Change two to 100, 1 hr split
      // hours should be added to inwork state; make sure completed state isn't statused
      SMAPromptChangeStatus.performChangeStatus(tasks, optionsRule.getOptions(),
         SimpleTaskResolutionOptionsRule.States.Complete.name(), 1, 100, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.Completed.getPageName(), 100, 0.50);

      // Change two to 99, 1 hr split
      // transitions to InWork and adds hours
      // make sure hours not added to completed state
      SMAPromptChangeStatus.performChangeStatus(tasks, optionsRule.getOptions(),
         SimpleTaskResolutionOptionsRule.States.In_Work.name(), 1, 99, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.InWork.getPageName(), 99, 0.75);

      // Change two to 55, 0
      // no transition, no hours spent
      SMAPromptChangeStatus.performChangeStatus(tasks, optionsRule.getOptions(),
         SimpleTaskResolutionOptionsRule.States.In_Work.name(), 0, 55, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.InWork.getPageName(), 55, 0.75);
   }

   @org.junit.Test
   public void testChangeStatusFailsIfTaskCancelled() throws Exception {
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      Collection<TaskArtifact> tasks =
         DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_Cancel", 2, transaction);
      transaction.execute();

      assertTrue(tasks.size() == 2);
      TaskArtifact cancelTask = tasks.iterator().next();

      // test that if one task is cancelled, can't change status
      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      TransitionHelper helper =
         new TransitionHelper("Transition to Cancelled", Arrays.asList(cancelTask), TaskStates.Cancelled.getPageName(),
            null, null, TransitionOption.None);
      TransitionManager transitionMgr = new TransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      transitionMgr.getTransaction().execute();
      Assert.assertTrue("Transition should have no errors", results.isEmpty());

      Result result = SMAPromptChangeStatus.isValidToChangeStatus(tasks);
      assertTrue(result.isFalse());
      assertTrue(result.getText().contains("Can not status a cancelled"));

   }

   @org.junit.Test
   public void testChangeStatusFailsIfTaskWrongRelatedToState() throws Exception {
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      Collection<TaskArtifact> tasks =
         DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_RelState", 2, transaction);
      transaction.execute();

      assertTrue(tasks.size() == 2);
      TaskArtifact taskArt = tasks.iterator().next();

      // test that if task not in related-to state of workflows's current status, can't change status
      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Prompt Change Status Test");
      taskArt.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, TeamState.Analyze.getPageName());
      transaction.execute();
      Result result = SMAPromptChangeStatus.isValidToChangeStatus(tasks);
      assertTrue(result.isFalse());
      assertTrue(result.getText().contains("Task work must be done in"));
   }

   @AfterClass
   public static void testCleanupPost() throws Exception {
      AtsTestUtil.cleanupSimpleTest(SMAPromptChangeStatusTest.class.getSimpleName());
   }

}
