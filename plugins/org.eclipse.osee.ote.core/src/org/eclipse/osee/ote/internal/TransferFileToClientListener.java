package org.eclipse.osee.ote.internal;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.filetransfer.TcpFileTransfer;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.remote.messages.TestEnvironmentTransferFile;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class TransferFileToClientListener implements EventHandler {

   private ExecutorService pool;
   private OTEApi oteApi;

   public TransferFileToClientListener(EventAdmin eventAdmin, OteUdpEndpoint oteEndpoint, OTEApi oteApi) {
      this.oteApi = oteApi;
      pool = Executors.newCachedThreadPool(new ThreadFactory() {
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setName("OTE File Transfer");
            return th;
         }
      });
   }

   @Override
   public void handleEvent(Event arg0) {
      if(oteApi.getTestEnvironment() == null){
         return;
      }
      TestEnvironmentTransferFile testEnvironmentTransferFile = new TestEnvironmentTransferFile(OteEventMessageUtil.getBytes(arg0));
      InetSocketAddress inetSocketAddress;
      try {
         inetSocketAddress = new InetSocketAddress(testEnvironmentTransferFile.ADDRESS.getAddress(), testEnvironmentTransferFile.ADDRESS.getPort());
         TcpFileTransfer.sendFile(pool, new File(testEnvironmentTransferFile.FILE_PATH.getValue()), inetSocketAddress);
      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}
