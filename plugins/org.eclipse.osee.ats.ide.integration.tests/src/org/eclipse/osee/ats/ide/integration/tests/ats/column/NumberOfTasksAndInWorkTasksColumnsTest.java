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

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.column.NumberOfTasksColumnUI;
import org.eclipse.osee.ats.ide.column.NumberOfTasksRemainingColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.task.TaskTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests NumberOfTasksColumn
 * @tests NumberOfTasksRemainingColumn
 * @author Donald G. Dunne
 */
public class NumberOfTasksAndInWorkTasksColumnsTest {

   @org.junit.Test
   public void getColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals("6", NumberOfTasksColumnUI.getInstance().getColumnText(codeArt, null, 0));
      Assert.assertEquals("6", NumberOfTasksRemainingColumnUI.getInstance().getColumnText(codeArt, null, 0));

      IAtsTask task = AtsApiService.get().getTaskService().getTasks(codeArt).iterator().next();
      Collection<AtsUser> taskAssignees = new HashSet<>();
      taskAssignees.addAll(codeArt.getAssignees());
      Result result = TaskTestUtil.transitionToCompleted((TaskArtifact) task, 0.0, 2);
      Assert.assertEquals(true, result.isTrue());

      Assert.assertEquals("6", NumberOfTasksColumnUI.getInstance().getColumnText(codeArt, null, 0));
      Assert.assertEquals("5", NumberOfTasksRemainingColumnUI.getInstance().getColumnText(codeArt, null, 0));

      result = TaskTestUtil.transitionToInWork((TaskArtifact) task, taskAssignees.iterator().next(), 0, -2);
      Assert.assertEquals(true, result.isTrue());

      Assert.assertEquals("6", NumberOfTasksColumnUI.getInstance().getColumnText(codeArt, null, 0));
      Assert.assertEquals("6", NumberOfTasksRemainingColumnUI.getInstance().getColumnText(codeArt, null, 0));

      TeamWorkFlowArtifact testArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Test);
      Assert.assertEquals("", NumberOfTasksColumnUI.getInstance().getColumnText(testArt, null, 0));
      Assert.assertEquals("", NumberOfTasksRemainingColumnUI.getInstance().getColumnText(testArt, null, 0));

      Artifact actionArt = (Artifact) codeArt.getParentAction().getStoreObject();
      Assert.assertEquals("6", NumberOfTasksColumnUI.getInstance().getColumnText(actionArt, null, 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
