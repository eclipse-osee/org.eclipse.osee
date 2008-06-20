package org.eclipse.osee.ote.connection.jini;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.lookup.entry.Comment;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.jini.service.core.GroupEntry;
import org.eclipse.osee.framework.jini.service.test.StaticStationInfo;
import org.eclipse.osee.ote.connection.jini.util.LeaseRenewTask;

/**
 * @author b1529404
 */
public class JiniServiceSideConnector extends JiniConnector {
   public static final String TYPE = "jini.service-end";
   private final HashMap<ServiceRegistration, LeaseRenewTask> registrations =
         new HashMap<ServiceRegistration, LeaseRenewTask>();
   private final Remote service;
   private final Remote serviceProxy;
   private final BasicJeriExporter serviceExporter;
   private final ServiceID serviceId;
   private final Timer timer = new Timer();
   private final ServiceItem serviceItem;
   private final Properties properties = new Properties();

   public JiniServiceSideConnector(Remote service, Entry[] entries) throws UnknownHostException, ExportException {
      super();
      this.service = service;
      serviceId = generateServiceId();
      serviceExporter =
            new BasicJeriExporter(TcpServerEndpoint.getInstance(Network.getValidIP().getHostAddress(), 0),
                  new BasicILFactory(null, null, Activator.getDefault().getExportClassLoader()), false, false);
      serviceProxy = (Remote) serviceExporter.export(service);
      serviceItem = new ServiceItem(serviceId, serviceProxy, entries);
      for (Entry entry : entries) {
         if (entry instanceof ServiceInfo) {
            ServiceInfo si = (ServiceInfo) entry;
            properties.setProperty("name", si.name);
            properties.setProperty("model", si.model == null ? "N.A." : si.model);
         } else if (entry instanceof Comment) {
            properties.setProperty("comment", ((Comment) entry).comment);
         } else if (entry instanceof GroupEntry) {
            properties.setProperty("groups", ((GroupEntry) entry).getFormmatedString());
         } else if (entry instanceof StaticStationInfo) {
            StaticStationInfo ssi = (StaticStationInfo) entry;
            properties.setProperty("type", ssi.type);
            properties.setProperty("station", ssi.station);
            properties.setProperty("mode", ssi.mode);
            properties.setProperty("date", ssi.dateStarted.toString());
         } else if (entry instanceof Name) {
            properties.setProperty("name", ((Name) entry).name);
         }
      }
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
   }

   /**
    * this method will cancel all current registrations of this connector
    */
   synchronized void removeAllRegistrations() {
      for (ServiceRegistration registration : registrations.keySet()) {
         try {
            removeRegistration(registration);
         } catch (Exception e) {
            e.printStackTrace();
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

   public synchronized void setAttributes(Entry[] entry) {
      for (ServiceRegistration registration : registrations.keySet()) {
         try {
            registration.setAttributes(entry);
         } catch (Exception ex) {
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
    * @see org.eclipse.osee.connection.service.IServiceConnector#getProperty(java.lang.String, java.lang.String)
    */
   @Override
   public String getProperty(String property, String defaultValue) {
      return properties.getProperty(property, defaultValue);
   }
}
