/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.defect;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.review.AtsReviewEndpointApi;
import org.eclipse.osee.ats.api.review.ReviewDefectData;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for the directed "Open Defect Items" search exposed through
 * {@link AtsReviewEndpointApi#getOpenDefectItems}. Exercises the "My Open Defects" (Joe Smith) and "User Open Defects"
 * (Kay Wheeler) cases against the demo data seeded by Pdd97CreateDemoDefectReviews, and confirms completed, closed, and
 * unresolved defects are excluded.
 *
 * @author Donald G. Dunne
 */
public class OpenDefectItemsTest {

   private AtsApi atsApi;
   private AtsReviewEndpointApi reviewEp;

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
      reviewEp = atsApi.getServerEndpoints().getReviewEp();
   }

   @Test
   public void testMyOpenDefects_JoeSmith() {
      ReviewDefectData result = getOpenDefectItems(DemoUsers.Joe_Smith.getId());

      Assert.assertFalse(result.getRd().toString(), result.getRd().isErrors());
      // 2 resolved, open, unclosed defects created by Joe in the one working review
      Assert.assertEquals(2, result.getReviewDefectItems().size());
      Assert.assertEquals(1, result.getPeerReviewIds().size());

      String joeUserId = atsApi.getUserService().getUserById(DemoUsers.Joe_Smith).getUserId();
      for (ReviewDefectItem defect : result.getReviewDefectItems()) {
         Assert.assertEquals(joeUserId, defect.getUserId());
         Assert.assertFalse("Closed defects must be excluded", defect.isClosed());
         Assert.assertFalse("Unresolved defects must be excluded", defect.getResolution().isEmpty());
         Assert.assertNotNull(result.getDefectIdToPeerReviewId().get(defect.getId()));
         Assert.assertNotNull(result.getDefectIdToPeerReviewData().get(defect.getId()));
      }
   }

   @Test
   public void testUserOpenDefects_KayWheeler() {
      ReviewDefectData result = getOpenDefectItems(DemoUsers.Kay_Wheeler.getId());

      Assert.assertFalse(result.getRd().toString(), result.getRd().isErrors());
      // 1 resolved, open, unclosed defect created by Kay Wheeler in the one working review
      Assert.assertEquals(1, result.getReviewDefectItems().size());
      Assert.assertEquals(1, result.getPeerReviewIds().size());

      String kayUserId = atsApi.getUserService().getUserById(DemoUsers.Kay_Wheeler).getUserId();
      ReviewDefectItem defect = result.getReviewDefectItems().iterator().next();
      Assert.assertEquals(kayUserId, defect.getUserId());
      Assert.assertFalse(defect.isClosed());
      Assert.assertFalse(defect.getResolution().isEmpty());
   }

   @Test
   public void testInvalidUser() {
      ReviewDefectData data = new ReviewDefectData();
      data.setUserArtifactId(ArtifactId.SENTINEL);
      ReviewDefectData result = reviewEp.getOpenDefectItems(data);

      Assert.assertTrue("Invalid user must produce an error", result.getRd().isErrors());
      Assert.assertTrue(result.getReviewDefectItems().isEmpty());
   }

   private ReviewDefectData getOpenDefectItems(Long userArtId) {
      ReviewDefectData data = new ReviewDefectData();
      data.setUserArtifactId(ArtifactId.valueOf(userArtId));
      return reviewEp.getOpenDefectItems(data);
   }

}
