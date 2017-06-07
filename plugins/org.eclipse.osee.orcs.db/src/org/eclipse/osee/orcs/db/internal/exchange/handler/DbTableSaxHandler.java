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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.exchange.ExchangeDb;
import org.eclipse.osee.orcs.db.internal.exchange.ExportImportXml;
import org.eclipse.osee.orcs.db.internal.exchange.IOseeExchangeDataProvider;
import org.eclipse.osee.orcs.db.internal.util.ZipBinaryResource;

/**
 * @author Roberto E. Escobar
 */
public class DbTableSaxHandler extends BaseDbSaxHandler {

   public static DbTableSaxHandler createWithLimitedCache(Log logger, JdbcClient jdbcClient, IResourceManager resourceManager, IdentityLocator identityService, IOseeExchangeDataProvider exportDataProvider, int cacheLimit) {
      return new DbTableSaxHandler(logger, jdbcClient, resourceManager, identityService, exportDataProvider, false,
         cacheLimit);
   }

   private final List<IResourceLocator> transferredBinaryContent;
   private final Set<BranchId> branchesToImport;
   private final IOseeExchangeDataProvider exportDataProvider;

   private final IResourceManager resourceManager;
   private final IdentityLocator identityService;
   private IExportItem exportItem;

   protected DbTableSaxHandler(Log logger, JdbcClient jdbcClient, IResourceManager resourceManager, IdentityLocator identityService, IOseeExchangeDataProvider exportDataProvider, boolean isCacheAll, int cacheLimit) {
      super(logger, jdbcClient, isCacheAll, cacheLimit);
      this.resourceManager = resourceManager;
      this.identityService = identityService;
      this.branchesToImport = new HashSet<>();
      this.transferredBinaryContent = new ArrayList<>();
      this.exportDataProvider = exportDataProvider;
   }

   private IResourceManager getResourceManager() {
      return resourceManager;
   }

   private IdentityLocator getIdentityService() {
      return identityService;
   }

   public void setSelectedBranchIds(Iterable<? extends BranchId> branchesToImport) {
      this.branchesToImport.clear();
      for (BranchId branch : branchesToImport) {
         this.branchesToImport.add(branch);
      }
   }

   private String importBinaryContent(String uriValue, String gammaId) throws OseeCoreException {
      String relativePath = Lib.isWindows() ? uriValue : uriValue.replaceAll("\\\\", File.separator);
      String entrySearch = ExportImportXml.RESOURCE_FOLDER_NAME + File.separator + relativePath;
      if (exportDataProvider.getExportedDataRoot() != null) {
         File entry = new File(exportDataProvider.getExportedDataRoot(), entrySearch);
         if (entry.exists()) {

            String name = uriValue.substring(uriValue.lastIndexOf('\\') + 1, uriValue.length());
            IResourceLocator locatorHint = getResourceManager().generateResourceLocator("attr", gammaId, name);

            IResourceLocator locator =
               getResourceManager().save(locatorHint, new ZipBinaryResource(entry, locatorHint), new PropertyStore());
            transferredBinaryContent.add(locator);
            return locator.getLocation().toASCIIString();
         } else {
            throw new OseeStateException("Unable to locate resource in zip file - ZipEntry was null for [%s]",
               uriValue);
         }
      } else {
         throw new OseeStateException("Uncompressed folder was Null.");
      }
   }

   private long getTypeId(IdentityLocator identityService, Map<String, String> fieldMap) throws OseeCoreException {
      Conditions.checkNotNull(identityService, "identityService");
      String hexString = fieldMap.get(ExchangeDb.TYPE_GUID);
      return identityService.parseToLocalId(hexString);
   }

   @Override
   protected void processData(Map<String, String> fieldMap) throws OseeCoreException {
      boolean process = true;
      try {
         if (!branchesToImport.isEmpty()) {
            String branchUuidString = fieldMap.get(ExportImportXml.PART_OF_BRANCH);
            if (Strings.isValid(branchUuidString)) {
               if (!branchesToImport.contains(new Integer(branchUuidString))) {
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
               long typeId = getTypeId(getIdentityService(), fieldMap);
               fieldMap.put("art_type_id", String.valueOf(typeId));
            }

            if (exportItem.equals(ExportItem.OSEE_ATTRIBUTE_DATA)) {
               long typeId = getTypeId(getIdentityService(), fieldMap);
               fieldMap.put("attr_type_id", String.valueOf(typeId));
            }

            if (exportItem.equals(ExportItem.OSEE_RELATION_LINK_DATA)) {
               long typeId = getTypeId(getIdentityService(), fieldMap);
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

            Object[] objectData = DataToSql.toDataArray(getMetaData(), getTranslator(), fieldMap);
            if (objectData != null) {
               addData(objectData);
               if (isStorageNeeded()) {
                  store();
               }
            }
         }
      } catch (OseeCoreException ex) {
         cleanUpBinaryContent();
         getLogger().error(ex, "Error processing in [%s]", getMetaData().getTableName());
         throw ex;
      }
   }

   @Override
   public void reset() {
      transferredBinaryContent.clear();
      super.reset();
   }

   private void cleanUpBinaryContent() {
      StringBuilder errorMessage = new StringBuilder();
      IResourceManager manager = getResourceManager();
      for (IResourceLocator locator : transferredBinaryContent) {
         try {
            manager.delete(locator);
         } catch (Exception ex) {
            errorMessage.append(String.format("Error deleting [%s]\n", locator.getLocation().toASCIIString()));

         }
      }
      if (errorMessage.length() > 0) {
         getLogger().error("Error deleting binary data after transfer error. Please delete all content manually. [%s]",
            errorMessage.toString());
      }
   }

   @Override
   public void clearDataTable() throws OseeCoreException {
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
