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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UpdateBranchData;
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

         Branch fromBranch = orcsApi.getQueryFactory().branchQuery().andId(fromBranchId).getResults().getOneOrNull();
         if (fromBranch.isInvalid()) {
            branchData.getResults().errorf("Update Branch fromBranch can't be found [%s]", this.fromBranchId);
            return branchData;
         }

         BranchToken toBranch = BranchToken.create(branchData.getToName());
         branchData.setNewBranchId(
            orcsApi.getBranchOps().createWorkingBranch(toBranch, fromBranch, ArtifactId.SENTINEL));

         branchData.getResults().logf("Update Branch [%s]", fromBranch.getShortName());

      } catch (Exception ex) {
         branchData.getResults().errorf("Exception in Update Operation: %s", Lib.exceptionToString(ex));
         return branchData;
      }
      return branchData;
   }
}
