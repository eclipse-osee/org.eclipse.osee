/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.dialog;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.ide.actions.wizard.NewActionPage1;
import org.eclipse.osee.ats.ide.actions.wizard.NewActionWizard;
import org.eclipse.ui.PlatformUI;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class NewActionWizardTest {

   @Test
   public void testNewActionWizard() {
      NewActionWizard wizard = new NewActionWizard();
      wizard.setInitialDescription("description");
      WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
      try {
         dialog.setBlockOnOpen(false);
         dialog.open();

         NewActionPage1 page1 = (NewActionPage1) wizard.getPages()[0];
         int count = page1.getTreeViewer().getViewer().getTree().getItemCount();
         Assert.assertTrue("Should be >=5 but was " + count, count >= 5);
      } finally {
         dialog.close();
      }
   }
}
