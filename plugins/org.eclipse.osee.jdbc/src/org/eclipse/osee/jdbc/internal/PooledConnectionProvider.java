/*********************************************************************
 * Copyright (c) 2011 Boeing
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
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import javax.sql.DataSource;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.jdbc.JdbcException;

/**
 * @author Roberto E. Escobar
 */
public class PooledConnectionProvider implements JdbcConnectionProvider {

   private final ConcurrentHashMap<String, LazyObject<DataSource>> dataSourceCache = new ConcurrentHashMap<>();

   private final PoolFactory poolFactory;

   public PooledConnectionProvider(PoolFactory poolFactory) {
      this.poolFactory = poolFactory;
   }

   @Override
   public JdbcConnectionImpl getConnection(JdbcConnectionInfo dbInfo) throws JdbcException {
      try {
         return new JdbcConnectionImpl(getDataSource(dbInfo).getConnection());
      } catch (Exception ex) {
         throw newJdbcException(ex);
      }
   }

   private DataSource getDataSource(final JdbcConnectionInfo dbInfo) {
      String poolId = dbInfo.getId();
      LazyObject<DataSource> provider = dataSourceCache.get(poolId);
      if (provider == null) {
         LazyObject<DataSource> newProvider = new LazyObject<DataSource>() {
            @Override
            protected FutureTask<DataSource> createLoaderTask() {
               Callable<DataSource> newCallable = poolFactory.createDataSourceFetcher(dbInfo);
               return new FutureTask<>(newCallable);
            }
         };
         provider = dataSourceCache.putIfAbsent(poolId, newProvider);
         if (provider == null) {
            provider = newProvider;
         }
      }
      return provider.get();
   }

   @Override
   public void dispose() {
      synchronized (poolFactory) {
         try {
            poolFactory.disposePools(dataSourceCache.keySet());
         } catch (Exception ex) {
            // Do nothing
         }
         dataSourceCache.clear();
      }
   }

   @Override
   public Map<String, String> getStatistics() {
      synchronized (poolFactory) {
         return poolFactory.getPoolStats();
      }
   }

}
