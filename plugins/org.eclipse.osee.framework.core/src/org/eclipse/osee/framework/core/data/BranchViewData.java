/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.List;

/**
 * @author Roberto E. Escobar
 */
public class BranchViewData {

   private BranchId branch;

   private List<ArtifactId> branchViews;

   public BranchViewData() {
      // Do Nothing
   }

   public BranchViewData(BranchId branch, List<ArtifactId> branchViews) {
      this.branch = branch;
      this.branchViews = branchViews;
   }

   public BranchId getBranch() {
      return branch;
   }

   public List<ArtifactId> getBranchViews() {
      return branchViews;
   }

   public void setBranchId(BranchId branch) {
      this.branch = branch;
   }

   public void setBranchViews(List<ArtifactId> branchViews) {
      this.branchViews = branchViews;
   }
}
