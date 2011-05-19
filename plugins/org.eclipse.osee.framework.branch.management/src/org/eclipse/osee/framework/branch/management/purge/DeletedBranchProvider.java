/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.purge;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author John Misinco
 */
public final class DeletedBranchProvider implements IBranchesProvider {
   private final BranchCache branchCache;

   public DeletedBranchProvider(BranchCache branchCache) {
      this.branchCache = branchCache;
   }

   @Override
   public Collection<Branch> getBranches() throws OseeCoreException {
      Conditions.checkNotNull(branchCache, "branchCache");
      BranchFilter branchFilter = new BranchFilter(BranchArchivedState.ARCHIVED);
      branchFilter.setBranchStates(BranchState.DELETED);
      branchFilter.setNegatedBranchTypes(BranchType.BASELINE);

      List<Branch> branches = branchCache.getBranches(branchFilter);
      Collection<Branch> branchesToReturn = new LinkedHashSet<Branch>();
      branchesToReturn.addAll(branches);
      for (Branch branch : branches) {
         branch.getChildBranches(branchesToReturn, true, branchFilter);
      }

      return branchesToReturn;
   }
}