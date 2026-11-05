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

import java.util.Date;

/**
 * Peer review metadata returned with a defect search result.
 */
public class ReviewDefectPeerReviewData {

   private String reviewAtsId = "";
   private Date needByDate;
   private String title = "";

   public ReviewDefectPeerReviewData() {
      // For JAX-RS serialization
   }

   public ReviewDefectPeerReviewData(String reviewAtsId, Date needByDate, String title) {
      this.reviewAtsId = reviewAtsId;
      this.needByDate = needByDate;
      this.title = title;
   }

   public String getReviewAtsId() {
      return reviewAtsId;
   }

   public void setReviewAtsId(String reviewAtsId) {
      this.reviewAtsId = reviewAtsId;
   }

   public Date getNeedByDate() {
      return needByDate;
   }

   public void setNeedByDate(Date needByDate) {
      this.needByDate = needByDate;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }
}
