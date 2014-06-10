package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsClientFactory;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServer;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerAvailableNodes;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerResult;
import org.eclipse.osee.ote.master.rest.model.OTEServer;

public class OTEMasterServerImpl implements OTEMasterServer {

   static final String CONTEXT_NAME = "otemaster";
   static final String CONTEXT_SERVERS = "servers";

   private volatile JaxRsClient client;
   private ExecutorService executor;

   public void start(Map<String, Object> props) {
      executor = Executors.newCachedThreadPool(new ThreadFactory() {
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setName("OTE Master Client " + th.getId());
            th.setDaemon(true);
            return th;
         }
      });
      update(props);
   }

   public void stop() {
      if (executor != null) {
         executor.shutdown();
      }
      client = null;
   }

   public void update(Map<String, Object> props) {
      client = JaxRsClientFactory.createClient(props);
   }

   @Override
   public Future<OTEMasterServerAvailableNodes> getAvailableServers(URI uri) {
      return executor.submit(new GetAvailableServers(client, uri));
   }

   @Override
   public Future<OTEMasterServerResult> addServer(URI uri, OTEServer server) {
      return executor.submit(new AddServer(client, uri, server));
   }

   @Override
   public Future<OTEMasterServerResult> removeServer(URI uri, OTEServer server) {
      return executor.submit(new RemoveServer(client, uri, server));
   }

}
