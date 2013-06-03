package org.eclipse.osee.ote.master.rest.internal;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.eclipse.osee.ote.master.OTELookup;

public class OTERestApplication extends Application {

   private static OTELookup oteLookup;
   
   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(OTEAvailableServersResource.class);
      return classes;
   }

   public void start(){
   }
   
   public void stop(){
   }
   
   public void bindOTELookup(OTELookup oteLookupSrv){
      OTERestApplication.oteLookup = oteLookupSrv;
   }
   
   public void unbindOTELookup(OTELookup oteLookupSrv){
      OTERestApplication.oteLookup = null;
   }
   
   static OTELookup getOTELookup(){
      return oteLookup;
   }

}
