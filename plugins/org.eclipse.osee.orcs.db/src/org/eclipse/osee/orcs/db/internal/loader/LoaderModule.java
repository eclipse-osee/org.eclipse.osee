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
package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.loader.data.OrcsObjectFactoryImpl;
import org.eclipse.osee.orcs.db.internal.loader.handlers.LoaderSqlHandlerFactoryUtil;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;

/**
 * @author Roberto E. Escobar
 */
public class LoaderModule {

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityManager idFactory;
   private final SqlProvider sqlProvider;
   private final DataProxyFactoryProvider proxyProvider;

   public LoaderModule(Log logger, IOseeDatabaseService dbService, IdentityManager idFactory, SqlProvider sqlProvider, DataProxyFactoryProvider proxyProvider) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.idFactory = idFactory;
      this.sqlProvider = sqlProvider;
      this.proxyProvider = proxyProvider;
   }

   public OrcsObjectFactory createOrcsObjectFactory(AttributeTypes attributeTypes) {
      ProxyDataFactory proxyFactory = new AttributeDataProxyFactory(proxyProvider, attributeTypes);
      return new OrcsObjectFactoryImpl(proxyFactory);
   }

   public DataFactory createDataFactory(OrcsObjectFactory factory, ArtifactTypes artifactTypes) {
      return new DataFactoryImpl(idFactory, factory, artifactTypes);
   }

   public DataLoaderFactory createDataLoaderFactory(OrcsObjectFactory objectFactory, BranchCache branchCache) {
      SqlObjectLoader createSqlObjectLoader = createSqlObjectLoader(objectFactory);
      return createDataLoaderFactory(createSqlObjectLoader, branchCache);
   }

   public DataLoaderFactory createDataLoaderFactory(SqlObjectLoader sqlObjectLoader, BranchCache branchCache) {
      return new DataLoaderFactoryImpl(logger, dbService, sqlObjectLoader, branchCache);
   }

   protected SqlObjectLoader createSqlObjectLoader(OrcsObjectFactory objectFactory) {
      SqlHandlerFactory handlerFactory = LoaderSqlHandlerFactoryUtil.createHandlerFactory(logger, idFactory);
      return new SqlObjectLoader(logger, dbService, sqlProvider, handlerFactory, objectFactory);
   }

}
