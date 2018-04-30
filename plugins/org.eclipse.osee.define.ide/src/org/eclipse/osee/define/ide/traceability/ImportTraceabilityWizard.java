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
package org.eclipse.osee.define.ide.traceability;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.define.ide.internal.Activator;
import org.eclipse.osee.define.ide.traceability.TraceUnitExtensionManager.TraceHandler;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Ryan D. Brooks
 */
public class ImportTraceabilityWizard extends Wizard implements IImportWizard {
   private ImportTraceabilityPage mainPage;
   private IStructuredSelection selection;

   public ImportTraceabilityWizard() {
      super();
      setWindowTitle("Traceability Import Wizard");
   }

   @Override
   public boolean performFinish() {
      try {
         BranchId branch = mainPage.getSelectedBranch();
         File file = mainPage.getImportFile();
         boolean isGitBased = mainPage.isGitBased();
         boolean includeImpd = mainPage.includeImpd();

         Collection<TraceHandler> handlers = new LinkedList<>();
         for (String handlerId : mainPage.getTraceUnitHandlerIds()) {
            handlers.add(TraceUnitExtensionManager.getInstance().getTraceUnitHandlerById(handlerId));
         }
         Operations.executeWorkAndCheckStatus(new ScriptTraceabilityOperation(file, branch, true, handlers, isGitBased,
            ArtifactId.SENTINEL, includeImpd));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Traceability Import Error", ex);
      }
      return true;
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      this.selection = selection;
   }

   @Override
   public void addPages() {
      mainPage = new ImportTraceabilityPage(selection);
      addPage(mainPage);
   }
}
