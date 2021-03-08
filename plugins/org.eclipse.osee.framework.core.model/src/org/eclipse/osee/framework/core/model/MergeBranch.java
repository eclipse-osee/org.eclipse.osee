/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.model;

import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author Roberto E. Escobar
 */
public final class MergeBranch extends Branch {
   private BranchToken source;
   private BranchToken destination;

   public MergeBranch(Long branchId, String name, BranchType branchType, BranchState branchState, boolean isArchived, boolean inheritAccessControl) {
      super(branchId, name, branchType, branchState, isArchived, inheritAccessControl);
   }

   public BranchToken getSourceBranch() {
      return source;
   }

   public BranchToken getDestinationBranch() {
      return destination;
   }

   public void setSourceBranch(BranchToken branch) {
      this.source = branch;
   }

   public void setDestinationBranch(BranchToken branch) {
      this.destination = branch;
   }
}