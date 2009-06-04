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
package org.eclipse.osee.ats.test.cases;

import java.util.Collection;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact.TaskStates;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.editor.SMAManager.TransitionOption;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.test.util.SMATestUtil;
import org.eclipse.osee.ats.util.widgets.dialog.SimpleTaskResolutionOptionsRule;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class SMAPromptChangeStatusTest extends TestCase {

   public static TeamWorkFlowArtifact teamArt;

   /**
    * @throws java.lang.Exception
    */
   @Override
   protected void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsPlugin.isProductionDb());
   }

   public void testCleanupPre() throws Exception {
      DemoTestUtil.cleanupSimpleTest(getClass().getSimpleName());
   }

   public void testInitialize() throws Exception {
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      teamArt = DemoTestUtil.createSimpleAction(getClass().getSimpleName(), transaction);
      transaction.execute();
      assertNotNull(teamArt);
   }

   public void testChangeTaskStatusNoResolution() throws Exception {

      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      Collection<TaskArtifact> tasks =
            DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_NoRes", 4, transaction);
      transaction.execute();

      assertTrue(tasks.size() == 4);

      SMAPromptChangeStatus promptChangeStatus = new SMAPromptChangeStatus(tasks);
      assertTrue(promptChangeStatus.isValidToChangeStatus().isTrue());

      // Change two to 100, 1 hr split
      promptChangeStatus.performChangeStatus(null, null, 1, 100, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.Completed.name(), 100, 0.25);

      // Change two to 100, 1 hr split
      // hours should be added to inwork state; make sure completed state isn't statused
      promptChangeStatus.performChangeStatus(null, null, 1, 100, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.Completed.name(), 100, 0.50);

      // Change two to 99, 1 hr split
      // transitions to InWork and adds hours
      // make sure hours not added to completed state
      promptChangeStatus.performChangeStatus(null, null, 1, 99, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.InWork.name(), 99, 0.75);

      // Change two to 55, 0
      // no transition, no hours spent
      promptChangeStatus.performChangeStatus(null, null, 0, 55, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.InWork.name(), 55, 0.75);

   }

   public void testChangeTaskStatusWithResolutionOptions() throws Exception {

      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      Collection<TaskArtifact> tasks =
            DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_Res", 4, transaction);
      transaction.execute();

      assertTrue(tasks.size() == 4);

      SMAPromptChangeStatus promptChangeStatus = new SMAPromptChangeStatus(tasks);
      assertTrue(promptChangeStatus.isValidToChangeStatus().isTrue());
      SimpleTaskResolutionOptionsRule optionsRule = new SimpleTaskResolutionOptionsRule();

      // Change two to 100, 1 hr split
      promptChangeStatus.performChangeStatus(optionsRule.getOptions(),
            SimpleTaskResolutionOptionsRule.States.Complete.name(), 1, 100, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.Completed.name(), 100, 0.25);

      // Change two to 100, 1 hr split
      // hours should be added to inwork state; make sure completed state isn't statused
      promptChangeStatus.performChangeStatus(optionsRule.getOptions(),
            SimpleTaskResolutionOptionsRule.States.Complete.name(), 1, 100, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.Completed.name(), 100, 0.50);

      // Change two to 99, 1 hr split
      // transitions to InWork and adds hours
      // make sure hours not added to completed state
      promptChangeStatus.performChangeStatus(optionsRule.getOptions(),
            SimpleTaskResolutionOptionsRule.States.In_Work.name(), 1, 99, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.InWork.name(), 99, 0.75);

      // Change two to 55, 0
      // no transition, no hours spent
      promptChangeStatus.performChangeStatus(optionsRule.getOptions(),
            SimpleTaskResolutionOptionsRule.States.In_Work.name(), 0, 55, true, true);
      SMATestUtil.validateSMAs(tasks, TaskStates.InWork.name(), 55, 0.75);
   }

   public void testChangeStatusFailsIfTaskCancelled() throws Exception {
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      Collection<TaskArtifact> tasks =
            DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_Cancel", 2, transaction);
      transaction.execute();

      assertTrue(tasks.size() == 2);
      TaskArtifact cancelTask = tasks.iterator().next();

      // test that if one task is cancelled, can't change status
      transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      cancelTask.getSmaMgr().transition(TaskStates.Cancelled.name(), (User) null, transaction, TransitionOption.Persist);
      transaction.execute();
      SMAPromptChangeStatus promptChangeStatus = new SMAPromptChangeStatus(tasks);
      Result result = promptChangeStatus.isValidToChangeStatus();
      assertTrue(result.isFalse());
      assertTrue(result.getText().contains("Can not status a cancelled"));

   }

   public void testChangeStatusFailsIfTaskWrongRelatedToState() throws Exception {
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      Collection<TaskArtifact> tasks =
            DemoTestUtil.createSimpleTasks(teamArt, getClass().getSimpleName() + "_RelState", 2, transaction);
      transaction.execute();

      assertTrue(tasks.size() == 2);
      TaskArtifact taskArt = tasks.iterator().next();

      // test that if task not in related-to state of workflows's current status, can't change status
      transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      taskArt.setSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(),
            DefaultTeamState.Analyze.name());
      transaction.execute();
      SMAPromptChangeStatus promptChangeStatus = new SMAPromptChangeStatus(tasks);
      Result result = promptChangeStatus.isValidToChangeStatus();
      assertTrue(result.isFalse());
      assertTrue(result.getText().contains("Task work must be done in"));
   }

   public void testCleanupPost() throws Exception {
      DemoTestUtil.cleanupSimpleTest(getClass().getSimpleName());
   }

}
