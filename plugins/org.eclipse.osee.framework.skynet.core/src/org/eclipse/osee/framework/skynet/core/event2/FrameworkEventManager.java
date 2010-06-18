/*
 * Created on Mar 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.skynet.core.event.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBroadcastEventListener;
import org.eclipse.osee.framework.skynet.core.event.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.IRemoteEventManagerEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event2.filter.FilteredEventListener;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;

/**
 * @author Donald G. Dunne
 */
public class FrameworkEventManager {
   private static final List<IEventListener> priorityListeners = new CopyOnWriteArrayList<IEventListener>();
   private static final List<IEventListener> listeners = new CopyOnWriteArrayList<IEventListener>();
   private static List<EventBasicGuidRelation> EMPTY_EVENT_RELATIONS = new ArrayList<EventBasicGuidRelation>();

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

   public static void removeAllListeners() {
      listeners.clear();
      priorityListeners.clear();
   }

   public static int getNumberOfListeners() {
      return listeners.size();
   }

   public static boolean isHandledBy(IEventListener event) {
      return event instanceof IArtifactEventListener || (event instanceof FilteredEventListener && ((FilteredEventListener) event).isOfType(IArtifactEventListener.class));
   }

   public static void processBranchEvent(Sender sender, BranchEvent branchEvent) {
      OseeEventManager.eventLog(String.format("FEM: processBranchEvent [%s]", branchEvent));
      for (IEventListener listener : priorityListeners) {
         processBranchEventListener(listener, sender, branchEvent);
      }
      for (IEventListener listener : listeners) {
         processBranchEventListener(listener, sender, branchEvent);
      }
   }

   private static void processBranchEventListener(IEventListener listener, Sender sender, BranchEvent branchEvent) {
      IBranchListener branchListner = null;
      Collection<IEventFilter> eventFilters = null;
      if (listener instanceof IBranchListener) {
         branchListner = (IBranchListener) listener;
         eventFilters = Collections.emptyList();
      } else if (listener instanceof FilteredEventListener && ((FilteredEventListener) listener).getEventListener() instanceof IBranchListener) {
         branchListner = (IBranchListener) ((FilteredEventListener) listener).getEventListener();
         eventFilters = ((FilteredEventListener) listener).getEventFilters();
      }
      if (branchListner != null) {
         // TODO handle filters first
         ((IBranchListener) listener).handleBranchEvent(sender, branchEvent);
      }
   }

   public static void processEventArtifactsAndRelations(Sender sender, ArtifactEvent artifactEvent) {
      OseeEventManager.eventLog(String.format("FEM: processArtsAndRels [%s]", artifactEvent));
      for (IEventListener listener : priorityListeners) {
         processEventArtifactsAndRelationsListener(listener, artifactEvent, sender);
      }
      for (IEventListener listener : listeners) {
         processEventArtifactsAndRelationsListener(listener, artifactEvent, sender);
      }
   }

   private static void processEventArtifactsAndRelationsListener(IEventListener listener, ArtifactEvent artifactEvent, Sender sender) {
      IArtifactEventListener artifactEventListener = null;
      Collection<IEventFilter> eventFilters = null;
      if (listener instanceof IArtifactEventListener) {
         artifactEventListener = (IArtifactEventListener) listener;
         eventFilters = Collections.emptyList();
      } else if (listener instanceof FilteredEventListener && ((FilteredEventListener) listener).getEventListener() instanceof IArtifactEventListener) {
         artifactEventListener = (IArtifactEventListener) ((FilteredEventListener) listener).getEventListener();
         eventFilters = ((FilteredEventListener) listener).getEventFilters();
      }
      if (artifactEventListener != null) {
         // TODO handle filters first
         artifactEventListener.handleArtifactEvent(artifactEvent, sender);
      }
   }

   public static void processAccessControlEvent(Sender sender, AccessControlEvent accessControlEvent) {
      OseeEventManager.eventLog(String.format("FEM: processAccessControlEvent [%s]", accessControlEvent));
      for (IEventListener listener : priorityListeners) {
         if (listener instanceof IAccessControlEventListener) {
            ((IAccessControlEventListener) listener).handleAccessControlArtifactsEvent(sender, accessControlEvent);
         }
      }
      for (IEventListener listener : listeners) {
         if (listener instanceof IAccessControlEventListener) {
            ((IAccessControlEventListener) listener).handleAccessControlArtifactsEvent(sender, accessControlEvent);
         }
      }
   }

   public static void processEventBroadcastEvent(Sender sender, BroadcastEvent broadcastEvent) {
      OseeEventManager.eventLog(String.format("FEM: processEventBroadcastEvent [%s]", broadcastEvent));
      if (broadcastEvent.getUsers().size() == 0) return;
      for (IEventListener listener : priorityListeners) {
         if (listener instanceof IBroadcastEventListener) {
            ((IBroadcastEventListener) listener).handleBroadcastEvent(sender, broadcastEvent);
         }
      }
      for (IEventListener listener : listeners) {
         if (listener instanceof IBroadcastEventListener) {
            ((IBroadcastEventListener) listener).handleBroadcastEvent(sender, broadcastEvent);
         }
      }
   }

   public static void processRemoteEventManagerEvent(Sender sender, RemoteEventServiceEventType remoteEventServiceEvent) {
      OseeEventManager.eventLog(String.format("FEM: processRemoteEventManagerEvent [%s]", remoteEventServiceEvent));
      for (IEventListener listener : priorityListeners) {
         if (listener instanceof IRemoteEventManagerEventListener) {
            ((IRemoteEventManagerEventListener) listener).handleRemoteEventManagerEvent(sender, remoteEventServiceEvent);
         }
      }
      for (IEventListener listener : listeners) {
         if (listener instanceof IRemoteEventManagerEventListener) {
            ((IRemoteEventManagerEventListener) listener).handleRemoteEventManagerEvent(sender, remoteEventServiceEvent);
         }
      }
   }

   public static void processTransactionEvent(Sender sender, TransactionEvent transactionEvent) {
      OseeEventManager.eventLog(String.format("FEM: processTransactionEvent [%s]", transactionEvent));
      for (IEventListener listener : priorityListeners) {
         if (listener instanceof ITransactionEventListener) {
            ((ITransactionEventListener) listener).handleTransactionEvent(sender, transactionEvent);
         }
      }
      for (IEventListener listener : listeners) {
         if (listener instanceof ITransactionEventListener) {
            ((ITransactionEventListener) listener).handleTransactionEvent(sender, transactionEvent);
         }
      }
   }
}
