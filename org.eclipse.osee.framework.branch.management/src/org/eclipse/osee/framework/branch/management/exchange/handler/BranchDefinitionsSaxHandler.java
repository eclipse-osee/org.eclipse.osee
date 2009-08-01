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
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;

/**
 * @author Roberto E. Escobar
 */
public class BranchDefinitionsSaxHandler extends BaseDbSaxHandler {

   private final Set<Integer> allowedBranches;

   public static BranchDefinitionsSaxHandler createWithCacheAll() {
      return new BranchDefinitionsSaxHandler(true, 0);
   }

   public static BranchDefinitionsSaxHandler newLimitedCacheBranchDataSaxHandler(int cacheLimit) {
      return new BranchDefinitionsSaxHandler(false, cacheLimit);
   }

   private BranchDefinitionsSaxHandler(boolean isCacheAll, int cacheLimit) {
      super(isCacheAll, cacheLimit);
      this.allowedBranches = new HashSet<Integer>();
   }

   public void setStoredBranches(int... branchIds) {
      if (branchIds != null) {
         for (int entry : branchIds) {
            this.allowedBranches.add(entry);
         }
      }
   }

   @Override
   protected void processData(Map<String, String> dataMap) throws OseeDataStoreException {
      Integer branchId = new Integer(dataMap.get("mapped_branch_id"));
      if (allowedBranches.contains(branchId)) {
         Object[] objectData = DataToSql.toDataArray(getConnection(), getMetaData(), getTranslator(), dataMap);
         if (objectData != null) {
            addData(objectData);
            if (isStorageNeeded()) {
               store(getConnection());
            }
         }
      }
   }

   public void store() throws OseeDataStoreException {
      super.store(this.getConnection());
   }
}
