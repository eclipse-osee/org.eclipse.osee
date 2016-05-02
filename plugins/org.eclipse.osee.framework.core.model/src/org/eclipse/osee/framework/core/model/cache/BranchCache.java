/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class BranchCache extends AbstractOseeLoadingCache<Branch> {

   private final TransactionCache txCache;

   public BranchCache(IOseeDataAccessor<Branch> dataAccessor, TransactionCache txCache) {
      super(OseeCacheEnum.BRANCH_CACHE, dataAccessor);
      this.txCache = txCache;
   }

   public MergeBranch findMergeBranch(BranchId sourceBranch, BranchId destinationBranch) throws OseeCoreException {
      Conditions.checkNotNull(sourceBranch, "source branch");
      Conditions.checkNotNull(destinationBranch, "destination branch");
      MergeBranch toReturn = null;
      for (Branch branch : getAll()) {
         if (branch instanceof MergeBranch) {
            MergeBranch mergeBranch = (MergeBranch) branch;
            if (sourceBranch.equals(mergeBranch.getSourceBranch()) && destinationBranch.equals(
               mergeBranch.getDestinationBranch())) {
               toReturn = mergeBranch;
               break;
            }
         }
      }
      return toReturn;
   }

   public MergeBranch findFirstMergeBranch(BranchId sourceBranch) throws OseeCoreException {
      Conditions.checkNotNull(sourceBranch, "source branch");
      MergeBranch toReturn = null;
      for (Branch branch : getAll()) {
         if (branch instanceof MergeBranch) {
            MergeBranch mergeBranch = (MergeBranch) branch;
            if (sourceBranch.equals(mergeBranch.getSourceBranch())) {
               toReturn = mergeBranch;
               break;
            }
         }
      }
      return toReturn;
   }

   public List<MergeBranch> findAllMergeBranches(BranchId sourceBranch) throws OseeCoreException {
      Conditions.checkNotNull(sourceBranch, "source branch");
      List<MergeBranch> toReturn = new ArrayList<>();
      for (Branch branch : getAll()) {
         if (branch instanceof MergeBranch) {
            MergeBranch mergeBranch = (MergeBranch) branch;
            if (sourceBranch.equals(mergeBranch.getSourceBranch())) {
               toReturn.add(mergeBranch);
            }
         }
      }
      return toReturn;
   }

   public synchronized <T extends BranchId> List<T> getBranches(BranchFilter branchFilter) {
      Collection<Branch> allBranches = getRawValues();
      List<T> branches = new LinkedList<>();
      for (Branch branch : allBranches) {
         if (branchFilter.matches(branch)) {
            branches.add((T) branch);
         }
      }
      return branches;
   }

   @Override
   public synchronized boolean reloadCache() throws OseeCoreException {
      super.reloadCache();
      txCache.reloadCache();
      return true;
   }

   @Override
   public synchronized void decacheAll() {
      super.decacheAll();
      txCache.decacheAll();
   }
}