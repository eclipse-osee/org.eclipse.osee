package org.eclipse.osee.ote.internal;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.remote.messages.AVAILABLE_PHYSICAL_TYPES_REQ;
import org.eclipse.osee.ote.remote.messages.SerializedAvailablePhysicalTypesMessage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class PhysicalTypesResponceListener implements EventHandler {

   private final EventAdmin eventAdmin;
   private final IRemoteMessageService messageService;
   private final SerializedAvailablePhysicalTypesMessage resp;
   private final AVAILABLE_PHYSICAL_TYPES_REQ req;

   public PhysicalTypesResponceListener(EventAdmin eventAdmin, IRemoteMessageService messageService) {
      this.eventAdmin = eventAdmin;
      this.messageService = messageService;
      resp = new SerializedAvailablePhysicalTypesMessage();
      req = new AVAILABLE_PHYSICAL_TYPES_REQ();
   }

   @Override
   public void handleEvent(Event event) {
      OteEventMessageUtil.putBytes(event, req);
      resp.getHeader().RESPONSE_ID.setValue(req.getHeader().MESSAGE_SEQUENCE_NUMBER.getValue());
      try {
         resp.setObject((Serializable)messageService.getAvailablePhysicalTypes());
         OteEventMessageUtil.postEvent(resp, eventAdmin);
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
   }

}
