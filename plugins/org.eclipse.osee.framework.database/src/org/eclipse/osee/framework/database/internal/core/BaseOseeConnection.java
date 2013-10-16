/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class BaseOseeConnection extends OseeConnection {

   private static final int timeout = 60000;
   private final Connection conn;

   public BaseOseeConnection(Connection conn) {
      super();
      this.conn = conn;
   }

   @Override
   public boolean isStale() {
      boolean result = true;
      try {
         result = !conn.isValid(timeout);
      } catch (SQLException ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public void close() throws OseeCoreException {
      destroy();
   }

   @Override
   public boolean isClosed() throws OseeCoreException {
      try {
         return conn.isClosed();
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return false; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public DatabaseMetaData getMetaData() throws OseeCoreException {
      try {
         return conn.getMetaData();
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
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

   @SuppressWarnings("unused")
   @Override
   protected void destroy() throws OseeCoreException {
      try {
         conn.close();
      } catch (SQLException ex) {
         // Do Nothing - OseeExceptions.wrapAndThrow(ex);
      }
   }

   @Override
   protected void setAutoCommit(boolean autoCommit) throws OseeCoreException {
      try {
         conn.setAutoCommit(autoCommit);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   @Override
   protected boolean getAutoCommit() throws OseeCoreException {
      try {
         return conn.getAutoCommit();
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return false; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   protected void commit() throws OseeCoreException {
      try {
         conn.commit();
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   @Override
   protected void rollback() throws OseeCoreException {
      try {
         conn.rollback();
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

}