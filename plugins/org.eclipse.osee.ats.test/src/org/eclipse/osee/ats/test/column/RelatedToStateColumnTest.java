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
import org.eclipse.osee.ats.column.RelatedToStateColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.support.test.util.DemoWorkType;

/**
 * @tests RelatedToStateColumn
 * @author Donald G Dunne
 */
public class RelatedToStateColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals("",
         RelatedToStateColumn.getInstance().getColumnText(codeArt, AssigneeColumn.getInstance(), 0));

      PeerToPeerReviewArtifact peerArt =
         (PeerToPeerReviewArtifact) codeArt.getRelatedArtifact(AtsRelationTypes.TeamWorkflowToReview_Review);
      Assert.assertEquals("Implement",
         RelatedToStateColumn.getInstance().getColumnText(peerArt, AssigneeColumn.getInstance(), 0));

      TaskArtifact taskArt =
         (TaskArtifact) codeArt.getRelatedArtifacts(AtsRelationTypes.SmaToTask_Task).iterator().next();
      Assert.assertEquals("Implement",
         RelatedToStateColumn.getInstance().getColumnText(taskArt, AssigneeColumn.getInstance(), 0));

      TeamWorkFlowArtifact reqArt = (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals("", RelatedToStateColumn.getInstance().getColumnText(reqArt, AssigneeColumn.getInstance(), 0));

      ActionArtifact actionArt = reqArt.getParentActionArtifact();
      Assert.assertEquals("",
         RelatedToStateColumn.getInstance().getColumnText(actionArt, AssigneeColumn.getInstance(), 0));

   }

}
