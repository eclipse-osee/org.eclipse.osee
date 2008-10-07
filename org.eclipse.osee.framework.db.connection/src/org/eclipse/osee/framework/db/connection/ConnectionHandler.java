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
import java.util.Arrays;
import java.util.Collection;
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

   private static Connection connection = null;
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

   public static Connection getConnection() throws OseeDataStoreException {
      return getConnection(false);
   }

   private static Connection getConnection(boolean overrideTransaction) throws OseeDataStoreException {
      DbTransactionManager manager = dbTransactionManager.get();
      if (!overrideTransaction && manager.inTransaction()) {
         if (manager.getConnection() == null) manager.setConnection(getPooledConnection());
         return manager.getConnection();
      } else {
         try {
            if (connection == null || connection.isClosed()) {
               try {
                  connection = getNewConnection();
               } finally {
                  notifyConnectionListeners();
               }
            }
         } catch (SQLException ex) {
            throw new OseeDataStoreException(ex);
         }
         return connection;
      }
   }

   private static void notifyConnectionListeners() {
      for (IDbConnectionListener listener : listeners) {
         listener.onConnectionStatusUpdate(isOpen());
      }
   }

   private static Connection getNewConnection() throws OseeDataStoreException {
      if (OseeDb.getDefaultDatabaseService() == null) {
         throw new OseeDataStoreException("Unable to get the default database service.");
      }
      return DBConnection.getNewConnection(OseeDb.getDefaultDatabaseService());
   }

   /**
    * returns existing connection, or attempts to acquire a connection on does not exist and reports whether we are
    * connected after this processing
    * 
    * @throws OseeDataStoreException
    */
   public static boolean isConnected() throws OseeDataStoreException {
      return (getConnection() != null);
   }

   /**
    * purely a check (does not attempt to acquire a connection)
    */
   public static boolean isOpen() {
      return connection != null;
   }

   /**
    * Forces a new connection
    * 
    * @throws OseeDataStoreException
    */
   private static void reGetConnection(boolean overrideTransaction) throws OseeDataStoreException {
      DbTransactionManager manager = dbTransactionManager.get();
      if (!overrideTransaction && manager.inTransaction()) {
         manager.setConnection(null);
      } else if (connection != null) {
         try {
            connection.close();
         } catch (SQLException ex) {
            OseeLog.log(Activator.class, Level.WARNING,
                  "Unable to close database connection: " + ex.getLocalizedMessage(), ex);
         } finally {
            connection = null;
         }
      }
      getConnection();
   }

   public static Connection getPooledConnection() throws OseeDataStoreException {
      Connection pooledConnection;
      synchronized (pooledConnections) {
         if (pooledConnections.isEmpty()) {
            pooledConnection = getNewConnection();
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
            OseeLog.log(Activator.class, Level.WARNING,
                  "Unable to close database statement: " + ex.getLocalizedMessage(), ex);
         }
      }
   }

   public static void close(ConnectionHandlerStatement chStmt) {
      if (chStmt != null) {
         chStmt.close();
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

   public static void close() {
      close(connection);
      connection = null;
   }

   public static void close(Connection connection, ConnectionHandlerStatement chStmt) {
      close(chStmt);
      close(connection);
   }

   public static int runPreparedUpdate(boolean overrideTransaction, String query, Object... data) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = runPreparedUpdateReturnStmt(overrideTransaction, query, data);
      } finally {
         ConnectionHandler.close(chStmt);
      }
      if (chStmt != null) {
         return chStmt.getUpdates();
      }
      return 0;
   }

   public static int runPreparedUpdate(String query, Object... data) throws OseeDataStoreException {
      return runPreparedUpdate(false, query, data);
   }

   public static int runPreparedUpdateReturnCount(String query, Object... data) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = null;
      int updateCount;
      try {
         chStmt = runPreparedUpdateReturnStmt(false, query, data);
         updateCount = chStmt.getStatement().getUpdateCount();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      } finally {
         ConnectionHandler.close(chStmt);
      }

      return updateCount;
   }

   public static ConnectionHandlerStatement runPreparedUpdateReturnStmt(boolean overrideTransaction, String query, Object... data) throws OseeDataStoreException {
      QueryRecord record = new QueryRecord(query, data);
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         PreparedStatement preparedStatement = getConnection(overrideTransaction).prepareStatement(query);
         chStmt.setStatement(preparedStatement);

         populateValuesForPreparedStatement(preparedStatement, data);

         record.markStart();
         int count = preparedStatement.executeUpdate();
         chStmt.setUpdates(count);
         record.markEnd();
      } catch (SQLException ex) {
         record.setSqlException(ex);
         OseeLog.log(Activator.class, Level.SEVERE, "update failed: " + query + "\n" + Arrays.deepToString(data), ex);
         reGetConnection(overrideTransaction);
         throw new OseeDataStoreException(ex);
      }
      return chStmt;
   }

   public static int runPreparedUpdate(Connection connection, String query, Object... data) throws OseeDataStoreException {
      PreparedStatement preparedStatement = null;
      int returnValue = 0;
      try {
         preparedStatement = connection.prepareStatement(query);
         populateValuesForPreparedStatement(preparedStatement, data);
         returnValue = preparedStatement.executeUpdate();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      } finally {
         close(preparedStatement);
      }
      return returnValue;
   }

   public static int runPreparedQueryFetchInt(int defaultValue, String query, Object... data) throws OseeDataStoreException {
      return runPreparedQueryFetchInt(OseeDbConnection.getConnection(), defaultValue, query, data);
   }

   public static int runPreparedQueryFetchInt(Connection connection, int defaultValue, String query, Object... data) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(connection, 1, query, data);
         if (chStmt.next()) {
            return chStmt.getInt(1);
         }
         return defaultValue;
      } finally {
         ConnectionHandler.close(chStmt);
      }
   }

   public static String runPreparedQueryFetchString(String defaultValue, String query, Object... data) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(1, false, query, data);
         if (chStmt.next()) {
            return chStmt.getString(1);
         }
         return defaultValue;
      } finally {
         ConnectionHandler.close(chStmt);
      }
   }

   public static ConnectionHandlerStatement runPreparedQuery(String query, Object... data) throws OseeDataStoreException {
      return runPreparedQuery(0, false, query, data);
   }

   public static ConnectionHandlerStatement runPreparedQuery(boolean overrideTranaction, String query, Object... data) throws OseeDataStoreException {
      return runPreparedQuery(0, overrideTranaction, query, data);
   }

   public static ConnectionHandlerStatement runPreparedQuery(int fetchSize, String query, Object... data) throws OseeDataStoreException {
      return runPreparedQuery(fetchSize, false, query, data);
   }

   public static ConnectionHandlerStatement runPreparedQuery(Connection connection, String query, Object... data) throws OseeDataStoreException {
      return runPreparedQuery(connection, 0, query, data);
   }

   public static ConnectionHandlerStatement runPreparedQuery(Connection connection, int fetchSize, String query, Object... data) throws OseeDataStoreException {
      QueryRecord record = new QueryRecord(query, data);
      PreparedStatement preparedStatement = null;

      try {
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         preparedStatement = connection.prepareStatement(query);
         chStmt.setStatement(preparedStatement);

         preparedStatement.setFetchSize(Math.min(fetchSize, 10000));
         populateValuesForPreparedStatement(preparedStatement, data);

         record.markStart();
         chStmt.setRset(preparedStatement.executeQuery());
         record.markEnd();
         return chStmt;
      } catch (SQLException ex) {
         record.setSqlException(ex);
         close(preparedStatement);
         throw new OseeDataStoreException(ex);
      }
   }

   /**
    * @param fetchSize hint as to the number of rows that should be fetched from the database at a time. will be limited
    *           to 10,000
    * @param overrideTranaction
    * @param query
    * @param data
    * @return
    * @throws OseeDataStoreException
    */
   private static ConnectionHandlerStatement runPreparedQuery(int fetchSize, boolean overrideTranaction, String query, Object... data) throws OseeDataStoreException {
      QueryRecord record = new QueryRecord(query, data);
      PreparedStatement preparedStatement = null;

      try {
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         preparedStatement = getConnection(overrideTranaction).prepareStatement(query);
         chStmt.setStatement(preparedStatement);

         preparedStatement.setFetchSize(Math.min(fetchSize, 10000));
         populateValuesForPreparedStatement(preparedStatement, data);

         record.markStart();
         chStmt.setRset(preparedStatement.executeQuery());
         record.markEnd();
         return chStmt;
      } catch (SQLException ex) {
         record.setSqlException(ex);
         close(preparedStatement);
         reGetConnection(overrideTranaction);
         throw new OseeDataStoreException(ex);
      }
   }

   public static int runPreparedUpdate(Connection connection, String query, List<Object[]> datas) throws OseeDataStoreException {
      QueryRecord record = new QueryRecord("<batchable: batched> " + query, datas.size());
      int returnCount = 0;
      PreparedStatement preparedStatement = null;
      try {
         preparedStatement = connection.prepareStatement(query);
         record.markStart();
         boolean needExecute = false;
         int count = 0;
         for (Object[] data : datas) {
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
         StringBuilder details = new StringBuilder(datas.size() * datas.get(0).length * 20);
         details.append("[ DATA OBJECT: \n");
         for (Object[] data : datas) {
            for (int i = 0; i < data.length; i++) {
               details.append(i);
               details.append(": ");
               Object dataValue = data[i];
               if (dataValue != null) {
                  details.append(dataValue.getClass().getName());
                  details.append(":");

                  String value = dataValue.toString();
                  if (value.length() > 50) {
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

   private static int processBatchUpdateResults(int[] updates) {
      int returnCount = 0;
      for (int update : updates) {
         if (update >= 0) {
            returnCount += update;
         } else if (Statement.EXECUTE_FAILED == update) {
            OseeLog.log(Activator.class, Level.SEVERE, "sql execute failes.");
         } else if (Statement.SUCCESS_NO_INFO == update) {
            returnCount++;
         }
      }
      return returnCount;
   }

   private static void populateValuesForPreparedStatement(PreparedStatement preparedStatement, Object... data) throws OseeDataStoreException {
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

   public static int runPreparedUpdateBatch(String query, Collection<Object[]> datas) throws OseeDataStoreException {
      QueryRecord record = new QueryRecord("<batched> " + query, datas.size());
      int returnCount = 0;
      if (datas.size() < 1) {
         throw new IllegalArgumentException(
               "The datas list must have at least one element otherwise no sql statements will be run.");
      }

      PreparedStatement preparedStatement = null;
      try {
         preparedStatement = getConnection().prepareStatement(query);
         record.markStart();
         boolean needExecute = false;
         int count = 0;
         for (Object[] data : datas) {
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
         StringBuilder details = new StringBuilder(datas.size() * datas.iterator().next().length * 20);
         details.append("[ DATA OBJECT: \n");
         for (Object[] data : datas) {
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

         reGetConnection(false);
         throw new OseeDataStoreException(ex);
      } finally {
         close(preparedStatement);
      }
      return returnCount;
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
      } catch (Exception ex) {
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