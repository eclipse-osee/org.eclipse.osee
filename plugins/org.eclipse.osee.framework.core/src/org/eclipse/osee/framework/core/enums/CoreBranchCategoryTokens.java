/*********************************************************************
 * Copyright (c) 20021 Boeing
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
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Audrey Denk
 */
public final class CoreBranchCategoryTokens {

   public static final BranchCategoryToken PLE = new BranchCategoryToken(1L, "PLE");
   public static final BranchCategoryToken ATS = new BranchCategoryToken(2L, "ATS");
   public static final BranchCategoryToken MIM = new BranchCategoryToken(3L, "MIM");

   public static List<BranchCategoryToken> values = Arrays.asList(PLE, ATS, MIM);

   private CoreBranchCategoryTokens() {
      // Constants
   }

   public static BranchCategoryToken valueOf(Long id) {
      BranchCategoryToken found =
         values.stream().filter(cat -> cat.getId().equals(id)).findFirst().orElse(BranchCategoryToken.SENTINEL);
      if (found.isInvalid()) {
         throw new OseeArgumentException("[%s] is not a valid Branch Category", id);
      }
      return found;
   }
}