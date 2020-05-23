/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.swt;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;

public abstract class DynamicWizardPage extends WizardPage {

   private String previousPage;
   private String nextPage;

   protected DynamicWizardPage(String pageName, String previousPage, String nextPage) {
      super(pageName);
      this.previousPage = previousPage;
      this.nextPage = nextPage;
   }

   public boolean onNextPressed() {
      return true;
   }

   @Override
   public IWizardPage getNextPage() {
      return this.getWizard().getPage(nextPage);
   }

   @Override
   public IWizardPage getPreviousPage() {
      return this.getWizard().getPage(previousPage);
   }

   public void setNextPage(String nextPage) {
      this.nextPage = nextPage;
   }

   public void setPreviousPage(String previousPage) {
      this.previousPage = previousPage;
   }
}
