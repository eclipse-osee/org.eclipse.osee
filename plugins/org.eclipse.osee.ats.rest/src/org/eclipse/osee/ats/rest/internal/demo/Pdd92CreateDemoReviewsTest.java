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

package org.eclipse.osee.ats.rest.internal.demo;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd92CreateDemoReviewsTest extends AbstractPopulateDemoDatabaseTest {

   public Pdd92CreateDemoReviewsTest(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Collection<IAtsWorkItem> workItems = atsApi.getQueryService().getWorkItems(AtsArtifactTypes.AbstractReview);
      int num123 = 0, autoDec = 0, autoPeer = 0, resolution = 0;
      for (IAtsWorkItem workItem : workItems) {
         if (!workItem.isReview()) {
            rd.errorf("Must be review, instead %s\n", workItem.getArtifactType().toStringWithId());
         }
         String name = workItem.getName();
         if (name.startsWith("1") || name.startsWith("2") || name.startsWith("3")) {
            num123++;
         } else if (name.startsWith("Auto-created Decision")) {
            autoDec++;
         } else if (name.startsWith("Auto-created Peer")) {
            autoPeer++;
         } else if (name.startsWith("Is the resolution")) {
            resolution++;
         }
      }
      rd.assertEquals("num123", 3, num123);
      rd.assertEquals("autoDec", 5, autoDec);
      rd.assertEquals("autoPeer", 5, autoPeer);
      rd.assertEquals("resolution", 2, resolution);
      if (rd.isErrors()) {
         return;
      }

      // test decision review 1
      IAtsTeamWorkflow teamWf1 = DemoUtil.getButtonWDoesntWorkOnSituationPageWf();
      Collection<IAtsAbstractReview> reviews1 = atsApi.getReviewService().getReviews(teamWf1);
      assertEquals("Should only 1 review", 1, reviews1.size());
      IAtsAbstractReview review1 = reviews1.iterator().next();
      String reviewTitle1 = atsApi.getReviewService().getValidateReviewTitle(teamWf1);
      testReviewContents(review1, reviewTitle1, DecisionReviewState.Followup.getName(), DemoUsers.Joe_Smith.getName());

      // test decision review2
      IAtsTeamWorkflow teamWf2 = DemoUtil.getProblemInTree_TeamWfWf();
      Collection<IAtsAbstractReview> reviews2 = atsApi.getReviewService().getReviews(teamWf2);
      assertEquals("Should only 1 review", 1, reviews1.size());
      IAtsAbstractReview review2 = reviews2.iterator().next();
      String reviewTitle2 = atsApi.getReviewService().getValidateReviewTitle(teamWf2);
      testReviewContents(review2, reviewTitle2, DecisionReviewState.Completed.getName());

      // test peer reviews
      IAtsTeamWorkflow teamWf3 = DemoUtil.getSawCodeCommittedWf();
      Collection<IAtsAbstractReview> reviews3 = atsApi.getReviewService().getReviews(teamWf3);
      assertEquals("Should only be two reviews", 2, reviews3.size());
      IAtsAbstractReview rev1 = null;
      IAtsAbstractReview rev2 = null;
      for (IAtsAbstractReview revArt : reviews3) {
         if (revArt.getName().contains("algorithm")) {
            rev1 = revArt;
         } else {
            rev2 = revArt;
         }
      }

      assertNotNull(rev1);
      assertNotNull(rev2);
      testReviewContents(rev1, "2 - Peer Review algorithm used in code", PeerToPeerReviewState.Review.getName(),
         new String[] {DemoUsers.Joe_Smith.getName(), DemoUsers.Kay_Jones.getName()});
      testReviewContents(rev2, "1 - Peer Review first set of code changes", PeerToPeerReviewState.Prepare.getName(),
         new String[] {AtsCoreUsers.UNASSIGNED_USER.getName()});

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
