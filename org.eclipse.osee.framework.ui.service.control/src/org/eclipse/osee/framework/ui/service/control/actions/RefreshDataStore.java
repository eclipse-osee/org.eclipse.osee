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
import org.eclipse.osee.framework.jini.discovery.EclipseJiniClassloader;
import org.eclipse.osee.framework.jini.discovery.ServiceDataStore;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.managers.ReggieCache;
import org.eclipse.osee.framework.ui.service.control.widgets.ManagerMain;

/**
 * @author Roberto E. Escobar
 */
public class RefreshDataStore extends Action {

   private ManagerMain mainWindow;

   public RefreshDataStore(ManagerMain mainWindow) {
      super();
      this.mainWindow = mainWindow;
      this.setText("Refresh");
      this.setToolTipText("Refresh Lookup Servers and Services.\n" + "NOTE: Disconnects from service when connected.");
      this.setImageDescriptor(ControlPlugin.getInstance().getImageDescriptor("refresh.gif"));
   }

   public void run() {
      mainWindow.getLookupUpdater().clear();
      mainWindow.getServicesManager().clear();
      mainWindow.getConnectionManager().forceDisconnect();
      mainWindow.getServicesViewer().refresh();
      mainWindow.getLookupViewer().refresh();
      mainWindow.getQuickViewer().clearTextArea();
      ServiceDataStore.getEclipseInstance(EclipseJiniClassloader.getInstance()).refresh();
      ReggieCache.getEclipseInstance(EclipseJiniClassloader.getInstance()).refresh();
   }

}
