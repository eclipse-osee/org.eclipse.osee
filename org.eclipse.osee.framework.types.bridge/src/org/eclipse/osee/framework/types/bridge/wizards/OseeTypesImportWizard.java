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
package org.eclipse.osee.framework.types.bridge.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeManager;
import org.eclipse.osee.framework.types.bridge.internal.Activator;
import org.eclipse.osee.framework.types.bridge.operations.CompareOseeTypeCacheOperation;
import org.eclipse.osee.framework.types.bridge.operations.ReportDirtyOseeTypesOperation;
import org.eclipse.osee.framework.types.bridge.operations.XTextToOseeTypeOperation;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypesImportWizard extends Wizard implements IImportWizard {
   private OseeTypesImportPage mainPage;
   private IStructuredSelection selection;

   public OseeTypesImportWizard() {
      super();
      setDialogSettings(Activator.getDefault().getDialogSettings());
      setWindowTitle("OSEE Types Import Wizard");
      setNeedsProgressMonitor(true);
      setHelpAvailable(true);
   }

   @Override
   public boolean performFinish() {
      final File file = mainPage.getTypesToImport();
      final boolean isPersistAllowed = mainPage.isPersistAllowed();
      final boolean isReport = mainPage.isReportChanges();
      final boolean useCompareEditor = mainPage.useCompareEditor();
      OseeTypeCache cache = OseeTypeManager.getCache();

      List<IOperation> ops = new ArrayList<IOperation>();
      ops.add(new XTextToOseeTypeOperation(cache, isPersistAllowed, null, file.toURI()));
      if (isReport) {
         ops.add(new ReportDirtyOseeTypesOperation(cache));
      }
      if (useCompareEditor) {
         ops.add(new CompareOseeTypeCacheOperation(cache));
      }
      IOperation operation = new CompositeOperation("Import Osee Types", Activator.PLUGIN_ID, ops);

      Job job = Operations.executeAsJob(operation, true);
      job.addJobChangeListener(new JobChangeAdapter() {
         @Override
         public void done(IJobChangeEvent event) {
            file.delete();
         }
      });
      return true;
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      this.selection = selection;
   }

   @Override
   public void addPages() {
      mainPage = new OseeTypesImportPage(selection, getWindowTitle());
      addPage(mainPage);
   }
}
