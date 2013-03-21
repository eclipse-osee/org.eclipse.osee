package org.eclipse.osee.ote.discovery.internal;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.osee.framework.messaging.services.messages.ServiceDescriptionPair;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;
import org.eclipse.osee.ote.discovery.OTEServerLocation;

public class OTEServerLocationServiceHealth implements OTEServerLocation {

   private URI brokerURI;
   private URI appServerURI;
   private String title;
   private String machineName;
   
   public OTEServerLocationServiceHealth(ServiceHealth health) throws URISyntaxException{
      this.brokerURI = new URI(health.getBrokerURI());
      for(ServiceDescriptionPair pair:health.getServiceDescription()){
         if("station".equals(pair.getName())){
            machineName = pair.getValue();
         } else if("name".equals(pair.getName())){
            title = pair.getValue();
         } else if ("appServerURI".equals(pair.getName())){
            appServerURI = new URI(pair.getValue());
         } 
      }
   }
   
   public boolean isValid(){
      return brokerURI!=null && appServerURI != null && title != null && machineName != null;
   }
   
   @Override
   public URI getBrokerURI() throws URISyntaxException {
      return brokerURI;
   }

   @Override
   public URI getApplicationServerURI() {
      return appServerURI;
   }

   @Override
   public String getTitle() {
      return title;
   }

   @Override
   public String getMachineName() {
      return machineName;
   }
}
