/*
 * Created on Apr 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.cases;

import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAPrint;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
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
public class SMAPrintTest {

   @Test
   public void testSMAPrint() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      IArtifactType DemoCodeTeamWorkflow = new ArtifactType("ABRNqDKnpGEKAyUm49gA", "Demo Code Team Workflow", false);

      TeamWorkFlowArtifact teamArt =
            (TeamWorkFlowArtifact) ArtifactQuery.getArtifactFromTypeAndName(DemoCodeTeamWorkflow,
                  "SAW (uncommitted) More Reqt Changes for Diagram View", AtsUtil.getAtsBranch());
      Assert.assertNotNull(teamArt);

      SMAPrint smaPrint = new SMAPrint(teamArt);
      XResultData resultData = smaPrint.getResultData();
      Assert.assertNotNull(resultData);
      // Make sure it's a reasonable length 
      Assert.assertTrue(resultData.getReport("report").getManipulatedHtml().length() > 7000);

      PeerToPeerReviewArtifact peerArt = (PeerToPeerReviewArtifact) ReviewManager.getReviews(teamArt).iterator().next();
      smaPrint = new SMAPrint(peerArt);
      resultData = smaPrint.getResultData();
      Assert.assertNotNull(resultData);
      // Make sure it's a reasonable length 
      Assert.assertTrue(resultData.getReport("report").getManipulatedHtml().length() > 3500);

      TaskArtifact taskArt = null;
      for (TaskArtifact taskArtifact : teamArt.getTaskArtifacts()) {
         if (taskArtifact.getName().equals("Deploy release")) {
            taskArt = taskArtifact;
         }
      }
      Assert.assertNotNull(taskArt);
      smaPrint = new SMAPrint(taskArt);
      resultData = smaPrint.getResultData();
      Assert.assertNotNull(resultData);
      // Make sure it's a reasonable length 
      Assert.assertTrue(resultData.getReport("report").getManipulatedHtml().length() > 2700);

      teamArt =
            (TeamWorkFlowArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.TeamWorkflow,
                  "Button S doesn't work on help", AtsUtil.getAtsBranch());
      Assert.assertNotNull(teamArt);
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) ReviewManager.getReviews(teamArt).iterator().next();
      smaPrint = new SMAPrint(decArt);
      resultData = smaPrint.getResultData();
      Assert.assertNotNull(resultData);
      // Make sure it's a reasonable length 
      Assert.assertTrue(resultData.getReport("report").getManipulatedHtml().length() > 2900);

      TestUtil.severeLoggingEnd(monitorLog);
   }
}
