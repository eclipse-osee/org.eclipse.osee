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

package org.eclipse.osee.ats.api.review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Request and response data for finding a user's resolved, open peer review defects.
 *
 * @author Donald G. Dunne
 */
public class ReviewDefectData {

   private ArtifactId userArtifactId = ArtifactId.SENTINEL;
   private List<ArtifactId> peerReviewIds = new ArrayList<>();
   private List<ReviewDefectItem> reviewDefectItems = new ArrayList<>();
   private Map<Long, ArtifactId> defectIdToPeerReviewId = new HashMap<>();
   private Map<Long, ReviewDefectPeerReviewData> defectIdToPeerReviewData = new HashMap<>();
   private XResultData rd = new XResultData();

   public ArtifactId getUserArtifactId() {
      return userArtifactId;
   }

   public void setUserArtifactId(ArtifactId userArtifactId) {
      this.userArtifactId = userArtifactId;
   }

   public List<ArtifactId> getPeerReviewIds() {
      return peerReviewIds;
   }

   public void setPeerReviewIds(List<ArtifactId> peerReviewIds) {
      this.peerReviewIds = peerReviewIds;
   }

   public List<ReviewDefectItem> getReviewDefectItems() {
      return reviewDefectItems;
   }

   public void setReviewDefectItems(List<ReviewDefectItem> reviewDefectItems) {
      this.reviewDefectItems = reviewDefectItems;
   }

   public Map<Long, ArtifactId> getDefectIdToPeerReviewId() {
      return defectIdToPeerReviewId;
   }

   public void setDefectIdToPeerReviewId(Map<Long, ArtifactId> defectIdToPeerReviewId) {
      this.defectIdToPeerReviewId = defectIdToPeerReviewId;
   }

   public Map<Long, ReviewDefectPeerReviewData> getDefectIdToPeerReviewData() {
      return defectIdToPeerReviewData;
   }

   public void setDefectIdToPeerReviewData(Map<Long, ReviewDefectPeerReviewData> defectIdToPeerReviewData) {
      this.defectIdToPeerReviewData = defectIdToPeerReviewData;
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }
}
