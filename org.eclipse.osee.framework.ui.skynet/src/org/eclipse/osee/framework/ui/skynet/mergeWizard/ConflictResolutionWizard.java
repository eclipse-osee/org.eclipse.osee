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
package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;

/**
 * @author Theron Virgin
 */
public class ConflictResolutionWizard extends Wizard {

   /* (non-Javadoc)
    * @see org.eclipse.jface.wizard.Wizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
    */

   private ConflictResolutionWizardPage conflictWizardPage;
   private EditAttributeWizardPage editWizardPage;
   private EditWFCAttributeWizardPage editWFCWizardPage;
   private final AttributeConflict conflict;

   public ConflictResolutionWizard(Conflict conflict) {
      if (conflict instanceof AttributeConflict)
         this.conflict = (AttributeConflict) conflict;
      else
         this.conflict = null;

   }

   @Override
   public void addPages() {
      conflictWizardPage = new ConflictResolutionWizardPage(conflict);
      addPage(conflictWizardPage);
      editWizardPage = new EditAttributeWizardPage(conflict);
      addPage(editWizardPage);
      editWFCWizardPage = new EditWFCAttributeWizardPage(conflict);
      addPage(editWFCWizardPage);
   }

   @Override
   public boolean performFinish() {
      IWizardPage page = getContainer().getCurrentPage();
      if (page.equals(conflictWizardPage)) {
         return conflictWizardPage.closingPage();
      }
      if (page.equals(editWizardPage)) {
         return editWizardPage.closingPage();
      }
      if (page.equals(editWFCWizardPage)) {
         return editWFCWizardPage.closingPage();
      }
      return true;
   }

   @Override
   public boolean canFinish() {
      IWizardPage page = getContainer().getCurrentPage();
      if (page.equals(conflictWizardPage)) {
         return conflictWizardPage.canFinish();
      }
      return true;
   }

   public boolean getResolved() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.wizard.Wizard#getStartingPage()
    */
   @Override
   public IWizardPage getStartingPage() {
      if (conflict.statusUntouched()) {
         return conflictWizardPage;
      } else {
         return conflictWizardPage.getNextPage();
      }
   }

   @Override
   public IWizardPage getPreviousPage(IWizardPage page) {
      if (page.equals(conflictWizardPage)) {
         return null;
      }
      if (page.equals(editWizardPage)) {
         return conflictWizardPage;
      }
      if (page.equals(editWFCWizardPage)) {
         return conflictWizardPage;
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.wizard.Wizard#performCancel()
    */
   @Override
   public boolean performCancel() {
      return super.performCancel();
   }

}
