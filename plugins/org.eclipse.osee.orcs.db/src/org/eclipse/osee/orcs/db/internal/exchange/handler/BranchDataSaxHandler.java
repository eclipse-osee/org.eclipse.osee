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
package org.eclipse.osee.orcs.db.internal.exchange.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ImportOptions;
import org.eclipse.osee.orcs.db.internal.exchange.ExchangeDb;

/**
 * @author Roberto E. Escobar
 */
public class BranchDataSaxHandler extends BaseDbSaxHandler {

   private final Map<Integer, BranchData> idToImportFileBranchData;
   private OseeConnection connection;

   public static BranchDataSaxHandler createWithCacheAll(Log logger, IOseeDatabaseService service) {
      return new BranchDataSaxHandler(logger, service, true, 0);
   }

   public static BranchDataSaxHandler newLimitedCacheBranchDataSaxHandler(Log logger, IOseeDatabaseService service, int cacheLimit) {
      return new BranchDataSaxHandler(logger, service, false, cacheLimit);
   }

   private BranchDataSaxHandler(Log logger, IOseeDatabaseService service, boolean isCacheAll, int cacheLimit) {
      super(logger, service, isCacheAll, cacheLimit);
      this.idToImportFileBranchData = new HashMap<Integer, BranchData>();
      this.connection = null;
   }

   @Override
   protected void processData(Map<String, String> dataMap) throws OseeArgumentException {
      BranchData branchData = new BranchData();
      for (String columnName : getMetaData().getColumnNames()) {
         String value = dataMap.get(columnName);
         branchData.setData(columnName, toObject(columnName, value));
      }
      this.idToImportFileBranchData.put(branchData.getId(), branchData);
   }

   private Object toObject(String key, String value) throws OseeArgumentException {
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
         Set<Integer> toCheck = new HashSet<Integer>();
         for (int entry : branchIds) {
            toCheck.add(entry);
         }
         toReturn = this.idToImportFileBranchData.keySet().containsAll(toCheck);
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

   private void checkSelectedBranches(int... branchesToImport) throws OseeDataStoreException {
      if (branchesToImport != null && branchesToImport.length > 0) {
         if (!areAvailable(branchesToImport)) {
            throw new OseeDataStoreException(
               "Branches not found in import file:\n\t\t- selected to import: [%s]\n\t\t- in import file: [%s]",
               branchesToImport, getAllBranchDataFromImportFile());
         }
      }
   }

   public int[] store(OseeConnection connection, boolean writeToDb, int... branchesToImport) throws OseeCoreException {
      checkSelectedBranches(branchesToImport);
      Collection<BranchData> branchesToStore = getSelectedBranchesToImport(branchesToImport);

      branchesToStore = checkTargetDbBranches(connection, branchesToStore);
      int[] toReturn = new int[branchesToStore.size()];
      int index = 0;
      for (BranchData branchData : branchesToStore) {
         if (!getOptions().getBoolean(ImportOptions.CLEAN_BEFORE_IMPORT.name()) && branchData.getBranchGuid().equals(
            CoreBranches.SYSTEM_ROOT.getGuid())) {
            continue;
         }

         toReturn[index] = branchData.getId();
         if (getOptions().getBoolean(ImportOptions.ALL_AS_ROOT_BRANCHES.name())) {
            branchData.setParentBranchId(1);
            branchData.setBranchType(BranchType.BASELINE);
         } else {
            branchData.setParentBranchId(translateId(BranchData.PARENT_BRANCH_ID, branchData.getParentBranchId()));
         }
         branchData.setBranchId(translateId(BranchData.BRANCH_ID, branchData.getId()));
         branchData.setAssociatedBranchId(translateId(BranchData.COMMIT_ART_ID, branchData.getAssociatedArtId()));

         Object[] data = branchData.toArray(getMetaData());
         if (data != null) {
            addData(data);
         }
         index++;
      }
      if (writeToDb) {
         super.store(connection);
      }
      return toReturn;
   }

   public void updateBaselineAndParentTransactionId(int[] branchesStored) throws OseeCoreException {
      List<BranchData> branches = getSelectedBranchesToImport(branchesStored);
      List<Object[]> data = new ArrayList<Object[]>();
      for (BranchData branchData : branches) {
         int branchId = branchData.getId();
         int parentTransactionId = translateId(ExchangeDb.TRANSACTION_ID, branchData.getParentTransactionId());
         if (parentTransactionId == 0) {
            parentTransactionId = 1;
         }

         int baselineTransactionId = translateId(ExchangeDb.TRANSACTION_ID, branchData.getBaselineTransactionId());
         if (baselineTransactionId == 0) {
            baselineTransactionId = 1;
         }
         data.add(new Object[] {parentTransactionId, baselineTransactionId, branchId});
      }
      if (!data.isEmpty()) {
         String query =
            "update osee_branch set parent_transaction_id = ?, baseline_transaction_id = ? where branch_id = ?";
         int updateCount = getDatabaseService().runBatchUpdate(query, data);
         getLogger().info("Updated [%s] baseline and parent transaction id info on branches [%s]", updateCount,
            Arrays.toString(branchesStored));
      } else {
         getLogger().info("No branches found to update baseline and parent txs: branches - [%s] - skipping",
            Arrays.toString(branchesStored));
      }
   }

   private int translateId(String id, int originalValue) throws OseeCoreException {
      Long original = new Long(originalValue);
      Long newValue = (Long) getTranslator().translate(id, original);
      return newValue.intValue();
   }

   private Collection<BranchData> checkTargetDbBranches(OseeConnection connection, Collection<BranchData> selectedBranches) throws OseeCoreException {
      Map<String, BranchData> guidToImportFileBranchData = new HashMap<String, BranchData>();
      for (BranchData data : selectedBranches) {
         guidToImportFileBranchData.put(data.getBranchGuid(), data);
      }

      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         chStmt.runPreparedQuery("select * from osee_branch");
         while (chStmt.next()) {
            String branchGuid = chStmt.getString(BranchData.BRANCH_GUID);
            Long branchId = chStmt.getLong(BranchData.BRANCH_ID);
            BranchData branchData = guidToImportFileBranchData.get(branchGuid);
            if (branchData != null) {
               getTranslator().checkIdMapping("branch_id", (long) branchData.getId(), branchId);
               // Remove from to store list so we don't store duplicate information
               guidToImportFileBranchData.remove(branchGuid);
            }
         }
      } finally {
         chStmt.close();
      }
      return guidToImportFileBranchData.values();
   }

   @Override
   public void clearDataTable() throws OseeCoreException {
      getDatabaseService().runPreparedUpdate(
         getConnection(),
         String.format("DELETE FROM %s where NOT branch_type = " + BranchType.SYSTEM_ROOT.getValue(),
            getMetaData().getTableName()));
   }

   public void setConnection(OseeConnection connection) {
      this.connection = connection;
   }

   public OseeConnection getConnection() {
      return connection;
   }

   @Override
   public void reset() {
      super.reset();
      setConnection(null);
   }

   @Override
   public void store() throws OseeCoreException {
      super.store(getConnection());
   }

}
