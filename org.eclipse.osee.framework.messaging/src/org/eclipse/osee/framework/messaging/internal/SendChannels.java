/*
 * Created on Apr 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.Collection;
import org.eclipse.osee.framework.messaging.ExceptionHandler;
import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.framework.messaging.MessageId;
import org.eclipse.osee.framework.messaging.MessageSendChannel;
import org.eclipse.osee.framework.messaging.ProtocolId;
import org.eclipse.osee.framework.messaging.SendListener;

/**
 * @author b1528444
 *
 */
public class SendChannels {

   /**
    * @param messageId
    * @param sendListener
    */
   public void add(MessageId messageId, SendListener sendListener) {
   }

   /**
    * @param sendChannel
    */
   public void add(MessageSendChannel sendChannel) {
   }

   /**
    * @param protocolId
    * @return
    */
   public ReceiveChannels get(ProtocolId protocolId) {
      return null;
   }


   /**
    * 
    */
   public void dispose() {
   }

   /**
    * @param protocolId
    * @param channel
    */
   public void put(ProtocolId protocolId, MessageSendChannel channel) {
   }

   /**
    * @param message
    * @param exceptionHandler
    */
   public void send(Message message, ExceptionHandler exceptionHandler) {
   }

   /**
    * @return
    */
   public Collection<MessageSendChannel> getAll() {
      return null;
   }

   /**
    * @param messageId
    * @param sendListener
    */
   public void remove(MessageId messageId, SendListener sendListener) {
   }

}
