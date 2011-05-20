/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.task.TaskManager;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.support.test.util.DemoWorkType;
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

      TaskArtifact taskArt = codeArt.getTaskArtifacts().iterator().next();
      Collection<IBasicUser> taskAssignees = new HashSet<IBasicUser>();
      taskAssignees.addAll(codeArt.getStateMgr().getAssignees());
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), getClass().getSimpleName());
      Result result = TaskManager.transitionToCompleted(taskArt, 0.0, 2, transaction);
      Assert.assertEquals(true, result.isTrue());
      taskArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals("6", NumberOfTasksColumn.getInstance().getColumnText(codeArt, null, 0));
      Assert.assertEquals("5", NumberOfTasksRemainingColumn.getInstance().getColumnText(codeArt, null, 0));

      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), getClass().getSimpleName());
      result = TaskManager.transitionToInWork(taskArt, taskAssignees.iterator().next(), 0, -2, transaction);
      Assert.assertEquals(true, result.isTrue());
      taskArt.persist(transaction);
      transaction.execute();

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
