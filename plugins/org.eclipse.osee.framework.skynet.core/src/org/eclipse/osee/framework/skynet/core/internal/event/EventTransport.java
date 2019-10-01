/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal.event;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IdeClientSession;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.messaging.event.res.IOseeCoreModelEventService;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
import org.eclipse.osee.framework.skynet.core.event.EventSystemPreferences;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.listener.EventQosType;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.FrameworkEvent;
import org.eclipse.osee.framework.skynet.core.event.model.HasEventType;
import org.eclipse.osee.framework.skynet.core.event.model.HasNetworkSender;
import org.eclipse.osee.framework.skynet.core.event.model.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class EventTransport implements Transport, IFrameworkEventListener {

   private final EventSystemPreferences preferences;
   private final EventHandlers handlers;
   private final EventListenerRegistry listenerRegistry;
   private final ExecutorService executorService;
   private final IOseeCoreModelEventService messagingService;
   private volatile boolean connectionStatus;

   public EventTransport(EventSystemPreferences preferences, EventHandlers handlers, EventListenerRegistry listenerRegistry, ExecutorService executorService, IOseeCoreModelEventService messagingService) {
      super();
      this.preferences = preferences;
      this.handlers = handlers;
      this.listenerRegistry = listenerRegistry;
      this.executorService = executorService;
      this.messagingService = messagingService;
      this.connectionStatus = false;
   }

   @Override
   public void setConnected(boolean value) {
      this.connectionStatus = value;
   }

   @Override
   public boolean isConnected() {
      return connectionStatus;
   }

   @Override
   public boolean isLoopbackEnabled() {
      return preferences.isEnableRemoteEventLoopback();
   }

   @Override
   public boolean isDispatchToLocalAllowed(Sender sender) {
      boolean normalOperation = !isLoopbackEnabled();
      return normalOperation || isLoopbackEnabled() && sender.isRemote();
   }

   private boolean areEventsAllowed() {
      return !preferences.isDisableEvents();
   }

   private Sender createSender(Object sourceObject, Object event) {
      Sender sender = null;
      if (RemoteEventServiceEventType.Rem_Connected.equals(
         event) || RemoteEventServiceEventType.Rem_DisConnected.equals(event)) {
         sender = Sender.createSender(sourceObject);
      } else {
         // Sender came from Remote Event Manager if source == sender
         if (sourceObject instanceof Sender && ((Sender) sourceObject).isRemote()) {
            sender = (Sender) sourceObject;
         } else {
            // create new sender based on sourceObject
            IdeClientSession session = ClientSessionManager.getSession();
            sender = Sender.createSender(sourceObject, session);
         }
      }
      return sender;
   }

   @Override
   public <E extends FrameworkEvent> void send(final Object object, final E event) {
      if (areEventsAllowed()) {
         Sender sender = createSender(object, event);
         if (event instanceof HasNetworkSender) {
            HasNetworkSender netSender = (HasNetworkSender) event;
            netSender.setNetworkSender(sender.getNetworkSender());
         }
         send(sender, event);
      }
   }

   @Override
   public <E extends FrameworkEvent> void send(final Sender sender, final E event) {
      if (areEventsAllowed()) {
         Conditions.checkNotNull(sender, "sender");
         Conditions.checkNotNull(event, "event");

         final EventHandlerLocal<? extends IEventListener, E> handler = handlers.getLocalHandler(event);
         Conditions.checkNotNull(handler, "eventHandler", "No event handler found for [%s]",
            event.getClass().getName());

         Runnable runnable = new Runnable() {
            @Override
            public void run() {
               if (preferences.isEnableRemoteEventLoopback()) {
                  EventUtil.eventLog("IEM: Loopback enabled [%s] - %s", event.getClass().getSimpleName(),
                     sender.isLocal() ? "Ignoring Local Kick" : "Kicking Local from Loopback");
               }
               try {
                  if (event instanceof HasEventType) {
                     HasEventType<?> hasEventType = (HasEventType<?>) event;
                     Conditions.checkNotNull(hasEventType.getEventType(), "eventType", "for event [%s]",
                        event.getClass().getSimpleName());
                  }
                  if (event instanceof HasNetworkSender) {
                     HasNetworkSender netSender = (HasNetworkSender) event;
                     Conditions.checkNotNull(netSender.getNetworkSender(), "networkSender", "for event [%s]",
                        event.getClass().getSimpleName());
                  }
                  handler.send(EventTransport.this, sender, event);
               } catch (Throwable th) {
                  EventUtil.eventLog(th, "IEM: Error sending event [%s] from sender [%s]",
                     event.getClass().getSimpleName(), sender);
               }
            }
         };

         EventUtil.eventLog("IEM: Dispatched Event - type[%s] to[%s]", event.getClass().getSimpleName(), sender);
         execute(runnable);
      }
   }

   @Override
   public <E extends FrameworkEvent, L extends IEventListener, H extends EventHandlerLocal<L, E>> void sendLocal(final Sender sender, final E event) {
      EventHandlerLocal<L, E> handler = handlers.getLocalHandler(event);
      Conditions.checkNotNull(handler, "localEventHandler", "No local event handler available for event [%s]",
         event.getClass().getName());
      EventUtil.eventLog("IEM: processing event [%s]", event);
      for (EventQosType qos : EventQosType.values()) {
         Collection<L> listeners = listenerRegistry.getListeners(qos, event);
         for (L listener : listeners) {
            try {
               handler.handle(listener, sender, event);
            } catch (Exception ex) {
               EventUtil.eventLog(ex, "IEM: Error processing - qos[%s] event[%s]", qos, event);
            }
         }
      }
      EventUtil.eventLog("IEM: processed event [%s]", event);
   }

   @Override
   public void sendRemote(final RemoteEvent remoteEvent) {
      if (isConnected()) {
         EventUtil.eventLog(String.format("IEM: kick - [%s]", remoteEvent));
         Job job = new Job(
            String.format("[%s] - sending [%s]", getClass().getSimpleName(), remoteEvent.getClass().getSimpleName())) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  messagingService.sendRemoteEvent(remoteEvent);
               } catch (Exception ex) {
                  EventUtil.eventLog("IEM: kick", ex);
                  return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
               }
               return Status.OK_STATUS;
            }
         };

         job.schedule();
      }

      if (preferences.isEnableRemoteEventLoopback()) {
         EventUtil.eventLog("IEM: Loopback enabled - Returning events as Remote event.");
         String newSessionId = GUID.create();
         remoteEvent.getNetworkSender().setSessionId(newSessionId);
         onEvent(remoteEvent);
      }
   }

   @Override
   public void onEvent(final RemoteEvent remoteEvent) {
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            try {
               if (remoteEvent != null) {
                  Sender sender = Sender.createSender(remoteEvent.getNetworkSender());

                  // If the sender's sessionId is the same as this client, then this event was
                  // created in this client and returned by remote event manager; ignore and continue
                  if (!sender.isLocal()) {
                     handleEvent(sender, remoteEvent);
                  }
               }
            } catch (Throwable th) {
               EventUtil.eventLog("IEM: RemoteEvent - onEvent", th);
            }
         }
      };
      execute(runnable);
   }

   private <E extends RemoteEvent> void handleEvent(Sender sender, E event) {
      EventHandlerRemote<E> handler = handlers.getRemoteHandler(event);
      Conditions.checkNotNull(handler, "remoteEventHandler", "No remote event handler available for event [%s]",
         event.getClass().getName());
      EventUtil.eventLog("IEM: processing remote event [%s]", event);
      handler.handle(this, sender, event);
      EventUtil.eventLog("IEM: processed remote event [%s]", event);
   }

   private void execute(Runnable runnable) {
      if (preferences.isPendRunning()) {
         runnable.run();
      } else {
         executorService.submit(runnable);
      }
   }

   /**
    * Kick a commit event to this local client to update artifact model for committed artifacts. This is needed cause
    * commit is made on server, but clients need to be notified of updates to commited branch artifact model.</br>
    * </br>
    * Normal event model sends artifact, attribute and relation changes to other clients upon persist. Since a commit is
    * not a client ide "persist", there is no "persist" event. This method sends a synthentically created ArtifactEvent
    * to the initiating client to update the artifact model on the commit-to branch for any artifacts loaded into the
    * cache. This is done for the remote clients through the normal events, but not to this client cause all events sent
    * remotely are ignored by the initiating client.
    */
   public void sendCommitEvent(Class<?> class1, ArtifactEvent artifactEvent) {
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            try {
               Sender sender = Sender.createSender(class1);
               artifactEvent.setNetworkSender(sender.getNetworkSender());
               RemotePersistEvent1 remoteEvent = FrameworkEventUtil.getRemotePersistEvent(artifactEvent);
               EventUtil.eventLog("IEM: processing commit remote event [%s]", artifactEvent);
               handleEvent(sender, remoteEvent);
               EventUtil.eventLog("IEM: processed commit remote event [%s]", artifactEvent);
            } catch (Throwable th) {
               EventUtil.eventLog("IEM: RemoteEvent - onEvent", th);
            }
         }
      };
      execute(runnable);
   }

}
