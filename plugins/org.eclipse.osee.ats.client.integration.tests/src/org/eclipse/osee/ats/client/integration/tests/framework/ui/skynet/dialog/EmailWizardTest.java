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
package org.eclipse.osee.ats.client.integration.tests.framework.ui.skynet.dialog;

import java.util.ArrayList;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.framework.jdk.core.util.EmailGroup;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailWizard;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailWizardPage;
import org.eclipse.ui.PlatformUI;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class EmailWizardTest {

   @Test
   public void test() {
      EmailWizard wizard = new EmailWizard("html", "subject", new ArrayList<EmailGroup>(), new ArrayList<>());
      WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
      try {
         dialog.setBlockOnOpen(false);
         dialog.open();

         EmailWizardPage page1 = (EmailWizardPage) wizard.getPages()[0];
         int count = page1.getNamesList().getViewer().getTree().getItemCount();
         Assert.assertTrue(count >= 10);
      } finally {
         dialog.close();
      }
   }
}
