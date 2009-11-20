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
package org.eclipse.osee.framework.skynet.core.access;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;

/**
 * @author Jeff C. Phillips
 */
public class BranchAccessObject extends AccessObject {
   private final int branchId;
   private static final Map<Integer, BranchAccessObject> cache = new HashMap<Integer, BranchAccessObject>();

   @Override
   public int hashCode() {
      int result = 17;
      result = 31 * result + branchId;
      return result;
   }

   public BranchAccessObject(int branchId) {
      this.branchId = branchId;
   }

   @Override
   public int getId() {
      return branchId;
   }

   @Override
   public void removeFromCache() {
      cache.remove(branchId);
   }

   @Override
   public void removeFromDatabase(int subjectId) throws OseeDataStoreException {
      final String DELETE_BRANCH_ACL = "DELETE FROM OSEE_BRANCH_ACL WHERE privilege_entity_id = ? AND branch_id =?";
      ConnectionHandler.runPreparedUpdate(DELETE_BRANCH_ACL, subjectId, branchId);
   }

   public static BranchAccessObject getBranchAccessObject(Branch branch) {
      return getBranchAccessObject(branch.getId());
   }

   public static BranchAccessObject getBranchAccessObject(Integer branchId) {
      BranchAccessObject branchAccessObject;
      if (cache.containsKey(branchId)) {
         branchAccessObject = cache.get(branchId);
      } else {
         branchAccessObject = new BranchAccessObject(branchId);
         cache.put(branchId, branchAccessObject);
      }
      return branchAccessObject;
   }

   public static BranchAccessObject getBranchAccessObjectFromCache(Branch branch) {
      Integer branchId = branch.getId();
      return cache.get(branchId);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof BranchAccessObject)) {
         return false;
      }
      return branchId == ((BranchAccessObject) obj).branchId;
   }
}
