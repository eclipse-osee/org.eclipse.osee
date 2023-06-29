/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.cr;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.demo.DemoArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstDefinition;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.cr.demo.XTaskEstDemoWidget;
import org.eclipse.osee.ats.ide.workflow.cr.taskest.TaskEstNameProvider;
import org.eclipse.osee.ats.ide.workflow.cr.taskest.TaskEstOperations;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.Before;

/**
 * This test will take the created CR Workflow and test that the task estimating widget loads correctly and tasks get
 * generated correctly.
 *
 * @author Donald G. Dunne
 */
public class XTaskEstWidgetTest implements TaskEstNameProvider {

   AtsApi atsApi;

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
   }

   @Override
   public String getTaskName(TaskEstDefinition ted) {
      return String.format("Estimates for %s", ted.getName());
   }

   @SuppressWarnings("unlikely-arg-type")
   @org.junit.Test
   public void testCreate() {

      ArtifactToken artifactByName = atsApi.getQueryService().getArtifactByName(
         DemoArtifactTypes.DemoChangeRequestTeamWorkflow, CreateNewDemoChangeRequestBlamTest.TITLE);
      Assert.assertNotNull(artifactByName);
      IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(artifactByName);

      XTaskEstDemoWidget mgr = new XTaskEstDemoWidget();
      Collection<TaskEstDefinition> taskDefs = mgr.getTaskEstDefs();

      Assert.assertEquals("3 UserGroup tasks and 2 code tasks should be found", 5, taskDefs.size());

      for (TaskEstDefinition ted : taskDefs) {
         ted.setChecked(true);
      }
      TaskEstOperations ops =
         new TaskEstOperations(atsApi, DemoWorkDefinitions.WorkDef_Task_Demo_For_CR_Estimating, this);
      NewTaskSet newTaskSet = ops.createCannedTasks(teamWf, taskDefs);

      Assert.assertFalse(newTaskSet.isErrors());
      Assert.assertEquals(5, newTaskSet.getTaskData().getTasks().size());

      JaxAtsTask reqTask = null;
      for (JaxAtsTask jTask : newTaskSet.getTaskData().getTasks()) {
         if (jTask.getName().contains("Requirements")) {
            reqTask = jTask;
            break;
         }
      }
      Assert.assertNotNull(reqTask);

      ArtifactToken artifact = atsApi.getQueryService().getArtifact(reqTask.getId());
      IAtsTask rTask = atsApi.getWorkItemService().getTask(artifact);
      Assert.assertNotNull(rTask);
      Assert.assertEquals("Estimates for Requirements", rTask.getName());
      Assert.assertEquals("desc", rTask.getDescription());
      Assert.assertEquals(2, rTask.getAssignees().size());
      Assert.assertTrue(rTask.getAssignees().contains(DemoUsers.Joe_Smith));
      Assert.assertTrue(rTask.getAssignees().contains(DemoUsers.Kay_Jones));
   }

}
