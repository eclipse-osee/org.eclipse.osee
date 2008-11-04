/*
 * Created on Aug 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.database.initialize.tasks;

import java.util.logging.Level;
import org.eclipse.osee.framework.database.IDbInitializationTask;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class PostDatabaseInitialization implements IDbInitializationTask {

   private static final String POSTGRESQL_VACUUM_AND_STATS = "VACUUM FULL VERBOSE ANALYZE";
   private static final String ORACLE_GATHER_STATS =
         "begin DBMS_STATS.GATHER_SCHEMA_STATS (ownname => '', estimate_percent => 99," + " granularity => 'ALL', degree => NULL , cascade => TRUE); end;";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#run(java.sql.Connection)
    */
   @Override
   public void run(OseeConnection connection) throws OseeCoreException {
      OseeLog.log(PostDatabaseInitialization.class, Level.INFO, "Running Post-Initialization Process...");
      SupportedDatabase supportedDb = SupportedDatabase.getDatabaseType(connection);
      switch (supportedDb) {
         case postgresql:
            OseeLog.log(PostDatabaseInitialization.class, Level.INFO, "Vacuumiing PostgreSQL");
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
