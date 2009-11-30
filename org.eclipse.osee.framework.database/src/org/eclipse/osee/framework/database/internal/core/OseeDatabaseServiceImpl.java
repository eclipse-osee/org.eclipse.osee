/*
 * Created on Nov 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.database.internal.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeConnectionProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeSequence;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.internal.InternalActivator;
import org.eclipse.osee.framework.database.sql.QueryRecord;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class OseeDatabaseServiceImpl implements IOseeDatabaseService {
   private static final Timer timer = new Timer();
   private static final Map<String, OseeConnectionPoolImpl> dbInfoToPools =
         new HashMap<String, OseeConnectionPoolImpl>();

   private final IOseeSequence oseeSequence;
   private final IOseeConnectionProvider connectionProvider;

   public OseeDatabaseServiceImpl(IOseeSequence sequenceManager, IOseeConnectionProvider connectionProvider) {
      this.oseeSequence = sequenceManager;
      this.connectionProvider = connectionProvider;
   }

   private IDatabaseInfo getDatabaseInfoProvider() throws OseeDataStoreException {
      return connectionProvider.getApplicationDatabaseProvider().getDatabaseInfo();
   }

   private OseeConnectionPoolImpl getDefaultConnectionPool() throws OseeDataStoreException {
      return getConnectionPool(getDatabaseInfoProvider());
   }

   @Override
   public IOseeSequence getSequence() {
      return oseeSequence;
   }

   @Override
   public OseeConnection getConnection() throws OseeDataStoreException {
      return getConnection(getDatabaseInfoProvider());
   }

   private OseeConnectionPoolImpl getConnectionPool(IDatabaseInfo databaseInfo) throws OseeDataStoreException {
      if (databaseInfo == null) {
         throw new OseeDataStoreException("Unable to get connection - database info was null.");
      }
      OseeConnectionPoolImpl pool = dbInfoToPools.get(databaseInfo.getId());
      if (pool == null) {
         pool =
               new OseeConnectionPoolImpl(connectionProvider, databaseInfo.getDriver(),
                     databaseInfo.getConnectionUrl(), databaseInfo.getConnectionProperties());
         dbInfoToPools.put(databaseInfo.getId(), pool);
         timer.schedule(new StaleConnectionCloser(pool), 900000, 900000);
      }
      return pool;
   }

   @Override
   public OseeConnection getConnection(IDatabaseInfo databaseInfo) throws OseeDataStoreException {
      return getConnectionPool(databaseInfo).getConnection();
   }

   @Override
   public IOseeStatement getStatement() throws OseeDataStoreException {
      return new OseeStatementImpl(getDefaultConnectionPool());
   }

   @Override
   public IOseeStatement getStatement(OseeConnection connection) throws OseeDataStoreException {
      return new OseeStatementImpl(getDefaultConnectionPool(), (OseeConnectionImpl) connection);
   }

   @Override
   public IOseeStatement getStatement(OseeConnection connection, boolean autoClose) throws OseeDataStoreException {
      return new OseeStatementImpl(getDefaultConnectionPool(), (OseeConnectionImpl) connection, autoClose);
   }

   @Override
   public IOseeStatement getStatement(int resultSetType, int resultSetConcurrency) throws OseeDataStoreException {
      throw new UnsupportedOperationException("This needs to be implemented");
      //      return null;
   }

   @Override
   public <O extends Object> int runPreparedUpdate(OseeConnection connection, String query, O... data) throws OseeDataStoreException {
      if (connection == null) {
         return runPreparedUpdate(query, data);
      }
      PreparedStatement preparedStatement = null;
      int updateCount = 0;
      try {
         preparedStatement = ((OseeConnectionImpl) connection).prepareStatement(query);
         StatementUtil.populateValuesForPreparedStatement(preparedStatement, data);
         updateCount = preparedStatement.executeUpdate();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      } finally {
         StatementUtil.close(preparedStatement);
      }
      return updateCount;
   }

   @Override
   public <O extends Object> int runBatchUpdate(OseeConnection connection, String query, List<O[]> dataList) throws OseeDataStoreException {
      if (connection == null) {
         return runBatchUpdate(query, dataList);
      }
      QueryRecord record = new QueryRecord("<batchable: batched> " + query, dataList.size());
      int returnCount = 0;
      PreparedStatement preparedStatement = null;
      try {
         preparedStatement = ((OseeConnectionImpl) connection).prepareStatement(query);
         record.markStart();
         boolean needExecute = false;
         int count = 0;
         for (Object[] data : dataList) {
            count++;
            StatementUtil.populateValuesForPreparedStatement(preparedStatement, data);
            preparedStatement.addBatch();
            preparedStatement.clearParameters();
            needExecute = true;
            if (count > 2000) {
               int[] updates = preparedStatement.executeBatch();
               returnCount += StatementUtil.calculateBatchUpdateResults(updates);
               count = 0;
               needExecute = false;
            }
         }
         if (needExecute) {
            int[] updates = preparedStatement.executeBatch();
            returnCount += StatementUtil.calculateBatchUpdateResults(updates);
         }

         record.markEnd();
      } catch (SQLException ex) {
         record.setSqlException(ex);
         SQLException exlist;
         if ((exlist = ex.getNextException()) != null) {
            OseeLog.log(InternalActivator.class, Level.SEVERE, "This is the nested exception", exlist);
         }
         throw new OseeDataStoreException(
               "sql update failed: \n" + query + "\n" + StatementUtil.getBatchErrorMessage(dataList), ex);
      } finally {
         StatementUtil.close(preparedStatement);
      }
      return returnCount;
   }

   @Override
   public <O> int runBatchUpdate(String query, List<O[]> dataList) throws OseeDataStoreException {
      OseeConnection connection = getConnection();
      try {
         return runBatchUpdate(connection, query, dataList);
      } finally {
         connection.close();
      }
   }

   @Override
   public <O> int runPreparedUpdate(String query, O... data) throws OseeDataStoreException {
      OseeConnection connection = getConnection();
      try {
         return runPreparedUpdate(connection, query, data);
      } finally {
         connection.close();
      }
   }

   @Override
   public <T, O extends Object> T runPreparedQueryFetchObject(T defaultValue, String query, O... data) throws OseeDataStoreException {
      return runPreparedQueryFetchObject(getStatement(), defaultValue, query, data);
   }

   @Override
   public <T, O extends Object> T runPreparedQueryFetchObject(OseeConnection connection, T defaultValue, String query, O... data) throws OseeDataStoreException {
      return runPreparedQueryFetchObject(getStatement(connection), defaultValue, query, data);
   }

   @SuppressWarnings("unchecked")
   private <T, O extends Object> T runPreparedQueryFetchObject(IOseeStatement chStmt, T defaultValue, String query, O... data) throws OseeDataStoreException {
      try {
         Conditions.checkNotNull(defaultValue, "default value");
      } catch (OseeCoreException ex) {
         throw new OseeDataStoreException(ex);
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
}
