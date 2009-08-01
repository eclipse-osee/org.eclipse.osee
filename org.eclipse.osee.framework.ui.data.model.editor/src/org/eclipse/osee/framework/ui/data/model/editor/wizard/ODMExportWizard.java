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
package org.eclipse.osee.framework.ui.data.model.editor.wizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditorActivator;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.osee.framework.ui.data.model.editor.operation.ODMToXmlOperation;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class ODMExportWizard extends Wizard implements IExportWizard {

   private ODMSelectPage selectTypesPage;
   private ODMExportOutputPage exportOutputPage;
   private DataTypeCache dataTypeCache;

   public ODMExportWizard(DataTypeCache dataTypeCache) {
      setDefaultPageImageDescriptor(ImageManager.getImageDescriptor(ODMImage.EXPORT_IMAGE));
      setNeedsProgressMonitor(true);
      setWindowTitle("Osee Data Model Export Wizard");
      this.dataTypeCache = dataTypeCache;
   }

   @Override
   public void addPages() {
      addPage(selectTypesPage = new ODMSelectPage("Osee Data Type Select", "Select Osee Data Types to export."));
      addPage(exportOutputPage = new ODMExportOutputPage("Osee Data Type Export", "Select export destination."));
      selectTypesPage.setInput(dataTypeCache);
   }

   @Override
   public boolean canFinish() {
      ArtifactDataType[] selectedTypes = selectTypesPage.getSelected();
      return selectedTypes != null && selectedTypes.length > 0 && Strings.isValid(exportOutputPage.getExportToXmlPath());
   }

   @Override
   public boolean performFinish() {
      ArtifactDataType[] selectedTypes = selectTypesPage.getSelected();
      if (selectedTypes != null && selectedTypes.length > 0) {
         IExceptionableRunnable worker = null;
         String jobName = null;
         if (exportOutputPage.isDataStoreExport()) {
            jobName = "Export artifact types into data store";
            worker = createDataStoreExportWorker(selectedTypes);
         } else {
            jobName = "Export artifact types as xml";
            worker = createXmlExportWorker(selectedTypes);
         }
         if (worker != null) {
            Jobs.runInJob(jobName, worker, ODMEditorActivator.class, ODMEditorActivator.PLUGIN_ID, true);
         }
      }
      return true;
   }

   private IExceptionableRunnable createDataStoreExportWorker(final ArtifactDataType[] selectedTypes) {
      final String backupfilePath = exportOutputPage.getExportToDataStoreBackupFilePath();
      final boolean isBackupEnabled = exportOutputPage.isDataStoreBackupOption();
      return new IExceptionableRunnable() {

         @Override
         public IStatus run(IProgressMonitor monitor) throws Exception {
            String extra = " no backup";
            if (isBackupEnabled) {
               extra = " backup " + backupfilePath;
            }
            System.out.println(String.format("Export into data store - %s", extra));
            return Status.OK_STATUS;
         }
      };
   }

   private IExceptionableRunnable createXmlExportWorker(final ArtifactDataType[] selectedTypes) {
      final String filePath = exportOutputPage.getExportToXmlPath();
      final boolean exportToSingleFile = exportOutputPage.isExportToSingleXmlFileSelected();
      return new IExceptionableRunnable() {

         @Override
         public IStatus run(IProgressMonitor monitor) throws Exception {
            ODMToXmlOperation operation = new ODMToXmlOperation(filePath, exportToSingleFile, selectedTypes);
            operation.execute(monitor);
            return Status.OK_STATUS;
         }
      };
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
   }
}
