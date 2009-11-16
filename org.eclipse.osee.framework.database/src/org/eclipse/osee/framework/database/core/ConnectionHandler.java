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

package org.eclipse.osee.framework.database.core;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.internal.InternalActivator;

/**
 * Handles connection recovery in the event of database connection being lost
 * 
 * @author Jeff C. Phillips
 */
public final class ConnectionHandler {

   private static IOseeDatabaseService getDatabase() throws OseeDataStoreException {
      return InternalActivator.getInstance().getDatabaseService();
   }

   /**
    * Avoid using if possible
    */
   public static OseeConnection getConnection() throws OseeDataStoreException {
      return getDatabase().getConnection();
   }

   /**
    * Avoid using if possible
    */
   public static OseeConnection getConnection(IDatabaseInfo info) throws OseeDataStoreException {
      return getDatabase().getConnection(info);
   }

   public static ConnectionHandlerStatement getStatement() throws OseeDataStoreException {
      return getDatabase().getStatement();
   }

   public static ConnectionHandlerStatement getStatement(OseeConnection connection) throws OseeDataStoreException {
      return getDatabase().getStatement(connection);
   }

   public static ConnectionHandlerStatement getStatement(OseeConnection connection, boolean autoClose) throws OseeDataStoreException {
      return getDatabase().getStatement(connection, autoClose);
   }

   /**
    * This method should only be used when not contained in a DB transaction
    * 
    * @param query
    * @param data
    * @return number of records updated
    * @throws OseeDataStoreException
    */
   public static <O extends Object> int runPreparedUpdate(String query, O... data) throws OseeDataStoreException {
      OseeConnection connection = getDatabase().getConnection();
      try {
         return runPreparedUpdate(connection, query, data);
      } finally {
         connection.close();
      }
   }

   /**
    * This method should only be used when not contained in a DB transaction
    * 
    * @param query
    * @param dataList
    * @return number of records updated
    * @throws OseeDataStoreException
    */
   public static <O extends Object> int runBatchUpdate(String query, List<O[]> dataList) throws OseeDataStoreException {
      OseeConnection connection = getDatabase().getConnection();
      try {
         return runBatchUpdate(connection, query, dataList);
      } finally {
         connection.close();
      }
   }

   /**
    * This method should only be used when contained in a DB transaction
    * 
    * @param connection
    * @param query
    * @param data
    * @return number of records updated
    * @throws OseeDataStoreException
    */
   public static <O extends Object> int runPreparedUpdate(OseeConnection connection, String query, O... data) throws OseeDataStoreException {
      if (connection == null) {
         return runPreparedUpdate(query, data);
      }
      return getDatabase().runPreparedUpdate(connection, query, data);
   }

   public static <O extends Object> int runBatchUpdate(OseeConnection connection, String query, List<O[]> dataList) throws OseeDataStoreException {
      if (connection == null) {
         return runBatchUpdate(query, dataList);
      }
      return getDatabase().runBatchUpdate(connection, query, dataList);
   }

   public static <O extends Object> int runPreparedQueryFetchInt(int defaultValue, String query, O... data) throws OseeDataStoreException {
      return runPreparedQueryFetchInt(getStatement(), defaultValue, query, data);
   }

   public static <O extends Object> int runPreparedQueryFetchInt(OseeConnection connection, int defaultValue, String query, O... data) throws OseeDataStoreException {
      return runPreparedQueryFetchInt(getStatement(connection), defaultValue, query, data);
   }

   private static <O extends Object> int runPreparedQueryFetchInt(ConnectionHandlerStatement chStmt, int defaultValue, String query, O... data) throws OseeDataStoreException {
      try {
         chStmt.runPreparedQuery(1, query, data);
         if (chStmt.next()) {
            return chStmt.getInt(1);
         }
         return defaultValue;
      } finally {
         chStmt.close();
      }
   }

   public static <O extends Object> int runCallableStatementFetchInt(String query, O... data) throws OseeDataStoreException {
      return runCallableStatementFetchInt(getStatement(), query, data);
   }

   public static <O extends Object> int runCallableStatementFetchInt(OseeConnection connection, String query, O... data) throws OseeDataStoreException {
      return runCallableStatementFetchInt(getStatement(connection), query, data);
   }

   public static <O extends Object> int runCallableStatementFetchInt(ConnectionHandlerStatement chStmt, String query, O... data) throws OseeDataStoreException {
      try {
         chStmt.runCallableStatement(query, data);
         return chStmt.getCallableInt(1);
      } finally {
         chStmt.close();
      }
   }

   public static <O extends Object> double runCallableStatementFetchDouble(String query, O... data) throws OseeDataStoreException {
      return runCallableStatementFetchDouble(getStatement(), query, data);
   }

   public static <O extends Object> double runCallableStatementFetchDouble(OseeConnection connection, String query, O... data) throws OseeDataStoreException {
      return runCallableStatementFetchDouble(getStatement(connection), query, data);
   }

   private static <O extends Object> double runCallableStatementFetchDouble(ConnectionHandlerStatement chStmt, String query, O... data) throws OseeDataStoreException {
      try {
         chStmt.runCallableStatement(query, data);
         return chStmt.getCallableDouble(1);
      } finally {
         chStmt.close();
      }
   }

   public static <O extends Object> long runPreparedQueryFetchLong(long defaultValue, String query, O... data) throws OseeDataStoreException {
      return runPreparedQueryFetchLong(getStatement(), defaultValue, query, data);
   }

   public static <O extends Object> long runPreparedQueryFetchLong(OseeConnection connection, long defaultValue, String query, O... data) throws OseeDataStoreException {
      return runPreparedQueryFetchLong(getStatement(connection), defaultValue, query, data);
   }

   private static <O extends Object> long runPreparedQueryFetchLong(ConnectionHandlerStatement chStmt, long defaultValue, String query, O... data) throws OseeDataStoreException {
      try {
         chStmt.runPreparedQuery(1, query, data);
         if (chStmt.next()) {
            return chStmt.getLong(1);
         }
         return defaultValue;
      } finally {
         chStmt.close();
      }
   }

   public static <O extends Object> String runPreparedQueryFetchString(String defaultValue, String query, O... data) throws OseeDataStoreException {
      return runPreparedQueryFetchString(getStatement(), defaultValue, query, data);
   }

   public static <O extends Object> String runPreparedQueryFetchString(OseeConnection connection, String defaultValue, String query, O... data) throws OseeDataStoreException {
      return runPreparedQueryFetchString(getStatement(connection), defaultValue, query, data);
   }

   private static <O extends Object> String runPreparedQueryFetchString(ConnectionHandlerStatement chStmt, String defaultValue, String query, O... data) throws OseeDataStoreException {
      try {
         chStmt.runPreparedQuery(1, query, data);
         if (chStmt.next()) {
            return chStmt.getString(1);
         }
         return defaultValue;
      } finally {
         chStmt.close();
      }
   }

   /**
    * Cause constraint checking to be deferred until the end of the current transaction.
    * 
    * @param connection
    * @throws OseeDataStoreException
    */
   public static void deferConstraintChecking(OseeConnection connection) throws OseeDataStoreException {
      if (SupportedDatabase.getDatabaseType(connection.getMetaData()) == SupportedDatabase.derby) {
         return;
      }
      // NOTE: this must be a PreparedStatement to play correctly with DB Transactions.
      runPreparedUpdate(connection, "SET CONSTRAINTS ALL DEFERRED");
   }

   public static DatabaseMetaData getMetaData() throws OseeDataStoreException {
      OseeConnection connection = getDatabase().getConnection();
      try {
         return connection.getMetaData();
      } finally {
         connection.close();
      }
   }

   public static boolean doesTableExist(String targetTable) {
      return doesTableExist(null, targetTable);
   }

   public static boolean doesTableExist(String targetSchema, String targetTable) {
      ResultSet resultSet = null;
      try {
         resultSet = getMetaData().getTables(null, null, null, new String[] {"TABLE"});
         if (resultSet != null) {
            while (resultSet.next()) {
               String tableName = resultSet.getString("TABLE_NAME");
               String schemaName = resultSet.getString("TABLE_SCHEM");
               if (targetTable.equalsIgnoreCase(tableName)) {
                  if (targetSchema == null || targetSchema.equalsIgnoreCase(schemaName)) {
                     return true;
                  }
               }
            }
         }
      } catch (Exception ex) {
         // Do nothing
      } finally {
         if (resultSet != null) {
            try {
               resultSet.close();
            } catch (SQLException ex) {
               // Do nothing
            }
         }
      }
      return false;
   }
}