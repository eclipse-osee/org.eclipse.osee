/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.column;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.workflow.task.TaskTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.column.NumberOfTasksColumn;
import org.eclipse.osee.ats.column.NumberOfTasksRemainingColumn;
import org.eclipse.osee.ats.demo.api.DemoWorkType;
import org.eclipse.osee.ats.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
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
      Assert.assertEquals("6", NumberOfTasksColumn.getInstance().getColumnText(codeArt, null, 0));
      Assert.assertEquals("6", NumberOfTasksRemainingColumn.getInstance().getColumnText(codeArt, null, 0));

      IAtsTask task = AtsClientService.get().getTaskService().getTasks(codeArt).iterator().next();
      Collection<IAtsUser> taskAssignees = new HashSet<>();
      taskAssignees.addAll(codeArt.getStateMgr().getAssignees());
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      Result result = TaskTestUtil.transitionToCompleted((TaskArtifact) task, 0.0, 2, changes);
      Assert.assertEquals(true, result.isTrue());
      changes.execute();

      Assert.assertEquals("6", NumberOfTasksColumn.getInstance().getColumnText(codeArt, null, 0));
      Assert.assertEquals("5", NumberOfTasksRemainingColumn.getInstance().getColumnText(codeArt, null, 0));

      changes.clear();
      result = TaskTestUtil.transitionToInWork((TaskArtifact) task, taskAssignees.iterator().next(), 0, -2, changes);
      Assert.assertEquals(true, result.isTrue());
      changes.execute();

      Assert.assertEquals("6", NumberOfTasksColumn.getInstance().getColumnText(codeArt, null, 0));
      Assert.assertEquals("6", NumberOfTasksRemainingColumn.getInstance().getColumnText(codeArt, null, 0));

      TeamWorkFlowArtifact testArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Test);
      Assert.assertEquals("", NumberOfTasksColumn.getInstance().getColumnText(testArt, null, 0));
      Assert.assertEquals("", NumberOfTasksRemainingColumn.getInstance().getColumnText(testArt, null, 0));

      Artifact actionArt = codeArt.getParentActionArtifact();
      Assert.assertEquals("6", NumberOfTasksColumn.getInstance().getColumnText(actionArt, null, 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
