package org.eclipse.osee.ote.rest.client.internal;

import java.net.URI;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.ote.rest.client.OTECacheItem;
import org.eclipse.osee.ote.rest.client.Progress;
import org.eclipse.osee.ote.rest.model.OTEConfiguration;
import org.eclipse.osee.ote.rest.model.OTEConfigurationIdentity;
import org.eclipse.osee.ote.rest.model.OTEConfigurationItem;
import org.eclipse.osee.ote.rest.model.OTEJobStatus;

public class PrepareOteServerFile extends BaseClientCallable<Progress> {

   private static final long POLLING_RATE = 1000;
   private final URI uri;
   private final List<OTECacheItem> jars;
   private final Progress progress;
   private final JaxRsClient factory;
   private OTEJobStatus status;
   private final String baseJarURL;

   public PrepareOteServerFile(URI uri, String baseJarURL, List<OTECacheItem> jars, Progress progress, JaxRsClient factory) {
      super(progress);
      this.uri = uri;
      this.jars = jars;
      this.progress = progress;
      this.factory = factory;
      this.baseJarURL = baseJarURL;
   }

   @Override
   public void doWork() throws Exception {
      status = sendBundleConfiguration();
      if (!status.isJobComplete()) {
         waitForJobComplete();
      }

      if (!status.isSuccess()) {
         throw new Exception("Failed to update the environment cache: " + status.getErrorLog());
      }
   }

   private void waitForJobComplete() throws Exception {

      URI jobUri = status.getUpdatedJobStatus().toURI();
      final WebTarget service = factory.target(jobUri);

      while (!status.isJobComplete()) {
         Thread.sleep(POLLING_RATE);
         status = service.request(MediaType.APPLICATION_JSON).get(OTEJobStatus.class);
         progress.setUnitsOfWork(status.getTotalUnitsOfWork());
         progress.setUnitsWorked(status.getUnitsWorked());
      }
   }

   private OTEJobStatus sendBundleConfiguration() throws Exception {
      OTEConfiguration configuration = new OTEConfiguration();
      OTEConfigurationIdentity identity = new OTEConfigurationIdentity();
      identity.setName("test");
      configuration.setIdentity(identity);
      for (OTECacheItem bundleInfo : jars) {
         OTEConfigurationItem item = new OTEConfigurationItem();
         item.setBundleName(bundleInfo.getFile().getName());
         item.setBundleVersion("N/A");
         item.setLocationUrl(baseJarURL + bundleInfo.getMd5());
         item.setMd5Digest(bundleInfo.getMd5());
         configuration.addItem(item);
      }
      WebTarget baseService = factory.target(uri);

      return baseService.path("ote").path("cache").request(MediaType.APPLICATION_JSON).post(Entity.json(configuration),
         OTEJobStatus.class);
   }

}
