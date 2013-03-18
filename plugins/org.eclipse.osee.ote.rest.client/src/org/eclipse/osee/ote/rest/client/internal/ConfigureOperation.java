package org.eclipse.osee.ote.rest.client.internal;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.ote.rest.client.internal.jarserver.BundleInfo;
import org.eclipse.osee.ote.rest.client.internal.jarserver.HeadlessClassServer;
import org.eclipse.osee.ote.rest.model.OteConfiguration;
import org.eclipse.osee.ote.rest.model.OteConfigurationIdentity;
import org.eclipse.osee.ote.rest.model.OteConfigurationItem;
import org.eclipse.osee.ote.rest.model.OteJobStatus;

import com.sun.jersey.api.client.WebResource;

public class ConfigureOperation extends AbstractOperation {

   private static final String PLUGIN_ID = "org.eclipse.osee.ote.rest.client";
   
   private static final long POLLING_RATE = 1000;

   private final WebResourceFactory factory;
   private final URI uri;
   private final List<File> jars;

   // Temps
   private OteJobStatus status;
//   private final Object lock = new Object();

   public ConfigureOperation(WebResourceFactory factory, URI uri, List<File> jars) {
      super("Configure Server", PLUGIN_ID);
      this.factory = factory;
      this.uri = uri;
      this.jars = jars;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkNotNull(uri, "uri");
      Conditions.checkNotNull(factory, "factory");
      Conditions.checkNotNullOrEmpty(jars, "jars");

      status = sendBundleConfiguration(monitor, 0.20);
      if(!status.isJobComplete()){
         waitForJobComplete(monitor, 0.80);
      } 
      
      if(!status.isSuccess()){
         throw new Exception("Failed to configure the environment: " + status.getErrorLog());
      }
   }

   // Put on JobStatus
   private long getEstimatedTotalTime(OteJobStatus jobStatus) {
      return 1000 * 60;
   }

   private long calculateConfigurationTicks(OteJobStatus jobStatus) {
      return getEstimatedTotalTime(jobStatus) / (long) POLLING_RATE;
   }

   private void waitForJobComplete(final IProgressMonitor monitor, double percent) throws Exception {
      URI jobUri = status.getUpdatedJobStatus().toURI();

      final WebResource service = factory.createResource(jobUri);

      double stepAmount = percent / (double) calculateConfigurationTicks(status);
      final int amount = calculateWork(stepAmount);

//      Timer timer = new Timer();
//      timer.schedule(new TimerTask() {
//         @Override
//         public void run() {
            boolean shouldCancel = false;
            while(!shouldCancel){
               Thread.sleep(POLLING_RATE);
            try {
               checkForCancelledStatus(monitor);

               status = service.accept(MediaType.APPLICATION_XML).get(OteJobStatus.class);
               if (status.isJobComplete()) {
                  shouldCancel = true;
               }
            } catch (Exception ex) {
               ex.printStackTrace();
               shouldCancel = true;
            } finally {
               monitor.worked(amount);
            }
//            if (shouldCancel) {
//               cancel();
//               synchronized (lock) {
//                  lock.notify();
//               }
//            }
         }
//      }, POLLING_RATE);

//      if(!status.isJobComplete()){
//         synchronized (lock) {
//            lock.wait();
//         }
//      }
//      if(!status.isSuccess()){
//         throw new Exception(status.getErrorLog());
//      }
   }

   private OteJobStatus sendBundleConfiguration(IProgressMonitor monitor, double percent) throws Exception {
      OteConfiguration configuration = new OteConfiguration();
      OteConfigurationIdentity identity = new OteConfigurationIdentity();
      identity.setName("test");
      configuration.setIdentity(identity);
      HeadlessClassServer classServer = new HeadlessClassServer(PortUtil.getInstance().getValidPort(), InetAddress.getLocalHost(), jars);
      for (BundleInfo bundleInfo : classServer.getBundles()) {
         checkForCancelledStatus(monitor);
         OteConfigurationItem item = new OteConfigurationItem();
         item.setBundleName(bundleInfo.getSymbolicName());
         item.setBundleVersion(bundleInfo.getVersion());
         item.setLocationUrl(bundleInfo.getSystemLocation().toString());
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
