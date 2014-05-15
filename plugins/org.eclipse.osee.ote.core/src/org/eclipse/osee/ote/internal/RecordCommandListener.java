package org.eclipse.osee.ote.internal;

import java.io.IOException;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.commands.RecordCommand;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.remote.messages.SerializedRecordCommandMessage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class RecordCommandListener implements EventHandler {

   private final IRemoteMessageService messageService;

   public RecordCommandListener(EventAdmin eventAdmin, IRemoteMessageService messageService) {
      this.messageService = messageService;
   }

   @Override
   public void handleEvent(Event arg0) {
      RecordCommand cmd;
      try {
         SerializedRecordCommandMessage msg = new SerializedRecordCommandMessage(OteEventMessageUtil.getBytes(arg0));
         cmd = msg.getObject();
         messageService.startRecording(cmd);
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } catch (ClassNotFoundException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
   }

}
