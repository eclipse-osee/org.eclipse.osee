package org.eclipse.osee.framework.database.internal.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.database.core.IConnectionFactory;
import org.eclipse.osee.framework.database.internal.core.OseeDatabaseServiceImpl.ConnectionPoolProvider;

public final class ConnectionPoolProviderImpl implements ConnectionPoolProvider {

   private final Map<String, OseeConnectionPoolImpl> dbInfoToPools = new HashMap<String, OseeConnectionPoolImpl>();
   private final Map<String, Timer> timers = new HashMap<String, Timer>();

   private final Map<String, IConnectionFactory> factories;

   public ConnectionPoolProviderImpl(Map<String, IConnectionFactory> factories) {
      super();
      this.factories = factories;
   }

   @Override
   public void disposeConnectionPool(IDatabaseInfo databaseInfo) {
      String key = databaseInfo.getId();
      OseeConnectionPoolImpl pool = dbInfoToPools.remove(key);
      if (pool != null) {
         //         pool.
      }
      Timer timer = timers.remove(key);
      if (timer != null) {
         timer.cancel();
      }
   }

   @Override
   public OseeConnectionPoolImpl getConnectionPool(IDatabaseInfo databaseInfo) throws OseeDataStoreException {
      if (databaseInfo == null) {
         throw new OseeDataStoreException("Unable to get connection - database info was null.");
      }
      OseeConnectionPoolImpl pool = dbInfoToPools.get(databaseInfo.getId());
      if (pool == null) {
         String key = databaseInfo.getId();

         pool = createConnectionPool(databaseInfo);
         dbInfoToPools.put(key, pool);

         Timer timer = new Timer();
         timer.schedule(new StaleConnectionCloser(pool), 900000, 900000);
         timers.put(key, timer);
      }
      return pool;
   }

   private OseeConnectionPoolImpl createConnectionPool(IDatabaseInfo databaseInfo) {
      IConnectionFactory factory = getFactory(databaseInfo.getDriver());
      return new OseeConnectionPoolImpl(factory, databaseInfo.getConnectionUrl(),
         databaseInfo.getConnectionProperties());
   }

   public IConnectionFactory getFactory(String driver) {
      IConnectionFactory factory = factories.get(driver);
      if (factory == null) {
         return new DefaultConnectionFactory(driver);
      } else {
         return factory;
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

}