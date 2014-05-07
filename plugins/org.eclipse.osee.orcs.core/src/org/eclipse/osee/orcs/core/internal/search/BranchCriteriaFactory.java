package org.eclipse.osee.orcs.core.internal.search;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllBranches;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchAncestorOf;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchArchived;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchChildOf;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchUuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchName;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchState;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchType;

/**
 * @author Roberto E. Escobar
 */
public class BranchCriteriaFactory {

   public Criteria createAllBranchesCriteria() {
      return new CriteriaAllBranches();
   }

   public Criteria createBranchUuidsCriteria(Collection<Long> ids) {
      return new CriteriaBranchUuids(ids);
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

   public Criteria createBranchChildOfCriteria(IOseeBranch parent) {
      return new CriteriaBranchChildOf(parent);
   }

   public Criteria createBranchAncestorOfCriteria(IOseeBranch child) {
      return new CriteriaBranchAncestorOf(child);
   }

   public Criteria createBranchArchivedCriteria(Collection<BranchArchivedState> states) {
      return new CriteriaBranchArchived(states);
   }
}