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
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.export.ImportBranchJob;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard for importing branches from XML produced by the BranchExporter class.
 * 
 * @author Robert A. Fisher
 */
public class BranchImportWizard extends Wizard implements IImportWizard {
   private static final String TITLE = "Import branches into Skynet";

   private BranchImportPage mainPage;
   private File importFile;
   private Branch destinationBranch;

   public BranchImportWizard() {
      super();
      setDialogSettings(SkynetGuiPlugin.getInstance().getDialogSettings());
      setWindowTitle("Branch Import Wizard");
   }

   public void setImportResourceAndArtifactDestination(File importFile, Branch destinationBranch) {
      if (importFile == null) throw new IllegalArgumentException("importFile can not be null");
      if (destinationBranch == null) throw new IllegalArgumentException("destinationBranch can not be null");

      this.destinationBranch = destinationBranch;
      this.importFile = importFile;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.wizard.Wizard#performFinish()
    */
   @Override
   public boolean performFinish() {
      File file = mainPage.getImportFile();
      Branch branch = mainPage.getSelectedBranch();
      boolean includeMainLevelBranch = mainPage.isIncludeMainLevelBranch();
      boolean includeDescendantBranches = mainPage.isIncludeDescendantBranches();

      Jobs.startJob(new ImportBranchJob(file, branch, includeMainLevelBranch, includeDescendantBranches));
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
    *      org.eclipse.jface.viewers.IStructuredSelection)
    */
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      if (importFile != null) {
         this.mainPage = new BranchImportPage(importFile, destinationBranch);
      } else {
         this.mainPage = new BranchImportPage(selection);
      }

      mainPage.setTitle(TITLE);
      mainPage.setDescription(TITLE);
   }

   /**
    * (non-Javadoc) Method declared on Wizard.
    */
   public void addPages() {
      addPage(mainPage);
   }

   @Override
   public boolean canFinish() {
      return mainPage.isPageComplete();
   }
}
