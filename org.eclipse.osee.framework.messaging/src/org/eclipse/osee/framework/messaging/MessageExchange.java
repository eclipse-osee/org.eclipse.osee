/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import java.util.Collection;
import java.util.Properties;

/**
 * @author b1528444
 *
 */
public interface MessageExchange {

   public void send(Message message, ExceptionHandler exceptionHandler);
 
   public void start(ProtocolId id, Properties properties);
//   public Collection<ProtocolId> getSendProtocolIds();
//   public Collection<ProtocolId> getRecieveProtocolIds();
//   public Collection<ProtocolId> getAllProtocolIds();
   public void dispose();
   
   
   public void bind(MessageSendChannel sendChannel);
   public void unbind(MessageSendChannel sendChannel);
   
   public void bind(MessageReceiveChannel receiveChannel);
   public void unbind(MessageReceiveChannel receiveChannel);
   
   public Collection<MessageSendChannel> getSendChannels();
   public Collection<MessageReceiveChannel> getReceiveChannels();
   
   public void bindSendProtocol(ProtocolId protocolId, MessageSendChannel channel);
   public void unbindSendProtocol(ProtocolId protocolId, MessageSendChannel channel);
   
   public void bindReceiveProtocol(ProtocolId protocolId, MessageReceiveChannel channel);
   public void unbindReceiveProtocol(ProtocolId protocolId, MessageReceiveChannel channel);
   
   public void bindSendMessage(MessageId messageId, ProtocolId protocolId);
   public void unbindSendMessage(MessageId messageId, ProtocolId protocolId);
   
   public void bindReceiveMessage(MessageId messageId, ProtocolId protocolId);
   public void unbindReceiveMessage(MessageId messageId, ProtocolId protocolId);
   
   public void addSendListener(MessageId messageId, SendListener sendListener);
   public void removeSendListener(MessageId messageId, SendListener sendListener);
   
   public void addReceiveListener(MessageId messageId, ReceiveListener recieveListener);
   public void removeReceiveListener(MessageId messageId, ReceiveListener recieveListener);
}
