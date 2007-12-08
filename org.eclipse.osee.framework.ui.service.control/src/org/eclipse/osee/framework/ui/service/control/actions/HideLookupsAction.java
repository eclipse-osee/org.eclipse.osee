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

/**
 * @author Roberto E. Escobar
 */
public class HideLookupsAction extends Action {

   private ManagerMain mainWindow;
   private static String ACTION_TITLE = "non-member Lookup Servers";

   public HideLookupsAction(ManagerMain mainWindow) {
      super("", Action.AS_CHECK_BOX);
      this.mainWindow = mainWindow;
      setImageDescriptor(ControlPlugin.getInstance().getImageDescriptor("tools.gif"));
      setText("Display " + ACTION_TITLE);
   }

   public void run() {
      mainWindow.getLookupUpdater().filterLookupServers(isChecked());
   }
}
