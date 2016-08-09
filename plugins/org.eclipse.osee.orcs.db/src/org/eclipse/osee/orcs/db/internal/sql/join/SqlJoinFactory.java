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

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.SystemPreferences;

/**
 * @author Roberto E. Escobar
 */
public class SqlJoinFactory {

   public static final String JOIN_CLEANER__EXECUTOR_ID = "join.cleaner.executor.id";
   private static final Long DEFAULT_JOIN_EXPIRATION_SECONDS = 3L * 60L * 60L; // 3 hours
   private static final long DEFAULT_JOIN_CLEANER__PERIOD_MINUTES = 60L; // 60 minutes;

   private static final String EXPIRATION_SECS__ARTIFACT_JOIN_QUERY = "artifact.join.expiration.secs";
   private static final String EXPIRATION_SECS__CHAR_JOIN_QUERY = "char.join.expiration.secs";
   private static final String EXPIRATION_SECS__EXPORT_IMPORT_JOIN_QUERY = "export.import.join.expiration.secs";
   private static final String EXPIRATION_SECS__ID_JOIN_QUERY = "id.join.expiration.secs";
   private static final String EXPIRATION_SECS__TAG_QUEUE_JOIN_QUERY = "tag.queue.join.expiration.secs";
   private static final String EXPIRATION_SECS__TX_JOIN_QUERY = "tx.join.expiration.secs";

   private Log logger;
   private JdbcService jdbcService;
   private SystemPreferences preferences;
   private ExecutorAdmin executorAdmin;

   private Random random;
   private IJoinAccessor joinAccessor;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void setSystemPreferences(SystemPreferences preferences) {
      this.preferences = preferences;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void start() throws Exception {
      random = new Random();

      JdbcClient jdbcClient = jdbcService.getClient();

      joinAccessor = new DatabaseJoinAccessor(jdbcClient);

      Callable<?> callable = new JoinCleanerCallable(logger, jdbcClient);
      executorAdmin.scheduleAtFixedRate(JOIN_CLEANER__EXECUTOR_ID, callable, DEFAULT_JOIN_CLEANER__PERIOD_MINUTES,
         DEFAULT_JOIN_CLEANER__PERIOD_MINUTES, TimeUnit.MINUTES);
   }

   public void stop() throws Exception {
      if (executorAdmin != null) {
         executorAdmin.shutdown(JOIN_CLEANER__EXECUTOR_ID);
      }
      random = null;
   }

   private int getNewQueryId() {
      return random.nextInt();
   }

   private IJoinAccessor getAccessor() {
      return joinAccessor;
   }

   public TransactionJoinQuery createTransactionJoinQuery() {
      return createTransactionJoinQuery(null);
   }

   public TransactionJoinQuery createTransactionJoinQuery(Long expiresIn) {
      Long actualExpiration = getExpiresIn(expiresIn, EXPIRATION_SECS__TX_JOIN_QUERY);
      return new TransactionJoinQuery(getAccessor(), actualExpiration, getNewQueryId());
   }

   public IdJoinQuery createIdJoinQuery() {
      return createIdJoinQuery(null);
   }

   public IdJoinQuery createIdJoinQuery(Long expiresIn) {
      Long actualExpiration = getExpiresIn(expiresIn, EXPIRATION_SECS__ID_JOIN_QUERY);
      return new IdJoinQuery(getAccessor(), actualExpiration, getNewQueryId());
   }

   public ArtifactJoinQuery createArtifactJoinQuery() {
      return createArtifactJoinQuery(null);
   }

   public ArtifactJoinQuery createArtifactJoinQuery(Long expiresIn) {
      Long actualExpiration = getExpiresIn(expiresIn, EXPIRATION_SECS__ARTIFACT_JOIN_QUERY);
      return new ArtifactJoinQuery(getAccessor(), actualExpiration, getNewQueryId(), getMaxArtifactJoinSize());
   }

   public TagQueueJoinQuery createTagQueueJoinQuery() {
      return createTagQueueJoinQuery(null);
   }

   public TagQueueJoinQuery createTagQueueJoinQuery(Long expiresIn) {
      Long actualExpiration = getExpiresIn(expiresIn, EXPIRATION_SECS__TAG_QUEUE_JOIN_QUERY);
      return new TagQueueJoinQuery(getAccessor(), actualExpiration, getNewQueryId());
   }

   public ExportImportJoinQuery createExportImportJoinQuery() {
      return createExportImportJoinQuery(null);
   }

   public ExportImportJoinQuery createExportImportJoinQuery(Long expiresIn) {
      Long actualExpiration = getExpiresIn(expiresIn, EXPIRATION_SECS__EXPORT_IMPORT_JOIN_QUERY);
      return new ExportImportJoinQuery(getAccessor(), actualExpiration, getNewQueryId());
   }

   public CharJoinQuery createCharJoinQuery() {
      return createCharJoinQuery(null);
   }

   public CharJoinQuery createCharJoinQuery(Long expiresIn) {
      Long actualExpiration = getExpiresIn(expiresIn, EXPIRATION_SECS__CHAR_JOIN_QUERY);
      return new CharJoinQuery(getAccessor(), actualExpiration, getNewQueryId());
   }

   private Long getExpiresIn(Long actual, String defaultKey) {
      Long toReturn = DEFAULT_JOIN_EXPIRATION_SECONDS;
      if (actual != null) {
         toReturn = actual;
      } else {
         String expiration = preferences.getCachedValue(defaultKey);
         if (Strings.isNumeric(expiration)) {
            toReturn = Long.parseLong(expiration);
         }
      }
      return toReturn;
   }

   private int getMaxArtifactJoinSize() {
      int toReturn = Integer.MAX_VALUE;
      String maxSize = preferences.getCachedValue("artifact.join.max.size");
      if (Strings.isNumeric(maxSize)) {
         toReturn = Integer.parseInt(maxSize);
      }
      return toReturn;
   }

}
