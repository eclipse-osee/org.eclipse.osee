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
import org.eclipse.osee.ats.column.OriginatorColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.support.test.util.DemoUsers;
import org.eclipse.osee.support.test.util.DemoWorkType;

/**
 * @tests OriginatorColumn
 * @author Donald G Dunne
 */
public class OriginatorColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertEquals(UserManager.getUser(DemoUsers.Joe_Smith).getName(),
         OriginatorColumn.getInstance().getColumnText(reqArt, AssigneeColumn.getInstance(), 0));

      ActionArtifact actionArt = reqArt.getParentActionArtifact();
      Assert.assertEquals(UserManager.getUser(DemoUsers.Joe_Smith).getName(),
         OriginatorColumn.getInstance().getColumnText(actionArt, AssigneeColumn.getInstance(), 0));

   }

}
