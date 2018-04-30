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
package org.eclipse.osee.define.ide.traceability.importer;

import java.net.URI;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.define.ide.internal.Activator;
import org.eclipse.osee.define.ide.traceability.operations.ImportTraceUnitsOperation;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
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
      setWindowTitle("Import Trace Units Wizard");
   }

   @Override
   public boolean performFinish() {
      try {
         BranchId importToBranch = page.getSelectedBranch();
         boolean isRecursive = page.isFolderRecursionAllowed();
         boolean isPersistChanges = page.isArtifactPersistanceAllowed();
         Iterable<URI> sources = page.getSourceURI();
         String[] traceUnitHandlerIds = page.getTraceUnitHandlerIds();
         boolean fileWithMultiPaths = page.isFileContainingMultiplePaths();
         boolean addGuidToSourceFile = page.isAddGuidToSourceFileAllowed();
         boolean includeImpd = page.isImpdIncluded();
         IOperation op = new ImportTraceUnitsOperation("Import Trace Units", importToBranch, sources, isRecursive,
            isPersistChanges, fileWithMultiPaths, addGuidToSourceFile, includeImpd, traceUnitHandlerIds);
         Operations.executeAsJob(op, true);
         page.saveWidgetValues();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Import Trace Unit Error", ex);
      }
      return true;
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      this.selection = selection;
   }

   @Override
   public void addPages() {
      page = new ImportTraceUnitPage(selection);
      addPage(page);
   }
}
