/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.Date;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.workflow.EstimatedHoursUtil;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @tests EstimatedHoursColumn
 * @author Donald G. Dunne
 */
public class EstimatedHoursColumnTest {

   @AfterClass
   @BeforeClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(EstimatedHoursColumnTest.class.getSimpleName());
   }

   @org.junit.Test
   public void testGetDateAndStrAndColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtil.getAtsBranch(), EstimatedHoursColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt1 =
         DemoTestUtil.createSimpleAction(EstimatedHoursColumnTest.class.getSimpleName(), transaction);

      Artifact actionArt = teamArt1.getParentActionArtifact();
      TeamWorkFlowArtifact teamArt2 =
         DemoTestUtil.addTeamWorkflow(actionArt, EstimatedHoursColumnTest.class.getSimpleName(), transaction);
      TaskArtifact taskArt1 =
         teamArt1.createNewTask(EstimatedHoursColumnTest.class.getSimpleName(), new Date(), UserManager.getUser());
      taskArt1.persist(transaction);
      TaskArtifact taskArt2 =
         teamArt1.createNewTask(EstimatedHoursColumnTest.class.getSimpleName(), new Date(), UserManager.getUser());
      taskArt2.persist(transaction);
      PeerToPeerReviewArtifact peerArt =
         PeerToPeerReviewManager.createNewPeerToPeerReview(teamArt1, getClass().getSimpleName(),
            teamArt1.getStateMgr().getCurrentStateName(), transaction);
      peerArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(actionArt), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      teamArt1.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 1.4);
      teamArt1.persist();
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(actionArt), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      taskArt1.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 1.4);
      taskArt1.persist();
      Assert.assertEquals(2.8, EstimatedHoursUtil.getEstimatedHours(actionArt), 0);
      Assert.assertEquals(2.8, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      peerArt.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 4.0);
      peerArt.persist();
      Assert.assertEquals(6.8, EstimatedHoursUtil.getEstimatedHours(actionArt), 0);
      Assert.assertEquals(6.8, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(4.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      teamArt2.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 2.1);
      teamArt2.persist();
      Assert.assertEquals(8.9, EstimatedHoursUtil.getEstimatedHours(actionArt), 0);
      Assert.assertEquals(6.8, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(2.1, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(4.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      taskArt2.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.1);
      taskArt2.persist();
      Assert.assertEquals(9.0, EstimatedHoursUtil.getEstimatedHours(actionArt), 0);
      Assert.assertEquals(6.9, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(2.1, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.1, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(4.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
