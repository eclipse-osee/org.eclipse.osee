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

import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.loader.data.OrcsObjectFactoryImpl;

/**
 * @author Roberto E. Escobar
 */
public class DataModuleFactory {

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityService identityService;

   public DataModuleFactory(Log logger, IOseeDatabaseService dbService, IdentityService identityService) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.identityService = identityService;
   }

   public OrcsObjectFactory createOrcsObjectFactory(DataProxyFactoryProvider proxyProvider, AttributeTypeCache attributeTypeCache) {
      AttributeDataProxyFactory dataProxyFactory = new AttributeDataProxyFactory(proxyProvider, attributeTypeCache);
      return new OrcsObjectFactoryImpl(dataProxyFactory, identityService);
   }

   public DataFactory createDataFactory(OrcsObjectFactory factory, BranchCache branchCache, ArtifactTypeCache artifactTypeCache) {
      return new DataFactoryImpl(factory, dbService, branchCache, artifactTypeCache);
   }

   public DataLoader createDataLoader(SqlProvider sqlProvider, OrcsObjectFactory factory) {
      ArtifactLoader artifactLoader = new ArtifactLoader(logger, sqlProvider, dbService, factory);
      AttributeLoader attributeLoader = new AttributeLoader(logger, sqlProvider, dbService, factory);
      RelationLoader relationLoader = new RelationLoader(logger, sqlProvider, dbService, factory);
      return new DataLoaderImpl(dbService, artifactLoader, attributeLoader, relationLoader);
   }
}
