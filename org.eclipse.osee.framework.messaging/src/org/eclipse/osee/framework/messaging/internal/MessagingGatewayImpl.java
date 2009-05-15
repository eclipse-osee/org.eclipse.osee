/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.internal;

import java.util.Collection;
import org.eclipse.osee.framework.messaging.EndpointReceive;
import org.eclipse.osee.framework.messaging.EndpointSend;
import org.eclipse.osee.framework.messaging.ExceptionHandler;
import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.framework.messaging.MessagingGateway;
import org.eclipse.osee.framework.messaging.ReceiveListener;
import org.eclipse.osee.framework.messaging.SendListener;
import org.eclipse.osee.framework.messaging.id.MessageId;
import org.eclipse.osee.framework.messaging.id.ProtocolId;

/**
 * @author Andrew M. Finkbeiner
 */
public class MessagingGatewayImpl implements MessagingGateway, ApplicationDistributer {

   private MessageListenerCollection messageListeners;
   private EndpointReceiveCollection endpointReceivers;
   private EndpointSendCollection endpointSenders;

   public MessagingGatewayImpl() {
      messageListeners = new MessageListenerCollection();
      endpointReceivers = new EndpointReceiveCollection();
      endpointSenders = new EndpointSendCollection();
   }

   @Override
   public void addReceiveListener(MessageId messageId, ReceiveListener receiveListener) {
      messageListeners.addReceiveListener(messageId, receiveListener);
   }

   @Override
   public void addSendListener(MessageId messageId, SendListener sendListener) {
      messageListeners.addSendListener(messageId, sendListener);
   }

   @Override
   public void removeReceiveListener(MessageId messageId, ReceiveListener receiveListener) {
      messageListeners.removeReceiveListener(messageId, receiveListener);
   }

   @Override
   public void removeSendListener(MessageId messageId, SendListener sendListener) {
      messageListeners.removeSendListener(messageId, sendListener);
   }

   @Override
   public void distribute(Message message) {
      messageListeners.notifyReceiveListeners(message);
   }

   @Override
   public boolean bind(EndpointReceive endpoint) {
      if (endpointReceivers.add(endpoint)) {
         endpoint.onBind(this);
         return true;
      }
      return false;
   }

   @Override
   public boolean unbind(EndpointReceive endpoint) {
      endpoint.onUnbind(this);
      return endpointReceivers.remove(endpoint);
   }

   @Override
   public Collection<EndpointReceive> getReceiveEndpoints() {
      return endpointReceivers.getAll();
   }

   @Override
   public boolean bind(EndpointSend endpoint) {
      return endpointSenders.add(endpoint);
   }

   @Override
   public boolean unbind(EndpointSend endpoint) {
      return endpointSenders.remove(endpoint);
   }

   @Override
   public boolean bindSendProtocol(ProtocolId protocolId, EndpointSend endpoint) {
      return endpointSenders.bind(protocolId, endpoint);
   }

   @Override
   public boolean unbindSendProtocol(ProtocolId protocolId, EndpointSend endpoint) {
      return endpointSenders.unbind(protocolId, endpoint);
   }

   @Override
   public boolean bindSendMessage(MessageId messageId, ProtocolId protocolId) {
      return endpointSenders.bind(messageId, protocolId);
   }

   @Override
   public boolean unbindSendMessage(MessageId messageId, ProtocolId protocolId) {
      return endpointSenders.unbind(messageId, protocolId);
   }

   @Override
   public Collection<EndpointSend> getSendEndpoints() {
      return endpointSenders.getAll();
   }

   @Override
   public void send(Message message, ExceptionHandler exceptionHandler) {
      EndpointSend sender = endpointSenders.get(message.getId());
      if (sender == null) {
         String errorMessage = String.format("No registered senders for messageId[%s].", message.getId().toString());
         exceptionHandler.handleException(new Exception(errorMessage));
      } else {
         sender.send(message, exceptionHandler);
      }
   }

   @Override
   public void dispose() {
      messageListeners.dispose();
      endpointReceivers.dispose();
      endpointSenders.dispose();
   }
}
