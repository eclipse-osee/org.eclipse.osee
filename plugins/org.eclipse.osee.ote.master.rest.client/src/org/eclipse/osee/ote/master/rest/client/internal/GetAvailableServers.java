package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.URI;
import java.util.concurrent.Callable;

import javax.ws.rs.core.MediaType;

import org.eclipse.osee.ote.master.rest.client.OTEMasterServerAvailableNodes;
import org.eclipse.osee.ote.master.rest.model.OTEServer;

import com.sun.jersey.api.client.WebResource;

public class GetAvailableServers implements Callable<OTEMasterServerAvailableNodes> {

   private WebClientProvider webClientProvider;
   private URI uri;

   public GetAvailableServers(WebClientProvider webClientProvider, URI uri) {
      this.webClientProvider = webClientProvider;
      this.uri = uri;
   }

   @Override
   public OTEMasterServerAvailableNodes call() throws Exception {
      OTEMasterServerAvailableNodes result = new OTEMasterServerAvailableNodes();
      try{
         WebResource resource = webClientProvider.createResource(uri);
         OTEServer[] servers = resource.path(OTEMasterServerImpl.CONTEXT_NAME).path(OTEMasterServerImpl.CONTEXT_SERVERS).accept(MediaType.APPLICATION_XML).get(OTEServer[].class);
         result.setServers(servers);
         result.setSuccess(true);
      } catch (Throwable th){
         result.setSuccess(false);
         result.setThrowable(th);
      }
      return result;
   }

}
