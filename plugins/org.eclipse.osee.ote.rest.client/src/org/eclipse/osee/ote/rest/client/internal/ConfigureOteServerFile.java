package org.eclipse.osee.ote.rest.client.internal;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.ote.rest.client.ConfigurationProgress;
import org.eclipse.osee.ote.rest.client.internal.jarserver.BundleInfo;
import org.eclipse.osee.ote.rest.client.internal.jarserver.HeadlessClassServer;
import org.eclipse.osee.ote.rest.model.OteConfiguration;
import org.eclipse.osee.ote.rest.model.OteConfigurationIdentity;
import org.eclipse.osee.ote.rest.model.OteConfigurationItem;
import org.eclipse.osee.ote.rest.model.OteJobStatus;
import com.sun.jersey.api.client.WebResource;

public class ConfigureOteServerFile extends BaseClientCallable<ConfigurationProgress> {

   private static final long POLLING_RATE = 1000;
   private URI uri;
   private List<File> jars;
   private ConfigurationProgress progress;
   private WebResourceFactory factory;
   private OteJobStatus status;
   private HeadlessClassServer classServer;

   public ConfigureOteServerFile(URI uri, List<File> jars, ConfigurationProgress progress, WebResourceFactory factory) {
      super(progress);
      this.uri = uri;
      this.jars = jars;
      this.progress = progress;
      this.factory = factory;
   }

   @Override
   public void doWork() throws Exception {
      try{
         Conditions.checkNotNull(uri, "uri");
         Conditions.checkNotNull(factory, "factory");
         Conditions.checkNotNullOrEmpty(jars, "jars");

         status = sendBundleConfiguration();
         if(!status.isJobComplete()){
            waitForJobComplete();
         } 

         if(!status.isSuccess()){
            throw new Exception("Failed to configure the environment: " + status.getErrorLog());
         }
      } finally {
         if(classServer != null){
            classServer.stop();
         }
      }
   }

   private void waitForJobComplete() throws Exception {
      
      URI jobUri = status.getUpdatedJobStatus().toURI();
      final WebResource service = factory.createResource(jobUri);

      while(!status.isJobComplete()){
         Thread.sleep(POLLING_RATE);
         status = service.accept(MediaType.APPLICATION_XML).get(OteJobStatus.class);
         progress.setUnitsOfWork(status.getTotalUnitsOfWork());
         progress.setUnitsWorked(status.getUnitsWorked());
      }
   }
   
   private OteJobStatus sendBundleConfiguration() throws Exception {
      OteConfiguration configuration = new OteConfiguration();
      OteConfigurationIdentity identity = new OteConfigurationIdentity();
      identity.setName("test");
      configuration.setIdentity(identity);
      classServer = new HeadlessClassServer(PortUtil.getInstance().getValidPort(), InetAddress.getLocalHost(), jars);
      for (BundleInfo bundleInfo : classServer.getBundles()) {
         OteConfigurationItem item = new OteConfigurationItem();
         item.setBundleName(bundleInfo.getSymbolicName());
         item.setBundleVersion(bundleInfo.getVersion());
         item.setLocationUrl(bundleInfo.getServerBundleLocation().toString());
         item.setMd5Digest(bundleInfo.getMd5Digest());
         configuration.addItem(item);
      }
      WebResource baseService = factory.createResource(uri);

      OteConfiguration currentConfig = baseService.path("ote").path("config").accept(MediaType.APPLICATION_XML).get(OteConfiguration.class);
      if(currentConfig.equals(configuration)){
         OteJobStatus status = new OteJobStatus();
         status.setSuccess(true);
         status.setJobComplete(true);
         return status;
      } else {
         return baseService.path("ote").path("config").accept(MediaType.APPLICATION_XML).post(OteJobStatus.class, configuration);
      }
   }

}
