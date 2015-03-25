package org.eclipse.osee.ote.remote.messages;

import java.io.Serializable;
import java.util.UUID;

public class DisconnectRemoteTestEnvironment implements Serializable {

   private static final long serialVersionUID = 1100894850334052780L;
  
   private UUID id;
   
   public DisconnectRemoteTestEnvironment(UUID id){
      this.id = id;
   }

   public UUID getId() {
      return id;
   }
}
