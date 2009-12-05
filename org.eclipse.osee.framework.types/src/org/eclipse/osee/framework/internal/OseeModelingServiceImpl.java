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
package org.eclipse.osee.framework.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.osee.framework.core.data.OseeImportModelRequest;
import org.eclipse.osee.framework.core.data.OseeImportModelResponse;
import org.eclipse.osee.framework.core.data.TableData;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactoryProvider;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.osee.CreateOseeTypeChangesReportOperation;
import org.eclipse.osee.framework.osee.EMFCompareOperation;
import org.eclipse.osee.framework.osee.ModelUtil;
import org.eclipse.osee.framework.osee.OseeToXtextOperation;
import org.eclipse.osee.framework.osee.OseeTypeCache;
import org.eclipse.osee.framework.osee.XTextToOseeTypeOperation;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.oseeTypes.OseeTypesFactory;
import org.eclipse.osee.framework.services.IOseeModelingService;

/**
 * @author Roberto E. Escobar
 */
public class OseeModelingServiceImpl implements IOseeModelingService {

   private final IOseeModelFactoryServiceProvider provider;
   private final IOseeCachingServiceProvider cachingProvider;
   private final IOseeCachingServiceFactoryProvider cacheFactoryProvider;
   private final OseeTypesFactory modelFactory;

   public OseeModelingServiceImpl(IOseeModelFactoryServiceProvider provider, IOseeCachingServiceProvider cachingProvider, IOseeCachingServiceFactoryProvider cacheFactoryProvider) {
      this.provider = provider;
      this.cacheFactoryProvider = cacheFactoryProvider;
      this.cachingProvider = cachingProvider;
      this.modelFactory = OseeTypesFactory.eINSTANCE;
   }

   @Override
   public void exportOseeTypes(IProgressMonitor monitor, OutputStream outputStream) throws OseeCoreException {
      IOseeCachingService provider = cachingProvider.getOseeCachingService();
      OseeTypeCache cache =
            new OseeTypeCache(provider.getArtifactTypeCache(), provider.getAttributeTypeCache(),
                  provider.getRelationTypeCache(), provider.getEnumTypeCache());

      OseeTypeModel model = modelFactory.createOseeTypeModel();

      IOperation operation = new OseeToXtextOperation(cache, modelFactory, model);
      Operations.executeWorkAndCheckStatus(operation, monitor, -1);
      try {
         ModelUtil.saveModel(model, "osee:/oseeTypes_" + Lib.getDateTimeString() + ".osee", outputStream, false);
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public void importOseeTypes(IProgressMonitor monitor, boolean isInitializing, OseeImportModelRequest request, OseeImportModelResponse response) throws OseeCoreException {
      String modelName = request.getModelName();
      if (!modelName.endsWith(".osee")) {
         modelName += ".osee";
      }
      OseeTypeModel inputModel = ModelUtil.loadModel("osee:/" + modelName, request.getModel());

      IOseeCachingService newCacheService = cacheFactoryProvider.getFactory().createCachingService();
      OseeTypeCache cache =
            new OseeTypeCache(newCacheService.getArtifactTypeCache(), newCacheService.getAttributeTypeCache(),
                  newCacheService.getRelationTypeCache(), newCacheService.getEnumTypeCache());

      List<TableData> reportData = new ArrayList<TableData>();
      ComparisonResourceSnapshot comparisonSnapshot = DiffFactory.eINSTANCE.createComparisonResourceSnapshot();
      OseeTypeModel baseModel = modelFactory.createOseeTypeModel();
      OseeTypeModel modifiedModel = modelFactory.createOseeTypeModel();

      List<IOperation> ops = new ArrayList<IOperation>();

      if (request.isCreateCompareReport()) {
         ops.add(new OseeToXtextOperation(cache, modelFactory, baseModel));
      }

      ops.add(new XTextToOseeTypeOperation(provider.getOseeFactoryService(), cache, newCacheService.getBranchCache(),
            inputModel));
      if (request.isCreateTypeChangeReport()) {
         ops.add(new CreateOseeTypeChangesReportOperation(cache, reportData));
      }
      if (request.isCreateCompareReport()) {
         ops.add(new OseeToXtextOperation(cache, modelFactory, baseModel));
         ops.add(new EMFCompareOperation(baseModel, modifiedModel, comparisonSnapshot));
      }
      IOperation operation = new CompositeOperation("Import Osee Types", InternalTypesActivator.PLUGIN_ID, ops);
      Operations.executeWorkAndCheckStatus(operation, monitor, -1);

      if (request.isPersistAllowed()) {
         cache.storeAllModified();
         response.setPersisted(true);
         IOseeCachingService systemCachingService = cachingProvider.getOseeCachingService();
         if (isInitializing) {
            systemCachingService.clearAll();
         }
         systemCachingService.reloadAll();
      } else {
         response.setPersisted(false);
      }
      response.setReportData(reportData);

      if (request.isCreateCompareReport()) {
         response.setComparisonSnapshotModelName("osee_compare.diff");
         String modelString =
               ModelUtil.modelToString(comparisonSnapshot, "osee:/osee_compare.diff",
               Collections.<String, Boolean> emptyMap());
         response.setComparisonSnapshotModel(modelString);
      }
   }
   //      Map<String, OseeTypeModel> changedModels = new HashMap<String, OseeTypeModel>();
   //      doSubWork(new OseeToXtextOperation(modifiedCache, changedModels), monitor, 0.20);
   //
   //      OseeTypeCache storeCache = createEmptyCache();
   //      storeCache.ensurePopulated();
   //      Map<String, OseeTypeModel> baseModels = new HashMap<String, OseeTypeModel>();
   //      doSubWork(new OseeToXtextOperation(storeCache, baseModels), monitor, 0.20);
   //
   //      OseeTypeModel changedModel = null;
   //      OseeTypeModel baseModel = null;
   //      for (String key : changedModels.keySet()) {
   //         changedModel = changedModels.get(key);
   //         baseModel = baseModels.get(key);
   //      }
   //   }
}
