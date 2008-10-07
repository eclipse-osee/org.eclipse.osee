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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.ImportOptions;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.BranchType;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class BranchDataSaxHandler extends BaseDbSaxHandler {

   private final Map<Integer, BranchData> idToImportFileBranchData;

   public static BranchDataSaxHandler createWithCacheAll() {
      return new BranchDataSaxHandler(true, 0);
   }

   public static BranchDataSaxHandler newLimitedCacheBranchDataSaxHandler(int cacheLimit) {
      return new BranchDataSaxHandler(false, cacheLimit);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.exchange.handler.BaseExportImportSaxHandler#processData(java.util.Map)
    */
   private BranchDataSaxHandler(boolean isCacheAll, int cacheLimit) {
      super(isCacheAll, cacheLimit);
      this.idToImportFileBranchData = new HashMap<Integer, BranchData>();
   }

   @Override
   protected void processData(Map<String, String> dataMap) throws Exception {
      BranchData branchData = new BranchData();
      for (String columnName : getMetaData().getColumnNames()) {
         String value = dataMap.get(columnName);
         branchData.setData(columnName, toObject(columnName, value));
      }
      this.idToImportFileBranchData.put(branchData.getBranchId(), branchData);
   }

   private Object toObject(String key, String value) {
      Object toReturn = null;
      if (Strings.isValid(value)) {
         Class<?> clazz = getMetaData().toClass(key);
         toReturn = DataToSql.stringToObject(clazz, key, value);
      } else {
         toReturn = getMetaData().toDataType(key);
      }
      return toReturn;
   }

   public boolean areAvailable(int... branchIds) {
      boolean toReturn = false;
      if (branchIds != null && branchIds.length > 0) {
         toReturn = this.idToImportFileBranchData.keySet().containsAll(Arrays.asList(branchIds));
      }
      return toReturn;
   }

   public Collection<BranchData> getAllBranchDataFromImportFile() {
      return this.idToImportFileBranchData.values();
   }

   private List<BranchData> getSelectedBranchesToImport(int... branchesToImport) {
      List<BranchData> toReturn = new ArrayList<BranchData>();
      if (branchesToImport != null && branchesToImport.length > 0) {
         for (int branchId : branchesToImport) {
            BranchData data = this.idToImportFileBranchData.get(branchId);
            if (data != null) {
               toReturn.add(data);
            }
         }
      } else {
         toReturn.addAll(this.idToImportFileBranchData.values());
      }
      return toReturn;
   }

   private void checkSelectedBranches(int... branchesToImport) throws Exception {
      if (branchesToImport != null && branchesToImport.length > 0) {
         if (!areAvailable(branchesToImport)) {
            throw new Exception(String.format(
                  "Branches not found in import file:\n\t\t- selected to import: [%s]\n\t\t- in import file: [%s]",
                  branchesToImport, getAllBranchDataFromImportFile()));
         }
      }
   }
   
   public int[] store(int... branchesToImport) throws Exception {
      checkSelectedBranches(branchesToImport);
      Collection<BranchData> branchesToStore = getSelectedBranchesToImport(branchesToImport);
      branchesToStore = checkTargetDbBranches(branchesToStore);
      int[] toReturn = new int[branchesToStore.size()];
      int index = 0;
      for (BranchData branchData : branchesToStore) {
         toReturn[index] = branchData.getBranchId();
         if (getOptions().getBoolean(ImportOptions.ALL_AS_ROOT_BRANCHES.name())) {
            branchData.setParentBranchId(-1);
            branchData.setBranchType(BranchType.ROOT);
         }
         Long original = new Long(branchData.getBranchId());
         Long newValue = (Long) getTranslator().translate(BranchData.BRANCH_ID, original);
         branchData.setBranchId(newValue.intValue());
         
         original = new Long(branchData.getAssociatedArtId());
         newValue = (Long) getTranslator().translate(BranchData.COMMIT_ART_ID, original);
         branchData.setAssociatedBranchId(newValue.intValue());
         
         original = new Long(branchData.getParentBranchId());
         newValue = (Long) getTranslator().translate(BranchData.PARENT_BRANCH_ID, original);
         branchData.setParentBranchId(newValue.intValue());
         
         Object[] data = branchData.toArray(getMetaData());
         if (data != null) {
            addData(data);
         }
         index++;
      }
      super.store(getConnection());
      return toReturn;
   }

   private Collection<BranchData> checkTargetDbBranches(Collection<BranchData> selectedBranches) throws OseeDataStoreException {
      Map<String, BranchData> nameToImportFileBranchData = new HashMap<String, BranchData>();
      for (BranchData data : selectedBranches) {
         nameToImportFileBranchData.put(data.getBranchName(), data);
      }

      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(getConnection(), "select * from osee_branch");
         while (chStmt.next()) {
            String name = chStmt.getString(BranchData.BRANCH_NAME);
            Long branchId = chStmt.getLong(BranchData.BRANCH_ID);
            BranchData branchData = nameToImportFileBranchData.get(name);
            if (branchData != null) {
               getTranslator().checkIdMapping("branch_id", (long) branchData.getBranchId(), branchId);
               // Remove from to store list so we don't store duplicate information
               nameToImportFileBranchData.remove(name);
            }
         }
      } finally {
         ConnectionHandler.close(chStmt);
      }
      return nameToImportFileBranchData.values();
   }
}
