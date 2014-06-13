package org.eclipse.osee.ote.rest.client.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.ote.rest.client.Progress;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class GetOteServerFile extends BaseClientCallable<Progress> {

   private final URI uri;
   private final String filePath;
   @SuppressWarnings("unused")
   private final Progress progress;
   private final JaxRsClient factory;
   private final File destination;

   public GetOteServerFile(URI uri, File destination, String filePath, Progress progress, JaxRsClient factory) {
      super(progress);
      this.uri = uri;
      this.filePath = filePath;
      this.progress = progress;
      this.factory = factory;
      this.destination = destination;
   }

   @Override
   public void doWork() throws Exception {
      WebResource client = factory.createResource(uri);
      ClientResponse response =
         client.queryParam("path", filePath).path("ote").path("file").accept(MediaType.APPLICATION_XML).get(
            ClientResponse.class);
      if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
         InputStream is = response.getEntityInputStream();
         FileOutputStream fos = new FileOutputStream(destination);
         try {
            byte[] data = new byte[2048];
            int numRead = 0;
            while ((numRead = is.read(data)) != -1) {
               fos.write(data, 0, numRead);
            }
            fos.flush();
         } finally {
            Lib.close(fos);
         }
      } else {
         throw new Exception(response.toString());
      }
   }

}
