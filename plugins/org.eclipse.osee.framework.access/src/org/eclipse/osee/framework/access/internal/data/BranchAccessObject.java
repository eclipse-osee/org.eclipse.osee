/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.internal.data;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.access.AccessObject;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;

/**
 * @author Jeff C. Phillips
 */
public class BranchAccessObject extends AccessObject {
   private final BranchId branch;
   private static final Map<BranchId, BranchAccessObject> cache = new HashMap<>();

   @Override
   public int hashCode() {
      int result = 17;
      result = 31 * result + branch.hashCode();
      return result;
   }

   public BranchAccessObject(BranchId branch) {
      this.branch = branch;
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   @Override
   public void removeFromCache() {
      cache.remove(branch);
   }

   @Override
   public void removeFromDatabase(int subjectId)  {
      final String DELETE_BRANCH_ACL = "DELETE FROM OSEE_BRANCH_ACL WHERE privilege_entity_id = ? AND branch_id =?";
      ConnectionHandler.runPreparedUpdate(DELETE_BRANCH_ACL, subjectId, branch);
   }

   public static BranchAccessObject getBranchAccessObject(BranchId branch) {
      BranchAccessObject branchAccessObject;
      if (cache.containsKey(branch)) {
         branchAccessObject = cache.get(branch);
      } else {
         branchAccessObject = new BranchAccessObject(branch);
         cache.put(branch, branchAccessObject);
      }
      return branchAccessObject;
   }

   public static BranchAccessObject getBranchAccessObjectFromCache(BranchId branch)  {
      return cache.get(branch);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof BranchAccessObject)) {
         return false;
      }
      return branch.equals(((BranchAccessObject) obj).branch);
   }
}
