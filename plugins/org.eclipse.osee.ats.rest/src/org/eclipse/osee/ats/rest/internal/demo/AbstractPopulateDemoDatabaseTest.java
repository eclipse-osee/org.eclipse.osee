/*******************************************************************************
 * Copyright (c) 2024 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.demo;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractPopulateDemoDatabaseTest {

   protected final XResultData rd;
   protected final AtsApi atsApi;

   public AbstractPopulateDemoDatabaseTest(XResultData rd, AtsApi atsApi) {
      this.rd = rd;
      this.atsApi = atsApi;
   }

   public abstract void run();

   protected void assertEquals(int expected, int actual) {
      if (expected != actual) {
         rd.errorf("Expected %s but actual was %s\n", expected, actual);
      }
   }

   protected void assertNotNull(Object obj) {
      if (obj == null) {
         rd.errorf("Object should not be null\n");
      }
   }

   protected IAtsTeamDefinition getTeamDef(ArtifactId artifactId) {
      return atsApi.getTeamDefinitionService().getTeamDefinitionById(artifactId);
   }

   protected void testTeamContents(IAtsTeamWorkflow teamWf, String title, String priority, String versionName,
      String currentStateName, String actionableItemStr, String assigneeStr, ArtifactTypeToken artifactType,
      IAtsTeamDefinition teamDef) {
      assertEquals(currentStateName, teamWf.getCurrentStateName());
      assertEquals(priority,
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, ""));
      // want targeted version, not error/exception
      String targetedVerStr = "";
      IAtsVersion version = atsApi.getVersionService().getTargetedVersion(teamWf);
      if (version != null) {
         targetedVerStr = version.getName();
      }
      assertEquals(versionName, targetedVerStr);
      assertEquals(artifactType, teamWf.getArtifactType());
      assertEquals(teamDef, teamWf.getTeamDefinition());
      assertEquals(assigneeStr, teamWf.getAssigneesStr());
      assertEquals(actionableItemStr, atsApi.getActionableItemService().getActionableItemsStr(teamWf));
   }

   private void assertEquals(String expected, String actual) {
      if (!expected.equals(actual)) {
         rd.errorf("Expected %s but actual was %s\n", expected, actual);
      }
   }

   protected void assertEquals(ArtifactTypeToken expected, ArtifactTypeToken actual) {
      if (!expected.equals(actual)) {
         rd.errorf("Expected %s but actual was %s\n", expected, actual);
      }
   }

   protected void assertEquals(IAtsObject expected, IAtsObject actual) {
      if (!expected.equals(actual)) {
         rd.errorf("Expected %s but actual was %s\n", expected, actual);
      }
   }

   protected void assertTrue(boolean actual) {
      if (!actual) {
         rd.errorf("Expected true\n");
      }
   }

   public void testSwDesign1PeerAnd1DecisionReview(IAtsTeamWorkflow designTeamWf) {
      assertNotNull(designTeamWf);
      IAtsPeerToPeerReview peerWf = null;
      IAtsDecisionReview decWf = null;
      for (IAtsAbstractReview revWf : atsApi.getReviewService().getReviews(designTeamWf)) {
         if (revWf.getName().contains("PeerToPeer")) {
            peerWf = (IAtsPeerToPeerReview) revWf;
         } else {
            decWf = (IAtsDecisionReview) revWf;
         }
      }
      assertNotNull(peerWf);
      assertNotNull(decWf);
      testReviewContents(peerWf,
         "Auto-created Peer Review from ruleId atsAddPeerToPeerReview.test.addPeerToPeerReview.Authorize.None.TransitionTo",
         PeerToPeerReviewState.Prepare.getName(), "UnAssigned");
      testReviewContents(decWf,
         "Auto-created Decision Review from ruleId: atsAddDecisionReview.test.addDecisionReview.Analyze.None.TransitionTo",
         DecisionReviewState.Decision.getName(), "UnAssigned");
   }

   public void testReviewContents(IAtsAbstractReview review, String title, String currentStateName,
      String... assigneeStrs) {
      assertEquals(title, review.getName());
      assertEquals(currentStateName, review.getCurrentStateName());

      Collection<String> assigneeNames = Named.getNames(review.getAssignees());

      assertEquals(assigneeStrs.length, assigneeNames.size());
      for (String assignee : assigneeStrs) {
         if (!assigneeNames.contains(assignee)) {
            fail(String.format("revArt.getAssignees(), does not contain user: %s", assignee));
         }
      }
   }

   protected void fail(String message) {
      rd.errorf("Fail: %s\n", message);
   }

}
