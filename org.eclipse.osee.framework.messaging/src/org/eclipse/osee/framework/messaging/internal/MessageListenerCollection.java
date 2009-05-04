/*
 * Created on May 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.framework.messaging.ReceiveListener;
import org.eclipse.osee.framework.messaging.SendListener;
import org.eclipse.osee.framework.messaging.id.MessageId;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class MessageListenerCollection {

   private ConcurrentListMap<MessageId, ReceiveListener> receiveListeners;
   private ConcurrentListMap<MessageId, SendListener> sendListeners;
   
   public MessageListenerCollection(){
      receiveListeners = new ConcurrentListMap<MessageId, ReceiveListener>();
      sendListeners = new ConcurrentListMap<MessageId, SendListener>();
   }
   
   public boolean addReceiveListener(MessageId messageId, ReceiveListener receiveListener) {
      return receiveListeners.add(messageId, receiveListener);
   }

   public boolean addSendListener(MessageId messageId, SendListener sendListener) {
      return sendListeners.add(messageId, sendListener);
   }

   public boolean removeReceiveListener(MessageId messageId, ReceiveListener receiveListener) {
      return receiveListeners.remove(messageId, receiveListener);
   }

   public boolean removeSendListener(MessageId messageId, SendListener sendListener) {
      return sendListeners.remove(messageId, sendListener);
   }

   public void notifyReceiveListeners(Message message) {
      for(ReceiveListener listener : receiveListeners.get(message.getId())){
         listener.handle(message);
      }
   }

   public void dispose() {
      receiveListeners.clear();
      sendListeners.clear();
   }

}
