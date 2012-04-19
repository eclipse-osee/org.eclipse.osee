/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.internal.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.DatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IConnectionFactory;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;
import org.eclipse.osee.framework.database.core.IOseeSequence;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.internal.DatabaseHelper;
import org.eclipse.osee.framework.database.internal.core.OseeDatabaseServiceImpl.ConnectionPoolProvider;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class OseeDatabaseServiceProxy implements IOseeDatabaseService {

   private final Map<String, IConnectionFactory> factories = new ConcurrentHashMap<String, IConnectionFactory>();

   private ConnectionPoolProvider poolProvider;
   private DatabaseService databaseService;
   private IOseeSequence oseeSequence;

   private IDatabaseInfoProvider databaseInfoProvider;

   public void setDatabaseInfoProvider(IDatabaseInfoProvider databaseInfoProvider) {
      this.databaseInfoProvider = databaseInfoProvider;
   }

   public void addConnectionFactory(IConnectionFactory connectionFactory) {
      factories.put(connectionFactory.getDriver(), connectionFactory);
   }

   public void removeConnectionFactory(IConnectionFactory connectionFactory) {
      factories.remove(connectionFactory.getDriver());
   }

   public void start() {
      poolProvider = new ConnectionPoolProviderImpl(factories);
      databaseService = new OseeDatabaseServiceImpl(poolProvider, databaseInfoProvider);
      oseeSequence = new OseeSequenceImpl(databaseService);
   }

   public void stop() {
      oseeSequence = null;
      databaseService = null;

      if (databaseInfoProvider != null && poolProvider != null) {
         IDatabaseInfo databaseInfo = null;
         try {
            databaseInfo = databaseInfoProvider.getDatabaseInfo();
         } catch (OseeDataStoreException ex) {
            OseeLog.log(DatabaseHelper.class, Level.WARNING, ex);
         }
         if (databaseInfo != null) {
            poolProvider.disposeConnectionPool(databaseInfo);
         }
      }
      poolProvider = null;
   }

   private DatabaseService getDatabaseService() {
      return databaseService;
   }

   private void checkInitialized() throws OseeDataStoreException {
      if (databaseService == null) {
         throw new OseeDataStoreException("Error initializing database service");
      }
   }

   @Override
   public IOseeSequence getSequence() throws OseeDataStoreException {
      if (oseeSequence == null) {
         throw new OseeDataStoreException("Error initializing database service with osee sequence");
      }
      return oseeSequence;
   }

   @Override
   public IOseeStatement getStatement() throws OseeDataStoreException {
      checkInitialized();
      return getDatabaseService().getStatement();
   }

   @Override
   public IOseeStatement getStatement(OseeConnection connection) throws OseeDataStoreException {
      checkInitialized();
      return getDatabaseService().getStatement(connection);
   }

   @Override
   public IOseeStatement getStatement(OseeConnection connection, boolean autoClose) throws OseeDataStoreException {
      checkInitialized();
      return getDatabaseService().getStatement(connection, autoClose);
   }

   @Override
   public IOseeStatement getStatement(int resultSetType, int resultSetConcurrency) throws OseeDataStoreException {
      checkInitialized();
      return getDatabaseService().getStatement(resultSetType, resultSetConcurrency);
   }

   @Override
   public OseeConnection getConnection() throws OseeCoreException {
      checkInitialized();
      return getDatabaseService().getConnection();
   }

   @Override
   public OseeConnection getConnection(IDatabaseInfo info) throws OseeCoreException {
      checkInitialized();
      return getDatabaseService().getConnection(info);
   }

   @Override
   public <O> int runBatchUpdate(String query, List<O[]> dataList) throws OseeCoreException {
      checkInitialized();
      return getDatabaseService().runBatchUpdate(query, dataList);
   }

   @Override
   public <O> int runPreparedUpdate(String query, O... data) throws OseeCoreException {
      checkInitialized();
      return getDatabaseService().runPreparedUpdate(query, data);
   }

   @Override
   public <O> int runBatchUpdate(OseeConnection connection, String query, List<O[]> dataList) throws OseeCoreException {
      checkInitialized();
      return getDatabaseService().runBatchUpdate(connection, query, dataList);
   }

   @Override
   public <O> int runPreparedUpdate(OseeConnection connection, String query, O... data) throws OseeCoreException {
      checkInitialized();
      return getDatabaseService().runPreparedUpdate(connection, query, data);
   }

   @Override
   public <T, O> T runPreparedQueryFetchObject(T defaultValue, String query, O... data) throws OseeCoreException {
      checkInitialized();
      return getDatabaseService().runPreparedQueryFetchObject(defaultValue, query, data);
   }

   @Override
   public <T, O> T runPreparedQueryFetchObject(OseeConnection connection, T defaultValue, String query, O... data) throws OseeCoreException {
      checkInitialized();
      return getDatabaseService().runPreparedQueryFetchObject(connection, defaultValue, query, data);
   }

   @Override
   public boolean isProduction() throws OseeCoreException {
      checkInitialized();
      return getDatabaseService().isProduction();
   }
}
