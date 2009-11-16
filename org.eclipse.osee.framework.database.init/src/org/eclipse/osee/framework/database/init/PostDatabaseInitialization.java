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
package org.eclipse.osee.framework.database.init;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class PostDatabaseInitialization implements IDbInitializationTask {

   private static final String POSTGRESQL_VACUUM_AND_STATS = "VACUUM FULL VERBOSE ANALYZE";
   private static final String ORACLE_GATHER_STATS =
         "begin DBMS_STATS.GATHER_SCHEMA_STATS (ownname => '', estimate_percent => 99," + " granularity => 'ALL', degree => NULL , cascade => TRUE); end;";

   @Override
   public void run() throws OseeCoreException {
      OseeLog.log(PostDatabaseInitialization.class, Level.INFO, "Running Post-Initialization Process...");
      SupportedDatabase supportedDb = SupportedDatabase.getDatabaseType(ConnectionHandler.getMetaData());
      switch (supportedDb) {
         case postgresql:
            OseeLog.log(PostDatabaseInitialization.class, Level.INFO, "Vacuuming PostgreSQL");
            ConnectionHandler.runPreparedUpdate(POSTGRESQL_VACUUM_AND_STATS);
            break;
         case oracle:
            OseeLog.log(PostDatabaseInitialization.class, Level.INFO, "Gathering Oracle Statistics");
            ConnectionHandler.runPreparedUpdate(ORACLE_GATHER_STATS);
            break;
         default:
            OseeLog.log(PostDatabaseInitialization.class, Level.INFO, "No - postdbinit process to run");
            break;
      }
   }
}
