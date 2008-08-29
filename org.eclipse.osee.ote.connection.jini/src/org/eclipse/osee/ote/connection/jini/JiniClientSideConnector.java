package org.eclipse.osee.ote.connection.jini;

import java.io.File;
import java.net.URI;
import java.rmi.RemoteException;

import net.jini.core.lookup.ServiceItem;

/**
 * @author b1529404
 */
public class JiniClientSideConnector extends JiniConnector {
   public static final String TYPE = "jini.client-end";
   private final ServiceItem serviceItem;
   private final IJiniConnectorLink link;
   JiniClientSideConnector(ServiceItem serviceItem) {
      super();
      this.serviceItem = serviceItem;
      buildPropertiesFromEntries(serviceItem.attributeSets, getProperties());
      link = (IJiniConnectorLink) getProperties().getProperty(LINK_PROPERTY);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#getService()
    */
   @Override
   public Object getService() {
         return serviceItem.service;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#getType()
    */
   @Override
   public String getConnectorType() {
      return TYPE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#upload(java.io.File)
    */
   @Override
   public URI upload(File file) throws Exception {
      return null;
   }

    @Override
    public boolean ping() {
	try {
	    return link.ping();
	} catch (RemoteException e) {
	    return false;
	}
    }

}
