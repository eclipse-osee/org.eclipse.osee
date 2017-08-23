/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.demo;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.populate.Pdd90CreateDemoTasks;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.task.TaskStates;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
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
      List<String> taskNames = new LinkedList<String>();
      taskNames.addAll(DemoUtil.Saw_Code_Committed_Task_Titles);
      for (TaskArtifact task : teamWf.getTaskArtifacts()) {
         testTaskContents(task, TaskStates.InWork.getName(), TeamState.Implement.getName());
         taskNames.remove(task.getName());
         Assert.assertEquals("Joe Smith; Kay Jones", task.getStateMgr().getAssigneesStr());
      }
      if (!taskNames.isEmpty()) {
         Assert.assertEquals(String.format("Not all tasks exist.  [%s] remain", taskNames), taskNames.size(),
            teamWf.getTaskArtifacts());
      }

      teamWf = DemoUtil.getSawCodeUnCommittedWf();
      taskNames.clear();
      taskNames.addAll(DemoUtil.Saw_Code_UnCommitted_Task_Titles);
      for (TaskArtifact task : teamWf.getTaskArtifacts()) {
         testTaskContents(task, TaskStates.InWork.getName(), TeamState.Implement.getName());
         taskNames.remove(task.getName());
         Assert.assertEquals("Joe Smith", task.getStateMgr().getAssigneesStr());
      }
      if (!taskNames.isEmpty()) {
         Assert.assertEquals(String.format("Not all tasks exist.  [%s] remain", taskNames), taskNames.size(),
            teamWf.getTaskArtifacts());
      }

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
