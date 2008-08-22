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
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class RelationalSaxHandler extends BaseDbSaxHandler {

   public static RelationalSaxHandler newCacheAllDataRelationalSaxHandler() {
      return new RelationalSaxHandler(true, 0);
   }

   public static RelationalSaxHandler newLimitedCacheRelationalSaxHandler(int cacheLimit) {
      return new RelationalSaxHandler(false, cacheLimit);
   }

   private Set<Integer> branchesToImport;

   protected RelationalSaxHandler(boolean isCacheAll, int cacheLimit) {
      super(isCacheAll, cacheLimit);
      this.branchesToImport = new HashSet<Integer>();
   }

   public void setBranchesToImport(int... branchIds) {
      if (branchIds != null && branchIds.length > 0) {
         this.branchesToImport.clear();
         for (int branchId : branchIds) {
            this.branchesToImport.add(branchId);
         }
      }
   }

   @Override
   protected void processData(Map<String, String> fieldMap) throws Exception {
      boolean process = true;

      System.out.println(String.format("Table: [%s] Data: %s ", getMetaData(), fieldMap));
      if (!branchesToImport.isEmpty()) {
         String branchIdString = fieldMap.get(ExportImportXml.PART_OF_BRANCH);
         if (Strings.isValid(branchIdString)) {
            if (!branchesToImport.contains(new Integer(branchIdString))) {
               process = false;
            }
         }
      }

      if (process) {
         Object[] objectData = DataToSql.toDataArray(getConnection(), getMetaData(), getTranslator(), fieldMap);
         if (objectData != null) {
            addData(objectData);
            if (isStorageNeeded()) {
               store();
            }
         }
      }
   }
}
