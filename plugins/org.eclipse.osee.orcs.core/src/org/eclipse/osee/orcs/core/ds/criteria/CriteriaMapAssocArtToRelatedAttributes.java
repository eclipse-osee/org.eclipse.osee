/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

public class CriteriaMapAssocArtToRelatedAttributes extends Criteria implements BranchCriteria {

   String value = "";
   BranchId relatedBranch = BranchId.SENTINEL;
   List<Pair<ArtifactTypeToken, AttributeTypeToken>> artAttrPairs;

   public CriteriaMapAssocArtToRelatedAttributes(String value, BranchId relatedBranch, List<Pair<ArtifactTypeToken, AttributeTypeToken>> artAttrPairs) {
      this.value = value;
      this.relatedBranch = relatedBranch;
      this.artAttrPairs = artAttrPairs;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkExpressionFailOnTrue(value == null, "Value cannot be null");
      Conditions.checkExpressionFailOnTrue(relatedBranch == null, "Related branch cannot be null");
      Conditions.checkExpressionFailOnTrue(getArtAttrPairs() == null,
         "Related Artifact Types/Attribute Types pairs cannot be null");
      Conditions.checkExpressionFailOnTrue(value == "", "value cannot be empty");
      Conditions.checkExpressionFailOnTrue(relatedBranch.getId() == BranchId.SENTINEL.getId(),
         "Related branch cannot be sentinel");
      Conditions.checkExpressionFailOnTrue(getArtAttrPairs().size() == 0,
         "Related Artifact Types/Attribute Types pairs cannot be empty");
   }

   @Override
   public String toString() {
      return "CriteriaMapAssocArtToRelatedAttributes [value=" + value + "," + "relatedBranch=" + relatedBranch.getId() + "]";
   }

   public BranchId getRelatedBranch() {
      return this.relatedBranch;
   }

   public String getRelatedValue() {
      return this.value;
   }

   public List<Pair<ArtifactTypeToken, AttributeTypeToken>> getArtAttrPairs() {
      return artAttrPairs;
   }
}
