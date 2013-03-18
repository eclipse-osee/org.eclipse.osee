package org.eclipse.osee.ote.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.OTEConfiguration;
import org.eclipse.osee.ote.OTEConfigurationStatus;
import org.eclipse.osee.ote.OTEFuture;
import org.eclipse.osee.ote.OTEStatusCallback;

public final class OTEApiImpl implements OTEApi{

   private final OTEBundleLoader bundleLoader;
   private final OTEConfiguration emptyConfiguration;
   private final ReentrantLock configurationLock;
   
   private OTEFuture<OTEConfigurationStatus> currentConfigurationFuture;
   private ExecutorService executor;
   
   OTEApiImpl(OTEBundleLoader bundleLoader){
      this.configurationLock = new ReentrantLock();
      this.bundleLoader = bundleLoader;
      this.emptyConfiguration = new OTEConfiguration();
      this.currentConfigurationFuture = new OTEFutureImpl(new OTEConfigurationStatus(emptyConfiguration, true, ""));
      this.executor = Executors.newSingleThreadExecutor(new ThreadFactory(){
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setName("OteConfiguration");
            return th;
         }
      });
   }
   
   @Override
   public OTEFuture<OTEConfigurationStatus> loadConfiguration(OTEConfiguration configuration, OTEStatusCallback<OTEConfigurationStatus> callable) throws InterruptedException, ExecutionException {
      OTEFuture<OTEConfigurationStatus> status;
      configurationLock.lock();
      try{
         if(currentConfigurationFuture != null && !currentConfigurationFuture.isDone()){
            status = new OTEFutureImpl(new OTEConfigurationStatus(configuration, false, "In the process of loading a configuration."));
            callable.complete(status.get());
         } else if(currentConfigurationFuture.get().getConfiguration() == emptyConfiguration || configuration == emptyConfiguration || !currentConfigurationFuture.get().isSuccess()){
            status = new OTEFutureImpl(executor.submit(new Configure(bundleLoader, configuration, callable)));
            currentConfigurationFuture = status;
         } else {
            status = new OTEFutureImpl(new OTEConfigurationStatus(configuration, false, "Environment already configured."));
            callable.complete(status.get());
         }
      } finally {
         configurationLock.unlock();
      }
      return status;
   }
   
   @Override
   public OTEFuture<OTEConfigurationStatus> resetConfiguration(OTEStatusCallback<OTEConfigurationStatus> callable) throws InterruptedException, ExecutionException {
      return loadConfiguration(emptyConfiguration, callable);
   }

   @Override
   public OTEFuture<OTEConfigurationStatus> getConfiguration() {
      return currentConfigurationFuture;
   }
   
   void clearJarCache(){
      bundleLoader.clearJarCache();
   }

}
