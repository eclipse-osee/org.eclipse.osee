/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.AssigneeColumn;
import org.eclipse.osee.ats.column.ParentIdColumn;
import org.eclipse.osee.ats.column.ParentStateColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.support.test.util.DemoWorkType;

/**
 * @tests ParentStateColumn
 * @tests ParentIdColumn
 * @author Donald G Dunne
 */
public class ParentStateAndIdColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      ActionArtifact actionArt = codeArt.getParentActionArtifact();

      Assert.assertEquals("", ParentStateColumn.getInstance().getColumnText(codeArt, AssigneeColumn.getInstance(), 0));
      Assert.assertEquals(actionArt.getHumanReadableId(),
         ParentIdColumn.getInstance().getColumnText(codeArt, AssigneeColumn.getInstance(), 0));

      PeerToPeerReviewArtifact peerArt =
         (PeerToPeerReviewArtifact) codeArt.getRelatedArtifact(AtsRelationTypes.TeamWorkflowToReview_Review);
      Assert.assertEquals(DefaultTeamState.Implement.name(),
         ParentStateColumn.getInstance().getColumnText(peerArt, AssigneeColumn.getInstance(), 0));
      Assert.assertEquals(codeArt.getHumanReadableId(),
         ParentIdColumn.getInstance().getColumnText(peerArt, AssigneeColumn.getInstance(), 0));

      Assert.assertEquals("", ParentStateColumn.getInstance().getColumnText(actionArt, AssigneeColumn.getInstance(), 0));

   }

}
