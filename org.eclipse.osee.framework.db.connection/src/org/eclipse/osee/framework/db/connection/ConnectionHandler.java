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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.core.query.QueryRecord;
import org.eclipse.osee.framework.db.connection.core.transaction.DbTransactionManager;
import org.eclipse.osee.framework.db.connection.core.transaction.IDbTransactionListener;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Handles connection recovery in the event of database connection being lost
 * 
 * @author Jeff C. Phillips
 */
public final class ConnectionHandler {

   private static List<IDbConnectionListener> listeners = new CopyOnWriteArrayList<IDbConnectionListener>();

   private static final ThreadLocal<DbTransactionManager> dbTransactionManager =
         new ThreadLocal<DbTransactionManager>() {
            @Override
            protected DbTransactionManager initialValue() {
               DbTransactionManager dbTransactionManager = new DbTransactionManager();

               return dbTransactionManager;
            }
         };

   private static final Queue<Connection> pooledConnections = new LinkedList<Connection>();

   public static void addListener(IDbConnectionListener listener) {
      listeners.add(listener);
   }

   public static void removeListener(IDbConnectionListener listener) {
      listeners.remove(listener);
   }

   private static void notifyConnectionListeners() {
      for (IDbConnectionListener listener : listeners) {
         listener.onConnectionStatusUpdate(OseeDbConnection.hasOpenConnection());
      }
   }

   public static Connection getPooledConnection() throws OseeDataStoreException {
      Connection pooledConnection;
      synchronized (pooledConnections) {
         if (pooledConnections.isEmpty()) {
            pooledConnection = OseeDbConnection.getConnection();
         } else {
            pooledConnection = pooledConnections.poll();
         }
      }

      return pooledConnection;
   }

   public static void repoolConnection(Connection connection) {
      synchronized (pooledConnections) {
         pooledConnections.add(connection);
      }
   }

   private static void close(PreparedStatement stmt) {
      if (stmt != null) {
         try {
            stmt.close();
         } catch (SQLException ex) {
            OseeLog.log(Activator.class, Level.WARNING, "Unable to close database statement: ", ex);
         }
      }
   }

   public static void close(Connection connection) {
      if (connection != null) {
         try {
            connection.close();
         } catch (SQLException ex) {
            OseeLog.log(Activator.class, Level.WARNING,
                  "Unable to close database connection: " + ex.getLocalizedMessage(), ex);
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
   public static int runPreparedUpdate(Connection connection, String query, Object... data) throws OseeDataStoreException {
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

   public static int runBatchUpdate(Connection connection, String query, List<Object[]> dataList) throws OseeDataStoreException {
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
            System.out.println("this is the next exception");
            exlist.printStackTrace();
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
         OseeLog.log(Activator.class, Level.SEVERE, "sql update failed: \n" + query + "\n" + details + "\n", ex);
         throw new OseeDataStoreException(ex);
      } finally {
         close(preparedStatement);
      }
      return returnCount;
   }

   public static int runPreparedQueryFetchInt(int defaultValue, String query, Object... data) throws OseeDataStoreException {
      return runPreparedQueryFetchInt(new ConnectionHandlerStatement(), defaultValue, query, data);
   }

   public static int runPreparedQueryFetchInt(Connection connection, int defaultValue, String query, Object... data) throws OseeDataStoreException {
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

   public static long runPreparedQueryFetchLong(long defaultValue, String query, Object... data) throws OseeDataStoreException {
      return runPreparedQueryFetchLong(new ConnectionHandlerStatement(), defaultValue, query, data);
   }

   public static long runPreparedQueryFetchLong(Connection connection, long defaultValue, String query, Object... data) throws OseeDataStoreException {
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

   public static String runPreparedQueryFetchString(Connection connection, String defaultValue, String query, Object... data) throws OseeDataStoreException {
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
            OseeLog.log(Activator.class, Level.SEVERE, "sql execute failed.");
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
                  OseeLog.log(Activator.class, Level.WARNING, "SQL value too long:" + length + "\nValue: " + dataValue);
                  dataValue = ((String) dataValue).substring(0, 3999);
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

   public static void startTransactionLevel(Object key) throws OseeDataStoreException {
      dbTransactionManager.get().startTransactionLevel(key);
   }

   public static void setTransactionLevelAsSuccessful(Object key) {
      dbTransactionManager.get().setTransactionLevelSuccess(key);
   }

   public static void endTransactionLevel(Object key) {
      try {
         dbTransactionManager.get().endTransactionLevel(key);
      } catch (OseeDataStoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public static void requestRollback() {
      dbTransactionManager.get().requestRollback();
   }

   public static void addDbTransactionListener(IDbTransactionListener listener) {
      dbTransactionManager.get().addListener(listener);
   }

   public static boolean removeDbTransactionListener(IDbTransactionListener listener) {
      return dbTransactionManager.get().removeListener(listener);
   }
}