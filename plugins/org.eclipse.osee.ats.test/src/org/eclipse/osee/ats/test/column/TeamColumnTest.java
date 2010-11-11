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
import org.eclipse.osee.ats.column.TeamColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.support.test.util.DemoWorkType;

/**
 * @tests TeamColumn
 * @author Donald G Dunne
 */
public class TeamColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertEquals("SAW Requirements",
         TeamColumn.getInstance().getColumnText(reqArt, AssigneeColumn.getInstance(), 0));

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals("SAW Code", TeamColumn.getInstance().getColumnText(codeArt, AssigneeColumn.getInstance(), 0));

      ActionArtifact actionArt = reqArt.getParentActionArtifact();
      Assert.assertEquals("", TeamColumn.getInstance().getColumnText(actionArt, AssigneeColumn.getInstance(), 0));

   }

}
