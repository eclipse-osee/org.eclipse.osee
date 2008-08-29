package org.eclipse.osee.ote.connection.jini;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.Timer;
import java.util.logging.Level;

import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.ote.connection.jini.util.LeaseRenewTask;

/**
 * @author b1529404
 */
public class JiniServiceSideConnector extends JiniConnector implements
	IJiniConnectorLink {
   public static final String TYPE = "jini.service-end";
   private final HashMap<ServiceRegistration, LeaseRenewTask> registrations =
         new HashMap<ServiceRegistration, LeaseRenewTask>();
   private final Remote service;
   private final Remote serviceProxy;
   private final BasicJeriExporter serviceExporter;
   private final ServiceID serviceId;
   private final Timer timer = new Timer();
   private final ServiceItem serviceItem;
   private final BasicJeriExporter linkExporter;
   private final IJiniConnectorLink exportedThis;
   public JiniServiceSideConnector(Remote service, EnhancedProperties props) throws UnknownHostException, ExportException {
      super(props);
      this.service = service;
      serviceId = generateServiceId();
      serviceExporter =
            new BasicJeriExporter(TcpServerEndpoint.getInstance(Network.getValidIP().getHostAddress(), 0),
                  new BasicILFactory(null, null, Activator.getDefault().getExportClassLoader()), false, false);
      linkExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(
		Network.getValidIP().getHostAddress(), 0), new BasicILFactory(
		null, null, Activator.getDefault().getExportClassLoader()),
		false, false);
      serviceProxy = (Remote) serviceExporter.export(service);
      exportedThis = (IJiniConnectorLink) linkExporter.export(this);
      props.setProperty(LINK_PROPERTY, (Serializable) exportedThis);
      serviceItem = new ServiceItem(serviceId, serviceProxy, createEntries());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#getService()
    */
   @Override
   public Remote getService() {
      return service;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#stop()
    */
   @Override
   public synchronized void stop() throws Exception {
      super.stop();
      removeAllRegistrations();
      serviceExporter.unexport(true);
      linkExporter.unexport(true);
   }

   /**
    * this method will cancel all current registrations of this connector
    */
   synchronized void removeAllRegistrations() {
      for (ServiceRegistration registration : registrations.keySet()) {
         try {
            removeRegistration(registration);
         } catch (Exception e) {
            Activator.log(Level.SEVERE, "exception removing registration",
			e);
         }
      }
   }

   private ServiceID generateServiceId() {
      Uuid uuid = UuidFactory.generate();
      Long lsb = new Long(uuid.getLeastSignificantBits());
      Long msb = new Long(uuid.getMostSignificantBits());
      return new ServiceID(msb.longValue(), lsb.longValue());
   }

   synchronized void addRegistration(ServiceRegistration registration) {
      registrations.put(registration, new LeaseRenewTask(timer, registration));
   }

   synchronized void removeRegistration(ServiceRegistration registration) {
      System.out.println("removing registration");
      LeaseRenewTask task = registrations.remove(registration);
      if (task != null) {
         task.cancel();
      }
   }

   ServiceItem getServiceItem() {
      return serviceItem;
   }

   private synchronized void setAttributes(Entry[] entry) {
      for (ServiceRegistration registration : registrations.keySet()) {
         try {
            registration.setAttributes(entry);
         } catch (Exception ex) {
             Activator.log(Level.SEVERE, "exception setting attributes", ex);
            registrations.remove(registration);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#getType()
    */
   @Override
   public String getConnectorType() {
      return TYPE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.connection.jini.JiniConnector#setProperty(java.lang.String, java.lang.String)
    */
   @Override
   public void setProperty(String key, Serializable value) {
      super.setProperty(key, value);
      setAttributes(createEntries());
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
	return true;
    }

}
