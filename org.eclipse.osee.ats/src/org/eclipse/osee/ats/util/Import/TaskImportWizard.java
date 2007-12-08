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
package org.eclipse.osee.ats.util.Import;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Donald G. Dunne
 */
public class TaskImportWizard extends Wizard implements IImportWizard {
   private TaskImportPage mainPage;
   private IStructuredSelection selection;
   private String hrid;

   public TaskImportWizard() {
      super();
      setDialogSettings(AtsPlugin.getInstance().getDialogSettings());
      setWindowTitle("Task Import Wizard");
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
      this.selection = selection;
   }

   public void setHrid(String hrid) {
      this.hrid = hrid;
   }

   /**
    * (non-Javadoc) Method declared on Wizard.
    */
   public void addPages() {
      mainPage = new TaskImportPage("Import Tasks into OSEE ATS", selection, hrid);
      addPage(mainPage);
   }

   public String getHrid() {
      return hrid;
   }

}
