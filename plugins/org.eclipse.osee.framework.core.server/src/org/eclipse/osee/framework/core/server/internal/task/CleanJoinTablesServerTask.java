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
package org.eclipse.osee.framework.core.server.internal.task;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.server.IServerTask;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SchedulingScheme;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class CleanJoinTablesServerTask implements IServerTask {

   private final static String DELETE_JOIN_TIME = "DELETE FROM %s WHERE insert_time < ?";
   private final static String DELETE_JOIN_CLEANUP = "DELETE FROM osee_join_cleanup WHERE query_id = ?";
   private final static String DELETE_JOIN_TABLE_SESSION = "DELETE FROM %s WHERE query_id = ?";
   private final static String SELECT_SESSION_FROM_JOIN = "SELECT * from osee_join_cleanup order by session_id";
   private final static long SIXTY_MINUTES = 60;
   private final static long THREE_HOURS = 1000 * 60 * 60 * 3;

   private static final String NAME = "Clean up join tables";

   private static final String[] TABLES = new String[] {
      "osee_join_artifact",
      "osee_join_transaction",
      "osee_join_export_import",
      "osee_tag_gamma_queue",
      "osee_join_id"};

   private IOseeDatabaseService dbService;
   private Log logger;
   private ISessionManager sessionManager;
   private IStatus lastStatus = Status.OK_STATUS;
   private final Map<String, Integer> deletedByTime = new ConcurrentHashMap<String, Integer>();
   private final CompositeKeyHashMap<String, String, Integer> sessionDeletes =
      new CompositeKeyHashMap<String, String, Integer>(10, true);
   private long lastRan = 0;

   public void setDbService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setSessionManager(ISessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public void run() {
      try {
         lastRan = System.currentTimeMillis();
         deletedByTime.clear();
         sessionDeletes.clear();

         Timestamp time = new Timestamp(lastRan - THREE_HOURS);
         for (String table : TABLES) {
            int rows = dbService.runPreparedUpdate(String.format(DELETE_JOIN_TIME, table), time);
            deletedByTime.put(table, rows);
         }
         deleteFromJoinCleanup();
         lastStatus = Status.OK_STATUS;
      } catch (OseeCoreException ex) {
         String message = "Error cleaning up tables";
         logger.warn(ex, message);
         lastStatus = new Status(IStatus.ERROR, CleanJoinTablesServerTask.class.getName(), message, ex);
      }
   }

   private void deleteFromJoinCleanup() throws OseeCoreException {
      List<Integer[]> queryIds = new ArrayList<Integer[]>();
      IOseeStatement chStmt = dbService.getStatement();
      boolean isAlive = false;
      ISessionManager manager = sessionManager;
      try {
         chStmt.runPreparedQuery(SELECT_SESSION_FROM_JOIN);

         String prevSessionId = "";
         while (chStmt.next()) {
            String sessionId = chStmt.getString("session_id");
            String tableName = chStmt.getString("table_name");
            Integer queryId = chStmt.getInt("query_id");
            if (!sessionId.equals(prevSessionId)) {
               ISession sessionById = manager.getSessionById(sessionId);
               isAlive = sessionById != null && SessionUtil.isAlive(sessionById);
            }
            if (!isAlive) {
               queryIds.add(new Integer[] {queryId});
               int rows = dbService.runPreparedUpdate(String.format(DELETE_JOIN_TABLE_SESSION, tableName), queryId);
               sessionDeletes.put(sessionId, tableName, rows);
            }
            prevSessionId = sessionId;
         }
         dbService.runBatchUpdate(DELETE_JOIN_CLEANUP, queryIds);
      } finally {
         chStmt.close();
      }
   }

   @Override
   public long getInitialDelay() {
      return getPeriod();
   }

   @Override
   public long getPeriod() {
      return SIXTY_MINUTES;
   }

   @Override
   public SchedulingScheme getSchedulingScheme() {
      return SchedulingScheme.FIXED_RATE;
   }

   @Override
   public TimeUnit getTimeUnit() {
      return TimeUnit.MINUTES;
   }

   @Override
   public IStatus getLastStatus() {
      IStatus toReturn = lastStatus;
      if (toReturn.isOK()) {
         String message = buildMessage();
         toReturn = new Status(IStatus.OK, CleanJoinTablesServerTask.class.getName(), message.toString());
      }
      return toReturn;
   }

   private String buildMessage() {
      StringBuilder message = new StringBuilder();
      message.append(String.format("[%s] - Last ran: [", getName()));
      if (lastRan != 0) {
         message.append(Lib.getElapseString(lastRan));
         message.append(" ago]\n");
      } else {
         message.append("Never]\n");
      }
      for (Entry<String, Integer> entry : deletedByTime.entrySet()) {
         message.append(String.format("Deleted [%s] from table [%s]\n", entry.getValue(), entry.getKey()));
      }

      for (Entry<Pair<String, String>, Integer> entry : sessionDeletes.entrySet()) {
         String sessionId = entry.getKey().getFirst();
         String tableName = entry.getKey().getSecond();
         Integer rows = entry.getValue();
         message.append(String.format("Deleted [%s] from table [%s] for sessionId [%s]\n", rows, tableName, sessionId));
      }
      return message.toString();
   }
}
