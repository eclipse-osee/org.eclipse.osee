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
   private final Long branchUuid;
   private static final Map<Long, BranchAccessObject> cache = new HashMap<>();

   @Override
   public int hashCode() {
      int result = 17;
      result = 31 * result + branchUuid.hashCode();
      return result;
   }

   public BranchAccessObject(long branchUuid) {
      this.branchUuid = branchUuid;
   }

   @Override
   public long getBranchId() {
      return branchUuid;
   }

   @Override
   public void removeFromCache() {
      cache.remove(branchUuid);
   }

   @Override
   public void removeFromDatabase(int subjectId) throws OseeCoreException {
      final String DELETE_BRANCH_ACL = "DELETE FROM OSEE_BRANCH_ACL WHERE privilege_entity_id = ? AND branch_id =?";
      ConnectionHandler.runPreparedUpdate(DELETE_BRANCH_ACL, subjectId, branchUuid);
   }

   public static BranchAccessObject getBranchAccessObject(BranchId branch) throws OseeCoreException {
      return getBranchAccessObject(branch.getId());
   }

   public static BranchAccessObject getBranchAccessObject(long branchUuid) {
      BranchAccessObject branchAccessObject;
      if (cache.containsKey(branchUuid)) {
         branchAccessObject = cache.get(branchUuid);
      } else {
         branchAccessObject = new BranchAccessObject(branchUuid);
         cache.put(branchUuid, branchAccessObject);
      }
      return branchAccessObject;
   }

   public static BranchAccessObject getBranchAccessObjectFromCache(BranchId branch) throws OseeCoreException {
      return cache.get(branch.getUuid());
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof BranchAccessObject)) {
         return false;
      }
      return branchUuid.equals(((BranchAccessObject) obj).branchUuid);
   }
}
