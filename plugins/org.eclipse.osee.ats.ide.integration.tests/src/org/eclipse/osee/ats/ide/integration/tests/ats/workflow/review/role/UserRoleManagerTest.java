/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewRequiredMinimum;
import org.eclipse.osee.ats.api.review.ReviewRole;
import org.eclipse.osee.ats.api.review.ReviewRoleType;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.review.UserRoleError;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.review.UserRoleManager;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.junit.Assert;

/**
 * Test unit for {@link UserRole}
 *
 * @author Vaibhav Patel
 */
public class UserRoleManagerTest {

   @org.junit.Test
   public void testValidateReviewRoleRequiredMinimum() {
      // test peer reviews
      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();
      Collection<IAtsAbstractReview> reviews = AtsApiService.get().getReviewService().getReviews(teamWf);
      IAtsPeerToPeerReview rev1 = null;
      for (IAtsAbstractReview revArt : reviews) {
         if (revArt.getName().contains("algorithm") && revArt instanceof IAtsPeerToPeerReview) {
            rev1 = (IAtsPeerToPeerReview) revArt;
         }
      }

      if (rev1 != null) {
         ReviewRole TestRole1 = new ReviewRole(1L, "Test Role 1", ReviewRoleType.Reviewer);
         ReviewRole TestRole2 = new ReviewRole(2L, "Test Role 2", ReviewRoleType.Reviewer);
         List<ReviewRequiredMinimum> reviewRequiredMinimums1 = new ArrayList<>();
         reviewRequiredMinimums1.add(
            new ReviewRequiredMinimum(TestRole1, 1, DemoArtifactToken.SAW_PL_Requirements_TeamDef));
         List<ReviewRequiredMinimum> reviewRequiredMinimums2 = new ArrayList<>();
         reviewRequiredMinimums2.add(new ReviewRequiredMinimum(TestRole2, 1, DemoArtifactToken.SAW_Code));
         List<ReviewRequiredMinimum> reviewRequiredMinimums3 = new ArrayList<>();

         UserRoleManager userRoleManager = new UserRoleManager(rev1, AtsApiService.get());
         UserRoleError error1 = userRoleManager.validateReviewRequiredMinimum(rev1, reviewRequiredMinimums1);
         Assert.assertEquals(error1.isOK(), true);
         UserRoleError error2 = userRoleManager.validateReviewRequiredMinimum(rev1, reviewRequiredMinimums2);
         Assert.assertEquals(error2.isOK(), false);
         UserRoleError error3 = userRoleManager.validateReviewRequiredMinimum(rev1, reviewRequiredMinimums3);
         Assert.assertEquals(error3.isOK(), true);
      }

   }
}
