package org.eclipse.osee.orcs.core.internal.search;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllBranches;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAssociatedArtId;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchAncestorOf;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchArchived;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchChildOf;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchName;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchState;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchType;
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

   public Criteria createBranchNameCriteria(String value, boolean isPattern) {
      return new CriteriaBranchName(value, isPattern);
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
}