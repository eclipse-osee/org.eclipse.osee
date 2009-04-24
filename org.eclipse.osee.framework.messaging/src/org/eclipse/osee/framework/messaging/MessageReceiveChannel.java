/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

/**
 * @author b1528444
 *
 */
public interface MessageReceiveChannel {
   public void receive(Message message);
   public void registerMessageRecieveHandler(ReceiveHandler ... recieveHandler);
  
   public void registerMessageRecieveListener(MessageId messageId, ReceiveListener recieveHandler);
   public void unregisterMessageRecieveHandler(MessageId messageId, ReceiveListener recieveHandler);
   /**
    * @param messageId
    */
   public void addMessage(MessageId messageId);
}
