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
package org.eclipse.osee.ats.ide.integration.tests.ats.editor;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.editor.WfePrint;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Demo test that will run the SMAPrint action against Demo populated actions to ensure that nothing has broken.This
 * test simply runs the html generation portion of SMAPrint, ensures the results are of a reasonable length and looks
 * for exceptions at the end.
 *
 * @author Donald G. Dunne
 */
public class WfePrintTest {

   @Test
   public void testSMAPrint() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeUnCommittedWf();
      Assert.assertNotNull(teamWf);

      WfePrint smaPrint = new WfePrint(teamWf);
      XResultData resultData = smaPrint.getResultData();
      Assert.assertNotNull(resultData);
      // Make sure it's a reasonable length
      String html = XResultDataUI.getReport(resultData, "report").getManipulatedHtml();
      Assert.assertTrue(html.length() > 5600);

      PeerToPeerReviewArtifact peerArt = (PeerToPeerReviewArtifact) ReviewManager.getReviews(teamWf).iterator().next();
      smaPrint = new WfePrint(peerArt);
      resultData = smaPrint.getResultData();
      Assert.assertNotNull(resultData);
      // Make sure it's a reasonable length
      Assert.assertTrue(
         "Expected > 3500 chars, was " + XResultDataUI.getReport(resultData, "report").getManipulatedHtml().length(),
         XResultDataUI.getReport(resultData, "report").getManipulatedHtml().length() > 3500);

      TaskArtifact taskArt = null;
      for (IAtsTask task : AtsClientService.get().getTaskService().getTasks(teamWf)) {
         if (task.getName().equals("Deploy release")) {
            taskArt = (TaskArtifact) task.getStoreObject();
         }
      }
      Assert.assertNotNull(taskArt);
      smaPrint = new WfePrint(taskArt);
      resultData = smaPrint.getResultData();
      Assert.assertNotNull(resultData);
      // Make sure it's a reasonable length
      Assert.assertTrue(XResultDataUI.getReport(resultData, "report").getManipulatedHtml().length() > 2600);

      teamWf = (TeamWorkFlowArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.TeamWorkflow,
         "Button S doesn't work on help", AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(teamWf);
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) ReviewManager.getReviews(teamWf).iterator().next();
      smaPrint = new WfePrint(decArt);
      resultData = smaPrint.getResultData();
      Assert.assertNotNull(resultData);
      // Make sure it's a reasonable length
      Assert.assertTrue(XResultDataUI.getReport(resultData, "report").getManipulatedHtml().length() > 2900);

      TestUtil.severeLoggingEnd(monitorLog);
   }
}
