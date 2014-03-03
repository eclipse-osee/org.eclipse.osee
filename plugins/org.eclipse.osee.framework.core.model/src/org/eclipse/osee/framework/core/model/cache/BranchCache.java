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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class BranchCache extends AbstractOseeCache<String, Branch> {

   public BranchCache(IOseeDataAccessor<String, Branch> dataAccessor) {
      super(OseeCacheEnum.BRANCH_CACHE, dataAccessor, false);
   }

   public Branch getSystemRootBranch() throws OseeCoreException {
      return get(CoreBranches.SYSTEM_ROOT);
   }

   public Branch getCommonBranch() throws OseeCoreException {
      return get(CoreBranches.COMMON);
   }

   public MergeBranch findMergeBranch(IOseeBranch sourceBranch, IOseeBranch destinationBranch) throws OseeCoreException {
      Conditions.checkNotNull(sourceBranch, "source branch");
      Conditions.checkNotNull(destinationBranch, "destination branch");
      MergeBranch toReturn = null;
      for (Branch branch : getAll()) {
         if (branch instanceof MergeBranch) {
            MergeBranch mergeBranch = (MergeBranch) branch;
            if (sourceBranch.equals(mergeBranch.getSourceBranch()) && destinationBranch.equals(mergeBranch.getDestinationBranch())) {
               toReturn = mergeBranch;
               break;
            }
         }
      }
      return toReturn;
   }

   public MergeBranch findFirstMergeBranch(Branch sourceBranch) throws OseeCoreException {
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

   public List<MergeBranch> findAllMergeBranches(Branch sourceBranch) throws OseeCoreException {
      Conditions.checkNotNull(sourceBranch, "source branch");
      List<MergeBranch> toReturn = new ArrayList<MergeBranch>();
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

   public synchronized List<Branch> getBranches(BranchFilter branchFilter) throws OseeCoreException {
      Collection<Branch> allBranches = getRawValues();
      List<Branch> branches = new LinkedList<Branch>();
      for (Branch branch : allBranches) {
         if (branchFilter.matches(branch)) {
            branches.add(branch);
         }
      }
      return branches;
   }
}
