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

package org.eclipse.osee.ats.rest.internal.demo;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.review.ReviewDefectData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.rest.internal.review.ReviewOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Server-side validation that {@link Pdd97CreateDemoDefectReviews} seeded the expected open, resolved, unclosed peer
 * review defects and that completed/closed/unresolved defects are excluded.
 *
 * @author Donald G. Dunne
 */
public class Pdd97CreateDemoDefectReviewsTest extends AbstractPopulateDemoDatabaseTest {

   public Pdd97CreateDemoDefectReviewsTest(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());

      // My Open Defects for Joe Smith: 2 resolved/open defects in 1 working review
      ReviewDefectData joeResult = getOpenDefectItems(DemoUsers.Joe_Smith);
      assertTrue(!joeResult.getRd().isErrors());
      assertEquals("Joe Smith open defect count", 2, joeResult.getReviewDefectItems().size());
      assertEquals("Joe Smith peer review count", 1, joeResult.getPeerReviewIds().size());

      // User Open Defects for Kay Wheeler: 1 resolved/open defect in 1 working review
      ReviewDefectData kayResult = getOpenDefectItems(DemoUsers.Kay_Wheeler);
      assertTrue(!kayResult.getRd().isErrors());
      assertEquals("Kay Wheeler open defect count", 1, kayResult.getReviewDefectItems().size());
      assertEquals("Kay Wheeler peer review count", 1, kayResult.getPeerReviewIds().size());
   }

   private ReviewDefectData getOpenDefectItems(org.eclipse.osee.framework.core.data.UserToken userToken) {
      AtsUser user = atsApi.getUserService().getUserById(userToken);
      ReviewDefectData data = new ReviewDefectData();
      data.setUserArtifactId(ArtifactId.valueOf(user.getId()));
      return new ReviewOperations(atsApi).getOpenDefectItems(data);
   }

}
