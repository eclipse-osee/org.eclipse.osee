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

package org.eclipse.osee.framework.core.data;

public class BranchSelected {

   private final BranchToken branch;
   private final boolean selected;
   private final boolean selectable;
   private final boolean committedToBaseline;

   public BranchSelected() {
      this.branch = Branch.SENTINEL;
      this.selected = false;
      this.selectable = false;
      this.committedToBaseline = false;
   }

   public BranchSelected(BranchToken branch, boolean selected) {
      this(branch, selected, false);
   }

   public BranchSelected(BranchToken branch, boolean selected, boolean committedToBaseline) {
      this.branch = branch;
      this.selected = selected;
      this.selectable = true; // TODO this will be used once the query is in place to determine if a branch is selectable.
      this.committedToBaseline = committedToBaseline;
   }

   public BranchToken getBranch() {
      return branch;
   }

   public boolean isSelected() {
      return selected;
   }

   public boolean isSelectable() {
      return selectable;
   }

   public boolean isCommittedToBaseline() {
      return committedToBaseline;
   }

}
