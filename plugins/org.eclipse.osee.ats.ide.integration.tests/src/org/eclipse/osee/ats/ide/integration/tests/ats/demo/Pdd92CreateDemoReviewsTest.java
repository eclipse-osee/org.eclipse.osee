/*********************************************************************
 * Copyright (c) 2017 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.demo;

import java.util.Collection;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd92CreateDemoReviews;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd92CreateDemoReviewsTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() throws Exception {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd92CreateDemoReviews create = new Pdd92CreateDemoReviews();
      create.run();

      // test decision review 1
      IAtsTeamWorkflow teamWf1 = DemoUtil.getButtonWDoesntWorkOnSituationPageWf();
      Collection<IAtsAbstractReview> reviews1 = AtsApiService.get().getReviewService().getReviews(teamWf1);
      Assert.assertEquals("Should only 1 review", 1, reviews1.size());
      IAtsAbstractReview review1 = reviews1.iterator().next();
      String reviewTitle1 = AtsApiService.get().getReviewService().getValidateReviewTitle(teamWf1);
      testReviewContents(review1, reviewTitle1, DecisionReviewState.Followup.getName(), DemoUsers.Joe_Smith.getName());

      // test decision review2
      IAtsTeamWorkflow teamWf2 = DemoUtil.getProblemInDiagramTree_TeamWfWf();
      Collection<IAtsAbstractReview> reviews2 = AtsApiService.get().getReviewService().getReviews(teamWf2);
      Assert.assertEquals("Should only 1 review", 1, reviews1.size());
      IAtsAbstractReview review2 = reviews2.iterator().next();
      String reviewTitle2 = AtsApiService.get().getReviewService().getValidateReviewTitle(teamWf2);
      testReviewContents(review2, reviewTitle2, DecisionReviewState.Completed.getName());

      // test peer reviews
      IAtsTeamWorkflow teamWf3 = DemoUtil.getSawCodeCommittedWf();
      Collection<IAtsAbstractReview> reviews3 = AtsApiService.get().getReviewService().getReviews(teamWf3);
      Assert.assertEquals("Should only be two reviews", 2, reviews3.size());
      IAtsAbstractReview rev1 = null;
      IAtsAbstractReview rev2 = null;
      for (IAtsAbstractReview revArt : reviews3) {
         if (revArt.getName().contains("algorithm")) {
            rev1 = revArt;
         } else {
            rev2 = revArt;
         }
      }
      Assert.assertNotNull(rev1);
      Assert.assertNotNull(rev2);
      testReviewContents(rev1, "2 - Peer Review algorithm used in code", PeerToPeerReviewState.Review.getName(),
         new String[] {DemoUsers.Joe_Smith.getName(), DemoUsers.Kay_Jones.getName()});
      testReviewContents(rev2, "1 - Peer Review first set of code changes", PeerToPeerReviewState.Prepare.getName(),
         new String[] {AtsCoreUsers.UNASSIGNED_USER.getName()});

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
