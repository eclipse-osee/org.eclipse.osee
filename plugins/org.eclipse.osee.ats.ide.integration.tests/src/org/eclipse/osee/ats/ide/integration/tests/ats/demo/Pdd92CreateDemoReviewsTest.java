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
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd92CreateDemoReviews;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewState;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewState;
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
      IAtsTeamWorkflow teamWf = DemoUtil.getButtonWDoesntWorkOnSituationPageWf();
      Collection<IAtsAbstractReview> reviews = AtsClientService.get().getReviewService().getReviews(teamWf);
      Assert.assertEquals("Should only 1 review", 1, reviews.size());
      IAtsAbstractReview review = reviews.iterator().next();
      testReviewContents(review, "Is the resolution of this Action valid?", DecisionReviewState.Followup.getName(),
         DemoUsers.Joe_Smith.getName());

      // test decision review2
      IAtsTeamWorkflow teamWf1 = DemoUtil.getProblemInDiagramTree_TeamWfWf();
      Collection<IAtsAbstractReview> reviews1 = AtsClientService.get().getReviewService().getReviews(teamWf1);
      Assert.assertEquals("Should only 1 review", 1, reviews.size());
      IAtsAbstractReview review1 = reviews1.iterator().next();
      testReviewContents(review1, "Is the resolution of this Action valid?", DecisionReviewState.Completed.getName());

      // test peer reviews reviews
      IAtsTeamWorkflow teamWf3 = DemoUtil.getSawCodeCommittedWf();
      Collection<IAtsAbstractReview> reviews3 = AtsClientService.get().getReviewService().getReviews(teamWf3);
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
      testReviewContents(rev1, "Peer Review algorithm used in code", PeerToPeerReviewState.Review.getName(),
         new String[] {DemoUsers.Joe_Smith.getName(), DemoUsers.Kay_Jones.getName()});
      testReviewContents(rev2, "Peer Review first set of code changes", PeerToPeerReviewState.Prepare.getName(),
         DemoUsers.Joe_Smith.getName());

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
