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
package org.eclipse.osee.jdbc.internal;

import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javax.sql.DataSource;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.jdbc.JdbcPoolConfig;

/**
 * @author Roberto E. Escobar
 */
public class PoolFactory {

   private final PoolingDriverRef poolingDriver = new PoolingDriverRef();
   private final JdbcConnectionFactoryManager manager;
   private final JdbcPoolConfig poolConfiguration;

   public PoolFactory(JdbcConnectionFactoryManager manager, JdbcPoolConfig poolConfiguration) {
      this.manager = manager;
      this.poolConfiguration = poolConfiguration;
   }

   public void disposePools(Iterable<String> keys) {
      PoolingDriver driver = poolingDriver.get();
      for (String key : keys) {
         try {
            driver.closePool(key);
         } catch (SQLException ex) {
            // Do Nothing
         }
      }
      poolingDriver.invalidate();
   }

   public Map<String, String> getPoolStats() {
      Map<String, String> stats = new LinkedHashMap<>();

      PoolingDriver driver = poolingDriver.get();

      stats.put("db.pool.driver", poolConfiguration.getPoolConnectionDriver());

      String poolVersion = String.format("%s.%s", driver.getMajorVersion(), driver.getMinorVersion());
      stats.put("db.pool.version", poolVersion);

      String[] names = driver.getPoolNames();
      int count = 0;
      for (String name : names) {

         try {
            ObjectPool<?> pool = driver.getConnectionPool(name);
            stats.put(String.format("db.pool.%s.id", count), name);
            stats.put(String.format("db.pool.%s.active", count), String.valueOf(pool.getNumActive()));
            stats.put(String.format("db.pool.%s.idle", count), String.valueOf(pool.getNumIdle()));
         } catch (SQLException ex) {
            // Do Nothing
         } finally {
            count++;
         }
      }
      return stats;
   }

   public Callable<DataSource> createDataSourceFetcher(JdbcConnectionInfo dbInfo) {
      return new PooledDataSourceFetcher(manager, poolingDriver, poolConfiguration, dbInfo);
   }

   private final class PoolingDriverRef extends LazyObject<PoolingDriver> {

      @Override
      protected final FutureTask<PoolingDriver> createLoaderTask() {
         Callable<PoolingDriver> callable = new Callable<PoolingDriver>() {
            @Override
            public PoolingDriver call() throws Exception {
               String connectionPoolDriver = poolConfiguration.getPoolConnectionDriver();
               String connectionPoolId = poolConfiguration.getPoolConnectionId();
               try {
                  Class.forName(connectionPoolDriver);
               } catch (Exception ex) {
                  throw newJdbcException(ex, "Error loading connection pool driver [%s]", connectionPoolDriver);
               }
               PoolingDriver driver;
               try {
                  driver = (PoolingDriver) DriverManager.getDriver(connectionPoolId);
               } catch (SQLException ex) {
                  throw newJdbcException(ex, "Error finding connection pool driver with id [%s]", connectionPoolId);
               }
               return driver;
            }
         };
         return new FutureTask<>(callable);
      }
   }
}
