/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.column;

import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.client.workflow.EstimatedHoursUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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

      AtsChangeSet changes = new AtsChangeSet(EstimatedHoursColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt1 =
         DemoTestUtil.createSimpleAction(EstimatedHoursColumnTest.class.getSimpleName(), changes);

      Artifact actionArt = teamArt1.getParentActionArtifact();
      TeamWorkFlowArtifact teamArt2 =
         DemoTestUtil.addTeamWorkflow(actionArt, EstimatedHoursColumnTest.class.getSimpleName(), changes);
      TaskArtifact taskArt1 =
         teamArt1.createNewTask(EstimatedHoursColumnTest.class.getSimpleName(), new Date(),
            AtsClientService.get().getUserService().getCurrentUser(), changes);
      changes.add(taskArt1);
      TaskArtifact taskArt2 =
         teamArt1.createNewTask(EstimatedHoursColumnTest.class.getSimpleName(), new Date(),
            AtsClientService.get().getUserService().getCurrentUser(), changes);
      changes.add(taskArt2);
      PeerToPeerReviewArtifact peerArt =
         PeerToPeerReviewManager.createNewPeerToPeerReview(teamArt1, getClass().getSimpleName(),
            teamArt1.getStateMgr().getCurrentStateName(), changes);
      changes.add(peerArt);
      changes.execute();

      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(actionArt), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      teamArt1.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 1.4);
      teamArt1.persist(getClass().getSimpleName());
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(actionArt), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      taskArt1.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 1.4);
      taskArt1.persist(getClass().getSimpleName());
      Assert.assertEquals(2.8, EstimatedHoursUtil.getEstimatedHours(actionArt), 0);
      Assert.assertEquals(2.8, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      peerArt.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 4.0);
      peerArt.persist(getClass().getSimpleName());
      Assert.assertEquals(6.8, EstimatedHoursUtil.getEstimatedHours(actionArt), 0);
      Assert.assertEquals(6.8, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(4.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      teamArt2.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 2.1);
      teamArt2.persist(getClass().getSimpleName());
      Assert.assertEquals(8.9, EstimatedHoursUtil.getEstimatedHours(actionArt), 0);
      Assert.assertEquals(6.8, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(2.1, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(4.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      taskArt2.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.1);
      taskArt2.persist(getClass().getSimpleName());
      Assert.assertEquals(9.0, EstimatedHoursUtil.getEstimatedHours(actionArt), 0);
      Assert.assertEquals(6.9, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(2.1, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.1, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(4.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
