/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.DataStoreTypeCache;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.ArtifactRowHandler;
import org.eclipse.osee.orcs.core.ds.AttributeRowHandler;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.RelationRowHandler;
import org.eclipse.osee.orcs.db.internal.sql.StaticSqlProvider;

public class DataLoaderImpl implements DataLoader {

   private Log logger;
   private StaticSqlProvider sqlProvider;
   private IOseeDatabaseService oseeDatabaseService;
   private IdentityService identityService;
   private SystemPreferences preferences;
   private DataStoreTypeCache dataStoreTypeCache;
   private DataProxyFactoryProvider dataProxyFactoryProvider;

   private ArtifactLoader artifactLoader;
   private AttributeLoader attributeLoader;
   private RelationLoader relationLoader;

   public void start() {
      sqlProvider = new StaticSqlProvider();
      sqlProvider.setLogger(logger);
      sqlProvider.setPreferences(preferences);
      artifactLoader = new ArtifactLoader(logger, sqlProvider, oseeDatabaseService, identityService);
      AttributeDataProxyFactory attributeDataProxyFactory =
         new AttributeDataProxyFactory(dataProxyFactoryProvider, dataStoreTypeCache.getAttributeTypeCache());
      attributeLoader =
         new AttributeLoader(sqlProvider, oseeDatabaseService, identityService, attributeDataProxyFactory);
      relationLoader = new RelationLoader(sqlProvider, oseeDatabaseService);
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setSystemPreferences(SystemPreferences preferences) {
      this.preferences = preferences;
   }

   public void setOseeDatabaseService(IOseeDatabaseService oseeDatabaseService) {
      this.oseeDatabaseService = oseeDatabaseService;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   public void setDataProxyFactoryProvider(DataProxyFactoryProvider dataProxyFactoryProvider) {
      this.dataProxyFactoryProvider = dataProxyFactoryProvider;
   }

   public void setDataStoreTypeCache(DataStoreTypeCache dataStoreTypeCache) {
      this.dataStoreTypeCache = dataStoreTypeCache;
   }

   @Override
   public void loadArtifacts(ArtifactRowHandler handler, LoadOptions options, int fetchSize, int queryId) throws OseeCoreException {
      artifactLoader.loadFromQueryId(handler, options, fetchSize, queryId);
   }

   @Override
   public void loadAttributes(AttributeRowHandler handler, LoadOptions options, int fetchSize, int queryId) throws OseeCoreException {
      attributeLoader.loadFromQueryId(handler, options, fetchSize, queryId);
   }

   @Override
   public void loadRelations(RelationRowHandler handler, LoadOptions options, int fetchSize, int queryId) throws OseeCoreException {
      relationLoader.loadFromQueryId(handler, options, fetchSize, queryId);
   }

}
