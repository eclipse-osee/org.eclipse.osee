/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.orcs.rest.internal.branch;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UpdateBranchData;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Dominic Guss
 */
public class UpdateBranchOperation {
   private final BranchId toBranchId;
   private final BranchId fromBranchId;
   private final UpdateBranchData branchData;
   private final OrcsApi orcsApi;

   public UpdateBranchOperation(UpdateBranchData branchData, OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.toBranchId = branchData.getToBranch();
      this.fromBranchId = branchData.getFromBranch();
      this.branchData = branchData;
   }

   public UpdateBranchData run() {
      try {
         if (toBranchId.isInvalid()) {
            branchData.getResults().errorf("Update Branch toBranch is invalid [%s]", this.toBranchId);
            return branchData;
         }
         if (fromBranchId.isInvalid()) {
            branchData.getResults().errorf("Update Branch fromBranch is invalid [%s]", this.fromBranchId);
            return branchData;
         }

         Branch fromBranch =
            orcsApi.getQueryFactory().branchQuery().andId(fromBranchId).getResults().getOneOrDefault(Branch.SENTINEL);
         if (fromBranch.isInvalid()) {
            branchData.getResults().errorf("Update Branch fromBranch can't be found [%s]", this.fromBranchId);
            return branchData;
         }
         List<BranchCategoryToken> toBranchCategories = orcsApi.getQueryFactory().branchQuery().getBranchCategories(branchData.getToBranch());
         BranchToken toBranch = BranchToken.create(branchData.getToName());
         
         branchData.setNewBranchId(
            orcsApi.getBranchOps().createWorkingBranch(toBranch, fromBranch, ArtifactId.SENTINEL));

         /*
          * When doing an update from parent, categories are set from the parent branch. If any categories were newly
          * added to the working branch (e.g. PR) those do not get inherited by the above call to
          * orcsApi.getBranchOps().createWorkingBranch Therefore we must add any newly added categories back onto the
          * newly updated branch as needed. 
          * Additionally we store the branchState in order to use later
          */
         
         BranchState newBranchState = orcsApi.getQueryFactory().branchQuery().andId(branchData.getNewBranchId()).getResults().getExactlyOne().getBranchState();
         List<BranchCategoryToken> newBranchCategories = orcsApi.getQueryFactory().branchQuery().getBranchCategories(branchData.getNewBranchId());
         if (!newBranchCategories.containsAll(toBranchCategories)) {
            toBranchCategories.removeAll(newBranchCategories);
            for (BranchCategoryToken category : toBranchCategories) {
               orcsApi.getBranchOps().setBranchCategory(branchData.getNewBranchId(), category);
            }
            //Change branch categories should not cause a change in BranchState
            orcsApi.getBranchOps().setBranchState(branchData.getNewBranchId(), newBranchState);
         }
         
         branchData.getResults().logf("Update Branch [%s]", fromBranch.getShortName());

      } catch (Exception ex) {
         branchData.getResults().errorf("Exception in Update Operation: %s", Lib.exceptionToString(ex));
         return branchData;
      }
      return branchData;
   }
}
