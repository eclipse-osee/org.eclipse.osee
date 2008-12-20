package org.eclipse.osee.framework.application.server;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osee.framework.database.initialize.DatabaseInitializationOperation;

public class DbInitApplication implements IApplication {

   @Override
   public Object start(IApplicationContext context) throws Exception {
      DatabaseInitializationOperation.executeConfigureFromJvmProperties();
      return EXIT_OK;
   }

   @Override
   public void stop() {
   }
}
