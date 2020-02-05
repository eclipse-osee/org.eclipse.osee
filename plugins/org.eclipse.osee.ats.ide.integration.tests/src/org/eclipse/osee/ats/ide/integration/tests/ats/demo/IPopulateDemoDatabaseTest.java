/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.demo;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewState;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public interface IPopulateDemoDatabaseTest {

   @Test
   public default void testPopulate() {
      Assert.assertTrue(true);
   }

   default void testTaskContents(TaskArtifact task, String currentStateName, String relatedToState) {
      Assert.assertEquals(currentStateName, task.getCurrentStateName());
      Assert.assertEquals(relatedToState, task.getSoleAttributeValue(AtsAttributeTypes.RelatedToState, ""));
   }

   default void testReviewContents(IAtsAbstractReview review, String title, String currentStateName, String... assigneeStrs) {
      Assert.assertEquals(title, review.getName());
      Assert.assertEquals(currentStateName, review.getStateMgr().getCurrentStateName());

      Collection<String> assigneeNames = AtsObjects.getNames(review.getStateMgr().getAssignees());

      Assert.assertEquals(assigneeStrs.length, assigneeNames.size());
      for (String assignee : assigneeStrs) {
         if (!assigneeNames.contains(assignee)) {
            Assert.fail(String.format("revArt.getStateMgr().getAssignees(), does not contain user: %s", assignee));
         }
      }
   }

   default void testTeamContents(IAtsTeamWorkflow teamWf, String title, String priority, String versionName, String currentStateName, String actionableItemStr, String assigneeStr, ArtifactTypeToken artifactType, IAtsTeamDefinition teamDef) {
      Assert.assertEquals(currentStateName, teamWf.getStateMgr().getCurrentStateName());
      Assert.assertEquals(priority,
         AtsClientService.get().getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, ""));
      // want targeted version, not error/exception
      String targetedVerStr = "";
      IAtsVersion version = AtsClientService.get().getVersionService().getTargetedVersion(teamWf);
      if (version != null) {
         targetedVerStr = version.getName();
      }
      Assert.assertEquals(versionName, targetedVerStr);
      Assert.assertEquals(artifactType, teamWf.getArtifactType());
      Assert.assertEquals(teamDef, teamWf.getTeamDefinition());
      Assert.assertEquals(assigneeStr, teamWf.getStateMgr().getAssigneesStr());
      Assert.assertEquals(actionableItemStr,
         AtsClientService.get().getActionableItemService().getActionableItemsStr(teamWf));
   }

   default void testSwDesign1PeerAnd1DecisionReview(TeamWorkFlowArtifact designTeam) {
      Assert.assertNotNull(designTeam);
      PeerToPeerReviewArtifact peerArt = null;
      DecisionReviewArtifact decArt = null;
      for (AbstractReviewArtifact revArt1 : ReviewManager.getReviews(designTeam)) {
         if (revArt1.getName().contains("PeerToPeer")) {
            peerArt = (PeerToPeerReviewArtifact) revArt1;
         } else {
            decArt = (DecisionReviewArtifact) revArt1;
         }
      }
      Assert.assertNotNull(peerArt);
      Assert.assertNotNull(decArt);
      testReviewContents(peerArt,
         "Auto-created Peer Review from ruleId atsAddPeerToPeerReview.test.addPeerToPeerReview.Authorize.None.TransitionTo",
         PeerToPeerReviewState.Prepare.getName(), "UnAssigned");
      testReviewContents(decArt,
         "Auto-created Decision Review from ruleId: atsAddDecisionReview.test.addDecisionReview.Analyze.None.TransitionTo",
         DecisionReviewState.Decision.getName(), "UnAssigned");

   }

}
