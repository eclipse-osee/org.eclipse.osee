package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.eclipse.osee.ote.master.rest.client.OTEMasterServer;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerAvailableNodes;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerResult;
import org.eclipse.osee.ote.master.rest.model.OTEServer;

public class OTEMasterServerImpl implements OTEMasterServer {

   static final String CONTEXT_NAME = "otemaster";
   static final String CONTEXT_SERVERS = "servers";
   
   private WebClientProvider webClientProvider;
   private ExecutorService executor;
   
   public OTEMasterServerImpl(){
      executor = Executors.newCachedThreadPool(new ThreadFactory(){
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setName("OTE Master Client " + th.getId());
            th.setDaemon(true);
            return th;
         }
      });
   }
   
   public void start(){
      
   }
   
   public void stop(){
      
   }
   
   public void bindWebClientProvider(WebClientProvider webClientProvider){
      this.webClientProvider = webClientProvider;
   }
   
   public void unbindWebClientProvider(WebClientProvider webClientProvider){
      this.webClientProvider = null;
   }
   
   @Override
   public Future<OTEMasterServerAvailableNodes> getAvailableServers(URI uri) {
      return executor.submit(new GetAvailableServers(webClientProvider, uri));
   }

   @Override
   public Future<OTEMasterServerResult> addServer(URI uri, OTEServer server) {
      return executor.submit(new AddServer(webClientProvider, uri, server));
   }

   @Override
   public Future<OTEMasterServerResult> removeServer(URI uri, OTEServer server) {
      return executor.submit(new RemoveServer(webClientProvider, uri, server));
   }

}
