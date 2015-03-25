package org.eclipse.osee.ote.internal;

import java.io.IOException;
import java.rmi.RemoteException;

import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.endpoint.OteUdpEndpointSender;
import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.remote.messages.SerializedEnhancedProperties;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class GetPropertiesListener implements EventHandler {

   private final OTEApi oteApi;
   private final OteUdpEndpoint endpoint;

   public GetPropertiesListener(EventAdmin eventAdmin, OteUdpEndpoint endpoint, OTEApi oteApi) {
      this.oteApi = oteApi;
      this.endpoint = endpoint;
   }
   
   @Override
   public void handleEvent(Event arg0) {
      OteEventMessage oteEventMessage = OteEventMessageUtil.getOteEventMessage(arg0);
      SerializedEnhancedProperties properties = new SerializedEnhancedProperties();
      IHostTestEnvironment host = oteApi.getIHostTestEnvironment();
      if(host != null){
         try {
            properties.setResponse(oteEventMessage);
            OteUdpEndpointSender oteEndpointSender = endpoint.getOteEndpointSender(oteEventMessage.getHeader().getSourceInetSocketAddress());
            properties.setObject(host.getProperties());
            oteEndpointSender.send(properties);            
         } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

}
