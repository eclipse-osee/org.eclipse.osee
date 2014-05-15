package org.eclipse.osee.ote.internal;

import java.io.IOException;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.commands.ZeroizeElement;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.remote.messages.SerializedZeroizeElementMessage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class ZeroizeElementCommandListener implements EventHandler {

   private final IRemoteMessageService messageService;

   public ZeroizeElementCommandListener(EventAdmin eventAdmin, IRemoteMessageService messageService) {
      this.messageService = messageService;
   }

   @Override
   public void handleEvent(Event arg0) {
      ZeroizeElement cmd;
      try {
         SerializedZeroizeElementMessage msg = new SerializedZeroizeElementMessage(OteEventMessageUtil.getBytes(arg0));
         cmd = msg.getObject();
         messageService.zeroizeElement(cmd);
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } catch (ClassNotFoundException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
   }

}
