/*
 * Created on May 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.pool.OseeConnectionPool;

/**
 * @author b1528444
 */
public class OseeDbConnection {
   private static DbInformation defaultDbInformation;
   private static Map<DbInformation, OseeConnectionPool> dbInfoToPools =
         new ConcurrentHashMap<DbInformation, OseeConnectionPool>();

   public static Connection getConnection() throws SQLException {
      verifyDefaultDbInformation();
      return getConnection(defaultDbInformation);
   }

   public static Connection getConnection(DbInformation dbInformation) throws SQLException {
      return getConnection(dbInformation, true);
   }

   public static Connection getConnection(DbInformation dbInformation, boolean validityCheck) throws SQLException {
      OseeConnectionPool pool = dbInfoToPools.get(dbInformation);
      if (pool == null) {
         pool = new OseeConnectionPool(dbInformation, validityCheck);
         dbInfoToPools.put(dbInformation, pool);
      }
      return pool.getConnection();
   }

   private static void verifyDefaultDbInformation() {
      if (defaultDbInformation == null) {
         defaultDbInformation = OseeDb.getDefaultDatabaseService();
      }
   }
}
