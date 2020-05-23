/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.jdbc.internal;

import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import javax.sql.DataSource;
import org.apache.commons.dbcp.AbandonedConfig;
import org.apache.commons.dbcp.AbandonedObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.jdbc.JdbcPoolConfig;
import org.eclipse.osee.jdbc.internal.JdbcConnectionFactoryManager.MetaData;

/**
 * @author Roberto E. Escobar
 */
public class PooledDataSourceFetcher implements Callable<DataSource> {

   private final JdbcConnectionFactoryManager manager;
   private final LazyObject<? extends PoolingDriver> poolingDriver;
   private final JdbcPoolConfig poolConfig;
   private final JdbcConnectionInfo dbInfo;

   public PooledDataSourceFetcher(JdbcConnectionFactoryManager manager, LazyObject<? extends PoolingDriver> poolingDriver, JdbcPoolConfig poolConfig, JdbcConnectionInfo dbInfo) {
      this.manager = manager;
      this.poolConfig = poolConfig;
      this.poolingDriver = poolingDriver;
      this.dbInfo = dbInfo;
   }

   @Override
   public DataSource call() throws Exception {
      String driverClazz = poolConfig.getPoolConnectionDriver();
      try {
         Class.forName(driverClazz);
      } catch (Exception ex) {
         throw newJdbcException(ex, "Error loading connection pool driver [%s]", driverClazz);
      }

      String poolId = dbInfo.getId();
      ObjectPool<Connection> connectionPool = createConnectionPool();

      PoolingDriver driver = poolingDriver.get();
      driver.registerPool(poolId, connectionPool);

      return new PoolingDataSource(connectionPool);
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   private ObjectPool<Connection> createConnectionPool() throws Exception {
      MetaData metadata = manager.getMetaData(dbInfo);

      JdbcConnectionFactory proxiedFactory = manager.getFactory(dbInfo.getDriver());
      ConnectionFactory connectionFactory =
         new ConnectionFactoryProxy(proxiedFactory, dbInfo, metadata.isTxIsolationLevelSupported());

      AbandonedObjectPool connectionPool = new AbandonedObjectPool(null, getAbandonedConnectionConfig(poolConfig));
      connectionPool.setConfig(getPoolConfig(poolConfig));

      GenericKeyedObjectPoolFactory statementPool = null;
      if (poolConfig.isPoolPreparedStatementsAllowed()) {
         statementPool = new GenericKeyedObjectPoolFactory(null, getStatementPoolConfig(poolConfig));
      }
      AbandonedConfig abandoned = new AbandonedConfig();
      abandoned.setLogAbandoned(true);
      abandoned.setLogWriter(new PrintWriter(System.out));

      String validationQuery = metadata.getValidationQuery();
      int validationQueryTimeoutSecs = poolConfig.getPoolValidationQueryTimeoutSecs();
      boolean defaultReadOnly = false;
      boolean defaultAutoCommit = true;
      new PoolableConnectionFactory(connectionFactory, connectionPool, statementPool, validationQuery,
         validationQueryTimeoutSecs, defaultReadOnly, defaultAutoCommit);
      return connectionPool;
   }

   private GenericObjectPool.Config getPoolConfig(JdbcPoolConfig config) {
      GenericObjectPool.Config toReturn = new GenericObjectPool.Config();
      toReturn.maxActive = config.getPoolMaxActiveConnections();
      toReturn.maxIdle = config.getPoolMaxIdleConnections();
      toReturn.minIdle = config.getPoolMinIdleConnections();
      toReturn.maxWait = config.getPoolMaxWaitForConnection();
      toReturn.whenExhaustedAction = config.getPoolExhaustedAction().asByteValue();
      toReturn.testOnBorrow = config.isPoolTestOnBorrowEnabled();
      toReturn.testOnReturn = config.isPoolTestOnReturnEnabled();
      toReturn.testWhileIdle = config.isPoolTestWhileIdeEnabled();
      toReturn.timeBetweenEvictionRunsMillis = config.getPoolTimeBetweenEvictionCheckMillis();
      toReturn.numTestsPerEvictionRun = config.getPoolNumberTestsPerEvictionRun();
      toReturn.minEvictableIdleTimeMillis = config.getPoolMinEvictableIdleTimeMillis();
      toReturn.lifo = config.isPoolLifo();
      toReturn.softMinEvictableIdleTimeMillis = config.getPoolSoftMinEvictableTimeoutMillis();
      return toReturn;
   }

   private GenericKeyedObjectPool.Config getStatementPoolConfig(JdbcPoolConfig config) {
      GenericKeyedObjectPool.Config toReturn = new GenericKeyedObjectPool.Config();

      toReturn.maxTotal = config.getPoolMaxTotalPreparedStatements();
      toReturn.maxActive = config.getPoolMaxActivePreparedStatements();
      toReturn.maxIdle = config.getPoolMaxIdlePreparedStatements();
      toReturn.minIdle = config.getPoolMinIdlePreparedStatements();
      toReturn.maxWait = config.getPoolMaxWaitPreparedStatements();

      // Same as Connection Pool
      toReturn.whenExhaustedAction = config.getPoolExhaustedAction().asByteValue();
      toReturn.testOnBorrow = config.isPoolTestOnBorrowEnabled();
      toReturn.testOnReturn = config.isPoolTestOnReturnEnabled();
      toReturn.testWhileIdle = config.isPoolTestWhileIdeEnabled();
      toReturn.timeBetweenEvictionRunsMillis = config.getPoolTimeBetweenEvictionCheckMillis();
      toReturn.numTestsPerEvictionRun = config.getPoolNumberTestsPerEvictionRun();
      toReturn.minEvictableIdleTimeMillis = config.getPoolMinEvictableIdleTimeMillis();
      toReturn.lifo = config.isPoolLifo();
      return toReturn;
   }

   private AbandonedConfig getAbandonedConnectionConfig(JdbcPoolConfig config) {
      AbandonedConfig abandoned = new AbandonedConfig();
      abandoned.setLogAbandoned(config.isPoolAbandonedLoggingEnabled());
      abandoned.setRemoveAbandoned(config.isPoolAbandonedRemovalEnabled());
      abandoned.setRemoveAbandonedTimeout(config.getPoolAbandonedRemovalTimeout());
      return abandoned;
   }

   private static final class ConnectionFactoryProxy implements ConnectionFactory {
      private final JdbcConnectionFactory proxiedFactory;
      private final JdbcConnectionInfo dbInfo;

      private final boolean supportsIsolationLevel;

      public ConnectionFactoryProxy(JdbcConnectionFactory proxiedFactory, JdbcConnectionInfo dbInfo, boolean supportsIsolationLevel) {
         super();
         this.proxiedFactory = proxiedFactory;
         this.dbInfo = dbInfo;
         this.supportsIsolationLevel = supportsIsolationLevel;
      }

      @Override
      public Connection createConnection() throws SQLException {
         Connection connection = null;
         try {
            connection = proxiedFactory.getConnection(dbInfo);
            if (supportsIsolationLevel) {
               connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            }
         } catch (Exception ex) {
            if (connection != null) {
               try {
                  connection.close();
               } catch (SQLException ex1) {
                  // Do nothing on close exception;
               }
            }
            if (ex instanceof SQLException) {
               throw (SQLException) ex;
            } else {
               throw new SQLException(ex);
            }
         }
         return connection;
      }
   }
}