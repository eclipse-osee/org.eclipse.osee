package org.eclipse.osee.ote.internal;

import java.util.concurrent.ExecutionException;

import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.OTEConfiguration;
import org.eclipse.osee.ote.OTEConfigurationStatus;
import org.eclipse.osee.ote.OTEFuture;
import org.eclipse.osee.ote.OTEStatusCallback;

public class OTEApiComponent implements OTEApi {

   private OTEApiImpl oteApi;

   public void start(){
      OTEBundleLoader loader = new OTEBundleLoader();
      oteApi = new OTEApiImpl(loader);
   }
   
   public void stop(){
      oteApi = null;
   }

   @Override
   public OTEFuture<OTEConfigurationStatus> loadConfiguration(OTEConfiguration configuration, OTEStatusCallback<OTEConfigurationStatus> callable) throws InterruptedException, ExecutionException {
      return oteApi.loadConfiguration(configuration, callable);
   }

   @Override
   public OTEFuture<OTEConfigurationStatus> resetConfiguration(OTEStatusCallback<OTEConfigurationStatus> callable) throws InterruptedException, ExecutionException {
      return oteApi.resetConfiguration(callable);
   }

   @Override
   public OTEFuture<OTEConfigurationStatus> getConfiguration() {
      return oteApi.getConfiguration();
   }
   
   void clearJarCache(){
      oteApi.clearJarCache();
   }
}
