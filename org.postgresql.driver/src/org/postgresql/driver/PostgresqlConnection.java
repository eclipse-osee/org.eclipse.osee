/*
 * Created on May 30, 2007
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

package org.postgresql.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.eclipse.osee.framework.db.connection.IConnection;

public class PostgresqlConnection implements IConnection {

   private static final String driverName = "org.postgresql.Driver";

   public PostgresqlConnection() {
   }

   public Connection getConnection(Properties properties, String connectionURL) throws ClassNotFoundException, SQLException {
      Class.forName(driverName);
      Connection connection = DriverManager.getConnection(connectionURL, properties);
      return connection;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IConnection#getDriver()
    */
   @Override
   public String getDriver() {
      return driverName;
   }
}
