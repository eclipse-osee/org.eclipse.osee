package org.eclipse.osee.ote.endpoint;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.ote.message.event.FileAvailableRequest;
import org.junit.Assert;
import org.junit.Test;

public class OteUdpEndpointTest {

   @Test
   public void testCreation() throws IOException {
      int port = PortUtil.getInstance().getValidPort();
      OteUdpEndpointSender sender = new OteUdpEndpointSender(new InetSocketAddress(port));
      Assert.assertNotNull(sender);
      port = PortUtil.getInstance().getValidPort();
      OteUdpEndpointReceiverImpl receiver = new OteUdpEndpointReceiverImpl(new InetSocketAddress(port));
      Assert.assertNotNull(receiver);
   }
   
   @Test
   public void testSendReceive() throws IOException, InterruptedException {
      int port = PortUtil.getInstance().getValidPort();
      OteUdpEndpointReceiverImpl receiver = new OteUdpEndpointReceiverImpl(new InetSocketAddress(InetAddress.getLocalHost(), port));
      receiver.setDebugOutput(true);
      receiver.start();
      Thread.sleep(20);
      InetSocketAddress address = receiver.getEndpoint();
      OteUdpEndpointSender sender = new OteUdpEndpointSender(address);
      sender.start();
      FileAvailableRequest request = new FileAvailableRequest();
      sender.send(request);
      
      Thread.sleep(20);
      
      sender.stop();
      receiver.stop();
   }

}
