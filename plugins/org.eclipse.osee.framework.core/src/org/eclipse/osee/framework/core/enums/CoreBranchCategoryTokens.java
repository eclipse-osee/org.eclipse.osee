/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.framework.core.enums;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;

/**
 * @author Audrey Denk
 */
public final class CoreBranchCategoryTokens {

   public static final BranchCategoryToken PLE = new BranchCategoryToken(1L, "PLE");
   public static final BranchCategoryToken ATS = new BranchCategoryToken(2L, "ATS");
   public static final BranchCategoryToken MIM = new BranchCategoryToken(3L, "MIM");
   public static final BranchCategoryToken CI = new BranchCategoryToken(4L, "CI");
   public static final BranchCategoryToken PR = new BranchCategoryToken(5L, "PR");

   public static List<BranchCategoryToken> values = Arrays.asList(PLE, ATS, MIM, CI, PR);

   private CoreBranchCategoryTokens() {
      // Constants
   }

   public static BranchCategoryToken valueOf(Long id) {
      BranchCategoryToken found =
         values.stream().filter(cat -> cat.getId().equals(id)).findFirst().orElse(BranchCategoryToken.SENTINEL);
      return found;
   }
}