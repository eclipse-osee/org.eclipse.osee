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

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.connection.service.IConnectorListener;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.environment.TestEnvironmentConfig;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.service.Activator;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.IEnvironmentConfigurer;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.service.ITestConnectionListener;
import org.eclipse.osee.ote.service.ITestEnvironmentAvailibilityListener;
import org.eclipse.osee.ote.service.OteServiceProperties;
import org.eclipse.osee.ote.service.SessionDelegate;
import org.eclipse.osee.ote.service.TestSessionException;

public class TestClientServiceImpl implements IOteClientService, IConnectorListener {

	private static final String NO_USER_MSG = "a user has not been set";
	private final IConnectionService connectionService;
	private final HashMap<String, IServiceConnector> testHosts = new HashMap<>();
	private final ListenerNotifier listenerNotifier = new ListenerNotifier();
	private final HashSet<ITestEnvironmentAvailibilityListener> hostAvailabilityListeners =
			new HashSet<ITestEnvironmentAvailibilityListener>();
	private ClientSession session = null;
	private volatile boolean stopped = false;
	private TestHostConnection testConnection;

//	private final OteClientEndpointSend endpointSend;
//	private final OteClientEndpointReceive endpointReceive;

	public TestClientServiceImpl(IConnectionService connectionService) {
		this.connectionService = connectionService;
//		this.endpointReceive = endpointReceive;
//		this.endpointSend = endpointSend;
//		endpointSend.setTestClientService(this);
	}

	public void init() {
		connectionService.addListener(this);
	}

	@Override
	public void addEnvironmentAvailibiltyListener(ITestEnvironmentAvailibilityListener listener) {
		checkState();
		final Set<Entry<String, IServiceConnector>> entrySet;
		synchronized (this) {
			if (hostAvailabilityListeners.add(listener)) {
				entrySet = new HashSet<>(testHosts.entrySet());
			} else {
				entrySet = Collections.emptySet();
			}
		}
		for (Entry<String, IServiceConnector> entry : entrySet) {
			listener.environmentAvailable(entry.getValue(),
					new OteServiceProperties(entry.getValue()));
		}
	}

	@Override
	public  ConnectionEvent connect(IHostTestEnvironment testHost, IEnvironmentConfigurer configurer, TestEnvironmentConfig config, IProgressMonitor monitor) throws IllegalArgumentException, TestSessionException {
		checkState();
		final IServiceConnector connector;
		final ClientSession localSession;
		synchronized (this) {
			if (session == null) {
				throw new IllegalArgumentException("a user has not been set");
			}
			if (testConnection != null) {
				throw new IllegalStateException("already connected");
			}
			if (testHost == null) {
				throw new IllegalArgumentException("test host cannot be null");
			}
			connector = getConnector(testHost);
			localSession = session;
		}
		try {
			if (connector == null) {
				throw new IllegalStateException("Unable to find a connection.");
			}
			if(configurer.configure(testHost, new SubProgressMonitor(monitor, 95)) && !monitor.isCanceled()){
			   testConnection = localSession.connect(connector, testHost, config);
			   if (testConnection != null) {
			      // success
			      ConnectionEvent event = new ConnectionEvent(testHost, connector, testConnection.getConnectEnvironment(), testConnection.getSessionKey());
			      listenerNotifier.notifyPostConnection(event);
			      return event;
			   }
			}
		} catch (Exception e) {
			Activator.log(Level.SEVERE, "failed to establish connection", e);
			testConnection = null;
		}
		return null;
	}

	@Override
	public IServiceConnector getConnector(IHostTestEnvironment host) {
		try {
			EnhancedProperties properties = host.getProperties();
			if(properties != null){
			   String passedInId = (String) properties.getProperty("id");
			   for (IServiceConnector connector : testHosts.values()) {
			      String loopId = (String) connector.getProperty("id", "no");
			      if (passedInId != null && loopId != null && loopId.equals(passedInId)) {
			         return connector;
			      }
			   }
			}
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public void disconnect() throws TestSessionException {
		checkState();
		synchronized (this) {
			if (session == null) {
				throw new IllegalStateException(NO_USER_MSG);
			}
			if (testConnection == null) {
				return;
			}
			Activator.log(Level.INFO,
					"disconnecting from " + new OteServiceProperties(testConnection.getServiceConnector()).getStation());
			ITestEnvironment envirnonment = testConnection.getConnectEnvironment();

			if (!testConnection.getServiceConnector().ping()) {
				listenerNotifier.notifyConnectionLost(testConnection.getServiceConnector());
				testConnection = null;
			} else {
				ConnectionEvent event =
						new ConnectionEvent(this.getConnectedHost(), testConnection.getConnectedTestHost(), envirnonment,
								testConnection.getSessionKey());
				listenerNotifier.notifyDisconnect(event);
				try {
					session.disconnect(testConnection);
				} catch (Exception e) {
					throw new TestSessionException("could not properly disconnect from test environment", e);
				} finally {
					testConnection = null;
				}
			}
		}

	}

	/**
	 * stops this service
	 */
	public synchronized void stop() {
		connectionService.removeListener(this);
		if (session != null) {
			try {
				if (testConnection != null) {
					disconnect();
				}
			} catch (Exception e) {
				Activator.log(Level.SEVERE, "exception trying to disconnect during stop()", e);
			} finally {
				session.close();
			}
		}
		stopped = true;
		hostAvailabilityListeners.clear();
	}

	@Override
	public void addConnectionListener(ITestConnectionListener listener) {
		checkState();
		if (listenerNotifier.addTestConnectionListener(listener)) {
			if (session == null) {
				return;
			}
			try {
				final ConnectionEvent event;
				// we want to avoid holding a lock when we call onPostConnect due to potential deadlock. so lets do what we can
				// in a small synchronized block prior to calling onPostConnect
				synchronized (this) {
					// check to see if a connection already established
					if (testConnection  != null) {
						event = new ConnectionEvent(this.getConnectedHost(),
								testConnection.getServiceConnector(), testConnection.getConnectEnvironment(),
								testConnection.getSessionKey());
					} else {
						event = null;
					}
				}
				if (event != null) {
					listener.onPostConnect(event);
				}
			} catch (Exception e) {
				Activator.log(Level.SEVERE, "Exception notifying listener of connection event", e);
			}
		}
	}

	@Override
	public void removeConnectionListener(ITestConnectionListener listener) {
		listenerNotifier.removeTestConnectionListener(listener);
	}

	@Override
	public synchronized void removeEnvironmentAvailibiltyListener(ITestEnvironmentAvailibilityListener listener) {
		checkState();
		hostAvailabilityListeners.remove(listener);
	}

	private void checkState() {
		if (stopped) {
			throw new IllegalStateException("service stopped");
		}
	}

	@Override
	public synchronized void setUser(OSEEPerson1_4 user, InetAddress address) throws TestSessionException {
		checkState();
		if (testConnection != null) {
			disconnect();
		}
		if (session != null) {
			session.close();
		}
		session = new ClientSession(user, address);
	}

	@Override
	public synchronized OSEEPerson1_4 getUser() {
		if (session != null) {
			return session.getUser();
		}
		return null;
	}

	@Override
	public synchronized UUID getSessionKey() {
		return testConnection != null ? testConnection.getSessionKey() : null;

	}

	@Override
	public synchronized void setSessionDelegate(SessionDelegate sessionDelegate) {
		checkState();
		if (session == null) {
			throw new IllegalStateException(NO_USER_MSG);
		}
		session.setSessionDelegate(sessionDelegate);
	}


	private void notifyHostAvailable(IServiceConnector connector, OteServiceProperties props) {
		for (ITestEnvironmentAvailibilityListener listener : hostAvailabilityListeners) {
			listener.environmentAvailable(connector, props);
		}
	}

	private void notifyHostUnavailable(IServiceConnector connector, OteServiceProperties props) {
		for (ITestEnvironmentAvailibilityListener listener : hostAvailabilityListeners) {
			try {
				listener.environmentUnavailable(connector, props);
			} catch (Exception e) {
				Activator.log(Level.SEVERE, "exception in listener during host unavailable event notification", e);
			}
		}
	}

	@Override
	public synchronized ITestEnvironment getConnectedEnvironment() throws IllegalStateException {
		checkState();
		return testConnection == null ? null : testConnection.getConnectEnvironment();
	}

	@Override
	public synchronized IHostTestEnvironment getConnectedHost() throws IllegalStateException {
		checkState();
		return testConnection == null ? null : (IHostTestEnvironment) testConnection.getConnectedTestHost().getService();

	}

	@Override
	public synchronized IServiceConnector getConnector() {
		checkState();
		return testConnection == null ? null : testConnection.getServiceConnector();
	}

	@Override
	public synchronized List<IServiceConnector> getAvailableTestHosts() {
		List<IServiceConnector> envs = new ArrayList<>();
		for (IServiceConnector conn : testHosts.values()) {
			envs.add(conn);
		}
		return envs;
	}

	@Override
	public synchronized boolean isConnected() {
		return testConnection != null;
	}

	@Override
	public OteServiceProperties getProperties(IHostTestEnvironment testHost) {

		IServiceConnector connector = getConnector(testHost);
		if (connector != null) {
			return new OteServiceProperties(connector);
		}
		try {
			return new OteServiceProperties(testHost.getProperties());
		} catch (RemoteException ex) {
			return null;
		}
	}

	@Override
	public void onConnectionServiceStopped() {
	}

	@Override
	public synchronized void onConnectorsAdded(Collection<IServiceConnector> connectors) {
		for (IServiceConnector connector : connectors) {
			OteServiceProperties props = new OteServiceProperties(connector);
			props.printStats();
			testHosts.put(connector.getUniqueServerId(), connector);
			notifyHostAvailable(connector, props);
		}
	}

	@Override
	public synchronized void onConnectorRemoved(IServiceConnector connector) {
		String id = connector.getUniqueServerId();
		testHosts.remove(id);
		notifyHostUnavailable(connector, new OteServiceProperties(connector));
		IHostTestEnvironment connectedHost = getConnectedHost();
		if (testConnection != null && testConnection.getId().equals(id)) {
			testConnection = null;
			listenerNotifier.notifyConnectionLost(connector);
		}
	}

}
