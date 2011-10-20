/*
 * Created on Oct 20, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.operation;

import java.util.Arrays;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.action.ActionArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class MoveTeamWorkflowsOperationTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testDoWork() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      ActionArtifact actArt = AtsTestUtil.getActionArt();
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      ActionArtifact actArt2 = AtsTestUtil.getActionArt2();
      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();

      MoveTeamWorkflowsOperation operation =
         new MoveTeamWorkflowsOperation("Move", teamWf, Arrays.asList(teamWf2), "new title");
      operation.doWork(null);

      Assert.assertEquals("Parent Actions should be same", teamWf.getParentActionArtifact(),
         teamWf.getParentActionArtifact());
      Assert.assertEquals("new title", actArt.getName());
      Assert.assertTrue("Action Artifact 2 should be deleted", actArt2.isDeleted());
      Assert.assertFalse("No artifact should be dirty",
         actArt.isDirty() && teamWf.isDirty() && actArt2.isDirty() && teamWf2.isDirty());
   }
}
