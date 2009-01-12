package org.eclipse.osee.framework.db.connection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

public class OseeConnection {
   final static private long timeout = 60000;
   private OseeConnectionPool pool;
   private Connection conn;
   private volatile boolean inuse;
   private long lastUsedTime;

   OseeConnection(Connection conn, OseeConnectionPool pool) {
      this.conn = conn;
      this.pool = pool;
      this.inuse = true;
      this.lastUsedTime = 0;
   }

   public void close() {
      pool.returnConnection(this);
   }

   public boolean isClosed() throws OseeDataStoreException {
      try {
         return conn.isClosed();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public boolean isStale() {
      return !inUse() && getLastUse() + timeout < System.currentTimeMillis();
   }

   public DatabaseMetaData getMetaData() throws OseeDataStoreException {
      try {
         return conn.getMetaData();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   PreparedStatement prepareStatement(String sql) throws SQLException {
      return conn.prepareStatement(sql);
   }

   CallableStatement prepareCall(String sql) throws SQLException {
      return conn.prepareCall(sql);
   }

   synchronized boolean lease() {
      if (inuse) {
         return false;
      } else {
         inuse = true;
         return true;
      }
   }

   void destroy() throws OseeDataStoreException {
      try {
         conn.close();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
      pool.removeConnection(this);
   }

   boolean inUse() {
      return inuse;
   }

   long getLastUse() {
      return lastUsedTime;
   }

   void expireLease() {
      inuse = false;
      lastUsedTime = System.currentTimeMillis();
   }

   void setAutoCommit(boolean autoCommit) throws OseeDataStoreException {
      try {
         conn.setAutoCommit(autoCommit);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   boolean getAutoCommit() throws SQLException {
      return conn.getAutoCommit();
   }

   void commit() throws SQLException {
      conn.commit();
   }

   void rollback() throws OseeDataStoreException {
      try {
         conn.rollback();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }
}