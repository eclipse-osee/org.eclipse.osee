/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.AssigneeColumn;
import org.eclipse.osee.ats.column.PriorityColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.support.test.util.DemoWorkType;

/**
 * @tests PriorityColumn
 * @author Donald G Dunne
 */
public class PriorityColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertEquals("1", PriorityColumn.getInstance().getColumnText(reqArt, AssigneeColumn.getInstance(), 0));

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals("1", PriorityColumn.getInstance().getColumnText(codeArt, AssigneeColumn.getInstance(), 0));

      PeerToPeerReviewArtifact peerArt =
         (PeerToPeerReviewArtifact) codeArt.getRelatedArtifact(AtsRelationTypes.TeamWorkflowToReview_Review);
      Assert.assertEquals("", PriorityColumn.getInstance().getColumnText(peerArt, AssigneeColumn.getInstance(), 0));

      TaskArtifact taskArt =
         (TaskArtifact) codeArt.getRelatedArtifacts(AtsRelationTypes.SmaToTask_Task).iterator().next();
      Assert.assertEquals("", PriorityColumn.getInstance().getColumnText(taskArt, AssigneeColumn.getInstance(), 0));

      ActionArtifact actionArt = reqArt.getParentActionArtifact();
      Assert.assertEquals("1", PriorityColumn.getInstance().getColumnText(actionArt, AssigneeColumn.getInstance(), 0));

   }

}
