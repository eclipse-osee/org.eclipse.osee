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
package org.eclipse.osee.framework.branch.management.export;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.ExportImportJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
final class ExportController implements IExportListener, Runnable {
   private static final String ZIP_EXTENSION = ".zip";

   private BranchExport branchExport;
   private String exportName;
   private Options options;
   private int[] branchIds;
   private ExportImportJoinQuery joinQuery;
   private ExecutorService executorService;

   public ExportController(BranchExport branchExport, String exportName, Options options, int... branchIds) throws Exception {
      if (branchIds == null || branchIds.length <= 0) {
         throw new Exception("No branch selected for export.");
      }
      this.branchExport = branchExport;
      this.exportName = exportName;
      this.options = options;
      this.branchIds = branchIds;
      this.joinQuery = JoinUtility.createExportImportJoinQuery();
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
      String userHome = System.getProperty("user.home");
      File rootDirectory = new File(userHome + File.separator + "export." + new Date().getTime() + File.separator);
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
         if (exportItem instanceof RelationalExportItem) {
            RelationalExportItem relationalExportItem = ((RelationalExportItem) exportItem);
            relationalExportItem.setJoinQueryId(joinQuery.getQueryId());
         }
         exportItem.addExportListener(this);
      }
      executorService = Executors.newFixedThreadPool(2);
   }

   private Future<?> submitTask(int exportQueryId, Runnable runnable) {
      //      FutureTask<Object> futureTask = new FutureExportTask(exportQueryId, runnable);
      //      this.futureTasks.put(exportQueryId, futureTask);
      return this.executorService.submit(runnable);
   }

   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      long startTime = System.currentTimeMillis();
      Connection connection = null;
      List<AbstractExportItem> taskList = branchExport.getTaskList();
      try {
         File tempFolder = createTempFolder();
         connection = OseeDbConnection.getConnection();
         setUp(connection, taskList, tempFolder);

         List<Future<?>> futures = new ArrayList<Future<?>>();
         for (AbstractExportItem exportItem : taskList) {
            futures.add(submitTask(joinQuery.getQueryId(), exportItem));
         }

         for (Future<?> future : futures) {
            future.get();
         }

         String zipTargetName = exportName + ZIP_EXTENSION;
         System.out.println(String.format("Compressing Branch Export Data - [%s]", zipTargetName));
         File zipTarget = new File(tempFolder.getParent(), zipTargetName);
         Lib.compressDirectory(tempFolder, zipTarget.getAbsolutePath(), true);
         Lib.deleteDir(tempFolder);
      } catch (Exception ex) {
         onException(this.getClass().getName(), ex);
      } finally {
         cleanUp(connection, taskList);
         if (connection != null) {
            try {
               connection.close();
            } catch (SQLException ex) {
               onException(this.getClass().getName(), ex);
            }
         }
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
      System.err.println(String.format("Export Error in: [%s]\n", name));
      ex.printStackTrace(System.err);
      //      branchExport.cancelExport(getExportQueryId());
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
      //      System.out.println(String.format("Exporting: [%s] ", name));
   }
}
