/*
 * Created on Aug 21, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class BranchDataSaxHandler extends BaseDbSaxHandler {

   private Map<Integer, BranchData> oldIdsToData;

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
   }

   @Override
   protected void processData(Map<String, String> dataMap) throws Exception {
      BranchData branchData =
            new BranchData((String) getData(dataMap, "branch_name"), (Integer) getData(dataMap, "branch_id"));
      branchData.setBranchType((Integer) getData(dataMap, "branch_type"));
      branchData.setAssociatedArtId((Integer) getData(dataMap, "associated_art_id"));
      branchData.setIsArchived((Integer) getData(dataMap, "archived"));
      branchData.setShortName((String) getData(dataMap, "short_name"));
      branchData.setParentBranchId((Integer) getData(dataMap, "parent_branch_id"));

      this.oldIdsToData.put(branchData.getBranchId(), branchData);
   }

   private Object getData(Map<String, String> dataMap, String key) {
      Object toReturn = null;
      String value = dataMap.get(key);
      if (Strings.isValid(value)) {
         Class<?> clazz = getMetaData().toClass(key);
         toReturn = DataToSql.stringToObject(clazz, key, value);
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

   public Collection<BranchData> getBranchData() {
      return this.oldIdsToData.values();
   }

   private final class BranchData {
      private String branchName;
      private Object branchType;
      private int associatedArtId;
      private int isArchived;
      private int branchId;
      private String shortName;
      private int parentBranchId;

      public BranchData(String branchName, int branchId) {
         super();
         this.associatedArtId = -1;
         this.branchId = branchId;
         this.branchName = branchName;
         this.branchType = null;
         this.isArchived = -1;
         this.parentBranchId = -1;
         this.shortName = "";
      }

      public String getBranchName() {
         return branchName;
      }

      public Object getBranchType() {
         return branchType;
      }

      public int getAssociatedArtId() {
         return associatedArtId;
      }

      public int getIsArchived() {
         return isArchived;
      }

      public int getBranchId() {
         return branchId;
      }

      public String getShortName() {
         return shortName;
      }

      public int getParentBranchId() {
         return parentBranchId;
      }

      private void setBranchType(Object branchType) {
         this.branchType = branchType;
      }

      private void setAssociatedArtId(int associatedArtId) {
         this.associatedArtId = associatedArtId;
      }

      private void setIsArchived(int isArchived) {
         this.isArchived = isArchived;
      }

      private void setShortName(String shortName) {
         this.shortName = shortName;
      }

      private void setParentBranchId(int parentBranchId) {
         this.parentBranchId = parentBranchId;
      }

      public String toString() {
         return String.format("(name[%s] id[%s])", getBranchName(), getBranchId());
      }
   }
}
