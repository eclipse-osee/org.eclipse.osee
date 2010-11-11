/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.EstimatedHoursColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @tests EstimatedHoursColumn
 * @author Donald G Dunne
 */
public class EstimatedHoursColumnTest {

   @AfterClass
   @BeforeClass
   public static void cleanup() throws Exception {
      DemoTestUtil.cleanupSimpleTest(EstimatedHoursColumnTest.class.getSimpleName());
   }

   @org.junit.Test
   public void testGetDateAndStrAndColumnText() throws Exception {
      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtil.getAtsBranch(), EstimatedHoursColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt1 =
         DemoTestUtil.createSimpleAction(EstimatedHoursColumnTest.class.getSimpleName(), transaction);

      ActionArtifact actionArt = teamArt1.getParentActionArtifact();
      TeamWorkFlowArtifact teamArt2 =
         DemoTestUtil.addTeamWorkflow(actionArt, EstimatedHoursColumnTest.class.getSimpleName(), transaction);
      TaskArtifact taskArt1 = teamArt1.createNewTask(EstimatedHoursColumnTest.class.getSimpleName());
      taskArt1.persist(transaction);
      TaskArtifact taskArt2 = teamArt1.createNewTask(EstimatedHoursColumnTest.class.getSimpleName());
      taskArt2.persist(transaction);
      PeerToPeerReviewArtifact peerArt =
         ReviewManager.createNewPeerToPeerReview(teamArt1, getClass().getSimpleName(),
            teamArt1.getStateMgr().getCurrentStateName(), transaction);
      peerArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(actionArt));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(teamArt1));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(teamArt2));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(taskArt1));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(taskArt2));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(peerArt));

      teamArt1.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 1.4);
      teamArt1.persist();
      Assert.assertEquals(1.4, EstimatedHoursColumn.getEstimatedHours(actionArt));
      Assert.assertEquals(1.4, EstimatedHoursColumn.getEstimatedHours(teamArt1));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(teamArt2));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(taskArt1));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(taskArt2));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(peerArt));

      taskArt1.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 1.4);
      taskArt1.persist();
      Assert.assertEquals(2.8, EstimatedHoursColumn.getEstimatedHours(actionArt));
      Assert.assertEquals(2.8, EstimatedHoursColumn.getEstimatedHours(teamArt1));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(teamArt2));
      Assert.assertEquals(1.4, EstimatedHoursColumn.getEstimatedHours(taskArt1));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(taskArt2));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(peerArt));

      peerArt.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 4.0);
      peerArt.persist();
      Assert.assertEquals(6.8, EstimatedHoursColumn.getEstimatedHours(actionArt));
      Assert.assertEquals(6.8, EstimatedHoursColumn.getEstimatedHours(teamArt1));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(teamArt2));
      Assert.assertEquals(1.4, EstimatedHoursColumn.getEstimatedHours(taskArt1));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(taskArt2));
      Assert.assertEquals(4.0, EstimatedHoursColumn.getEstimatedHours(peerArt));

      teamArt2.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 2.1);
      teamArt2.persist();
      Assert.assertEquals(8.9, EstimatedHoursColumn.getEstimatedHours(actionArt));
      Assert.assertEquals(6.8, EstimatedHoursColumn.getEstimatedHours(teamArt1));
      Assert.assertEquals(2.1, EstimatedHoursColumn.getEstimatedHours(teamArt2));
      Assert.assertEquals(1.4, EstimatedHoursColumn.getEstimatedHours(taskArt1));
      Assert.assertEquals(0.0, EstimatedHoursColumn.getEstimatedHours(taskArt2));
      Assert.assertEquals(4.0, EstimatedHoursColumn.getEstimatedHours(peerArt));

      taskArt2.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.1);
      taskArt2.persist();
      Assert.assertEquals(9.0, EstimatedHoursColumn.getEstimatedHours(actionArt));
      Assert.assertEquals(6.9, EstimatedHoursColumn.getEstimatedHours(teamArt1));
      Assert.assertEquals(2.1, EstimatedHoursColumn.getEstimatedHours(teamArt2));
      Assert.assertEquals(1.4, EstimatedHoursColumn.getEstimatedHours(taskArt1));
      Assert.assertEquals(0.1, EstimatedHoursColumn.getEstimatedHours(taskArt2));
      Assert.assertEquals(4.0, EstimatedHoursColumn.getEstimatedHours(peerArt));

   }
}
