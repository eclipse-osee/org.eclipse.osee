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
package org.eclipse.osee.define.traceability.importer;

import java.net.URI;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.define.traceability.jobs.ImportTraceUnitsJob;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class ImportTraceUnitWizard extends Wizard implements IImportWizard {
   private ImportTraceUnitPage page;
   private IStructuredSelection selection;

   public ImportTraceUnitWizard() {
      super();
      setDialogSettings(DefinePlugin.getInstance().getDialogSettings());
      setWindowTitle("Import Trace Units Wizard");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.wizard.Wizard#performFinish()
    */
   @Override
   public boolean performFinish() {
      try {
         Branch importToBranch = page.getSelectedBranch();
         boolean isRecursive = page.isFolderRecursionAllowed();
         boolean isPersistChanges = page.isArtifactPersistanceAllowed();
         URI source = page.getSourceURI();
         String[] traceUnitHandlerIds = page.getTraceUnitHandlerIds();
         boolean fileWithMultiPaths = page.isFileContainingMultiplePaths();

         Job job =
               new ImportTraceUnitsJob("Import Trace Units", importToBranch, source, isRecursive, isPersistChanges,
                     fileWithMultiPaths, traceUnitHandlerIds);
         Jobs.startJob(job, true);
         page.saveWidgetValues();
      } catch (Exception ex) {
         OseeLog.log(DefinePlugin.class, OseeLevel.SEVERE_POPUP, "Import Trace Unit Error", ex);
      }
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
    *      org.eclipse.jface.viewers.IStructuredSelection)
    */
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      this.selection = selection;
   }

   /**
    * (non-Javadoc) Method declared on Wizard.
    */
   @Override
   public void addPages() {
      page = new ImportTraceUnitPage(selection);
      addPage(page);
   }
}
