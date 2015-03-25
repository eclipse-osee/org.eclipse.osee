package org.eclipse.osee.ote.core;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.OTEException;

public abstract class BaseUdpReceiveRunnable implements Runnable {

   private static final int DATA_SIZE = 65536;
   private static final int UDP_TIMEOUT = 240000; // 4 MINUTES
   private static final int ONE_MEG = 1024 * 1024;
   
   private volatile boolean run = true; 
   private Class<BaseUdpReceiveRunnable> logger = BaseUdpReceiveRunnable.class;
   private InetSocketAddress address;

   public BaseUdpReceiveRunnable(InetSocketAddress address){
      this.address = address;
   }
   
   public void stop(){
      run = false;
   }
   
   @Override
   public void run() {
      ByteBuffer buffer = ByteBuffer.allocateDirect(DATA_SIZE);
      DatagramChannel channel = null;
      try{
         while(run){
            try {
               channel = DatagramChannel.open();
               channel.socket().setReuseAddress(true);
               channel.socket().bind(address);
               channel.socket().setSoTimeout(UDP_TIMEOUT);
               channel.socket().setReceiveBufferSize(ONE_MEG);
               channel.configureBlocking(true);
             
               while (run) {
                  buffer.clear();
                  channel.receive(buffer);
                  buffer.flip();
                  processBuffer(buffer);
               }
            } catch (BindException ex) {
               OseeLog.log(logger, Level.FINEST, ex);
               channel.close();
               Thread.sleep(1000);
            }
         }
      }catch (InterruptedIOException ex) {
         Thread.interrupted();
         if (run) {
            OseeLog.log(logger, Level.WARNING, "Unexpected interruption", ex);
         }
      } catch (ClosedByInterruptException ie) {
         Thread.interrupted();
         if (run) {
            OseeLog.log(logger, Level.WARNING, "Unexpected interruption", ie);
         }
      } catch (Throwable t) {
         throw new OTEException(t);
      } finally {
         try {
            if (channel != null) {
               channel.close();
            }
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
   }

   protected abstract void processBuffer(ByteBuffer buffer);
   
   public InetSocketAddress getAddress() {
      return address;
   }

}
