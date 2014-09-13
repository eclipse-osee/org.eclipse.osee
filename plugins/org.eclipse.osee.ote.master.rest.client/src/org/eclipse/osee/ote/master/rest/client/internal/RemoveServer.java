package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerResult;
import org.eclipse.osee.ote.master.rest.model.OTEServer;

public class RemoveServer implements Callable<OTEMasterServerResult> {

   private final JaxRsClient webClientProvider;
   private final OTEServer server;
   private final URI uri;

   public RemoveServer(JaxRsClient webClientProvider, URI uri, OTEServer server) {
      this.webClientProvider = webClientProvider;
      this.uri = uri;
      this.server = server;
   }

   @Override
   public OTEMasterServerResult call() throws Exception {
      URI targetUri =
         UriBuilder.fromUri(uri).path(OTEMasterServerImpl.CONTEXT_NAME).path(OTEMasterServerImpl.CONTEXT_SERVERS).path(server.getUUID().toString()).build();

      OTEMasterServerResult result = new OTEMasterServerResult();
      try {
         webClientProvider.target(targetUri).request(MediaType.APPLICATION_XML).method(HttpMethod.DELETE);
      } catch (Throwable th) {
         result.setSuccess(false);
         result.setThrowable(th);
      }
      return result;
   }

}
