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
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import javax.sql.DataSource;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.data.LazyObject;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

public class PooledConnectionProvider implements ConnectionProvider {

   private final ConcurrentHashMap<String, LazyObject<DataSource>> dataSourceCache =
      new ConcurrentHashMap<String, LazyObject<DataSource>>();

   private final PoolFactory poolFactory;
   private final IDatabaseInfoProvider dbInfoProvider;

   public PooledConnectionProvider(IDatabaseInfoProvider dbInfoProvider, PoolFactory poolFactory) {
      this.dbInfoProvider = dbInfoProvider;
      this.poolFactory = poolFactory;
   }

   @Override
   public IDatabaseInfo getDefaultDatabaseInfo() throws OseeCoreException {
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

   private DataSource getDataSource(final IDatabaseInfo dbInfo) throws OseeCoreException {
      String poolId = dbInfo.getId();
      LazyObject<DataSource> provider = dataSourceCache.get(poolId);
      if (provider == null) {
         LazyObject<DataSource> newProvider = new LazyObject<DataSource>() {
            @Override
            protected FutureTask<DataSource> createLoaderTask() {
               Callable<DataSource> newCallable = poolFactory.createDataSourceFetcher(dbInfo);
               return new FutureTask<DataSource>(newCallable);
            }
         };
         provider = dataSourceCache.putIfAbsent(poolId, newProvider);
         if (provider == null) {
            provider = newProvider;
         }
      }
      return provider.get();
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
   public void dispose() throws OseeCoreException {
      synchronized (poolFactory) {
         poolFactory.disposePools(dataSourceCache.keySet());
         dataSourceCache.clear();
      }
   }

   @Override
   public Map<String, String> getStatistics() throws OseeCoreException {
      synchronized (poolFactory) {
         return poolFactory.getPoolStats();
      }
   }

}
