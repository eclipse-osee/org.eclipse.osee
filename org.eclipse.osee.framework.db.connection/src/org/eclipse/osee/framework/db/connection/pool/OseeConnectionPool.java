package org.eclipse.osee.framework.db.connection.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.Activator;
import org.eclipse.osee.framework.db.connection.IConnection;
import org.eclipse.osee.framework.db.connection.core.OseeDbVersion;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.info.DbDetailData.ConfigField;
import org.eclipse.osee.framework.logging.OseeLog;

public class OseeConnectionPool {

   private Vector<OseeConnection> connections;
   final private long timeout = 60000;
   private ConnectionReaper reaper;
   private DbInformation dbInformation;
   private boolean validityCheck;

   /**
    * @param dbInformation
    * @param validityCheck
    */
   public OseeConnectionPool(DbInformation dbInformation, boolean validityCheck) {
      connections = new Vector<OseeConnection>();
      this.dbInformation = dbInformation;
      this.validityCheck = validityCheck;
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

   public synchronized Connection getConnection() throws SQLException {
      OseeConnection c;
      for (int i = 0; i < connections.size(); i++) {
         c = (OseeConnection) connections.elementAt(i);
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
         OseeLog.log(Activator.class.getName(), Level.SEVERE, "Unable to get database connection.", th);
         throw new SQLException("Unable to get a database connection: " + th.getMessage());
      }
   }

   private OseeConnection getOseeConnection() throws Exception {
      IConnection connectionFactory =
            org.eclipse.osee.framework.db.connection.Activator.getInstance().getDbConnectionFactory().get(
                  dbInformation.getConnectionData().getDBDriver());

      String userName = dbInformation.getDatabaseDetails().getFieldValue(ConfigField.UserName);

      // Connection properties and attributes are added in the
      // Connection Description portion of the Database Config XML file.
      Properties properties = dbInformation.getProperties();
      String dbUrl = dbInformation.getConnectionUrl();

      OseeLog.log(Activator.class.getName(), Level.INFO, "Getting new connection: " + dbUrl);
      Connection connection = connectionFactory.getConnection(properties, dbUrl);
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      if (validityCheck && !userName.equals("peer")) {
         try {
            OseeDbVersion.ensureDatabaseCompatability(connection);
         } catch (Exception ex) {
            connection.close();
            throw ex;
         }
      }
      return new OseeConnection(connection, this);
   }

   public synchronized void returnConnection(OseeConnection conn) {
      conn.expireLease();
   }
}