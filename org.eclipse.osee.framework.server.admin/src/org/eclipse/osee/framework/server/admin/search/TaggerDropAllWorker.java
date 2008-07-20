/*
 * Created on Jul 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin.search;

import java.sql.Connection;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;

/**
 * @author Roberto E. Escobar
 */
class TaggerDropAllWorker extends BaseCmdWorker {

   private static final String TRUNCATE_SQL = "TRUNCATE osee_search_tags";

   //private static final String DELETE_TABLE_SQL = "DELETE FROM osee_search_tags";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.server.admin.search.BaseCmdWorker#doWork(java.sql.Connection, long)
    */
   @Override
   protected void doWork(long startTime) throws Exception {
      Connection connection = null;
      try {
         connection = ConnectionHandler.getConnection();
         ConnectionHandler.runPreparedUpdate(connection, TRUNCATE_SQL);
         println(String.format("Dropped all tags in %s.", getElapsedTime(startTime)));
      } finally {
         try {
            if (connection != null) {
               connection.close();
            }
         } catch (Exception ex) {
            printStackTrace(ex);
         }
      }
   }
}
