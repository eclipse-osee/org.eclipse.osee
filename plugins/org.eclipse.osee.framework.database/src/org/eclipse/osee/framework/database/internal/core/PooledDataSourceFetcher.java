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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import javax.sql.DataSource;
import org.apache.commons.dbcp.AbandonedConfig;
import org.apache.commons.dbcp.AbandonedObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.database.core.IConnectionFactory;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.database.internal.core.PoolFactory.PoolConfiguration;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;

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

      IConnectionFactory proxiedFactory = getFactory(dbInfo.getDriver());

      MetaData metadata = getMetaData(proxiedFactory, dbInfo);

      ConnectionFactory connectionFactory =
         new ConnectionFactoryProxy(proxiedFactory, dbInfo, metadata.isTxIsolationLevelSupported());

      AbandonedObjectPool connectionPool =
         new AbandonedObjectPool(null, PoolConfigUtil.getAbandonedConnectionConfig(configProps));
      connectionPool.setConfig(PoolConfigUtil.getPoolConfig(configProps));

      GenericKeyedObjectPoolFactory statementPool = null;
      if (PoolConfigUtil.isPoolingPreparedStatementsAllowed(configProps)) {
         statementPool = new GenericKeyedObjectPoolFactory(null, PoolConfigUtil.getStatementPoolConfig(configProps));
      }
      AbandonedConfig abandoned = new AbandonedConfig();
      abandoned.setLogAbandoned(true);
      abandoned.setLogWriter(new PrintWriter(System.out));

      String validationQuery = metadata.getValidationQuery();
      int validationQueryTimeoutSecs = PoolConfigUtil.getValidationQueryTimeoutSecs(configProps);
      boolean defaultReadOnly = false;
      boolean defaultAutoCommit = true;
      new PoolableConnectionFactory(connectionFactory, connectionPool, statementPool, validationQuery,
         validationQueryTimeoutSecs, defaultReadOnly, defaultAutoCommit);
      return connectionPool;
   }

   private IConnectionFactory getFactory(String driver) {
      IConnectionFactory factory = factories.get(driver);
      if (factory == null) {
         factory = new DefaultConnectionFactory(driver);
         factories.put(driver, factory);
      }
      return factory;
   }

   private MetaData getMetaData(IConnectionFactory proxiedFactory, IDatabaseInfo dbInfo) throws Exception {
      MetaData metaData = new MetaData();
      Connection connection = null;
      try {
         connection = proxiedFactory.getConnection(dbInfo);
         DatabaseMetaData metadata = connection.getMetaData();
         metaData.setTxIsolationLevelSupported(metadata.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED));
         metaData.setValidationQuery(SupportedDatabase.getValidationSql(metadata));
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
      return metaData;
   }

   private final class MetaData {
      private boolean isTxIsolationLevelSupported;
      private String validationQuery;

      public boolean isTxIsolationLevelSupported() {
         return isTxIsolationLevelSupported;
      }

      public void setTxIsolationLevelSupported(boolean isTxIsolationLevelSupported) {
         this.isTxIsolationLevelSupported = isTxIsolationLevelSupported;
      }

      public String getValidationQuery() {
         return validationQuery;
      }

      public void setValidationQuery(String validationQuery) {
         this.validationQuery = validationQuery;
      }

   }

   private static final class DefaultConnectionFactory implements IConnectionFactory {

      private final String driver;

      public DefaultConnectionFactory(String driver) {
         this.driver = driver;
      }

      @Override
      public Connection getConnection(IDatabaseInfo dbInfo) throws Exception {
         try {
            Class.forName(driver);
         } catch (Exception ex) {
            throw new OseeNotFoundException("Unable to find connection factory with driver [%s]", driver);
         }
         return DriverManager.getConnection(dbInfo.getConnectionUrl(), dbInfo.getConnectionProperties());
      }

      @Override
      public String getDriver() {
         return driver;
      }
   }

   private static final class ConnectionFactoryProxy implements ConnectionFactory {
      private final IConnectionFactory proxiedFactory;
      private final IDatabaseInfo dbInfo;

      private final boolean supportsIsolationLevel;

      public ConnectionFactoryProxy(IConnectionFactory proxiedFactory, IDatabaseInfo dbInfo, boolean supportsIsolationLevel) {
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
            throw new SQLException(ex);
         }
         return connection;
      }
   }
}