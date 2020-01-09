/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.executor.ExecutionCallbackAdapter;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.ExportOptions;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.SystemProperties;
import org.eclipse.osee.orcs.db.internal.exchange.ExchangeUtil;
import org.eclipse.osee.orcs.db.internal.exchange.ExportItemFactory;
import org.eclipse.osee.orcs.db.internal.exchange.export.AbstractExportItem;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class ExportBranchDatabaseCallable extends AbstractDatastoreCallable<URI> {

   private static final String BRANCH_EXPORT_EXECUTOR_ID = "branch.export.worker";

   private final ExportItemFactory factory;
   private final SqlJoinFactory joinFactory;
   private final SystemProperties properties;
   private final ExecutorAdmin executorAdmin;
   private final List<? extends BranchId> branches;
   private final PropertyStore options;

   private String exportName;

   public ExportBranchDatabaseCallable(OrcsSession session, ExportItemFactory factory, SqlJoinFactory joinFactory, SystemProperties properties, ExecutorAdmin executorAdmin, List<? extends BranchId> branches, PropertyStore options, String exportName) {
      super(factory.getLogger(), session, factory.getDbService());
      this.joinFactory = joinFactory;
      this.factory = factory;
      this.properties = properties;
      this.executorAdmin = executorAdmin;
      this.branches = branches;
      this.options = options;
      this.exportName = exportName;
   }

   private ExecutorAdmin getExecutorAdmin() {
      return executorAdmin;
   }

   private String getExchangeFileName() {
      return this.exportName;
   }

   private void setExchangeFileName(String name) {
      this.exportName = name;
   }

   @Override
   public URI call() throws Exception {
      long startTime = System.currentTimeMillis();
      try {
         Conditions.checkNotNull(factory, "exportItemFactory");
         Conditions.checkNotNull(executorAdmin, "executorAdmin");

         Conditions.checkNotNullOrEmpty(branches, "branches");
         Conditions.checkNotNull(options, "options");
         doWork();
         return factory.getResourceManager().generateResourceLocator(ResourceConstants.EXCHANGE_RESOURCE_PROTOCOL, "",
            getExchangeFileName()).getLocation();
      } finally {
         getLogger().info("Exported [%s] branch%s in [%s]", branches.size(), branches.size() != 1 ? "es" : "",
            Lib.getElapseString(startTime));
      }
   }

   private File createTempFolder() {
      String exchangeBasePath = ResourceConstants.getExchangeDataPath(properties);
      File rootDirectory = ExchangeUtil.createTempFolder(exchangeBasePath);
      if (!Strings.isValid(getExchangeFileName())) {
         setExchangeFileName(rootDirectory.getName());
      }
      return rootDirectory;
   }

   private void doWork() throws Exception {
      try (IdJoinQuery joinQuery = joinFactory.createIdJoinQuery()) {
         joinQuery.addAndStore(branches);
         List<AbstractExportItem> taskList = factory.createTaskList((long) joinQuery.getQueryId(), options);
         File tempFolder = createTempFolder();

         for (AbstractExportItem exportItem : taskList) {
            exportItem.setWriteLocation(tempFolder);
         }

         executeTasks(taskList);

         finishExport(tempFolder);

         for (AbstractExportItem exportItem : taskList) {
            exportItem.cleanUp();
         }
      }
   }

   private void finishExport(File tempFolder) throws IllegalArgumentException, IOException {
      String zipTargetName = getExchangeFileName() + "." + ResourceConstants.ZIP_EXTENSION;

      if (options.getBoolean(ExportOptions.COMPRESS.name())) {
         getLogger().info("Compressing Branch Export Data - [%s]", zipTargetName);
         File zipTarget = new File(tempFolder.getParent(), zipTargetName);
         Lib.compressDirectory(tempFolder, zipTarget.getAbsolutePath(), true);
         getLogger().info("Deleting Branch Export Temp Folder - [%s]", tempFolder);
         Lib.deleteDir(tempFolder);
      } else {
         File target = new File(tempFolder.getParent(), getExchangeFileName());
         if (!target.equals(tempFolder)) {
            if (!tempFolder.renameTo(target)) {
               getLogger().info("Unable to move [%s] to [%s]", tempFolder.getAbsolutePath(), target.getAbsolutePath());
            }
         }
      }
   }

   private void executeTasks(List<AbstractExportItem> taskList) throws Exception {
      final List<Throwable> throwables = new LinkedList<>();
      final List<Future<?>> futures = new CopyOnWriteArrayList<>();

      ExecutorAdmin executor = getExecutorAdmin();
      for (AbstractExportItem exportItem : taskList) {
         Future<?> future =
            executor.schedule(BRANCH_EXPORT_EXECUTOR_ID, exportItem, new ExecutionCallbackAdapter<Boolean>() {

               @Override
               public void onFailure(Throwable throwable) {
                  super.onFailure(throwable);
                  throwables.add(throwable);
                  for (Future<?> future : futures) {
                     if (!future.isDone() && !future.isCancelled()) {
                        future.cancel(true);
                     }
                  }
               }

            });
         futures.add(future);
      }

      for (Future<?> future : futures) {
         future.get();
      }

      if (!throwables.isEmpty()) {
         List<StackTraceElement> trace = new LinkedList<>();
         for (Throwable th : throwables) {
            for (StackTraceElement element : th.getStackTrace()) {
               trace.add(element);
            }
         }
         OseeCoreException exception = new OseeCoreException("Error detected during branch export");
         exception.setStackTrace(trace.toArray(new StackTraceElement[trace.size()]));
         throw exception;
      }
   }
}