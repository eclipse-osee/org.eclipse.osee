/*
 * Created on Mar 11, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.res;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Donald G. Dunne
 */
public class ResMessagingTracker extends ServiceTracker {

   private ConnectionNode connectionNode;

   public ResMessagingTracker() {
      super(Activator.getInstance().getBundle().getBundleContext(), MessageService.class.getName(), null);
   }

   @Override
   public Object addingService(ServiceReference reference) {
      MessageService service = (MessageService) super.addingService(reference);
      try {
         connectionNode = service.getDefault();
      } catch (OseeCoreException ex) {
         System.err.println("Can't add RES messaging service " + ex.getLocalizedMessage());
         ex.printStackTrace();
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

   public ConnectionNode getConnectionNode() {
      return connectionNode;
   }

   public void setConnectionNode(ConnectionNode connectionNode) {
      this.connectionNode = connectionNode;
   }

}
