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
package org.eclipse.osee.ote.ui.define.importer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.ui.define.OteDefineImage;
import org.eclipse.osee.ote.ui.define.internal.Activator;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Roberto E. Escobar
 */
public class OutfileImportWizard extends Wizard implements IImportWizard {
   private static final String TITLE = "Import outfiles into Define";
   private static final ImageDescriptor WIZARD_IMAGE = ImageManager.getImageDescriptor(OteDefineImage.COMMIT_WIZ);
   private OutfileImportPage mainPage;

   public OutfileImportWizard() {
      super();
      setDialogSettings(Activator.getInstance().getDialogSettings());
      setWindowTitle("Outfile Import Wizard");
      setDefaultPageImageDescriptor(WIZARD_IMAGE);
   }

   @Override
   public boolean performFinish() {
      return mainPage.finish();
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      this.mainPage = new OutfileImportPage(selection);
      mainPage.setTitle(TITLE);
      mainPage.setDescription("Import artifacts into Define");
      addPage(mainPage);
   }

   @Override
   public IWizardPage getNextPage(IWizardPage page) {
      return null;
   }

   @Override
   public IWizardPage getPreviousPage(IWizardPage page) {
      return null;
   }

   @Override
   public boolean canFinish() {
      return mainPage.isPageComplete();
   }
}
