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

package org.eclipse.osee.framework.ui.branch.graph.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.Adaptable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class BranchGraphEditorInput implements IEditorInput, Adaptable {

   private final BranchToken branch;

   public BranchGraphEditorInput(BranchToken branch) {
      this.branch = branch;
   }

   public BranchGraphEditorInput() {
      this(CoreBranches.SYSTEM_ROOT);
   }

   @Override
   public boolean exists() {
      return false;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(getName());
   }

   @Override
   public String getName() {
      return branch != null ? branch.getName() : "Branch was Null";
   }

   @Override
   public IPersistableElement getPersistable() {
      return null;
   }

   @Override
   public String getToolTipText() {
      return getName();
   }

   public BranchId getBranch() {
      return branch;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (branch == null ? 0 : branch.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      BranchGraphEditorInput other = (BranchGraphEditorInput) obj;
      if (branch == null) {
         if (other.branch != null) {
            return false;
         }
      } else if (branch.notEqual(other.branch)) {
         return false;
      }
      return true;
   }
}