/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.IOseeCoreModelEventService;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.messaging.event.res.RemoteTopicEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBroadcastEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;
import org.eclipse.osee.framework.skynet.core.event.EventSystemPreferences;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventService;
import org.eclipse.osee.framework.skynet.core.event.listener.EventQosType;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.FrameworkEvent;
import org.eclipse.osee.framework.skynet.core.event.model.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.internal.event.ConnectionListenerImpl;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlers;
import org.eclipse.osee.framework.skynet.core.internal.event.EventListenerRegistry;
import org.eclipse.osee.framework.skynet.core.internal.event.EventTransport;
import org.eclipse.osee.framework.skynet.core.internal.event.OseeEventThreadFactory;
import org.eclipse.osee.framework.skynet.core.internal.event.TopicEventAdmin;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.ArtifactEventHandler;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.ArtifactRemoteEventHandler;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.BranchEventHandler;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.BranchRemoteEventHandler;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.RemoteServiceEventHandler;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.TopicLocalEventHandler;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.TopicRemoteEventHandler;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.TransactionEventHandler;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.TransactionRemoteEventHandler;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public class OseeEventServiceImpl implements OseeEventService {

   private final EventSystemPreferences preferences;
   private final EventListenerRegistry listeners;
   private final EventHandlers handlers;

   private IOseeCoreModelEventService messagingService;

   private EventTransport eventTransport;
   private ConnectionListenerImpl connectionStatus;
   private ExecutorService executor;

   private final List<ServiceReference<IEventListener>> pendingServices =
      new CopyOnWriteArrayList<>();

   private Thread thread;

   public OseeEventServiceImpl() {
      super();
      this.handlers = new EventHandlers();

      // TODO Fix initialization of OseeEventManager - These should not be singletons
      this.preferences = Activator.getEventPreferences();
      this.listeners = Activator.getEventListeners();
   }

   public void setOseeCoreModelEventService(IOseeCoreModelEventService messagingService) {
      this.messagingService = messagingService;
   }

   public void addListener(ServiceReference<IEventListener> reference) {
      if (isReady()) {
         registerListener(reference);
      } else {
         pendingServices.add(reference);
      }

   }

   public void removeListener(ServiceReference<IEventListener> reference) {
      if (isReady()) {
         unregisterListener(reference);
      } else {
         pendingServices.remove(reference);
      }
   }

   private void registerListener(ServiceReference<IEventListener> reference) {
      IEventListener listener = getService(reference);
      EventQosType qos = getEventQosType(reference);

      EventUtil.eventLog("registering event listener - qos[%s] name[%s]", reference.getProperty("component.name"));
      addListener(qos, listener);
   }

   private void unregisterListener(ServiceReference<IEventListener> reference) {
      IEventListener listener = getService(reference);
      EventQosType qos = getEventQosType(reference);

      EventUtil.eventLog("deregistering event listener - qos[%s] name[%s]", reference.getProperty("component.name"));
      removeListener(qos, listener);
   }

   private IEventListener getService(ServiceReference<IEventListener> reference) {
      Bundle bundle = reference.getBundle();
      BundleContext context = bundle.getBundleContext();
      IEventListener listener = context.getService(reference);
      return listener;
   }

   private boolean isReady() {
      return eventTransport != null;
   }

   public void start() {
      registerEventHandlers(handlers);

      executor = createExecutor("Osee Client Events");
      listeners.addListener(EventQosType.PRIORITY, new TopicEventAdmin());
      eventTransport = new EventTransport(preferences, handlers, listeners, executor, messagingService);
      connectionStatus = new ConnectionListenerImpl(preferences, eventTransport);

      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            for (ServiceReference<IEventListener> reference : pendingServices) {
               registerListener(reference);
            }
            pendingServices.clear();

            try {
               messagingService.addConnectionListener(connectionStatus);
               messagingService.addFrameworkListener(eventTransport);
               eventTransport.send(this, RemoteEventServiceEventType.Rem_Connected);
            } catch (OseeCoreException ex) {
               eventTransport.setConnected(false);
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      thread = new Thread(runnable);
      thread.start();
   }

   public void stop() {
      if (thread != null) {
         thread.interrupt();
         thread = null;
      }
      if (connectionStatus != null) {
         messagingService.removeConnectionListener(connectionStatus);
         connectionStatus = null;
      }
      if (eventTransport != null) {
         eventTransport.setConnected(false);
         messagingService.removeFrameworkListener(eventTransport);
      }
      if (executor != null) {
         executor.shutdown();
      }
      deregisterEventHandlers(handlers);
      eventTransport = null;
   }

   private EventQosType getEventQosType(ServiceReference<IEventListener> reference) {
      EventQosType type = EventQosType.NORMAL;
      String value = (String) reference.getProperty("qos");
      if (Strings.isValid(value)) {
         for (EventQosType qosType : EventQosType.values()) {
            if (qosType.name().equalsIgnoreCase(value)) {
               type = qosType;
               break;
            }
         }
      }
      return type;
   }

   private void registerEventHandlers(EventHandlers handlers) {
      handlers.addLocalHandler(ArtifactEvent.class, new ArtifactEventHandler());
      handlers.addLocalHandler(BranchEvent.class, new BranchEventHandler());
      handlers.addLocalHandler(RemoteEventServiceEventType.class, new RemoteServiceEventHandler());
      handlers.addLocalHandler(TransactionEvent.class, new TransactionEventHandler());
      handlers.addLocalHandler(TopicEvent.class, new TopicLocalEventHandler());

      handlers.addRemoteHandler(RemotePersistEvent1.class, new ArtifactRemoteEventHandler());
      handlers.addRemoteHandler(RemoteBranchEvent1.class, new BranchRemoteEventHandler());
      handlers.addRemoteHandler(RemoteTransactionEvent1.class, new TransactionRemoteEventHandler());
      handlers.addRemoteHandler(RemoteTopicEvent1.class, new TopicRemoteEventHandler());
   }

   private void deregisterEventHandlers(EventHandlers handlers) {
      handlers.removeLocalHandler(ArtifactEvent.class);
      handlers.removeLocalHandler(BranchEvent.class);
      handlers.removeLocalHandler(RemoteEventServiceEventType.class);
      handlers.removeLocalHandler(TransactionEvent.class);

      handlers.removeRemoteHandler(RemotePersistEvent1.class);
      handlers.removeRemoteHandler(RemoteBranchEvent1.class);
      handlers.removeRemoteHandler(RemoteBroadcastEvent1.class);
      handlers.removeRemoteHandler(RemoteTransactionEvent1.class);
   }

   private ExecutorService createExecutor(String threadPrefix) {
      int numberOfProcessors = Runtime.getRuntime().availableProcessors();
      if (numberOfProcessors > 4) {
         numberOfProcessors = 4;
      }
      ThreadFactory threadFactory = new OseeEventThreadFactory(threadPrefix);
      return Executors.newFixedThreadPool(numberOfProcessors, threadFactory);
   }

   @Override
   public boolean isConnected() {
      return eventTransport.isConnected();
   }

   @Override
   public <E extends FrameworkEvent> void send(Object object, E event) {
      eventTransport.send(object, event);
   }

   @Override
   public <E extends RemoteEvent> void receive(E event) {
      eventTransport.onEvent(event);
   }

   @Override
   public <E extends FrameworkEvent> void receive(Sender sender, E event) {
      eventTransport.sendLocal(sender, event);
   }

   @Override
   public void addListener(EventQosType qos, IEventListener listener) {
      listeners.addListener(qos, listener);
   }

   @Override
   public void removeListener(EventQosType qos, IEventListener listener) {
      listeners.removeListener(qos, listener);
   }

}
