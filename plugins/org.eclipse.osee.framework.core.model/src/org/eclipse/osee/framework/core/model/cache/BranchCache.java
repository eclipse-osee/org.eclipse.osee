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
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class BranchCache extends AbstractOseeLoadingCache<Branch> {

   private final CompositeKeyHashMap<BranchId, ArtifactId, Branch> branchViews = new CompositeKeyHashMap<>(50, false);

   public BranchCache(IOseeDataAccessor<Branch> dataAccessor) {
      super(OseeCacheEnum.BRANCH_CACHE, dataAccessor);
   }

   public MergeBranch findMergeBranch(BranchId sourceBranch, BranchId destinationBranch) {
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

   public MergeBranch findFirstMergeBranch(BranchId sourceBranch) {
      Conditions.checkNotNull(sourceBranch, "source branch");
      for (Branch branch : getAll()) {
         if (branch instanceof MergeBranch) {
            MergeBranch mergeBranch = (MergeBranch) branch;
            if (sourceBranch.equals(mergeBranch.getSourceBranch())) {
               return mergeBranch;
            }
         }
      }
      throw new OseeNotFoundException("Merge branch not found for source [%s]", sourceBranch);
   }

   public List<MergeBranch> findAllMergeBranches(BranchId sourceBranch) {
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

   public Branch getBranchWithView(BranchId branchId, ArtifactId viewId) {
      return branchViews.get(branchId, viewId);
   }

   public Branch cacheBranchWithView(Branch branch, ArtifactToken view) {
      Branch branchView = Branch.createBranchView(branch, view, branch.getName() + " [view: " + view.getName() + "]");
      branchViews.put(branchView, view, branchView);
      return branchView;
   }

   public synchronized List<IOseeBranch> getBranches(Predicate<Branch> branchFilter) {
      return getRawValues().stream().filter(branchFilter).collect(Collectors.toList());
   }

   public Branch get(BranchId branch) {
      return getById(branch.getId());
   }
}