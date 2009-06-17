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
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.osee.ote.client.msg.core.IMessageDbFactory;
import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.client.msg.core.db.AbstractMessageDataBase;
import org.eclipse.osee.ote.core.environment.UserTestSessionKey;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.message.commands.RecordCommand;
import org.eclipse.osee.ote.message.commands.RecordCommand.MessageRecordDetails;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystem;
import org.eclipse.osee.ote.message.tool.IFileTransferHandle;
import org.eclipse.osee.ote.message.tool.TransferConfig;
import org.eclipse.osee.ote.message.tool.UdpFileTransferHandler;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.IMessageDictionary;
import org.eclipse.osee.ote.service.IMessageDictionaryListener;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.service.ITestConnectionListener;

/**
 * @author author Ken J. Aguilar
 * 
 */
public class MessageSubscriptionService implements IOteMessageService, IMessageDictionaryListener, ITestConnectionListener, IMsgToolServiceClient {


	/** * Static Fields ** */
	private static final int MAX_CONCURRENT_WORKER_THREADS = Runtime.getRuntime().availableProcessors() + 1;

	private final InetAddress localAddress;
	private final LinkedList<MessageSubscription> subscriptions = new LinkedList<MessageSubscription>();
	private IMsgToolServiceClient exportedThis = null;
	private AbstractMessageDataBase msgDatabase;
	private UdpFileTransferHandler fileTransferHandler;

	private final ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CONCURRENT_WORKER_THREADS, new ThreadFactory() {
		private final ThreadGroup group = new ThreadGroup(Thread.currentThread().getThreadGroup(), "Msg Watch Workers");
		private int count = 1;

		public Thread newThread(Runnable arg0) {
			Thread thread = new Thread(group, arg0, "Msg Watch Wrkr - " + count++);
			thread.setDaemon(false);
			return thread;
		}

	});

	/**
	 * Monitors a set of channels for message updates and dispatches the updates
	 * to worker threads
	 */
	private UpdateDispatcher dispatcher = null;
	private IRemoteMessageService service;

	private final IMessageDbFactory messageDbFactory;
	private final OteClientServiceTracker tracker;
	private IOteClientService clientService;
	
	public MessageSubscriptionService(IMessageDbFactory messageDbFactory) throws IOException {
		tracker = new OteClientServiceTracker(this);
		this.messageDbFactory = messageDbFactory;
		localAddress = InetAddress.getLocalHost();
		OseeLog.log(Activator.class, Level.INFO, "OTE client message service started on: " + localAddress.getHostAddress());
		tracker.open();
	}

	void oteClientServiceAcquired(IOteClientService service) {
		clientService = service;
		clientService.addDictionaryListener(this);
		clientService.addConnectionListener(this);
	}
	
	void oteClientServiceLost() {
	
	}
	
	public synchronized IMessageSubscription subscribe(String name) {
		MessageSubscription subscription = new MessageSubscription(this);
		subscription.bind(name);
		if (msgDatabase != null) {
			subscription.attachMessageDb(msgDatabase);
			if (service != null) {
				subscription.attachService(service);
			}
		}
		subscriptions.add(subscription);
		return subscription;
	}

	/**
	 * Shuts down the client message service. All worker threads will be
	 * terminated and all IO resources will be closed.
	 */
	public void shutdown() throws IOException {
		if (clientService != null) {
			clientService.removeDictionaryListener(this);
			clientService.removeConnectionListener(this);
		}
		tracker.close();
		shutdownDispatcher();
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException ex1) {
			OseeLog.log(Activator.class, Level.WARNING, ex1.toString(), ex1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.service.ITestConnectionListener#onConnectionLost
	 * (org.eclipse.osee.connection.service.IServiceConnector,
	 * org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment)
	 */
	@Override
	public synchronized void onConnectionLost(IServiceConnector connector, IHostTestEnvironment testHost) {
		OseeLog.log(Activator.class, Level.INFO, "connection lost: ote client message service halted");
		shutdownDispatcher();
		msgDatabase.detachService(null);
		for (MessageSubscription subscription : subscriptions) {
			subscription.detachService(null);
		}
		exportedThis = null;
		service = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.service.ITestConnectionListener#onPostConnect(org
	 * .eclipse.osee.ote.service.ConnectionEvent)
	 */
	@Override
	public synchronized void onPostConnect(ConnectionEvent event) {
		assert msgDatabase != null;
		OseeLog.log(Activator.class, Level.INFO, "connecting OTE client message service");
		if (event.getEnvironment() instanceof ITestEnvironmentMessageSystem) {
			ITestEnvironmentMessageSystem env = (ITestEnvironmentMessageSystem) event.getEnvironment();
			try {
				service = env.getMessageToolServiceProxy();
				if (service == null) {
					throw new Exception("could not get message tool service proxy");
				}
				exportedThis = (IMsgToolServiceClient) event.getConnector().export(this);
			} catch (Exception e) {
				OseeLog.log(MessageSubscriptionService.class, Level.SEVERE, "failed to create exported Message Tool Client", e);
				service = null;
				exportedThis = null;
				return;
			}

			try {
				dispatcher = new UpdateDispatcher(service.getMsgUpdateSocketAddress());
			} catch (Exception e) {
				OseeLog.log(MessageSubscriptionService.class, Level.SEVERE, "failed to create update dispatcher", e);
				service = null;
				exportedThis = null;
				return;
			}

			try {
				createProccessors();
			} catch (Exception e) {
				OseeLog.log(MessageSubscriptionService.class, Level.SEVERE, "failed to create update processors", e);
				service = null;
				exportedThis = null;
				return;
			}

			msgDatabase.attachToService(service, exportedThis);
			for (MessageSubscription subscription : subscriptions) {
				subscription.attachService(service);
			}
			dispatcher.start();
		}
	}

	private void createProccessors() throws IOException {
		EnumSet<MemType> availableTypes = service.getAvailablePhysicalTypes();

		int port = PortUtil.getInstance().getConsecutiveValidPorts(availableTypes.size());
		for (MemType type : availableTypes) {
			final ChannelProcessor handler;
			switch (type) {
			case MUX:
				handler = new ChannelProcessor(20, 256, threadPool, msgDatabase, type);
				break;
			case MUX_LM:
				handler = new ChannelProcessor(5, 256, threadPool, msgDatabase, type);
				break;
			case PUB_SUB:
				handler = new ChannelProcessor(10, 65455, threadPool, msgDatabase, type);
				break;
			case WIRE_AIU:
				// pass through
			case WIRE_MP_DIRECT:
				// pass through
			case IGTTS_WIRE:
				handler = new ChannelProcessor(5, 512, threadPool, msgDatabase, type);
				break;
			case ETHERNET:
				handler = new ChannelProcessor(5, 2048, threadPool, msgDatabase, type);
				break;
			case SERIAL:
				handler = new ChannelProcessor(3, 10000, threadPool, msgDatabase, type);
				break;
			case TS_META_DATA:
				handler = new ChannelProcessor(3, 2048, threadPool, msgDatabase, type);
				break;
			default:
				throw new Error("no case for mem type of " + type);
			}
			dispatcher.addChannel(localAddress, port, type, handler);
			port++;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.service.ITestConnectionListener#onPreDisconnect(
	 * org.eclipse.osee.ote.service.ConnectionEvent)
	 */
	@Override
	public synchronized void onPreDisconnect(ConnectionEvent event) {
		msgDatabase.detachService(service);
		for (MessageSubscription subscription : subscriptions) {
			subscription.detachService(service);
		}
		try {
			event.getConnector().unexport(this);
		} catch (Exception e) {
			OseeLog.log(MessageSubscriptionService.class, Level.WARNING, "problems unexporting Message Tool Client", e);
		}
		shutdownDispatcher();
		exportedThis = null;
		service = null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient#
	 * changeIsScheduled(java.lang.String, boolean)
	 */
	@Override
	public void changeIsScheduled(String msgName, boolean isScheduled) throws RemoteException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient#changeRate
	 * (java.lang.String, double)
	 */
	@Override
	public void changeRate(String msgName, double rate) throws RemoteException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient#
	 * getAddressByType(java.lang.String, int)
	 */
	@Override
	public InetSocketAddress getAddressByType(String messageName, int memType) throws RemoteException {
		final DatagramChannel channel = dispatcher.getChannel(MemType.values()[memType]);
		OseeLog.log(Activator.class, Level.INFO, String.format("callback from remote msg manager: msg=%s, type=%s, ip=%s:%d\n", messageName,
				MemType.values()[memType], localAddress.toString(), channel.socket().getLocalPort()));

		return new InetSocketAddress(localAddress, channel.socket().getLocalPort());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient#
	 * getTestSessionKey()
	 */
	@Override
	public UserTestSessionKey getTestSessionKey() throws RemoteException {
		return clientService.getSessionKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.service.IMessageDictionaryListener#onDictionaryLoaded
	 * (org.eclipse.osee.ote.service.IMessageDictionary)
	 */
	@Override
	public void onDictionaryLoaded(IMessageDictionary dictionary) {
		msgDatabase = messageDbFactory.createMessageDataBase(dictionary);
		for (MessageSubscription subscription : subscriptions) {
			subscription.attachMessageDb(msgDatabase);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.service.IMessageDictionaryListener#onDictionaryUnloaded
	 * (org.eclipse.osee.ote.service.IMessageDictionary)
	 */
	@Override
	public void onDictionaryUnloaded(IMessageDictionary dictionary) {
		for (MessageSubscription subscription : subscriptions) {
			subscription.detachMessageDb(msgDatabase);
		}
		msgDatabase = null;
	}

	@Override
	public IFileTransferHandle startRecording(String fileName, List<MessageRecordDetails> list) throws FileNotFoundException, IOException {
		if (service == null) {
			throw new IllegalStateException("can't record: not connected to test server");
		}
		if (fileTransferHandler == null) {
			fileTransferHandler = new UdpFileTransferHandler();
			fileTransferHandler.start();
		}
		int port = PortUtil.getInstance().getValidPort();
		// get the address of the socket the message recorder is going to write
		// data to
		InetSocketAddress recorderOutputAddress = service.getRecorderSocketAddress();

		// setup a transfer from a socket to a file
		TransferConfig config = new TransferConfig(fileName, recorderOutputAddress, new InetSocketAddress(InetAddress.getLocalHost(), port),
				TransferConfig.Direction.SOCKET_TO_FILE, 128000);
		IFileTransferHandle handle = fileTransferHandler.registerTransfer(config);

		// send the command to start recording
		RecordCommand cmd = new RecordCommand(exportedThis, new InetSocketAddress(InetAddress.getLocalHost(), port), list);
		service.startRecording(cmd);
		OseeLog.log(Activator.class, Level.INFO, "recording started with " + list.size() + " entries, recorder output socket="
				+ recorderOutputAddress.toString());
		return handle;
	}

	@Override
	public void stopRecording() throws RemoteException, IOException {
		try {
			service.stopRecording();
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

	public IRemoteMessageService getService() {
		return service;
	}

	public void removeSubscription(MessageSubscription subscription) {
		subscriptions.remove(subscription);
	}
}
