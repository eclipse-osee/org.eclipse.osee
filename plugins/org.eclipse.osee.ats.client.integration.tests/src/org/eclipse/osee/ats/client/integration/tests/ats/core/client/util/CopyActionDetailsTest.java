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
package org.eclipse.osee.ats.client.integration.tests.ats.core.client.util;

import static org.junit.Assert.assertEquals;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil.AtsTestUtilState;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.util.CopyActionDetails;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   public static void cleanup() throws OseeCoreException {
      AtsTestUtil.cleanup();
   }

   @Test
   public void test01GetDetailsStringForTeamWf() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      String str = new CopyActionDetails(AtsTestUtil.getTeamWf()).getDetailsString();
      assertEquals(
         "\"Team Workflow\" - " + AtsTestUtil.getTeamWf().getAtsId() + " - \"AtsTestUtil - Team WF [CopyActionDetailsTest]\"",
         str);
   }

   @Test
   public void test02GetDetailsStringForTask() throws OseeCoreException {
      String str = new CopyActionDetails(AtsTestUtil.getOrCreateTaskOffTeamWf1()).getDetailsString();
      assertEquals(
         "\"Task\" - " + AtsTestUtil.getOrCreateTaskOffTeamWf1().getAtsId() + " - \"AtsTestUtil - Task [CopyActionDetailsTest]\"",
         str);
   }

   @Test
   public void test03GetDetailsStringForDecisionReview() throws OseeCoreException {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      DecisionReviewArtifact review =
         AtsTestUtil.getOrCreateDecisionReview(ReviewBlockType.Commit, AtsTestUtilState.Analyze, changes);
      String str = new CopyActionDetails(review).getDetailsString();
      assertEquals("\"Decision Review\" - " + review.getAtsId() + " - \"AtsTestUtil Test Decision Review\"", str);
      changes.execute();
   }

   @Test
   public void test04GetDetailsStringForPeerReview() throws OseeCoreException {
      PeerToPeerReviewArtifact review = AtsTestUtil.getOrCreatePeerReview(ReviewBlockType.None,
         AtsTestUtilState.Analyze, AtsClientService.get().createChangeSet("test04GetDetailsStringForPeerReview"));
      String str = new CopyActionDetails(review).getDetailsString();
      assertEquals("\"PeerToPeer Review\" - " + review.getAtsId() + " - \"AtsTestUtil Test Peer Review\"", str);
      review.persist(getClass().getSimpleName());
   }
}
