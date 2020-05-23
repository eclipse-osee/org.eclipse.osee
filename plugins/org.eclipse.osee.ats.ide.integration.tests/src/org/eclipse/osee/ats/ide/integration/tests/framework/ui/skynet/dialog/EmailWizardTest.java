/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.framework.ui.skynet.dialog;

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
