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
package org.eclipse.osee.framework.database.internal.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.OseeConnection;

public class OseeConnectionImpl extends OseeConnection {
   final static private long timeout = 60000;
   private final OseeConnectionPoolImpl pool;
   private final Connection conn;
   private volatile boolean inuse;
   private long lastUsedTime;

   public OseeConnectionImpl(Connection conn, OseeConnectionPoolImpl pool) {
      super();
      this.conn = conn;
      this.pool = pool;
      this.inuse = true;
      this.lastUsedTime = 0;
   }

   @Override
   public void close() {
      pool.returnConnection(this);
   }

   @Override
   public boolean isClosed() throws OseeDataStoreException {
      try {
         return conn.isClosed();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public boolean isStale() {
      return !inUse() && getLastUse() + timeout < System.currentTimeMillis();
   }

   @Override
   public DatabaseMetaData getMetaData() throws OseeDataStoreException {
      try {
         return conn.getMetaData();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
   }

   PreparedStatement prepareStatement(String sql) throws SQLException {
      return prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
   }

   CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
   }

   synchronized boolean lease() {
      if (inuse) {
         return false;
      } else {
         inuse = true;
         return true;
      }
   }

   @Override
   protected void destroy() throws OseeDataStoreException {
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

   @Override
   protected void setAutoCommit(boolean autoCommit) throws OseeDataStoreException {
      try {
         conn.setAutoCommit(autoCommit);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   protected boolean getAutoCommit() throws SQLException {
      return conn.getAutoCommit();
   }

   @Override
   protected void commit() throws SQLException {
      conn.commit();
   }

   @Override
   protected void rollback() throws OseeDataStoreException {
      try {
         conn.rollback();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }
}