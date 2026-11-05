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

package org.eclipse.osee.ats.rest.internal.review;

import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewDefectData;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.ReviewDefectPeerReviewData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Server-side review query operations.
 *
 * @author Donald G. Dunne
 */
public class ReviewOperations {

   private final AtsApi atsApi;

   public ReviewOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public ReviewDefectData getOpenDefectItems(ReviewDefectData data) {
      if (data == null) {
         data = new ReviewDefectData();
      }
      if (data.getRd() == null) {
         data.setRd(new XResultData());
      }
      if (data.getUserArtifactId() == null || !data.getUserArtifactId().isValid()) {
         data.getRd().error("User artifact id is required.");
         return data;
      }

      try {
         AtsUser user = atsApi.getUserService().getUserById(data.getUserArtifactId());
         if (user == null || Strings.isInValid(user.getUserId())) {
            data.getRd().errorf("User artifact [%s] was not found.", data.getUserArtifactId());
            return data;
         }

         String userId = user.getUserId();
         for (ArtifactToken reviewArt : atsApi.getQueryService().createQuery(AtsArtifactTypes.PeerToPeerReview).andAttr(
            AtsAttributeTypes.CurrentStateType, StateType.Working.name(), QueryOption.EXACT_MATCH_OPTIONS).andAttr(
               AtsAttributeTypes.ReviewDefect, userId, QueryOption.CONTAINS_MATCH_OPTIONS).getArtifacts()) {
            IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(reviewArt);
            if (!(workItem instanceof IAtsPeerToPeerReview)) {
               continue;
            }
            IAtsPeerToPeerReview review = (IAtsPeerToPeerReview) workItem;
            ReviewDefectPeerReviewData reviewData =
               new ReviewDefectPeerReviewData(review.getAtsId(), atsApi.getAttributeResolver().getSoleAttributeValue(
                  review, AtsAttributeTypes.ReviewDueDate, (Date) null), review.getName());
            boolean matchedReview = false;
            for (ReviewDefectItem defect : review.getDefectManager().getDefectItems()) {
               if (userId.equals(defect.getUserId()) && !defect.isClosed() && Strings.isValid(defect.getResolution())) {
                  data.getReviewDefectItems().add(defect);
                  data.getDefectIdToPeerReviewId().put(defect.getId(), review.getArtifactId());
                  data.getDefectIdToPeerReviewData().put(defect.getId(), reviewData);
                  data.getRd().logf("Peer Review [%s]: Defect [%s] - %s", review.getIdString(), defect.getId(),
                     defect.getDescription());
                  matchedReview = true;
               }
            }
            if (matchedReview) {
               data.getPeerReviewIds().add(review.getArtifactId());
            }
         }
         data.getRd().logf("Found %d open resolved defect item(s) in %d peer review(s) for %s.",
            data.getReviewDefectItems().size(), data.getPeerReviewIds().size(), userId);
      } catch (Exception ex) {
         data.getRd().errorf("Error finding open defect items: %s", ex.getMessage());
      }
      return data;
   }
}
