package org.eclipse.osee.ote.endpoint;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

import org.eclipse.osee.ote.OTEException;
import org.eclipse.osee.ote.collections.ObjectPool;
import org.eclipse.osee.ote.collections.ObjectPoolConfiguration;
import org.eclipse.osee.ote.message.event.OteEventMessage;

/**
 * Launches a Thread that monitors a queue for data to send to specified UDP endpoints.
 *
 * @author b1528444
 *
 */
public class OteUdpEndpointSender implements OteEndpointSender {

   static final AddressBuffer POISON_PILL = new AddressBuffer();

   private final ObjectPool<AddressBuffer> buffers;
   private final ArrayBlockingQueue<AddressBuffer> toSend;

   private final InetSocketAddress address;

   private boolean debug = false;

   private volatile boolean isClosed = false;

   private volatile Thread thread;

   public OteUdpEndpointSender(InetSocketAddress address){
      toSend = new ArrayBlockingQueue<AddressBuffer>(5000);
      buffers = new ObjectPool<AddressBuffer>(new ObjectPoolConfiguration<AddressBuffer>(50,true) {
         @Override
         public AddressBuffer make() {
            return new AddressBuffer();
         }
      });
      this.address = address;
   }

   @Override
   public void start(){
      thread = new Thread(new OteEndpointSendRunnable(toSend, buffers, debug));
      thread.setName(String.format("OTE Endpoint Sender[%s]", address.toString()));
      thread.setDaemon(true);
      thread.start();
   }

   @Override
   public void stop() throws InterruptedException{
      toSend.put(POISON_PILL);
      isClosed  = true;
   }

   @Override
   public InetSocketAddress getAddress(){
      return address;
   }

   @Override
   public void send(OteEventMessage message) {
      if(debug){
         System.out.printf("[%s] sending: [%s] to [%s] [%d]\n", new Date(), message.getHeader().TOPIC.getValue(), address.toString(), message.getData().length);
      }
      AddressBuffer obj = buffers.getObject();
      obj.getBuffer().clear();
      obj.getBuffer().put(message.getData());
      obj.getBuffer().flip();
      obj.setAddress(address);
      try {
         toSend.put(obj);
      } catch (InterruptedException e) {
         throw new OTEException(e);
      }

      if (!thread.isAlive()) {
         // our thread has sat idle for too long and self terminated go ahead and start a new one
         start();
      }
   }

   @Override
   public boolean isClosed() {
      return isClosed;
   }

   @Override
   public void setDebug(boolean debug){
      this.debug = debug;
   }

}
