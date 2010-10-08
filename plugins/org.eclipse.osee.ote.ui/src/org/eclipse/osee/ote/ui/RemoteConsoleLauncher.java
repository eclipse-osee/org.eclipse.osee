/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui;

import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.ui.internal.TestCoreGuiPlugin;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class RemoteConsoleLauncher extends ServiceTracker {

   private OteRemoteConsole remoteConsole;

   private final IOteConsoleService oteConsoleService;

   public RemoteConsoleLauncher(IOteConsoleService oteConsoleService) {
      super(TestCoreGuiPlugin.getContext(), IOteClientService.class.getName(), null);
      this.oteConsoleService = oteConsoleService;
   }

   @Override
   public Object addingService(ServiceReference reference) {
      IOteClientService clientService = (IOteClientService) super.addingService(reference);
      remoteConsole = new OteRemoteConsole(oteConsoleService);
      clientService.addConnectionListener(remoteConsole);
      return clientService;
   }

   @Override
   public void close() {
      if (remoteConsole != null) {
         IOteClientService service = (IOteClientService) getService();
         service.removeConnectionListener(remoteConsole);
         remoteConsole.close();
      }
      super.close();
   }

   @Override
   public void removedService(ServiceReference reference, Object service) {
      if (remoteConsole != null) {
         remoteConsole.close();
         remoteConsole = null;
      }
      super.removedService(reference, service);
   }
}
