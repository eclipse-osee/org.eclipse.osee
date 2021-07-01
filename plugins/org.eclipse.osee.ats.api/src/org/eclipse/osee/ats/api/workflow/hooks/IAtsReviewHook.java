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

/**
 * @author Donald G. Dunne
 */
public interface IAtsReviewHook {

   /**
    * Notification that a review was created. This allows the extension to do necessary initial tasks after the review
    * workflow artifact is created. All changes made to review will be persisted after this call.
    */
   public void reviewCreated(IAtsAbstractReview reviewWf);

   public String getDescription();

}
