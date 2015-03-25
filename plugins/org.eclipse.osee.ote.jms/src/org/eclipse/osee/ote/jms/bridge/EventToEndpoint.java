package org.eclipse.osee.ote.jms.bridge;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.OTEException;
import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.endpoint.OteUdpEndpointSender;
import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class EventToEndpoint  implements EventHandler {

   private OteUdpEndpoint endpoint;

   public void bindOteUdpEndpoint(OteUdpEndpoint endpoint){
      this.endpoint = endpoint;
   }
   
   public void unbindOteUdpEndpoint(OteUdpEndpoint endpoint){
      this.endpoint = null;
   }
   
   @Override
   public void handleEvent(Event arg0) {
      OteEventMessage oteEventMessage = OteEventMessageUtil.getOteEventMessage(arg0);
      if(oteEventMessage != null && oteEventMessage.getHeader().TTL.getValue().intValue() == 0){
         oteEventMessage.getHeader().ADDRESS.setAddress(endpoint.getLocalEndpoint().getAddress());
         oteEventMessage.getHeader().ADDRESS.setPort(endpoint.getLocalEndpoint().getPort());
         CopyOnWriteNoIteratorList<OteUdpEndpointSender> broadcastSenders = endpoint.getBroadcastSenders();
         OteUdpEndpointSender[] oteUdpEndpointSenders = broadcastSenders.get();
         for(int i = 0; i < oteUdpEndpointSenders.length; i++){
            try{
               oteUdpEndpointSenders[i].send(oteEventMessage);
            } catch (OTEException ex){
               OseeLog.log(getClass(), Level.SEVERE, ex);
            }
         }
      }
   }

}
