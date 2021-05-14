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
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;

/**
 * @author Donald G. Dunne
 */
public class Branches {

   private Branches() {
      // Utility Class
   }

   public static boolean isValid(BranchId branch) {
      return branch != null && branch.isValid();
   }

   public static boolean isInValid(BranchId branch) {
      return !isValid(branch);
   }

   public static boolean isNotEmpty(Collection<BranchToken> branches) {
      return !branches.isEmpty();
   }
}
