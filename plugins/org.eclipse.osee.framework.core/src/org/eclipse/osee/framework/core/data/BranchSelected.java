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

   private BranchToken branch;

   private boolean selected;
   private boolean selectable;

   public BranchSelected() {
      // Do Nothing
   }

   public BranchSelected(BranchToken branch, boolean selected) {
      this.setBranch(branch);
      this.setSelected(selected);
      this.setSelectable(true); // TODO this will be used once the query is in place to determine if a branch is selectable.
   }

   public BranchToken getBranch() {
      return branch;
   }

   public void setBranch(BranchToken branch) {
      this.branch = branch;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public boolean isSelectable() {
      return selectable;
   }

   public void setSelectable(boolean selectable) {
      this.selectable = selectable;
   }

}
