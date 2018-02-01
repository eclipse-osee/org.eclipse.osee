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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
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

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(EstimatedHoursColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt1 =
         (TeamWorkFlowArtifact) DemoTestUtil.createSimpleAction(EstimatedHoursColumnTest.class.getSimpleName(),
            changes).getStoreObject();

      IAtsAction action = teamArt1.getParentAction();
      TeamWorkFlowArtifact teamArt2 = (TeamWorkFlowArtifact) DemoTestUtil.addTeamWorkflow(action,
         EstimatedHoursColumnTest.class.getSimpleName(), changes).getStoreObject();
      PeerToPeerReviewArtifact peerArt = PeerToPeerReviewManager.createNewPeerToPeerReview(teamArt1,
         getClass().getSimpleName(), teamArt1.getStateMgr().getCurrentStateName(), changes);
      changes.add(peerArt);
      changes.execute();

      Collection<IAtsTask> createTasks = AtsClientService.get().getTaskService().createTasks(teamArt1,
         Arrays.asList(getClass().getSimpleName(), getClass().getSimpleName() + " 2"), null, new Date(),
         AtsClientService.get().getUserService().getCurrentUser(), null, null, null, getClass().getSimpleName());

      Artifact taskArt1 = null, taskArt2 = null;
      for (IAtsTask task : createTasks) {
         if (task.getName().endsWith("2")) {
            taskArt2 = (Artifact) AtsClientService.get().getQueryService().getArtifact(task);
         } else {
            taskArt1 = (Artifact) AtsClientService.get().getQueryService().getArtifact(task);
         }
      }

      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(action), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      teamArt1.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 1.4);
      teamArt1.persist(getClass().getSimpleName());
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(action), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      Assert.assertNotNull(taskArt1);

      taskArt1.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 1.4);
      taskArt1.persist(getClass().getSimpleName());
      Assert.assertEquals(2.8, EstimatedHoursUtil.getEstimatedHours(action), 0);
      Assert.assertEquals(2.8, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      peerArt.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 4.0);
      peerArt.persist(getClass().getSimpleName());
      Assert.assertEquals(6.8, EstimatedHoursUtil.getEstimatedHours(action), 0);
      Assert.assertEquals(6.8, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(4.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      teamArt2.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 2.1);
      teamArt2.persist(getClass().getSimpleName());
      Assert.assertEquals(8.9, EstimatedHoursUtil.getEstimatedHours(action), 0);
      Assert.assertEquals(6.8, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(2.1, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.0, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(4.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      Assert.assertNotNull(taskArt2);
      taskArt2.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.1);
      taskArt2.persist(getClass().getSimpleName());
      Assert.assertEquals(9.0, EstimatedHoursUtil.getEstimatedHours(action), 0);
      Assert.assertEquals(6.9, EstimatedHoursUtil.getEstimatedHours(teamArt1), 0);
      Assert.assertEquals(2.1, EstimatedHoursUtil.getEstimatedHours(teamArt2), 0);
      Assert.assertEquals(1.4, EstimatedHoursUtil.getEstimatedHours(taskArt1), 0);
      Assert.assertEquals(0.1, EstimatedHoursUtil.getEstimatedHours(taskArt2), 0);
      Assert.assertEquals(4.0, EstimatedHoursUtil.getEstimatedHours(peerArt), 0);

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
