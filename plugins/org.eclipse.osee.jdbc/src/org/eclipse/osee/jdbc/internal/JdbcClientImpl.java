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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcClientConfig;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcException;
import org.eclipse.osee.jdbc.JdbcMigrationOptions;
import org.eclipse.osee.jdbc.JdbcMigrationResource;
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
   public JdbcDbType getDbType() {
      if (dbType == null) {
         try (JdbcConnection connection = getConnection()) {
            dbType = JdbcDbType.getDbType(connection);
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
      try (JdbcConnection connection = getConnection()) {
         return runPreparedUpdate(connection, query, data);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T runFunction(T defaultValue, String function, Object... data) {
      if (defaultValue == null) {
         throw newJdbcException("defaultValue cannot be null");
      }
      String sql = getDbType().getFunctionCallSql(function);
      try (JdbcConnectionImpl connection = getConnection()) {
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

            if (getDbType().equals(JdbcDbType.oracle)) {
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
      }
   }

   @Override
   public Map<String, String> getStatistics() throws JdbcException {
      return connectionProvider.getStatistics();
   }

   @Override
   public int runQuery(JdbcConnection connection, Consumer<JdbcStatement> consumer, String query, Object... data) {
      return runQuery(connection, consumer, 0, query, data);
   }

   @Override
   public int runQuery(Consumer<JdbcStatement> consumer, String query, Object... data) {
      return runQuery(consumer, 0, query, data);
   }

   @Override
   public int runQuery(Consumer<JdbcStatement> consumer, int fetchSize, String query, Object... data) {
      try (JdbcConnection conn = getConnection()) {
         return runQuery(conn, consumer, fetchSize, query, data);
      }
   }

   @Override
   public int runQuery(JdbcConnection connection, Consumer<JdbcStatement> consumer, int fetchSize, String query, Object... data) {
      int rowCount = 0;
      try (JdbcStatement stmt = getStatement(connection)) {
         stmt.runPreparedQuery(fetchSize, query, data);
         while (stmt.next()) {
            consumer.accept(stmt);
            rowCount++;
         }
      }
      return rowCount;
   }

   @Override
   public void runCall(String call, Object... data) {
      try (JdbcStatement stmt = getStatement()) {
         stmt.runPreparedQuery(call, data);
      }
   }

   private static final String LimitStart = "select * from (";
   private static final String LimitEnd = ") where rownum <= ?";

   @Override
   public int runQueryWithLimit(Consumer<JdbcStatement> consumer, int limit, String query, Object... data) {
      StringBuilder strB = new StringBuilder(query.length() + LimitStart.length() + LimitEnd.length());
      if (getDbType().equals(JdbcDbType.oracle)) {
         strB.append(LimitStart);
         strB.append(query);
         strB.append(LimitEnd);
      } else {
         strB.append(query);
         strB.append(" limit ?");
      }

      Object[] fullData = new Object[data.length + 1];
      System.arraycopy(data, 0, fullData, 0, data.length);
      fullData[data.length] = limit;
      return runQuery(consumer, limit, strB.toString(), fullData);
   }

   @Override
   public <R> R fetch(R defaultValue, String query, Object... data) {
      return fetch(null, defaultValue, query, data);
   }

   @Override
   public <R> R fetch(JdbcConnection connection, R defaultValue, String query, Object... data) {
      return fetch(connection, defaultValue, stmt -> fetch(stmt, defaultValue), query, data);
   }

   private static <R> R fetch(JdbcStatement stmt, R defaultValue) {
      return fetch(stmt, defaultValue, (Class<R>) defaultValue.getClass());
   }

   private static <R> R fetch(JdbcStatement stmt, R defaultValue, Class<R> clazz) {
      Object toReturn = null;
      if (Integer.class.isAssignableFrom(clazz)) {
         toReturn = stmt.getInt(1);
      } else if (Long.class.isAssignableFrom(clazz)) {
         toReturn = stmt.getLong(1);
      } else if (String.class.isAssignableFrom(clazz)) {
         toReturn = stmt.getString(1);
      } else if (Boolean.class.isAssignableFrom(clazz)) {
         String value = stmt.getObject(1).toString();
         toReturn = Boolean.parseBoolean(value);
      } else if (BaseId.class.isAssignableFrom(clazz)) {
         toReturn = ((BaseId) defaultValue).clone(stmt.getLong(1));
      } else {
         throw new OseeArgumentException("Unsupported type: %s", clazz.getName());
      }
      return (R) toReturn;
   }

   @Override
   public <R> R fetchOrException(Class<R> clazz, Supplier<OseeCoreException> exSupplier, String query, Object... data) {
      return fetchOrException(null, exSupplier, stmt -> fetch(stmt, null, clazz), query, data);
   }

   @Override
   public <R> R fetch(R defaultValue, Function<JdbcStatement, R> function, String query, Object... data) {
      return fetch(null, defaultValue, function, query, data);
   }

   @Override
   public <R> R fetch(JdbcConnection connection, R defaultValue, Function<JdbcStatement, R> function, String query, Object... data) {
      try (JdbcStatement chStmt = getStatement(connection)) {
         chStmt.runPreparedQuery(query, data);
         if (chStmt.next()) {
            return function.apply(chStmt);
         }
         return defaultValue;
      }
   }

   @Override
   public <R> R fetchOrException(Supplier<OseeCoreException> exSupplier, Function<JdbcStatement, R> function, String query, Object... data) {
      return fetchOrException(null, exSupplier, function, query, data);
   }

   @Override
   public <R> R fetchOrException(JdbcConnection connection, Supplier<OseeCoreException> exSupplier, Function<JdbcStatement, R> function, String query, Object... data) {
      try (JdbcStatement chStmt = getStatement(connection)) {
         chStmt.runPreparedQuery(query, data);
         if (chStmt.next()) {
            return function.apply(chStmt);
         }
         throw exSupplier.get();
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
         if (!connection.isClosed()) {
            connection.setAutoCommit(initialAutoCommit);
            connection.close();
         }
         dbWork.handleTxFinally();
         if (saveException != null) {
            throw OseeCoreException.wrap(saveException);
         }
      }
   }

   @Override
   public void runTransaction(JdbcTransaction dbWork) throws JdbcException {
      runTransaction(getConnection(), dbWork);

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
   public void migrate(JdbcMigrationOptions options, Iterable<JdbcMigrationResource> schemaResources) {
      migration.migrate(options, schemaResources);
   }

   @Override
   public OseePreparedStatement getBatchStatement(String query) {
      return getBatchStatement(query, JDBC__MAX_TX_ROW_COUNT);
   }

   @Override
   public OseePreparedStatement getBatchStatement(String query, int batchIncrementSize) {
      return getBatchStatement(null, query, batchIncrementSize);
   }

   @Override
   public OseePreparedStatement getBatchStatement(JdbcConnection connection, String query) {
      return getBatchStatement(connection, query, JDBC__MAX_TX_ROW_COUNT);
   }

   @Override
   public OseePreparedStatement getBatchStatement(JdbcConnection connection, String query, int batchIncrementSize) {
      try {
         JdbcConnectionImpl connect;
         boolean autoClose = false;
         if (connection == null) {
            connect = getConnection();
            autoClose = true;
         } else {
            connect = ((JdbcConnectionImpl) connection);
         }

         PreparedStatement preparedStatement = connect.prepareStatement(query);
         return new OseePreparedStatement(preparedStatement, batchIncrementSize, connect, autoClose);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public int clearTable(String tableName) {
      String cmd = isTruncateSupported(tableName) ? "TRUNCATE TABLE" : "DELETE FROM";
      return runPreparedUpdate(String.format("%s %s", cmd, tableName));
   }

   private boolean isTruncateSupported(String tableName) {
      try (JdbcConnection connection = getConnection(); ResultSet resultSet = getPrivileges(connection, tableName)) {
         while (resultSet.next()) {
            String value = resultSet.getString("PRIVILEGE");
            if ("TRUNCATE".equalsIgnoreCase(value)) {
               return true;
            }
         }
      } catch (SQLException ex) {
         return false;
      }
      return false;
   }

   private ResultSet getPrivileges(JdbcConnection connection, String tableName) throws SQLException {
      return connection.getMetaData().getTablePrivileges(null, null, tableName.toUpperCase());
   }
}