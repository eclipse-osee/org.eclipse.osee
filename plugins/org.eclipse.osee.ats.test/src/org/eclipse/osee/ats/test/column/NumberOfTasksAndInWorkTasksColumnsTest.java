/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.NumberOfTasksColumn;
import org.eclipse.osee.ats.column.NumberOfTasksRemainingColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.TransitionOption;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.support.test.util.DemoWorkType;

/**
 * @tests NumberOfTasksColumn
 * @tests NumberOfTasksRemainingColumn
 * @author Donald G Dunne
 */
public class NumberOfTasksAndInWorkTasksColumnsTest {

   @org.junit.Test
   public void getColumnText() throws Exception {
      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals("6", NumberOfTasksColumn.getInstance().getColumnText(codeArt, null, 0));
      Assert.assertEquals("6", NumberOfTasksRemainingColumn.getInstance().getColumnText(codeArt, null, 0));

      TaskArtifact taskArt = codeArt.getTaskArtifacts().iterator().next();
      Collection<User> taskAssignees = taskArt.getStateMgr().getAssignees();
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), getClass().getSimpleName());
      taskArt.transitionToCompleted(2, transaction, TransitionOption.OverrideTransitionValidityCheck);
      taskArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals("6", NumberOfTasksColumn.getInstance().getColumnText(codeArt, null, 0));
      Assert.assertEquals("5", NumberOfTasksRemainingColumn.getInstance().getColumnText(codeArt, null, 0));

      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), getClass().getSimpleName());
      taskArt.transitionToInWork(taskAssignees.iterator().next(), 0, -2, transaction,
         TransitionOption.OverrideTransitionValidityCheck);
      taskArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals("6", NumberOfTasksColumn.getInstance().getColumnText(codeArt, null, 0));
      Assert.assertEquals("6", NumberOfTasksRemainingColumn.getInstance().getColumnText(codeArt, null, 0));

      TeamWorkFlowArtifact testArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Test);
      Assert.assertEquals("", NumberOfTasksColumn.getInstance().getColumnText(testArt, null, 0));
      Assert.assertEquals("", NumberOfTasksRemainingColumn.getInstance().getColumnText(testArt, null, 0));

      ActionArtifact actionArt = codeArt.getParentActionArtifact();
      Assert.assertEquals("6", NumberOfTasksColumn.getInstance().getColumnText(actionArt, null, 0));

   }
}
