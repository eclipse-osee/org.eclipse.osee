package org.eclipse.osee.ote.server.internal;

import java.rmi.server.ExportException;
import java.util.logging.Level;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class MessageToolExportCustomizer implements ServiceTrackerCustomizer{

   private final IServiceConnector connector;
   private IRemoteMessageService messageToolServiceInstance;
   public MessageToolExportCustomizer(IServiceConnector connector) {
      this.connector = connector;
   }

   @Override
   public Object addingService(ServiceReference reference) {
      messageToolServiceInstance = (IRemoteMessageService) Activator.getDefault().getContext().getService(reference);
      try {
         return connector.export(messageToolServiceInstance);
      } catch (ExportException e) {
         OseeLog.log(MessageToolExportCustomizer.class, Level.SEVERE, "failed to export message tool service", e);
         return null;
      }

   }

   @Override
   public void modifiedService(ServiceReference reference, Object service) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void removedService(ServiceReference reference, Object service) {
      try {
         connector.unexport(messageToolServiceInstance);
      } catch (Exception e) {
         OseeLog.log(MessageToolExportCustomizer.class, Level.WARNING, "failed to unexport  message tool service", e);
      }
   }

}
