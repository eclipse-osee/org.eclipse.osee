/*
 * Created on Apr 20, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.messaging.ExceptionHandler;
import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.framework.messaging.MessageExchange;
import org.eclipse.osee.framework.messaging.MessageId;
import org.eclipse.osee.framework.messaging.ReceiveListener;
import org.eclipse.osee.framework.messaging.SendHandler;

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
    * @see org.eclipse.osee.framework.messaging.MessageExchange#registerMessageRecieveListener(org.eclipse.osee.framework.messaging.MessageId, org.eclipse.osee.framework.messaging.RecieveListener)
    */
   @Override
   public void registerMessageRecieveListener(MessageId messageId, ReceiveListener recieveHandler) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#registerMessageSendHandler(org.eclipse.osee.framework.messaging.MessageId, org.eclipse.osee.framework.messaging.SendHandler[])
    */
   @Override
   public void registerMessageSendHandler(MessageId messageId, SendHandler... sendHandler) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#unregisterMessageSendHandler(org.eclipse.osee.framework.messaging.MessageId, org.eclipse.osee.framework.messaging.SendHandler[])
    */
   @Override
   public void unregisterMessageSendHandler(MessageId messageId, SendHandler... sendHandler) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#send(org.eclipse.osee.framework.messaging.Message, org.eclipse.osee.framework.messaging.ExceptionHandler)
    */
   @Override
   public void send(Message message, ExceptionHandler exceptionHandler) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.MessageExchange#unregisterMessageRecieveHandler(org.eclipse.osee.framework.messaging.MessageId, org.eclipse.osee.framework.messaging.RecieveListener)
    */
   @Override
   public void unregisterMessageRecieveHandler(MessageId messageId, ReceiveListener recieveHandler) {
   }
   
   
}
