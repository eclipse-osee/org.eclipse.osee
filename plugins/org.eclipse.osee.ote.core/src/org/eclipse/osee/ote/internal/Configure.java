package org.eclipse.osee.ote.internal;

import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.Configuration;
import org.eclipse.osee.ote.ConfigurationStatus;
import org.eclipse.osee.ote.OTEStatusCallback;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;

public class Configure implements Callable<ConfigurationStatus> {

   private final IRuntimeLibraryManager bundleLoader;
   private final Configuration configuration;
   private final OTEStatusCallback<ConfigurationStatus> callable;
   
   public Configure(IRuntimeLibraryManager bundleLoader2, Configuration configuration, OTEStatusCallback<ConfigurationStatus> callable) {
      this.bundleLoader = bundleLoader2;
      this.configuration = configuration;
      this.callable = callable;
   }

   @Override
   public ConfigurationStatus call() throws Exception {
      long startTime = System.currentTimeMillis();
      ConfigurationStatus status = new ConfigurationStatus(configuration, true, "");
      try{
         boolean completedUninstall = true;
         callable.setTotalUnitsOfWork(determineUnitsOfWork());
         if(bundleLoader.installed()){
            completedUninstall = bundleLoader.uninstall(callable);
            callable.incrememtUnitsWorked(1);
         }
         if(completedUninstall){
            if(bundleLoader.install(configuration, callable)){
               if(!bundleLoader.start(callable)){
                  status.setFail("Bundle start failed.");
                  bundleLoader.uninstall(callable);
               }
            } else {
               status.setFail("Bundle install failed.");
               bundleLoader.uninstall(callable);
            }
         } else {
            status.setFail("Failed to uninstall bundles.");
         }
      } finally {
         callable.complete(status);
      }
      long elapsedTime = System.currentTimeMillis() - startTime;
      OseeLog.log(getClass(), Level.INFO, String.format("Took %d ms to load and start the test environment.", elapsedTime));
      return status;
   }

   private int determineUnitsOfWork() {
      return (bundleLoader.installed() ? 1 : 0) + configuration.getItems().size()*2;      
   }

}
