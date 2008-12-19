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
package org.eclipse.osee.framework.branch.management.exchange;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.eclipse.osee.framework.branch.management.ExportOptions;
import org.eclipse.osee.framework.branch.management.IExchangeTaskListener;
import org.eclipse.osee.framework.branch.management.exchange.export.AbstractDbExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.AbstractExportItem;
import org.eclipse.osee.framework.core.data.JoinUtility;
import org.eclipse.osee.framework.core.data.JoinUtility.ExportImportJoinQuery;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;

/**
 * @author Roberto E. Escobar
 */
final class ExportController extends DbTransaction implements IExchangeTaskListener {
   private static final String ZIP_EXTENSION = ".zip";

   private String exportName;
   private final Options options;
   private final int[] branchIds;
   private ExportImportJoinQuery joinQuery;
   private ExecutorService executorService;
   private List<String> errorList;

   ExportController(String exportName, Options options, int... branchIds) throws Exception {
      if (branchIds == null || branchIds.length <= 0) {
         throw new Exception("No branch selected for export.");
      }
      this.exportName = exportName;
      this.options = options;
      this.branchIds = branchIds;
      this.joinQuery = JoinUtility.createExportImportJoinQuery();
      this.errorList = Collections.synchronizedList(new ArrayList<String>());
   }

   public String getExchangeFileName() throws MalformedLocatorException {
      return this.exportName;
   }

   public int getExportQueryId() {
      return joinQuery != null ? joinQuery.getQueryId() : -1;
   }

   private void cleanUp(OseeConnection connection, List<AbstractExportItem> taskList) {
      for (AbstractExportItem exportItem : taskList) {
         exportItem.cleanUp();
      }
      try {
         if (joinQuery != null) {
            joinQuery.delete(connection);
            joinQuery = null;
         }
      } catch (OseeDataStoreException ex) {
         onException("Export Clean-Up", ex);
      }
      this.executorService.shutdown();
      this.executorService = null;
   }

   private File createTempFolder() {
      File rootDirectory = ExchangeUtil.createTempFolder();
      if (!Strings.isValid(exportName)) {
         this.exportName = rootDirectory.getName();
      }
      return rootDirectory;
   }

   private void setUp(OseeConnection connection, List<AbstractExportItem> taskList, File tempFolder) throws OseeDataStoreException {
      joinQuery = JoinUtility.createExportImportJoinQuery();
      for (int branchId : branchIds) {
         joinQuery.add(branchId, -1);
      }
      joinQuery.store(connection);

      long maxTx = ConnectionHandler.runPreparedQueryFetchLong(connection, -1, ExchangeDb.GET_MAX_TX);
      long userMaxTx = ExchangeDb.getMaxTransaction(options);
      if (userMaxTx == Long.MIN_VALUE || userMaxTx > maxTx) {
         options.put(ExportOptions.MAX_TXS.name(), Long.toString(maxTx));
      }

      for (AbstractExportItem exportItem : taskList) {
         exportItem.setOptions(options);
         exportItem.setWriteLocation(tempFolder);
         if (exportItem instanceof AbstractDbExportItem) {
            AbstractDbExportItem exportItem2 = ((AbstractDbExportItem) exportItem);
            exportItem2.setJoinQueryId(joinQuery.getQueryId());
            exportItem2.setConnection(connection);
         }
         exportItem.addExportListener(this);
      }
      executorService =
            Executors.newFixedThreadPool(2, CoreServerActivator.createNewThreadFactory("branch.export.worker"));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction#handleTxWork(java.sql.Connection)
    */
   @Override
   protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
      long startTime = System.currentTimeMillis();
      List<AbstractExportItem> taskList = ExchangeDb.createTaskList();
      try {
         File tempFolder = createTempFolder();
         setUp(connection, taskList, tempFolder);

         List<Future<?>> futures = new ArrayList<Future<?>>();
         for (AbstractExportItem exportItem : taskList) {
            futures.add(this.executorService.submit(exportItem));
         }

         for (Future<?> future : futures) {
            future.get();
            if (this.errorList.size() > 0) {
               throw new OseeCoreException(errorList.toString());
            }
         }

         String zipTargetName = exportName + ZIP_EXTENSION;
         if (this.options.getBoolean(ExportOptions.COMPRESS.name())) {
            OseeLog.log(this.getClass(), Level.INFO, String.format("Compressing Branch Export Data - [%s]",
                  zipTargetName));
            File zipTarget = new File(tempFolder.getParent(), zipTargetName);
            Lib.compressDirectory(tempFolder, zipTarget.getAbsolutePath(), true);
            OseeLog.log(this.getClass(), Level.INFO, String.format("Deleting Branch Export Temp Folder - [%s]",
                  tempFolder));
            Lib.deleteDir(tempFolder);
         } else {
            File target = new File(tempFolder.getParent(), exportName);
            if (!target.equals(tempFolder)) {
               tempFolder.renameTo(target);
            }
         }
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      } finally {
         cleanUp(connection, taskList);
      }
      int branchTotal = branchIds != null ? branchIds.length : 0;
      OseeLog.log(this.getClass(), Level.INFO, String.format("Exported [%s] branch%s in [%s]", branchTotal,
            branchTotal != 1 ? "es" : "", Lib.getElapseString(startTime)));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.export.IExportListener#onException(java.lang.String, java.lang.Throwable)
    */
   @Override
   synchronized public void onException(String name, Throwable ex) {
      errorList.add(Lib.exceptionToString(ex));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.export.IExportListener#onExportItemCompleted(java.lang.String, long)
    */
   @Override
   synchronized public void onExportItemCompleted(String name, long timeToProcess) {
      System.out.println(String.format("Exported: [%s] in [%s] ms", name, timeToProcess));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.export.IExportListener#onExportItemStarted(java.lang.String)
    */
   @Override
   public void onExportItemStarted(String name) {
   }
}
