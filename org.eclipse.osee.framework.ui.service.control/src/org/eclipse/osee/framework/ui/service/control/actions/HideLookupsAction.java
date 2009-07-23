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
import org.eclipse.osee.framework.ui.service.control.ServiceControlImage;
import org.eclipse.osee.framework.ui.service.control.widgets.ManagerMain;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Roberto E. Escobar
 */
public class HideLookupsAction extends Action {

   private ManagerMain mainWindow;
   private static String ACTION_TITLE = "non-member Lookup Servers";

   public HideLookupsAction(ManagerMain mainWindow) {
      super("", Action.AS_CHECK_BOX);
      this.mainWindow = mainWindow;
      setImageDescriptor(ImageManager.getImageDescriptor(ServiceControlImage.TOOLS));
      setText("Display " + ACTION_TITLE);
   }

   public void run() {
      mainWindow.getLookupUpdater().filterLookupServers(isChecked());
   }
}
