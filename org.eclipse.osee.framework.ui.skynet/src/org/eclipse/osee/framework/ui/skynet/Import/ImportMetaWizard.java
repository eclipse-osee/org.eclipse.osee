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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Robert A. Fisher
 * @author Jeff C. Phillips
 */
public class ImportMetaWizard extends Wizard implements IImportWizard {
   private ImportMetaPage mainPage;
   private IStructuredSelection selection;

   /**
    * 
    */
   public ImportMetaWizard() {
      super();
      setWindowTitle("Skynet Types Import Wizard");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.wizard.Wizard#performFinish()
    */
   @Override
   public boolean performFinish() {
      try {
         Branch branch = mainPage.getSelectedBranch();
         File file = mainPage.getImportFile();
         Jobs.startJob(new ImportMetaJob(file, branch));
      } catch (Exception ex) {
         OSEELog.logException(getClass(), "Define Import Error", ex, true);
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
   public void addPages() {
      mainPage = new ImportMetaPage(selection);
      mainPage.setTitle("Import Skynet types into Define");
      mainPage.setDescription("Import Skynet types into Define");

      addPage(mainPage);
   }
}
