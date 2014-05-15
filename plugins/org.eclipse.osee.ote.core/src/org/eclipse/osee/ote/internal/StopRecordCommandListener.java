package org.eclipse.osee.ote.internal;

import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class StopRecordCommandListener implements EventHandler {

   private final IRemoteMessageService messageService;
   
   public StopRecordCommandListener(EventAdmin eventAdmin, IRemoteMessageService messageService) {
      this.messageService = messageService;
   }

   @Override
   public void handleEvent(Event arg0) {
      messageService.stopRecording();
   }

}
