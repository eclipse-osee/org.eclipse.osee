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
   private final Branch defaultBranch;

   public ExportBranchWizard() {
      this(null);
   }

   public ExportBranchWizard(Branch defaultBranch) {
      super();
      this.defaultBranch = defaultBranch;

      setWindowTitle("Skynet Branch Export Wizard");
   }

   @Override
   public boolean performFinish() {
      return mainPage.finish();
   }

   public void init(IWorkbench workbench, IStructuredSelection selection) {
   }

   @Override
   public void addPages() {
      mainPage = new ExportBranchPage("Export Branch", defaultBranch);
      addPage(mainPage);
   }
}
