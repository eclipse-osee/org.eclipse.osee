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
public class ReviewRoleType extends NamedIdBase {

   public static final ReviewRoleType Author = new ReviewRoleType(1L, "Author");
   public static final ReviewRoleType Moderator = new ReviewRoleType(2L, "Moderator");
   public static final ReviewRoleType ModeratorReviewer = new ReviewRoleType(3L, "Moderator/Reviewer");
   public static final ReviewRoleType Quality = new ReviewRoleType(4L, "Quality");
   public static final ReviewRoleType Reviewer = new ReviewRoleType(5L, "Reviewer");

   public ReviewRoleType(Long id, String name) {
      super(id, name);
   }
}