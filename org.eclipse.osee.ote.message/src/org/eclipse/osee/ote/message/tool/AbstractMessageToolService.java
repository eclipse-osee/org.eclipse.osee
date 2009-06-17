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
package org.eclipse.osee.ote.message.tool;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.UserTestSessionKey;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.commands.RecordCommand;
import org.eclipse.osee.ote.message.commands.SetElementValue;
import org.eclipse.osee.ote.message.commands.SetMessageModeCmd;
import org.eclipse.osee.ote.message.commands.SubscribeToMessage;
import org.eclipse.osee.ote.message.commands.UnSubscribeToMessage;
import org.eclipse.osee.ote.message.commands.ZeroizeElement;
import org.eclipse.osee.ote.message.commands.RecordCommand.MessageRecordDetails;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.IMessageScheduleChangeListener;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;
import org.eclipse.osee.ote.message.interfaces.IOSEEMessageReaderListener;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.message.internal.Activator;
import org.eclipse.osee.ote.message.listener.MessageSystemListener;
import org.eclipse.osee.ote.message.tool.rec.IMessageEntryFactory;
import org.eclipse.osee.ote.message.tool.rec.MessageRecordConfig;
import org.eclipse.osee.ote.message.tool.rec.MessageRecorder;

/**
 * Service that dispatches message updates to registered clients
 * 
 * @author Andrew M. Finkbeiner
 * @author Ken J. Aguilar
 */
public class AbstractMessageToolService implements IRemoteMessageService {

   private static final boolean debugEnabled = false;

   private final HashMap<String, Throwable> cancelledSubscriptions = new HashMap<String, Throwable>(40);
   private final DatagramChannel channel;
   private final HashMap<String, EnumMap<MemType, EnumMap<MessageMode, SubscriptionRecord>>> messageMap =
      new HashMap<String, EnumMap<MemType, EnumMap<MessageMode, SubscriptionRecord>>>(100);

   private final IMessageRequestor messageRequestor;
   private MessageRecorder recorder;
   private DatagramChannel recorderOutputChannel;
   private volatile boolean terminated = false;
   private final AtomicInteger idCounter = new AtomicInteger(0x0DEF0000);
   private final class ClientInfo {
      private final IMsgToolServiceClient remoteReference;

      private final InetSocketAddress ipAddress;

      public ClientInfo(final IMsgToolServiceClient remoteReference, final InetSocketAddress ipAddress) {
         super();
         this.remoteReference = remoteReference;
         this.ipAddress = ipAddress;
      }

      public InetSocketAddress getIpAddress() {
         return ipAddress;
      }

      public IMsgToolServiceClient getRemoteReference() {
         return remoteReference;
      }

      @Override
      public int hashCode() {
         return ipAddress.hashCode() ^ remoteReference.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         ClientInfo client = (ClientInfo) obj;
         return ipAddress.equals(client.ipAddress) && remoteReference.equals(client.remoteReference);
      }

   }

   /**
    * Associates a {@link Message} with a {@link IOSEEMessageReaderListener} and handles transmitting the message data
    * upon a the method call
    * {@link org.eclipse.osee.ote.message.listener.IOSEEMessageListener#onDataAvailable(MessageData, MemType)}. When a
    * listener's {@link #onDataAvailable(MessageData, MemType)} method is invoked it will transmit the new data to all
    * registered clients
    * 
    * @author Ken J. Aguilar
    */
   private final class SubscriptionRecord implements IOSEEMessageReaderListener, IMessageScheduleChangeListener {

      private final Message<?, ?, ?> msg;
      private final HashSet<ClientInfo> clients = new HashSet<ClientInfo>(10);
      private ByteBuffer buffer;
      private int msgStartPos;
      private ByteBuffer msgUpdatePart;
      private long updateCount = 0;
      private final SubscriptionKey key;
      private StringBuilder strBuilder = new StringBuilder();

      /**
       * Creates a new listener. A listener is a one to one mapping of a message to a list of client addresses
       * 
       * @param reader
       * @param writer
       * @param type
       * @param clients
       */
      SubscriptionRecord(final Message<?, ?, ?> msg, final MemType type, final MessageMode mode, final ClientInfo... clients) {
         this.msg = msg;
         this.key = new SubscriptionKey(idCounter.incrementAndGet(), type, mode, msg.getName());

         for (ClientInfo client : clients) {
            this.clients.add(client);
         }

         allocateBackingBuffer(msg.getMaxDataSize(type));

         MessageSystemListener systemListener = msg.getListener();
         if (!systemListener.containsListener(this)) {
            systemListener.addListener(this);
         }
         assert (systemListener.containsListener(this));

         this.msg.addSchedulingChangeListener(this);
      }

      private void allocateBackingBuffer(int maxDataSize) {
         final byte[] nameAsBytes = msg.getClass().getName().getBytes();
         buffer = ByteBuffer.allocateDirect(maxDataSize + nameAsBytes.length + 100);
         buffer.position(0);
         buffer.putInt(key.getId());
         msgUpdatePart = buffer.slice();
         msgStartPos = buffer.position();
      }

      public synchronized String toString() {
         String string =
            String.format("Message Watch Entry: mem type=%s, mode=%s, upd cnt=%d", key.getType(), key.getMode(),
                  updateCount);
         String string2 = " clients: ";
         for (ClientInfo addr : clients) {
            string2 = string2 + addr.ipAddress.toString() + " ";
         }
         return string + string2;
      }

      SubscriptionRecord(final Message<?, ?, ?> msg, final MemType type, final MessageMode mode, final ArrayList<ClientInfo> clients) {

         this.msg = msg;
         this.key = new SubscriptionKey(idCounter.incrementAndGet(), type, mode, msg.getName());


         this.clients.addAll(clients);

         final byte[] nameAsBytes = msg.getClass().getName().getBytes();
         buffer = ByteBuffer.allocateDirect(msg.getMaxDataSize(type) + nameAsBytes.length + 100);
         buffer.position(0);
         buffer.putInt(key.getId());
         msgUpdatePart = buffer.slice();
         msgStartPos = buffer.position();


         MessageSystemListener systemListener = msg.getListener();
         if (!systemListener.containsListener(this)) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.INFO, "Installing listener on msg reader for " + msg.getMessageName());
            systemListener.addListener(this);
         } else {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.INFO, "this listener already installed on msg reader for " + msg.getMessageName());
         }
         this.msg.addSchedulingChangeListener(this);
      }

      /**
       * Adds a new client who will be notified when new updates occur
       * 
       * @param client
       */
      public synchronized void addClient(final ClientInfo client) {
         clients.add(client);
      }

      public synchronized void removeClient(final ClientInfo client) {
         clients.remove(client);
      }

      //      public synchronized ClientInfo findClient(InetSocketAddress address) {
      //         for (ClientInfo client : clients) {
      //            if (client.getIpAddress().equals(address)) {
      //               return client;
      //            }
      //         }
      //         return null;
      //      }

      public synchronized ClientInfo findClient(IMsgToolServiceClient client) {
         for (ClientInfo clientInfo : clients) {
            if (clientInfo.remoteReference.equals(client)) {
               return clientInfo;
            }
         }
         return null;
      }

      /**
       * removes this listener from the {@link Message} set of listeners. Thus no message updates will be sent to this
       * listener any longer
       */
      public synchronized void unregister() {
         msg.removeSchedulingChangeListener(this);
         msg.getListener().removeListener(this);
      }

      /**
       * checks to see if this listener is still registered for message updates
       * 
       * @return true if the listener is registered
       */
      public synchronized boolean isRegistered() {
         return msg.getListener().containsListener(this);
      }

      /**
       * This is a callback from the underlying messaging system that is invoked when data is received for the
       * particular message
       * 
       * @see IOSEEMessageReaderListener
       */
      public synchronized void onDataAvailable(final MessageData data, final MemType type) {
         final byte[] msgData = data.toByteArray();
         final int msgLength = data.getCurrentLength();
         /* do nothing if there is no clients registered for this type */
         try {
            if (key.getType() == type) {
               msgUpdatePart.clear();
               msgUpdatePart.putLong(Activator.getTestEnvironment().getEnvTime());
               if (msgUpdatePart.remaining() < msgLength) {// != bodyLength + bodyStartPosition){
                  allocateBackingBuffer(msgLength);
                  OseeLog.log(
                        MessageSystemTestEnvironment.class,
                        Level.WARNING,
                        String.format("Backing buffer was changed in AbstractMessageTool %s to %d", msg.getName(),
                              msgLength));
               }
               msgUpdatePart.put(msgData, 0, msgLength);

               /* Message body data has changed so transmit the whole thing to clients */
               buffer.limit(msgStartPos + msgUpdatePart.position()).position(0);
               xmitMsg(clients, msg.getName(), buffer);
               updateCount++;

            }
         } catch (Exception t) {
            // stop listening for updates on this message
            unregister();
            cancelledSubscriptions.put(msg.getMessageName(), t);
            OseeLog.log(
                  MessageSystemTestEnvironment.class,
                  Level.SEVERE,
                  String.format(
                        "Exception during processing of update for %s: data length=%d, payload size=%d, buf start=%d, buf cap=%d",
                        msg.getMessageName(), msgData.length, data.getPayloadSize(), msgStartPos,
                        msgUpdatePart.capacity()), t);
         }
      }

      /**
       * do nothing stub required for interface implementation
       * 
       * @see IOSEEMessageReaderListener
       */
      public void onInitListener() {
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.ote.message.interfaces.IMessageScheduleChangeListener#onRateChanged(double)
       */
      public void onRateChanged(Message<?, ?, ?> message, double old, double rate) {
         try {
            for (ClientInfo client : clients)
               client.getRemoteReference().changeRate(msg.getClass().getName(), rate);
         } catch (RemoteException ex) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.SEVERE, ex.toString(), ex);
         }
         OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
               msg.getName() + " has a rate change to " + rate + " hz!!!!!");
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.ote.message.interfaces.IMessageScheduleChangeListener#isScheduledChanged(boolean)
       */
      public void isScheduledChanged(boolean isScheduled) {
         try {
            for (ClientInfo client : clients)
               client.getRemoteReference().changeIsScheduled(msg.getClass().getName(), isScheduled);
         } catch (RemoteException ex) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.SEVERE, ex.toString(), ex);
         }
         OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
               msg.getName() + " scheduling has changed to " + isScheduled);
      }

      /**
       * @return Returns the msg.
       */
      public Message<?, ?, ?> getMsg() {
         return msg;
      }

   } /* end of MsgListener */

   /**
    * Constructs a new message manager service
    * 
    * @param accessor
    * @throws IOException
    */
   public AbstractMessageToolService(IMessageManager messageManager) throws IOException {

      channel = DatagramChannel.open();
      channel.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), PortUtil.getInstance().getValidPort()));
      channel.configureBlocking(true);
      recorderOutputChannel = DatagramChannel.open();
      recorderOutputChannel.socket().bind(
            new InetSocketAddress(InetAddress.getLocalHost(), PortUtil.getInstance().getValidPort()));
      messageRequestor = messageManager.createMessageRequestor(getClass().getName());
   }

   public synchronized void setupRecorder(IMessageEntryFactory factory) {
      if (recorder != null && recorder.isRecording()) {
         throw new IllegalStateException("a record session is currently running");
      }
      recorder = new MessageRecorder(factory);
   }

   /**
    * Attempts to set message data.
    * 
    * @see IRemoteMessageService#setElementValue(SetElementValue)
    */
   public synchronized void setElementValue(SetElementValue cmd) throws RemoteException {
      final String msgName = cmd.getMessage();
      try {
         final Class<?> msgWriterClass = Activator.getTestEnvironment().getRuntimeManager().loadFromRuntimeLibraryLoader(msgName);

         /* check to see if an instance of a writer for the specified message exists */
         Message<?, ?, ?> writer = messageRequestor.getMessageWriter(msgWriterClass);
         if (writer == null) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.SEVERE,
                  String.format("Attempting to set message data for %s even though no previous writer exist", msgName));
            throw new Exception("Could not find the class definition for " + msgName + " message writer");
         }

         List<Object> elementPath = cmd.getElement();
         if (elementPath != null) {
            final Element element = writer.getElement(elementPath, cmd.getMemType());
            OseeLog.log(
                  MessageSystemTestEnvironment.class,
                  Level.INFO,
                  "Updating message data for element " + element.getElementName() + " on message " + writer.getName() + "(mem type = " + writer.getMemType() + ") to " + cmd.getValue());
            if (element instanceof DiscreteElement<?>) {
               ((DiscreteElement<?>) element).parseAndSet((ITestEnvironmentAccessor)Activator.getTestEnvironment(), cmd.getValue());
            } else {
               OseeLog.log(MessageSystemTestEnvironment.class,
                     Level.WARNING, "not a DiscreteElement: " + element.getName());
            }
         }
         writer.send(cmd.getMemType());

      } catch (Throwable t) {
         OseeLog.log(MessageSystemTestEnvironment.class,
               Level.WARNING,
               "Exception occurred when attempting to set element value for message " + cmd.getMessage(), t);
         throw new RemoteException(String.format("failed to set %s of %s to %s", cmd.getElement(), cmd.getMessage(),
               cmd.getValue()), t);
      }
   }

   public synchronized void zeroizeElement(ZeroizeElement cmd) throws RemoteException {
      final String msgName = cmd.getMessage();
      try {
         final Class<?> msgWriterClass = Activator.getTestEnvironment().getRuntimeManager().loadFromRuntimeLibraryLoader(msgName);
         /* check to see if an instance of a writer for the specified message exists */
         Message<?, ?, ?> writer = messageRequestor.getMessageWriter(msgWriterClass);
         if (writer == null) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.SEVERE,
                  String.format("Attempting to zeroize data for %s even though no previous writer exist", msgName));
            throw new Exception("Could not find the class definition for " + msgName + " message writer");
         }
         List<Object> elementPath = cmd.getElement();
         if (elementPath != null) {
            final Element element = writer.getElement(elementPath, cmd.getMemType());
            OseeLog.log(
                  MessageSystemTestEnvironment.class,
                  Level.INFO,
                  "Zeroizing message data for element " + element.getElementName() + " on message " + writer.getName() + "(mem type = " + writer.getMemType());
            element.zeroize();
         } else {
            writer.zeroize();
         }
         writer.send(cmd.getMemType());

      } catch (Throwable t) {
         OseeLog.log(MessageSystemTestEnvironment.class,
               Level.WARNING,
               "Exception occurred when attempting to set element value for message " + cmd.getMessage(), t);
         throw new RemoteException(String.format("failed to zeroize element %s on %s", cmd.getElement(),
               cmd.getMessage()), t);
      }
   }

   /**
    * Handles subscription request from remote clients.
    * 
    * @return a {@link org.eclipse.osee.ote.message.MessageState} object detailing the current message state as it
    *         exists in the environment
    * @see IRemoteMessageService
    */
   public synchronized SubscriptionDetails subscribeToMessage(final SubscribeToMessage cmd) throws RemoteException {
      if (terminated) {
         throw new IllegalStateException("tool service has been terminated");
      }
      final String name = cmd.getMessage();
      UserTestSessionKey key = null;
      Class<?> msgClass;
      try {

         msgClass = Activator.getTestEnvironment().getRuntimeManager().loadFromRuntimeLibraryLoader(name);
      } catch (ClassNotFoundException e) {
         throw new RemoteException(String.format("could find %s", name), e);
      }
      try {

         /* check to see if an instance of a writer for the specified message exists */
         Message<?, ?, ?> msgInstance =
            cmd.getMode() == MessageMode.READER ? messageRequestor.getMessageReader(msgClass) : messageRequestor.getMessageWriter(msgClass);
            if (msgInstance == null) {
               throw new Exception("Could not instantiate reader for " + name);
            }
            final MemType type = cmd.getType();
            if (!((MessageSystemTestEnvironment)Activator.getTestEnvironment()).isPhysicalTypeAvailable(type)) {
               // the message can't exist in this environment return null;
               return null;
            }

            /* ask the client for an address when given the message name and mem type */
            IMsgToolServiceClient reference = cmd.getCallback();

            key = reference.getTestSessionKey();
            final InetSocketAddress address = reference.getAddressByType(msgInstance.getMessageName(), type.ordinal());
            if (address == null) {
               throw new Exception(
                     "client callback for user " + key.getUser().getName() + " returned a null address when subscribing to " + name);
            }
            OseeLog.log(
                  MessageSystemTestEnvironment.class,
                  Level.INFO,
                  String.format("Client %s at %s is subscribing to message %s: current mem=%s", key.getUser().getName(),
                        address.toString(), name, type));

            EnumMap<MemType, EnumMap<MessageMode, SubscriptionRecord>> memToModeMap = messageMap.get(name);
            if (memToModeMap == null) {
               memToModeMap = new EnumMap<MemType, EnumMap<MessageMode, SubscriptionRecord>>(MemType.class);
               messageMap.put(name, memToModeMap);
            }
            EnumMap<MessageMode, SubscriptionRecord> modeMap = memToModeMap.get(type);
            if (modeMap == null) {
               modeMap = new EnumMap<MessageMode, SubscriptionRecord>(MessageMode.class);
               memToModeMap.put(type, modeMap);
            }
            SubscriptionRecord record = modeMap.get(cmd.getMode());
            ClientInfo client = new ClientInfo(reference, address);
            /* see if we have a listener already created for the specified message*/
            if (record != null) {
               /*
                * make sure the listener is still registered for message update. This should always be
                * the case
                */
               if (record.isRegistered()) {
                  OseeLog.log(MessageSystemTestEnvironment.class,
                        Level.SEVERE,
                        String.format("Existing listener for %s (mem = %s) is not registered for updates", name, type));
               }
               /* there is atleast one client already registered, add this one as well */
               record.addClient(client);
            } else {
               /* this is the first subscription request for this message */
               assert (name.equals(msgInstance.getClass().getName()));
               msgInstance.setMemTypeActive(type);
               record = new SubscriptionRecord(msgInstance, type, cmd.getMode(), client);
               modeMap.put(cmd.getMode(), record);
            }

            /*
             * return the message state back to the client. if both a reader and a writer exist then
             * always favor the writer
             */
            return new SubscriptionDetails(record.key, msgInstance.getActiveDataSource(type).toByteArray(),
                  msgInstance.getAvailableMemTypes());

      } catch (Throwable ex) {
         OseeLog.log(MessageSystemTestEnvironment.class,
               Level.WARNING, "Exception occurred when subscribing to " + name, ex);
         if (key != null) {
            throw new RemoteException("User " + key.getUser().getName() + "Could not subscribe to message " + name, ex);
         } else {
            throw new RemoteException("Could not subscribe to message " + name, ex);
         }
      }
   }

   //   public synchronized int changeSubscription(final ChangeSubscription cmd) throws RemoteException {
   //      final String name = cmd.getMsgName();
   //      final MemType oldMemType = MemType.values()[cmd.getOldMemTypeOrdinal()];
   //      final MemType newMemType = MemType.values()[cmd.getNewMemTypeOrdinal()];
   //      OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
   //            String.format("changing subscription for %s from %s to %s", name, oldMemType, newMemType));
   //      OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
   //            String.format("old address = %s. new address = %s", cmd.getOldAddress(), cmd.getNewAddress()));
   //
   //      final EnumMap<MemType, EnumMap<MessageMode, SubscriptionRecord>> memToModeMap = messageMap.get(name);
   //
   //      if (memToModeMap != null) {
   //         final EnumMap<MessageMode, SubscriptionRecord> modeMap = memToModeMap.get(oldMemType);
   //         if (modeMap != null) {
   //            SubscriptionRecord record = modeMap.get(cmd.getMode());
   //            if (record != null) {
   //               /* remove the old destination address from the listener */
   //               ClientInfo client = record.findClient(cmd.getOldAddress());
   //               if (client == null) {
   //                  OseeLog.log(MessageSystemTestEnvironment.class,
   //                        Level.SEVERE, "we didn't find a client");
   //               }
   //               record.removeClient(client);
   //               if (record.clients.isEmpty()) {
   //                  record.unregister();
   //                  modeMap.remove(cmd.getMode());
   //                  if (modeMap.isEmpty()) {
   //                     modeMap.remove(oldMemType);
   //                  }
   //               }
   //               EnumMap<MessageMode, SubscriptionRecord> newModeMap = memToModeMap.get(newMemType);
   //               if (newModeMap == null) {
   //                  newModeMap = new EnumMap<MessageMode, SubscriptionRecord>(MessageMode.class);
   //                  memToModeMap.put(newMemType, newModeMap);
   //               }
   //               SubscriptionRecord newListener = newModeMap.get(cmd.getMode());
   //               client = new ClientInfo(client.getRemoteReference(), cmd.getNewAddress());
   //               if (newListener == null) {
   //                  record.msg.setMemTypeActive(newMemType);
   //                  newListener = new SubscriptionRecord(record.msg, newMemType, record.key.getMode(), client);
   //                  newModeMap.put(cmd.getMode(), newListener);
   //               } else {
   //                  newListener.addClient(client);
   //               }
   //               return newMemType.ordinal();
   //            } else {
   //               OseeLog.log(
   //                     MessageSystemTestEnvironment.class,
   //                     "org.eclipse.osee.ote.message",
   //                     Level.WARNING,
   //                     String.format(
   //                           "Can't change registration for %s from %s to %s: Subscription not for message mode of %s",
   //                           name, oldMemType, newMemType, cmd.getMode()));
   //               return oldMemType.ordinal();
   //            }
   //         } else {
   //            OseeLog.log(
   //                  MessageSystemTestEnvironment.class,
   //                  "org.eclipse.osee.ote.message",
   //                  Level.WARNING,
   //                  String.format("Can't change registration for %s from %s to %s: Subscription not current mem type",
   //                        name, oldMemType, newMemType));
   //            return oldMemType.ordinal();
   //         }
   //      } else {
   //         OseeLog.log(
   //               MessageSystemTestEnvironment.class,
   //               "org.eclipse.osee.ote.message",
   //               Level.WARNING,
   //               String.format("Can't change registration for %s from %s to %s: No subscriptions for this message", name,
   //                     oldMemType, newMemType));
   //         return oldMemType.ordinal();
   //      }
   //   }

   public synchronized void unsubscribeToMessage(final UnSubscribeToMessage cmd) throws RemoteException {
      final String name = cmd.getMessage();
      final MemType type = cmd.getMemTypeOrdinal();



      final EnumMap<MemType, EnumMap<MessageMode, SubscriptionRecord>> memToModeMap = messageMap.get(name);
      if (memToModeMap == null) {
         /* no listeners for this message so return */
         return;
      }
      final EnumMap<MessageMode, SubscriptionRecord> modeMap = memToModeMap.get(type);
      if (modeMap == null) {
         throw new RemoteException(String.format("no subscription appears to exist for %s in %s mode", name,
               type.name()));
      }
      final SubscriptionRecord record = modeMap.get(cmd.getMode());
      ClientInfo client = record.findClient(cmd.getClient());
      OseeLog.log(
            MessageSystemTestEnvironment.class,
            Level.INFO,
            String.format("client at %s is unsubscribing to the %s for %s(%s)", client.ipAddress.toString(),
                  cmd.getMode(), name, type));

      if (record != null) {
         /* remove the client address from the listener's client list */

         record.removeClient(client);

         /*
          * if the listener has no more clients then remove the listener and unregister the listener
          * for message updates.
          */
         if (record.clients.isEmpty()) {
            OseeLog.log(
                  MessageSystemTestEnvironment.class,
                  Level.INFO,
                  String.format("No longer listening for updates for message %s. Final update count=%d", name,
                        record.updateCount));
            record.unregister();
            record.msg.setMemTypeInactive(type);
            messageRequestor.remove(record.msg);
            modeMap.remove(cmd.getMode());
            memToModeMap.remove(type);
            assert (!memToModeMap.containsKey(type));
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.interfaces.IRemoteMessageService#startRecording(org.eclipse.osee.ote.message.commands.RecordCommand)
    */
   public synchronized boolean startRecording(RecordCommand cmd) throws RemoteException {
      if (terminated) {
         throw new IllegalStateException("tool service has been terminated");
      }
      String user;
      try {
         user = cmd.getClient().getTestSessionKey().getUser().getName();
      } catch (Exception ex) {
         OseeLog.log(MessageSystemTestEnvironment.class,
               Level.WARNING, "Problems retrieving the active user", ex);
         user = "N.A.";
      }
      try {
         LinkedList<MessageRecordConfig> msgsToRecord = new LinkedList<MessageRecordConfig>();
         for (MessageRecordDetails details : cmd.getMsgsToRecord()) {
            String name = details.getName();
            final Class<?> msgClass = Activator.getTestEnvironment().getRuntimeManager().loadFromRuntimeLibraryLoader(name);
            /* check to see if an instance of a writer for the specified message exists */
            Message<?, ?, ?> reader = messageRequestor.getMessageReader(msgClass);
            if (reader == null) {
               throw new RemoteException("Could not instantiate reader for " + name);
            }
            MemType type = details.getType();
            List<List<Object>> elementNames = details.getBodyElementNames();
            ArrayList<Element> elementsToRecord = new ArrayList<Element>(elementNames.size());
            for (List<Object> elementName : elementNames) {
               final Element element = reader.getElement(elementName, type);
               if(element == null){

               } else {
                  if (!element.isNonMappingElement()) {
                     elementsToRecord.add(element);
                  }
               }
            }

            List<List<Object>> headerElementNames = details.getHeaderElementNames();
            ArrayList<Element> headerElementsToRecord = new ArrayList<Element>(headerElementNames.size());
            Element[] headerElements = reader.getActiveDataSource(type).getMsgHeader().getElements();
            if(headerElements != null && headerElementNames != null){
               for (List<Object> elementName : headerElementNames) {
                  if(elementName.size() == 2){
                     Object obj = elementName.get(1);
                     if(obj instanceof String){
                        String headerName =  (String)obj;
                        for(Element element:headerElements){
                           if(element.getElementName().equals(headerName)){
                              headerElementsToRecord.add(element);
                           }
                        }
                     }
                  }
               }
            }
            MessageRecordConfig config =
               new MessageRecordConfig(reader, type, details.getHeaderDump(), headerElementsToRecord.toArray(new Element[headerElementsToRecord.size()]),
                     details.getBodyDump(), elementsToRecord.toArray(new Element[elementsToRecord.size()]));
            msgsToRecord.add(config);
         }
         recorderOutputChannel.connect(cmd.getDestAddress());
         recorder.startRecording(msgsToRecord, recorderOutputChannel);
         OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
               "Recording start by user " + user + ", sending recorder output to " + cmd.getDestAddress().toString());
         return true;
      } catch (Throwable ex) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
               "Exception while starting message recording for user " + user, ex);
         throw new RemoteException("failed to start recording", ex);
      }

   }

   public synchronized InetSocketAddress getRecorderSocketAddress() throws RemoteException {
      if (terminated) {
         throw new IllegalStateException("tool service has been terminated");
      }
      if (!recorderOutputChannel.isOpen()) {
         throw new RemoteException("Recorder output channel is closed");
      }
      final DatagramSocket socket = recorderOutputChannel.socket();

      return new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort());
   }

   public synchronized InetSocketAddress getMsgUpdateSocketAddress() throws RemoteException {
      if (terminated) {
         throw new IllegalStateException("tool service has been terminated");
      }
      final DatagramSocket socket = channel.socket();
      return new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort());
   }

   public void stopRecording() throws RemoteException {
      if (terminated) {
         throw new IllegalStateException("tool service has been terminated");
      }
      if (recorder.isRecording()) {
         try {
            recorder.stopRecording(false);
         } catch (IOException e) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.INFO, "Exception while stopping message recording", e);
            throw new RemoteException("could not stop recorder", e);
         } catch (InterruptedException e) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.INFO, "Exception while stopping message recording", e);
            throw new RemoteException("could not stop recorder", e);
         }
         try {
            recorderOutputChannel.disconnect();
         } catch (IOException e) {
            throw new RemoteException("could not disconnect recorder output channel", e);
         }
      }

   }

   /**
    * terminates the message tool service
    */
   public void terminateService() {
      if (terminated) {
         return;
      }
      OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
      "terminate message tool service");
      try {
         for (EnumMap<MemType, EnumMap<MessageMode, SubscriptionRecord>> memToModeMap : messageMap.values()) {
            for (EnumMap<MessageMode, SubscriptionRecord> modeMap : memToModeMap.values()) {
               for (SubscriptionRecord listener : modeMap.values()) {
                  /* unregister the listenr for message updates */
                  listener.unregister();
                  if (!listener.clients.isEmpty()) {
                     OseeLog.log(MessageSystemTestEnvironment.class,
                           Level.WARNING, "Message Watch clients still exist while terminateing message watch service");
                  }
               }
               modeMap.clear();
            }
            memToModeMap.clear();
         }
         messageMap.clear();
         cancelledSubscriptions.clear();
         try {
            channel.close();
         } catch (IOException ex) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.SEVERE, ex.getMessage(), ex);
         }
         if (recorder != null && recorder.isRecording()) {
            try {
               recorder.stopRecording(false);
            } catch (IOException e) {
               OseeLog.log(MessageSystemTestEnvironment.class,
                     Level.SEVERE, "failed to stop recording", e);
            } catch (InterruptedException e) {
               OseeLog.log(MessageSystemTestEnvironment.class,
                     Level.SEVERE, "failed to stop recording", e);
            }
         }

         try {
            recorderOutputChannel.close();
         } catch (IOException ex) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.SEVERE, ex.getMessage(), ex);
         }
      } finally {
         terminated = true;
      }
      OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
      "terminated message tool service");
   }

   /**
    * sends the message update buffer to all of the specified addresses data
    * 
    * @param sendToList
    * @param msgName
    * @param buffer
    */
   private void xmitMsg(final Collection<ClientInfo> sendToList, final String msgName, final ByteBuffer buffer) throws IOException {
      assert (buffer.position() == 0);
      assert (buffer.limit() > 0);
      int limit = buffer.limit();
      for (ClientInfo client : sendToList) {
         if (debugEnabled) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.INFO, String.format("sending update for message %s to %s", msgName, client.toString()));
         }
         if (client == null) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.INFO, String.format("client was null %s", msgName, client.toString()));
         } else if (client.getIpAddress() == null) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.INFO, String.format("client ip address is null %s to %s", msgName, client.toString()));
         } else {

            channel.send(buffer, client.getIpAddress());
         }
         /*
          * rewind the buffer for next address since we are sending the same data to each client
          */
         buffer.rewind();
      }

   }

   public EnumSet<MemType> getAvailablePhysicalTypes() {
      final EnumSet<MemType> available = EnumSet.noneOf(MemType.class);
      for (MemType type : MemType.values()) {
         if (((MessageSystemTestEnvironment)Activator.getTestEnvironment()).isPhysicalTypeAvailable(type)) {
            available.add(type);
         }
      }
      return available;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.interfaces.IRemoteMessageService#setReaderWriterMode(org.eclipse.osee.ote.message.tool.MessageMode)
    */
   public synchronized SubscriptionDetails setReaderWriterMode(SetMessageModeCmd cmd) throws RemoteException {
      final String name = cmd.getName();

      try {
         final EnumMap<MemType, EnumMap<MessageMode, SubscriptionRecord>> memToModeMap = messageMap.get(name);
         if (memToModeMap == null) {
            throw new IllegalStateException("No subscriptions registered for message " + name);
         }
         final EnumMap<MessageMode, SubscriptionRecord> modeMap = memToModeMap.get(cmd.getType());
         if (modeMap == null) {
            throw new IllegalStateException("Could not find record for Mem Type of " + cmd.getType() + " for message ");
         }
         final SubscriptionRecord record = modeMap.get(cmd.getOldMode());
         if (record == null) {
            throw new IllegalStateException("Could not find record for " + cmd.getOldMode() + " of the message ");
         }
         ClientInfo client = record.findClient(cmd.getClient());
         record.removeClient(client);
         final InetSocketAddress address = client.ipAddress;
         OseeLog.log(
               MessageSystemTestEnvironment.class,
               Level.INFO,
               String.format("Client at %s is changing message mode for %s from %s to %s", address.toString(), name,
                     cmd.getOldMode(), cmd.getNewMode()));
         if (record.clients.isEmpty()) {
            record.unregister();
            messageRequestor.remove(record.msg);
            modeMap.remove(cmd.getOldMode());
         }
         SubscriptionRecord newRecord = modeMap.get(cmd.getNewMode());
         if (newRecord == null) {
            final Class<?> msgWriterClass = Activator.getTestEnvironment().getRuntimeManager().loadFromRuntimeLibraryLoader(name);
            /* check to see if an instance of a writer for the specified message exists */
            Message<?, ?, ?> newMsg =
               cmd.getNewMode() == MessageMode.WRITER ? messageRequestor.getMessageWriter(msgWriterClass) : messageRequestor.getMessageReader(msgWriterClass);
               if (newMsg == null) {
                  throw new ClassNotFoundException(
                        "Could not find class for the " + cmd.getNewMode().toString() + " of the message");
               }
               OseeLog.log(
                     MessageSystemTestEnvironment.class,
                     Level.INFO,
                     "Adding a subscription to: " + newMsg.getName() + " type[" + record.key.getType() + "] mode[" + cmd.getNewMode() + "]");
               newRecord = new SubscriptionRecord(newMsg, record.key.getType(), cmd.getNewMode(), client);
               newMsg.setMemTypeActive(record.key.getType());
               modeMap.put(cmd.getNewMode(), newRecord);
         } else {
            newRecord.addClient(client);
         }

         OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
               "success changing reader/writer mode. New mode = " + newRecord.key.getMode());
         return new SubscriptionDetails(newRecord.key, newRecord.msg.getActiveDataSource(cmd.getType()).toByteArray(), newRecord.msg.getAvailableMemTypes());
      } catch (Throwable t) {
         String title = "Could not set reader/writer for message " + name;
         OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
               t.getMessage(), t);
         throw new RemoteException(title, t);
      }
   }

   public Map<String, Throwable> getCancelledSubscriptions() {
      return cancelledSubscriptions;
   }

   public void reset() {
   }
}
