package org.eclipse.osee.ote.endpoint;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.OTEException;
import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageHeader;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;

public class OteEndpointReceiveRunnable implements Runnable {

   private static final int DATA_SIZE = 65536;
   private static final int UDP_TIMEOUT = 240000; // 4 MINUTES
   private static final int ONE_MEG = 1024 * 1024;

   private volatile boolean run = true;
   private volatile boolean debugOutput = false;
   private Class<OteEndpointReceiveRunnable> logger = OteEndpointReceiveRunnable.class;
   private final InetSocketAddress address;

   private CopyOnWriteArrayList<EndpointDataProcessor> dataProcessors = new CopyOnWriteArrayList<>();

   public OteEndpointReceiveRunnable(InetSocketAddress address) {
      this.address = address;
   }

   public void stop() {
      run = false;
   }

   @Override
   public void run() {
      ByteBuffer buffer = ByteBuffer.allocate(DATA_SIZE);
      DatagramChannel channel = null;
      try {
         while (run) {
            try {
               channel = DatagramChannel.open();
               channel.socket().setReuseAddress(true);
               channel.socket().bind(address);
               channel.socket().setSoTimeout(UDP_TIMEOUT);
               channel.socket().setReceiveBufferSize(ONE_MEG);
               channel.configureBlocking(true);

               while (run) {
                  try {
                     buffer.clear();
                     channel.receive(buffer);
                     buffer.flip();
                     processBuffer(buffer);
                  } catch (ClosedByInterruptException ex) {
                     stop();
                  } catch (Throwable th) {
                     th.printStackTrace();
                  }
               }
            } catch (BindException ex) {
               if (debugOutput) {
                  OseeLog.log(logger, Level.FINEST, ex);
               }
               if (channel != null) {
                  channel.close();
               }

               Thread.sleep(1000);
            }
         }
      } catch (InterruptedIOException ex) {
         Thread.interrupted();
         if (run && debugOutput) {
            OseeLog.log(logger, Level.WARNING, "Unexpected interruption", ex);
         }
      } catch (ClosedByInterruptException ie) {
         Thread.interrupted();
         if (run && debugOutput) {
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
            if (debugOutput) {
               ex.printStackTrace();
            }
         }
      }
   }

   private void processBuffer(ByteBuffer buffer) {
      int typeId = buffer.getShort(0) & 0xFFFF;
      if (typeId == OteEventMessageHeader.MARKER_VALUE) {
         byte[] data = new byte[buffer.remaining()];
         buffer.get(data);
         OteEventMessage msg = new OteEventMessage((byte[]) data);
         msg.getHeader().TTL.setNoLog(1);
         if (debugOutput) {
            try {
               System.out.printf("[%s] received: [%s][%d] from [%s:%d]\n", new Date(), msg.getHeader().TOPIC.getValue(),
                  msg.getData().length, msg.getHeader().ADDRESS.getAddress().getHostAddress(),
                  msg.getHeader().ADDRESS.getPort());
            } catch (UnknownHostException e) {
               e.printStackTrace();
            }
         }
         OteEventMessageUtil.postEvent(msg);
      } else {
         for (EndpointDataProcessor processor : dataProcessors) {
            if (processor.getTypeId() == typeId) {
               try {
                  processor.processBuffer(buffer);
               } catch (Throwable th) {
                  th.printStackTrace();
               }
            }
         }
      }
   }

   public void setDebugOutput(boolean enable) {
      debugOutput = enable;
   }

   public InetSocketAddress getAddress() {
      return address;
   }

   public void addDataProcessor(EndpointDataProcessor processor) {
      dataProcessors.add(processor);
   }

   public void removeDataProcessor(EndpointDataProcessor processor) {
      dataProcessors.remove(processor);
   }

}
