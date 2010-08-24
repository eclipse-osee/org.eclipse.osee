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
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;

/**
 * @author Roberto E. Escobar
 */
public class ServerOseeCachingServiceFactory implements IOseeCachingServiceFactory {

   private final IOseeDatabaseService databaseService;
   private final IOseeModelFactoryService factoryService;
   private final IDataTranslationService translationService;
   private final IApplicationServerLookup serverLookUp;
   private final IApplicationServerManager appManager;

   public ServerOseeCachingServiceFactory(IOseeDatabaseService databaseService, IOseeModelFactoryService factoryService, IDataTranslationService translationService, IApplicationServerLookup serverLookUp, IApplicationServerManager appManager) {
      this.databaseService = databaseService;
      this.factoryService = factoryService;
      this.translationService = translationService;
      this.serverLookUp = serverLookUp;
      this.appManager = appManager;
   }

   @Override
   public IOseeCachingService createCachingService() {
      OseeEnumTypeCache oseeEnumTypeCache =
         new OseeEnumTypeCache(new DatabaseOseeEnumTypeAccessor(databaseService,
            factoryService.getOseeEnumTypeFactory()));

      IOseeDataAccessor<AttributeType> attrAccessor =
         new DatabaseAttributeTypeAccessor(databaseService, oseeEnumTypeCache, factoryService.getAttributeTypeFactory());

      AttributeTypeCache attributeCache = new AttributeTypeCache(attrAccessor);

      TransactionCache txCache = new TransactionCache();
      IBranchUpdateEvent branchEventSender = new BranchUpdateEventImpl(translationService, appManager, serverLookUp);
      BranchCache branchCache =
         new BranchCache(new DatabaseBranchAccessor(databaseService, branchEventSender, txCache,
            factoryService.getBranchFactory()));
      txCache.setAccessor(new DatabaseTransactionRecordAccessor(databaseService, branchCache,
         factoryService.getTransactionFactory()));

      ArtifactTypeCache artifactCache =
         new ArtifactTypeCache(new DatabaseArtifactTypeAccessor(databaseService, branchCache, attributeCache,
            factoryService.getArtifactTypeFactory()));

      RelationTypeCache relationCache =
         new RelationTypeCache(new DatabaseRelationTypeAccessor(databaseService, artifactCache,
            factoryService.getRelationTypeFactory()));

      return new OseeCachingService(branchCache, txCache, artifactCache, attributeCache, relationCache,
         oseeEnumTypeCache);
   }
}
