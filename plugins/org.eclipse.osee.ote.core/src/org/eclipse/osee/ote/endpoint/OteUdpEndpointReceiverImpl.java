package org.eclipse.osee.ote.endpoint;

import java.net.InetSocketAddress;

public class OteUdpEndpointReceiverImpl {

   private OteEndpointReceiveRunnable oteEndpointReceiveRunnable;
   private Thread th;

   public OteUdpEndpointReceiverImpl(InetSocketAddress address){
      oteEndpointReceiveRunnable = new OteEndpointReceiveRunnable(address);
   }
   
   public void start(){
      th = new Thread(oteEndpointReceiveRunnable);
      th.setName("OTE UDP Endpoint Receiver");
      th.setDaemon(true);
      th.start();
   }
   
   public void stop(){
      oteEndpointReceiveRunnable.stop();
      th.interrupt();
   }
   
   public void setDebugOutput(boolean enable){
      oteEndpointReceiveRunnable.setDebugOutput(enable);
   }
   
   public InetSocketAddress getEndpoint(){
      return oteEndpointReceiveRunnable.getAddress();
   }

   public void addDataProcessor(EndpointDataProcessor processor) {
      oteEndpointReceiveRunnable.addDataProcessor(processor);  
   }

   public void removeDataProcessor(EndpointDataProcessor processor) {
      oteEndpointReceiveRunnable.removeDataProcessor(processor);
   }
   
}
