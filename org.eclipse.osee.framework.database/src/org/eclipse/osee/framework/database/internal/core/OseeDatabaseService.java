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
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.internal.IDbConnectionFactory;
import org.eclipse.osee.framework.database.internal.InternalActivator;
import org.eclipse.osee.framework.database.sql.QueryRecord;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class OseeDatabaseService implements IOseeDatabaseService {
   private static final Timer timer = new Timer();
   private static final Map<String, OseeConnectionPoolImpl> dbInfoToPools =
         new HashMap<String, OseeConnectionPoolImpl>();

   public OseeDatabaseService() {
   }

   //
   private IDatabaseInfo getDatabaseInfoProvider() throws OseeDataStoreException {
      return InternalActivator.getInstance().getApplicationDatabaseProvider().getDatabaseInfo();
   }

   private IDbConnectionFactory getConnectionFactory() throws OseeDataStoreException {
      return InternalActivator.getInstance().getConnectionFactory();
   }

   private OseeConnectionPoolImpl getDefaultConnectionPool() throws OseeDataStoreException {
      return getConnectionPool(getDatabaseInfoProvider());
   }

   //
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
               new OseeConnectionPoolImpl(getConnectionFactory(), databaseInfo.getDriver(),
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
   public ConnectionHandlerStatement getStatement() throws OseeDataStoreException {
      return new ConnectionHandlerStatementImpl(getDefaultConnectionPool());
   }

   @Override
   public ConnectionHandlerStatement getStatement(OseeConnection connection) throws OseeDataStoreException {
      return new ConnectionHandlerStatementImpl(getDefaultConnectionPool(), (OseeConnectionImpl) connection);
   }

   @Override
   public ConnectionHandlerStatement getStatement(OseeConnection connection, boolean autoClose) throws OseeDataStoreException {
      return new ConnectionHandlerStatementImpl(getDefaultConnectionPool(), (OseeConnectionImpl) connection, autoClose);
   }

   @Override
   public <O extends Object> int runPreparedUpdate(OseeConnection connection, String query, O... data) throws OseeDataStoreException {
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

   //
   //   public boolean hasOpenConnection() throws OseeDataStoreException {
   //      OseeConnectionPoolImpl pool = getDefaultConnectionPool();
   //      if (pool == null) {
   //         return false;
   //      }
   //      return pool.hasOpenConnection();
   //   }

}
