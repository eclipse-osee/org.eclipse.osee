/*
 * Created on Mar 11, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.event;

import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Donald G. Dunne
 */
public class OseeMessagingTracker extends ServiceTracker {

   private ConnectionNode connectionNode;

   public OseeMessagingTracker() {
      super(Activator.getInstance().getBundle().getBundleContext(), MessageService.class.getName(), null);
   }

   @Override
   public Object addingService(ServiceReference reference) {
      MessageService service = (MessageService) super.addingService(reference);
      try {
         connectionNode = service.getDefault();
         CoverageEventManager.getInstance().addingRemoteEventService(connectionNode);
      } catch (OseeCoreException ex) {
         OseeLog.log(OseeMessagingTracker.class, Level.SEVERE, ex);
      }
      return service;
   }

   @Override
   public void removedService(ServiceReference reference, Object service) {
      super.removedService(reference, service);
   }

   @Override
   public MessageService getService() {
      return (MessageService) super.getService();
   }

}
