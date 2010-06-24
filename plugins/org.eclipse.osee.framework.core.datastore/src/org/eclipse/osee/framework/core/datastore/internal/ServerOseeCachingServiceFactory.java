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
package org.eclipse.osee.framework.core.datastore.internal;

import org.eclipse.osee.framework.core.datastore.cache.BranchUpdateEventImpl;
import org.eclipse.osee.framework.core.datastore.cache.DatabaseArtifactTypeAccessor;
import org.eclipse.osee.framework.core.datastore.cache.DatabaseAttributeTypeAccessor;
import org.eclipse.osee.framework.core.datastore.cache.DatabaseBranchAccessor;
import org.eclipse.osee.framework.core.datastore.cache.DatabaseOseeEnumTypeAccessor;
import org.eclipse.osee.framework.core.datastore.cache.DatabaseRelationTypeAccessor;
import org.eclipse.osee.framework.core.datastore.cache.DatabaseTransactionRecordAccessor;
import org.eclipse.osee.framework.core.datastore.cache.IBranchUpdateEvent;
import org.eclipse.osee.framework.core.model.OseeCachingService;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.core.model.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.server.IApplicationServerLookupProvider;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.translation.IDataTranslationServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public class ServerOseeCachingServiceFactory implements IOseeCachingServiceFactory {

   private final IOseeDatabaseServiceProvider databaseProvider;
   private final IOseeModelFactoryServiceProvider factoryProvider;
   private final IDataTranslationServiceProvider txProvider;
   private final IApplicationServerLookupProvider serverLookUpProvider;
   private final IApplicationServerManager appManager;

   public ServerOseeCachingServiceFactory(IOseeDatabaseServiceProvider databaseProvider, IOseeModelFactoryServiceProvider factoryProvider, IDataTranslationServiceProvider txProvider, IApplicationServerLookupProvider serverLookUpProvider, IApplicationServerManager appManager) {
      this.databaseProvider = databaseProvider;
      this.factoryProvider = factoryProvider;
      this.txProvider = txProvider;
      this.serverLookUpProvider = serverLookUpProvider;
      this.appManager = appManager;
   }

   public IOseeCachingService createCachingService() {
      OseeEnumTypeCache oseeEnumTypeCache =
            new OseeEnumTypeCache(new DatabaseOseeEnumTypeAccessor(databaseProvider, factoryProvider));

      IOseeDataAccessor<AttributeType> attrAccessor =
            new DatabaseAttributeTypeAccessor(databaseProvider, factoryProvider, oseeEnumTypeCache);

      AttributeTypeCache attributeCache = new AttributeTypeCache(attrAccessor);

      TransactionCache txCache = new TransactionCache();
      IBranchUpdateEvent branchEventSender = new BranchUpdateEventImpl(txProvider, appManager, serverLookUpProvider);
      BranchCache branchCache =
            new BranchCache(new DatabaseBranchAccessor(databaseProvider, factoryProvider, branchEventSender, txCache));
      txCache.setAccessor(new DatabaseTransactionRecordAccessor(databaseProvider, factoryProvider, branchCache));

      ArtifactTypeCache artifactCache =
            new ArtifactTypeCache(new DatabaseArtifactTypeAccessor(databaseProvider, factoryProvider, branchCache,
                  attributeCache));

      RelationTypeCache relationCache =
            new RelationTypeCache(new DatabaseRelationTypeAccessor(databaseProvider, factoryProvider, artifactCache));

      return new OseeCachingService(branchCache, txCache, artifactCache, attributeCache, relationCache,
            oseeEnumTypeCache);
   }
}
