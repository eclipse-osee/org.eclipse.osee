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
package org.eclipse.osee.orcs.db.internal.exchange;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.core.ExportImportJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.db.internal.exchange.export.AbstractExportItem;

/**
 * @author Roberto E. Escobar
 */
final class ExportController implements IExchangeTaskListener {
   private static final String ZIP_EXTENSION = ".zip";

   private String exportName;
   private final PropertyStore options;
   private final List<Integer> branchIds;
   private final ExportImportJoinQuery exportJoinId;
   private final List<String> errorList = new CopyOnWriteArrayList<String>();
   private final OseeServices oseeServices;

   ExportController(OseeServices oseeServices, String exportName, PropertyStore options, List<Integer> branchIds) throws OseeCoreException {
      if (branchIds.isEmpty()) {
         throw new OseeArgumentException("No branch selected for export.");
      }
      this.oseeServices = oseeServices;
      this.exportName = exportName;
      this.options = options;
      this.branchIds = branchIds;
      this.exportJoinId = JoinUtility.createExportImportJoinQuery();
   }

   public String getExchangeFileName() {
      return this.exportName;
   }

   public void setExchangeFileName(String value) {
      this.exportName = value;
   }

   private void cleanUp(List<AbstractExportItem> taskList) {
      for (AbstractExportItem exportItem : taskList) {
         exportItem.cleanUp();
      }
      try {
         exportJoinId.delete();
      } catch (OseeCoreException ex) {
         onException("Export Clean-Up", ex);
      }
   }

   private File createTempFolder() throws OseeCoreException {
      File rootDirectory = ExchangeUtil.createTempFolder(oseeServices.getExchangeBasePath());
      if (!Strings.isValid(getExchangeFileName())) {
         setExchangeFileName(rootDirectory.getName());
      }
      return rootDirectory;
   }

   private void setUp() throws OseeCoreException {
      for (int branchId : branchIds) {
         exportJoinId.add((long) branchId, -1L);
      }
      exportJoinId.store();

      long maxTx = oseeServices.getDatabaseService().runPreparedQueryFetchObject(-1L, ExchangeDb.GET_MAX_TX);
      long userMaxTx = ExchangeDb.getMaxTransaction(options);
      if (userMaxTx == Long.MIN_VALUE || userMaxTx > maxTx) {
         options.put(ExportOptions.MAX_TXS.name(), Long.toString(maxTx));
      }
   }

   protected void handleTxWork() throws OseeCoreException {
      long startTime = System.currentTimeMillis();
      setUp();
      ExchangeDb exchangeDb = new ExchangeDb(oseeServices, options, exportJoinId.getQueryId());

      List<AbstractExportItem> taskList = exchangeDb.createTaskList();
      try {
         File tempFolder = createTempFolder();

         for (AbstractExportItem exportItem : taskList) {
            exportItem.setWriteLocation(tempFolder);
            exportItem.addExportListener(this);
         }

         sendTasksToExecutor(taskList, tempFolder);

         finishExport(tempFolder);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         cleanUp(taskList);
      }
      oseeServices.getLogger().info("Exported [%s] branch%s in [%s]", branchIds.size(),
         branchIds.size() != 1 ? "es" : "", Lib.getElapseString(startTime));
   }

   private void finishExport(File tempFolder) throws IllegalArgumentException, IOException {
      String zipTargetName = getExchangeFileName() + ZIP_EXTENSION;
      if (options.getBoolean(ExportOptions.COMPRESS.name())) {
         oseeServices.getLogger().info("Compressing Branch Export Data - [%s]", zipTargetName);
         File zipTarget = new File(tempFolder.getParent(), zipTargetName);
         Lib.compressDirectory(tempFolder, zipTarget.getAbsolutePath(), true);
         oseeServices.getLogger().info("Deleting Branch Export Temp Folder - [%s]", tempFolder);
         Lib.deleteDir(tempFolder);
      } else {
         File target = new File(tempFolder.getParent(), getExchangeFileName());
         if (!target.equals(tempFolder)) {
            if (!tempFolder.renameTo(target)) {
               oseeServices.getLogger().info("Unable to move [%s] to [%s]", tempFolder.getAbsolutePath(),
                  target.getAbsolutePath());
            }
         }
      }
   }

   private void sendTasksToExecutor(List<AbstractExportItem> taskList, final File exportFolder) throws Exception {
      List<Future<?>> futures = new ArrayList<Future<?>>();

      ExecutorAdmin executor = oseeServices.getExecutorAdmin();
      for (AbstractExportItem exportItem : taskList) {
         futures.add(executor.schedule("branch.export.worker", exportItem));
      }

      for (Future<?> future : futures) {
         future.get();
         if (!this.errorList.isEmpty()) {
            throw new OseeCoreException(errorList.toString());
         }
      }
   }

   @Override
   public void onException(String name, Throwable ex) {
      errorList.add(Lib.exceptionToString(ex));
   }

   @Override
   synchronized public void onExportItemCompleted(String name, long timeToProcess) {
      System.out.println(String.format("Exported: [%s] in [%s] ms", name, timeToProcess));
   }
}