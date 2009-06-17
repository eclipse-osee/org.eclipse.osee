package org.eclipse.osee.ote.message.internal;

import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.message.tool.AbstractMessageToolService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class MessageWatchActivator extends ServiceTracker{

   private ServiceRegistration registration;

   MessageWatchActivator(BundleContext context) {
      super(context, IMessageManager.class.getName(), null);
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
    */
   @Override
   public IMessageManager addingService(ServiceReference reference) {
      IMessageManager manager = (IMessageManager) super.addingService(reference);
      try {
         AbstractMessageToolService toolService = new AbstractMessageToolService(manager);
         registration = context.registerService(IRemoteMessageService.class.getName(), toolService, null);
      } catch (IOException e) {
         OseeLog.log(MessageWatchActivator.class, Level.SEVERE, "failed to create message tool service", e);
      }
      return manager;
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
    */
   @Override
   public synchronized void removedService(ServiceReference reference, Object service) {
      disposeToolService();
      super.removedService(reference, service);
   }

   private void disposeToolService() {
      try {
         AbstractMessageToolService toolService = (AbstractMessageToolService)context.getService(registration.getReference());
         toolService.terminateService();
      } finally {
         registration.unregister();
         registration = null;
      }
   }
   
   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#close()
    */
   @Override
   public synchronized void close() {
      if (registration != null){
         disposeToolService();
      }
      super.close();
   }
}
