package org.eclipse.osee.ote.remote.messages;

import java.io.IOException;

import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class SerializedDisconnectRemoteTestEnvironment   extends SerializedClassMessage<DisconnectRemoteTestEnvironment> {

   public static final String TOPIC = "ote/message/serialdisconnecttestenv";

   public SerializedDisconnectRemoteTestEnvironment() {
      super(TOPIC);
   }
   
   public SerializedDisconnectRemoteTestEnvironment(DisconnectRemoteTestEnvironment commandAdded) throws IOException {
      super(TOPIC, commandAdded);
   }
   
   public SerializedDisconnectRemoteTestEnvironment(byte[] bytes){
      super(bytes);
   }
}
