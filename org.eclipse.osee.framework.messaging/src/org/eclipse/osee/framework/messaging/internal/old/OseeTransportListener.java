/*
 * Created on Aug 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal.old;

import java.io.IOException;
import java.util.Properties;

import org.apache.activemq.transport.TransportListener;
import org.eclipse.osee.framework.messaging.Component;
import org.eclipse.osee.framework.messaging.internal.old.OseeMessagingImpl.StatusNotifier;

/**
 * @author b1122182
 */
public class OseeTransportListener implements TransportListener {

   private volatile boolean transportAvailable;
   private final StatusNotifier notifier;
   private final Component component;

   public OseeTransportListener(Component component, StatusNotifier notifier) {
      this.component = component;
      this.notifier = notifier;
      this.transportAvailable = false;
   }

   @Override
   public void onCommand(Object arg0) {
   }

   @Override
   public void onException(IOException ex) {
      notifier.notifyError(component, ex);
   }

   @Override
   public void transportInterupted() {
      setAvailable(false);
   }

   @Override
   public void transportResumed() {
      setAvailable(true);
   }

   public void setAvailable(boolean isAvailable) {
      if (transportAvailable != isAvailable) {
         this.transportAvailable = isAvailable;
         notifier.notify(createStatusMessage());
      }
   }

   public boolean isAvailable() {
      return transportAvailable;
   }

   public Properties createStatusMessage() {
      Properties properties = new Properties();
      properties.put("component", Component.JMS.getComponentName());
      properties.put("isAvailable", isAvailable());
      return properties;
   }
}
