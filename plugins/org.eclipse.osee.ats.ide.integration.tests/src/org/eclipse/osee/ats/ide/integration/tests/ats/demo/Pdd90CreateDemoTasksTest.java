/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.demo;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd90CreateDemoTasks;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskStates;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd90CreateDemoTasksTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() throws Exception {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd90CreateDemoTasks create = new Pdd90CreateDemoTasks();
      create.run();

      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      List<String> taskNames = new LinkedList<>();
      taskNames.addAll(DemoUtil.Saw_Code_Committed_Task_Titles);
      for (IAtsTask task : AtsApiService.get().getTaskService().getTasks(teamWf)) {
         testTaskContents((TaskArtifact) task, TaskStates.InWork.getName(), TeamState.Implement.getName());
         taskNames.remove(task.getName());
         Assert.assertEquals(DemoUsers.Joe_Smith_And_Kay_Jones, task.getAssigneesStr());
      }
      if (!taskNames.isEmpty()) {
         Assert.assertEquals(String.format("Not all tasks exist.  [%s] remain", taskNames), taskNames.size(),
            AtsApiService.get().getTaskService().getTasks(teamWf));
      }

      teamWf = DemoUtil.getSawCodeUnCommittedWf();
      taskNames.clear();
      taskNames.addAll(DemoUtil.Saw_Code_UnCommitted_Task_Titles);
      for (IAtsTask task : AtsApiService.get().getTaskService().getTasks(teamWf)) {
         testTaskContents((TaskArtifact) task, TaskStates.InWork.getName(), TeamState.Implement.getName());
         taskNames.remove(task.getName());
         Assert.assertEquals(DemoUsers.Joe_Smith.getName(), task.getAssigneesStr());
      }
      if (!taskNames.isEmpty()) {
         Assert.assertEquals(String.format("Not all tasks exist.  [%s] remain", taskNames), taskNames.size(),
            AtsApiService.get().getTaskService().getTasks(teamWf));
      }

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
