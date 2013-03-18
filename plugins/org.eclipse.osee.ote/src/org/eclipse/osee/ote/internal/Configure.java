package org.eclipse.osee.ote.internal;

import java.util.concurrent.Callable;

import org.eclipse.osee.ote.OTEConfiguration;
import org.eclipse.osee.ote.OTEConfigurationStatus;
import org.eclipse.osee.ote.OTEStatusCallback;

public class Configure implements Callable<OTEConfigurationStatus> {

   private final OTEBundleLoader bundleLoader;
   private final OTEConfiguration configuration;
   private final OTEStatusCallback<OTEConfigurationStatus> callable;
   
   public Configure(OTEBundleLoader bundleLoader, OTEConfiguration configuration, OTEStatusCallback<OTEConfigurationStatus> callable) {
      this.bundleLoader = bundleLoader;
      this.configuration = configuration;
      this.callable = callable;
   }

   @Override
   public OTEConfigurationStatus call() throws Exception {
      OTEConfigurationStatus status = new OTEConfigurationStatus(configuration, true, "");
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
      return status;
   }

   private int determineUnitsOfWork() {
      return (bundleLoader.installed() ? 1 : 0) + configuration.getItems().size()*2;      
   }

}
