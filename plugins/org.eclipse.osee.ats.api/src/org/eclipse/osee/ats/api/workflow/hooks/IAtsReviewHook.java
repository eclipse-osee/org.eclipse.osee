/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.workflow.hooks;

import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IAtsReviewHook {

   /**
    * Notification that a review was created. This allows the extension to do necessary initial tasks after the review
    * workflow artifact is created. All changes made to review will be persisted after this call.
    */
   default public void reviewCreated(IAtsAbstractReview reviewWf) {
      // do nothing
   }

   default public void checkDefectCanClose(ReviewDefectItem defectItems, boolean closed, XResultData rd) {
      // do nothing
   }

   public String getDescription();

}
