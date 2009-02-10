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
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.IServerTask;
import org.eclipse.osee.framework.core.server.SchedulingScheme;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class CleanJoinTablesServerTask implements IServerTask {

   private final static String DELETE_JOIN_TIME = "DELETE FROM %s WHERE insert_time < ?";
   private final static long TEN_MINUTES = 10;
   private final static long TWENTY_MINUTES = 1000 * 60 * 20;

   private static final String NAME = "Clean up join tables";

   private static final String[] TABLES =
         new String[] {"osee_join_artifact", "osee_join_attribute", "osee_join_transaction", "osee_join_export_import",
               "osee_join_search_tags"};

   public String getName() {
      return NAME;
   }

   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      try {
         //         if (DEBUG) {
         //            Calendar cal = Calendar.getInstance();
         //            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy 'at' hh:mm:ss");
         //            OseeLog.log(CoreServerActivator.class, Level.INFO, String.format("Join Table cleanup ran on %s",
         //                  sdf.format(cal.getTime())));
         //         }
         Timestamp time = new Timestamp(System.currentTimeMillis() - TWENTY_MINUTES);
         for (String table : TABLES) {
            ConnectionHandler.runPreparedUpdate(String.format(DELETE_JOIN_TIME, table), time);
         }
      } catch (OseeDataStoreException ex) {
         OseeLog.log(CoreServerActivator.class, Level.WARNING, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.IServerTask#getInitialDelay()
    */
   @Override
   public long getInitialDelay() {
      return getPeriod();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.IServerTask#getPeriod()
    */
   @Override
   public long getPeriod() {
      return TEN_MINUTES;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.IServerTask#getSchedulingScheme()
    */
   @Override
   public SchedulingScheme getSchedulingScheme() {
      return SchedulingScheme.FIXED_RATE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.IServerTask#getTimeUnit()
    */
   @Override
   public TimeUnit getTimeUnit() {
      return TimeUnit.MINUTES;
   }
}
