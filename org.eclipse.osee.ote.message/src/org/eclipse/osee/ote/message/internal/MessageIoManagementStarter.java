/*
 * Created on Jun 25, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message.internal;

import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.message.io.IMessageIoManagementService;
import org.eclipse.osee.ote.message.io.MessageIoManagementService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author b1529404
 *
 */
public class MessageIoManagementStarter extends ServiceTracker{

   private ServiceRegistration registration;
   /**
    * @param context
    * @param filter
    * @param customizer
    */
   public MessageIoManagementStarter(BundleContext context) {
      super(context, TestEnvironmentInterface.class.getName(), null);
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
    */
   @Override
   public synchronized TestEnvironmentInterface addingService(ServiceReference reference) {
      TestEnvironmentInterface manager = (TestEnvironmentInterface) super.addingService(reference);
      registration = context.registerService(IMessageIoManagementService.class.getName(), new MessageIoManagementService(), null);
      return manager;
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
    */
   @Override
   public synchronized void removedService(ServiceReference reference, Object service) {
      registration.unregister();
      registration = null;
      super.removedService(reference, service);
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#close()
    */
   @Override
   public synchronized void close() {
      if (registration != null) {
         registration.unregister();
      }
      super.close();
   }
   
   
}
