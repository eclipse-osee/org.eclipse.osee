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

package org.eclipse.osee.ats.rest.internal.demo;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.workflow.state.TaskStates;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd90CreateDemoTasksTest extends AbstractPopulateDemoDatabaseTest {

   public Pdd90CreateDemoTasksTest(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();
      List<String> taskNames = new LinkedList<>();
      taskNames.addAll(DemoUtil.Saw_Code_Committed_Task_Titles);
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWf)) {
         testTaskContents(task, TaskStates.InWork.getName(), TeamState.Implement.getName());
         taskNames.remove(task.getName());
         String assignees = task.getAssigneesStr();
         boolean containsJoe = assignees.contains(DemoUsers.Joe_Smith.getName());
         boolean containsKay = assignees.contains(DemoUsers.Kay_Jones.getName());
         assertTrue(containsJoe && containsKay);
      }
      if (!taskNames.isEmpty()) {
         assertEquals(String.format("Not all tasks exist.  [%s] remain", taskNames), taskNames.size(),
            atsApi.getTaskService().getTasks(teamWf).size());
      }

      teamWf = DemoUtil.getSawCodeUnCommittedWf();
      taskNames.clear();
      taskNames.addAll(DemoUtil.Saw_Code_UnCommitted_Task_Titles);
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWf)) {
         testTaskContents(task, TaskStates.InWork.getName(), TeamState.Implement.getName());
         taskNames.remove(task.getName());
         assertEquals(DemoUsers.Joe_Smith.getName(), task.getAssigneesStr());
      }
      if (!taskNames.isEmpty()) {
         assertEquals(String.format("Not all tasks exist.  [%s] remain", taskNames), taskNames.size(),
            atsApi.getTaskService().getTasks(teamWf).size());
      }

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
