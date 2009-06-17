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
package org.eclipse.osee.ote.service.core;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.service.Activator;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.IMessageDictionary;
import org.eclipse.osee.ote.service.IMessageDictionaryListener;
import org.eclipse.osee.ote.service.ITestConnectionListener;

/**
 * @author Ken J. Aguilar
 */
class ListenerNotifier {
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private CopyOnWriteArraySet<ITestConnectionListener> testConnectionListeners = new CopyOnWriteArraySet<ITestConnectionListener>();

	private final CopyOnWriteArraySet<IMessageDictionaryListener> dictionaryListeners = new CopyOnWriteArraySet<IMessageDictionaryListener>();

	boolean addTestConnectionListener(ITestConnectionListener listener) {
		return testConnectionListeners.add(listener);
	}

	boolean removeTestConnectionListener(ITestConnectionListener listener) {
		return testConnectionListeners.remove(listener);
	}

	public boolean addDictionaryListener(
			IMessageDictionaryListener listener) {
		return dictionaryListeners.add(listener);
	}

	public void removeDictionaryListener(IMessageDictionaryListener listener) {
		dictionaryListeners.remove(listener);
	}

	void notifyPostConnection(final ConnectionEvent event) {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				for (ITestConnectionListener listener : testConnectionListeners) {
					try {
						listener.onPostConnect(event);
					} catch (Exception ex) {
						OseeLog
						.log(
								Activator.class,
								Level.SEVERE,
								"exception notifying listener of post connect event",
								ex);
					}
				}
			}

		});
	}

	void notifyDisconnect(final ConnectionEvent event) {
		for (ITestConnectionListener listener : testConnectionListeners) {
			try {
				listener.onPreDisconnect(event);
			} catch (Exception ex) {
				OseeLog.log(Activator.class, Level.SEVERE,
						"exception notifying listener of disconnect event", ex);
			}
		}
	}

	void notifyConnectionLost(final IServiceConnector connector,
			final IHostTestEnvironment testHost) {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				for (ITestConnectionListener listener : testConnectionListeners) {
					try {
						listener.onConnectionLost(connector, testHost);
					} catch (Exception ex) {
						OseeLog
						.log(
								Activator.class,
								Level.SEVERE,
								"exception notifying listener of connection error event",
								ex);
					}
				}
			}
		});

	}

	void notifyDictionaryLoaded(final IMessageDictionary dictionary) {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				for (IMessageDictionaryListener listener : dictionaryListeners) {
					try {
						listener.onDictionaryLoaded(dictionary);
					} catch (Exception e) {
						Activator
						.log(
								Level.SEVERE,
								"exception in listener during dictionary load event notification",
								e);
					}
				}
			}

		});

	}

	void notifyDictionaryUnloaded(final IMessageDictionary dictionary) {
		for (IMessageDictionaryListener listener : dictionaryListeners) {
			try {
				listener.onDictionaryUnloaded(dictionary);
			} catch (Exception e) {
				Activator
				.log(
						Level.SEVERE,
						"exception in listener during dictionary unload event notification",
						e);
			}
		}
	}
}
