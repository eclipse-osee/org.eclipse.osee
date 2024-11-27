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

package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Ryan T. Baldwin
 */
public class CriteriaAssociatedArtIds extends Criteria implements BranchCriteria {

   private final List<ArtifactId> associatedArtIds;

   public CriteriaAssociatedArtIds(List<ArtifactId> associatedArtIds) {
      this.associatedArtIds = associatedArtIds;
   }

   public List<ArtifactId> getAssociatedArtIds() {
      return associatedArtIds;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkExpressionFailOnTrue(associatedArtIds.stream().anyMatch(artId -> artId.isInvalid()),
         "Associated artifact id cannot be null");
   }

   @Override
   public String toString() {
      return "CriteriaAssociatedArtId [associatedArtIds=" + associatedArtIds.toString() + "]";
   }
}
