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
package org.eclipse.osee.orcs.db.internal.sql.join;

import java.util.concurrent.TimeUnit;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class SqlJoinFactory {

   public static final String JOIN_CLEANER__EXECUTOR_ID = "join.cleaner.executor.id";
   private static final long DEFAULT_JOIN_CLEANER__PERIOD_MINUTES = 60L; // 60 minutes;

   private Log logger;
   private JdbcClient jdbcClient;
   private ExecutorAdmin executorAdmin;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setJdbcService(JdbcService jdbcService) {
      jdbcClient = jdbcService.getClient();
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void start() throws Exception {
      Runnable runnable = new JoinCleanerCallable(logger, jdbcClient);
      executorAdmin.scheduleAtFixedRate(JOIN_CLEANER__EXECUTOR_ID, runnable, DEFAULT_JOIN_CLEANER__PERIOD_MINUTES,
         DEFAULT_JOIN_CLEANER__PERIOD_MINUTES, TimeUnit.MINUTES);
   }

   public void stop() throws Exception {
      if (executorAdmin != null) {
         executorAdmin.shutdown(JOIN_CLEANER__EXECUTOR_ID);
      }
   }

   public IdJoinQuery createIdJoinQuery() {
      return createIdJoinQuery(null);
   }

   public IdJoinQuery createIdJoinQuery(JdbcConnection connection) {
      return new IdJoinQuery(jdbcClient, connection);
   }

   public Id4JoinQuery createId4JoinQuery() {
      return createId4JoinQuery(null);
   }

   public Id4JoinQuery createId4JoinQuery(JdbcConnection connection) {
      return new Id4JoinQuery(jdbcClient, connection);
   }

   public TagQueueJoinQuery createTagQueueJoinQuery() {
      return createTagQueueJoinQuery(null);
   }

   public TagQueueJoinQuery createTagQueueJoinQuery(JdbcConnection connection) {
      return new TagQueueJoinQuery(jdbcClient, connection);
   }

   public ExportImportJoinQuery createExportImportJoinQuery() {
      return createExportImportJoinQuery(null);
   }

   public ExportImportJoinQuery createExportImportJoinQuery(JdbcConnection connection) {
      return new ExportImportJoinQuery(jdbcClient, connection);
   }

   public CharJoinQuery createCharJoinQuery() {
      return createCharJoinQuery(null);
   }

   public CharJoinQuery createCharJoinQuery(JdbcConnection connection) {
      return new CharJoinQuery(jdbcClient, connection);
   }
}