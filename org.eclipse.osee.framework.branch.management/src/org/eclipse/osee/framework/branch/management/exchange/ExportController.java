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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.branch.management.IExchangeTaskListener;
import org.eclipse.osee.framework.branch.management.exchange.export.AbstractDbExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.AbstractExportItem;
import org.eclipse.osee.framework.branch.management.exchange.resource.ExchangeProvider;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.ExportImportJoinQuery;
import org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.common.Activator;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;

/**
 * @author Roberto E. Escobar
 */
final class ExportController extends DbTransaction implements IExchangeTaskListener {
   private static final String ZIP_EXTENSION = ".zip";
   private static final String TEMP_NAME_PREFIX = "branch.xchng.";
   private String exportName;
   private Options options;
   private int[] branchIds;
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

   private void cleanUp(Connection connection, List<AbstractExportItem> taskList) {
      for (AbstractExportItem exportItem : taskList) {
         exportItem.cleanUp();
      }
      try {
         if (joinQuery != null) {
            joinQuery.delete(connection);
            joinQuery = null;
         }
      } catch (SQLException ex) {
         onException("Export Clean-Up", ex);
      }
   }

   private File createTempFolder() {
      String basePath = ExchangeProvider.getExchangeFilePath();
      String fileName = TEMP_NAME_PREFIX + Lib.getDateTimeString();
      if (!Strings.isValid(exportName)) {
         this.exportName = fileName;
      }
      File rootDirectory = new File(basePath + fileName + File.separator);
      rootDirectory.mkdirs();
      return rootDirectory;
   }

   private void setUp(Connection connection, List<AbstractExportItem> taskList, File tempFolder) throws SQLException {
      joinQuery = JoinUtility.createExportImportJoinQuery();
      for (int branchId : branchIds) {
         joinQuery.add(branchId, -1);
      }
      joinQuery.store(connection);
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
            Executors.newFixedThreadPool(2, Activator.getInstance().createNewThreadFactory("branch.export.worker"));
   }

   private Future<?> submitTask(int exportQueryId, Runnable runnable) {
      return this.executorService.submit(runnable);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction#handleTxWork(java.sql.Connection)
    */
   @Override
   protected void handleTxWork(Connection connection) throws Exception {
      long startTime = System.currentTimeMillis();
      List<AbstractExportItem> taskList = BranchExportTaskConfig.getTaskList();
      try {
         File tempFolder = createTempFolder();
         setUp(connection, taskList, tempFolder);

         List<Future<?>> futures = new ArrayList<Future<?>>();
         for (AbstractExportItem exportItem : taskList) {
            futures.add(submitTask(joinQuery.getQueryId(), exportItem));
         }

         for (Future<?> future : futures) {
            future.get();
            if (this.errorList.size() > 0) {
               throw new Exception(errorList.toString());
            }
         }

         String zipTargetName = exportName + ZIP_EXTENSION;
         System.out.println(String.format("Compressing Branch Export Data - [%s]", zipTargetName));
         File zipTarget = new File(tempFolder.getParent(), zipTargetName);
         Lib.compressDirectory(tempFolder, zipTarget.getAbsolutePath(), true);
         Lib.deleteDir(tempFolder);
      } finally {
         cleanUp(connection, taskList);
      }
      int branchTotal = branchIds != null ? branchIds.length : 0;
      System.out.println(String.format("Exported [%s] branch%s in [%s]", branchTotal, branchTotal != 1 ? "es" : "",
            Lib.getElapseString(startTime)));
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
