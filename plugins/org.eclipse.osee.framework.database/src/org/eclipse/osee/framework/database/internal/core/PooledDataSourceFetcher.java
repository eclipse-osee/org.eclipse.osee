/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.internal.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import javax.sql.DataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.data.LazyObject;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.database.core.IConnectionFactory;
import org.eclipse.osee.framework.database.internal.core.PoolFactory.PoolConfiguration;

/**
 * @author Roberto E. Escobar
 */
public class PooledDataSourceFetcher implements Callable<DataSource> {

   private final Map<String, IConnectionFactory> factories;
   private final LazyObject<? extends PoolingDriver> poolingDriver;
   private final LazyObject<? extends PoolConfiguration> poolConfig;
   private final IDatabaseInfo dbInfo;

   public PooledDataSourceFetcher(Map<String, IConnectionFactory> factories, LazyObject<? extends PoolingDriver> poolingDriver, LazyObject<? extends PoolConfiguration> poolConfig, IDatabaseInfo dbInfo) {
      this.factories = factories;
      this.poolConfig = poolConfig;
      this.poolingDriver = poolingDriver;
      this.dbInfo = dbInfo;
   }

   @Override
   public DataSource call() throws Exception {
      PoolConfiguration configuration = poolConfig.get();
      String driverClazz = configuration.getConnectionPoolDriver();
      try {
         Class.forName(driverClazz);
      } catch (Exception ex) {
         throw new OseeDataStoreException(ex, "Error loading connection pool driver [%s]", driverClazz);
      }

      String poolId = dbInfo.getId();
      ObjectPool<Connection> connectionPool = createConnectionPool();

      PoolingDriver driver = poolingDriver.get();
      driver.registerPool(poolId, connectionPool);

      return new PoolingDataSource(connectionPool);
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   private ObjectPool<Connection> createConnectionPool() throws Exception {
      PoolConfiguration configuration = poolConfig.get();
      Properties configProps = configuration.getProperties();

      ConnectionFactory connectionFactory = createConnectionFactory(dbInfo);

      GenericObjectPool<Connection> connectionPool = new GenericObjectPool<Connection>();
      connectionPool.setConfig(PoolConfigUtil.getPoolConfig(configProps));

      GenericKeyedObjectPoolFactory statementPool = null;
      if (PoolConfigUtil.isPoolingPreparedStatementsAllowed(configProps)) {
         statementPool = new GenericKeyedObjectPoolFactory(null, PoolConfigUtil.getStatementPoolConfig(configProps));
      }

      String validationQuery = PoolConfigUtil.getValidationQuery(configProps);
      int validationQueryTimeoutSecs = PoolConfigUtil.getValidationQueryTimeoutSecs(configProps);
      boolean defaultReadOnly = false;
      boolean defaultAutoCommit = true;
      new PoolableConnectionFactory(connectionFactory, connectionPool, statementPool, validationQuery,
         validationQueryTimeoutSecs, defaultReadOnly, defaultAutoCommit);
      return connectionPool;
   }

   private ConnectionFactory createConnectionFactory(IDatabaseInfo dbInfo) throws Exception {
      IConnectionFactory proxiedFactory = getFactory(dbInfo.getDriver());
      String connectionURL = dbInfo.getConnectionUrl();
      Properties properties = dbInfo.getConnectionProperties();
      boolean isTxIsolationLevelSupported =
         isTxReadCommittedIsolationLevelSupported(proxiedFactory, connectionURL, properties);
      return new ConnectionFactoryProxy(proxiedFactory, connectionURL, properties, isTxIsolationLevelSupported);
   }

   private IConnectionFactory getFactory(String driver) {
      IConnectionFactory factory = factories.get(driver);
      if (factory == null) {
         factory = new DefaultConnectionFactory(driver);
         factories.put(driver, factory);
      }
      return factory;
   }

   private boolean isTxReadCommittedIsolationLevelSupported(IConnectionFactory proxiedFactory, String connectionURL, Properties properties) throws Exception {
      boolean isTxIsolationLevelSupported = false;
      Connection connection = null;
      try {
         connection = proxiedFactory.getConnection(properties, connectionURL);
         DatabaseMetaData metadata = connection.getMetaData();
         isTxIsolationLevelSupported =
            metadata.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
      return isTxIsolationLevelSupported;
   }

   private static final class DefaultConnectionFactory implements IConnectionFactory {

      private final String driver;

      public DefaultConnectionFactory(String driver) {
         this.driver = driver;
      }

      @Override
      public Connection getConnection(Properties properties, String connectionURL) throws Exception {
         try {
            Class.forName(driver);
         } catch (Exception ex) {
            throw new OseeNotFoundException("Unable to find connection factory with driver [%s]", driver);
         }
         return DriverManager.getConnection(connectionURL, properties);
      }

      @Override
      public String getDriver() {
         return driver;
      }
   }

   private static final class ConnectionFactoryProxy implements ConnectionFactory {
      private final IConnectionFactory proxiedFactory;
      private final String connectionURL;
      private final Properties properties;

      private final boolean supportsIsolationLevel;

      public ConnectionFactoryProxy(IConnectionFactory proxiedFactory, String connectionURL, Properties properties, boolean supportsIsolationLevel) {
         super();
         this.proxiedFactory = proxiedFactory;
         this.connectionURL = connectionURL;
         this.properties = properties;
         this.supportsIsolationLevel = supportsIsolationLevel;
      }

      @Override
      public Connection createConnection() throws SQLException {
         Connection connection = null;
         try {
            connection = proxiedFactory.getConnection(properties, connectionURL);
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
            throw new SQLException(ex);
         }
         return connection;
      }
   }
}