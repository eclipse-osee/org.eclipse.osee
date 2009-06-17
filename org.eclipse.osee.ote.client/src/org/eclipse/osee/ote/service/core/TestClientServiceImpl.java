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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.connection.service.IConnectorListener;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.environment.TestEnvironmentConfig;
import org.eclipse.osee.ote.core.environment.UserTestSessionKey;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.service.Activator;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.IEnvironmentConfigurer;
import org.eclipse.osee.ote.service.ILibraryLoader;
import org.eclipse.osee.ote.service.IMessageDictionary;
import org.eclipse.osee.ote.service.IMessageDictionaryListener;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.service.IOteRuntimeLibraryProvider;
import org.eclipse.osee.ote.service.ITestConnectionListener;
import org.eclipse.osee.ote.service.ITestEnvironmentAvailibilityListener;
import org.eclipse.osee.ote.service.ITestEnvironmentFilter;
import org.eclipse.osee.ote.service.OteServiceProperties;
import org.eclipse.osee.ote.service.SessionDelegate;
import org.eclipse.osee.ote.service.TestSessionException;

public class TestClientServiceImpl implements IOteClientService, IConnectorListener, ILibraryLoader {
   
   private static final String NO_USER_MSG = "a user has not been set";
   private final IConnectionService connectionService;
   private final HashMap<IHostTestEnvironment, IServiceConnector> testHosts = new HashMap<IHostTestEnvironment, IServiceConnector>();
   private final ListenerNotifier listenerNotifier = new ListenerNotifier();
   private final HashSet<ITestEnvironmentAvailibilityListener> hostAvailabilityListeners = new HashSet<ITestEnvironmentAvailibilityListener>();
   private final HostFilter hostFilter = new HostFilter();
   private ClientSession session = null;
   private volatile boolean stopped = false;
   private final ArrayList<ITestEnvironmentFilter> environmentFilters = new ArrayList<ITestEnvironmentFilter>();
   private IMessageDictionary dictionary;
   private TestHostConnection testConnection;
   private final HashSet<IOteRuntimeLibraryProvider> libraryProviders = new HashSet<IOteRuntimeLibraryProvider>();
   
   private final OteClientEndpointSend endpointSend;
   private final OteClientEndpointReceive endpointReceive;
   
   public TestClientServiceImpl(IConnectionService connectionService, OteClientEndpointSend endpointSend, OteClientEndpointReceive endpointReceive) {
      this.connectionService = connectionService;
      this.endpointReceive = endpointReceive;
      this.endpointSend = endpointSend;
      endpointSend.setTestClientService(this);
      connectionService.addListener(this);
   }

   public void init() {
      for (IOteRuntimeLibraryProvider provider : libraryProviders) {
         provider.initialize(this);
      }
   }



   /*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.osee.ote.service.ClientService#addEnvironmentAvailibiltyListener
    * ()
    */
   @Override
   public synchronized void addEnvironmentAvailibiltyListener(ITestEnvironmentAvailibilityListener listener) {
      checkState();
      if (hostAvailabilityListeners.add(listener)) {
         for (Entry<IHostTestEnvironment, IServiceConnector> entry : testHosts.entrySet()) {
            listener.environmentAvailable(entry.getKey(), entry.getValue(), new OteServiceProperties(entry.getValue()));
         }
      }

   }

   public synchronized ConnectionEvent connect(IHostTestEnvironment testHost, IEnvironmentConfigurer configurer, TestEnvironmentConfig config) throws IllegalArgumentException, TestSessionException {
      checkState();
      if (session == null) {
         throw new IllegalArgumentException("a user has not been set");
      }

      if (testConnection != null) {
         throw new IllegalStateException("already connected");
      }
      
      if (testHost == null) {
    	 throw new IllegalArgumentException("test host cannot be null");
      }
      try {
         testConnection = session.connect(testHosts.get(testHost), testHost, config);
         if (testConnection != null) {
            // success
            ConnectionEvent event =
                  new ConnectionEvent(testHosts.get(testConnection.getConnectedTestHost()),
                        testConnection.getConnectEnvironment(), testConnection.getSessionKey());
            if (configurer != null) {
               try {
                  configurer.configure(event);
               } catch (Exception e) {
                  session.disconnect(testConnection);
                  throw new IllegalStateException("could not configure environment", e);
               }
            }
            listenerNotifier.notifyPostConnection(event);
            return event;
         }
      } catch (Exception e) {
         Activator.log(Level.SEVERE, "failed to establish connection", e);
         testConnection = null;
      }
      return null;
   }

   @Override
   public IServiceConnector getConnector(IHostTestEnvironment host) {
      return testHosts.get(host);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.service.IOteClientService#disconnect()
    */
   @Override
   public synchronized void disconnect() throws TestSessionException {
      checkState();
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
         listenerNotifier.notifyConnectionLost(testConnection.getServiceConnector(),
               testConnection.getConnectedTestHost());
         testConnection = null;
      } else {
         ConnectionEvent event =
               new ConnectionEvent(testHosts.get(testConnection.getConnectedTestHost()), envirnonment,
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

   /**
    * stops this service
    */
   public void stop() {
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
      if (dictionary != null) {
         try {
            unloadMessageDictionary();
         } catch (Exception e) {
            Activator.log(Level.SEVERE, "exception while trying to unload dictionary during stop()", e);
         }
      }
      stopped = true;
      hostAvailabilityListeners.clear();
      environmentFilters.clear();
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.osee.ote.service.ClientService#addConnectionListener(org.
    * eclipse.osee.ote.service.ITestConnectionListener)
    */
   @Override
   public synchronized void addConnectionListener(ITestConnectionListener listener) {
      checkState();
      if (listenerNotifier.addTestConnectionListener(listener)) {
         if (session == null) {
            return;
         }
         try {
            // check to see if a connection already established
            if (testConnection != null) {
               listener.onPostConnect(new ConnectionEvent(testConnection.getServiceConnector(),
                     testConnection.getConnectEnvironment(), testConnection.getSessionKey()));
            }
         } catch (Exception e) {
            Activator.log(Level.SEVERE, "Exception notifying listener of connection event", e);
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.osee.ote.service.ClientService#removeConnectionListener(org
    * .eclipse.osee.ote.service.ITestConnectionListener)
    */
   @Override
   public void removeConnectionListener(ITestConnectionListener listener) {
      listenerNotifier.removeTestConnectionListener(listener);
   }

   /*
    * (non-Javadoc)
    * 
    * @seeorg.eclipse.osee.ote.service.ClientService#
    * removeEnvironmentAvailibiltyListener
    * (org.eclipse.osee.ote.service.TestEnvironmentAvailibilityListener)
    */
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

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.osee.ote.service.ClientService#setUser(org.eclipse.osee.ote
    * .core.OSEEPerson1_4)
    */
   @Override
   public synchronized void setUser(OSEEPerson1_4 user, InetAddress address) throws TestSessionException {
      checkState();
      if (testConnection != null) {
         disconnect();
      }
      if (session != null) {
         session.close();
      }
      session = new ClientSession(user, address, endpointReceive);
   }

   @Override
   public OSEEPerson1_4 getUser() {
      if (session != null) {
         return session.getUser();
      }
      return null;
   }

   @Override
   public UserTestSessionKey getSessionKey() {
      return (testConnection != null) ? testConnection.getSessionKey() : null;

   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.osee.ote.service.IOteClientService#setPromptHandler(org.eclipse
    * .osee.ote.service.IPromptHandler)
    */
   @Override
   public synchronized void setSessionDelegate(SessionDelegate sessionDelegate) {
      checkState();
      if (session == null) {
         throw new IllegalStateException(NO_USER_MSG);
      }
      session.setSessionDelegate(sessionDelegate);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.osee.ote.service.IOteClientService#addConnectionFilters()
    */
   @Override
   public synchronized void addConnectionFilters(ITestEnvironmentFilter filter) {
      checkState();
      if (environmentFilters.add(filter)) {
         Iterator<Entry<IHostTestEnvironment, IServiceConnector>> envIterator = testHosts.entrySet().iterator();
         while (envIterator.hasNext()) {
            Entry<IHostTestEnvironment, IServiceConnector> entry = envIterator.next();
            OteServiceProperties props = new OteServiceProperties(entry.getValue());
            if (!filter.accept(entry.getKey(), props)) {
               notifyHostUnavailable(entry.getKey(), entry.getValue(), props);
               envIterator.remove();
            }
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.osee.ote.service.IOteClientService#removeConnectionFilters
    * (org.eclipse.osee.ote.service.ITestEnvironmentFilter)
    */
   @Override
   public synchronized void removeConnectionFilters(ITestEnvironmentFilter filter) {
      checkState();
      if (environmentFilters.remove(filter)) {
         for (IServiceConnector connector : connectionService.getAllConnectors()) {
            if (!hostFilter.accept(connector)) {
               // this connector does not connect to a test environment
               // service
               continue;
            }
            IHostTestEnvironment env = (IHostTestEnvironment) connector.getService();
            OteServiceProperties props = new OteServiceProperties(connector);

            if (!isAcceptableTestEnvironment(env, props)) {
               // this environment did not pass the filters. If we were
               // tracking this environment then we
               // need to remove it
               if (testHosts.containsKey(env)) {
                  // we were tracking this environment but our filters
                  // don't approve so we need to remove it
                  testHosts.remove(env);
                  notifyHostUnavailable(env, connector, props);
               }
            } else {
               // the filters have accepted this environment. Lets see if
               // we were tracking it
               if (!testHosts.containsKey(env)) {
                  // we were not tracking this environment so go ahead and
                  // add it
                  testHosts.put(env, connector);
                  notifyHostAvailable(env, connector, props);
               }
            }
         }
      }
   }

   /**
    * compares the environment and its properties against all currently active filters
    * 
    * @param env
    * @param connector
    * @return true if the environment was accepted by all filters, false otherwise
    */
   private boolean isAcceptableTestEnvironment(IHostTestEnvironment env, OteServiceProperties props) {
      for (ITestEnvironmentFilter filter : environmentFilters) {
         if (!filter.accept(env, props)) {
            return false;
         }
      }
      return true;
   }

   private void notifyHostAvailable(IHostTestEnvironment env, IServiceConnector connector, OteServiceProperties props) {
      for (ITestEnvironmentAvailibilityListener listener : hostAvailabilityListeners) {
         listener.environmentAvailable(env, connector, props);
      }
   }

   private void notifyHostUnavailable(IHostTestEnvironment env, IServiceConnector connector, OteServiceProperties props) {
      for (ITestEnvironmentAvailibilityListener listener : hostAvailabilityListeners) {
         try {
            listener.environmentUnavailable(env, connector, props);
         } catch (Exception e) {
            Activator.log(Level.SEVERE, "exception in listener during host unavailable event notification", e);
         }
      }
   }



   /*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.osee.ote.service.IOteClientService#getConnectedEnvironment()
    */
   @Override
   public synchronized ITestEnvironment getConnectedEnvironment() throws IllegalStateException {
      checkState();
      return testConnection == null ? null : testConnection.getConnectEnvironment();
   }

   @Override
   public synchronized IHostTestEnvironment getConnectedHost() throws IllegalStateException {
      checkState();
      return testConnection == null ? null : testConnection.getConnectedTestHost();

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.service.IOteClientService#getConnector()
    */
   @Override
   public IServiceConnector getConnector() {
	   checkState();
	   return testConnection == null ? null : testConnection.getServiceConnector();
   }

/*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.osee.ote.service.IOteClientService#addDictionaryListener(
    * org.eclipse.osee.ote.service.IMessageDictionaryListener)
    */
   @Override
   public void addDictionaryListener(IMessageDictionaryListener listener) {
      if (listenerNotifier.addDictionaryListener(listener) && dictionary != null) {
         listener.onDictionaryLoaded(dictionary);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.osee.ote.service.IOteClientService#removeDictionaryListener
    * (org.eclipse.osee.ote.service.IMessageDictionaryListener)
    */
   @Override
   public void removeDictionaryListener(IMessageDictionaryListener listener) {
      listenerNotifier.removeDictionaryListener(listener);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.eclipse.osee.ote.service.IOteClientService#getAvailableTestHosts()
    */
   @Override
   public synchronized Collection<IHostTestEnvironment> getAvailableTestHosts() {
      return testHosts.keySet();
   }

   @Override
   public boolean isConnected() {
      return testConnection != null;
   }

   public void addLibraryProvider(Collection<IOteRuntimeLibraryProvider> provider) {
      libraryProviders.addAll(provider);
   }

   @Override
   public OteServiceProperties getProperties(IHostTestEnvironment testHost) {

      IServiceConnector connector = testHosts.get(testHost);
      if (connector != null) {
         return new OteServiceProperties(connector);
      }
      return null;
   }

   
   @Override
   public synchronized void loadMessageDictionary(IMessageDictionary newDictionary) {
      checkState();
      if (newDictionary == null) {
         throw new NullPointerException("dictionary cannot be null");
      }
      if (dictionary != null) {
         listenerNotifier.notifyDictionaryUnloaded(dictionary);
      }
      dictionary = newDictionary;
      listenerNotifier.notifyDictionaryLoaded(newDictionary);
   }

   @Override
   public synchronized void unloadMessageDictionary() {
      checkState();
      listenerNotifier.notifyDictionaryUnloaded(dictionary);
      dictionary = null;
   }
   
   @Override
   public synchronized IMessageDictionary getLoadedDictionary() {
      return dictionary;
   }
   
   @Override
   public void onConnectionServiceStopped() {
   }

   @Override
   public synchronized void onConnectorsAdded(Collection<IServiceConnector> connectors) {
      for (IServiceConnector connector : hostFilter.accept(connectors)) {
         OteServiceProperties props = new OteServiceProperties(connector);
         props.printStats();
         IHostTestEnvironment env = (IHostTestEnvironment) connector.getService();
         if (isAcceptableTestEnvironment(env, props)) {
            testHosts.put(env, connector);
            notifyHostAvailable(env, connector, props);
         }
      }
   }

   @Override
   public synchronized void onConnectorRemoved(IServiceConnector connector) {
      if (hostFilter.accept(connector)) {
         IHostTestEnvironment env = (IHostTestEnvironment) connector.getService();
         testHosts.remove(env);
         notifyHostUnavailable(env, connector, new OteServiceProperties(connector));
         IHostTestEnvironment connectedHost = getConnectedHost();
         if (connectedHost != null && connectedHost.equals(env)) {
            testConnection = null;
            listenerNotifier.notifyConnectionLost(connector, env);
         }
      }
   }
   
}
