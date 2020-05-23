/*********************************************************************
 * Copyright (c) 2014 Boeing
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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author John Misinco
 */
public class CriteriaAssociatedArtId extends Criteria implements BranchCriteria {

   private final ArtifactId associatedArtId;

   public CriteriaAssociatedArtId(ArtifactId associatedArtId) {
      this.associatedArtId = associatedArtId;
   }

   public ArtifactId getAssociatedArtId() {
      return associatedArtId;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkExpressionFailOnTrue(associatedArtId.isInvalid(), "Associated artifact id cannot be null");
   }

   @Override
   public String toString() {
      return "CriteriaAssociatedArtId [associatedArtId=" + associatedArtId + "]";
   }
}
