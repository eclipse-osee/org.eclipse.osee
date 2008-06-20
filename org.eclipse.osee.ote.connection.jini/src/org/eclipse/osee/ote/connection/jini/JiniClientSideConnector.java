package org.eclipse.osee.ote.connection.jini;

import java.util.Properties;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.entry.Comment;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;
import org.eclipse.osee.framework.jini.service.core.GroupEntry;
import org.eclipse.osee.framework.jini.service.test.StaticStationInfo;

/**
 * @author b1529404
 */
public class JiniClientSideConnector extends JiniConnector {
   public static final String TYPE = "jini.client-end";
   private final ServiceItem serviceItem;
   private volatile boolean serviceStopped = false;
   private final Properties properties = new Properties();

   JiniClientSideConnector(ServiceItem serviceItem) {
      this.serviceItem = serviceItem;
      for (Entry entry : serviceItem.attributeSets) {
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#getProperty(java.lang.String, java.lang.String)
    */
   @Override
   public String getProperty(String property, String defaultValue) {
      return properties.getProperty(property, defaultValue);
   }

}
