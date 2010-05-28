package org.eclipse.osee.ote.ui;

import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.ui.internal.TestCoreGuiPlugin;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class RemoteConsoleLauncher extends ServiceTracker {

   private OteRemoteConsole remoteConsole;

   public RemoteConsoleLauncher() {
      super(TestCoreGuiPlugin.getDefault().getBundle().getBundleContext(), IOteClientService.class.getName(), null);

   }

   @Override
   public Object addingService(ServiceReference reference) {
      IOteClientService clientService = (IOteClientService) super.addingService(reference);
      remoteConsole = new OteRemoteConsole();
      clientService.addConnectionListener(remoteConsole);
      return clientService;
   }

   @Override
   public void close() {
      if (remoteConsole != null) {
         remoteConsole.close();
      }
      super.close();
   }

   @Override
   public void removedService(ServiceReference reference, Object service) {
      if (remoteConsole != null) {
         remoteConsole.close();
      }
      super.removedService(reference, service);
   }
}
