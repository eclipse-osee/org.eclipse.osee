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
import org.eclipse.osee.framework.skynet.core.event.IEventListener;
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

   public static void processEventArtifactsAndRelations(Sender sender, TransactionEvent transEvent) {
      processEventArtifactsAndRelations(sender, transEvent.getArtifacts(), transEvent.getRelations());
   }

   public static void processEventArtifactsAndRelations(Sender sender, Collection<EventBasicGuidArtifact> eventArtifacts) {
      processEventArtifactsAndRelations(sender, eventArtifacts, EMPTY_EVENT_RELATIONS);
   }

   public static void processEventArtifactsAndRelations(Sender sender, Collection<EventBasicGuidArtifact> eventArtifacts, Collection<EventBasicGuidRelation> eventRelations) {
      for (IEventListener listener : listeners) {
         IArtifactListener artifactListener = null;
         Collection<IEventFilter> eventFilters = null;
         if (listener instanceof IArtifactListener) {
            artifactListener = (IArtifactListener) listener;
            eventFilters = Collections.emptyList();
         } else if (listener instanceof FilteredEventListener) {
            artifactListener = (IArtifactListener) ((FilteredEventListener) listener).getEventListener();
            eventFilters = ((FilteredEventListener) listener).getEventFilters();
         }
         if (artifactListener != null) {
            // TODO handle filters first
            artifactListener.handleArtifactModified(eventArtifacts, eventRelations, sender);

            // TODO handle artifact change type??
         }
      }
   }
}
