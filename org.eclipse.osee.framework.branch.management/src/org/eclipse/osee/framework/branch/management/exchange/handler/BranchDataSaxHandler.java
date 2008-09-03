/*
 * Created on Aug 21, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.ImportOptions;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.BranchType;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.ExportImportJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class BranchDataSaxHandler extends BaseDbSaxHandler {
   private static final String BRANCH_QUERY =
         "select br1.* from osee_define_branch br1, osee_join_export_import jex1 WHERE br1.branch_id = jex1.id1 AND jex1.query_id=?";

   private static final String BRANCH_NAME = "branch_name";
   private static final String BRANCH_ID = "branch_id";
   private static final String BRANCH_TYPE = "branch_type";
   private static final String COMMIT_ART_ID = "associated_art_id";
   private static final String IS_ARCHIVED_BRANCH = "archived";
   private static final String BRANCH_SHORT_NAME = "short_name";
   private static final String PARENT_BRANCH_ID = "parent_branch_id";

   private Map<Integer, BranchData> oldIdsToData;
   private List<BranchData> toImport;
   private int[] branchIds;

   public static BranchDataSaxHandler newCacheAllDataBranchDataSaxHandler() {
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
      this.oldIdsToData = new HashMap<Integer, BranchData>();
      this.toImport = null;
      this.branchIds = null;
   }

   @Override
   protected void processData(Map<String, String> dataMap) throws Exception {
      BranchData branchData = new BranchData();
      for (String columnName : getMetaData().getColumnNames()) {
         String value = dataMap.get(columnName);
         branchData.setData(columnName, toObject(columnName, value));
      }
      this.oldIdsToData.put(branchData.getBranchId(), branchData);
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
         toReturn = this.oldIdsToData.keySet().containsAll(Arrays.asList(branchIds));
      }
      return toReturn;
   }

   public void setSelectedBranchIds(int... branchIds) {
      this.branchIds = branchIds;
   }

   public int[] getSelectedBranchIds() {
      return this.branchIds;
   }

   public Collection<BranchData> getAllBranchDataFromImportFile() {
      return this.oldIdsToData.values();
   }

   public List<BranchData> getSelectedBranchDataToImport(int... branchesToImport) {
      List<BranchData> toReturn = new ArrayList<BranchData>();
      if (branchesToImport != null && branchesToImport.length > 0) {
         for (int branchId : branchesToImport) {
            BranchData data = this.oldIdsToData.get(branchId);
            if (data != null) {
               toReturn.add(data);
            }
         }
      } else {
         toReturn.addAll(this.oldIdsToData.values());
      }
      return toReturn;
   }

   public List<BranchData> getImportedBranches() {
      List<BranchData> toReturn = toImport;
      if (toImport == null) {
         toReturn = java.util.Collections.emptyList();
      }
      return toReturn;
   }

   public void store() throws Exception {
      int[] branchesToImport = getSelectedBranchIds();
      if (branchesToImport != null && branchesToImport.length > 0) {
         if (!areAvailable(branchesToImport)) {
            throw new IllegalArgumentException(String.format(
                  "Branches not found in import file:\n\t\t- selected to import: [%s]\n\t\t- in import file: [%s]",
                  branchesToImport, getAllBranchDataFromImportFile()));
         }
      }
      for (BranchData branchData : getSelectedBranchDataToImport(branchIds)) {//getImportBranches(branchesToImport)) {
         Long original = new Long(branchData.getBranchId());
         if (branchData.getBranchId() != 1) {
            if (getOptions().getBoolean(ImportOptions.ALL_AS_ROOT_BRANCHES.name())) {
               branchData.setParentBranchId(-1);
               branchData.setBranchType(BranchType.ROOT);
            }
            Long newValue = (Long) getTranslator().translate(getConnection(), BRANCH_ID, original);
            branchData.setBranchId(newValue.intValue());
            Object[] data = branchData.toArray();
            if (data != null) {
               addData(data);
            }
         } else {
            getTranslator().addMappingTo("branch_id", original, original);
         }
      }
      super.store(getConnection());
   }

   public final class BranchData implements Cloneable {
      private Map<String, Object> backingData;

      private BranchData() {
         this.backingData = new HashMap<String, Object>();
      }

      private void setData(String key, Object object) {
         this.backingData.put(key, object);
      }

      public String getBranchName() {
         return (String) backingData.get(BRANCH_NAME);
      }

      public Integer getBranchType() {
         return (Integer) backingData.get(BRANCH_TYPE);
      }

      public int getAssociatedArtId() {
         return (Integer) backingData.get(COMMIT_ART_ID);
      }

      public int getIsArchived() {
         return (Integer) backingData.get(IS_ARCHIVED_BRANCH);
      }

      public int getBranchId() {
         return (Integer) backingData.get(BRANCH_ID);
      }

      public String getShortName() {
         return (String) backingData.get(BRANCH_SHORT_NAME);
      }

      public int getParentBranchId() {
         return (Integer) backingData.get(PARENT_BRANCH_ID);
      }

      public Object[] toArray() {
         return DataToSql.toDataArray(getMetaData(), backingData);
      }

      public BranchData clone() {
         BranchData clone = new BranchData();
         for (String key : this.backingData.keySet()) {
            clone.setData(key, this.backingData.get(key));
         }
         return clone;
      }

      /* (non-Javadoc)
       * @see java.lang.Object#equals(java.lang.Object)
       */
      @Override
      public String toString() {
         return String.format("(name[%s] id[%s])", getBranchName(), getBranchId());
      }

      /* (non-Javadoc)
       * @see java.lang.Object#equals(java.lang.Object)
       */
      @Override
      public boolean equals(Object obj) {
         if (obj == this) return true;
         if (!(obj instanceof BranchData)) return false;
         BranchData other = (BranchData) obj;
         boolean keysMatch = Collections.setComplement(this.backingData.keySet(), other.backingData.keySet()).isEmpty();
         if (!keysMatch) return false;
         boolean valuesMatch = true;
         for (String key : this.backingData.keySet()) {
            Object obj1 = this.backingData.get(key);
            Object obj2 = other.backingData.get(key);
            if (obj1 == null && obj2 != null || obj1 != null && obj2 == null || (obj1 != null && obj2 != null && !obj1.equals(obj2))) {
               valuesMatch = false;
               break;
            }
         }
         return valuesMatch;
      }

      public void setBranchId(int nextSeqVal) {
         this.backingData.put(BRANCH_ID, nextSeqVal);
      }

      public void setParentBranchId(int nextSeqVal) {
         this.backingData.put(PARENT_BRANCH_ID, nextSeqVal);
      }

      public void setBranchType(BranchType branchType) {
         this.backingData.put(BRANCH_TYPE, branchType.ordinal());
      }
   }

   // TODO: Determine whether this will be needed for future features
   private List<BranchData> getImportBranches(int... branchIds) throws SQLException {
      if (toImport == null) {
         toImport = new ArrayList<BranchData>();
         Map<Integer, BranchData> matchingBranches = getDbBranchesMatchingImportFileBranchIds(getConnection());
         Collection<BranchData> selectedImportBranches = getSelectedBranchDataToImport(branchIds);
         for (BranchData branchToImport : selectedImportBranches) {
            BranchData dataInDb = matchingBranches.get(branchToImport.getBranchId());
            if (dataInDb != null) {
               if (!branchToImport.equals(dataInDb)) {
                  if (branchToImport.getBranchName().equals(dataInDb.getBranchName())) {
                     // Don't add to import since already in DB. 
                     // Both Branch Name and Branch Id matched - that is all we care about
                  } else {
                     // Branch Names didn't Match -- branch_id has already been used 
                     //                     // Get New Branch Id and add to Import
                     //                     BranchData newItem = branchToImport.clone();
                     //                     newItem.setBranchId(Query.getNextSeqVal(SkynetDatabase.BRANCH_ID_SEQ));
                     //                     toImport.add(newItem);
                  }
               } else {
                  // Don't add to import since already in DB.
               }
            } else {
               toImport.add(branchToImport);
            }
         }
      }
      return toImport;
   }

   private Map<Integer, BranchData> getDbBranchesMatchingImportFileBranchIds(Connection connection) throws SQLException {
      Map<Integer, BranchData> databaseBranches = new HashMap<Integer, BranchData>();
      ConnectionHandlerStatement chStmt = null;
      ExportImportJoinQuery joinQuery = JoinUtility.createExportImportJoinQuery();
      try {
         for (Integer key : this.oldIdsToData.keySet()) {
            joinQuery.add(key, -1);
         }
         joinQuery.store(connection);
         chStmt = ConnectionHandler.runPreparedQuery(connection, BRANCH_QUERY, joinQuery.getQueryId());
         while (chStmt.next()) {
            BranchData dbData = new BranchData();
            int nullCount = 0;
            for (String columnName : getMetaData().getColumnNames()) {
               String value = chStmt.getRset().getString(columnName);
               Object converted = toObject(columnName, value);
               dbData.setData(columnName, converted);
               if (converted != null) {
                  nullCount++;
               }
            }
            if (nullCount > 0) {
               databaseBranches.put(dbData.getBranchId(), dbData);
            }
         }
      } finally {
         DbUtil.close(chStmt);
         joinQuery.delete(connection);
      }
      return databaseBranches;
   }
}
