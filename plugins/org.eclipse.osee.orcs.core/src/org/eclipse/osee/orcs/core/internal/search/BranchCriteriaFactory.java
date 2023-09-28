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

package org.eclipse.osee.orcs.core.internal.search;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllBranches;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAssociatedArtId;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchAncestorOf;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchArchived;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchCategory;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchChildOf;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchName;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchOrderByName;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchState;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaMapAssocArtToRelatedAttributes;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaMergeBranchFor;

/**
 * @author Roberto E. Escobar
 */
public class BranchCriteriaFactory {

   public Criteria createAllBranchesCriteria() {
      return new CriteriaAllBranches();
   }

   public Criteria createBranchTypeCriteria(Collection<BranchType> types) {
      return new CriteriaBranchType(types);
   }

   public Criteria createBranchStateCriteria(Collection<BranchState> states) {
      return new CriteriaBranchState(states);
   }

   public Criteria createBranchNameCriteria(String value, boolean isPattern, boolean isPatternIgnoreCase) {
      return new CriteriaBranchName(value, isPattern, isPatternIgnoreCase);
   }

   public Criteria createBranchChildOfCriteria(BranchId parent) {
      return new CriteriaBranchChildOf(parent);
   }

   public Criteria createBranchAncestorOfCriteria(BranchId child) {
      return new CriteriaBranchAncestorOf(child);
   }

   public Criteria createBranchArchivedCriteria(Collection<BranchArchivedState> states) {
      return new CriteriaBranchArchived(states);
   }

   public Criteria createMergeForCriteria(BranchId source, BranchId destination) {
      return new CriteriaMergeBranchFor(source, destination);
   }

   public Criteria createAssociatedArtIdCriteria(ArtifactId artId) {
      return new CriteriaAssociatedArtId(artId);
   }

   public Criteria createBranchCategoryCriteria(BranchCategoryToken category) {
      return new CriteriaBranchCategory(category);
   }

   public Criteria createMapAssocArtToRelatedAttributesCriteria(String value, BranchId relatedBranch,
      List<Pair<ArtifactTypeToken, AttributeTypeToken>> artAttrPairs) {
      return new CriteriaMapAssocArtToRelatedAttributes(value, relatedBranch, artAttrPairs);
   }

   public Criteria createBranchOrderByNameCriteria() {
      return new CriteriaBranchOrderByName();
   }
}