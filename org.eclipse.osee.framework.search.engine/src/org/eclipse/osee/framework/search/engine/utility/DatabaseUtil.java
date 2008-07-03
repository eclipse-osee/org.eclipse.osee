/*
 * Created on Jul 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.search.engine.utility;

import java.sql.Connection;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseUtil {

   public static void executeQuery(String sql, IRowProcessor processor, Object... data) throws Exception {
      Connection connection = null;
      ConnectionHandlerStatement chStmt = null;
      try {
         connection = OseeDbConnection.getConnection();
         chStmt = ConnectionHandler.runPreparedQuery(connection, sql, data);
         while (chStmt.next()) {
            processor.processRow(chStmt.getRset());
         }
      } finally {
         DbUtil.close(chStmt);
         if (connection != null && connection.isClosed() != true) {
            connection.close();
         }
      }
   }
}
