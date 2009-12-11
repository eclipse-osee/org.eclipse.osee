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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.branch.management.exchange.ExchangeDb;
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.branch.management.exchange.resource.ZipBinaryResource;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class RelationalSaxHandler extends BaseDbSaxHandler {
   private final IOseeCachingService service;

   public static RelationalSaxHandler createWithLimitedCache(IOseeDbExportDataProvider exportDataProvider, int cacheLimit) throws OseeCoreException {
      return new RelationalSaxHandler(exportDataProvider, false, cacheLimit);
   }

   private final List<IResourceLocator> transferredBinaryContent;
   private final Set<Integer> branchesToImport;
   private final IOseeDbExportDataProvider exportDataProvider;
   private IExportItem exportItem;

   protected RelationalSaxHandler(IOseeDbExportDataProvider exportDataProvider, boolean isCacheAll, int cacheLimit) throws OseeCoreException {
      super(isCacheAll, cacheLimit);
      this.branchesToImport = new HashSet<Integer>();
      this.transferredBinaryContent = new ArrayList<IResourceLocator>();
      this.exportDataProvider = exportDataProvider;
      service = Activator.getInstance().getOseeCachingService();
   }

   public void setSelectedBranchIds(int... branchIds) {
      if (branchIds != null && branchIds.length > 0) {
         this.branchesToImport.clear();
         for (int branchId : branchIds) {
            this.branchesToImport.add(branchId);
         }
      }
   }

   public void store() throws OseeDataStoreException {
      super.store(this.getConnection());
   }

   private String importBinaryContent(String uriValue, String gammaId) throws Exception {
      String relativePath = Lib.isWindows() ? uriValue : uriValue.replaceAll("\\\\", File.separator);
      String entrySearch = ExportImportXml.RESOURCE_FOLDER_NAME + File.separator + relativePath;
      if (exportDataProvider.getExportedDataRoot() != null) {
         File entry = new File(exportDataProvider.getExportedDataRoot(), entrySearch);
         if (entry.exists()) {

            String name = uriValue.substring(uriValue.lastIndexOf('\\') + 1, uriValue.length());
            IResourceLocator locatorHint =
                  Activator.getInstance().getResourceLocatorManager().generateResourceLocator("attr", gammaId, name);

            IResourceLocator locator =
                  Activator.getInstance().getResourceManager().save(locatorHint,
                        new ZipBinaryResource(entry, locatorHint), new Options());
            transferredBinaryContent.add(locator);
            return locator.getLocation().toASCIIString();
         } else {
            throw new OseeStateException(String.format(
                  "Unable to locate resource in zip file - ZipEntry was null for [%s]", uriValue));
         }
      } else {
         throw new OseeStateException("Uncompressed folder was Null.");
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
               String gammaId = fieldMap.get(ExchangeDb.GAMMA_ID);
               Object translated = getTranslator().translate(ExchangeDb.GAMMA_ID, Long.valueOf(gammaId));
               uriValue = importBinaryContent(uriValue, translated.toString());
               fieldMap.put("uri", uriValue);
            }
            String stringValue = fieldMap.get(STRING_CONTENT);
            if (Strings.isValid(stringValue)) {
               fieldMap.put("value", stringValue);
            }

            if (exportItem.equals(ExportItem.OSEE_ARTIFACT_DATA)) {
               int typeId = service.getArtifactTypeCache().getByGuid(fieldMap.get(ExchangeDb.TYPE_GUID)).getId();
               fieldMap.put("art_type_id", String.valueOf(typeId));
            }

            if (exportItem.equals(ExportItem.OSEE_ATTRIBUTE_DATA)) {
               int typeId = service.getAttributeTypeCache().getByGuid(fieldMap.get(ExchangeDb.TYPE_GUID)).getId();
               fieldMap.put("attr_type_id", String.valueOf(typeId));
            }

            if (exportItem.equals(ExportItem.OSEE_RELATION_LINK_DATA)) {
               int typeId = service.getRelationTypeCache().getByGuid(fieldMap.get(ExchangeDb.TYPE_GUID)).getId();
               fieldMap.put("rel_link_type_id", String.valueOf(typeId));
            }

            String conflictId = fieldMap.get(ExchangeDb.CONFLICT_ID);
            String conflictType = fieldMap.get(ExchangeDb.CONFLICT_TYPE);
            if (Strings.isValid(conflictId) && Strings.isValid(conflictType)) {
               int conflictOrdinal = Integer.valueOf(conflictType);
               for (ConflictType type : ConflictType.values()) {
                  if (type.getValue() == conflictOrdinal) {
                     Object value = Integer.valueOf(conflictId);
                     switch (type) {
                        case ARTIFACT:
                           value = getTranslator().translate(ExchangeDb.ARTIFACT_ID, value);
                           break;
                        case ATTRIBUTE:
                           value = getTranslator().translate(ExchangeDb.ATTRIBUTE_ID, value);
                           break;
                        case RELATION:
                           value = getTranslator().translate(ExchangeDb.RELATION_ID, value);
                           break;
                        default:
                           break;
                     }
                     fieldMap.put(ExchangeDb.CONFLICT_ID, value.toString());
                     break;
                  }
               }
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
         cleanUpBinaryContent();
         throw new Exception(String.format("Error processing in [%s]", getMetaData().getTableName()), ex);
      }
   }

   @Override
   public void reset() {
      transferredBinaryContent.clear();
      super.reset();
   }

   private void cleanUpBinaryContent() {
      StringBuilder errorMessage = new StringBuilder();
      IResourceManager manager = Activator.getInstance().getResourceManager();
      for (IResourceLocator locator : transferredBinaryContent) {
         try {
            manager.delete(locator);
         } catch (Exception ex) {
            errorMessage.append(String.format("Error deleting [%s]\n", locator.getLocation().toASCIIString()));

         }
      }
      if (errorMessage.length() > 0) {
         OseeLog.log(
               this.getClass(),
               Level.SEVERE,
               "Error deleting binary data after transfer error. Please delete all content manually. " + errorMessage.toString());
      }
   }

   @Override
   public void clearDataTable() throws OseeDataStoreException {
      if (!getMetaData().getTableName().equals("osee_tx_details")) {
         super.clearDataTable();
      }
   }

   /**
    * @param exportItem the exportItem to set
    */
   public void setExportItem(IExportItem exportItem) {
      this.exportItem = exportItem;
   }
}
