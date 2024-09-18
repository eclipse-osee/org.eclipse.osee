/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.mim.types;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;

public class PeerReviewApplyData {

   private List<BranchId> addBranches = new ArrayList<>();
   private List<BranchId> removeBranches = new ArrayList<>();

   public PeerReviewApplyData() {
   }

   public PeerReviewApplyData(List<BranchId> addBranches, List<BranchId> removeBranches) {
      this.setAddBranches(addBranches);
      this.setRemoveBranches(removeBranches);
   }

   public List<BranchId> getAddBranches() {
      return addBranches;
   }

   public void setAddBranches(List<BranchId> addBranches) {
      this.addBranches = addBranches;
   }

   public List<BranchId> getRemoveBranches() {
      return removeBranches;
   }

   public void setRemoveBranches(List<BranchId> removeBranches) {
      this.removeBranches = removeBranches;
   }
}
