package org.eclipse.osee.ote.internal;

import java.net.InetSocketAddress;

import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.remote.messages.GET_INET_ADDRESS_REQ;
import org.eclipse.osee.ote.remote.messages.GET_INET_ADDRESS_RESP;
import org.eclipse.osee.ote.remote.messages.SOCKET_ID;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class AddressResponseListener implements EventHandler {

   private final EventAdmin eventAdmin;
   private final IRemoteMessageService messageService;
   private final GET_INET_ADDRESS_RESP resp;
   private final GET_INET_ADDRESS_REQ req;

   public AddressResponseListener(EventAdmin eventAdmin, IRemoteMessageService messageService) {
      this.eventAdmin = eventAdmin;
      this.messageService = messageService;
      resp = new GET_INET_ADDRESS_RESP();
      req = new GET_INET_ADDRESS_REQ();
   }

   @Override
   public void handleEvent(Event event) {
      OteEventMessageUtil.putBytes(event, req);
      InetSocketAddress address;
      resp.getHeader().RESPONSE_ID.setValue(req.getHeader().MESSAGE_SEQUENCE_NUMBER.getValue());
      if(req.SOCKET_ID.getValue() == SOCKET_ID.MSG_UPDATES){
         address = messageService.getMsgUpdateSocketAddress();
      } else {
         address = messageService.getRecorderSocketAddress();
      }
      resp.SOCKET_ID.setValue(req.SOCKET_ID.getValue());
      resp.ADDRESS.setAddress(address.getAddress());
      resp.ADDRESS.setPort(address.getPort());
      OteEventMessageUtil.postEvent(resp, eventAdmin);
   }

}
