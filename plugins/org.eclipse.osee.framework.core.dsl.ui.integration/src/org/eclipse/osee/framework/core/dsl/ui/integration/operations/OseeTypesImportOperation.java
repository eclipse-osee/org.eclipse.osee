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
package org.eclipse.osee.framework.core.dsl.ui.integration.operations;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.compare.diff.metamodel.ComparisonSnapshot;
import org.eclipse.emf.compare.ui.editor.ModelCompareEditorInput;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.dsl.ui.integration.internal.DslUiIntegrationConstants;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.OseeImportModelRequest;
import org.eclipse.osee.framework.core.model.OseeImportModelResponse;
import org.eclipse.osee.framework.core.model.TableData;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypesImportOperation extends AbstractOperation {
   private final IOseeCachingService cacheService;
   private final URI model;
   private final boolean isPersistAllowed;
   private final boolean createTypeChangeReport;
   private final boolean createCompareReport;

   public OseeTypesImportOperation(IOseeCachingService cacheService, URI model, boolean createTypeChangeReport, boolean createCompareReport, boolean isPersistAllowed) {
      super("Import Osee Types Model", DslUiIntegrationConstants.PLUGIN_ID);
      this.cacheService = cacheService;
      this.model = model;
      this.isPersistAllowed = isPersistAllowed;
      this.createCompareReport = createCompareReport;
      this.createTypeChangeReport = createTypeChangeReport;
   }

   private String getModel(URL url) throws IOException {
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(url.openStream());
         return Lib.inputStreamToString(inputStream);
      } finally {
         Lib.close(inputStream);
      }
   }

   private String getName(URI uri) {
      String name = uri.toASCIIString();
      int index = name.lastIndexOf("/");
      if (index > 0) {
         name = name.substring(index + 1, name.length());
      }
      return name;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Map<String, String> parameters = new HashMap<String, String>();

      OseeImportModelRequest modelRequest =
         new OseeImportModelRequest(getName(model), getModel(model.toURL()), createTypeChangeReport,
            createCompareReport, isPersistAllowed);

      OseeImportModelResponse response =
         HttpClientMessage.send(OseeServerContext.OSEE_MODEL_CONTEXT, parameters,
            CoreTranslatorId.OSEE_IMPORT_MODEL_REQUEST, modelRequest, CoreTranslatorId.OSEE_IMPORT_MODEL_RESPONSE);

      if (response.wasPersisted()) {
         cacheService.getEnumTypeCache().reloadCache();
         cacheService.getAttributeTypeCache().reloadCache();
         cacheService.getArtifactTypeCache().reloadCache();
         cacheService.getRelationTypeCache().reloadCache();
      }

      if (createTypeChangeReport) {
         openTabReport(response.getReportData());
      }

      if (createCompareReport) {
         String compareName = response.getComparisonSnapshotModelName();
         String compareData = response.getComparisonSnapshotModel();
         if (Strings.isValid(compareData) && Strings.isValid(compareName)) {
            ComparisonSnapshot snapshot = loadComparisonSnapshot(compareName, compareData);
            openCompareEditor(snapshot);
         }
      }
   }

   private static ComparisonSnapshot loadComparisonSnapshot(String compareName, String compareData) throws OseeCoreException {
      ComparisonSnapshot snapshot = null;
      try {
         ResourceSet resourceSet = new ResourceSetImpl();
         Resource resource = resourceSet.createResource(org.eclipse.emf.common.util.URI.createURI(compareName));
         resource.load(new ByteArrayInputStream(compareData.getBytes("UTF-8")), resourceSet.getLoadOptions());
         snapshot = (ComparisonSnapshot) resource.getContents().get(0);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return snapshot;
   }

   private void openCompareEditor(final ComparisonSnapshot snapshot) {
      Job job = new UIJob("Open Compare") {

         @Override
         public IStatus runInUIThread(IProgressMonitor monitor) {
            IStatus status;
            try {
               CompareEditorInput input = new ModelCompareEditorInput(snapshot);
               IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
               page.openEditor(input, "org.eclipse.compare.CompareEditor", true);
               status = Status.OK_STATUS;
            } catch (Exception ex) {
               status =
                  new Status(IStatus.ERROR, DslUiIntegrationConstants.PLUGIN_ID, "Error opening compare editor", ex);
            }
            return status;
         }
      };
      Jobs.startJob(job);
   }

   private void openTabReport(List<TableData> tableData) {
      Operations.executeAsJob(new CreateEditorReportOperation("Un-Persisted Osee Types", tableData), true);
   }
}
