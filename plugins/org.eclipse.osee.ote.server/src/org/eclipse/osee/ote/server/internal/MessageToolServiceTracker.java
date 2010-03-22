package org.eclipse.osee.ote.server.internal;

import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class MessageToolServiceTracker extends ServiceTracker{

   public MessageToolServiceTracker() {
      super(Activator.getDefault().getContext(), IRemoteMessageService.class.getName(), null);
   }
   
   public MessageToolServiceTracker(ServiceTrackerCustomizer customizer) {
      super(Activator.getDefault().getContext(), IRemoteMessageService.class.getName(), customizer);
   }

   @Override
   public IRemoteMessageService waitForService(long timeout) throws InterruptedException {
      return (IRemoteMessageService) super.waitForService(timeout);
   }
   
   
}
