package org.eclipse.osee.ote.internal;

import java.io.IOException;

import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.remote.messages.DisconnectRemoteTestEnvironment;
import org.eclipse.osee.ote.remote.messages.SerializedDisconnectRemoteTestEnvironment;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class DisconnectListener implements EventHandler {

   private OTEApi oteApi;
   private OteUdpEndpoint oteEndpoint;

   public DisconnectListener(EventAdmin eventAdmin, OteUdpEndpoint oteEndpoint, OTEApi oteApi) {
      this.oteApi = oteApi;
      this.oteEndpoint = oteEndpoint;
   }

   @Override
   public void handleEvent(Event arg0) {
      SerializedDisconnectRemoteTestEnvironment serializedDisconnectRemoteTestEnvironment = new SerializedDisconnectRemoteTestEnvironment(OteEventMessageUtil.getBytes(arg0));
      DisconnectRemoteTestEnvironment disconnect;
      try {
         disconnect = serializedDisconnectRemoteTestEnvironment.getObject();
         oteApi.getIHostTestEnvironment().disconnect(disconnect.getId());
         oteEndpoint.removeBroadcast(oteEndpoint.getOteEndpointSender(serializedDisconnectRemoteTestEnvironment.getHeader().getSourceInetSocketAddress()));
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
   }

}
