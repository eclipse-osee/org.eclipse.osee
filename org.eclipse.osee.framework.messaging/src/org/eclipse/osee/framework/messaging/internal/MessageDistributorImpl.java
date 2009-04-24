/*
 * Created on Apr 20, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.messaging.ExceptionHandler;
import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.framework.messaging.MessageExchange;
import org.eclipse.osee.framework.messaging.MessageId;
import org.eclipse.osee.framework.messaging.MessageReceiveChannel;
import org.eclipse.osee.framework.messaging.MessageSendChannel;
import org.eclipse.osee.framework.messaging.ProtocolId;
import org.eclipse.osee.framework.messaging.ReceiveListener;
import org.eclipse.osee.framework.messaging.SendHandler;
import org.eclipse.osee.framework.messaging.SendListener;

public class MessageDistributorImpl implements MessageExchange {

   private Map<MessageId, List<SendHandler>> sendHandlers = new ConcurrentHashMap<MessageId, List<SendHandler>>();
   private Map<MessageId, List<ReceiveListener>> recieveHandlers = new ConcurrentHashMap<MessageId,  List<ReceiveListener>>();
   
   public MessageDistributorImpl(){
      
   }
   
//   @Override
//   public void registerMessageRecieveHandler(MessageId messageId, RecieveListener recieveHandler) {
//      List<RecieveListener> handlers = recieveHandlers.get(messageId);
//      if(handlers == null){
//         handlers = new CopyOnWriteArrayList<RecieveListener>();
//      }
//      if(!handlers.contains(recieveHandler)){
//         handlers.add(recieveHandler);
//      }
//   }
//
//   @Override
//   public void registerMessageSendHandler(MessageId messageId, SendHandler sendHandler) {
//      List<SendHandler> handlers = sendHandlers.get(messageId);
//      if(handlers == null){
//         handlers = new CopyOnWriteArrayList<SendHandler>();
//      }
//      if(!handlers.contains(sendHandler)){
//         handlers.add(sendHandler);
//      }
//   }
//
//   @Override
//   public void unregisterMessageRecieveHandler(MessageId messageId, RecieveListener recieveHandler) {
//      List<RecieveListener> handlers = recieveHandlers.get(messageId);
//      if(handlers != null){
//         handlers.remove(recieveHandler);
//      }
//   }
//
//   @Override
//   public void unregisterMessageSendHandler(MessageId messageId, SendHandler sendHandler) {
//      List<SendHandler> handlers = sendHandlers.get(messageId);
//      if(handlers != null){
//         handlers.remove(sendHandler);
//      }
//   }
//  
//   /* (non-Javadoc)
//    * @see org.eclipse.osee.framework.messaging.MessageDistributor#send(org.eclipse.osee.framework.messaging.Message, org.eclipse.osee.framework.messaging.ExceptionHandler)
//    */
//   @Override
//   public void send(Message message, ExceptionHandler exceptionHandler) {
//      List<SendHandler> handlers = sendHandlers.get(message.getId());
//      for(SendHandler handler : handlers){
//         handler.handle(message, exceptionHandler);
//      }
//   }
//
//   /* (non-Javadoc)
//    * @see org.eclipse.osee.framework.messaging.MessageDistributor#recieveFromChannel(org.eclipse.osee.framework.messaging.Message)
//    */
//   @Override
//   public void recieveFromChannel(Message message) {
//      List<RecieveListener> handlers = recieveHandlers.get(message.getId());
//      for(RecieveListener handler: handlers){
//         handler.handle(message);
//      }
//   }



   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#send(org.eclipse.osee.framework.messaging.Message, org.eclipse.osee.framework.messaging.ExceptionHandler)
    */
   @Override
   public void send(Message message, ExceptionHandler exceptionHandler) {
   }



   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#addReceiveListener(org.eclipse.osee.framework.messaging.MessageId, org.eclipse.osee.framework.messaging.ReceiveListener)
    */
   @Override
   public void addReceiveListener(MessageId messageId, ReceiveListener recieveListener) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#addSendListener(org.eclipse.osee.framework.messaging.MessageId, org.eclipse.osee.framework.messaging.SendListener)
    */
   @Override
   public void addSendListener(MessageId messageId, SendListener sendListener) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#bind(org.eclipse.osee.framework.messaging.MessageSendChannel)
    */
   @Override
   public void bind(MessageSendChannel sendChannel) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#bind(org.eclipse.osee.framework.messaging.MessageReceiveChannel)
    */
   @Override
   public void bind(MessageReceiveChannel receiveChannel) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#bindReceiveMessage(org.eclipse.osee.framework.messaging.MessageId, org.eclipse.osee.framework.messaging.ProtocolId)
    */
   @Override
   public void bindReceiveMessage(MessageId messageId, ProtocolId protocolId) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#bindReceiveProtocol(org.eclipse.osee.framework.messaging.ProtocolId, org.eclipse.osee.framework.messaging.MessageReceiveChannel)
    */
   @Override
   public void bindReceiveProtocol(ProtocolId protocolId, MessageReceiveChannel channel) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#bindSendMessage(org.eclipse.osee.framework.messaging.MessageId, org.eclipse.osee.framework.messaging.ProtocolId)
    */
   @Override
   public void bindSendMessage(MessageId messageId, ProtocolId protocolId) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#bindSendProtocol(org.eclipse.osee.framework.messaging.ProtocolId, org.eclipse.osee.framework.messaging.MessageSendChannel)
    */
   @Override
   public void bindSendProtocol(ProtocolId protocolId, MessageSendChannel channel) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#dispose()
    */
   @Override
   public void dispose() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#getReceiveChannels()
    */
   @Override
   public Collection<MessageReceiveChannel> getReceiveChannels() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#getSendChannels()
    */
   @Override
   public Collection<MessageSendChannel> getSendChannels() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#removeReceiveListener(org.eclipse.osee.framework.messaging.MessageId, org.eclipse.osee.framework.messaging.ReceiveListener)
    */
   @Override
   public void removeReceiveListener(MessageId messageId, ReceiveListener recieveListener) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#removeSendListener(org.eclipse.osee.framework.messaging.MessageId, org.eclipse.osee.framework.messaging.SendListener)
    */
   @Override
   public void removeSendListener(MessageId messageId, SendListener sendListener) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#start(org.eclipse.osee.framework.messaging.ProtocolId, java.util.Properties)
    */
   @Override
   public void start(ProtocolId id, Properties properties) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#unbind(org.eclipse.osee.framework.messaging.MessageSendChannel)
    */
   @Override
   public void unbind(MessageSendChannel sendChannel) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#unbind(org.eclipse.osee.framework.messaging.MessageReceiveChannel)
    */
   @Override
   public void unbind(MessageReceiveChannel receiveChannel) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#unbindReceiveMessage(org.eclipse.osee.framework.messaging.MessageId, org.eclipse.osee.framework.messaging.ProtocolId)
    */
   @Override
   public void unbindReceiveMessage(MessageId messageId, ProtocolId protocolId) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#unbindReceiveProtocol(org.eclipse.osee.framework.messaging.ProtocolId, org.eclipse.osee.framework.messaging.MessageReceiveChannel)
    */
   @Override
   public void unbindReceiveProtocol(ProtocolId protocolId, MessageReceiveChannel channel) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#unbindSendMessage(org.eclipse.osee.framework.messaging.MessageId, org.eclipse.osee.framework.messaging.ProtocolId)
    */
   @Override
   public void unbindSendMessage(MessageId messageId, ProtocolId protocolId) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#unbindSendProtocol(org.eclipse.osee.framework.messaging.ProtocolId, org.eclipse.osee.framework.messaging.MessageSendChannel)
    */
   @Override
   public void unbindSendProtocol(ProtocolId protocolId, MessageSendChannel channel) {
   }
   
   
}
