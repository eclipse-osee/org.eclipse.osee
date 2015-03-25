package org.eclipse.osee.ote.remote.messages;

import java.io.IOException;

import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class SerializedRequestRemoteTestEnvironment  extends SerializedClassMessage<RequestRemoteTestEnvironment> {

   public static final String TOPIC = "ote/message/serialrequesttestenv";

   public SerializedRequestRemoteTestEnvironment() {
      super(TOPIC);
      
      getHeader().RESPONSE_TOPIC.setValue(SerializedConnectionRequestResult.EVENT);
   }
   
   public SerializedRequestRemoteTestEnvironment(RequestRemoteTestEnvironment commandAdded) throws IOException {
      super(TOPIC, commandAdded);
   }
   
   public SerializedRequestRemoteTestEnvironment(byte[] bytes){
      super(bytes);
   }
}
