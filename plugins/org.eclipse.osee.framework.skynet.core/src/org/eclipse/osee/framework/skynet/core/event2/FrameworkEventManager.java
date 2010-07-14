/*
 * Created on Mar 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.skynet.core.event.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBroadcastEventListener;
import org.eclipse.osee.framework.skynet.core.event.IEventFilteredListener;
import org.eclipse.osee.framework.skynet.core.event.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.IRemoteEventManagerEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event2.filter.BranchGuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;

/**
 * @author Donald G. Dunne
 */
public class FrameworkEventManager {
   private static final List<IEventListener> priorityListeners = new CopyOnWriteArrayList<IEventListener>();
   private static final List<IEventListener> listeners = new CopyOnWriteArrayList<IEventListener>();

   public static void addListener(IEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }
      if (!listeners.contains(listener)) {
         listeners.add(listener);
         OseeEventManager.eventLog("FEM: addListener (" + priorityListeners.size() + ") " + listener);
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
      OseeEventManager.eventLog("FEM: addPriorityListener (" + priorityListeners.size() + ") " + listener);
   }

   public static void removeListener(IEventListener listener) {
      listeners.remove(listener);
      priorityListeners.remove(listener);
   }

   public static void removeListeners(IEventListener listener) {
      OseeEventManager.eventLog("FEM: removeListener: (" + listeners.size() + ") " + listener);
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
      OseeEventManager.eventLog(String.format("FEM: processBranchEvent [%s]", branchEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            processBranchEventListener(listener, sender, branchEvent);
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("FEM: processBranchEvent [%s] error processing priorityListeners", branchEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            processBranchEventListener(listener, sender, branchEvent);
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("FEM: processBranchEvent [%s] error processing listeners", branchEvent), ex);
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
      OseeEventManager.eventLog(String.format("FEM: processArtsAndRels [%s]", artifactEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            processEventArtifactsAndRelationsListener(listener, artifactEvent, sender);
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("FEM: processArtsAndRels [%s] error processing priorityListeners", artifactEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            processEventArtifactsAndRelationsListener(listener, artifactEvent, sender);
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("FEM: processArtsAndRels [%s] error processing listeners", artifactEvent), ex);
         }
      }
   }

   private static void processEventArtifactsAndRelationsListener(IEventListener listener, ArtifactEvent artifactEvent, Sender sender) {
      System.out.println("FEM Processing " + listener);
      if (listener != null && !(listener instanceof IArtifactEventListener)) return;
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
      OseeEventManager.eventLog(String.format("FEM: processAccessControlEvent [%s]", accessControlEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof IAccessControlEventListener) {
               ((IAccessControlEventListener) listener).handleAccessControlArtifactsEvent(sender, accessControlEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(String.format(
                  "FEM: processAccessControlEvent [%s] error processing priorityListeners", accessControlEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (listener instanceof IAccessControlEventListener) {
               ((IAccessControlEventListener) listener).handleAccessControlArtifactsEvent(sender, accessControlEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("FEM: processAccessControlEvent [%s] error processing listeners", accessControlEvent),
                  ex);
         }
      }
   }

   public static void processEventBroadcastEvent(Sender sender, BroadcastEvent broadcastEvent) {
      OseeEventManager.eventLog(String.format("FEM: processEventBroadcastEvent [%s]", broadcastEvent));
      if (broadcastEvent.getUsers().size() == 0) return;
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof IBroadcastEventListener) {
               ((IBroadcastEventListener) listener).handleBroadcastEvent(sender, broadcastEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(String.format(
                  "FEM: processEventBroadcastEvent [%s] error processing priorityListeners", broadcastEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (listener instanceof IBroadcastEventListener) {
               ((IBroadcastEventListener) listener).handleBroadcastEvent(sender, broadcastEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("FEM: processEventBroadcastEvent [%s] error processing listeners", broadcastEvent), ex);
         }
      }
   }

   public static void processRemoteEventManagerEvent(Sender sender, RemoteEventServiceEventType remoteEventServiceEvent) {
      OseeEventManager.eventLog(String.format("FEM: processRemoteEventManagerEvent [%s]", remoteEventServiceEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof IRemoteEventManagerEventListener) {
               ((IRemoteEventManagerEventListener) listener).handleRemoteEventManagerEvent(sender,
                     remoteEventServiceEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(String.format(
                  "FEM: processRemoteEventManagerEvent [%s] error processing priorityListeners",
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
                  "FEM: processRemoteEventManagerEvent [%s] error processing listeners", remoteEventServiceEvent), ex);
         }
      }
   }

   public static void processTransactionEvent(Sender sender, TransactionEvent transactionEvent) {
      OseeEventManager.eventLog(String.format("FEM: processTransactionEvent [%s]", transactionEvent));
      for (IEventListener listener : priorityListeners) {
         try {
            if (listener instanceof ITransactionEventListener) {
               ((ITransactionEventListener) listener).handleTransactionEvent(sender, transactionEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(String.format(
                  "FEM: processTransactionEvent [%s] error processing priorityListeners", transactionEvent), ex);
         }
      }
      for (IEventListener listener : listeners) {
         try {
            if (listener instanceof ITransactionEventListener) {
               ((ITransactionEventListener) listener).handleTransactionEvent(sender, transactionEvent);
            }
         } catch (Exception ex) {
            OseeEventManager.eventLog(
                  String.format("FEM: processTransactionEvent [%s] error processing listeners", transactionEvent), ex);
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

}
