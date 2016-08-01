/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.core.client.task;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IWorkDefinitionMatch;
import org.eclipse.osee.ats.api.workdef.JaxAtsWorkDef;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.task.TaskMover;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */

public class TaskMoverTest {

   // @formatter:off
   @Mock private IAtsTeamWorkflow teamWf, teamWf2;
   @Mock private IAtsTask task1;
   @Mock private IAtsWorkDefinitionAdmin workDefinitionAdmin;
   @Mock private IWorkDefinitionMatch match1;
   @Mock private IAtsWorkDefinition taskWorkDef1, taskWorkDef2;
   // @formatter:on

   @Before
   public void setup() {
      AtsTestUtil.cleanup();
      MockitoAnnotations.initMocks(this);
   }

   @BeforeClass
   public static void cleanup() throws OseeCoreException {
      AtsTestUtil.cleanup();
   }

   /**
    * Test can move task between teamWfs that have same Task WorkDefinition
    */
   @org.junit.Test
   public void testMoveTasks_sameWorkDefinitions() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("testMoveTasks - sameWorkDefs");
      TaskArtifact taskToMove = AtsTestUtil.getOrCreateTaskOffTeamWf1();
      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();

      IAtsWorkDefinition taskWorkDef =
         AtsClientService.get().getWorkDefinitionAdmin().getWorkDefinitionForTask(taskToMove).getWorkDefinition();
      IAtsWorkDefinition newTaskWorkDef =
         AtsClientService.get().getWorkDefinitionAdmin().getWorkDefinitionForTaskNotYetCreated(
            teamWf2).getWorkDefinition();
      Assert.assertNotNull(taskWorkDef);

      Assert.assertEquals(taskWorkDef, newTaskWorkDef);
      TaskMover mover =
         new TaskMover(AtsClientService.get().getWorkDefinitionAdmin(), teamWf2, Arrays.asList(taskToMove));
      Result result = mover.moveTasks();

      Assert.assertTrue("This failed: " + result.getText(), result.isTrue());
   }

   /**
    * Test can't move task between teamWfs with differing Task WorkDefinitions
    */
   @org.junit.Test
   public void testMoveTasks_differentWorkDefinitions() throws OseeCoreException {
      List<IAtsTask> tasks = new ArrayList<>();
      tasks.add(task1);
      when(teamWf.getId()).thenReturn(34534L);
      when(teamWf2.getId()).thenReturn(9999L);
      when(task1.getParentTeamWorkflow()).thenReturn(teamWf);

      // fail when moving to same workflow as is already parent
      TaskMover mover = new TaskMover(workDefinitionAdmin, teamWf, tasks);
      Result result = mover.validate();
      Assert.assertTrue(result.isFalse());
      Assert.assertTrue(result.toString().contains("workflows are the same"));

      // move if task defines it's own work definition
      when(workDefinitionAdmin.isTaskOverridingItsWorkDefinition(task1)).thenReturn(true);
      mover = new TaskMover(workDefinitionAdmin, teamWf2, tasks);
      result = mover.validate();
      Assert.assertTrue(result.isTrue());

      when(workDefinitionAdmin.isTaskOverridingItsWorkDefinition(task1)).thenReturn(false);
      when(workDefinitionAdmin.getWorkDefinitionForTaskNotYetCreated(teamWf2)).thenReturn(match1);
      when(match1.getWorkDefinition()).thenReturn(taskWorkDef1);
      when(task1.getWorkDefinition()).thenReturn(taskWorkDef2);
      mover = new TaskMover(workDefinitionAdmin, teamWf2, tasks);
      result = mover.validate();
      Assert.assertTrue(result.isFalse());
      Assert.assertTrue(result.toString().contains("does not match current"));
   }

   /**
    * Test can move task to teamWf if it defines/overrides i t's own WorkDefinition; eg. WorkDef specified in Task
    * attribute
    */
   @org.junit.Test
   public void testMoveTasks_diffWorkDefinitionsButTaskOverride() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("testMoveTasks - diffWorkDefinitionsButTaskOverride");
      TaskArtifact taskToMove = AtsTestUtil.getOrCreateTaskOffTeamWf1();
      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();
      IAtsWorkDefinition taskWorkDef =
         AtsClientService.get().getWorkDefinitionAdmin().getWorkDefinitionForTask(taskToMove).getWorkDefinition();

      // create new task work def
      XResultData resultData = new XResultData();
      AtsClientService.get().getWorkDefinitionAdmin();
      IAtsWorkDefinition differentTaskWorkDef = AtsClientService.get().getWorkDefinitionAdmin().copyWorkDefinition(
         taskWorkDef.getName() + "2", taskWorkDef, resultData);
      Assert.assertFalse("Should be no errors: " + resultData.toString(), resultData.isErrors());
      resultData.clear();

      storeWorkDefinition(resultData, differentTaskWorkDef);

      Assert.assertFalse("Should be no errors: " + resultData.toString(), resultData.isErrors());
      AtsClientService.get().getWorkDefinitionAdmin().addWorkDefinition(differentTaskWorkDef);

      // set work definition override on task; move should go through
      taskToMove.setSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, differentTaskWorkDef.getName());
      taskToMove.persist("testMoveTasks - set workDef attribute on task");

      IAtsWorkDefinition newTaskWorkDef =
         AtsClientService.get().getWorkDefinitionAdmin().getWorkDefinitionForTaskNotYetCreated(
            teamWf2).getWorkDefinition();
      Assert.assertNotNull(taskWorkDef);
      Assert.assertSame("Should be same", taskWorkDef, newTaskWorkDef);
      TaskMover mover =
         new TaskMover(AtsClientService.get().getWorkDefinitionAdmin(), teamWf2, Arrays.asList(taskToMove));
      Result result = mover.moveTasks();

      Assert.assertTrue("This should pass: " + result.getText(), result.isTrue());
   }

   private void storeWorkDefinition(XResultData resultData, IAtsWorkDefinition differentTaskWorkDef) {
      JaxAtsWorkDef jaxWorkDef = new JaxAtsWorkDef();
      jaxWorkDef.setName(differentTaskWorkDef.getName());
      String workDefXml;
      try {
         workDefXml =
            AtsClientService.get().getWorkDefinitionAdmin().getStorageString(differentTaskWorkDef, resultData);
      } catch (Exception ex) {
         resultData.errorf("Exception getting storage string for work def [%s]: ex [%s]",
            differentTaskWorkDef.getName(), ex.getLocalizedMessage());
         return;
      }
      jaxWorkDef.setWorkDefDsl(workDefXml);
      AtsClientService.getConfigEndpoint().storeWorkDef(jaxWorkDef);
   }

}
