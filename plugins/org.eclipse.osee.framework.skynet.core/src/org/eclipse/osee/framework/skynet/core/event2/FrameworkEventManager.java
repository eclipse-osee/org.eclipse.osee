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
import org.eclipse.osee.framework.skynet.core.event.IBroadcastEventListener;
import org.eclipse.osee.framework.skynet.core.event.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.IRemoteEventManagerEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactListener;
import org.eclipse.osee.framework.skynet.core.event2.filter.FilteredEventListener;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;

/**
 * @author Donald G. Dunne
 */
public class FrameworkEventManager {
   private static final List<IEventListener> listeners = new CopyOnWriteArrayList<IEventListener>();
   private static List<EventBasicGuidRelation> EMPTY_EVENT_RELATIONS = new ArrayList<EventBasicGuidRelation>();

   public static void addListener(IEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }
      if (!listeners.contains(listener)) {
         listeners.add(listener);
      }
   }

   public static void removeListener(IEventListener listener) {
      listeners.remove(listener);
   }

   public static void removeAllListeners() {
      listeners.clear();
   }

   public static int getNumberOfListeners() {
      return listeners.size();
   }

   public static boolean isHandledBy(IEventListener event) {
      return event instanceof IArtifactListener || (event instanceof FilteredEventListener && ((FilteredEventListener) event).isOfType(IArtifactListener.class));
   }

   public static void processBranchEvent(Sender sender, BranchEvent branchEvent) {
      OseeEventManager.eventLog(String.format("FEM: processBranchEvent [%s]", branchEvent));
      for (IEventListener listener : listeners) {
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
   }

   public static void processEventArtifactsAndRelations(Sender sender, PersistEvent transEvent) {
      processEventArtifactsAndRelations(sender, transEvent.getArtifacts(), transEvent.getRelations());
   }

   public static void processEventArtifactsAndRelations(Sender sender, Collection<EventBasicGuidArtifact> eventArtifacts) {
      processEventArtifactsAndRelations(sender, eventArtifacts, EMPTY_EVENT_RELATIONS);
   }

   public static void processEventArtifactsAndRelations(Sender sender, Collection<EventBasicGuidArtifact> eventArtifacts, Collection<EventBasicGuidRelation> eventRelations) {
      OseeEventManager.eventLog(String.format("FEM: processArtsAndRels arts[%s] rels[%s]", eventArtifacts,
            eventRelations));
      for (IEventListener listener : listeners) {
         IArtifactListener artifactListener = null;
         Collection<IEventFilter> eventFilters = null;
         if (listener instanceof IArtifactListener) {
            artifactListener = (IArtifactListener) listener;
            eventFilters = Collections.emptyList();
         } else if (listener instanceof FilteredEventListener && ((FilteredEventListener) listener).getEventListener() instanceof IArtifactListener) {
            artifactListener = (IArtifactListener) ((FilteredEventListener) listener).getEventListener();
            eventFilters = ((FilteredEventListener) listener).getEventFilters();
         }
         if (artifactListener != null) {
            // TODO handle filters first
            artifactListener.handleArtifactModified(eventArtifacts, eventRelations, sender);
         }
      }
   }

   public static void processEventBroadcastEvent(Sender sender, BroadcastEvent broadcastEvent) {
      OseeEventManager.eventLog(String.format("FEM: processEventBroadcastEvent [%s]", broadcastEvent));
      if (broadcastEvent.getUsers().size() == 0) return;
      for (IEventListener listener : listeners) {
         if (listener instanceof IBroadcastEventListener) {
            ((IBroadcastEventListener) listener).handleBroadcastEvent(sender, broadcastEvent);
         }
      }
   }

   public static void processRemoteEventManagerEvent(Sender sender, RemoteEventServiceEventType remoteEventServiceEvent) {
      OseeEventManager.eventLog(String.format("FEM: processRemoteEventManagerEvent [%s]", remoteEventServiceEvent));
      for (IEventListener listener : listeners) {
         if (listener instanceof IRemoteEventManagerEventListener) {
            ((IRemoteEventManagerEventListener) listener).handleRemoteEventManagerEvent(sender, remoteEventServiceEvent);
         }
      }
   }

   public static void processTransactionEvent(Sender sender, TransactionEvent transactionEvent) {
      OseeEventManager.eventLog(String.format("FEM: processTransactionEvent [%s]", transactionEvent));
      for (IEventListener listener : listeners) {
         if (listener instanceof ITransactionEventListener) {
            ((ITransactionEventListener) listener).handleTransactionEvent(sender, transactionEvent);
         }
      }
   }
}
