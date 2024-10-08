/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.handlers.LoaderSqlHandlerFactoryUtil;
import org.eclipse.osee.orcs.db.internal.loader.processor.DynamicLoadProcessor;
import org.eclipse.osee.orcs.db.internal.proxy.AttributeDataProxyFactory;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class LoaderModule {
   private final Log logger;
   private final JdbcClient jdbcClient;
   private final IdentityManager idFactory;
   private final SqlJoinFactory joinFactory;
   private final IResourceManager resourceManager;

   public LoaderModule(Log logger, JdbcClient jdbcClient, IdentityManager idFactory, SqlJoinFactory joinFactory, IResourceManager resourceManager) {
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.idFactory = idFactory;
      this.joinFactory = joinFactory;
      this.resourceManager = resourceManager;
   }

   public AttributeDataProxyFactory createProxyDataFactory() {
      return new AttributeDataProxyFactory(resourceManager, logger);
   }

   public DataFactory createDataFactory(OrcsObjectFactory factory) {
      return new DataFactoryImpl(idFactory, factory);
   }

   public DynamicLoadProcessor createDynamicLoadProcessor(OrcsTokenService tokenService,
      AttributeDataProxyFactory proxyFactory) {
      return new DynamicLoadProcessor(logger, tokenService, proxyFactory);
   }

   public DataLoaderFactory createDataLoaderFactory(SqlObjectLoader sqlObjectLoader) {
      return new DataLoaderFactoryImpl(logger, jdbcClient, sqlObjectLoader, joinFactory);
   }

   public SqlObjectLoader createSqlObjectLoader(OrcsObjectFactory objectFactory,
      DynamicLoadProcessor dynamicLoadProcessor, OrcsTokenService tokenService) {
      SqlHandlerFactory handlerFactory = LoaderSqlHandlerFactoryUtil.createHandlerFactory();
      return new SqlObjectLoader(logger, jdbcClient, joinFactory, handlerFactory, objectFactory, dynamicLoadProcessor,
         tokenService);
   }
}
