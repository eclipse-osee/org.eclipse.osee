/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.util;

import static org.junit.Assert.assertEquals;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.ats.workflow.AtsTestUtil.AtsTestUtilState;
import org.eclipse.osee.ats.util.CopyActionDetails;
import org.eclipse.osee.ats.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Donald G. Dunne
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CopyActionDetailsTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() {
      AtsTestUtil.cleanup();
   }

   @Test
   public void test01GetDetailsStringForTeamWf() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      String str = new CopyActionDetails(AtsTestUtil.getTeamWf()).getDetailsString();
      assertEquals(
         "\"Team Workflow\" - " + AtsTestUtil.getTeamWf().getAtsId() + " - \"AtsTestUtil - Team WF [CopyActionDetailsTest]\"",
         str);
   }

   @Test
   public void test02GetDetailsStringForTask() {
      String str = new CopyActionDetails(AtsTestUtil.getOrCreateTaskOffTeamWf1()).getDetailsString();
      assertEquals(
         "\"Task\" - " + AtsTestUtil.getOrCreateTaskOffTeamWf1().getAtsId() + " - \"AtsTestUtil - Task [CopyActionDetailsTest]\"",
         str);
   }

   @Test
   public void test03GetDetailsStringForDecisionReview() {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      DecisionReviewArtifact review =
         AtsTestUtil.getOrCreateDecisionReview(ReviewBlockType.Commit, AtsTestUtilState.Analyze, changes);
      String str = new CopyActionDetails(review).getDetailsString();
      assertEquals("\"Decision Review\" - " + review.getAtsId() + " - \"AtsTestUtil Test Decision Review\"", str);
      changes.execute();
   }

   @Test
   public void test04GetDetailsStringForPeerReview() {
      PeerToPeerReviewArtifact review = AtsTestUtil.getOrCreatePeerReview(ReviewBlockType.None,
         AtsTestUtilState.Analyze, AtsClientService.get().createChangeSet("test04GetDetailsStringForPeerReview"));
      String str = new CopyActionDetails(review).getDetailsString();
      assertEquals("\"PeerToPeer Review\" - " + review.getAtsId() + " - \"AtsTestUtil Test Peer Review\"", str);
      review.persist(getClass().getSimpleName());
   }

   @Test
   public void test05GetDetailsStringForTeamWfWithTeamDefConfig() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();
      IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.ActionDetailsFormat,
         "<atsid> - <name> - <artType> - <changeType>");
      changes.setSoleAttributeValue((IAtsWorkItem) teamWf, AtsAttributeTypes.LegacyPcrId, "PCR100");
      changes.execute();

      String str = new CopyActionDetails(teamWf).getDetailsString();
      assertEquals(teamWf.getAtsId() + " - AtsTestUtil - Team WF [CopyActionDetailsTest] - Team Workflow - Improvement",
         str);

      changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.ActionDetailsFormat,
         "[<actionatsid>] - [<atsid>]<legacypcrid> - <name>");
      changes.setSoleAttributeValue((IAtsWorkItem) teamWf, AtsAttributeTypes.LegacyPcrId, "PCR100");
      changes.execute();

      IAtsAction action = teamWf.getParentAction();
      str = new CopyActionDetails(teamWf).getDetailsString();
      assertEquals(
         "[" + action.getAtsId() + "] - [" + teamWf.getAtsId() + "] - [PCR100] - AtsTestUtil - Team WF [CopyActionDetailsTest]",
         str);

   }

}
