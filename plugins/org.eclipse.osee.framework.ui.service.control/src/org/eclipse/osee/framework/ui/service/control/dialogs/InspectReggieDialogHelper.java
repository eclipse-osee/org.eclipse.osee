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
package org.eclipse.osee.framework.ui.service.control.dialogs;

import net.jini.core.lookup.ServiceRegistrar;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.ui.service.control.renderer.ReggieItemHandler;
import org.eclipse.osee.framework.ui.service.control.widgets.ManagerMain;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Roberto E. Escobar
 */
public class InspectReggieDialogHelper implements Runnable {

   private int result;
   private Shell shell;
   private ServiceRegistrar reggie;
   private ReggieItemHandler reggieParser;

   public InspectReggieDialogHelper(ManagerMain mainWindow, ServiceRegistrar reggie) {
      this.shell = mainWindow.getShell();
      this.reggie = reggie;
      this.reggieParser = new ReggieItemHandler(reggie);
   }

   public void run() {
      InspectReggieDialog dlg =
            new InspectReggieDialog(shell, reggie, reggieParser, "Inspect Lookup Server", null, String.format(
                  "Services Registered on %s:%s", reggieParser.getHost(), reggieParser.getPort()),
                  MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);

      result = dlg.open();
      if (result == Window.OK) {
      }
   }

   public int getResult() {
      return result;
   }
}
