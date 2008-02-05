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

import java.io.ByteArrayInputStream;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.config.OSEEConfig;
import org.eclipse.osee.framework.ui.plugin.event.ConnectionEvent;
import org.eclipse.osee.framework.ui.plugin.event.CoreEventManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;

/**
 * Handles connection recovery in the event of database connection being lost
 * 
 * @author Jeff C. Phillips
 */
public final class ConnectionHandler {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ConnectionHandler.class);
   private static final CoreEventManager eventManager = CoreEventManager.getInstance();
   private static final int ONE_MINUTE = 1000 * 60;
   private static final OseeProperties properties = OseeProperties.getInstance();
   private static Connection connection = null;

   private static final ThreadLocal<DbTransactionManager> dbTransactionManager =
         new ThreadLocal<DbTransactionManager>() {
            @Override
            protected DbTransactionManager initialValue() {
               DbTransactionManager dbTransactionManager = new DbTransactionManager();

               return dbTransactionManager;
            }
         };

   private static final Queue<Connection> pooledConnections = new LinkedList<Connection>();

   private ConnectionHandler() {
      super();
      throw new UnsupportedOperationException("why would you construct a class that only has static methods");
   }

   public static Connection getConnection() throws SQLException {
      return getConnection(false);
   }

   public static Connection getConnection(boolean overrideTransaction) throws SQLException {
      DbTransactionManager manager = dbTransactionManager.get();
      if (!overrideTransaction && manager.inTransaction()) {
         if (manager.getConnection() == null) manager.setConnection(getPooledConnection());
         return manager.getConnection();
      } else {
         if (connection == null) {
            try {
               connection = getNewConnection();
            } finally {
               eventManager.kick(new ConnectionEvent(null));
            }
         }
         return connection;
      }
   }

   private static Connection getNewConnection() throws SQLException {
      OSEEConfig config = ConfigUtil.getConfigFactory().getOseeConfig();
      if (config.getDefaultClientData() == null) {
         throw new SQLException("Unable to get the default database service.");
      }
      return DBConnection.getNewConnection(config.getDefaultClientData());
   }

   /**
    * returns existing connection, or attempts to acquire a connection on does not exist and reports whether we are
    * connected after this processing
    */
   public static boolean isConnected() {
      try {
         return (getConnection() != null);
      } catch (Exception ex) {
         logger.log(Level.SEVERE, "Unable to get a database connection: " + ex.getLocalizedMessage(), ex);
      }
      return false;
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
    * @throws SQLException
    */
   private static void reGetConnection(boolean overrideTransaction) throws SQLException {
      DbTransactionManager manager = dbTransactionManager.get();
      if (!overrideTransaction && manager.inTransaction()) {
         manager.setConnection(null);
      } else if (connection != null) {
         try {
            connection.close();
         } catch (SQLException ex) {
            logger.log(Level.WARNING, "Unable to close database connection: " + ex.getLocalizedMessage(), ex);
         } finally {
            connection = null;
         }
      }
      getConnection();
   }

   protected static Connection getPooledConnection() throws SQLException {
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

   protected static void repoolConnection(Connection connection) {
      synchronized (pooledConnections) {
         pooledConnections.add(connection);
      }
   }

   public static void close() {
      if (isOpen()) {
         try {
            connection.close();
         } catch (Exception ex) {
            logger.log(Level.WARNING, "Unable to close database connection: " + ex.getLocalizedMessage(), ex);
         }
         connection = null;
      }
   }

   private static void printSql(String... sqls) {
      if (properties.isPrintSqlEnabled()) {
         for (String sql : sqls) {
            System.out.println(Query.replaceBindValues(sql));
         }
      }
   }

   /**
    * @param query - String query
    * @param size - int size of statement fetch size
    * @return ConnectionHandlerStatement contains a Resultset and a Statement
    * @throws SQLException
    */
   @Deprecated
   public static ConnectionHandlerStatement runQuery(String query, int size) throws SQLException {
      printSql(query);
      QueryRecord record = new QueryRecord(query);
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      Statement statement = null;
      ResultSet rset = null;

      // curentTime = System.currentTimeMillis();

      try {
         statement = getConnection().createStatement();
         new StatementCloseWatcher(statement, ONE_MINUTE).start();

         if (size > 0) statement.setFetchSize(size);

         record.markStart();
         rset = statement.executeQuery(query);
         record.markEnd();
         chStmt.setRset(rset);
         chStmt.setStatement(statement);
      } catch (SQLException ex) {
         record.setSqlException(ex);
         logger.log(Level.SEVERE, "Query: *" + query + "*", ex);

         reGetConnection(false);

         statement = getConnection().createStatement();
         new StatementCloseWatcher(statement, ONE_MINUTE).start();
         if (size > 0) statement.setFetchSize(size);

         rset = statement.executeQuery(query);
         chStmt.setRset(rset);
         chStmt.setStatement(statement);
      }
      return chStmt;
   }

   /**
    * @param query - String query
    * @return ConnectionHandlerStatement that contains a Resultset and a Statement
    * @throws SQLException
    */
   @Deprecated
   public static ConnectionHandlerStatement runQuery(String query) throws SQLException {
      return runQuery(query, 0);
   }

   /**
    * all updates require parameters so only a prepared update should be done use runPreparedUpdate() instead.
    * 
    * @param queries
    * @throws SQLException
    */
   @Deprecated
   public static void runUpdate(String... queries) throws SQLException {
      runUpdate(new NullProgressMonitor(), queries);
   }

   // TODO this used to do transactions, check calling methods to ensure they get what they want
   public static void runUpdate(IProgressMonitor monitor, String... queries) throws SQLException {
      printSql(queries);
      Statement statement = null;

      // curentTime = System.currentTimeMillis();
      QueryRecord record = null;

      try {
         statement = getConnection().createStatement();
         new StatementCloseWatcher(statement, ONE_MINUTE).start();

         for (int x = 0; x < queries.length; x++) {
            monitor.subTask("Processing SQL set " + (x + 1) + "/" + queries.length);
            record = new QueryRecord(queries[x]);
            record.markStart();
            statement.execute(queries[x]);
            record.markEnd();
            monitor.worked(1);
         }
      } catch (BatchUpdateException ex) {
         record.setSqlException(ex);
         logger.log(Level.SEVERE, "sql batch update failed: \n" + record.getSql(), ex);
         reGetConnection(false);
         throw ex;
      } catch (SQLException ex) {
         record.setSqlException(ex);
         logger.log(Level.SEVERE, "sql update failed: \n" + record.getSql(), ex);
         reGetConnection(false);
         throw ex;
      } finally {
         if (statement != null) statement.close();
      }
   }

   public static void runPreparedUpdate(boolean overrideTransaction, String query, Object... data) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = runPreparedUpdateReturnStmt(overrideTransaction, query, data);
      } finally {
         DbUtil.close(chStmt);
      }
   }

   public static void runPreparedUpdate(String query, Object... data) throws SQLException {
      runPreparedUpdate(false, query, data);
   }

   public static int runPreparedUpdateReturnCount(String query, Object... data) throws SQLException {
      ConnectionHandlerStatement chStmt = runPreparedUpdateReturnStmt(false, query, data);
      int updateCount = chStmt.getStatement().getUpdateCount();
      DbUtil.close(chStmt);
      return updateCount;
   }

   public static ConnectionHandlerStatement runPreparedUpdateReturnStmt(String query, Object... data) throws SQLException {
      return runPreparedUpdateReturnStmt(false, query, data);
   }

   public static ConnectionHandlerStatement runPreparedUpdateReturnStmt(boolean overrideTransaction, String query, Object... data) throws SQLException {
      printSql(query);
      QueryRecord record = new QueryRecord(query, data);
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         PreparedStatement preparedStatement = getConnection(overrideTransaction).prepareStatement(query);
         new StatementCloseWatcher(preparedStatement, ONE_MINUTE).start();
         populateValuesForPreparedStatement(preparedStatement, data);

         record.markStart();
         preparedStatement.executeUpdate();
         record.markEnd();
         chStmt.setStatement(preparedStatement);
      } catch (SQLException ex) {
         record.setSqlException(ex);
         logger.log(Level.SEVERE, "sql update failed: " + query, ex);
         logger.log(Level.SEVERE, Arrays.deepToString(data));
         reGetConnection(overrideTransaction);
         throw ex;
      }
      return chStmt;
   }

   public static ConnectionHandlerStatement runPreparedQuery(String query, Object... data) throws SQLException {
      return runPreparedQuery(0, false, query, data);
   }

   public static ConnectionHandlerStatement runPreparedQuery(boolean overrideTranaction, String query, Object... data) throws SQLException {
      return runPreparedQuery(0, overrideTranaction, query, data);
   }

   public static ConnectionHandlerStatement runPreparedQuery(int fetchSize, String query, Object... data) throws SQLException {
      return runPreparedQuery(fetchSize, false, query, data);
   }

   public static ConnectionHandlerStatement runPreparedQuery(int fetchSize, boolean overrideTranaction, String query, Object... data) throws SQLException {
      printSql(query);
      QueryRecord record = new QueryRecord(query, data);
      PreparedStatement preparedStatement = null;

      try {
         preparedStatement = getConnection(overrideTranaction).prepareStatement(query);
         preparedStatement.setFetchSize(fetchSize);
         populateValuesForPreparedStatement(preparedStatement, data);
         new StatementCloseWatcher(preparedStatement, ONE_MINUTE).start();

         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         record.markStart();
         chStmt.setRset(preparedStatement.executeQuery());
         record.markEnd();
         chStmt.setStatement(preparedStatement);
         return chStmt;
      } catch (SQLException ex) {
         record.setSqlException(ex);
         if (preparedStatement != null) {
            preparedStatement.close();
         }
         reGetConnection(overrideTranaction);
         throw ex;
      }
   }

   private static void populateValuesForPreparedStatement(PreparedStatement preparedStatement, Object... data) throws SQLException {

      for (int i = 0, preparedIndex = 1; i < data.length; i += 2, preparedIndex++) {

         if (!(data[i] instanceof SQL3DataType)) {
            throw new IllegalArgumentException("Invalid argument type");
         }

         SQL3DataType sqlDataType = (SQL3DataType) data[i];
         Object dataValue = data[i + 1];

         if (dataValue instanceof String) {
            int length = ((String) dataValue).length();
            if (length > 4000) {
               logger.log(Level.WARNING, "SQL value too long:" + length + "\nValue: " + dataValue);
               dataValue = ((String) dataValue).substring(0, 3999);
            }
         }

         if (dataValue == null) {
            if (sqlDataType == SQL3DataType.BLOB) {
               // for PostgreSql, setNull for BLOB with the new JDBC driver gives the error "column
               // "content" is of type bytea but expression is of type oid"
               preparedStatement.setBytes(preparedIndex, null);
            } else {
               preparedStatement.setNull(preparedIndex, sqlDataType.getSQLTypeNumber());
            }

         } else if (dataValue instanceof ByteArrayInputStream) {
            preparedStatement.setBinaryStream(preparedIndex, (ByteArrayInputStream) dataValue,
                  ((ByteArrayInputStream) dataValue).available());
         } else {
            preparedStatement.setObject(preparedIndex, dataValue);
         }
      }
   }

   // ************* WARNING-BATCHING HAD TO BE REMOVED BECAUSE IT IS FREEZING ON LARGE BLOB OBJECTS
   // **************
   // ************************************************************************************************************

   // TODO this used to do transactions, check calling methods to ensure they get what they want
   public static void runPreparedUpdate(String query, List<Object[]> datas) throws SQLException {
      runBatchablePreparedUpdate(query, false, datas);
   }

   public static void runBatchablePreparedUpdate(String query, boolean useBatching, List<Object[]> datas) throws SQLException {
      printSql(query);
      QueryRecord record =
            new QueryRecord("<batchable: " + (useBatching ? "" : "not ") + "batched> " + query, SQL3DataType.INTEGER,
                  datas.size());

      if (datas.size() < 1) {
         throw new IllegalArgumentException(
               "The datas list must have at least one element otherwise no sql statements will be run.");
      }

      PreparedStatement preparedStatement = getConnection().prepareStatement(query);
      new StatementCloseWatcher(preparedStatement, ONE_MINUTE).start();

      try {
         record.markStart();
         if (useBatching) {
            boolean needExecute = false;
            int count = 0;
            for (Object[] data : datas) {
               populateValuesForPreparedStatement(preparedStatement, data);
               preparedStatement.addBatch();
               needExecute = true;
               if (count > 20000) {
                  preparedStatement.executeBatch();
                  count = 0;
                  needExecute = false;
               }
            }
            if (needExecute) {
               preparedStatement.executeBatch();
            }
         } else {
            for (Object[] data : datas) {
               populateValuesForPreparedStatement(preparedStatement, data);
               preparedStatement.execute();
            }
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
         logger.log(Level.SEVERE, "sql update failed: \n" + query + "\n" + details + "\n", ex);

         reGetConnection(false);
         throw ex;
      } finally {
         preparedStatement.close();
      }
   }

   public static void startTransactionLevel(Object key) throws SQLException {
      dbTransactionManager.get().startTransactionLevel(key);
   }

   public static void setTransactionLevelAsSuccessful(Object key) {
      dbTransactionManager.get().setTransactionLevelSuccess(key);
   }

   public static void endTransactionLevel(Object key) {
      try {
         dbTransactionManager.get().endTransactionLevel(key);
      } catch (IllegalArgumentException ex) {
         // If the terminate had to be done because the endBatchLevel died in onLastExit with
         // SQLException
         // then this will occur
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (IllegalStateException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
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

   // public static void executeTransaction(ISQLRunnable runnable) {
   // executeTransaction(runnable, new NullProgressMonitor());
   // }
   //      
   // /**
   // * Since this transaction relies on the available connection, it is not possible for one JVM to
   // process
   // * more than one database transaction at a time. For this reason it is crucial that this method
   // remain
   // * synchronized. This implies that the system can do concurrent reading, but not concurrent
   // writing.
   // */
   // public static synchronized void executeTransaction(ISQLRunnable runnable, IProgressMonitor
   // monitor) {
   // boolean autoCommit = true;
   // Connection connection = null;
   //      
   // // This will keep track of whether or not this is an initial entry or called from within the
   // runnable.run()
   // boolean initialEntry = transactionThread == null;
   //      
   // try {
   // transactionThread = Thread.currentThread();
   //         
   // connection = ConnectionHandler.getConnection();
   // autoCommit = connection.getAutoCommit();
   //         
   // connection.setAutoCommit(false);
   //         
   // boolean success = runnable.run(monitor);
   //            
   // if (initialEntry) {
   // if (success && !lowerLevelRollbackRequest) {
   // connection.commit();
   // } else {
   // connection.rollback();
   // }
   // } else if (!success) {
   // lowerLevelRollbackRequest = true;
   // }
   // }
   // catch (SQLException ex) {
   // logger.log(Level.SEVERE, ex.toString(), ex);
   // try {
   // if (initialEntry && connection != null)
   // connection.rollback();
   // else
   // lowerLevelRollbackRequest = true;
   // }
   // catch (SQLException ex1) {
   // logger.log(Level.SEVERE, ex1.toString(), ex1);
   // }
   // }
   // finally {
   // if (initialEntry)
   // transactionThread = null;
   // try {
   // if (connection != null)
   // connection.setAutoCommit(autoCommit);
   // }
   // catch (SQLException ex) {
   // logger.log(Level.SEVERE, ex.toString(), ex);
   // }
   // }
   // }
}