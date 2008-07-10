package org.eclipse.osee.ote.connection.jini;

import net.jini.core.lookup.ServiceItem;

/**
 * @author b1529404
 */
public class JiniClientSideConnector extends JiniConnector {
   public static final String TYPE = "jini.client-end";
   private final ServiceItem serviceItem;
   private volatile boolean serviceStopped = false;

   JiniClientSideConnector(ServiceItem serviceItem) {
      super();
      this.serviceItem = serviceItem;
      buildPropertiesFromEntries(serviceItem.attributeSets, getProperties());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#getService()
    */
   @Override
   public Object getService() {
      if (!isServiceStopped()) {
         return serviceItem.service;
      } else {
         throw new IllegalStateException("the service has been stopped");
      }
   }

   /**
    * @return the serviceStopped
    */
   boolean isServiceStopped() {
      return serviceStopped;
   }

   /**
    * @param serviceStopped the serviceStopped to set
    */
   void setServiceStopped(boolean serviceStopped) {
      this.serviceStopped = serviceStopped;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#getType()
    */
   @Override
   public String getConnectorType() {
      return TYPE;
   }

}
