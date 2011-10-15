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
package org.eclipse.osee.orcs.db.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.osee.event.EventService;
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
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.accessor.ArtifactTypeDataAccessor;
import org.eclipse.osee.orcs.db.internal.accessor.DatabaseBranchAccessor;
import org.eclipse.osee.orcs.db.internal.accessor.DatabaseTransactionRecordAccessor;

/**
 * @author Roberto E. Escobar
 */
public class CacheFactoryService implements IOseeCachingServiceFactory {

   private IOseeDatabaseService dbService;
   private IOseeModelFactoryService factoryService;
   private IOseeModelingService modelingService;
   private IdentityService identityService;
   private Log logger;
   private EventService eventService;

   private ExecutorService executor;

   public void start() {
      // TODO: Pass in executor admin service
      executor = Executors.newFixedThreadPool(5);
   }

   public void stop() {
      executor.shutdownNow();
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   public void setFactoryService(IOseeModelFactoryService factoryService) {
      this.factoryService = factoryService;
   }

   public void setModelService(IOseeModelingService modelingService) {
      this.modelingService = modelingService;
   }

   public void setEventService(EventService eventService) {
      this.eventService = eventService;
   }

   @Override
   public IOseeCachingService createCachingService() {
      TransactionCache txCache = new TransactionCache();
      BranchCache branchCache =
         new BranchCache(new DatabaseBranchAccessor(logger, executor, eventService, dbService, txCache,
            factoryService.getBranchFactory()));
      txCache.setAccessor(new DatabaseTransactionRecordAccessor(dbService, branchCache,
         factoryService.getTransactionFactory()));

      OseeEnumTypeCache oseeEnumTypeCache =
         new OseeEnumTypeCache(new ArtifactTypeDataAccessor<OseeEnumType>(modelingService, identityService));
      AttributeTypeCache attributeCache =
         new AttributeTypeCache(new ArtifactTypeDataAccessor<AttributeType>(modelingService, identityService));
      ArtifactTypeCache artifactCache =
         new ArtifactTypeCache(new ArtifactTypeDataAccessor<ArtifactType>(modelingService, identityService));
      RelationTypeCache relationCache =
         new RelationTypeCache(new ArtifactTypeDataAccessor<RelationType>(modelingService, identityService));

      return new OseeCachingService(branchCache, txCache, artifactCache, attributeCache, relationCache,
         oseeEnumTypeCache, identityService);
   }
}
