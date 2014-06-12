package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.URI;
import java.util.concurrent.Callable;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerAvailableNodes;
import org.eclipse.osee.ote.master.rest.model.OTEServer;

public class GetAvailableServers implements Callable<OTEMasterServerAvailableNodes> {

   private final JaxRsClient webClientProvider;
   private final URI uri;

   public GetAvailableServers(JaxRsClient webClientProvider, URI uri) {
      this.webClientProvider = webClientProvider;
      this.uri = uri;
   }

   @Override
   public OTEMasterServerAvailableNodes call() throws Exception {
      OTEMasterServerAvailableNodes result = new OTEMasterServerAvailableNodes();
      try {
         WebTarget resource = webClientProvider.target(uri);
         OTEServer[] servers =
            resource.path(OTEMasterServerImpl.CONTEXT_NAME).path(OTEMasterServerImpl.CONTEXT_SERVERS).request(
               MediaType.APPLICATION_JSON).get(OTEServer[].class);
         result.setServers(servers);
         result.setSuccess(true);
      } catch (Throwable th) {
         result.setSuccess(false);
         result.setThrowable(th);
      }
      return result;
   }

}
