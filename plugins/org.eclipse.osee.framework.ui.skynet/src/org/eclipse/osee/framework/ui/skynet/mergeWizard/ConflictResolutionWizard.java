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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.XMergeLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Theron Virgin
 */
public class ConflictResolutionWizard extends Wizard {
   public static final String TITLE = "How would you like to resolve this conflict?";
   public static final String INDENT = "     ";
   public static final String SOURCE_TITLE = "Source Value:";
   public static final String DEST_TITLE = "Destination value:";
   public static final String ART_TEXT = "Artifact: ";
   public static final String TYPE_TEXT = "Attribute type: ";

   private WizardPage editWizardPage;
   private final AttributeConflict conflict;

   public ConflictResolutionWizard(Conflict conflict) {
      if (conflict instanceof AttributeConflict) {
         this.conflict = (AttributeConflict) conflict;
      } else {
         this.conflict = null;
      }

   }

   @Override
   public void addPages() {
      if (conflict.isWordAttribute()) {
         editWizardPage = new EditWFCAttributeWizardPage(conflict);
      } else {
         editWizardPage = new EditAttributeWizardPage(conflict);
      }
      addPage(editWizardPage);
   }

   @Override
   public boolean performFinish() {
      IWizardPage page = getContainer().getCurrentPage();
      if (page instanceof EditAttributeWizardPage) {
         return ((EditAttributeWizardPage) page).closingPage();
      } else if (page instanceof EditWFCAttributeWizardPage) {
         return ((EditWFCAttributeWizardPage) page).closingPage();
      }
      return true;
   }

   @Override
   public boolean canFinish() {
      return true;
   }

   public boolean getResolved() {
      return true;
   }

   @Override
   public IWizardPage getStartingPage() {
      if (conflict.isWordAttribute()) {
         return getPage(EditWFCAttributeWizardPage.TITLE);
      } else {
         return getPage(EditAttributeWizardPage.TITLE);
      }

   }

   @Override
   public IWizardPage getPreviousPage(IWizardPage page) {
      return null;
   }

   @Override
   public boolean performCancel() {
      return super.performCancel();
   }

   public void setResolution()  {
      if (getContainer() != null) {
         IWizardPage page = getContainer().getCurrentPage();
         Image image = XMergeLabelProvider.getMergeImage(conflict);
         if (page instanceof EditAttributeWizardPage) {
            ((EditAttributeWizardPage) page).setResolution(image);
         } else if (page instanceof EditWFCAttributeWizardPage) {
            ((EditWFCAttributeWizardPage) page).setResolution(image);
         }
      }

   }

}
