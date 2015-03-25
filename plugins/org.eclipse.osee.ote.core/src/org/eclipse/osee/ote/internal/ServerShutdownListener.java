package org.eclipse.osee.ote.internal;

import java.rmi.RemoteException;

import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.remote.messages.TestEnvironmentServerShutdown;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class ServerShutdownListener implements EventHandler {

   private OTEApi oteApi;

   public ServerShutdownListener(EventAdmin eventAdmin, OteUdpEndpoint oteEndpoint, OTEApi oteApi) {
      this.oteApi = oteApi;
   }

   @Override
   public void handleEvent(Event arg0) {
      if(oteApi.getIHostTestEnvironment() == null){
         return;
      }
      TestEnvironmentServerShutdown serverShutdown = new TestEnvironmentServerShutdown(OteEventMessageUtil.getBytes(arg0));
      String id;
      try {
         id = (String)oteApi.getIHostTestEnvironment().getProperties().getProperty("id", "dontknow");
         if(serverShutdown.SERVER_ID.getValue().equals(id)){
            shutdown();
         }
      } catch (RemoteException e) {
         e.printStackTrace();
      }
   }
   
   private void shutdown(){
      BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
      Bundle systemBundle = context.getBundle(0);
      try {
         systemBundle.stop();
         boolean canExit = false;
         while(!canExit){
            try{
               Thread.sleep(20);
            } catch (Throwable th){
            }
            canExit = true;
            try{
               for(Bundle b:context.getBundles()){
                  if(b.getState() != Bundle.ACTIVE){
                     canExit = false;
                  }
               }
            } catch (Throwable th){
               canExit = true;
            }
         }
      } catch (BundleException e) {
      }
      System.exit(0);
   }

}
