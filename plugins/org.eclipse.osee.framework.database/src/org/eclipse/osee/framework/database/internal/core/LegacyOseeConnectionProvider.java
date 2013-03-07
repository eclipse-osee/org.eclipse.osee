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

import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;
import org.eclipse.osee.framework.database.internal.core.OseeDatabaseServiceImpl.ConnectionPoolProvider;

/**
 * @author Roberto E. Escobar
 */
public class LegacyOseeConnectionProvider implements ConnectionProvider {

   private final ConnectionPoolProvider poolProvider;
   private final IDatabaseInfoProvider dbInfoProvider;

   public LegacyOseeConnectionProvider(ConnectionPoolProvider poolProvider, IDatabaseInfoProvider dbInfoProvider) {
      this.poolProvider = poolProvider;
      this.dbInfoProvider = dbInfoProvider;
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
   public BaseOseeConnection getConnection(IDatabaseInfo databaseInfo) throws OseeCoreException {
      OseeConnectionPoolImpl pool = getConnectionPool(databaseInfo);
      return pool.getConnection();
   }

   private OseeConnectionPoolImpl getConnectionPool(IDatabaseInfo databaseInfo) throws OseeDataStoreException {
      return poolProvider.getConnectionPool(databaseInfo);
   }

   @Override
   public void dispose() {
      poolProvider.dispose();
   }

}
