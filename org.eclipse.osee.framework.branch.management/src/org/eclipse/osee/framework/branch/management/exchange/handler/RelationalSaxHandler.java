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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.osee.framework.branch.management.Activator;
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.branch.management.exchange.resource.ZipBinaryResource;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;

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
   private ZipFile zipFile;

   protected RelationalSaxHandler(boolean isCacheAll, int cacheLimit) {
      super(isCacheAll, cacheLimit);
      this.branchesToImport = new HashSet<Integer>();
   }

   public void setSelectedBranchIds(int... branchIds) {
      if (branchIds != null && branchIds.length > 0) {
         this.branchesToImport.clear();
         for (int branchId : branchIds) {
            this.branchesToImport.add(branchId);
         }
      }
   }

   public void setZipFile(ZipFile zipFile) {
      this.zipFile = zipFile;
   }

   public ZipFile getZipFile() {
      return zipFile;
   }

   public void store() throws Exception {
      super.store(this.getConnection());
   }

   private String importBinaryContent(String uriValue, String gammaId) throws Exception {
      String entrySearch = ExportImportXml.RESOURCE_FOLDER_NAME + "\\" + uriValue;
      if (this.zipFile != null) {
         ZipEntry entry = zipFile.getEntry(entrySearch);
         if (entry == null) {
            entry = zipFile.getEntry(entrySearch.replace('\\', '/'));
         }

         if (entry != null) {

            String name = uriValue.substring(uriValue.lastIndexOf('\\') + 1, uriValue.length());
            IResourceLocator locatorHint =
                  Activator.getInstance().getResourceLocatorManager().generateResourceLocator("attr", gammaId, name);

            IResourceLocator locator =
                  Activator.getInstance().getResourceManager().save(locatorHint,
                        new ZipBinaryResource(getZipFile(), entry, locatorHint), new Options());
            return locator.getLocation().toASCIIString();
         } else {
            throw new RuntimeException(String.format(
                  "Unable to locate resource in zip file - ZipEntry was null for [%s]", uriValue));
         }
      } else {
         throw new RuntimeException("ZipFile was Null.");
      }
   }

   @Override
   protected void processData(Map<String, String> fieldMap) throws Exception {
      boolean process = true;
      //      boolean exclude = false;
      try {
         //         if(shouldExcludeBaselineTxs()){
         //            String txType = fieldMap.get("tx_type");
         //            if(Strings.isValid(txType) && Integer.parseInt(txType) == 0){
         //               exclude =
         //            }
         //         }
         //         System.out.println(String.format("Table: [%s] Data: %s ", getMetaData(), fieldMap));
         if (!branchesToImport.isEmpty()) {
            String branchIdString = fieldMap.get(ExportImportXml.PART_OF_BRANCH);
            if (Strings.isValid(branchIdString)) {
               if (!branchesToImport.contains(new Integer(branchIdString))) {
                  process = false;
               }
            }
         }

         if (process) {
            String uriValue = fieldMap.get(BINARY_CONTENT_LOCATION);
            if (Strings.isValid(uriValue)) {
               String gammaId = fieldMap.get("gamma_id");
               Object translated = getTranslator().translate(getConnection(), "gamma_id", Long.valueOf(gammaId));
               uriValue = importBinaryContent(uriValue, translated.toString());
               fieldMap.put("uri", uriValue);
            }
            String stringValue = fieldMap.get(STRING_CONTENT);
            if (Strings.isValid(stringValue)) {
               fieldMap.put("value", stringValue);
            }
            Object[] objectData = DataToSql.toDataArray(getConnection(), getMetaData(), getTranslator(), fieldMap);
            if (objectData != null) {
               addData(objectData);
               if (isStorageNeeded()) {
                  store();
               }
            }
         }
      } catch (Exception ex) {
         // TODO Clean up binary content transfer;
         throw new Exception(String.format("Error processing in [%s]", getMetaData().getTableName()), ex);
      }
   }
}
