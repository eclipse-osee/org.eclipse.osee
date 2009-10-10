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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.IServerTask;
import org.eclipse.osee.framework.core.server.SchedulingScheme;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class CleanJoinTablesServerTask implements IServerTask {

   private final static String DELETE_JOIN_TIME = "DELETE FROM %s WHERE insert_time < ?";
   private final static long TEN_MINUTES = 10;
   private final static long THREE_HOURS = 1000 * 60 * 60 * 3;

   private static final String NAME = "Clean up join tables";

   private static final String[] TABLES =
         new String[] {"osee_join_artifact", "osee_join_attribute", "osee_join_transaction", "osee_join_export_import",
               "osee_join_search_tags"};

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
      } catch (OseeDataStoreException ex) {
         OseeLog.log(CoreServerActivator.class, Level.WARNING, ex);
      }
   }

   @Override
   public long getInitialDelay() {
      return getPeriod();
   }

   @Override
   public long getPeriod() {
      return TEN_MINUTES;
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
