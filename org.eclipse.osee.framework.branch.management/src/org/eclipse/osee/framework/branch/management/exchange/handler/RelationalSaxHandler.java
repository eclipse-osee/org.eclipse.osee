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

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

   public static RelationalSaxHandler createWithCacheAll() {
      return new RelationalSaxHandler(true, 0);
   }

   public static RelationalSaxHandler createWithLimitedCache(int cacheLimit) {
      return new RelationalSaxHandler(false, cacheLimit);
   }

   private final Set<Integer> branchesToImport;
   private File decompressedFolder;

   protected RelationalSaxHandler(boolean isCacheAll, int cacheLimit) {
      super(isCacheAll, cacheLimit);
      this.branchesToImport = new HashSet<Integer>();
      this.decompressedFolder = null;
   }

   public void setSelectedBranchIds(int... branchIds) {
      if (branchIds != null && branchIds.length > 0) {
         this.branchesToImport.clear();
         for (int branchId : branchIds) {
            this.branchesToImport.add(branchId);
         }
      }
   }

   public void setDecompressedFolder(File decompressedFolder) {
      this.decompressedFolder = decompressedFolder;
   }

   public File getDecompressedFolder() {
      return decompressedFolder;
   }

   public void store() throws Exception {
      super.store(this.getConnection());
   }

   private String importBinaryContent(String uriValue, String gammaId) throws Exception {
      String entrySearch = ExportImportXml.RESOURCE_FOLDER_NAME + "\\" + uriValue;
      if (this.decompressedFolder != null) {
         File entry = new File(decompressedFolder, entrySearch);
         if (entry.exists()) {

            String name = uriValue.substring(uriValue.lastIndexOf('\\') + 1, uriValue.length());
            IResourceLocator locatorHint =
                  Activator.getInstance().getResourceLocatorManager().generateResourceLocator("attr", gammaId, name);

            IResourceLocator locator =
                  Activator.getInstance().getResourceManager().save(locatorHint,
                        new ZipBinaryResource(entry, locatorHint), new Options());
            return locator.getLocation().toASCIIString();
         } else {
            throw new RuntimeException(String.format(
                  "Unable to locate resource in zip file - ZipEntry was null for [%s]", uriValue));
         }
      } else {
         throw new RuntimeException("Uncompressed folder was Null.");
      }
   }

   @Override
   protected void processData(Map<String, String> fieldMap) throws Exception {
      boolean process = true;
      try {
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
               Object translated = getTranslator().translate("gamma_id", Long.valueOf(gammaId));
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
