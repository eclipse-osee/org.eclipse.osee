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
package org.eclipse.osee.framework.skynet.core.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
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
import org.eclipse.osee.framework.skynet.core.event2.filter.BranchGuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;

/**
 * Internal implementation of OSEE Event Manager that should only be accessed from RemoteEventManager and
 * OseeEventManager classes.
 * 
 * @author Donald G. Dunne
 */
public class InternalEventManager2 {

   public static final Collection<UnloadedArtifact> EMPTY_UNLOADED_ARTIFACTS = Collections.emptyList();
   private static boolean disableEvents = false;
   private static final ThreadFactory threadFactory = new OseeEventThreadFactory("Osee Events2");
   private static final ExecutorService executorService = Executors.newFixedThreadPool(
         Runtime.getRuntime().availableProcessors(), threadFactory);
   private static final List<IEventListener> priorityListeners = new CopyOnWriteArrayList<IEventListener>();
   private static final List<IEventListener> listeners = new CopyOnWriteArrayList<IEventListener>();

   // This will disable all Local TransactionEvents and enable loopback routing of Remote TransactionEvents back
   // through the RemoteEventService as if they came from another client.  This is for testing purposes only and
   // should be reset to false before release.
   private static boolean enableRemoteEventLoopback = false;

   private static void execute(Runnable runnable) {
      executorService.submit(runnable);
   }

   public static void addListener(IEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }
      if (!listeners.contains(listener)) {
         listeners.add(listener);
         OseeEventManager.eventLog("IEM2: addListener (" + priorityListeners.size() + ") " + listener);
      }
   }

   /**
    * Add a priority listener. This should only be done for caches where they need to be updated before all other
    * listeners are called.
    */
   public static void addPriorityListener(IEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }
      if (!priorityListeners.contains(listener)) {
         priorityListeners.add(listener);
      }
      OseeEventManager.eventLog("IEM2: addPriorityListener (" + priorityListeners.size() + ") " + listener);
   }

   public static void removeListener(IEventListener listener) {
      listeners.remove(listener);
      priorityListeners.remove(listener);
   }

   public static void removeListeners(IEventListener listener) {
      OseeEventManager.eventLog("IEM2: removeListener: (" + listeners.size() + ") " + listener);
      listeners.remove(listener);
      priorityListeners.remove(listener);
   }

   /**
    * Clear all registered listeners. Should be used for testing purposes only.
    */
   public static void internalRemoveAllListeners() {
      listeners.clear();
      priorityListeners.clear();
   }

   public static int getNumberOfListeners() {
      return listeners.size();
   }

   public static void processBranchEvent(Sender sender, BranchEvent branchEvent) {
      OseeEventManager.eventLog(String.format("IEM2: processBranchEvent [%s]", branchEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            processBranchEventListener(listener, sender, branchEvent);
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("IEM2: processBranchEvent [%s] error processing priorityListeners", branchEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            processBranchEventListener(listener, sender, branchEvent);
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("IEM2: processBranchEvent [%s] error processing listeners", branchEvent), ex);
         }
      }
   }

   private static void processBranchEventListener(IEventListener listener, Sender sender, BranchEvent branchEvent) {
      // If true, listener will be called
      boolean match = true;
      if (listener instanceof BranchGuidEventFilter) {
         // If this branch doesn't match, don't pass events through
         if (!((BranchGuidEventFilter) listener).isMatch(branchEvent.getBranchGuid())) {
            match = false;
         }
      }
      // Call listener if we matched any of the filters
      if (listener instanceof IBranchEventListener && match) {
         ((IBranchEventListener) listener).handleBranchEvent(sender, branchEvent);
      }
   }

   public static void processEventArtifactsAndRelations(Sender sender, ArtifactEvent artifactEvent) {
      for (IEventListener listener : priorityListeners) {
         try {
            processEventArtifactsAndRelationsListener(listener, artifactEvent, sender);
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("IEM2: processArtsAndRels [%s] error processing priorityListeners", artifactEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            processEventArtifactsAndRelationsListener(listener, artifactEvent, sender);
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("IEM2: processArtsAndRels [%s] error processing listeners", artifactEvent), ex);
         }
      }
   }

   private static void processEventArtifactsAndRelationsListener(IEventListener listener, ArtifactEvent artifactEvent, Sender sender) {
      if (listener != null && !(listener instanceof IArtifactEventListener)) return;
      OseeEventManager.eventLog(String.format("IEM2: processArtsAndRels [%s]", artifactEvent));
      // If true, listener will be called
      boolean match = false;
      if (listener instanceof IEventFilteredListener) {
         // If no filters, this is a match
         if (((IEventFilteredListener) listener).getEventFilters() == null || ((IEventFilteredListener) listener).getEventFilters().isEmpty()) {
            match = true;
         } else {
            // Loop through filters and see if anything matches what's desired
            for (IEventFilter filter : ((IEventFilteredListener) listener).getEventFilters()) {
               for (EventBasicGuidArtifact guidArt : artifactEvent.getArtifacts()) {
                  if (filter.isMatch(guidArt)) match = true;
                  break;
               }
               if (match) break;
               for (EventBasicGuidRelation guidRel : artifactEvent.getRelations()) {
                  if (filter.isMatch(guidRel)) match = true;
                  break;
               }
               if (match) break;
            }
         }
      }
      // If no filters, this is a match
      else {
         match = true;
      }
      // Call listener if we matched any of the filters
      if (match) {
         ((IArtifactEventListener) listener).handleArtifactEvent(artifactEvent, sender);
      }
   }

   public static void processAccessControlEvent(Sender sender, AccessControlEvent accessControlEvent) {
      OseeEventManager.eventLog(String.format("IEM2: processAccessControlEvent [%s]", accessControlEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof IAccessControlEventListener) {
               ((IAccessControlEventListener) listener).handleAccessControlArtifactsEvent(sender, accessControlEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(String.format(
                  "IEM2: processAccessControlEvent [%s] error processing priorityListeners", accessControlEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (listener instanceof IAccessControlEventListener) {
               ((IAccessControlEventListener) listener).handleAccessControlArtifactsEvent(sender, accessControlEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("IEM2: processAccessControlEvent [%s] error processing listeners", accessControlEvent),
                  ex);
         }
      }
   }

   public static void processEventBroadcastEvent(Sender sender, BroadcastEvent broadcastEvent) {
      OseeEventManager.eventLog(String.format("IEM2: processEventBroadcastEvent [%s]", broadcastEvent));
      if (broadcastEvent.getUsers().size() == 0) return;
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof IBroadcastEventListener) {
               ((IBroadcastEventListener) listener).handleBroadcastEvent(sender, broadcastEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(String.format(
                  "IEM2: processEventBroadcastEvent [%s] error processing priorityListeners", broadcastEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (listener instanceof IBroadcastEventListener) {
               ((IBroadcastEventListener) listener).handleBroadcastEvent(sender, broadcastEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("IEM2: processEventBroadcastEvent [%s] error processing listeners", broadcastEvent), ex);
         }
      }
   }

   public static void processRemoteEventManagerEvent(Sender sender, RemoteEventServiceEventType remoteEventServiceEvent) {
      OseeEventManager.eventLog(String.format("IEM2: processRemoteEventManagerEvent [%s]", remoteEventServiceEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof IRemoteEventManagerEventListener) {
               ((IRemoteEventManagerEventListener) listener).handleRemoteEventManagerEvent(sender,
                     remoteEventServiceEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(String.format(
                  "IEM2: processRemoteEventManagerEvent [%s] error processing priorityListeners",
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
            OseeEventManager.eventLog(String.format(
                  "IEM2: processRemoteEventManagerEvent [%s] error processing listeners", remoteEventServiceEvent), ex);
         }
      }
   }

   public static void processTransactionEvent(Sender sender, TransactionEvent transactionEvent) {
      OseeEventManager.eventLog(String.format("IEM2: processTransactionEvent [%s]", transactionEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof ITransactionEventListener) {
               ((ITransactionEventListener) listener).handleTransactionEvent(sender, transactionEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(String.format(
                  "IEM2: processTransactionEvent [%s] error processing priorityListeners", transactionEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (listener instanceof ITransactionEventListener) {
               ((ITransactionEventListener) listener).handleTransactionEvent(sender, transactionEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("IEM2: processTransactionEvent [%s] error processing listeners", transactionEvent), ex);
         }
      }
   }

   public static String getListenerReport() {
      List<String> listenerStrs = new ArrayList<String>();
      for (IEventListener listener : priorityListeners) {
         listenerStrs.add("Priority: " + getObjectSafeName(listener));
      }
      for (IEventListener listener : listeners) {
         listenerStrs.add(getObjectSafeName(listener));
      }
      String[] listArr = listenerStrs.toArray(new String[listenerStrs.size()]);
      Arrays.sort(listArr);
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString("\n", (Object[]) listArr);
   }

   public static String getObjectSafeName(Object object) {
      try {
         return object.toString();
      } catch (Exception ex) {
         return object.getClass().getSimpleName() + " - exception on toString: " + ex.getLocalizedMessage();
      }
   }

   /*
    * Kick LOCAL and REMOTE access control events
    */
   static void kickAccessControlArtifactsEvent(final Sender sender, final AccessControlEvent accessControlEvent) {
      if (sender == null) {
         throw new IllegalArgumentException("sender can not be null");
      }
      if (accessControlEvent.getEventType() == null) {
         throw new IllegalArgumentException("accessControlEventType can not be null");
      }
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickAccessControlEvent - type: " + accessControlEvent + sender + " artifacts: " + accessControlEvent.getArtifacts());
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Kick LOCAL
               boolean normalOperation = !enableRemoteEventLoopback;
               boolean loopbackTestEnabledAndRemoteEventReturned = enableRemoteEventLoopback && sender.isRemote();
               if ((normalOperation && sender.isLocal()) || loopbackTestEnabledAndRemoteEventReturned) {
                  processAccessControlEvent(sender, accessControlEvent);
               }
               // Kick REMOTE
               if (sender.isLocal() && accessControlEvent.getEventType().isRemoteEventType()) {
                  RemoteEventManager2.getInstance().kick(
                        FrameworkEventUtil.getRemoteAccessControlEvent(accessControlEvent));
               }
            } catch (Exception ex) {
               OseeEventManager.eventLog("IEM2 kickAccessControlEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL "remote event manager" event
   static void kickLocalRemEvent(final Sender sender, final RemoteEventServiceEventType remoteEventServiceEventType) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickLocalRemEvent: type: " + remoteEventServiceEventType + " - " + sender);
      Runnable runnable = new Runnable() {
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
   static void kickLocalArtifactReloadEvent(final Sender sender, final ArtifactEvent artifactEvent) {
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickArtifactReloadEvent [" + artifactEvent + "] - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Kick LOCAL
               boolean normalOperation = !enableRemoteEventLoopback;
               boolean loopbackTestEnabledAndRemoteEventReturned = enableRemoteEventLoopback && sender.isRemote();
               if ((normalOperation && sender.isLocal()) || loopbackTestEnabledAndRemoteEventReturned) {
                  processEventArtifactsAndRelations(sender, artifactEvent);
               }

               // NO REMOTE KICK
            } catch (Exception ex) {
               OseeEventManager.eventLog("IEM2 kickArtifactReloadEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   /*
    * Kick LOCAL and REMOTE branch events
    */
   static void kickBranchEvent(final Sender sender, final BranchEvent branchEvent) {
      if (branchEvent.getNetworkSender() == null) {
         OseeEventManager.eventLog("IEM2: kickBranchEvent - ERROR networkSender can't be null.");
         return;
      }
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickBranchEvent: type: " + branchEvent.getEventType() + " guid: " + branchEvent.getBranchGuid() + " - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Log if this is a loopback and what is happening
               if (enableRemoteEventLoopback) {
                  OseeEventManager.eventLog("IEM2: BranchEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }
               BranchEventType branchEventType = branchEvent.getEventType();

               // Kick LOCAL
               boolean normalOperation = !enableRemoteEventLoopback;
               boolean loopbackTestEnabledAndRemoteEventReturned = enableRemoteEventLoopback && sender.isRemote();
               if ((normalOperation && sender.isLocal() && branchEventType.isLocalEventType()) || loopbackTestEnabledAndRemoteEventReturned) {
                  processBranchEvent(sender, branchEvent);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal() && branchEventType.isRemoteEventType()) {
                  RemoteEventManager2.getInstance().kick(FrameworkEventUtil.getRemoteBranchEvent(branchEvent));
               }
            } catch (OseeAuthenticationRequiredException ex) {
               OseeEventManager.eventLog("IEM2 kickBranchEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL and REMOTE ArtifactEvent
   static void kickPersistEvent(final Sender sender, final ArtifactEvent artifactEvent) {
      if (artifactEvent.getNetworkSender() == null) {
         OseeEventManager.eventLog("IEM2: kickPersistEvent - ERROR networkSender can't be null.");
         return;
      }
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickPersistEvent [" + artifactEvent + "] - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            // Roll-up change information
            try {
               // Log if this is a loopback and what is happening
               if (enableRemoteEventLoopback) {
                  OseeEventManager.eventLog("IEM2: ArtifactEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }

               // Kick LOCAL
               boolean normalOperation = !enableRemoteEventLoopback;
               boolean loopbackTestEnabledAndRemoteEventReturned = enableRemoteEventLoopback && sender.isRemote();
               if (normalOperation || loopbackTestEnabledAndRemoteEventReturned) {
                  processEventArtifactsAndRelations(sender, artifactEvent);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal()) {
                  RemoteEventManager2.getInstance().kick(FrameworkEventUtil.getRemotePersistEvent(artifactEvent));
               }
            } catch (Exception ex) {
               OseeEventManager.eventLog("IEM2 kickPersistEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL and REMOTE ArtifactEvent
   static void kickTransactionEvent(final Sender sender, final TransactionEvent transEvent) {
      if (transEvent.getNetworkSender() == null) {
         OseeEventManager.eventLog("IEM2: kickTransactionEvent - ERROR networkSender can't be null.");
         return;
      }
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickTransactionEvent [" + transEvent + "] - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            // Roll-up change information
            try {
               // Log if this is a loopback and what is happening
               if (!enableRemoteEventLoopback) {
                  OseeEventManager.eventLog("IEM2: TransactionEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }

               // Kick LOCAL
               boolean normalOperation = !enableRemoteEventLoopback;
               boolean loopbackTestEnabledAndRemoteEventReturned = enableRemoteEventLoopback && sender.isRemote();
               if (normalOperation || loopbackTestEnabledAndRemoteEventReturned) {
                  processTransactionEvent(sender, transEvent);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal()) {
                  // Kick REMOTE
                  RemoteEventManager2.getInstance().kick(FrameworkEventUtil.getRemoteTransactionEvent(transEvent));
               }
            } catch (Exception ex) {
               OseeEventManager.eventLog("IEM2: kickTransactionEvent", ex);
            }
         }
      };
      execute(runnable);
   }

   /*
    * Kick LOCAL and REMOTE broadcast event
    */
   static void kickBroadcastEvent(final Sender sender, final BroadcastEvent broadcastEvent) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }

      if (!broadcastEvent.getBroadcastEventType().isPingOrPong()) {
         OseeEventManager.eventLog("IEM2: kickBroadcastEvent: type: " + broadcastEvent.getBroadcastEventType().name() + " message: " + broadcastEvent.getMessage() + " - " + sender);
      }
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Kick from REMOTE
               if (sender.isRemote() || sender.isLocal() && broadcastEvent.getBroadcastEventType().isLocalEventType()) {
                  processEventBroadcastEvent(sender, broadcastEvent);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal() && broadcastEvent.getBroadcastEventType().isRemoteEventType()) {
                  RemoteEventManager2.getInstance().kick(FrameworkEventUtil.getRemoteBroadcastEvent(broadcastEvent));
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   static boolean isDisableEvents() {
      return disableEvents;
   }

   public static void setDisableEvents(boolean disableEvents) {
      InternalEventManager2.disableEvents = disableEvents;
   }

   public static boolean isEnableRemoteEventLoopback() {
      return enableRemoteEventLoopback;
   }

   public static void setEnableRemoteEventLoopback(boolean enableRemoteEventLoopback) {
      InternalEventManager2.enableRemoteEventLoopback = enableRemoteEventLoopback;
   }
}
