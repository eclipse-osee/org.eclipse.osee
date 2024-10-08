/*********************************************************************
 * Copyright (c) 2010 Boeing
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

/**
 * @author Roberto E. Escobar
 */
public interface HasBranch extends HasBranchId {

   @Override
   BranchToken getBranch();

   @Override
   default String getBranchIdString() {
      return getBranch().getIdString();
   }

   default boolean isOnSameBranch(HasBranch other) {
      return other == null ? false : getBranch().equals(other.getBranch());
   }

   @Override
   default boolean isOnBranch(BranchId branch) {
      return getBranch().equals(branch);
   }
}