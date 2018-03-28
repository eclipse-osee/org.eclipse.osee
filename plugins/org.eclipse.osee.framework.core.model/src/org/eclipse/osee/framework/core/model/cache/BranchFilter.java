/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.cache;

import java.util.function.Predicate;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Ryan D. Brooks
 */
public class BranchFilter implements Predicate<Branch> {
   private final BranchArchivedState archivedState;
   private final BranchType[] branchTypes;
   private ArtifactToken associatedArtifact;

   private BranchState[] branchStates;
   private BranchState[] negatedBranchStates;
   private BranchType[] negatedBranchTypes;

   /**
    * @param archivedState filter will only match branches that are of one of the specified states
    * @param branchTypes filter will only match branches that are of one of the specified types
    */
   public BranchFilter(BranchArchivedState archivedState, BranchType... branchTypes) {
      this.archivedState = archivedState;
      this.branchTypes = branchTypes;
   }

   /**
    * @param branchTypes filter will only match branches that are of one of the specified types
    */
   public BranchFilter(BranchType... branchTypes) {
      this(BranchArchivedState.ALL, branchTypes);
   }

   @Override
   public boolean test(Branch branch) {
      if (associatedArtifact != null && !branch.getAssociatedArtifactId().equals(associatedArtifact)) {
         return false;
      }
      if (!archivedState.matches(branch.isArchived())) {
         return false;
      }
      if (branchTypes.length > 0 && !branch.getBranchType().matches(branchTypes)) {
         return false;
      }
      if (branchStates != null && !branch.getBranchState().matches(branchStates)) {
         return false;
      }
      if (negatedBranchStates != null && branch.getBranchState().matches(negatedBranchStates)) {
         return false;
      }
      if (negatedBranchTypes != null && branch.getBranchType().matches(negatedBranchTypes)) {
         return false;
      }
      return true;
   }

   public void setAssociatedArtifact(ArtifactToken associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   public void setBranchStates(BranchState... branchStates) {
      this.branchStates = branchStates;
   }

   public void setNegatedBranchStates(BranchState... negatedBranchStates) {
      this.negatedBranchStates = negatedBranchStates;
   }

   public void setNegatedBranchTypes(BranchType... negatedBranchTypes) {
      this.negatedBranchTypes = negatedBranchTypes;
   }
}