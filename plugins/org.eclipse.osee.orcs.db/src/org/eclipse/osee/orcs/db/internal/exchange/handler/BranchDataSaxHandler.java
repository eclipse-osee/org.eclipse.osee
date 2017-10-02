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

import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ImportOptions;
import org.eclipse.osee.orcs.db.internal.exchange.ExchangeDb;

/**
 * @author Roberto E. Escobar
 */
public class BranchDataSaxHandler extends BaseDbSaxHandler {

   private final Map<BranchId, BranchData> idToImportFileBranchData;
   private JdbcConnection connection;

   public static BranchDataSaxHandler createWithCacheAll(Log logger, JdbcClient service) {
      return new BranchDataSaxHandler(logger, service, true, 0);
   }

   public static BranchDataSaxHandler newLimitedCacheBranchDataSaxHandler(Log logger, JdbcClient service, int cacheLimit) {
      return new BranchDataSaxHandler(logger, service, false, cacheLimit);
   }

   private BranchDataSaxHandler(Log logger, JdbcClient service, boolean isCacheAll, int cacheLimit) {
      super(logger, service, isCacheAll, cacheLimit);
      this.idToImportFileBranchData = new HashMap<>();
      this.connection = null;
   }

   @Override
   protected void processData(Map<String, String> dataMap)  {
      BranchData branchData = new BranchData();
      for (String columnName : getMetaData().getColumnNames()) {
         String value = dataMap.get(columnName);
         branchData.setData(columnName, toObject(columnName, value));
      }
      this.idToImportFileBranchData.put(BranchId.valueOf(branchData.getId()), branchData);
   }

   private Object toObject(String key, String value)  {
      Object toReturn = null;
      if (Strings.isValid(value)) {
         Class<?> clazz = getMetaData().toClass(key);
         toReturn = DataToSql.stringToObject(clazz, key, value);
      } else {
         toReturn = getMetaData().toDataType(key);
      }
      return toReturn;
   }

   public boolean areAvailable(List<? extends BranchId> branchesToImport) {
      Set<BranchId> toCheck = new HashSet<>();
      for (BranchId entry : branchesToImport) {
         toCheck.add(entry);
      }
      return this.idToImportFileBranchData.keySet().containsAll(toCheck);
   }

   public Collection<BranchData> getAllBranchDataFromImportFile() {
      return this.idToImportFileBranchData.values();
   }

   private List<BranchData> getSelectedBranchesToImport(Collection<? extends BranchId> branchesToImport) {
      List<BranchData> toReturn = new ArrayList<>();
      if (branchesToImport.isEmpty()) {
         toReturn.addAll(this.idToImportFileBranchData.values());
      } else {
         for (BranchId branch : branchesToImport) {
            BranchData data = this.idToImportFileBranchData.get(branch);
            if (data != null) {
               toReturn.add(data);
            }
         }
      }
      return toReturn;
   }

   private void checkSelectedBranches(List<? extends BranchId> branchesToImport) throws OseeDataStoreException {
      if (!areAvailable(branchesToImport)) {
         throw new OseeDataStoreException(
            "Branches not found in import file:\n\t\t- selected to import: [%s]\n\t\t- in import file: [%s]",
            branchesToImport, getAllBranchDataFromImportFile());
      }
   }

   public Set<BranchId> store(JdbcConnection connection, boolean writeToDb, List<? extends BranchId> branchesToImport)  {
      checkSelectedBranches(branchesToImport);
      Collection<BranchData> branchesToStore = getSelectedBranchesToImport(branchesToImport);

      branchesToStore = checkTargetDbBranches(connection, branchesToStore);
      Set<BranchId> toReturn = new HashSet<>(branchesToImport.size());
      for (BranchData branchData : branchesToStore) {
         if (!getOptions().getBoolean(ImportOptions.CLEAN_BEFORE_IMPORT.name()) //
            && SYSTEM_ROOT.equals(branchData.getId())) {
            continue;
         }

         toReturn.add(BranchId.valueOf(branchData.getId()));
         if (getOptions().getBoolean(ImportOptions.ALL_AS_ROOT_BRANCHES.name())) {
            branchData.setParentBranchId(1);
            branchData.setBranchType(BranchType.BASELINE);
         } else {
            branchData.setParentBranchId(translateLongId(BranchData.PARENT_BRANCH_ID, branchData.getParentBranchId()));
         }
         branchData.setBranchId(translateLongId(BranchData.BRANCH_ID, branchData.getId()));
         branchData.setAssociatedBranchId(translateIntId(BranchData.COMMIT_ART_ID, branchData.getAssociatedArtId()));

         Object[] data = branchData.toArray(getMetaData());
         if (data != null) {
            addData(data);
         }
      }
      if (writeToDb) {
         super.store(connection);
      }
      return toReturn;
   }

   public void updateBaselineAndParentTransactionId(Set<BranchId> branchesStored)  {
      List<BranchData> branches = getSelectedBranchesToImport(branchesStored);
      List<Object[]> data = new ArrayList<>();
      for (BranchData branchData : branches) {
         long branchUuid = branchData.getId();
         int parentTransactionId = translateIntId(ExchangeDb.TRANSACTION_ID, branchData.getParentTransactionId());
         if (parentTransactionId == 0) {
            parentTransactionId = 1;
         }

         int baselineTransactionId = translateIntId(ExchangeDb.TRANSACTION_ID, branchData.getBaselineTransactionId());
         if (baselineTransactionId == 0) {
            baselineTransactionId = 1;
         }
         data.add(new Object[] {parentTransactionId, baselineTransactionId, branchUuid});
      }
      if (!data.isEmpty()) {
         String query =
            "update osee_branch set parent_transaction_id = ?, baseline_transaction_id = ? where branch_id = ?";
         int updateCount = getDatabaseService().runBatchUpdate(query, data);
         getLogger().info("Updated [%s] baseline and parent transaction id info on branches [%s]", updateCount,
            branchesStored);
      } else {
         getLogger().info("No branches found to update baseline and parent txs: branches - [%s] - skipping",
            branchesStored);
      }
   }

   private long translateLongId(String id, long originalValue)  {
      Long original = new Long(originalValue);
      Long newValue = (Long) getTranslator().translate(id, original);
      return newValue.intValue();
   }

   private int translateIntId(String id, int originalValue)  {
      Long original = new Long(originalValue);
      Long newValue = (Long) getTranslator().translate(id, original);
      return newValue.intValue();
   }

   private Collection<BranchData> checkTargetDbBranches(JdbcConnection connection, Collection<BranchData> selectedBranches)  {
      Map<Long, BranchData> idToBranchData = new HashMap<>();
      for (BranchData data : selectedBranches) {
         idToBranchData.put(data.getId(), data);
      }

      JdbcStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         chStmt.runPreparedQuery("select * from osee_branch");
         while (chStmt.next()) {
            Long branchUuid = chStmt.getLong(BranchData.BRANCH_ID);
            BranchData branchData = idToBranchData.get(branchUuid);
            if (branchData != null) {
               getTranslator().checkIdMapping("branch_id", branchData.getId(), branchUuid);
               // Remove from to store list so we don't store duplicate information
               idToBranchData.remove(branchUuid);
            }
         }
      } finally {
         chStmt.close();
      }
      return idToBranchData.values();
   }

   @Override
   public void clearDataTable()  {
      getDatabaseService().runPreparedUpdate(getConnection(), String.format(
         "DELETE FROM %s where NOT branch_type = " + BranchType.SYSTEM_ROOT.getValue(), getMetaData().getTableName()));
   }

   public void setConnection(JdbcConnection connection) {
      this.connection = connection;
   }

   public JdbcConnection getConnection() {
      return connection;
   }

   @Override
   public void reset() {
      super.reset();
      setConnection(null);
   }

   @Override
   public void store()  {
      super.store(getConnection());
   }

}
