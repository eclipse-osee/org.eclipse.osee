/*********************************************************************
 * Copyright (c) 2026 Boeing
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
 * Holds a branch id paired with the artifact ids that exist only on that branch (not on the compared branch).
 */
public class BranchUniqueArtifacts {

   private BranchId branch;
   private List<ArtifactId> uniqueArtifacts;

   public BranchUniqueArtifacts() {
      // for JSON deserialization
   }

   public BranchUniqueArtifacts(BranchId branch, List<ArtifactId> uniqueArtifacts) {
      this.branch = branch;
      this.uniqueArtifacts = uniqueArtifacts;
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public List<ArtifactId> getUniqueArtifacts() {
      return uniqueArtifacts;
   }

   public void setUniqueArtifacts(List<ArtifactId> uniqueArtifacts) {
      this.uniqueArtifacts = uniqueArtifacts;
   }
}
