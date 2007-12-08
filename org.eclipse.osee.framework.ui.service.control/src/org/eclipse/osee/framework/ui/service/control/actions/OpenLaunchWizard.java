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
package org.eclipse.osee.framework.ui.service.control.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.widgets.ManagerMain;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchWizard;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceWizardDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class OpenLaunchWizard extends Action {

   public OpenLaunchWizard(ManagerMain mainWindow) {
      super();
      setImageDescriptor(ControlPlugin.getInstance().getImageDescriptor("rocket.gif"));
      setText("Launch A Service");
      setToolTipText("Open the service launching wizard.");
   }

   public void run() {
      super.run();
      ServiceLaunchWizard wizard = new ServiceLaunchWizard();
      ServiceWizardDialog dialog = new ServiceWizardDialog(Display.getDefault().getActiveShell(), wizard);
      dialog.setBlockOnOpen(true);
      dialog.open();
   }
}
