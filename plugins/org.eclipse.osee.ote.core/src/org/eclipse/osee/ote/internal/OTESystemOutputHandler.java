package org.eclipse.osee.ote.internal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.endpoint.OteUdpEndpointSender;
import org.eclipse.osee.ote.io.SystemOutputListener;
import org.eclipse.osee.ote.remote.messages.ConsoleOutputMessage;

public class OTESystemOutputHandler implements SystemOutputListener {

   private ByteBuffer buffer = ByteBuffer.allocate(4096);
   private OteUdpEndpoint endpoint;
   private ConsoleOutputMessage outputMessage;
   private ScheduledExecutorService newSingleThreadScheduledExecutor;
   
   public OTESystemOutputHandler(OteUdpEndpoint endpoint) {
      this.endpoint = endpoint;
      outputMessage = new ConsoleOutputMessage();
      newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setDaemon(true);
            th.setName("OTEConsole Send");
            return th;
         }
      });
      newSingleThreadScheduledExecutor.scheduleAtFixedRate(new Runnable(){
         @Override
         public void run() {
            send();
         }
      }, 1000, 1000, TimeUnit.MILLISECONDS);
   }

   @Override
   public void close() throws IOException {
      flush();
   }

   @Override
   public synchronized void flush() throws IOException {
      send();
   }
   
   public synchronized void send(){
      if(buffer.position() > 0){
         try{
            buffer.flip();
            outputMessage.setStringData(buffer);
            buffer.clear();
            CopyOnWriteNoIteratorList<OteUdpEndpointSender> broadcastSenders = endpoint.getBroadcastSenders();
            OteUdpEndpointSender[] oteUdpEndpointSenders = broadcastSenders.get();
            for(int i = 0; i < oteUdpEndpointSenders.length; i++){
               oteUdpEndpointSenders[i].send(outputMessage);
            }
         } catch (Throwable th){
         }
      }
   }

   @Override
   public synchronized void write(byte[] b, int off, int len) throws IOException {
      while(len > 0){
         if(buffer.position() > 0 && buffer.remaining() <= len){
            flush();
         }
         int length = (buffer.remaining() < len) ? buffer.remaining() : len;
         len = len - length;
         buffer.put(b, off, length);   
         off += length;
      }
   }

   @Override
   public synchronized void write(byte[] b) throws IOException {
      write(b, 0, b.length);
   }

}
