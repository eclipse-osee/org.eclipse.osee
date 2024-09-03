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

   public BranchSelected() {
      // Do Nothing
   }

   public BranchSelected(BranchToken branch, boolean selected) {
      this.setBranch(branch);
      this.setSelected(selected);
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

   
}
