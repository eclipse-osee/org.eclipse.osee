package org.eclipse.osee.ote.core;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class ServerApplication implements IApplication {

   @Override
   public Object start(IApplicationContext context) throws Exception {
      while(true){
         synchronized (this) {
            this.wait();
         }
      }
   }

   @Override
   public void stop() {
      // TODO Auto-generated method stub

   }

}
