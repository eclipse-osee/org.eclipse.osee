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
package org.eclipse.osee.framework.ui.skynet.export;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Robert A. Fisher
 */
public class ExportBranchWizard extends Wizard implements IExportWizard {
   private ExportBranchPage mainPage;
   private Branch defaultBranch;

   public ExportBranchWizard() {
      this(null);
   }

   public ExportBranchWizard(Branch defaultBranch) {
      super();
      this.defaultBranch = defaultBranch;

      setWindowTitle("Skynet Branch Export Wizard");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.wizard.Wizard#performFinish()
    */
   @Override
   public boolean performFinish() {
      return mainPage.finish();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
    *      org.eclipse.jface.viewers.IStructuredSelection)
    */
   public void init(IWorkbench workbench, IStructuredSelection selection) {
   }

   /**
    * (non-Javadoc) Method declared on Wizard.
    */
   public void addPages() {
      mainPage = new ExportBranchPage("Export Branch", defaultBranch);
      addPage(mainPage);
   }
}
