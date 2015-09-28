package org.eclipse.osee.ote.master.rest.client;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.ote.master.rest.model.OTEServer;

public class OTEMasterServerAvailableNodes extends OTEMasterServerResult {

   private List<OTEServer> oteServers;
   
   public OTEMasterServerAvailableNodes(){
      oteServers = new ArrayList<>();
   }
   
   public List<OTEServer> getServers(){
      return oteServers;
   }
   
   public void setServers(OTEServer[] servers) {
      oteServers.clear();
      for(OTEServer server:servers){
         oteServers.add(server);
      }
   }

}
