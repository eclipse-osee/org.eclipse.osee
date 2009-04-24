/*
 * Created on Apr 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.Collection;
import org.eclipse.osee.framework.messaging.MessageId;
import org.eclipse.osee.framework.messaging.MessageReceiveChannel;
import org.eclipse.osee.framework.messaging.ProtocolId;
import org.eclipse.osee.framework.messaging.ReceiveListener;

/**
 * @author b1528444
 *
 */
public class ReceiveChannels {

   /**
    * @param messageId
    * @param receiveListener
    */
   public void add(MessageId messageId, ReceiveListener receiveListener) {
   }

   /**
    * @param receiveChannel
    */
   public void add(MessageReceiveChannel receiveChannel) {
   }

   /**
    * @param protocolId
    * @return
    */
   public MessageReceiveChannel get(ProtocolId protocolId) {
      return null;
   }

   /**
    * @param protocolId
    * @param channel
    */
   public void put(ProtocolId protocolId, MessageReceiveChannel channel) {
   }

   /**
    * 
    */
   public void dispose() {
   }

   /**
    * @param messageId
    */
   public void add(MessageId messageId) {
   }

   /**
    * @return
    */
   public Collection<MessageReceiveChannel> getAll() {
      return null;
   }

   /**
    * @param messageId
    * @param receiveListener
    */
   public void remove(MessageId messageId, ReceiveListener receiveListener) {
   }

}
