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

package org.eclipse.osee.ats.api.review;

import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;

/**
 * @author Vaibhav Patel
 */

public class ReviewRequiredMinimum {

   private final ReviewRole reviewRole;
   private final int min;
   private final IAtsTeamDefinitionArtifactToken parentTeamDef;

   public ReviewRequiredMinimum(ReviewRole reviewRole, int min, IAtsTeamDefinitionArtifactToken parentTeamDef) {
      this.reviewRole = reviewRole;
      this.min = min;
      this.parentTeamDef = parentTeamDef;
   }

   public ReviewRole getReviewRole() {
      return reviewRole;
   }

   public int getMin() {
      return min;
   }

   public IAtsTeamDefinitionArtifactToken getParentTeamDef() {
      return parentTeamDef;
   }

}
