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
package org.eclipse.osee.framework.core.dsl.integration.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.osee.framework.core.dsl.integration.CreateOseeTypeChangesReportOperation;
import org.eclipse.osee.framework.core.dsl.integration.EMFCompareOperation;
import org.eclipse.osee.framework.core.dsl.integration.OseeToXtextOperation;
import org.eclipse.osee.framework.core.dsl.integration.OseeTypeCache;
import org.eclipse.osee.framework.core.dsl.integration.XTextToOseeTypeOperation;
import org.eclipse.osee.framework.core.dsl.integration.util.ModelUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.OseeImportModelRequest;
import org.eclipse.osee.framework.core.model.OseeImportModelResponse;
import org.eclipse.osee.framework.core.model.TableData;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelingService;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public class OseeModelingServiceImpl implements IOseeModelingService {

   private IOseeModelFactoryService modelFactoryService;
   private IOseeCachingService systemCachingService;
   private IOseeCachingServiceFactory cachingFactoryService;
   private OseeDslFactory modelFactory;

   public void setFactoryService(IOseeModelFactoryService modelFactoryService) {
      this.modelFactoryService = modelFactoryService;
   }

   // This needs to be dynamic since there is a cycle
   public void setCacheService(IOseeCachingService systemCachingService) {
      this.systemCachingService = systemCachingService;
   }

   public void unsetCacheService(IOseeCachingService systemCachingService) {
      this.systemCachingService = systemCachingService;
   }

   // This needs to be dynamic since there is a cycle
   public void setCacheFactory(IOseeCachingServiceFactory cachingFactoryService) {
      this.cachingFactoryService = cachingFactoryService;
   }

   public void unsetCacheFactory(IOseeCachingServiceFactory cachingFactoryService) {
      this.cachingFactoryService = cachingFactoryService;
   }

   public void start() {
      modelFactory = OseeDslFactory.eINSTANCE;
   }

   public void stop() {
      modelFactory = null;
   }

   @Override
   public void exportOseeTypes(IProgressMonitor monitor, OutputStream outputStream) throws OseeCoreException {
      OseeTypeCache cache =
         new OseeTypeCache(systemCachingService.getArtifactTypeCache(), systemCachingService.getAttributeTypeCache(),
            systemCachingService.getRelationTypeCache(), systemCachingService.getEnumTypeCache());

      OseeDsl model = modelFactory.createOseeDsl();

      IOperation operation = new OseeToXtextOperation(cache, modelFactory, model);
      Operations.executeWorkAndCheckStatus(operation, monitor);
      try {
         ModelUtil.saveModel(model, "osee:/oseeTypes_" + Lib.getDateTimeString() + ".osee", outputStream, false);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   @Override
   public void importOseeTypes(IProgressMonitor monitor, boolean isInitializing, OseeImportModelRequest request, OseeImportModelResponse response) throws OseeCoreException {
      String modelName = request.getModelName();
      if (!modelName.endsWith(".osee")) {
         modelName += ".osee";
      }
      OseeDsl inputModel = ModelUtil.loadModel("osee:/" + modelName, request.getModel());

      IOseeCachingService tempCacheService = cachingFactoryService.createCachingService();
      OseeTypeCache tempCache =
         new OseeTypeCache(tempCacheService.getArtifactTypeCache(), tempCacheService.getAttributeTypeCache(),
            tempCacheService.getRelationTypeCache(), tempCacheService.getEnumTypeCache());

      List<TableData> reportData = new ArrayList<TableData>();
      ComparisonResourceSnapshot comparisonSnapshot = DiffFactory.eINSTANCE.createComparisonResourceSnapshot();
      OseeDsl baseModel = modelFactory.createOseeDsl();
      OseeDsl modifiedModel = modelFactory.createOseeDsl();

      List<IOperation> ops = new ArrayList<IOperation>();

      if (request.isCreateCompareReport()) {
         ops.add(new OseeToXtextOperation(tempCache, modelFactory, baseModel));
      }

      ops.add(new XTextToOseeTypeOperation(modelFactoryService, tempCache, tempCacheService.getBranchCache(),
         inputModel));
      if (request.isCreateTypeChangeReport()) {
         ops.add(new CreateOseeTypeChangesReportOperation(tempCache, reportData));
      }
      if (request.isCreateCompareReport()) {
         ops.add(new OseeToXtextOperation(tempCache, modelFactory, baseModel));
         ops.add(new EMFCompareOperation(baseModel, modifiedModel, comparisonSnapshot));
      }
      IOperation operation = new CompositeOperation("Import Osee Types", Activator.PLUGIN_ID, ops);
      Operations.executeWorkAndCheckStatus(operation, monitor);

      if (request.isPersistAllowed()) {
         // TODO Make this call transaction based
         tempCache.storeAllModified();
         response.setPersisted(true);
         if (isInitializing) {
            systemCachingService.clearAll();
         }
         systemCachingService.getEnumTypeCache().cacheFrom(tempCache.getEnumTypeCache());
         systemCachingService.getAttributeTypeCache().cacheFrom(tempCache.getAttributeTypeCache());
         systemCachingService.getArtifactTypeCache().cacheFrom(tempCache.getArtifactTypeCache());
         systemCachingService.getRelationTypeCache().cacheFrom(tempCache.getRelationTypeCache());

         systemCachingService.reloadAll();
      } else {
         response.setPersisted(false);
      }
      response.setReportData(reportData);

      if (request.isCreateCompareReport()) {
         response.setComparisonSnapshotModelName("osee_compare.diff");
         String modelString =
            ModelUtil.modelToStringXML(comparisonSnapshot, "osee:/osee_compare.diff",
               Collections.<String, Boolean> emptyMap());
         response.setComparisonSnapshotModel(modelString);
      }
   }
}
