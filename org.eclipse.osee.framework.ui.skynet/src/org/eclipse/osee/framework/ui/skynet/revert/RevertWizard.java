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
package org.eclipse.osee.framework.ui.skynet.revert;

import java.util.List;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author fw314c
 */
public class RevertWizard extends Wizard {
   private List<List<Artifact>> artifacts;
   private RevertWizardPage revertWizardPage;

   public RevertWizard(List<List<Artifact>> artifacts) {
      this.artifacts = artifacts;
   }

   @Override
   public void addPages() {
      revertWizardPage = new RevertWizardPage(artifacts);
      addPage(revertWizardPage);
   }

   @Override
   public boolean performFinish() {
      return revertWizardPage.closingPage();
   }

   @Override
   public boolean canFinish() {
      return true;
   }

   public boolean getResolved() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.wizard.Wizard#getStartingPage()
    */
   @Override
   public IWizardPage getStartingPage() {
      return getPage(revertWizardPage.TITLE);
   }

   @Override
   public IWizardPage getPreviousPage(IWizardPage page) {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.wizard.Wizard#performCancel()
    */
   @Override
   public boolean performCancel() {
      return super.performCancel();
   }

}
