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
package org.eclipse.osee.ote.client.msg.core.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.client.msg.core.db.AbstractMessageDataBase;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageDefinitionProvider;
import org.eclipse.osee.ote.message.commands.RecordCommand;
import org.eclipse.osee.ote.message.commands.RecordCommand.MessageRecordDetails;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystem;
import org.eclipse.osee.ote.message.tool.IFileTransferHandle;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.osee.ote.message.tool.TransferConfig;
import org.eclipse.osee.ote.message.tool.UdpFileTransferHandler;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.service.ITestConnectionListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Ken J. Aguilar
 */
public class MessageSubscriptionService implements IOteMessageService, ITestConnectionListener, IMsgToolServiceClient {

   /** * Static Fields ** */
   private static final int MAX_CONCURRENT_WORKER_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 4);

   private final InetAddress localAddress;
   private final List<MessageSubscription> subscriptions = new CopyOnWriteArrayList<MessageSubscription>();
   private volatile AbstractMessageDataBase msgDatabase;
   private UdpFileTransferHandler fileTransferHandler;
   private volatile boolean connected = false;

   private final ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CONCURRENT_WORKER_THREADS,
      new ThreadFactory() {
         private final ThreadGroup group =
            new ThreadGroup(Thread.currentThread().getThreadGroup(), "Msg Watch Workers");
         private int count = 1;

         @Override
         public Thread newThread(Runnable arg0) {
            Thread thread = new Thread(group, arg0, "Msg Watch Wrkr - " + count++);
            thread.setDaemon(false);
            return thread;
         }
      });

   /**
    * Monitors a set of channels for message updates and dispatches the updates to worker threads
    */
   private UpdateDispatcher dispatcher = null;
//   private volatile IRemoteMessageService service;

   private volatile IOteClientService clientService;

   public void start(){
	   clientService.addConnectionListener(this);
   }
   
   public void stop(){
	   clientService.removeConnectionListener(this);
   }
   
   public void bindOteClientService(IOteClientService clientService){
	   this.clientService = clientService;
   }
   
   public void unbindOteClientService(IOteClientService clientService){
	   this.clientService = null;
   }
   
   public MessageSubscriptionService() throws IOException {
      localAddress = InetAddress.getLocalHost();
      msgDatabase = new MessageDatabase(this);
      OseeLog.log(Activator.class, Level.INFO,
         "OTE client message service started on: " + localAddress.getHostAddress());
   }

   @Override
   public synchronized IMessageSubscription subscribe(String name) {
      MessageSubscription subscription = new MessageSubscription(this);
      subscription.bind(name);
      if (msgDatabase != null) {
         subscription.attachMessageDb(msgDatabase);
         if(connected){
            subscription.attachService();
         }
      }
      subscriptions.add(subscription);
      return subscription;
   }
   
   @Override
   public IMessageSubscription subscribe(String name, MessageMode mode) {
	      return subscribe(name, (DataType)null, mode);
   }

   @Override
   public IMessageSubscription subscribe(String name, DataType dataType,
		   MessageMode mode) {
	   MessageSubscription subscription = new MessageSubscription(this);
	   subscription.bind(name, dataType, mode);
	   if (msgDatabase != null) {
		   subscription.attachMessageDb(msgDatabase);
		   if(connected){
		      subscription.attachService();
		   }
	   }
	   subscriptions.add(subscription);
	   return subscription;
   }
   
   @Override
   public IMessageSubscription subscribe(String name, String dataType,
		   MessageMode mode) {
	   MessageSubscription subscription = new MessageSubscription(this);
	   subscription.bind(name, dataType, mode);
	   if (msgDatabase != null) {
		   subscription.attachMessageDb(msgDatabase);
		   if(connected) {
		      subscription.attachService();
		   }
	   }
	   subscriptions.add(subscription);
	   return subscription;
   }

   /**
    * Shuts down the client message service. All worker threads will be terminated and all IO resources will be closed.
    */
   public void shutdown() {
      OseeLog.log(MessageSubscriptionService.class, Level.INFO, "shutting down subscription service");
      clientService.removeConnectionListener(this);
      shutdownDispatcher();
      threadPool.shutdown();
      try {
         threadPool.awaitTermination(5, TimeUnit.SECONDS);
      } catch (InterruptedException ex1) {
         OseeLog.log(Activator.class, Level.WARNING, ex1.toString(), ex1);
      }
   }

   @Override
   public synchronized void onConnectionLost(IServiceConnector connector) {
      OseeLog.log(Activator.class, Level.INFO, "connection lost: ote client message service halted");
      shutdownDispatcher();
      msgDatabase.detachService();
      for (MessageSubscription subscription : subscriptions) {
         subscription.detachService();
      }
      connected = false;
   }

   @Override
   public synchronized void onPostConnect(ConnectionEvent event) {
      assert msgDatabase != null;
      connected = true;
      OseeLog.log(Activator.class, Level.INFO, "connecting OTE client message service");
      if (event.getEnvironment() instanceof ITestEnvironmentMessageSystem) {
         ITestEnvironmentMessageSystem env = (ITestEnvironmentMessageSystem) event.getEnvironment();
         try{
            dispatcher = new UpdateDispatcher(MessageServiceSupport.getMsgUpdateSocketAddress());
            try {
               createProccessors();
            } catch (Exception e) {
               OseeLog.log(MessageSubscriptionService.class, Level.SEVERE, "failed to create update processors", e);
               return;
            }

            msgDatabase.attachToService(this);
            for (MessageSubscription subscription : subscriptions) {
               subscription.attachService();
            }
            dispatcher.start();
         } catch (IOException ex){
            OseeLog.log(MessageSubscriptionService.class, Level.SEVERE, "failed to create update processors", ex);
         }
      }
   }

   private void createProccessors() throws IOException {
      Set<? extends DataType> availableTypes = MessageServiceSupport.getAvailablePhysicalTypes();

      for (DataType type : availableTypes) {
         final ChannelProcessor handler =
            new ChannelProcessor(1, type.getToolingBufferSize(), threadPool, msgDatabase, type);
         dispatcher.addChannel(localAddress, 0, type, handler);
      }
   }

   private void shutdownDispatcher() {
      if (dispatcher != null && dispatcher.isRunning()) {
         try {
            dispatcher.close();
         } catch (Throwable ex) {
            OseeLog.log(MessageSubscriptionService.class, Level.WARNING, "exception while closing down dispatcher", ex);
         } finally {
            dispatcher = null;
         }
      }
   }

   @Override
   public synchronized void onPreDisconnect(ConnectionEvent event) {
      msgDatabase.detachService();
      for (MessageSubscription subscription : subscriptions) {
         subscription.detachService();
      }
      shutdownDispatcher();
      connected = false;
   }

   @Override
   public void changeIsScheduled(String msgName, boolean isScheduled) throws RemoteException {

   }

   @Override
   public void changeRate(String msgName, double rate) throws RemoteException {

   }

   @Override
   public InetSocketAddress getAddressByType(String messageName, DataType dataType) throws RemoteException {
      if(dispatcher == null){
         return null;
      }
      final DatagramChannel channel = dispatcher.getChannel(dataType);
      if(channel == null){
         return null;
      }
      return new InetSocketAddress(localAddress, channel.socket().getLocalPort());
   }

   @Override
   public UUID getTestSessionKey() throws RemoteException {
      return clientService.getSessionKey();
   }

   public void addMessageDefinitionProvider(MessageDefinitionProvider provider){
	   for (MessageSubscription subscription : subscriptions) {
		   if(!subscription.isResolved()){
			   subscription.attachMessageDb(msgDatabase);
		   }
	   }
   }
   
   public void removeMessageDefinitionProvider(MessageDefinitionProvider provider){
	   for (MessageSubscription subscription : subscriptions) {
		   if(subscription.isResolved()){
			   Class<? extends Message> msg = null;
			   Bundle hostBundle = null;
			   try {
				   msg = ExportClassLoader.getInstance().loadClass(subscription.getMessageClassName()).asSubclass(Message.class);
				   hostBundle = FrameworkUtil.getBundle(msg.getClass());
			   } catch (ClassNotFoundException e) {
			   } finally{
				   if(msg == null || hostBundle == null){
					   subscription.detachMessageDb(msgDatabase);
				   }
			   }
		   }
	   }
   }
   
   @Override
   public synchronized IFileTransferHandle startRecording(String fileName, List<MessageRecordDetails> list) throws FileNotFoundException, IOException {
      if(!connected){
         throw new IllegalStateException("can't record: not connected to test server");
      }
      if (fileTransferHandler == null) {
         fileTransferHandler = new UdpFileTransferHandler();
         fileTransferHandler.start();
      }
      int port = PortUtil.getInstance().getValidPort();
      // get the address of the socket the message recorder is going to write
      // data to
      InetSocketAddress recorderOutputAddress = MessageServiceSupport.getRecorderSocketAddress();

      // setup a transfer from a socket to a file
      TransferConfig config =
         new TransferConfig(fileName, recorderOutputAddress, new InetSocketAddress(InetAddress.getLocalHost(), port),
            TransferConfig.Direction.SOCKET_TO_FILE, 128000);
      IFileTransferHandle handle = fileTransferHandler.registerTransfer(config);

      // send the command to start recording
      RecordCommand cmd =
         new RecordCommand(this.getTestSessionKey(), new InetSocketAddress(InetAddress.getLocalHost(), port), list);
      MessageServiceSupport.startRecording(cmd);
      OseeLog.log(
         Activator.class,
         Level.INFO,
         "recording started with " + list.size() + " entries, recorder output socket=" + recorderOutputAddress.toString());
      return handle;
   }

   @Override
   public synchronized void stopRecording() throws RemoteException, IOException {
      try {
         MessageServiceSupport.stopRecording();
      } finally {
         if (fileTransferHandler != null && fileTransferHandler.hasActiveTransfers()) {
            fileTransferHandler.stopAllTransfers();
         }
         fileTransferHandler = null;
      }
   }

   public AbstractMessageDataBase getMsgDatabase() {
      return msgDatabase;
   }

   public void removeSubscription(MessageSubscription subscription) {
      subscriptions.remove(subscription);
   }

   public boolean isConnected() {
      return connected;
   }

}
