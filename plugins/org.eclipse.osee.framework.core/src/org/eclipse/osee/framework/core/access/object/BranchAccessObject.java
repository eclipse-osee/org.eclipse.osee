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

package org.eclipse.osee.framework.core.access.object;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;

/**
 * @author Jeff C. Phillips
 */
public class BranchAccessObject extends AccessObject {
   private final BranchToken branch;
   private static final Map<BranchId, BranchAccessObject> cache = new HashMap<>();

   @Override
   public int hashCode() {
      int result = 17;
      result = 31 * result + branch.hashCode();
      return result;
   }

   private BranchAccessObject(BranchToken branch) {
      super(branch);
      this.branch = branch;
   }

   @Override
   public BranchToken getBranch() {
      return branch;
   }

   @Override
   public void removeFromCache() {
      cache.remove(branch);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof BranchAccessObject)) {
         return false;
      }
      return branch.equals(((BranchAccessObject) obj).branch);
   }

   @Override
   public boolean isBranch() {
      return true;
   }

   @Override
   public Long getId() {
      return branch.getId();
   }

   public static BranchAccessObject valueOf(BranchToken branch) {
      BranchAccessObject bao = cache.get(branch);
      if (bao == null) {
         bao = new BranchAccessObject(branch);
         cache.put(branch, bao);
      }
      return bao;
   }

   @Override
   public String toString() {
      return "Branch  " + branch.toStringWithId();
   }
}
