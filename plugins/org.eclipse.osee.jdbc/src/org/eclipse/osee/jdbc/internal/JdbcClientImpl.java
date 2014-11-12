/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jdbc.internal;

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_VARCHAR_LENGTH;
import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcClientConfig;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcException;
import org.eclipse.osee.jdbc.JdbcProcessor;
import org.eclipse.osee.jdbc.JdbcSchemaOptions;
import org.eclipse.osee.jdbc.JdbcSchemaResource;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.osee.jdbc.internal.schema.data.SchemaData;
import org.eclipse.osee.jdbc.internal.schema.ops.CreateSchemaTx;
import org.eclipse.osee.jdbc.internal.schema.ops.ExtractSchemaCallable;
import org.eclipse.osee.jdbc.internal.schema.ops.LoadUserSchemasCallable;

/**
 * @author Roberto E. Escobar
 */
public final class JdbcClientImpl implements JdbcClient {

   private final JdbcClientConfig config;
   private final JdbcConnectionProvider connectionProvider;
   private final JdbcSequenceProvider sequenceProvider;
   private final JdbcConnectionInfo dbInfo;

   private volatile JdbcDbType dbType;

   public JdbcClientImpl(JdbcClientConfig config, JdbcConnectionProvider connectionProvider, JdbcSequenceProvider sequenceProvider, JdbcConnectionInfo dbInfo) {
      super();
      this.config = config;
      this.connectionProvider = connectionProvider;
      this.sequenceProvider = sequenceProvider;
      this.dbInfo = dbInfo;
   }

   @Override
   public JdbcClientConfig getConfig() {
      return config;
   }

   @Override
   public JdbcDbType getDatabaseType() {
      if (dbType == null) {
         JdbcConnectionImpl connection = getConnection();
         try {
            DatabaseMetaData metaData = connection.getMetaData();
            dbType = JdbcDbType.getDatabaseType(metaData);
         } finally {
            connection.close();
         }
      }
      return dbType;
   }

   @Override
   public JdbcConnectionImpl getConnection() throws JdbcException {
      return connectionProvider.getConnection(dbInfo);
   }

   @Override
   public JdbcStatement getStatement() {
      return new JdbcStatementImpl(dbInfo, connectionProvider);
   }

   @Override
   public JdbcStatement getStatement(JdbcConnection connection) {
      return new JdbcStatementImpl(dbInfo, connectionProvider, (JdbcConnectionImpl) connection);
   }

   @Override
   public JdbcStatement getStatement(JdbcConnection connection, boolean autoClose) {
      return new JdbcStatementImpl(dbInfo, connectionProvider, (JdbcConnectionImpl) connection, autoClose);
   }

   @Override
   public JdbcStatement getStatement(int resultSetType, int resultSetConcurrency) {
      return new JdbcStatementImpl(dbInfo, connectionProvider, resultSetType, resultSetConcurrency);
   }

   @Override
   public int runPreparedUpdate(JdbcConnection connection, String query, Object... data) throws JdbcException {
      if (connection == null) {
         return runPreparedUpdate(query, data);
      }
      PreparedStatement preparedStatement = null;
      int updateCount = 0;
      try {
         preparedStatement = ((JdbcConnectionImpl) connection).prepareStatement(query);
         JdbcUtil.populateValuesForPreparedStatement(preparedStatement, data);
         updateCount = preparedStatement.executeUpdate();
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      } finally {
         close(preparedStatement);
      }
      return updateCount;
   }

   @Override
   public int runBatchUpdate(JdbcConnection connection, String query, Iterable<Object[]> dataList) throws JdbcException {
      if (connection == null) {
         return runBatchUpdate(query, dataList);
      }
      int returnCount = 0;
      PreparedStatement preparedStatement = null;
      try {
         preparedStatement = ((JdbcConnectionImpl) connection).prepareStatement(query);
         boolean needExecute = false;
         int count = 0;
         for (Object[] data : dataList) {
            count++;
            JdbcUtil.populateValuesForPreparedStatement(preparedStatement, data);
            preparedStatement.addBatch();
            preparedStatement.clearParameters();
            needExecute = true;
            if (count > 2000) {
               int[] updates = preparedStatement.executeBatch();
               returnCount += calculateBatchUpdateResults(updates);
               count = 0;
               needExecute = false;
            }
         }
         if (needExecute) {
            int[] updates = preparedStatement.executeBatch();
            returnCount += calculateBatchUpdateResults(updates);
         }

      } catch (SQLException ex) {
         // Get the nested exception
         SQLException nestedEx = ex.getNextException();
         if (nestedEx == null) {
            nestedEx = ex;
         }
         throw newJdbcException(nestedEx, "sql update failed: \n%s\n%s", query, getBatchErrorMessage(dataList));
      } finally {
         close(preparedStatement);
      }
      return returnCount;
   }

   private static <O extends Object> String getBatchErrorMessage(Iterable<O[]> dataList) {
      StringBuilder details = new StringBuilder(JDBC__MAX_VARCHAR_LENGTH);
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
         details.append("---------\n");
         if (details.length() > JDBC__MAX_VARCHAR_LENGTH) {
            break;
         }
      }
      details.append("]\n");
      return details.toString();
   }

   private static void close(PreparedStatement stmt) {
      if (stmt != null) {
         try {
            stmt.close();
         } catch (SQLException ex) {
            // do nothing
         }
      }
   }

   private static int calculateBatchUpdateResults(int[] updates) throws JdbcException {
      int returnCount = 0;
      for (int update : updates) {
         if (update >= 0) {
            returnCount += update;
         } else if (Statement.EXECUTE_FAILED == update) {
            throw newJdbcException("Batch update failed");
         } else if (Statement.SUCCESS_NO_INFO == update) {
            returnCount++;
         }
      }
      return returnCount;
   }

   @Override
   public int runBatchUpdate(String query, Iterable<Object[]> dataList) throws JdbcException {
      JdbcConnection connection = getConnection();
      try {
         return runBatchUpdate(connection, query, dataList);
      } finally {
         connection.close();
      }
   }

   @Override
   public int runPreparedUpdate(String query, Object... data) throws JdbcException {
      JdbcConnection connection = getConnection();
      try {
         return runPreparedUpdate(connection, query, data);
      } finally {
         connection.close();
      }
   }

   @Override
   public <T> T runPreparedQueryFetchObject(T defaultValue, String query, Object... data) throws JdbcException {
      return runPreparedQueryFetchObject(getStatement(), defaultValue, query, data);
   }

   @Override
   public <T> T runPreparedQueryFetchObject(JdbcConnection connection, T defaultValue, String query, Object... data) throws JdbcException {
      return runPreparedQueryFetchObject(getStatement(connection), defaultValue, query, data);
   }

   @SuppressWarnings("unchecked")
   private <T> T runPreparedQueryFetchObject(JdbcStatement chStmt, T defaultValue, String query, Object... data) throws JdbcException {
      if (defaultValue == null) {
         throw newJdbcException("%s cannot be null", defaultValue);
      }
      try {
         chStmt.runPreparedQuery(1, query, data);
         if (chStmt.next()) {
            Object toReturn = null;
            Class<?> classValue = defaultValue.getClass();
            if (classValue.isAssignableFrom(Integer.class)) {
               toReturn = chStmt.getInt(1);
            } else if (classValue.isAssignableFrom(String.class)) {
               toReturn = chStmt.getString(1);
            } else if (classValue.isAssignableFrom(Long.class)) {
               toReturn = chStmt.getLong(1);
            } else if (classValue.isAssignableFrom(Boolean.class)) {
               String value = chStmt.getObject(1).toString();
               toReturn = Boolean.parseBoolean(value);
            } else {
               toReturn = chStmt.getObject(1);
            }
            return (T) toReturn;
         }
         return defaultValue;
      } finally {
         chStmt.close();
      }
   }

   @Override
   public Map<String, String> getStatistics() throws JdbcException {
      return connectionProvider.getStatistics();
   }

   @Override
   public void runQuery(JdbcProcessor processor, String query, Object... data) throws JdbcException {
      JdbcStatement chStmt = getStatement();
      try {
         chStmt.runPreparedQuery(query, data);
         while (chStmt.next()) {
            processor.processNext(chStmt);
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void runTransaction(JdbcTransaction dbWork) throws JdbcException {
      JdbcConnectionImpl connection = getConnection();
      boolean initialAutoCommit = true;
      Exception saveException = null;
      try {
         initialAutoCommit = connection.getAutoCommit();
         connection.setAutoCommit(false);
         setConstraintChecking(connection, false);
         dbWork.handleTxWork(connection);

         connection.commit();
      } catch (Exception ex) {
         saveException = ex;
         try {
            connection.rollback();
         } finally {
            try {
               connection.destroy();
            } finally {
               dbWork.handleTxException(ex);
            }
         }
      } finally {
         try {
            try {
               if (!connection.isClosed()) {
                  try {
                     setConstraintChecking(connection, true);
                  } catch (Exception ex) {
                     if (saveException == null) {
                        saveException = ex;
                     }
                  } finally {
                     connection.setAutoCommit(initialAutoCommit);
                     connection.close();
                  }
               }
            } finally {
               dbWork.handleTxFinally();
            }
         } catch (Exception ex) {
            if (saveException == null) {
               saveException = ex;
            }
         }

         if (saveException != null) {
            throw newJdbcException(saveException);
         }
      }
   }

   private void setConstraintChecking(JdbcConnection connection, boolean enable) throws JdbcException {
      String cmd = null;
      JdbcDbType dbType = JdbcDbType.getDatabaseType(connection.getMetaData());
      switch (dbType) {
         case h2:
            cmd = String.format("SET REFERENTIAL_INTEGRITY = %s", Boolean.toString(enable).toUpperCase());
            break;
         case hsql:
            cmd = String.format("SET DATABASE REFERENTIAL INTEGRITY %s", Boolean.toString(enable).toUpperCase());
            break;
         default:
            // NOTE: this must be a PreparedStatement to play correctly with DB Transactions.
            cmd = String.format("SET CONSTRAINTS ALL %s", enable ? "IMMEDIATE" : "DEFERRED");
            break;
      }
      runPreparedUpdate(connection, cmd);
   }

   @Override
   public void initSchema(JdbcSchemaOptions options, JdbcSchemaResource... resources) throws JdbcException {
      initSchema(options, Arrays.asList(resources));
   }

   @Override
   public void initSchema(JdbcSchemaOptions options, Iterable<JdbcSchemaResource> resources) throws JdbcException {
      Map<String, SchemaData> userSpecifiedConfig = new HashMap<String, SchemaData>();
      Map<String, SchemaData> currentDatabaseConfig = new HashMap<String, SchemaData>();

      executeCallable(new LoadUserSchemasCallable(this, userSpecifiedConfig, resources, options));
      executeCallable(new ExtractSchemaCallable(this, userSpecifiedConfig.keySet(), currentDatabaseConfig));
      runTransaction(new CreateSchemaTx(this, userSpecifiedConfig, currentDatabaseConfig));
   }

   private void executeCallable(Callable<?> callable) {
      try {
         callable.call();
      } catch (Exception ex) {
         throw JdbcException.newJdbcException(ex);
      }
   }

   @Override
   public long getNextSequence(String sequenceName) {
      return sequenceProvider.getNextSequence(this, sequenceName);
   }

   @Override
   public void invalidateSequences() {
      sequenceProvider.invalidate();
   }

}