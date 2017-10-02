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
package org.eclipse.osee.framework.database.init.internal;

import java.util.logging.Level;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcDbType;

/**
 * @author Roberto E. Escobar
 */
public class PostDatabaseInitialization implements IDbInitializationTask {

   private static final String POSTGRESQL_VACUUM_AND_STATS = "VACUUM FULL VERBOSE ANALYZE";
   private static final String ORACLE_GATHER_STATS =
      "begin DBMS_STATS.GATHER_SCHEMA_STATS (ownname => '', estimate_percent => 99," + " granularity => 'ALL', degree => NULL , cascade => TRUE); end;";

   @Override
   public void run()  {
      OseeLog.log(PostDatabaseInitialization.class, Level.INFO, "Running Post-Initialization Process...");

      JdbcClient jdbcClient = DatabaseInitActivator.getInstance().getJdbcClient();
      JdbcDbType dbType = jdbcClient.getDbType();
      if (dbType.equals(JdbcDbType.postgresql)) {
         OseeLog.log(PostDatabaseInitialization.class, Level.INFO, "Vacuuming PostgreSQL");
         jdbcClient.runPreparedUpdate(POSTGRESQL_VACUUM_AND_STATS);
      } else if (dbType.equals(JdbcDbType.oracle)) {
         OseeLog.log(PostDatabaseInitialization.class, Level.INFO, "Gathering Oracle Statistics");
         jdbcClient.runPreparedUpdate(ORACLE_GATHER_STATS);
      } else {
         OseeLog.log(PostDatabaseInitialization.class, Level.INFO, "No - postdbinit process to run");
      }
   }
}
