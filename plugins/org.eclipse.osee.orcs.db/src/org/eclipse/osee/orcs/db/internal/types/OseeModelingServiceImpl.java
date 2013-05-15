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
package org.eclipse.osee.orcs.db.internal.types;

import java.io.OutputStream;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.dsl.OseeDslResource;
import org.eclipse.osee.framework.core.dsl.OseeDslResourceUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.OseeCachingService;
import org.eclipse.osee.framework.core.model.OseeImportModelRequest;
import org.eclipse.osee.framework.core.model.OseeImportModelResponse;
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
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.accessor.ArtifactTypeDataAccessor;
import org.eclipse.osee.orcs.db.internal.accessor.DatabaseBranchAccessor;
import org.eclipse.osee.orcs.db.internal.accessor.DatabaseTransactionRecordAccessor;
import org.eclipse.osee.orcs.db.internal.accessor.TypeLoaderImpl;

/**
 * @author Roberto E. Escobar
 */
public class OseeModelingServiceImpl implements IOseeModelingService {

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityService identityService;
   private final ExecutorAdmin executorAdmin;
   private final IResourceManager resourceManager;
   private final IOseeModelFactoryService modelFactoryService;
   private final IOseeCachingService caches;
   private final EventService eventService;

   public OseeModelingServiceImpl(Log logger, IOseeDatabaseService dbService, IdentityService identityService, ExecutorAdmin executorAdmin, IResourceManager resourceManager, IOseeModelFactoryService modelFactoryService, EventService eventService, IOseeCachingService caches) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.identityService = identityService;
      this.executorAdmin = executorAdmin;
      this.resourceManager = resourceManager;
      this.modelFactoryService = modelFactoryService;
      this.caches = caches;
      this.eventService = eventService;
   }

   @Override
   public void exportOseeTypes(OutputStream outputStream) throws OseeCoreException {
      OseeTypeCache cache =
         new OseeTypeCache(caches.getArtifactTypeCache(), caches.getAttributeTypeCache(),
            caches.getRelationTypeCache(), caches.getEnumTypeCache());

      OseeDslFactory modelFactory = OseeDslFactory.eINSTANCE;
      OseeDsl model = modelFactory.createOseeDsl();

      IOperation operation = new OseeToXtextOperation(cache, modelFactory, model);
      Operations.executeWorkAndCheckStatus(operation, null);
      try {
         OseeDslResourceUtil.saveModel(model, "osee:/oseeTypes_" + Lib.getDateTimeString() + ".osee", outputStream,
            false);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   @Override
   public void importOseeTypes(boolean isInitializing, OseeImportModelRequest request, OseeImportModelResponse response) throws OseeCoreException {
      String modelName = request.getModelName();
      if (!modelName.endsWith(".osee")) {
         modelName += ".osee";
      }

      OseeDsl inputModel = null;
      try {
         OseeDslResource dslResource = OseeDslResourceUtil.loadModel("osee:/" + modelName, request.getModel());
         inputModel = dslResource.getModel();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      IOseeCachingService tempCacheService = createCachingService(false);
      OseeTypeCache tempCache =
         new OseeTypeCache(tempCacheService.getArtifactTypeCache(), tempCacheService.getAttributeTypeCache(),
            tempCacheService.getRelationTypeCache(), tempCacheService.getEnumTypeCache());

      IOperation operation =
         new XTextToOseeTypeOperation(modelFactoryService, tempCache, tempCacheService.getBranchCache(), inputModel);
      Operations.executeWorkAndCheckStatus(operation);

      if (request.isPersistAllowed()) {
         tempCache.storeAllModified();
         response.setPersisted(true);
         if (isInitializing) {
            caches.clearAll();
         }
         caches.getEnumTypeCache().cacheFrom(tempCache.getEnumTypeCache());
         caches.getAttributeTypeCache().cacheFrom(tempCache.getAttributeTypeCache());
         caches.getArtifactTypeCache().cacheFrom(tempCache.getArtifactTypeCache());
         caches.getRelationTypeCache().cacheFrom(tempCache.getRelationTypeCache());

         caches.reloadAll();
      } else {
         response.setPersisted(false);
      }
   }

   public IOseeCachingService createCachingService(boolean needsPriming) {
      TransactionCache txCache = new TransactionCache();
      BranchCache branchCache =
         new BranchCache(new DatabaseBranchAccessor(logger, executorAdmin, eventService, dbService, txCache,
            modelFactoryService.getBranchFactory()));
      txCache.setAccessor(new DatabaseTransactionRecordAccessor(dbService, branchCache,
         modelFactoryService.getTransactionFactory()));

      TypeLoaderImpl loader =
         new TypeLoaderImpl(this, identityService, dbService, resourceManager, branchCache, needsPriming);

      boolean typesSynchronized = false;
      OseeEnumTypeCache oseeEnumTypeCache =
         new OseeEnumTypeCache(new ArtifactTypeDataAccessor<OseeEnumType>(identityService, loader));
      oseeEnumTypeCache.setSynchronizedEnsurePopulate(typesSynchronized);

      AttributeTypeCache attributeCache =
         new AttributeTypeCache(new ArtifactTypeDataAccessor<AttributeType>(identityService, loader));
      attributeCache.setSynchronizedEnsurePopulate(typesSynchronized);

      ArtifactTypeCache artifactCache =
         new ArtifactTypeCache(new ArtifactTypeDataAccessor<ArtifactType>(identityService, loader));
      artifactCache.setSynchronizedEnsurePopulate(typesSynchronized);

      RelationTypeCache relationCache =
         new RelationTypeCache(new ArtifactTypeDataAccessor<RelationType>(identityService, loader));
      relationCache.setSynchronizedEnsurePopulate(typesSynchronized);

      return new OseeCachingService(branchCache, txCache, artifactCache, attributeCache, relationCache,
         oseeEnumTypeCache, identityService);
   }
}
