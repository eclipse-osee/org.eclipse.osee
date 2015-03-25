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
public class OteUdpEndpointSender {

   static final AddressBuffer POISON_PILL = new AddressBuffer();
   
   private final ObjectPool<AddressBuffer> buffers;
   private final ArrayBlockingQueue<AddressBuffer> toSend;
   
   private final InetSocketAddress address;

   private boolean debug = false;

   private volatile boolean isClosed = false;

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
   
   public void start(){
      Thread th = new Thread(new OteEndpointSendRunnable(toSend, buffers, debug));
      th.setName(String.format("OTE Endpoint Sender[%s]", address.toString()));
      th.setDaemon(true);
      th.start();
   }
   
   public void stop() throws InterruptedException{
      toSend.put(POISON_PILL);
      isClosed  = true;
   }
   
   public InetSocketAddress getAddress(){
      return address;
   }
   
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
   }

   public boolean isClosed() {
      return isClosed;
   }
   
   public void setDebug(boolean debug){
      this.debug = debug;
   }
   
}
