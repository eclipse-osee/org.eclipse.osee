/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaArtifact;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaAttribute;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaRelation;
import org.eclipse.osee.orcs.db.internal.loader.data.OrcsObjectFactoryImpl;
import org.eclipse.osee.orcs.db.internal.loader.handlers.ArtifactSqlHandler;
import org.eclipse.osee.orcs.db.internal.loader.handlers.AttributeSqlHandler;
import org.eclipse.osee.orcs.db.internal.loader.handlers.RelationSqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactoryImpl;

/**
 * @author Roberto E. Escobar
 */
public class DataModuleFactory {

   private final Log logger;

   private DataFactory dataFactory;
   private DataLoaderFactory dataLoaderFactory;

   public DataModuleFactory(Log logger) {
      super();
      this.logger = logger;
   }

   public void create(IOseeDatabaseService dbService, IdFactory idFactory, IdentityService identityService, SqlProvider sqlProvider, DataProxyFactoryProvider proxyProvider, BranchCache branchCache, ArtifactTypeCache artifactTypeCache, AttributeTypeCache attributeTypeCache) {
      ProxyDataFactory proxyDataFactory = createDataFactory(proxyProvider, attributeTypeCache);
      OrcsObjectFactory rowDataFactory = createOrcsObjectFactory(identityService, proxyDataFactory);

      dataFactory = createDataFactory(rowDataFactory, idFactory, artifactTypeCache);

      SqlHandlerFactory handlerFactory = createHandlerFactory(identityService);
      SqlArtifactLoader loader = createArtifactLoader(dbService, handlerFactory, sqlProvider, rowDataFactory);
      dataLoaderFactory = createDataLoader(dbService, loader, branchCache);
   }

   public void stop() {
      dataLoaderFactory = null;
      dataFactory = null;
   }

   public DataFactory getDataFactory() {
      return dataFactory;
   }

   public DataLoaderFactory getDataLoaderFactory() {
      return dataLoaderFactory;
   }

   protected ProxyDataFactory createDataFactory(DataProxyFactoryProvider proxyProvider, AttributeTypeCache attributeTypeCache) {
      return new AttributeDataProxyFactory(proxyProvider, attributeTypeCache);
   }

   protected OrcsObjectFactory createOrcsObjectFactory(IdentityService identityService, ProxyDataFactory proxyDataFactory) {
      return new OrcsObjectFactoryImpl(proxyDataFactory, identityService);
   }

   protected DataFactory createDataFactory(OrcsObjectFactory factory, IdFactory idFactory, ArtifactTypeCache artifactTypeCache) {
      return new DataFactoryImpl(idFactory, factory, artifactTypeCache);
   }

   protected SqlHandlerFactory createHandlerFactory(IdentityService identityService) {
      Map<Class<? extends Criteria<?>>, Class<? extends SqlHandler<?, ?>>> handleMap =
         new HashMap<Class<? extends Criteria<?>>, Class<? extends SqlHandler<?, ?>>>();

      // Query
      handleMap.put(CriteriaArtifact.class, ArtifactSqlHandler.class);
      handleMap.put(CriteriaAttribute.class, AttributeSqlHandler.class);
      handleMap.put(CriteriaRelation.class, RelationSqlHandler.class);

      return new SqlHandlerFactoryImpl(logger, identityService, handleMap);
   }

   protected SqlArtifactLoader createArtifactLoader(IOseeDatabaseService dbService, SqlHandlerFactory handlerFactory, SqlProvider sqlProvider, OrcsObjectFactory factory) {
      return new SqlArtifactLoader(logger, dbService, sqlProvider, handlerFactory, factory);
   }

   protected DataLoaderFactory createDataLoader(IOseeDatabaseService dbService, SqlArtifactLoader loader, BranchCache branchCache) {
      return new DataLoaderFactoryImpl(logger, dbService, loader, branchCache);
   }
}
