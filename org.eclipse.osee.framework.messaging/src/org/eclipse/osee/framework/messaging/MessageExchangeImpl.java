/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import java.util.Collection;
import java.util.Properties;
import org.eclipse.osee.framework.messaging.internal.Activator;
import org.osgi.framework.BundleContext;

/**
 * @author b1528444
 *
 */
public class MessageExchangeImpl implements MessageExchange {

   public MessageExchangeImpl(){
      setupMessageReceiveChannelConsumption();
      setupMessageSendChannelConsumption();
   }
   
   /**
    * 
    */
   private void setupMessageReceiveChannelConsumption() {
      BundleContext context = Activator.getInstance().getContext();
   }

   @Override
   public void addReceiveListener(MessageId messageId, ReceiveListener receiveListener) {
      receiveChannels.add(messageId, receiveListener);
   }

   @Override
   public void addSendListener(MessageId messageId, SendListener sendListener) {
      sendChannels.add(messageId, sendListener);
   }

   @Override
   public void bind(MessageSendChannel sendChannel) {
      sendChannels.add(sendChannel);
   }

   @Override
   public void bind(MessageReceiveChannel receiveChannel) {
      receiveChannels.add(sendChannel);
   }

   @Override
   public void bindReceiveMessage(MessageId messageId, ProtocolId protocolId) {
      receiveChannels.get(protocolId).addMessage(messageId);
   }

   @Override
   public void bindReceiveProtocol(ProtocolId protocolId, MessageReceiveChannel channel) {
      receiveChannels.put(protocolId, channel);
   }

   @Override
   public void bindSendMessage(MessageId messageId, ProtocolId protocolId) {
      sendChannels.get(protocolId).add(messageId);
   }

   @Override
   public void bindSendProtocol(ProtocolId protocolId, MessageSendChannel channel) {
      sendChannels.put(protocolId, channel);
   }

   @Override
   public void dispose() {
      sendChannels.dispose();
      receiveChannels.dispose();
   }

   @Override
   public Collection<ProtocolId> getAllProtocolIds() {
      return null;
   }

   @Override
   public Collection<MessageReceiveChannel> getReceiveChannels() {
      return receiveChannels.getAll();
   }

   @Override
   public Collection<ProtocolId> getRecieveProtocolIds() {
      return null;
   }

   @Override
   public Collection<MessageSendChannel> getSendChannels() {
      return sendChannels.getAll();
   }

   @Override
   public Collection<ProtocolId> getSendProtocolIds() {
      return sendChannels.getAllProtocolIds();
   }

   @Override
   public void removeReceiveListener(MessageId messageId, ReceiveListener receiveListener) {
      receiveChannels.remove(messageId, receiveListener);
   }

   @Override
   public void removeSendListener(MessageId messageId, SendListener sendListener) {
      sendChannels.remove(messageId, sendListener);
   }

   @Override
   public void send(Message message, ExceptionHandler exceptionHandler) {
      sendChannels.send(message, exceptionHandler);
   }

   @Override
   public void start(ProtocolId id, Properties properties) {
      sdfads
   }

   @Override
   public void unbind(MessageSendChannel sendChannel) {
   }

   @Override
   public void unbind(MessageReceiveChannel receiveChannel) {
   }

   @Override
   public void unbindReceiveMessage(MessageId messageId, ProtocolId protocolId) {
   }

   @Override
   public void unbindReceiveProtocol(ProtocolId protocolId, MessageReceiveChannel channel) {
   }

   @Override
   public void unbindSendMessage(MessageId messageId, ProtocolId protocolId) {
   }

   @Override
   public void unbindSendProtocol(ProtocolId protocolId, MessageSendChannel channel) {
   }
}
