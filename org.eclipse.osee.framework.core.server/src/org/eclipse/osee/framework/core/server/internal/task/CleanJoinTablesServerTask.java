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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.IServerTask;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SchedulingScheme;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.logging.OseeLog;

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

   private static final String[] TABLES =
         new String[] {"osee_join_artifact", "osee_join_attribute", "osee_join_transaction", "osee_join_export_import",
               "osee_join_search_tags", "osee_tag_gamma_queue", "osee_join_id"};

   public String getName() {
      return NAME;
   }

   @Override
   public void run() {
      try {
         Timestamp time = new Timestamp(System.currentTimeMillis() - THREE_HOURS);
         for (String table : TABLES) {
            ConnectionHandler.runPreparedUpdate(String.format(DELETE_JOIN_TIME, table), time);
         }
         deleteFromJoinCleanup();
      } catch (OseeCoreException ex) {
         OseeLog.log(CoreServerActivator.class, Level.WARNING, ex);
      }
   }

   private void deleteFromJoinCleanup() throws OseeCoreException {
      List<Integer[]> queryIds = new ArrayList<Integer[]>();
      IOseeStatement chStmt = null;
      boolean isAlive = false;
      ISessionManager manager = CoreServerActivator.getSessionManager();
      try {
         chStmt = ConnectionHandler.getStatement();
         chStmt.runPreparedQuery(SELECT_SESSION_FROM_JOIN);
         String prevSessionId = "";
         while (chStmt.next()) {
            String sessionId = chStmt.getString("session_id");
            String tableName = chStmt.getString("table_name");
            Integer queryId = chStmt.getInt("query_id");
            if (!sessionId.equals(prevSessionId)) {
               isAlive = manager.isAlive(manager.getSessionById(sessionId).getSession());
            }
            if (!isAlive) {
               queryIds.add(new Integer[] {queryId});
               ConnectionHandler.runPreparedUpdate(String.format(DELETE_JOIN_TABLE_SESSION, tableName), queryId);
            }
            prevSessionId = sessionId;
         }
         ConnectionHandler.runBatchUpdate(DELETE_JOIN_CLEANUP, queryIds);
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
}
