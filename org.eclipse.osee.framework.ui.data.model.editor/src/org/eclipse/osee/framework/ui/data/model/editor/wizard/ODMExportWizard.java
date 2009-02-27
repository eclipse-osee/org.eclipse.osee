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
package org.eclipse.osee.framework.ui.data.model.editor.wizard;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImages;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class ODMExportWizard extends Wizard implements IExportWizard {

   private ISelection selection;
   private ODMSelectTypesPage selectTypesPage;
   private DataTypeCache dataTypeCache;

   public ODMExportWizard(DataTypeCache dataTypeCache) {
      this.dataTypeCache = dataTypeCache;
      setNeedsProgressMonitor(true);
      setWindowTitle("Osee Data Model Export Wizard");
      setDefaultPageImageDescriptor(ODMImages.getImageDescriptor(ODMImages.EXPORT_IMAGE));
   }

   @Override
   public void addPages() {
      addPage(selectTypesPage = new ODMSelectTypesPage("Osee Data Model Wizard"));
      selectTypesPage.setInput(dataTypeCache);
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.wizard.Wizard#performFinish()
    */
   @Override
   public boolean performFinish() {
      IStructuredSelection selection = (IStructuredSelection) selectTypesPage.getTreeViewer().getSelection();

      System.out.println("Finish Selected: " + selection.toList());
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
    */
   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      this.selection = selection;
   }

}
