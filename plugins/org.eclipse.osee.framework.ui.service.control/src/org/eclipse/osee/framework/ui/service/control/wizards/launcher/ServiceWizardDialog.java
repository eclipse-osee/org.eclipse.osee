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
package org.eclipse.osee.framework.ui.service.control.wizards.launcher;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.framework.ui.swt.DynamicWizardPage;
import org.eclipse.swt.widgets.Shell;

public class ServiceWizardDialog extends WizardDialog {

   public ServiceWizardDialog(Shell parentShell, IWizard newWizard) {
      super(parentShell, newWizard);
   }

   @Override
   protected void nextPressed() {
      if (((DynamicWizardPage) this.getCurrentPage()).onNextPressed()) {
         super.nextPressed();
      }
   }
}
