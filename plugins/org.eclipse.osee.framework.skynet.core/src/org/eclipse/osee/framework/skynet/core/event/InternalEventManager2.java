/*
 * Created on Apr 5, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event2.BroadcastEvent;
import org.eclipse.osee.framework.skynet.core.event2.FrameworkEventManager;
import org.eclipse.osee.framework.skynet.core.event2.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event2.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;

/**
 * Internal implementation of OSEE Event Manager that should only be accessed from RemoteEventManager and
 * OseeEventManager classes.
 * 
 * @author Donald G. Dunne
 */
public class InternalEventManager2 {

   private static final List<IEventListener> priorityListeners = new CopyOnWriteArrayList<IEventListener>();
   private static final List<IEventListener> listeners = new CopyOnWriteArrayList<IEventListener>();
   public static final Collection<UnloadedArtifact> EMPTY_UNLOADED_ARTIFACTS = Collections.emptyList();
   private static boolean disableEvents = false;

   private static final ThreadFactory threadFactory = new OseeEventThreadFactory("Osee Events2");
   private static final ExecutorService executorService =
         Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);

   // This will disable all Local TransactionEvents and enable loopback routing of Remote TransactionEvents back
   // through the RemoteEventService as if they came from another client.  This is for testing purposes only and
   // should be reset to false before release.
   private static boolean enableRemoteEventLoopback = false;

   private static void execute(Runnable runnable) {
      executorService.submit(runnable);
   }

   // Kick LOCAL "remote event manager" event
   static void kickLocalRemEvent(final Sender sender, final RemoteEventServiceEventType remoteEventServiceEventType) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM1: kickLocalRemEvent: type: " + remoteEventServiceEventType + " - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            // Kick LOCAL
            try {
               if (sender.isLocal() && remoteEventServiceEventType.isLocalEventType()) {
                  FrameworkEventManager.processRemoteEventManagerEvent(sender, remoteEventServiceEventType);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      execute(runnable);
   }

   // Kick LOCAL ArtifactReloadEvent
   static void kickLocalArtifactReloadEvent(final Sender sender, final Set<EventBasicGuidArtifact> artifactChanges) {
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2: kickArtifactReloadEvent #Reloads: " + artifactChanges.size() + " - " + sender);
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Log if this is a loopback and what is happening
               if (enableRemoteEventLoopback) {
                  OseeEventManager.eventLog("IEM2: kickArtifactReloadEvent Loopback enabled" + (sender.isLocal() ? " - Ignoring Local Kick" : " - Kicking Local from Loopback"));
               }

               // Kick LOCAL
               if (!enableRemoteEventLoopback) {
                  FrameworkEventManager.processEventArtifactsAndRelations(sender, artifactChanges);
               }
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
         OseeEventManager.eventLog("IEM2: kickTransactionEvent <<ERROR>> networkSender can't be null.");
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
                  FrameworkEventManager.processBranchEvent(sender, branchEvent);
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

   // Kick LOCAL and REMOTE TransactionEvent
   static void kickTransactionEvent(final Sender sender, final TransactionEvent transEvent) {
      if (transEvent.getNetworkSender() == null) {
         OseeEventManager.eventLog("IEM2: kickTransactionEvent <<ERROR>> networkSender can't be null.");
         return;
      }
      if (isDisableEvents()) {
         return;
      }
      OseeEventManager.eventLog("IEM2:kickTransactionEvent [" + transEvent + "] - " + sender);
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
                  FrameworkEventManager.processEventArtifactsAndRelations(sender, transEvent);
               }

               // Kick REMOTE (If source was Local and this was not a default branch changed event
               if (sender.isLocal()) {
                  RemoteEventManager2.getInstance().kick(FrameworkEventUtil.getRemoteTransactionEvent(transEvent));
               }
            } catch (Exception ex) {
               OseeEventManager.eventLog("IEM2 kickTransactionEvent", ex);
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
         OseeEventManager.eventLog("IEM1: kickBroadcastEvent: type: " + broadcastEvent.getBroadcastEventType().name() + " message: " + broadcastEvent.getMessage() + " - " + sender);
      }
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               // Kick from REMOTE
               if (sender.isRemote() || sender.isLocal() && broadcastEvent.getBroadcastEventType().isLocalEventType()) {
                  FrameworkEventManager.processEventBroadcastEvent(sender, broadcastEvent);
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

   /**
    * Add a priority listener. This should only be done for caches where they need to be updated before all other
    * listeners are called.
    */
   static void addPriorityListener(IEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }
      if (!priorityListeners.contains(listener)) {
         priorityListeners.add(listener);
      }
      OseeEventManager.eventLog("IEM2:addPriorityListener (" + priorityListeners.size() + ") " + listener);
   }

   static void addListener(IEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }
      if (!listeners.contains(listener)) {
         listeners.add(listener);
      }
      OseeEventManager.eventLog("IEM2:addListener (" + listeners.size() + ") " + listener);
   }

   static void removeListeners(IEventListener listener) {
      OseeEventManager.eventLog("IEM2:removeListener: (" + listeners.size() + ") " + listener);
      listeners.remove(listener);
      priorityListeners.remove(listener);
   }

   // This method clears all listeners. Should only be used for testing purposes.
   public static void removeAllListeners() {
      listeners.clear();
      priorityListeners.clear();
   }

   public static String getObjectSafeName(Object object) {
      try {
         return object.toString();
      } catch (Exception ex) {
         return object.getClass().getSimpleName() + " - exception on toString: " + ex.getLocalizedMessage();
      }
   }

   static boolean isDisableEvents() {
      return disableEvents;
   }

   public static void setDisableEvents(boolean disableEvents) {
      InternalEventManager2.disableEvents = disableEvents;
   }

   static String getListenerReport() {
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

   public static boolean isEnableRemoteEventLoopback() {
      return enableRemoteEventLoopback;
   }

   public static void setEnableRemoteEventLoopback(boolean enableRemoteEventLoopback) {
      InternalEventManager2.enableRemoteEventLoopback = enableRemoteEventLoopback;
   }
}
