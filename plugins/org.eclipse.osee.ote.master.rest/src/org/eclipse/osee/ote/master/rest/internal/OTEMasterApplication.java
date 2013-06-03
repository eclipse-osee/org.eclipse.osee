package org.eclipse.osee.ote.master.rest.internal;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class OTEMasterApplication implements IApplication {

   @Override
   public Object start(IApplicationContext context) throws Exception {
      Object obj = new Object();
      synchronized (obj) {
         obj.wait();
      }
      return IApplication.EXIT_OK;
   }

   @Override
   public void stop() {

   }
   
}
