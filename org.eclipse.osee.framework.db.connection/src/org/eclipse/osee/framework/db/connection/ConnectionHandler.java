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

package org.eclipse.osee.framework.db.connection;

import java.io.ByteArrayInputStream;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.core.query.QueryRecord;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.db.connection.internal.InternalActivator;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Handles connection recovery in the event of database connection being lost
 * 
 * @author Jeff C. Phillips
 */
public final class ConnectionHandler {

   private static void close(PreparedStatement stmt) {
      if (stmt != null) {
         try {
            stmt.close();
         } catch (SQLException ex) {
            OseeLog.log(InternalActivator.class, Level.WARNING, "Unable to close database statement: ", ex);
         }
      }
   }

   /**
    * This method should only be used when not contained in a DB transaction
    * 
    * @param query
    * @param data
    * @return number of records updated
    * @throws OseeDataStoreException
    */
   public static int runPreparedUpdate(String query, Object... data) throws OseeDataStoreException {
      OseeConnection connection = OseeDbConnection.getConnection();
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
   public static int runBatchUpdate(String query, List<Object[]> dataList) throws OseeDataStoreException {
      OseeConnection connection = OseeDbConnection.getConnection();
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
   public static int runPreparedUpdate(OseeConnection connection, String query, Object... data) throws OseeDataStoreException {
      if (connection == null) {
         return runPreparedUpdate(query, data);
      }
      PreparedStatement preparedStatement = null;
      int updateCount = 0;
      try {
         preparedStatement = connection.prepareStatement(query);
         populateValuesForPreparedStatement(preparedStatement, data);
         updateCount = preparedStatement.executeUpdate();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      } finally {
         close(preparedStatement);
      }
      return updateCount;
   }

   public static int runBatchUpdate(OseeConnection connection, String query, List<Object[]> dataList) throws OseeDataStoreException {
      if (connection == null) {
         return runBatchUpdate(query, dataList);
      }

      QueryRecord record = new QueryRecord("<batchable: batched> " + query, dataList.size());
      int returnCount = 0;
      PreparedStatement preparedStatement = null;
      try {
         preparedStatement = connection.prepareStatement(query);
         record.markStart();
         boolean needExecute = false;
         int count = 0;
         for (Object[] data : dataList) {
            count++;
            populateValuesForPreparedStatement(preparedStatement, data);
            preparedStatement.addBatch();
            preparedStatement.clearParameters();
            needExecute = true;
            if (count > 2000) {
               int[] updates = preparedStatement.executeBatch();
               returnCount += processBatchUpdateResults(updates);
               count = 0;
               needExecute = false;
            }
         }
         if (needExecute) {
            int[] updates = preparedStatement.executeBatch();
            returnCount += processBatchUpdateResults(updates);
         }

         record.markEnd();
      } catch (SQLException ex) {
         record.setSqlException(ex);
         SQLException exlist;
         if ((exlist = ex.getNextException()) != null) {
            OseeLog.log(InternalActivator.class, Level.SEVERE, "This is the nested exception", exlist);
         }
         StringBuilder details = new StringBuilder(dataList.size() * dataList.get(0).length * 20);
         details.append("[ DATA OBJECT: \n");
         for (Object[] data : dataList) {
            for (int i = 0; i < data.length; i++) {
               details.append(i);
               details.append(": ");
               Object dataValue = data[i];
               if (dataValue != null) {
                  details.append(dataValue.getClass().getName());
                  details.append(":");

                  String value = dataValue.toString();
                  if (value.length() > 35) {
                     details.append(value.substring(0, 35));
                  } else {
                     details.append(value);
                  }
                  details.append("\n");
               } else {
                  details.append("NULL\n");
               }
            }
         }
         details.append("]\n");
         throw new OseeDataStoreException("sql update failed: \n" + query + "\n" + details, ex);
      } finally {
         close(preparedStatement);
      }
      return returnCount;
   }

   public static int runPreparedQueryFetchInt(int defaultValue, String query, Object... data) throws OseeDataStoreException {
      return runPreparedQueryFetchInt(new ConnectionHandlerStatement(), defaultValue, query, data);
   }

   public static int runPreparedQueryFetchInt(OseeConnection connection, int defaultValue, String query, Object... data) throws OseeDataStoreException {
      return runPreparedQueryFetchInt(new ConnectionHandlerStatement(connection), defaultValue, query, data);
   }

   private static int runPreparedQueryFetchInt(ConnectionHandlerStatement chStmt, int defaultValue, String query, Object... data) throws OseeDataStoreException {
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

   public static int runCallableStatementFetchInt(String query, Object... data) throws OseeDataStoreException {
      return runCallableStatementFetchInt(new ConnectionHandlerStatement(), query, data);
   }

   public static int runCallableStatementFetchInt(OseeConnection connection, String query, Object... data) throws OseeDataStoreException {
      return runCallableStatementFetchInt(new ConnectionHandlerStatement(connection), query, data);
   }

   public static int runCallableStatementFetchInt(ConnectionHandlerStatement chStmt, String query, Object... data) throws OseeDataStoreException {
      try {
         chStmt.runCallableStatement(query, data);
         return chStmt.getCallableInt(1);
      } finally {
         chStmt.close();
      }
   }

   public static double runCallableStatementFetchDouble(String query, Object... data) throws OseeDataStoreException {
      return runCallableStatementFetchDouble(new ConnectionHandlerStatement(), query, data);
   }

   public static double runCallableStatementFetchDouble(OseeConnection connection, String query, Object... data) throws OseeDataStoreException {
      return runCallableStatementFetchDouble(new ConnectionHandlerStatement(connection), query, data);
   }

   private static double runCallableStatementFetchDouble(ConnectionHandlerStatement chStmt, String query, Object... data) throws OseeDataStoreException {
      try {
         chStmt.runCallableStatement(query, data);
         return chStmt.getCallableDouble(1);
      } finally {
         chStmt.close();
      }
   }

   public static long runPreparedQueryFetchLong(long defaultValue, String query, Object... data) throws OseeDataStoreException {
      return runPreparedQueryFetchLong(new ConnectionHandlerStatement(), defaultValue, query, data);
   }

   public static long runPreparedQueryFetchLong(OseeConnection connection, long defaultValue, String query, Object... data) throws OseeDataStoreException {
      return runPreparedQueryFetchLong(new ConnectionHandlerStatement(connection), defaultValue, query, data);
   }

   private static long runPreparedQueryFetchLong(ConnectionHandlerStatement chStmt, long defaultValue, String query, Object... data) throws OseeDataStoreException {
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

   public static String runPreparedQueryFetchString(String defaultValue, String query, Object... data) throws OseeDataStoreException {
      return runPreparedQueryFetchString(new ConnectionHandlerStatement(), defaultValue, query, data);
   }

   public static String runPreparedQueryFetchString(OseeConnection connection, String defaultValue, String query, Object... data) throws OseeDataStoreException {
      return runPreparedQueryFetchString(new ConnectionHandlerStatement(connection), defaultValue, query, data);
   }

   private static String runPreparedQueryFetchString(ConnectionHandlerStatement chStmt, String defaultValue, String query, Object... data) throws OseeDataStoreException {
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

   private static int processBatchUpdateResults(int[] updates) {
      int returnCount = 0;
      for (int update : updates) {
         if (update >= 0) {
            returnCount += update;
         } else if (Statement.EXECUTE_FAILED == update) {
            OseeLog.log(InternalActivator.class, Level.SEVERE, "sql execute failed.");
         } else if (Statement.SUCCESS_NO_INFO == update) {
            returnCount++;
         }
      }
      return returnCount;
   }

   static void populateValuesForPreparedStatement(PreparedStatement preparedStatement, Object... data) throws OseeDataStoreException {
      try {
         int preparedIndex = 0;
         for (Object dataValue : data) {
            preparedIndex++;
            if (dataValue instanceof String) {
               int length = ((String) dataValue).length();
               if (length > 4000) {
                  throw new OseeDataStoreException(
                        "SQL data value length must be  <= 4000 not " + length + "\nValue: " + dataValue);
               }
            }

            if (dataValue == null) {
               throw new OseeDataStoreException(
                     "instead of passing null for an query parameter, pass the corresponding SQL3DataType");
            } else if (dataValue instanceof SQL3DataType) {
               int dataTypeNumber = ((SQL3DataType) dataValue).getSQLTypeNumber();
               if (dataTypeNumber == java.sql.Types.BLOB) {
                  // TODO Need to check this - for PostgreSql, setNull for BLOB with the new JDBC driver gives the error "column
                  //  "content" is of type bytea but expression is of type oid"
                  preparedStatement.setBytes(preparedIndex, null);
               } else {
                  preparedStatement.setNull(preparedIndex, dataTypeNumber);
               }
            } else if (dataValue instanceof ByteArrayInputStream) {
               preparedStatement.setBinaryStream(preparedIndex, (ByteArrayInputStream) dataValue,
                     ((ByteArrayInputStream) dataValue).available());
            } else {
               preparedStatement.setObject(preparedIndex, dataValue);
            }
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   /**
    * Cause constraint checking to be deferred until the end of the current transaction.
    * 
    * @param connection
    * @throws OseeDataStoreException
    */
   public static void deferConstraintChecking(OseeConnection connection) throws OseeDataStoreException {
      if (SupportedDatabase.getDatabaseType(connection) == SupportedDatabase.derby) {
         return;
      }
      // NOTE: this must be a PreparedStatement to play correctly with DB Transactions.
      runPreparedUpdate(connection, "SET CONSTRAINTS ALL DEFERRED");
   }

   public static DatabaseMetaData getMetaData() throws OseeDataStoreException {
      OseeConnection connection = OseeDbConnection.getConnection();
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