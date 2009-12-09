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
package org.eclipse.osee.framework.branch.management.internal;

import org.eclipse.osee.framework.branch.management.IBranchUpdateEvent;
import org.eclipse.osee.framework.branch.management.cache.BranchUpdateEventImpl;
import org.eclipse.osee.framework.branch.management.cache.DatabaseArtifactTypeAccessor;
import org.eclipse.osee.framework.branch.management.cache.DatabaseAttributeTypeAccessor;
import org.eclipse.osee.framework.branch.management.cache.DatabaseBranchAccessor;
import org.eclipse.osee.framework.branch.management.cache.DatabaseOseeEnumTypeAccessor;
import org.eclipse.osee.framework.branch.management.cache.DatabaseRelationTypeAccessor;
import org.eclipse.osee.framework.branch.management.cache.DatabaseTransactionRecordAccessor;
import org.eclipse.osee.framework.core.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.core.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.OseeCachingService;
import org.eclipse.osee.framework.core.server.IApplicationServerLookupProvider;
import org.eclipse.osee.framework.core.services.IDataTranslationServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public class ServerOseeCachingServiceFactory implements IOseeCachingServiceFactory {

   private final IOseeDatabaseServiceProvider databaseProvider;
   private final IOseeModelFactoryServiceProvider factoryProvider;
   private final IDataTranslationServiceProvider txProvider;
   private final IApplicationServerLookupProvider serverLookUpProvider;

   public ServerOseeCachingServiceFactory(IOseeDatabaseServiceProvider databaseProvider, IOseeModelFactoryServiceProvider factoryProvider, IDataTranslationServiceProvider txProvider, IApplicationServerLookupProvider serverLookUpProvider) {
      this.databaseProvider = databaseProvider;
      this.factoryProvider = factoryProvider;
      this.txProvider = txProvider;
      this.serverLookUpProvider = serverLookUpProvider;
   }

   public IOseeCachingService createCachingService() {
      OseeEnumTypeCache oseeEnumTypeCache =
            new OseeEnumTypeCache(new DatabaseOseeEnumTypeAccessor(databaseProvider, factoryProvider));

      IOseeDataAccessor<AttributeType> attrAccessor =
            new DatabaseAttributeTypeAccessor(databaseProvider, factoryProvider, oseeEnumTypeCache);

      AttributeTypeCache attributeCache = new AttributeTypeCache(attrAccessor);

      TransactionCache txCache = new TransactionCache();
      IBranchUpdateEvent branchEventSender = new BranchUpdateEventImpl(txProvider, serverLookUpProvider);
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
