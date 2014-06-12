package org.eclipse.osee.ote.rest.client.internal;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.ote.core.BundleInfo;
import org.eclipse.osee.ote.rest.client.Progress;
import org.eclipse.osee.ote.rest.client.internal.jarserver.HeadlessClassServer;
import org.eclipse.osee.ote.rest.model.OTEConfiguration;
import org.eclipse.osee.ote.rest.model.OTEConfigurationIdentity;
import org.eclipse.osee.ote.rest.model.OTEConfigurationItem;
import org.eclipse.osee.ote.rest.model.OTEJobStatus;

public class ConfigureOteServer extends BaseClientCallable<Progress> {

   private static final long POLLING_RATE = 1000;
   private final URI uri;
   private List<File> jars;
   private final Progress progress;
   private final JaxRsClient factory;
   private OTEJobStatus status;
   private HeadlessClassServer classServer;
   private OTEConfiguration configuration;

   public ConfigureOteServer(URI uri, List<File> jars, Progress progress, JaxRsClient factory) {
      super(progress);
      this.uri = uri;
      this.jars = jars;
      this.progress = progress;
      this.factory = factory;
   }

   public ConfigureOteServer(URI uri, OTEConfiguration configuration, Progress progress, JaxRsClient factory) {
      super(progress);
      this.uri = uri;
      this.configuration = configuration;
      this.progress = progress;
      this.factory = factory;
   }

   @Override
   public void doWork() throws Exception {
      try {
         status = sendBundleConfiguration();
         if (!status.isJobComplete()) {
            waitForJobComplete();
         }

         if (!status.isSuccess()) {
            throw new Exception("Failed to configure the environment: " + status.getErrorLog());
         }
      } finally {
         if (classServer != null) {
            classServer.stop();
         }
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
      WebTarget baseService = factory.target(uri);
      if (configuration == null) {
         OTEConfiguration localConfiguration = new OTEConfiguration();
         OTEConfigurationIdentity identity = new OTEConfigurationIdentity();
         identity.setName("test");
         localConfiguration.setIdentity(identity);
         classServer = new HeadlessClassServer(PortUtil.getInstance().getValidPort(), InetAddress.getLocalHost(), jars);
         for (BundleInfo bundleInfo : classServer.getBundles()) {
            OTEConfigurationItem item = new OTEConfigurationItem();
            item.setBundleName(bundleInfo.getSymbolicName());
            item.setBundleVersion(bundleInfo.getVersion());
            item.setLocationUrl(bundleInfo.getServerBundleLocation().toString());
            item.setMd5Digest(bundleInfo.getMd5Digest());
            localConfiguration.addItem(item);
         }
         OTEConfiguration currentConfig =
            baseService.path("ote").path("config").request(MediaType.APPLICATION_JSON).get(OTEConfiguration.class);
         if (currentConfig.equals(localConfiguration)) {
            OTEJobStatus status = new OTEJobStatus();
            status.setSuccess(true);
            status.setJobComplete(true);
            return status;
         } else {
            return baseService.path("ote").path("config").request(MediaType.APPLICATION_JSON).post(
               Entity.xml(localConfiguration), OTEJobStatus.class);
         }
      } else {
         return baseService.path("ote").path("config").request(MediaType.APPLICATION_JSON).post(
            Entity.xml(configuration), OTEJobStatus.class);
      }
   }
}
