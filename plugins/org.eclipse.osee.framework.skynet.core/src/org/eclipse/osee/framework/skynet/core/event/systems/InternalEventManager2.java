/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event.systems;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.messaging.event.res.IOseeCoreModelEventService;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.EventSystemPreferences;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBroadcastEventListener;
import org.eclipse.osee.framework.skynet.core.event.IEventFilteredListener;
import org.eclipse.osee.framework.skynet.core.event.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.IRemoteEventManagerEventListener;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.event2.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event2.BroadcastEvent;
import org.eclipse.osee.framework.skynet.core.event2.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event2.ITransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event2.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * Internal implementation of OSEE Event Manager that should only be accessed from OseeEventManager classes.
 * 
 * @author Donald G. Dunne
 */
public class InternalEventManager2 {
   public static interface ConnectionStatus {
      boolean isConnected();
   }

   private final Collection<IEventListener> priorityListeners;
   private final Collection<IEventListener> listeners;
   private final IOseeCoreModelEventService coreModelEventService;
   private final ExecutorService executorService;
   private final EventSystemPreferences preferences;
   private final IFrameworkEventListener frameworkListener;
   private final ConnectionStatus connectionStatus;

   public InternalEventManager2(IOseeCoreModelEventService coreModelEventService, Collection<IEventListener> listeners, Collection<IEventListener> priorityListeners, ExecutorService executorService, EventSystemPreferences preferences, ConnectionStatus connectionStatus) {
      this.coreModelEventService = coreModelEventService;
      this.listeners = listeners;
      this.priorityListeners = priorityListeners;
      this.executorService = executorService;
      this.preferences = preferences;
      this.frameworkListener = new FrameworkEventToRemoteEvent2Listener(this);
      this.connectionStatus = connectionStatus;
   }

   public void start() {
      coreModelEventService.addFrameworkListener(frameworkListener);
   }

   public void stop() {
      coreModelEventService.removeFrameworkListener(frameworkListener);
   }

   public boolean isConnected() {
      return preferences.isNewEvents() && connectionStatus.isConnected();
   }

   private void execute(Runnable runnable) {
      if (preferences.isPendRunning()) {
         runnable.run();
      } else {
         executorService.submit(runnable);
      }
   }

   /**
    * For all IBranchEventListener, process priorityListeners, then normal listeners
    */
   public void processBranchEvent(Sender sender, BranchEvent branchEvent) {
      EventUtil.eventLog(String.format("IEM2: processBranchEvent [%s]", branchEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof IBranchEventListener) {
               processBranchEventListener((IBranchEventListener) listener, sender, branchEvent);
            }
         } catch (Exception ex) {
            EventUtil.eventLog(
               String.format("IEM2: processBranchEvent [%s] error processing priorityListeners", branchEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (listener instanceof IBranchEventListener) {
               processBranchEventListener((IBranchEventListener) listener, sender, branchEvent);
            }
         } catch (Exception ex) {
            EventUtil.eventLog(String.format("IEM2: processBranchEvent [%s] error processing listeners", branchEvent),
               ex);
         }
      }
   }

   private void processBranchEventListener(IBranchEventListener listener, Sender sender, BranchEvent branchEvent) {
      // If any filter doesn't match, don't call listener
      if (((IEventFilteredListener) listener).getEventFilters() != null) {
         for (IEventFilter eventFilter : ((IEventFilteredListener) listener).getEventFilters()) {
            // If this branch doesn't match, don't pass events through
            if (!eventFilter.isMatch(branchEvent.getBranchGuid())) {
               return;
            }
         }
      }
      // Call listener if we matched all of the filters
      listener.handleBranchEvent(sender, branchEvent);
   }

   /**
    * For all IBranchEventListener, process priorityListeners, then normal listeners
    */
   public void processEventArtifactsAndRelations(Sender sender, ArtifactEvent artifactEvent) {
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof IArtifactEventListener) {
               processEventArtifactsAndRelationsListener((IArtifactEventListener) listener, artifactEvent, sender);
            }
         } catch (Exception ex) {
            EventUtil.eventLog(
               String.format("IEM2: processArtsAndRels [%s] error processing priorityListeners", artifactEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (listener instanceof IArtifactEventListener) {
               processEventArtifactsAndRelationsListener((IArtifactEventListener) listener, artifactEvent, sender);
            }
         } catch (Exception ex) {
            EventUtil.eventLog(
               String.format("IEM2: processArtsAndRels [%s] error processing listeners", artifactEvent), ex);
         }
      }
   }

   private void processEventArtifactsAndRelationsListener(IArtifactEventListener listener, ArtifactEvent artifactEvent, Sender sender) {
      EventUtil.eventLog(String.format("IEM2: processArtsAndRels [%s]", artifactEvent));
      // If any filter doesn't match, don't call listener
      if (((IEventFilteredListener) listener).getEventFilters() != null) {
         for (IEventFilter eventFilter : ((IEventFilteredListener) listener).getEventFilters()) {
            // If this branch doesn't match, don't pass events through
            if (!eventFilter.isMatch(artifactEvent.getBranchGuid())) {
               return;
            }
            for (EventBasicGuidArtifact guidArt : artifactEvent.getArtifacts()) {
               if (!eventFilter.isMatch(guidArt)) {
                  return;
               }
            }
            for (EventBasicGuidRelation guidRel : artifactEvent.getRelations()) {
               if (!eventFilter.isMatch(guidRel)) {
                  return;
               }
            }
         }
      }
      // Call listener if we matched all of the filters
      listener.handleArtifactEvent(artifactEvent, sender);
   }

   public void processAccessControlEvent(Sender sender, AccessControlEvent accessControlEvent) {
      EventUtil.eventLog(String.format("IEM2: processAccessControlEvent [%s]", accessControlEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof IAccessControlEventListener) {
               ((IAccessControlEventListener) listener).handleAccessControlArtifactsEvent(sender, accessControlEvent);
            }
         } catch (Exception ex) {
            EventUtil.eventLog(String.format("IEM2: processAccessControlEvent [%s] error processing priorityListeners",
               accessControlEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (listener instanceof IAccessControlEventListener) {
               ((IAccessControlEventListener) listener).handleAccessControlArtifactsEvent(sender, accessControlEvent);
            }
         } catch (Exception ex) {
            EventUtil.eventLog(
               String.format("IEM2: processAccessControlEvent [%s] error processing listeners", accessControlEvent), ex);
         }
      }
   }

   public void processEventBroadcastEvent(Sender sender, BroadcastEvent broadcastEvent) {
      EventUtil.eventLog(String.format("IEM2: processEventBroadcastEvent [%s]", broadcastEvent));
      if (broadcastEvent.getUsers().size() == 0) {
         return;
      }
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof IBroadcastEventListener) {
               ((IBroadcastEventListener) listener).handleBroadcastEvent(sender, broadcastEvent);
            }
         } catch (Exception ex) {
            EventUtil.eventLog(
               String.format("IEM2: processEventBroadcastEvent [%s] error processing priorityListeners", broadcastEvent),
               ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (listener instanceof IBroadcastEventListener) {
               ((IBroadcastEventListener) listener).handleBroadcastEvent(sender, broadcastEvent);
            }
         } catch (Exception ex) {
            EventUtil.eventLog(
               String.format("IEM2: processEventBroadcastEvent [%s] error processing listeners", broadcastEvent), ex);
         }
      }
   }

   public void processRemoteEventManagerEvent(Sender sender, RemoteEventServiceEventType remoteEventServiceEvent) {
      EventUtil.eventLog(String.format("IEM2: processRemoteEventManagerEvent [%s]", remoteEventServiceEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof IRemoteEventManagerEventListener) {
               ((IRemoteEventManagerEventListener) listener).handleRemoteEventManagerEvent(sender,
                  remoteEventServiceEvent);
            }
         } catch (Exception ex) {
            EventUtil.eventLog(
               String.format("IEM2: processRemoteEventManagerEvent [%s] error processing priorityListeners",
                  remoteEventServiceEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (listener instanceof IRemoteEventManagerEventListener) {
               ((IRemoteEventManagerEventListener) listener).handleRemoteEventManagerEvent(sender,
                  remoteEventServiceEvent);
            }
         } catch (Exception ex) {
            EventUtil.eventLog(String.format("IEM2: processRemoteEventManagerEvent [%s] error processing listeners",
               remoteEventServiceEvent), ex);
         }
      }
   }

   public void processTransactionEvent(Sender sender, TransactionEvent transactionEvent) {
      EventUtil.eventLog(String.format("IEM2: processTransactionEvent [%s]", transactionEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof ITransactionEventListener) {
               ((ITransactionEventListener) listener).handleTransactionEvent(sender, transactionEvent);
            }
         } catch (Exception ex) {
            EventUtil.eventLog(
               String.format("IEM2: processTransactionEvent [%s] error processing priorityListeners", transactionEvent),
               ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (listener instanceof ITransactionEventListener) {
               ((ITransactionEventListener) listener).handleTransactionEvent(sender, transactionEvent);
            }
         } catch (Exception ex) {
            EventUtil.eventLog(
               String.format("IEM2: processTransactionEvent [%s] error processing listeners", transactionEvent), ex);
         }
      }
   }

   //   public String getListenerReport() {
   //      return EventUtil.getListenerReport(listeners, priorityListeners);
   //   }

   /*
    * Kick LOCAL and REMOTE access control events
    */
   public void kickAccessControlArtifactsEvent(final Sender sender, final AccessControlEvent accessControlEvent) {
      if (sender == null) {
         throw new IllegalArgumentException("sender can not be null");
      }
      if (accessControlEvent.getEventType() == null) {
         throw new IllegalArgumentException("accessControlEventType can not be null");
      }
      if (preferences.isDisableEvents()) {
         return;
      }
      EventUtil.eventLog("IEM2: kickAccessControlEvent - type: " + accessControlEvent + sender + " artifacts: " + accessControlEvent.getArtifacts());
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            try {
               // Kick LOCAL
               boolean normalOperation = !preferences.isEnableRemoteEventLoopback();
               boolean loopbackTestEnabledAndRemoteEventReturned =
                  preferences.isEnableRemoteEventLoopback() && sender.isRemote();
               if (normalOperation && sender.isLocal() || loopbackTestEnabledAndRemoteEventReturned) {
                  processAccessControlEvent(sender, accessControlEvent);
               }
               // Kick REMOTE
               if (sender.isLocal() && accessControlEvent.getEventType().isRemoteEventType()) {
                  sendRemoteEvent(FrameworkEventUtil.getRemoteAccessControlEvent(accessControlEvent));
               }
            } catch (Exception ex) {
               EventUtil.eventLog("IEM2 kickAccessControlEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL "remote event manager" event
   public void kickLocalRemEvent(final Sender sender, final RemoteEventServiceEventType remoteEventServiceEventType) {
      if (preferences.isDisableEvents()) {
         return;
      }
      EventUtil.eventLog("IEM2: kickLocalRemEvent: type: " + remoteEventServiceEventType + " - " + sender);
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            // Kick LOCAL
            try {
               if (sender.isLocal() && remoteEventServiceEventType.isLocalEventType()) {
                  processRemoteEventManagerEvent(sender, remoteEventServiceEventType);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL ArtifactReloadEvent
   public void kickLocalArtifactReloadEvent(final Sender sender, final ArtifactEvent artifactEvent) {
      if (preferences.isDisableEvents()) {
         return;
      }
      EventUtil.eventLog("IEM2: kickArtifactReloadEvent [" + artifactEvent + "] - " + sender);
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            try {
               // Kick LOCAL
               boolean normalOperation = !preferences.isEnableRemoteEventLoopback();
               boolean loopbackTestEnabledAndRemoteEventReturned =
                  preferences.isEnableRemoteEventLoopback() && sender.isRemote();
               if (normalOperation && sender.isLocal() || loopbackTestEnabledAndRemoteEventReturned) {
                  processEventArtifactsAndRelations(sender, artifactEvent);
               }

               // NO REMOTE KICK
            } catch (Exception ex) {
               EventUtil.eventLog("IEM2 kickArtifactReloadEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   /*
    * Kick LOCAL and REMOTE branch events
    */
   public void kickBranchEvent(final Sender sender, final BranchEvent branchEvent) {
      if (branchEvent.getNetworkSender() == null) {
         EventUtil.eventLog("IEM2: kickBranchEvent - ERROR networkSender can't be null.");
         return;
      }
      if (preferences.isDisableEvents()) {
         return;
      }
      EventUtil.eventLog("IEM2: kickBranchEvent: type: " + branchEvent.getEventType() + " guid: " + branchEvent.getBranchGuid() + " - " + sender);
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            // Log if this is a loopback and what is happening
            if (preferences.isEnableRemoteEventLoopback()) {
               EventUtil.eventLog("IEM2: BranchEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
            }
            BranchEventType branchEventType = branchEvent.getEventType();

            // Kick LOCAL
            boolean normalOperation = !preferences.isEnableRemoteEventLoopback();
            boolean loopbackTestEnabledAndRemoteEventReturned =
               preferences.isEnableRemoteEventLoopback() && sender.isRemote();
            if (normalOperation || loopbackTestEnabledAndRemoteEventReturned) {
               processBranchEvent(sender, branchEvent);
            }

            // Kick REMOTE (If source was Local and this was not a default branch changed event
            if (sender.isLocal() && branchEventType.isRemoteEventType()) {
               sendRemoteEvent(FrameworkEventUtil.getRemoteBranchEvent(branchEvent));
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL and REMOTE ArtifactEvent
   public void kickArtifactEvent(final Sender sender, final ArtifactEvent artifactEvent) {
      if (artifactEvent.getNetworkSender() == null) {
         EventUtil.eventLog("IEM2: kickArtifactEvent - ERROR networkSender can't be null.");
         return;
      }
      if (preferences.isDisableEvents()) {
         return;
      }
      EventUtil.eventLog("IEM2: kickArtifactEvent [" + artifactEvent + "] - " + sender);
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            // Roll-up change information
            try {
               // Log if this is a loopback and what is happening
               if (preferences.isEnableRemoteEventLoopback()) {
                  EventUtil.eventLog("IEM2: ArtifactEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }

               // Kick LOCAL
               boolean normalOperation = !preferences.isEnableRemoteEventLoopback();
               boolean loopbackTestEnabledAndRemoteEventReturned =
                  preferences.isEnableRemoteEventLoopback() && sender.isRemote();
               if (normalOperation || loopbackTestEnabledAndRemoteEventReturned) {
                  processEventArtifactsAndRelations(sender, artifactEvent);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal()) {
                  sendRemoteEvent(FrameworkEventUtil.getRemotePersistEvent(artifactEvent));
               }
            } catch (Exception ex) {
               EventUtil.eventLog("IEM2 kickArtifactEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL and REMOTE ArtifactEvent
   public void kickTransactionEvent(final Sender sender, final TransactionEvent transEvent) {
      if (transEvent.getNetworkSender() == null) {
         EventUtil.eventLog("IEM2: kickTransactionEvent - ERROR networkSender can't be null.");
         return;
      }
      if (preferences.isDisableEvents()) {
         return;
      }
      EventUtil.eventLog("IEM2: kickTransactionEvent [" + transEvent + "] - " + sender);
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            // Roll-up change information
            try {
               // Log if this is a loopback and what is happening
               if (!preferences.isEnableRemoteEventLoopback()) {
                  EventUtil.eventLog("IEM2: TransactionEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }

               // Kick LOCAL
               boolean normalOperation = !preferences.isEnableRemoteEventLoopback();
               boolean loopbackTestEnabledAndRemoteEventReturned =
                  preferences.isEnableRemoteEventLoopback() && sender.isRemote();
               if (normalOperation || loopbackTestEnabledAndRemoteEventReturned) {
                  processTransactionEvent(sender, transEvent);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal()) {
                  // Kick REMOTE
                  sendRemoteEvent(FrameworkEventUtil.getRemoteTransactionEvent(transEvent));
               }
            } catch (Exception ex) {
               EventUtil.eventLog("IEM2: kickTransactionEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   /*
    * Kick LOCAL and REMOTE broadcast event
    */
   public void kickBroadcastEvent(final Sender sender, final BroadcastEvent broadcastEvent) {
      if (preferences.isDisableEvents()) {
         return;
      }

      if (!broadcastEvent.getBroadcastEventType().isPingOrPong()) {
         EventUtil.eventLog("IEM2: kickBroadcastEvent: type: " + broadcastEvent.getBroadcastEventType().name() + " message: " + broadcastEvent.getMessage() + " - " + sender);
      }
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            try {
               // Kick from REMOTE
               if (sender.isRemote() || sender.isLocal() && broadcastEvent.getBroadcastEventType().isLocalEventType()) {
                  processEventBroadcastEvent(sender, broadcastEvent);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal() && broadcastEvent.getBroadcastEventType().isRemoteEventType()) {
                  sendRemoteEvent(FrameworkEventUtil.getRemoteBroadcastEvent(broadcastEvent));
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   /**
    * InternalEventManager.enableRemoteEventLoopback will enable a testing loopback that will take the kicked remote
    * events and loop them back as if they came from an external client. It will allow for the testing of the OEM -> REM
    * -> OEM processing. In addition, this onEvent is put in a non-display thread which will test that all handling by
    * applications is properly handled by doing all processing and then kicking off display-thread when need to update
    * ui. SessionId needs to be modified so this client doesn't think the events came from itself.
    */
   private void sendRemoteEvent(final RemoteEvent remoteEvent) {
      if (preferences.isNewEvents() && isConnected()) {
         EventUtil.eventLog(String.format("REM2: kick - [%s]", remoteEvent));
         Job job =
            new Job(String.format("[%s] - sending [%s]", getClass().getSimpleName(),
               remoteEvent.getClass().getSimpleName())) {
               @Override
               protected IStatus run(IProgressMonitor monitor) {
                  try {
                     coreModelEventService.sendRemoteEvent(remoteEvent);
                  } catch (Exception ex) {
                     EventUtil.eventLog("REM2: kick", ex);
                     return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
                  }
                  return Status.OK_STATUS;
               }
            };

         job.schedule();
      }

      if (preferences.isEnableRemoteEventLoopback()) {
         EventUtil.eventLog("REM2: Loopback enabled - Returning events as Remote event.");
         String newSessionId = GUID.create();
         remoteEvent.getNetworkSender().setSessionId(newSessionId);
         try {
            frameworkListener.onEvent(remoteEvent);
         } catch (RemoteException ex) {
            EventUtil.eventLog("REM2: RemoteEvent - onEvent", ex);
         }
      }
   }

   public void testSendRemoteEventThroughFrameworkListener(final RemoteEvent remoteEvent) throws RemoteException {
      frameworkListener.onEvent(remoteEvent);
   }
}
