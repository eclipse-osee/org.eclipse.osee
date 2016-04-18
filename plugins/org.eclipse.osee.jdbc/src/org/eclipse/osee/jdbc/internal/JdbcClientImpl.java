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

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_TX_ROW_COUNT;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_VARCHAR_LENGTH;
import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.IVariantData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcClientConfig;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcException;
import org.eclipse.osee.jdbc.JdbcMigrationOptions;
import org.eclipse.osee.jdbc.JdbcMigrationResource;
import org.eclipse.osee.jdbc.JdbcProcessor;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.jdbc.SQL3DataType;

/**
 * @author Roberto E. Escobar
 */
public final class JdbcClientImpl implements JdbcClient {

   private final JdbcClientConfig config;
   private final JdbcConnectionProvider connectionProvider;
   private final JdbcSequenceProvider sequenceProvider;
   private final JdbcConnectionInfo dbInfo;
   private final JdbcMigration migration;

   private volatile JdbcDbType dbType;

   public JdbcClientImpl(JdbcClientConfig config, JdbcConnectionProvider connectionProvider, JdbcSequenceProvider sequenceProvider, JdbcConnectionInfo dbInfo) {
      super();
      this.config = config;
      this.connectionProvider = connectionProvider;
      this.sequenceProvider = sequenceProvider;
      this.dbInfo = dbInfo;
      this.migration = new JdbcMigration(this);
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
         JdbcUtil.setInputParametersForStatement(preparedStatement, data);
         updateCount = preparedStatement.executeUpdate();
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      } finally {
         JdbcUtil.close(preparedStatement);
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
            JdbcUtil.setInputParametersForStatement(preparedStatement, data);
            preparedStatement.addBatch();
            preparedStatement.clearParameters();
            needExecute = true;
            if (count > 2000) {
               int[] updates = preparedStatement.executeBatch();
               returnCount += JdbcUtil.calculateBatchUpdateResults(updates);
               count = 0;
               needExecute = false;
            }
         }
         if (needExecute) {
            int[] updates = preparedStatement.executeBatch();
            returnCount += JdbcUtil.calculateBatchUpdateResults(updates);
         }

      } catch (SQLException ex) {
         // Get the nested exception
         SQLException nestedEx = ex.getNextException();
         if (nestedEx == null) {
            nestedEx = ex;
         }
         throw newJdbcException(nestedEx, "sql update failed: \n%s\n%s", query, getBatchErrorMessage(dataList));
      } finally {
         JdbcUtil.close(preparedStatement);
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
         throw newJdbcException("default value cannot be null");
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

   @SuppressWarnings("unchecked")
   @Override
   public <T> T runFunction(T defaultValue, String function, Object... data) {
      if (defaultValue == null) {
         throw newJdbcException("defaultValue cannot be null");
      }
      String sql;
      if (JdbcDbType.oracle == getDatabaseType()) {
         sql = String.format("{ ? = call %s }", function);
      } else {
         sql = String.format("call %s", function);
      }
      JdbcConnectionImpl connection = getConnection();
      try {
         CallableStatement stmt = null;
         try {
            stmt = connection.prepareCall(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            Class<?> classValue = defaultValue.getClass();
            SQL3DataType dataType = null;
            if (classValue.isAssignableFrom(String.class)) {
               dataType = SQL3DataType.VARCHAR;
            } else if (classValue.isAssignableFrom(Boolean.class)) {
               dataType = SQL3DataType.BOOLEAN;
            } else if (classValue.isAssignableFrom(Integer.class)) {
               dataType = SQL3DataType.INTEGER;
            } else if (classValue.isAssignableFrom(Long.class)) {
               dataType = SQL3DataType.BIGINT;
            } else if (classValue.isAssignableFrom(Double.class)) {
               dataType = SQL3DataType.DOUBLE;
            } else if (classValue.isAssignableFrom(Date.class)) {
               dataType = SQL3DataType.TIMESTAMP;
            } else {
               throw newJdbcException(
                  "Unable to determine ouput SQL3DataType for function [%s] using default value [%s]", function,
                  defaultValue);
            }

            if (JdbcDbType.oracle == getDatabaseType()) {
               stmt.registerOutParameter(1, dataType.getSQLTypeNumber());
               JdbcUtil.setInputParametersForStatement(stmt, 2, data);
            } else {
               JdbcUtil.setInputParametersForStatement(stmt, 1, data);
            }

            boolean hasResultSet = stmt.execute();
            Object toReturn = null;
            if (hasResultSet) {
               ResultSet rSet = stmt.getResultSet();
               rSet.next();
               switch (dataType) {
                  case VARCHAR:
                     toReturn = rSet.getString(1);
                     break;
                  case BOOLEAN:
                     toReturn = rSet.getBoolean(1);
                     break;
                  case INTEGER:
                     toReturn = rSet.getInt(1);
                     break;
                  case BIGINT:
                     toReturn = rSet.getLong(1);
                     break;
                  case DOUBLE:
                     toReturn = rSet.getDouble(1);
                     break;
                  default:
                     toReturn = rSet.getObject(1);
                     break;
               }
            } else {
               switch (dataType) {
                  case VARCHAR:
                     toReturn = stmt.getString(1);
                     break;
                  case BOOLEAN:
                     toReturn = stmt.getBoolean(1);
                     break;
                  case INTEGER:
                     toReturn = stmt.getInt(1);
                     break;
                  case BIGINT:
                     toReturn = stmt.getLong(1);
                     break;
                  case DOUBLE:
                     toReturn = stmt.getDouble(1);
                     break;
                  default:
                     toReturn = stmt.getObject(1);
                     break;
               }
            }
            return toReturn != null ? (T) toReturn : defaultValue;
         } catch (SQLException ex) {
            throw newJdbcException(ex);
         } finally {
            JdbcUtil.close(stmt);
         }
      } finally {
         connection.close();
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
   public void runTransaction(JdbcConnection jdbcConnection, JdbcTransaction dbWork) throws JdbcException {
      JdbcConnectionImpl connection = (JdbcConnectionImpl) jdbcConnection;
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
            throw OseeCoreException.wrap(saveException);
         }
      }
   }

   @Override
   public void runTransaction(JdbcTransaction dbWork) throws JdbcException {
      runTransaction(getConnection(), dbWork);

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
   public long getNextSequence(String sequenceName, boolean aggressiveFetch) {
      return sequenceProvider.getNextSequence(this, sequenceName, aggressiveFetch);
   }

   @Override
   public void invalidateSequences() {
      sequenceProvider.invalidate();
   }

   @Override
   public List<IVariantData> runQuery(String query, Object... data) {
      List<IVariantData> toReturn = new ArrayList<>();
      runQuery(new JdbcVariantDataProcessor(toReturn), query, data);
      return toReturn;
   }

   @Override
   public void migrate(JdbcMigrationOptions options, Iterable<JdbcMigrationResource> schemaResources) {
      migration.migrate(options, schemaResources);
   }

   @Override
   public OseePreparedStatement getBatchStatement(String query) {
      return getBatchStatement(getConnection(), query, JDBC__MAX_TX_ROW_COUNT);
   }

   @Override
   public OseePreparedStatement getBatchStatement(String query, int batchIncrementSize) {
      return getBatchStatement(getConnection(), query, batchIncrementSize);
   }

   @Override
   public OseePreparedStatement getBatchStatement(JdbcConnection connection, String query) {
      return getBatchStatement(connection, query, JDBC__MAX_TX_ROW_COUNT);
   }

   @Override
   public OseePreparedStatement getBatchStatement(JdbcConnection connection, String query, int batchIncrementSize) {
      try {
         PreparedStatement preparedStatement = ((JdbcConnectionImpl) connection).prepareStatement(query);
         return new OseePreparedStatement(preparedStatement, batchIncrementSize);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }
}