/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

/**
 * @author Donald G. Dunne
 */
public interface HasBranchId {

   BranchId getBranch();

   default String getBranchIdString() {
      return getBranch().getIdString();
   }

   default boolean isOnSameBranch(HasBranchId other) {
      return other == null ? false : getBranch().equals(other.getBranch());
   }

   default boolean isOnBranch(BranchId branch) {
      return getBranch().equals(branch);
   }
}
