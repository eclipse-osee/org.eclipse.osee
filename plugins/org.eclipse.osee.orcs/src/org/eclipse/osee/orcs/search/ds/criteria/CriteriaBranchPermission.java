/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.orcs.search.ds.criteria;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.search.ds.Criteria;
import org.eclipse.osee.orcs.search.ds.Options;

/**
 * @author Audrey Denk
 */
public class CriteriaBranchPermission extends Criteria implements BranchCriteria {

   private final boolean includePermission;
   private final ArtifactId userArtId;
   public CriteriaBranchPermission(ArtifactId userArtId, boolean includePermission) {
      this.includePermission = includePermission;
      this.userArtId = userArtId;
   }

   public boolean isIncludePermission() {
      return includePermission;
   }

   public ArtifactId getUserArtId() {
      return userArtId;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkExpressionFailOnTrue(userArtId == null, "User Art Id cannot be null");
   }

   @Override
   public String toString() {
      return "CriteriaBranchPermission [userArtId=" + userArtId.getId() + "]";
   }
}
