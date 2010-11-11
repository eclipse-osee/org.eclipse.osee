/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.AssigneeColumn;
import org.eclipse.osee.ats.column.BranchStatusColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.support.test.util.DemoWorkType;

/**
 * @tests BranchStatusColumn
 * @author Donald G Dunne
 */
public class BranchStatusColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertEquals("Working",
         BranchStatusColumn.getInstance().getColumnText(reqArt, AssigneeColumn.getInstance(), 0));

      TeamWorkFlowArtifact testArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Test);
      Assert.assertEquals("", BranchStatusColumn.getInstance().getColumnText(testArt, AssigneeColumn.getInstance(), 0));

      ActionArtifact actionArt = reqArt.getParentActionArtifact();
      Assert.assertEquals("Working",
         BranchStatusColumn.getInstance().getColumnText(actionArt, AssigneeColumn.getInstance(), 0));

      TeamWorkFlowArtifact reqArt2 =
         (TeamWorkFlowArtifact) DemoTestUtil.getCommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertEquals("Committed",
         BranchStatusColumn.getInstance().getColumnText(reqArt2, AssigneeColumn.getInstance(), 0));

   }

}
