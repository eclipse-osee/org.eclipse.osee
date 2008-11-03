package org.eclipse.osee.framework.db.connection.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.Activator;
import org.eclipse.osee.framework.db.connection.IConnection;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.logging.OseeLog;

public class OseeConnectionPool {

   private final Vector<OseeConnection> connections;
   final private long timeout = 60000;
   private final ConnectionReaper reaper;
   private final String dbDriver;
   private final String dbUrl;
   private final Properties properties;

   /**
    * @param dbInformation
    */
   public OseeConnectionPool(DbInformation dbInformation) {
      this(dbInformation.getConnectionData().getDBDriver(), dbInformation.getConnectionUrl(),
            dbInformation.getProperties());
   }

   public OseeConnectionPool(String dbDriver, String dbUrl, Properties properties) {
      connections = new Vector<OseeConnection>();
      this.dbDriver = dbDriver;
      this.dbUrl = dbUrl;
      this.properties = properties;
      reaper = new ConnectionReaper(this);
      reaper.start();
   }

   public synchronized void reapConnections() {

      long stale = System.currentTimeMillis() - timeout;
      Enumeration<OseeConnection> connlist = connections.elements();

      while ((connlist != null) && (connlist.hasMoreElements())) {
         OseeConnection conn = connlist.nextElement();
         if ((conn.inUse()) && (stale > conn.getLastUse()) && (!conn.validate())) {
            removeConnection(conn);
         }
      }
   }

   public synchronized boolean hasOpenConnection() {
      return connections.size() > 0;
   }

   public synchronized void closeConnections() {

      Enumeration<OseeConnection> connlist = connections.elements();

      while ((connlist != null) && (connlist.hasMoreElements())) {
         OseeConnection conn = connlist.nextElement();
         removeConnection(conn);
      }
   }

   private synchronized void removeConnection(OseeConnection conn) {
      connections.removeElement(conn);
   }

   public synchronized OseeConnection getConnection() throws OseeDataStoreException {
      OseeConnection c;
      for (int i = 0; i < connections.size(); i++) {
         c = connections.elementAt(i);
         if (c.lease()) {
            return c;
         }
      }
      try {
         c = getOseeConnection();
         c.lease();
         connections.addElement(c);
         return c;
      } catch (Throwable th) {
         throw new OseeDataStoreException("Unable to get a database connection: ", th);
      }
   }

   public OseeConnection getOseeConnection() throws Exception {
      IConnection connectionFactory =
            org.eclipse.osee.framework.db.connection.Activator.getDbConnectionFactory().get(dbDriver);
      OseeLog.log(Activator.class, Level.INFO, "Getting new connection: " + dbUrl);
      Connection connection = connectionFactory.getConnection(properties, dbUrl);
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
      return new OseeConnection(connection, this);
   }

   public synchronized void returnConnection(OseeConnection conn) {
      try {
         if (conn == null || conn.isClosed()) {
            removeConnection(conn);
         } else {
            conn.expireLease();
         }
      } catch (SQLException ex) {
         removeConnection(conn);
      }
   }
}