package org.eclipse.osee.ote.endpoint;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.eclipse.osee.ote.OTEException;
import org.eclipse.osee.ote.message.event.OteEventMessage;

/**
 * Launches a Thread that monitors a queue for data to send to specified UDP endpoints.
 *
 * @author b1528444
 */
public class OteUdpEndpointInlineSender implements OteEndpointSender {
   private static final int SEND_BUFFER_SIZE = 1024 * 512;

   private final InetSocketAddress address;

   public OteUdpEndpointInlineSender(InetSocketAddress address) {
      this.address = address;
   }

   @Override
   public InetSocketAddress getAddress() {
      return address;
   }

   @Override
   public void send(OteEventMessage message) {

      try {
         DatagramChannel channel = DatagramChannel.open();
         if (channel.socket().getSendBufferSize() < SEND_BUFFER_SIZE) {
            channel.socket().setSendBufferSize(SEND_BUFFER_SIZE);
         }
         channel.socket().setReuseAddress(true);
         channel.configureBlocking(true);

         ByteBuffer buffer = ByteBuffer.allocate(SEND_BUFFER_SIZE);

         buffer.put(message.getData());
         buffer.flip();
         channel.send(buffer, address);
      } catch (IOException e) {
         throw new OTEException(e);
      }
   }

   @Override
   public void stop() {
      //not needed
   }

   @Override
   public boolean isClosed() {
      return false;
   }

   @Override
   public void setDebug(boolean debug) {
      //not needed
   }

   @Override
   public void start() {
      //not needed
   }

}
