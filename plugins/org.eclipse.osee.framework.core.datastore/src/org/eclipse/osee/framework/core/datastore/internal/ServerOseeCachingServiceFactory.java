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
import org.eclipse.osee.framework.core.datastore.cache.DatabaseBranchAccessor;
import org.eclipse.osee.framework.core.datastore.cache.DatabaseTransactionRecordAccessor;
import org.eclipse.osee.framework.core.datastore.cache.IBranchUpdateEvent;
import org.eclipse.osee.framework.core.model.OseeCachingService;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;

/**
 * @author Roberto E. Escobar
 */
public class ServerOseeCachingServiceFactory implements IOseeCachingServiceFactory {

   private final IOseeDatabaseService databaseService;
   private final IOseeModelFactoryService factoryService;
   private final IDataTranslationService translationService;
   private final IApplicationServerLookup serverLookUp;
   private final IApplicationServerManager appManager;
   private final ModelingServiceProvider modelingService;
   private final IResourceLocatorManager locatorManager;
   private final IResourceManager resourceManager;
   private final IdentityService identityService;

   public ServerOseeCachingServiceFactory(IOseeDatabaseService databaseService, IOseeModelFactoryService factoryService, IDataTranslationService translationService, IApplicationServerLookup serverLookUp, IApplicationServerManager appManager, ModelingServiceProvider modelingService, IResourceLocatorManager locatorManager, IResourceManager resourceManager, IdentityService identityService) {
      this.databaseService = databaseService;
      this.factoryService = factoryService;
      this.translationService = translationService;
      this.serverLookUp = serverLookUp;
      this.appManager = appManager;
      this.modelingService = modelingService;
      this.locatorManager = locatorManager;
      this.resourceManager = resourceManager;
      this.identityService = identityService;
   }

   @Override
   public IOseeCachingService createCachingService() {
      TransactionCache txCache = new TransactionCache();
      IBranchUpdateEvent branchEventSender = new BranchUpdateEventImpl(translationService, appManager, serverLookUp);
      BranchCache branchCache =
         new BranchCache(new DatabaseBranchAccessor(databaseService, branchEventSender, txCache,
            factoryService.getBranchFactory()));
      txCache.setAccessor(new DatabaseTransactionRecordAccessor(databaseService, branchCache,
         factoryService.getTransactionFactory()));

      OseeEnumTypeCache oseeEnumTypeCache =
         new OseeEnumTypeCache(new ArtifactTypeDataAccessor<OseeEnumType>(modelingService, databaseService,
            locatorManager, resourceManager, branchCache, identityService));
      AttributeTypeCache attributeCache =
         new AttributeTypeCache(new ArtifactTypeDataAccessor<AttributeType>(modelingService, databaseService,
            locatorManager, resourceManager, branchCache, identityService));
      ArtifactTypeCache artifactCache =
         new ArtifactTypeCache(new ArtifactTypeDataAccessor<ArtifactType>(modelingService, databaseService,
            locatorManager, resourceManager, branchCache, identityService));
      RelationTypeCache relationCache =
         new RelationTypeCache(new ArtifactTypeDataAccessor<RelationType>(modelingService, databaseService,
            locatorManager, resourceManager, branchCache, identityService));

      return new OseeCachingService(branchCache, txCache, artifactCache, attributeCache, relationCache,
         oseeEnumTypeCache);
   }
}
