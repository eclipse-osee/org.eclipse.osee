package org.eclipse.osee.framework.db.connection;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.internal.InternalActivator;
import org.eclipse.osee.framework.logging.OseeLog;

class OseeConnectionPool {
   private final List<OseeConnection> connections = new CopyOnWriteArrayList<OseeConnection>();
   private final String dbDriver;
   private final String dbUrl;
   private final Properties properties;

   /**
    * @param dbInformation
    */
   public OseeConnectionPool(IDatabaseInfo databaseInfo) {
      this(databaseInfo.getDriver(), databaseInfo.getConnectionUrl(), databaseInfo.getConnectionProperties());
   }

   public OseeConnectionPool(String dbDriver, String dbUrl, Properties properties) {
      this.dbDriver = dbDriver;
      this.dbUrl = dbUrl;
      this.properties = properties;
   }

   public synchronized boolean hasOpenConnection() {
      return connections.size() > 0;
   }

   /**
    * at a minimum this should be called on jvm shutdown
    */
   public synchronized void closeConnections() {
      for (OseeConnection connection : connections) {
         connection.close();
      }
      connections.clear();
   }

   synchronized void removeConnection(OseeConnection conn) {
      connections.remove(conn);
      OseeLog.log(InternalActivator.class, Level.INFO, String.format(
            "removeConnection - %s - connection pool size [%s]", dbUrl, connections.size()));
   }

   public synchronized OseeConnection getConnection() throws OseeDataStoreException {
      for (OseeConnection connection : connections) {
         if (connection.lease()) {
            return connection;
         }
      }

      try {
         OseeConnection connection = getOseeConnection();
         connections.add(connection);
         OseeLog.log(InternalActivator.class, Level.INFO, String.format(
               "getConnection - %s - connection pool size [%s]", dbUrl, connections.size()));
         return connection;
      } catch (Throwable th) {
         throw new OseeDataStoreException("Unable to get a database connection: ", th);
      }
   }

   private OseeConnection getOseeConnection() throws Exception {
      IConnection connectionFactory = InternalActivator.getConnectionFactory().get(dbDriver);
      Connection connection = connectionFactory.getConnection(properties, dbUrl);
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
      return new OseeConnection(connection, this);
   }

   synchronized void returnConnection(OseeConnection connection) {
      try {
         if (connection.isClosed()) {
            removeConnection(connection);
         } else {
            connection.expireLease();
         }
      } catch (OseeDataStoreException ex) {
         OseeLog.log(InternalActivator.class, Level.SEVERE, ex);
         removeConnection(connection);
      }
   }

   synchronized void releaseUneededConnections() throws OseeDataStoreException {
      for (OseeConnection connection : connections) {
         if (connection.isStale()) {
            connection.destroy();
         }
      }
   }
}