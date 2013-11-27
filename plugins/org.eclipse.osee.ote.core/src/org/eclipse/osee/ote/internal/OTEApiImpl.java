package org.eclipse.osee.ote.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.osee.ote.Configuration;
import org.eclipse.osee.ote.ConfigurationStatus;
import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.OTEServerFolder;
import org.eclipse.osee.ote.OTEServerRuntimeCache;
import org.eclipse.osee.ote.OTEStatusCallback;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.model.IModelManager;

public final class OTEApiImpl implements OTEApi {

   private final Configuration emptyConfiguration;
   private final ReentrantLock configurationLock;
   
   private Future<ConfigurationStatus> currentConfigurationFuture;
   private ExecutorService executor;
   private IRuntimeLibraryManager runtimeLibraryManager;
   private OTEServerFolder serverFolder;
   private OTEServerRuntimeCache serverRuntimeCache;
   private TestEnvironmentInterface env;
   private IModelManager modelManager;
   
   /**
    * ds component method
    */
   public void start(){
   }
   
   /**
    * ds component method
    */
   public void stop(){
   }
   
   /**
    * ds component method
    */
   public void bindRuntimeLibraryManager(IRuntimeLibraryManager runtimeLibraryManager){
      this.runtimeLibraryManager = runtimeLibraryManager;
   }
   
   /**
    * ds component method
    */
   public void unbindRuntimeLibraryManager(IRuntimeLibraryManager runtimeLibraryManager){
      this.runtimeLibraryManager = null;
   }
   
   /**
    * ds component method
    */
   public void bindOTEServerFolder(OTEServerFolder serverFolder){
      this.serverFolder = serverFolder;
   }
   
   /**
    * ds component method
    */
   public void unbindOTEServerFolder(OTEServerFolder serverFolder){
      this.serverFolder = null;
   }
   
   /**
    * ds component method
    */
   public void bindOTEServerRuntimeCache(OTEServerRuntimeCache serverRuntimeCache){
      this.serverRuntimeCache = serverRuntimeCache;
   }
   
   /**
    * ds component method
    */
   public void unbindOTEServerRuntimeCache(OTEServerRuntimeCache serverRuntimeCache){
      this.serverRuntimeCache = null;
   }
   
   /**
    * ds component method
    */
   public void bindTestEnvironmentInterface(TestEnvironmentInterface env){
      this.env = env;
   }
   
   /**
    * ds component method
    */
   public void unbindTestEnvironmentInterface(TestEnvironmentInterface env){
      this.env = null;
   }
   
   /**
    * ds component method
    */
   public void bindIModelManager(IModelManager modelManager){
      this.modelManager = modelManager;
   }
   
   /**
    * ds component method
    */
   public void unbindIModelManager(IModelManager modelManager){
      this.modelManager = null;
   }
   
   public OTEApiImpl(){
      this.configurationLock = new ReentrantLock();
      this.emptyConfiguration = new Configuration();
      this.currentConfigurationFuture = new OTEFutureImpl(new ConfigurationStatus(emptyConfiguration, true, ""));
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
   public Future<ConfigurationStatus> loadConfiguration(Configuration configuration, OTEStatusCallback<ConfigurationStatus> callable) throws InterruptedException, ExecutionException {
      Future<ConfigurationStatus> status;
      configurationLock.lock();
      try{
         if(currentConfigurationFuture != null && !currentConfigurationFuture.isDone()){
            status = new OTEFutureImpl(new ConfigurationStatus(configuration, false, "In the process of loading a configuration."));
            callable.complete(status.get());
         } else if(currentConfigurationFuture.get().getConfiguration() == emptyConfiguration || configuration == emptyConfiguration || !currentConfigurationFuture.get().isSuccess()){
            status = new OTEFutureImpl(executor.submit(new Configure(runtimeLibraryManager, configuration, callable)));
            currentConfigurationFuture = status;
         } else {
            status = new OTEFutureImpl(new ConfigurationStatus(configuration, false, "Environment already configured."));
            callable.complete(status.get());
         }
      } finally {
         configurationLock.unlock();
      }
      return status;
   }
   
   @Override
   public Future<ConfigurationStatus> resetConfiguration(OTEStatusCallback<ConfigurationStatus> callable) throws InterruptedException, ExecutionException {
      return loadConfiguration(emptyConfiguration, callable);
   }

   @Override
   public Future<ConfigurationStatus> getConfiguration() {
      return currentConfigurationFuture;
   }
   
   @Override
   public Future<ConfigurationStatus> downloadConfigurationJars(Configuration configuration, OTEStatusCallback<ConfigurationStatus> callable) throws InterruptedException, ExecutionException {
      return new OTEFutureImpl(executor.submit(new DownloadConfiguration(runtimeLibraryManager, configuration, callable)));
   }

   @Override
   public IModelManager getModelManager() {
      return modelManager;
   }

   @Override
   public TestEnvironmentInterface getTestEnvironment() {
      return env;
   }

   @Override
   public OTEServerFolder getServerFolder() {
      return serverFolder;
   }

   @Override
   public OTEServerRuntimeCache getRuntimeCache() {
      return serverRuntimeCache;
   }

   @Override
   public Class<?> loadFromScriptClassLoader(String clazz) throws ClassNotFoundException {
      return runtimeLibraryManager.loadFromRuntimeLibraryLoader(clazz);
   }

   @Override
   public Class<?> loadFromRuntimeLibraryLoader(String clazz) throws ClassNotFoundException {
      return runtimeLibraryManager.loadFromScriptClassLoader(clazz);
   }

}
