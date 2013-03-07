/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.internal.core;

/**
 * @author Roberto E. Escobar
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import javax.sql.DataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.data.LazyObject;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.database.core.IConnectionFactory;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;

public class PooledConnectionProvider implements ConnectionProvider {

   private static final String CONNECTION_POOL_DRIVER = "org.apache.commons.dbcp.PoolingDriver";
   private static final String CONNECTION_POOL_ID = "jdbc:apache:commons:dbcp:";

   private final ConcurrentHashMap<String, FutureTask<DataSource>> dataSourceCache =
      new ConcurrentHashMap<String, FutureTask<DataSource>>();

   private final PoolingDriverRef poolingDriver = new PoolingDriverRef();

   private final Map<String, IConnectionFactory> factories;
   private final IDatabaseInfoProvider dbInfoProvider;

   public PooledConnectionProvider(IDatabaseInfoProvider dbInfoProvider, Map<String, IConnectionFactory> factories) {
      this.dbInfoProvider = dbInfoProvider;
      this.factories = factories;
   }

   @Override
   public IDatabaseInfo getDefaultDatabaseInfo() throws OseeDataStoreException {
      return dbInfoProvider.getDatabaseInfo();
   }

   @Override
   public BaseOseeConnection getConnection() throws OseeCoreException {
      return getConnection(getDefaultDatabaseInfo());
   }

   @Override
   public BaseOseeConnection getConnection(IDatabaseInfo dbInfo) throws OseeCoreException {
      DataSource dataSource = getDataSource(dbInfo);
      return getOseeConnection(dataSource);
   }

   private DataSource getDataSource(IDatabaseInfo dbInfo) throws OseeCoreException {
      String poolId = dbInfo.getId();
      FutureTask<DataSource> task = dataSourceCache.get(poolId);
      if (task == null) {
         Callable<DataSource> newCallable = new DataSourceFetcher(dbInfo);
         FutureTask<DataSource> newTask = new FutureTask<DataSource>(newCallable);
         task = dataSourceCache.putIfAbsent(poolId, newTask);
         if (task == null) {
            task = newTask;
            newTask.run();
         }
      }

      DataSource dataSource = null;
      try {
         dataSource = task.get();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return dataSource;
   }

   private BaseOseeConnection getOseeConnection(DataSource dataSource) throws OseeCoreException {
      Connection connection = null;
      try {
         connection = dataSource.getConnection();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return new BaseOseeConnection(connection);
   }

   @Override
   public synchronized void dispose() throws OseeCoreException {
      PoolingDriver driver = poolingDriver.get();
      for (String key : dataSourceCache.keySet()) {
         try {
            driver.closePool(key);
         } catch (SQLException ex) {
            // Do Nothing
         }
      }
      poolingDriver.set(null);
      dataSourceCache.clear();
   }

   private final class PoolingDriverRef extends LazyObject<PoolingDriver> {

      @Override
      protected PoolingDriver instance() throws OseeCoreException {
         try {
            Class.forName(CONNECTION_POOL_DRIVER);
         } catch (Exception ex) {
            throw new OseeDataStoreException(ex, "Error loading connection pool driver [%s]", CONNECTION_POOL_DRIVER);
         }
         PoolingDriver driver;
         try {
            driver = (PoolingDriver) DriverManager.getDriver(CONNECTION_POOL_ID);
         } catch (SQLException ex) {
            throw new OseeDataStoreException(ex, "Error finding connection pool driver with id [%s]",
               CONNECTION_POOL_ID);
         }
         return driver;
      }

   }

   private final class DataSourceFetcher implements Callable<DataSource> {

      private final IDatabaseInfo dbInfo;

      public DataSourceFetcher(IDatabaseInfo dbInfo) {
         this.dbInfo = dbInfo;
      }

      @Override
      public DataSource call() throws Exception {
         try {
            Class.forName(CONNECTION_POOL_DRIVER);
         } catch (Exception ex) {
            throw new OseeDataStoreException(ex, "Error loading connection pool driver [%s]", CONNECTION_POOL_DRIVER);
         }

         ConnectionFactory connectionFactory = createConnectionFactory(dbInfo);

         ObjectPool<Connection> connectionPool = new GenericObjectPool<Connection>();

         @SuppressWarnings({"rawtypes", "unchecked"})
         KeyedObjectPoolFactory statementPool = new GenericKeyedObjectPoolFactory(null);
         new PoolableConnectionFactory(connectionFactory, connectionPool, statementPool, null, false, true);
         DataSource dataSource = new PoolingDataSource(connectionPool);

         String poolId = dbInfo.getId();

         PoolingDriver driver = poolingDriver.get();
         driver.registerPool(poolId, connectionPool);
         return dataSource;
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
            throw new SQLException(ex);
         }
         return connection;
      }
   }

}
