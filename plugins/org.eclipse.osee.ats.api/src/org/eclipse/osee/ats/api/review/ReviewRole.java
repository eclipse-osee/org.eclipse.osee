/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Ryan D. Brooks
 */
public class ReviewRole extends NamedIdBase {

   public static final ReviewRole Author = new ReviewRole(1L, "Author", ReviewRoleType.Author);
   public static final ReviewRole Moderator = new ReviewRole(2L, "Moderator", ReviewRoleType.Moderator);
   public static final ReviewRole ModeratorReviewer =
      new ReviewRole(3L, "Moderator/Reviewer", ReviewRoleType.Reviewer);;
   public static final ReviewRole Quality = new ReviewRole(4L, "Quality", ReviewRoleType.Quality);
   public static final ReviewRole Reviewer = new ReviewRole(5L, "Reviewer", ReviewRoleType.Reviewer);
   private final ReviewRoleType reviewRoleType;

   public ReviewRole(Long id, String name, ReviewRoleType reviewRoleType) {
      super(id, name);
      this.reviewRoleType = reviewRoleType;
   }

   public ReviewRoleType getReviewRoleType() {
      return reviewRoleType;
   }
}