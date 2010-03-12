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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.enums.MemType;

/**
 * A thread that listens for activity on a set of channels and then
 * dispatches any recieved UDP packets to the appropriate worker threads
 * 
 * @author Ken J. Aguilar
 */
public final class UpdateDispatcher {
	/** * Class Instance Fields ** */
	private final EnumMap<MemType, DatagramChannel> channelMap = new EnumMap<MemType, DatagramChannel>(MemType.class);

	private final InetSocketAddress remoteAddress;
	private final Object gate = new Object();
	private final Selector channelSelector;
	private volatile boolean running = false;

	private final Runnable runner = new Runnable() {

		@Override
		public void run() {

			running = true;
			try {
				while (running) {
					/* check to see if something has interrupted this thread */
					if (Thread.interrupted()) {
						OseeLog.log(Activator.class, Level.FINE, "Channel Listener Thread interrupted");
						running = false;
					} else {
						/*
						 * wait for channel activity on all channels registered
						 * with this selector
						 */
						final int readyCount = channelSelector.select();
						if (readyCount != 0) {
							/* get the set of readable channels */
							final Set<SelectionKey> readyChannels = channelSelector.selectedKeys();

							/* iterate through the set of readable channels */
							final Iterator<SelectionKey> keys = readyChannels.iterator();
							while (keys.hasNext()) {
								final SelectionKey key = keys.next();
								/* make sure the channel is still valid */
								if (key.isValid() && key.isReadable()) {
									((ChannelProcessor) key.attachment()).process((DatagramChannel) key.channel());
								}
								keys.remove();
							}
						}
						synchronized (gate) {
							/*
							 * do this to prevent the current thread from
							 * entering the channelSelector.select() method
							 * during registration of channels with the
							 * selector. Not doing this will cause deadlock
							 */
						}
					}
				}
			} catch (InterruptedException ie) {
				/*
				 * something has interrupted us, most likely we need to shut
				 * down. Catching the exception clears the interrupted flag
				 */
				OseeLog.log(Activator.class, Level.INFO, "Channel Listener Interrupted... Shutting down");
			} catch (IOException ioe) {
				OseeLog.log(Activator.class, Level.INFO, "IOException occurred in channel listening thread... shutting down ", ioe);
			} catch (Throwable t) {
				OseeLog.log(Activator.class, Level.SEVERE, "Unusual exception occurred in channel listening thread... shutting down ", t);
			}
			running = false;
			OseeLog.log(Activator.class, Level.INFO, "Channel Listener thread has terminated");
		}

	};

	private final Thread thread = new Thread(runner, "Message Update Dispatcher Thread");


	public UpdateDispatcher(InetSocketAddress remoteAddress) throws IOException {
		this.remoteAddress = remoteAddress;
		thread.setDaemon(false);
		channelSelector = Selector.open();
	}

	public void start() {
		thread.start();
	}


	public boolean isRunning() {
		return running;
	}

	public DatagramChannel getChannel(MemType type) {
		return channelMap.get(type);
	}

	public SelectionKey addChannel(InetAddress localAddress, int port, MemType type, ChannelProcessor processor)
			throws IOException {
		final DatagramChannel channel = DatagramChannel.open();
		channel.configureBlocking(false);
		channel.socket().bind(new InetSocketAddress(localAddress, port));
		channel.connect(remoteAddress);
		if (channelMap.put(type, channel) != null) {
			OseeLog.log(MessageSubscriptionService.class, Level.WARNING, "A previous channel was replaced");
		}
		synchronized (gate) {
			channelSelector.wakeup();
			return channel.register(channelSelector, SelectionKey.OP_READ, processor);
		}
	}
	/**
	 * terminates this thread in a graceful manner and attempts to release
	 * resources
	 */
	public void close() {
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException ex) {
			OseeLog.log(Activator.class, Level.WARNING, "Interrupted while joining", ex);
		} finally {
			OseeLog.log(Activator.class, Level.INFO, "clearing pool");
			/* release IO resources */
			try {
				channelSelector.close();
			} catch (Exception ex) {
				OseeLog.log(Activator.class, Level.WARNING, "Exception closing selector", ex);
			} finally {
				for (final MemType type : channelMap.keySet()) {
					try {
						final DatagramChannel channel = channelMap.get(type);
						if (channel != null) {
							channel.close();
						}

					} catch (Throwable ex) {
						OseeLog.log(Activator.class, Level.WARNING, "could not close channel for " + type, ex);
					}
				}
				channelMap.clear();
			}
		}
	}
}