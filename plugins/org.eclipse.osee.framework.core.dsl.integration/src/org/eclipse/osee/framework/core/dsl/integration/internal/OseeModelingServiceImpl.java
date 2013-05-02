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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.osee.framework.core.dsl.OseeDslResourceUtil;
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
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public class OseeModelingServiceImpl implements IOseeModelingService {

   private IOseeModelFactoryService modelFactoryService;

   private OseeDslFactory modelFactory;

   public void setFactoryService(IOseeModelFactoryService modelFactoryService) {
      this.modelFactoryService = modelFactoryService;
   }

   public void start() {
      modelFactory = OseeDslFactory.eINSTANCE;
   }

   public void stop() {
      modelFactory = null;
   }

   private static BundleContext getBundleContext() throws OseeCoreException {
      Bundle bundle = FrameworkUtil.getBundle(OseeModelingServiceImpl.class);
      Conditions.checkNotNull(bundle, "bundle");
      return bundle.getBundleContext();
   }

   private static <T> T getService(Class<T> clazz) throws OseeCoreException {
      BundleContext context = getBundleContext();
      Conditions.checkNotNull(context, "bundleContext");
      ServiceReference<T> reference = context.getServiceReference(clazz);
      Conditions.checkNotNull(reference, "serviceReference");
      T service = context.getService(reference);
      Conditions.checkNotNull(service, "service");
      return service;
   }

   @Override
   public void exportOseeTypes(IProgressMonitor monitor, OutputStream outputStream) throws OseeCoreException {
      IOseeCachingService caches = getService(IOseeCachingService.class);
      OseeTypeCache cache =
         new OseeTypeCache(caches.getArtifactTypeCache(), caches.getAttributeTypeCache(),
            caches.getRelationTypeCache(), caches.getEnumTypeCache());

      OseeDsl model = modelFactory.createOseeDsl();

      IOperation operation = new OseeToXtextOperation(cache, modelFactory, model);
      Operations.executeWorkAndCheckStatus(operation, monitor);
      try {
         OseeDslResourceUtil.saveModel(model, "osee:/oseeTypes_" + Lib.getDateTimeString() + ".osee", outputStream,
            false);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   @Override
   public void importOseeTypes(IProgressMonitor monitor, boolean isInitializing, OseeImportModelRequest request, OseeImportModelResponse response) throws OseeCoreException {
      String modelName = request.getModelName();
      if (!modelName.endsWith(".osee")) {
         modelName += ".osee";
      }
      OseeDsl inputModel = null;
      try {
         inputModel = OseeDslResourceUtil.loadModel("osee:/" + modelName, request.getModel()).getModel();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      IOseeCachingService tempCacheService = getService(IOseeCachingServiceFactory.class).createCachingService(false);
      OseeTypeCache tempCache =
         new OseeTypeCache(tempCacheService.getArtifactTypeCache(), tempCacheService.getAttributeTypeCache(),
            tempCacheService.getRelationTypeCache(), tempCacheService.getEnumTypeCache());

      List<TableData> reportData = new ArrayList<TableData>();
      ComparisonResourceSnapshot comparisonSnapshot = DiffFactory.eINSTANCE.createComparisonResourceSnapshot();
      OseeDsl baseModel = modelFactory.createOseeDsl();
      OseeDsl modifiedModel = modelFactory.createOseeDsl();

      List<IOperation> ops = new ArrayList<IOperation>();

      ops.add(new XTextToOseeTypeOperation(modelFactoryService, tempCache, tempCacheService.getBranchCache(),
         inputModel));
      if (request.isCreateTypeChangeReport()) {
         ops.add(new CreateOseeTypeChangesReportOperation(tempCache, reportData));
      }
      if (request.isCreateCompareReport()) {
         ops.add(new OseeToXtextOperation(tempCache, modelFactory, baseModel));
         ops.add(new EMFCompareOperation(baseModel, modifiedModel, comparisonSnapshot));
      }
      IOperation operation = new CompositeOperation("Import Osee Types", DslIntegrationConstants.PLUGIN_ID, ops);
      Operations.executeWorkAndCheckStatus(operation, monitor);

      if (request.isPersistAllowed()) {
         IOseeCachingService caches = getService(IOseeCachingService.class);

         // TODO Make this call transaction based
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
