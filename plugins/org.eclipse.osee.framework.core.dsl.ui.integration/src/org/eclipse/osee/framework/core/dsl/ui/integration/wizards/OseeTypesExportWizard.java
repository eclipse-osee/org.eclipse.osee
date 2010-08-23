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
package org.eclipse.osee.framework.core.dsl.ui.integration.wizards;

import java.io.File;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.core.dsl.ui.integration.operations.OseeTypesExportOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypesExportWizard extends Wizard implements IImportWizard {
   private ResourceSelectionPage mainPage;

   public OseeTypesExportWizard() {
      super();
      // setDialogSettings(Activator.getInstance().getDialogSettings());
      setWindowTitle("OSEE Types Export Wizard");
      setNeedsProgressMonitor(true);
      setHelpAvailable(true);
   }

   @Override
   public boolean performFinish() {
      File folder = mainPage.getFile();
      Operations.executeAsJob(new OseeTypesExportOperation(folder), true);
      return true;
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      mainPage = new ResourceSelectionPage(getWindowTitle());
   }

   @Override
   public void addPages() {
      addPage(mainPage);
   }
}
