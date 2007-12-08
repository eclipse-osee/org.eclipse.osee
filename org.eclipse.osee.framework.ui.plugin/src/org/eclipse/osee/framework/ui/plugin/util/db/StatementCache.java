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
package org.eclipse.osee.framework.ui.plugin.util.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * @author Robert A. Fisher
 */
public abstract class StatementCache<S extends Statement> {
   // This map is hashed on the SQL statement, and stores the statement
   private HashMap<String, S> statementCache;
   private Connection connection;

   /**
    * 
    */
   public StatementCache(Connection connection) {
      this(connection, 20, .75f);
   }

   public StatementCache(Connection connection, int initialCapacity, float loadFactor) {
      this.connection = connection;
      statementCache = new HashMap<String, S>(initialCapacity, loadFactor);
   }

   public S getStatement(String sql) throws SQLException {
      S statement = statementCache.get(sql);
      if (statement == null) {
         statement = getNewConnection(connection, sql);
         statementCache.put(sql, statement);
      }

      return statement;
   }

   public final void clearCache() {
      for (Statement statement : statementCache.values()) {
         try {
            statement.close();
         } catch (SQLException ex) {
            ex.printStackTrace();
         }
      }
      statementCache.clear();
   }

   protected abstract S getNewConnection(Connection connection, String sql) throws SQLException;

   public static class Callable extends StatementCache<CallableStatement> {

      /**
       * @param connection
       * @param initialCapacity
       * @param loadFactor
       */
      public Callable(Connection connection, int initialCapacity, float loadFactor) {
         super(connection, initialCapacity, loadFactor);
      }

      /**
       * @param connection
       */
      public Callable(Connection connection) {
         super(connection);
      }

      @Override
      protected CallableStatement getNewConnection(Connection connection, String sql) throws SQLException {
         return connection.prepareCall(sql);
      }

   }

   public static class Prepared extends StatementCache<PreparedStatement> {

      /**
       * @param connection
       * @param initialCapacity
       * @param loadFactor
       */
      public Prepared(Connection connection, int initialCapacity, float loadFactor) {
         super(connection, initialCapacity, loadFactor);
      }

      /**
       * @param connection
       */
      public Prepared(Connection connection) {
         super(connection);
      }

      @Override
      protected PreparedStatement getNewConnection(Connection connection, String sql) throws SQLException {
         return connection.prepareStatement(sql);
      }

   }
}
