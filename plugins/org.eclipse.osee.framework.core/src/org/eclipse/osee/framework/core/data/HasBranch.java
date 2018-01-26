/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

/**
 * @author Roberto E. Escobar
 */
public interface HasBranch {

   BranchId getBranch();

   default String getBranchIdString() {
      return getBranch().getIdString();
   }

   default boolean isOnSameBranch(HasBranch other) {
      return other == null ? false : getBranch().equals(other.getBranch());
   }

   default boolean isOnBranch(BranchId branch) {
      return getBranch().equals(branch);
   }
}